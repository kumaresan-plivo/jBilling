package com.sapienter.jbilling.server.mediation.converter.common.validation;

import com.sapienter.jbilling.server.mediation.CallDataRecord;
import com.sapienter.jbilling.server.mediation.converter.common.steps.*;
import com.sapienter.jbilling.server.mediation.converter.db.JMRRepository;
import com.sapienter.jbilling.server.mediation.converter.db.JbillingMediationRecordDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by coredevelopment on 25/02/16.
 */
public class DuplicateRecordValidationStep implements com.sapienter.jbilling.server.mediation.converter.common.steps.IMediationStepValidation {
    public static String BEAN_NAME = "DuplicateRecordValidationStep";

    @Autowired
    private JMRRepository jmrRepository;

    @Override
    public boolean isValid(CallDataRecord record, MediationStepResult result) {
        JbillingMediationRecordDao byRecordKey = jmrRepository.findByRecordKey(record.getKey());
        if (byRecordKey == null) {
            return true;
        }
        result.addError("JB-DUPLICATE");
        return false;
    }
}
