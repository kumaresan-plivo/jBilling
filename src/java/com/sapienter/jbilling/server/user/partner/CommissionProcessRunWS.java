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


import com.sapienter.jbilling.server.security.WSSecured;

import java.io.Serializable;
import java.util.Date;

public class CommissionProcessRunWS implements WSSecured, Serializable {
    private int id;
    private Date runDate;
    private Date periodStart;
    private Date periodEnd;
    private Integer entityId;

    public CommissionProcessRunWS () {
    }

  
    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public Integer getEntityId () {
        return entityId;
    }

    public void setEntityId (Integer entityId) {
        this.entityId = entityId;
    }

    public Date getRunDate () {
        return runDate;
    }

    public void setRunDate (Date runDate) {
        this.runDate = runDate;
    }

    public Date getPeriodStart () {
        return periodStart;
    }

    public void setPeriodStart (Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd () {
        return periodEnd;
    }

    public void setPeriodEnd (Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    @Override
    public String toString () {
        return "CommissionWS{"
                + "id=" + id
                + ", entityId=" + entityId
                + ", runDate=" + runDate
                + ", periodStart=" + periodStart
                + ", periodEnd=" + periodEnd
                + '}';

    }

    public Integer getOwningEntityId () {
        return entityId;
    }

    public Integer getOwningUserId () {
        return null;
    }
}
