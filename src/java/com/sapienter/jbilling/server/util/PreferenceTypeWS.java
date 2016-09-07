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
import java.util.HashSet;
import java.util.Set;

import com.sapienter.jbilling.server.metafields.validation.ValidationRuleWS;

public class PreferenceTypeWS implements Serializable {

    private int id;
    private String description;
    private String defaultValue;
    private ValidationRuleWS validationRule;

    public PreferenceTypeWS() {
    }

    public PreferenceTypeWS(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ValidationRuleWS getValidationRule() {
        return validationRule;
    }

    public void setValidationRule(ValidationRuleWS validationRule) {
        this.validationRule = validationRule;
    }

    @Override
    public String toString() {
        return "PreferenceTypeWS{"
               + "id=" + id
               + ", description='" + description + '\''
               + ", defaultValue='" + defaultValue + '\''
               + ", validationRule='" + validationRule + '\''
               + '}';
    }
}
