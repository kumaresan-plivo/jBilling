package com.sapienter.jbilling.server.mediation.converter.db;

import java.io.Serializable;

/**
 * Created by marcolin on 08/10/15.
 */
public class JbillingMediationErrorRecordId implements Serializable {
    Integer jBillingCompanyId = null;
    Integer mediationCfgId = null;
    String recordKey = null;

    public JbillingMediationErrorRecordId() {}

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
}
