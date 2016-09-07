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

/*
 * Created on Dec 18, 2003
 *
 */
package com.sapienter.jbilling.server.user;

import com.sapienter.jbilling.common.Constants;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.payment.PaymentInformationWS;
import com.sapienter.jbilling.server.security.HierarchicalEntity;
import com.sapienter.jbilling.server.security.WSSecured;
import com.sapienter.jbilling.server.util.api.validation.CreateValidationGroup;
import com.sapienter.jbilling.server.util.api.validation.EntitySignupValidationGroup;
import com.sapienter.jbilling.server.util.api.validation.UpdateValidationGroup;
import com.sapienter.jbilling.server.util.cxf.CxfMapIntegerDateAdapter;
import com.sapienter.jbilling.server.util.cxf.CxfMapListDateAdapter;
import com.sapienter.jbilling.server.util.cxf.CxfMapMapListMetafieldAdapter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.lang.Integer;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/** @author Emil */
public class UserWS implements WSSecured, HierarchicalEntity, Serializable {

	@Min(value = 1, message = "validation.error.min,1", groups = UpdateValidationGroup.class)
    @Max(value = 0, message = "validation.error.max,0", groups = CreateValidationGroup.class)
    private int id;
    private Integer currencyId;
    @Pattern(regexp=Constants.PASSWORD_PATTERN_4_UNIQUE_CLASSES, message="validation.error.password.size,8,128", groups = {CreateValidationGroup.class, EntitySignupValidationGroup.class })
    private String password;
    private boolean createCredentials = false;
    private int deleted;
    private Date createDatetime;
    private Date lastStatusChange;
    private Date lastLogin;
    private boolean accountExpired;
    private Date accountDisabledDate;
    @NotNull(message="validation.error.notnull")
    @Size(min = 5, max = 50, message = "validation.error.size,5,50")
    @Pattern(regexp=Constants.USERNAME_PATTERN, message="validation.error.invalid.username", groups = {CreateValidationGroup.class, EntitySignupValidationGroup.class })
    private String userName;
    private int failedAttempts;
    private Integer languageId;

    @Valid
    private ContactWS contact = null;
    private String role = null;
    private String language = null;
    private String status = null;
    private Integer mainRoleId = null;
    private Integer statusId = null;
    private Integer subscriberStatusId = null;
    private Integer customerId = null;
    @Digits(integer = 12, fraction = 0, message= "validation.error.invalid.agentid")
    private Integer partnerRoleId = null;
    private Integer[] partnerIds = null;
    private Integer parentId = null;
    private Boolean isParent = null;
    private Boolean invoiceChild = null;
    private Boolean useParentPricing = null;
    private Boolean excludeAgeing = null;
    private String[] blacklistMatches = null;
    private Boolean userIdBlacklisted = null;
    private Integer[] childIds = null;
    private String owingBalance = null;
    private String dynamicBalance = null;
    @Digits(integer = 12, fraction = 10, message="validation.error.not.a.number")
    private String autoRecharge = null;
    @Digits(integer = 12, fraction = 10, message="validation.error.not.a.number")
    @Min(value = 0, message = "validation.error.not.a.positive.number")
    private String creditLimit = null;
	private String creditLimitNotification1 = null;
	private String creditLimitNotification2 = null;
    private String notes;
    private Integer automaticPaymentType;
    private String companyName;
    private Boolean isAccountLocked;

    private Integer invoiceDeliveryMethodId;
    private Integer dueDateUnitId;
    private Integer dueDateValue;
    private Date nextInvoiceDate;

    @Digits(integer = 12, fraction = 10, message="validation.error.not.a.number")
    private String rechargeThreshold = "-1";
    @Digits(integer = 12, fraction = 10, message="validation.error.not.a.number")
    private String monthlyLimit;
    private Integer entityId;

    @Valid
    private MetaFieldValueWS[] metaFields;

    @Valid
    private MainSubscriptionWS mainSubscription;
    private CustomerNoteWS[] customerNotes;

    private Integer accountTypeId;
    private String invoiceDesign;
    private Integer invoiceTemplateId;

