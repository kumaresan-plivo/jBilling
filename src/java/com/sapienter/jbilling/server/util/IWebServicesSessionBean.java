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

package com.sapienter.jbilling.server.util;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import javax.jws.WebService;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentWS;
import com.sapienter.jbilling.server.diameter.DiameterResultWS;
import com.sapienter.jbilling.server.discount.DiscountWS;
import com.sapienter.jbilling.server.ediTransaction.EDIFileStatusWS;
import com.sapienter.jbilling.server.ediTransaction.EDIFileWS;
import com.sapienter.jbilling.server.ediTransaction.EDITypeWS;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.item.*;
import com.sapienter.jbilling.server.mediation.*;
import com.sapienter.jbilling.server.metafields.MetaFieldGroupWS;
import com.sapienter.jbilling.server.metafields.MetaFieldWS;
import com.sapienter.jbilling.server.notification.MessageDTO;
import com.sapienter.jbilling.server.order.*;
import com.sapienter.jbilling.server.payment.*;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskTypeCategoryWS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskTypeWS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskWS;
import com.sapienter.jbilling.server.pricing.DataTableQueryWS;
import com.sapienter.jbilling.server.pricing.RateCardWS;
import com.sapienter.jbilling.server.pricing.RatingUnitWS;
import com.sapienter.jbilling.server.pricing.RouteRecordWS;
import com.sapienter.jbilling.server.process.AgeingWS;
import com.sapienter.jbilling.server.process.BillingProcessConfigurationWS;
import com.sapienter.jbilling.server.process.BillingProcessWS;
import com.sapienter.jbilling.server.process.ProcessStatusWS;
import com.sapienter.jbilling.server.provisioning.ProvisioningCommandType;
import com.sapienter.jbilling.server.provisioning.ProvisioningCommandWS;
import com.sapienter.jbilling.server.provisioning.ProvisioningRequestWS;
import com.sapienter.jbilling.server.security.Validator;
import com.sapienter.jbilling.server.usagePool.CustomerUsagePoolWS;
import com.sapienter.jbilling.server.usagePool.UsagePoolWS;
import com.sapienter.jbilling.server.user.AccountInformationTypeWS;
import com.sapienter.jbilling.server.user.AccountTypeWS;
import com.sapienter.jbilling.server.user.CompanyWS;
import com.sapienter.jbilling.server.user.ContactWS;
import com.sapienter.jbilling.server.user.CreateResponseWS;
import com.sapienter.jbilling.server.user.CustomerNoteWS;
import com.sapienter.jbilling.server.user.MatchingFieldWS;
import com.sapienter.jbilling.server.user.RouteRateCardWS;
import com.sapienter.jbilling.server.user.RouteWS;
import com.sapienter.jbilling.server.user.UserCodeWS;
import com.sapienter.jbilling.server.user.UserTransitionResponseWS;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.user.ValidatePurchaseWS;
import com.sapienter.jbilling.server.user.partner.CommissionProcessConfigurationWS;
import com.sapienter.jbilling.server.user.partner.CommissionProcessRunWS;
import com.sapienter.jbilling.server.user.partner.CommissionWS;
import com.sapienter.jbilling.server.user.partner.PartnerWS;
import com.sapienter.jbilling.server.util.search.SearchCriteria;
import com.sapienter.jbilling.server.util.search.SearchResultString;


/**
 * Web service bean interface. 
 * {@see com.sapienter.jbilling.server.util.WebServicesSessionSpringBean} for documentation.
 */
@WebService
public interface IWebServicesSessionBean {

    public Integer getCallerId();
    public Integer getCallerCompanyId();
    public Integer getCallerLanguageId();
    public Integer getCallerCurrencyId();

    /*
        Users
     */
    public UserWS getUserWS(Integer userId) throws SessionInternalError;
    public Integer createUser(UserWS newUser) throws SessionInternalError;
    public Integer createUserWithCompanyId(UserWS newUser, Integer entityId) throws SessionInternalError;
    public void updateUser(UserWS user) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void updateUserWithCompanyId(UserWS user, Integer entityId) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void deleteUser(Integer userId) throws SessionInternalError;
    public void initiateTermination(Integer userId, String reasonCode, Date terminationDate) throws SessionInternalError;

    public boolean userExistsWithName(String userName);
    public boolean userExistsWithId(Integer userId);

    public ContactWS[] getUserContactsWS(Integer userId) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void updateUserContact(Integer userId, ContactWS contact) throws SessionInternalError;

    @Validator(type = Validator.Type.EDIT)
    public void setAuthPaymentType(Integer userId, Integer autoPaymentType, boolean use) throws SessionInternalError;
    public Integer getAuthPaymentType(Integer userId) throws SessionInternalError;

    public Integer[] getUsersByStatus(Integer statusId, boolean in) throws SessionInternalError;
    public Integer[] getUsersInStatus(Integer statusId) throws SessionInternalError;
    public Integer[] getUsersNotInStatus(Integer statusId) throws SessionInternalError;

