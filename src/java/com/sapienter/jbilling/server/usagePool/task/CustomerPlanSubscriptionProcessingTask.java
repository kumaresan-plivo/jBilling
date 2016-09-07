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

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import org.apache.log4j.Logger;

import com.sapienter.jbilling.server.invoice.task.ApplyNegativeInvoiceToPaymentTask;
import com.sapienter.jbilling.server.item.db.PlanDAS;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.process.event.InvoiceDeletedEvent;
import com.sapienter.jbilling.server.system.event.Event;
import com.sapienter.jbilling.server.system.event.task.IInternalEventsTask;
import com.sapienter.jbilling.server.usagePool.CustomerUsagePoolBL;
import com.sapienter.jbilling.server.usagePool.UsagePoolWS;
import com.sapienter.jbilling.server.usagePool.db.CustomerUsagePoolDTO;
import com.sapienter.jbilling.server.usagePool.db.UsagePoolDTO;
import com.sapienter.jbilling.server.usagePool.event.CustomerPlanSubscriptionEvent;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.user.db.CompanyDTO;

/**
 * CustomerPlanSubscriptionProcessingTask
 * This is an internal events task that subscribes to CustomerPlanSubscriptionEvent. 
 * When a customer subscribes to plan, this task creates the customer usage pool 
 * association for all usage pools attached on the plan.
 * @author Amol Gadre
 * @since 01-Dec-2013
 */

public class CustomerPlanSubscriptionProcessingTask extends PluggableTask
implements IInternalEventsTask {

	private static final FormatLogger LOG = new FormatLogger(CustomerPlanSubscriptionProcessingTask.class);

	@SuppressWarnings("unchecked")
	private static final Class<Event> events[] = new Class[]{
		CustomerPlanSubscriptionEvent.class
	};

	public Class<Event>[] getSubscribedEvents () {
		return events;
	}

	/**
	 * This method creates the customer usage pool associations for all the
	 * usage pools attached to the plan. The plan is obtained from the plan order
	 * through which customer is subscribing to the plan. 
	 */
	@Override
	public void process(Event event) throws PluggableTaskException {
		
		LOG.debug("Entering Customer Plan Subscription process - event: " + event);
	
		CustomerPlanSubscriptionEvent customerPlanSubEvent = (CustomerPlanSubscriptionEvent) event;

		Integer entityId = customerPlanSubEvent.getEntityId();
        OrderLineDTO line= customerPlanSubEvent.getOrderLine();
		OrderDTO order= line.getPurchaseOrder();
        LOG.debug("Processing plan subscription order id %s, line id %s", order.getId(), line.getId());

		Integer customerId = order.getBaseUserByUserId().getCustomer().getId();
		
        if (!line.getItem().getPlans().isEmpty() && line.getDeleted() == 0) {

           PlanDTO plan = new PlanDAS().findPlanByItemId(line.getItemId());
           CustomerUsagePoolBL customerUsagePoolBl = new CustomerUsagePoolBL();
           for ( UsagePoolDTO usagePool: plan.getUsagePools() ) {

               CustomerUsagePoolDTO customerUsagePool =
                       customerUsagePoolBl.getCreateCustomerUsagePoolDto(usagePool.getId(),
                               customerId, order.getActiveSince(),
                               order.getOrderPeriod(), plan,
                               order.getActiveUntil(), order.getCreateDate());

               customerUsagePoolBl.createOrUpdateCustomerUsagePool(customerUsagePool);
           }
        }
	
		LOG.debug("Customer Plan Subscription process. END.");
	
	}

}
