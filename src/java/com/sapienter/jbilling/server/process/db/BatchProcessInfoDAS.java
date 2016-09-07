/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */
package com.sapienter.jbilling.server.process.db;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class BatchProcessInfoDAS extends AbstractDAS<BatchProcessInfoDTO> {

    //private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(ProcessRunDAS.class));

	public BatchProcessInfoDTO create(BillingProcessDTO billingProcessDTO, Integer jobExecutionId,
            Integer totalFailedUsers, Integer totalSuccessfulUsers) {
		BatchProcessInfoDTO dto = new BatchProcessInfoDTO(billingProcessDTO,jobExecutionId,
                totalFailedUsers,totalSuccessfulUsers);
  
        dto = save(dto);
        return dto;
    }
  
    public List<BatchProcessInfoDTO> getEntitiesByBillingProcessId(Integer entityId) {
        final String hql =
            "select a " +
            "  from BatchProcessInfoDTO a " +
            " where a.billingProcess.id = :entity " +
            " order by a.id desc ";
       
        Query query = getSession().createQuery(hql);
        query.setParameter("entity", entityId);
        return (List<BatchProcessInfoDTO>) query.list();
    }
}