    public Integer getUserId(String username) throws SessionInternalError;
    public Integer getUserIdByEmail(String email) throws SessionInternalError;

    public UserTransitionResponseWS[] getUserTransitions(Date from, Date to) throws SessionInternalError;
    public UserTransitionResponseWS[] getUserTransitionsAfterId(Integer id) throws SessionInternalError;

    @Validator(type = Validator.Type.EDIT)
    public CreateResponseWS create(UserWS user, OrderWS order, OrderChangeWS[] orderChanges) throws SessionInternalError;

    @Validator(type = Validator.Type.EDIT)
    public Integer createUserCode(UserCodeWS userCode) throws SessionInternalError;
    public UserCodeWS[] getUserCodesForUser(Integer userId) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void updateUserCode(UserCodeWS userCode) throws SessionInternalError;
    public Integer[] getCustomersByUserCode(String userCode) throws SessionInternalError;
    public Integer[] getOrdersByUserCode(String userCode) throws SessionInternalError;
    public Integer[] getOrdersLinkedToUser(Integer userId) throws SessionInternalError;
    public Integer[] getCustomersLinkedToUser(Integer userId) throws SessionInternalError;
    @Validator(type = Validator.Type.NONE)
    public void resetPassword(int userId) throws SessionInternalError;


    /*
        Partners
     */

    public PartnerWS getPartner(Integer partnerId) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public Integer createPartner(UserWS newUser, PartnerWS partner) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void updatePartner(UserWS newUser, PartnerWS partner) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void deletePartner (Integer partnerId) throws SessionInternalError;


    /*
        Items
     */

    // categories parentness
    public ItemTypeWS[] getItemCategoriesByPartner(String partner, boolean parentCategoriesOnly);
    public ItemTypeWS[] getChildItemCategories(Integer itemTypeId);

    public ItemDTOEx getItem(Integer itemId, Integer userId, String pricing);
    public ItemDTOEx[] getAllItems() throws SessionInternalError;
    public Integer createItem(ItemDTOEx item) throws SessionInternalError;
    public void updateItem(ItemDTOEx item);
    @Validator(type = Validator.Type.EDIT)
    public void deleteItem(Integer itemId);

    public ItemDTOEx[] getAddonItems(Integer itemId);

    public ItemDTOEx[] getItemByCategory(Integer itemTypeId);
    public Integer[] getUserItemsByCategory(Integer userId, Integer categoryId);

	public ItemTypeWS getItemCategoryById(Integer id);
    public ItemTypeWS[] getAllItemCategories();
    public Integer createItemCategory(ItemTypeWS itemType) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void updateItemCategory(ItemTypeWS itemType) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void deleteItemCategory(Integer itemCategoryId);
    
    public ItemTypeWS[] getAllItemCategoriesByEntityId(Integer entityId);
    public ItemDTOEx[] getAllItemsByEntityId(Integer entityId);
    
    public String isUserSubscribedTo(Integer userId, Integer itemId);

    public InvoiceWS getLatestInvoiceByItemType(Integer userId, Integer itemTypeId) throws SessionInternalError;
    public Integer[] getLastInvoicesByItemType(Integer userId, Integer itemTypeId, Integer number) throws SessionInternalError;

    public OrderWS getLatestOrderByItemType(Integer userId, Integer itemTypeId) throws SessionInternalError;
    public Integer[] getLastOrdersByItemType(Integer userId, Integer itemTypeId, Integer number) throws SessionInternalError;

    public ValidatePurchaseWS validatePurchase(Integer userId, Integer itemId, String fields);
    public ValidatePurchaseWS validateMultiPurchase(Integer userId, Integer[] itemId, String[] fields);
    public Integer getItemID(String productCode) throws SessionInternalError;


    /*
        Orders
     */

    public OrderWS getOrder(Integer orderId) throws SessionInternalError;
    public Integer createOrder(OrderWS order, OrderChangeWS[] orderChanges) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void updateOrder(OrderWS order, OrderChangeWS[] orderChanges) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public Integer createUpdateOrder(OrderWS order, OrderChangeWS[] orderChanges) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public String deleteOrder(Integer id) throws SessionInternalError;

    public Integer createOrderAndInvoice(OrderWS order, OrderChangeWS[] orderChanges) throws SessionInternalError;

    public OrderWS getCurrentOrder(Integer userId, Date date) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public OrderWS updateCurrentOrder(Integer userId, OrderLineWS[] lines, String pricing, Date date, String eventDescription) throws SessionInternalError;

    public OrderWS[] getUserSubscriptions(Integer userId) throws SessionInternalError;
    
    public OrderLineWS getOrderLine(Integer orderLineId) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void updateOrderLine(OrderLineWS line) throws SessionInternalError;

