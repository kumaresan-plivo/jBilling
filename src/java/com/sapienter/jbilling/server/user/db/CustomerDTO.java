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


import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.audit.Auditable;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.invoice.InvoiceTemplateDAS;
import com.sapienter.jbilling.server.invoice.InvoiceTemplateDTO;
import com.sapienter.jbilling.server.metafields.*;

import com.sapienter.jbilling.server.invoice.db.InvoiceDeliveryMethodDAS;
import com.sapienter.jbilling.server.invoice.db.InvoiceDeliveryMethodDTO;
import com.sapienter.jbilling.server.metafields.db.GroupCustomizedEntity;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.usagePool.db.CustomerUsagePoolDTO;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.UserCodeAssociate;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.user.partner.db.PartnerDTO;
import com.sapienter.jbilling.server.user.partner.db.PartnerDAS;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import java.math.BigDecimal;
import java.util.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@TableGenerator(
        name="customer_GEN",
        table="jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue="customer",
        allocationSize = 100
)
// No cache, mutable and critical
@Table(name="customer")
public class CustomerDTO extends GroupCustomizedEntity
        implements java.io.Serializable, UserCodeAssociate<UserCodeCustomerLinkDTO>, Auditable {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(CustomerDTO.class));

    private int id;
    private UserDTO baseUser;
    private InvoiceDeliveryMethodDTO invoiceDeliveryMethod;
    private Set<PartnerDTO> partners;
    private Integer referralFeePaid;
    private Integer autoPaymentType;
    private Integer dueDateUnitId;
    private Integer dueDateValue;
    private Integer dfFm;
    private CustomerDTO parent;
    private Set<CustomerDTO> children = new HashSet<CustomerDTO>(0);
    private Set<CustomerNoteDTO> customerNotes = new HashSet<CustomerNoteDTO>(0);
    private Integer isParent;
    private int excludeAging;
    private Integer invoiceChild;
    private boolean useParentPricing = false;
    private BigDecimal dynamicBalance;
    private BigDecimal autoRecharge;
    private BigDecimal creditLimit;
    private BigDecimal creditNotificationLimit1;
    private BigDecimal creditNotificationLimit2;
    private int versionNum;
    private Date lastInvoiceDate;
    private Set<UserCodeCustomerLinkDTO> userCodeLinks = new HashSet<UserCodeCustomerLinkDTO>(0);
    private List<CustomerUsagePoolDTO> customerUsagePools = new ArrayList<CustomerUsagePoolDTO>(0);
    private Date nextInvoiceDate;
    
    private MainSubscriptionDTO mainSubscription;
    private AccountTypeDTO accountType;
    private String invoiceDesign;
    private InvoiceTemplateDTO invoiceTemplate;

    private Set<CustomerAccountInfoTypeMetaField> customerAccountInfoTypeMetaFields = new HashSet<CustomerAccountInfoTypeMetaField>();
	private Map<Integer, List<MetaFieldValue>> aitMetaFieldMap= new HashMap<Integer, List<MetaFieldValue>>(0);

    //#4501 - custom auto recharge
    private BigDecimal rechargeThreshold;
    private BigDecimal monthlyLimit;
    private BigDecimal currentMonthlyAmount;
    private Date currentMonth;
    
    public CustomerDTO() {
    }

    public CustomerDTO(int id) {
        this.id = id;
    }

    public CustomerDTO(int id, InvoiceDeliveryMethodDTO invoiceDeliveryMethod, int excludeAging) {
        this.id = id;
        this.invoiceDeliveryMethod = invoiceDeliveryMethod;
        this.excludeAging = excludeAging;
    }

    public CustomerDTO(int id, UserDTO baseUser, InvoiceDeliveryMethodDTO invoiceDeliveryMethod, Set<PartnerDTO> partners,
                       Integer referralFeePaid, Integer autoPaymentType, Integer dueDateUnitId,
                       Integer dueDateValue, Integer dfFm, CustomerDTO parent, Integer isParent, int excludeAging,
                       Integer invoiceChild, MainSubscriptionDTO mainSubscription, AccountTypeDTO accountType,
                       String invoiceDesign, Date nextInvoiceDate, InvoiceTemplateDTO invoiceTemplate) {
       this.id = id;
       this.baseUser = baseUser;
       this.invoiceDeliveryMethod = invoiceDeliveryMethod;
       this.partners = partners;
       this.referralFeePaid = referralFeePaid;
       this.autoPaymentType = autoPaymentType;
       this.dueDateUnitId = dueDateUnitId;
       this.dueDateValue = dueDateValue;
       this.dfFm = dfFm;
       this.parent = parent;
       this.isParent = isParent;
       this.excludeAging = excludeAging;
       this.invoiceChild = invoiceChild;
       this.mainSubscription = mainSubscription;
       this.accountType = accountType;
       this.invoiceDesign = invoiceDesign;
       this.nextInvoiceDate = nextInvoiceDate;
       this.invoiceTemplate = invoiceTemplate;
    }

    public CustomerDTO(Integer entityId, UserWS user) {
        setBaseUser(new UserDAS().find(user.getUserId()));

        if (user.getPartnerIds() != null) {
            setPartners(new HashSet<>(new PartnerDAS().findAllByIdInList(Arrays.asList(user.getPartnerIds()))));
        }

        if (user.getParentId() != null) {
            setParent(new CustomerDTO(user.getParentId()));
        }

        if (user.getIsParent() != null) {
            setIsParent(user.getIsParent().booleanValue() ? 1 : 0);
        }

        if (user.getInvoiceChild() != null) {
            setInvoiceChild(user.getInvoiceChild() ? 1 : 0);
        }

        if (user.getUseParentPricing() != null) {
            setUseParentPricing(user.useParentPricing());
        }

        if (user.getInvoiceDeliveryMethodId() != null) {
            InvoiceDeliveryMethodDTO deliveryMethod = new InvoiceDeliveryMethodDAS().find(user.getInvoiceDeliveryMethodId());
            setInvoiceDeliveryMethod(deliveryMethod);
        }

        setMainSubscription(UserBL.convertMainSubscriptionFromWS(user.getMainSubscription(), entityId));

        setCreditLimit(user.getCreditLimit() == null ? null : new BigDecimal(user.getCreditLimit()));
        setDynamicBalance(user.getDynamicBalance() == null ? null : new BigDecimal(user.getDynamicBalance()));
        setAutoRecharge(user.getAutoRecharge() == null ? null : new BigDecimal(user.getAutoRecharge()));

	    setCreditNotificationLimit1(user.getCreditLimitNotification1AsDecimal());
	    setCreditNotificationLimit2(user.getCreditLimitNotification2AsDecimal());

        setAutoPaymentType(user.getAutomaticPaymentType());

        setDueDateUnitId(user.getDueDateUnitId());
        setDueDateValue(user.getDueDateValue());
        
        setNextInvoiceDate(user.getNextInvoiceDate());

        setExcludeAging(user.getExcludeAgeing() != null && user.getExcludeAgeing() ? 1 : 0);
        MetaFieldBL.fillMetaFieldsFromWS(entityId, this, user.getMetaFields());

        if (null != user.getAccountTypeId()) {
            AccountTypeDTO accountType = new AccountTypeDAS().find(user.getAccountTypeId());
            setAccountType(accountType);
        }

        setInvoiceDesign(user.getInvoiceDesign());
        setInvoiceTemplate(new InvoiceTemplateDAS().find(user.getInvoiceTemplateId()));

        if(user.getUserCodeLink() != null && user.getUserCodeLink().length() > 0) {
            UserBL userBL = new UserBL();
            UserCodeDTO userCodeDTO = userBL.findUserCodeForIdentifier(user.getUserCodeLink(), entityId);
            if(userCodeDTO == null) {
                throw new SessionInternalError("User code not found:"+user.getUserCodeLink(), new String[] {"UserWS,linkedUserCodes,validation.error.userCode.not.exist,"+user.getUserCodeLink()});
            }
            getUserCodeLinks().add(new UserCodeCustomerLinkDTO(userCodeDTO, this));
        }

        setRechargeThreshold(user.getRechargeThresholdAsDecimal());
        setMonthlyLimit(user.getMonthlyLimitAsDecimal());

        LOG.debug("Customer created with auto-recharge: " + getAutoRecharge() + " incoming var, " + user.getAutoRecharge());
    }

    @Id @GeneratedValue(strategy = GenerationType.TABLE, generator = "customer_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public UserDTO getBaseUser() {
        return this.baseUser;
    }

    public void setBaseUser(UserDTO baseUser) {
        this.baseUser = baseUser;
    }
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    public Set<CustomerNoteDTO> getCustomerNotes() {
        return customerNotes;
    }

    public void setCustomerNotes(Set<CustomerNoteDTO> customerNotes) {
        this.customerNotes = customerNotes;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_delivery_method_id", nullable = false)
    public InvoiceDeliveryMethodDTO getInvoiceDeliveryMethod() {
        return this.invoiceDeliveryMethod;
    }

    public void setInvoiceDeliveryMethod(InvoiceDeliveryMethodDTO invoiceDeliveryMethod) {
        this.invoiceDeliveryMethod = invoiceDeliveryMethod;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "customer_partner_map", joinColumns = {
            @JoinColumn(name = "customer_id", updatable = true) }, inverseJoinColumns = {
            @JoinColumn(name = "partner_id", updatable = true) })
    public Set<PartnerDTO> getPartners() {
        if(partners == null) {
            setPartners(new HashSet<>());
        }
        return this.partners;
    }

    public void setPartners(Set<PartnerDTO> partners) {
        this.partners = partners;
    }

    @Column(name = "referral_fee_paid")
    public Integer getReferralFeePaid() {
        return this.referralFeePaid;
    }

    public void setReferralFeePaid(Integer referralFeePaid) {
        this.referralFeePaid = referralFeePaid;
    }

    @Column(name = "auto_payment_type")
    public Integer getAutoPaymentType() {
        return this.autoPaymentType;
    }

    public void setAutoPaymentType(Integer autoPaymentType) {
        this.autoPaymentType = autoPaymentType;
    }

    @Column(name = "due_date_unit_id")
    public Integer getDueDateUnitId() {
        return this.dueDateUnitId;
    }

    public void setDueDateUnitId(Integer dueDateUnitId) {
        this.dueDateUnitId = dueDateUnitId;
    }

    @Column(name = "due_date_value")
    public Integer getDueDateValue() {
        return this.dueDateValue;
    }

    public void setDueDateValue(Integer dueDateValue) {
        this.dueDateValue = dueDateValue;
    }

    @Column(name = "df_fm")
    public Integer getDfFm() {
        return this.dfFm;
    }

    public void setDfFm(Integer dfFm) {
        this.dfFm = dfFm;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    public Set<CustomerDTO> getChildren() {
        return children;
    }

    public void setChildren(Set<CustomerDTO> children) {
        this.children = children;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public Set<UserCodeCustomerLinkDTO> getUserCodeLinks() {
        return userCodeLinks;
    }

    public void setUserCodeLinks(Set<UserCodeCustomerLinkDTO> userCodes) {
        this.userCodeLinks = userCodes;
    }

    public void addUserCodeLinks(Collection<UserCodeCustomerLinkDTO> userCodeLinks) {
        if(userCodeLinks != null) {
            for(UserCodeCustomerLinkDTO link : userCodeLinks) {
                addUserCodeLink(link);
            }
        }
    }

    public void addUserCodeLink(UserCodeCustomerLinkDTO userCodeLink) {
        userCodeLink.setCustomer(this);
        userCodeLinks.add(userCodeLink);
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    public CustomerDTO getParent() {
        return this.parent;
    }

    public void setParent(CustomerDTO parent) {
        this.parent = parent;
    }

    @Column(name = "is_parent")
    public Integer getIsParent() {
        return this.isParent;
    }

    public void setIsParent(Integer isParent) {
        this.isParent = isParent;
    }

    @Column(name = "exclude_aging", nullable = false)
    public int getExcludeAging() {
        return this.excludeAging;
    }

    public void setExcludeAging(int excludeAging) {
        this.excludeAging = excludeAging;
    }

    @Column(name = "invoice_child")
    public Integer getInvoiceChild() {
        return this.invoiceChild;
    }

    public void setInvoiceChild(Integer invoiceChild) {
        this.invoiceChild = invoiceChild;
    }

    @Column(name = "use_parent_pricing", nullable = false)
    public boolean getUseParentPricing() {
        return useParentPricing;
    }

    public boolean useParentPricing() {
        return useParentPricing;
    }

    public void setUseParentPricing(boolean useParentPricing) {
        this.useParentPricing = useParentPricing;
    }

    @Column(name = "auto_recharge")
    public BigDecimal getAutoRecharge() {
        if (autoRecharge == null)
            return BigDecimal.ZERO;

        return autoRecharge;
    }

    public void setAutoRecharge(BigDecimal autoRecharge) {
        this.autoRecharge = autoRecharge;
    }

    @Column(name = "credit_limit")
    public BigDecimal getCreditLimit() {
        if (creditLimit == null) {
            return BigDecimal.ZERO;
        }
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Column(name = "credit_notification_limit1")
    public BigDecimal getCreditNotificationLimit1() {
        return creditNotificationLimit1;
    }

    public void setCreditNotificationLimit1(BigDecimal creditNotificationLimit1) {
        this.creditNotificationLimit1 = creditNotificationLimit1;
    }

    @Column(name = "credit_notification_limit2")
    public BigDecimal getCreditNotificationLimit2() {
        return creditNotificationLimit2;
    }

    public void setCreditNotificationLimit2(BigDecimal creditNotificationLimit2) {
        this.creditNotificationLimit2 = creditNotificationLimit2;
    }

    @Transient
    public Set<CustomerPriceDTO> getPrices() {
        return getBaseUser().getPrices();
    }

    public void setPrices(Set<CustomerPriceDTO> prices) {
        this.getBaseUser().setPrices(prices);
    }

    @Column(name = "dynamic_balance")
    public BigDecimal getDynamicBalance() {
        if (dynamicBalance == null) {
            return BigDecimal.ZERO;
        }
        return dynamicBalance;
    }

    public void setDynamicBalance(BigDecimal dynamicBalance) {
        this.dynamicBalance = dynamicBalance;
    }

    @Embedded
    public MainSubscriptionDTO getMainSubscription() {
		return mainSubscription;
	}

	public void setMainSubscription(MainSubscriptionDTO mainSubscription) {
		this.mainSubscription = mainSubscription;
	}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_type_id", nullable = true)
    public AccountTypeDTO getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountTypeDTO accountType) {
        this.accountType = accountType;
    }

    @Column(name = "invoice_design", length = 100)
    public String getInvoiceDesign() {
        return invoiceDesign;
    }

    public void setInvoiceDesign(String invoiceDesign) {
        this.invoiceDesign = invoiceDesign;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_template_id", nullable = true)
    public InvoiceTemplateDTO getInvoiceTemplate() {
        return invoiceTemplate;
    }

    public void setInvoiceTemplate(InvoiceTemplateDTO invoiceTemplate) {
        this.invoiceTemplate = invoiceTemplate;
    }

    @Version
    @Column(name = "OPTLOCK")
    public Integer getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
    }
    
    @Column(name="next_inovice_date", length=29)
    public Date getNextInvoiceDate() {
        return this.nextInvoiceDate;
    }

    public void setNextInvoiceDate(Date nextInvoiceDate) {
        this.nextInvoiceDate = nextInvoiceDate;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinTable(
            name = "customer_meta_field_map",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "meta_field_value_id")
    )
    @Sort(type = SortType.COMPARATOR, comparator = MetaFieldHelper.MetaFieldValuesOrderComparator.class)
    public List<MetaFieldValue> getMetaFields() {
        return getMetaFieldsList();
    }

    @Column(name = "recharge_threshold", nullable = true)
    public BigDecimal getRechargeThreshold () {
        return rechargeThreshold;
    }

    public void setRechargeThreshold (BigDecimal rechargeThreshold) {
        this.rechargeThreshold = rechargeThreshold;
    }

    @Column(name = "monthly_limit", nullable = true)
    public BigDecimal getMonthlyLimit () {
        return monthlyLimit;
    }

    public void setMonthlyLimit (BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    @Column(name = "current_monthly_amount", nullable = true)
    public BigDecimal getCurrentMonthlyAmount () {
        return currentMonthlyAmount;
    }

    public void setCurrentMonthlyAmount (BigDecimal currentMonthlyAmount) {
        this.currentMonthlyAmount = currentMonthlyAmount;
    }

    @Column(name = "current_month", nullable = true)
    public Date getCurrentMonth () {
        return currentMonth;
    }

    public void setCurrentMonth (Date currentMonth) {
        this.currentMonth = currentMonth;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public Set<CustomerAccountInfoTypeMetaField> getCustomerAccountInfoTypeMetaFields() {
        return this.customerAccountInfoTypeMetaFields;
    }
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "customer")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public List<CustomerUsagePoolDTO> getCustomerUsagePools() {
        return customerUsagePools;
    }

    public void setCustomerUsagePools(List<CustomerUsagePoolDTO> customerUsagePools) {
        this.customerUsagePools = customerUsagePools;
    }
    
    @Transient
    public boolean hasCustomerUsagePools() {
    	return null != getCustomerUsagePools() && !getCustomerUsagePools().isEmpty();
    }
     
    @Transient
    public void addCustomerAccountInfoTypeMetaField(MetaFieldValue value, AccountInformationTypeDTO accountInfoType, Date effectiveDate) {
    	CustomerAccountInfoTypeMetaField existed = getCustomerAccountInfoTypeMetaField(value.getField().getName(), accountInfoType.getId(), effectiveDate);
    	if(existed != null) {
    		existed.getMetaFieldValue().setValue(value.getValue());
    	} else {
    		CustomerAccountInfoTypeMetaField metaField = new CustomerAccountInfoTypeMetaField(this, accountInfoType, value, effectiveDate);
    		this.customerAccountInfoTypeMetaFields.add(metaField);
    	}
    }
    
    @Transient
    public void insertCustomerAccountInfoTypeMetaField(MetaFieldValue value, AccountInformationTypeDTO accountInfoType, Date effectiveDate) {
      CustomerAccountInfoTypeMetaField existed = getCustomerAccountInfoTypeMetaField(value.getField().getName(), accountInfoType.getId(), effectiveDate);
      if(existed == null) {
        CustomerAccountInfoTypeMetaField metaField = new CustomerAccountInfoTypeMetaField(this, accountInfoType, value, effectiveDate);
        this.customerAccountInfoTypeMetaFields.add(metaField);
      }
    }

    @Transient
    public void removeCustomerAccountInfoTypeMetaFields(Integer groupId, Date effectiveDate) {
    	for(CustomerAccountInfoTypeMetaField existed : getCustomerAccountInfoTypeMetaFields(groupId, effectiveDate)) {
    		this.customerAccountInfoTypeMetaFields.remove(existed);
    	}
    }
    
    @Transient
    public CustomerAccountInfoTypeMetaField getCustomerAccountInfoTypeMetaField(String name, Integer groupId, Date effectiveDate) {
        for (CustomerAccountInfoTypeMetaField infoTypeMetaField : customerAccountInfoTypeMetaFields) {
        	if(groupId == infoTypeMetaField.getAccountInfoType().getId() && effectiveDate.equals(infoTypeMetaField.getEffectiveDate())) {
        		MetaFieldValue suspect = infoTypeMetaField.getMetaFieldValue();
        		if(suspect.getField().getName().equals(name)) {
        			return infoTypeMetaField;
        		}
        	}
        }
        return null;
    }

    @Transient
    public CustomerAccountInfoTypeMetaField getCustomerAccountInfoTypeMetaField(String name, Integer groupId) {
        for (CustomerAccountInfoTypeMetaField infoTypeMetaField : customerAccountInfoTypeMetaFields) {
            if(groupId == infoTypeMetaField.getAccountInfoType().getId()) {
                MetaFieldValue suspect = infoTypeMetaField.getMetaFieldValue();
                if(suspect.getField().getName().equals(name)) {
                    return infoTypeMetaField;
                }
            }
        }
        return null;
    }

    /**
     * Returns the first meta field value with specified name (across all meta field groups)
     * @param name
     * @return
     */
    @Transient
    public CustomerAccountInfoTypeMetaField getCustomerAccountInfoTypeMetaField(String name) {
        for (CustomerAccountInfoTypeMetaField infoTypeMetaField : customerAccountInfoTypeMetaFields) {
            MetaFieldValue suspect = infoTypeMetaField.getMetaFieldValue();
            if(suspect.getField().getName().equals(name)) {
                return infoTypeMetaField;
            }
        }
        return null;
    }

    @Transient
    public List<CustomerAccountInfoTypeMetaField> getCustomerAccountInfoTypeMetaFields(Integer groupId, Date effectiveDate) {
    	List<CustomerAccountInfoTypeMetaField> fields = new ArrayList<CustomerAccountInfoTypeMetaField>();
    	for (CustomerAccountInfoTypeMetaField infoTypeMetaField : customerAccountInfoTypeMetaFields) {
        	if(groupId == infoTypeMetaField.getAccountInfoType().getId() && effectiveDate.equals(infoTypeMetaField.getEffectiveDate())) {
        		fields.add(infoTypeMetaField);
        	}
        }
        return fields;
    }
    
    @Transient
    public void removeAitMetaFields () {
    	this.customerAccountInfoTypeMetaFields.removeAll(this.customerAccountInfoTypeMetaFields);
    }
    
    public void setCustomerAccountInfoTypeMetaFields(Set<CustomerAccountInfoTypeMetaField> customerAccountInfoTypeMetaFields) {
    	this.customerAccountInfoTypeMetaFields = customerAccountInfoTypeMetaFields;
    }

    @Transient
    public Map<Integer, List<MetaFieldValue>> getAitMetaFieldMap() {
		return aitMetaFieldMap;
	}

	public void setAitMetaFieldMap(Map<Integer, List<MetaFieldValue>> aitMetaFieldMap) {
		this.aitMetaFieldMap = aitMetaFieldMap;
	}

	@Transient
    public EntityType[] getCustomizedEntityType() {
        return new EntityType[] { EntityType.CUSTOMER, EntityType.ACCOUNT_TYPE };
    }

    @Transient
    public Integer getTotalSubAccounts() {
        LOG.debug("sub acounts = " + getChildren().size());
        return (getChildren().size() == 0) ? null : new Integer(getChildren().size());
    }

    /**
     * Usefull method for updating ait meta fields with validation before entity saving
     *
     * @param dto dto with new data
     */
    @Transient
    public void updateAitMetaFieldsWithValidation(Integer entityId, Integer accountTypeId, MetaContent dto) {
        MetaFieldExternalHelper.updateAitMetaFieldsWithValidation(new CompanyDAS().find(entityId).getLanguageId(),
                entityId, accountTypeId, this, dto);
    }
    
    @Transient
    public void setAitMetaField(Integer entityId, Integer groupId, String name, Object value) {
        MetaFieldExternalHelper.setAitMetaField(entityId, this, groupId, name, value);
    }
    
    @Transient
    public void setAitMetaField(MetaFieldValue field, Integer groupId) {
        if(getAitMetaFieldMap().containsKey(groupId)) {
    		getAitMetaFieldMap().get(groupId).add(field);
    	} else {
    		List<MetaFieldValue> fields = new ArrayList<MetaFieldValue>();
    		fields.add(field);
    		getAitMetaFieldMap().put(groupId, fields);
    	}
    }

    @Transient
    public boolean invoiceAsChild() {
        return ( invoiceChild == null || invoiceChild.intValue() == 0 );
    }

    @Transient
    public Map<Integer, Map<Date, List<MetaFieldValue>>> getAitTimelineMetaFieldsMap() {
    	Map<Integer, Map<Date, List<MetaFieldValue>>> accountInfoTypeFieldsMap = new HashMap<Integer, Map<Date, List<MetaFieldValue>>>();
        for(CustomerAccountInfoTypeMetaField accountInfoTypeField : getCustomerAccountInfoTypeMetaFields()) {
        	Integer groupId = accountInfoTypeField.getAccountInfoType().getId();
        	if(accountInfoTypeFieldsMap.containsKey(accountInfoTypeField.getAccountInfoType().getId())) {
        		Map<Date, List<MetaFieldValue>> metaFieldMap =  accountInfoTypeFieldsMap.get(accountInfoTypeField.getAccountInfoType().getId());
        		List<MetaFieldValue> valueList;
        		
        		if(metaFieldMap.containsKey(accountInfoTypeField.getEffectiveDate())) {
        			valueList = metaFieldMap.get(accountInfoTypeField.getEffectiveDate());
        			valueList.add(accountInfoTypeField.getMetaFieldValue());
        		} else {
        			valueList = new ArrayList<MetaFieldValue>();
            		valueList.add(accountInfoTypeField.getMetaFieldValue());
        		}
        		
        		metaFieldMap.put(accountInfoTypeField.getEffectiveDate(),valueList);
        		accountInfoTypeFieldsMap.put(accountInfoTypeField.getAccountInfoType().getId(), metaFieldMap);
        	} else {
        		Map<Date, List<MetaFieldValue>> metaFieldMap = new HashMap<Date, List<MetaFieldValue>>();
        		List<MetaFieldValue> valueList = new ArrayList<MetaFieldValue>();
        		
        		valueList.add(accountInfoTypeField.getMetaFieldValue());
        		metaFieldMap.put(accountInfoTypeField.getEffectiveDate(),valueList);
        		
        		accountInfoTypeFieldsMap.put(accountInfoTypeField.getAccountInfoType().getId(), metaFieldMap);
        	}
        }
        return accountInfoTypeFieldsMap;
    }

    @Transient
    public boolean isTerminatedOrDropped() {
        MetaFieldValue<String> metaFieldValue = getMetaField(FileConstants.TERMINATION_META_FIELD);
        return (metaFieldValue != null &&
                (FileConstants.TERMINATION_PROCESSING.equals(metaFieldValue.getValue()) ||
                        FileConstants.DROPPED.equals(metaFieldValue.getValue())) );
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
               "id=" + id +
               ", baseUser.userId=" + (baseUser != null ? baseUser.getUserId() : null) +
               ", baseUser.userName=" + (baseUser != null ? baseUser.getUserName() : null) +
               ", dynamicBalance = " + this.dynamicBalance +
               ", credit limit = " + this.creditLimit +
               ", nextInvoiceDate=" + this.nextInvoiceDate + 
               '}';
    }

    public String getAuditKey(Serializable id) {
        StringBuilder key = new StringBuilder();
        key.append(getBaseUser().getCompany().getId())
            .append("-usr-")
            .append(getBaseUser().getId())
            .append("-")
            .append(id);

        return key.toString();
    }

}


