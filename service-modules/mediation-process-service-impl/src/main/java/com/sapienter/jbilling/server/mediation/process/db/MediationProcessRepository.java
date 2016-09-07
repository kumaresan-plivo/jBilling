package com.sapienter.jbilling.server.mediation.process.db;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Created by andres on 19/10/15.
 */
public interface MediationProcessRepository extends
        PagingAndSortingRepository<MediationProcessDAO, UUID>{

    @Query(value ="SELECT configuration_id FROM jbilling_mediation_process WHERE id = :mediationProcessId" , nativeQuery = true)
    int getCfgIdForMediationProcess(@Param("mediationProcessId") UUID mediationProcessId);

}
