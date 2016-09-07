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

package com.sapienter.jbilling.server.process;

import com.sapienter.jbilling.server.order.OrderProcessWS;
import com.sapienter.jbilling.server.security.WSSecured;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * BillingProcessWS
 *
 * @author Brian Cowdery
 * @since 25-10-2010
 */
public class BillingProcessWS implements WSSecured, Serializable {
    
    // PaperInvoiceBatchDTO excluded from WS

    private Integer id;
    private Integer entityId;
    private Integer periodUnitId;
    private Integer periodValue;
    private Date billingDate;
    private Date billingDateEnd;
    private Integer isReview;
    private Integer retries;
    private Integer retriesToDo;
    private List<Integer> invoiceIds = new ArrayList<Integer>(0);
    private List<OrderProcessWS> orderProcesses = new ArrayList<OrderProcessWS>(0);
    private List<ProcessRunWS> processRuns = new ArrayList<ProcessRunWS>(0);

    // todo: extensions of ProcessRunDTO and ProcessRunTotalDTO, may not be necessary.
    // List<BillingProcessRunDTOEx> runs
    // BillingProcessRunDTOEx grandTotal

    public BillingProcessWS() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getPeriodUnitId() {
        return periodUnitId;
    }

    public void setPeriodUnitId(Integer periodUnitId) {
        this.periodUnitId = periodUnitId;
    }

    public Integer getPeriodValue() {
        return periodValue;
    }

    public void setPeriodValue(Integer periodValue) {
        this.periodValue = periodValue;
    }

    public Date getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    public Date getBillingDateEnd() {
        return billingDateEnd;
    }

    public void setBillingDateEnd(Date billingDateEnd) {
        this.billingDateEnd = billingDateEnd;
    }

    public Integer getReview() {
        return isReview;
    }

    public void setReview(Integer review) {
        isReview = review;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getRetriesToDo() {
        return retriesToDo;
    }

    public void setRetriesToDo(Integer retriesToDo) {
        this.retriesToDo = retriesToDo;
    }

    public List<Integer> getInvoiceIds() {
        return invoiceIds;
    }

    public void setInvoiceIds(List<Integer> invoiceIds) {
        this.invoiceIds = invoiceIds;
    }

    public List<OrderProcessWS> getOrderProcesses() {
        return orderProcesses;
    }

    public void setOrderProcesses(List<OrderProcessWS> orderProcesses) {
        this.orderProcesses = orderProcesses;
    }

    public List<ProcessRunWS> getProcessRuns() {
        return processRuns;
    }

    public void setProcessRuns(List<ProcessRunWS> processRuns) {
        this.processRuns = processRuns;
    }

    public Integer getOwningEntityId() {
        return getEntityId();
    }

    /**
     * Unsupported, web-service security enforced using {@link #getOwningEntityId()}
     * @return null
     */
    public Integer getOwningUserId() {
        return null;
    }

    @Override
    public String toString() {
        return "BillingProcessWS{"
               + "id=" + id
               + ", entityId=" + entityId
               + ", periodUnitId=" + periodUnitId
               + ", periodValue=" + periodValue
               + ", billingDate=" + billingDate
               + ", billingDateEnd=" + billingDateEnd
               + ", isReview=" + isReview
               + ", retries=" + retries
               + ", retriesToDo=" + retriesToDo
               + ", invoiceIds=" + (invoiceIds != null ? invoiceIds.size() : null)
               + ", orderProcesses=" + (orderProcesses != null ? orderProcesses.size() : null)
               + ", processRuns=" + (processRuns != null ? processRuns.size() : null)
               + '}';
    }
}
