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

package com.sapienter.jbilling.server.util.api;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.customerEnrollment.CustomerEnrollmentWS;
import com.sapienter.jbilling.server.diameter.DiameterResultWS;
import com.sapienter.jbilling.server.discount.DiscountWS;
import com.sapienter.jbilling.server.ediTransaction.*;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.item.AssetAssignmentWS;
import com.sapienter.jbilling.server.item.AssetSearchResult;
import com.sapienter.jbilling.server.item.AssetStatusDTOEx;
import com.sapienter.jbilling.server.item.AssetTransitionDTOEx;
import com.sapienter.jbilling.server.item.AssetWS;
import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.item.ItemTypeWS;
import com.sapienter.jbilling.server.item.PlanItemWS;
import com.sapienter.jbilling.server.item.PlanWS;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.mediation.*;
import com.sapienter.jbilling.server.metafields.MetaFieldGroupWS;
import com.sapienter.jbilling.server.metafields.MetaFieldWS;
import com.sapienter.jbilling.server.notification.MessageDTO;
import com.sapienter.jbilling.server.order.OrderChangeStatusWS;
import com.sapienter.jbilling.server.order.OrderChangeTypeWS;
import com.sapienter.jbilling.server.order.OrderChangeWS;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderPeriodWS;
import com.sapienter.jbilling.server.order.OrderProcessWS;
import com.sapienter.jbilling.server.order.OrderStatusFlag;
import com.sapienter.jbilling.server.order.OrderStatusWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.order.SwapMethod;
import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.payment.*;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskTypeCategoryWS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskTypeWS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskWS;
import com.sapienter.jbilling.server.pricing.DataTableQueryWS;
import com.sapienter.jbilling.server.pricing.RatingUnitWS;
import com.sapienter.jbilling.server.pricing.RouteRecordWS;
import com.sapienter.jbilling.server.process.AgeingWS;
import com.sapienter.jbilling.server.process.BillingProcessConfigurationWS;
import com.sapienter.jbilling.server.process.BillingProcessWS;
import com.sapienter.jbilling.server.process.ProcessStatusWS;
import com.sapienter.jbilling.server.provisioning.ProvisioningCommandType;
import com.sapienter.jbilling.server.provisioning.ProvisioningCommandWS;
import com.sapienter.jbilling.server.provisioning.ProvisioningRequestWS;
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
import com.sapienter.jbilling.server.util.*;
import com.sapienter.jbilling.server.util.search.SearchCriteria;
import com.sapienter.jbilling.server.util.search.SearchResultString;

public class SpringAPI implements JbillingAPI {

    private IWebServicesSessionBean session = null;

    public SpringAPI() {
        this(RemoteContext.Name.API_CLIENT);
    }

    public SpringAPI(String beanName) {
    	session = (IWebServicesSessionBean) RemoteContext.getBean(beanName);
    }
    
    public SpringAPI(RemoteContext.Name bean) {
        session = (IWebServicesSessionBean) RemoteContext.getBean(bean);
    }

    public Integer applyPayment(PaymentWS payment, Integer invoiceId) {
        return session.applyPayment(payment, invoiceId);
    }

    public PaymentAuthorizationDTOEx processPayment(PaymentWS payment, Integer invoiceId) {
        return session.processPayment(payment, invoiceId);
    }

    public PaymentAuthorizationDTOEx[] processPayments(PaymentWS[] payments, Integer invoiceId) {
        return session.processPayments(payments, invoiceId);
    }


    public PartnerWS getPartner(Integer partnerId) {
        return session.getPartner(partnerId);
    }
    
    public Integer createPartner(UserWS newUser, PartnerWS partner) {
        return session.createPartner(newUser, partner);
    }
    
    public void updatePartner(UserWS newUser, PartnerWS partner) {
        session.updatePartner(newUser, partner);
    }
    
    public void deletePartner (Integer partnerId){
        session.deletePartner(partnerId);
    }

    public CreateResponseWS create(UserWS user, OrderWS order, OrderChangeWS[] orderChanges) {
        return session.create(user, order, orderChanges);
    }

    public Integer createUserCode(UserCodeWS userCode) {
        return session.createUserCode(userCode);
    }

    public UserCodeWS[] getUserCodesForUser(Integer userId) {
        return session.getUserCodesForUser(userId);
    }
    public void updateUserCode(UserCodeWS userCode) {
        session.updateUserCode(userCode);
    }
    public Integer[] getCustomersByUserCode(String userCode) {
        return session.getCustomersByUserCode(userCode);
    }

    public Integer[] getOrdersByUserCode(String userCode) {
        return session.getOrdersByUserCode(userCode);
    }

    public Integer[] getOrdersLinkedToUser(Integer userId) {
        return session.getOrdersLinkedToUser(userId);
    }

    public Integer[] getCustomersLinkedToUser(Integer userId) {
        return session.getCustomersLinkedToUser(userId);
    }

    public Integer createItem(ItemDTOEx dto) {
        return session.createItem(dto);
    }

    public Integer createOrder(OrderWS order, OrderChangeWS[] orderChanges) {
        return session.createOrder(order, orderChanges);
    }

    public Integer createOrderAndInvoice(OrderWS order, OrderChangeWS[] orderChanges) {
        return session.createOrderAndInvoice(order, orderChanges);
    }

    public PaymentAuthorizationDTOEx createOrderPreAuthorize(OrderWS order, OrderChangeWS[] orderChanges) {
        return session.createOrderPreAuthorize(order, orderChanges);
    }
    
    public OrderWS[] getLinkedOrders(Integer primaryOrderId) {
    	return session.getLinkedOrders(primaryOrderId);
    }

    public Integer createUser(UserWS newUser) {
        return session.createUser(newUser);
    }

    public String deleteOrder(Integer id) {
        return session.deleteOrder(id);
    }

    public void deleteUser(Integer userId) {
        session.deleteUser(userId);
    }

    public void initiateTermination(Integer userId, String reasonCode, Date terminationDate) {
        session.initiateTermination(userId, reasonCode, terminationDate);
    }
    public boolean userExistsWithName(String userName) {
        return session.userExistsWithName(userName);
    }

    public boolean userExistsWithId(Integer userId) {
        return session.userExistsWithId(userId);
    }

    public void deleteInvoice(Integer invoiceId) {
        session.deleteInvoice(invoiceId);
    }

    public Integer saveLegacyInvoice(InvoiceWS invoiceWS) {
        return session.saveLegacyInvoice(invoiceWS);
    }

    public Integer saveLegacyPayment(PaymentWS paymentWS) {
        return session.saveLegacyPayment(paymentWS);
    }

    public Integer saveLegacyOrder(OrderWS orderWS) {
        return session.saveLegacyOrder(orderWS);
    }

    public ItemDTOEx[] getAllItems() {
        return session.getAllItems();
    }

