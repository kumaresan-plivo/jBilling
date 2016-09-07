package com.sapienter.jbilling.server.mediation.converter.db;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
@Entity
@Table(name="jbilling_mediation_record")
@IdClass(JbillingMediationRecordId.class)
public class JbillingMediationRecordDao implements Serializable {

    public enum STATUS {
        UNPROCESSED, PROCESSED, NOT_BILLABLE
    }

    public enum TYPE {
        MEDIATION, DIAMETER
    }


    @Enumerated(value = EnumType.STRING)
    @Column(name="status", nullable=false)
    private STATUS status = STATUS.UNPROCESSED;
    @Enumerated(value = EnumType.STRING)
    @Column(name="type", nullable=false)
    private TYPE type = TYPE.MEDIATION;
    @Column(name="jbilling_entity_id")
    private Integer jBillingCompanyId = null;
    @Column(name="mediation_cfg_id")
    private Integer mediationCfgId = null;
    @Id
    @Column(name="record_key")
    private String recordKey = null;
    @Column(name="user_id")
    private Integer userId = null;
    @Id
    @Column(name="event_date")
    private Date eventDate = null;
    @Column(name="processing_date")
    private Date processingDate = null;
    @Column(name="quantity")
    private BigDecimal quantity = null;
    @Column(name="description")
    private String description = null;
    @Column(name="currency_id")
    private Integer currencyId = null;
    @Column(name="item_id")
    private Integer itemId = null;
    @Column(name="order_id")
    private Integer orderId = null;
    @Column(name="order_line_id")
    private Integer orderLineId = null;
    @Column(name="rated_price")
    private BigDecimal ratedPrice;
    @Column(name="rated_cost_price")
    private BigDecimal ratedCostPrice;
    @Column(name="process_id")
    private UUID processId = null;

    // these are pricing fields needed to resolve pricing. For example, the
    // destination number dialed for
    // long distance pricing based on a rate card
    // The String will later be processed with PricingField.getPricingFieldsValue()
    @Column(name="pricing_fields", length = 1000)
    private String pricingFields = null;

    public JbillingMediationRecordDao() {}

    public JbillingMediationRecordDao(STATUS status, TYPE type, Integer jBillingCompanyId,
                                      Integer mediationCfgId, String recordKey, Integer userId,
                                      Date eventDate, BigDecimal quantity, String description,
                                      Integer currencyId, Integer itemId, Integer orderId, Integer orderLineId,
                                      String pricingFields, BigDecimal ratedPrice, BigDecimal ratedCostPrice, UUID processId) {
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
        this.pricingFields = pricingFields;
        this.orderLineId = orderLineId;
        this.ratedPrice = ratedPrice;
        this.ratedCostPrice = ratedCostPrice;
        this.processId = processId;
        this.processingDate = new Date();
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

    public String getPricingFields() {
        return pricingFields;
    }

    public void setPricingFields(String pricingFields) {
        this.pricingFields = pricingFields;
    }

    public BigDecimal getRatedPrice() {
        return ratedPrice;
    }

    public void setRatedPrice(BigDecimal ratedPrice) {
        this.ratedPrice = ratedPrice;
    }

    public BigDecimal getRatedCostPrice() {
        return ratedCostPrice;
    }

    public void setRatedCostPrice(BigDecimal ratedCostPrice) {
        this.ratedCostPrice = ratedCostPrice;
    }

    public UUID getProcessId() {
        return processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }
}
