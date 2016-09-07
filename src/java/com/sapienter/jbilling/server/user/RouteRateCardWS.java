package com.sapienter.jbilling.server.user;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.pricing.RouteRecordWS;
import com.sapienter.jbilling.server.security.MappedSecuredWS;
import com.sapienter.jbilling.server.security.WSSecured;
import com.sapienter.jbilling.server.util.NameValueString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RouteRateCardWS implements WSSecured, Serializable {

    private Integer id;
    private String name;
    private Integer entityId;
    private String tableName;

    @NotNull(message="validation.error.notnull")
    private Integer ratingUnitId;

    private NameValueString[] attributes = new NameValueString[0];

    public RouteRateCardWS() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    @Size(min = 1, message = "validation.error.notnull")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public NameValueString[] getAttributes() {
        return attributes;
    }

    public void setAttributes(NameValueString[] attributes) {
        this.attributes = attributes;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getRatingUnitId() {
        return ratingUnitId;
    }

    public void setRatingUnitId(Integer ratingUnitId) {
        this.ratingUnitId = ratingUnitId;
    }

    public void validate() {
        for(NameValueString nv : this.getAttributes()) {
            if(nv.getName().trim().length() == 0) {
                throw new SessionInternalError("RouteRateCard record field has empty name", new String[] {"RouteRateCardRecordWS,fields,route.record.validation.attr.no.name"});
            }
            if(nv.getValue().trim().length() > 0) {
                return;
            }
        }
        throw new SessionInternalError("RouteRateCard record can not have all empty values", new String[] {"RouteRateCardRecordWS,fields,route.record.validation.no.data"});
    }

    public Map<String, String> routeRateCardRecordToMap() {
        Map<String, String> result = new HashMap<String, String>(this.getAttributes().length * 2);
        if(this.getId() != null) {
            result.put("id", this.getId().toString());
        }
        result.put("name", this.getName());
        for(NameValueString nv: this.getAttributes()) {
            result.put(nv.getName(), nv.getValue());
        }
        return result;
    }


    @Override
    public String toString() {
        return "RouteRateCardWS{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", entityId=" + entityId +
                ", tableName='" + tableName + '\'' +
                '}';
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
}