    public InvoiceWS getInvoiceWS(Integer invoiceId) {
        return session.getInvoiceWS(invoiceId);
    }

    public Integer[] getInvoicesByDate(String since, String until) {
        return session.getInvoicesByDate(since, until);
    }

    public byte[] getPaperInvoicePDF(Integer invoiceId) {
        return session.getPaperInvoicePDF(invoiceId);
    }

    public boolean notifyInvoiceByEmail(Integer invoiceId) {
        return session.notifyInvoiceByEmail(invoiceId);
    }

    public boolean notifyPaymentByEmail(Integer paymentId) {
        return session.notifyPaymentByEmail(paymentId);
    }
    
    public Integer[] getLastInvoices(Integer userId, Integer number) {
        return session.getLastInvoices(userId, number);
    }

    public Integer[] getUserInvoicesByDate(Integer userId, String since, String until) {
        return session.getUserInvoicesByDate(userId, since, until);
    }

    public InvoiceWS[] getUserInvoicesPage(Integer userId, Integer limit, Integer offset) {
        return session.getUserInvoicesPage(userId, limit,offset);
    }

    public Integer[] getUnpaidInvoices(Integer userId) {
        return session.getUnpaidInvoices(userId);
    }

    public Integer[] getLastInvoicesByItemType(Integer userId, Integer itemTypeId, Integer number) {
        return session.getLastInvoicesByItemType(userId, itemTypeId, number);
    }

    public Integer[] getLastOrders(Integer userId, Integer number) {
        return session.getLastOrders(userId, number);
    }

    public Integer[] getLastOrdersPage(Integer userId, Integer limit, Integer offset) {
        return session.getLastOrdersPage(userId, limit, offset);
    }

    public Integer[] getOrdersByDate(Integer userId, Date since, Date until) {
        return session.getOrdersByDate(userId, since, until);
    }


    public Integer[] getLastOrdersByItemType(Integer userId, Integer itemTypeId, Integer number) {
        return session.getLastOrdersByItemType(userId, itemTypeId, number);
    }

    public OrderWS[] getUserOrdersPage(Integer user, Integer limit, Integer offset) {
        return session.getUserOrdersPage(user, limit, offset);
    }

    public OrderWS getCurrentOrder(Integer userId, Date date) {
        return session.getCurrentOrder(userId, date);
    }

    public OrderWS updateCurrentOrder(Integer userId, OrderLineWS[] lines, PricingField[] fields, Date date,
                                      String eventDescription) {

        return session.updateCurrentOrder(userId, lines, PricingField.setPricingFieldsValue(fields), date,
                                          eventDescription);
    }

    public Integer[] getLastPayments(Integer userId, Integer number) {
        return session.getLastPayments(userId, number);
    }

    public Integer[] getLastPaymentsPage(Integer userId, Integer limit, Integer offset){
        return session.getLastPaymentsPage(userId, limit, offset);
    }

    public Integer[] getPaymentsByDate(Integer userId, Date since, Date until){
        return session.getPaymentsByDate(userId, since, until);
    }

    public PaymentWS getUserPaymentInstrument(Integer userId) {
        return session.getUserPaymentInstrument(userId);
    }

    public PaymentWS[] getUserPaymentsPage(Integer userId, Integer limit, Integer offset) {
        return session.getUserPaymentsPage(userId, limit, offset);
    }

    public Integer[] getAllInvoices(Integer userId) {
        return session.getAllInvoices(userId);
    }

    public InvoiceWS getLatestInvoice(Integer userId) {
        return session.getLatestInvoice(userId);
    }

    public InvoiceWS getLatestInvoiceByItemType(Integer userId, Integer itemTypeId) {
        return session.getLatestInvoiceByItemType(userId, itemTypeId);
    }

    public OrderWS getLatestOrder(Integer userId) {
        return session.getLatestOrder(userId);
    }

    public OrderWS getLatestOrderByItemType(Integer userId, Integer itemTypeId) {
        return session.getLatestOrderByItemType(userId, itemTypeId);
    }

    public PaymentWS getLatestPayment(Integer userId) {
        return session.getLatestPayment(userId);
    }

    public OrderWS getOrder(Integer orderId) {
        return session.getOrder(orderId);
    }

    public Integer[] getOrderByPeriod(Integer userId, Integer periodId) {
        return session.getOrderByPeriod(userId, periodId);
    }

    public OrderLineWS getOrderLine(Integer orderLineId) {
        return session.getOrderLine(orderLineId);
    }

    public PaymentWS getPayment(Integer paymentId) {
        return session.getPayment(paymentId);
    }

    public ContactWS[] getUserContactsWS(Integer userId) {
        return session.getUserContactsWS(userId);
    }

    public Integer getUserId(String username) {
        return session.getUserId(username);
    }

    public Integer getUserIdByEmail(String email){
        return session.getUserIdByEmail(email);
    }

    public UserTransitionResponseWS[] getUserTransitions(Date from, Date to) {
        return session.getUserTransitions(from, to);
    }

    public UserTransitionResponseWS[] getUserTransitionsAfterId(Integer id) {
        return session.getUserTransitionsAfterId(id);
    }

    public UserWS getUserWS(Integer userId) {
        return session.getUserWS(userId);
    }

    public Integer[] getUsersByStatus(Integer statusId, boolean in) {
        return session.getUsersByStatus(statusId, in);
    }

    public Integer[] getUsersInStatus(Integer statusId) {
        return session.getUsersInStatus(statusId);
    }

    public Integer[] getUsersNotInStatus(Integer statusId) {
        return session.getUsersNotInStatus(statusId);
    }

    public void createPaymentLink(Integer invoiceId, Integer paymentId) {
        session.createPaymentLink(invoiceId, paymentId);
    }

    public void removePaymentLink(Integer invoiceId, Integer paymentId) {
        session.removePaymentLink(invoiceId, paymentId);
    }

    public void removeAllPaymentLinks(Integer paymentId) {
        session.removeAllPaymentLinks(paymentId);
    }

    public PaymentAuthorizationDTOEx payInvoice(Integer invoiceId) {
        return session.payInvoice(invoiceId);
    }

    public Integer createPayment(PaymentWS payment) {
        return session.createPayment(payment);
    }

    public Integer[] createPayments(PaymentWS[] payments) {
        return session.createPayments(payments);
    }

    public void updatePayment(PaymentWS payment) {
        session.updatePayment(payment);
    }

    public void deletePayment(Integer paymentId) {
        session.deletePayment(paymentId);
    }

    public void updateOrder(OrderWS order, OrderChangeWS[] orderChanges) {
        session.updateOrder(order, orderChanges);
    }

    public void updateOrders(OrderWS[] orders, OrderChangeWS[] orderChanges) {
        session.updateOrders(orders, orderChanges);
    }

    public Integer createUpdateOrder(OrderWS order, OrderChangeWS[] orderChanges) {
        return session.createUpdateOrder(order, orderChanges);
    }

