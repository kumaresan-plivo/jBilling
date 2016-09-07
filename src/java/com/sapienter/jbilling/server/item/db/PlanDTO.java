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

import com.sapienter.jbilling.server.audit.Auditable;

import com.sapienter.jbilling.server.item.PlanWS;
import com.sapienter.jbilling.server.metafields.EntityType;
import com.sapienter.jbilling.server.metafields.MetaFieldHelper;
import com.sapienter.jbilling.server.metafields.db.CustomizedEntity;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue; 
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.usagePool.db.UsagePoolDTO;
import com.sapienter.jbilling.server.util.csv.Exportable;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author Brian Cowdery
 * @since 26-08-2010
 */
@Entity
@Table(name = "plan")
@TableGenerator(
        name = "plan_GEN",
        table = "jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue = "plan",
        allocationSize = 1
)
@NamedQueries({
        @NamedQuery(name = "PlanDTO.findByPlanItem",
                    query = "select plan from PlanDTO plan where plan.item.id = :plan_item_id"),

        @NamedQuery(name = "CustomerDTO.findCustomersByPlan",
                    query = "select user.customer"
                            + " from OrderLineDTO line "
                            + " inner join line.item.plans as plan "
                            + " inner join line.purchaseOrder.baseUserByUserId as user"
                            + " where plan.id = :plan_id"
                            +"  and line.deleted = 0 "
                            + " and line.purchaseOrder.orderPeriod.id != 1 " // Constants.ORDER_PERIOD_ONCE
                            + " and line.purchaseOrder.orderStatus.orderStatusFlag = 0" //OrderStatusFlag.INVOICE   
                            + " and line.purchaseOrder.deleted = 0"),

        @NamedQuery(name = "PlanDTO.isSubscribed",
                    query = "select line"
                            + " from OrderLineDTO line "
                            + " inner join line.item.plans as plan "
                            + " inner join line.purchaseOrder.baseUserByUserId as user "
                            + " where plan.id = :plan_id "
                            + " and user.id = :user_id "
                            + " and ( line.startDate <= :pricingDate or line.startDate = null ) "
                            + " and ( line.endDate > :pricingDate or line.endDate = null ) "
                            + " and   line.purchaseOrder.orderPeriod.id != 1 " // Constants.ORDER_PERIOD_ONCE
                            + " and   line.purchaseOrder.deleted = 0 and line.deleted = 0 "
                            //+ " and ( line.purchaseOrder.deletedDate > :pricingDate or line.purchaseOrder.deletedDate = null )"
                            + " and   line.purchaseOrder.activeSince  <= :pricingDate "
                            + " and ( line.purchaseOrder.activeUntil > :pricingDate or line.purchaseOrder.activeUntil = null)"),

        @NamedQuery(name = "PlanDTO.findByAffectedItem",
                    query = "select plan "
                            + " from PlanDTO plan "
                            + " inner join plan.planItems planItems "
                            + " where planItems.item.id = :affected_item_id"),

        @NamedQuery(name = "PlanDTO.findAllByEntity",
                    query = "select plan "
                            + " from PlanDTO plan inner join plan.item as it"
                            + " inner join it.entities as child"
                            + " where child.id= :entity_id"),
                            //+ " where plan.item.entity.id = :entity_id"),
        @NamedQuery(name = "PlanDTO.findAllActiveByEntity",
                query = "select distinct plan"
                        + " from PlanDTO plan"
                        + " inner join plan.item as it"
                        + " left outer join it.entities as child"
                        + " where ((child.id in (:entityIds)) or (it.entity.id = :entityId and it.global = true)) and it.deleted = 0"),
        @NamedQuery(name = "PlanDTO.findByItemId", query = "select plan "
	    		+ " from PlanDTO plan " + " where plan.item.id = :item_id"),
        @NamedQuery(name = "PlanDTO.isSubscribedFinished",
                query = "select line.id"
                        + " from OrderLineDTO line "
                        + " inner join line.item.plans as plan "
                        + " inner join line.purchaseOrder.baseUserByUserId as user "
                        + " where plan.id = :plan_id "
                        + " and user.id = :user_id "
                        +"  and line.deleted = 0 "
                        + " and line.purchaseOrder.orderPeriod.id != 1 " // Constants.ORDER_PERIOD_ONCE
                        + " and line.purchaseOrder.orderStatus.orderStatusFlag = 1" //+OrderStatusFlag.FINISHED
                        + " and line.purchaseOrder.deleted = 0")
})
// todo: cache config
public class PlanDTO extends CustomizedEntity implements Serializable, Exportable, Auditable {

