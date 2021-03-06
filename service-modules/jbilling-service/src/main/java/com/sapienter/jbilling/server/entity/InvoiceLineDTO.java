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

/*
 * Generated by XDoclet - Do not edit!
 */
package com.sapienter.jbilling.server.entity;

import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Value object for InvoiceLineEntity.
 */
@XmlType(name = "invoice-line")
public class InvoiceLineDTO implements Serializable {

    private Integer id;
    private String description;
    private String amount;
    private String price;
    private String quantity;
    private Integer deleted;
    private Integer itemId;
    private Integer sourceUserId;
    private Integer isPercentage;

    public InvoiceLineDTO() {
    }

    public InvoiceLineDTO(Integer id, String description, BigDecimal amount, BigDecimal price, BigDecimal quantity,
                          Integer deleted, Integer itemId, Integer sourceUserId, Integer percentage) {
        this.id = id;
        this.description = description;
        setAmount(amount);
        setPrice(price);
        setQuantity(quantity);
        this.deleted = deleted;
        this.itemId = itemId;
        this.sourceUserId = sourceUserId;
        isPercentage = percentage;
    }

    public InvoiceLineDTO(InvoiceLineDTO otherValue) {
        this.id = otherValue.id;
        this.description = otherValue.description;
        this.amount = otherValue.amount;
        this.price = otherValue.price;
        this.quantity = otherValue.quantity;
        this.deleted = otherValue.deleted;
        this.itemId = otherValue.itemId;
        this.sourceUserId = otherValue.sourceUserId;
        this.isPercentage = otherValue.isPercentage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = (amount != null ? amount.toString() : null);
    }

    public BigDecimal getAmountAsDecimal() {
        return amount == null ? null : new BigDecimal(amount);
    }

    public void setAmountAsDecimal(BigDecimal amount) {
        setAmount(amount);
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPrice(BigDecimal price) {
        this.price = (price != null ? price.toString() : null);
    }

    public BigDecimal getPriceAsDecimal() {
        return price == null ? null : new BigDecimal(price);
    }

    public void setPriceAsDecimal(BigDecimal price) {
        setPrice(price);
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = (quantity != null ? quantity.toString() : null);
    }

    public BigDecimal getQuantityAsDecimal() {
        return quantity == null ? null : new BigDecimal(quantity);
    }

    public void setQuantityAsDecimal(BigDecimal quantity) {
        setQuantity(quantity);
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(Integer sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public Integer getPercentage() {
        return isPercentage;
    }

    public void setPercentage(Integer percentage) {
        isPercentage = percentage;
    }

    @Override
    public String toString() {
        return "InvoiceLineDTO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                ", quantity=" + quantity +
                ", deleted=" + deleted +
                ", itemId=" + itemId +
                ", sourceUserId=" + sourceUserId +
                ", isPercentage=" + isPercentage +
                '}';
    }
}