    private Map<Integer, HashMap<Date, ArrayList<MetaFieldValueWS>>> accountInfoTypeFieldsMap = new HashMap<Integer, HashMap<Date, ArrayList<MetaFieldValueWS>>>();
    private Map<Integer, ArrayList<Date>> timelineDatesMap = new HashMap<Integer, ArrayList<Date>>(0);
    private Map<Integer, Date> effectiveDateMap = new HashMap<Integer, Date>(0);
    private Map<Integer, ArrayList<Date>> removedDatesMap = new HashMap<Integer, ArrayList<Date>>(0);
    
    //user codes of other users linked to this customer
    private String userCodeLink;
    // payment instruments
    private List<PaymentInformationWS> paymentInstruments = new ArrayList<PaymentInformationWS>();

    private List<Integer> accessEntities;

    public UserWS() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
	public Integer[] getPartnerIds() {
        return partnerIds;
    }

    public void setPartnerIds(Integer[] partnerIds) {
        this.partnerIds = partnerIds;
    }

    public Integer getPartnerRoleId() {
        return partnerRoleId;
    }

    public void setPartnerRoleId(Integer partnerRoleId) {
        this.partnerRoleId = partnerRoleId;
    }

    public String getUserCodeLink() {
        return userCodeLink;
    }

    public void setUserCodeLink(String userCodeLink) {
        this.userCodeLink = userCodeLink;
    }

    public ContactWS getContact() {
        return contact;
    }

    public void setContact(ContactWS contact) {
        this.contact = contact;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String type) {
        this.role = type;
    }

    public Integer getMainRoleId() {
        return mainRoleId;
    }

