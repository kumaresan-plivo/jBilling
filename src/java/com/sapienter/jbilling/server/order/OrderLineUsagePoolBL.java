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

package com.sapienter.jbilling.server.order;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.order.db.OrderLineUsagePoolDAS;
import com.sapienter.jbilling.server.order.db.OrderLineUsagePoolDTO;

/**
 * OrderLineUsagePoolBL
 * This has the find by id method for OrderLineUsagePoolDTO.
 * @author Amol Gadre
 * @since 01-Dec-2013
 */

public class OrderLineUsagePoolBL {
	
	OrderLineUsagePoolDAS olUsagePooldas = null;
	OrderLineUsagePoolDTO olUsagePool = null;

	private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(OrderLineUsagePoolBL.class));


    public OrderLineUsagePoolBL() {
        init();
    }

    private void init() {
        this.olUsagePooldas = new OrderLineUsagePoolDAS();
        this.olUsagePool = new OrderLineUsagePoolDTO();
    }
    
    /**
     * find by id method for OrderLineUsagePoolDTO.
     * @param olUsagePoolId
     * @return OrderLineUsagePoolDTO
     */
    public OrderLineUsagePoolDTO find(Integer olUsagePoolId) {
        return olUsagePooldas.find(olUsagePoolId);
    }
}