    public void updateOrderLine(OrderLineWS line) {
        session.updateOrderLine(line);
    }

    public void updateUser(UserWS user) {
        session.updateUser(user);
    }

    public void updateUserContact(Integer userId, ContactWS contact) {
        session.updateUserContact(userId, contact);
    }

    public ItemDTOEx getItem(Integer itemId, Integer userId, PricingField[] fields) {
        return session.getItem(itemId, userId, PricingField.setPricingFieldsValue(fields));
    }

    public ItemTypeWS[] getItemCategoriesByPartner(String partner, boolean parentCategoriesOnly) {
        return session.getItemCategoriesByPartner(partner, parentCategoriesOnly);
    }
    public ItemTypeWS[] getChildItemCategories(Integer itemTypeId) {
        return session.getChildItemCategories(itemTypeId);
    }

    public ItemDTOEx[] getAddonItems(Integer itemId) {
        return session.getAddonItems(itemId);
    }

    public OrderWS rateOrder(OrderWS order, OrderChangeWS[] orderChanges) {
        return session.rateOrder(order, orderChanges);
    }

    public OrderWS[] rateOrders(OrderWS orders[], OrderChangeWS[] orderChanges) {
        return session.rateOrders(orders, orderChanges);
    }

    public OrderChangeWS[] calculateSwapPlanChanges(OrderWS order, Integer existingPlanItemId, Integer swapPlanItemId, SwapMethod method, Date effectiveDate) {
        return session.calculateSwapPlanChanges(order, existingPlanItemId, swapPlanItemId, method, effectiveDate);
    }

    public void updateItem(ItemDTOEx item) {
        session.updateItem(item);
    }

    public void deleteItem(Integer itemId) {
        session.deleteItem(itemId);
    }

    public Integer[] createInvoice(Integer userId, boolean onlyRecurring) {
        return session.createInvoice(userId, onlyRecurring);
    }

    public Integer[] createInvoiceWithDate(Integer userId, Date billingDate, Integer dueDatePeriodId, Integer dueDatePeriodValue, boolean onlyRecurring) {
        return session.createInvoiceWithDate(userId, billingDate, dueDatePeriodId, dueDatePeriodValue, onlyRecurring);
    }

    public Integer createInvoiceFromOrder(Integer orderId, Integer invoiceId) {
        return session.createInvoiceFromOrder(orderId, invoiceId);
    }

    public Integer applyOrderToInvoice(Integer orderId, InvoiceWS invoiceWs) {
        return session.applyOrderToInvoice(orderId, invoiceWs);
    }

    public String isUserSubscribedTo(Integer userId, Integer itemId) {
        return session.isUserSubscribedTo(userId, itemId);
    }

    public Integer[] getUserItemsByCategory(Integer userId, Integer categoryId) {
        return session.getUserItemsByCategory(userId, categoryId);
    }

    public ItemDTOEx[] getItemByCategory(Integer itemTypeId) {
        return session.getItemByCategory(itemTypeId);
    }

	public ItemTypeWS getItemCategoryById(Integer id) {
		return session.getItemCategoryById(id);
	}

    public ItemTypeWS[] getAllItemCategories() {
        return session.getAllItemCategories();
    }

    public ValidatePurchaseWS validatePurchase(Integer userId, Integer itemId, PricingField[] fields) {
        return session.validatePurchase(userId, itemId, PricingField.setPricingFieldsValue(fields));
    }

    public ValidatePurchaseWS validateMultiPurchase(Integer userId, Integer[] itemIds, PricingField[][] fields) {
        String[] pricingFields = null;
        if (fields != null) {
            pricingFields = new String[fields.length];
            for (int i = 0; i < pricingFields.length; i++) {
                pricingFields[i] = PricingField.setPricingFieldsValue(fields[i]);
            }
        }
        return session.validateMultiPurchase(userId, itemIds, pricingFields);
    }

    public Integer getItemID(String productCode) {
        return session.getItemID(productCode);
    }

    public Integer createItemCategory(ItemTypeWS itemType) {
        return session.createItemCategory(itemType);
    }

    public void updateItemCategory(ItemTypeWS itemType) {
        session.updateItemCategory(itemType);
    }

    public void deleteItemCategory(Integer itemCategoryId) {
        session.deleteItemCategory(itemCategoryId);
    }
    
    public ItemTypeWS[] getAllItemCategoriesByEntityId(Integer entityId) {
    	return session.getAllItemCategoriesByEntityId(entityId);
    }
    
    public ItemDTOEx[] getAllItemsByEntityId(Integer entityId) {
    	return session.getAllItemsByEntityId(entityId);
    }

    public Integer getAutoPaymentType(Integer userId) {
        return session.getAuthPaymentType(userId);
    }

    public void setAutoPaymentType(Integer userId, Integer autoPaymentType, boolean use) {
        session.setAuthPaymentType(userId, autoPaymentType, use);
    }

    public void resetPassword(int userId) {
        session.resetPassword(userId);
    }

    /*
        Billing process
     */

    public void triggerBillingAsync(Date runDate) {
        session.triggerBillingAsync(runDate);
    }

    public boolean triggerBilling(Date runDate) {
        return session.triggerBilling(runDate);
    }


    public void triggerAgeing(Date runDate) {
        session.triggerAgeing(runDate);
    }

    public boolean isAgeingProcessRunning() {
        return session.isAgeingProcessRunning();
    }

    public ProcessStatusWS getAgeingProcessStatus() {
        return session.getAgeingProcessStatus();
    }

    public BillingProcessConfigurationWS getBillingProcessConfiguration() {
        return session.getBillingProcessConfiguration();
    }

    public Integer createUpdateBillingProcessConfiguration(BillingProcessConfigurationWS ws) {
        return session.createUpdateBillingProcessConfiguration(ws);
    }

    public Integer createUpdateCommissionProcessConfiguration(CommissionProcessConfigurationWS ws){
        return session.createUpdateCommissionProcessConfiguration(ws);
    }

    public void calculatePartnerCommissions(){
        session.calculatePartnerCommissions();
    }

    public void calculatePartnerCommissionsAsync(){
        session.calculatePartnerCommissionsAsync();
    }

    public boolean isPartnerCommissionRunning() {
        return session.isPartnerCommissionRunning();
    }

    public CommissionProcessRunWS[] getAllCommissionRuns(){
        return session.getAllCommissionRuns();
    }

    public CommissionWS[] getCommissionsByProcessRunId(Integer processRunId){
        return session.getCommissionsByProcessRunId(processRunId);
    }

    public BillingProcessWS getBillingProcess(Integer processId) {
        return session.getBillingProcess(processId);
    }

    public Integer getLastBillingProcess() {
        return session.getLastBillingProcess();
    }

    public  OrderProcessWS[] getOrderProcesses(Integer orderId) {
        return session.getOrderProcesses(orderId);
    }

