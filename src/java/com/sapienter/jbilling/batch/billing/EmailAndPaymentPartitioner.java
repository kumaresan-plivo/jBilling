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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.util.Constants;

/**
 * @author Igor Poteryaev
 */
public class EmailAndPaymentPartitioner implements InitializingBean, Partitioner {

    private static final FormatLogger LOG = new FormatLogger(EmailAndPaymentPartitioner.class);

    private List<Integer>             ids;

    @Value("#{stepExecution}")
    private StepExecution             stepExecution;

    @Override
    public Map<String, ExecutionContext> partition (int gridSize) {
        LOG.debug("Entering partition(), where gridSize # %s", gridSize);
        int size = ids.size() - 1;
        int targetSize = size / gridSize + 1;
        LOG.debug("Target size for each step # %s", targetSize);

        Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
        int number = 0;
        int start = 0;
        int end = start + targetSize - 1;

        while (start <= size) {
            ExecutionContext value = new ExecutionContext();
            result.put("email-partition" + number, value);

            if (end >= size) {
                end = size;
            }
            value.putInt("minValue", ids.get(start));
            value.putInt("maxValue", ids.get(end));
            start += targetSize;
            end += targetSize;
            number++;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet () throws Exception {
        LOG.debug("Entering afterPropertiesSet() - stepExecution: %s", this.stepExecution);
        this.ids = (List<Integer>) this.stepExecution
                .getJobExecution()
                .getExecutionContext()
                .get(Constants.JOBCONTEXT_SUCCESSFULL_USERS_LIST_KEY);
        // sorts list in ascending order so that we can partition ids across multiple step executions
        Collections.sort(this.ids);
        LOG.debug("Leaving afterPropertiesSet() - stepExecution: %s, ids.size: ", this.stepExecution, this.ids.size());
    }
}