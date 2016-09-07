package com.sapienter.jbilling.server.provisioning;


import com.sapienter.jbilling.server.security.WSSecured;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProvisioningRequestWS implements WSSecured, Serializable {

    private Integer id;
    private String identifier;
    private Integer provisioningCommandId;
    private Integer entityId;
    private String processor;

    private Integer executionOrder;

    private Date createDate;
    private Date submitDate;

    private String submitRequest;
    private String rollbackRequest;

    private ProvisioningRequestStatus requestStatus;
    private Date resultReceivedDate;
    private Map<String, String> resultMap = new HashMap<String, String>();

    private int versionNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    public String getRollbackRequest() {
        return rollbackRequest;
    }

    public void setRollbackRequest(String rollbackRequest) {
        this.rollbackRequest = rollbackRequest;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public Integer getProvisioningCommandId() {
        return provisioningCommandId;
    }

    public void setProvisioningCommandId(Integer provisioningCommandId) {
        this.provisioningCommandId = provisioningCommandId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(Integer executionOrder) {
        this.executionOrder = executionOrder;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public String getSubmitRequest() {
        return submitRequest;
    }

    public void setSubmitRequest(String submitRequest) {
        this.submitRequest = submitRequest;
    }

    public ProvisioningRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(ProvisioningRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Date getResultReceivedDate() {
        return resultReceivedDate;
    }

    public void setResultReceivedDate(Date resultReceivedDate) {
        this.resultReceivedDate = resultReceivedDate;
    }

    public Map<String, String> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, String> resultMap) {
        this.resultMap = resultMap;
    }

    @Override
    public Integer getOwningEntityId() {
        return entityId;
    }

    @Override
    public Integer getOwningUserId() {
        return null;
    }

/*    @Override
    public String toString() {
        return "ProvisioningRequestedWS{"
                + "id=" + id
                + ", entity_id=" + entity
                + ", execution_order=" + executionOrder
                + ", create_date=" + createDate
                + ", last_update_date=" + lastUpdateDate
                + ", command_status=" + commandStatus
                + ", optlock=" + versionNum
                + '}';
    }*/

}
