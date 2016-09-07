package com.sapienter.jbilling.server.mediation.converter;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.filter.Filter;
import com.sapienter.jbilling.server.mediation.*;
import com.sapienter.jbilling.server.mediation.converter.common.MediationJob;
import com.sapienter.jbilling.server.mediation.converter.db.*;
import org.apache.log4j.Logger;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Created by marcolin on 08/10/15.
 */
public class MediationServiceImplementation implements MediationService, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(MediationServiceImplementation.class));
    public static final String PARAMETER_MEDIATION_PROCESS_ID_KEY = "mediationProcessId";
    public static final String PARAMETER_MEDIATION_FILE_PATH_KEY = "filePath";
    public static final String PARAMETER_MEDIATION_CONFIG_ID_KEY = "mediationCfgId";

    @Autowired
    private JMRRepository jmrRepository;

    @Autowired
    private JMRRepositoryDAS jmrRepositoryDAS;

    @Autowired
    private JMErrorRepository jmErrorRepository;

    @Override
    public boolean isMediationProcessRunning() {
        JobExplorer jobExplorer = (JobExplorer) applicationContext.getBean("jobExplorer");
        Set<JobExecution> jobInExecution = jobExplorer.findRunningJobExecutions("mediationJobLauncher");
        return jobInExecution.size() != 0;
    }

    @Override
    public MediationProcessStatus mediationProcessStatus() {
        JobExplorer jobExplorer = (JobExplorer) applicationContext.getBean("jobExplorer");
        Set<JobExecution> jobInExecution = jobExplorer.findRunningJobExecutions("mediationJobLauncher");
        if (jobInExecution.size() == 0)
            return MediationProcessStatus.valueOf(BatchStatus.COMPLETED.name());
        JobExecution inProgress = jobInExecution.iterator().next();
        return MediationProcessStatus.valueOf(inProgress.getStatus().name());
    }

    @Override
    @Transactional(value = Transactional.TxType.NOT_SUPPORTED)
    public void launchMediation(Integer entityId, Integer mediationCfgId, String jobName) {
        launchMediation(entityId, mediationCfgId, jobName, null);
    }

    @Override
    public void launchMediation(Integer entityId, Integer mediationCfgId, String jobName, File file) {
        MediationContext mediationContext = new MediationContext();
        mediationContext.setEntityId(entityId);
        mediationContext.setMediationCfgId(mediationCfgId);
        mediationContext.setJobName(jobName);
        mediationContext.setFileWithCdrs(file);
        launchMediation(mediationContext);
    }

    @Override
    public List<JbillingMediationRecord> launchMediation(MediationContext mediationContext) {
        validateMediationContext(mediationContext);
        launchMediationSettingParameters(mediationContext.getEntityId(), mediationContext.getMediationCfgId(),
                mediationContext.getJobName(), parameters -> {
                    if (mediationContext.getFileWithCdrs() != null){
                        parameters.put(PARAMETER_MEDIATION_FILE_PATH_KEY, new JobParameter("" + mediationContext.getFileWithCdrs().getPath()));
                    }
                    if (mediationContext.getProcessIdForMediation() != null) {
                        parameters.put(PARAMETER_MEDIATION_PROCESS_ID_KEY, new JobParameter("" + mediationContext.getProcessIdForMediation()));
                    }
        });
        UUID mediationProcessId =  mediationContext.getProcessIdForMediation();
        if (mediationProcessId == null) {
            return new ArrayList<>();
        } else {
            MediationProcessService mediationProcessService = (MediationProcessService)applicationContext.getBean(MediationProcessService.BEAN_NAME);
            mediationProcessId = mediationProcessService.getLastMediationProcessId(mediationContext.getEntityId());
            return getMediationRecordsForProcess(mediationProcessId);
        }
    }

    private void validateMediationContext(MediationContext mediationContext) {
        if (mediationContext.getEntityId() == null ||
                mediationContext.getMediationCfgId() == null ||
                mediationContext.getJobName() == null)  {
            LOG.error("A mediation needs a entity id, a configuration id and a job name to start. " +
                    "entityId:" + mediationContext.getEntityId() + "," +
                    "mediationConfigId:" + mediationContext.getMediationCfgId() + "," +
                    "jobName:" + mediationContext.getJobName());
            throw new IllegalArgumentException("A mediation needs a entity id, a configuration id and a job name to start. " +
                    "entityId:" + mediationContext.getEntityId() + "," +
                    "mediationConfigId:" + mediationContext.getMediationCfgId() + "," +
                    "jobName:" + mediationContext.getJobName());
        }
    }

    @Override
    public void processCdr(Integer entityId, Integer mediationCfgId, String jobName, String records) {
        try {
            File recordsFile = File.createTempFile("records", ".tmp");
            PrintWriter printWriter = new PrintWriter(recordsFile);
            printWriter.println(records);
            printWriter.close();
            launchMediation(entityId, mediationCfgId, jobName, recordsFile);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }


    private void launchMediationSettingParameters(Integer entityId, Integer mediationCfgId, String jobName,
                                                  Consumer<Map<String, JobParameter>> parameterConsumer) {
        MediationJob mediationJob = MediationJobs.getJobForName(jobName);
        if (mediationJob != null) {
            JobLauncher jobLauncher = (JobLauncher) applicationContext.getBean("mediationJobLauncher");
            Map<String, JobParameter> parametersMap = new HashMap<>();
            parametersMap.put("datetime", new JobParameter(new Date()));
            parametersMap.put("entityId", new JobParameter("" + entityId));
            parametersMap.put(PARAMETER_MEDIATION_CONFIG_ID_KEY, new JobParameter("" + mediationCfgId));
            parametersMap.put("isGlobal", new JobParameter(1L));
            parameterConsumer.accept(parametersMap);

            JobParameters jobParameters = new JobParameters(parametersMap);

            try {
                jobLauncher.run(getJob(mediationJob.getJob()), jobParameters);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public List<JbillingMediationErrorRecord> findMediationErrorRecordsByFilters(Integer page, Integer size, List<Filter> filters) {
        return jmrRepositoryDAS.findMediationErrorRecordsByFilters(page, size, filters).stream()
                .map(DaoConverter::getMediationErrorRecord).collect(Collectors.toList());
    }

    @Override
    public List<JbillingMediationErrorRecord> findMediationDuplicateRecordsByFilters(Integer page, Integer size, List<Filter> filters) {
        return jmrRepositoryDAS.findMediationDuplicateRecordsByFilters(page, size, filters).stream()
                .map(DaoConverter::getMediationErrorRecord).collect(Collectors.toList());
    }

    @Override
    public List<JbillingMediationRecord> findMediationRecordsByFilters(Integer page, Integer size, List<Filter> filters) {
        return jmrRepositoryDAS.findMediationRecordsByFilters(page, size, filters).stream()
                .map(DaoConverter::getMediationRecord).collect(Collectors.toList());
    }

    @Override
    public List<JbillingMediationRecord> getMediationRecordsForMediationConfigId(Integer mediationCfgId) {
        return jmrRepository.getMediationRecordsForConfigId(mediationCfgId).stream().map(DaoConverter::getMediationRecord)
                .collect(Collectors.toList());
    }

    private Job getJob(String name) {
        return (Job) applicationContext.getBean(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public List<JbillingMediationRecord> getMediationRecordsForProcess(UUID processId) {
        return jmrRepository.getMediationRecordsForProcess(processId).stream().map(DaoConverter::getMediationRecord)
                .collect(Collectors.toList());
    }

    @Override
    public List<JbillingMediationErrorRecord> getMediationErrorRecordsForMediationConfigId(Integer mediationCfgId) {
        return jmErrorRepository.getMediationErrorRecordsForMediationConfigId(mediationCfgId).stream()
                .map(DaoConverter::getMediationErrorRecord).collect(Collectors.toList());
    }

    @Override
    public List<JbillingMediationErrorRecord> getMediationErrorRecordsForProcess(UUID processId) {
        return jmErrorRepository.getMediationErrorRecordsForProcess(processId).stream()
                .map(DaoConverter::getMediationErrorRecord).collect(Collectors.toList());
    }

    @Override
    public List<JbillingMediationErrorRecord> getMediationDuplicatesRecordsForProcess(UUID processId) {
        return jmErrorRepository.getMediationDuplicateRecordsForProcess(processId).stream()
                .map(DaoConverter::getMediationErrorRecord).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getOrdersForMediationProcess(UUID processId) {
        return jmrRepository.getOrdersForMediationProcess(processId);
    }

    @Override
    public List<JbillingMediationRecord> getMediationRecordsForOrder(Integer orderId) {
        return jmrRepository.getMediationRecordsForOrderId(orderId).stream().map(DaoConverter::getMediationRecord)
                .collect(Collectors.toList());
    }

    @Override
    public List<JbillingMediationRecord> getMediationRecordsForProcessAndOrder(UUID processId, Integer orderId) {
        return jmrRepository.getMediationRecordsForProcessIdOrderId(processId, orderId).stream()
                .map(DaoConverter::getMediationRecord).collect(Collectors.toList());
    }

    @Override
    public List<JbillingMediationRecord> getMediationRecordsForOrderLine(Integer orderLineId) {
        return jmrRepository.getMediationRecordsForOrderLineId(orderLineId);
    }

    @Override
    public void deleteErrorMediationRecords(UUID processId) {
        jmErrorRepository.delete(jmErrorRepository.getMediationErrorRecordsForProcess(processId));
    }

    @Override
    public void deleteDuplicateMediationRecords(UUID processId) {
        jmErrorRepository.delete(jmErrorRepository.getMediationDuplicateRecordsForProcess(processId));
    }

    @Override
    public void deleteMediationRecords(List<JbillingMediationRecord> recordList) {
        jmrRepository.delete(recordList.stream().map(DaoConverter::getMediationRecordDao).collect(Collectors.toList()));
    }

    @Override
    public void recycleCdr(Integer entityId, Integer mediationCfgId, String jobName) {
        String recycleJobName = MediationJobs.getRecycleJobForMediationJob(jobName).getJob();
        launchMediationSettingParameters(entityId, mediationCfgId, recycleJobName, parameters -> {
            parameters.put("mediationCfgId", new JobParameter("" + mediationCfgId));
        });
    }

    @Override
    public void recycleCdr(Integer entityId, Integer mediationCfgId, String jobName, UUID processId) {
        String recycleJobName = MediationJobs.getRecycleJobForMediationJob(jobName).getJob();
        launchMediationSettingParameters(entityId, mediationCfgId, recycleJobName, parameters -> {
            parameters.put("mediationCfgId", new JobParameter("" + mediationCfgId));
            parameters.put("processId", new JobParameter("" + processId));
        });
    }

    @Override
    public void saveDiameterEventAsJMR(JbillingMediationRecord diameterEvent) {
        jmrRepository.save(DaoConverter.getMediationRecordDao(diameterEvent));
    }

}
