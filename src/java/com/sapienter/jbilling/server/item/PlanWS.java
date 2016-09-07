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

package com.sapienter.jbilling.server.item;

import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.usagePool.UsagePoolWS;
import com.sapienter.jbilling.server.security.WSSecured;
import com.sapienter.jbilling.server.util.cxf.CxfSMapIntMetafieldsAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Brian Cowdery
 * @since 20-09-2010
 */
public class PlanWS implements WSSecured, Serializable {

    private Integer id;
    private Integer itemId; // plan subscription item
    private Integer periodId; // plan item period
    @Size (min=0,max=255, message="validation.error.size,1,255")
    private String description;
    private int editable = 0;
    private List<PlanItemWS> planItems = new ArrayList<PlanItemWS>();
    private Integer[] usagePoolIds;

    @Valid
    private MetaFieldValueWS[] metaFields;
    private SortedMap <Integer, MetaFieldValueWS[]> metaFieldsMap = new TreeMap<Integer, MetaFieldValueWS[]>();

    public PlanWS() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getPlanSubscriptionItemId() {
        return getItemId();
    }

    public void setPlanSubscriptionItemId(Integer planSubscriptionItemId) {
        setItemId(planSubscriptionItemId);
    }

    public Integer getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Integer periodId) {
        this.periodId = periodId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEditable() {
        return editable;
    }

    @XmlTransient
    public void setEditable(int editable) {
        this.editable = editable;
    }

   	public List<PlanItemWS> getPlanItems() {
        return planItems;
    }

    public void setPlanItems(List<PlanItemWS> planItems) {
        this.planItems = planItems;
    }

    public void addPlanItem(PlanItemWS planItem) {
        getPlanItems().add(planItem);
    }

    public MetaFieldValueWS[] getMetaFields() {
    	return metaFields;
    }

	public void setMetaFields(MetaFieldValueWS[] metaFields) {
    	this.metaFields = metaFields;
    }

    @XmlJavaTypeAdapter(CxfSMapIntMetafieldsAdapter.class)
    public SortedMap <Integer, MetaFieldValueWS[]> getMetaFieldsMap() {
		return metaFieldsMap;
	}
	
	public void setMetaFieldsMap(SortedMap <Integer, MetaFieldValueWS[]> metaFieldsMap) {
		this.metaFieldsMap = metaFieldsMap;
	}

	public Integer[] getUsagePoolIds() {
        return this.usagePoolIds;
    }

    public void setUsagePoolIds(Integer[] usagePoolIds) {
        this.usagePoolIds = usagePoolIds;
    }

    @Override
    public Integer getOwningEntityId() {
        return itemId != null ? new ItemBL(itemId).getEntity().getEntity().getId() : null;
    }

    @Override
    public Integer getOwningUserId() {
        return null;
    }

	@Override
    public String toString() {
		String strFUPIds = "";
		if (null != usagePoolIds && usagePoolIds.length > 0) {
			for (Integer fupId : usagePoolIds) {
				strFUPIds += fupId + ":";
			}
		}
        return "PlanWS{"
               + "id=" + id
               + ", itemId=" + itemId
               + ", periodId=" + periodId
               + ", description='" + description + '\''
                + ", editable=" + editable
               + ", planItems=" + planItems
               + ", usagePoolIds=" + strFUPIds
               + '}';
    }
}