    public Integer[] getOrderByPeriod(Integer userId, Integer periodId) throws SessionInternalError;
    public OrderWS getLatestOrder(Integer userId) throws SessionInternalError;
    public Integer[] getLastOrders(Integer userId, Integer number) throws SessionInternalError;
    public Integer[] getOrdersByDate (Integer userId, Date since, Date until);
    public OrderWS[] getUserOrdersPage(Integer user, Integer limit, Integer offset) throws SessionInternalError;

    public Integer[] getLastOrdersPage(Integer userId, Integer limit, Integer offset) throws SessionInternalError;

    public OrderWS rateOrder(OrderWS order, OrderChangeWS[] orderChanges) throws SessionInternalError;
    public OrderWS[] rateOrders(OrderWS orders[], OrderChangeWS[] orderChanges) throws SessionInternalError;

    public OrderChangeWS[] calculateSwapPlanChanges(OrderWS order,  Integer existingPlanItemId, Integer swapPlanItemId, SwapMethod method, Date effectiveDate);

    public boolean updateOrderPeriods(OrderPeriodWS[] orderPeriods) throws SessionInternalError;
    public boolean updateOrCreateOrderPeriod(OrderPeriodWS orderPeriod) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public boolean deleteOrderPeriod(Integer periodId) throws SessionInternalError;

    public PaymentAuthorizationDTOEx createOrderPreAuthorize(OrderWS order, OrderChangeWS[] orderChanges) throws SessionInternalError;
    
    public OrderPeriodWS[] getOrderPeriods() throws SessionInternalError;

    public OrderPeriodWS getOrderPeriodWS(Integer orderPeriodId) throws SessionInternalError;

    @Validator(type = Validator.Type.EDIT)
    public void updateOrders(OrderWS[] orders, OrderChangeWS[] orderChanges) throws SessionInternalError;

    /*
        Account Type
     */
    public Integer createAccountType(AccountTypeWS accountType) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public boolean updateAccountType(AccountTypeWS accountType);
    @Validator(type = Validator.Type.EDIT)
    public boolean deleteAccountType(Integer accountTypeId) throws SessionInternalError;
    public AccountTypeWS getAccountType(Integer accountTypeId) throws SessionInternalError;
    public AccountTypeWS[] getAllAccountTypes() throws SessionInternalError;

    /*
        Account Information Types
    */
    public AccountInformationTypeWS[] getInformationTypesForAccountType(Integer accountTypeId);
    public Integer createAccountInformationType(AccountInformationTypeWS accountInformationType);
    @Validator(type = Validator.Type.EDIT)
    public void updateAccountInformationType(AccountInformationTypeWS accountInformationType);
    @Validator(type = Validator.Type.EDIT)
    public boolean deleteAccountInformationType(Integer accountInformationTypeId);
    public AccountInformationTypeWS getAccountInformationType(Integer accountInformationType);

    public OrderWS[] getLinkedOrders(Integer primaryOrderId) throws SessionInternalError;
    public Integer createOrderPeriod(OrderPeriodWS orderPeriod) throws SessionInternalError;

    /*
        Invoices
     */

    public InvoiceWS getInvoiceWS(Integer invoiceId) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public Integer[] createInvoice(Integer userId, boolean onlyRecurring);
    @Validator(type = Validator.Type.EDIT)
    public Integer[] createInvoiceWithDate(Integer userId, Date billingDate, Integer dueDatePeriodId, Integer dueDatePeriodValue, boolean onlyRecurring);
    @Validator(type = Validator.Type.EDIT)
    public Integer createInvoiceFromOrder(Integer orderId, Integer invoiceId) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public Integer applyOrderToInvoice(Integer orderId, InvoiceWS invoiceWs);
    @Validator(type = Validator.Type.EDIT)
    public void deleteInvoice(Integer invoiceId);
    @Validator(type = Validator.Type.EDIT)
    public Integer saveLegacyInvoice(InvoiceWS invoiceWS);
    @Validator(type = Validator.Type.EDIT)
    public Integer saveLegacyPayment(PaymentWS paymentWS);
    @Validator(type = Validator.Type.EDIT)
    public Integer saveLegacyOrder(OrderWS orderWS);

    public InvoiceWS[] getAllInvoicesForUser(Integer userId);
    public Integer[] getAllInvoices(Integer userId);
    public InvoiceWS getLatestInvoice(Integer userId) throws SessionInternalError;
    public Integer[] getLastInvoices(Integer userId, Integer number) throws SessionInternalError;

    public Integer[] getInvoicesByDate(String since, String until) throws SessionInternalError;
    public Integer[] getUserInvoicesByDate(Integer userId, String since, String until) throws SessionInternalError;
    public Integer[] getUnpaidInvoices(Integer userId) throws SessionInternalError;
    public InvoiceWS[] getUserInvoicesPage(Integer userId, Integer limit, Integer offset) throws SessionInternalError;

    public byte[] getPaperInvoicePDF(Integer invoiceId) throws SessionInternalError;
    public boolean notifyInvoiceByEmail(Integer invoiceId);
    public boolean notifyPaymentByEmail(Integer paymentId);

