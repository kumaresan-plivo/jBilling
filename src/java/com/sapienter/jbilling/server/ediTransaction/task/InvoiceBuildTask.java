package com.sapienter.jbilling.server.ediTransaction.task;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.ediTransaction.*;
import com.sapienter.jbilling.server.ediTransaction.db.EDIFileDAS;
import com.sapienter.jbilling.server.ediTransaction.db.EDIFileDTO;
import com.sapienter.jbilling.server.ediTransaction.invoiceRead.InvoiceReadTask;
import com.sapienter.jbilling.server.entity.InvoiceLineDTO;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.fileProcessing.fileGenerator.FlatFileGenerator;
import com.sapienter.jbilling.server.fileProcessing.xmlParser.FileFormat;
import com.sapienter.jbilling.server.invoice.InvoiceBL;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.invoice.db.InvoiceDAS;
import com.sapienter.jbilling.server.invoice.db.InvoiceDTO;
import com.sapienter.jbilling.server.invoice.db.InvoiceLineDAS;
import com.sapienter.jbilling.server.item.PlanWS;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.metafields.EntityType;
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.metafields.db.MetaField;
import com.sapienter.jbilling.server.metafields.db.MetaFieldDAS;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.order.db.OrderDAS;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderProcessDAS;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.process.event.InvoicesGeneratedEvent;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.user.AccountTypeWS;
import com.sapienter.jbilling.server.user.CompanyWS;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.user.db.AccountInformationTypeDAS;
import com.sapienter.jbilling.server.user.db.AccountInformationTypeDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.IWebServicesSessionBean;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by aman on 9/11/15.
 */
