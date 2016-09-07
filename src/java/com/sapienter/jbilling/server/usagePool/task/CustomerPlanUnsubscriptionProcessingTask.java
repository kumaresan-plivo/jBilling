/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2014] Enterprise jBilling Software Ltd.
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
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.usagePool.CustomerUsagePoolBL;
import com.sapienter.jbilling.server.usagePool.db.CustomerUsagePoolDAS;
import com.sapienter.jbilling.server.usagePool.db.CustomerUsagePoolDTO;
import com.sapienter.jbilling.server.usagePool.event.CustomerPlanSubscriptionEvent;
import com.sapienter.jbilling.server.usagePool.event.CustomerPlanUnsubscriptionEvent;
import com.sapienter.jbilling.server.util.Constants;

/**
 * CustomerPlanUnsubscriptionProcessingTask
 * This is an internal events task that subscribes to CustomerPlanUnsubscriptionEvent. 
 * When a customer unsubscribes from a plan, this task updates the customer usage pool 
 * to set the cycle end date as today's date in case of deletion of plan order or as
 * active until date in case of update to the order with active until date.
 * @author Amol Gadre
 * @since 23-Jan-2014
 */

public class CustomerPlanUnsubscriptionProcessingTask extends PluggableTask
implements IInternalEventsTask {
	
	private static final Logger logger = Logger.getLogger(CustomerPlanUnsubscriptionProcessingTask.class);

	@SuppressWarnings("unchecked")
	private static final Class<Event> events[] = new Class[]{
		CustomerPlanUnsubscriptionEvent.class
	};

	public Class<Event>[] getSubscribedEvents () {
		return events;
	}
	
	/**
	 * This method updates the customer usage pools for all the
	 * usage pools attached to the plan being unsubscribed. 
	 */
	@Override
	public void process(Event event) throws PluggableTaskException {
		
		logger.debug("Entering Customer Plan Unsubscription processing - event: " + event);
		
		CustomerPlanUnsubscriptionEvent customerPlanUnsubribeEvent = (CustomerPlanUnsubscriptionEvent) event;

		Integer entityId = customerPlanUnsubribeEvent.getEntityId();
		Integer orderId = customerPlanUnsubribeEvent.getOrder().getId();
		Integer customerId = customerPlanUnsubribeEvent.getOrder().getBaseUserByUserId().getCustomer().getId();
		
		// we need to update the cycle end date to today's date if the plan order is deleted.
		// if the active until is updated on plan order, the cycle end date = active until
		Date cycleEndDate = new Date(0);
		if (Constants.CUSTOMER_PLAN_UNSUBSCRIBE_UPDATE_ACTIVE_UNTIL.
				equals(customerPlanUnsubribeEvent.getTriggeringAction())) {
			Date activeUntil = customerPlanUnsubribeEvent.getOrder().getActiveUntil();
			if (null != activeUntil) {
				cycleEndDate = activeUntil;
			}
		}
		
		logger.debug("cycleEndDate: " + cycleEndDate);
		
		CustomerUsagePoolDAS das = new CustomerUsagePoolDAS();
		
		for (OrderLineDTO line : customerPlanUnsubribeEvent.getOrder().getLines()) {
			
			if (null != line.getItem() && line.getItem().hasPlans()) {
				// this line contains a plan
				Set<PlanDTO> plans = line.getItem().getPlans();
				
				for (PlanDTO plan : plans) {
					// get the customer usage pools created by this plan
					List<CustomerUsagePoolDTO> customerUsagePools = das.getCustomerUsagePoolsByPlanId(plan.getId());
					CustomerUsagePoolBL bl = new CustomerUsagePoolBL();
					for (CustomerUsagePoolDTO customerUsagePool : customerUsagePools) {
                        if(line.getPurchaseOrder().getUser().getCustomer().equals(customerUsagePool.getCustomer())) {
						    customerUsagePool.setCycleEndDate(cycleEndDate);
						    bl.createOrUpdateCustomerUsagePool(customerUsagePool);
                        }
					}
				}
			}
		}
	}
}