    /*
        Payments
     */

    public PaymentWS getPayment(Integer paymentId) throws SessionInternalError;
    public PaymentWS getLatestPayment(Integer userId) throws SessionInternalError;
    public Integer[] getLastPayments(Integer userId, Integer number) throws SessionInternalError;

    public Integer[] getLastPaymentsPage(Integer userId, Integer limit, Integer offset) throws SessionInternalError;
    public Integer[] getPaymentsByDate(Integer userId, Date since, Date until) throws SessionInternalError;

    public BigDecimal getTotalRevenueByUser (Integer userId) throws SessionInternalError;

    public PaymentWS getUserPaymentInstrument(Integer userId) throws SessionInternalError;
    public PaymentWS[] getUserPaymentsPage(Integer userId, Integer limit, Integer offset) throws SessionInternalError;

    public Integer createPayment(PaymentWS payment);
    @Validator(type = Validator.Type.EDIT)
    public void updatePayment(PaymentWS payment);
    @Validator(type = Validator.Type.EDIT)
    public void deletePayment(Integer paymentId);

    public void removePaymentLink(Integer invoiceId, Integer paymentId) throws SessionInternalError;
    public void createPaymentLink(Integer invoiceId, Integer paymentId);
    @Validator(type = Validator.Type.EDIT)
    public void removeAllPaymentLinks(Integer paymentId) throws SessionInternalError;

    @Validator(type = Validator.Type.EDIT)
    public PaymentAuthorizationDTOEx payInvoice(Integer invoiceId) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public Integer applyPayment(PaymentWS payment, Integer invoiceId) throws SessionInternalError;
    public PaymentAuthorizationDTOEx processPayment(PaymentWS payment, Integer invoiceId);

//    public CardValidationWS validateCreditCard(com.sapienter.jbilling.server.entity.CreditCardDTO creditCard, ContactWS contact, int level);

    public PaymentAuthorizationDTOEx[] processPayments(PaymentWS[] payments, Integer invoiceId);

    public Integer[] createPayments(PaymentWS[] payment);

    /*
	 * Payment Transfer
	 */
    public void transferPayment(PaymentTransferWS paymentTransfer);
    
    /*
        Billing process
     */

    public boolean isBillingRunning(Integer entityId);
    public ProcessStatusWS getBillingProcessStatus();
    public void triggerBillingAsync(final Date runDate);
    public boolean triggerBilling(Date runDate);

    public void triggerAgeing(Date runDate);
    public void triggerCollectionsAsync (final Date runDate);
    public boolean isAgeingProcessRunning();
    public ProcessStatusWS getAgeingProcessStatus();

    public BillingProcessConfigurationWS getBillingProcessConfiguration() throws SessionInternalError;
    public Integer createUpdateBillingProcessConfiguration(BillingProcessConfigurationWS ws) throws SessionInternalError;

    public Integer createUpdateCommissionProcessConfiguration(CommissionProcessConfigurationWS ws) throws SessionInternalError;
    public void calculatePartnerCommissions() throws SessionInternalError;
    public void calculatePartnerCommissionsAsync() throws SessionInternalError;
    public boolean isPartnerCommissionRunning();
    public CommissionProcessRunWS[] getAllCommissionRuns() throws SessionInternalError;
    public CommissionWS[] getCommissionsByProcessRunId(Integer processRunId) throws SessionInternalError;

    public BillingProcessWS getBillingProcess(Integer processId);
    public Integer getLastBillingProcess() throws SessionInternalError;
    
    public OrderProcessWS[] getOrderProcesses(Integer orderId);
    public OrderProcessWS[] getOrderProcessesByInvoice(Integer invoiceId);

    public BillingProcessWS getReviewBillingProcess();
    public BillingProcessConfigurationWS setReviewApproval(Boolean flag) throws SessionInternalError;

    public Integer[] getBillingProcessGeneratedInvoices(Integer processId);

    public AgeingWS[] getAgeingConfiguration(Integer languageId) throws SessionInternalError;
    public void saveAgeingConfiguration(AgeingWS[] steps, Integer languageId) throws SessionInternalError;


    /*
        Mediation process
     */

    public void triggerMediation();
    public UUID triggerMediationByConfiguration(Integer cfgId);
    public UUID launchMediation(Integer mediationCfgId, String jobName, File file);
    public void undoMediation(UUID processId) throws SessionInternalError;
    public boolean isMediationProcessRunning();
    public ProcessStatusWS getMediationProcessStatus();

    public MediationProcess getMediationProcess(UUID mediationProcessId);
    public MediationProcess[] getAllMediationProcesses();
    public JbillingMediationRecord[] getMediationEventsForOrder(Integer orderId);
    public JbillingMediationRecord[] getMediationEventsForOrderDateRange(Integer orderId,Date startDate, Date endDate, int offset, int limit);
    public JbillingMediationRecord[] getMediationEventsForInvoice(Integer invoiceId);
    public JbillingMediationRecord[] getMediationRecordsByMediationProcess(UUID mediationProcessId, Integer page, Integer size, Date startDate, Date endDate);
    public RecordCountWS[] getNumberOfMediationRecordsByStatuses();
    public RecordCountWS[] getNumberOfMediationRecordsByStatusesByMediationProcess(UUID mediationProcess);


