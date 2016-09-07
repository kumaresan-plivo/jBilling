package com.sapienter.jbilling.server.mediation.process;

import com.sapienter.jbilling.server.filter.Filter;
import com.sapienter.jbilling.server.filter.FilterConstraint;
import com.sapienter.jbilling.server.mediation.JbillingMediationRecord;
import com.sapienter.jbilling.server.mediation.MediationProcess;
import com.sapienter.jbilling.server.mediation.MediationProcessService;
import com.sapienter.jbilling.server.mediation.MediationService;
import com.sapienter.jbilling.server.mediation.process.db.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by andres on 19/10/15.
 */
public class MediationProcessServiceImpl implements MediationProcessService {

    @Autowired
    private MediationProcessRepository mediationProcessRepository;

    @Autowired
    private MediationProcessDAS mediationProcessDAS;

    @Autowired
    private MediationService mediationService;

    @Override
    public MediationProcess getMediationProcess(UUID id) {
        if (id == null) return null;
        MediationProcessDAO mediationProcessDAO = mediationProcessRepository.findOne(id);
        return null != mediationProcessDAO ? DaoConverter.getMediationProcess(mediationProcessDAO)
                : null;
    }

    @Override
    public MediationProcess saveMediationProcess(Integer entityId, Integer configurationId) {
        MediationProcess mediationProcess = new MediationProcess();
        mediationProcess.setId(UUID.randomUUID());
        mediationProcess.setEntityId(entityId);
        mediationProcess.setConfigurationId(configurationId);
        return DaoConverter.getMediationProcess(mediationProcessRepository.save(DaoConverter.getMediationProcessDAO(mediationProcess)));
    }

    @Override
    public MediationProcess updateMediationProcess(MediationProcess mediationProcess) {
        return DaoConverter.getMediationProcess(mediationProcessRepository.save(DaoConverter.getMediationProcessDAO(mediationProcess)));
    }

    @Override
    public MediationProcess updateMediationProcessCounters(UUID mediationProcessId) {
        MediationProcess mediationProcess = getMediationProcess(mediationProcessId);
        mediationProcess.setDoneAndBillable(mediationService.getMediationRecordsForProcess(mediationProcessId).size());
        mediationProcess.setErrors(mediationService.getMediationErrorRecordsForProcess(mediationProcessId).size());
        mediationProcess.setRecordsProcessed(mediationProcess.getDoneAndBillable() + mediationProcess.getErrors());
        return updateMediationProcess(mediationProcess);
    }

    @Override
    public void deleteMediationProcess(UUID mediationProcessId) {
        mediationProcessRepository.delete(mediationProcessId);
    }

    @Override
    public List<MediationProcess> findLatestMediationProcess(Integer entityId, int page, int size) {
        return findMediationProcessByFilters(entityId, page, size, "startDate", "desc", Arrays.asList());
    }

    @Override
    public List<MediationProcess> findMediationProcessByFilters(Integer entityId, int page, int size, String sort, String order, List<Filter> filters) {
        filters = new ArrayList<>(filters);
        filters.add(new Filter("entityId", FilterConstraint.EQ, "" + entityId));
        List<Filter> filtersWithoutOrderIdFilter = filters.stream().filter(f -> !f.getFieldString().equals("orderId")).collect(Collectors.toList());
        List<MediationProcess> mediationProcessFilteredWithMediationProcessFilters =
                mediationProcessDAS.findMediationProcessByFilters(page, size, sort, order, filtersWithoutOrderIdFilter).stream()
                        .map(DaoConverter::getMediationProcess).collect(Collectors.toList());
        return filterByJMROrderId(filters.stream().filter(f -> f.getFieldString().equals("orderId")).findFirst(),
                mediationProcessFilteredWithMediationProcessFilters);
    }

    private List<MediationProcess> filterByJMROrderId(Optional<Filter> orderIdFilterOption, final List<MediationProcess> mediationProcessToFilter) {
        if (orderIdFilterOption.isPresent()) {
            return mediationProcessToFilter.stream().filter(mediationProcess -> {
                List<JbillingMediationRecord> mediationRecordsForProcess = mediationService.getMediationRecordsForProcess(mediationProcess.getId());
                return mediationRecordsForProcess.stream().filter(jmr -> jmr.getOrderId().equals(orderIdFilterOption.get().getValue())).findFirst().isPresent();
            }).collect(Collectors.toList());
        }
        return mediationProcessToFilter;
    }

    @Override
    public Integer getCfgIdForMediattionProcessId(UUID mediationProcessId) {
        return mediationProcessRepository.getCfgIdForMediationProcess(mediationProcessId);
    }

    @Override
    public UUID getLastMediationProcessId(Integer entityid) {
        List<MediationProcess> mediationProcessByFilters = findMediationProcessByFilters(entityid, 0, 1, "startDate", "desc", Arrays.asList());
        if (mediationProcessByFilters.size() == 1) return mediationProcessByFilters.get(0).getId();
        return null;
    }

}
