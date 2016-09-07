package com.jbilling.framework.pageclasses;

import java.util.HashMap;

import org.testng.Assert;

import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.utilities.textutilities.TextUtilities;
import com.jbilling.framework.utilities.xmlutils.TestData;

public class CustomersPage {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@LocateBy(xpath = "//select[@name='mainSubscription.periodId']")
	private ElementField BT_BILLING_CYCLE_UNIT;

	@LocateBy(xpath = "//a[@id='companyBillingCycle']/span")
	private ElementField BT_BILLINGCYCLE;

	@LocateBy(xpath = "//select[@name='mainSubscription.nextInvoiceDayOfPeriod']")
	private ElementField BT_BILLINGCYCLEDAY;

	@LocateBy(xpath = "//strong[contains(text(), 'Bill 2')]")
	private ElementField BT_INVOICE;

	@LocateBy(xpath = "//label[text()='ach.customer.name']/../../div[2]/div/input")
	private ElementField TB_CUSTOMER_NAME;

	@LocateBy(xpath = "//label[text()='ach.routing.number']/../../div[1]/div/input")
	private ElementField TB_ROUTING_NUMBER;

	@LocateBy(xpath = "//label[text()='ach.account.number']/../../div[3]/div/input")
	private ElementField TB_ACCOUNT_NUMBER;

	@LocateBy(xpath = "//label[text()='ach.bank.name']/../../div[4]/div/input")
	private ElementField TB_BANK_NAME;

	@LocateBy(xpath = "//label[text()='ach.account.type']/../../div[5]/select")
	private ElementField DD_ACCOUNT_TYPE;
	
	@LocateBy(xpath = "//td[text()='Minimum Period:']/following::td[1]")
	private ElementField PERIOD_OPTION_VALUE;

	@LocateBy(xpath = "//table[@id='users']")
	private ElementField TAB_USER_TYPES_NAMES;

	@LocateBy(xpath = "//span[text()= 'Generate Invoice']")
	private ElementField GT_INVOICE;

	@LocateBy(css = ".submit.payment>span")
	private ElementField BT_MAKE;

	@LocateBy(xpath = "//label[text()='Allow Sub-Accounts']/preceding-sibling::input[@type='checkbox']")
	private ElementField CB_ALLOWSUBACCOUNT;

	@LocateBy(xpath = "//li[@aria-controls='ui-tabs-products']/a")
	private ElementField LT_PRODUCTS;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.accountExpired']")
	private ElementField CHB_AccountInactive;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.isAccountLocked']")
	private ElementField CHB_AccountLocked;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.isParent']")
	private ElementField CHB_Allow_Sub_Accounts;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//label[contains(text(),'Blacklisted')]/preceding-sibling::input[contains(@id,'metaField_')]")
	private ElementField CHB_Blacklist;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.createCredentials']")
	private ElementField CHB_CreateCredentials;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div[2]/div[2]/input[2]")
	private ElementField CHB_CreateOrderDetailsIncludeNotes;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div/div[11]/input[2]")
	private ElementField CHB_CreateOrderDetailsNotifyUser;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div/div[12]/div/input[2]")
	private ElementField CHB_CreateOrderDetailsProRate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.excludeAgeing']")
	private ElementField CHB_ExcludeCollection;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.invoiceChild']")
	private ElementField CHB_InvoiceIfChild;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.useParentPricing']")
	private ElementField CHB_UseParentPricing;

