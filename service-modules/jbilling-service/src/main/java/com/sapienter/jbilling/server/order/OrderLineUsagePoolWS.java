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

import com.sapienter.jbilling.common.Util;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * OrderLineUsagePoolWS
 * The WS object for Order Line Usage Pool association. 
 * Note this is for keeping how much of free quantity was drawn from 
 * each available customer usage pool by a particular order line.
 * @author Amol Gadre
 * @since 01-Dec-2013
 */

public class OrderLineUsagePoolWS implements Serializable {

	private int id;
    private Integer orderLineId;
    private Integer customerUsagePoolId;
    private String quantity;
    
    public OrderLineUsagePoolWS() {
    	
    }
    
    public OrderLineUsagePoolWS(int id, Integer orderLineId, Integer customerUsagePoolId, String quantity) {
    	this.id = id;
    	this.orderLineId = orderLineId;
    	this.customerUsagePoolId = customerUsagePoolId;
    	this.quantity = quantity;	
    }
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getOrderLineId() {
		return orderLineId;
	}
	public void setOrderLineId(Integer orderLineId) {
		this.orderLineId = orderLineId;
	}
	public Integer getCustomerUsagePoolId() {
		return customerUsagePoolId;
	}
	public void setCustomerUsagePoolId(Integer customerUsagePoolId) {
		this.customerUsagePoolId = customerUsagePoolId;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getQuantityAsDecimal() {
        return Util.string2decimal(quantity);
    }

	@Override
	public String toString() {
		return "OrderLineUsagePoolWS [id=" + id + ", orderLineId="
				+ orderLineId + ", customerUsagePoolId=" + customerUsagePoolId
				+ ", quantity=" + quantity + "]";
	}
	
    
}
