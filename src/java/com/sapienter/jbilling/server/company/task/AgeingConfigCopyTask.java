package com.sapienter.jbilling.server.company.task;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.process.AgeingBL;
import com.sapienter.jbilling.server.process.AgeingDTOEx;
import com.sapienter.jbilling.server.process.AgeingWS;
import com.sapienter.jbilling.server.process.db.AgeingEntityStepDAS;
import com.sapienter.jbilling.server.process.db.AgeingEntityStepDTO;
import org.apache.log4j.Logger;

/**
 * Created by vivek on 21/11/14.
 */
public class AgeingConfigCopyTask extends AbstractCopyTask {

    AgeingEntityStepDAS ageingEntityStepDAS = null;

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(AgeingConfigCopyTask.class));

    private static final Class dependencies[] = new Class[]{};

    public Class[] getDependencies() {
        return dependencies;
    }

    public Boolean isTaskCopied(Integer entityId, Integer targetEntityId) {
        LOG.debug("It has not been implemented yet. Remove this message after implementation");
        return false;
    }


    public AgeingConfigCopyTask() {
        init();
    }

    private void init() {
        ageingEntityStepDAS = new AgeingEntityStepDAS();
    }

    public void create(Integer entityId, Integer targetEntityId) {
        initialise(entityId, targetEntityId);  // This will create all the entities on which the current entity is dependent.
        LOG.debug("Create AgeingConfigCopyTask");
        copyAgeingConfig(entityId, targetEntityId);
    }

    private void copyAgeingConfig(int entityId, int targetEntityId) {


        AgeingBL ageing = new AgeingBL();
        AgeingDTOEx[] dtoArr = ageing.getSteps(entityId, null, null);
        AgeingWS[] wsArr = new AgeingWS[dtoArr.length];
        AgeingDTOEx[] dtoList = new AgeingDTOEx[dtoArr.length];
        AgeingBL bl = new AgeingBL();

        for (AgeingEntityStepDTO ageingEntityStepDTO : ageingEntityStepDAS.findAgeingStepsForEntity(entityId)) {
        }

        for (int i = 0; i < wsArr.length; i++) {
            wsArr[i] = bl.getWS(dtoArr[i]);
            wsArr[i].setEntityId(targetEntityId);
            wsArr[i].setStatusId(0);

            dtoList[i] = bl.getDTOEx(wsArr[i]);
            if (dtoList[i] != null) {
                ageingEntityStepDAS.create(targetEntityId, dtoList[i].getStatusStr(), 1, dtoList[i].getDays(), dtoList[i].getSendNotification(), dtoList[i].getRetryPayment(), dtoList[i].getSendNotification());
            }
        }
        LOG.debug("Ageing process has been completed.");
    }
}
