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
package com.sapienter.jbilling.server.util;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.sapienter.jbilling.server.util.api.validation.UpdateValidationGroup;

public class PreferenceWS implements Serializable {

    private Integer id;
    private PreferenceTypeWS preferenceType;
    private Integer tableId;
    private Integer foreignId;
    @Size(min=0, max=200, message="validation.error.max,200")
    private String value;
    @Digits(integer=12, fraction=0, message="validation.error.not.a.number.12.integer")
    private String intValue;

    public PreferenceWS() {
    }

    public PreferenceWS(PreferenceTypeWS preferenceType, String value) {
        this.preferenceType = preferenceType;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PreferenceTypeWS getPreferenceType() {
        return this.preferenceType;
    }

    public void setPreferenceType(PreferenceTypeWS preferenceType) {
        this.preferenceType = preferenceType;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }
    
    public Integer getForeignId() {
        return this.foreignId;
    }

    public void setForeignId(Integer foreignId) {
        this.foreignId = foreignId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PreferenceWS{"
               + "id=" + id
               + ", preferenceType=" + preferenceType
               + ", tableId=" + tableId
               + ", foreignId=" + foreignId
               + ", value='" + value + '\''
               + '}';
    }
}
