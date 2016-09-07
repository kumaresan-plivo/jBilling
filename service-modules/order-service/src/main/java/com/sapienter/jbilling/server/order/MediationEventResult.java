package com.sapienter.jbilling.server.order;

import java.math.BigDecimal;

/**
 * Created by marcolin on 03/11/15.
 */
public class MediationEventResult {
    private Integer orderLinedId;
    private Integer costOrderLineId;
    private BigDecimal amountForChange;
    private BigDecimal costAmountForChange;
    private Integer quantityEvaluated;
    private Integer currentOrderId;

    public Integer getCostOrderLineId() {
        return costOrderLineId;
    }

    public void setCostOrderLineId(Integer costOrderLineId) {
        this.costOrderLineId = costOrderLineId;
    }

    public Integer getOrderLinedId() {
        return orderLinedId;
    }

    public void setOrderLinedId(Integer orderLinedId) {
        this.orderLinedId = orderLinedId;
    }

    public BigDecimal getAmountForChange() {
        return amountForChange;
    }

    public void setAmountForChange(BigDecimal amountForChange) {
        this.amountForChange = amountForChange;
    }

    public BigDecimal getCostAmountForChange() {
        return costAmountForChange;
    }

    public void setCostAmountForChange(BigDecimal costAmountForChange) {
        this.costAmountForChange = costAmountForChange;
    }

    public Integer getQuantityEvaluated() {
        return quantityEvaluated;
    }

    public void setQuantityEvaluated(Integer quantityEvaluated) {
        this.quantityEvaluated = quantityEvaluated;
    }

    public Integer getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(Integer currentOrderId) {
        this.currentOrderId = currentOrderId;
    }
}
