package com.sapienter.jbilling.server.customerEnrollment.csv;

import com.googlecode.jcsv.reader.CSVEntryParser;
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentStatus;
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentWS;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.PlanDAS;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.metafields.DataType;
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.user.db.*;
import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerEnrollmentEntryParser implements CSVEntryParser<CustomerEnrollmentWS> {

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private CompanyDAS companyDAS = new CompanyDAS();
    private AccountTypeDAS accountTypeDAS = new AccountTypeDAS();
    private AccountInformationTypeDAS accountInformationTypeDAS = new AccountInformationTypeDAS();
    private ItemDAS itemDAS = new ItemDAS();
    private PlanDAS planDAS = new PlanDAS();

    public CustomerEnrollmentWS parseEntry(String... data) {
        CustomerEnrollmentWS customerEnrollment = new CustomerEnrollmentWS();

        customerEnrollment.setId(0);
        customerEnrollment.setCreateDatetime(new Date());
        customerEnrollment.setStatus(CustomerEnrollmentStatus.PENDING);
        customerEnrollment.setBulkEnrollment(true);
        customerEnrollment.setBrokerId(data[0]);
        customerEnrollment.setCompanyName(data[1]);
        customerEnrollment.setAccountNumber(data[21]);

        boolean isResidentialCustomer = data[2].equals("R");
        String accountTypeName = isResidentialCustomer ? FileConstants.RESIDENTIAL_ACCOUNT_TYPE : (data[2].equals("C") || data[2].equals("I") ? FileConstants.COMMERCIAL_ACCOUNT_TYPE : null);

        try {
            CompanyDTO company = companyDAS.findEntityByName(customerEnrollment.getCompanyName());
            Integer companyId = company.getId();
            AccountTypeDTO accountTypeDTO = accountTypeDAS.findAccountTypeByName(companyId, accountTypeName);
            Integer accountTypeId = accountTypeDTO != null ? accountTypeDTO.getId() : null;

            customerEnrollment.setAccountTypeName(accountTypeName);
            customerEnrollment.setEntityId(companyId);
            customerEnrollment.setAccountTypeId(accountTypeId);
            customerEnrollment.setBrokerCatalogVersion(data[4].substring(0, 6));

            // MetaFields mapping
            String planInternalNumber = data[4].substring(6);
            PlanDTO plan = planDAS.findPlanByItemId(itemDAS.findItemByInternalNumber(planInternalNumber, companyId).getId());
            String division = (String) plan.getMetaField(FileConstants.DIVISION).getValue();

            List<MetaFieldValueWS> metaFieldValues = new ArrayList<>();

            if (isResidentialCustomer) {
                // Customer Information
                Integer customerInformationMetaFieldGroupId = accountInformationTypeDAS.getAccountInformationTypeByName(companyId, accountTypeId, FileConstants.CUSTOMER_INFORMATION_AIT).getId();
                metaFieldValues.add(new MetaFieldValueWS(FileConstants.DIVISION, customerInformationMetaFieldGroupId, DataType.ENUMERATION, true, division));
                metaFieldValues.add(new MetaFieldValueWS("NAME", customerInformationMetaFieldGroupId, DataType.STRING, true, data[5]));
                metaFieldValues.add(new MetaFieldValueWS("ADDRESS1", customerInformationMetaFieldGroupId, DataType.STRING, true, data[6]));
                metaFieldValues.add(new MetaFieldValueWS("ADDRESS2", customerInformationMetaFieldGroupId, DataType.STRING, false, data[7]));
                metaFieldValues.add(new MetaFieldValueWS("CITY", customerInformationMetaFieldGroupId, DataType.STRING, true, data[8]));
                metaFieldValues.add(new MetaFieldValueWS(FileConstants.STATE, customerInformationMetaFieldGroupId, DataType.ENUMERATION, true, data[9]));
                metaFieldValues.add(new MetaFieldValueWS("ZIP_CODE", customerInformationMetaFieldGroupId, DataType.STRING, true, data[10]));
                metaFieldValues.add(new MetaFieldValueWS("TELEPHONE", customerInformationMetaFieldGroupId, DataType.STRING, false, data[11]));
                metaFieldValues.add(new MetaFieldValueWS("Email", customerInformationMetaFieldGroupId, DataType.STRING, true, data[13]));
            }
            else {
                // Business Information
                Integer businessInformationMetaFieldGroupId = accountInformationTypeDAS.getAccountInformationTypeByName(companyId, accountTypeId, FileConstants.BUSINESS_INFORMATION_AIT).getId();
                metaFieldValues.add(new MetaFieldValueWS(FileConstants.DIVISION, businessInformationMetaFieldGroupId, DataType.ENUMERATION, true, division));
                metaFieldValues.add(new MetaFieldValueWS("NAME", businessInformationMetaFieldGroupId, DataType.STRING, true, data[5]));
                metaFieldValues.add(new MetaFieldValueWS("ADDRESS1", businessInformationMetaFieldGroupId, DataType.STRING, true, data[6]));
                metaFieldValues.add(new MetaFieldValueWS("ADDRESS2", businessInformationMetaFieldGroupId, DataType.STRING, false, data[7]));
                metaFieldValues.add(new MetaFieldValueWS("CITY", businessInformationMetaFieldGroupId, DataType.STRING, true, data[8]));
                metaFieldValues.add(new MetaFieldValueWS(FileConstants.STATE, businessInformationMetaFieldGroupId, DataType.ENUMERATION, true, data[9]));
                metaFieldValues.add(new MetaFieldValueWS("ZIP_CODE", businessInformationMetaFieldGroupId, DataType.STRING, true, data[10]));
                metaFieldValues.add(new MetaFieldValueWS("TELEPHONE", businessInformationMetaFieldGroupId, DataType.STRING, false, data[11]));
                metaFieldValues.add(new MetaFieldValueWS("Email", businessInformationMetaFieldGroupId, DataType.STRING, true, data[13]));

                // Contact Information
                Integer contactInformationMetaFieldGroupId = accountInformationTypeDAS.getAccountInformationTypeByName(companyId, accountTypeId, FileConstants.CONTACT_INFORMATION_AIT).getId();
                metaFieldValues.add(new MetaFieldValueWS("NAME", contactInformationMetaFieldGroupId, DataType.STRING, true, data[14]));
                metaFieldValues.add(new MetaFieldValueWS("ADDRESS1", contactInformationMetaFieldGroupId, DataType.STRING, true, data[15]));
                metaFieldValues.add(new MetaFieldValueWS("ADDRESS2", contactInformationMetaFieldGroupId, DataType.STRING, false, data[16]));
                metaFieldValues.add(new MetaFieldValueWS("CITY", contactInformationMetaFieldGroupId, DataType.STRING, true, data[17]));
                metaFieldValues.add(new MetaFieldValueWS(FileConstants.STATE, contactInformationMetaFieldGroupId, DataType.ENUMERATION, true, data[18]));
                metaFieldValues.add(new MetaFieldValueWS("ZIP_CODE", contactInformationMetaFieldGroupId, DataType.STRING, true, data[19]));
                metaFieldValues.add(new MetaFieldValueWS("TELEPHONE", contactInformationMetaFieldGroupId, DataType.STRING, false, data[20]));
            }

            // Account Information
            Integer accountInformationMetaFieldGroupId = accountInformationTypeDAS.getAccountInformationTypeByName(companyId, accountTypeId, FileConstants.ACCOUNT_INFORMATION_AIT).getId();
            metaFieldValues.add(new MetaFieldValueWS(FileConstants.COMMODITY, accountInformationMetaFieldGroupId, DataType.ENUMERATION, true, data[3].equals("E") ? "Electricity" : (data[3].equals("G") ? "Gas" : null)));
            metaFieldValues.add(new MetaFieldValueWS("Notification Method", accountInformationMetaFieldGroupId, DataType.ENUMERATION, true, data[12].equals("E") ? "Email" : (data[12].equals("R") ? "Paper" : (data[12].equals("B") ? "Both" : null))));
            MetaFieldValue durationMetaFieldValue = plan.getMetaField(FileConstants.DURATION);
            if (durationMetaFieldValue != null) {
                metaFieldValues.add(new MetaFieldValueWS(FileConstants.DURATION, accountInformationMetaFieldGroupId, DataType.STRING, true, durationMetaFieldValue.getValue().toString()));
            }
            metaFieldValues.add(new MetaFieldValueWS(FileConstants.PLAN, accountInformationMetaFieldGroupId, DataType.ENUMERATION, true, planInternalNumber));
            metaFieldValues.add(new MetaFieldValueWS(FileConstants.CUSTOMER_ACCOUNT_KEY, accountInformationMetaFieldGroupId, DataType.STRING, true, data[21]));
            metaFieldValues.add(new MetaFieldValueWS("METER_TYPE", accountInformationMetaFieldGroupId, DataType.STRING, true, data[22].equals("N") ? "Non Interval" : (data[22].equals("I") ? "Interval" : (data[22].equals("U") ? "Unknown" : null))));
            metaFieldValues.add(new MetaFieldValueWS(FileConstants.CUST_ENROLL_AGREE_DT, accountInformationMetaFieldGroupId, DataType.DATE, false, StringUtils.isNotEmpty(data[23]) ? dateFormat.parse(data[23]) : null));
            metaFieldValues.add(new MetaFieldValueWS(FileConstants.CUST_LIFE_SUPPORT, accountInformationMetaFieldGroupId, DataType.BOOLEAN, true, data[24].equals("Y")));

            customerEnrollment.setMetaFields(metaFieldValues.toArray(new MetaFieldValueWS[metaFieldValues.size()]));
        }
        catch (CustomerEnrollmentEntryParserException e) {
            throw e;
        }
        catch (Exception e) {
            throw new CustomerEnrollmentEntryParserException(customerEnrollment, "Incomplete or invalid data");
        }

        return customerEnrollment;
    }
}