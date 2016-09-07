/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2013] Enterprise jBilling Software Ltd.
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

package com.sapienter.jbilling.server.usagePool.task;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.process.IBillingProcessSessionBean;
import com.sapienter.jbilling.server.process.task.AbstractBackwardSimpleScheduledTask;
import com.sapienter.jbilling.server.usagePool.CustomerUsagePoolBL;
import com.sapienter.jbilling.server.usagePool.ICustomerUsagePoolEvaluationSessionBean;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.Context;

/**
 * CustomerUsagePoolEvaluationTask
 * This is the Customer Usage Pool evaluation task, which is a scheduled task 
 * extending AbstractBackwardSimpleScheduledTask. It has been setup to run 
 * every midnight by default.
 * @author Amol Gadre
 * @since 01-Dec-2013
 */

public class CustomerUsagePoolEvaluationTask extends AbstractBackwardSimpleScheduledTask {
	
	private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(CustomerUsagePoolEvaluationTask.class));
	public String getTaskName() {
		return "Customer Usage Pool Evalution Process: , entity id " + getEntityId() + ", taskId " + getTaskId();
	}
	 
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(Util.getSysPropBooleanTrue(Constants.PROPERTY_RUN_CUSTOMER_USAGE_POOL)) {
			super.execute(context);
			ICustomerUsagePoolEvaluationSessionBean
	        usageEvaluationBean = (ICustomerUsagePoolEvaluationSessionBean) 
	        	Context.getBean(Context.Name.CUSTOMER_USAGE_POOL_EVALUATION_SESSION);
					usageEvaluationBean.trigger(getEntityId());
		} else {
			LOG.warn("Failed to trigger CustomerUsagePoolEvaluation process at " + context.getFireTime()
                    + ", another process is already running.");
		}
	}
}