    public OrderProcessWS[] getOrderProcessesByInvoice(Integer invoiceId) {
        return session.getOrderProcessesByInvoice(invoiceId);
    }

    public BillingProcessWS getReviewBillingProcess() {
        return session.getReviewBillingProcess();
    }

    public BillingProcessConfigurationWS setReviewApproval(Boolean flag) {
        return session.setReviewApproval(flag);
    }

    public Integer[] getBillingProcessGeneratedInvoices(Integer processId) {
        return session.getBillingProcessGeneratedInvoices(processId);
    }

    public AgeingWS[] getAgeingConfiguration(Integer languageId) {
        return session.getAgeingConfiguration(languageId);
    }

    public void saveAgeingConfiguration(AgeingWS[] steps, Integer languageId) {
        session.saveAgeingConfiguration(steps, languageId);
    }

    /*
       Mediation process
    */

    public void triggerMediation() {
        session.triggerMediation();
    }

    public void undoMediation(UUID processId) {
        session.undoMediation(processId);
    }

    public UUID triggerMediationByConfiguration(Integer cfgId) {
        return session.triggerMediationByConfiguration(cfgId);
    }

    public UUID launchMediation(Integer mediationCfgId, String jobName, File file){
        return session.launchMediation(mediationCfgId, jobName, file);
    }

    public boolean isMediationProcessRunning() {
        return session.isMediationProcessRunning();
    }

    public ProcessStatusWS getMediationProcessStatus() {
        return session.getMediationProcessStatus();
    }

    public MediationProcess getMediationProcess(UUID mediationProcessId) {
        return session.getMediationProcess(mediationProcessId);
    }

    public MediationProcess[] getAllMediationProcesses() {
        return session.getAllMediationProcesses();
    }

    public JbillingMediationRecord[] getMediationEventsForOrder(Integer orderId) {
        return session.getMediationEventsForOrder(orderId);
    }
    
    public JbillingMediationRecord[] getMediationEventsForOrderDateRange(Integer orderId,
    		Date startDate, Date endDate, int offset, int limit) {
    	return session.getMediationEventsForOrderDateRange(orderId, startDate, endDate, offset, limit);
    }

    public JbillingMediationRecord[] getMediationEventsForInvoice(Integer invoiceId) {
        return session.getMediationEventsForInvoice(invoiceId);
    }

    public JbillingMediationRecord[] getMediationRecordsByMediationProcess(UUID mediationProcessId, Integer page, Integer size, Date startDate, Date endDate) {
        return session.getMediationRecordsByMediationProcess(mediationProcessId, page, size, startDate, endDate);
    }

    public RecordCountWS[] getNumberOfMediationRecordsByStatuses() {
        return session.getNumberOfMediationRecordsByStatuses();
    }

    public RecordCountWS[] getNumberOfMediationRecordsByStatusesByMediationProcess(UUID mediationProcess){
        return session.getNumberOfMediationRecordsByStatusesByMediationProcess(mediationProcess);
    }

    public MediationConfigurationWS[] getAllMediationConfigurations() {
        return session.getAllMediationConfigurations();
    }

    public Integer createMediationConfiguration(MediationConfigurationWS cfg) {
        return session.createMediationConfiguration(cfg);
    }

    public Integer[] updateAllMediationConfigurations(List<MediationConfigurationWS> configurations) {
        return session.updateAllMediationConfigurations(configurations);
    }

    public void deleteMediationConfiguration(Integer cfgId) {
        session.deleteMediationConfiguration(cfgId);
    }

    public JbillingMediationErrorRecord[] getMediationErrorRecordsByMediationProcess(UUID mediationProcessId, Integer mediationRecordStatusId) {
        return session.getMediationErrorRecordsByMediationProcess(mediationProcessId, mediationRecordStatusId);
    }


    /*
       Provisioning process
    */

    public void triggerProvisioning() {
        session.triggerProvisioning();
    }

    public void updateOrderAndLineProvisioningStatus(Integer inOrderId, Integer inLineId, String result) {
        session.updateOrderAndLineProvisioningStatus(inOrderId, inLineId, result);
    }

    public void updateLineProvisioningStatus(Integer orderLineId, Integer provisioningStatus) {
        session.updateLineProvisioningStatus(orderLineId, provisioningStatus);
    }


    public ProvisioningCommandWS[] getProvisioningCommands(ProvisioningCommandType typeId, Integer Id) {
        return session.getProvisioningCommands(typeId, Id);
    }

    public ProvisioningCommandWS getProvisioningCommandById(Integer provisioningCommandId) {
        return session.getProvisioningCommandById(provisioningCommandId);
    }

    public ProvisioningRequestWS[] getProvisioningRequests(Integer provisioningCommandId) {
        return session.getProvisioningRequests(provisioningCommandId);
    }

    public ProvisioningRequestWS getProvisioningRequestById(Integer provisioningRequestId) {
        return session.getProvisioningRequestById(provisioningRequestId);
    }

    /*
        Preferences
     */

    public void updatePreferences(PreferenceWS[] prefList) {
        session.updatePreferences(prefList);
    }

    public void updatePreference(PreferenceWS preference) {
        session.updatePreference(preference);
    }

    public PreferenceWS getPreference(Integer preferenceTypeId) {
        return session.getPreference(preferenceTypeId);
    }


    /*
        Currencies
     */

    public CurrencyWS[] getCurrencies() {
        return session.getCurrencies();
    }

    public void updateCurrencies(CurrencyWS[] currencies) {
        session.updateCurrencies(currencies);
    }

    public void updateCurrency(CurrencyWS currency) {
        session.updateCurrency(currency);
    }

    public Integer createCurrency(CurrencyWS currency) {
        return session.createCurrency(currency);
    }


    /*
       Plug-ins
    */

    public PluggableTaskWS getPluginWS(Integer pluginId) {
        return session.getPluginWS(pluginId);
    }

    public PluggableTaskWS[] getPluginsWS(Integer entityId, String className){
        return session.getPluginsWS(entityId, className);
    }

    public Integer createPlugin(PluggableTaskWS plugin) {
        return session.createPlugin(plugin);
    }

    public void updatePlugin(PluggableTaskWS plugin) {
        session.updatePlugin(plugin);
    }

    public void deletePlugin(Integer plugin) {
        session.deletePlugin(plugin);
    }
    
    /*                                                                     
     * Quartz jobs                                                         
     */                                                                    
    public void rescheduleScheduledPlugin(Integer pluginId) {              
    	session.rescheduleScheduledPlugin(pluginId);                        
    }

    public void triggerScheduledTask(Integer pluginId, Date date){session.triggerScheduledTask(pluginId, date);}


    /*
        Plans and special pricing
     */

    public PlanWS getPlanWS(Integer planId) {
        return session.getPlanWS(planId);
    }

