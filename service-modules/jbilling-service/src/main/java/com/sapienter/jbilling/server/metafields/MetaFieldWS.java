package com.sapienter.jbilling.server.metafields;

import com.sapienter.jbilling.server.metafields.validation.ValidationRuleWS;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import com.sapienter.jbilling.server.metafields.validation.ValidationRuleWS;
import com.sapienter.jbilling.server.security.WSSecured;
import org.hibernate.validator.constraints.ScriptAssert;

public class MetaFieldWS implements WSSecured, Serializable {

    private static final long serialVersionUID = -1889507849327464287L;

    private int id;
    private Integer entityId;

    @NotNull(message="validation.error.notnull")
    @Size(min = 1, max = 100, message = "validation.error.size,1,100")
    private String name;
    private Integer fakeId;
    private EntityType entityType;
    private DataType dataType;

    private boolean disabled = false;
    private boolean mandatory = false;

    private Integer displayOrder = 1;

    private Integer[] dependentMetaFields;
    Integer dataTableId;
    private String helpDescription;
    private String helpContentURL;


    @Valid
    private MetaFieldValueWS defaultValue = null;

    @Valid
    private ValidationRuleWS validationRule;

    //indicate whether the metafield is a primary field and can be used for creation of metafield groups and for providing
    //    a meta-fields to be populated for the entity type they belong to
    //Metafields created from the Configuration - MetaField menu will be considered as primary metafields by default. 
    //All other dynamic metafields created on the fly in the system (example: Account Information Type, Product Category) will not be considered as primary
    private boolean primary;
    private MetaFieldType fieldUsage;
    
    @Size(min = 0, max = 100, message = "validation.error.size,0,100")
    private String filename;
    
	public MetaFieldWS() {
	}


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public EntityType getEntityType() {
        return entityType;
    }


    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }


    public DataType getDataType() {
        return dataType;
    }


    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }


    public boolean isDisabled() {
        return disabled;
    }


    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }


    public boolean isMandatory() {
        return mandatory;
    }


    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }


    public Integer getDisplayOrder() {
        return displayOrder;
    }


    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }


    public MetaFieldValueWS getDefaultValue() {
        return defaultValue;
    }


    public void setDefaultValue(MetaFieldValueWS defaultValue) {
        this.defaultValue = defaultValue;
    }


    public boolean isPrimary() {
        return primary;
    }


    public void setPrimary(boolean primary) {
        this.primary = primary;
    }


    public ValidationRuleWS getValidationRule() {
        return validationRule;
    }


    public void setValidationRule(ValidationRuleWS validationRule) {
        this.validationRule = validationRule;
    }

    public MetaFieldType getFieldUsage() {
        return fieldUsage;
    }

    public void setFieldUsage(MetaFieldType fieldUsage) {
        this.fieldUsage = fieldUsage;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Returns the entity ID of the company owning the secure object, or null
     * if the entity ID is not available.
     *
     * @return owning entity ID
     */
    @Override
    public Integer getOwningEntityId() {
        return entityId;
    }

    /**
     * Returns the user ID of the user owning the secure object, or null if the
     * user ID is not available.
     *
     * @return owning user ID
     */
    @Override
    public Integer getOwningUserId() {
        return null;
    }
    
    public Integer[] getDependentMetaFields() {
        return dependentMetaFields;
    }

    public void setDependentMetaFields(Integer[] dependentMetaFields) {
        this.dependentMetaFields = dependentMetaFields;
    }

    public Integer getDataTableId() {
        return dataTableId;
    }

    public void setDataTableId(Integer dataTableId) {
        this.dataTableId = dataTableId;
    }

    public Integer getFakeId() {
        return fakeId;
    }

    public void setFakeId(Integer fakeId) {
        this.fakeId = fakeId;
    }

    public String getHelpDescription() {
        return helpDescription;
    }

    public void setHelpDescription(String helpDescription) {
        this.helpDescription = helpDescription;
    }

    public String getHelpContentURL() {
        return helpContentURL;
    }

    public void setHelpContentURL(String helpContentURL) {
        this.helpContentURL = helpContentURL;
    }
}
