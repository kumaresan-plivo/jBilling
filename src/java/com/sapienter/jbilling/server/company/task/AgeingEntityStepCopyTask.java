package com.sapienter.jbilling.server.company.task;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.process.db.AgeingEntityStepDAS;
import com.sapienter.jbilling.server.process.db.AgeingEntityStepDTO;
import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.user.db.CompanyDTO;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by vivek on 31/10/14.
 */
public class AgeingEntityStepCopyTask extends AbstractCopyTask {

    AgeingEntityStepDAS ageingEntityStepDAS = null;
    CompanyDAS companyDAS = null;

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(AgeingEntityStepCopyTask.class));

    private static final Class dependencies[] = new Class[]{};

    public Boolean isTaskCopied(Integer entityId, Integer targetEntityId) {
        List<AgeingEntityStepDTO> ageingEntityStepDTOList = ageingEntityStepDAS.findAgeingStepsForEntity(targetEntityId);
        return ageingEntityStepDTOList != null && !ageingEntityStepDTOList.isEmpty();
    }

    public Class[] getDependencies() {
        return dependencies;
    }


    public AgeingEntityStepCopyTask() {
        init();
    }

    private void init() {
        ageingEntityStepDAS = new AgeingEntityStepDAS();
        companyDAS = new CompanyDAS();
    }

    public void create(Integer entityId, Integer targetEntityId) {
        initialise(entityId, targetEntityId);  // This will create all the entities on which the current entity is dependent.
        LOG.debug("Create AgeingEntityStepCopyTask ");
        CompanyDTO companyDTO = companyDAS.find(targetEntityId);
        List<AgeingEntityStepDTO> ageingEntityStepDTOList = ageingEntityStepDAS.findAgeingStepsForEntity(entityId);
        for (AgeingEntityStepDTO ageingEntityStepDTO : ageingEntityStepDTOList) {
            new AgeingEntityStepDAS().create(targetEntityId,
                    ageingEntityStepDTO.getDescription() != null ? ageingEntityStepDTO.getDescription() : "Default Description",
                    companyDTO.getLanguageId(), ageingEntityStepDTO.getDays(), ageingEntityStepDTO.getSendNotification(),
                    ageingEntityStepDTO.getRetryPayment(), ageingEntityStepDTO.getSuspend());
        }
        LOG.debug("AgeingEntityStepCopyTask has been completed");
    }
}