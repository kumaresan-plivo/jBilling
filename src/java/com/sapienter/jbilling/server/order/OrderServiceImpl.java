package com.sapienter.jbilling.server.order;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.mediation.*;
import com.sapienter.jbilling.server.order.db.OrderDAS;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDAS;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.IWebServicesSessionBean;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by marcolin on 13/10/15.
 */
public class OrderServiceImpl implements OrderService {
    IWebServicesSessionBean webServicesSessionBean;

    private static final FormatLogger LOG = new FormatLogger(
            Logger.getLogger(OrderServiceImpl.class));

    public void setWebServicesSessionBean(IWebServicesSessionBean webServicesSessionBean) {
        this.webServicesSessionBean = webServicesSessionBean;
    }

    @Override
    public OrderWS getCurrentOrder(Integer userId, Date date) throws SessionInternalError {
        return webServicesSessionBean.getCurrentOrder(userId, date);
    }

    @Override
    public List<OrderWS> lastOrders(Integer userId, int numberOfOrdersToRetrieve) {
        return Arrays.asList(webServicesSessionBean.getLastOrders(userId, numberOfOrdersToRetrieve))
                .stream().map(i -> webServicesSessionBean.getOrder(i)).collect(Collectors.toList());
    }

    @Override
    public List<OrderChangeStatusWS> getOrderChangeStatusesForCompany() {
        return Arrays.asList(webServicesSessionBean.getOrderChangeStatusesForCompany());
    }

    @Override
    public List<OrderChangeTypeWS> getOrderChangeTypesForCompany() {
        return Arrays.asList(webServicesSessionBean.getOrderChangeTypesForCompany());
    }

    @Override
    @Transactional( propagation = Propagation.REQUIRED)
    public MediationEventResult addMediationEvent(JbillingMediationRecord jmr) {
        Integer userId = jmr.getUserId();
        Integer itemId = jmr.getItemId();
        String quantity = jmr.getQuantity().toString();
        Date eventDate = jmr.getEventDate();
        String pricingFields = jmr.getPricingFields();
        //normal processing
        MediationEventResult mediationEventResult = new MediationEventResult();

        UserBL userbl = new UserBL(userId);

        //NOTE: should be the same as ownerEntityId at this point
        Integer companyId = userbl.getEntityId(userId);

        // get currency from the user
        Integer currencyId = userbl.getCurrencyId();

        // get language from the caller
        Integer languageId = userbl.getLanguage();

        // convert the JMR to record. Important thing
        // here are the pricing fields, they weill be
        // subsequently used in ItemBL to pricing
        List<CallDataRecord> records = null;

        // get the current order and init OrderBL
        OrderDTO order = OrderBL.getOrCreateCurrentOrder(userId, eventDate, currencyId, true);
        mediationEventResult.setCurrentOrderId(order.getId());
        OrderBL bl = new OrderBL(order);
        List<OrderLineDTO> oldLines = OrderHelper.copyOrderLinesToDto(bl.getDTO().getLines());
        // add the line to the current order
        bl.addItem(
                itemId, new BigDecimal(quantity),
                languageId, userId, companyId,
                currencyId, records, true, eventDate);

        // set isMediated flag true if line pass from mediation.
        for (OrderLineDTO orderLine : bl.getDTO().getLines()) {
            orderLine.setMediated(true);
        }
        if (null != order) {
            for (OrderLineDTO line : order.getLines()) {
                if (itemId.equals(line.getItemId())) {
                    line.setMediatedQuantity(new BigDecimal(quantity));
                }
            }
        }

        UserDTO reseller = new CompanyDAS().find(companyId).getReseller();
        BigDecimal costAmount = BigDecimal.ZERO;
        if (reseller != null) {
            bl.processLines(
                    bl.getDTO(), languageId, reseller.getEntity().getId(),
                    reseller.getId(), reseller.getCurrency().getId(), pricingFields);

            List<OrderLineDTO> diffLines =
                    OrderLineBL.diffOrderLines(oldLines, bl.getDTO().getLines());
            for (int index = 0; index < diffLines.size(); index++) {
                OrderLineDTO line = diffLines.get(index);
                BigDecimal costAmountForMediationEvent = line.getPrice().multiply(line.getQuantity());
                costAmount = costAmount.add(costAmountForMediationEvent);
                mediationEventResult.setCostOrderLineId(line.getId());
                mediationEventResult.setCostAmountForChange(costAmountForMediationEvent);
            }
        }

        // process lines to update prices
        // and details from the source items
        bl.processLines(
                bl.getDTO(), languageId, companyId,
                userId, currencyId, pricingFields);

        /**
         * Recalculate prices for mediated lines if Lines use FUP.
         */
        if (null != order) {
            for (OrderLineDTO line : order.getLines()) {
                if (line.hasOrderLineUsagePools() && itemId.equals(line.getItemId())) {
                    if (line.getPrice() != null && line.getQuantity() != null) {
                        line.setAmount(line.getPrice().multiply(line.getQuantity()));
                    }
                }
            }
        }

        List<OrderLineDTO> diffLines =
                OrderLineBL.diffOrderLines(oldLines, bl.getDTO().getLines());

        //once we have inserted the mediation_records and
        //we have their row keys now we insert rows in JMR table
        BigDecimal amountForEvent = BigDecimal.ZERO;
        for(int index = 0; index < diffLines.size(); index++) {
            BigDecimal amount = diffLines.get(index).getAmount();
            amountForEvent = amountForEvent.add(amount);
        }
        mediationEventResult.setAmountForChange(amountForEvent);

        LOG.debug("diffLines = %s", diffLines);

        boolean billable = diffLines.size() > 0 ? true : false;
        if (billable) {
            //do processing for billable record
            mediationEventResult.setOrderLinedId(diffLines.get(0).getId());
            // generate NewQuantityEvents
            bl.checkOrderLineQuantities(
                    oldLines, bl.getDTO().getLines(),
                    companyId, bl.getDTO().getId(), true);

            LOG.debug("MediationEventResult = %s", mediationEventResult);
            return mediationEventResult;
        }
        return null;
    }

