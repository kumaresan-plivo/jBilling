package com.sapienter.jbilling.server.mediation;

import com.sapienter.jbilling.server.util.csv.Exportable;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by andres on 19/10/15.
 */
public class MediationProcess implements Serializable, Exportable {

    private UUID id;
    private Integer entityId;
    private Boolean global = false;
    private Date startDate;
    private Date endDate;
    private Integer recordsProcessed = 0;
    private Integer doneAndBillable = 0;
    private Integer errors = 0;
    private Integer duplicates = 0;
    private Integer configurationId;

    public MediationProcess() {
    }

    public MediationProcess(UUID id, Integer entityId, Integer configurationId, Boolean global, Date startDate, Date endDate, Integer recordsProcessed, Integer doneAndBillable, Integer errors, Integer duplicates) {
        this.id = id;
        this.entityId = entityId;
        this.configurationId = configurationId;
        this.global = global;
        this.startDate = startDate;
        this.endDate = endDate;
        this.recordsProcessed = recordsProcessed;
        this.doneAndBillable = doneAndBillable;
        this.errors = errors;
        this.duplicates = duplicates;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Boolean getGlobal() {
        return global;
    }

    public void setGlobal(Boolean global) {
        this.global = global;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getRecordsProcessed() {
        return recordsProcessed;
    }

    public void setRecordsProcessed(Integer recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }

    public Integer getDoneAndBillable() {
        return doneAndBillable;
    }

    public void setDoneAndBillable(Integer doneAndBillable) {
        this.doneAndBillable = doneAndBillable;
    }

    public Integer getErrors() {
        return errors;
    }

    public void setErrors(Integer errors) {
        this.errors = errors;
    }

    public Integer getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(Integer duplicates) {
        this.duplicates = duplicates;
    }

    public void setConfigurationId(Integer configurationId) {
        this.configurationId = configurationId;
    }

    public Integer getConfigurationId() {
        return configurationId;
    }

    @Override
    public String[] getFieldNames() {
        return new String[] {
            "id",
            "entityId",
            "configurationId",
            "global",
            "startDate",
            "endDate",
            "recordsProcessed",
            "doneAndBillable",
            "errors",
            "duplicates"
        };
    }

    @Override
    public Object[][] getFieldValues() {
        return new Object[][]{
            {
                id,
                entityId,
                configurationId,
                global,
                startDate,
                endDate,
                recordsProcessed,
                doneAndBillable,
                errors,
                duplicates
            }
        };
    }
}