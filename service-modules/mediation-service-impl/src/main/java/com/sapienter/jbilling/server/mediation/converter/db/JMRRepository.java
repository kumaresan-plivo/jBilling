package com.sapienter.jbilling.server.mediation.converter.db;


import com.sapienter.jbilling.server.mediation.JbillingMediationRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Created by marcolin on 08/10/15.
 */
public interface JMRRepository extends
        PagingAndSortingRepository<JbillingMediationRecordDao, JbillingMediationRecordId> {

    public static String orderByProcessingDateString = " ORDER BY mp.processing_date DESC";

    @Query(value ="SELECT * FROM  jbilling_mediation_record mp WHERE mp.process_id = :processId" + orderByProcessingDateString, nativeQuery = true)
    List<JbillingMediationRecordDao> getMediationRecordsForProcess(@Param("processId") UUID processId);

    @Query(value ="SELECT distinct mp.order_id FROM  jbilling_mediation_record mp WHERE mp.process_id = :processId AND mp.status = 'PROCESSED'", nativeQuery = true)
    List<Integer> getOrdersForMediationProcess(@Param("processId") UUID processId);

    @Query(value ="SELECT * FROM  jbilling_mediation_record mp WHERE mp.order_id = :orderId"
            + orderByProcessingDateString, nativeQuery = true)
    List<JbillingMediationRecordDao> getMediationRecordsForOrderId(@Param("orderId") Integer orderId);

    @Query(value ="SELECT mp.order_id FROM jbilling_mediation_record mp WHERE mp.order_line_id = :orderLineId", nativeQuery = true)
    List<JbillingMediationRecord> getMediationRecordsForOrderLineId(@Param("orderLineId") Integer orderLineId);

    @Query(value ="SELECT * FROM  jbilling_mediation_record mp WHERE mp.process_id = :processId AND mp.order_id = :orderId"
            + orderByProcessingDateString, nativeQuery = true)
    List<JbillingMediationRecordDao> getMediationRecordsForProcessIdOrderId(
            @Param("processId") UUID processId, @Param("orderId") Integer orderId);

    @Query(value ="SELECT * FROM  jbilling_mediation_record mp WHERE mp.mediation_cfg_id = :configId"
            + orderByProcessingDateString, nativeQuery = true)
    List<JbillingMediationRecordDao> getMediationRecordsForConfigId(@Param("configId") Integer processId);

    @Query(value ="select m from JbillingMediationRecordDao m where m.recordKey = :recordKey")
    JbillingMediationRecordDao findByRecordKey(@Param("recordKey") String recordKey);

    List<JbillingMediationRecordDao> findByUserIdAndStatus(Integer userId, JbillingMediationRecordDao.STATUS status);
    List<JbillingMediationRecordDao> findByStatus(JbillingMediationRecordDao.STATUS status);
}
