package com.sapienter.jbilling.NGES;

import com.sapienter.jbilling.server.metafields.DataType;
import com.sapienter.jbilling.server.metafields.EntityType;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.test.framework.TestEnvironmentBuilder;
import com.sapienter.jbilling.test.framework.builders.AccountTypeBuilder;
import com.sapienter.jbilling.test.framework.builders.ConfigurationBuilder;
import com.sapienter.jbilling.test.framework.TestBuilder;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by marcomanzicore on 27/11/15.
 */
@org.testng.annotations.Test(groups = {"integration", "nges"})
public abstract class NGESBaseTest {
    public TestBuilder getNGESEnvironment() {
        return TestBuilder.newTest().given(testEnvironmentCreator -> {
            createParentConfiguration(testEnvironmentCreator);
            createChildConfiguration(testEnvironmentCreator);
        });
    }

    private void createChildConfiguration(TestEnvironmentBuilder testEnvironmentBuilder) {
        JbillingAPI resellerApi = testEnvironmentBuilder.getResellerApi();
        ConfigurationBuilder configurationBuilder = testEnvironmentBuilder.configurationBuilder(resellerApi );
        addDataTables(configurationBuilder);
        addEnumerationsForChildCompany(configurationBuilder);
        addMetaFieldsForChildCompany(configurationBuilder);
        addPluginsForChildCompany(configurationBuilder);
        configurationBuilder.build();
    }

    private void addPluginsForChildCompany(ConfigurationBuilder configurationBuilder) {
        configurationBuilder
                .addPluginWithParameters("com.sapienter.jbilling.server.customerEnrollment.task.CustomerEnrollmentFileGenerationTask",
                        new Hashtable<String, String>() {{ put("edi-status", "Ready to send"); }})
                .addPlugin("com.sapienter.jbilling.server.customerEnrollment.task.BrokerResponseManagerTask")
                .addPlugin("com.sapienter.jbilling.server.customerEnrollment.task.BrokerCatalogCreatorTask")
                .addPluginWithParameters("com.sapienter.jbilling.server.customerEnrollment.task.SendEnrollmentTask",
                        new Hashtable<String, String>() {{
                            put("success_status", "Sent to LDC");
                            put("error_status", "File transfer error");
                            put("source_status", "Ready to send");
                            put("cron_exp", "0 0/1 * * * ?");
                        }})
                .addPluginWithParameters("com.sapienter.jbilling.server.customerEnrollment.task.EnrollmentResponseParserTask",
                        new Hashtable<String, String>() {{
                            put("accept_status", "Accepted");
                            put("reject_status", "Rejected");
                            put("invalid_file_status", "Invalid File");
                            put("success_status", "Sent to LDC");
                            put("internal_error", "Internal Error");
                            put("cron_exp", "10 0/1 * * * ?");
                        }})
                .addPluginWithParameters("com.sapienter.jbilling.server.ediTransaction.task.MeterReadParserTask",
                        new Hashtable<String, String>() {{
                            put("replacement_status", "EXP002");
                            put("done_status", "Done");
                            put("historical_status", "Historical Meter Read");
                            put("cancellation_status", "EXP001");
                            put("rejected_status", "Rejected");
                            put("invalid_data_status", "Invalid Data");
                            put("cron_exp", "20 1/3 * * * ?");
                        }})
                .addPluginWithParameters("com.sapienter.jbilling.server.ediTransaction.task.PaymentParserTask",
                        new Hashtable<String, String>() {{
                            put("inconsistent_payment_status", "INCONSISTENT_PAYMENT");
                            put("rejected", "Rejected");
                            put("invalid_data", "INVALID_DATA");
                            put("duplication_transaction", "DUPLICATE_PAYMENT");
                            put("done", "DONE");
                            put("cron_exp", "30 2/3 * * * ?");
                        }})
                .addPluginWithParameters("com.sapienter.jbilling.server.earlyTermination.task.CustomerTerminationTask",
                        new Hashtable<String, String>() {{
                            put("accept_status", "Accepted");
                            put("reject_status", "Rejected");
                            put("invalid_file_status", "Invalid File");
                            put("cron_exp", "40 1/3 * * * ?");
                        }})
                .addPluginWithParameters("com.sapienter.jbilling.server.ediTransaction.invoiceRead.InvoiceReadTask",
                        new Hashtable<String, String>() {{
                            put("replacement_status", "EXP002");
                            put("file_mismatch_status", "MisMatch");
                            put("invalid_file_status", "Invalid File");
                            put("accepted_status", "Accepted");
                            put("cancellation_status", "EXP001");
                            put("rejected_status", "Rejected");
                            put("meter_file_status", "Done");
                            put("deprecated_status", "Deprecated");
                            put("cron_exp", "50 0/3 * * * ?");
                        }})
                .addPluginWithParameters("com.sapienter.jbilling.server.ediTransaction.task.AcknowledgementParserTask",
                        new Hashtable<String, String>() {{
                            put("accept_status", "Accepted");
                            put("reject_status", "Rejected");
                            put("invalid_file_status", "Invalid File");
                            put("acknowledge_status", "Acknowledged");
                            put("cron_exp", "45 1/3 * * * ?");
                        }})
                .addPluginWithParameters("com.sapienter.jbilling.server.customerEnrollment.task.BulkEnrollmentReaderTask",
                        new Hashtable<String, String>() {{
                            put("cron_exp", "0 2/3 * * * ?");
                        }});
    }

