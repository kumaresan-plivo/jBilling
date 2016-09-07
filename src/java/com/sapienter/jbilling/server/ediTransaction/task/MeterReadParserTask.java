package com.sapienter.jbilling.server.ediTransaction.task;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.ediTransaction.*;
import com.sapienter.jbilling.server.ediTransaction.db.EDIFileDAS;
import com.sapienter.jbilling.server.ediTransaction.db.EDIFileDTO;
import com.sapienter.jbilling.server.ediTransaction.db.EDIFileFieldDTO;
import com.sapienter.jbilling.server.ediTransaction.db.EDIFileRecordDTO;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.invoice.IInvoiceSessionBean;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.metafields.EntityType;
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldDAS;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.order.IOrderSessionBean;
import com.sapienter.jbilling.server.order.db.OrderDAS;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.OrderStatusFlag;
import com.sapienter.jbilling.server.order.db.*;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.system.event.EventManager;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.IWebServicesSessionBean;
import jbilling.RouteService;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.batch.core.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MeterReadParserTask extends AbstractScheduledTransactionProcessor implements StepExecutionListener {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(MeterReadParserTask.class));

    public static enum MeterRead implements FileStructure {
        KEY, HDR, UMR, QTY, REA, MTR;
    }

    public static enum MeterReadField {
        START_SERVICE_DT,
        END_SERVICE_DT,
        TRANS_REF_NR,
        UTILITY_CUST_ACCT_NR,
        USAGE_TYPE,
        READ_CONSUMPTION,       // REA usage field
        TOTAL_CONSUMPTION,     //QTY usage field (this one should be used for calculation)
        FINAL_IND,
        CUST_ENROLL_AGREE_DT,
        COMMODITY,
        SUM,
        SUPPLIER_RATE_CD,
        edi_file_id,
        ORG_867_TRAN_NR,
        INTERVAL_DT,
        INTERVAL_TIME,
        INTERVAL_TYPE
    }

    protected static final ParameterDescription DONE_STATUS_NAME =
            new ParameterDescription("done_status", true, ParameterDescription.Type.STR);
    protected static final ParameterDescription REPLACEMENT_STATUS_NAME =
            new ParameterDescription("replacement_status", true, ParameterDescription.Type.STR);
    protected static final ParameterDescription INVALID_DATA_STATUS_NAME =
            new ParameterDescription("invalid_data_status", true, ParameterDescription.Type.STR);
    protected static final ParameterDescription CANCELLATION_STATUS_NAME =
            new ParameterDescription("cancellation_status", true, ParameterDescription.Type.STR);
    protected static final ParameterDescription HISTORICAL_RECORD_STATUS_NAME =
            new ParameterDescription("historical_status", true, ParameterDescription.Type.STR);
    protected static final ParameterDescription REJECTED_STATUS_NAME =
            new ParameterDescription("rejected_status", true, ParameterDescription.Type.STR);
    protected static final ParameterDescription DEPRECATED_STATUS_NAME =
            new ParameterDescription("deprecated_status", true, ParameterDescription.Type.STR);

    public static final String  METER_READ_ORIGINAL_RECORD = "00";
    public static final String METER_READ_CANCELLATION_RECORD = "01";
    public static final String METER_READ_REPLACEMENT_RECORD = "05";
    public static final String METER_READ_HISTORICAL_RECORD = "52";
    public static final Integer METER_READ_DATE_TOLERANCE = 3;
    public static final String METER_READ_RECORD_TYPE = "867_PURPOSE_CD";

    {
        descriptions.add(DONE_STATUS_NAME);
        descriptions.add(REPLACEMENT_STATUS_NAME);
        descriptions.add(INVALID_DATA_STATUS_NAME);
        descriptions.add(CANCELLATION_STATUS_NAME);
        descriptions.add(HISTORICAL_RECORD_STATUS_NAME);
        descriptions.add(REJECTED_STATUS_NAME);
        descriptions.add(DEPRECATED_STATUS_NAME);
    }


    //Batch processor instance variable needs to initialize

    IWebServicesSessionBean webServicesSessionSpringBean;
    IEDITransactionBean ediTransactionBean;
    IOrderSessionBean orderSessionBean;
    private RouteService routeService;
    IInvoiceSessionBean invoiceSessionBean=Context.getBean(Context.Name.INVOICE_SESSION);
    private String DONE_STATUS;
    private String REPLACEMENT_STATUS;
    private String INVALID_DATA_STATUS;
    private String CANCELLATION_STATUS;
    private String HISTORICAL_RECORD_STATUS;
    private String REJECTED_STATUS;
    private String DEPRECATED_STATUS;

    private Integer ediTypeId;
    private String supplierDUNS;
    private String utilityDUNS;

    private int companyId;
    private String comment;
    private UserDTO userDTO;
    private List<Object[]> persistedMeterRecordsUsage=new ArrayList<Object[]>();
    private Date startDate;
    private Date endDate;
    private String customerAccountNumber;
    private Map<String, Object> summaryDetail=new HashMap<String, Object>();
    protected Integer changeRequestTypeId;
    private Boolean isFinalMeterRead=false;

    // valid meter read status
    private String[] statuses=new String[]{"Done", "EXP001", "EXP002", "Deprecated", "Historical Meter Read"};

    @Override
    public String getTaskName() {
        return "Meter read parser: " + getEntityId() + ", task Id: " + getTaskId();
    }

    @Override
    protected String getJobName() {
        return Context.Name.BATCH_EDI_METER_TRANSACTION_PROCESS.getName();
    }

    @Override
    public void preBatchConfiguration(Map jobParams) {
        IWebServicesSessionBean webServicesSessionSpringBean = Context.getBean(Context.Name.WEB_SERVICES_SESSION);

        LOG.debug("Execute MeadReadParserTask plugin.");

        EDI_TYPE_ID = (Integer) companyMetaFieldValueMap.get(FileConstants.METER_READ_EDI_TYPE_ID_META_FIELD_NAME);
        changeRequestTypeId = (Integer) companyMetaFieldValueMap.get(FileConstants.CHANGE_REQUEST_EDI_TYPE_ID_META_FIELD_NAME);
        if(EDI_TYPE_ID == null) {
            throwException("EDI type id not valid", null, REJECTED_STATUS);
        }

        EDITypeWS ediType = webServicesSessionSpringBean.getEDIType(EDI_TYPE_ID);
        if (ediType == null)
            throwException("EDI type id not found: " + EDI_TYPE_ID, null, REJECTED_STATUS);

        jobParams.put("DONE_STATUS", new JobParameter(parameters.get(DONE_STATUS_NAME.getName())));
        jobParams.put("REPLACEMENT_STATUS", new JobParameter(parameters.get(REPLACEMENT_STATUS_NAME.getName())));
        jobParams.put("INVALID_DATA_STATUS", new JobParameter(parameters.get(INVALID_DATA_STATUS_NAME.getName())));
        jobParams.put("CANCELLATION_STATUS", new JobParameter(parameters.get(CANCELLATION_STATUS_NAME.getName())));
        jobParams.put("HISTORICAL_RECORD_STATUS", new JobParameter(parameters.get(HISTORICAL_RECORD_STATUS_NAME.getName())));
        jobParams.put("REJECTED_STATUS", new JobParameter(parameters.get(REJECTED_STATUS_NAME.getName())));
        jobParams.put("DEPRECATED_STATUS", new JobParameter(parameters.get(DEPRECATED_STATUS_NAME.getName())));

        jobParams.put("ediTypeId", new JobParameter(EDI_TYPE_ID.longValue()));
        jobParams.put("changeRequestTypeId", new JobParameter(changeRequestTypeId.longValue()));
        jobParams.put("supplierDUNS", new JobParameter(SUPPLIER_DUNS));
        jobParams.put("utilityDUNS", new JobParameter(UTILITY_DUNS));
//        Set transaction type from suffix.
        jobParams.put("TRANSACTION_SET", new JobParameter(ediType.getEdiSuffix()));
    }

    public void processFile(EDIFileWS ediFileWS, String escapeExceptionStatus) throws Exception{
        this.escapeExceptionStatus=escapeExceptionStatus;
        this.ediFile = ediFileWS;
        setMetaFieldValues(companyId);

        try {
            processMeterReadFile();
        } catch (Exception ex) {
            LOG.error(ex);
            status = (status == null) ? INVALID_DATA_STATUS : status;
            comment = ex.getMessage();
        }
        EDITypeWS ediType = webServicesSessionSpringBean.getEDIType(ediTypeId);
        EDIFileStatusWS statusWS = null;
        for(EDIFileStatusWS ediStatus : ediType.getEdiStatuses()){
            if(ediStatus.getName().equals(status)){
                statusWS = ediStatus;
                break;
            }
        }
        ediFileWS.setEdiFileStatusWS(statusWS);
        ediFileWS.setComment(comment);
        if(exceptionCode!=null)ediFileWS.setExceptionCode(exceptionCode);
    }

    public void bindPluginParameter(Map<String, String> pluginParameter){
        webServicesSessionSpringBean = Context.getBean(Context.Name.WEB_SERVICES_SESSION);
        ediTransactionBean = Context.getBean(Context.Name.EDI_TRANSACTION_SESSION);
        orderSessionBean = Context.getBean(Context.Name.ORDER_SESSION);
        routeService = Context.getBean(Context.Name.ROUTE_SERVICE);

        companyId = Integer.parseInt(pluginParameter.get("companyId"));
        DONE_STATUS = pluginParameter.get("done_status");
        REPLACEMENT_STATUS = pluginParameter.get("replacement_status");
        INVALID_DATA_STATUS = pluginParameter.get("invalid_data_status");
        CANCELLATION_STATUS = pluginParameter.get("cancellation_status");
        HISTORICAL_RECORD_STATUS = pluginParameter.get("historical_status");
        REJECTED_STATUS = pluginParameter.get("rejected_status");
        DEPRECATED_STATUS = pluginParameter.get("deprecated_status");
        setMetaFieldValues(companyId);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        LOG.debug("EDI File Invoice Item Processor: Before Step");
        webServicesSessionSpringBean = Context.getBean(Context.Name.WEB_SERVICES_SESSION);
        ediTransactionBean = Context.getBean(Context.Name.EDI_TRANSACTION_SESSION);
        orderSessionBean = Context.getBean(Context.Name.ORDER_SESSION);
        routeService = Context.getBean(Context.Name.ROUTE_SERVICE);
        JobParameters jobParameters = stepExecution.getJobParameters();

        ediTypeId = jobParameters.getLong("ediTypeId").intValue();
        changeRequestTypeId = jobParameters.getLong("changeRequestTypeId").intValue();
        utilityDUNS = jobParameters.getString("utilityDUNS");
        supplierDUNS = jobParameters.getString("supplierDUNS");


        DONE_STATUS = jobParameters.getString("DONE_STATUS");
        REPLACEMENT_STATUS = jobParameters.getString("REPLACEMENT_STATUS");
        INVALID_DATA_STATUS = jobParameters.getString("INVALID_DATA_STATUS");
        CANCELLATION_STATUS = jobParameters.getString("CANCELLATION_STATUS");
        HISTORICAL_RECORD_STATUS = jobParameters.getString("HISTORICAL_RECORD_STATUS");
        REJECTED_STATUS = jobParameters.getString("REJECTED_STATUS");
        DEPRECATED_STATUS = jobParameters.getString("DEPRECATED_STATUS");
        companyId = jobParameters.getLong("companyId").intValue();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    public EDIFileWS process(EDIFileWS ediFileWS) throws Exception {
        LOG.debug("Meter Read Task Process Method");
        try {
            if (ediFileWS.getEdiFileStatusWS().getId() == FileConstants.EDI_STATUS_PROCESSED) {
                this.ediFile = ediFileWS;
                setMetaFieldValues(companyId);
                processMeterReadFile();
            }
        } catch (Exception ex) {
            LOG.error(ex);
            status = (status == null) ? INVALID_DATA_STATUS : status;
            comment = ex.getMessage();
        }

        EDITypeWS ediType = webServicesSessionSpringBean.getEDIType(ediTypeId);
        EDIFileStatusWS statusWS = null;
        for(EDIFileStatusWS ediStatus : ediType.getEdiStatuses()){
            if(ediStatus.getName().equals(status)){
                statusWS = ediStatus;
            }
        }
        if(statusWS!=null) ediFileWS.setEdiFileStatusWS(statusWS);
        if(comment!=null) ediFileWS.setComment(comment);
        if(exceptionCode!=null) ediFileWS.setExceptionCode(exceptionCode);
        return ediFileWS;
    }

    public void processMeterReadFile(){
        ediTypeId = (Integer) companyMetaFieldValueMap.get(FileConstants.METER_READ_EDI_TYPE_ID_META_FIELD_NAME);
        changeRequestTypeId=(Integer) companyMetaFieldValueMap.get(FileConstants.CHANGE_REQUEST_EDI_TYPE_ID_META_FIELD_NAME);
        if(ediTypeId == null) {
            throwException("Configuration issue: EDI type id not configured for meter read: " + EDI_TYPE_ID, null, REJECTED_STATUS);
        }

        List<String> maidenRecords = new LinkedList<String>();
        maidenRecords.add(MeterRead.KEY.toString());
        maidenRecords.add(MeterRead.HDR.toString());
        parseRecords(maidenRecords, ediFile.getEDIFileRecordWSes());

        //Validating meter read record
        validateMeterRecord();

        if (ediTransactionBean.hasPlanSendRateChangeDaily(userDTO)) {
            status = ediFile.getEdiFileStatusWS().getName();
            comment = "Can't Create Order Because Customer Subscribed Day Ahead Product Plan";
            return;
        }

        String newRecordType=findField(MeterRead.HDR, METER_READ_RECORD_TYPE, true); // Original, cancellation, replacement
        if(newRecordType.equals(METER_READ_ORIGINAL_RECORD)){
            finalRecord();
            createOrderForMeterRead(ediFile);
            status=DONE_STATUS;
        }else if(newRecordType.equals(METER_READ_CANCELLATION_RECORD)){
            LOG.info("Cancellation record");

            EDIFileDTO cancellationRecord=getExistingRecord(METER_READ_CANCELLATION_RECORD);
            if(cancellationRecord!=null){
                throwException("There is duplicate cancellation request", FileConstants.METER_READ_DUPLICATE_CANCELLATION_EXP_CODE, INVALID_DATA_STATUS);
            }
            EDIFileDTO replacementRecord=getExistingRecord(METER_READ_REPLACEMENT_RECORD);
            if(replacementRecord!=null){
                exceptionCode=FileConstants.METER_READ_CANCELLATION_AFTER_PANDING_REPLACEMENT_EXP_CODE;
            }

            EDIFileDTO originalRecord=findOriginalRecord(newRecordType);

            if(escapeExceptionStatus==null){
                status=CANCELLATION_STATUS;
            }else{
                //code for deleting order and set status of cancellation meter read to  Done and mark original meter as depricated
                if(originalRecord!=null){
                    deleteOrder(originalRecord);
                    updateFileStatus(originalRecord.getId(), DEPRECATED_STATUS);
                }
                status=DONE_STATUS;
            }


        }else if(newRecordType.equals(METER_READ_REPLACEMENT_RECORD)){
            LOG.info("Replacement Record");
            EDIFileDTO replacementRecord=getExistingRecord(METER_READ_REPLACEMENT_RECORD);
            if(replacementRecord!=null){
                throwException("Duplicate meter read replacement transaction.", FileConstants.METER_READ_DUPLICATE_REPLACEMENT_EXP_CODE, INVALID_DATA_STATUS);
            }

            EDIFileDTO cancellationRequest=getExistingRecord(METER_READ_CANCELLATION_RECORD);
            EDIFileDTO originalRecord=findOriginalRecord(newRecordType);

            if(cancellationRequest==null && escapeExceptionStatus==null) {
                exceptionCode = FileConstants.MISSING_CANCELLATION_FOR_REPLACEMENT_EXP_CODE;
                status = REPLACEMENT_STATUS;
            }else if(cancellationRequest==null || (!cancellationRequest.getFileStatus().getName().equals(DONE_STATUS)) ){
                if(originalRecord!=null){
                    deleteOrder(originalRecord);
                    //mark original meter read edi file as deprecated
                    updateFileStatus(originalRecord.getId(), DEPRECATED_STATUS);
                }
                // set exception status of cancellation Meter as Done
                if(cancellationRequest!=null){
                    updateFileStatus(cancellationRequest.getId(), DONE_STATUS);
                }

                createOrderForMeterRead(ediFile);
                status=DONE_STATUS;
            }else{
                createOrderForMeterRead(ediFile);
                status=DONE_STATUS;
            }
        }else if(newRecordType.equals(METER_READ_HISTORICAL_RECORD)){
            LOG.info("Historical Record");
            status=HISTORICAL_RECORD_STATUS;
        }else{
            status=INVALID_DATA_STATUS;
            comment="Record Type is not a valid record type. It should be 00, 01, 05 or 52 ";
        }
    }

    private void validateMeterRecord() {

        LOG.info("Validating Meter Read record");
        //Check Transfer_NR
        String TRANS_REF_NR = findField(MeterRead.HDR, MeterReadField.TRANS_REF_NR.toString());
        LOG.debug(MeterReadField.TRANS_REF_NR.toString() + "value found: " + TRANS_REF_NR);
        try {
            ediTransactionBean.isUniqueKeyExistForFile(companyId, ediTypeId, ediFile.getId(), MeterReadField.TRANS_REF_NR.toString(), TRANS_REF_NR, TransactionType.INBOUND);
        } catch (Exception e) {
            throwException(e.getMessage(), FileConstants.METER_READ_DUPLICATE_TRANSACTION_EXP_CODE, REJECTED_STATUS);
        }

        isFinalMeterRead=isFinalMeterRead();

        LOG.info("Validating is customer exist");
        customerAccountNumber = findField(MeterRead.HDR, MeterReadField.UTILITY_CUST_ACCT_NR.toString());
        if(customerAccountNumber==null){
            throwException("Account Id not found in Meter read file. EDI File Id: " + ediFile.getId(), FileConstants.METER_READ_UNKNOWN_ACCOUNT_EXP_CODE, REJECTED_STATUS);
            LOG.error("Account Id not found in Meter read file. EDI File Id: " + ediFile.getId());
        }

        try{
            userDTO = ediTransactionBean.findUserByAccountNumber(companyId, MeterReadField.UTILITY_CUST_ACCT_NR.toString(), customerAccountNumber, isFinalMeterRead);
            LOG.debug("Find customer " + userDTO);
        }catch (SessionInternalError e){
            LOG.error("Customer not found in the system. EDI File Id: " + ediFile.getId());
            throwException(e.getMessage(), FileConstants.METER_READ_UNKNOWN_ACCOUNT_EXP_CODE, REJECTED_STATUS);
        }

        MetaFieldValue metaFieldValue=userDTO.getCustomer().getMetaField("Termination");
        if(metaFieldValue!=null && metaFieldValue.getValue()!=null){
            String terminatedValue=(String)metaFieldValue.getValue();
            if(terminatedValue.equals("Dropped") || terminatedValue.equals("Esco Rejected")){
                throwException("Customer is dropped. We can not process meter read for dropped customer", FileConstants.METER_READ_DROP_CUSTOMER_EXP_CODE, INVALID_DATA_STATUS);
            }
        }

        validateTotalConsumption();
        validateOverlapOrOvergap();
    }

    private Map<String, Object> getSummaryMeterData(EDIFileWS ediFile){

        EDIFileRecordWS[] recordList = ediFile.getEDIFileRecordWSes();

        Map<String, Object> meterFields = new HashMap<String, Object>();
        for(EDIFileRecordWS ediFileRecordWS: recordList){
            if(ediFileRecordWS.getHeader().equals("UMR")){
                Map<String, String> UMRFields = parseRecord(ediFileRecordWS);
                Map<String, String> QTYFields = parseRecord(recordList[ediFileRecordWS.getRecordOrder()]);
                meterFields.put(MeterReadField.USAGE_TYPE.toString(), findField(UMRFields, MeterReadField.USAGE_TYPE.toString(), true));
                meterFields.put(MeterReadField.TOTAL_CONSUMPTION.toString(), findField(QTYFields, MeterReadField.TOTAL_CONSUMPTION.toString(), "BigDecimal", true));
                meterFields.put(MeterReadField.START_SERVICE_DT.toString(), findField(UMRFields, MeterReadField.START_SERVICE_DT.toString(), "Date", true));
                meterFields.put(MeterReadField.END_SERVICE_DT.toString(), findField(UMRFields, MeterReadField.END_SERVICE_DT.toString(),"Date", true));
                break;
            }
        }
        return meterFields;
    }

    private BigDecimal getTotalConsumption(EDIFileWS ediFile){
        //finding interval QTY record
        List<EDIFileRecordWS> ediFileRecordWSes=Arrays.asList(ediFile.getEDIFileRecordWSes()).stream().filter((EDIFileRecordWS ediFileRecordWS) -> ediFileRecordWS.getHeader().equals("QTY")).collect(Collectors.toList());
        ediFileRecordWSes.remove(0);
        BigDecimal totalConsumption=BigDecimal.ZERO;

        // calculating total of interval record consumption
        for(EDIFileRecordWS ediFileRecordWS: ediFileRecordWSes){
            for(EDIFileFieldWS ediFileFieldWS:ediFileRecordWS.getEdiFileFieldWSes()){
                if (ediFileFieldWS.getKey().equals(MeterReadField.TOTAL_CONSUMPTION.toString())){
                    totalConsumption = totalConsumption.add(new BigDecimal(ediFileFieldWS.getValue()));
                }
            }
        }

        return totalConsumption;
    }


    public void finalRecord(){
        if(isFinalMeterRead){
            LOG.debug("Final Meter Read");
            UserWS userWS = webServicesSessionSpringBean.getUserWS(userDTO.getId());
            Boolean isTerminatedOrDropped= isUserAlreadyTerminatedOrDropped(userWS);
            if(isTerminatedOrDropped){
                // making customer dropped
                userWS=AbstractScheduledTransactionProcessor.updateTerminationMetaField(userWS, FileConstants.DROPPED, null);
                updateUser(userWS);
                List<OrderDTO> subscriptions = new OrderDAS()
                        .findByUserSubscriptions(userDTO.getId());
                if(subscriptions.size()>0){
                    //Need to uncomment
                    //TODO need to be refactor: as we know there is one subscription order for one LDC Company. But we need to add check for plan .
                    OrderDTO orderDTO=subscriptions.get(0);
                    orderDTO.setActiveUntil(endDate);
                    new OrderDAS().save(orderDTO);
                }

                EventManager.process(new CustomerDroppedEvent(userWS.getEntityId(), userWS.getCustomerId(), endDate));
            }
        }
    }

    private boolean isFinalMeterRead() {
        String finalField = findField(MeterRead.HDR, MeterReadField.FINAL_IND.toString(), false);
        if (finalField != null && finalField.equals("F")) {
            return true;
        }
        return false;
    }

    public EDIFileDTO getExistingRecord(String recordType){
        for(int i=0; i < persistedMeterRecordsUsage.size(); i = i + 3){
            String record=findPersistedRecordValue(i, METER_READ_RECORD_TYPE);
            if(record.equals(recordType)){
                String startDateString=findPersistedRecordValue(i, MeterReadField.START_SERVICE_DT.toString());;
                String endDateString=findPersistedRecordValue(i, MeterReadField.END_SERVICE_DT.toString());
                Date existingRecordStartDate = dateFormat.parseDateTime(startDateString).toDate();
                Date existingRecordEndDate = dateFormat.parseDateTime(endDateString).toDate();
                if(existingRecordStartDate.equals(startDate) && existingRecordEndDate.equals(endDate)){
                    Integer ediFileId=(Integer)persistedMeterRecordsUsage.get(i)[0];
                    return new EDIFileDAS().find(ediFileId);
                }
            }
        }
        return null;
    }


    public void validateTotalConsumption(){
        LOG.info("Validating sum of details record consumption with the summary record total consumption");
        List <Map<String, Object>> meterReadServiceDetails=new ArrayList<Map<String, Object>>();


        BigDecimal totalConsumption=getTotalConsumption(ediFile);
        BigDecimal summaryRecordConsumption=BigDecimal.ZERO;

        summaryDetail=getSummaryMeterData(ediFile);


        summaryRecordConsumption=(BigDecimal)summaryDetail.get(MeterReadField.TOTAL_CONSUMPTION.toString());

        if(totalConsumption.compareTo(summaryRecordConsumption) != 0){
            LOG.error("Total consumptions of the details record is not equal to the summary record total consumption. Total Consumption: "+totalConsumption+" Summary Record: "+summaryRecordConsumption);
            throwException("Total consumption does not match with summary's total consumption value. Total Consumption: "+totalConsumption+" Summary Record Total Consumption: "+summaryRecordConsumption, null, INVALID_DATA_STATUS);
        }

    }

    public void validateOverlapOrOvergap(){
        LOG.info("Validating record date with Previous record date");
        String newRecordType=findField(MeterRead.HDR, METER_READ_RECORD_TYPE, true);
        startDate=(Date)summaryDetail.get(MeterReadField.START_SERVICE_DT.toString());

        //validating a meter read service date is not before the customer enrollment agreement date

        UserBL userBL=new UserBL(userDTO);
        UserWS userWS=userBL.getUserWS();

        for(MetaFieldValueWS metaFieldValueWS:userWS.getMetaFields()){
            if(metaFieldValueWS.getFieldName().equals(MeterReadField.CUST_ENROLL_AGREE_DT.toString())){
                Date customerEnrollmentAgreementDate=(Date)metaFieldValueWS.getValue();
                if(customerEnrollmentAgreementDate.compareTo(startDate)>0){
                    throwException("MeterRead start date should be greater than the customer enrollment agreement date", FileConstants.METER_READ_ENROLLMENT_DATE_SHOULD_BE_GREATER_THEN_AGREEMENT_DATE_EXP_CODE, INVALID_DATA_STATUS);
                }
                break;
            }
        }

        endDate=(Date)summaryDetail.get(MeterReadField.END_SERVICE_DT.toString());
        DateTime currentRecordStartDateTime = new DateTime(startDate);
        DateTime currentRecordEndDateTime =new DateTime(endDate);
        Integer tolerance=METER_READ_DATE_TOLERANCE;
        if(Days.daysBetween(currentRecordStartDateTime.toLocalDate(), currentRecordEndDateTime.toLocalDate()).getDays() <= tolerance ){
            throwException("Difference between  MeterRead start date and end date should be greater than the tolerance("+tolerance+")", FileConstants.METER_READ_DIFFRENCE_BETRWEEN_START_AND_END_DATE_GREATER_THEN_TOLLERANCE_EXP_CODE, INVALID_DATA_STATUS);

        }

        Conjunction conjunction = Restrictions.conjunction();
        conjunction.add(Restrictions.eq("ediType.id", ediFile.getEdiTypeWS().getId()));
        conjunction.add(Restrictions.eq("entity.id", companyId));
        conjunction.add(Restrictions.eq("type", TransactionType.INBOUND));
        conjunction.add(Restrictions.in("status.name", statuses));
        conjunction.add(Restrictions.eq("fileFields.ediFileFieldKey", MeterReadField.UTILITY_CUST_ACCT_NR.toString()));
        conjunction.add(Restrictions.eq("fileFields.ediFileFieldValue", customerAccountNumber));
        conjunction.add(Restrictions.not(Restrictions.eq("id", ediFile.getId())));

        //will return all the meter read records ids
        List<Integer> meterFileRecordIds = new EDIFileDAS().findFileByData(conjunction);
        if(meterFileRecordIds.size()>0){
            Conjunction usageRecordCon = Restrictions.conjunction();
            usageRecordCon.add(Restrictions.disjunction().add(Restrictions.eq("record.ediFileRecordHeader", "UMR")).add(Restrictions.eq("record.ediFileRecordHeader", "HDR")));
            usageRecordCon.add(Restrictions.disjunction().add(Restrictions.eq("record.recordOrder", 3)).add(Restrictions.eq("record.recordOrder", 2)));
            usageRecordCon.add(Restrictions.in("file.id", meterFileRecordIds));
            usageRecordCon.add(Restrictions.disjunction().add(Restrictions.eq("ediFileFieldKey", "START_SERVICE_DT"))
                    .add(Restrictions.eq("ediFileFieldKey", "END_SERVICE_DT")).add(Restrictions.eq("ediFileFieldKey", "867_PURPOSE_CD")));

            persistedMeterRecordsUsage = new EDIFileDAS().findDataFromField(usageRecordCon);
            // Overlap and Overgap condition should be check if it is an original meter read and not final meter read. Because final meter read can come for drop customer in past period
            if(newRecordType.equals(METER_READ_ORIGINAL_RECORD) && !isFinalMeterRead()){
                for(int i = 0; i < persistedMeterRecordsUsage.size(); i = i + 3){
                    String recordType=findPersistedRecordValue(i, METER_READ_RECORD_TYPE);

                    if(recordType.equals(METER_READ_ORIGINAL_RECORD)){
                        String startDateString=findPersistedRecordValue(i, MeterReadField.START_SERVICE_DT.toString());;
                        String endDateString=findPersistedRecordValue(i, MeterReadField.END_SERVICE_DT.toString());

                        Date existingRecordEndDate = dateFormat.parseDateTime(endDateString).toDate();
                        DateTime previousRecordEndDateTime = new DateTime(existingRecordEndDate);
                        Integer days=Days.daysBetween(previousRecordEndDateTime.toLocalDate(), currentRecordStartDateTime.toLocalDate()).getDays();
                        if(days > 0 && Math.abs(days) > tolerance){
                            throwException("There is gap with last meter read. Last Meter Read period was "+startDateString+" to "+endDateString, FileConstants.METER_READ_GAP_EXP_CODE, INVALID_DATA_STATUS);

                        }
                        if(days < 0 && Math.abs(days) > tolerance){
                            throwException("There is overlap with last meter read. Last Meter Read period was "+startDateString+" to "+endDateString, FileConstants.METER_READ_OVERLAP_EXP_CODE, INVALID_DATA_STATUS);
                        }
                        return;
                    }
                }
            }
        }
    }

    private String findPersistedRecordValue(Integer count, String recordType){

        if(recordType.equals((String) persistedMeterRecordsUsage.get(count)[1])){
            return (String) persistedMeterRecordsUsage.get(count)[2];
        }
        if(recordType.equals((String)persistedMeterRecordsUsage.get(count+1)[1])){
            return (String) persistedMeterRecordsUsage.get(count+1)[2];
        }
        if(recordType.equals((String)persistedMeterRecordsUsage.get(count+2)[1])){
            return (String) persistedMeterRecordsUsage.get(count+2)[2];
        }
        //In meter read case name return null
        return null;
    }

    private void createOrderForMeterRead(EDIFileWS ediFileWS) {
        //Customer exist
        ItemDAS itemDAS=new ItemDAS();
        ItemDTO item = null;
        BigDecimal totalConsumption = BigDecimal.ZERO;
        Date startDate=null;
        Date endDate=null;

        Map<String, String> headerRecord=new HashMap<>();
        getHeaderRecord(ediFileWS, headerRecord, MeterRead.HDR.toString());
        String recordType=findField(headerRecord, METER_READ_RECORD_TYPE, true);

        Map<String, Object> serviceDetail=getSummaryMeterData(ediFileWS);

            if(serviceDetail.get(MeterReadField.USAGE_TYPE.toString()).toString().equals(MeterReadField.SUM.toString())){
                if(recordType.equals(METER_READ_CANCELLATION_RECORD)){
                    totalConsumption=((BigDecimal)serviceDetail.get(MeterReadField.TOTAL_CONSUMPTION.toString())).negate();
                }else{
                    totalConsumption=(BigDecimal) serviceDetail.get(MeterReadField.TOTAL_CONSUMPTION.toString());
                }
                startDate=(Date)serviceDetail.get(MeterReadField.START_SERVICE_DT.toString());
                endDate=(Date)serviceDetail.get(MeterReadField.END_SERVICE_DT.toString());
            }

        String commodity=null;
        for(EDIFileRecordWS ediFileRecordWS:ediFileWS.getEDIFileRecordWSes()){
            if(ediFileRecordWS.getHeader().equals(MeterRead.KEY.toString())){
                Map<String, String> keyRecord= parseRecord(ediFileRecordWS);
                commodity=findField(keyRecord, MeterReadField.COMMODITY.toString(), true);
                item = itemDAS.findByMetaFieldNameAndValue(companyId, MeterReadField.COMMODITY.toString(), commodity);
                break;
            }
        }
        if (item!=null ){
            OrderDTO order = new OrderDTO();
            OrderPeriodDTO period = new OrderPeriodDAS().find(Constants.ORDER_PERIOD_ONCE);
            order.setOrderPeriod(period);
            OrderBillingTypeDTO type = new OrderBillingTypeDTO();
            type.setId(com.sapienter.jbilling.server.util.Constants.ORDER_BILLING_POST_PAID);
            order.setOrderBillingType(type);
            order.setCreateDate(Calendar.getInstance().getTime());
            order.setCurrency(userDTO.getCurrency());
            order.setActiveSince(startDate);
            order.setActiveUntil(endDate);
            order.setBaseUserByUserId(userDTO);
            UserWS user = new UserBL(userDTO.getId()).getUserWS();
            String customerType = getCustomerType(user);
            Integer orderStatus = null;

            EntityType[] entityType = {EntityType.ORDER};
            MetaField ediFileMetaField=new MetaFieldDAS().getFieldByName(companyId, entityType, MeterReadField.edi_file_id.toString());

            if(ediFileMetaField==null){
                throwException("Configuration issue: order should have edi_file_id meta field", null, REJECTED_STATUS);
            }
            order.setMetaField(ediFileMetaField, ediFileWS.getId()+"");

            Integer languageId = userDTO.getLanguageIdField();
            String description = item.getDescription(languageId);

            OrderLineDTO line = new OrderLineDTO();
            line.setDescription(description);
            line.setItemId(item.getId());
            line.setQuantity(totalConsumption);
            line.setTypeId(com.sapienter.jbilling.server.util.Constants.ORDER_LINE_TYPE_ITEM);
            line.setPurchaseOrder(order);
            order.getLines().add(line);
            OrderBL orderBL = new OrderBL();
            OrderStatusFlag orderStatusFlag=OrderStatusFlag.NOT_INVOICE;
            if (customerType.equals(FileConstants.BILLING_MODEL_BILL_READY) || customerType.equals(FileConstants.BILLING_MODEL_DUAL)) {
                if(customerType.equals(FileConstants.BILLING_MODEL_DUAL)){
                    orderStatusFlag=OrderStatusFlag.INVOICE;
                }
                orderBL.processLines(order, languageId, companyId, userDTO.getId(), userDTO.getCurrencyId(), "");
            }else{
                LOG.debug("finding rate for RateReady Customer");
                if(findRateForRateReady()!=null){
                    line.setPrice(findRateForRateReady());
                }else{
                    MetaFieldValue metaFieldValue=userDTO.getCustomer().getMetaField("Rate");
                    if(metaFieldValue==null || metaFieldValue.getValue()==null){
                        throwException("Customer should have value for Rate metaField", null, REJECTED_STATUS);
                    }
                    line.setPrice((BigDecimal)metaFieldValue.getValue());
                }
            }
            orderStatus = new OrderStatusDAS().getDefaultOrderStatusId(
                    orderStatusFlag,  userDTO.getEntity().getId());
            order.setStatusId(orderStatus);

            orderBL.set(order);
            orderBL.create(userDTO.getEntity().getId(), null, order);
        }else{
            throwException("Item not found for commodity "+commodity, null, REJECTED_STATUS);
        }

    }

    private BigDecimal findRateForRateReady(){
        LOG.debug("Finding rate for rate ready customer");
        Conjunction conjunction = Restrictions.conjunction();
        conjunction.add(Restrictions.eq("ediType.id", changeRequestTypeId));
        conjunction.add(Restrictions.eq("entity.id", companyId));
        conjunction.add(Restrictions.le("createDatetime", endDate));
        conjunction.add(Restrictions.eq("type", TransactionType.OUTBOUND));
        conjunction.add(Restrictions.eq("status.name", "Done"));
        conjunction.add(Restrictions.eq("fileFields.ediFileFieldKey", MeterReadField.UTILITY_CUST_ACCT_NR.toString()));
        conjunction.add(Restrictions.eq("fileFields.ediFileFieldValue", customerAccountNumber));

        EDIFileDTO changeRequest = new EDIFileDAS().findEDIFile(conjunction);
        LOG.debug("Find Change Request: "+changeRequest);

        if(changeRequest!=null){
            EDIFileWS changRequestWS=ediTransactionBean.getEDIFileWS(changeRequest.getId());
            Map<String, String> meterRecord=new HashMap<>();
            getHeaderRecord(changRequestWS, meterRecord, MeterRead.MTR.toString());
            String rateCode=findField(meterRecord, MeterReadField.SUPPLIER_RATE_CD.toString(), true);
            LOG.debug("Searched rate code: "+rateCode);
            if(rateCode!=null){
               return ediTransactionBean.getRateByRateCode(companyId, rateCode);
            }
        }

        return null;
    }


    private EDIFileDTO findOriginalRecord(String recordType){

        LOG.debug("finding original Meter read");
        EDIFileDTO originalEdiFile=null;
        String originalMeterReadId=findField(MeterRead.HDR, MeterReadField.ORG_867_TRAN_NR.toString());
        LOG.debug("Original Meter Read TRANS_REF_NR : " + originalMeterReadId);
        if(originalMeterReadId!=null){

            Conjunction conjunction = Restrictions.conjunction();
            conjunction.add(Restrictions.eq("ediType.id", ediFile.getEdiTypeWS().getId()));
            conjunction.add(Restrictions.eq("entity.id", companyId));
            conjunction.add(Restrictions.eq("type", TransactionType.INBOUND));
            conjunction.add(Restrictions.in("status.name", statuses));
            conjunction.add(Restrictions.eq("fileFields.ediFileFieldKey", MeterReadField.TRANS_REF_NR.toString()));
            conjunction.add(Restrictions.eq("fileFields.ediFileFieldValue", originalMeterReadId));
            originalEdiFile = new EDIFileDAS().findEDIFile(conjunction);


            EDIFileWS originalMeterReadWS=null;
            String oldEDIUsageType=null;

            if(originalEdiFile!=null){
                originalMeterReadWS=new EDIFileBL(originalEdiFile).getWS();
                oldEDIUsageType=EdiUtil.findRecord(originalMeterReadWS, MeterRead.HDR.toString(), METER_READ_RECORD_TYPE);
            }

            LOG.debug("Original Meter read : "+originalEdiFile);

            //checking is a valid original meter read exit in the system for the given TRANS_REF_NR.
            if(originalEdiFile==null || (oldEDIUsageType !=null && !oldEDIUsageType.equals(METER_READ_ORIGINAL_RECORD))){
                LOG.debug("No original EDI file found for TRANS_REF_NR : "+originalMeterReadId);
                String exceptionCode=(recordType.equals(METER_READ_CANCELLATION_RECORD)? FileConstants.METER_READ_CANCELLATION_WITHOUT_ORIGINAL_CODE_EXP_CODE:FileConstants.ORIGINAL_METER_READ_FOR_REPLACEMENT_DOES_NOT_EXIST_EXP_CODE);
                throwException("No original Meter Read found for TRANS_REF_NR : "+originalMeterReadId, exceptionCode, INVALID_DATA_STATUS);
            }else{
                if(!isFileMatch(originalEdiFile)){
                    String exceptionCode=(recordType.equals(METER_READ_CANCELLATION_RECORD)? FileConstants.METER_READ_CANCELLATION_NOT_MATCH_WITH_ORIGINAL_CODE_EXP_CODE:FileConstants.MISMATCH_ORIGINAL_FOR_REPLACEMENT_EXP_CODE);
                    throwException("Original Meter Read not found of customer "+customerAccountNumber+" for period "+startDate+" to "+ endDate, exceptionCode, INVALID_DATA_STATUS);
                }
            }
        }else{
            LOG.debug("Missing original meter read trans_ref_nr ");
            originalEdiFile=getExistingRecord(METER_READ_ORIGINAL_RECORD);
            if(originalEdiFile==null){
                //NO Matching record found for customer
                String exceptionCode=(recordType.equals(METER_READ_CANCELLATION_RECORD)? FileConstants.METER_READ_CANCELLATION_WITHOUT_ORIGINAL_CODE_EXP_CODE:FileConstants.ORIGINAL_METER_READ_FOR_REPLACEMENT_DOES_NOT_EXIST_EXP_CODE);
                throwException("No original meter read found for the given period", exceptionCode, INVALID_DATA_STATUS);
            }
        }

        return originalEdiFile;

    }

    //this method match account number, start date and end date of  the current meter read with given meter read
    private Boolean isFileMatch(EDIFileDTO ediFileDTO){
        String accountNumber=null;
        String oldMeterStartDate=null;
        String oldMeterEndDate=null;

        for(EDIFileRecordDTO ediFileRecordDTO:ediFileDTO.getEdiFileRecords()){
            //matching account number
            if(ediFileRecordDTO.getEdiFileRecordHeader().equals(MeterRead.HDR.toString())){
                for(EDIFileFieldDTO ediFileFieldDTO:ediFileRecordDTO.getFileFields()){
                    if(ediFileFieldDTO.getEdiFileFieldKey().equals(MeterReadField.UTILITY_CUST_ACCT_NR.toString())){
                        accountNumber=ediFileFieldDTO.getEdiFileFieldValue();
                    }
                }
            }

            //matching start date and end date
            if(ediFileRecordDTO.getEdiFileRecordHeader().equals(MeterRead.UMR.toString())){

                for(EDIFileFieldDTO ediFileFieldDTO:ediFileRecordDTO.getFileFields()){
                    if(ediFileFieldDTO.getEdiFileFieldKey().equals(MeterReadField.START_SERVICE_DT.toString())){
                        oldMeterStartDate=ediFileFieldDTO.getEdiFileFieldValue();
                    }

                    if(ediFileFieldDTO.getEdiFileFieldKey().equals(MeterReadField.END_SERVICE_DT.toString())){
                        oldMeterEndDate=ediFileFieldDTO.getEdiFileFieldValue();
                    }
                }
                break;
            }
        }

        if(findField(MeterRead.HDR, MeterReadField.UTILITY_CUST_ACCT_NR.toString()).equals(accountNumber) && startDate.equals(dateFormat.parseDateTime(oldMeterStartDate).toDate()) && endDate.equals(dateFormat.parseDateTime(oldMeterEndDate).toDate()) ){
            return true;
        }

        LOG.debug("Current file did not match with original meter read");
        return false;
    }

    private void deleteOrder(EDIFileDTO originalRecord){
        OrderDAS orderDAS=new OrderDAS();
        //Find order for original meter read and check is invoice is generated for it
        OrderDTO orderDTO=orderDAS.findOrderByMetaFieldValue(companyId, "edi_file_id", originalRecord.getId() + "");

        if(orderDTO==null){
            throwException("No order find for original meter read " + originalRecord.getId() + " in the system", null, REJECTED_STATUS, true);
        }

        // if order have generated invoice then you din not delete order.
        if(orderDTO!=null && orderDTO.getOrderProcesses().size()==0){
            UserDTO adminUser=new UserDAS().findByUserName("admin", companyId);
            LOG.debug("Admin User: "+ adminUser);
            orderSessionBean.delete(orderDTO.getId(), adminUser.getUserId());
        }else{
            throwException("Order "+orderDTO.getId()+" has generated invoices. Please upload a cancellation invoice read or manually delete the invoice for this order", null, INVALID_DATA_STATUS, true);
        }
        updateFileStatus(originalRecord.getId(), DEPRECATED_STATUS);
    }
}
