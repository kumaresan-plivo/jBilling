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

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.util.Constants;

/**
 * @author Igor Poteryaev
 */
public class EmailAndPaymentUserReader implements ItemReader<Integer> {

    private static final FormatLogger LOG = new FormatLogger(EmailAndPaymentUserReader.class);

    private List<Integer>             ids;

    private StepExecution             stepExecution;

    @BeforeStep
    public void beforeStepStepExecution (StepExecution stepExecution) {
        LOG.debug("Entering beforeStepStepExecution()");
        this.stepExecution = stepExecution;
        Integer minValue = this.stepExecution.getExecutionContext().getInt("minValue");
        Integer maxValue = this.stepExecution.getExecutionContext().getInt("maxValue");
        ids = getIdsInRange(minValue, maxValue);
        LOG.debug("Leaving beforeStepStepExecution() - Total # %s ids were found for", ids.size());
    }

    /**
     * returns next values present in a user list.
     */
    @Override
    public synchronized Integer read () {

        LOG.debug("Entering read()");
        if (ids.size() > 0) {
            Integer removed = ids.remove(0);
            LOG.debug("Returning id # %s from the list of total size # %s", removed, ids.size());
            return removed;
        }
        return null;
    }

    /**
     * returns a subset of user ids that lies with in given range
     * 
     * @param start
     *            : first id of range
     * @param end
     *            : last id of range
     * @return : list of ids that lies within range
     */
    private List<Integer> getIdsInRange (Integer start, Integer end) {
        List<Integer> required = new ArrayList<Integer>();
        @SuppressWarnings("unchecked")
        List<Integer> userIds = (List<Integer>) this.stepExecution
                .getJobExecution()
                .getExecutionContext()
                .get(Constants.JOBCONTEXT_SUCCESSFULL_USERS_LIST_KEY);
        for (Integer id : userIds) {
            if (id >= start && id <= end) {
                required.add(id);
            }
        }
        return required;
    }
}