    private void addDataTables(ConfigurationBuilder configurationBuilder) {
        String ngesPlansFilePath = "/com/sapienter/jbilling/NGES/nges_plans.csv";
        String ngesCalendarFilePath = "/com/sapienter/jbilling/NGES/nges_calender_2015.csv";
        configurationBuilder.addRoute("Plans", ngesPlansFilePath);
        configurationBuilder.addRoute("Calendar2015", ngesCalendarFilePath);
    }

    private void addEnumerationsForChildCompany(ConfigurationBuilder configurationBuilder) {
        configurationBuilder
                .addEnumeration("Termination", "Termination Processing", "Dropped", "Esco Initiated", "Esco Rejected")
                .addEnumeration("Billing Model", "Rate Ready", "Bill Ready", "Dual", "Supplier Consolidated")
                .addEnumeration("Notification Method", "Email", "Paper", "Both");
    }

    private void addMetaFieldsForChildCompany(ConfigurationBuilder configurationBuilder) {
        configurationBuilder
                .addMetaField("BlackList", DataType.BOOLEAN, EntityType.CUSTOMER)
                .addMetaField("Rate", DataType.DECIMAL, EntityType.CUSTOMER)
                .addMetaField("CYCLE_NUMBER", DataType.INTEGER, EntityType.CUSTOMER)
                .addMetaField("LAST_ENROLLMENT", DataType.DATE, EntityType.CUSTOMER)
                .addMetaField("Termination", DataType.ENUMERATION, EntityType.CUSTOMER)

                .addMetaField("COMMODITY", DataType.STRING, EntityType.PRODUCT)

                .addMetaField("edi_file_id", DataType.STRING, EntityType.ORDER)

                .addMetaField("INVOICE_NR", DataType.STRING, EntityType.INVOICE)
                .addMetaField("Suretax Response Trans Id", DataType.INTEGER, EntityType.INVOICE)
                .addMetaField("Meter read file", DataType.INTEGER, EntityType.INVOICE)

                .addMetaField("Billing Model", DataType.ENUMERATION, EntityType.PLAN)
                .addMetaField("DIVISION", DataType.ENUMERATION, EntityType.PLAN)

                .addMetaField("Lead Time 1", DataType.INTEGER, EntityType.COMPANY)
                .addMetaField("Lead Time 2", DataType.INTEGER, EntityType.COMPANY)
                .addMetaField("SUPPLIER_DUNS", DataType.STRING, EntityType.COMPANY)
                .addMetaField("METER_TYPE", DataType.STRING, EntityType.COMPANY)
                .addMetaField("SERVICE_REQUESTED2", DataType.STRING, EntityType.COMPANY)
                .addMetaField("Cycle Calendar", DataType.STRING, EntityType.COMPANY)
                .addMetaField("Buffer Time", DataType.INTEGER, EntityType.COMPANY)
                .addMetaField("SUPPLIER_NAME", DataType.STRING, EntityType.COMPANY)
                .addMetaField("UTILITY_DUNS", DataType.STRING, EntityType.COMPANY)
                .addMetaField("ACKNOWLEDGE_EDI_TYPE", DataType.INTEGER, EntityType.COMPANY)
                .addMetaField("Rate Code Table Name", DataType.STRING, EntityType.COMPANY)
                .addMetaField("UTILITY_NAME", DataType.STRING, EntityType.COMPANY)
                .addMetaField("BILL_CALC", DataType.STRING, EntityType.COMPANY)
                .addMetaField("BILL_DELIVER", DataType.STRING, EntityType.COMPANY)
                .addMetaField("ENROLLMENT_EDI_TYPE_ID", DataType.STRING, EntityType.COMPANY)
                .addMetaField("METER_READ_EDI_TYPE_ID", DataType.STRING, EntityType.COMPANY)
                .addMetaField("PAYMENT_EDI_TYPE_ID", DataType.STRING, EntityType.COMPANY)
                .addMetaField("INVOICE_EDI_TYPE_ID", DataType.STRING, EntityType.COMPANY)
                .addMetaField("TERMINATION_EDI_TYPE_ID", DataType.STRING, EntityType.COMPANY)
                .addMetaField("CHANGE_REQUEST_EDI_TYPE_ID", DataType.STRING, EntityType.COMPANY)
                .addMetaField("ESCO_TERMINATION_EDI_TYPE_ID", DataType.STRING, EntityType.COMPANY)
                .addMetaField("Wiring Instructions Line 1", DataType.STRING, EntityType.COMPANY)
                .addMetaField("Wiring Instructions Line 2", DataType.STRING, EntityType.COMPANY)
                .addMetaField("Wiring Instructions Line 3", DataType.STRING, EntityType.COMPANY)
                .addMetaField("Wiring Instructions Line 4", DataType.STRING, EntityType.COMPANY)
                .addMetaField("Customer Emergency Phone Nr", DataType.STRING, EntityType.COMPANY)
                .addMetaField("Product Categories in Invoice", DataType.STRING, EntityType.COMPANY);
    }