    @Override
    public void undoMediation(UUID processId) {
        MediationProcessService mediationProcessService = Context.getBean("mediationProcessService");
        MediationService service = Context.getBean(MediationService.BEAN_NAME);
        OrderDAS orderDAS = new OrderDAS();
        OrderLineDAS orderLineDAS = new OrderLineDAS();
        MediationProcess process = mediationProcessService.getMediationProcess(processId);
        int entityId = process.getEntityId();

        List<Integer> ordersToRemove = service.getOrdersForMediationProcess(processId);
        List<JbillingMediationRecord> recordsToRemove = new ArrayList<>();


        for (Integer orderId : ordersToRemove) {
            OrderBL orderBL = new OrderBL(orderId);
            if (orderBL.getEntity() != null && orderBL.getEntity().getDeleted() == 0) {
                //preserve old line to be able to compare with new lines
                List<OrderLineDTO> oldLines = OrderLineBL.copy(orderBL.getDTO().getLines());

                List<JbillingMediationRecord> orderRecordLines = service.getMediationRecordsForProcessAndOrder(processId, orderId);
                if (null != orderRecordLines) {
                    for (JbillingMediationRecord recordLine : orderRecordLines) {
                        OrderLineDTO orderLine = orderBL.getDTO().getLineById(recordLine.getOrderLineId());
                        orderLine.setQuantity(orderLine.getQuantity().subtract(recordLine.getQuantity()));
                        orderLine.setAmount(orderLine.getAmount().subtract(recordLine.getRatedPrice()));
                        orderLine.setTotalReadOnly(true);

                        if (null != orderLine.getQuantity()
                                && (0 == BigDecimal.ZERO.compareTo(orderLine.getQuantity()))) {
                            orderBL.getDTO().removeLineById(orderLine.getId());
                            orderLineDAS.delete(orderLine);
                        } else {
                            new OrderLineDAS().save(orderLine);
                        }
                        recordsToRemove.add(recordLine);
                    }
                }

                //recalculates the price
                OrderDTO order = orderBL.getDTO();
                Integer userId = order.getUserId();
                Integer languageId = order.getUser().getLanguage().getId();
                Integer currencyId = order.getUser().getCurrencyId();
                orderBL.processLines(order, languageId, entityId, userId, currencyId, null);

                // total amount, taxes ... based on new prices
                orderBL.recalculate(entityId);

                //fire new quantity events
                orderBL.checkOrderLineQuantities(
                        oldLines, orderBL.getDTO().getLines(),
                        entityId, orderId, true);

                //once we update all the order lines from the affected order
                //check if this order still has any lines left
                if (null == orderBL.getDTO().getLines()
                        || 0 == orderBL.getDTO().getLines().size()) {
                    orderDAS.delete(orderBL.getDTO());
                } else {
                    orderDAS.save(orderBL.getDTO());
                }
            }
        }

        //Delete The mediation process Data
        service.deleteErrorMediationRecords(processId);
        service.deleteDuplicateMediationRecords(processId);
        service.deleteMediationRecords(recordsToRemove);
        mediationProcessService.deleteMediationProcess(processId);
    }
}
