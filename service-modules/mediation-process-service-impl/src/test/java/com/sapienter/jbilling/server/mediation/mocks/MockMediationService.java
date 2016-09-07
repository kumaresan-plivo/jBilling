package com.sapienter.jbilling.server.mediation.mocks;


import com.sapienter.jbilling.server.filter.Filter;
import com.sapienter.jbilling.server.mediation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by marcolin on 29/10/15.
 */
public class MockMediationService implements MediationService {

    private static List<JbillingMediationRecord> mediationRecordsForTest = new ArrayList<>();

    public void setMediationRecordsForTest(List<JbillingMediationRecord> mediationRecordsForTest) {
        this.mediationRecordsForTest = mediationRecordsForTest;
    }

    @Override
    public boolean isMediationProcessRunning() {
        return false;
    }

    @Override
    public MediationProcessStatus mediationProcessStatus() {
        return null;
    }

    @Override
    public void launchMediation(Integer entityId, Integer mediationCfgId, String jobName) {

    }

    @Override
    public void launchMediation(Integer entityId, Integer mediationCfgId, String jobName, File file) {

    }

    @Override
    public List<JbillingMediationRecord> launchMediation(MediationContext mediationContext) {
        return null;
    }

    @Override
    public void deleteErrorMediationRecords(UUID processId) {

    }

    @Override
    public void deleteDuplicateMediationRecords(UUID processId) {

    }

    @Override
    public void deleteMediationRecords(List<JbillingMediationRecord> recordList) {

    }

    @Override
    public void processCdr(Integer entityId, Integer mediationCfgId, String jobName, String record) {
    }

    @Override
    public List<JbillingMediationErrorRecord> findMediationErrorRecordsByFilters(Integer page, Integer size, List<Filter> filters) {
        return null;
    }

    @Override
    public List<JbillingMediationErrorRecord> findMediationDuplicateRecordsByFilters(Integer page, Integer size, List<Filter> filters) {
        return null;
    }

    @Override
    public List<JbillingMediationErrorRecord> getMediationErrorRecordsForMediationConfigId(Integer mediationCfgId) {
        return null;
    }

    @Override
    public List<JbillingMediationRecord> getMediationRecordsForMediationConfigId(Integer mediationCfgId) {
        return null;
    }

    @Override
    public List<JbillingMediationRecord> getMediationRecordsForProcess(UUID processId) {

        return mediationRecordsForTest;
    }

    @Override
    public List<JbillingMediationRecord> getMediationRecordsForProcessAndOrder(UUID processId, Integer orderId) {
        return null;
    }

    @Override
    public List<JbillingMediationErrorRecord> getMediationErrorRecordsForProcess(UUID processId) {
        return null;
    }

    @Override
    public List<JbillingMediationErrorRecord> getMediationDuplicatesRecordsForProcess(UUID processId) {
        return null;
    }

    @Override
    public List<Integer> getOrdersForMediationProcess(UUID processId) {
        return null;
    }

    @Override
    public List<JbillingMediationRecord> findMediationRecordsByFilters(Integer page, Integer size, List<Filter> filters) {
        return null;
    }

    @Override
    public List<JbillingMediationRecord> getMediationRecordsForOrder(Integer orderId) {
        return null;
    }

    @Override
    public List<JbillingMediationRecord> getMediationRecordsForOrderLine(Integer orderLineId) {
        return null;
    }

    @Override
    public void recycleCdr(Integer entityId, Integer mediationCfgId, String jobName) {

    }

    @Override
    public void recycleCdr(Integer entityId, Integer mediationCfgId, String jobName, UUID processId) {

    }

    @Override
    public void saveDiameterEventAsJMR(JbillingMediationRecord diameterEvent) {

    }
}