    private void createParentConfiguration(TestEnvironmentBuilder testEnvironmentBuilder) {
        JbillingAPI prancingPonyApi = testEnvironmentBuilder.getPrancingPonyApi();
        ConfigurationBuilder configurationBuilder = testEnvironmentBuilder.configurationBuilder(prancingPonyApi);
        addEnumerationsForParentCompany(configurationBuilder)
                .addMetaField("BlackList", DataType.BOOLEAN, EntityType.CUSTOMER);
        addAccountTypesForParentCompany(testEnvironmentBuilder.accountTypeBuilder(prancingPonyApi));
        configurationBuilder.build();
    }

    private void addAccountTypesForParentCompany(AccountTypeBuilder accountTypeBuilder) {
        accountTypeBuilder.withName("Residential").addAccountInformationType("Customer Information",
                new HashMap<String, DataType>() {{
                    put("DIVISION", DataType.ENUMERATION);
                    put("NAME", DataType.STRING);
                    put("ADDRESS1", DataType.STRING);
                    put("ADDRESS2", DataType.STRING);
                    put("CITY", DataType.STRING);
                    put("STATE", DataType.ENUMERATION);
                    put("ZIP_CODE", DataType.STRING);
                    put("TELEPHONE", DataType.STRING);
                    put("Email", DataType.STRING);
                }}).build();
        accountTypeBuilder.withName("Commercial/Industrial").addAccountInformationType("Business Information",
                new HashMap<String, DataType>() {{
                    put("DIVISION", DataType.ENUMERATION);
                    put("NAME", DataType.STRING);
                    put("ADDRESS1", DataType.STRING);
                    put("ADDRESS2", DataType.STRING);
                    put("CITY", DataType.STRING);
                    put("STATE", DataType.ENUMERATION);
                    put("ZIP_CODE", DataType.STRING);
                    put("TELEPHONE", DataType.STRING);
                    put("Email", DataType.STRING);
                }}).addAccountInformationType("AccountInformation Information",
                new HashMap<String, DataType>() {{
                    put("NAME", DataType.STRING);
                    put("ADDRESS1", DataType.STRING);
                    put("ADDRESS2", DataType.STRING);
                    put("CITY", DataType.STRING);
                    put("STATE", DataType.ENUMERATION);
                    put("ZIP_CODE", DataType.STRING);
                    put("TELEPHONE", DataType.STRING);
                }}).build();
    }

    protected ConfigurationBuilder addEnumerationsForParentCompany(ConfigurationBuilder configurationBuilder) {
        return configurationBuilder.addEnumeration("STATE", "New York", "Texas")
                .addEnumeration("DIVISION", "East", "West", "North", "South");
    }
}