    public MediationConfigurationWS[] getAllMediationConfigurations();
    public Integer createMediationConfiguration(MediationConfigurationWS cfg);
    @Validator(type = Validator.Type.EDIT)
    public Integer[] updateAllMediationConfigurations(List<MediationConfigurationWS> configurations) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void deleteMediationConfiguration(Integer cfgId);

    public OrderWS processJMRData(
            UUID processId, String recordKey, Integer userId,
            Integer currencyId, Date eventDate, String description,
            Integer productId, String quantity, String pricing);
    public JbillingMediationErrorRecord[] getMediationErrorRecordsByMediationProcess(UUID mediationProcessId, Integer mediationRecordStatusId);

    public OrderWS processJMRRecord(UUID processId, JbillingMediationRecord JMR);

    public UUID processCDR(Integer configId, List<String> callDataRecords);

    /*
        Provisioning process
     */

    public void triggerProvisioning();

    @Validator(type = Validator.Type.EDIT)
    public void updateOrderAndLineProvisioningStatus(Integer inOrderId, Integer inLineId, String result);
    @Validator(type = Validator.Type.EDIT)
    public void updateLineProvisioningStatus(Integer orderLineId, Integer provisioningStatus);

    public ProvisioningCommandWS[] getProvisioningCommands(ProvisioningCommandType type, Integer id);
    public ProvisioningCommandWS getProvisioningCommandById(Integer provisioningCommandId);

    public ProvisioningRequestWS[] getProvisioningRequests(Integer provisioningCommandId);
    public ProvisioningRequestWS getProvisioningRequestById(Integer provisioningRequestId);

    /*
        Preferences
     */

    public void updatePreferences(PreferenceWS[] prefList);
    public void updatePreference(PreferenceWS preference);
    public PreferenceWS getPreference(Integer preferenceTypeId);


    /*
        Currencies
     */

    public CurrencyWS[] getCurrencies();
    public void updateCurrencies(CurrencyWS[] currencies);
    public void updateCurrency(CurrencyWS currency);
    public Integer createCurrency(CurrencyWS currency);
    public boolean deleteCurrency(Integer currencyId);

    public CompanyWS getCompany();
    public CompanyWS[] getCompanies();
    public void updateCompany(CompanyWS companyWS);
    
    /*
        Notifications
    */

    public void createUpdateNotification(Integer messageId, MessageDTO dto);


    /*
        Plug-ins
     */

    public PluggableTaskWS getPluginWS(Integer pluginId);
    public PluggableTaskWS[] getPluginsWS(Integer entityId, String className);
    public Integer createPlugin(PluggableTaskWS plugin);
    @Validator(type = Validator.Type.EDIT)
    public void updatePlugin(PluggableTaskWS plugin);
    @Validator(type = Validator.Type.EDIT)
    public void deletePlugin(Integer plugin);

	/*
	 * Quartz jobs
	 */
	public void rescheduleScheduledPlugin(Integer pluginId);
    public void unscheduleScheduledPlugin(Integer pluginId);
    public void triggerScheduledTask(Integer pluginId, Date date);

    /*
        Plans and special pricing
     */

    public PlanWS getPlanWS(Integer planId);
    public PlanWS[] getAllPlans();
    public Integer createPlan(PlanWS plan);
    @Validator(type = Validator.Type.EDIT)
    public void updatePlan(PlanWS plan);
    @Validator(type = Validator.Type.EDIT)
    public void deletePlan(Integer planId);
    @Validator(type = Validator.Type.EDIT)
    public void addPlanPrice(Integer planId, PlanItemWS price);

    public boolean isCustomerSubscribed(Integer planId, Integer userId);
    public boolean isCustomerSubscribedForDate(Integer planId, Integer userId, Date eventDate);

    public Integer[] getSubscribedCustomers(Integer planId);
    public Integer[] getPlansBySubscriptionItem(Integer itemId);
    public Integer[] getPlansByAffectedItem(Integer itemId);

    public Usage getItemUsage(Integer excludedOrderId, Integer itemId, Integer owner, List<Integer> userIds , Date startDate, Date endDate);

    @Validator(type = Validator.Type.EDIT)
    public PlanItemWS createCustomerPrice(Integer userId, PlanItemWS planItem, Date expiryDate);
    public void updateCustomerPrice(Integer userId, PlanItemWS planItem, Date expiryDate);
    public void deleteCustomerPrice(Integer userId, Integer planItemId);

    public PlanItemWS[] getCustomerPrices(Integer userId);
    public PlanItemWS getCustomerPrice(Integer userId, Integer itemId);
    public PlanItemWS getCustomerPriceForDate(Integer userId, Integer itemId, Date pricingDate, Boolean planPricingOnly);

