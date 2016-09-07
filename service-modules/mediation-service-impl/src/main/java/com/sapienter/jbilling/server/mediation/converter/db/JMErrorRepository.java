package com.sapienter.jbilling.server.mediation.converter.db;

import com.sapienter.jbilling.server.mediation.JbillingMediationErrorRecord;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by marcolin on 08/10/15.
 */
public interface JMErrorRepository extends
        PagingAndSortingRepository<JbillingMediationErrorRecordDao, JbillingMediationErrorRecordId> {

    @Query(value ="SELECT * FROM  jbilling_mediation_error_record mer WHERE mer.process_id = :processId and mer.status is null " +
            "and error_codes not like '%JB-DUPLICATE%'" , nativeQuery = true)
    List<JbillingMediationErrorRecordDao> getMediationErrorRecordsForProcess(@Param("processId") UUID processId);

    @Query(value ="SELECT * FROM  jbilling_mediation_error_record mer WHERE mer.process_id = :processId and mer.status is null " +
            "and error_codes like '%JB-DUPLICATE%'" , nativeQuery = true)
    List<JbillingMediationErrorRecordDao> getMediationDuplicateRecordsForProcess(@Param("processId") UUID processId);

    @Query(value ="SELECT * FROM  jbilling_mediation_error_record mer WHERE mer.mediation_cfg_id = :configId" , nativeQuery = true)
    List<JbillingMediationErrorRecordDao> getMediationErrorRecordsForMediationConfigId(@Param("configId") Integer configId);

    @Query(value ="SELECT * FROM  jbilling_mediation_error_record mer WHERE mer.status = 'TO_BE_RECYCLED'" , nativeQuery = true)
    List<JbillingMediationErrorRecordDao> getMediationErrorRecordsToBeRecycle();

    @Modifying
    @Query(value ="UPDATE jbilling_mediation_error_record SET status = 'TO_BE_RECYCLED' WHERE mediation_cfg_id = :mediationCfgId " +
            "and error_codes not like '%JB-DUPLICATE%'" , nativeQuery = true)
    @Transactional(propagation = Propagation.REQUIRED)
    void setErrorRecordsToBeRecycledForMediationCfgId(@Param("mediationCfgId") Integer mediationCfgId);

    @Modifying
    @Query(value ="UPDATE jbilling_mediation_error_record SET status = 'TO_BE_RECYCLED' WHERE mediation_cfg_id = :mediationCfgId AND process_id = :processId " +
            "and error_codes not like '%JB-DUPLICATE%'" , nativeQuery = true)
    @Transactional(propagation = Propagation.REQUIRED)
    void setErrorRecordsToBeRecycledForMediationAndProcessId(@Param("mediationCfgId") Integer mediationCfgId, @Param("processId") UUID processId);

    @Modifying
    @Query(value ="DELETE FROM jbilling_mediation_error_record WHERE status = 'TO_BE_RECYCLED'", nativeQuery = true)
    @Transactional(propagation = Propagation.REQUIRED)
    void deleteRecycledRecords();


}