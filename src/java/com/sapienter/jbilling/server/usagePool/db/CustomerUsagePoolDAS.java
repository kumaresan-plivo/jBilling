/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2013] Enterprise jBilling Software Ltd.
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

package com.sapienter.jbilling.server.usagePool.db;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.server.util.db.AbstractDAS;

/**
 * CustomerUsagePoolDAS
 * This DAS has various finder methods to fetch customer usage pools based on various criteria.
 * @author Amol Gadre
 * @since 01-Dec-2013
 */

public class CustomerUsagePoolDAS extends AbstractDAS<CustomerUsagePoolDTO>{
	
	/**
	 * A finder method that returns a list of Customer Usage Pools 
	 * based on customer id provided to it as parameter.
	 * @param customerId
	 * @return List<CustomerUsagePoolDTO>
	 */
    @SuppressWarnings("unchecked")
	public List<CustomerUsagePoolDTO> findAllCustomerUsagePoolsByCustomerId(Integer customerId) {
		Query query = getSession().getNamedQuery("CustomerUsagePoolDTO.findAllCustomerUsagePoolsByCustomerId");
        query.setParameter("customer_id", customerId);

        return query.list();
	}
    
    /**
     * A query to find customer usage pool based on given customer id and usage pool id.
     */
    private static final String findCustomerUsagePoolByusagePoolIdAndCustomerIdNameSQL =
                    " FROM CustomerUsagePoolDTO a " +
                    " WHERE a.usagePool.id = :usagePoolId "+
                    " AND a.customer.id = :customerId";
    
    /**
     * This method fetches the specific Customer Usage Pool given the customer id and usage pool id.
     * @param usagePoolId
     * @param customerId
     * @return CustomerUsagePoolDTO
     */
    public CustomerUsagePoolDTO getCustomerUsagePoolByPoolIdAndCustomerId(Integer usagePoolId, Integer customerId) {
    	Query query = getSession().createQuery(findCustomerUsagePoolByusagePoolIdAndCustomerIdNameSQL);
    	query.setParameter("usagePoolId", usagePoolId);
    	query.setParameter("customerId", customerId);
    	return  null != query.list() ? (CustomerUsagePoolDTO)query.list().get(0) : null;
    }
    
    /**
     * Query to find customer usage pool by id.
     */
    private static final String findCustomerUsagePoolByIddNameSQL =
            " FROM CustomerUsagePoolDTO a " +
            " WHERE a.id = :id";
    
    /**
     * A finder method to fetch customer usage pool by its id.
     * @param customerUsagePoolId
     * @return CustomerUsagePoolDTO
     */
    public CustomerUsagePoolDTO findCustomerUsagePoolsById(Integer customerUsagePoolId) {
        Query query = getSession().createQuery(findCustomerUsagePoolByIddNameSQL);
        query.setParameter("id", customerUsagePoolId);
        return  null != query.list() ? (CustomerUsagePoolDTO)query.list().get(0) : null;
    }
    
    /**
     * This method returns all customer usage pool ids for records
     * that are eligible for evaluation and update of cycle end date and quantity.
     * It simply picks up all records that have cycle end date less that equal to current date/time.
     * @return List<Integer> customer usage pool ids
     */
    public List<Integer> findCustomerUsagePoolsForEvaluation(Integer entityId) { 
    	Criteria criteria = getSession().createCriteria(getPersistentClass(), "customerUsagePool");
    	criteria.createAlias("customer", "customer", CriteriaSpecification.INNER_JOIN);
    	criteria.createAlias("customer.baseUser", "user", CriteriaSpecification.INNER_JOIN);
    	criteria.createAlias("user.company", "entity", CriteriaSpecification.INNER_JOIN);
    	criteria.add(Restrictions.eq("entity.id", entityId));
		criteria.add(Restrictions.le("customerUsagePool.cycleEndDate", new Date()));
		criteria.setProjection(Projections.id());
        return criteria.list();
	}
    
    /**
     * A finder method that fetches list of customer usage pools given the customer id.
     * @param customerId
     * @return List<CustomerUsagePoolDTO>
     */
    @SuppressWarnings("unchecked")
    public List<CustomerUsagePoolDTO> findCustomerUsagePoolByCustomerId(Integer customerId) {
    	Criteria criteria = getSession().createCriteria(getPersistentClass())
                .add(Restrictions.eq("customer.id", customerId));
        
        return criteria.list();
    }
    
    /**
     * A method that fetches list of customer usage pools given the customer id.
     * @param customerId
     * @return List<CustomerUsagePoolDTO>
     */
    @SuppressWarnings("unchecked")
    public List<CustomerUsagePoolDTO> getCustomerUsagePoolsByCustomerId(Integer customerId) {
    	Criteria criteria = getSession().createCriteria(getPersistentClass())
                .add(Restrictions.eq("customer.id", customerId));
        
        return criteria.list();
    }
    
    /**
     * A method that fetches list of customer usage pools given the plan id that created them.
     * @param planId
     * @return List<CustomerUsagePoolDTO>
     */
    @SuppressWarnings("unchecked")
    public List<CustomerUsagePoolDTO> getCustomerUsagePoolsByPlanId(Integer planId) {
    	Criteria criteria = getSession().createCriteria(getPersistentClass())
                .add(Restrictions.eq("plan.id", planId));
        
        return criteria.list();
    }
}
