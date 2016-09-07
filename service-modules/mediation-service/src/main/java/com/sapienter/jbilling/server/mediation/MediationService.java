package com.sapienter.jbilling.server.mediation;

import com.sapienter.jbilling.server.filter.Filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by marcolin on 08/10/15.
 */
public interface MediationService {
    static final String BEAN_NAME = "mediationService";

    boolean isMediationProcessRunning();
    public MediationProcessStatus  mediationProcessStatus();

    void launchMediation(Integer entityId, Integer mediationCfgId, String jobName);
    void launchMediation(Integer entityId, Integer mediationCfgId, String jobName, File file);
    List<JbillingMediationRecord> launchMediation(MediationContext mediationContext);
    void deleteErrorMediationRecords(UUID processId);
    void deleteDuplicateMediationRecords(UUID processId);
    void deleteMediationRecords(List<JbillingMediationRecord> recordList);
    void processCdr(Integer entityId, Integer mediationCfgId, String jobName, String record);

    List<JbillingMediationErrorRecord> findMediationErrorRecordsByFilters(Integer page, Integer size, List<Filter> filters);
    List<JbillingMediationErrorRecord> findMediationDuplicateRecordsByFilters(Integer page, Integer size, List<Filter> filters);
    List<JbillingMediationErrorRecord> getMediationErrorRecordsForMediationConfigId(Integer mediationCfgId);
    List<JbillingMediationErrorRecord> getMediationErrorRecordsForProcess(UUID processId);
    List<JbillingMediationErrorRecord> getMediationDuplicatesRecordsForProcess(UUID processId);

    List<Integer> getOrdersForMediationProcess(UUID processId);
    List<JbillingMediationRecord> findMediationRecordsByFilters(Integer page, Integer size, List<Filter> filters);
    List<JbillingMediationRecord> getMediationRecordsForMediationConfigId(Integer mediationCfgId);
    List<JbillingMediationRecord> getMediationRecordsForOrder(Integer orderId);
    List<JbillingMediationRecord> getMediationRecordsForProcess(UUID processId);
    List<JbillingMediationRecord> getMediationRecordsForProcessAndOrder(UUID processId, Integer orderId);
    List<JbillingMediationRecord> getMediationRecordsForOrderLine(Integer orderLineId);

    void recycleCdr(Integer entityId, Integer mediationCfgId, String jobName);
    void recycleCdr(Integer entityId, Integer mediationCfgId, String jobName, UUID processId);


    //TODO: USED FOR DIAMETER, THIS CAN GO IN A DIAMETER TABLE INSTEAD OF BE SAVED HERE
    void saveDiameterEventAsJMR(JbillingMediationRecord diameterEvent);
}
