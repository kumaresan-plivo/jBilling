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
package com.sapienter.jbilling.server.user.permisson.db;

import com.sapienter.jbilling.server.user.db.CompanyDTO;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Criteria;

import com.sapienter.jbilling.server.util.db.AbstractDAS;

import java.util.List;

public class RoleDAS extends AbstractDAS<RoleDTO> {

	public RoleDTO findByRoleTypeIdAndCompanyId(Integer roleTypeId, Integer companyId) {
		
	    Criteria criteria =getSession().createCriteria(getPersistentClass())
                            .add(Restrictions.eq("roleTypeId", roleTypeId));
        if (null != companyId) {
            criteria.add(Restrictions.eq("company.id", companyId));
        } else {
        	criteria.add(Restrictions.isNull("company"));
        }
        return findFirst(criteria);
	}

    public Integer findDefaultCompanyId() {
        Criteria criteria = getSession().createCriteria(CompanyDTO.class)
                .setProjection(Projections.min("id"));
        return (Integer)criteria.uniqueResult();
    }

    public List<RoleDTO> findAllRolesByEntity(Integer entityId) {
        Criteria criteria = getSession().createCriteria(RoleDTO.class)
                .add(Restrictions.eq("company.id", entityId));
        return criteria.list();
    }
}
