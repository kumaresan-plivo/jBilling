/*
 JBILLING CONFIDENTIAL
 _____________________

 [2003] - [2012] Enterprise jBilling Software Ltd.
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Enterprise jBilling Software.
 The intellectual and technical concepts contained
 herein are proprietary to Enterprise jBilling Software
 and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden.
 */

package com.sapienter.jbilling.server.mediation.converter.common.steps;

import com.sapienter.jbilling.server.mediation.CallDataRecord;
import com.sapienter.jbilling.server.mediation.converter.common.FormatLogger;
import com.sapienter.jbilling.server.mediation.converter.common.processor.MediationStepContext;
import com.sapienter.jbilling.server.mediation.converter.common.processor.MediationStepType;
import com.sapienter.jbilling.server.mediation.converter.common.validation.DuplicateRecordValidationStep;
import com.sapienter.jbilling.server.mediation.converter.common.validation.MediationResultValidationStep;
import com.sapienter.jbilling.server.util.Context;
import org.apache.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Basic mediation cdr resolver. Contains multiple steps for resolving the CDR-s into JMR
 * <p/>
 * It splits the work of resolution into multiple mediation steps that will be executed
 *
 * @author Panche Isajeski
 * @since 12/16/12
 */
public class JMRMediationCdrResolver implements IMediationCdrResolver {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(JMRMediationCdrResolver.class));

    private Map<MediationStepType, IMediationStep> steps = new LinkedHashMap<MediationStepType, IMediationStep>();

    private Map<MediationStepType, IMediationStepValidation> validationSteps = new LinkedHashMap<MediationStepType, IMediationStepValidation>();

    public JMRMediationCdrResolver() {
        initSteps();
    }

    private void initSteps() {

        // pre-defined validation steps
        validationSteps.put(MediationStepType.DUPLICATE_RECORD_VALIDATION, Context.getBean(DuplicateRecordValidationStep.BEAN_NAME));
        validationSteps.put(MediationStepType.MEDIATION_RESULT_VALIDATION, new MediationResultValidationStep());

        // default step values
        steps.put(MediationStepType.USER_CURRENCY, getUserResolutionStep());
        steps.put(MediationStepType.EVENT_DATE, getEventDateResolutionStep());
        steps.put(MediationStepType.ORDER_LINE_ITEM, getItemResolutionStep());
        steps.put(MediationStepType.PRICING, getPricingResolutionStep());
    }

    public MediationResolverStatus resolveCdr(MediationStepResult result,
                                              CallDataRecord record) {

        if (hasValidationProblem(MediationStepType.DUPLICATE_RECORD_VALIDATION,
                String.format("Duplicate mediation record %s", record), record, result)) {
            return  MediationResolverStatus.DUPLICATE;
        }

        if (hasValidationProblem(MediationStepType.MEDIATION_RECORD_FORMAT_VALIDATION,
                String.format("Mediation record %s does not have a valid format.", record),
                record, result)) {
            return MediationResolverStatus.ERROR;
        }

        MediationStepContext context = new MediationStepContext(result, record, record.getEntityId());
        // generic CDR process steps
        for (Map.Entry<MediationStepType, IMediationStep> entry : steps.entrySet()) {
            entry.getValue().executeStep(context);
        }

        if (hasValidationProblem(MediationStepType.MEDIATION_RESULT_VALIDATION,
                String.format("Invalid mediation result %s returned on resolving the CDR record %s.", result, record),
                record, result)) {
            return MediationResolverStatus.ERROR;
        }

        return MediationResolverStatus.SUCCESS;
    }

    public boolean hasValidationProblem(MediationStepType stepType, String message,
                                        CallDataRecord record, MediationStepResult result) {
        IMediationStepValidation validator = validationSteps.get(stepType);
        if (null != validator && !validator.isValid(record, result)) {
            LOG.debug(message);
            return true;
        }
        return false;
    }


    public void setSteps(Map<MediationStepType, IMediationStep> steps) {
        this.steps = steps;
    }

    public void clearSteps() {
        steps.clear();
    }

    public void addStep(MediationStepType type, IMediationStep step) {
        steps.put(type, step);
    }

    public Map<MediationStepType, IMediationStepValidation> getValidationSteps() {
        return validationSteps;
    }

    public void setValidationSteps(Map<MediationStepType, IMediationStepValidation> validationSteps) {
        this.validationSteps = validationSteps;
    }

    public void clearValidationSteps() {
        validationSteps.clear();
    }

    protected AbstractUserResolutionStep getUserResolutionStep() {
        return new JMRUserLoginResolutionStep();
    }

    protected AbstractMediationStep getEventDateResolutionStep() {
        return new JMREventDateResolutionStep();
    }

    protected AbstractItemResolutionStep getItemResolutionStep() {
        return new JMRItemResolutionStep();
    }

    protected AbstractMediationStep getPricingResolutionStep() {
        return new JMRPricingResolutionStep();
    }
}
