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
package com.sapienter.jbilling.server.user.partner;


import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.security.WSSecured;

import java.io.Serializable;
import java.math.BigDecimal;

public class CommissionWS implements WSSecured, Serializable {
    private int id;
    
    private String amount;
    private String type;
    private Integer partnerId;
    private Integer commissionProcessRunId;
    private Integer currencyId;
    private Integer owningEntityId;

    public CommissionWS () {
    }

   
    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getAmount () {
        return amount;
    }

    public BigDecimal getAmountAsDecimal() {
        return Util.string2decimal(amount);
    }

    public void setAmount (String amount) {
        this.amount = amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = (amount != null ? amount.toString() : null);
    }

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public Integer getPartnerId () {
        return partnerId;
    }

    public void setPartnerId (Integer partnerId) {
        this.partnerId = partnerId;

    }

    public Integer getCommissionProcessRunId () {
        return commissionProcessRunId;
    }

    public void setCommissionProcessRunId (Integer commissionProcessRunId) {
        this.commissionProcessRunId = commissionProcessRunId;
    }

    @Override
    public String toString () {
        return "CommissionWS{"
                + "id=" + id
                + ", amount=" + amount
                + ", type=" + type
                + ", partnerId=" + partnerId
                + ", commissionProcessRunId=" + commissionProcessRunId
                + ", currencyId=" + currencyId
                + '}';

    }

    public Integer getOwningEntityId () {
       return owningEntityId;
    }
    
    public void setOwningEntityId(Integer owningEntityId){
    	this.owningEntityId = owningEntityId;
    }

    public Integer getOwningUserId () {
        return null;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    
}
