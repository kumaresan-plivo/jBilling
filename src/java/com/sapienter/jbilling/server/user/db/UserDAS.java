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
package com.sapienter.jbilling.server.user.db;

import java.util.Date;
import java.util.List;

import com.sapienter.jbilling.server.util.Constants;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.type.StringType;

import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.user.UserDTOEx;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

import org.hibernate.type.DateType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;

public class UserDAS extends AbstractDAS<UserDTO> {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(UserDAS.class));

     private static final String findInStatusSQL =
         "SELECT a " +
         "  FROM UserDTO a " +
         " WHERE a.userStatus.id = :status " +
         "   AND a.company.id = :entity " +
         "   AND a.deleted = 0" ;

     private static final String findNotInStatusSQL =
         "SELECT a " +
         "  FROM UserDTO a " +
         " WHERE a.userStatus.id <> :status " +
         "   AND a.company.id = :entity " +
         "   AND a.deleted = 0";

     private static final String findAgeingSQL =
         "SELECT a " +
         "  FROM UserDTO a " +
         " WHERE a.userStatus.id > " + UserDTOEx.STATUS_ACTIVE +
         "   AND a.customer.excludeAging = 0 " +
         "   AND a.company.id = :entity " +
         "   AND a.deleted = 0";

     private static final String CURRENCY_USAGE_FOR_ENTITY_SQL =
             "SELECT count(*) " +
             "  FROM UserDTO a " +
             " WHERE a.currency.id = :currency " +
             "	  AND a.company.id = :entity "+
             "   AND a.deleted = 0";
     
     private static final String FIND_CHILD_LIST_SQL =
    	        "SELECT u.id " +
    	        "FROM UserDTO u " +
    	        "WHERE u.deleted=0 and u.customer.parent.baseUser.id = :parentId";


	public List<Integer> findChildList(Integer userId) {
		Query query = getSession().createQuery(FIND_CHILD_LIST_SQL);
		query.setParameter("parentId", userId);
		
		return query.list();
	}

     public Long findUserCountByCurrencyAndEntity(Integer currencyId, Integer entityId){
         Query query = getSession().createQuery(CURRENCY_USAGE_FOR_ENTITY_SQL);
         query.setParameter("currency", currencyId);
         query.setParameter("entity", entityId);

         return (Long) query.uniqueResult();
     }

    private static final String findCurrencySQL =
          "SELECT count(*) " +
          "  FROM UserDTO a " +
          " WHERE a.currency.id = :currency "+
          "   AND a.deleted = 0";

    public UserDTO findRoot(String username) {
        if (username == null || username.length() == 0) {
            LOG.error("can not find an empty root: " + username);
            return null;
        }
        // I need to access an association, so I can't use the parent helper class
        Criteria criteria = getSession().createCriteria(UserDTO.class)
            .add(Restrictions.eq("userName", username))
            .add(Restrictions.eq("deleted", 0))
            .createAlias("roles", "r")
                .add(Restrictions.eq("r.roleTypeId", CommonConstants.TYPE_ROOT));

        criteria.setCacheable(true); // it will be called over an over again

        return (UserDTO) criteria.uniqueResult();
    }

    public UserDTO findWebServicesRoot(String username) {
        if (username == null || username.length() == 0) {
            LOG.error("can not find an empty root: " + username);
            return null;
        }
        // I need to access an association, so I can't use the parent helper class
        Criteria criteria = getSession().createCriteria(UserDTO.class)
            .add(Restrictions.eq("userName", username))
            .add(Restrictions.eq("deleted", 0))
            .createAlias("roles", "r")
                .add(Restrictions.eq("r.roleTypeId", CommonConstants.TYPE_ROOT))
            .createAlias("permissions", "p")
                .add(Restrictions.eq("p.permission.id", 120));

        criteria.setCacheable(true); // it will be called over an over again

        return (UserDTO) criteria.uniqueResult();
    }

    public UserDTO findByUserId(Integer userId, Integer entityId) {
        // I need to access an association, so I can't use the parent helper class
        Criteria criteria = getSession().createCriteria(UserDTO.class)
                .add(Restrictions.eq("id", userId))
                .add(Restrictions.eq("deleted", 0))
                .createAlias("company", "e")
                .add(Restrictions.eq("e.id", entityId))
                .add(Restrictions.eq("e.deleted", 0));

        return (UserDTO) criteria.uniqueResult();
    }

    public UserDTO findByUserName(String username, Integer entityId) {
        // I need to access an association, so I can't use the parent helper class
        Criteria criteria = getSession().createCriteria(UserDTO.class)
                .add(Restrictions.eq("userName", username).ignoreCase())
                .add(Restrictions.eq("deleted", 0))
                .createAlias("company", "e")
                    .add(Restrictions.eq("e.id", entityId))
                    .add(Restrictions.eq("e.deleted", 0));

        return (UserDTO) criteria.uniqueResult();
    }

    public List<UserDTO> findByEmail(String email, Integer entityId) {
        Criteria criteria = getSession().createCriteria(UserDTO.class)
                .add(Restrictions.eq("deleted", 0)) 
                .createAlias("company", "e")
                .add(Restrictions.eq("e.id", entityId))
                .createAlias("contact", "c")
                .add(Restrictions.eq("c.email", email).ignoreCase());

        return criteria.list();
    }

    public List<UserDTO> findInStatus(Integer entityId, Integer statusId) {
        Query query = getSession().createQuery(findInStatusSQL);
        query.setParameter("entity", entityId);
        query.setParameter("status", statusId);
        return query.list();
    }

    public List<UserDTO> findNotInStatus(Integer entityId, Integer statusId) {
        Query query = getSession().createQuery(findNotInStatusSQL);
        query.setParameter("entity", entityId);
        query.setParameter("status", statusId);
        return query.list();
    }

    public List<UserDTO> findAgeing(Integer entityId) {
        Query query = getSession().createQuery(findAgeingSQL);
        query.setParameter("entity", entityId);
        return query.list();
    }

    public boolean exists(Integer userId, Integer entityId) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .add(Restrictions.idEq(userId))
                .createAlias("company", "company")
                .add(Restrictions.eq("company.id", entityId))
                .setProjection(Projections.rowCount());

        return (criteria.uniqueResult() != null && ((Long) criteria.uniqueResult()) > 0);
    }

    public Long findUserCountByCurrency(Integer currencyId){
        Query query = getSession().createQuery(findCurrencySQL);
        query.setParameter("currency", currencyId);
        return (Long) query.uniqueResult();
    }

    public List<UserDTO> findAdminUsers(Integer entityId) {
        Criteria criteria = getSession().createCriteria(UserDTO.class)
                .add(Restrictions.eq("company.id", entityId))
                .add(Restrictions.eq("deleted", 0))
                .createAlias("roles", "r")
                .add(Restrictions.or(
                        Restrictions.eq("r.roleTypeId", CommonConstants.TYPE_ROOT),
                        Restrictions.eq("r.roleTypeId", CommonConstants.TYPE_SYSTEM_ADMIN)));

        return criteria.list();
    }

    public List<UserDTO> findAllCustomers(Integer entityId) {
        Criteria criteria = getSession().createCriteria(UserDTO.class)
                .add(Restrictions.eq("company.id", entityId))
                .add(Restrictions.eq("deleted", 0))
                .createAlias("roles", "r")
                .add(Restrictions.eq("r.roleTypeId", CommonConstants.TYPE_CUSTOMER));

        return criteria.list();
    }

    public List<Integer> findAdminUserIds(Integer entityId) {
        Criteria criteria = getSession().createCriteria(UserDTO.class)
                .add(Restrictions.eq("company.id", entityId))
                .add(Restrictions.eq("deleted", 0))
                .createAlias("roles", "r")
                .add(Restrictions.or(
                        Restrictions.eq("r.roleTypeId", CommonConstants.TYPE_ROOT),
                        Restrictions.eq("r.roleTypeId", CommonConstants.TYPE_SYSTEM_ADMIN)));
        criteria.setProjection(Projections.id());

        return criteria.list();
    }

    public List<Integer> findClerkUserIds(Integer entityId) {
        Criteria criteria = getSession().createCriteria(UserDTO.class)
                .add(Restrictions.eq("company.id", entityId))
                .add(Restrictions.eq("deleted", 0))
                .createAlias("roles", "r")
                .add(Restrictions.eq("r.roleTypeId", CommonConstants.TYPE_CLERK));
        criteria.setProjection(Projections.id());

        return (List<Integer>) criteria.list();
    }


    @SuppressWarnings("unchecked")
    public ScrollableResults findUserIdsWithUnpaidInvoicesForAgeing(Integer entityId) {
        DetachedCriteria query = DetachedCriteria.forClass(UserDTO.class)
                .add(Restrictions.eq("deleted", 0))
                .createAlias("customer", "customer", CriteriaSpecification.INNER_JOIN)
                .add(Restrictions.eq("customer.excludeAging", 0))
                .createAlias("invoices", "invoice", CriteriaSpecification.INNER_JOIN)  // only with invoices
                .add(Restrictions.eq("invoice.isReview", 0))
                .add(Restrictions.eq("invoice.deleted", 0))
                .createAlias("invoice.invoiceStatus", "status", CriteriaSpecification.INNER_JOIN)
                .add(Restrictions.ne("status.id", Constants.INVOICE_STATUS_PAID))
                .setProjection(Projections.distinct(Projections.property("id")));
        if (entityId != null) {
            query.add(Restrictions.eq("company.id", entityId));
        }
        // added order to get all ids in ascending order
        query.addOrder(Order.asc("id"));

        Criteria criteria = query.getExecutableCriteria(getSession());
        return criteria.scroll();
    }

    public List<UserDTO> findByMetaFieldValueIds(Integer entityId, List<Integer> valueIds){
        Criteria criteria = getSession().createCriteria(UserDTO.class, "user");
        criteria.add(Restrictions.eq("company.id", entityId));
        criteria.createAlias("user.customer", "customer");
        criteria.createAlias("customer.metaFields", "values");
        criteria.add(Restrictions.in("values.id", valueIds));
        return criteria.list();
    }
    
    public List<UserDTO> findByAitMetaFieldValueIds(Integer entityId, List<Integer> valueIds){
        Criteria criteria = getSession().createCriteria(UserDTO.class, "user");
        criteria.add(Restrictions.eq("company.id", entityId));
        criteria.createAlias("user.customer", "customer");
        criteria.createAlias("customer.customerAccountInfoTypeMetaFields", "cmfs");
        criteria.createAlias("cmfs.metaFieldValue", "value");
        criteria.add(Restrictions.in("value.id", valueIds));
        return criteria.list();
    }

    public UserDTO findByMetaFieldNameAndValue(Integer entityId, String metaFieldName, String metaFieldValue){
        Criteria criteria = getSession().createCriteria(UserDTO.class, "user");
        criteria.add(Restrictions.eq("company.id", entityId));
        criteria.createAlias("user.customer", "customer");
        criteria.createAlias("customer.customerAccountInfoTypeMetaFields", "cmfs");
        criteria.createAlias("cmfs.metaFieldValue", "value");
        criteria.createAlias("value.field", "metaField");
        criteria.add(Restrictions.eq("deleted", 0));
        criteria.add(Restrictions.sqlRestriction("string_value =  ?", metaFieldValue, StringType.INSTANCE));
        criteria.add(Restrictions.eq("metaField.name", metaFieldName));
        return (UserDTO) criteria.uniqueResult();
    }

    /**
     * Returns the entity ID for the user. Executes really
     * fast and does not use any joins.
     */
    public Integer getUserCompanyId(Integer userId){
        SQLQuery query = getSession().createSQLQuery("select entity_id from base_user where id = :userId");
        query.setParameter("userId", userId);
        return (Integer) query.uniqueResult();
    }
    
    public boolean hasSubscriptionProduct(Integer userId) {
    	DetachedCriteria dc = DetachedCriteria.forClass(CustomerDTO.class).
    							createAlias("parent", "parent").
    							createAlias("parent.baseUser", "parentUser").
    			 				add(Restrictions.eq("parentUser.id", userId)).
    			 				createAlias("baseUser", "baseUser").
    			 				setProjection(Projections.property("baseUser.id"));
    	
 		Criteria c = getSession().createCriteria(OrderDTO.class).
 				     			add(Restrictions.eq("deleted", 0)).
 				     			createAlias("baseUserByUserId","user").
 				     			add(Property.forName("user.id").in(dc)).
 				     			
 				 				createAlias("lines","lines").
 				 				createAlias("lines.item", "item").
 				 				createAlias("item.itemTypes", "types").
 				 				add(Restrictions.eq("types.orderLineTypeId", Constants.ORDER_LINE_TYPE_SUBSCRIPTION)).
 				 				setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
 		
 		return c.list().size() > 0;
    }
    
    public boolean isSubscriptionAccount(Integer userId) {
        Criteria c = getSession().createCriteria(OrderDTO.class).
                add(Restrictions.eq("deleted", 0)).
                createAlias("baseUserByUserId", "user").
                add(Restrictions.eq("user.id", userId)).

                createAlias("lines", "lines").
                createAlias("lines.item", "item").
                createAlias("item.itemTypes", "types").
                add(Restrictions.eq("types.orderLineTypeId", Constants.ORDER_LINE_TYPE_SUBSCRIPTION)).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        return c.list().size() > 0;
    }
    
    public void saveUserWithNewPasswordScheme(Integer userId, String newPassword, Integer newScheme, Integer entityId){
    	String hql = "Update UserDTO u set password = :password, encryptionScheme = :newScheme where id = :id and company.id = :entityId";
    	Query query = getSession().createQuery(hql).setString("password", newPassword).setInteger("newScheme", newScheme)
    			.setInteger("id", userId).setInteger("entityId", entityId);
    	query.executeUpdate();
    }

    public List<Integer> findUsersInActiveSince(Date activityThresholdDate, Integer entityId) {
        if (null == activityThresholdDate) {
            LOG.error("can not find users on empty date %s for entity id %s",activityThresholdDate, entityId );
            return null;
        }
        // Get a list of users that have not logged in since before the provided date
        Criteria criteria = getSession().createCriteria(UserDTO.class)
                .add(Restrictions.or(Restrictions.and(Restrictions.isNotNull("lastLogin"), Restrictions.le("lastLogin", activityThresholdDate)), Restrictions.and(Restrictions.isNull("lastLogin"), Restrictions.le("createDatetime", activityThresholdDate))))
                .add(Restrictions.eq("entity_id", entityId))
                .add(Restrictions.eq("deleted", 0))
                .add(Restrictions.isNull("accountDisabledDate"))
                .setProjection(Projections.id());

        return criteria.list();
    }

    public List<UserDTO> getUsersNotInvoiced(Integer entityId, Date startDate, Date endDate, Integer max, Integer offset) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append("	distinct u.* ");
        sb.append("from ");
        sb.append("	base_user u ");
        sb.append("	join entity e on u.entity_id = e.id ");
        sb.append("	join entity parent_entity  on e.parent_id = parent_entity.id ");
        sb.append("	join customer c on c.user_id = u.id ");
        sb.append("	left join customer_meta_field_map termination_cmfm on c.id = termination_cmfm.customer_id ");
        sb.append("	left join meta_field_value termination_mfv on termination_cmfm.meta_field_value_id = termination_mfv.id ");
        sb.append("	left join ( ");
        sb.append("		select mfv.string_value as value, c.id as customer_id from customer c ");
        sb.append("			join customer_meta_field_map cmfm on c.id = cmfm.customer_id ");
        sb.append("			join meta_field_value mfv on cmfm.meta_field_value_id = mfv.id ");
        sb.append("			join meta_field_name mfn on mfv.meta_field_name_id = mfn.id ");
        sb.append("		where mfn.name = 'Termination' ");
        sb.append("	) termination_mf on termination_mf.customer_id = c.id ");
        sb.append("where true ");
        sb.append("	and (u.entity_id = :entityId or parent_entity.id = :entityId) ");
        sb.append("	and e.parent_id is not null ");
        sb.append("	and u.deleted = 0 ");
        sb.append("	and u.id not in( ");
        sb.append("		select ");
        sb.append("			distinct i.user_id ");
        sb.append("		from ");
        sb.append("			invoice i ");
        sb.append("		where true ");
        sb.append("			and i.deleted = 0 ");
        sb.append("         and i.is_review = 0 ");
        sb.append("			and i.create_datetime between :startDate and :endDate ");
        sb.append("	) ");
        sb.append("	and (lower(termination_mf.value) <> 'dropped' or termination_mf.value is null) ");

        // Exclude those customers which are subscribed to Day head rate change plans
        sb.append("	and u.id not in( ");
        sb.append("     select bu.id from base_user bu join customer c on bu.id = c.user_id ");
        sb.append("     join customer_account_info_type_timeline caitt on c.id = caitt.customer_id ");
        sb.append("     join meta_field_value mfv on caitt.meta_field_value_id = mfv.id ");
        sb.append("     join meta_field_name mfn on mfv.meta_field_name_id = mfn.id ");
        sb.append("     where mfn.name = 'PLAN' AND mfv.string_value in (select internal_number from item i ");
        sb.append("         join plan p on i.id = p.item_id ");
        sb.append("         join plan_meta_field_map pmfm on p.id = pmfm.plan_id ");
        sb.append("         join meta_field_value mfv on pmfm.meta_field_value_id = mfv.id ");
        sb.append("         join meta_field_name mfn on mfv.meta_field_name_id = mfn.id ");
        sb.append("         where mfn.name = 'Send rate change daily' AND mfv.boolean_value = TRUE AND mfn.entity_id = :entityId)");
        sb.append("	) ");

        sb.append("order by u.user_name ");
        sb.append("limit :max ");
        sb.append("offset :offset ");

        Query query = getSessionFactory().getCurrentSession().createSQLQuery(sb.toString())
                .addEntity(UserDTO.class)
                .setParameter("entityId", entityId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("max", max)
                .setParameter("offset", offset);

        return query.list();
    }

    public Long getUsersNotInvoicedCount(Integer entityId, Date startDate, Date endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append("	count(*) as total ");
        sb.append("from ");
        sb.append("	base_user u ");
        sb.append("	join entity e on u.entity_id = e.id ");
        sb.append("	join entity parent_entity  on e.parent_id = parent_entity.id ");
        sb.append("	join customer c on c.user_id = u.id ");
        sb.append("	left join customer_meta_field_map termination_cmfm on c.id = termination_cmfm.customer_id ");
        sb.append("	left join meta_field_value termination_mfv on termination_cmfm.meta_field_value_id = termination_mfv.id ");
        sb.append("	left join ( ");
        sb.append("		select mfv.string_value as value, c.id as customer_id from customer c ");
        sb.append("			join customer_meta_field_map cmfm on c.id = cmfm.customer_id ");
        sb.append("			join meta_field_value mfv on cmfm.meta_field_value_id = mfv.id ");
        sb.append("			join meta_field_name mfn on mfv.meta_field_name_id = mfn.id ");
        sb.append("		where mfn.name = 'Termination' ");
        sb.append("	) termination_mf on termination_mf.customer_id = c.id ");
        sb.append("where true ");
        sb.append("	and (u.entity_id = :entityId or parent_entity.id = :entityId) ");
        sb.append("	and e.parent_id is not null ");
        sb.append("	and u.deleted = 0 ");
        sb.append("	and u.id not in( ");
        sb.append("		select ");
        sb.append("			distinct i.user_id ");
        sb.append("		from ");
        sb.append("			invoice i ");
        sb.append("		where true ");
        sb.append("			and i.deleted = 0 ");
        sb.append("         and i.is_review = 0 ");
        sb.append("			and i.create_datetime between :startDate and :endDate ");
        sb.append("	) ");
        sb.append("	and (lower(termination_mf.value) <> 'dropped' or termination_mf.value is null) ");

        // Exclude those customers which are subscribed to day head rate change plans
        sb.append("	and u.id not in( ");
        sb.append("     select bu.id from base_user bu join customer c on bu.id = c.user_id ");
        sb.append("     join customer_account_info_type_timeline caitt on c.id = caitt.customer_id ");
        sb.append("     join meta_field_value mfv on caitt.meta_field_value_id = mfv.id ");
        sb.append("     join meta_field_name mfn on mfv.meta_field_name_id = mfn.id ");
        sb.append("     where mfn.name = 'PLAN' AND mfv.string_value in (select internal_number from item i ");
        sb.append("         join plan p on i.id = p.item_id ");
        sb.append("         join plan_meta_field_map pmfm on p.id = pmfm.plan_id ");
        sb.append("         join meta_field_value mfv on pmfm.meta_field_value_id = mfv.id ");
        sb.append("         join meta_field_name mfn on mfv.meta_field_name_id = mfn.id ");
        sb.append("         where mfn.name = 'Send rate change daily' AND mfv.boolean_value = TRUE AND mfn.entity_id = :entityId)");
        sb.append("	) ");

        Query query = getSessionFactory().getCurrentSession().createSQLQuery(sb.toString())
                .addScalar("total", StandardBasicTypes.LONG)
                .setParameter("entityId", entityId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);

        return (Long) query.uniqueResult();
    }

    public List<UserDTO> findByMetaFieldNameAndValue(String metaFieldName, Date date, Integer entityId){
        Criteria criteria = getSession().createCriteria(UserDTO.class, "user");
        criteria.add(Restrictions.eq("company.id", entityId));
        criteria.createAlias("user.customer", "customer");
        criteria.createAlias("customer.metaFields", "value");
        criteria.createAlias("value.field", "metaField");
        criteria.add(Restrictions.eq("deleted", 0));
        criteria.add(Restrictions.sqlRestriction("date_value =  ?", date, DateType.INSTANCE));
        criteria.add(Restrictions.eq("metaField.name", metaFieldName));
        return criteria.list();
    }

    public UserDTO findSingleByMetaFieldNameAndValue(String metaFieldName, String metaFieldValue) {
        Criteria criteria = getSession().createCriteria(UserDTO.class, "user");
        criteria.createAlias("user.customer", "customer");
        criteria.createAlias("customer.metaFields", "value");
        criteria.createAlias("value.field", "metaField");
        criteria.add(Restrictions.eq("deleted", 0));
        criteria.add(Restrictions.sqlRestriction("string_value =  ?", metaFieldValue, StringType.INSTANCE));
        criteria.add(Restrictions.eq("metaField.name", metaFieldName));
        return (UserDTO)criteria.uniqueResult();
    }

    public List<Integer> findByMetaFieldNameAndValues(String metaFieldName, List<String> values, Integer entityId){
        String valuesConCat = "";
        for(String value : values){
            valuesConCat = valuesConCat.toString().isEmpty()?valuesConCat+"'"+value+"'":valuesConCat+","+"'"+value+"'";
        }
        Criteria criteria = getSession().createCriteria(UserDTO.class, "user");
        criteria.add(Restrictions.eq("company.id", entityId));
        criteria.createAlias("user.customer", "customer");
        criteria.createAlias("customer.customerAccountInfoTypeMetaFields", "cmfs");
        criteria.createAlias("cmfs.metaFieldValue", "value");
        criteria.createAlias("value.field", "metaField");
        criteria.add(Restrictions.eq("deleted", 0));
        criteria.add(Restrictions.sqlRestriction("string_value in (" + valuesConCat + ")"));
        criteria.add(Restrictions.eq("metaField.name", metaFieldName));
        criteria.setProjection(Projections.distinct(Projections.property("id")));
        return criteria.list();
    }

    /*
    * NGES : Find drop customers for the account number.
    * @params enityId
    * @params metaFieldName user account metafield name
    * @params metaFieldValue user account metafield value
    *
    * @return
    * */
    public List<Integer> findDropCustomers(Integer entityId, String metaFieldName, String metaFieldValue){
        //this query returning the all drop customer customer id for a utility account number
        StringBuilder sb = new StringBuilder();
        sb.append("select bu.id from base_user bu ");
        sb.append("inner join customer customer on bu.id=customer.user_id ");
        sb.append("inner join customer_account_info_type_timeline caitt on customer.id=caitt.customer_id ");
        sb.append("left outer join meta_field_group mfg on caitt.account_info_type_id=mfg.id ");
        sb.append("inner join meta_field_value aitValue on caitt.meta_field_value_id=aitValue.id ");
        sb.append("inner join meta_field_name aitMetaFieldName on aitValue.meta_field_name_id=aitMetaFieldName.id ");
        sb.append("inner join customer_meta_field_map cmfm on customer.id=cmfm.customer_id ");
        sb.append("inner join meta_field_value customerMetaFieldValue on cmfm.meta_field_value_id=customerMetaFieldValue.id ");
        sb.append("inner join meta_field_name customerMetaFieldName on customerMetaFieldValue.meta_field_name_id=customerMetaFieldName.id ");
        sb.append("where ");
        sb.append("bu.entity_id= :entityId ");
        sb.append("and bu.deleted=0 ");
        sb.append("and ( ");
        sb.append("      ( ");
        sb.append("      aitValue.string_value =  :metaFieldValue ");
        sb.append("      and aitMetaFieldName.name=:metaFieldName ");
        sb.append("      ) ");
        sb.append("      and ( ");
        sb.append("      customerMetaFieldValue.string_value in  ( ");
        sb.append("       'Dropped','Esco Rejected' ");
        sb.append("       ) ");
        sb.append("       and customerMetaFieldName.name='Termination' ");
        sb.append("       ) ");
        sb.append("    ) order by bu.create_datetime desc ");
        Query query = getSessionFactory().getCurrentSession().createSQLQuery(sb.toString())
                .setParameter("entityId", entityId)
                .setParameter("metaFieldName", metaFieldName)
                .setParameter("metaFieldValue", metaFieldValue);

        return query.list();
    }

    // NGES : This method is NGES specific and it is returning non drop User
    public UserDTO findUserByAccountNumber(Integer entityId, String metaFieldName, String metaFieldValue){
        List<Integer> dropUsers=findDropCustomers(entityId,metaFieldName,metaFieldValue);

        Criteria criteria = getSession().createCriteria(UserDTO.class, "user");
        criteria.add(Restrictions.eq("company.id", entityId));
        criteria.createAlias("user.customer", "customer");
        criteria.createAlias("customer.customerAccountInfoTypeMetaFields", "cmfs");
        criteria.createAlias("cmfs.metaFieldValue", "value");
        criteria.createAlias("value.field", "metaField");
        if(dropUsers.size()>0){
            criteria.add(Restrictions.not(
                    Restrictions.in("user.id", dropUsers)
            ));
        }
        criteria.add(Restrictions.eq("deleted", 0));
        criteria.add(Restrictions.sqlRestriction("string_value =  ?", metaFieldValue, StandardBasicTypes.STRING));
        criteria.add(Restrictions.eq("metaField.name", metaFieldName));
        return (UserDTO)criteria.uniqueResult();
    }
}