    public PlanItemWS createAccountTypePrice(Integer accountTypeId, PlanItemWS planItem, Date expiryDate);
    public void updateAccountTypePrice(Integer accountTypeId, PlanItemWS planItem, Date expiryDate);
    public void deleteAccountTypePrice(Integer accountTypeId, Integer planItemId);

    public PlanItemWS[] getAccountTypePrices(Integer accountTypeId);
    public PlanItemWS getAccountTypePrice(Integer accountTypeId, Integer itemId);

    
	public void createCustomerNote(CustomerNoteWS note);
    /*
     * Assets
     */

    public Integer createAsset(AssetWS asset) throws SessionInternalError ;
    @Validator(type = Validator.Type.EDIT)
    public void updateAsset(AssetWS asset) throws SessionInternalError ;
    public AssetWS getAsset(Integer assetId);
    public AssetWS getAssetByIdentifier(String assetIdentifier);
    @Validator(type = Validator.Type.EDIT)
    public void deleteAsset(Integer assetId) throws SessionInternalError ;
    public Integer[] getAssetsForCategory(Integer categoryId);
    public Integer[] getAssetsForItem(Integer itemId) ;
    public AssetTransitionDTOEx[] getAssetTransitions(Integer assetId);
    public Long startImportAssetJob(int itemId, String identifierColumnName, String notesColumnName,String globalColumnName,String entitiesColumnName, String sourceFilePath, String errorFilePath) throws SessionInternalError;
    public AssetSearchResult findAssets(int productId, SearchCriteria criteria) throws SessionInternalError ;
    public AssetWS[] findAssetsByProductCode(String productCode) throws SessionInternalError ;
    public AssetStatusDTOEx[] findAssetStatuses(String identifier) throws SessionInternalError ;
    public AssetWS findAssetByProductCodeAndIdentifier(String productCode, String identifier) throws SessionInternalError ;
    public AssetWS[] findAssetsByProductCodeAndStatus(String productCode, Integer assetStatusId) throws SessionInternalError ;

    /*
     *  Rate Card
     */
    
    public Integer createRateCard(RateCardWS rateCard, File rateCardFile);
    public void updateRateCard(RateCardWS rateCard, File rateCardFile);
    public void deleteRateCard(Integer rateCardId);

    public Integer createRouteRecord(RouteRecordWS routeRecord, Integer routeId) throws SessionInternalError ;
    public Integer createRouteRateCardRecord(RouteRateCardWS routeRateCardRecord, Integer routeRateCardId) throws SessionInternalError ;
    public void updateRouteRecord(RouteRecordWS routeRecord, Integer routeId) throws SessionInternalError ;
    public void updateRouteRateCardRecord(RouteRateCardWS record, Integer routeRateCardId) throws SessionInternalError ;
    public void deleteRouteRecord(Integer routeId, Integer recordId) throws SessionInternalError ;
    public void deleteRateCardRecord(Integer routeRateCardId, Integer recordId) throws SessionInternalError ;
    public String getRouteTable(Integer routeId) throws SessionInternalError ;
    public SearchResultString searchDataTable(Integer routeId, SearchCriteria criteria) throws SessionInternalError;
    public Set<String> searchDataTableWithFilter(Integer routeId, String filters, String searchName) throws SessionInternalError;
    public SearchResultString searchRouteRateCard(Integer routeRateCardId, SearchCriteria criteria) throws SessionInternalError;
    public Integer createDataTableQuery(DataTableQueryWS queryWS) throws SessionInternalError;
    public DataTableQueryWS getDataTableQuery(int id) throws SessionInternalError;
    public void deleteDataTableQuery(int id) throws SessionInternalError;
    public DataTableQueryWS[] findDataTableQueriesForTable(int routeId) throws SessionInternalError;

    /*
     * Trigger RE-Cycle for Mediation configuration
     */
    public UUID runRecycleForConfiguration(Integer configId);
    public UUID runRecycleForMediationProcess(UUID processId);

    public Integer reserveAsset(Integer assetId, Integer userId);
    public void releaseAsset(Integer assetId, Integer userId);

	public AssetAssignmentWS[] getAssetAssignmentsForAsset(Integer assetId);
	public AssetAssignmentWS[] getAssetAssignmentsForOrder(Integer orderId);
	public Integer findOrderForAsset(Integer assetId, Date date);
	public Integer[] findOrdersForAssetAndDateRange(Integer assetId, Date startDate, Date endDate);
    public List<AssetWS> getAssetsByUserId(Integer userId);

    /*
     *  MetaField Group
     */
    
    public Integer createMetaFieldGroup(MetaFieldGroupWS metafieldGroup);
    @Validator(type = Validator.Type.EDIT)
	public void updateMetaFieldGroup(MetaFieldGroupWS metafieldGroupWs);
    @Validator(type = Validator.Type.EDIT)
    public void deleteMetaFieldGroup(Integer metafieldGroupId);
	public MetaFieldGroupWS getMetaFieldGroup(Integer metafieldGroupId);
    public MetaFieldGroupWS[] getMetaFieldGroupsForEntity(String entityType);