	@LocateBy(name = "asset.select.0")
	private ElementField CHK_SIM;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='mainSubscription.nextInvoiceDayOfPeriod']")
	private ElementField Customers_BillingCycleDay;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='mainSubscription.periodId']")
	private ElementField Customers_BillingCycleUnit;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[2]/div[2]/div[2]/div[1]/div[3]/div/input")
	private ElementField Customers_BusinessInfo_Address1;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[2]/div[2]/div[2]/div[1]/div[4]/div/input")
	private ElementField Customers_BusinessInfo_Address2;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[2]/div[2]/div[2]/div[1]/div/select")
	private ElementField Customers_BusinessInfo_Division;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[2]/div[2]/div[2]/div[1]/div[2]/div/input")
	private ElementField Customers_BusinessInfo_Name;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[contains(text(),'Business Information')]")
	private ElementField Customers_BusinessInfo_Section;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[2]/div[2]/div[2]/div[2]/div/select")
	private ElementField Customers_BusinessInfo_State;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//a[@class='btn-open']/span")
	private ElementField Customers_ContactInfoSection;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[1]/div[2]/div[2]/div[1]/div[4]/div/input[@class='field text field_usage']")
	private ElementField Customers_ContactInfoSection_City;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div[2]/div[1]/textarea")
	private ElementField Customers_CreateOrderDetails_NotesText;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='user.dueDateUnitId']")
	private ElementField Customers_DueDateUnit;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards-title'])[3]/a/span/label")
	private ElementField Customers_EditCustomers_PaymentMethod;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//textarea[@id='noteContent']")
	private ElementField Customers_Notes_NotesContent;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//a[@class='btn-open']/span[contains(text(),'Notes')]")
	private ElementField Customers_NotesSection;

	@LocateBy(xpath = "//select[@id='accountTypeId']")
	private ElementField DD_ACCOUNTTYPE;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='mainSubscription.periodId']")
	private ElementField DD_BILLING_CYCLE_UNIT;

	@LocateBy(xpath = "//select[@id='discountableItem.0.lineLevelDetails']")
	private ElementField DD_DISCOUNTABLEITEM;

	@LocateBy(xpath = "//select[@id='discount.0.id']")
	private ElementField DD_DISCOUNTS;

	@LocateBy(xpath = "//select[@id = 'billingTypeId']")
	private ElementField DD_ORDER;

	@LocateBy(xpath = "//select[@id='paymentMethod_0.paymentMethodTypeId']")
	private ElementField DD_PAYMENTMETHOD;

	@LocateBy(xpath = "//select[@id='user.automaticPaymentType']")
	private ElementField DD_PREFRDPAYMENT;
	
	@LocateBy(xpath = "//a[text()='Plans']")
	private ElementField PLANS_TAB;

	@LocateBy(xpath = "//select[@id = 'orderPeriod']")
	private ElementField DD_PERIOD;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='accountTypeId']")
	private ElementField Dropdown_AddCustomer_AccountType;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div/div[6]/span/div[2]/select")
	private ElementField Dropdown_CreateOrderDetails_DueDateUnit;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div/div[8]/select")
	private ElementField Dropdown_CreateOrderDetailsCancellationFees;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div/div[3]/select")
	private ElementField Dropdown_CreateOrderDetailsStatus;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div/div[2]/select")
	private ElementField Dropdown_CreateOrderDetailsType;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='user.currencyId']")
	private ElementField Dropdown_Currency;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div[1]/div/select")
	private ElementField Dropdown_CustomersCreateOrderDetailsPeriod;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='filters.CUSTOMER-EQ_Deleted.integerValue']")
	private ElementField Dropdown_Deleted;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@id='payment-methods']/div[2]/div/div/div/div/select")
	private ElementField Dropdown_EditCustomersPaymentType;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='user.invoiceDeliveryMethodId']")
	private ElementField Dropdown_InvoiceDeliveryMethod;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='user.invoiceTemplateId']")
	private ElementField Dropdown_InvoiceTemplate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='user.languageId']")
	private ElementField Dropdown_Language;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='user.automaticPaymentType']")
	private ElementField Dropdown_PaymentType;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[1]/div[2]/div[2]/div[2]/div/select")
	private ElementField Dropdown_State;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='user.statusId']")
	private ElementField Dropdown_Status;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='select-bg']/select")
	private ElementField DropDown_Status;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@name='user.subscriberStatusId']")
	private ElementField Dropdown_SubscriberStatus;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Confirm']")
	private ElementField Header_DeleteCustomerPopupConfirm;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Select']")
	private ElementField LT_AddCustomer;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='dropdown']/a/span")
	private ElementField LT_AddFilters;

	@LocateBy(xpath = "//div[@class='btn-box']/a/span")
	private ElementField LT_ADDNEW;

	@LocateBy(name = "//span[text()='Add to Order']")
	private ElementField LT_ADDORDER;

	@LocateBy(name = "//div[@id='buttons-id-add']/a//span[contains(text(),'Add to Order')]")
	private ElementField BT_ADDORDER;

	@LocateBy(name = "//span[text()='Add Selected']")
	private ElementField LT_ADDSELECTED;

	@LocateBy(xpath = "//span[text() = 'Add Sub-Account']")
	private ElementField LT_ALLOWSUBACCOUNT;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='btn-hold']/a/span")
	private ElementField LT_ApplyFilters;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='ui-dialog-content ui-widget-content']/div/div/fieldset/div")
	private ElementField LT_BusinessInfoPopUpDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='ui-dialog-buttonset']//button/span[contains(text(),'Save')]")
	private ElementField LT_BusinessInfoPopupSave;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[2]/div[2]/div/div[1]/div/ul/li[2]/a")
	private ElementField LT_BusinessInfoSection;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Cancel']")
	private ElementField LT_Cancel;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='ui-dialog-buttonset']//button/span[contains(text(),'Cancel')]")
	private ElementField LT_ContactInfoSection_Popup;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@id='timeline']/div/ul/li[2]/a")
	private ElementField LT_ContactInfoSectionAddDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='ui-dialog-buttonset']//button/span[contains(text(),'Save')]")
	private ElementField LT_ContactInfoSectionPopupSave;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Create Order']")
	private ElementField LT_CreateOrder;

	@LocateBy(xpath = "//span[text()= 'Create Order']")
	private ElementField LT_CREATEORDER;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Cancel']")
	private ElementField LT_CreateOrderCancel;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Save Changes']")
	private ElementField LT_CreateOrderSaveChanges;

	@LocateBy(xpath = "//span[text()='Current order']")
	private ElementField LT_CURRENT_ORDER;

	@LocateBy(xpath = "//strong[contains(text(), 'Customer A')]")
	private ElementField LT_CUSTOMER;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Delete']")
	private ElementField LT_DeleteCustomer;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='No']")
	private ElementField LT_DeleteCustomerPopupNo;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Yes']")
	private ElementField LT_DeleteCustomerPopupYes;

	@LocateBy(xpath = "//span[text()='Dependency']")
	private ElementField LT_DEPENDENCY_BUTTON;

	@LocateBy(xpath = "//li[@aria-controls='ui-tabs-discounts']/a")
	private ElementField LT_DISCOUNTS;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='download']/a")
	private ElementField LT_DownloadCSV;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Edit']")
	private ElementField LT_EditCustomer;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Add Instrument']")
	private ElementField LT_EditCustomers_AddInstrument;

	@LocateBy(xpath = "//table[@id='products']/tbody//td/a/strong")
	private ElementField LT_htmlWebTableRow;

	@LocateBy(xpath = "//table[@id='users']/tbody//td/a/strong")
	private ElementField LT_UsersWebTable;

	@LocateBy(xpath = "//table[@id='plans']/tbody//td/a/strong")
	private ElementField LT_PlansWebTable;
	
	@LocateBy(xpath = "//span[text()='Remove']")
	private ElementField BT_REMOVE;
	
	@LocateBy(xpath = "//a[text()='Products']")
	private ElementField PRODUCT_TAB;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Load']")
	private ElementField LT_Load;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Make Payment']")
	private ElementField LT_MakePayment;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//a[@class='submit add']/span")
	private ElementField LT_Notes_AddNotes;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='ui-dialog-buttonset']/button[2]/span")
	private ElementField LT_Notes_Cancel;

	@LocateBy(xpath = "//strong[contains(text(), 'Bill 2')]")
	private ElementField LT_PRODUCT;

	@LocateBy(xpath = "")
	private ElementField LT_RECENTCUSTOMER;

	@LocateBy(xpath = "//a[text()='Review']")
	private ElementField LT_REVIEW;
	
	

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='btn-hold']/a[2]/span")
	private ElementField LT_Save;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='buttons']/ul/li/a/span")
	private ElementField LT_SaveChanges;
	
	@LocateBy(xpath = "//input[@id='user.nextInvoiceDate']")
	private ElementField TB_NEXT_INCOICE_DATE;

	@LocateBy(xpath = "//span[text()='Save Changes']")
	private ElementField LT_SAVECHANGES;

	@LocateBy(xpath = "//span[text()='Select']")
	private ElementField LT_SELECT;

	@LocateBy(xpath = "//span[text()='Update']")
	private ElementField LT_UPDATE;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='ui-dialog-buttonset']//button/span[contains(text(),'Cancel')]")
	private ElementField LTBusinessInfoPopupCancel;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//ul[@role='tablist']")
	private ElementField Tab_CreateOrderInnerLeft;

	@LocateBy(xpath = "//strong[text()='Installation Fee']")
	private ElementField TAB_DEPENDENT_PRODUCT;

	@LocateBy(xpath = "//div[@id='builder-tabs']/ul//li[@aria-controls='ui-tabs-products']/a")
	private ElementField TAB_PRODUCTS;

	@LocateBy(xpath = "//a[@id='ui-id-9']")
	private ElementField TAB_PLAN;

	@LocateBy(xpath = "//table[@id='products']/tbody//td/a/strong")
	private ElementField TAB_LIST_PRODUCT;

	@LocateBy(xpath = "")
	private ElementField TAB_USERS;

	@LocateBy(xpath = "//label[text()='Active Since']/../div/input")
	private ElementField TB_ACTIVEDATE;

	@LocateBy(xpath = "//input[@id = 'user.partnerIdList']")
	private ElementField TB_AGENT;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.partnerIdList']")
	private ElementField TB_AgentId;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.autoRechargeAsDecimal']")
	private ElementField TB_AutoRechargeAmount;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.monthlyLimit']")
	private ElementField TB_AutoRechargeMonthlyLimit;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.rechargeThreshold']")
	private ElementField TB_AutoRechargeThreshold;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[2]/div[2]/div[2]/div[1]/div[5]/div/input")
	private ElementField TB_BusinessInfo;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[2]/div[2]/div[2]/div[2]/div[4]/div/input")
	private ElementField TB_BusinessInfoEmail;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[2]/div[2]/div[2]/div[2]/div[3]/div/input")
	private ElementField TB_BusinessInfoTelephone;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[2]/div[2]/div[2]/div[2]/div[2]/div/input")
	private ElementField TB_BusinessInfoZipCode;

	@LocateBy(xpath = "//div[@id = 'payment-method-fields-0']/div[3]/div/input")
	private ElementField TB_CARDEXPIRYDATE;

	@LocateBy(xpath = "//div[@id = 'payment-method-fields-0']/div/div/input")
	private ElementField TB_CARDHOLDERNAME;

	@LocateBy(xpath = "//div[@id = 'payment-method-fields-0']/div[2]/div/input")
	private ElementField TB_CARDNUMBER;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[contains(@class,'field text field_usage')]")
	private ElementField TB_ContactInfoSection;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[1]/div[2]/div[2]/div[1]/div[3]/div/input")
	private ElementField TB_ContactInfoSection_Address2;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[1]/div[2]/div[2]/div[1]/div[2]/div/input")
	private ElementField TB_ContactInfoSectionAddress1;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//input[@class='field hasDatepicker'])[1]")
	private ElementField TB_ContactInfoSectionPopUpDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[1]/div[2]/div[2]/div[2]/div[3]/div/input")
	private ElementField TB_ContactInfoSectionTelephone;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//div[@class='box-cards box-cards-open'])[1]/div[2]/div[2]/div[2]/div[2]/div/input")
	private ElementField TB_ContactInfoSectionZipCode;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div/div[4]/div/input")
	private ElementField TB_CreateOrderDetailsActiveSinceDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div/div[5]/div/input")
	private ElementField TB_CreateOrderDetailsActiveUntillDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div/div[6]/span/div/input")
	private ElementField TB_CreateOrderDetailsDueDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div/div[7]/span/div/input")
	private ElementField TB_CreateOrderDetailsMinimumPeriod;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//form[@id='order-details-form']/div/div[14]/span/div/input")
	private ElementField TB_CreateOrderDetailsUserCode;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.creditLimitAsDecimal']")
	private ElementField TB_CreditLimit;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='filters.CUSTOMER-DATE_BETWEEN_Customer_nextInvoiceDate.startDateValue']")
	private ElementField TB_DateFrom;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='filters.CUSTOMER-DATE_BETWEEN_Customer_nextInvoiceDate.endDateValue']")
	private ElementField TB_DateTo;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.dueDateValue']")
	private ElementField TB_DueDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@id='payment-methods']/div[2]/div/div/div[2]/div/div/input")
	private ElementField TB_EditCustomersCardHolderName;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@id='payment-methods']/div[2]/div/div/div[2]/div[2]/div/input")
	private ElementField TB_EditCustomersccNumber;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@id='payment-methods']/div[2]/div/div/div[2]/div[3]/div/input")
	private ElementField TB_EditCustomersExpireDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@id='payment-methods']/div[2]/div/div/div/div[2]/div/input")
	private ElementField TB_EditCustomersProcessingOrder;

	@LocateBy(xpath = "//label[text()='Effective Date']/../div/input")
	private ElementField TB_EFFECTIVE_DATE;

	@LocateBy(xpath = "//label[contains(text(),'Email')]/../div/input")
	private ElementField TB_EMAIL;

	@LocateBy(xpath = "//input[@id='filterBy']")
	private ElementField TB_FILTER_PRODUCTS_BY;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@class='{validate:{ digits: true }}']")
	private ElementField TB_IDField;

	@LocateBy(xpath = "//input[@id='user.userName']")
	private ElementField TB_LOGINNAME;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.userName']")
	private ElementField TB_LoginUserName;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='filters.CUSTOMER-LIKE_UserName.stringValue']")
	private ElementField TB_Name;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='noteTitle']")
	private ElementField TB_NotesTitle;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.parentId']")
	private ElementField TB_ParentId;

	@LocateBy(xpath = "//input[@id = 'change--3.quantityAsDecimal']")
	private ElementField TB_QUANTITY;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Use Company Billing Cycle']")
	private ElementField TB_UseBillingCycle;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@name='user.userCodeLink']")
	private ElementField TB_UserCode;

	@LocateBy(xpath = "")
	private ElementField TXT_CUSTOMER_STATUS;

	@LocateBy(xpath = "//table[@id='users']")
	private ElementField TAB_SAVED_CUSTOMER_NAME;

	@LocateBy(xpath = "//table[@id='users']/tbody//td/a/strong")
	private ElementField TAB_LIST_CUSTOMER_NAME;

	@LocateBy(xpath = "//div[@id='breadcrumbs']")
	private ElementField BREADCRUMBS;

	@LocateBy(id = "payment_amountAsDecimal")
	private ElementField TB_PAYMENT_AMOUNT;

	@LocateBy(xpath = "//label[text()='cheque.bank.name']/../../div[1]/div/input")
	private ElementField TB_CHEQUE_NAME;

	@LocateBy(xpath = "//label[text()='cheque.bank.name']/../../div[2]/div/input")
	private ElementField TB_CHEQUE_NUMBER;

	@LocateBy(xpath = "//label[text()='cheque.bank.name']/../../div[3]/div/input")
	private ElementField TB_CHEQUE_DATE;

	@LocateBy(xpath = "//a[@class='submit payment']")
	private ElementField LT_REVIEW_PAYMENT;

	@LocateBy(xpath = "//label[text()='Processing Order']/../div/input")
	private ElementField TB_PAYMENT_ORDER;

	@LocateBy(xpath = "//table[@id='payments']")
	private ElementField TAB_PAYMENTS;

	@LocateBy(xpath = "//div[@id='builder-tabs']/ul//li[@aria-controls='ui-tabs-products']/a")
	private ElementField TAB_PRODUCT;

	@LocateBy(xpath = "//input[@id='activeSince']")
	private ElementField DATE_FIELD;

	@LocateBy(xpath = "//table[@id='products']/tbody/tr[1]/td[1]/a/strong")
	private ElementField ADDED_PRODUCT_NAME;

	@LocateBy(xpath = "//span[text()='Create Asset']")
	private ElementField BT_CREATE_ASSET;

	@LocateBy(xpath = "//input[@id='identifier']")
	private ElementField TB_IDENTIFIER;

	@LocateBy(id = "global-checkbox")
	private ElementField CB_GLOBLE;

	@LocateBy(xpath = "//span[text()='Add to Order']")
	private ElementField BT_Add_TO_Order;

	@LocateBy(xpath = "//div[@id='review-messages']/div")
	private ElementField UPDATE_SUCCESSFUL_MESSAGE;

	@LocateBy(xpath = "//div[@id='review-tabs']/ul/li[@aria-controls='ui-tabs-review']/a")
	private ElementField TAB_REVIEW;

	@LocateBy(xpath = "//span[text()='Show Assets']")
	private ElementField BT_SHOW_ASSET;

	@LocateBy(xpath = "//ul[@class='top-nav']/li[1]")
	private ElementField COMPANY;

	@LocateBy(xpath = "//select[@id='company-select']")
	private ElementField DD_COMPANY;

	@LocateBy(xpath = "//form[@id='save-asset-form']/fieldset//div[@class='buttons']/ul/li//a[@class='submit save']")
	private ElementField SAVE_CHANGE_ON_ASSET;

	@LocateBy(xpath = "//table[@class='innerTable']/tbody/tr/td")
	private ElementField TABLE_ADDED_ASSERT;

	@LocateBy(xpath = "//span[text()='Reserve']")
	private ElementField BT_RESERVE;

	@LocateBy(xpath = "//span[text()='Cancel']")
	private ElementField BT_CANCEL;

	@LocateBy(xpath = "//div[@class='ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons ui-draggable ui-resizable']//strong[text()='Lemonade Plan Setup Fee']")
	private ElementField CLICK_DEPENDENCIES;

	@LocateBy(xpath = "//span[text()='New SubOrder']")
	private ElementField CLICK_SUBORDER;

	@LocateBy(id = "ui-id-4")
	public ElementField CLICK_REVIEW;

	@LocateBy(xpath = "//*[@id='order-details-form']/div[1]/div[4]/div/img")
	public ElementField CLICK_CALENDAR_ICON;

	@LocateBy(xpath = "//img[@alt='inspect customer']")
	public ElementField CLICK_CUSTOMER_SEARCH_ICON;

	@LocateBy(xpath = "//*[@id='change--3.description']")
	public ElementField CLICK_DESCRIPTION;

	@LocateBy(xpath = "//tr[2]/td[2]/a/strong")
	public ElementField CLICK_SECONDOPTION;

	@LocateBy(xpath = "//div[2]/div[7]/div[1]/a[3]/span")
	public ElementField CLICK_FIRST_EDIT_BUTTON;

	@LocateBy(xpath = "//*[@id='change--3-update-form']/div[2]/a[1]/span")
	public ElementField CLICK_UPDATE_ICON;

	@LocateBy(xpath = "//input[@id='product.activeSince']")
	private ElementField TB_START_DATE;

	@LocateBy(xpath = "//input[@id='activeSince']")
	private ElementField TB_START_DATE_IN_ORDER;

	@LocateBy(xpath = "//input[@id='product.activeUntil']")
	private ElementField TB_END_DATE;

	@LocateBy(xpath = "//input[@id='activeUntil']")
	private ElementField TB_END_DATE_IN_ORDER;

	@LocateBy(xpath = "//label[text()='Quantity']/following::input[1]")
	private ElementField TB_PRODUCT_QUANTITY;

	@LocateBy(xpath = "//ul[@id='review-lines']/form/li[1]//span[@class='description']")
	private ElementField ADDED_ORDER_BAR;

	@LocateBy(xpath = "//span[text()='Change']")
	private ElementField BT_CHANGE;

	@LocateBy(xpath = "//table[@id='users']/tbody/tr/td/a/strong")
	private ElementField TAB_CUSTOMER;
	
	@LocateBy(xpath = "//label[text()='Payment Method Type']/following::select[1]")
	private ElementField DD_PAYMENT_METHOD;
	
	@LocateBy(xpath = "//label[text()='cc.cardholder.name']/../../div[1]/div/input")
	private ElementField TB_CARDHOLDER_NAME;

	@LocateBy(xpath = "//label[text()='cc.number']/following::input[1]")
	private ElementField TB_CARDHOLDER_NUMBER;

	@LocateBy(xpath = "//label[text()='cc.expiry.date']/following::input[1]")
	private ElementField TB_CARDHOLDER_DATE;
	
	@LocateBy(xpath = "//a[text()='Discounts']")
	private ElementField TAB_DISCOUNT;
	
	@LocateBy(xpath = "(//table[@class='innerTable']/thead/tr/th[text()='Total']/following::td[5])[2]")
	private ElementField DISCOUNT_VALUE;
	
	@LocateBy(xpath = "(//table[@class='innerTable'])[1]")
	private ElementField FIRST_ITEM;

	@LocateBy(xpath = "(//table[@class='innerTable'])[2]")
	private ElementField SECOND_ITEM;

	@LocateBy(xpath = "//a[contains(text(),'Show all orders')]")
	private ElementField LT_SHOW_ALL_ORDER;

	@LocateBy(xpath = "//table[@id='orders']/tbody/tr/td/a/strong")
	private ElementField TAB_ORDERS;

	@LocateBy(xpath = "(//thead/tr/th/a")
	private ElementField ORDER_HEARDER;
	
	@LocateBy(xpath = "//table[@id='users']/tbody/tr[1]/td[1]/a/strong")
	 private ElementField table_selectCus;

	public OrdersPage addAndEditCustomerWithSIM(String testDataSetName, String category, String product) throws Exception {
		String rowText = TestData.read("PageCustomers.xml", testDataSetName, "rowText", category);
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);
		String order = TestData.read("PageCustomers.xml", testDataSetName, "order", category);
		boolean sim = TextUtilities.compareValue(TestData.read("PageCustomers.xml", testDataSetName, "sim", category), "true", true,
				TextComparators.equals);
		// this.selectRowItem(rowText);
		// this.clickCreateOrderButton();
		this.setCurrentActiveSinceDate();
		this.selectPeriod(period);
		this.selectOrderType(order);
		this.clickProductsTabinOrdersForm();
		this.selectProduct(product);
		this.checkSIM(sim);
		this.clickAddSelectedButton();
		this.clickAddToOrderButton();
		this.clickUpdate();
		this.clickProductsTabinOrdersForm();
		this.selectProduct(product);
		this.checkSIM(sim);
		this.clickAddSelectedButton();
		this.clickAddToOrderButton();
		this.clickUpdate();
		this.clickSaveChanges();

		return GlobalController.brw.initElements(OrdersPage.class);
	}

	public String addChildCustomer(String accountType, String attachedPaymentMethod, String testDataSetName, String category)
			throws Exception {
		String billingCycleDay = TestData.read("PageCustomers.xml", testDataSetName, "billingCycleDay", category);
		String cardHolderName = TestData.read("PageCustomers.xml", testDataSetName, "cardHolderName", category);
		String cardNumber = TestData.read("PageCustomers.xml", testDataSetName, "cardNumber", category);
		String expiryDate = TestData.read("PageCustomers.xml", testDataSetName, "expiryDate", category);
		String login = TestData.read("PageCustomers.xml", testDataSetName, "loginName", category);
		String agent = TestData.read("PageCustomers.xml", testDataSetName, "agent", category);
		String email = TestData.read("PageCustomers.xml", testDataSetName, "email", category);
		String billingCycleUnit = TestData.read("PageCustomers.xml", testDataSetName, "billingCycleUnit", category);

		boolean allowSubAccount = TextUtilities.compareValue(
				TestData.read("PageCustomers.xml", testDataSetName, "allowSubAccount", category), "true", true, TextComparators.equals);
		this.clickAllowSubAccount();
		this.selectAccountType(accountType);
		this.clickSelectButton();
		this.setLoginName(login);
		this.checkAllowSubAccount(allowSubAccount);
		this.setaAgentID(agent);

		this.clickCompanyBillingCycle();
        this.selectBillingUnit(billingCycleUnit);
		this.selectBillingDay(billingCycleDay);
		this.selectPaymentType(attachedPaymentMethod);
		this.cardHolderName(cardHolderName);
		this.cardNumber(cardNumber);
		this.cardExpiryDate(expiryDate);

		this.clickSaveChangesbutton();
		return login;
	}

	public String addCustomer(String accountType, String attachedPaymentMethod, String testDataSetName, String category) throws Exception {

		String billingCycleDay = TestData.read("PageCustomers.xml", testDataSetName, "billingCycleDay", category);
		String paymentMethod = TestData.read("PageCustomers.xml", testDataSetName, "paymentMethod2", category);
		String cardHolderName = TestData.read("PageCustomers.xml", testDataSetName, "cardHolderName", category);
		String cardNumber = TestData.read("PageCustomers.xml", testDataSetName, "cardNumber", category);
		String expiryDate = TestData.read("PageCustomers.xml", testDataSetName, "expiryDate", category);
		String login = TestData.read("PageCustomers.xml", testDataSetName, "loginName", category);
		String agent = TestData.read("PageCustomers.xml", testDataSetName, "agent", category);
		String email = TestData.read("PageCustomers.xml", testDataSetName, "email", category);
		String billingCycleUnit = TestData.read("PageCustomers.xml", testDataSetName, "billingCycleUnit", category);
		boolean allowSubAccount = TextUtilities.compareValue(
				TestData.read("PageCustomers.xml", testDataSetName, "allowSubAccount", category), "true", true, TextComparators.equals);
		this.clickAddNewButton();
		this.selectAccountType(accountType);
		this.clickSelectButton();
		this.setLoginName(login);
		this.checkAllowSubAccount(allowSubAccount);
		// this.setEmail(email);
		// this.setaAgentID(agent);

		/*
		 * this.clickCompanyBillingCycle();
		 * this.selectBillingDay(billingCycleDay);
		 * this.selectBillingUnit(billingCycleUnit);
		 */
		this.selectPaymentType(attachedPaymentMethod);
		this.cardHolderName(cardHolderName);
		this.cardNumber(cardNumber);
		this.cardExpiryDate(expiryDate);

		this.clickSaveChangesbutton();
		return login;
	}

	/**
	 * This method will ACH Customer Type
	 * 
	 * @param expiryDate
	 * @throws Exception
	 */

	public String addACHCustomerType(String accountType, String attachedPaymentMethod, String testDataSetName, String category)
			throws Exception {

		String billingCycleDay = TestData.read("PageCustomers.xml", testDataSetName, "billingCycleDay", category);
		String login = TestData.read("PageCustomers.xml", testDataSetName, "loginName", category);
		String billingCycleUnit = TestData.read("PageCustomers.xml", testDataSetName, "billingCycleUnit", category);
		String achroutingnumber = TestData.read("PageCustomers.xml", testDataSetName, "achroutingnumber", category);
		String achcustomername = TestData.read("PageCustomers.xml", testDataSetName, "achcustomername", category);
		String achaccounnumber = TestData.read("PageCustomers.xml", testDataSetName, "achaccounnumber", category);
		String achbankname = TestData.read("PageCustomers.xml", testDataSetName, "achbankname", category);
		String achaccounttype = TestData.read("PageCustomers.xml", testDataSetName, "achaccounttype", category);

		this.clickAddNewButton();
		this.selectAccountType(accountType);
		this.clickSelectButton();
		this.setLoginName(login);
        this.selectBillingUnit(billingCycleUnit);
		this.selectBillingDay(billingCycleDay);
		this.selectPaymentType(attachedPaymentMethod);

		this.setAchRoutingNumber(achroutingnumber);
		this.setAchCustomerName(achcustomername);
		this.setAchAccountNumber(achaccounnumber);
		this.setAchBankName(achbankname);
		this.selectAchAccountType(achaccounttype);

		this.clickSaveChangesbutton();
		return login;
	}

	/**
	 * This method will set Card Expiry Date for Payment
	 * 
	 * @param expiryDate
	 * @throws Exception
	 */
	protected CustomersPage cardExpiryDate(String expiryDate) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_CARDEXPIRYDATE, expiryDate);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will set Card Holder name for payment
	 * 
	 * @param cardHolderName
	 * @throws Exception
	 */
	protected CustomersPage cardHolderName(String cardHolderName) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_CARDHOLDERNAME, cardHolderName);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will set Card Number for Payment
	 * 
	 * @param cardNumber
	 * @throws Exception
	 */
	protected CustomersPage cardNumber(String cardNumber) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_CARDNUMBER, cardNumber);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will check/uncheck Allow sub Account checkbox
	 * 
	 * @param allowSubAccount
	 * @throws Exception
	 */
	protected CustomersPage checkAllowSubAccount(Boolean allowSubAccount) throws Exception {
		if (allowSubAccount) {
			GlobalController.brw.check(this.CB_ALLOWSUBACCOUNT);
		} else {
			GlobalController.brw.uncheck(this.CB_ALLOWSUBACCOUNT);
		}
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	protected String checkCustomerStatus() throws Exception {
		String status = GlobalController.brw.getText(this.TXT_CUSTOMER_STATUS);
		return status;
	}

	/**
	 * This method will check SIM checkbox
	 * 
	 * @param rememberMe
	 * @throws Exception
	 */
	protected OrdersPage checkSIM(Boolean SIM) throws Exception {
		if (SIM) {
			GlobalController.brw.check(this.CHK_SIM);
		} else {
			GlobalController.brw.uncheck(this.CHK_SIM);
		}
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	public CustomersPage clickAddNewButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADDNEW);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on Add Selected button
	 * 
	 * @throws Exception
	 */
	protected CustomersPage clickCancelButton() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_CANCEL);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on Add Selected button
	 * 
	 * @throws Exception
	 */
	protected CustomersPage clickReserveButton() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_RESERVE);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on Add Selected button
	 * 
	 * @throws Exception
	 */
	protected OrdersPage clickAddSelectedButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADDSELECTED);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will click on Add to order button
	 * 
	 * @throws Exception
	 */
	protected OrdersPage clickAddToOrderButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADDORDER);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will click on Add to order button on add Asset pop-up
	 * 
	 * @throws Exception
	 */
	protected OrdersPage clickOnAddToOrderButton() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_ADDORDER);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will click on Allow Sub Account Button
	 * 
	 * @throws Exception
	 */
	protected CustomersPage clickAllowSubAccount() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ALLOWSUBACCOUNT);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click Company Billing Cycle button
	 * 
	 * @throws Exception
	 */

	protected CustomersPage clickCompanyBillingCycle() throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.click(this.BT_BILLINGCYCLE);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on Create Order Button
	 * 
	 * @throws Exception
	 */
	public CustomersPage clickCreateOrder() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_CREATEORDER);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will set RoutingNumber
	 * 
	 * @throws Exception
	 */
	protected CustomersPage setAchRoutingNumber(String achroutingnumber) throws Exception {
		GlobalController.brw.setText(this.TB_ROUTING_NUMBER, achroutingnumber);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will set CUSTOMER_NAME
	 * 
	 * @throws Exception
	 */
	protected CustomersPage setAchCustomerName(String achcustomername) throws Exception {
		GlobalController.brw.setText(this.TB_CUSTOMER_NAME, achcustomername);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will set AchAccountNumber
	 * 
	 * @throws Exception
	 */
	protected CustomersPage setAchAccountNumber(String achaccountnumber) throws Exception {
		GlobalController.brw.setText(this.TB_ACCOUNT_NUMBER, achaccountnumber);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will set ach.bank.name
	 * 
	 * @throws Exception
	 */
	protected CustomersPage setAchBankName(String achbankname) throws Exception {
		GlobalController.brw.setText(this.TB_BANK_NAME, achbankname);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will select Account Type from Dropdown
	 * 
	 * @param ACCOUNT
	 *            Type
	 * @throws Exception
	 */

	protected CustomersPage selectAchAccountType(String achaccounttype) throws Exception {

		GlobalController.brw.selectDropDown(this.DD_ACCOUNT_TYPE, achaccounttype);

		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click create order button
	 * 
	 * @throws Exception
	 */
	protected CustomersPage clickCreateOrderButton() throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.clickLinkText(this.LT_CREATEORDER);

		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on current Order button
	 * 
	 * @throws Exception
	 */
	protected OrdersPage clickCurrentOrder() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_CURRENT_ORDER);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will click on Customer
	 * 
	 * @throws Exception
	 */

	protected CustomersPage clickCustomeritem() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_CUSTOMER);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	protected OrdersPage clickDependencyButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_DEPENDENCY_BUTTON);
		return GlobalController.brw.initElements(OrdersPage.class);

	}

	/**
	 * This method will click on Discounts Tab
	 * 
	 * @throws Exception
	 */
	protected OrdersPage clickDiscountsTabinOrdersForm() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_DISCOUNTS);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method clicks on Make Payment
	 * 
	 * @throws Exception
	 */
	public CustomersPage clickMakePayment() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_MAKE);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on Product Item
	 * 
	 * @throws Exception
	 */

	protected CustomersPage clickProductItem() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_PRODUCT);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click products tab within orders form
	 * 
	 * @throws Exception
	 */
	protected CustomersPage clickProductsTabinOrdersForm() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_PRODUCTS);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on recent customer
	 * 
	 * @throws Exception
	 */
	protected CustomersPage clickRecentCustomer() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_RECENTCUSTOMER);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on Review Tab
	 * 
	 * @throws Exception
	 */
	protected OrdersPage clickReviewTab() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_REVIEW);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will click on Save Changes Button
	 * 
	 * @throws Exception
	 */
	protected CustomersPage clickSaveChanges() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_SAVECHANGES);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on Save Changes Button
	 * 
	 * @throws Exception
	 */
	protected CustomersPage clickSaveChangesOnNewAssetPage() throws Exception {
		GlobalController.brw.clickLinkText(this.SAVE_CHANGE_ON_ASSET);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click Save Changes Button
	 * 
	 * @throws Exception
	 */

	public CustomersPage clickSaveChangesbutton() throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.clickLinkText(this.LT_SAVECHANGES);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click Select Button
	 * 
	 * @throws Exception
	 */
	public CustomersPage clickSelectButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_SELECT);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will validate Users table saved test data
	 * 
	 * @throws Exception
	 */
	public CustomersPage validateUsersSavedTestData(String data) throws Exception {
		GlobalController.brw.validateSavedTestData(this.TAB_USER_TYPES_NAMES, data);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on Update Button
	 * 
	 * @throws Exception
	 */
	protected CustomersPage clickUpdate() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_UPDATE);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	public OrdersPage createOrderForCustomer(String testDataSetName, String category) throws Exception {
		CustomersPage.logger.enterMethod();
		String rowText = TestData.read("PageCustomers.xml", testDataSetName, "rowText", category);
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);
		String product = TestData.read("PageCustomers.xml", testDataSetName, "product", category);
		String setDate = TestData.read("PageCustomers.xml", testDataSetName, "setDate", category);
		String quantity = TestData.read("PageCustomers.xml", testDataSetName, "quantity", category);
		this.setCurrentActiveSinceDate();
		this.selectPeriod(period);
		this.clickProductsTabinOrdersForm();
		this.selectProduct(product);
		this.setEffectiveDateDefault(setDate);
		// this.setQuantity(quantity);
		this.clickUpdate();
		this.clickSaveChanges();
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	public CustomersPage editCustomer(String testDataSetName, String category) throws Exception {
		String rowText = TestData.read("PageCustomers.xml", testDataSetName, "rowText", category);
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);
		String order = TestData.read("PageCustomers.xml", testDataSetName, "order", category);
		String product = TestData.read("PageCustomers.xml", testDataSetName, "product", category);
		String effectiveDate = TestData.read("PageCustomers.xml", testDataSetName, "effectiveDate", category);
		String quantity = TestData.read("PageCustomers.xml", testDataSetName, "quantity", category);

		this.selectRowItem(rowText);

		this.clickCreateOrderButton();
		this.setCurrentActiveSinceDate();

		this.selectPeriod(period);

		this.selectOrderType(order);

		this.clickProductsTabinOrdersForm();

		this.selectRowItem(product);

		this.setEffectiveDateDefault(effectiveDate);
		this.setQuantity(quantity);
		this.clickUpdate();
		this.clickSaveChanges();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	protected OrdersPage filterProducts(String textForProductFiltering) throws Exception {
		ElementField efTbForFiltering = this.TB_FILTER_PRODUCTS_BY;
		GlobalController.brw.setText(efTbForFiltering, textForProductFiltering.replace(" ", "_"));
		GlobalController.brw.pressTab(efTbForFiltering);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will generate invoice
	 * 
	 * @throws Exception
	 */

	protected CustomersPage generateInvoice() throws Exception {
		GlobalController.brw.clickLinkText(this.GT_INVOICE);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	public void generateInvoice (String testDataSetName, String category) throws Exception {
        this.createOrderForInvoice(testDataSetName, category);
		this.generateInvoice();
		this.verifyGeneratedInvoice();
	}

    public void createOrderForInvoice (String testDataSetName, String category) throws Exception {
        String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);
        String type = TestData.read("PageCustomers.xml", testDataSetName, "type", category);
        String product = TestData.read("PageCustomers.xml", testDataSetName, "product", category);
        this.setCurrentActiveSinceDate();
        this.selectPeriod(period);
        this.selectOrderType(type);
        this.clickSaveChanges();
        this.clickProductsTabinOrdersForm();
        this.filterProducts(product);
        this.selectRowItem(product);
        this.clickUpdate();
        this.clickSaveChanges();
    }

	public OrdersPage orderProductHavingDependency(String testDataSetName, String category) throws Exception {
		String customerName = TestData.read("PageCustomers.xml", testDataSetName, "customerName", category);
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);
		String type = TestData.read("PageCustomers.xml", testDataSetName, "type", category);
		String product2 = TestData.read("PageCustomers.xml", testDataSetName, "product2", category);
		String depandentProduct = TestData.read("PageCustomers.xml", testDataSetName, "depandentProduct", category);
		String message = TestData.read("PageCustomers.xml", testDataSetName, "message", category);
		String effectiveDate = TestData.read("PageCustomers.xml", testDataSetName, "effectiveDate", category);
		String quantity = TestData.read("PageCustomers.xml", testDataSetName, "quantity", category);

		this.selectPeriod(period);
		this.selectOrderType(type);

		this.clickProductsTabinOrdersForm();
        this.filterProducts(product2);
		this.selectProduct(product2);
		this.setEffectiveDateDefault(effectiveDate);
		this.clickUpdate();
		this.clickSaveChanges();

		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will select Account type in account type dropdown
	 * 
	 * @param AccountType
	 * @throws Exception
	 */
	public CustomersPage selectAccountType(String AccountType) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.selectDropDown(this.DD_ACCOUNTTYPE, AccountType);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will select Account Type Dropdown for new Customer
	 * 
	 * @param AccountType
	 * @throws Exception
	 */
	protected CustomersPage selectAccountTypeDropDownToAddNewCustomer(String AccountType) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_ACCOUNTTYPE, AccountType);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will select Billing day from Dropdown
	 * 
	 * @param billingCycleDay
	 * @throws Exception
	 */

	protected CustomersPage selectBillingDay(String billingCycleDay) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.selectDropDown(this.BT_BILLINGCYCLEDAY, billingCycleDay);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will select Billing day from Dropdown
	 * 
	 * @param billingCycleDay
	 * @throws Exception
	 */

	protected CustomersPage selectBillingUnit(String billingCycleUnit) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.selectDropDown(this.BT_BILLING_CYCLE_UNIT, billingCycleUnit);
        GlobalController.brw.waitForAjaxElement(this.BT_BILLINGCYCLEDAY);

		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on customer
	 * 
	 * @throws Exception
	 */
	public CustomersPage selectCustomer(String user) throws Exception {
		GlobalController.brw.selectListItem(this.TAB_LIST_CUSTOMER_NAME, user);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	public OrdersPage selectCustomerAndClickCreateOrder(String testDataSetName, String category, HashMap<String, String> runTimeVariables)
			throws Exception {
		String customerName = TestData.read("PageCustomers.xml", testDataSetName, "customerName", category);

		this.selectCustomer(runTimeVariables.get("TC_6.2_CHILD_CUSTOMER_NAME"));

		this.clickCreateOrder();

		return GlobalController.brw.initElements(OrdersPage.class);
	}

	public InvoicePage selectCustomerAndClickMakePayment(String testDataSetName, String category, HashMap<String, String> runTimeVariables)
			throws Exception {
		String customerName = TestData.read("PageCustomers.xml", testDataSetName, "customerName", category);

		this.selectCustomer(runTimeVariables.get("TC_6.2_CHILD_CUSTOMER_NAME"));

		this.clickMakePayment();

		return GlobalController.brw.initElements(InvoicePage.class);
	}

	public InvoicePage selectCustomerToMakePayment(String customerName) throws Exception {
		this.selectCustomer(customerName);
		this.clickMakePayment();
		return GlobalController.brw.initElements(InvoicePage.class);
	}

	public OrdersPage selectCustomerToAddOrder(String customer) throws Exception {
		this.selectCustomer(customer);
		this.clickCreateOrder();
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will select dependent Product
	 * 
	 * @param depandentProduct
	 * @throws Exception
	 */
	protected OrdersPage selectDependentProduct(String depandentProduct) throws Exception {
		GlobalController.brw.clickLinkText(this.TAB_DEPENDENT_PRODUCT);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will click on Product Tab
	 * 
	 * @param depandentProduct
	 * @throws Exception
	 */
	protected CustomersPage clickOnProductTab() throws Exception {
		GlobalController.brw.clickLinkText(this.TAB_PRODUCTS);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on Plan Tab
	 * 
	 * @param depandentPlan
	 * @throws Exception
	 */
	protected CustomersPage clickOnPlanTab() throws Exception {
		GlobalController.brw.clickLinkText(this.TAB_PLAN);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on Create Asset Button
	 * 
	 * @param depandentProduct
	 * @throws Exception
	 */
	protected CustomersPage clickOnCreateAssetButton() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_CREATE_ASSET);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will select Discount
	 * 
	 * @throws Exception
	 */
	protected OrdersPage selectDiscount(String discount) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_DISCOUNTS, discount);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will select Discountable Item
	 * 
	 * @throws Exception
	 */
	protected OrdersPage selectDiscountableItem(String discountItem) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_DISCOUNTABLEITEM, discountItem);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will select period
	 * 
	 * @throws Exception
	 */
	protected CustomersPage selectOrderType(String order) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_ORDER, order);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will select Payment Type Dropdown
	 * 
	 * @param paymentMethod
	 * @throws Exception
	 */
	protected CustomersPage selectPaymentType(String paymentMethod) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.selectDropDown(this.DD_PAYMENTMETHOD, paymentMethod);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will select period
	 * 
	 * @throws Exception
	 */
	protected CustomersPage selectPeriod(String period) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PERIOD, period);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click on product
	 * 
	 * @throws Exception
	 */
	protected OrdersPage selectProduct(String product) throws Exception {
		this.filterProducts(product);
		GlobalController.brw.selectListItem(this.TAB_LIST_PRODUCT, product);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method selects row item from the web table
	 * 
	 * @throws Exception
	 */
	protected CustomersPage selectRowItem(String rowText) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.selectListItem(this.LT_htmlWebTableRow, rowText);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method selects customer from the web table
	 * 
	 * @throws Exception
	 */
	protected CustomersPage selectCustomerFUP(String customer) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.selectListItem(this.LT_UsersWebTable, customer);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method selects Plan from the web table
	 * 
	 * @throws Exception
	 */
	protected CustomersPage selectPlanItem() throws Exception {
		GlobalController.brw.click(this.LT_PlansWebTable);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will Enter Agent ID for new customer
	 * 
	 * @param name
	 * @throws Exception
	 */
	protected CustomersPage setaAgentID(String text) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_AGENT, text);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will click Add New button
	 */
	protected CustomersPage setCurrentActiveSinceDate() throws Exception {
		GlobalController.brw.setcurrentDate(this.TB_ACTIVEDATE);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will set Default Date
	 * 
	 * @throws Exception
	 */
	protected CustomersPage setEffectiveDateDefault(String effectiveDate) throws Exception {
		GlobalController.brw.setText(this.TB_EFFECTIVE_DATE, effectiveDate);
        GlobalController.brw.pressTab(this.TB_EFFECTIVE_DATE); // to close date dialog
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will Enter Email for new customer
	 * 
	 * @param name
	 * @throws Exception
	 */
	protected CustomersPage setEmail(String email) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_EMAIL, email);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will Enter Login name for new customer
	 * 
	 * @param name
	 * @throws Exception
	 */
	public CustomersPage setLoginName(String name) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_LOGINNAME, name);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	protected CustomersPage selectCurrentCompany() throws Exception {
		String company = GlobalController.brw.getText(this.COMPANY);
		GlobalController.brw.selectDropDown(this.DD_COMPANY, company);
		return GlobalController.brw.initElements(CustomersPage.class);

	}

	/**
	 * This method will set quantity
	 * 
	 * @param rate
	 * @throws Exception
	 */
	protected CustomersPage setQuantity(String quantity) throws Exception {
		GlobalController.brw.setText(this.TB_QUANTITY, quantity);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	public CustomersPage statusCycle(String testDataSetNameForCustomerOne, String testDataSetNameForCustomerTwo, String category)
			throws Exception {
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will verify generated invoice
	 * 
	 * @throws Exception
	 */
	protected OrdersPage verifyGeneratedInvoice() throws Exception {
        Assert.assertTrue(MessagesPage.isIntermediateSuccessMessageAppeared());
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will validate user table saved test data
	 * 
	 * @throws Exception
	 */
	public CustomersPage validateSavedTestDataInTable(String data) throws Exception {
		GlobalController.brw.validateSavedTestData(this.TAB_SAVED_CUSTOMER_NAME, data);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will validate Added Asset saved test data
	 * 
	 * @throws Exception
	 */
	public CustomersPage validateAddedAssetInTable(String data) throws Exception {
		GlobalController.brw.validateSavedTestData(this.TABLE_ADDED_ASSERT, data);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will verify breadcrumbs ui component
	 * 
	 * @throws Exception
	 */
	public CustomersPage verifyUIComponent() throws Exception {
		GlobalController.brw.verifyUIComponent(this.BREADCRUMBS);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will validate payments table saved test data
	 * 
	 * @throws Exception
	 */
	public CustomersPage validateSavedTestDataInPaymentsTable(String data) throws Exception {
		GlobalController.brw.validateSavedTestData(this.TAB_PAYMENTS, data);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will set Cheque Holder name for payment
	 * 
	 * @param cardHolderName
	 * @throws Exception
	 */
	protected CustomersPage ChequeHolderName(String chequeHolderName) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_CHEQUE_NAME, chequeHolderName);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will set Cheque Number for Payment
	 * 
	 * @param cardNumber
	 * @throws Exception
	 */
	protected CustomersPage ChequeNumber(String chequeNumber) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_CHEQUE_NUMBER, chequeNumber);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will set Cheque Number for Payment
	 * 
	 * @param cardNumber
	 * @throws Exception
	 */
	protected CustomersPage ChequeDate(String chequeDate) throws Exception {
		CustomersPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_CHEQUE_DATE, chequeDate);
		CustomersPage.logger.exitMethod();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * @author shashank
	 * 
	 * 
	 *         This method will set payment amount for making payment
	 * @param paymentAmount
	 * @return
	 * @throws Exception
	 */
	protected CustomersPage setPaymentAmount(String paymentAmount) throws Exception {
		GlobalController.brw.setText(this.TB_PAYMENT_AMOUNT, paymentAmount);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	protected CustomersPage clickReviewPayment() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_REVIEW_PAYMENT);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	protected CustomersPage setPaymentOrder(String PaymentOrder) throws Exception {
		GlobalController.brw.setText(this.TB_PAYMENT_ORDER, PaymentOrder);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	protected CustomersPage assetDetails(String identifier) throws Exception {
		GlobalController.brw.setText(this.TB_IDENTIFIER, identifier);
		return GlobalController.brw.initElements(CustomersPage.class);

	}

	CustomersPage enableGlobleCheckBox(boolean globle) throws Exception {
		if (globle) {
			GlobalController.brw.check(this.CB_GLOBLE);
		} else {
			GlobalController.brw.uncheck(this.CB_GLOBLE);
		}
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	public String addCustomerWithMakePayment(String testDataSetName, String category, String accountName) throws Exception {
		String name = TestData.read("PageCustomers.xml", testDataSetName, "name", category);

		this.clickAddNewButton();
		this.selectAccountType(accountName);
		this.clickSelectButton();
		this.setLoginName(name);
		// this.setEmail(email);
		this.clickSaveChangesbutton();
		return name;
	}

    public void addCustomerWithAccountType (String customerName, String accountTypeName) throws Exception {

        this.clickAddNewButton();
        this.selectAccountType(accountTypeName);
        this.clickSelectButton();
        this.setLoginName(customerName);
        this.clickSaveChangesbutton();
    }


	public CustomersPage verifyPaymentAvailableForCustomer(String accName, String paymentMethod) throws Exception {

		this.clickAddNewButton();
		this.selectAccountType(accName);
		this.clickSelectButton();
		this.selectPaymentType(paymentMethod);

		return GlobalController.brw.initElements(CustomersPage.class);

	}

	public CustomersPage addNewCustomer(String accountType, String attachedPaymentMethod, String testdataset, String category)
			throws Exception {

		String cardHolderName = TestData.read("PageCustomers.xml", testdataset, "cardHolderName", category);
		String cardNumber = TestData.read("PageCustomers.xml", testdataset, "cardNumber", category);
		String expiryDate = TestData.read("PageCustomers.xml", testdataset, "expiryDate", category);
		String loginName = TestData.read("PageCustomers.xml", testdataset, "loginName", category);

		this.clickAddNewButton();
		GlobalController.brw.selectDropDown(this.DD_ACCOUNTTYPE, accountType);
		this.clickSelectButton();
		this.setLoginName(loginName);
		this.selectPaymentType(attachedPaymentMethod);
		this.cardHolderName(cardHolderName);
		this.cardNumber(cardNumber);
		this.cardExpiryDate(expiryDate);

		this.clickSaveChangesbutton();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	public OrdersPage createOrderCustomer(String testDataSetName, String category) throws Exception {

		String rowText = TestData.read("PageCustomers.xml", testDataSetName, "rowText", category);
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);
		String product = TestData.read("PageCustomers.xml", testDataSetName, "product", category);
		String effectiveDate = TestData.read("PageCustomers.xml", testDataSetName, "effectiveDate", category);
		String quantity = TestData.read("PageCustomers.xml", testDataSetName, "quantity", category);
		String order = TestData.read("PageCustomers.xml", testDataSetName, "order", category);
		String secondProduct = TestData.read("PageCustomers.xml", testDataSetName, "secondProduct", category);
		String activeDate = TestData.read("PageCustomers.xml", testDataSetName, "activeDate", category);

		this.clickCreateOrder();
		this.selectPeriod(period);
		this.selectOrderType(order);
		this.setActiveSinceDate(activeDate);
		this.clickProductsTabinOrdersForm();
		this.selectProduct(product);
		this.setEffectiveDateDefault(effectiveDate);
		this.clickDependencyButton();
		GlobalController.brw.click(this.CLICK_DEPENDENCIES);
		GlobalController.brw.click(this.CLICK_SUBORDER);
		this.selectPeriod(period);
		this.selectOrderType(order);
		this.setActiveSinceDate(activeDate);
		GlobalController.brw.click(this.LT_SAVECHANGES);
		GlobalController.brw.click(this.CLICK_SECONDOPTION);
		GlobalController.brw.click(this.CLICK_FIRST_EDIT_BUTTON);
		this.clickProductsTabinOrdersForm();
		this.selectProduct(secondProduct);
		this.setEffectiveDateDefault(effectiveDate);
		GlobalController.brw.click(this.CLICK_UPDATE_ICON);
		GlobalController.brw.click(this.LT_SAVECHANGES);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	protected CustomersPage setActiveSinceDate(String activeDate) throws Exception {
		GlobalController.brw.setText(this.TB_ACTIVEDATE, activeDate);
        GlobalController.brw.pressTab(this.TB_ACTIVEDATE); // to close date dialog
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	public String createOrder(String testDataSetName, String category, String produtDescription) throws Exception {
		String identifier = TestData.read("PageCustomers.xml", testDataSetName, "identifier1", category);
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);
		String order = TestData.read("PageCustomers.xml", testDataSetName, "order", category);
		String effectiveDate = TestData.read("PageCustomers.xml", testDataSetName, "order", category);
		boolean globle = TextUtilities.compareValue(TestData.read("PageProducts.xml", testDataSetName, "globle", category), "true", true,
				TextComparators.equals);
		this.clickCreateOrder();
		this.selectPeriod(period);
		this.selectOrderType(order);
		this.setCurrentActiveSinceDate();
		this.clickOnProductTab();
		this.selectRowItem(produtDescription);
		this.clickOnCreateAssetButton();
		this.assetDetails(identifier);
		this.selectCurrentCompany();
		this.clickSaveChangesOnNewAssetPage();
		this.clickOnAddToOrderButton();
		this.setEffectiveDateDefault(effectiveDate);
		this.clickUpdate();
		this.clickReviewTab();
		this.clickSaveChanges();
		return identifier;
	}

	public CustomersPage verifyAddedAsset(String customerName, String identifier) throws Exception {
		this.selectCustomer(customerName);
		this.validateAddedAssetInTable(identifier);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	public CustomersPage createOrderForFUPCustomer(String tC_115_CUSTOMER_NAME, String testDataSetName, String category) throws Exception {
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);
		String type = TestData.read("PageCustomers.xml", testDataSetName, "type", category);

		this.selectCustomerFUP(tC_115_CUSTOMER_NAME);
		this.clickCreateOrder();
		this.selectPeriod(period);
		this.selectOrderType(type);
		this.clickOnPlanTab();
		this.selectPlanItem();
		this.clickUpdate();
		this.clickSaveChanges();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	public CustomersPage EditOrderForFUPCustomer(String tC_115_CUSTOMER_NAME, String testDataSetName, String category) throws Exception {

		this.selectCustomerFUP(tC_115_CUSTOMER_NAME);
		GlobalController.brw.click(this.CLICK_CUSTOMER_SEARCH_ICON);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will set quantity
	 * 
	 * @param rate
	 * @throws Exception
	 */
	protected CustomersPage setProductQuantity(String quantity) throws Exception {
		GlobalController.brw.setText(this.TB_PRODUCT_QUANTITY, quantity);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will Set Start Date
	 * 
	 * @throws Exception
	 */
	CustomersPage setStartDateInOrder(String startDate) throws Exception {
		GlobalController.brw.setText(this.TB_START_DATE_IN_ORDER, startDate);

		return GlobalController.brw.initElements(CustomersPage.class);

	}

	/**
	 * activeUntil This method will Set End Date
	 * 
	 * @throws Exception
	 */
	CustomersPage setEndDateInOrder(String endDate) throws Exception {
		GlobalController.brw.setText(this.TB_END_DATE_IN_ORDER, endDate);

		return GlobalController.brw.initElements(CustomersPage.class);

	}

	/**
	 * This method will verify successful Message Plan
	 * 
	 * @throws Exception
	 */
	protected OrdersPage verifySuccessfulMessage() throws Exception {
		MessagesPage.isIntermediateSuccessMessageAppeared();
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will Click on Add order button
	 * 
	 * @throws Exception
	 */
	public CustomersPage clickOnAddedOrder() throws Exception {
		GlobalController.brw.clickLinkText(this.ADDED_ORDER_BAR);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will Click on Change button
	 * 
	 * @throws Exception
	 */
	public CustomersPage clickOnChangeButton() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_CHANGE);
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	/**
	 * This method will select existing customer Plan
	 * 
	 * @throws Exception
	 */
	protected OrdersPage selectExistingCustomer(String data) throws Exception {
		GlobalController.brw.selectListItem(this.TAB_CUSTOMER, data);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	public CustomersPage createOrderForOnePerCustomer(String testDataSetName, String category, String productDescription) throws Exception {
		String startDate = TestData.read("PageCustomers.xml", testDataSetName, "startDate", category);
		String endDate = TestData.read("PageCustomers.xml", testDataSetName, "endDate", category);
		String effectiveDate = TestData.read("PageCustomers.xml", testDataSetName, "effectiveDate", category);
		String quantity = TestData.read("PageCustomers.xml", testDataSetName, "quantity", category);
		String quantity1 = TestData.read("PageCustomers.xml", testDataSetName, "quantity1", category);
		boolean globle = TextUtilities.compareValue(TestData.read("PageProducts.xml", testDataSetName, "globle", category), "true", true,
				TextComparators.equals);
		this.clickCreateOrder();
		this.setStartDateInOrder(startDate);
		this.setEndDateInOrder(endDate);
		this.clickOnProductTab();
		this.selectRowItem(productDescription);
        this.setProductQuantity(quantity);
		this.setEffectiveDateDefault(effectiveDate);
		this.clickUpdate();
		MessagesPage.isErrorMessagesListAppeared();
		this.setProductQuantity(quantity1);
		this.clickUpdate();
		this.verifySuccessfulMessage();
		this.clickSaveChanges();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	public void addProductInExistingCustomer(String testDataSetName, String category, String customerName, String produtDescription)
			throws Exception {
		String startDate = TestData.read("PageCustomers.xml", testDataSetName, "startDate", category);
		String effectiveDate = TestData.read("PageCustomers.xml", testDataSetName, "EffectiveDate", category);
		this.selectExistingCustomer(customerName);
        this.clickCreateOrder();
        this.setStartDateInOrder(startDate);
		this.clickOnProductTab();
		this.selectRowItem(produtDescription);
		this.setEffectiveDateDefault(effectiveDate);
		this.clickUpdate();
        this.verifySuccessfulMessage();
	}

	private static final String TC_140_ERROR_MESSAGE = "User has already subscribed to a product/plan from the given category.";

	public void createOrderForOnePerOrder(String testDataSetName, String category, String productDescription,
			String productDescription1) throws Exception {
		String startDate = TestData.read("PageCustomers.xml", testDataSetName, "startDate", category);
		String endDate = TestData.read("PageCustomers.xml", testDataSetName, "endDate", category);
		String effectiveDate = TestData.read("PageCustomers.xml", testDataSetName, "effectiveDate", category);
		this.clickCreateOrder();
        this.setStartDateInOrder(startDate);
        this.setEndDateInOrder(endDate);
		this.clickOnProductTab();
		this.selectRowItem(productDescription);
		this.setEffectiveDateDefault(effectiveDate);
		this.clickUpdate();
		this.clickOnProductTab();
		this.selectRowItem(productDescription1);
		MessagesPage.assertTextInFirstErrorMessage(TC_140_ERROR_MESSAGE);
	}

	public void verifyAddedProductInPlan(String productDescription, String productDescription3, String productDescription4)
			throws Exception {
		this.clickPlansTab();
		this.selectPlanItemInCustomer(productDescription3);
        MessagesPage.assertTextInFirstErrorMessage(TC_140_ERROR_MESSAGE);
		this.clickOnRemoveButton();
		this.selectPlanItemInCustomer(productDescription3);
		this.clickUpdate();
		this.clickPlansTab();
		this.selectPlanItemInCustomer(productDescription4);
        MessagesPage.assertTextInFirstErrorMessage(TC_140_ERROR_MESSAGE);
		this.clickProductTab();
		this.selectRowItem(productDescription);
        MessagesPage.assertTextInFirstErrorMessage(TC_140_ERROR_MESSAGE);
	}

	CustomersPage clickPlansTab() throws Exception {
		GlobalController.brw.clickLinkText(this.PLANS_TAB);
		return GlobalController.brw.initElements(CustomersPage.class);

	}
	
	/**
	 * This method selects Plan from the web table
	 * 
	 * @throws Exception
	 */
	protected CustomersPage selectPlanItemInCustomer(String productDescription) throws Exception {
		GlobalController.brw.click(this.LT_PlansWebTable);
		return GlobalController.brw.initElements(CustomersPage.class);
	}
	
	/**
	 * This method will Remove Button
	 * 
	 * @throws Exception
	 */
	CustomersPage clickOnRemoveButton() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_REMOVE);
		return GlobalController.brw.initElements(CustomersPage.class);

	}
	
	/**
	 * This method will Product Tab
	 * 
	 * @throws Exception
	 */
	CustomersPage clickProductTab() throws Exception {
		GlobalController.brw.clickLinkText(this.PRODUCT_TAB);
		return GlobalController.brw.initElements(CustomersPage.class);

	}
	
	public String createCustomerWithPaymentType(String testDataSetName, String category, String accountName, String paymentType)
			throws Exception {
		String customerName = TestData.read("PageCustomers.xml", testDataSetName, "customerName", category);
		String name = TestData.read("PageCustomers.xml", testDataSetName, "name", category);
		String number = TestData.read("PageCustomers.xml", testDataSetName, "number", category);
		String date = TestData.read("PageCustomers.xml", testDataSetName, "date", category);
		String nextInvoiceDate = TestData.read("PageCustomers.xml", testDataSetName, "nextInvoiceDate", category);
		this.clickAddNewButton();
		this.selectAccountType(accountName);
		this.clickSelectButton();
		this.setLoginName(customerName);
		this.selectPaymentMethodType(paymentType);
		this.setCardHolderName(name);
		this.setCardHolderNumber(number);
		this.setCardHolderDate(date);
		this.clickSaveChangesbutton();
		this.clickOnEditButton();
		this.setNextInvoiceDate(nextInvoiceDate);
		this.clickSaveChangesbutton();
		return customerName;
	}
	
	protected CustomersPage setNextInvoiceDate(String invoiceDate) throws Exception {
		GlobalController.brw.setText(this.TB_NEXT_INCOICE_DATE, invoiceDate);
		return GlobalController.brw.initElements(CustomersPage.class);

	}
	
	protected CustomersPage clickOnEditButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_EditCustomer);
		return GlobalController.brw.initElements(CustomersPage.class);

	}
	
	/**
	 * This method will select Payment Type from DropDown
	 * 
	 * @throws Exception
	 */
	CustomersPage selectPaymentMethodType(String paymentType) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PAYMENT_METHOD, paymentType);
		return GlobalController.brw.initElements(CustomersPage.class);

	}

	/**
	 * This method will Get Period value
	 * 
	 * @throws Exception
	 */
	String getPeriodValue() throws Exception {
		String periodValue = GlobalController.brw.getText(this.PERIOD_OPTION_VALUE);
		return periodValue;

	}
	
	protected CustomersPage setCardHolderName(String name) throws Exception {
		GlobalController.brw.setText(this.TB_CARDHOLDER_NAME, name);
		return GlobalController.brw.initElements(CustomersPage.class);

	}

	protected CustomersPage setCardHolderNumber(String number) throws Exception {
		GlobalController.brw.setText(this.TB_CARDHOLDER_NUMBER, number);
		return GlobalController.brw.initElements(CustomersPage.class);

	}
	
	protected CustomersPage setCardHolderDate(String date) throws Exception {
		GlobalController.brw.setText(this.TB_CARDHOLDER_DATE, date);
		return GlobalController.brw.initElements(CustomersPage.class);

	}
	
	public CustomersPage createOrderWithDiscount(String testDataSetName, String category, String produtDescription, String discountName)
			throws Exception {
		String startDate = TestData.read("PageCustomers.xml", testDataSetName, "startDate", category);
		String endDate = TestData.read("PageCustomers.xml", testDataSetName, "endDate", category);
		String type = TestData.read("PageCustomers.xml", testDataSetName, "type", category);
		String effectiveDate = TestData.read("PageCustomers.xml", testDataSetName, "effectiveDate", category);
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);
		String discountableItem = TestData.read("PageCustomers.xml", testDataSetName, "discountableItem", category);

		boolean globle = TextUtilities.compareValue(TestData.read("PageProducts.xml", testDataSetName, "globle", category), "true", true,
				TextComparators.equals);
		this.clickCreateOrder();
		this.selectPeriod(period);
		this.selectOrderType(type);
		this.setStartDateInOrder(startDate);
		this.setEndDateInOrder(endDate);
		this.clickOnProductTab();
		this.selectRowItem(produtDescription);
		this.clickOnDiscountTab();
		this.selectDiscountableItem(discountableItem);
		this.selectDiscount(discountName);
		this.setEffectiveDateDefault(effectiveDate);
		this.clickUpdate();
		this.clickSaveChanges();
		return GlobalController.brw.initElements(CustomersPage.class);
	}
	
	/**
	 * This method will Click on Discount Tab
	 * 
	 * @throws Exception
	 */
	public CustomersPage clickOnDiscountTab() throws Exception {
		GlobalController.brw.clickLinkText(this.TAB_DISCOUNT);
		return GlobalController.brw.initElements(CustomersPage.class);

	}
	
	/**
	 * This method will get the applied Discount Value
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAppliedDiscount() throws Exception {
		String appliedDiscount = GlobalController.brw.getText(this.DISCOUNT_VALUE);
		return appliedDiscount;
	}
	
	public String calculatedDiscount(String testDataSetName, String category) throws Exception {
		String DiscountRate = TestData.read("PageCustomers.xml", testDataSetName, "DiscountRate", category);
		String productRate = TestData.read("PageCustomers.xml", testDataSetName, "productRate", category);
		int percentageRate1 = Integer.parseInt(DiscountRate);
		int productRate1 = Integer.parseInt(productRate);
		int calculatedAppliedDiscount = (percentageRate1 * productRate1) / 100;
		String calculatedAppliedDiscount1 = Integer.toString(calculatedAppliedDiscount);
		return "-US$" + calculatedAppliedDiscount + ".00";
	}
	
	public CustomersPage verifyAppliedDiscount(String appliedDiscount, String calculatedDiscount) throws Exception {
		if (appliedDiscount.equals(calculatedDiscount)) {
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(false);
		}

		return GlobalController.brw.initElements(CustomersPage.class);
	}
	public CustomersPage verifyItemInMonthlyPostpaidOrder() throws Exception {
		Assert.assertTrue(GlobalController.brw.isElementPresent(this.FIRST_ITEM));
		Assert.assertTrue(GlobalController.brw.isElementPresent(this.SECOND_ITEM));
		return GlobalController.brw.initElements(CustomersPage.class);
	}

	public CustomersPage createOrderYearly(String testDataSetName, String category, String planDescription) throws Exception {
		String startDate = TestData.read("PageCustomers.xml", testDataSetName, "startDate", category);
		String type = TestData.read("PageCustomers.xml", testDataSetName, "type", category);
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);

		boolean globle = TextUtilities.compareValue(TestData.read("PageProducts.xml", testDataSetName, "globle", category), "true", true,
				TextComparators.equals);
		this.clickCreateOrder();
		this.selectPeriod(period);
		this.selectOrderType(type);
		this.setStartDateInOrder(startDate);
		this.clickOnPlanTab();
		this.selectPlanItemInCustomer(planDescription);
		this.clickUpdate();
		this.clickSaveChanges();
		this.verifyItemInMonthlyPostpaidOrder();
		return GlobalController.brw.initElements(CustomersPage.class);
	}
	public CustomersPage createOrderMonthly(String testDataSetName, String category, String planDescription) throws Exception {
		String startDate = TestData.read("PageCustomers.xml", testDataSetName, "startDate", category);
		String type = TestData.read("PageCustomers.xml", testDataSetName, "type", category);
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);

		boolean globle = TextUtilities.compareValue(TestData.read("PageProducts.xml", testDataSetName, "globle", category), "true", true,
				TextComparators.equals);
		this.clickCreateOrder();
		this.selectPeriod(period);
		this.selectOrderType(type);
		this.setStartDateInOrder(startDate);
		this.clickOnPlanTab();
		this.selectPlanItemInCustomer(planDescription);
		this.clickUpdate();
		this.clickSaveChanges();
		this.verifyItemInMonthlyPostpaidOrder();
		return GlobalController.brw.initElements(CustomersPage.class);
	}
	public CustomersPage createOrderForCancel(String testDataSetName, String category) throws Exception {
		String startDate = TestData.read("PageCustomers.xml", testDataSetName, "startDate", category);
		String type = TestData.read("PageCustomers.xml", testDataSetName, "type", category);
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);

		boolean globle = TextUtilities.compareValue(TestData.read("PageProducts.xml", testDataSetName, "globle", category), "true", true,
				TextComparators.equals);
		this.clickCreateOrder();
		this.selectPeriod(period);
		this.selectOrderType(type);
		this.setStartDateInOrder(startDate);
		this.clickCancelButton();
		return GlobalController.brw.initElements(CustomersPage.class);
	}
	/**
	 * This method selects Plan from the web table
	 * 
	 * @throws Exception
	 */
	protected CustomersPage selectPlanInCustomer(String productDescription) throws Exception {
		GlobalController.brw.selectListItem(this.LT_PlansWebTable, productDescription);
		return GlobalController.brw.initElements(CustomersPage.class);
	}
	
	
	public CustomersPage createOrderBundleAndWithoutBundle(String testDataSetName, String category, String firstPlanDescription,
			String planDescription2) throws Exception {
		String startDate = TestData.read("PageCustomers.xml", testDataSetName, "startDate", category);
		String type = TestData.read("PageCustomers.xml", testDataSetName, "type", category);
		String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);

		boolean globle = TextUtilities.compareValue(TestData.read("PageProducts.xml", testDataSetName, "globle", category), "true", true,
				TextComparators.equals);
		this.clickCreateOrder();
		this.selectPeriod(period);
		this.selectOrderType(type);
		this.setStartDateInOrder(startDate);
		this.clickOnPlanTab();
		this.selectPlanInCustomer(firstPlanDescription);
		this.clickOnRemoveButton();
		this.selectPlanInCustomer(planDescription2);
		this.clickUpdate();
		this.clickSaveChanges();
		return GlobalController.brw.initElements(CustomersPage.class);
	}

    public void verifyShowAllOrder(String customerName) throws Exception {
        GlobalController.brw.selectListItem(this.TAB_LIST_CUSTOMER_NAME, customerName);
        GlobalController.brw.clickLinkText(this.LT_SHOW_ALL_ORDER);
        Assert.assertTrue(GlobalController.brw.isElementPresent(this.TAB_ORDERS));
        GlobalController.brw.selectListItem(this.TAB_ORDERS, customerName);
    }
    
    public CustomersPage selectCustomerRecent(String user) throws Exception {
    	  GlobalController.brw.click(this.table_selectCus);
    	  return GlobalController.brw.initElements(CustomersPage.class);
    	 }
    
    public CustomersPage createOrderwithdifferentCurrency(String planvalue, String testDataSetName, String category) throws Exception {
    	  String period = TestData.read("PageCustomers.xml", testDataSetName, "period", category);
    	  String type = TestData.read("PageCustomers.xml", testDataSetName, "type", category);

    	  this.selectPeriod(period);
    	  this.selectOrderType(type);
    	  this.clickOnPlanTab();
    	  this.selectPlanItemInCustomer(planvalue);
    	  this.clickUpdate();
    	  this.clickSaveChanges();
    	  return GlobalController.brw.initElements(CustomersPage.class);
    	 }
}
