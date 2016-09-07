package com.sapienter.jbilling.server.ediTransaction;

import com.sapienter.jbilling.client.util.Constants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.customer.CustomerBL;
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentStatus;
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentWS;
import com.sapienter.jbilling.server.customerEnrollment.db.CustomerEnrollmentDAS;
import com.sapienter.jbilling.server.customerEnrollment.db.CustomerEnrollmentDTO;
import com.sapienter.jbilling.server.ediTransaction.db.EDIFileDAS;
import com.sapienter.jbilling.server.ediTransaction.db.EDIFileDTO;
import com.sapienter.jbilling.server.ediTransaction.task.EdiUtil;
import com.sapienter.jbilling.server.ediTransaction.task.InvoiceBuildTask;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.fileProcessing.fileGenerator.FlatFileGenerator;
import com.sapienter.jbilling.server.fileProcessing.fileParser.FlatFileParser;
import com.sapienter.jbilling.server.fileProcessing.xmlParser.FileFormat;
import com.sapienter.jbilling.server.invoice.IInvoiceSessionBean;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.invoice.NewInvoiceContext;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.PlanDAS;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.metafields.*;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.OrderStatusFlag;
import com.sapienter.jbilling.server.order.db.*;
import com.sapienter.jbilling.server.pricing.RouteBeanFactory;
import com.sapienter.jbilling.server.pricing.db.DataTableQueryDAS;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.db.RouteDAS;
import com.sapienter.jbilling.server.pricing.db.RouteDTO;
import com.sapienter.jbilling.server.pricing.strategy.PricingStrategy;
import com.sapienter.jbilling.server.process.BillingProcessBL;
import com.sapienter.jbilling.server.user.*;
import com.sapienter.jbilling.server.user.db.*;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.IWebServicesSessionBean;
import com.sapienter.jbilling.server.util.search.SearchResult;
import jbilling.RouteService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Created by aman on 16/10/15.
 */