    public PlanWS[] getAllPlans() {
        return session.getAllPlans();
    }

    public Integer createPlan(PlanWS plan) {
        return session.createPlan(plan);
    }

    public void updatePlan(PlanWS plan) {
        session.updatePlan(plan);
    }

    public void deletePlan(Integer planId) {
        session.deletePlan(planId);
    }

    public void addPlanPrice(Integer planId, PlanItemWS price) {
        session.addPlanPrice(planId, price);
    }

    public boolean isCustomerSubscribed(Integer planId, Integer userId) {
        return session.isCustomerSubscribed(planId, userId);
    }

    public boolean isCustomerSubscribedForDate(Integer planId, Integer userId, Date eventDate) {
        return session.isCustomerSubscribedForDate(planId, userId, eventDate);
    }

    public Integer[] getSubscribedCustomers(Integer planId) {
        return session.getSubscribedCustomers(planId);
    }

    public Integer[] getPlansBySubscriptionItem(Integer itemId) {
        return session.getPlansBySubscriptionItem(itemId);
    }

    public Integer[] getPlansByAffectedItem(Integer itemId) {
        return session.getPlansByAffectedItem(itemId);
    }

    public Usage getItemUsage(Integer excludedOrderId, Integer itemId, Integer owner, List<Integer> userIds , Date startDate, Date endDate) {
        return session.getItemUsage(excludedOrderId, itemId, owner, userIds, startDate, endDate);
    }

    public PlanItemWS createCustomerPrice(Integer userId, PlanItemWS planItem, Date expiryDate) {
        return session.createCustomerPrice(userId, planItem, expiryDate);
    }

    public void updateCustomerPrice(Integer userId, PlanItemWS planItem, Date expiryDate) {
        session.updateCustomerPrice(userId, planItem, expiryDate);
    }

    public void deleteCustomerPrice(Integer userId, Integer planItemId) {
        session.deleteCustomerPrice(userId, planItemId);
    }

    public PlanItemWS[] getCustomerPrices(Integer userId) {
        return session.getCustomerPrices(userId);
    }

    public PlanItemWS getCustomerPrice(Integer userId, Integer itemId) {
        return session.getCustomerPrice(userId, itemId);
    }

    public PlanItemWS getCustomerPriceForDate(Integer userId, Integer itemId, Date pricingDate, Boolean planPricingOnly) {
        return session.getCustomerPriceForDate(userId, itemId, pricingDate, planPricingOnly);
    }

    /*
        Assets
     */

    public Integer createAsset(AssetWS asset) {
        return session.createAsset(asset);
    }

    public void updateAsset(AssetWS asset) {
        session.updateAsset(asset);
    }

    public AssetWS getAsset(Integer assetId) {
        return session.getAsset(assetId);
    }

    public void deleteAsset(Integer assetId) {
        session.deleteAsset(assetId);
    }

    public Integer[] getAssetsForCategory(Integer categoryId) {
        return session.getAssetsForCategory(categoryId);
    }

    public Integer[] getAssetsForItem(Integer itemId) {
        return session.getAssetsForItem(itemId);
    }

    public AssetTransitionDTOEx[] getAssetTransitions(Integer assetId) {
        return session.getAssetTransitions(assetId);
    }

    public Long startImportAssetJob(int itemId, String idColumnName, String notesColumnName, String globalColumnName,String entitiesColumnName, String sourceFilePath, String errorFilePath) {
        return session.startImportAssetJob(itemId, idColumnName, notesColumnName, globalColumnName, entitiesColumnName, sourceFilePath, errorFilePath);
    }

    public AssetSearchResult findAssets(int productId, SearchCriteria criteria) {
        return session.findAssets(productId, criteria);
    }

	public AssetAssignmentWS[] getAssetAssignmentsForAsset(Integer assetId) {
		return session.getAssetAssignmentsForAsset(assetId);
	}

	public AssetAssignmentWS[] getAssetAssignmentsForOrder(Integer orderId) {
		return session.getAssetAssignmentsForOrder(orderId);
	}

	public Integer findOrderForAsset(Integer assetId, Date date) {
		return session.findOrderForAsset(assetId, date);
	}

	public Integer[] findOrdersForAssetAndDateRange(Integer assetId, Date startDate, Date endDate) {
		return session.findOrdersForAssetAndDateRange(assetId, startDate, endDate);
	}

    public AssetWS[] findAssetsByProductCode(String productCode){
        return session.findAssetsByProductCode(productCode);
    }
    public AssetStatusDTOEx[] findAssetStatuses(String identifier){
        return session.findAssetStatuses(identifier);
    }
    public AssetWS findAssetByProductCodeAndIdentifier(String productCode, String identifier){
        return session.findAssetByProductCodeAndIdentifier(productCode, identifier);
    }

    public AssetWS[] findAssetsByProductCodeAndStatus(String productCode, Integer assetStatusId){
        return session.findAssetsByProductCodeAndStatus(productCode, assetStatusId);
    }

    public PlanItemWS createAccountTypePrice(Integer accountTypeId, PlanItemWS planItem, Date expiryDate) {
        return session.createAccountTypePrice(accountTypeId, planItem, expiryDate);
    }

    public void updateAccountTypePrice(Integer accountTypeId, PlanItemWS planItem, Date expiryDate) {
        session.updateAccountTypePrice(accountTypeId, planItem, expiryDate);
    }

    public void deleteAccountTypePrice(Integer accountTypeId, Integer planItemId) {
        session.deleteAccountTypePrice(accountTypeId, planItemId);
    }

    public PlanItemWS[] getAccountTypePrices(Integer accountTypeId) {
        return session.getAccountTypePrices(accountTypeId);
    }

    public PlanItemWS getAccountTypePrice(Integer accountTypeId, Integer itemId) {
        return session.getAccountTypePrice(accountTypeId, itemId);
    }

    public BigDecimal getTotalRevenueByUser(Integer userId) {
        return session.getTotalRevenueByUser(userId);
    }

    public CompanyWS getCompany() {
        return session.getCompany();
    }

    public CompanyWS[] getCompanies() {
        return session.getCompanies();
    }

    public Integer getCallerCompanyId() {
        return session.getCallerCompanyId();
    }

    public Integer getCallerId() {
        return session.getCallerId();
    }

    public Integer getCallerLanguageId() {
        return session.getCallerLanguageId();
    }

    public Integer getCallerCurrencyId() {
        return session.getCallerCurrencyId();
    }
    
    public InvoiceWS[] getAllInvoicesForUser(Integer userId) {
        return session.getAllInvoicesForUser(userId);
    }

    public OrderWS[] getUserSubscriptions(Integer userId) {
        return session.getUserSubscriptions(userId);
    }

    public boolean deleteOrderPeriod(Integer periodId) {
        return session.deleteOrderPeriod(periodId);
    }

