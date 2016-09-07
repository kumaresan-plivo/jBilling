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

import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.process.IBillingProcessSessionBean;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.Context;

/**
 * @author Igor Poteryaev
 */
public class EmailAndPaymentProcessor extends JobContextHandler implements InitializingBean,
        ItemProcessor<Integer, Integer> {

    private static final FormatLogger  LOG = new FormatLogger(EmailAndPaymentProcessor.class);

    private IBillingProcessSessionBean local;

    private Map<Integer, Integer[]>    map;

    private Integer                    entityId;
    private Integer                    billingProcessId;
    private boolean                    review;

    @Override
    public Integer process (Integer userId) {
        long enteringTime = System.currentTimeMillis();
        LOG.debug("BillingProcessId # %s || UserId # %s +++ Enter process(Integer userId)", billingProcessId, userId);
        Integer[] result = map.get(userId);
        if (!review) {
            LOG.debug("Sending email and processing payments for UserId # %s", userId);
            for (int f = 0; f < result.length; f++) {
                local.email(entityId, result[f], billingProcessId);
            }
            LOG.debug("BillingProcessId # %s || UserId # %s +++ User %s done email & payment.", billingProcessId,
                    userId, userId);
        }
        LOG.debug("BillingProcessId # %s || UserId # %s +++ Leaving process(Integer userId)", billingProcessId, userId);
        long exitTime = System.currentTimeMillis();
        LOG.debug("User # %s executed in # %s secs", userId, (exitTime - enteringTime) / 1000);
        return userId;
    }

    @Override
    public void afterPropertiesSet () {
        LOG.debug("Entering afterPropertiesSet()");

        map = this.getMapFromContext(Constants.JOBCONTEXT_PROCESS_USER_RESULT_KEY);

        billingProcessId = this.getIntegerFromContext(Constants.JOBCONTEXT_BILLING_PROCESS_ID_KEY);
        LOG.debug("billing process id from context: " + billingProcessId);

        local = (IBillingProcessSessionBean) Context.getBean(Context.Name.BILLING_PROCESS_SESSION);
    }

    public void setEntityId (String entityId) {
        this.entityId = Integer.parseInt(entityId);
    }

    public void setReview (String review) {
        this.review = Integer.parseInt(review) == 1 ? true : false;
    }
}
