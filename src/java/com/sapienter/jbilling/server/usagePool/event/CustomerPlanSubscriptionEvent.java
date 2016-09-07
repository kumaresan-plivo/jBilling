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

package com.sapienter.jbilling.server.usagePool.event;

import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.system.event.Event;

/**
 * CustomerPlanSubscriptionEvent
 * This is a new Event object that represents the successful 
 * subscription of a customer to a plan.
 * @author Amol Gadre
 * @since 01-Dec-2013
 */

public class CustomerPlanSubscriptionEvent implements Event {

    private final Integer  entityId;
    private final OrderLineDTO line;

    /**
     *    @param entityId
     *    @param planLine
     */
    public CustomerPlanSubscriptionEvent(Integer entityId, OrderLineDTO planLine) {
        this.entityId = entityId;
        this.line    = planLine;
    }
    
    public Integer getEntityId() {
        return entityId;
    }

    /**
     *     @return the order
     */
    public OrderLineDTO getOrderLine() {
        return line;
    }

    public String getName() {
        return "Customer Plan Subscription Event - entity " + entityId;
    }

    public String toString() {
        return getName();
    }

}
