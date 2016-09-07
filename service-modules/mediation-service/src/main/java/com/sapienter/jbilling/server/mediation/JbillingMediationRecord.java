package com.sapienter.jbilling.server.mediation;
import com.sapienter.jbilling.server.util.csv.Exportable;
import com.sapienter.jbilling.server.item.PricingField;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
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
 * Basic record to mediate. It has all the information cooked so
 * jBilling can update the current order without any processing.
 *
 */
public class JbillingMediationRecord implements Serializable, Exportable {

    @XmlType(namespace="MEDIATION")
    public enum STATUS {
        UNPROCESSED(0), PROCESSED(1), NOT_BILLABLE(2);
        private final int id;

        STATUS(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    @XmlType(namespace="MEDIATION")
    public enum TYPE {
        MEDIATION, DIAMETER
    }

    private STATUS status = STATUS.UNPROCESSED;
    private Integer jBillingCompanyId = null;
    private Integer mediationCfgId = null;
    private String recordKey = null;
    private Integer userId = null;
    private Date eventDate = null;
    private BigDecimal quantity = null;
    private String description = null;
    private Integer currencyId = null;
    private Integer itemId = null;
    private Integer orderId = null;
    private Integer orderLineId;
    private BigDecimal ratedPrice;
    private BigDecimal ratedCostPrice;
    private UUID processId;

    // these are pricing fields needed to resolve pricing. For example, the
    // destination number dialed for
    // long distance pricing based on a rate card
    // The String will later be processed with PricingField.getPricingFieldsValue()
    private String pricingFields = null;
    private TYPE type = TYPE.MEDIATION;

    public JbillingMediationRecord() {}

    public JbillingMediationRecord(STATUS status, TYPE type, Integer jBillingCompanyId,
                                   Integer mediationCfgId, String recordKey, Integer userId,
                                   Date eventDate, BigDecimal quantity, String description,
                                   Integer currencyId, Integer itemId, Integer orderId, Integer orderLineId,
                                   String pricingFields, BigDecimal ratedPrice, BigDecimal ratedCostPrice,
                                   UUID processId) {
        this.status = status;
        this.type = type;
        this.jBillingCompanyId = jBillingCompanyId;
        this.mediationCfgId = mediationCfgId;
        this.recordKey = recordKey;
        this.userId = userId;
        this.eventDate = eventDate;
        this.quantity = quantity;
        this.description = description;
        this.currencyId = currencyId;
        this.itemId = itemId;
        this.orderId = orderId;
        this.orderLineId = orderLineId;
        this.pricingFields = pricingFields;
        this.ratedPrice = ratedPrice;
        this.ratedCostPrice = ratedCostPrice;
        this.processId = processId;
    }

    @Override
    public String toString() {
        return "JbillingMediationRecord{" +
                "status='" + status + '\'' +
                ", jBillingEntiytId='" + jBillingCompanyId + '\'' +
                ", mediationCfgId=" + mediationCfgId +
                ", recordKey=" + recordKey +
                ", processId=" + processId +
                ", userId=" + userId +
                ", eventDate='" + eventDate + '\'' +
                ", quantity=" + quantity +
                ", description=" + description +
                ", currencyId='" + currencyId + '\'' +
                ", itemId=" + itemId +
                '}';
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
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

    public BigDecimal getQuantity() {
        return quantity;
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

    public Integer getItemId() {
        return itemId;
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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(Integer orderLineId) {
        this.orderLineId = orderLineId;
    }

    public BigDecimal getRatedPrice() {
        return ratedPrice;
    }

    public void setRatedPrice(BigDecimal ratedPrice) {
        this.ratedPrice = ratedPrice;
    }

    public UUID getProcessId() {
        return processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    public BigDecimal getRatedCostPrice() {
        return ratedCostPrice;
    }

    public void setRatedCostPrice(BigDecimal ratedCostPrice) {
        this.ratedCostPrice = ratedCostPrice;
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