	public Integer createMetaField(MetaFieldWS metafield);
    @Validator(type = Validator.Type.EDIT)
    public void updateMetaField(MetaFieldWS metafieldWs);
    @Validator(type = Validator.Type.EDIT)
	public void deleteMetaField(Integer metafieldId);
	public MetaFieldWS getMetaField(Integer metafieldId);
    public MetaFieldWS[] getMetaFieldsForEntity(String entityType);

    public Integer createOrUpdateDiscount(DiscountWS discount);
    public DiscountWS getDiscountWS(Integer discountId);
    @Validator(type = Validator.Type.EDIT)
    public void deleteDiscount(Integer discountId);

    /*
     * OrderChangeStatus
     */
    public OrderChangeStatusWS[] getOrderChangeStatusesForCompany();
    public Integer createOrderChangeStatus(OrderChangeStatusWS orderChangeStatusWS) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void updateOrderChangeStatus(OrderChangeStatusWS orderChangeStatusWS) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void deleteOrderChangeStatus(Integer id) throws SessionInternalError;
    public void saveOrderChangeStatuses(OrderChangeStatusWS[] orderChangeStatuses) throws SessionInternalError;

    /*
     * OrderChangeType
     */
    public OrderChangeTypeWS[] getOrderChangeTypesForCompany();
    public OrderChangeTypeWS getOrderChangeTypeByName(String name);
    public OrderChangeTypeWS getOrderChangeTypeById(Integer orderChangeTypeId);
    public Integer createUpdateOrderChangeType(OrderChangeTypeWS orderChangeTypeWS);
    @Validator(type = Validator.Type.EDIT)
    public void deleteOrderChangeType(Integer orderChangeTypeId);

    /*
     * OrderChange
     */
    public OrderChangeWS[] getOrderChanges(Integer orderId);
    
    /*
       Diameter Protocol
    */
    DiameterResultWS createSession(String sessionId, Date timestamp, BigDecimal units, 
    		String string) throws SessionInternalError;
    DiameterResultWS reserveUnits(String sessionId, Date timestamp, int units, 
    		String string) throws SessionInternalError;
    DiameterResultWS updateSession(String sessionId, Date timestamp, BigDecimal usedUnits, 
    		BigDecimal reqUnits, String string) throws SessionInternalError;
    DiameterResultWS extendSession(String sessionId, Date timestamp, BigDecimal usedUnits, 
    		BigDecimal reqUnits) throws SessionInternalError;
    DiameterResultWS endSession(String sessionId, Date timestamp, BigDecimal usedUnits, 
    		int causeCode) throws SessionInternalError;
    DiameterResultWS consumeReservedUnits(String sessionId, Date timestamp, int usedUnits,
    		int causeCode) throws SessionInternalError;
    /*
      Route Based Rating
     */
    public Integer createRoute(RouteWS routeWS, File routeFile);
    @Validator(type = Validator.Type.EDIT)
    public void deleteRoute(Integer routeId);
    public RouteWS getRoute(Integer routeId);
    public Integer createMatchingField(MatchingFieldWS matchingFieldWS);
    @Validator(type = Validator.Type.EDIT)
    public void deleteMatchingField(Integer matchingFieldId);
    public MatchingFieldWS getMatchingField(Integer matchingFieldId);
    @Validator(type = Validator.Type.EDIT)
    public boolean updateMatchingField(MatchingFieldWS matchingFieldWS);
    public Integer createRouteRateCard(RouteRateCardWS routeRateCardWS, File routeRateCardFile);
    @Validator(type = Validator.Type.EDIT)
    public void deleteRouteRateCard(Integer routeId);
    @Validator(type = Validator.Type.EDIT)
    public void updateRouteRateCard(RouteRateCardWS routeRateCardWS, File routeRateCardFile);
    public RouteRateCardWS getRouteRateCard(Integer routeRateCardId);

    public Integer createRatingUnit(RatingUnitWS ratingUnitWS) throws SessionInternalError;
    public void updateRatingUnit(RatingUnitWS ratingUnitWS) throws SessionInternalError;
    public boolean deleteRatingUnit(Integer ratingUnitId) throws SessionInternalError;
    public RatingUnitWS getRatingUnit(Integer ratingUnitId) throws SessionInternalError;
    public RatingUnitWS[] getAllRatingUnits() throws SessionInternalError;

    /*
     * UsagePool
     */
    public Integer createUsagePool(UsagePoolWS usagePool);
    @Validator(type = Validator.Type.EDIT)
    public void updateUsagePool(UsagePoolWS usagePool);
    public UsagePoolWS getUsagePoolWS(Integer usagePoolId);
    @Validator(type = Validator.Type.EDIT)
    public boolean deleteUsagePool(Integer usagePoolId);
    public UsagePoolWS[] getAllUsagePools();
    public UsagePoolWS[] getUsagePoolsByPlanId(Integer planId);
    public CustomerUsagePoolWS getCustomerUsagePoolById(Integer customerUsagePoolId);
    public CustomerUsagePoolWS[] getCustomerUsagePoolsByCustomerId(Integer customerId);
    