    public boolean isBillingRunning(Integer entityId) {
        return session.isBillingRunning(entityId);
    }
    public boolean updateOrderPeriods(OrderPeriodWS[] orderPeriods) {
        return session.updateOrderPeriods(orderPeriods);
    }

    public boolean updateOrCreateOrderPeriod(OrderPeriodWS orderPeriod) {
        return session.updateOrCreateOrderPeriod(orderPeriod);
    }

    public Integer createAccountType(AccountTypeWS accountType){
        return session.createAccountType(accountType);
    }
    public boolean updateAccountType(AccountTypeWS accountType) {
        return session.updateAccountType(accountType);
    }
    public AccountTypeWS getAccountType(Integer accountTypeId) {
        return session.getAccountType(accountTypeId);
    }
    public AccountTypeWS[] getAllAccountTypes() {
        return session.getAllAccountTypes();
    }

    public boolean deleteAccountType(Integer accountTypeId){
        return session.deleteAccountType(accountTypeId);
    }

    public AccountInformationTypeWS[] getInformationTypesForAccountType(Integer accountTypeId) {
        return session.getInformationTypesForAccountType(accountTypeId);
    }

    public Integer createAccountInformationType(AccountInformationTypeWS accountInformationType) {
        return session.createAccountInformationType(accountInformationType);
    }

    public void updateAccountInformationType(AccountInformationTypeWS accountInformationType) {
        session.updateAccountInformationType(accountInformationType);
    }

    public boolean deleteAccountInformationType(Integer accountInformationTypeId) {
        return session.deleteAccountInformationType(accountInformationTypeId);
    }

    public AccountInformationTypeWS getAccountInformationType(Integer accountInformationType) {
        return session.getAccountInformationType(accountInformationType);
    }

    
    public void createUpdateNotification(Integer messageId, MessageDTO dto) {
        session.createUpdateNotification(messageId, dto);
    }


    public void updateCompany(CompanyWS companyWS) {
        session.updateCompany(companyWS);
    }

    public Integer createOrUpdateDiscount(DiscountWS discount) {
        return session.createOrUpdateDiscount(discount);
    }
    public DiscountWS getDiscountWS(Integer discountId) {
        return session.getDiscountWS(discountId);
    }
    public void deleteDiscount(Integer discountId) {
        session.deleteDiscount(discountId);
    }

    public ProcessStatusWS getBillingProcessStatus() {
        return session.getBillingProcessStatus();
    }
    
    public OrderWS processJMRData(
            UUID processId, String recordKey, Integer userId,
            Integer currencyId, Date eventDate, String description,
            Integer productId, String quantity, String pricing) {
        
        return session.processJMRData(processId, recordKey, userId, currencyId, eventDate, description, productId, quantity, pricing);
    }

    public OrderWS processJMRRecord(UUID processId, JbillingMediationRecord JMR) {
    	return session.processJMRRecord(processId, JMR);
    }

    public UUID processCDR(Integer configId, List<String> callDataRecords) {
    	return session.processCDR(configId, callDataRecords);
    }
    
    public UUID runRecycleForConfiguration(Integer configId) {
    	return session.runRecycleForConfiguration(configId);
    }

    @Override
    public UUID runRecycleForProcess(UUID processId) {
        return session.runRecycleForMediationProcess(processId);
    }

    public void createCustomerNote(CustomerNoteWS note)
    {
          session.createCustomerNote(note);
    }

    public Integer createMetaFieldGroup(MetaFieldGroupWS metafieldGroup) {
       return  session.createMetaFieldGroup(metafieldGroup);
    }

	public MetaFieldGroupWS getMetaFieldGroup(Integer metafieldGroupId) {
		return session.getMetaFieldGroup(metafieldGroupId);
		
	}
	
	public void updateMetaFieldGroup(MetaFieldGroupWS metafieldGroupWs) {
		session.updateMetaFieldGroup(metafieldGroupWs);
		
	}

	
	public void deleteMetaFieldGroup(Integer metafieldGroupId) {
		session.deleteMetaFieldGroup(metafieldGroupId);
		
	}

    public Integer createMetaField(MetaFieldWS metafieldWS) {
        return  session.createMetaField(metafieldWS);
     }
    
	public void updateMetaField(MetaFieldWS metafieldWs){
		session.updateMetaField(metafieldWs);
	}
	
	public void deleteMetaField(Integer metafieldId){
		session.deleteMetaField(metafieldId);
		
	}
	
	public MetaFieldWS getMetaField(Integer metafieldId){
		return session.getMetaField(metafieldId);
	}

    public MetaFieldGroupWS[] getMetaFieldGroupsForEntity(String entityType) {
        return session.getMetaFieldGroupsForEntity(entityType);
    }

    public MetaFieldWS[] getMetaFieldsForEntity(String entityType) {
        return session.getMetaFieldsForEntity(entityType);
    }

    public OrderPeriodWS[] getOrderPeriods(){
        return session.getOrderPeriods();
    }
    /*
       Diameter Protocol
    */

    public OrderChangeStatusWS[] getOrderChangeStatusesForCompany() {
        return session.getOrderChangeStatusesForCompany();
    }

    public Integer createOrderChangeStatus(OrderChangeStatusWS orderChangeStatusWS) throws SessionInternalError {
        return session.createOrderChangeStatus(orderChangeStatusWS);
    }

    public void updateOrderChangeStatus(OrderChangeStatusWS orderChangeStatusWS) throws SessionInternalError {
        session.updateOrderChangeStatus(orderChangeStatusWS);
    }

    public void deleteOrderChangeStatus(Integer id) throws SessionInternalError {
        session.deleteOrderChangeStatus(id);
    }

    public void saveOrderChangeStatuses(OrderChangeStatusWS[] orderChangeStatuses) throws SessionInternalError {
        session.saveOrderChangeStatuses(orderChangeStatuses);
    }

    public OrderChangeWS[] getOrderChanges(Integer orderId) {
        return session.getOrderChanges(orderId);
    }

    public OrderChangeTypeWS[] getOrderChangeTypesForCompany() {
        return session.getOrderChangeTypesForCompany();
    }

    public OrderChangeTypeWS getOrderChangeTypeByName(String name) {
        return session.getOrderChangeTypeByName(name);
    }

    public OrderChangeTypeWS getOrderChangeTypeById(Integer orderChangeTypeId) {
        return session.getOrderChangeTypeById(orderChangeTypeId);
    }

    public Integer createUpdateOrderChangeType(OrderChangeTypeWS orderChangeTypeWS) {
        return session.createUpdateOrderChangeType(orderChangeTypeWS);
    }

    public void deleteOrderChangeType(Integer orderChangeTypeId) {
        session.deleteOrderChangeType(orderChangeTypeId);
    }
    
    public Integer createUsagePool(UsagePoolWS usagePool) {
        return session.createUsagePool(usagePool);
    }
    
