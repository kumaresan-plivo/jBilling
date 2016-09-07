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

public class CommissionProcessConfigurationWS implements WSSecured, Serializable {
    private int id;
    private Integer entityId;
    private Date nextRunDate;
    private Integer periodUnitId;
    private Integer periodValue;

    public CommissionProcessConfigurationWS () {
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

    public Date getNextRunDate () {
        return nextRunDate;
    }

    public void setNextRunDate (Date nextRunDate) {
        this.nextRunDate = nextRunDate;
    }

    public Integer getPeriodUnitId () {
        return periodUnitId;
    }

    public void setPeriodUnitId (Integer periodUnitId) {
        this.periodUnitId = periodUnitId;
    }

    public Integer getPeriodValue () {
        return periodValue;
    }

    public void setPeriodValue (Integer periodValue) {
        this.periodValue = periodValue;
    }

    

    @Override
    public String toString () {
        return "CommissionWS{"
                + "id=" + id
                + ", entityId=" + entityId
                + ", nextRunDate=" + nextRunDate
                + ", periodUnitId=" + periodUnitId
                + '}';

    }

    public Integer getOwningEntityId () {
        return entityId;
    }

    public Integer getOwningUserId () {
        return null;
    }
}
