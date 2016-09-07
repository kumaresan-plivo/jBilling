package com.sapienter.jbilling.server.mediation.listener;

import com.sapienter.jbilling.server.mediation.MediationProcess;
import com.sapienter.jbilling.server.mediation.MediationProcessService;
import com.sapienter.jbilling.server.mediation.MediationService;
import com.sapienter.jbilling.server.mediation.converter.MediationServiceImplementation;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Created by andres on 20/10/15.
 */
public class MediationJobListener implements JobExecutionListener {

    @Autowired
    private MediationProcessService mediationProcessService;

    @Autowired
    private MediationService mediationService;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String mediationProcessString = jobExecution.getJobParameters().getString(MediationServiceImplementation.PARAMETER_MEDIATION_PROCESS_ID_KEY);
        UUID mediationProcessId = null;
        if (mediationProcessString != null) mediationProcessId = UUID.fromString(mediationProcessString);
        if (mediationProcessId == null) {
            int entityId = Integer.parseInt(jobExecution.getJobParameters().getString("entityId"));
            int configurationId = Integer.parseInt(jobExecution.getJobParameters().getString(MediationServiceImplementation.PARAMETER_MEDIATION_CONFIG_ID_KEY));
            mediationProcessId = mediationProcessService.saveMediationProcess(entityId, configurationId).getId();
        }
        jobExecution.getExecutionContext().put(MediationServiceImplementation.PARAMETER_MEDIATION_PROCESS_ID_KEY, mediationProcessId);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        UUID mediationProcessId = (UUID) jobExecution.getExecutionContext().get(MediationServiceImplementation.PARAMETER_MEDIATION_PROCESS_ID_KEY);
        MediationProcess mediationProcess = mediationProcessService.getMediationProcess(mediationProcessId);
        mediationProcess.setStartDate(jobExecution.getStartTime());
        mediationProcess.setEndDate(jobExecution.getEndTime());
        mediationProcess.setDoneAndBillable(mediationService.getMediationRecordsForProcess(mediationProcessId).size());
        mediationProcess.setErrors(mediationService.getMediationErrorRecordsForProcess(mediationProcessId).size());
        mediationProcess.setDuplicates(mediationService.getMediationDuplicatesRecordsForProcess(mediationProcessId).size());
        mediationProcess.setRecordsProcessed(mediationProcess.getDoneAndBillable() + mediationProcess.getErrors());
        mediationProcessService.updateMediationProcess(mediationProcess);
    }
}
