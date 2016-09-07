package com.sapienter.jbilling.server.mediation;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.util.csv.Exportable;

import java.io.Serializable;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

/**
 * Created by marcolin on 06/10/15.
 */
/**
 *         Basic record to mediate. It has all the information cooked so
 *         jBilling can update the current order without any processing.
 *
 */
public class JbillingMediationErrorRecord implements Serializable, Exportable {


    private Integer jBillingCompanyId = null;
    private Integer mediationCfgId = null;
    private String recordKey = null;
    private String errorCodes = null;
    private UUID processId = null;

    // these are pricing fields needed to resolve pricing. For example, the
    // destination number dialed for
    // long distance pricing based on a rate card
    // The String will later be processed with PricingField.getPricingFieldsValue()
    private String pricingFields = null;
    private String status = null;

    public JbillingMediationErrorRecord() {}

    public JbillingMediationErrorRecord(Integer jBillingCompanyId, Integer mediationCfgId,
                                        String recordKey, String errorCodes, String pricingFields, UUID processId, String status) {
        super();
        this.jBillingCompanyId = jBillingCompanyId;
        this.recordKey = recordKey;
        this.mediationCfgId = mediationCfgId;
        this.errorCodes = errorCodes;
        this.pricingFields = pricingFields;
        this.processId = processId;
        this.status = status;
    }

    @Override
    public String toString() {
        return "JbillingMediationRecord{" +
                " jBillingCompanyId='" + jBillingCompanyId + '\'' +
                ", mediationCfgId=" + mediationCfgId +
                ", recordKey=" + recordKey +
                ", processId=" + processId +
                ", errorCode='" + errorCodes + '\'' +
                '}';
    }

    public Integer getjBillingCompanyId() {
        return jBillingCompanyId;
    }

    public void setjBillingCompanyId(Integer jBillingCompanyId) {
        this.jBillingCompanyId = jBillingCompanyId;
    }

    public Integer getMediationCfgId() {
        return mediationCfgId;
    }

    public void setMediationCfgId(Integer mediationCfgId) {
        this.mediationCfgId = mediationCfgId;
    }

    public String getRecordKey() {
        return recordKey;
    }

    public void setRecordKey(String recordKey) {
        this.recordKey = recordKey;
    }

    public String getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(String errorCodes) {
        this.errorCodes = errorCodes;
    }

    public String getPricingFields() {
        return pricingFields;
    }

    public void setPricingFields(String pricingFields) {
        this.pricingFields = pricingFields;
    }

    public UUID getProcessId() {
        return processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String[] getFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        PricingField[] pFields = PricingField.getPricingFieldsValue(pricingFields);
        for(int i=0;i<pFields.length; i++){
            fieldNames.add(pFields[i].getName());
        }
        return fieldNames.toArray(new String[fieldNames.size()]);
    }

    @Override
    public Object[][] getFieldValues() {
        PricingField[] pFields = PricingField.getPricingFieldsValue(pricingFields);
        Object[][] fieldValues = new Object[1][pFields.length];
        for(int i=0;i<pFields.length; i++){
            fieldValues[0][i] = pFields[i].getValue();
        }
        return fieldValues;
    }
}