    private Integer id;
    private ItemDTO item; // plan subscription item
    private OrderPeriodDTO period;
    private String description;
    private int editable = 0;
    private List<PlanItemDTO> planItems = new ArrayList<PlanItemDTO>();
    private List<MetaFieldValue> metaFields = new LinkedList<MetaFieldValue>();
    private Set<UsagePoolDTO> usagePools = new HashSet<UsagePoolDTO>(0);
    
    private PlanDTO parentPlan;
    private Set<PlanDTO> childPlans = new HashSet<PlanDTO>(0);

    public PlanDTO() {
    }

    public PlanDTO(PlanWS ws, ItemDTO item, OrderPeriodDTO period, List<PlanItemDTO> planItems, Set<UsagePoolDTO> usagePools) {
        this.id = ws.getId();
        this.item = item;
        this.period = period;
        this.description = ws.getDescription();
        this.editable = ws.getEditable();
        this.planItems = planItems;
        this.usagePools = usagePools;
    }

    @Id @GeneratedValue(strategy = GenerationType.TABLE, generator = "plan_GEN")
    @Column(name = "id", nullable = false, unique = true)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Item holding this plan. When the customer subscribes to this item the
     * plan prices will be added for the customer.
     *
     * @return plan subscription item
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    public ItemDTO getItem() {
        return item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }

    @Transient
    public Integer getItemId() {
    	return getItem().getId();
    }

    /**
     * Returns the plan subscription item.
     * Syntax sugar, alias for {@link #getItem()}
     * @return plan subscription item
     */
    @Transient
    public ItemDTO getPlanSubscriptionItem() {
        return getItem();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    public OrderPeriodDTO getPeriod() {
        return period;
    }

    public void setPeriod(OrderPeriodDTO period) {
        this.period = period;
    }

    @Column(name = "description", nullable = true, length = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "editable", nullable = false)
    public int getEditable() {
        return editable;
    }

    public void setEditable(int editable) {
        this.editable = editable;
    }

    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "plan")
    public List<PlanItemDTO> getPlanItems() {
        return planItems;
    }

    public void setPlanItems(List<PlanItemDTO> planItems) {
        for (PlanItemDTO planItem : planItems)
            planItem.setPlan(this);

        this.planItems = planItems;
    }

    public void addPlanItem(PlanItemDTO planItem) {
        planItem.setPlan(this);
        this.planItems.add(planItem);
    }
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinTable(
    		name = "plan_meta_field_map",
    		joinColumns = @JoinColumn(name = "plan_id"),
    		inverseJoinColumns = @JoinColumn(name = "meta_field_value_id")
    )
    @Sort(type = SortType.COMPARATOR, comparator = MetaFieldHelper.MetaFieldValuesOrderComparator.class)
    public List<MetaFieldValue> getMetaFields() {
    	return metaFields;
    }
    
