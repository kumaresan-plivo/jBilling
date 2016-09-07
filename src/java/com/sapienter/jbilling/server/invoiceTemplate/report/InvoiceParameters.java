package com.sapienter.jbilling.server.invoiceTemplate.report;

import java.math.BigDecimal;

import net.sf.jasperreports.engine.JRDataSource;

/**
 * This enumeration contains a definition of all the parameters sent from jBilling to the ITG tool and can then be used
 * from the UI.
 * <p/>
 * Each entry has 2 fields:
 * <p/>
 * <ul>
 * <li>name: Name of the used to call it from the UI.</li>
 * <li>typeClass: Class of the Type for that parameter.</li>
 * </ul>
 * <p/>
 * Created by Juan on 4/18/2014.
 */
public enum InvoiceParameters {
    INVOICE_ID("invoice_id", Integer.class),
    INVOICE_NUMBER("invoice_number", String.class),
    INVOICE_USER_ID("invoice_user_id", Integer.class),
    INVOICE_CREATE_DATETIME("invoice_create_datetime", String.class),
    INVOICE_DUE_DATE("invoice_dueDate", String.class),
    PAYMENT_DUE_IN("payment_due_in_days", String.class),

    BILLING_PERIOD_START_DATE("billing_period_start_date", String.class),
    BILLING_PERIOD_END_DATE("billing_period_end_date", String.class),
    BILLING_DATE("billing_date", String.class),

    OWNER_COMPANY("owner_company", String.class),
    OWNER_STREET_ADDRESS("owner_street_address", String.class),
    OWNER_ZIP("owner_zip", String.class),
    OWNER_CITY("owner_city", String.class),
    OWNER_STATE("owner_state", String.class),
    OWNER_COUNTRY("owner_country", String.class),
    OWNER_PHONE("owner_phone", String.class),
    OWNER_EMAIL("owner_email", String.class),

    RECEIVER_COMPANY("receiver_company", String.class),
    RECEIVER_NAME("receiver_name", String.class),
    RECEIVER_STREET_ADDRESS("receiver_street_address", String.class),
    RECEIVER_ZIP("receiver_zip", String.class),
    RECEIVER_CITY("receiver_city", String.class),
    RECEIVER_STATE("receiver_state", String.class),
    RECEIVER_COUNTRY("receiver_country", String.class),
    RECEIVER_PHONE("receiver_phone", String.class),
    RECEIVER_EMAIL("receiver_email", String.class),
    RECEIVER_ID("receiver_id", String.class),

    CURRENCY_SYMBOL("currency_symbol", String.class),

    MESSAGE_1("message_1", String.class),
    MESSAGE_2("message_2", String.class),
    CUSTOMER_NOTES("customer_notes", String.class),
    INVOICE_NOTES("invoice_notes", String.class),

    SALES_TAX("sales_tax", BigDecimal.class),
    TAX_PRICE("tax_price", String.class),
    TAX_AMOUNT("tax_amount", String.class),

    INVOICE_LINE_TAX_ID("invoice_line_tax_id", Integer.class),

    PAYMENT_TERMS("payment_terms", String.class),
    SUB_TOTAL("sub_total", String.class),
    TOTAL("total", String.class),

    FORMAT_UTIL(FormatUtil.PARAMETER_NAME, FormatUtil.class),

    INVOICE_LINES_DATA_SET(ReportBuildVisitor.INVOICE_LINES_DATASET, JRDataSource.class),
    CDR_LINES_DATA_SET(ReportBuildVisitor.CDR_LINES_DATASET, JRDataSource.class);

    String name;
    Class typeClass;

    InvoiceParameters(String name, Class typeClass) {
        this.name = name;
        this.typeClass = typeClass;
    }

    public String getName() {
        return this.name;
    }

    Class getTypeClass() {
        return this.typeClass;
    }
}
