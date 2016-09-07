/*
 JBILLING CONFIDENTIAL
 _____________________

 [2003] - [2012] Enterprise jBilling Software Ltd.
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Enterprise jBilling Software.
 The intellectual and technical concepts contained
 herein are proprietary to Enterprise jBilling Software
 and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden.
 */

package com.sapienter.jbilling.server.mediation.converter.common.steps;


import com.sapienter.jbilling.server.mediation.CallDataRecord;
import com.sapienter.jbilling.server.mediation.JbillingMediationErrorRecord;
import com.sapienter.jbilling.server.mediation.JbillingMediationRecord;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Result of the process of resolving the CDR-s into JMR
 * <p/>
 * Each mediation step will contribute to the mediation result which at the end,
 * when all steps are executed, will be used to build the JMR record
 *
 * @author Panche Isajeski
 * @since 12/16/12
 */
public class MediationStepResult {

    public static final String ITEM_ID = "itemId";
    public static final String QUANTITY = "quantity";
    public static final String USER_ID = "userId";
    public static final String CURRENCY_ID = "currencyId";

    private Integer jBillingCompanyId;
    private Integer mediationCfgId;
    private String cdrRecordKey;
    private Integer userId;
    private Date eventDate;
    private BigDecimal quantity;
    private String description;
    private Integer currencyId;
    private Integer itemId;
    private String pricingFields;
    private List<String> errors = new ArrayList<>();

    public MediationStepResult() {}

    public MediationStepResult(CallDataRecord callDataRecord) {
        this.jBillingCompanyId = callDataRecord.getEntityId();
        this.mediationCfgId = callDataRecord.getMediationCfgId();
        this.cdrRecordKey = callDataRecord.getKey();
    }

    public String getItemId() {
        if (this.itemId == null)
            return null;
        return "" + this.itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getPricingFields() {
        return pricingFields;
    }

    public void setPricingFields(String pricingFields) {
        this.pricingFields = pricingFields;
    }

    public String getQuantity() {
        if (this.quantity == null) {
            return null;
        }
        return this.quantity.toString();
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
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

    public String getCdrRecordKey() {
        return cdrRecordKey;
    }

    public void setCdrRecordKey(String cdrRecordKey) {
        this.cdrRecordKey = cdrRecordKey;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public JbillingMediationRecord tojBillingMediationRecord() {
        JbillingMediationRecord jbillingMediationRecord = new JbillingMediationRecord(
                JbillingMediationRecord.STATUS.UNPROCESSED, JbillingMediationRecord.TYPE.MEDIATION,
                jBillingCompanyId, mediationCfgId, cdrRecordKey, userId,
                eventDate, quantity, description,
                currencyId, itemId, null, null, pricingFields, null, null, null);
        return jbillingMediationRecord;
    }

    public JbillingMediationErrorRecord toJBillingMediationError() {
        JbillingMediationErrorRecord jbillingMediationRecord = new JbillingMediationErrorRecord(
                jBillingCompanyId, mediationCfgId,
                cdrRecordKey, getErrors(), pricingFields, null, null);

        return jbillingMediationRecord;
    }

    private String getErrors() {
        String errorsString = "[";
        for (String error: this.errors) {
            if (!errorsString.equals("[")) {
                errorsString += ", ";
            }
            errorsString += error;
        }
        errorsString += "]";
        return errorsString;
    }

    @Override
    public String toString() {
        return  "[jBillingCompanyId=" + jBillingCompanyId + ", " +
                "mediationCfgId=" + mediationCfgId + ", " +
                "cdrRecordKey=" + cdrRecordKey + ", " +
                "userId=" + userId + ", " +
                "eventDate=" + eventDate + ", " +
                "quantity=" + quantity + ", " +
                "description=" + description + ", " +
                "currencyId=" + currencyId + ", " +
                "itemId=" + itemId + ", " +
                "pricingFields=" + pricingFields + ", " +
                "]"
                ;

    }

    public void addError(String error) {
        if (!errors.contains(error)) {
            errors.add(error);
        }
    }

    //DEPRECATED METHODS USED DURING THE CURRENT ORDER UPDATE
    private boolean done;
    private boolean persist;
    private Object currentOrder;
    private List<Object> lines;
    private List<Object> oldLines;
    private List<Object> diffLines;

    public void setPersist(boolean persist) {
        this.persist = persist;
    }

    public boolean isPersist() {
        return persist;
    }

    public void setDiffLines(List<Object> diffLines) {
        this.diffLines = diffLines;
    }

    public List<Object> getDiffLines() {
        return diffLines;
    }

    public void setLines(List<Object> lines) {
        this.lines = lines;
    }

    public List<Object> getLines() {
        return lines;
    }

    public void setOldLines(List<Object> oldLines) {
        this.oldLines = oldLines;
    }

    public List<Object> getOldLines() {
        return oldLines;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }

    public void setCurrentOrder(Object currentOrder) {
        this.currentOrder = currentOrder;
    }

    public Object getCurrentOrder() {
        return currentOrder;
    }
}