//@Transactional( propagation = Propagation.REQUIRED )
public class EDITransactionSessionBean implements IEDITransactionBean {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(EDITransactionSessionBean.class));
    private IWebServicesSessionBean webServicesSessionSpringBean = Context.getBean(Context.Name.WEB_SERVICES_SESSION);

    private RouteService routeService = Context.getBean(Context.Name.ROUTE_SERVICE);
    IInvoiceSessionBean invoiceSessionBean=Context.getBean(Context.Name.INVOICE_SESSION);

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EDIFileWS getEDIFileWS(Integer ediFileId) {
        EDIFileWS fileWS = null;
        if (ediFileId != null) {
            fileWS = new EDIFileBL(ediFileId).getWS();
        }
        return fileWS;
    }

    @Override
    public EDIFileWS parseEDIFile(FlatFileParser fileParser) {
        EDIFileDTO ediFileDTO = fileParser.parseAndSaveFile();
        return new EDIFileBL(ediFileDTO).getWS();
    }

    @Override
    public boolean isUniqueKeyExistForFile(Integer entityId, Integer ediTypeId, Integer ediFileId, String key, String value, TransactionType transactionType) throws SessionInternalError {
        //  Every EDI file has a file field which value should be unique at entity level. e.g for enrollment and termination field name is TRANS_REF_NR and for invoice it is INVOICE_NR.
        if (StringUtils.trimToNull(value) == null) {
            throw new SessionInternalError("value for " + key + " should not be blank.");
        }
        EDIFileDAS ediFileDAS = new EDIFileDAS();
        if (!ediFileDAS.isRecordExistForFileFieldKeyAndValue(entityId, ediTypeId, ediFileId, key, value, transactionType)) {
            throw new SessionInternalError(key + " should be unique.");
        }
        return true;
    }

    //todo : should use WS instead of DTO
    public OrderDTO getOrder(Integer orderId) throws SessionInternalError {
        try {
            OrderDAS das = new OrderDAS();
            OrderDTO order = das.findNow(orderId);
            order.touch();
            return order;

        } catch (Exception e) {
            throw new SessionInternalError(e);
        }
    }


    /*Enrollment Response parser task methods*/

    public EDIFileWS getOutboundFileForCustomerEnrollment(String enrollmentId, Integer entityId, Integer ediTypeId) {
        EDIFileDTO ediFileDTO = new EDIFileDAS().getEDIFileForEnrollment(enrollmentId, entityId, ediTypeId);
        return getEDIFileWS(ediFileDTO.getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createCustomerAndOrder(CustomerEnrollmentWS customerEnrollmentWS, Map<String, String> ediFileData) throws SessionInternalError{
        Integer userId = createCustomer(customerEnrollmentWS, ediFileData);
        if (userId != null) {
            createOrder(userId, customerEnrollmentWS.getId());
        }
    }

    private Integer createCustomer(CustomerEnrollmentWS customerEnrollmentWS, Map<String, String> ediFileData) {
        /*TODO: Customer enrollment id can be change in customer enrollment ws.*/
        LOG.debug(" Creating customer for Enrollment Id" + customerEnrollmentWS.getId());
        Integer userId = null;
        try {
            if (customerEnrollmentWS.getStatus().equals(CustomerEnrollmentStatus.VALIDATED) && (customerEnrollmentWS.getCustomerId() == null || customerEnrollmentWS.getCustomerId() <= 0)) {
                LOG.debug("Customer enrollment is in validate mode and no customer has been created till yet.");
                Integer parentId = getParentId(customerEnrollmentWS);

                UserWS userWS = createUserWS(customerEnrollmentWS);
                LOG.debug("Parent Id is: " + parentId);
                userWS.setParentId(parentId);
                userWS.setInvoiceChild(true);
                userWS.setIsParent(false);
                userWS.setCreateCredentials(true);


                MetaFieldValueWS[] values = userWS.getMetaFields();
                List<MetaFieldValueWS> metaFieldValueWSList = new ArrayList<MetaFieldValueWS>(Arrays.asList(values));
                for(String key:ediFileData.keySet()){
                    String value=ediFileData.get(key);
                    metaFieldValueWSList.add(customerMetaFieldBinder(key, value, customerEnrollmentWS.getEntityId()));
                }

                Date effectiveDate = (Date) customerEnrollmentWS.getMetaFieldValue(FileConstants.CUST_ENROLL_AGREE_DT);
                metaFieldValueWSList.add(new MetaFieldValueWS(FileConstants.CUSTOMER_LAST_ENROLLMENT_METAFIELD_NAME, null, DataType.DATE, Boolean.FALSE, effectiveDate));

                userWS.setMetaFields(metaFieldValueWSList.toArray(new MetaFieldValueWS[metaFieldValueWSList.size()]));

                String notificationMethod=(String)customerEnrollmentWS.getMetaFieldValue("Notification Method");
                if(notificationMethod!=null){
                    Integer invoiceDeliveryMethodId=null;
                    switch (notificationMethod){
                        case "Email":
                            invoiceDeliveryMethodId=1;
                            break;
                        case "Paper":
                            invoiceDeliveryMethodId=2;
                            break;
                        case "Both":
                            invoiceDeliveryMethodId=3;
                            break;

                    }

                    if(invoiceDeliveryMethodId!=null)
                        userWS.setInvoiceDeliveryMethodId(invoiceDeliveryMethodId);

                }
                userId = webServicesSessionSpringBean.createUserWithCompanyId(userWS, customerEnrollmentWS.getEntityId());
                customerEnrollmentWS.setCustomerId(userId);
            } else {
                if (customerEnrollmentWS.getStatus().equals(CustomerEnrollmentStatus.ENROLLED) && customerEnrollmentWS.getCustomerId() > 0) {
                    LOG.error("Customer has already been enrolled");
                    throw new SessionInternalError("Customer has already been created.");
                } else {
                    LOG.error("Customer is neither validated nor enrolled.");
                    throw new SessionInternalError("Customer is not validated. Please validate customer first.");
                }
            }

        } catch (SessionInternalError sie) {
            throw sie;
        } catch (Exception ex) {
            LOG.error("In EnrollmentResponseParserTask could not create customer " + ex.getMessage());
            throw new SessionInternalError("problem in creating child customer");
        }

        return userId;
    }

    private MetaFieldValueWS customerMetaFieldBinder(String name, String value, Integer entityId){

        MetaFieldValueWS metaFieldValue=new MetaFieldValueWS();
        metaFieldValue.setFieldName(name);
        metaFieldValue.setMandatory(true);
        metaFieldValue.setDisabled(false);
        EntityType[] entityType=new EntityType[]{EntityType.CUSTOMER};
        List<MetaField>  metaFields= MetaFieldBL.getAvailableFieldsList(entityId, entityType);
        for(MetaField metaField:metaFields){
            if(metaField.getName().equals(name)){
                DataType dataType=metaField.getDataType();
                metaFieldValue.setDataType(dataType);
                if(dataType==DataType.DATE){
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                    Date mfv=formatter.parseDateTime(value).toDate();
                    metaFieldValue.setValue(mfv);
                }else if(dataType==DataType.INTEGER){
                    metaFieldValue.setDataType(DataType.INTEGER);
                    Integer mfv=Integer.parseInt(value);
                    metaFieldValue.setValue(mfv);
                }else if(dataType==DataType.DECIMAL){
                    metaFieldValue.setDataType(DataType.DECIMAL);
                    BigDecimal mfv=new BigDecimal(value);
                    metaFieldValue.setValue(mfv);
                }else {
                    metaFieldValue.setDataType(DataType.STRING);
                    metaFieldValue.setValue(value);
                }
                break;
            }
        }

        return metaFieldValue;
    }


    private Integer getParentId(CustomerEnrollmentWS customerEnrollmentWS) {
        Integer parentId = null;
        if (customerEnrollmentWS.getParentUserId() == null) {
//                    Check if parent customer not exist.
            if (customerEnrollmentWS.getParentEnrollmentId() != null) {
//                        check if parent enrollment exist.
                CustomerEnrollmentWS parentCustomerEnrollmentWS = webServicesSessionSpringBean.getCustomerEnrollment(customerEnrollmentWS.getParentEnrollmentId());
                if (parentCustomerEnrollmentWS.getParentUserId() != null) {
//                            If parent customer exist in parent enrollment.
                    parentId = parentCustomerEnrollmentWS.getParentUserId();
                } else {
//                            else Create parent Customer.
                    parentId = createParentCustomer(customerEnrollmentWS);
                    parentCustomerEnrollmentWS.setParentUserId(parentId);
                    webServicesSessionSpringBean.createUpdateEnrollment(parentCustomerEnrollmentWS);
                }
            } else {
//                        parent customer and parent enrollment does not exist.
                parentId = createParentCustomer(customerEnrollmentWS);
                customerEnrollmentWS.setParentUserId(parentId);
            }
        } else {
//                    parent customer already exist.
            parentId = customerEnrollmentWS.getParentUserId();
        }
        if (parentId == null) {
            throw new SessionInternalError("Could not create parent customer for enrollment.");
        }
        return parentId;
    }

    private UserWS createUserWS(CustomerEnrollmentWS customerEnrollmentWS) {
        LOG.debug("In createUserWS customerEnrollmentWS  " + customerEnrollmentWS);
        UserWS userWS = new UserWS();
        userWS.setUserName(createCustomerName(customerEnrollmentWS, false));
        userWS.setAccountTypeId(customerEnrollmentWS.getAccountTypeId());
        userWS.setEntityId(customerEnrollmentWS.getEntityId());
        userWS.setMainRoleId(Constants.ROLE_CUSTOMER);
        userWS.setMetaFields(customerEnrollmentWS.getMetaFields());
        return userWS;
    }

    private Integer createParentCustomer(CustomerEnrollmentWS customerEnrollmentWS) {
        UserWS parentUser = createParentUserWS(customerEnrollmentWS);
        if (parentUser == null) {
            LOG.error("Parent customer not created");
            throw new SessionInternalError("unable to create parent customer as parent company does not exist");
        }
        try {
            return webServicesSessionSpringBean.createUserWithCompanyId(parentUser, parentUser.getEntityId());
        } catch (SessionInternalError sie) {
            throw sie;
        } catch (Exception ex) {
            throw new SessionInternalError("Problem occur while trying to create parent customer");
        }
    }

    private UserWS createParentUserWS(CustomerEnrollmentWS customerEnrollmentWS) {
        LOG.debug("In Parent createUserWS customerEnrollmentWS  " + customerEnrollmentWS);
        UserWS userWS = new UserWS();
        userWS.setUserName(createCustomerName(customerEnrollmentWS, true));

        Integer parentCompanyId = new CompanyDAS().getParentCompanyId(customerEnrollmentWS.getEntityId());
        if (parentCompanyId == null) {
            LOG.error("No parent company found");
            throw new SessionInternalError("Could not found parent Company Id.");
        }

        userWS.setEntityId(parentCompanyId);
        userWS.setMainRoleId(Constants.ROLE_CUSTOMER);
        userWS.setMetaFields(customerEnrollmentWS.getMetaFields());
        generateMetaField(customerEnrollmentWS, userWS);
        userWS.setIsParent(true);
        return userWS;
    }

    private String createCustomerName(CustomerEnrollmentWS enrollmentWS, boolean isParentCustomer) {
//        Customer name will follow this rule.
//        For child company:  (Customer name)-(Commodity Name)-(enrollment id). e.g: Vivek Yadav-ELECTRICITY-201
//        For parent company: (Customer name)-(Time millisecond). e.g: Vivek Yadav-25647892225
        StringBuffer customerName = new StringBuffer();
        if (enrollmentWS.getAccountTypeName().equals(FileConstants.RESIDENTIAL_ACCOUNT_TYPE)) {
            AccountInformationTypeDTO accountInformationTypeDTO = new AccountInformationTypeDAS().findByName(FileConstants.CUSTOMER_INFORMATION_AIT, enrollmentWS.getEntityId(), enrollmentWS.getAccountTypeId());
            String name = (String) enrollmentWS.getMetaFieldValueByGroupId(FileConstants.NAME, accountInformationTypeDTO.getId());
            customerName.append(name);
        } else if (enrollmentWS.getAccountTypeName().equals(FileConstants.COMMERCIAL_ACCOUNT_TYPE)) {
            AccountInformationTypeDTO accountInformationTypeDTO = new AccountInformationTypeDAS().findByName(FileConstants.BUSINESS_INFORMATION_AIT, enrollmentWS.getEntityId(), enrollmentWS.getAccountTypeId());
            String name = (String) enrollmentWS.getMetaFieldValueByGroupId(FileConstants.NAME, accountInformationTypeDTO.getId());
            customerName.append(name);
        }

        if (!isParentCustomer) {
            customerName.append("-")
                    .append(enrollmentWS.getMetaFieldValue(FileConstants.COMMODITY))
                    .append("-")
                    .append(enrollmentWS.getId());
        } else {
            customerName.append("-")
                    .append(System.currentTimeMillis());
        }

        return customerName.toString();
    }

    private void createOrder(Integer userId, Integer customerEnrollmentId) {
        LOG.debug("Started creating order.");
        /*TODO: Order should create using order change ws.*/

        CustomerEnrollmentWS enrollmentWS = webServicesSessionSpringBean.getCustomerEnrollment(customerEnrollmentId);
        LOG.debug("Customer enrollment ws is: " + enrollmentWS);

        UserDTO user = new UserDAS().findNow(userId);
        // create the first order for customer.
        OrderDTO order = new OrderDTO();
        OrderPeriodDTO period = new OrderPeriodDAS().findOrderPeriod(user.getEntity().getId(), 1, 1);
        order.setOrderPeriod(period);

        OrderBillingTypeDTO type = new OrderBillingTypeDTO();
        type.setId(com.sapienter.jbilling.server.util.Constants.ORDER_BILLING_POST_PAID);
        order.setOrderBillingType(type);
        order.setCreateDate(Calendar.getInstance().getTime());
        order.setCurrency(user.getCurrency());
//        Added active since date using effective date metafield
        Date effectiveDate = (Date) enrollmentWS.getMetaFieldValue(FileConstants.CUST_ENROLL_AGREE_DT);
        LOG.debug("effective date is: " + effectiveDate);
        order.setActiveSince(effectiveDate);

//        Added Until date by adding duration metafield in effective date metafield.
        Integer duration = 0;
        try {
            duration = Integer.parseInt((String) enrollmentWS.getMetaFieldValue(FileConstants.DURATION));

        } catch (NumberFormatException nfe) {
            LOG.error("Could not convert duration into integer " + nfe);
            throw new SessionInternalError("Could not convert duration into integer");
        }
        LOG.debug("duration is: " + duration);
        if (effectiveDate != null) {
            Calendar untilDate = Calendar.getInstance();
            untilDate.setTime(effectiveDate);
            untilDate.add(Calendar.MONTH, duration);
            LOG.debug("untilDate is: " + untilDate);
            order.setActiveUntil(untilDate.getTime());
        } else {
            throw new SessionInternalError("Effective date must not be null");
        }


        order.setBaseUserByUserId(user);
        String plan = (String) enrollmentWS.getMetaFieldValue(FileConstants.PLAN);
        LOG.debug("Plan is: " + plan);
//        Get plan product using plan met metafield.
        ItemDAS itemDAS = new ItemDAS();
        ItemDTO item = itemDAS.findItemByInternalNumber(plan, user.getEntity().getId());

        Integer languageId = user.getLanguageIdField();
        String description = "";
        if (item == null) {
            throw new SessionInternalError("Plan does not exist. Please create a plan");
        }
        description = item.getDescription(languageId);

        OrderLineDTO line = new OrderLineDTO();
        line.setDescription(description);
        line.setItemId(item.getId());
        line.setQuantity(1);
        line.setPrice(item.getPrice(new Date(), user.getEntity().getId()).getRate());
        line.setTypeId(com.sapienter.jbilling.server.util.Constants.ORDER_LINE_TYPE_ITEM);
        line.setPurchaseOrder(order);
        order.getLines().add(line);

        OrderBL orderBL = new OrderBL();
        orderBL.set(order);

        // create the db record
        Integer orderId = orderBL.create(user.getEntity().getId(), null, order);

        LOG.debug("New Order has been created with id: " + orderId);
    }

    private void generateMetaField(CustomerEnrollmentWS customerEnrollmentWS, UserWS user) {
        List<AccountTypeDTO> accountTypeDTOs = new AccountTypeDAS().findAll(user.getEntityId());
        //Find the enrollment account from parent company accounts list
        //I am assuming here that account type name will be same for parent and child
        AccountTypeWS enrollmentAccountType = webServicesSessionSpringBean.getAccountType(customerEnrollmentWS.getAccountTypeId());
        AccountTypeWS expectedParentAccountType = null;
        for (AccountTypeDTO accountTypeDTO : accountTypeDTOs) {
            AccountTypeWS targetAccountType = webServicesSessionSpringBean.getAccountType(accountTypeDTO.getId());
            if (enrollmentAccountType.getDescriptions().get(0).getContent().equals(targetAccountType.getDescriptions().get(0).getContent())) {
                user.setAccountTypeId(targetAccountType.getId());
                expectedParentAccountType = targetAccountType;
                break;
            }
        }

        if (expectedParentAccountType == null) {
            LOG.debug("No account type found.");
            throw new SessionInternalError("No Account type found");
        }

        List<MetaFieldValueWS> metaFieldWSList = new LinkedList<MetaFieldValueWS>();
        AccountInformationTypeWS[] accountInformationTypeWSes = webServicesSessionSpringBean.getInformationTypesForAccountType(enrollmentAccountType.getId());
        AccountInformationTypeWS[] requiredAccountInformationTypeWSes = webServicesSessionSpringBean.getInformationTypesForAccountType(expectedParentAccountType.getId());
        for (AccountInformationTypeWS accountInformationTypeDTO : accountInformationTypeWSes) {
            for (AccountInformationTypeWS aitRequired : requiredAccountInformationTypeWSes) {

                if (aitRequired.getName().equals(accountInformationTypeDTO.getName())) {
                    LOG.debug("Enrollment's AIT name: " + accountInformationTypeDTO.getName());
                    LOG.debug("Parent Customer's AIT name: " + accountInformationTypeDTO.getName());
                    for (MetaFieldValueWS metaFieldValue : customerEnrollmentWS.getMetaFields()) {
                        int aitId = accountInformationTypeDTO.getId();
                        if (metaFieldValue.getGroupId() == aitId) {
                            LOG.debug("Meta field has Copied to parent " + metaFieldValue.getFieldName());
                            MetaFieldValueWS field = metaFieldValue.clone();
                            field.setGroupId(aitRequired.getId());
                            metaFieldWSList.add(field);
                        }
                    }
                    break;
                }
            }
        }
        MetaFieldValueWS[] metaFieldValueWSes = new MetaFieldValueWS[metaFieldWSList.size()];
        user.setMetaFields(metaFieldWSList.toArray(metaFieldValueWSes));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CompanyWS getCompanyWS(Integer entityId) throws SessionInternalError {
        CompanyDTO companyDTO = new CompanyDAS().find(entityId);
        return EntityBL.getCompanyWS(companyDTO);
    }

    /**
     * Initiate an Esco Termiation for the customer.
     *
     * @param userId
     * @param reasonCode
     * @param terminationDate
     * @throws SessionInternalError
     */
    public void initiateTermination(Integer userId, String reasonCode, Date terminationDate) throws SessionInternalError {
        UserBL bl = new UserBL(userId);
        UserWS user = bl.getUserWS();

        if(EdiUtil.userAlreadyTerminatedOrDropped(user)) {
            throw new SessionInternalError("User already terminated - "+userId,
                    new String[] { "UserWS,termination,validation.error.user.already.terminated" });
        }

        //check that the termination date is after the enrollment date
        UserDTO userDTO = bl.getEntity();
        Date enrollmentDate = new CustomerEnrollmentDAS().findCustomerEnrollmentDate(userDTO.getEntity().getId(),
                userDTO.getCustomer().getCustomerAccountInfoTypeMetaField(FileConstants.UTILITY_CUST_ACCT_NR).getMetaFieldValue().getValue().toString());
        if(enrollmentDate != null && enrollmentDate.after(terminationDate)) {
            throw new SessionInternalError("User ["+userId+"] can not be terminated ["+terminationDate+"] before enrollment date ["+enrollmentDate+"]",
                    new String[] { "UserWS,termination,validation.error.termination.before.enrollment" });
        }

        CompanyDTO company = bl.getEntity().getCompany();

        EscoTerminationRequest request = new EscoTerminationRequest(company, bl.getEntity().getCustomer(), reasonCode, terminationDate);
        request.generateFile();

        MetaFieldHelper.setMetaField(company.getId(), bl.getEntity().getCustomer(), FileConstants.TERMINATION_META_FIELD, FileConstants.TERMINATION_ESCO_INITIATED);
    }

    /*EDI file generation task*/

    public String getCommodityCode(String internalNumber, Integer entityId) {
        ItemDAS itemDAS = new ItemDAS();
        ItemDTO itemDTO = itemDAS.findItemByInternalNumber(internalNumber, entityId);
        if (itemDTO != null) {
            for (MetaFieldValue metaFieldValue : itemDTO.getMetaFields()) {
                if (metaFieldValue.getField() != null && metaFieldValue.getField().getName().equals(FileConstants.COMMODITY)) {
                    return (String) metaFieldValue.getValue();
                }
            }
        } else {
            throw new SessionInternalError("Item did not found for product code " + internalNumber);
        }
        return null;
    }

    /* calculate future Rate change date using LDC Calendar
    *
    * @params date
    * @params cycleNumber customer cycle number
    * @params entityId
    *
    * @return next customer Rate Change Date
    * */
    public DateTime getRateChangeDate(DateTime date, Integer cycleNumber, Integer entityId) {
        LOG.debug("Calculating rate change date");
        CompanyDTO  companyDTO = new CompanyDAS().find(entityId);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM-yyyy").withLocale(Locale.ENGLISH);
        String trip=formatter.print(date).toUpperCase();
        LOG.debug("Monthly trip : "+trip);
        MetaFieldValue leadTime1MetaField=companyDTO.getMetaField(FileConstants.COMPANY_LEAD_TIME_1_META_FIELD_NAME);
        if(leadTime1MetaField.getValue()==null)
            throw new SessionInternalError("Configuration issue : Lead time 1 cannot be blank" );
        Integer leadTime1=(Integer)leadTime1MetaField.getValue();
        LOG.debug("Lead Time 1 : " + leadTime1);
        MetaFieldValue leadTime2MetaField=companyDTO.getMetaField(FileConstants.COMPANY_LEAD_TIME_2_META_FIELD_NAME);
        if(leadTime1MetaField.getValue()==null)
            throw new SessionInternalError("Configuration issue : Lead time 2 cannot be blank" );
        Integer leadTime2=(Integer)leadTime2MetaField.getValue();
        LOG.debug("Lead Time 2 : "+leadTime2);
        MetaFieldValue bufferTimeMetaFiledValue=companyDTO.getMetaField(FileConstants.COMPANY_BUFFER_TIME_META_FIELD_NAME);
        if(leadTime1MetaField.getValue()==null)
            throw new SessionInternalError("Configuration issue : Buffer Time cannot be blank" );
        Integer bufferTime=(Integer)bufferTimeMetaFiledValue.getValue();
        LOG.debug("bufferTime: " + bufferTime);
        MetaFieldValue calendarNameMetaField=companyDTO.getMetaField(FileConstants.COMPANY_CALENDAR_META_FIELD_NAME);
        if(calendarNameMetaField.getValue()==null)
            throw new SessionInternalError("Configuration issue : Calendar is not configured" );
        String calendarName=(String)calendarNameMetaField.getValue();
        String meterDate=getCalendarDate(entityId, cycleNumber, trip, calendarName.trim());
        LOG.debug("Searched Calendar day: " + meterDate);
        String meterDay=meterDate.split("/")[1];
        DateTime dateTime=new DateTime();
        DateTimeFormatter format = DateTimeFormat.forPattern("MM/dd/yyyy");
        DateTime expectedMeterReadDate=format.parseDateTime(""+date.getMonthOfYear() +"/"+ meterDay +"/"+ dateTime.getYear());
        LOG.debug("Meter Read collection date : " + expectedMeterReadDate);
        expectedMeterReadDate=expectedMeterReadDate.plusDays(leadTime1).minusDays(leadTime2).minusDays(bufferTime);

        // if calculated rate change date is less then current date then calculate next future Rate Change Date.
        if(expectedMeterReadDate.toDate().compareTo(new Date())<0){
            return getRateChangeDate(date.plusMonths(1), cycleNumber, entityId);
        }
        LOG.debug("Rate Change send date : " + expectedMeterReadDate);
        return expectedMeterReadDate;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    private String getCalendarDate(Integer companyId, Integer cycleNumber, String trip, String calendarName){

        String columnName="date";
        RouteDTO routeDTO=new RouteDAS().getRoute(companyId, calendarName);
        if(routeDTO==null){
            throw new SessionInternalError("Configuration Issue: No Calender configured with name "+calendarName);
        }
        RouteBeanFactory factory = new RouteBeanFactory(routeDTO);
        List<String> columnNames = factory.getTableDescriptorInstance().getColumnsNames();

        Map<String, String> map = new HashMap<>();
        map.put("trip",trip);
        map.put("cycle_number", cycleNumber+"");

        SearchResult<String> result = routeService.getFilteredRecords(routeDTO.getId(), map);

        if(result.getRows().size()==0){
            throw new SessionInternalError("No record found for trip( "+trip+") and cycle_number ("+cycleNumber+") in calendar "+calendarName );
        }

        Integer searchNameIdx = columnNames.indexOf(columnName);
        if(searchNameIdx.equals(-1)){
            throw new SessionInternalError("Calendar "+calendarName +" have not "+columnName+" column ");
        }
        return result.getRows().get(0).get(searchNameIdx);
    }


    /*
    * This method is find  rate for rate code
    * the closest but lower rate's ratecode
    *
    * @params companyId
    * @params rateCode
    *
    * @return rate
    * */

     public BigDecimal getRateByRateCode(Integer companyId, String rateCode){
        CompanyDTO companyDTO=new CompanyDAS().find(companyId);
        MetaFieldValue rateCodeTableMetaField=companyDTO.getMetaField(FileConstants.RATE_CODE_TABLE_NAME);

        if(rateCodeTableMetaField.getValue()==null){
            LOG.debug("RateCode table is not configured so returning the price");
            return new BigDecimal(rateCode);
        }

        RouteDTO routeDTO=new RouteDAS().getRoute(companyId, (String)rateCodeTableMetaField.getValue());
        Map map=new HashMap();
        map.put("RateReadyCode", rateCode);

        RouteBeanFactory factory = new RouteBeanFactory(routeDTO);
        List<String> columnNames = factory.getTableDescriptorInstance().getColumnsNames();
        Integer searchRateIdx = columnNames.indexOf("Rate".toLowerCase());
        SearchResult<String> result= routeService.getFilteredRecords(routeDTO.getId(), map);
        if(searchRateIdx.equals(-1)){
            throw new SessionInternalError("Rate code data table did not contains Rate column");
        }

        if(result.getRows().size()!=0){
            String rate=result.getRows().get(0).get(searchRateIdx);
            if(rate!=null){
                return new BigDecimal(rate);
            }
            /*if rateCode not found in a Rate Code Data Table then guess this is price and validate it.
            if validate then return, otherwise throw the exception*/
        }else if (rateCode.matches("^\\d*(\\.\\d+)$")) {
            return new BigDecimal(rateCode);
        }else{
            throw new SessionInternalError("Rate code not found in Rate code data table: "+rateCode);
        }

        return null;
    }

    /*
    * If ratecode is found for the rate then it returns that rate code else return the
    * the closest but lower rate's ratecode
    *
    * @params companyId
    * @params adjustedPrice
    * @params price
    *
    * @return rateCode
    * */
    @Transactional(propagation = Propagation.REQUIRED)
    public String getRateCode(Integer companyId, Double price){

        CompanyDTO companyDTO=new CompanyDAS().find(companyId);
        MetaFieldValue rateCodeTableMetaField=companyDTO.getMetaField(FileConstants.RATE_CODE_TABLE_NAME);

        RouteDTO routeDTO=new RouteDAS().getRoute(companyId, (String)rateCodeTableMetaField.getValue());

        //if rate code is not configured then send rate.
        if(routeDTO==null){
            return price+"";
        }

        //finding the closest but small rate's ratecode
        String rateCode=new DataTableQueryDAS().getRateCode(routeDTO.getTableName(), price.toString());
        LOG.debug("Searched Rate Code "+rateCode);
        if(rateCode==null){
            throw new SessionInternalError("No rate code found for rate "+price, new String[]{"No rate code found for rate "+price});
        }
        return rateCode;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public EDIFileDTO generateEDIFile(FileFormat fileFormat, Integer entityId, String name,List<Map<String, String>> recordMapList){
        FlatFileGenerator generator = new FlatFileGenerator(fileFormat, entityId, name, recordMapList);
        return generator.validateAndSaveInput();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(UserDTO userDTO){
        new UserDAS().save(userDTO);
    }

    public Integer generateInvoice(String meterReadId, Integer companyId, String INVOICE_NR) {
        PlatformTransactionManager transactionManager = Context.getBean(Context.Name.TRANSACTION_MANAGER);
        TransactionStatus transaction = null;
        Integer invoiceId;
        try {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            def.setName("InvoiceRead-Process-File");
            transaction = transactionManager.getTransaction(def);
            invoiceId = generateInvoiceWithTransaction(meterReadId, companyId, INVOICE_NR);
            transactionManager.commit(transaction);
        }catch (Throwable ex){
            if((transaction != null) && !transaction.isCompleted()) {
                LOG.debug("Transaction not completed, initiate rollback");
                transactionManager.rollback(transaction);
            }
            throw ex;
        }
        return invoiceId;
    }

    private Integer generateInvoiceWithTransaction(String meterReadId, Integer companyId, String INVOICE_NR) {
        OrderDTO order=new OrderDAS().findOrderByMetaFieldValue(companyId, "edi_file_id", meterReadId);
        if (order==null) {
            LOG.error("No Order found for Transfer_NR");
            throw new SessionInternalError("No Order found for Transfer_NR");
        }

        Integer invoiceId = null;

        // JBIIE-1080: If order has generated invoice then delete first and update order status to suspended
        Boolean hasGeneratedInvoice=order.getOrderProcesses().size()>0;
        if(hasGeneratedInvoice){
            for(OrderProcessDTO orderProcess:order.getOrderProcesses()){
                invoiceSessionBean.delete(orderProcess.getInvoice().getId(), null);
            }

            Integer orderStatus = new OrderStatusDAS().getDefaultOrderStatusId(
                    OrderStatusFlag.NOT_INVOICE,  order.getUser().getEntity().getId());
            order.setStatusId(orderStatus);
            new OrderDAS().save(order);

        }

        // NGES: for invoice read, order should be in suspended state to generate the invoice.
        if(order.getOrderStatus().getId() != new OrderStatusDAS().getDefaultOrderStatusId(OrderStatusFlag.NOT_INVOICE, companyId)){
            LOG.error("Status of the order should be Suspended. Order Id: "+order.getId());
            throw new SessionInternalError("Status of the order should be Suspended. Order Id: "+order.getId());
        }

        OrderBL orderBL = new OrderBL(order);
        orderBL.setStatus(null, new OrderStatusDAS().getDefaultOrderStatusId(OrderStatusFlag.INVOICE, companyId));
        //Generate Invoice
        if (invoiceId == null) {

            BillingProcessBL process = new BillingProcessBL();
            try {
                NewInvoiceContext template = new NewInvoiceContext();


                List<MetaFieldValue> metaFields = new LinkedList<MetaFieldValue>();
                EntityType[] entityType = {EntityType.INVOICE};
                Map<String, MetaField> fieldMap = new MetaFieldBL().getAvailableFields(companyId, entityType);
                for (Map.Entry<String, MetaField> field : fieldMap.entrySet()) {
                    if (field.getKey().equals("INVOICE_NR")) {
                        MetaField field1 = field.getValue();
                        MetaFieldValue value = MetaFieldBL.createValueFromDataType(field1, INVOICE_NR, DataType.STRING);
                        metaFields.add(value);
                        continue;
                    }

                    if (field.getKey().equals(FileConstants.META_FIELD_METER_READ_FILE)) {
                        MetaField field1 = field.getValue();
                        MetaFieldValue value = MetaFieldBL.createValueFromDataType(field1,Integer.parseInt(meterReadId), DataType.INTEGER);
                        metaFields.add(value);
                    }
                }
                template.setMetaFields(metaFields);

                InvoiceDTO invoice = process.generateInvoice(order.getId(), null, template, null);
                invoiceId = invoice.getId();
            } catch (Exception e) {
                LOG.error("Exception occurred at invoice creation : "+e.getMessage());
                throw new SessionInternalError("Exception occurred at invoice creation : "+e.getMessage());
            }

        } else {
            InvoiceWS invoiceWS = webServicesSessionSpringBean.getInvoiceWS(invoiceId);
            webServicesSessionSpringBean.applyOrderToInvoice(order.getId(), invoiceWS);
        }
        return invoiceId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BigDecimal getPlanItemPrice(UserDTO userDTO, Integer itemId, Integer companyId ){
        OrderDTO order = new OrderDTO();
        OrderPeriodDTO period = new OrderPeriodDAS().find(com.sapienter.jbilling.server.util.Constants.ORDER_PERIOD_ONCE);
        order.setOrderPeriod(period);
        OrderBillingTypeDTO type = new OrderBillingTypeDTO();
        type.setId(com.sapienter.jbilling.server.util.Constants.ORDER_BILLING_POST_PAID);
        order.setOrderBillingType(type);
        order.setCreateDate(Calendar.getInstance().getTime());
        order.setCurrency(userDTO.getCurrency());
        order.setActiveSince(new Date());
        order.setActiveUntil(new Date());
        order.setBaseUserByUserId(userDTO);
        order.setStatusId(new OrderStatusDAS().getDefaultOrderStatusId(
                OrderStatusFlag.INVOICE, userDTO.getEntity().getId()));
        OrderLineDTO line = new OrderLineDTO();
        line.setDescription("111");
        line.setItemId(itemId);
        line.setQuantity(1);
        line.setTypeId(com.sapienter.jbilling.server.util.Constants.ORDER_LINE_TYPE_ITEM);
        line.setPurchaseOrder(order);
        order.getLines().add(line);

        OrderBL orderBL = new OrderBL();
        orderBL.processLines(order, userDTO.getLanguageIdField(), companyId, userDTO.getId(), userDTO.getCurrencyId(),"");
        return order.getLines().get(0).getPrice();
    }

    @Override
    public boolean hasPlanSendRateChangeDaily(PlanDTO planDTO) {
        MetaFieldValue fieldValue = planDTO.getMetaField(FileConstants.SEND_RATE_CHANGE_DAILY);
        return (fieldValue != null && (Boolean) fieldValue.getValue()) ? true : false;
    }

    @Override
    public boolean hasPlanSendRateChangeDaily(UserDTO userDTO) {
        UserWS userWS = new UserBL(userDTO.getId()).getUserWS();
        Object planValue = null;
        for (MetaFieldValueWS metaFieldValueWS : userWS.getMetaFields()) {
            if (metaFieldValueWS.getFieldName().equals(FileConstants.PLAN)) {
                LOG.debug("Meta field: " + FileConstants.PLAN + " found for: " + metaFieldValueWS.getValue());
                planValue = metaFieldValueWS.getValue();
                break;
            }
        }
        if (planValue == null) {
            throw new SessionInternalError("Customer should be subscribed to plan");
        }
        ItemDTO item = new ItemDAS().findItemByInternalNumber(planValue.toString(), userWS.getEntityId());
        PlanDTO planDTO = new PlanDAS().findPlanByItemId(item.getId());
        return hasPlanSendRateChangeDaily(planDTO);
    }

    public String calculateRate(PlanDTO planDTO,CustomerEnrollmentDTO enrollmentDTO, Integer itemId,Integer entityId){
        //create dummy order
        OrderDTO order = new OrderDTO();
        OrderPeriodDTO period = new OrderPeriodDAS().find(com.sapienter.jbilling.server.util.Constants.ORDER_PERIOD_ONCE);
        order.setOrderPeriod(period);
        OrderBillingTypeDTO type = new OrderBillingTypeDTO();
        type.setId(com.sapienter.jbilling.server.util.Constants.ORDER_BILLING_POST_PAID);
        order.setOrderBillingType(type);
        order.setCreateDate(Calendar.getInstance().getTime());
        order.setActiveSince(new Date());
        OrderLineDTO line = new OrderLineDTO();
        line.setDescription("111");
        line.setItemId(itemId);
        line.setQuantity(1);
        line.setTypeId(com.sapienter.jbilling.server.util.Constants.ORDER_LINE_TYPE_ITEM);
        line.setPurchaseOrder(order);
        order.getLines().add(line);

        List<PricingField> fields=new ArrayList<PricingField>();
        MetaFieldValue metaFieldValue=enrollmentDTO.getMetaField(FileConstants.CUST_ENROLL_AGREE_DT);
        Date effectiveDate=(Date)metaFieldValue.getValue();
        order.setActiveUntil(effectiveDate);
        PricingResult pricingResult=new PricingResult(itemId,new BigDecimal("1"), null, 1);

        if(planDTO.getPlanItems()==null || planDTO.getPlanItems().size()==0){
            throw new SessionInternalError("Plan should have plan item", new String[]{"No plan item found for plan "+planDTO.getDescription()+" "});
        }
        PriceModelDTO priceModelDTO=planDTO.getPlanItems().get(0).getModels().get(planDTO.getPlanItems().get(0).getModels().firstKey());
        PricingStrategy strategy=priceModelDTO.getType().getStrategy();
        strategy.applyTo(order, pricingResult, fields, priceModelDTO, new BigDecimal("1"), null, false);
        Double price=new Double(pricingResult.getPrice().stripTrailingZeros().toPlainString());
        return getRateCode(entityId, price);
    }

    /*
    * This method check is valid customer/enrollment exist in the system for an account number
    * */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean isCustomerExistForAccountNumber(CustomerEnrollmentDTO enrollmentDTO){
        CustomerEnrollmentDAS customerEnrollmentDAS=new CustomerEnrollmentDAS();
        String customerAccountNumber=(String)enrollmentDTO.getMetaFields().stream().filter((MetaFieldValue metaFieldValue) -> {return metaFieldValue.getField().getName().equals(FileConstants.CUSTOMER_ACCOUNT_KEY);}).findFirst().get().getValue();

        //finding user
        UserDTO user=new UserDAS().findUserByAccountNumber(enrollmentDTO.getCompany().getId(), FileConstants.CUSTOMER_ACCOUNT_KEY, customerAccountNumber);
        LOG.debug("User of account number "+customerAccountNumber+" : "+user);

        if(user !=null ){
            throw new SessionInternalError("A Customer already exist for account number "+customerAccountNumber, new String[]{"customer.UTILITY.CUST.ACCT.NR.duplicate,"+customerAccountNumber});
        }

        Long enrollmentCount=customerEnrollmentDAS.countEnrollmentByAccountNumber(enrollmentDTO.getCompany().getId(), FileConstants.CUSTOMER_ACCOUNT_KEY, customerAccountNumber, CustomerEnrollmentStatus.VALIDATED);
        LOG.debug(enrollmentCount+" Validated Enrollment found for account number " + customerAccountNumber);
        // enrollmentCount will be one if there is no enrollment for a account number because there is one enrollment object in the hibernate session.
        if(enrollmentCount>0){
            throw new SessionInternalError("A Enrollment already exist for account number "+customerAccountNumber, new String[]{"enrollment.UTILITY.CUST.ACCT.NR.duplicate,"+customerAccountNumber});
        }
        return true;
    }

    /*
    * this method return the non drop customer from the database
    * @params companyId
    * @params metaFieldName metafield name for customer account number
    * @params customerAccountNumber  customer utility account number
    * */
    public UserDTO findUserByAccountNumber(Integer entityId, String metaFieldName, String metaFieldValue, Boolean isFinal){

        UserDAS userDAS=new UserDAS();
        List<Integer> dropCustomerList= userDAS.findDropCustomers(entityId, metaFieldName, metaFieldValue);

        UserDTO userDTO=userDAS.findUserByAccountNumber(entityId, metaFieldName, metaFieldValue);

        if(isFinal){
            // if final request and customer exit with status termination processing and waiting for the final meter read then find that customer
            if(userDTO!=null){
                MetaFieldValue terminationMetafieldValue=userDTO.getCustomer().getMetaField(FileConstants.TERMINATION_META_FIELD);
                LOG.debug("termination Metafield Value : "+terminationMetafieldValue);
                if(terminationMetafieldValue!=null && terminationMetafieldValue.getValue().equals(FileConstants.TERMINATION_PROCESSING)){
                    return userDTO;
                }
            }
            // find latest drop customer
            if(dropCustomerList.size()>0){
                LOG.debug("Drop customer id : "+dropCustomerList.get(0));
                return userDAS.find(dropCustomerList.get(0));
            }

            // else return null if no drop customer or customer in under the terminassion process.
            throw new SessionInternalError("You can upload a final meter for customer which is waiting for final meter read or system have drop customer for the given account number");
        }

        //if request is not final, finding  a valid non drop user in the system
        if(userDTO!=null){
            return userDTO;
        }

        if(dropCustomerList.size()>0){
            return userDAS.find(dropCustomerList.get(0));
        }

        throw new SessionInternalError("User not found for account number "+metaFieldValue);

    }


    public UserDTO findUserByAccountNumber(Integer companyId,String metaFieldName, String customerAccountNumber){
        return findUserByAccountNumber(companyId, metaFieldName, customerAccountNumber, false);
    }

}
