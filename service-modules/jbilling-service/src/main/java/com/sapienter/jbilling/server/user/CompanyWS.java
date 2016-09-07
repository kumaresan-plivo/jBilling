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
package com.sapienter.jbilling.server.user;

import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;

import com.sapienter.jbilling.server.security.WSSecured;

import javax.validation.Valid;
import javax.validation.constraints.Size;

public class CompanyWS implements java.io.Serializable, WSSecured {

    private static final long serialVersionUID = 20140605L;

    private int id;
    private Integer currencyId;
    private Integer languageId;
    @Size(min = 5, max = 100, message = "validation.error.size,5,100")
    private String description;
    @Valid
    private ContactWS contact;
    private Integer owningEntityId;
    
    private String customerInformationDesign;
    private Integer uiColor;
    @Valid
    private MetaFieldValueWS[] metaFields;

    public CompanyWS() {
    }

    public CompanyWS(int i) {
        id = i;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContactWS getContact() {
        return contact;
    }

    public void setContact(ContactWS contact) {
        this.contact = contact;
    }

    public String getCustomerInformationDesign() {
        return customerInformationDesign;
    }

    public void setCustomerInformationDesign(String customerInformationDesign) {
        this.customerInformationDesign = customerInformationDesign;
    }

    public Integer getUiColor() {
        return uiColor;
    }

    public void setUiColor(Integer uiColor) {
        this.uiColor = uiColor;
    }

    public MetaFieldValueWS[] getMetaFields() {
        return metaFields;
    }

    public void setMetaFields(MetaFieldValueWS[] metaFields) {
        this.metaFields = metaFields;
    }

    public String toString() {
        return "CompanyWS [id=" + id + ", currencyId=" + currencyId
                + ", languageId=" + languageId + ", description=" + description
                + ", contact=" + contact + "]";
    }

    @Override
    public Integer getOwningEntityId() {
        return this.owningEntityId;
    }

    @Override
    public Integer getOwningUserId() {
        return null;
    }
}