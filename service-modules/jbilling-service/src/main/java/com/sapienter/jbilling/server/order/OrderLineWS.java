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

package com.sapienter.jbilling.server.order;

import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.provisioning.ProvisioningCommandWS;
import com.sapienter.jbilling.server.util.api.validation.CreateValidationGroup;
import com.sapienter.jbilling.server.util.api.validation.UpdateValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Emil
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderLineWS implements Serializable {

    private static final long serialVersionUID = 20130704L;

    private int id;
    private Integer orderId;
    private String amount; // use strings instead of BigDecimal for WS compatibility
    @NotNull(message = "validation.error.null.quantity")
    @Digits(integer = 12, fraction = 10, message="validation.error.not.a.number", groups = {CreateValidationGroup.class, UpdateValidationGroup.class} )
    private String quantity;
    @Digits(integer = 12, fraction = 10, message="validation.error.not.a.number", groups = {CreateValidationGroup.class, UpdateValidationGroup.class} )
    private String price;
    private Date createDatetime;
    private int deleted;
    private String description;
    private Integer versionNum;
    private Boolean editable = null;
    private Integer[] assetIds;
    private Integer[] assetAssignmentIds;
    private String sipUri;

    @Valid
    private MetaFieldValueWS[] metaFields;
    
    //provisioning fields
    private Integer provisioningStatusId;
    private String provisioningRequestId;


    // other fields, non-persistent
    private String priceStr = null;
    private Integer typeId = null;
    private Boolean useItem = null;
    @NotNull(message = "validation.error.missing.item.id", groups = {UpdateValidationGroup.class})
    private Integer itemId = null;
    private OrderLineUsagePoolWS[] orderLineUsagePools=null;

    private Date startDate;
    private Date endDate;

    private String productCode;
    @Valid
    @XmlAttribute(name = "parentLineId")
    @XmlIDREF
    private OrderLineWS parentLine;

    @Valid
    @XmlElement(name="childLine")
    @XmlIDREF
    private OrderLineWS[] childLines;

    @XmlAttribute @XmlID
    private String objectId;
    private boolean isPercentage =false;
    
    public String getObjectId() {
        return objectId;
    }

    /**
     * Verifone - AdjustedPrice after applying line level discount.
     * Only for display
     */
    private String adjustedPrice;

    private ProvisioningCommandWS provisioningCommands[];
    
	public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    
    public OrderLineUsagePoolWS[] getOrderLineUsagePools() {
        return orderLineUsagePools;
    }

    public void setOrderLineUsagePools(OrderLineUsagePoolWS[] orderLineUsagePools) {
        this.orderLineUsagePools = orderLineUsagePools;
    }

	public OrderLineWS() {
        objectId = UUID.randomUUID().toString();
    }

    public OrderLineWS(Integer id, Integer itemId, String description, BigDecimal amount, BigDecimal quantity,
                       BigDecimal price,
                       Date create, Integer deleted, Integer newTypeId, Boolean editable, Integer orderId,
                       Boolean useItem, Integer version, Integer provisioningStatusId, String provisioningRequestId, 
                       String productCode, Integer[] assetIds, MetaFieldValueWS[] metaFields, 
                       String sipUri) {
        setId(id);
        setItemId(itemId);
        setDescription(description);
        setAmount(amount);
        setQuantity(quantity);
        setPrice(price);
        setCreateDatetime(create);
        setDeleted(deleted);
        setTypeId(newTypeId);
        setEditable(editable);
        setOrderId(orderId);
        setUseItem(useItem);
        setVersionNum(version);
        setProvisioningStatusId(provisioningStatusId);
        setProvisioningRequestId(provisioningRequestId);
        setAssetIds(assetIds);
        setSipUri(sipUri);
        setProductCode(productCode);
        setMetaFields(metaFields);
        objectId = UUID.randomUUID().toString();
    }


    public OrderLineWS(Integer id, Integer itemId, String description, BigDecimal amount, BigDecimal quantity,
                       BigDecimal price,
                       Date create, Integer deleted, Integer newTypeId, Boolean editable, Integer orderId,
                       Boolean useItem, Integer version, Integer provisioningStatusId, String provisioningRequestId, 
                       OrderLineUsagePoolWS[] orderLineUsagePools,
                       String productCode, Integer[] assetIds, MetaFieldValueWS[] metaFields, String sipUri, boolean isPercentage) {
        setId(id);
        setItemId(itemId);
        setDescription(description);
        setAmount(amount);
        setQuantity(quantity);
        setPrice(price);
        setCreateDatetime(create);
        setDeleted(deleted);
        setTypeId(newTypeId);
        setEditable(editable);
        setOrderId(orderId);
        setUseItem(useItem);
        setVersionNum(version);
        setProvisioningStatusId(provisioningStatusId);
        setProvisioningRequestId(provisioningRequestId);
        setAssetIds(assetIds);
        setSipUri(sipUri);
        setProductCode(productCode);
        setOrderLineUsagePools(orderLineUsagePools);
        setMetaFields(metaFields);
        objectId = UUID.randomUUID().toString();
        setProductCode(productCode);
        setPercentage(isPercentage);
    }

    public OrderLineWS(Integer id, Integer itemId, String description, BigDecimal amount, BigDecimal quantity,
            BigDecimal price,
            Date create, Integer deleted, Integer newTypeId, Boolean editable, Integer orderId,
            Boolean useItem, Integer version, Integer provisioningStatusId, String provisioningRequestId) {
		setId(id);
		setItemId(itemId);
		setDescription(description);
		setAmount(amount);
		setQuantity(quantity);
		setPrice(price);
		setCreateDatetime(create);
		setDeleted(deleted);
		setTypeId(newTypeId);
		setEditable(editable);
		setOrderId(orderId);
		setUseItem(useItem);
		setVersionNum(version);
		setProvisioningStatusId(provisioningStatusId);
		setProvisioningRequestId(provisioningRequestId);
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Boolean getUseItem() {
        return useItem == null ? new Boolean(false) : useItem;
    }

    public void setUseItem(Boolean useItem) {
        this.useItem = useItem;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getAmount() {
        return amount;
    }

    public BigDecimal getAmountAsDecimal() {
        return Util.string2decimal(amount);
    }

    public void setAmountAsDecimal(BigDecimal amount) {
        setAmount(amount);
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = (amount != null ? amount.toString() : null);
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

   
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getPrice() {
        return price;
    }

    public BigDecimal getPriceAsDecimal() {
        return Util.string2decimal(price);
    }

    public void setPriceAsDecimal(BigDecimal price) {
        setPrice(price);
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPrice(BigDecimal price) {
        this.price = (price != null ? price.toString() : null);
    }

    public String getPriceStr() {
        return priceStr;
    }

    public String getQuantity() {
        return quantity;
    }

    public BigDecimal getQuantityAsDecimal() {
        return Util.string2decimal(quantity);
    }

    public void setQuantityAsDecimal(BigDecimal quantity) {
        setQuantity(quantity);
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(Integer quantity) {
        setQuantity(new BigDecimal(quantity));
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = (quantity != null ? quantity.toString() : null);
    }

    public Integer getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
    }

    public String getSipUri () {
        return sipUri;
    }

    public void setSipUri (String sipUri) {
        this.sipUri = sipUri;
    }

    /**
     * @return the provisioningStatusId
     */
    public Integer getProvisioningStatusId() {
        return provisioningStatusId;
    }

    /**
     * @param provisioningStatusId the provisioningStatusId to set
     */
    public void setProvisioningStatusId(Integer provisioningStatusId) {
        this.provisioningStatusId = provisioningStatusId;
    }

    public boolean hasAssets() {
        return assetIds != null && assetIds.length > 0;
    }

    public Integer[] getAssetIds() {
        return assetIds;
    }

    public void setAssetIds(Integer[] assetIds) {
        this.assetIds = assetIds;
    }

    public void removeAsset(Integer id) {
        List ids = new ArrayList(Arrays.asList(assetIds));
        ids.remove(id);
        assetIds = (Integer[]) ids.toArray(new Integer[ids.size()]);
    }

    public void removeAllAssets(Integer id) {
        assetIds =  new Integer[0];
        quantity = "0";
    }

	public Integer[] getAssetAssignmentIds() {
		return assetAssignmentIds;
	}

	public void setAssetAssignmentIds(Integer[] assetAssignmentIds) {
		this.assetAssignmentIds = assetAssignmentIds;
	}

    public MetaFieldValueWS[] getMetaFields() {
        return metaFields;
    }

    public void setMetaFields(MetaFieldValueWS[] metaFields) {
        this.metaFields = metaFields;
    }

    public ProvisioningCommandWS[] getProvisioningCommands() {
        return provisioningCommands;
    }

    public void setProvisioningCommands(ProvisioningCommandWS[] provisioningCommands) {
        this.provisioningCommands = provisioningCommands;
    }

	/**
	 * @return the provisioningRequestId
	 */
	public String getProvisioningRequestId() {
	    return provisioningRequestId;
	}
	
	/**
	 * @param provisioningRequestId the provisioningRequestId to set
	 */
	public void setProvisioningRequestId(String provisioningRequestId) {
	    this.provisioningRequestId = provisioningRequestId;
	}

    /**
     * Renturns true if the line has assets assigned to it
     * @return
     */
    public boolean hasLinkedAssets() {
        return (assetIds != null && assetIds.length > 0);
    }

    public String getProductCode () {
        return productCode;
    }

    public void setProductCode (String productCode) {
        this.productCode = productCode;
    }
    
    public String getAdjustedPrice() {
        return adjustedPrice;
    }

    public void setAdjustedPrice(String adjustedPrice) {
        this.adjustedPrice = adjustedPrice;
    }
    
    public void setAdjustedPrice(BigDecimal adjustedPrice) {
        this.adjustedPrice = (adjustedPrice != null ? adjustedPrice.toString() : null);
    }
    
    public BigDecimal getAdjustedPriceAsDecimal() {
        return Util.string2decimal(adjustedPrice);
    }

    public OrderLineWS getParentLine() {
        return parentLine;
    }

    public void setParentLine(OrderLineWS parentLine) {
        this.parentLine = parentLine;
    }

    public OrderLineWS[] getChildLines() {
        return childLines;
    }

    public void setChildLines(OrderLineWS[] childLines) {
        this.childLines = childLines;
    }

    public boolean isPercentage() {
		return isPercentage;
	}

	public void setPercentage(boolean isPercentage) {
		this.isPercentage = isPercentage;
	}

    
    @Override public String toString() {

        return "OrderLineWS{"
               + "id=" + id
               +", orderLineUsagePools="+orderLineUsagePools
               + ", amount='" + amount + '\''
               + ", quantity='" + quantity + '\''
               + ", price='" + price + '\''
               + ", deleted=" + deleted
               + ", description='" + description + '\''
               + ", useItem=" + useItem
               + ", isPercentage=" + isPercentage
               + ", itemId=" + itemId
               + ", typeId=" + typeId
               + ", parentLineId=" + (parentLine != null ? parentLine.getId() : null)
               + ", metaFields=" + ((metaFields == null) ? "null" : Arrays.asList(metaFields))
               + '}';
    }

}
