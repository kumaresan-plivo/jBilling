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
package com.sapienter.jbilling.server.item.db;

import com.sapienter.jbilling.server.user.db.CompanyDAS;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Query;
import org.hibernate.type.StringType;

public class ItemDAS extends AbstractDAS<ItemDTO> {

    /**
     * Returns a list of all items for the given item type (category) id.
     * If no results are found an empty list will be returned.
     *
     * @param itemTypeId item type id
     * @return list of items, empty if none found
     */
    @SuppressWarnings("unchecked")
    public List<ItemDTO> findAllByItemType(Integer itemTypeId) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .createAlias("itemTypes", "type")
                .add(Restrictions.eq("type.id", itemTypeId))
                .add(Restrictions.eq("deleted", 0))
                .addOrder(Order.desc("id"));

        return criteria.list();
    }

    /**
     * Returns a list of all items with item type (category) who's
     * description matches the given prefix.
     *
     * @param prefix prefix to check
     * @return list of items, empty if none found
     */
    @SuppressWarnings("unchecked")
    public List<ItemDTO> findItemsByCategoryPrefix(String prefix) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .createAlias("itemTypes", "type")
                .add(Restrictions.like("type.description", prefix + "%"));

        return criteria.list();
    }    

    public List<ItemDTO> findItemsByInternalNumber(String internalNumber) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .add(Restrictions.eq("internalNumber", internalNumber));

        return criteria.list();
    }

    public ItemDTO findItemByInternalNumber(String internalNumber, Integer entityId) {

        Integer rootCompanyId = new CompanyDAS().getParentCompanyId(entityId);
        rootCompanyId = rootCompanyId!=null?rootCompanyId:entityId;
        Criteria criteria = getSession().createCriteria(getPersistentClass())
                .add(Restrictions.eq("internalNumber", internalNumber).ignoreCase())
                .createAlias("entities", "entities", CriteriaSpecification.LEFT_JOIN)
                .add(Restrictions.disjunction()
                        .add(Restrictions.conjunction().add(Restrictions.eq("entity.id", rootCompanyId)).add(Restrictions.eq("global", true)))
                        .add(Restrictions.eq("entities.id", entityId)))
                .add(Restrictions.eq("deleted", 0));

        return (ItemDTO)criteria.uniqueResult();
    }

    private static final String CURRENCY_USAGE_FOR_ENTITY_SQL =
            "select count(*) from " +
            " item i, " +
            " item_price_timeline ipt, " +
            " price_model pm, " +
            " item_entity_map iem " +
            " where " +
            "     ipt.model_map_id = (select item_id from entity_item_price_map where item_id = i.id and entity_id = :entityId) " +
            " and ipt.price_model_id = pm.id " +
            " and pm.currency_id = :currencyId " +
            " and i.id = iem.item_id " +
            " and iem.entity_id = :entityId " +
            " and i.deleted = 0 ";

    public Long findProductCountByCurrencyAndEntity(Integer currencyId, Integer entityId ) {
        Query sqlQuery = getSession().createSQLQuery(CURRENCY_USAGE_FOR_ENTITY_SQL);
        sqlQuery.setParameter("currencyId", currencyId);
        sqlQuery.setParameter("entityId", entityId);
        Number count = (Number) sqlQuery.uniqueResult();
        return Long.valueOf(null == count ? 0L : count.longValue());
    }

    public Long findProductCountByInternalNumber(String internalNumber, Integer entityId, boolean isNew, Integer id) {
        Criteria criteria = getSession().createCriteria(getPersistentClass())
        		.createAlias("entities","entities", CriteriaSpecification.LEFT_JOIN)
                .add(Restrictions.eq("internalNumber", internalNumber))
                .add(Restrictions.eq("deleted", 0))
                .add(Restrictions.eq("entities.id", entityId));

        if(!isNew)
            criteria.add(Restrictions.ne("id", id));

        return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }
    
    public List<ItemDTO> findByEntityId(Integer entityId) {
    	Criteria criteria = getSession().createCriteria(ItemDTO.class)
        		.createAlias("entities","entities", CriteriaSpecification.LEFT_JOIN)
        		.add(Restrictions.eq("entities.id", entityId))
        		.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    /**
     * Get all items for the given company its childs and global categories
     */
    public List<ItemDTO> findItems(Integer entity, List<Integer> entities, boolean isRoot) {
        Criteria criteria = getSession().createCriteria(ItemDTO.class)
                .createAlias("entities", "entities", CriteriaSpecification.LEFT_JOIN);

        Disjunction dis = Restrictions.disjunction();
        dis.add(Restrictions.eq("global", true));
        dis.add(Restrictions.in("entities.id", entities));
        if (isRoot) {
            dis.add(Restrictions.eq("entities.parent.id", entity));
        }

        criteria.add(dis)
                .addOrder(Order.asc("id"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return criteria.list();
	}

    private static final String PRODUCT_VISIBLE_TO_PARENT_COMPANY_SQL =
            "select count(*) from item i " +
                    " left join item_entity_map ie on ie.item_id = i.id where " +
                    " i.id = :itemId and " +
                    " (ie.entity_id = :entityId or i.entity_id = :entityId) and" +
                    " i.deleted = 0";
    
    private static final String PRODUCT_AVAILABLE_TO_PARENT_COMPANY_SQL =
            "select count(*) from item i " +
                    " left join item_entity_map ie on ie.item_id = i.id where " +
                    " i.id = :itemId and " +
                    " (ie.entity_id = :entityId) and" +
                    " i.deleted = 0";

    private static final String PRODUCT_VISIBLE_TO_CHILD_HIERARCHY_SQL =
            "select count(*) from item i "+
                "left outer join item_entity_map icem "+
                "on i.id = icem.item_id "+
                "where i.id = :itemId "+
                "and  i.deleted = 0 "+
                "and  (i.entity_id = :childCompanyId or " +
                " icem.entity_id = :childCompanyId or " +
                "((icem.entity_id = :parentCompanyId or icem.entity_id is null) and " +
                "i.global = true));";

    public boolean isProductVisibleToCompany(Integer itemId, Integer entityId, Integer parentId) {
        if (null == parentId) {
            //this means that the entityId is root so the
            //product must be defined for that company
            SQLQuery query = getSession().createSQLQuery(PRODUCT_VISIBLE_TO_PARENT_COMPANY_SQL);
            query.setParameter("itemId", itemId);
            query.setParameter("entityId", entityId);
            Number count = (Number) query.uniqueResult();
            return null != count ? count.longValue() > 0 : false;
        } else {
            //check if the product is visible to either the parent or the child company
            SQLQuery query = getSession().createSQLQuery(PRODUCT_VISIBLE_TO_CHILD_HIERARCHY_SQL);
            query.setParameter("itemId", itemId);
            query.setParameter("parentCompanyId", parentId);
            query.setParameter("childCompanyId", entityId);
            Number count = (Number) query.uniqueResult();
            return null != count ? count.longValue() > 0 : false;
        }
    }
    
    public boolean isProductAvailableToCompany(Integer itemId, Integer entityId) {
            //this means that the entityId is root so the
            //product must be defined for that company
            SQLQuery query = getSession().createSQLQuery(PRODUCT_AVAILABLE_TO_PARENT_COMPANY_SQL);
            query.setParameter("itemId", itemId);
            query.setParameter("entityId", entityId);
            Number count = (Number) query.uniqueResult();
            return null != count ? count.longValue() > 0 : false;
        
    }

    public ItemDTO findByMetaFieldNameAndValue(Integer entityId, String metaFieldName, String metaFieldValue){
        Criteria criteria = getSession().createCriteria(ItemDTO.class)
                .add(Restrictions.eq("entity.id", entityId))
                .createAlias("metaFields", "value")
                .createAlias("value.field", "mf")
                .add(Restrictions.eq("deleted", 0))
                .add(Restrictions.eq("mf.name", metaFieldName))
                .add(Restrictions.sqlRestriction("string_value =  ?", metaFieldValue, StringType.INSTANCE));
        return (ItemDTO)criteria.uniqueResult();
    }

    /* Method return all non global product which belongs to non global category */
    public List<ItemDTO> findNonGlobalItems(Integer entityId) {
        Criteria criteria = getSession().createCriteria(ItemDTO.class)
                .createAlias("entities", "entities", CriteriaSpecification.LEFT_JOIN)
                .createAlias("itemTypes","itemType")

                .add(Restrictions.eq("entities.id", entityId))
                .add(Restrictions.eq("itemType.global", false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return criteria.list();
    }
    
    private static final String PLAN_FOR_ITEM =
            "select count(*) from plan i " +
                    " where " +
                    " i.item_id = :itemId ";

    public boolean isPlan(int itemId) {
        SQLQuery query = getSession().createSQLQuery(PLAN_FOR_ITEM);
        query.setParameter("itemId", itemId);
        Number count = (Number) query.uniqueResult();
        return count.intValue() > 0;
    }
}
