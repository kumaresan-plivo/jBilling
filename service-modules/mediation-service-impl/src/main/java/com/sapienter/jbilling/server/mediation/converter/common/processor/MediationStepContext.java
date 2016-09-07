package com.sapienter.jbilling.server.mediation.converter.common.processor;/*
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

import com.sapienter.jbilling.server.mediation.CallDataRecord;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.mediation.converter.common.steps.MediationStepResult;

import java.util.List;

/**
 * Created by marcomanzi on 2/26/14.
 */
public class MediationStepContext {

    private MediationStepResult result;
    private CallDataRecord record;
    private Integer entityId;

    private MediationStepContext() {}

    public MediationStepContext(MediationStepResult result, CallDataRecord record, Integer entityId) {
        this.result = result;
        this.record = record;
        this.entityId = entityId;
    }

    public List<PricingField> getPricingFields() {
        return record.getFields();
    }

    public CallDataRecord getRecord() {
        return record;
    }

    public void setRecord(CallDataRecord record) {
        this.record = record;
    }

    public MediationStepResult getResult() {
        return result;
    }

    public void setResult(MediationStepResult result) {
        this.result = result;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }
}