    /*
     *Payment Method 
     */
    public PaymentMethodTemplateWS getPaymentMethodTemplate(Integer templateId);
    
    public Integer createPaymentMethodType(PaymentMethodTypeWS paymentMethod);
    public void updatePaymentMethodType(PaymentMethodTypeWS paymentMethod);
    public boolean deletePaymentMethodType(Integer paymentMethodTypeId);
    public PaymentMethodTypeWS getPaymentMethodType(Integer paymentMethodTypeId);
    
    public boolean removePaymentInstrument(Integer instrumentId);
	
    /*
     *  Order status
     */
    
    public Integer createUpdateOrderStatus(OrderStatusWS newOrderStatus) throws SessionInternalError;
    public Integer createUpdateEdiStatus(EDIFileStatusWS ediFileStatusWS) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public void deleteOrderStatus(OrderStatusWS orderStatus);
    public void deleteEdiFileStatus(Integer ediFileStatusId);
    public OrderStatusWS findOrderStatusById(Integer orderStatusId);
    public EDIFileStatusWS findEdiStatusById(Integer orderStatusId);
    public int getDefaultOrderStatusId(OrderStatusFlag flag, Integer entityId);
    
    /*
     * Plugin
     */
    
    public PluggableTaskTypeWS getPluginTypeWS(Integer id);
    public PluggableTaskTypeWS getPluginTypeWSByClassName(String className);
    public PluggableTaskTypeCategoryWS getPluginTypeCategory(Integer id);
    public PluggableTaskTypeCategoryWS getPluginTypeCategoryByInterfaceName(String interfaceName);
    
    /*
     * Subscription category
     */
    
    public Integer[] createSubscriptionAccountAndOrder(Integer parentAccountId, OrderWS order, boolean createInvoice, List<OrderChangeWS> orderChanges);

    /* Language */
    public Integer createOrEditLanguage(LanguageWS languageWS);
    public Long getMediationErrorRecordsCount(Integer mediationConfigurationId);

    /*
     * Enumerations
     */
    public EnumerationWS getEnumeration(Integer enumerationId);
    public EnumerationWS getEnumerationByName(String name);
    public List<EnumerationWS> getAllEnumerations(Integer max, Integer offset);
    public Long getAllEnumerationsCount();
    public Integer createUpdateEnumeration(EnumerationWS enumerationWS) throws SessionInternalError;
    @Validator(type = Validator.Type.EDIT)
    public boolean deleteEnumeration(Integer enumerationId) throws SessionInternalError;

    /*
   * Copy Company
   * */
    public UserWS copyCompany(String childCompanyTemplateName, Integer entityId, List<String> importEntities, boolean isCompanyChild, boolean copyProducts, boolean copyPlans);

    //Customer Enrollment
    public CustomerEnrollmentWS getCustomerEnrollment(Integer enrollmentId) throws SessionInternalError;
    public Integer createUpdateEnrollment(CustomerEnrollmentWS customerEnrollmentWS) throws SessionInternalError;
    public CustomerEnrollmentWS validateCustomerEnrollment(CustomerEnrollmentWS customerEnrollmentWS) throws SessionInternalError;
    public void deleteEnrollment(Integer customerEnrollmentId) throws SessionInternalError;

    // EDI File processor
    public EDITypeWS getEDIType(Integer ediTypeId) throws SessionInternalError;
    public Integer createEDIType(EDITypeWS ediTypeWS, File ediFormatFile) throws SessionInternalError;
    public void deleteEDIType(Integer ediTypeId) throws SessionInternalError;

    public  int generateEDIFile(Integer ediTypeId, Integer entityId, String fileName, Collection input) throws SessionInternalError;
    public int parseEDIFile(Integer ediTypeId, Integer entityId, File parserFile) throws SessionInternalError;

    public int saveEDIFileRecord(EDIFileWS ediFileWS) throws SessionInternalError;
    public void updateEDIFileStatus(Integer fileId, String statusName, String comment) throws SessionInternalError;

    public List<CompanyWS> getAllChildEntities(Integer parentId) throws SessionInternalError;
    public void updateEDIStatus(EDIFileWS ediFileWS, EDIFileStatusWS ediFileStatusWS) throws SessionInternalError;

    /* migration related api */
    public Integer createAdjustmentOrderAndInvoice(String customerPrimaryAccount, OrderWS order, OrderChangeWS[] orderChanges);
    public String createPaymentForHistoricalDateMigration(String customerPrimaryAccount, Integer chequePmId, String amount, String date);
    public String adjustUserBalance(String customerPrimaryAccount, String amount, Integer chequePmId, String date);

}
