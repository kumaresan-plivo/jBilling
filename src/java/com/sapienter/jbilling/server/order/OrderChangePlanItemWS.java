/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2014] Enterprise jBilling Software Ltd.
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
package com.sapienter.jbilling.server.order;

import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * @author: Alexander Aksenov
 * @since: 21.03.14
 */
public class OrderChangePlanItemWS implements Serializable {
    private int id;
    private int itemId;
    private String description;
    private int[] assetIds;
    @Valid
    private MetaFieldValueWS[] metaFields;
    private Integer optlock;

    /** Here for convenience. Not used by the server */
    private Integer bundledQuantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int[] getAssetIds() {
        return assetIds;
    }

    public void setAssetIds(int[] assetIds) {
        this.assetIds = assetIds;
    }

    public MetaFieldValueWS[] getMetaFields() {
        return metaFields;
    }

    public void setMetaFields(MetaFieldValueWS[] metaFields) {
        this.metaFields = metaFields;
    }

    public Integer getBundledQuantity() {
        return bundledQuantity;
    }

    public void setBundledQuantity(Integer bundledQuantity) {
        this.bundledQuantity = bundledQuantity;
    }

    public Integer getOptlock() {
        return optlock;
    }

    public void setOptlock(Integer optlock) {
        this.optlock = optlock;
    }
}