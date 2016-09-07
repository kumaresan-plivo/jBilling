/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */

package com.sapienter.jbilling.batch.billing;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.sapienter.jbilling.common.FormatLogger;

/**
 * @author Igor Poteryaev
 */
public class EmailAndPaymentWriter implements ItemWriter<Integer> {

    private static final FormatLogger LOG = new FormatLogger(EmailAndPaymentWriter.class);

    @Override
    public void write (List<? extends Integer> list) throws Exception {
        for (Integer user : list) {
            LOG.debug("User # %s was successfully processed", user);
        }
    }
}