    public void setMainRoleId(Integer mainRoleId) {
        this.mainRoleId = mainRoleId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getSubscriberStatusId() {
        return subscriberStatusId;
    }

    public void setSubscriberStatusId(Integer subscriberStatusId) {
        this.subscriberStatusId = subscriberStatusId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(Boolean isParent) {
        this.isParent = isParent;
    }

    public Boolean getInvoiceChild() {
        return invoiceChild;
    }

    public void setInvoiceChild(Boolean invoiceChild) {
        this.invoiceChild = invoiceChild;
    }

    public Boolean getUseParentPricing() {
        return useParentPricing;
    }

    public Boolean useParentPricing() {
        return useParentPricing;
    }

    public void setUseParentPricing(Boolean useParentPricing) {
        this.useParentPricing = useParentPricing;
    }

    public Boolean getExcludeAgeing() {
        return excludeAgeing;
    }

    public void setExcludeAgeing(Boolean excludeAgeing) {
        this.excludeAgeing = excludeAgeing;
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public int getUserId() {
        return id;
    }

    public void setUserId(int id) {
        this.id = id;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isAccountExpired() { 
        return accountExpired; 
    }

    public void setAccountExpired(boolean accountExpired) { 
        this.accountExpired = accountExpired; 
    }

    public Date getAccountDisabledDate() { 
        return accountDisabledDate; 
    }

    public void setAccountDisabledDate(Date accountDisabledDate) { 
        this.accountDisabledDate = accountDisabledDate; 
    }

    public Date getLastStatusChange() {
        return lastStatusChange;
    }

    public void setLastStatusChange(Date lastStatusChange) {
        this.lastStatusChange = lastStatusChange;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String[] getBlacklistMatches() {
        return blacklistMatches;
    }

    public void setBlacklistMatches(String[] blacklistMatches) {
        this.blacklistMatches = blacklistMatches;
    }

    public Boolean getUserIdBlacklisted() {
        return userIdBlacklisted;
    }

    public void setUserIdBlacklisted(Boolean userIdBlacklisted) {
        this.userIdBlacklisted = userIdBlacklisted;
    }

    public Integer[] getChildIds() {
        return childIds;
    }

    public void setChildIds(Integer[] childIds) {
        this.childIds = childIds;
    }

    public String getOwingBalance() {
        return owingBalance;
    }

    public BigDecimal getOwingBalanceAsDecimal() {
        return Util.string2decimal(owingBalance);
    }

    public void setOwingBalance(String owingBalance) {
        this.owingBalance = owingBalance;
    }

    public void setOwingBalance(BigDecimal owingBalance) {
        this.owingBalance = (owingBalance != null ? owingBalance.toString() : null);
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public BigDecimal getCreditLimitAsDecimal() {
        return Util.string2decimal(creditLimit);
    }

    public void setCreditLimitAsDecimal(BigDecimal creditLimit) {
        setCreditLimit(creditLimit);
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = (creditLimit != null ? creditLimit.toPlainString() : null);
    }

	public String getCreditLimitNotification1() {
		return creditLimitNotification1;
	}

	public void setCreditLimitNotification1(String creditLimitNotification1) {
		this.creditLimitNotification1 = creditLimitNotification1;
	}

	public BigDecimal getCreditLimitNotification1AsDecimal() {
		return Util.string2decimal(creditLimitNotification1);
	}

	public void setCreditLimitNotification1(BigDecimal creditLimitNotification1) {
		this.creditLimitNotification1 = (null != creditLimitNotification1 ? creditLimitNotification1.toString() : null);
	}

	public String getCreditLimitNotification2() {
		return creditLimitNotification2;
	}

	public void setCreditLimitNotification2(String creditLimitNotification2) {
		this.creditLimitNotification2 = creditLimitNotification2;
	}

	public BigDecimal getCreditLimitNotification2AsDecimal() {
		return Util.string2decimal(creditLimitNotification2);
	}

	public void setCreditLimitNotification2(BigDecimal creditLimitNotification2) {
		this.creditLimitNotification2 = (null != creditLimitNotification2 ? creditLimitNotification2.toString() : null);
	}

	public String getDynamicBalance() {
        return dynamicBalance;
    }

    public BigDecimal getDynamicBalanceAsDecimal() {
        return Util.string2decimal(dynamicBalance);
    }

    public void setDynamicBalance(String dynamicBalance) {
        this.dynamicBalance = dynamicBalance;
    }

    public void setDynamicBalance(BigDecimal dynamicBalance) {
        this.dynamicBalance = (dynamicBalance != null ? dynamicBalance.toPlainString() : null);
    }

    public String getAutoRecharge() {
        return autoRecharge;
    }

    public BigDecimal getAutoRechargeAsDecimal() {
        return Util.string2decimal(autoRecharge);
    }

    public void setAutoRechargeAsDecimal(BigDecimal autoRecharge) {
        setAutoRecharge(autoRecharge);
    }

    public void setAutoRecharge(String autoRecharge) {
        this.autoRecharge = autoRecharge;
    }

    public void setAutoRecharge(BigDecimal autoRecharge) {
        this.autoRecharge = (autoRecharge != null ? autoRecharge.toString() : null);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getAutomaticPaymentType() {
        return automaticPaymentType;
    }

    public void setAutomaticPaymentType(Integer automaticPaymentType) {
        this.automaticPaymentType = automaticPaymentType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getInvoiceDeliveryMethodId() {
        return invoiceDeliveryMethodId;
    }

    public void setInvoiceDeliveryMethodId(Integer invoiceDeliveryMethodId) {
        this.invoiceDeliveryMethodId = invoiceDeliveryMethodId;
    }

    public Integer getDueDateUnitId() {
        return dueDateUnitId;
    }

    public void setDueDateUnitId(Integer dueDateUnitId) {
        this.dueDateUnitId = dueDateUnitId;
    }

    public Integer getDueDateValue() {
        return dueDateValue;
    }

    public void setDueDateValue(Integer dueDateValue) {
        this.dueDateValue = dueDateValue;
    }

    public Date getNextInvoiceDate() {
        return nextInvoiceDate;
    }

    public void setNextInvoiceDate(Date nextInvoiceDate) {
        this.nextInvoiceDate = nextInvoiceDate;
    }

    public MetaFieldValueWS[] getMetaFields() {
        return metaFields;
    }

    public void setMetaFields(MetaFieldValueWS[] metaFields) {
        this.metaFields = metaFields;
    }

    public MainSubscriptionWS getMainSubscription() {
		return mainSubscription;
	}

	public void setMainSubscription(MainSubscriptionWS mainSubscription) {
		this.mainSubscription = mainSubscription;
	}

    public CustomerNoteWS[] getCustomerNotes() {
        return customerNotes;
    }

    public void setCustomerNotes(CustomerNoteWS[] customerNotes) {
        this.customerNotes = customerNotes;
    }

    public Integer getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Integer accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public String getInvoiceDesign() {
        return invoiceDesign;
    }

    public void setInvoiceDesign(String invoiceDesign) {
        this.invoiceDesign = invoiceDesign;
    }

    public Integer getInvoiceTemplateId() {
        return invoiceTemplateId;
    }

    public void setInvoiceTemplateId(Integer invoiceTemplateId) {
        this.invoiceTemplateId = invoiceTemplateId;
    }

    @XmlJavaTypeAdapter(CxfMapListDateAdapter.class)
	public Map<Integer, ArrayList<Date>> getTimelineDatesMap() {
		return timelineDatesMap;
	}

	public void setTimelineDatesMap(Map<Integer, ArrayList<Date>> timelineDatesMap) {
		this.timelineDatesMap = timelineDatesMap;
	}

    @XmlJavaTypeAdapter(CxfMapIntegerDateAdapter.class)
	public Map<Integer, Date> getEffectiveDateMap() {
		return effectiveDateMap;
	}

	public void setEffectiveDateMap(Map<Integer, Date> effectiveDateMap) {
		this.effectiveDateMap = effectiveDateMap;
	}

    @XmlJavaTypeAdapter(CxfMapListDateAdapter.class)
	public Map<Integer, ArrayList<Date>> getRemovedDatesMap() {
		return removedDatesMap;
	}

	public void setRemovedDatesMap(Map<Integer, ArrayList<Date>> removedDatesMap) {
		this.removedDatesMap = removedDatesMap;
	}

    @XmlJavaTypeAdapter(CxfMapMapListMetafieldAdapter.class)
	public Map<Integer, HashMap<Date, ArrayList<MetaFieldValueWS>>> getAccountInfoTypeFieldsMap() {
		return accountInfoTypeFieldsMap;
	}
	
	public void setAccountInfoTypeFieldsMap(Map<Integer, HashMap<Date, ArrayList<MetaFieldValueWS>>> accountInfoTypeFieldsMap) {
		this.accountInfoTypeFieldsMap = accountInfoTypeFieldsMap;
	}
	
    public Boolean isAccountLocked() {
        return isAccountLocked;
    }

    public void setIsAccountLocked(Boolean isAccountLocked) {
        this.isAccountLocked = isAccountLocked;
    }

    public String getRechargeThreshold() {
        return rechargeThreshold;
    }

    public BigDecimal getRechargeThresholdAsDecimal() {
        return rechargeThreshold != null ? new BigDecimal(rechargeThreshold) : null;
    }

    public void setRechargeThreshold(String rechargeThreshold) {
        this.rechargeThreshold = rechargeThreshold;
    }

    public void setRechargeThreshold(BigDecimal rechargeThreshold) {
        this.rechargeThreshold = (rechargeThreshold != null ? rechargeThreshold.toString() : null);
    }

    public String getMonthlyLimit () {
        return monthlyLimit;
    }

    public BigDecimal getMonthlyLimitAsDecimal () {
        return monthlyLimit != null ? new BigDecimal(monthlyLimit) : null;
    }

    public void setMonthlyLimit (String monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public void setMonthlyLimit (BigDecimal monthlyLimit) {
        this.monthlyLimit = (monthlyLimit != null ? monthlyLimit.toString() : null);
    }

    /**
     * Unsupported, web-service security enforced using {@link #getOwningUserId()}
     *
     * @return null
     */
    public Integer getOwningEntityId() {
        return null;
    }

    public Integer getOwningUserId() {
        return getUserId();
    }

    /**
     * Returns the list of jBilling Entity IDs within a hierarchy that have access to this object.
     *
     * @return list of entities that have access.
     */
    @Override
    public List<Integer> getAccessEntities() {
        return this.accessEntities;
    }

    public void setAccessEntities(List<Integer> accessEntities) {
        this.accessEntities = accessEntities;
    }

    public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public List<PaymentInformationWS> getPaymentInstruments() {
		return paymentInstruments;
	}

	public void setPaymentInstruments(List<PaymentInformationWS> paymentInstruments) {
		this.paymentInstruments = paymentInstruments;
	}

    public boolean isCreateCredentials() {
        return createCredentials;
    }

    public void setCreateCredentials(boolean createCredentials) {
        this.createCredentials = createCredentials;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(", autoRecharge=");
        builder.append(autoRecharge);
        builder.append(", automaticPaymentType=");
        builder.append(automaticPaymentType);
        builder.append(", blacklistMatches=");
        builder.append(Arrays.toString(blacklistMatches));
        builder.append(", childIds=");
        builder.append(Arrays.toString(childIds));
        builder.append(", companyName=");
        builder.append(companyName);
        builder.append(", contact=");
        builder.append(contact);
        builder.append(", createDatetime=");
        builder.append(createDatetime);
        builder.append(", creditLimit=");
        builder.append(creditLimit);
        builder.append(", currencyId=");
        builder.append(currencyId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", deleted=");
        builder.append(deleted);
        builder.append(", dueDateUnitId=");
        builder.append(dueDateUnitId);
        builder.append(", dueDateValue=");
        builder.append(dueDateValue);
        builder.append(", dynamicBalance=");
        builder.append(dynamicBalance);
        builder.append(", excludeAgeing=");
        builder.append(excludeAgeing);
        builder.append(", failedAttempts=");
        builder.append(failedAttempts);
        builder.append(", id=");
        builder.append(id);
        builder.append(", invoiceChild=");
        builder.append(invoiceChild);
        builder.append(", invoiceDeliveryMethodId=");
        builder.append(invoiceDeliveryMethodId);
        builder.append(", isParent=");
        builder.append(isParent);
        builder.append(", language=");
        builder.append(language);
        builder.append(", languageId=");
        builder.append(languageId);
        builder.append(", lastLogin=");
        builder.append(lastLogin);
        builder.append(", lastStatusChange=");
        builder.append(lastStatusChange);
        builder.append(", mainRoleId=");
        builder.append(mainRoleId);
        builder.append(", nextInvoiceDate=");
        builder.append(nextInvoiceDate);
        builder.append(", notes=");
        builder.append(notes);
        builder.append(", owingBalance=");
        builder.append(owingBalance);
        builder.append(", parentId=");
        builder.append(parentId);
        builder.append(", partnerIds=");
        builder.append(Arrays.toString(partnerIds));
        builder.append(", role=");
        builder.append(role);
        builder.append(", status=");
        builder.append(status);
        builder.append(", statusId=");
        builder.append(statusId);
        builder.append(", subscriberStatusId=");
        builder.append(subscriberStatusId);
        builder.append(", userIdBlacklisted=");
        builder.append(userIdBlacklisted);
        builder.append(", userName=");
        builder.append(userName);
        builder.append(", accountTypeId=");
        builder.append(accountTypeId);
        builder.append(", invoiceDesign=");
        builder.append(invoiceDesign);
        builder.append(", invoiceTemplateId=");
        builder.append(invoiceTemplateId);
        builder.append(", userCodeLink=");
        builder.append(userCodeLink);
        builder.append(", isAccountLocked=");
        builder.append(isAccountLocked);
        builder.append(", accountExpired=");
        builder.append(accountExpired);
        builder.append(", accountDisabledDate=");
        builder.append(accountDisabledDate);
        builder.append(']');
        return builder.toString();
    }

    /**
     * Named differently to avoid name conflict with implementing entities.
     *
     * @return
     */
    @Override
    public Boolean ifGlobal() {
        return false;
    }

}
