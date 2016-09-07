package com.sapienter.jbilling.server.provisioning;


import com.sapienter.jbilling.server.security.WSSecured;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProvisioningCommandWS implements WSSecured, Serializable {

    private static final long serialVersionUID = 20131125L;

    private int id;

    private Integer entityId;
    private String name;
    private Integer executionOrder;
    private Date createDate;
    private Date lastUpdateDate;

    private ProvisioningCommandType commandType;
    private ProvisioningCommandStatus commandStatus;

    private Map<String, String> parameterMap = new HashMap<String, String>();
    private ProvisioningRequestWS[] provisioningRequests;
    private Integer owningEntityId;

    private int versionNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public ProvisioningCommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(ProvisioningCommandType commandType) {
        this.commandType = commandType;
    }

    public ProvisioningCommandStatus getCommandStatus() {
        return commandStatus;
    }

    public void setCommandStatus(ProvisioningCommandStatus commandStatus) {
        this.commandStatus = commandStatus;
    }

    public Map<String, String> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public ProvisioningRequestWS[] getProvisioningRequests() {
        return provisioningRequests;
    }

    public void setProvisioningRequests(ProvisioningRequestWS[] provisioningRequests) {
        this.provisioningRequests = provisioningRequests;
    }

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    @Override
    public Integer getOwningEntityId() {
        return owningEntityId;
    }

    public void setOwningEntityId(Integer owningEntityId) {
        this.owningEntityId = owningEntityId;
    }

    @Override
    public Integer getOwningUserId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "ProvisioningCommandWS{"
                + "id=" + id
                + ", entity_id=" + entityId
                + ", execution_order=" + executionOrder
                + ", create_date=" + createDate
                + ", last_update_date=" + lastUpdateDate
                + ", command_status=" + commandStatus
                + ", optlock=" + versionNum
                + '}';
    }

}
