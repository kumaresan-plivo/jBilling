package com.sapienter.jbilling.server.mediation.converter.common.steps;


import com.sapienter.jbilling.server.mediation.CallDataRecord;

/**
 * Generic validation step
 *
 * @author Panche Isajeski
 * @since 12/17/12
 */
public interface IMediationStepValidation {

    public boolean isValid(CallDataRecord record, MediationStepResult result);
}
