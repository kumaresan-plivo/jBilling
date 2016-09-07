package com.sapienter.jbilling.server.customerEnrollment.csv;

import com.googlecode.jcsv.writer.CSVEntryConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CustomerEnrollmentResponseEntryConverter implements CSVEntryConverter<CustomerEnrollmentResponse> {

    private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

    @Override
    public String[] convertEntry(CustomerEnrollmentResponse customerEnrollmentResponse) {
        String[] columns = new String[6];

        columns[0] = customerEnrollmentResponse.getBrokerId();
        columns[1] = customerEnrollmentResponse.getLdc();
        columns[2] = customerEnrollmentResponse.getAccountNumber();
        columns[3] = customerEnrollmentResponse.getCode().name();
        columns[4] = customerEnrollmentResponse.getReason();
        columns[5] = dateFormat.format(customerEnrollmentResponse.getTimestamp());

        return columns;
    }
}