    public void updateUsagePool(UsagePoolWS usagePool) {
        session.updateUsagePool(usagePool);
    }
    public UsagePoolWS getUsagePoolWS(Integer usagePoolId) {
        return session.getUsagePoolWS(usagePoolId);
    }
    public boolean deleteUsagePool(Integer usagePoolId) {
        return session.deleteUsagePool(usagePoolId);
    }
    
    public UsagePoolWS[] getAllUsagePools() {
        return session.getAllUsagePools();
    }
    
    public UsagePoolWS[] getUsagePoolsByPlanId(Integer planId) {
    	return session.getUsagePoolsByPlanId(planId);
    }
    
    public CustomerUsagePoolWS getCustomerUsagePoolById(Integer customerUsagePoolId) {
        return session.getCustomerUsagePoolById(customerUsagePoolId);
    }
    
    public CustomerUsagePoolWS[] getCustomerUsagePoolsByCustomerId(Integer customerId) {
    	return session.getCustomerUsagePoolsByCustomerId(customerId);
    }

    /*
       Diameter Protocol
    */

    public DiameterResultWS createSession(String sessionId, Date timestamp, BigDecimal units,
    		PricingField[] data) throws SessionInternalError {
            return session.createSession(sessionId, timestamp, units,
                    		PricingField.setPricingFieldsValue(data));
    }

    public DiameterResultWS reserveUnits(String sessionId, Date timestamp, int units,
    		PricingField[] data) throws SessionInternalError {
        return session.reserveUnits(sessionId, timestamp, units, 
        		PricingField.setPricingFieldsValue(data));
    }

    public DiameterResultWS updateSession(String sessionId, Date timestamp, BigDecimal usedUnits,
    		BigDecimal reqUnits, PricingField[] data) throws SessionInternalError {
        return session.updateSession(sessionId, timestamp, usedUnits, reqUnits, 
        		PricingField.setPricingFieldsValue(data));
    }

    public DiameterResultWS extendSession(String sessionId, Date timestamp, BigDecimal usedUnits,
    		BigDecimal reqUnits) throws SessionInternalError {
        return session.extendSession(sessionId, timestamp, usedUnits, reqUnits);
    }

    public DiameterResultWS endSession(String sessionId, Date timestamp, BigDecimal usedUnits,
    		int causeCode) throws SessionInternalError {
    	return session.endSession(sessionId, timestamp, usedUnits, causeCode);
    }

    public DiameterResultWS consumeReservedUnits(String sessionId, Date timestamp, int usedUnits,
    		int causeCode) throws SessionInternalError {
    	return session.consumeReservedUnits(sessionId, timestamp, usedUnits, causeCode);
    }

    @Override
    public Integer createRoute(RouteWS routeWS, File routeFile) {
        return session.createRoute(routeWS,routeFile);
    }

    @Override
    public void deleteRoute(Integer routeId) {
        session.deleteRoute(routeId);
    }

    public RouteWS getRoute(Integer routeId) {
        return session.getRoute(routeId);
    }

    public Integer createMatchingField(MatchingFieldWS matchingFieldWS) {
        return session.createMatchingField(matchingFieldWS);
    }


    public void deleteMatchingField(Integer matchingFieldId) {
        session.deleteMatchingField(matchingFieldId);
    }

    public MatchingFieldWS getMatchingField(Integer matchingFieldId) {
        return  session.getMatchingField(matchingFieldId);
    }

    public boolean updateMatchingField(MatchingFieldWS matchingFieldWS){
        return  session.updateMatchingField(matchingFieldWS);
    }


    public Integer createRouteRateCard(RouteRateCardWS routeRateCardWS, File routeRateCardFile) {
        return session.createRouteRateCard(routeRateCardWS,routeRateCardFile);
    }

    public void deleteRouteRateCard(Integer routeId) {
        session.deleteRouteRateCard(routeId);
    }
    public void updateRouteRateCard(RouteRateCardWS routeRateCardWS, File routeRateCardFile){
        session.updateRouteRateCard(routeRateCardWS,routeRateCardFile);
    }

    public RouteRateCardWS getRouteRateCard(Integer routeRateCardId) {
        return session.getRouteRateCard(routeRateCardId);
    }

    public Integer createRouteRecord(RouteRecordWS record, Integer routeId) {
        return session.createRouteRecord(record, routeId );
    }

    public void updateRouteRecord(RouteRecordWS record, Integer routeId) {
        session.updateRouteRecord(record, routeId);
    }

    public void deleteRouteRecord(Integer routeId, Integer recordId) {
        session.deleteRouteRecord(routeId, recordId);
    }

    public SearchResultString searchDataTable(Integer routeId, SearchCriteria criteria) {
        return session.searchDataTable(routeId, criteria);
    }

    public Set<String> searchDataTableWithFilter(Integer routeId, String filters, String searchName) {
        return session.searchDataTableWithFilter(routeId, filters, searchName);
    }

    public String getRouteTable(Integer routeId) {
        return session.getRouteTable(routeId);
    }

    public Integer createDataTableQuery(DataTableQueryWS queryWS) {
        return session.createDataTableQuery(queryWS);
    }

    public DataTableQueryWS getDataTableQuery(int id) {
        return session.getDataTableQuery(id);
    }

    public void deleteDataTableQuery(int id) {
        session.deleteDataTableQuery(id);
    }

    public DataTableQueryWS[] findDataTableQueriesForTable(int routeId) {
        return session.findDataTableQueriesForTable(routeId);
    }

    public Integer createRatingUnit(RatingUnitWS ratingUnitWS) {
        return session.createRatingUnit(ratingUnitWS);
    }

    public void updateRatingUnit(RatingUnitWS ratingUnitWS) {
        session.updateRatingUnit(ratingUnitWS);
    }

    public boolean deleteRatingUnit(Integer ratingUnitId) {
        return session.deleteRatingUnit(ratingUnitId);
    }

    public RatingUnitWS getRatingUnit(Integer ratingUnitId) {
        return session.getRatingUnit(ratingUnitId);
    }

    public RatingUnitWS[] getAllRatingUnits() {
        return session.getAllRatingUnits();
    }
    
    /*
     *Payment Method 
     */
    public PaymentMethodTemplateWS getPaymentMethodTemplate(Integer templateId) {
    	return session.getPaymentMethodTemplate(templateId);
    }
    
    public Integer createPaymentMethodType(PaymentMethodTypeWS paymentMethod) {
    	return session.createPaymentMethodType(paymentMethod);
    }
    public void updatePaymentMethodType(PaymentMethodTypeWS paymentMethod){
        session.updatePaymentMethodType(paymentMethod);
    }
    public boolean deletePaymentMethodType(Integer paymentMethodTypeId){
        return session.deletePaymentMethodType(paymentMethodTypeId);
    }
    