public class InvoiceBuildTask extends PluggableTask
        implements IInternalEventsTask {

    private static final Logger LOG = Logger.getLogger(InvoiceBuildTask.class);
    private IEDITransactionBean ediTransactionBean;
    private FileFormat fileFormat = null;

    private Integer EDI_TYPE_ID;
    private String SUPPLIER_DUNS;
    private String UTILITY_DUNS;
    private String TRANSACTION_SET;
    private String SUPPLIER_NAME;
    private String UTILITY_NAME;
    private String BILL_CALC;
    private String BILL_DELIVER;


    private Integer invoiceId;
    private String transactionNumber;
    private String METER_READ_ID_VALUE;

    private UserWS user;
    private InvoiceWS invoiceWS;
    private OrderWS orderWS;

    private static final String UNDERSCORE_SEPARATOR = "_";
    private static final String DOT_SEPARATOR = ".";
    IWebServicesSessionBean webServicesSessionSpringBean;

    private static final Class<Event> events[] = new Class[]{
            InvoicesGeneratedEvent.class
    };

    InvoiceDAS invoiceDAS=new InvoiceDAS();
    MetaFieldDAS metaFieldDAS=new MetaFieldDAS();

    // File Constants
    public static enum InvoiceBuildConstants {
        TRANSACTION_SET("TRANSACTION_SET"),
        TRANSCTION_SUBSET("BR"),
        PLAN("PLAN"),

        //HDR Record
        HDR_INVOICE_DATE("INVOICE_DATE"),
        HDR_INVOICE_NR("INVOICE_NR"),
        METER_READ_TRANS_NR("867_TRANS_NR"),
        HDR_INVOICE_ACTION_CD("ME"),
        HDR_INVOICE_PURPOSE_CD("00"),
        HDR_SUPPLIER_CUST_ACCT_NR("SUPPLIER_CUST_ACCT_NR"),
        HDR_UTILITY_CUST_ACCT_NR("UTILITY_CUST_ACCT_NR"),
        HDR_BILL_DELIVER("LDC"),
        HDR_BILL_CALC("ESCO"),
        HDR_TDSP_NAME("TDSP_NAME"),
        HDR_CR_NAME("CR_NAME"),
        HDR_RECV_DATETIME("RECV_DATETIME"),
        HDR_UTILITY_SUPPLIER_ACCT_NR("420"),   //Hard coded number
        HDR_INVOICE_TOTAL("INVOICE_TOTAL"),

        //NME Record
        NME_NAME_TYPE("8R"),
        NME_NAME("NAME"),

        //SVG Record
        SRV_LINE_NUMBER("LINE_NUMBER"),
        SRV_SERVICE_CLASS("SC1"),
        SRV_START_SERVICE_DT("START_SERVICE_DT"),
        SRV_END_SERVICE_DT("END_SERVICE_DT"),
        SRV_METER_NR("METER_NR"),                //Not Used

        //CHG Record
        CHG_INDICATOR("C"),
        CHG_CHARGE_DETERMINENT("F950"),
        CHG_CHARGE_CODE("ENC001"),
        CHG_AMOUNT("AMOUNT"),
        CHG_UNIT_RATE("UNIT_RATE"),
        CHG_UOM("KH"),                     //Hard Coded
        CHG_QUANTITY("QUANTITY"),

        //TAX record
        TAX_LEVEL("A"),
        TAX_CODE("ST"),
        TAX_AMOUNT("AMOUNT"),
        TAX_JURISDICTION_CODE("F950"),
        TAX_INCLUDE_TAX("A"),;

        String val;

        InvoiceBuildConstants(String value) {
            val = value;
        }

        String getValue() {
            return val;
        }
    }

    public Class<Event>[] getSubscribedEvents() {
        return events;
    }

    public void process(Event event) throws PluggableTaskException {
        LOG.debug("Checking for EDI generation for Invoice");
        webServicesSessionSpringBean = Context.getBean(Context.Name.WEB_SERVICES_SESSION);
        InvoicesGeneratedEvent castEvent = (InvoicesGeneratedEvent) event;
        List<Integer> invoiceIds = castEvent.getInvoiceIds();
        // Find out bill ready customers
        List<Integer> billReadyInvoices = new LinkedList<Integer>();

        for (Integer invoiceId : invoiceIds) {
            InvoiceDTO invoice = invoiceDAS.find(invoiceId);

            if (invoice == null){
                LOG.error("No invoice found for id" + invoiceId);
                continue;
            }
            /* if invoice is in review state then did not generate Invoice Read for it */
            if(invoice.getIsReview()==1){
                LOG.error("Invoice review status " + invoice.getIsReview());
                continue;
            }
            Integer userId = invoice.getUserId();
            UserWS user = new UserBL(userId).getUserWS();
            Boolean isBillReady = isBillReady(user);
            if (isBillReady) {
                billReadyInvoices.add(invoiceId);
            }
        }

        if (billReadyInvoices.size() < 1) {
            LOG.debug("No bill ready invoice found out of " + invoiceIds.size());
            return;
        }

        final List<Integer> invoices = new LinkedList<Integer>(billReadyInvoices);

        // Make separate thread to run the edi generation code
        ediTransactionBean = Context.getBean(Context.Name.EDI_TRANSACTION_SESSION);

        getCompanyMetFields();
//        Thread t = new Thread(new Runnable() {
//            public void run() {
        generateFiles(invoices);
//            }
//        });
//        t.start();
    }

    private boolean isBillReady(UserWS userWS) {
        LOG.debug("Is that customer is bill ready : " + userWS.getId());
        Object planValue = findMetaFieldValue(userWS.getMetaFields(), InvoiceBuildConstants.PLAN.getValue());
        if (planValue == null) return false;

        ItemDTO item = new ItemDAS().findItemByInternalNumber(planValue.toString(), getEntityId());
//        ItemDTOEx item = new ItemBL().getWS(plan);

        PlanDTO planDTO = item.getPlans().iterator().next();
        PlanWS pln = webServicesSessionSpringBean.getPlanWS(planDTO.getId());
        Object value = findMetaFieldValue(pln.getMetaFields(), FileConstants.BILLING_MODEL);
        if (value != null && value.toString().equals(FileConstants.BILLING_MODEL_BILL_READY)) {
            LOG.debug("Plan is bill ready for customer :" + userWS.getUserName());
            return true;
        }

        return false;
    }

    private static Object findMetaFieldValue(MetaFieldValueWS[] metaFieldValueWSes, String metaFieldName) {
        for (MetaFieldValueWS metaFieldValueWS : metaFieldValueWSes) {
            if (metaFieldValueWS.getFieldName().equals(metaFieldName)) {
                LOG.debug("Meta field : " + metaFieldName + " found for  :" + metaFieldValueWS.getValue());
                return metaFieldValueWS.getValue();
            }
        }
        LOG.error("No met field found for " + metaFieldName);
        return null;
    }

    private void generateFiles(List<Integer> billReadyInvoices) {
        LOG.debug("Need to generate Invoice EDI for invoices : " + billReadyInvoices.size());

        Integer ediTypeId = EDI_TYPE_ID;
        EDITypeWS ediType = webServicesSessionSpringBean.getEDIType(ediTypeId);
        FileFormat fileFormat = FileFormat.getFileFormat(ediTypeId);

        String status = "Accepted";

        for (Integer invoiceId : billReadyInvoices) {

            transactionNumber = generateTransactionNumber(invoiceId);
            String FILE_NAME = UTILITY_DUNS + UNDERSCORE_SEPARATOR + SUPPLIER_DUNS + UNDERSCORE_SEPARATOR + transactionNumber + DOT_SEPARATOR + TRANSACTION_SET;

            try {
                InvoiceDTO invoiceDTO = invoiceDAS.find(invoiceId);
                List<Map<String, String>> recordMapList = generateEDIFile(invoiceDTO);
                FlatFileGenerator generator = new FlatFileGenerator(fileFormat, getEntityId(), FILE_NAME, recordMapList);
                EDIFileDTO ediFileDTO = generator.validateAndSaveInput();
                EDIFileWS fileWS = ediTransactionBean.getEDIFileWS(ediFileDTO.getId());

                EDIFileStatusWS processedFileWS = webServicesSessionSpringBean.findEdiStatusById(FileConstants.EDI_STATUS_PROCESSED);
                LOG.debug("fileWS.getEdiFileStatusWS().getName()  " + fileWS.getEdiFileStatusWS().getName());
                if (fileWS.getEdiFileStatusWS().getName().equalsIgnoreCase(processedFileWS.getName())) {
                    EDIFileStatusWS statusWS = null;
                    for (EDIFileStatusWS ediStatus : ediType.getEdiStatuses()) {
                        if (ediStatus.getName().equals(status)) {
                            statusWS = ediStatus;
                        }
                    }
                    fileWS.setEdiFileStatusWS(statusWS);
                    EDIFileBL bl = new EDIFileBL();
                    try {
                        LOG.debug("Saving EDI File. Id : " + fileWS.getId());
                        bl.saveEDIFile(fileWS);
                    } catch (Exception e) {
                        LOG.error("Error Occurred while saving the ediFile status");
                        throw new SessionInternalError(e);
                    }
                }
                EntityType[] entityType = {EntityType.INVOICE};
                MetaField invoiceMeterReadMetafield = metaFieldDAS.getFieldByName(getEntityId(), entityType, FileConstants.META_FIELD_METER_READ_FILE);
                invoiceDTO.setMetaField(invoiceMeterReadMetafield, Integer.parseInt(METER_READ_ID_VALUE));
                invoiceDAS.save(invoiceDTO);

            } catch (Exception e) {
                LOG.error("Error occurred while processing : " + e.getMessage());
                e.printStackTrace();
                throw new SessionInternalError("Error occurred while processing : " + e.getMessage());
            }
        }
    }

    private List<Map<String, String>> generateEDIFile(InvoiceDTO invoiceDTO) {
        LOG.debug("Generate Invoice EDI for invoice : " + invoiceDTO.getId());
        // Fetch invoice
        invoiceWS = new InvoiceBL(invoiceDTO).getWS();
        if (invoiceWS == null) {
            new SessionInternalError("Invoice not found for id : " + invoiceDTO.getId());
        }

        // Get order for that invoice
        Integer[] orders = invoiceWS.getOrders();
        //process orders
        METER_READ_ID_VALUE = null;
        for (InvoiceLineDTO line : invoiceWS.getInvoiceLines()) {
            com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO lineDTO = new InvoiceLineDAS().find(line.getId());
            if (lineDTO == null) {
                continue;
            }
            /* Escaping delegated invoices */
            if(lineDTO.getOrder()==null){
                continue;
            }
            OrderDTO orderDTO = new OrderDAS().find(lineDTO.getOrder().getId());
            orderWS = new OrderBL(orderDTO).getWS(Constants.LANGUAGE_ENGLISH_ID);
            if (orderWS != null) {
                Object meterReadFileId = findMetaFieldValue(orderWS.getMetaFields(), MeterReadParserTask.MeterReadField.edi_file_id.toString());
                if (meterReadFileId != null) {
                    METER_READ_ID_VALUE = meterReadFileId.toString();
                    break;
                }
            }
        }
        if (METER_READ_ID_VALUE == null) {
            throw new SessionInternalError("Meter read file Id not found in order");
        }
        // Fetch customer information
        Integer userId = invoiceWS.getUserId();
        user = webServicesSessionSpringBean.getUserWS(userId);


        List<Map<String, String>> list = new LinkedList<Map<String, String>>();
        list.add(buildKEYRecord());
        list.add(buildHDRRecord());
        list.add(buildNMERecord());
        int i = 1;
        for (InvoiceLineDTO line : invoiceWS.getInvoiceLines()) {
            com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO lineDTO = new InvoiceLineDAS().find(line.getId());
            if (lineDTO == null) {
                continue;
            }
            if (lineDTO.getTypeId() == Constants.INVOICE_LINE_TYPE_ITEM_ONETIME
                    || lineDTO.getTypeId() == Constants.INVOICE_LINE_TYPE_ITEM_RECURRING) {
                list.add(buildSRVRecord(lineDTO, i));
                list.add(buildCHGRecord(lineDTO));
                i++;
            } else if (lineDTO.getTypeId() == Constants.INVOICE_LINE_TYPE_TAX) {
                list.add(buildTaxRecord(lineDTO));
            }
        }
        return list;
    }

    private Map<String, String> buildKEYRecord() {
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("rec-id", InvoiceReadTask.InvoiceRead.KEY.name());
        keyMap.put(FileConstants.SUPPLIER_DUNS_META_FIELD_NAME, SUPPLIER_DUNS);
        keyMap.put(FileConstants.UTILITY_DUNS_META_FIELD_NAME, UTILITY_DUNS);
        keyMap.put(InvoiceBuildConstants.TRANSACTION_SET.getValue(), TRANSACTION_SET);
        keyMap.put(InvoiceBuildConstants.TRANSCTION_SUBSET.name(), InvoiceBuildConstants.TRANSCTION_SUBSET.getValue());

        return keyMap;
    }

    private Map<String, String> buildHDRRecord() {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("rec-id", InvoiceReadTask.InvoiceRead.HDR.name());
        headerMap.put(InvoiceBuildConstants.HDR_INVOICE_DATE.getValue(), convertDateToString(invoiceWS.getCreateDatetime(), "yyyyMMdd"));
        headerMap.put(InvoiceBuildConstants.HDR_INVOICE_NR.getValue(), transactionNumber);

        Object value = fetchEDIFileField(Integer.parseInt(METER_READ_ID_VALUE));
        if (value != null) {
            headerMap.put(InvoiceBuildConstants.METER_READ_TRANS_NR.getValue(), value.toString());
        }

        headerMap.put(InvoiceBuildConstants.HDR_INVOICE_ACTION_CD.name(), InvoiceBuildConstants.HDR_INVOICE_ACTION_CD.getValue());
        headerMap.put(InvoiceBuildConstants.HDR_INVOICE_PURPOSE_CD.name(), InvoiceBuildConstants.HDR_INVOICE_PURPOSE_CD.getValue());
        headerMap.put(InvoiceBuildConstants.HDR_SUPPLIER_CUST_ACCT_NR.getValue(), "" + user.getId());

        //Find meta field that store LDC provided account number
        Object metaFieldValueObject = findMetaFieldValue(user.getMetaFields(), FileConstants.CUSTOMER_ACCOUNT_KEY);
        if (metaFieldValueObject != null) {
            headerMap.put(InvoiceBuildConstants.HDR_UTILITY_CUST_ACCT_NR.getValue(), metaFieldValueObject.toString());
        }

        headerMap.put(InvoiceBuildConstants.HDR_BILL_DELIVER.name(), InvoiceBuildConstants.HDR_BILL_DELIVER.getValue());
        headerMap.put(InvoiceBuildConstants.HDR_BILL_CALC.name(), InvoiceBuildConstants.HDR_BILL_CALC.getValue());
        headerMap.put(InvoiceBuildConstants.HDR_TDSP_NAME.name(), UTILITY_NAME);
        headerMap.put(InvoiceBuildConstants.HDR_CR_NAME.name(), SUPPLIER_NAME);
        headerMap.put(InvoiceBuildConstants.HDR_RECV_DATETIME.getValue(), convertDateToString(new Date(), "yyyyMMddHHmmss"));
        headerMap.put(InvoiceBuildConstants.HDR_UTILITY_SUPPLIER_ACCT_NR.name(), InvoiceBuildConstants.HDR_UTILITY_SUPPLIER_ACCT_NR.getValue());
        headerMap.put(InvoiceBuildConstants.HDR_INVOICE_TOTAL.getValue(), invoiceWS.getBalanceAsDecimal().toString());
        return headerMap;
    }

    private Map<String, String> buildNMERecord() {
        Map<String, String> nmeMap = new HashMap<String, String>();
        nmeMap.put("rec-id", InvoiceReadTask.InvoiceRead.NME.name());
        nmeMap.put(InvoiceBuildConstants.NME_NAME_TYPE.name(), InvoiceBuildConstants.NME_NAME_TYPE.getValue());
        nmeMap.put(InvoiceBuildConstants.NME_NAME.getValue(), fetchCustomerName());
        return nmeMap;
    }

    private Map<String, String> buildSRVRecord(com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO lineDTO, int lineNumber) {
        Map<String, String> srvMap = new HashMap<String, String>();
        srvMap.put("rec-id", InvoiceReadTask.InvoiceRead.SRV.name());
        srvMap.put(InvoiceBuildConstants.SRV_LINE_NUMBER.getValue(), "" + lineNumber);
        srvMap.put(InvoiceBuildConstants.SRV_SERVICE_CLASS.name(), InvoiceBuildConstants.SRV_SERVICE_CLASS.getValue());

        srvMap.put(InvoiceBuildConstants.SRV_START_SERVICE_DT.getValue(), convertDateToString(orderWS.getActiveSince(), "yyyyMMdd"));
        srvMap.put(InvoiceBuildConstants.SRV_END_SERVICE_DT.getValue(), convertDateToString(orderWS.getActiveUntil(), "yyyyMMdd"));

        return srvMap;
    }

    private Map<String, String> buildCHGRecord(com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO lineDTO) {
        Map<String, String> chgMap = new HashMap<String, String>();
        chgMap.put("rec-id", InvoiceReadTask.InvoiceRead.CHG.name());
        chgMap.put(InvoiceBuildConstants.CHG_INDICATOR.name(), InvoiceBuildConstants.CHG_INDICATOR.getValue());
        chgMap.put(InvoiceBuildConstants.CHG_CHARGE_DETERMINENT.name(), InvoiceBuildConstants.CHG_CHARGE_DETERMINENT.getValue());
        chgMap.put(InvoiceBuildConstants.CHG_CHARGE_CODE.name(), InvoiceBuildConstants.CHG_CHARGE_CODE.getValue());
        chgMap.put(InvoiceBuildConstants.CHG_AMOUNT.getValue(), lineDTO.getAmount().toString());
        chgMap.put(InvoiceBuildConstants.CHG_UNIT_RATE.getValue(), lineDTO.getPrice().toString());
        chgMap.put(InvoiceBuildConstants.CHG_UOM.name(), InvoiceBuildConstants.CHG_UOM.getValue());
        chgMap.put(InvoiceBuildConstants.CHG_QUANTITY.getValue(), lineDTO.getQuantity().toString());

        return chgMap;
    }


    public void getCompanyMetFields() {
        CompanyWS companyWS = ediTransactionBean.getCompanyWS(getEntityId());
        MetaFieldValueWS[] metaFieldValues = companyWS.getMetaFields();
        Map<String, Object> companyMetaFieldValueMap = new HashMap<String, Object>();
        for (MetaFieldValueWS metaFieldValueWS : metaFieldValues) {
            companyMetaFieldValueMap.put(metaFieldValueWS.getFieldName(), metaFieldValueWS.getValue());
        }

        EDI_TYPE_ID = (Integer) companyMetaFieldValueMap.get(FileConstants.INVOICE_EDI_TYPE_ID_META_FIELD_NAME);
        SUPPLIER_DUNS = (String) companyMetaFieldValueMap.get(FileConstants.SUPPLIER_DUNS_META_FIELD_NAME);
        UTILITY_DUNS = (String) companyMetaFieldValueMap.get(FileConstants.UTILITY_DUNS_META_FIELD_NAME);

        isValueNull("EDI_TYPE_ID", EDI_TYPE_ID);
        isValueNull("SUPPLIER_DUNS", SUPPLIER_DUNS);
        isValueNull("UTILITY_DUNS", UTILITY_DUNS);

        SUPPLIER_NAME = (String) companyMetaFieldValueMap.get(FileConstants.SUPPLIER_NAME_META_FIELD_NAME);
        UTILITY_NAME = (String) companyMetaFieldValueMap.get(FileConstants.UTILITY_NAME_META_FIELD_NAME);
        BILL_CALC = (String) companyMetaFieldValueMap.get(FileConstants.BILL_CALC_META_FIELD_NAME);
        BILL_DELIVER = (String) companyMetaFieldValueMap.get(FileConstants.BILL_DELIVER_META_FIELD_NAME);

        LOG.debug("EDI_TYPE_ID  is: " + EDI_TYPE_ID);
        LOG.debug("SUPPLIER_DUNS  : " + SUPPLIER_DUNS);
        LOG.debug("UTILITY_DUNS  id is: " + UTILITY_DUNS);

        fileFormat = FileFormat.getFileFormat(EDI_TYPE_ID);
        TRANSACTION_SET = fileFormat.getEdiTypeDTO().getEdiSuffix();
    }


    private Map<String, String> buildTaxRecord(com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO lineDTO) {
        Map<String, String> taxMap = new HashMap<String, String>();
        taxMap.put("rec-id", InvoiceReadTask.InvoiceRead.TAX.name());
        taxMap.put(InvoiceBuildConstants.TAX_LEVEL.name(), InvoiceBuildConstants.TAX_LEVEL.getValue());
        taxMap.put(InvoiceBuildConstants.TAX_CODE.name(), InvoiceBuildConstants.TAX_CODE.getValue());
        taxMap.put(InvoiceBuildConstants.TAX_AMOUNT.getValue(), lineDTO.getAmount().toString());
        taxMap.put(InvoiceBuildConstants.TAX_JURISDICTION_CODE.name(), InvoiceBuildConstants.TAX_JURISDICTION_CODE.getValue());
        taxMap.put(InvoiceBuildConstants.TAX_INCLUDE_TAX.name(), InvoiceBuildConstants.TAX_INCLUDE_TAX.getValue());
        return taxMap;
    }

    private void isValueNull(String key, Object value) {
        if (value == null) {
            throw new SessionInternalError("" + key + " can not be null");
        }
    }

    private String convertDateToString(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    private String generateTransactionNumber(Integer invoiceId) {
        return "" + System.currentTimeMillis() + invoiceId;
    }

    private Object fetchEDIFileField(int meterFileRecord) {

        Conjunction transNR = Restrictions.conjunction();
        transNR.add(Restrictions.eq("record.ediFileRecordHeader", "HDR"));
        transNR.add(Restrictions.eq("ediFileFieldKey", "TRANS_REF_NR"));
        transNR.add(Restrictions.eq("file.id", meterFileRecord));

        List<Object[]> meterReadFileData = new EDIFileDAS().findDataFromField(transNR);
        if (meterReadFileData.size() == 1) {
            Object[] objects = meterReadFileData.get(0);
            return objects[2];
        }
        return null;
    }

    private String fetchCustomerName() {

        AccountTypeWS accountTypeWS = webServicesSessionSpringBean.getAccountType(user.getAccountTypeId());
        AccountInformationTypeDTO accountInformationTypeDTO = null;
        if (accountTypeWS.getDescription(Constants.LANGUAGE_ENGLISH_ID).equals(FileConstants.RESIDENTIAL_ACCOUNT_TYPE)) {
            accountInformationTypeDTO = new AccountInformationTypeDAS().findByName(FileConstants.CUSTOMER_INFORMATION_AIT, user.getEntityId(), accountTypeWS.getId());

        } else if (accountTypeWS.getDescription(Constants.LANGUAGE_ENGLISH_ID).equals(FileConstants.COMMERCIAL_ACCOUNT_TYPE)) {
            accountInformationTypeDTO = new AccountInformationTypeDAS().findByName(FileConstants.BUSINESS_INFORMATION_AIT, user.getEntityId(), accountTypeWS.getId());
        }
        String customerName = null;
        if (accountInformationTypeDTO != null) {
            customerName = (String) getMetaFieldValueByGroupId(FileConstants.NAME, accountInformationTypeDTO.getId());
        }
        LOG.debug("Customer Name for user id \"" + user.getId() + "\" is :" + customerName);
        return customerName;
    }

    public Object getMetaFieldValueByGroupId(String metaFieldName, int groupId) {
        Object value = null;
        for (MetaFieldValueWS metaFieldValueWS : user.getMetaFields()) {
            if (metaFieldValueWS.getGroupId().equals(groupId) && metaFieldValueWS.getFieldName().equals(metaFieldName)) {
                value = metaFieldValueWS.getValue();
            }
        }
        return value;
    }
}
