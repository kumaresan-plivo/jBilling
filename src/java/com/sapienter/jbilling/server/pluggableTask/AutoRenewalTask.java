package com.sapienter.jbilling.server.pluggableTask;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.customerEnrollment.helper.CustomerEnrollmentFileGenerationHelper;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.metafields.EntityType;
import com.sapienter.jbilling.server.metafields.MetaFieldBL;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.order.IOrderSessionBean;
import com.sapienter.jbilling.server.order.db.*;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.process.task.AbstractCronTask;
import com.sapienter.jbilling.server.system.event.EventManager;
import com.sapienter.jbilling.server.user.db.*;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.time.DateConvertUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.ScrollableResults;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;

public class AutoRenewalTask extends AbstractCronTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(AutoRenewalTask.class));

    private static final ParameterDescription PARAM_DAYS_BEFORE_NOTIFICATION =
            new ParameterDescription("days_before_notification", true, ParameterDescription.Type.STR);

    // Initializer for pluggable params
    {
        descriptions.add(PARAM_DAYS_BEFORE_NOTIFICATION);
    }

    private CustomerDAS customerDAS = new CustomerDAS();
    private OrderDAS orderDAS = new OrderDAS();
    private ItemDAS itemDAS = new ItemDAS();
    private AccountInformationTypeDAS accountInformationTypeDAS = new AccountInformationTypeDAS();

    public AutoRenewalTask() {
        setUseTransaction(true);
    }

    @Override
    public String getTaskName() {
        return "Auto Renewal task, entity Id: " + this.getEntityId() + ", task Id:" + getTaskId();
    }

    @Override
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
        _init(context);

        ScrollableResults customers = customerDAS.findAllByCompanyId(this.getEntityId());
        try {
            String daysBeforeNotificationParam = parameters.get(PARAM_DAYS_BEFORE_NOTIFICATION.getName());
            Integer daysBeforeNotification = NumberUtils.isNumber(daysBeforeNotificationParam) ? Integer.valueOf(daysBeforeNotificationParam) : 30;

            LocalDate today = DateConvertUtils.asLocalDate(executionDate);

            while (customers.next()) {
                CustomerDTO customer = (CustomerDTO) customers.get()[0];
                UserDTO user = customer.getBaseUser();
                List<OrderDTO> orders = orderDAS.findRecurringOrders(user.getId());

                if (!orders.isEmpty()) {
                    OrderDTO mainSubscriptionOrder = orders.get(0);

                    if (mainSubscriptionOrder.getActiveUntil() != null) {
                        LocalDate contractExpiryDate = DateConvertUtils.asLocalDate(mainSubscriptionOrder.getActiveUntil());

                        //Verify if the "contract expiry date" was reached
                        if (today.isEqual(contractExpiryDate)) {
                            MetaFieldValue defaultPlanMetaFieldValue = customer.getBaseUser().getEntity().getMetaField(FileConstants.DEFAULT_PLAN);

                            if (defaultPlanMetaFieldValue != null) {
                                ItemDTO defaultPlanItem = itemDAS.findItemByInternalNumber((String) defaultPlanMetaFieldValue.getValue(), this.getEntityId());

                                //Modify the “customer subscription order”, remove the current plan, add the default plan and update the active until date
                                OrderLineDTO orderLine = mainSubscriptionOrder.getLines().stream().filter(line -> line.getDeleted() == 0).findFirst().get();
                                OrderChangeDTO currentPlanRemovalOrderChange = this.buildOrderChange(user, mainSubscriptionOrder, orderLine, orderLine.getItem(), 0, -1);
                                OrderChangeDTO defaultPlanAdditionOrderChange = this.buildOrderChange(user, mainSubscriptionOrder, null, defaultPlanItem, 1, 1);
                                List<OrderChangeDTO> orderChanges = Arrays.asList(currentPlanRemovalOrderChange, defaultPlanAdditionOrderChange);

                                mainSubscriptionOrder.setActiveUntil(null);

                                //Set the last enrollment meta field on the customer
                                MetaField customerLastEnrollmentMetaField = MetaFieldBL.getFieldByName(user.getEntity().getId(), new EntityType[]{EntityType.CUSTOMER},
                                        FileConstants.CUSTOMER_LAST_ENROLLMENT_METAFIELD_NAME);
                                customer.setMetaField(customerLastEnrollmentMetaField, DateConvertUtils.asUtilDate(today));

                                //Save order
                                ((IOrderSessionBean) Context.getBean(Context.Name.ORDER_SESSION)).createUpdate(
                                        this.getEntityId(), user.getId(), user.getLanguageIdField(), mainSubscriptionOrder, orderChanges, Collections.emptyList());

                                //Send notification
                                this.sendNotification(customer, true, null);
                            }
                            else {
                                LOG.debug(String.format("%s meta field value is null. It should be not null.", FileConstants.DEFAULT_PLAN));
                            }
                        }
                        //Verify if the "contract expiry date" - "days before notification" was reached
                        else if (today.isEqual(contractExpiryDate.minusDays(daysBeforeNotification))) {
                            this.sendNotification(customer, false, daysBeforeNotification);
                        }
                        //Verify if exists a param with the customer state and if the "contract expiry date" - "state days before notification(param value)" was reached
                        else {
                            String customerBusinessAitName = customer.getAccountType().getDescription().equals(FileConstants.RESIDENTIAL_ACCOUNT_TYPE) ?
                                    FileConstants.CUSTOMER_INFORMATION_AIT : FileConstants.BUSINESS_INFORMATION_AIT;
                            Integer customerBusinessAitId = accountInformationTypeDAS.getAccountInformationTypeByName(
                                    this.getEntityId(), customer.getAccountType().getId(), customerBusinessAitName).getId();

                            MetaFieldValue stateMetaFieldValue = customer.getCustomerAccountInfoTypeMetaField(FileConstants.STATE, customerBusinessAitId).getMetaFieldValue();
                            if (stateMetaFieldValue != null) {
                                String customerState = CustomerEnrollmentFileGenerationHelper.USState.getAbbreviationForState((String) stateMetaFieldValue.getValue());

                                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                                    if (parameter.getKey().equals(customerState)) {
                                        Integer stateDaysBeforeNotification = Integer.valueOf(parameter.getValue());
                                        if (today.isEqual(contractExpiryDate.minusDays(stateDaysBeforeNotification))) {
                                            this.sendNotification(customer, false, stateDaysBeforeNotification);
                                        }
                                    }
                                }
                            }
                            else {
                                LOG.debug(String.format("%s meta field value is null. It should be not null.", FileConstants.STATE));
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOG.debug(e.getMessage());
        }
        finally {
            customers.close();
        }
    }

    private void sendNotification(CustomerDTO customer, boolean renewalReached, Integer daysBeforeNotification) {
        EventManager.process(new AutoRenewalEvent(this.getEntityId(), customer, renewalReached, daysBeforeNotification));
    }

    private OrderChangeDTO buildOrderChange(UserDTO user, OrderDTO order, OrderLineDTO orderLine, ItemDTO item, Integer userItem, Integer quantity) {
        OrderChangeDTO orderChangeDTO = new OrderChangeDTO();

        orderChangeDTO.setUser(user);
        orderChangeDTO.setOrder(order);
        orderChangeDTO.setOrderLine(orderLine);
        orderChangeDTO.setItem(item);
        orderChangeDTO.setDescription(item.getDescription());
        orderChangeDTO.setQuantity(new BigDecimal(quantity));
        orderChangeDTO.setPrice(item.getPrice());
        orderChangeDTO.setUseItem(userItem);
        orderChangeDTO.setStatus(new OrderChangeStatusDAS().findApplyStatus(item.getEntityId()));
        orderChangeDTO.setUserAssignedStatus(new OrderChangeStatusDAS().findApplyStatus(item.getEntityId()));

        OrderChangeTypeDTO orderChangeType = new OrderChangeTypeDAS().findNow(Constants.ORDER_CHANGE_TYPE_DEFAULT);
        orderChangeType.setOrderChangeTypeMetaFields(Collections.emptySet());
        orderChangeDTO.setOrderChangeType(orderChangeType);

        Date firstDayOfMonthDate = this.getFirstDayOfMonthDate();
        orderChangeDTO.setStartDate(firstDayOfMonthDate);
        orderChangeDTO.setNextBillableDate(firstDayOfMonthDate);

        return orderChangeDTO;
    }

    private Date getFirstDayOfMonthDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}