    @Transient
    public void setMetaFields(List<MetaFieldValue> fields) {
    	this.metaFields = fields;
    }
	
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "plan_usage_pool_map",
               joinColumns = {@JoinColumn(name = "plan_id", updatable = false)},
               inverseJoinColumns = {@JoinColumn(name = "usage_pool_id", updatable = false)}
    )
	public Set<UsagePoolDTO> getUsagePools() {
		return usagePools;
	}
    
	public void setUsagePools(Set<UsagePoolDTO> usagePools) {
		this.usagePools = usagePools;
	}

    @Transient
    public EntityType[] getCustomizedEntityType() {
    	return new EntityType[] { EntityType.PLAN };
    }

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "nested_plan",
            joinColumns = @JoinColumn(name = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_plan_id"))
    public PlanDTO getParentPlan() {
        return parentPlan;
    }*/

    public void setParentPlan(PlanDTO parentPlan) {
        this.parentPlan = parentPlan;
    }

    /*@OneToMany(fetch = FetchType.LAZY, mappedBy = "parentPlan")
    public Set<PlanDTO> getChildPlans() {
        return childPlans;
    }*/

    public void setChildPlans(Set<PlanDTO> childPlans) {
        this.childPlans = childPlans;
    }

    public PlanItemDTO findPlanItem(Integer itemId) {
        for(PlanItemDTO planItem : getPlanItems()) {
            if(itemId.equals(planItem.getItem().getId())) {
                return planItem;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanDTO planDTO = (PlanDTO) o;

        if (description != null ? !description.equals(planDTO.description) : planDTO.description != null) return false;
        if (editable != planDTO.editable) return false;
        if (id != null ? !id.equals(planDTO.id) : planDTO.id != null) return false;
        if (item != null ? !item.equals(planDTO.item) : planDTO.item != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + editable;
        return result;
    }

    @Override
    public String toString() {
        return "PlanDTO{"
               + "id=" + id
               + ", item=" + item
               + ", description='" + description + '\''
                + ", editable=" + editable
               + ", planItems=" + planItems
               + '}';
    }

    public String getAuditKey(Serializable id) {
        StringBuilder key = new StringBuilder();
        key.append(getItem().getEntity().getId())
                .append("-")
                .append(id);

        return key.toString();
    }

    @Transient
    public String[] getFieldNames() {
        return new String[] {
                "id",
                "productcode",
                "description",
                "Period",
                "currency",
                "rate",
                "itemid",
                //Plan Items
                "planItemProductCode",
                "planItemProductDescription",
                "planItemPrecedence",
                "bundeledQuanitity",
                "bundeledPeriod",
                "addTOCustomer",
                "pricingStrategy",
                 "rate",
                "currency",
                "attributes"
        };
    }

    @Transient
    public Object[][] getFieldValues() {

        List<Object[]> values = new ArrayList<Object[]>();
        
        // Now prices for company exist for different companies, 
        // have to tell for which company you want to get price for
        // current setting 'null', gives global price
        PriceModelDTO currentPrice = item.getPrice(new Date(), this.period.getCompany().getId());
       // main plan row
        values.add(
                new Object[] {
                        id,
                        (item!=null?item.getInternalNumber():null),
                        description,
                        (period !=null? period.getDescription():null),
                        (currentPrice != null ? currentPrice.getType().name() : null),
                        (currentPrice != null ? currentPrice.getRate() : null),
                        (item!=null?item.getId():null),

                }
                );

        // indented row for each invoice line  planItem.getModels().get(new Date()).getRate(),
       for (PlanItemDTO planItem : planItems) {
           PriceModelDTO priceModel = planItem.getModels().get(new Date());
           values.add(
                        new Object[] {
                                // padding for the main invoice columns
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                //planItems
                                (planItem.getItem()!=null?planItem.getItem().getInternalNumber():null),
                                (planItem.getItem()!=null?planItem.getItem().getDescription():null),
                                (planItem.getItem()!=null?planItem.getPrecedence():null),
                                (planItem.getBundle()!=null? planItem.getBundle().getQuantity():null),
                                (planItem.getBundle()!=null? planItem.getBundle().getPeriod().getValue():null),
                                (planItem.getBundle()!=null? planItem.getBundle().getTargetCustomer():null),
                                (planItem.getModels()!= null?(priceModel!=null?priceModel.getStrategy():null):null),
                                (planItem.getModels()!= null?(priceModel!=null?priceModel.getRate():null):null),
                                (planItem.getModels()!= null?(priceModel!=null?priceModel.getCurrency().getName():null):null),
                                (planItem.getModels()!= null?(priceModel!=null?priceModel.getAttributes():null):null)
                        }
           );
           }
        return values.toArray(new Object[values.size()][]);
    }
}

