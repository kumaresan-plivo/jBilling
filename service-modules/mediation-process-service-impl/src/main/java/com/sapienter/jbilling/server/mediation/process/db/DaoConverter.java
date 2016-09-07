package com.sapienter.jbilling.server.mediation.process.db;

import com.sapienter.jbilling.server.mediation.MediationProcess;

/**
 * Created by andres on 19/10/15.
 */
public class DaoConverter {

    public static MediationProcess getMediationProcess(MediationProcessDAO dao) {
        return new MediationProcess(dao.getId(), dao.getEntityId(), dao.getConfigurationId(),
                dao.getGlobal(), dao.getStartDate(), dao.getEndDate(),dao.getRecordsProcessed(),
                dao.getDoneAndBillable(), dao.getErrors(), dao.getDuplicates());
    }

    public static MediationProcessDAO getMediationProcessDAO (MediationProcess mediationProcess) {
        return new MediationProcessDAO(mediationProcess.getId(), mediationProcess.getEntityId(),
                mediationProcess.getConfigurationId(), mediationProcess.getGlobal(),
                mediationProcess.getStartDate(), mediationProcess.getEndDate(), mediationProcess.getRecordsProcessed(),
                mediationProcess.getDoneAndBillable(), mediationProcess.getErrors(), mediationProcess.getDuplicates());
    }
}