    public PaymentMethodTypeWS getPaymentMethodType(Integer paymentMethodTypeId) {
    	return session.getPaymentMethodType(paymentMethodTypeId);
    }
    
    public boolean removePaymentInstrument(Integer instrumentId) {
    	return session.removePaymentInstrument(instrumentId);
    }

    /* Customizable order status 7375 */
	public void deleteOrderStatus(OrderStatusWS orderStatus) {
		session.deleteOrderStatus(orderStatus);
	}

	public Integer createUpdateOrderStatus(OrderStatusWS orderStatusWS)
			throws SessionInternalError {
		return session.createUpdateOrderStatus(orderStatusWS);
	}

	public OrderStatusWS findOrderStatusById(Integer orderStatusId) {
		return session.findOrderStatusById(orderStatusId);
	}
	
	public int getDefaultOrderStatusId(OrderStatusFlag flag, Integer entityId){
		return session.getDefaultOrderStatusId(flag, entityId);
	}

	public PluggableTaskTypeWS getPluginTypeWS(Integer id) {
		return session.getPluginTypeWS(id);
	}

	public PluggableTaskTypeWS getPluginTypeWSByClassName(String className) {
		return session.getPluginTypeWSByClassName(className);
	}

	public PluggableTaskTypeCategoryWS getPluginTypeCategory(Integer id) {
		return session.getPluginTypeCategory(id);
	}

	public PluggableTaskTypeCategoryWS getPluginTypeCategoryByInterfaceName(
			String interfaceName) {
		return session.getPluginTypeCategoryByInterfaceName(interfaceName);
	}
	
	public Integer[] createSubscriptionAccountAndOrder(Integer parentAccountId, 
			OrderWS order, boolean createInvoice, List<OrderChangeWS> orderChanges) {
    	return session.createSubscriptionAccountAndOrder(parentAccountId, order, createInvoice, orderChanges);
	}
	
    public OrderPeriodWS getOrderPeriodWS(Integer orderPeriodId) {
    	return session.getOrderPeriodWS(orderPeriodId);
    }
    
    public Integer createOrderPeriod(OrderPeriodWS orderPeriod) {
    	return session.createOrderPeriod(orderPeriod);
    }
    	public Long getMediationErrorRecordsCount(Integer mediationConfigurationId){
        return session.getMediationErrorRecordsCount(mediationConfigurationId);
    }

    public Integer reserveAsset(Integer assetId, Integer userId) {
        return this.session.reserveAsset(assetId, userId);
    }

    public void releaseAsset(Integer assetId, Integer userId) {
        this.session.releaseAsset(assetId, userId);
    }


    // Enumerations

    @Override
    public EnumerationWS getEnumeration(Integer enumerationId) throws SessionInternalError {
        return session.getEnumeration(enumerationId);
    }

    @Override
    public EnumerationWS getEnumerationByName(String name) throws SessionInternalError {
        return session.getEnumerationByName(name);
    }

    @Override
    public List<EnumerationWS> getAllEnumerations(Integer max, Integer offset) {
        return session.getAllEnumerations(max, offset);
    }

    @Override
    public Long getAllEnumerationsCount(){
        return session.getAllEnumerationsCount();
    }

    @Override
    public Integer createUpdateEnumeration(EnumerationWS enumerationWS) throws SessionInternalError{
        return session.createUpdateEnumeration(enumerationWS);
    }

    @Override
    public boolean deleteEnumeration(Integer enumerationId) throws SessionInternalError{
        return session.deleteEnumeration(enumerationId);
    }

    public UserWS copyCompany(String templateForChildCompany, Integer entityId, List<String> importEntities, boolean isCompanyChild, boolean copyProducts, boolean copyPlans) {
        return session.copyCompany(templateForChildCompany, entityId,importEntities, isCompanyChild, copyProducts, copyPlans);
    }

    @Override
    public CustomerEnrollmentWS getCustomerEnrollment(Integer customerEnrollmentId) throws SessionInternalError{
        return session.getCustomerEnrollment(customerEnrollmentId);
    }

    @Override
    public Integer createUpdateEnrollment(CustomerEnrollmentWS customerEnrollmentWS) throws SessionInternalError{
        return session.createUpdateEnrollment(customerEnrollmentWS);
    }

    @Override
    public CustomerEnrollmentWS validateCustomerEnrollment(CustomerEnrollmentWS customerEnrollmentWS) throws SessionInternalError{
       return session.validateCustomerEnrollment(customerEnrollmentWS);
    }


    @Override
    public void deleteEnrollment(Integer customerEnrollmentId) throws SessionInternalError{
        session.deleteEnrollment(customerEnrollmentId);
    }

    @Override
    public int generateEDIFile(Integer ediTypeId, Integer entityId, String fileName, Collection input) throws SessionInternalError {
        return session.generateEDIFile(ediTypeId, entityId, fileName, input);
    }

    @Override
    public int parseEDIFile(Integer ediTypeId, Integer entityId, File parserFile) throws SessionInternalError {
        return session.parseEDIFile(ediTypeId, entityId, parserFile);
    }

    @Override
    public Integer createEDIType(EDITypeWS ediTypeWS, File ediFormatFile) {
        return session.createEDIType(ediTypeWS, ediFormatFile);
    }

    @Override
    public void deleteEDIType(Integer ediTypeId) {
        session.deleteEDIType(ediTypeId);
    }

    @Override
    public EDITypeWS getEDIType(Integer ediTypeId) {
        return session.getEDIType(ediTypeId);
    }

    @Override
    public List<CompanyWS> getAllChildEntities(Integer parentId) throws SessionInternalError {
        return session.getAllChildEntities(parentId);
    }

    /*
	 * Payment Transfer
	 */
    @Override
    public void transferPayment(PaymentTransferWS paymentTransfer) {
        session.transferPayment(paymentTransfer);
    }

    @Override
    public void updateEDIStatus(EDIFileWS ediFileWS, EDIFileStatusWS statusWS) throws SessionInternalError{
         session.updateEDIStatus(ediFileWS, statusWS);
    }

    @Override
    public Integer createAdjustmentOrderAndInvoice(String customerPrimaryAccount, OrderWS order, OrderChangeWS[] orderChanges) {
        return session.createAdjustmentOrderAndInvoice(customerPrimaryAccount, order, orderChanges);
    }

    @Override
    public String createPaymentForHistoricalDateMigration(String customerPrimaryAccount, Integer chequePmId, String amount, String date) {
        return session.createPaymentForHistoricalDateMigration(customerPrimaryAccount, chequePmId, amount, date);
    }

    @Override
    public String adjustUserBalance(String customerPrimaryAccount, String amount, Integer chequePmId, String date) {
        return session.adjustUserBalance(customerPrimaryAccount, amount, chequePmId, date);
    }

    @Override
    public EDIFileStatusWS findEdiStatusById(Integer ediStatusId){
        return session.findEdiStatusById(ediStatusId);
    }
}
