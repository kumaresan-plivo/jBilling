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

package com.sapienter.jbilling.server.report.db;

import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * ReportDAS
 *
 * @author Brian Cowdery
 * @since 07/03/11
 */
public class ReportDAS extends AbstractDAS<ReportDTO> {
    public List<ReportDTO> findAllReportsByCompany(CompanyDTO entity) {
        Criteria criteria = getSession().createCriteria(ReportDTO.class);
        criteria.createAlias("entities" , "entity");
        criteria.add(Restrictions.eq("entity.id", entity.getId()));
        return criteria.list();
    }

    public List<ReportDTO> findAllByEntityId(Integer entityId) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .createAlias("entities", "entities")
                .add(Restrictions.eq("entities.id", entityId))
                .addOrder(Order.desc("id"));

        return criteria.list();
    }

}
