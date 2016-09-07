package com.sapienter.jbilling.server.mediation.listener;

import com.sapienter.jbilling.server.mediation.MediationProcessService;
import com.sapienter.jbilling.server.mediation.converter.MediationServiceImplementation;
import com.sapienter.jbilling.server.mediation.converter.db.JMErrorRepository;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by andres on 20/10/15.
 */
public class RecycleMediationJobListener implements JobExecutionListener {

    @Autowired
    private JMErrorRepository jmErrorRepository;

    @Autowired
    private MediationProcessService mediationProcessService;

    private Set<UUID> processIdsToUpdate;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String mediationCfgId = jobExecution.getJobParameters().getString(MediationServiceImplementation.PARAMETER_MEDIATION_CONFIG_ID_KEY);
        String processId = jobExecution.getJobParameters().getString(MediationServiceImplementation.PARAMETER_MEDIATION_PROCESS_ID_KEY);
        if (processId != null) {
            jmErrorRepository.setErrorRecordsToBeRecycledForMediationAndProcessId(Integer.parseInt(mediationCfgId),
                    UUID.fromString(processId));
        } else {
            jmErrorRepository.setErrorRecordsToBeRecycledForMediationCfgId(Integer.parseInt(mediationCfgId));
        }
        processIdsToUpdate = jmErrorRepository.getMediationErrorRecordsToBeRecycle()
                .stream().map(er -> er.getProcessId()).collect(Collectors.toSet());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        processIdsToUpdate.forEach(mediationProcessId -> mediationProcessService.updateMediationProcessCounters(mediationProcessId));
        jmErrorRepository.deleteRecycledRecords();
    }

}
