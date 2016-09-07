package com.jbilling.framework.pageclasses;

import java.util.HashMap;

import org.testng.Assert;

import com.jbilling.framework.globals.GlobalConsts;
import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AccountTypeField;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AccountTypeInfo;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddMetaDataFields;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddMetaDataGroupFields;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddNewEnumeration;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.CollectionAgeingStep;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.NewEnumerationMsg;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageSuccessMessages;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PaymentMethodField;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.SetEnumValues;
import com.jbilling.framework.utilities.textutilities.TextUtilities;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;
import com.jbilling.framework.utilities.xmlutils.TestData;

public class ConfigurationPage {
	ConfigPropertiesReader propReader = new ConfigPropertiesReader();
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread
			.currentThread().getStackTrace()[1].getClassName());

	@LocateBy(xpath = "//div[@class='menu-items']/ul/li")
	private ElementField LB_CONFIGURATION_ITEMS;

	@LocateBy(xpath = "//div[@id='column1']/div[@class='table-box']/table/tbody//td/a/strong")
	private ElementField LV_PLUGIN_CATEGORIES;

	@LocateBy(xpath = "//span[text()='Add New']")
	private ElementField LT_ADD_NEW_BUTTON;

	@LocateBy(xpath = "//span[text()='Save Changes']")
	private ElementField LT_SAVE_CHANGES;

	@LocateBy(xpath = "//span[text()='Cancel']")
	private ElementField LT_CANCEL;

	@LocateBy(xpath = "//span[text()='Upload File']")
	private ElementField LT_UPLOAD_FILE;

	@LocateBy(xpath = "//input[@type='file']")
	private ElementField LT_BROWSE_BUTTON;

	@LocateBy(xpath = "//span[contains(text(),'Edit')]")
	private ElementField LT_EDIT_BUTTON;

	@LocateBy(xpath = "//a[@class='submit save']")
	private ElementField IT_SAVE_CHANGES;

	@LocateBy(xpath = "//select[@id='typeId']")
	private ElementField DD_PLUGINTYPE;

	@LocateBy(xpath = "//input[@id='processingOrder']")
	private ElementField TB_ORDER;

	@LocateBy(xpath = "//input[@id='name']")
	private ElementField TB_ACCOUNT_TYPE_INVOICE_NAME;

	@LocateBy(xpath = "//input[@id='plgDynamic.1.name']")
	private ElementField TB_PARAMETER_1_NAME;

	@LocateBy(xpath = "//input[@id='plgDynamic.1.value']")
	private ElementField TB_PARAMETER_1_VALUE;

	@LocateBy(xpath = "//img[@alt='add']")
	private ElementField LT_ADDMOREFIELD;

	@LocateBy(xpath = "//img[@alt='remove']")
	private ElementField LT_ADDMOREFIELD_NAME;

	@LocateBy(xpath = "//span[text()='Save Plug-in']")
	private ElementField LT_SAVEPLUGINS;

	@LocateBy(xpath = "//input[@id='nextRunDate']")
	private ElementField TB_NEXTRUNDATE;

	@LocateBy(xpath = "//input[@id='generateReport']")
	private ElementField CB_GENERATEREPORT;

	@LocateBy(xpath = "//select[@id='periodUnitId']")
	private ElementField DD_BILLINGPERIOD_UNIT;

	@LocateBy(xpath = "//select[@id='usagePool.consumptionActions.1.type']")
	private ElementField DD_usagePoolConsumptionNotification;

	@LocateBy(xpath = "//select[@id='usagePool.consumptionActions.1.notificationId']")
	private ElementField DD_usagePoolConsumptionNotification_Invoice;

	@LocateBy(xpath = "//select[@id='usagePool.consumptionActions.1.mediumType']")
	private ElementField DD_usagePoolConsumptionNotification_Invoice_mail;

	@LocateBy(xpath = "//select[@id='usagePool.consumptionActions.2.type']")
	private ElementField DD_usagePoolConsumptionFee;

	@LocateBy(xpath = "//select[@id = 'usagePool.itemTypes']")
	private ElementField DD_PRODUCT_CATEGORY;

	@LocateBy(xpath = "//select[@id = 'usagePool.items']")
	private ElementField DD_PRODUCT;

	@LocateBy(xpath = "//input[@id='maximumPeriods']")
	private ElementField TB_MAXPERIODTOINVOICE;

	@LocateBy(xpath = "//input[@id='billing.proratingType.neverProrating']")
	private ElementField RB_NEVRPRORATING_TO_BILLING_PROCESS;

	@LocateBy(xpath = "//span[text()='Run Commission Process']")
	private ElementField BT_RUN_COMMISSION_TO_BILLINGPROCESS;

	@LocateBy(xpath = "//span[text()='Run Billing']")
	private ElementField BT_RUN_BILLING_PROCESS;

	@LocateBy(xpath = "//input[@id='obj[0].statusStr']")
	private ElementField TB_STEP1;

	@LocateBy(xpath = "//input[@id='obj[0].days']")
	private ElementField TB_FORDAYS1;

	@LocateBy(xpath = "//input[@id='obj[0].sendNotification']")
	private ElementField CB_NOTIFICATION1;

	@LocateBy(xpath = "//input[@id='obj[0].paymentRetry']")
	private ElementField CB_PAYMENT1;

	@LocateBy(xpath = "//input[@id='obj[0].suspended']")
	private ElementField CB_SUSPEND1;

	@LocateBy(xpath = "//table[@id='ageingStepTable']/tbody/tr[2]/td[1]/strong")
	private ElementField TB_STEP2ID;

	@LocateBy(xpath = "//input[@id='obj[1].statusStr']")
	private ElementField TB_STEP2;

	@LocateBy(xpath = "//input[@id='obj[1].days']")
	private ElementField TB_FORDAYS2;

	@LocateBy(xpath = "//input[@id='obj[1].sendNotification']")
	private ElementField CB_NOTIFICATION2;

	@LocateBy(xpath = "//input[@id='obj[1].paymentRetry']")
	private ElementField CB_PAYMENT2;

	@LocateBy(xpath = "//input[@id='obj[1].suspended']")
	private ElementField CB_SUSPEND2;

	@LocateBy(xpath = "//input[@id='obj[2].statusStr']")
	private ElementField TB_STEP3;

	@LocateBy(xpath = "//input[@id='obj[2].days']")
	private ElementField TB_FORDAYS3;

	@LocateBy(xpath = "//input[@id='obj[2].sendNotification']")
	private ElementField CB_NOTIFICATION3;

	@LocateBy(xpath = "//input[@id='obj[2].sendNotification']")
	private ElementField CB_PAYMENT3;

	@LocateBy(xpath = "//input[@id='obj[2].suspended']")
	private ElementField CB_SUSPEND3;

	@LocateBy(xpath = "//input[@id='obj[3].statusStr']")
	private ElementField TB_STEP4;

	@LocateBy(xpath = "//input[@id='obj[3].days']")
	private ElementField TB_FORDAYS4;

	@LocateBy(xpath = "//input[@id='obj[3].sendNotification']")
	private ElementField CB_NOTIFICATION4;

	@LocateBy(xpath = "//input[@id='obj[3].paymentRetry']")
	private ElementField CB_PAYMENT4;

	@LocateBy(xpath = "//input[@name='obj[1].description_1']")
	private ElementField TB_PROCESSING_ORDER_STATUS;

	@LocateBy(xpath = "//input[@name='obj[2].description_1']")
	private ElementField TB_FINISHED_ORDER_STATUS;

	@LocateBy(xpath = "//input[@name='obj[3].description_1']")
	private ElementField TB_SUSUPENDED_ORDER_STATUS;

	@LocateBy(xpath = "//input[@id='obj[3].suspended']")
	private ElementField CB_SUSPEND4;

	@LocateBy(xpath = "//input[@id='collectionsRunDate']")
	private ElementField TB_COLLECTION_DATE;

	@LocateBy(xpath = "//input[@id='run' and @value='Run Collections']")
	private ElementField LT_RUN_COLLECTIONS;

	@LocateBy(xpath = "//span[text()='Run Collections']")
	private ElementField LT_POPUP_RUN_COLLECTIONS;

	@LocateBy(xpath = "//img[@alt='add']")
	private ElementField LT_ADD_AGEING_STEP;

	@LocateBy(xpath = "//table[@id='users']/tbody//td/a/strong")
	private ElementField LV_CONFIGURATIONAL_PREFERENCES;

	@LocateBy(xpath = "//table[@id='usagePools']/tbody//td/a/strong")
	private ElementField LV_USAGE_POOL_NAME;

	@LocateBy(xpath = "//table[@id='data_grid_column1']/tbody//td/div")
	private ElementField LV_JQCONFIGURATIONAL_PREFERENCES;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//table[@id='users']")
	private ElementField TAB_USERS;

	@LocateBy(xpath = "//table[@id='users']/tbody//td/a/strong")
	private ElementField TAB_USERS_ANCHORS_LIST;

	@LocateBy(xpath = "//span[text()='Permissions']")
	private ElementField LT_USERS_PERMISSIONS;

	@LocateBy(xpath = "//label[contains(text(),'Edit plug-in')]/preceding-sibling::input[@type='box']")
	private ElementField CB_PERMISSION_CONFIGURATION_EDITPLUGIN;

	@LocateBy(xpath = "//input[@id = 'permission.1902']")
	private ElementField CB_PERMISSION_CONFIGURATION_PLUGIN;

	@LocateBy(xpath = "//label[contains(text(),'Edit payment')]/preceding-sibling::input[@type='checkbox']")
	private ElementField CB_PERMISSION_CONFIGURATION_EDITPAYMENT;

	@LocateBy(xpath = "//label[contains(text(),'Delete payment')]/preceding-sibling::input[@type='checkbox']")
	private ElementField CB_PERMISSION_CONFIGURATION_DELETE_PAYMENT;

	@LocateBy(xpath = "//span[contains(text(),'Copy Company')]")
	private ElementField LT_COPY_COMPANY;

	@LocateBy(xpath = "//input[@id='isCompanyChild']")
	private ElementField CB_CHILD_COMPANY;

	@LocateBy(xpath = "//input[@id='childCompany']")
	public ElementField TB_TEMPLATE_COMPANY_NAME;

	@LocateBy(xpath = "//button/span[text()='Yes']")
	private ElementField LT_CONFIRM_YES;

	@LocateBy(xpath = "//button/span[text()='No']")
	private ElementField LT_CONFIRM_NO;

	@LocateBy(xpath = "//strong[text()='Done']/../p")
	private ElementField TXT_COMPANY_CREDENTIALS;

	@LocateBy(xpath = "//input[@id='name']")
	private ElementField TB_MEDIATIONNAME;

	@LocateBy(xpath = "//input[@id='orderValue']")
	private ElementField TB_ORDERVALUE;

	@LocateBy(xpath = "//select[@id='mediationJobLauncher']")
	private ElementField DD_JOBLAUNCHER;

	@LocateBy(xpath = "//input[@id='global']")
	private ElementField CB_GLOBAL;

	@LocateBy(xpath = "//span[text()='Run Mediation']")
	private ElementField LT_RUN_MEDIATION;

	@LocateBy(xpath = "//input[@id='description']")
	private ElementField TB_ORDER_PERIOD_DESCRIPTION;

	@LocateBy(xpath = "//select[@id='periodUnitId']")
	private ElementField DD_ORDER_PERIOD_UNIT;

	@LocateBy(xpath = "//input[@id='value']")
	private ElementField TB_ORDER_PERIOD_VALUE;

	@LocateBy(xpath = "//strong[text()='Should use JQGrid for tables']/parent::a")
	private ElementField LT_USEJQGRIDFORTABLES;

	@LocateBy(xpath = "//div[text()='Should use JQGrid for tables']/parent::td")
	private ElementField LT_JQGRIDFORTABLES;

	@LocateBy(xpath = "//strong[text()='ITG invoice notification']/parent::a")
	private ElementField LT_TGINotification;

	@LocateBy(xpath = "//input[@id='preference.value']")
	private ElementField TB_CONFIGURATION_PREFERENCE_VALUE;

	@LocateBy(xpath = "//input[@id='description']")
	private ElementField TB_ACCOUNT_TYPE_NAME;

	@LocateBy(xpath = "//input[@id='mainSubscription.nextInvoiceDayOfPeriod']")
	private ElementField TB_ACCOUNT_TYPE_BILLING_CYCLE;

	@LocateBy(xpath = "//input[@id='invoiceDesign']")
	private ElementField TB_ACCOUNT_TYPE_INVOICE_DESIGN;

	@LocateBy(xpath = "//table[@id='periods']")
	private ElementField TAB_ACCOUNT_TYPES_NAMES;

	@LocateBy(xpath = "//input[@class='cb checkbox']")
	private ElementField CB_APPLIED;

	@LocateBy(xpath = "//table[@id='periods']/tbody//td/a/strong")
	private ElementField TAB_ACCOUNT_TYPES_LIST_NAMES;

	@LocateBy(xpath = "//table[@id='categories']")
	private ElementField TBL_CATEGORIES_TABLE;

	@LocateBy(xpath = "//table[@id='periods']/tbody/tr[1]/td/a[1]/strong")
	private ElementField TXT_RECENT_ACCOUNT_TYPE;

	@LocateBy(xpath = "//span[text()='Add Information Type']")
	private ElementField LT_ADD_NEW_INFORMATION;

	@LocateBy(xpath = "//input[@id='name']")
	private ElementField TB_INFORMATION_TYPE_NAME;

	@LocateBy(xpath = "//span[text()='Add New Metafield']")
	private ElementField LT_ADD_NEW_METAFIELD;

	@LocateBy(xpath = "//span[text()='-']/parent::li")
	private ElementField LI_METAFIELDTAB_ACTIVATOR;

	@LocateBy(xpath = "//span[text()='-']")
	private ElementField SPAN_NEW_METAFIELDTAB;

	@LocateBy(xpath = "//input[@id='metaField0.name']")
	private ElementField TB_METAFIELDNAME;

	@LocateBy(xpath = "//select[@id='fieldType0']")
	private ElementField DD_METAFIELDTYPE;

	@LocateBy(xpath = "//li[contains(@class,'active')]/following::li//a/span[contains(text(),'Update')]")
	private ElementField LT_UPDATE_METAFIELD;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//li[contains(@class,'active')]/following::li//a/span[contains(text(),'Remove')]")
	private ElementField LT_REMOVE_METAFIELD;

	@LocateBy(xpath = "//div[@id='review-box']//span[text()='Save Changes']")
	private ElementField LT_SAVE_CHANGES_TO_INFORMATION_TYPE;

	@LocateBy(xpath = "//span[text()='Prices']")
	private ElementField LT_ACCOUNT_TYPE_PRICES;

	@LocateBy(xpath = "//select[@id='typeId']")
	private ElementField DD_ACCOUNT_TYPE_PRODUCT_CATEGORY;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//table[@id='products']")
	private ElementField TAB_ACCOUNT_TYPE_PRICES_PRODUCTS;

	@LocateBy(xpath = "//table[@id='products']/tbody//td/a/strong")
	private ElementField TAB_ACCOUNT_LIST_TYPE_PRICES_PRODUCTS;

	@LocateBy(xpath = "//span[text()='Add price']")
	private ElementField LT_ADD_PRICE;

	@LocateBy(xpath = "//input[@id = 'model.0.rateAsDecimal']")
	private ElementField TB_RATE;

	@LocateBy(xpath = "//img[@alt='edit']")
	private ElementField IBT_EDIT;

	@LocateBy(xpath = "//select[@id='templateId']")
	private ElementField DD_PAYMENTTEMPLATE;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//option[text()='Payment Card']")
	private ElementField LT_PAYMENTMETHOD;

	@LocateBy(id = "methodName")
	private ElementField TB_METHODNAME;

	@LocateBy(id = "isRecurring")
	private ElementField CHK_ISRECURRING;

	@LocateBy(id = "allAccount")
	private ElementField CHK_ALLACCOUNTTYPES;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='accountTypes']")
	private ElementField DD_ACCOUNTTYPE;

	@LocateBy(xpath = "//span[text()='cc.number']/parent::li")
	private ElementField TXT_CCNUMBERTAB_ACTIVATOR_LINK;

	@LocateBy(xpath = "//span[text() = 'cc.number']")
	private ElementField TXT_CCNUMBERTAB;

	@LocateBy(xpath = "//span[text()='cc.number']/parent::li/following::li[contains(@id,'editor')]//select[contains(@id,'newDescriptionLanguage')]")
	private ElementField DD_CCNUMBER_ERROR_LANGUAGE;

	@LocateBy(xpath = "//span[text()='cc.number']/parent::li/following::li[contains(@id,'editor')]//a[contains(@onclick,'addNewDescription')]/img")
	private ElementField IBT_ADDCCNUMBERERRMSG;

	@LocateBy(xpath = "//span[text()='cc.number']/../..//label[text()='English Error message']/../div/input[1]")
	private ElementField TB_CARDEXPIRYMESSAGE;

	@LocateBy(xpath = "//span[text()='cc.number']/parent::li/following::li//div[contains(@id,'descriptions')]//a/img[contains(@src,'cross.png')]")
	private ElementField IBT_REMOVE_CCNUMBER_ERR_MSG;

	@LocateBy(xpath = "//span[text()='cc.number']/parent::li/following::li[contains(@id,'editor')]//a//span[text()='Update']")
	private ElementField BT_UPDATECARD;

	@LocateBy(xpath = "//span[text()='cc.expiry.date']/parent::li")
	private ElementField TXT_EXPIRYDATETAB_ACTIVATOR_LINK;

	@LocateBy(xpath = "//span[text()='cc.expiry.date']")
	private ElementField TXT_EXPIRYDATETAB;

	@LocateBy(xpath = "//span[text()='cc.expiry.date']/parent::li/following::li[contains(@id,'editor')]//select[contains(@id,'newDescriptionLanguage')]")
	private ElementField DD_EXPIRYDATE_ERROR_LANGUAGE;

	@LocateBy(xpath = "//span[text()='cc.expiry.date']/parent::li/following::li[contains(@id,'editor')]//a[contains(@onclick,'addNewDescription')]/img")
	private ElementField IBT_ADD_EXPIRY_DATE_ERR_MSG;

	@LocateBy(xpath = "//span[text()='cc.expiry.date']/../..//label[text()='English Error message']/../div/input[1]")
	private ElementField TB_EXPIRYDATEMESSAGE;

	@LocateBy(xpath = "//span[text()='cc.expiry.date']/parent::li/following::li//div[contains(@id,'descriptions')]//a/img[contains(@src,'cross.png')]")
	private ElementField IBT_REMOVE_EXPIRYDATE_ERR_MSG;

	@LocateBy(xpath = "//span[text()='cc.expiry.date']/parent::li/following::li[contains(@id,'editor')]//a//span[text()='Update']")
	private ElementField BT_UPDATEDATE;

	@LocateBy(xpath = "//div[@class = 'buttons']//a[contains(@onclick,'submit')]")
	private ElementField BT_SELECT;

	@LocateBy(xpath = "//div[@id='breadcrumbs']")
	private ElementField BREADCRUMBS;

	@LocateBy(id = "description")
	private ElementField TB_COMPANY_DESCRIPTION;

	@LocateBy(id = "address1")
	private ElementField TB_COMPANY_ADDRESS;

	@LocateBy(id = "city")
	private ElementField TB_COMPANY_CITY;

	@LocateBy(id = "stateProvince")
	private ElementField TB_COMPANY_STATE;

	@LocateBy(id = "permission.1901")
	private ElementField CB_VIEW_PLUGIN;

	@LocateBy(id = "permission.1902")
	private ElementField CB_EDIT_PLUGIN;

	@LocateBy(id = "permission.1903")
	private ElementField CB_EDIT_ROLE;

	@LocateBy(xpath = "//div[@id='error-messages']/following-sibling::div//p")
	private static ElementField TXT_PLUGIN_DENIED;

	@LocateBy(id = "roles")
	private ElementField TAB_ROLES;

	@LocateBy(xpath = "//label[text()='Show agent menu']/../input[2]")
	private ElementField CB_SHOW_AGENT_MENU;

	@LocateBy(xpath = "//label[text()='Edit agent']/../input[2]")
	private ElementField CB_EDIT_AGENT;

	@LocateBy(xpath = "//label[text()='View agent details']/../input[2]")
	private ElementField CB_VIEW_AGENT_DETAILS;

	@LocateBy(xpath = "//a[@class='submit delete']")
	private ElementField LT_DELETE;

	@LocateBy(id = "creditLimitAsDecimal")
	private ElementField TB_CREDIT_LIMIT;

	@LocateBy(id = "creditNotificationLimit1AsDecimal")
	private ElementField TB_CREDIT_LIMIT_NOT_ONE;

	@LocateBy(id = "creditNotificationLimit2AsDecimal")
	private ElementField TB_CREDIT_LIMIT_NOT_TWO;

	@LocateBy(xpath = "//div[@id='messages']//ul/li")
	private ElementField TXT_VALIDATION_MESSAGE;

	@LocateBy(id = "payment-method-select")
	private ElementField DD_PAYMENT_METHOD_TYPE;

	@LocateBy(id = "gs_typeId")
	private ElementField TB_ACCOUNT_TYPE_SEARCH;

	@LocateBy(xpath = "//option[text()='")
	private ElementField TXT_ACCOUNT_TYPE;

	@LocateBy(xpath = "//strong[contains(text(),'Payment gateway integration')]")
	private ElementField LT_PAYMENT_PLUGIN;

	@LocateBy(id = "plg-parm-PaypalUserId")
	private ElementField TB_PAYPAL_USERNAME;

	@LocateBy(id = "plg-parm-PaypalPassword")
	private ElementField TB_PAYPAL_PASSWORD;

	@LocateBy(id = "plg-parm-PaypalSignature")
	private ElementField TB_PAYPAL_SIGNATURE;

	@LocateBy(xpath = "//a[contains(text(),'Company')]")
	private ElementField COMPANY_LINK;

	@LocateBy(xpath = "//div[@class='table-box']//tr//td//a")
	private ElementField TMD_ASSET_FIELD;

	@LocateBy(id = "metaField.name")
	private ElementField TB_MD_NAME;

	@LocateBy(id = "metaField.dataType")
	private ElementField DD_MD_NEW_DATA_TYPE;

	@LocateBy(id = "mandatoryCheck")
	private ElementField CB_NEW_MANDATORY;

	@LocateBy(id = "disableCheck")
	private ElementField CB_MD_NEW_DISABLE;

	@LocateBy(id = "defaultValue")
	private ElementField CB_MD_NEW_DEAULT;

	@LocateBy(id = "metaField.validationRule.ruleType")
	private ElementField DD_MD_VALIDATION;

	@LocateBy(xpath = "//a[@class='submit save']/span")
	private ElementField LT_MD_SAVECHANGES;

	@LocateBy(xpath = "//a[@class='submit cancel']/span")
	private ElementField LT_MD_CANCEL;

	@LocateBy(xpath = "//input[@id='helpCheck'] ")
	private ElementField CB_MD_HELP;

	@LocateBy(xpath = "//table[@id='roles']//tbody//tr")
	private ElementField TB_VERIFY_CREATED_META_DAT;

	@LocateBy(xpath = "//select[@id='vis-cols-multi-sel-group-left']//option")
	private ElementField MV_METADATA_GROUP;

	@LocateBy(xpath = "//a[@id='vis-cols-multi-sel-to-right']/span")
	private ElementField LT_ARROW_TOADD;

	@LocateBy(xpath = "//input[@id='name']")
	private ElementField LT_GROUP_META_DATA_NAME;

	@LocateBy(xpath = "//table[@id='payments']")
	private ElementField TAB_INNER_TABLE;

	@LocateBy(xpath = "//div[text()='Should use JQGrid for tables']/..")
	private ElementField LT_JQGRID;

	@LocateBy(xpath = "//table[@class='jqTable ui-jqgrid-btable']//tbody//tr/td/div")
	private ElementField TAB_ACCOUNT_TYPE_SEARCH;

	@LocateBy(xpath = "//select[contains(@id,'paymentMethodTypeId')]")
	private ElementField DD_PAYMENT_TEMPLATE;

	@LocateBy(id = "disableCheck")
	private ElementField CB_DISABLED;

	@LocateBy(xpath = "//select[contains(@id,'dataType')]")
	private ElementField DD_INFO_META_DATA_TYPE;

	@LocateBy(xpath = "//div[@id='left-column']//li[21]")
	private ElementField TAB_ORDERCHANGESTATUSES;

	@LocateBy(xpath = "//div[@class='lang_description_1']//input[@id='obj_0_description_1']")
	private ElementField NAME_ENGLISH_TEXTBOX_1;

	@LocateBy(xpath = "//div[@class='lang_description_1']//input[@id='obj_1_description_1']")
	private ElementField NAME_ENGLISH_TEXTBOX_2;

	@LocateBy(xpath = "//strong[contains(text(),'Order Change Statuses')]")
	private ElementField ORDER_CHANGE_STATUSES_PAGE_HEADER;

	@LocateBy(xpath = "//*[@id='obj[1].order']")
	private ElementField ORDER_TEXT_FIELD;

	@LocateBy(xpath = "//*[@id='obj[1].applyToOrder']")
	private ElementField SECOND_ROW_CHECKBOX;

	@LocateBy(xpath = "//*[@id='obj[0].applyToOrder']")
	private ElementField FIRST_ROW_CHECKBOX;

	@LocateBy(xpath = "//table[@id='invoiceTemplates']")
	private ElementField TB_Invoice_Template_NAMES;

	@LocateBy(xpath = "//*[@id='17']/em")
	private ElementField PLUGIN_ROW_NUMBER_17;

	@LocateBy(xpath = "//*[@id='processingOrder']")
	private ElementField ADD_NEW_PLUGIN_ORDER_TEXTBOX;

	@LocateBy(xpath = "//*[@id='typeId']")
	private ElementField ADD_NEW_PLUGIN_ORDER_DROPDOWN;

	@LocateBy(xpath = "//div[@class='heading']//strong")
	private ElementField ADD_NEW_PLUGIN_PAGE_HEADER;

	@LocateBy(xpath = "//*[@id='orderChangeStatusesTable']/tbody/tr[2]/td[5]/a/img")
	private ElementField SECOND_ROW_APPLY_INDICATOR;

	@LocateBy(xpath = "//*[@id='orderChangeStatusesTable']/tbody/tr[3]/td[5]/a/img")
	private ElementField THIRD_ROW_APPLY_INDICATOR;

	@LocateBy(xpath = "//*[@id='column1']/div/table/thead/tr/th[2]")
	private ElementField Plugins_categories_HEADER;

	@LocateBy(xpath = "//*[@id='CUSTOMER']/strong")
	private ElementField SELECT_CUSTOMER_IN_META_FIELD;

	@LocateBy(xpath = "//*[@id='column1']/div[1]/table/thead/tr/th")
	private ElementField METAFEILD_HEADER;

	@LocateBy(xpath = "//*[@id='metaField.name']")
	private ElementField METAFEILD_NAME;

	@LocateBy(xpath = "//*[@id='metaField.dataType']")
	private ElementField METAFEILD_DATATYPE;

	@LocateBy(xpath = "//*[@id='mandatoryCheck']")
	private ElementField MANDATORY_CHECK;

	@LocateBy(xpath = "//*[@class='table-box']//strong[contains(text(),'Salary')]")
	private ElementField META_SEARCH;

	@LocateBy(xpath = "(//button[@type='button'])[3]")
	private ElementField YES_BUTTON_FOR_DELETE_BUTTON;

	@LocateBy(xpath = "//a[contains(text(),'Payment Method')]")
	private ElementField PAYMENT_METHOD_LINK;

	@LocateBy(xpath = "//div[@class='menu-items']/ul/li/a[contains(text(),'Account Type')]")
	private ElementField ACCOUNTTYPE_LINK;

	@LocateBy(xpath = "//div[@id='messages']/div[@class='msg-box error']/strong")
	private static ElementField STRONG_ERROR_MESSAGE;

	@LocateBy(xpath = "//div[@id='messages']/div[@class='msg-box error']/ul/li")
	private static ElementField STRONG_ERROR_MESSAGE1;

	@LocateBy(xpath = "//a[contains(.,'Order Statuses')]")
	private ElementField LT_ORDER_STATUSES;

	@LocateBy(xpath = "//a[contains(.,'INVOICE')]")
	private ElementField TT_InvoiceFlag;

	@LocateBy(xpath = "//a[contains(.,'FINISHED')]")
	private ElementField TT_FINISHEDFlag;

	@LocateBy(xpath = "//a[contains(.,'NOT_INVOICE')]")
	private ElementField TT_NOTVOICEFlag;

	@LocateBy(xpath = "//a[contains(.,'SUSPENDED_AGEING')]")
	private ElementField TT_SUSPENDEDAGEINGFlag;

	@LocateBy(xpath = "//a[contains(.,'Active')]")
	private ElementField TT_INVOICEDESCR;

	@LocateBy(xpath = "//a[contains(.,'Finished')]")
	private ElementField TT_FINISHEDDESCR;

	@LocateBy(xpath = "//a[contains(.,'Suspended')]")
	private ElementField TT_NOTINVOICEDESCR;

	@LocateBy(xpath = "//a[contains(.,'Suspended ageing(auto)')]")
	private ElementField TT_SUSPENDEDDESCR;

	@LocateBy(xpath = "//input[@id='description']")
	private ElementField TB_NEWDESCRIPTION;

	@LocateBy(xpath = "//select[contains(@id,'orderStatusFlag')]")
	private ElementField DD_FLAGTYPE;

	@LocateBy(xpath = "//div[@id='error-messages']/ul/li")
	private ElementField TXT_ENUMERATION_MESSAGE;

	@LocateBy(xpath = "//div[@id='messages']//div[@class='msg-box error'][1]/ul/li")
	private ElementField TXT_ERROR_MESSAGE_ENUMERATION;

	@LocateBy(xpath = "//div[@id='error-messages']/ul/li[1]")
	private ElementField TXT_ENUMERATION_MESSAGE_NAME;

	@LocateBy(xpath = "//div[@id='error-messages']/ul/li[2]")
	private ElementField TXT_ENUMERATION_MESSAGE_VALUE;

	@LocateBy(id = "name")
	private ElementField TB_ENUMERATION_NAME;

	@LocateBy(id = "usagePool.names[0].content")
	private ElementField TB_ENGLISH_NAME;

	@LocateBy(id = "usagePool.cyclePeriodValue")
	private ElementField TB_CYCLE_PERIOD;

	@LocateBy(id = "usagePool.quantity")
	private ElementField TB_QUANTITY;

	@LocateBy(id = "usagePool.consumptionActions.1.percentage")
	private ElementField TB_CONSUMPTION1;

	@LocateBy(id = "usagePool.consumptionActions.2.percentage")
	private ElementField TB_CONSUMPTION2;

	@LocateBy(id = "usagePool.consumptionActions.2.productId")
	private ElementField TB_PRODUCT_ID;

	@LocateBy(xpath = "//input[contains(@id,'.value')]")
	private ElementField TB_ZERO_ENUMERATION_VALUE;

	@LocateBy(xpath = "//span[text()='Edit']")
	private ElementField LT_Edit;

	@LocateBy(xpath = "//label[text()='1']/..//input[contains(@id,'.value')]")
	private ElementField TB_FIRST_ENUMERATION_VALUE;

	@LocateBy(xpath = "//table[@id='enumerations']/tbody/tr/td//strong")
	private ElementField TAB_ENUMERATION_FIRST_CELL;

	@LocateBy(xpath = "//table[@id='enumerations']")
	private ElementField TAB_ENUMERATION;

	@LocateBy(xpath = "//img[@alt='Add more values']")
	private ElementField IMG_ADD_MORE_ENUMERATION_VALUE;

	@LocateBy(xpath = "//img[@alt='remove this value']")
	private ElementField IMG_REMOVE_ENUMERATION_VALUE;

	@LocateBy(xpath = "//span[@id='mandatory-meta-field']/../following::input[1]")
	private ElementField TB_METAFIELD_NAME;

	@LocateBy(xpath = "//label[text()='MetaField Type']/following::select[1]")
	private ElementField DD_METAFIELD_TYPE;

	@LocateBy(xpath = "//a[contains(text(),'Meta Fields')]")
	private ElementField METAFIELD_LINK;

	@LocateBy(xpath = "//div[@class='table-box']/table/tbody/tr/td/a/strong")
	private ElementField METAFIELD_CATEGORY;

	@LocateBy(xpath = "//ul[@id='metafield-ait']/form[1]//span[text()='Not Mandatory']")
	private ElementField METAFIELD_BAR;

	@LocateBy(xpath = "//label[text()='Validation Rule']/following::select")
	private ElementField DD_VALIDATION_RULE;

	@LocateBy(xpath = "//label[text()='Include in Notifications']/following::select")
	private ElementField DD_INCLUDE_NOTIFICATION;

	@LocateBy(xpath = "(//span[@id='mandatory-meta-field']/../following::input[1])[2]")
	private ElementField TB_METAFIELD_NAME2;

	@LocateBy(xpath = "(//label[text()='MetaField Type']/following::select[1])[2]")
	private ElementField DD_METAFIELD_TYPE2;

	@LocateBy(xpath = "(//ul[@id='metafield-ait']/form//span[text()='Not Mandatory'])[2]")
	private ElementField METAFIELD_BAR2;

	@LocateBy(xpath = "(//li[contains(@class,'active')]/following::li//a/span[contains(text(),'Update')])[2]")
	private ElementField LT_UPDATE_METAFIELD2;

	@LocateBy(xpath = "//*[@id='ORDER']/strong")
	private ElementField SELECT_ORDER_IN_META_FIELD;

	@LocateBy(xpath = "//*[@id='PAYMENT']/strong")
	private ElementField SELECT_PAYMENT_IN_META_FIELD;

	@LocateBy(xpath = "//span[text()='Clone']")
	private ElementField BT_CLONE;

	@LocateBy(xpath = "//table[@id='data_grid_column1']/tbody/tr[@id='63']/td/div")
	private ElementField TN_RECONFIGURE;

	@LocateBy(xpath = "//li/a[contains(text(), 'Order Change Statuses')]")
	private ElementField LT_CHANGEORDER_STATUSES;

	@LocateBy(xpath = "//ul[@class='list']//li/a[contains(text(), 'Order Change Types')]")
	private ElementField LT_CHANGEORDER_TYPE;

	@LocateBy(xpath = "//a/span[contains(text(), 'Save Changes')]")
	private ElementField LT_SAVECHANGES;

	@LocateBy(xpath = "//input[@name='allowOrderStatusChange']")
	private ElementField CB_ALLOWORDER;

	@LocateBy(xpath = "//tr[2]//input[@id='obj_1_description_1']")
	private ElementField TB_ORDERSTATUS_ORDERNAME;

	@LocateBy(xpath = "//tr[2]//input[@class='inp-bg numericOnly inp4']")
	private ElementField TB_ORDERSTATUS_ORDERNUMBER;

	@LocateBy(xpath = "//tr[2]//input[@class='cb checkbox']")
	private ElementField CHK_APPLIEDORDER;

	@LocateBy(xpath = "//a[@class='submit edit']")
	private ElementField LT_EDITPRODUCT;

	@LocateBy(xpath = "//input[@id='product.standardPartnerPercentageAsDecimal']")
	private ElementField TB_AGENT;

	@LocateBy(xpath = "//input[@id='product.masterPartnerPercentageAsDecimal']")
	private ElementField TB_MASTER;

	@LocateBy(xpath = "//div[@class='form-columns']/div/div/span")
	private ElementField PRODUCT_ID;

	@LocateBy(xpath = "//div[@class = 'buttons']//span[text()='Save Changes']")
	private ElementField BT_EDIT_SAVECHANGES;

	@LocateBy(xpath = "//div[@class='form-columns']//div[@class='inp-bg ']/input[@id='name']")
	private ElementField TB_ORDERENGLISHNAME;

	@LocateBy(xpath = "//select[@id='itemTypes_selector']")
	private ElementField DD_SELECTPRODUCTCATEGORY;

	@LocateBy(xpath = "//a[contains(text(),'Order Periods')]")
	private ElementField ORDER_PERIOD_LINK;

	@LocateBy(xpath = "//td[contains(text(),'Euro')]/following::input[@type='checkbox'][1]")
	private ElementField CB_EURO_CURRENCY;

	@LocateBy(xpath = "//a[contains(text(),'Currencies')]")
	private ElementField LT_CURRENCY;

	@LocateBy(xpath = "//select[@id='defaultCurrencyId']")
	private ElementField DD_DEFAULT_CURRENCY;

	@LocateBy(xpath = "//table[@class='dataTable']/tbody/tr[1]/td[2]/a")
	private ElementField METAFIELD_ID;

	@LocateBy(xpath = "/html/body/div/div[2]/div[3]/div[3]/div[2]/div/div/div[3]/a/span[text()='Edit']")
	private ElementField bttn_Edit_AITType;

	@LocateBy(xpath = "//input[@id='useForNotifications']")
	private ElementField chk_UserNotification_AccntType;

	@LocateBy(xpath = "//button[@class ='ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only']")
	private ElementField bttn_yes_popup_ait;

	@LocateBy(xpath = "//Select[@id='aitName']")
	private ElementField drp_Include_in_Notifications;

	public ConfigurationPage verifyAddPluginPageHeader() throws Exception {
		String headerText = GlobalController.brw
				.getText(this.ADD_NEW_PLUGIN_PAGE_HEADER);

		if (headerText.contains("ADD NEW PLUG-IN")) {

		} else {
			throw new Exception("Test Case failed: " + headerText);
		}

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will verify the Plug-ins categories Header
	 * 
	 * @return
	 * @throws Exception
	 */

	public ConfigurationPage verifyPluginscategoriesPageHeader()
			throws Exception {
		String headerText = GlobalController.brw
				.getText(this.Plugins_categories_HEADER);

		if (headerText.contains("PLUG-INS CATEGORIES")) {

		} else {
			throw new Exception("Test Case failed: " + headerText);
		}

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will delete row on orderChangeStatuses page
	 * 
	 * @return
	 * @throws Exception
	 */

	public ConfigurationPage setNumberOfRowsToTwo() throws Exception {
		boolean flag = false;
		flag = GlobalController.brw
				.isElementPresent(this.THIRD_ROW_APPLY_INDICATOR);
		if (flag == true) {
			GlobalController.brw.click(this.SECOND_ROW_APPLY_INDICATOR);
			this.clickSaveChangesButton();
			this.enterTestDataInBox2("secondRowInfo", "ai");
		} else {
			GlobalController.brw.clearTextBox(this.NAME_ENGLISH_TEXTBOX_2);
			this.enterTestDataInBox2("secondRowInfo", "ai");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	/**
	 * This method will enter text in the NAME,ORDER ,Click on + button on
	 * OrderChangeStatuses page
	 * 
	 * @param testDataSetName
	 * @param category
	 * @return
	 * @throws Exception
	 */
	public ConfigurationPage enterTestDataInBox2(String testDataSetName,
			String category) throws Exception {

		String secondRowNameTestData = TestData.read("PageConfiguration.xml",
				testDataSetName, "secondRowNameTestData", category);
		String secondRowOrderTestData = TestData.read("PageConfiguration.xml",
				testDataSetName, "secondRowOrderTestData", category);

		this.setSecondRowTestData(secondRowNameTestData, secondRowOrderTestData);

		this.clickPluginAddMoreParametersIcon();

		this.clickSaveChangesButton();

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set second row of data in order change statuses
	 * 
	 * @param secondRowTestData
	 * @throws Exception
	 */
	public void setSecondRowTestData(String secondRowNameTestData,
			String secondRowOrderTestData) throws Exception {
		// fill NAME
		GlobalController.brw.setText(this.NAME_ENGLISH_TEXTBOX_2,
				secondRowNameTestData);

		// fill ORDER
		GlobalController.brw.setText(this.ORDER_TEXT_FIELD,
				secondRowOrderTestData);

		// check checkbox is unchecked if not then do uncheck
		GlobalController.brw.uncheck(this.SECOND_ROW_CHECKBOX);
	}

	/**
	 * 
	 * This method verify if checkbox of "Test Status"is uncheck and if checkbox
	 * selected for "Default(Apply)"status on Order change statuses page
	 * 
	 * @throws Exception
	 */

	public ConfigurationPage CheckboxOrderChangeStatuses() throws Exception {
		boolean flagRowOne = GlobalController.brw
				.isElementPresent(this.FIRST_ROW_CHECKBOX);
		boolean flagRowTwo = GlobalController.brw
				.isElementPresent(this.SECOND_ROW_CHECKBOX);

		if ((flagRowOne == true) && (flagRowTwo == true)) {

			if ((GlobalController.brw
					.checkCheckBoxChecked(this.FIRST_ROW_CHECKBOX) == true)
					&& (GlobalController.brw
							.checkCheckBoxChecked(this.SECOND_ROW_CHECKBOX) == true)) {
				ConfigurationPage.logger
						.debug("Both the checkboxes are in correct state");
			} else {
				ConfigurationPage.logger
						.debug("The checkboxes are in Incorrect state");
			}
		} else {
			ConfigurationPage.logger
					.debug("The checkboxes are in Not displayed");
			throw new Exception("Test Case failed: " + flagRowOne + flagRowTwo);
		}
		this.clickSaveChangesButton();

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Click on "17 Generic internal events listener" on plug
	 * in page and then click on Add New Button
	 * 
	 * @throws Exception
	 */

	public ConfigurationPage ClickOnEventListner() throws Exception {
		GlobalController.brw.clickLinkText(this.PLUGIN_ROW_NUMBER_17);
		this.clickAddNewButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Select Type,Order,and Click on Save Plug-in on Add new
	 * Plug-in page
	 * 
	 * @param testdataset
	 * @param category
	 * @return
	 * @throws Exception
	 */
	public ConfigurationPage enterTestDataInOnPlugnin(String testdataset,
			String category) throws Exception {

		// select Dropdown
		String dropDownValueToBeSelected = "com.sapienter.jbilling.server.order.task.OrderChangeApplyOrderStatusTask";
		GlobalController.brw.selectDropDown(this.ADD_NEW_PLUGIN_ORDER_DROPDOWN,
				dropDownValueToBeSelected);

		// set order textbox with value
		String AddNewPluginOrderValue = TestData.read("PageConfiguration.xml",
				testdataset, "AddNewPluginOrderValue", category);

		this.setOrderTestData(AddNewPluginOrderValue);

		this.clickSavePlugin();

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will put value in order on Add new plugn page
	 * 
	 * @param secondRowTestData
	 * @throws Exception
	 */
	public void setOrderTestData(String OrderTestData) throws Exception {
		// fill ORDER
		GlobalController.brw.setText(this.ADD_NEW_PLUGIN_ORDER_TEXTBOX,
				OrderTestData);

	}

	/**
	 * Adds account type price for selected product
	 * 
	 * @param productCode
	 * @param testDataSetName
	 * @param category
	 * @throws Exception
	 */
	public ConfigurationPage addAccountTypePriceToSelectedProduct(
			String productCode, String testDataSetName, String category)
			throws Exception {
		String productCategory = TestData.read("PageConfiguration.xml",
				testDataSetName, "productCategory", category);
		String productRate = TestData.read("PageConfiguration.xml",
				testDataSetName, "rate", category);

		this.selectProductCategoryInAccountTypePrices(productCategory);
		this.selectProductInAccountTypePricesProductsTable(productCode,
				TextComparators.contains);
		this.clickAddPrice();
		this.setAccountTypePriceRate(productRate);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * 
	 * @param testDataSetName
	 * @param category
	 * @throws Exception
	 */
	public ConfigurationPage addAndRunMediationConfiguration(
			String testDataSetName, String category) throws Exception {
		this.clickAddNewButton();

		String mediationName = TestData.read("PageConfiguration.xml",
				testDataSetName, "mediationName", category);
		String orderValue = TestData.read("PageConfiguration.xml",
				testDataSetName, "orderValue", category);
		String jobLauncher = TestData.read("PageConfiguration.xml",
				testDataSetName, "jobLauncher", category);
		boolean global = TextUtilities.compareValue(TestData.read(
				"PageConfiguration.xml", testDataSetName, "global", category),
				"true", true, TextComparators.equals);

		this.setMediationConfigurationName(mediationName);
		this.setMediationConfigurationOrderValue(orderValue);
		this.selectMediationConfigurationJobLauncher(jobLauncher);
		// this.markMediationConfigurationAsGlobal(global);

		this.clickSaveChangesButton();
		this.clickUploadFile();
		this.browseForCsvFileToUpload();
		this.runMediation();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Add Billing Process in Configuration
	 * 
	 * @param pcbpd
	 * @throws Exception
	 */
	public ConfigurationPage addBillingProcess(String testDataSetName,
			String category) throws Exception {
		String nextRunDate = TestData.read("PageConfiguration.xml",
				testDataSetName, "nextRunDate", category);
		String billingPeriod = TestData.read("PageConfiguration.xml",
				testDataSetName, "billingPeriod", category);
		String maxPeriodInvoice = TestData.read("PageConfiguration.xml",
				testDataSetName, "maxPeriodInvoice", category);
		String generateRepo = TestData.read("PageConfiguration.xml",
				testDataSetName, "generateReport", category);
		String enablePror = TestData.read("PageConfiguration.xml",
				testDataSetName, "enableProrating", category);
		boolean enableProrating = TextUtilities.compareValue(enablePror,
				"true", true, TextComparators.equals);

		this.setBillingProcessNextRunDate(nextRunDate);

		this.selectBillingProcessPeriod(billingPeriod);

		this.clickSaveChangesToBillingProcess();

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This Method will have the details for CC Number Error Message
	 * 
	 * @param pcpmd
	 * @throws Exception
	 */
	protected ConfigurationPage addCCNumberErrorMsg(String cardErrorMsg)
			throws Exception {
		try {
			// click on remove button for error message if any message is
			// already there
			GlobalController.brw.click(this.IBT_REMOVE_CCNUMBER_ERR_MSG);
		} catch (Exception e) {
			// eat exception
		}

		// Choose english as language in error message language
		this.selectErrorMessageLanguage(this.DD_CCNUMBER_ERROR_LANGUAGE,
				"English");
		// Click on Add icon to add an error language
		GlobalController.brw.click(this.IBT_ADDCCNUMBERERRMSG);
		// set error message
		GlobalController.brw.setText(this.TB_CARDEXPIRYMESSAGE, cardErrorMsg);
		// update card details
		GlobalController.brw.click(this.BT_UPDATECARD);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Verify Created Product
	 * 
	 * @return
	 * @throws Exception
	 */
	ConfigurationPage verifySavedProduct() throws Exception {
		MessagesPage.isIntermediateSuccessMessageAppeared();
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	/**
	 * Adds collections ageing steps for given test data set name and category
	 * 
	 * @param ageingStep
	 * @param testDataSetName
	 * @param category
	 * @throws Exception
	 */
	public ConfigurationPage addCollectionsAgeingStep(
			CollectionAgeingStep ageingStep, String testDataSetName,
			String category) throws Exception {
		String step = TestData.read("PageConfiguration.xml", testDataSetName,
				"step", category);
		String days = TestData.read("PageConfiguration.xml", testDataSetName,
				"days", category);

		boolean notification = TextUtilities.compareValue(TestData.read(
				"PageConfiguration.xml", testDataSetName, "notification",
				category), "true", true, TextComparators.equals);
		boolean payment = TextUtilities.compareValue(TestData.read(
				"PageConfiguration.xml", testDataSetName, "payment", category),
				"true", true, TextComparators.equals);
		boolean suspend = TextUtilities.compareValue(TestData.read(
				"PageConfiguration.xml", testDataSetName, "suspend", category),
				"true", true, TextComparators.equals);

		switch (ageingStep) {
		case FIRST:
			this.setStepOne(step);
			this.setForDaysOne(days);
			this.checkNotificationsOne(notification);
			this.checkPaymentOne(payment);
			this.checkSuspendOne(suspend);
			break;
		case SECOND:
			this.setStepTwo(step);
			this.setForDaysTwo(days);
			this.checkNotificationsTwo(notification);
			this.checkPaymentTwo(payment);
			this.checkSuspendTwo(suspend);
			break;
		case THIRD:
			this.setStepThree(step);
			this.setForDaysThree(days);
			this.checkNotificationsThree(notification);
			this.checkPaymentThree(payment);
			this.checkSuspendThree(suspend);
			break;
		case FOURTH:
			this.setStepFour(step);
			this.setForDaysFour(days);
			this.checkNotificationsFour(notification);
			this.checkPaymentFour(payment);
			this.checkSuspendFour(suspend);
			break;
		default:
			throw new Exception(
					"Invalid Ageing Step provided to work on. Not defined in enumeration");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This Method will have the details for Expiry Date Error1 Message
	 * 
	 * @param pcpmd
	 * @throws Exception
	 */
	protected ConfigurationPage addExpiryDateErrorMsg(String dateErrorMsg)
			throws Exception {
		try {
			// click on remove button for error message if any message is
			// already there
			GlobalController.brw.click(this.IBT_REMOVE_EXPIRYDATE_ERR_MSG);
		} catch (Exception e) {
			// eat exception
		}
		// Choose english as language in error message language
		this.selectErrorMessageLanguage(this.DD_EXPIRYDATE_ERROR_LANGUAGE,
				"English");
		// Click on Add icon to add an error language
		GlobalController.brw.clickLinkText(this.IBT_ADD_EXPIRY_DATE_ERR_MSG);
		// set error message
		GlobalController.brw.setText(this.TB_EXPIRYDATEMESSAGE, dateErrorMsg);
		// update card details
		GlobalController.brw.click(this.BT_UPDATEDATE);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This Method will add Metafield
	 * 
	 * @param paitad
	 * @throws Exception
	 */
	protected ConfigurationPage addMetaField(AccountTypeInfo accountTypeInfo,
			String testDataSetName, String category) throws Exception {
		activateMetaFieldEditor();
		this.updateMetaFieldDetails(accountTypeInfo, testDataSetName, category);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Add New Information to selected account type
	 * 
	 * @throws Exception
	 */
	public String addNewInformationToSelectedAccountType(
			AccountTypeInfo accountTypeInfo, String testDataSetName,
			String category) throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADD_NEW_INFORMATION);

		String infoTypeName = TestData.read("PageConfiguration.xml",
				testDataSetName, "name", category);

		this.setAccountTypeInformationTypeName(infoTypeName);
		this.clickAddNewMetaField();
		this.addMetaField(accountTypeInfo, testDataSetName, category);
		this.clickSaveChangesToInformationType();
		return infoTypeName;
	}

	/**
	 * Adds new meta field to account type's information type
	 * 
	 * @param testDataSetName
	 * @param category
	 * @throws Exception
	 */
	protected ConfigurationPage addNewMetaFieldToInformationType(
			String testDataSetName, String category) throws Exception {
		String metaFieldName = TestData.read("PageConfiguration.xml",
				testDataSetName, "name", category);
		String metaFieldType = TestData.read("PageConfiguration.xml",
				testDataSetName, "metaFieldType", category);

		this.updateMetaFieldDetails(AccountTypeInfo.SIMPLE, metaFieldName,
				metaFieldType);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage addNewPluginInCategory(String testDataSetName,
			String category, HashMap<String, String> runTimeVariables)
			throws Exception {
		this.clickAddNewButton();

		String pluginType = TestData.read("PageConfiguration.xml",
				testDataSetName, "pluginType", category);
		String order = TestData.read("PageConfiguration.xml", testDataSetName,
				"order", category);
		String leftAttribute = TestData.read("PageConfiguration.xml",
				testDataSetName, "leftAttribute", category);

		this.selectPluginType(pluginType);
		this.setPluginOrder(order);
		// / this.setPluginParameterLeftAttribute(leftAttribute);
		// PageObjects.getPageConfigurationObject().setPluginParameterRightAttribute(this.graceId);
		// /
		// this.setPluginParameterRightAttribute(runTimeVariables.get("TC_2.5_GRACE_ID"));
		// / this.clickPluginAddMoreParametersIcon();
		this.clickSavePlugin();

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Add payment details
	 * 
	 * @param pcpmd
	 * @throws Exception
	 */
	public String addPaymentMethodDetails(String testDataSetName,
			String category) throws Exception {
		String methodName = TestData.read("PageConfiguration.xml",
				testDataSetName, "methodName", category);
		String cardErrorMsg = TestData.read("PageConfiguration.xml",
				testDataSetName, "cardErrorMsg", category);
		String dateErrorMsg = TestData.read("PageConfiguration.xml",
				testDataSetName, "dateErrorMsg", category);
		boolean isRecurring = TestData.read("PageConfiguration.xml",
				testDataSetName, "isRecurring", category).equals("true");
		boolean allAccountTypes = TestData.read("PageConfiguration.xml",
				testDataSetName, "allAccountTypes", category).equals("true");

		this.setMethodName(methodName);
		this.checkIsRecurring(isRecurring);
		this.checkAllAccountTypes(allAccountTypes);
		ConfigurationPage.logger
				.debug("Add CC Number Meta Field Error Message");
		this.createValRulOnCCNumberMF(cardErrorMsg);
		ConfigurationPage.logger
				.debug("Add Expiry Date Meta Field Error Message");
		this.createValRulOnExpiryDateMF(dateErrorMsg);
		this.clickSaveChangesButton();

		return methodName;
	}

	/**
	 * Click on Browse button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage browseForCsvFileToUpload() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_BROWSE_BUTTON);
		GlobalController.brw.openFileUsingOpenFileDialog(GlobalConsts
				.getProjectDir()
				+ "/resources/testdata/mediationFile/sampleMediation.csv");
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Check/Uncheck <b>Edit Plug-In</b> permission
	 * 
	 * @param editPlugin
	 * @throws Exception
	 */
	protected ConfigurationPage changeUserPermissionForEditPlugin(
			boolean editPlugin) throws Exception {
		if (editPlugin) {
			GlobalController.brw.check(this.CB_PERMISSION_CONFIGURATION_PLUGIN);
		} else {
			GlobalController.brw
					.uncheck(this.CB_PERMISSION_CONFIGURATION_PLUGIN);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Check All Account Type checkbox
	 * 
	 * @param allAccountTypes
	 * @throws Exception
	 */
	protected ConfigurationPage checkAllAccountTypes(boolean allAccountTypes)
			throws Exception {
		if (allAccountTypes) {
			GlobalController.brw.check(this.CHK_ALLACCOUNTTYPES);
		} else {
			GlobalController.brw.uncheck(this.CHK_ALLACCOUNTTYPES);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage checkBillingProcessGenerateReport(
			boolean generateReport) throws Exception {
		if (generateReport) {
			GlobalController.brw.check(this.CB_GENERATEREPORT);
		} else {
			GlobalController.brw.uncheck(this.CB_GENERATEREPORT);
		}

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Check/uncheck <b>Delete Payment</b> permission
	 * 
	 * @param jobLauncher
	 * @throws Exception
	 */
	protected ConfigurationPage checkDeletePayment(Boolean deletePayment)
			throws Exception {
		if (deletePayment) {
			GlobalController.brw
					.check(this.CB_PERMISSION_CONFIGURATION_DELETE_PAYMENT);
		} else {
			GlobalController.brw
					.uncheck(this.CB_PERMISSION_CONFIGURATION_DELETE_PAYMENT);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Check/uncheck <b>Edit Payment</b> permission
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage checkEditPayment(Boolean editPayment)
			throws Exception {
		if (editPayment) {
			GlobalController.brw
					.check(this.CB_PERMISSION_CONFIGURATION_EDITPAYMENT);
		} else {
			GlobalController.brw
					.uncheck(this.CB_PERMISSION_CONFIGURATION_EDITPAYMENT);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Check/uncheck Is Recurring check box
	 * 
	 * @param isRecurring
	 * @throws Exception
	 */
	protected ConfigurationPage checkIsRecurring(boolean isRecurring)
			throws Exception {
		if (isRecurring) {
			GlobalController.brw.check(this.CHK_ISRECURRING);
		} else {
			GlobalController.brw.uncheck(this.CHK_ISRECURRING);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck Notification checkbox
	 * 
	 * @param notification
	 * @throws Exception
	 */
	protected ConfigurationPage checkNotificationsFour(Boolean notification)
			throws Exception {
		if (notification) {
			GlobalController.brw.check(this.CB_NOTIFICATION4);
		} else {
			GlobalController.brw.uncheck(this.CB_NOTIFICATION4);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck Notification checkbox
	 * 
	 * @param notification
	 * @throws Exception
	 */
	protected ConfigurationPage checkNotificationsOne(Boolean notification)
			throws Exception {
		if (notification) {
			GlobalController.brw.check(this.CB_NOTIFICATION1);
		} else {
			GlobalController.brw.uncheck(this.CB_NOTIFICATION1);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click add new field
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickOrderStatuses() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ORDER_STATUSES);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck Notification checkbox
	 * 
	 * @param notification
	 * @throws Exception
	 */
	protected ConfigurationPage checkNotificationsThree(Boolean notification)
			throws Exception {
		if (notification) {
			GlobalController.brw.check(this.CB_NOTIFICATION3);
		} else {
			GlobalController.brw.uncheck(this.CB_NOTIFICATION3);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck Notification checkbox
	 * 
	 * @param notification
	 * @throws Exception
	 */
	protected ConfigurationPage checkNotificationsTwo(Boolean notification)
			throws Exception {
		if (notification) {
			GlobalController.brw.check(this.CB_NOTIFICATION2);
		} else {
			GlobalController.brw.uncheck(this.CB_NOTIFICATION2);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck payment checkbox
	 * 
	 * @param payment
	 * @throws Exception
	 */
	protected ConfigurationPage checkPaymentFour(boolean payment)
			throws Exception {
		if (payment) {
			GlobalController.brw.check(this.CB_PAYMENT4);
		} else {
			GlobalController.brw.uncheck(this.CB_PAYMENT4);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck payment checkbox
	 * 
	 * @param payment
	 * @throws Exception
	 */
	protected ConfigurationPage checkPaymentOne(boolean payment)
			throws Exception {
		if (payment) {
			GlobalController.brw.check(this.CB_PAYMENT1);
		} else {
			GlobalController.brw.uncheck(this.CB_PAYMENT1);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck payment checkbox
	 * 
	 * @param payment
	 * @throws Exception
	 */
	protected ConfigurationPage checkPaymentThree(boolean payment)
			throws Exception {
		if (payment) {
			GlobalController.brw.check(this.CB_PAYMENT3);
		} else {
			GlobalController.brw.uncheck(this.CB_PAYMENT3);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck payment checkbox
	 * 
	 * @param payment
	 * @throws Exception
	 */
	protected ConfigurationPage checkPaymentTwo(boolean payment)
			throws Exception {
		if (payment) {
			GlobalController.brw.check(this.CB_PAYMENT2);
		} else {
			GlobalController.brw.uncheck(this.CB_PAYMENT2);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck suspend checkbox
	 * 
	 * @param suspend
	 * @throws Exception
	 */
	protected ConfigurationPage checkSuspendFour(boolean suspend)
			throws Exception {
		if (suspend) {
			GlobalController.brw.check(this.CB_SUSPEND4);
		} else {
			GlobalController.brw.uncheck(this.CB_SUSPEND4);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck suspend checkbox
	 * 
	 * @param suspend
	 * @throws Exception
	 */
	protected ConfigurationPage checkSuspendOne(boolean suspend)
			throws Exception {
		if (suspend) {
			GlobalController.brw.check(this.CB_SUSPEND1);
		} else {
			GlobalController.brw.uncheck(this.CB_SUSPEND1);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck suspend checkbox
	 * 
	 * @param suspend
	 * @throws Exception
	 */
	protected ConfigurationPage checkSuspendThree(boolean suspend)
			throws Exception {
		if (suspend) {
			GlobalController.brw.check(this.CB_SUSPEND3);
		} else {
			GlobalController.brw.uncheck(this.CB_SUSPEND3);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check or uncheck suspend checkbox
	 * 
	 * @param suspend
	 * @throws Exception
	 */
	protected ConfigurationPage checkSuspendTwo(boolean suspend)
			throws Exception {
		if (suspend) {
			GlobalController.brw.check(this.CB_SUSPEND2);
		} else {
			GlobalController.brw.uncheck(this.CB_SUSPEND2);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click on Prices button
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage clickAccountTypePrices() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ACCOUNT_TYPE_PRICES);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click on Add new button
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage clickAddNewButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADD_NEW_BUTTON);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click add new field
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickAddNewMetaField() throws Exception {
		GlobalController.brw.waitForAjaxElement(this.LT_ADD_NEW_METAFIELD);
		GlobalController.brw.clickLinkText(this.LT_ADD_NEW_METAFIELD);
		GlobalController.brw.waitForAjaxElement(this.METAFIELD_BAR);
		activateMetaFieldEditor();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click Add Price button to add price in selected Account Type -> Product
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickAddPrice() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADD_PRICE);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click YES button to confirm popup
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage clickConfirmPopupYesButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_CONFIRM_YES);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click on Copy Company Button
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage clickCopyCompanyButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_COPY_COMPANY);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Clicks edit account type button for selected account type
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage clickEditAccountTypeButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_EDIT_BUTTON);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click on Image Edit button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickEditImageAccountTypePrice()
			throws Exception {
		GlobalController.brw.click(this.IBT_EDIT);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click on Add button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickOnAddAgeingStepButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADD_AGEING_STEP);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click on Add More Field button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickPluginAddMoreParametersIcon()
			throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADDMOREFIELD);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click on Add button More Name Fields
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickPluginAddMoreNameParametersIcon()
			throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADDMOREFIELD_NAME);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click on Run Collections button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickPopupRunCollections() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_POPUP_RUN_COLLECTIONS);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click Recently created Account Type
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage clickRecentlyCreatedAccountType() throws Exception {
		GlobalController.brw.click(this.TXT_RECENT_ACCOUNT_TYPE);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage clickRunBillingProcess() throws Exception {
		GlobalController.brw.click(this.BT_RUN_BILLING_PROCESS);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click on Run Collections button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickRunCollections() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_RUN_COLLECTIONS);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage clickRunCommmisionToBillingProcess()
			throws Exception {
		GlobalController.brw.click(this.BT_RUN_COMMISSION_TO_BILLINGPROCESS);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click Save Changes button
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage clickSaveChangesButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_SAVE_CHANGES);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage clickSaveChangesToBillingProcess()
			throws Exception {
		GlobalController.brw.click(this.LT_SAVE_CHANGES);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click Save Changes button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickSaveChangesTemplatebutton()
			throws Exception {
		GlobalController.brw.clickLinkText(this.IT_SAVE_CHANGES);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click Save Changes Button
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage clickSaveChangesToCollections() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_SAVE_CHANGES);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	// ////////////////////////////////////////////////////////////

	/**
	 * Save information type for selected account type
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickSaveChangesToInformationType()
			throws Exception {
		GlobalController.brw
				.clickLinkText(this.LT_SAVE_CHANGES_TO_INFORMATION_TYPE);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Saves changes to created/edited Order period
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickSaveChangesToOrderPeriod()
			throws Exception {
		GlobalController.brw.click(this.LT_SAVE_CHANGES);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Save changes to users permissions
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickSaveChangesToUsersPermissions()
			throws Exception {
		GlobalController.brw.click(this.LT_SAVE_CHANGES);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click on Save button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickSavePlugin() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_SAVEPLUGINS);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click on select Button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickSelectButton() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_SELECT);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click on specified user
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage clickSpecifiedUserInUsersTable(String userName)
			throws Exception {
		GlobalController.brw.selectListItem(this.TAB_USERS_ANCHORS_LIST,
				userName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	// ///////////////////////////////////////////////////////////////////////

	/**
	 * Update meta field information
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickUpdateButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_UPDATE_METAFIELD);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click on Upload file button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickUploadFile() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_UPLOAD_FILE);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click on Permissions button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickUsersPermissionsButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_USERS_PERMISSIONS);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Creates account type for given test data set and category
	 * 
	 * @param testDataSetName
	 * @param category
	 * @return Account Type name created
	 * @throws Exception
	 */
	public String createAccountType(String testDataSetName, String category)
			throws Exception {
		String accountName = TestData.read("PageConfiguration.xml",
				testDataSetName, "accountName", category);
		String billingCycle = TestData.read("PageConfiguration.xml",
				testDataSetName, "billingCycle", category);
		String invoiceDesign = TestData.read("PageConfiguration.xml",
				testDataSetName, "invoiceDesign", category);

		this.clickAddNewButton();
		this.setAccountTypeName(accountName);
		this.setAccountTypeBillingCycle(billingCycle);
		this.setAccountTypeInvoiceDesign(invoiceDesign);
		this.clickSaveChangesButton();

		return accountName;
	}

	/**
	 * Edit account type for last created account type
	 * 
	 * @param testDataSetName
	 * @param category
	 * @return Account Type Field edited
	 * @throws Exception
	 */
	public String editAccountType(AccountTypeField accntTypeField,
			String testDataSetName, String category) throws Exception {
		String accountName = TestData.read("PageConfiguration.xml",
				testDataSetName, "accountName", category);
		String billingCycle = TestData.read("PageConfiguration.xml",
				testDataSetName, "billingCycle", category);
		String invoiceDesign = TestData.read("PageConfiguration.xml",
				testDataSetName, "invoiceDesign", category);

		this.clickRecentlyCreatedAccountType();
		this.clickEditAccountTypeButton();
		switch (accntTypeField) {
		case ACCOUNT_NAME:
			this.setAccountTypeName(accountName);
			break;
		case BILLING_CYCLE:
			this.setAccountTypeBillingCycle(billingCycle);
			break;
		case INVOICE:
			this.setAccountTypeInvoiceDesign(invoiceDesign);
			break;
		case ALL:
			this.setAccountTypeName(accountName);
			this.setAccountTypeBillingCycle(billingCycle);
			this.setAccountTypeInvoiceDesign(invoiceDesign);
			break;

		default:
			throw new Exception(
					"Invalid Step Provided. Not defined in enumeration");

		}
		this.clickSaveChangesButton();
		return accountName;

	}

	/**
	 * Creates new order period for provided test data set and its category
	 * 
	 * @param testDataSetName
	 * @param category
	 * @throws Exception
	 */
	public String createNewOrderPeriod(String testDataSetName, String category)
			throws Exception {
		String description = TestData.read("PageConfiguration.xml",
				testDataSetName, "description", category);
		String unit = TestData.read("PageConfiguration.xml", testDataSetName,
				"unit", category);
		String value = TestData.read("PageConfiguration.xml", testDataSetName,
				"value", category);

		this.createNewOrderPeriod(description, unit, value);
		return description;
	}

	/**
	 * Creates new order period for provided input values
	 * 
	 * @param description
	 * @param unit
	 * @param value
	 * @throws Exception
	 */
	public ConfigurationPage createNewOrderPeriod(String description,
			String unit, String value) throws Exception {
		this.clickAddNewButton();
		this.setOrderPeriodDescription(description);
		this.selectOrderPeriodUnit(unit);
		this.setOrderPeriodValue(value);
		this.clickSaveChangesToOrderPeriod();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will add CC Number Meta Fields Error Message
	 * 
	 * @param pcpmd
	 * @throws Exception
	 */
	protected ConfigurationPage createValRulOnCCNumberMF(String cardErrorMsg)
			throws Exception {
		String attrVal = GlobalController.brw.getAttribute(
				this.TXT_CCNUMBERTAB_ACTIVATOR_LINK, "class");
		ConfigurationPage.logger
				.info("class attribute for cc number li element: " + attrVal);
		if (TextUtilities.compareValue("active", attrVal, true,
				TextComparators.notContains)) {
			// click on cc.number tab to show all fields
			GlobalController.brw.click(this.TXT_CCNUMBERTAB);
		}
		this.addCCNumberErrorMsg(cardErrorMsg);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will add Expiry Date Meta Fields Error Message
	 * 
	 * @param pcpmd
	 * @throws Exception
	 */
	protected ConfigurationPage createValRulOnExpiryDateMF(String dateErrorMsg)
			throws Exception {
		String attrVal = GlobalController.brw.getAttribute(
				this.TXT_EXPIRYDATETAB_ACTIVATOR_LINK, "class");
		ConfigurationPage.logger
				.info("class attribute for cc expiry li element: " + attrVal);
		if (TextUtilities.compareValue("active", attrVal, true,
				TextComparators.notContains)) {
			// click on cc.expiry tab to show all fields
			GlobalController.brw.click(this.TXT_EXPIRYDATETAB);
		}
		this.addExpiryDateErrorMsg(dateErrorMsg);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public String extractCompanyIdFromCompanyCreationMessage() throws Exception {
		String companyIdStartString = ", ID: ";

		String credentialsMessage = this.getNewCompanyCredentials();

		int companyIdStartIndex = TextUtilities.indexOf(credentialsMessage,
				companyIdStartString) + companyIdStartString.length();
		String companyId = TextUtilities.trim(TextUtilities.substring(
				credentialsMessage, companyIdStartIndex).replace(".", ""));
		ConfigurationPage.logger.info("\"" + companyId + "\"");

		return companyId;
	}

	public String extractCompanyNameFromCompanyCreationMessage()
			throws Exception {
		String companyNameStartString = ", Company Name:";
		String companyIdStartString = ", ID: ";

		String credentialsMessage = this.getNewCompanyCredentials();

		int companyNameStartIndex = TextUtilities.indexOf(credentialsMessage,
				companyNameStartString) + companyNameStartString.length();
		int companyNameEndIndex = TextUtilities.indexOf(credentialsMessage,
				companyIdStartString);
		String companyName = TextUtilities
				.trim(TextUtilities.substring(credentialsMessage,
						companyNameStartIndex, companyNameEndIndex));
		ConfigurationPage.logger.info("\"" + companyName + "\"");

		return companyName;
	}

	public String extractPasswordFromCompanyCreationMessage() throws Exception {
		String passwordStartString = ", password:";
		String companyNameStartString = ", Company Name:";

		String credentialsMessage = this.getNewCompanyCredentials();

		int passwordStartIndex = TextUtilities.indexOf(credentialsMessage,
				passwordStartString) + passwordStartString.length();
		int passwordEndIndex = TextUtilities.indexOf(credentialsMessage,
				companyNameStartString);
		String password = TextUtilities.trim(TextUtilities.substring(
				credentialsMessage, passwordStartIndex, passwordEndIndex));
		ConfigurationPage.logger.info("\"" + password + "\"");

		return password;
	}

	public String extractUserNameFromCompanyCreationMessage() throws Exception {
		String userNameStartString = "User name:";
		String passwordStartString = ", password:";

		String credentialsMessage = this.getNewCompanyCredentials();

		int userNameStartIndex = TextUtilities.indexOf(credentialsMessage,
				userNameStartString) + userNameStartString.length();
		int userNameEndIndex = TextUtilities.indexOf(credentialsMessage,
				passwordStartString);
		String userName = TextUtilities.trim(TextUtilities.substring(
				credentialsMessage, userNameStartIndex, userNameEndIndex));
		ConfigurationPage.logger.info("\"" + userName + "\"");

		return userName;
	}

	/**
	 * Return successful message string on creation of new company having all
	 * credentials information
	 * 
	 * @throws Exception
	 */
	protected String getNewCompanyCredentials() throws Exception {
		String credentials = GlobalController.brw
				.getText(this.TXT_COMPANY_CREDENTIALS);

		return credentials;
	}

	/**
	 * Get Step Two ID
	 * 
	 * @param pccd
	 * @return
	 * @throws Exception
	 */
	public String getStep2ID() throws Exception {
		String text = GlobalController.brw.getText(this.TB_STEP2ID);
		return text;
	}

	/**
	 * Verifies that if account information type is created successfully or not
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String isAccountInformationTypeCreatedSuccessfully()
			throws Exception {
		String msgToVerify = "Account Information Type created successfully";
		return MessagesPage.isOperationSuccessfulOnMessage(msgToVerify,
				TextComparators.contains);
	}

	/**
	 * Wait till notification page loaded
	 * 
	 * @return
	 * @throws Exception
	 */
	protected boolean isItgInvoiceNotificationPageLoaded() throws Exception {
		return GlobalController.brw.isElementPresent(this.LT_TGINotification);
	}

	/**
	 * Wait till JQGrid for tables page loaded
	 * 
	 * @return
	 * @throws Exception
	 */
	protected boolean isUseJQGridPageLoaded() throws Exception {
		return GlobalController.brw
				.isElementPresent(this.LT_USEJQGRIDFORTABLES);
	}

	/**
	 * Wait till JQGrid for tables page loaded
	 * 
	 * @return
	 * @throws Exception
	 */
	protected boolean isJQGridPageLoaded() throws Exception {
		return GlobalController.brw.isElementPresent(this.LT_JQGRIDFORTABLES);
	}

	/**
	 * Check Child Company check box
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage markCompanyAsChildCompany(boolean isChildCompany)
			throws Exception {
		if (isChildCompany) {
			GlobalController.brw.check(this.CB_CHILD_COMPANY);
		} else {
			GlobalController.brw.uncheck(this.CB_CHILD_COMPANY);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * 
	 * @param childCompanyName
	 * @return
	 * @throws Exception
	 */
	public ConfigurationPage setTemplateCompanyName() throws Exception {
		String templateCompanyName = this.getCompanyDescription();
		GlobalController.brw.setText(this.TB_TEMPLATE_COMPANY_NAME,
				templateCompanyName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Check/uncheck Global checkbox for Mediation Configuration
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage markMediationConfigurationAsGlobal(
			boolean isGlobal) throws Exception {
		if (isGlobal) {
			GlobalController.brw.check(this.CB_GLOBAL);
		} else {
			GlobalController.brw.uncheck(this.CB_GLOBAL);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Run collections for specified date
	 * 
	 * @param collectionDate
	 * @throws Exception
	 */
	public ConfigurationPage runCollectionsForDate(String collectionDate)
			throws Exception {
		this.setRunCollectionDate(collectionDate);
		this.clickRunCollections();
		this.clickPopupRunCollections();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click on Run Mediation to run selected mediation
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage runMediation() throws Exception {
		GlobalController.brw.click(this.LT_RUN_MEDIATION);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will select accountType
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage selectAccountTypeName(String accountTypeName)
			throws Exception {
		GlobalController.brw.selectListItem(this.TAB_ACCOUNT_TYPES_LIST_NAMES,
				accountTypeName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will select selectProductCategory dropdown option
	 * 
	 * @param description
	 * @throws Exception
	 */
	public PlansPage selectProductCategory(String category) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PRODUCT_CATEGORY, category);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select selectProduct dropdown option
	 * 
	 * @param description
	 * @throws Exception
	 */
	public PlansPage selectProduct(String product) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PRODUCT, product);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will click directCustomer
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage selectAccountTypeName(String accountTypeName,
			TextComparators comparator) throws Exception {
		GlobalController.brw.selectListItem(this.TAB_ACCOUNT_TYPES_LIST_NAMES,
				accountTypeName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage selectBillingProcessPeriod(String billingPeriod)
			throws Exception {
		GlobalController.brw.selectDropDown(this.DD_BILLINGPERIOD_UNIT,
				billingPeriod);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Clicks on provided configuration item in Configurations page
	 * 
	 * @param configName
	 * @return
	 * @throws Exception
	 */
	public ConfigurationPage selectConfiguration(
			GlobalEnumsPage.PageConfigurationItems configName) throws Exception {
		GlobalController.brw.selectListItem(this.LB_CONFIGURATION_ITEMS,
				configName.GetValue());

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	// ////////////////////////////////////////////////////////////////////

	// /**
	// * Click on ITG Notification
	// *
	// * @throws Exception
	// */
	// public PageConfiguration clickItgInvoiceNotification() throws Exception {
	// GlobalController.brw.clickLinkText(this.LT_TGINotification);
	// }
	//
	/**
	 * Selects configuration preference from Configuration -> All page having
	 * all configurational preferences
	 * 
	 * @param configItemName
	 * @throws Exception
	 */
	protected ConfigurationPage selectConfigurationPreference(
			String configPreferenceName) throws Exception {
		GlobalController.brw.selectListItem(
				this.LV_CONFIGURATIONAL_PREFERENCES, configPreferenceName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Selects configuration preference from Configuration -> All page having
	 * all configurational preferences
	 * 
	 * @param configItemName
	 * @throws Exception
	 */
	protected ConfigurationPage selectConfigurationPreferenceJQGrid(
			String configPreferenceName) throws Exception {
		GlobalController.brw.selectListItem(
				this.LV_JQCONFIGURATIONAL_PREFERENCES, configPreferenceName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage selectErrorMessageLanguage(
			ElementField dropdownFieldName, String lang) throws Exception {
		GlobalController.brw.selectDropDown(dropdownFieldName, lang);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Set Mediation Configuration Job Launcher
	 * 
	 * @param jobLauncher
	 * @throws Exception
	 */
	protected ConfigurationPage selectMediationConfigurationJobLauncher(
			String jobLauncher) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_JOBLAUNCHER, jobLauncher);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Select meta field type
	 * 
	 * @param metaFieldType
	 * @throws Exception
	 */
	protected ConfigurationPage selectMetafieldType(String metaFieldType)
			throws Exception {
		GlobalController.brw.selectDropDown(this.DD_METAFIELDTYPE,
				metaFieldType);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage selectNeverProratingRadio(
			boolean enableProrating) throws Exception {
		// TODO: Ask Pankaj that Enable Prorating Radio Button should be
		// selected
		// locator for this radio button RB_NEVRPRORATING and Datafield is
		// enableProrating

		if (enableProrating) {
			GlobalController.brw
					.check(this.RB_NEVRPRORATING_TO_BILLING_PROCESS);
		} else {
			GlobalController.brw
					.uncheck(this.RB_NEVRPRORATING_TO_BILLING_PROCESS);
		}

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Select Order Period Unit
	 * 
	 * @param unit
	 * @throws Exception
	 */
	protected ConfigurationPage selectOrderPeriodUnit(String unit)
			throws Exception {
		GlobalController.brw.selectDropDown(this.DD_ORDER_PERIOD_UNIT, unit);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will select Payment Template
	 * 
	 * @param paymentCard
	 * @throws Exception
	 */
	protected ConfigurationPage selectPaymentTemplate(String paymentCard)
			throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PAYMENTTEMPLATE,
				paymentCard);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will select Generic Plugin
	 * 
	 * @throws Exception
	 */

	public ConfigurationPage selectPluginCategory(String testDataSetName,
			String category) throws Exception {
		String pluginCategory = TestData.read("PageConfiguration.xml",
				testDataSetName, "pluginCategory", category);

		GlobalController.brw.selectListItem(this.LV_PLUGIN_CATEGORIES,
				pluginCategory);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will select plugin type dropdown
	 * 
	 * @param pluginType
	 * @throws Exception
	 */
	protected ConfigurationPage selectPluginType(String pluginType)
			throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PLUGINTYPE, pluginType);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will select Product Category dropdown option
	 * 
	 * @param productCategory
	 * @throws Exception
	 */
	protected ConfigurationPage selectProductCategoryInAccountTypePrices(
			String productCategory) throws Exception {
		GlobalController.brw.selectDropDown(
				this.DD_ACCOUNT_TYPE_PRODUCT_CATEGORY, productCategory);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Select/Click Product Code in (Account Type -> Prices -> Products Table)
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage selectProductInAccountTypePricesProductsTable(
			String productCodeName, TextComparators comparator)
			throws Exception {
		GlobalController.brw.selectListItem(
				this.TAB_ACCOUNT_LIST_TYPE_PRICES_PRODUCTS, productCodeName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Billing Cycle
	 * 
	 * @param billingCycle
	 * @throws Exception
	 */
	protected ConfigurationPage setAccountTypeBillingCycle(String billingCycle)
			throws Exception {
		GlobalController.brw.setText(this.TB_ACCOUNT_TYPE_BILLING_CYCLE,
				billingCycle);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Name
	 * 
	 * @param name
	 * @throws Exception
	 */
	protected ConfigurationPage setAccountTypeInformationTypeName(String name)
			throws Exception {
		GlobalController.brw.setText(this.TB_INFORMATION_TYPE_NAME, name);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Invoice Design
	 * 
	 * @param invoiceDesign
	 * @throws Exception
	 */
	protected ConfigurationPage setAccountTypeInvoiceDesign(String invoiceDesign)
			throws Exception {
		GlobalController.brw.setText(this.TB_ACCOUNT_TYPE_INVOICE_DESIGN,
				invoiceDesign);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Invoice Name
	 * 
	 * @param invoiceName
	 * @throws Exception
	 */
	protected ConfigurationPage setInvoiceName(String invoiceName)
			throws Exception {
		GlobalController.brw.setText(this.TB_ACCOUNT_TYPE_INVOICE_NAME,
				invoiceName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	// /////////////////////////////////////////////
	/**
	 * This method will will set Account Name
	 * 
	 * @param accountName
	 * @throws Exception
	 */
	protected ConfigurationPage setAccountTypeName(String accountName)
			throws Exception {
		GlobalController.brw.setText(this.TB_ACCOUNT_TYPE_NAME, accountName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Rate
	 * 
	 * @param rate
	 * @throws Exception
	 */
	protected ConfigurationPage setAccountTypePriceRate(String rate)
			throws Exception {
		GlobalController.brw.setText(this.TB_RATE, rate);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setBillingProcessNextRunDate(String nextRunDate)
			throws Exception {
		GlobalController.brw.setText(this.TB_NEXTRUNDATE, nextRunDate);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Sets preference value to selected configuration
	 * 
	 * @param preferenceValue
	 * @throws Exception
	 */
	protected ConfigurationPage setConfigurationalPreferenceValue(
			String preferenceValue) throws Exception {

		GlobalController.brw
				.clearTextBox(this.TB_CONFIGURATION_PREFERENCE_VALUE);
		GlobalController.brw.setText(this.TB_CONFIGURATION_PREFERENCE_VALUE,
				preferenceValue);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set For Days field
	 * 
	 * @param days
	 * @throws Exception
	 */
	protected ConfigurationPage setForDaysFour(String days) throws Exception {
		GlobalController.brw.setText(this.TB_FORDAYS4, days);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set For Days field
	 * 
	 * @param days
	 * @throws Exception
	 */
	protected ConfigurationPage setForDaysOne(String days) throws Exception {
		GlobalController.brw.setText(this.TB_FORDAYS1, days);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set For Days field
	 * 
	 * @param days
	 * @throws Exception
	 */
	protected ConfigurationPage setForDaysThree(String days) throws Exception {
		GlobalController.brw.setText(this.TB_FORDAYS3, days);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set For Days field
	 * 
	 * @param days
	 * @throws Exception
	 */
	protected ConfigurationPage setForDaysTwo(String days) throws Exception {
		GlobalController.brw.setText(this.TB_FORDAYS2, days);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setMaxInvoicePeriod(String maxPeriodInvoice)
			throws Exception {
		GlobalController.brw.setText(this.TB_MAXPERIODTOINVOICE,
				maxPeriodInvoice);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	// /////////////////////////////
	/**
	 * Set Mediation Configuration name
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage setMediationConfigurationName(
			String mediationName) throws Exception {
		GlobalController.brw.setText(this.TB_MEDIATIONNAME, mediationName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Set Mediation Configuration Order Value
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage setMediationConfigurationOrderValue(
			String orderValue) throws Exception {
		GlobalController.brw.setText(this.TB_ORDERVALUE, orderValue);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will fill details for meta field
	 * 
	 * @param paitad
	 * @throws Exception
	 */
	protected ConfigurationPage setMetaFieldName(String name) throws Exception {
		GlobalController.brw.setText(this.TB_METAFIELDNAME, name);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set method name
	 * 
	 * @param methodName
	 * @throws Exception
	 */
	protected ConfigurationPage setMethodName(String methodName)
			throws Exception {
		GlobalController.brw.setText(this.TB_METHODNAME, methodName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Set order period description
	 * 
	 * @param description
	 * @throws Exception
	 */
	protected ConfigurationPage setOrderPeriodDescription(String description)
			throws Exception {
		GlobalController.brw.setText(this.TB_ORDER_PERIOD_DESCRIPTION,
				description);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Set Order Period Value
	 * 
	 * @param value
	 * @throws Exception
	 */
	protected ConfigurationPage setOrderPeriodValue(String value)
			throws Exception {
		GlobalController.brw.setText(this.TB_ORDER_PERIOD_VALUE, value);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Order
	 * 
	 * @param order
	 * @throws Exception
	 */
	protected ConfigurationPage setPluginOrder(String order) throws Exception {
		GlobalController.brw.setText(this.TB_ORDER, order);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Left Attribute
	 * 
	 * @param LeftAttribute
	 * @throws Exception
	 */
	protected ConfigurationPage setPluginParameterLeftAttribute(
			String LeftAttribute) throws Exception {
		GlobalController.brw.setText(this.TB_PARAMETER_1_NAME, LeftAttribute);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Right Attribute
	 * 
	 * @param Rightattribute
	 * @throws Exception
	 */
	protected ConfigurationPage setPluginParameterRightAttribute(
			String Rightattribute) throws Exception {
		GlobalController.brw.setText(this.TB_PARAMETER_1_VALUE, Rightattribute);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	// /////////////////////////////////////////////////////

	/**
	 * This method will set Run Collection Date
	 * 
	 * @param collectionDate
	 * @throws Exception
	 */
	protected ConfigurationPage setRunCollectionDate(String collectionDate)
			throws Exception {
		GlobalController.brw.setText(this.TB_COLLECTION_DATE, collectionDate);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Step data
	 * 
	 * @param StepOne
	 * @throws Exception
	 */
	protected ConfigurationPage setStepFour(String step) throws Exception {
		GlobalController.brw.setText(this.TB_STEP4, step);
		GlobalController.brw.pressTab(this.TB_STEP4);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Step data
	 * 
	 * @param StepOne
	 * @throws Exception
	 */
	protected ConfigurationPage setStepOne(String step) throws Exception {
		GlobalController.brw.setText(this.TB_STEP1, step);
		GlobalController.brw.pressTab(this.TB_STEP1);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Step data
	 * 
	 * @param StepOne
	 * @throws Exception
	 */
	protected ConfigurationPage setStepThree(String step) throws Exception {
		GlobalController.brw.setText(this.TB_STEP3, step);
		GlobalController.brw.pressTab(this.TB_STEP3);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Step data
	 * 
	 * @param StepOne
	 * @throws Exception
	 */
	protected ConfigurationPage setStepTwo(String step) throws Exception {
		GlobalController.brw.setText(this.TB_STEP2, step);
		GlobalController.brw.pressTab(this.TB_STEP2);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Update account type price for provided product. This is valid if there is
	 * already added price for provided product.
	 * 
	 * @param testDataSetName
	 * @param category
	 * @throws Exception
	 */
	public ConfigurationPage updateAccountTypePriceForProduct(
			String testDataSetName, String category) throws Exception {
		String productRate = TestData.read("PageConfiguration.xml",
				testDataSetName, "rate", category);

		this.clickEditImageAccountTypePrice();
		this.setAccountTypePriceRate(productRate);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Add/Update meta field information
	 * 
	 * @param name
	 * @param metaFieldType
	 * @throws Exception
	 */
	protected ConfigurationPage updateMetaFieldDetails(
			AccountTypeInfo accountTypeInfo, String testDataSetName,
			String category) throws Exception {

		String metaFieldName = TestData.read("PageConfiguration.xml",
				testDataSetName, "metaFieldName", category);
		String metaFieldType = TestData.read("PageConfiguration.xml",
				testDataSetName, "metaFieldType", category);
		String metaFieldDayaType = TestData.read("PageConfiguration.xml",
				testDataSetName, "metaFieldDayaType", category);

		switch (accountTypeInfo) {
		case SIMPLE:
			this.setMetaFieldName(metaFieldName);
			this.selectMetafieldType(metaFieldType);
			this.clickUpdateButton();

			break;
		case DISABLE_CHECKBOX:
			this.setMetaFieldName(metaFieldName);
			this.selectInfoMetaDataType(metaFieldDayaType);
			this.checkboxDisableInMetaFields(true);
			this.clickUpdateButton();
			break;
		default:
			throw new Exception(
					"Invalid Metafield Step provided to work on. Not defined in enumeration");
		}

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Verifies configurational preference value is matching with provided value
	 * or not
	 * 
	 * @throws Exception
	 */

	protected ConfigurationPage verifyConfigurationalPreferenceValue(
			String expectedValue) throws Exception {
		GlobalController.brw
				.isElementPresent(this.TB_CONFIGURATION_PREFERENCE_VALUE);
		String actualValue = GlobalController.brw.getAttribute(
				(this.TB_CONFIGURATION_PREFERENCE_VALUE), "value");

		if (expectedValue.equals(actualValue) == false) {
			throw new Exception("Preference value " + actualValue
					+ " is not matching with provided value " + expectedValue);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage verifySavedCommision() throws Exception {
		MessagesPage.isIntermediateSuccessMessageAppeared();

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will verify triggered mediation
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage verifyTriggeredMediation() throws Exception {
		MessagesPage.isIntermediateSuccessMessageAppeared();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method verifies updated preference value
	 * 
	 * @throws Exception
	 */

	protected ConfigurationPage verifyUpdatedPreference() throws Exception {
		MessagesPage.isIntermediateSuccessMessageAppeared();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage setConfigurationPreference(String testDataSetName,
			String category) throws Exception {
		String notification = TestData.read("PageConfiguration.xml",
				testDataSetName, "notification", category);
		String preferenceValue = TestData.read("PageConfiguration.xml",
				testDataSetName, "preferenceValue", category);

		this.isItgInvoiceNotificationPageLoaded();
		this.isUseJQGridPageLoaded();
		this.selectConfigurationPreference(notification);
		this.setConfigurationalPreferenceValue(preferenceValue);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage configurePaymentMethod(String testDataSetName,
			String category) throws Exception {
		String paymentTemplate = TestData.read("PageConfiguration.xml",
				testDataSetName, "paymentTemplate", category);

		this.clickAddNewButton();
		this.selectPaymentTemplate(paymentTemplate);
		this.clickSelectButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage configurePluginPermissions(String userName)
			throws Exception {
		this.clickSpecifiedUserInUsersTable(userName);
		this.clickUsersPermissionsButton();
		this.changeUserPermissionForEditPlugin(true);
		this.clickSaveChangesToUsersPermissions();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage updatePreference(String testDataSetName,
			String category) throws Exception {
		String preference = TestData.read("PageConfiguration.xml",
				testDataSetName, "preference", category);
		String preferenceVal = TestData.read("PageConfiguration.xml",
				testDataSetName, "preferenceVal", category);

		this.selectConfigurationPreference(preference);
		this.setConfigurationalPreferenceValue(preferenceVal);
		this.verifyConfigurationalPreferenceValue(preferenceVal);
		this.clickSaveChangesButton();
		this.verifyUpdatedPreference();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage editUserPermissions() throws Exception {
		this.clickUsersPermissionsButton();
		this.checkEditPayment(true);
		this.checkDeletePayment(true);
		this.clickSaveChangesToUsersPermissions();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will validate periods table saved test data
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage validatePeriodsSavedTestData(String data)
			throws Exception {
		GlobalController.brw.validateSavedTestData(
				this.TAB_ACCOUNT_TYPES_NAMES, data);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will validate periods table saved test data
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage validateInvoiceSavedTestData(String data)
			throws Exception {
		GlobalController.brw.validateSavedTestData(
				this.TB_Invoice_Template_NAMES, data);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will validate categories table saved test data
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage validateCategoriesSavedTestData(String data)
			throws Exception {
		GlobalController.brw.validateSavedTestData(this.TBL_CATEGORIES_TABLE,
				data);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will verify breadcrumbs ui component
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage verifyUIComponent() throws Exception {
		GlobalController.brw.verifyUIComponent(this.BREADCRUMBS);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Get Company Description
	 * 
	 * @throws Exception
	 */
	public String getCompanyDescription() throws Exception {
		String companyName = GlobalController.brw.getAttribute(
				this.TB_COMPANY_DESCRIPTION, "Value");
		return companyName;
	}

	/**
	 * This method will set company descriptions in company details
	 * 
	 * @param companyDescription
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage setCompanyDescription(String companyDescription)
			throws Exception {
		GlobalController.brw.setText(this.TB_COMPANY_DESCRIPTION,
				companyDescription);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set company Address in company details
	 * 
	 * @param companyAddress
	 * @return
	 * @throws Exception
	 */

	protected ConfigurationPage setCompanyAddress(String companyAddress)
			throws Exception {
		GlobalController.brw.setText(this.TB_COMPANY_ADDRESS, companyAddress);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set company City in company details
	 * 
	 * @param companyCity
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage setCompanyCity(String companyCity)
			throws Exception {
		GlobalController.brw.setText(this.TB_COMPANY_CITY, companyCity);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set company State/Province in company details
	 * 
	 * @param companyState
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage setCompanyState(String companyState)
			throws Exception {
		GlobalController.brw.setText(this.TB_COMPANY_STATE, companyState);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage editCompanyDetails(String testDataSetName,
			String category) throws Exception {
		String companyDescription = TestData.read("PageConfiguration.xml",
				testDataSetName, "companyDescription", category);
		String companyAddress = TestData.read("PageConfiguration.xml",
				testDataSetName, "companyAddress", category);
		String companyCity = TestData.read("PageConfiguration.xml",
				testDataSetName, "companyCity", category);
		String companyState = TestData.read("PageConfiguration.xml",
				testDataSetName, "companyState", category);
		this.setCompanyDescription(companyDescription);
		this.setCompanyAddress(companyAddress);
		this.setCompanyCity(companyCity);
		this.setCompanyState(companyState);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	/**
	 * This method will check/uncheck View Plugin checkbox
	 * 
	 * @param viewPlugin
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage viewPluginCheckbox(Boolean viewPlugin)
			throws Exception {
		if (viewPlugin) {
			GlobalController.brw.check(this.CB_VIEW_PLUGIN);
		} else {
			GlobalController.brw.uncheck(this.CB_VIEW_PLUGIN);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check/uncheck edit Plugin checkbox
	 * 
	 * 
	 * @param editPlugin
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage editPluginCheckbox(Boolean editPlugin)
			throws Exception {
		if (editPlugin) {
			GlobalController.brw.check(this.CB_EDIT_PLUGIN);
		} else {
			GlobalController.brw.uncheck(this.CB_EDIT_PLUGIN);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage editRoleCheckbox(Boolean editPlugin)
			throws Exception {
		if (editPlugin) {
			GlobalController.brw.check(this.CB_EDIT_ROLE);
		} else {
			GlobalController.brw.uncheck(this.CB_EDIT_ROLE);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage removeUserPluginPermission(String testDataSetName,
			String category) throws Exception {
		String environment = this.propReader.readConfig("EnvironmentUnderTest");
		String userName = this.propReader.readConfig(environment + "_Username");
		this.clickSpecifiedUserInUsersTable(userName);
		this.clickUsersPermissionsButton();
		this.viewPluginCheckbox(false);
		this.editPluginCheckbox(false);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage restoreUserPluginPermission(
			String testDataSetName, String category) throws Exception {
		String environment = this.propReader.readConfig("EnvironmentUnderTest");
		String userName = this.propReader.readConfig(environment + "_Username");
		this.clickSpecifiedUserInUsersTable(userName);
		this.clickUsersPermissionsButton();
		this.viewPluginCheckbox(true);
		this.editPluginCheckbox(true);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage verifyDeniedPluginPermissionMessage(
			String testDataSetName, String category) throws Exception {
		String pluginMessage = TestData.read("PageConfiguration.xml",
				testDataSetName, "pluginMessage", category);
		this.verifyPermissionDeniedDisplayedMessageText(pluginMessage,
				TextComparators.contains);
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	/**
	 * Compares in messages element for both messages to verify in one go with
	 * AND operation.
	 * 
	 * 
	 * @param Verify
	 *            Flags
	 * 
	 * @param additionalMessage
	 *            Second message to verify
	 * @param comparator
	 * @return NULL if result is true <br>
	 *         Message String if result is false
	 * @throws Exception
	 */
	protected ConfigurationPage verifyFlagField(String FlagField1)
			throws Exception {
		String msg = GlobalController.brw.getText(this.TT_InvoiceFlag);
		if (TextUtilities.contains(msg, FlagField1)) {
			Assert.assertTrue(true);
		} else {
			throw new Exception("Test Case failed: ");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage verifyFlagField2(String FlagField2)
			throws Exception {
		String msg = GlobalController.brw.getText(this.TT_FINISHEDFlag);
		if (TextUtilities.contains(msg, FlagField2)) {
			Assert.assertTrue(true);
		} else {
			throw new Exception("Test Case failed: ");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage verifyFlagField3(String FlagField3)
			throws Exception {
		String msg = GlobalController.brw.getText(this.TT_NOTVOICEFlag);
		if (TextUtilities.contains(msg, FlagField3)) {
			Assert.assertTrue(true);
		} else {
			throw new Exception("Test Case failed: ");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage verifyFlagField4(String FlagField4)
			throws Exception {
		String msg = GlobalController.brw.getText(this.TT_SUSPENDEDAGEINGFlag);
		if (TextUtilities.contains(msg, FlagField4)) {
			Assert.assertTrue(true);
		} else {
			throw new Exception("Test Case failed: ");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage verifyINVOICEDESC(String Description1)
			throws Exception {
		String msg = GlobalController.brw.getText(this.TT_INVOICEDESCR);
		if (TextUtilities.contains(msg, Description1)) {
			Assert.assertTrue(true);
		} else {
			throw new Exception("Test Case failed: ");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage verifyFINISHEDDESC(String Description2)
			throws Exception {
		String msg = GlobalController.brw.getText(this.TT_FINISHEDDESCR);
		if (TextUtilities.contains(msg, Description2)) {
			Assert.assertTrue(true);
		} else {
			throw new Exception("Test Case failed: ");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage verifyNOTINVOICEDESC(String Description3)
			throws Exception {
		String msg = GlobalController.brw.getText(this.TT_NOTINVOICEDESCR);
		if (TextUtilities.contains(msg, Description3)) {
			Assert.assertTrue(true);
		} else {
			throw new Exception("Test Case failed: ");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage verifySUSPENDEDDESC(String Description4)
			throws Exception {
		String msg = GlobalController.brw.getText(this.TT_SUSPENDEDDESCR);
		if (TextUtilities.contains(msg, Description4)) {
			Assert.assertTrue(true);
		} else {
			throw new Exception("Test Case failed: ");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setNewDescription(String newDescription)
			throws Exception {
		GlobalController.brw.setText(this.TB_NEWDESCRIPTION, newDescription);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will select Flag type dropdown
	 * 
	 * @param Flag
	 * @throws Exception
	 */
	protected ConfigurationPage selectFlag(String Flag) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_FLAGTYPE, Flag);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Compares in messages element for both messages to verify in one go with
	 * AND operation.
	 * 
	 * 
	 * @param messageToVerify
	 *            First message to verify
	 * @param additionalMessage
	 *            Second message to verify
	 * @param comparator
	 * @return NULL if result is true <br>
	 *         Message String if result is false
	 * @throws Exception
	 */
	public static String isOperationSuccessfulOnMessage(String messageToVerify,
			String additionalMessage, TextComparators comparator)
			throws Exception {
		String msg = ConfigurationPage.isOperationSuccessfulOnMessage(
				messageToVerify, comparator);
		String msg2 = ConfigurationPage.isOperationSuccessfulOnMessage(
				additionalMessage, comparator);

		if (msg == null) {
			if (msg2 == null) {
				return null;
			}
			return msg2;
		}

		return msg;
	}

	public static String isOperationSuccessfulOnMessage(
			PageSuccessMessages messageToVerify, TextComparators comparator)
			throws Exception {
		return MessagesPage.isOperationSuccessfulOnMessage(
				messageToVerify.GetValue(), comparator);
	}

	public static String isOperationSuccessfulOnMessage(String messageToVerify,
			TextComparators comparator) throws Exception {
		String msg = GlobalController.brw
				.getText(ConfigurationPage.TXT_PLUGIN_DENIED);
		ConfigurationPage.logger.info(msg);

		msg = TextUtilities.nullToBlank(msg, true);
		boolean result = TextUtilities.compareValue(messageToVerify, msg, true,
				comparator);

		return (result ? null : msg);
	}

	public ConfigurationPage verifyPermissionDeniedDisplayedMessageText(
			String messageToVerify, TextComparators comparator)
			throws Exception {

		String rsltMsg = ConfigurationPage.isOperationSuccessfulOnMessage(
				messageToVerify, TextComparators.contains);
		if (rsltMsg != null) {
			throw new Exception("Test Case failed: " + rsltMsg);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	public ConfigurationPage addUserPluginPermission(String testDataSetName,
			String category) throws Exception {
		String userName = TestData.read("PageConfiguration.xml",
				testDataSetName, "userName", category);
		this.clickSpecifiedUserInUsersTable(userName);
		this.clickUsersPermissionsButton();
		this.viewPluginCheckbox(true);
		this.editPluginCheckbox(true);
		this.editRoleCheckbox(true);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage selectRoleFromTable(String role)
			throws Exception {
		GlobalController.brw.clickTableCellWithText(this.TAB_ROLES, role);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage showAgentMenuCheckbox(Boolean cb)
			throws Exception {

		if (cb) {
			GlobalController.brw.check(this.CB_SHOW_AGENT_MENU);
		} else {
			GlobalController.brw.uncheck(this.CB_SHOW_AGENT_MENU);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage editAgentCheckbox(Boolean cb) throws Exception {

		if (cb) {
			GlobalController.brw.check(this.CB_EDIT_AGENT);
		} else {
			GlobalController.brw.uncheck(this.CB_EDIT_AGENT);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage viewAgentDetailsCheckbox(Boolean cb)
			throws Exception {
		if (cb) {
			GlobalController.brw.check(this.CB_VIEW_AGENT_DETAILS);
		} else {
			GlobalController.brw.uncheck(this.CB_VIEW_AGENT_DETAILS);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	public ConfigurationPage setRolePermission(String testDataSetName,
			String category) throws Exception {
		String role = TestData.read("PageConfiguration.xml", testDataSetName,
				"role", category);
		this.selectRoleFromTable(role);
		this.clickEditAccountTypeButton();
		this.showAgentMenuCheckbox(true);
		this.editAgentCheckbox(true);
		this.viewAgentDetailsCheckbox(true);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public String addPaymentMethod(String testDataSetName, String category)
			throws Exception {
		String paymentCard = TestData.read("PageConfiguration.xml",
				testDataSetName, "paymentCard", category);
		String methodName = TestData.read("PageConfiguration.xml",
				testDataSetName, "methodName", category);
		boolean isRecurring = TestData.read("PageConfiguration.xml",
				testDataSetName, "isRecurring", category).equals("true");
		boolean allAccountType = TestData.read("PageConfiguration.xml",
				testDataSetName, "allAccountTypes", category).equals("true");
		this.clickAddNewButton();
		this.selectPaymentTemplate(paymentCard);
		this.clickSelectButton();
		this.setMethodName(methodName);
		this.checkIsRecurring(isRecurring);
		this.checkAllAccountTypes(allAccountType);
		this.clickSaveChangesButton();
		return methodName;
	}

	public String addACHPaymentMethod(String testDataSetName, String category)
			throws Exception {
		String paymentTemplateACH = TestData.read("PageConfiguration.xml",
				testDataSetName, "paymentTemplateACH", category);
		String methodName = TestData.read("PageConfiguration.xml",
				testDataSetName, "methodName", category);
		boolean isRecurring = TestData.read("PageConfiguration.xml",
				testDataSetName, "isRecurring", category).equals("true");
		boolean allAccountType = TestData.read("PageConfiguration.xml",
				testDataSetName, "allAccountTypes", category).equals("true");
		this.clickAddNewButton();
		this.selectPaymentTemplate(paymentTemplateACH);
		this.clickSelectButton();
		this.setMethodName(methodName);
		this.checkIsRecurring(isRecurring);
		this.checkAllAccountTypes(allAccountType);
		this.clickSaveChangesButton();
		return methodName;
	}

	public ConfigurationPage addEditDeletePaymentMethod(String testDataSetName,
			String category) throws Exception {
		String newMethodName = TestData.read("PageConfiguration.xml",
				testDataSetName, "newMethodName", category);
		this.addPaymentMethodWithoutMetaFields(PaymentMethodField.REECURRING,
				testDataSetName, category);
		this.clickEditAccountTypeButton();
		this.setMethodName(newMethodName);
		this.clickSaveChangesButton();
		this.clickDeleteButton();
		this.clickConfirmPopupYesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage clickDeleteButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_DELETE);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public String createAccountTypeWithCreditDetails(String testDataSetName,
			String category, String paymentMethodNameOne) throws Exception {
		String accountName = TestData.read("PageConfiguration.xml",
				testDataSetName, "accountName", category);
		String billingCycle = TestData.read("PageConfiguration.xml",
				testDataSetName, "billingCycle", category);
		String invoiceDesign = TestData.read("PageConfiguration.xml",
				testDataSetName, "invoiceDesign", category);
		String creditLimit = TestData.read("PageConfiguration.xml",
				testDataSetName, "creditLimit", category);
		String creditLimitOne = TestData.read("PageConfiguration.xml",
				testDataSetName, "creditLimitOne", category);
		String creditLimitTwo = TestData.read("PageConfiguration.xml",
				testDataSetName, "creditLimitTwo", category);

		this.clickAddNewButton();
		this.setAccountTypeName(accountName);
		this.setAccountTypeBillingCycle(billingCycle);
		this.setAccountTypeInvoiceDesign(invoiceDesign);
		this.setCreditLimitForAccountType(creditLimit);
		this.setCreditLimitNotificationOneForAccountType(creditLimitOne);
		this.setCreditLimitNotificationTwoForAccountType(creditLimitTwo);
		this.selectPaymentMethodTypes(paymentMethodNameOne);
		this.clickSaveChangesButton();
		return accountName;
	}

	public String AddAccountTypeWithCreditDetailsForThreePay(
			String testDataSetName, String category,
			String paymentMethodNameOne, String paymentMethodNameTwo,
			String paymentMethodNameThree) throws Exception {
		String accountName = TestData.read("PageConfiguration.xml",
				testDataSetName, "accountName", category);
		String billingCycle = TestData.read("PageConfiguration.xml",
				testDataSetName, "billingCycle", category);
		String invoiceDesign = TestData.read("PageConfiguration.xml",
				testDataSetName, "invoiceDesign", category);
		String creditLimit = TestData.read("PageConfiguration.xml",
				testDataSetName, "creditLimit", category);
		String creditLimitOne = TestData.read("PageConfiguration.xml",
				testDataSetName, "creditLimitOne", category);
		String creditLimitTwo = TestData.read("PageConfiguration.xml",
				testDataSetName, "creditLimitTwo", category);

		this.clickAddNewButton();
		this.setAccountTypeName(accountName);
		this.setAccountTypeBillingCycle(billingCycle);
		this.setAccountTypeInvoiceDesign(invoiceDesign);
		this.setCreditLimitForAccountType(creditLimit);
		this.setCreditLimitNotificationOneForAccountType(creditLimitOne);
		this.setCreditLimitNotificationTwoForAccountType(creditLimitTwo);
		this.selectPaymentMethodTypes(paymentMethodNameOne);
		this.selectPaymentMethodTypes(paymentMethodNameTwo);
		this.selectPaymentMethodTypes(paymentMethodNameThree);
		this.clickSaveChangesButton();
		return accountName;
	}

	public ConfigurationPage setConfigurationPreferenceForAnyPreference(
			String testDataSetName, String category) throws Exception {
		String userName = TestData.read("PageConfiguration.xml",
				testDataSetName, "userName", category);
		String preferenceValue = TestData.read("PageConfiguration.xml",
				testDataSetName, "preferenceValue", category);
		this.clickSpecifiedUserInUsersTable(userName);
		this.setConfigurationalPreferenceValue(preferenceValue);
		this.clickSaveChangesButton();
		this.verifyConfigurationalPreferenceValue(preferenceValue);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage verifyMandatoryFieldMessages(
			String testDataSetName, String category) throws Exception {
		String message = TestData.read("PageConfiguration.xml",
				testDataSetName, "message", category);
		this.clickAddNewButton();
		this.clickSaveChangesButton();
		this.verifyMandatoryFieldMessage(message);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setCreditLimitForAccountType(String creditLimit)
			throws Exception {
		GlobalController.brw.setText(this.TB_CREDIT_LIMIT, creditLimit);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setCreditLimitNotificationOneForAccountType(
			String creditLimitOne) throws Exception {
		GlobalController.brw.setText(this.TB_CREDIT_LIMIT_NOT_ONE,
				creditLimitOne);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setCreditLimitNotificationTwoForAccountType(
			String creditLimitTwo) throws Exception {
		GlobalController.brw.setText(this.TB_CREDIT_LIMIT_NOT_TWO,
				creditLimitTwo);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage verifyMandatoryFieldMessage(String message)
			throws Exception {
		String msg = GlobalController.brw.getText(this.TXT_VALIDATION_MESSAGE);
		if (TextUtilities.contains(msg, message)) {
			Assert.assertTrue(true);
		} else {
			throw new Exception("Test Case failed: ");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public String addPaymentMethodWithoutMetaFields(
			PaymentMethodField paymentMethodFields, String testDataSetName,
			String category) throws Exception {
		String paymentTemplate = TestData.read("PageConfiguration.xml",
				testDataSetName, "paymentTemplate", category);
		String methodName = TestData.read("PageConfiguration.xml",
				testDataSetName, "methodName", category);
		boolean allAccountTypes = TestData.read("PageConfiguration.xml",
				testDataSetName, "allAccountTypes", category).equals("true");
		boolean isRecurring = TestData.read("PageConfiguration.xml",
				testDataSetName, "isRecurring", category).equals("true");

		switch (paymentMethodFields) {
		case ALL:
			this.clickAddNewButton();
			this.selectPaymentTemplate(paymentTemplate);
			this.clickSelectButton();
			this.setMethodName(methodName);
			this.checkAllAccountTypes(allAccountTypes);
			this.checkIsRecurring(isRecurring);
			break;

		case REECURRING:
			this.clickAddNewButton();
			this.selectPaymentTemplate(paymentTemplate);
			this.clickSelectButton();
			this.setMethodName(methodName);
			this.checkIsRecurring(isRecurring);
			break;

		case ALL_ACCOUNTS:
			this.clickAddNewButton();
			this.selectPaymentTemplate(paymentTemplate);
			this.clickSelectButton();
			this.setMethodName(methodName);
			this.checkAllAccountTypes(allAccountTypes);
			break;
		default:
			throw new Exception(
					"Invalid Step Provided. Not defined in enumeration");

		}
		this.clickSaveChangesButton();
		return methodName;
	}

	protected ConfigurationPage selectPaymentMethodTypes(String paymentMethod)
			throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PAYMENT_METHOD_TYPE,
				paymentMethod);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage deSelectPaymentMethodTypes(String paymentMethod)
			throws Exception {
		GlobalController.brw.deSelectDropDown(this.DD_PAYMENT_METHOD_TYPE,
				paymentMethod);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public String verifyPaymentMethodAvailableWithAllCust(
			String testDataSetName, String category) throws Exception {
		String accountName = TestData.read("PageConfiguration.xml",
				testDataSetName, "accountName", category);
		String billingCycle = TestData.read("PageConfiguration.xml",
				testDataSetName, "billingCycle", category);
		String invoiceDesign = TestData.read("PageConfiguration.xml",
				testDataSetName, "invoiceDesign", category);
		String creditLimit = TestData.read("PageConfiguration.xml",
				testDataSetName, "creditLimit", category);
		String creditLimitOne = TestData.read("PageConfiguration.xml",
				testDataSetName, "creditLimitOne", category);
		String creditLimitTwo = TestData.read("PageConfiguration.xml",
				testDataSetName, "creditLimitTwo", category);

		this.clickAddNewButton();
		this.setAccountTypeName(accountName);
		this.setAccountTypeBillingCycle(billingCycle);
		this.setAccountTypeInvoiceDesign(invoiceDesign);
		this.setCreditLimitForAccountType(creditLimit);
		this.setCreditLimitNotificationOneForAccountType(creditLimitOne);
		this.setCreditLimitNotificationTwoForAccountType(creditLimitTwo);
		this.clickSaveChangesButton();
		return accountName;
	}

	protected ConfigurationPage setAccNameInSearchField(String accName)
			throws Exception {
		GlobalController.brw.setText(this.TB_ACCOUNT_TYPE_SEARCH, accName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public String verifyPayMethodDefaultSelectedForAddingAccountType(
			String testDataSetName, String category, String paymentMethodName)
			throws Exception {
		String accountName = TestData.read("PageConfiguration.xml",
				testDataSetName, "accountName", category);

		this.clickAddNewButton();
		this.setAccountTypeName(accountName);
		this.VerifyPaymentMethodItemIsSelectedInDropdown(paymentMethodName);
		this.clickSaveChangesButton();
		return accountName;
	}

	public ConfigurationPage editPaymentMethodWithAllAccountTypeChecked(
			String testDataSetName, String category, String paymentMethodName)
			throws Exception {
		boolean allAccountTypes = TestData.read("PageConfiguration.xml",
				testDataSetName, "allAccountTypes", category).equals("true");

		this.selectAccountTypeName(paymentMethodName, TextComparators.equals);
		this.clickEditAccountTypeButton();
		this.checkAllAccountTypes(allAccountTypes);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage setPaymentIntegrationPluginForPaypal(
			String testDataSetName, String category) throws Exception {
		String pluginType = TestData.read("PageConfiguration.xml",
				testDataSetName, "pluginType", category);
		String order = TestData.read("PageConfiguration.xml", testDataSetName,
				"order", category);
		String paypaluserName = TestData.read("PageConfiguration.xml",
				testDataSetName, "paypaluserName", category);
		String paypalPassword = TestData.read("PageConfiguration.xml",
				testDataSetName, "paypalPassword", category);
		String paypalSignature = TestData.read("PageConfiguration.xml",
				testDataSetName, "paypalSignature", category);

		this.selectPluginType(pluginType);
		this.setPaypalUserName(paypaluserName);
		this.setPaypalPassword(paypalPassword);
		this.setPaypalSignature(paypalSignature);
		this.setPluginOrder(order);
		this.clickSavePlugin();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage clickPaymentIntegrationPlugin()
			throws Exception {
		GlobalController.brw.clickLinkText(this.TB_ACCOUNT_TYPE_SEARCH);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setPaypalUserName(String userName)
			throws Exception {
		GlobalController.brw.setText(this.TB_PAYPAL_USERNAME, userName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setPaypalPassword(String paypalPassword)
			throws Exception {
		GlobalController.brw.setText(this.TB_PAYPAL_PASSWORD, paypalPassword);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setPaypalSignature(String paypalSignature)
			throws Exception {
		GlobalController.brw.setText(this.TB_PAYPAL_SIGNATURE, paypalSignature);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method provide new name in "New Meta Data Group" page
	 * 
	 * @param Name
	 * @ret
	 **/
	public ConfigurationPage clickOnComapanyLink() throws Exception {
		GlobalController.brw.click(this.COMPANY_LINK);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage clickOnChildCompanyChkBox() throws Exception {
		GlobalController.brw.click(this.COMPANY_LINK);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage clickOnPaymentMethodLink() throws Exception {
		GlobalController.brw.click(this.PAYMENT_METHOD_LINK);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage clickOnAccountTypeLink() throws Exception {
		GlobalController.brw.click(this.ACCOUNTTYPE_LINK);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage clickMetaDataFieldValue(String metaFieldName)
			throws Exception {
		GlobalController.brw
				.selectListItem(this.TMD_ASSET_FIELD, metaFieldName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click On Clone button * @return
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickOnCloneButton() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_CLONE);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method provide new name in "New Meta Data" page
	 * 
	 * @param Name
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage enterNewMetaDataName(String Name)
			throws Exception {
		GlobalController.brw.setText(this.TB_MD_NAME, Name);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method provide new name in "New Meta Data Group" page
	 * 
	 * @param Name
	 * @return
	 * @throws Exception
	 */

	protected ConfigurationPage enterNewMetaDataGroupName(String Name)
			throws Exception {
		GlobalController.brw.setText(this.LT_GROUP_META_DATA_NAME, Name);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method provide new value in "Default" field
	 * 
	 * @param Name
	 * @return
	 * @throws Exception
	 */

	protected ConfigurationPage enterNewMetaDataDefaultValue(String Value)
			throws Exception {
		GlobalController.brw.setText(this.CB_MD_NEW_DEAULT, Value);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method select new meta data from drop down
	 * 
	 * @param dataTypeValue
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage selectDataTypeForNewMataData(
			String dataTypeValue) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_MD_NEW_DATA_TYPE,
				dataTypeValue);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method select new validation rule data from drop down
	 * 
	 * @param dataTypeValue
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage selectValidationRuleForNewMataData(
			String dataTypeValue) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_MD_VALIDATION,
				dataTypeValue);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Check/Uncheck <b>Manadatory</b> permission
	 * 
	 * @param editPlugin
	 * @throws Exception
	 */
	protected ConfigurationPage changeUserPermissionForMandatory(
			boolean editManadatory) throws Exception {
		if (editManadatory) {
			GlobalController.brw.check(this.CB_NEW_MANDATORY);
		} else {
			GlobalController.brw.uncheck(this.CB_NEW_MANDATORY);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Check/Uncheck <b>DIsabled</b> permission
	 * 
	 * @param editPlugin
	 * @throws Exception
	 */
	protected ConfigurationPage changeUserPermissionForeditDIsabled(
			boolean editDIsabled) throws Exception {
		if (editDIsabled) {
			GlobalController.brw.check(this.CB_MD_NEW_DISABLE);
		} else {
			GlobalController.brw.uncheck(this.CB_MD_NEW_DISABLE);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Check/Uncheck <b>Help</b> permission
	 * 
	 * @param editPlugin
	 * @throws Exception
	 */
	protected ConfigurationPage changeUserPermissionForeditHelp(boolean editHelp)
			throws Exception {
		if (editHelp) {
			GlobalController.brw.check(this.CB_MD_HELP);
		} else {
			GlobalController.brw.uncheck(this.CB_MD_HELP);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Wraper class for meta data methods
	 * 
	 * @param testDataSetName
	 * @param category
	 * @throws Exception
	 */
	public String setNewMetaData(AddMetaDataFields addMetaField,
			String testDataSetName, String category) throws Exception {
		String Name = TestData.read("PageConfiguration.xml", testDataSetName,
				"Name", category);
		String Value = TestData.read("PageConfiguration.xml", testDataSetName,
				"Value", category);
		String dataTypeValue = TestData.read("PageConfiguration.xml",
				testDataSetName, "dataTypeValue", category);
		boolean permission = TestData.read("PageConfiguration.xml",
				testDataSetName, "permission", category).equals("true");

		switch (addMetaField) {
		case DATA_FIELD:
			this.enterNewMetaDataName(Name);
			this.changeUserPermissionForMandatory(permission);
			this.enterNewMetaDataDefaultValue(Value);
			this.clickSaveChangesButton();
			break;
		case DATA_TYPE:
			this.enterNewMetaDataName(Name);
			this.enterNewMetaDataDefaultValue(Value);
			this.selectDataTypeForNewMataData(dataTypeValue);
			this.clickSaveChangesButton();
			break;
		case DATA_DEFAULT_VALUE:
			this.enterNewMetaDataName(Name);
			this.enterNewMetaDataDefaultValue(Value);
			this.clickSaveChangesButton();
			break;
		default:
			throw new Exception("Bad enum value: " + addMetaField);
		}
		return Name;
	}

	public ConfigurationPage validateMetaSavedTestData(String data)
			throws Exception {
		GlobalController.brw.validateSavedTestData(
				this.TB_VERIFY_CREATED_META_DAT, data);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Selects multiple valued from "Available Metadata" displayed on page
	 * 
	 * @param metaGroupValue
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage selectMetaDataGroupValue(String metaGroupValue)
			throws Exception {
		GlobalController.brw.selectListItem(this.MV_METADATA_GROUP,
				metaGroupValue);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click Arrow ">" button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickArrowButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ARROW_TOADD);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage setNewMetaDataGroup(
			AddMetaDataGroupFields addMetaField, String testDataSetName,
			String category, String valueOne, String valueTwo) throws Exception {

		String GroupName1 = TestData.read("PageConfiguration.xml",
				testDataSetName, "Name1", category);
		String GroupName2 = TestData.read("PageConfiguration.xml",
				testDataSetName, "Name2", category);
		switch (addMetaField) {
		case GROUP_DATA_FIELD:
			this.enterNewMetaDataGroupName(GroupName1);
			this.selectMetaDataGroupValue(valueOne);
			this.clickArrowButton();
			this.selectMetaDataGroupValue(valueTwo);
			this.clickArrowButton();
			this.clickSaveChangesButton();
			this.clickEditAccountTypeButton();
			this.enterNewMetaDataGroupName(GroupName2);
			break;

		default:
			throw new Exception("Bad enum value: " + addMetaField);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	/**
	 * @author Shashank Gupta
	 * 
	 *         This method will validate payments table saved test data
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage validateInformationForSavedData(String data)
			throws Exception {

		GlobalController.brw.validateSavedTestData(this.TAB_INNER_TABLE, data);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage pressEnter() throws Exception {
		GlobalController.brw.pressEnter(this.TB_ACCOUNT_TYPE_SEARCH);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click on specified Account Name
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage validateSpecifiedAccountName(String accName)
			throws Exception {
		GlobalController.brw.validateSavedTestData(
				this.TAB_ACCOUNT_TYPE_SEARCH, accName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage searchAndSortFunctionalityOfAccType(String accName)
			throws Exception {

		this.setAccNameInSearchField(accName);
		this.pressEnter();
		this.validateSpecifiedAccountName(accName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage clickJQGridPreference() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_JQGRID);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage ReupdateJQGridPreference(String testDataSetName,
			String category) throws Exception {
		String preferenceVal = TestData.read("PageConfiguration.xml",
				testDataSetName, "preferenceVal", category);

		this.clickJQGridPreference();
		this.setConfigurationalPreferenceValue(preferenceVal);
		this.clickSaveChangesButton();
		this.verifyUpdatedPreference();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Edit account type for last created account type
	 * 
	 * @param testDataSetName
	 * @param category
	 * @return Account Type Field edited
	 * @throws Exception
	 */
	public String editAccountTypeForGivenAccountWithThreePay(
			String testDataSetName, String category, String accName,
			String paymentMethodOne, String paymentMethodTwo,
			String paymentMethodThree) throws Exception {
		String accountName = TestData.read("PageConfiguration.xml",
				testDataSetName, "accountName", category);

		this.selectAccountTypeName(accName, TextComparators.equals);
		this.clickEditAccountTypeButton();
		this.setAccountTypeName(accountName);
		this.selectPaymentMethodTypes(paymentMethodOne);
		this.selectPaymentMethodTypes(paymentMethodTwo);
		this.selectPaymentMethodTypes(paymentMethodThree);
		this.clickSaveChangesButton();
		return accountName;

	}

	/**
	 * Edit account type for last created account type
	 * 
	 * @param testDataSetName
	 * @param category
	 * @return Account Type Field edited
	 * @throws Exception
	 */

	public ConfigurationPage editAccountTypeForGivenAccountDeselectPay(
			String accName, String paymentMethod) throws Exception {

		this.selectAccountTypeName(accName, TextComparators.equals);
		this.clickEditAccountTypeButton();
		this.deSelectPaymentMethodTypes(paymentMethod);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage VerifyPaymentMethodItemIsSelectedInDropdown(
			String targetValue) throws Exception {
		GlobalController.brw.getDropDownOptionIsSelected(
				this.DD_PAYMENT_METHOD_TYPE, targetValue);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage verifyPayMethodDefaultSelectedForAccountType(
			String paymentMethodName) throws Exception {

		this.clickAddNewButton();
		this.VerifyPaymentMethodItemIsSelectedInDropdown(paymentMethodName);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage checkboxDisableInMetaFields(Boolean cb)
			throws Exception {
		if (cb) {
			GlobalController.brw.check(this.CB_DISABLED);
		} else {
			GlobalController.brw.uncheck(this.CB_DISABLED);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	protected ConfigurationPage selectInfoMetaDataType(String targetValue)
			throws Exception {
		GlobalController.brw.selectDropDown(this.DD_INFO_META_DATA_TYPE,
				targetValue);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage addAccountWithMetaInformation(
			String testDataSetName, String category) throws Exception {
		String metaFieldName = TestData.read("PageConfiguration.xml",
				testDataSetName, "name", category);
		String metaFieldType = TestData.read("PageConfiguration.xml",
				testDataSetName, "metaFieldType", category);
		this.setMetaFieldName(metaFieldName);
		this.selectMetafieldType(metaFieldType);
		this.clickUpdateButton();

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Delete Existing Meta field and Add
	 */
	public ConfigurationPage createMetafield() throws Exception {

		try {
			String displayedText = GlobalController.brw
					.getText(this.METAFEILD_HEADER);
			String expectedText = "META FIELD CATEGORIES";
			if (displayedText.contains(expectedText)) {
				ConfigurationPage.logger.info(this.METAFEILD_HEADER
						+ "is Visible");
			} else {
				ConfigurationPage.logger.info(this.METAFEILD_HEADER
						+ "is not Visible");
				throw new Exception();
			}
			GlobalController.brw.click(this.SELECT_CUSTOMER_IN_META_FIELD);
			boolean flag = false;
			flag = GlobalController.brw.isElementPresent(this.META_SEARCH);
			System.out.println("I am here ------------------------------"
					+ flag);
			if (flag == true) {
				GlobalController.brw.click(this.META_SEARCH);
				this.clickDeleteButton();
				GlobalController.brw.click(this.YES_BUTTON_FOR_DELETE_BUTTON);
				// GlobalController.brw.acceptAlert();
			}
			this.clickAddNewButton();
			this.clickSaveChangesButton();
			MessagesPage.isErrorMessageAppeared();
			this.addMetaData();
			MessagesPage.isIntermediateSuccessMessageAppeared();

		} catch (Exception e) {
			ConfigurationPage.logger.info(e);
		}
		return null;
	}

	/**
	 * Verify Pre-existing order statuses and new Order can be customized
	 * 
	 * @param testDataSetName
	 * @param category
	 * @throws Exception
	 */
	public ConfigurationPage VerifyOrderStatus(String testDataSetName,
			String category) throws Exception {
		String FlagField1 = TestData.read("PageConfiguration.xml",
				testDataSetName, "FlagField1", category);
		String FlagField2 = TestData.read("PageConfiguration.xml",
				testDataSetName, "FlagField2", category);
		String FlagField3 = TestData.read("PageConfiguration.xml",
				testDataSetName, "FlagField3", category);
		String FlagField4 = TestData.read("PageConfiguration.xml",
				testDataSetName, "FlagField4", category);
		String Description1 = TestData.read("PageConfiguration.xml",
				testDataSetName, "Description1", category);
		String Description2 = TestData.read("PageConfiguration.xml",
				testDataSetName, "Description2", category);
		String Description3 = TestData.read("PageConfiguration.xml",
				testDataSetName, "Description3", category);
		String Description4 = TestData.read("PageConfiguration.xml",
				testDataSetName, "Description4", category);
		String newDescription = TestData.read("PageConfiguration.xml",
				testDataSetName, "newDescription", category);
		String newDescription1 = TestData.read("PageConfiguration.xml",
				testDataSetName, "newDescription1", category);
		String Flag = TestData.read("PageConfiguration.xml", testDataSetName,
				"Flag", category);

		this.clickOrderStatuses();
		this.verifyFlagField(FlagField1);
		this.verifyFlagField2(FlagField2);
		this.verifyFlagField3(FlagField3);
		this.verifyFlagField4(FlagField4);
		this.verifyINVOICEDESC(Description1);
		this.verifyFINISHEDDESC(Description2);
		this.verifyNOTINVOICEDESC(Description3);
		this.verifySUSPENDEDDESC(Description4);
		this.clickAddNewButton();
		this.setNewDescription(newDescription);
		this.clickSaveChangesButton();
		this.clickAddNewButton();
		this.selectFlag(Flag);
		this.setNewDescription(newDescription1);
		this.clickSaveChangesButton();

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public void addMetaData() throws Exception {
		String name = TestData.read("PageConfiguration.xml", "MetadataName",
				"Metadata", "MDN");
		String data_type = TestData.read("PageConfiguration.xml",
				"MetadataName", "MetDataType", "MDN");
		GlobalController.brw.setText(this.METAFEILD_NAME, name);
		GlobalController.brw.selectDropDown(this.METAFEILD_DATATYPE, data_type);
		GlobalController.brw.check(this.MANDATORY_CHECK);
		this.clickSaveChangesButton();

	}

	/**
	 * 
	 * @param childCompanyName
	 * @return
	 * @throws Exception
	 */
	public ConfigurationPage setChild_CompanyName(String childCompanyName)
			throws Exception {
		GlobalController.brw.setText(this.TB_TEMPLATE_COMPANY_NAME,
				childCompanyName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Add Invoice Templates
	 */

	public String addInvoiceTemplate(String testDataSetName, String category)
			throws Exception {
		String templateName = TestData.read("PageConfiguration.xml",
				testDataSetName, "Name", category);
		this.clickAddNewButton();
		this.setInvoiceName(templateName);
		this.clickSaveChangesTemplatebutton();

		return templateName;
	}

	/**
	 * Add data status Processing ,Finished and Suspended
	 */

	public String enterDataStatus(String testDataSetName, String category)
			throws Exception {

		String enterProcessingOrderStatus = TestData.read(
				"PageConfiguration.xml", testDataSetName,
				"enterProcessingOrderStatus", category);
		String enterFinishedOrderStatus = TestData.read(
				"PageConfiguration.xml", testDataSetName,
				"enterFinishedOrderStatus", category);

		String enterSuspendedOrderStatus = TestData.read(
				"PageConfiguration.xml", testDataSetName,
				"enterSuspendedOrderStatus", category);

		this.setOrderStatusProcessing(enterProcessingOrderStatus);
		this.clickPluginAddMoreParametersIcon();

		this.setFinishedOrderStatus(enterFinishedOrderStatus);
		this.clickPluginAddMoreParametersIcon();

		this.setSuspendedOrderStatus(enterSuspendedOrderStatus);
		this.clickPluginAddMoreParametersIcon();
		this.unCheckAppliedbox();
		this.CheckAppliedbox();

		return enterSuspendedOrderStatus;

	}

	public String createAccount(String paymentMethod, String testDataSetName,
			String category) throws Exception {
		String accountName = TestData.read("PageConfiguration.xml",
				testDataSetName, "accountName", category);
		this.clickAddNewButton();
		this.setAccountTypeName(accountName);
		this.selectPaymentMethodTypes(paymentMethod);
		this.clickSaveChangesButton();
		return accountName;

	}

	public ConfigurationPage copyCompany(String testDataSetName, String category)
			throws Exception {
		boolean childCompany = TextUtilities.compareValue(TestData.read(
				"PageConfiguration.xml", testDataSetName, "childCompany",
				category), "true", true, TextComparators.equals);
		this.clickOnComapanyLink();
		String companyDescription = this.getCompanyDescription();
		this.clickCopyCompanyButton();
		this.markCompanyAsChildCompany(childCompany);
		this.setChild_CompanyName(companyDescription);
		this.clickConfirmPopupYesButton();
		this.verifySavedProduct();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setOrderStatusProcessing(
			String enterProcessingOrderStatus) throws Exception {

		String processingBox = GlobalController.brw
				.getText(this.TB_PROCESSING_ORDER_STATUS);

		if (processingBox != null) {

			GlobalController.brw.clearTextBox(this.TB_PROCESSING_ORDER_STATUS);
			GlobalController.brw.setText(this.TB_PROCESSING_ORDER_STATUS,
					enterProcessingOrderStatus);

		} else {
			GlobalController.brw.setText(this.TB_PROCESSING_ORDER_STATUS,
					enterProcessingOrderStatus);

		}

		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	protected ConfigurationPage setFinishedOrderStatus(
			String enterFinishedOrderStatus) throws Exception {

		String finishedBox = GlobalController.brw
				.getText(this.TB_FINISHED_ORDER_STATUS);

		if (finishedBox != null) {

			GlobalController.brw.clearTextBox(this.TB_FINISHED_ORDER_STATUS);
			GlobalController.brw.setText(this.TB_FINISHED_ORDER_STATUS,
					enterFinishedOrderStatus);

		} else {
			GlobalController.brw.setText(this.TB_FINISHED_ORDER_STATUS,
					enterFinishedOrderStatus);

		}

		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	protected ConfigurationPage setSuspendedOrderStatus(
			String enterSuspendedOrderStatus) throws Exception {

		String FourthEnglishBox = GlobalController.brw
				.getText(this.TB_SUSUPENDED_ORDER_STATUS);

		if (FourthEnglishBox != null) {

			GlobalController.brw.clearTextBox(this.TB_SUSUPENDED_ORDER_STATUS);
			GlobalController.brw.setText(this.TB_SUSUPENDED_ORDER_STATUS,
					enterSuspendedOrderStatus);

		} else {
			GlobalController.brw.setText(this.TB_SUSUPENDED_ORDER_STATUS,
					enterSuspendedOrderStatus);

		}
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	public String unCheckAppliedbox() throws Exception {

		GlobalController.brw.uncheck(this.CB_APPLIED);

		this.clickSaveChangesButton();

		return GlobalController.brw
				.getText(ConfigurationPage.STRONG_ERROR_MESSAGE);

	}

	public void CheckAppliedbox() throws Exception {

		GlobalController.brw.check(this.CB_APPLIED);

		this.clickSaveChangesButton();

	}

	/**
	 * Add New Information to selected account type
	 * 
	 * @throws Exception
	 */
	public String addNewInformationToSelectedAccountType(
			String testDataSetName, String category) throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADD_NEW_INFORMATION);

		String infoTypeName = TestData.read("PageConfiguration.xml",
				testDataSetName, "name", category);
		String metaFieldName = TestData.read("PageConfiguration.xml",
				testDataSetName, "name", category);
		String metaFieldType = TestData.read("PageConfiguration.xml",
				testDataSetName, "metaFieldType", category);

		this.setAccountTypeInformationTypeName(infoTypeName);
		this.clickAddNewMetaField();
		this.addMetaField(metaFieldName, metaFieldType);
		this.clickSaveChangesToInformationType();

		return infoTypeName;
	}

	/**
	 * This Method will add Metafield
	 * 
	 * @param paitad
	 * @throws Exception
	 */
	protected ConfigurationPage addMetaField(String name, String metaFieldType)
			throws Exception {
		activateMetaFieldEditor();
		this.updateMetaFieldDetails(name, metaFieldType);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Add/Update meta field information
	 * 
	 * @param name
	 * @param metaFieldType
	 * @throws Exception
	 */
	protected ConfigurationPage updateMetaFieldDetails(String name,
			String metaFieldType) throws Exception {
		this.setMetaFieldName(name);
		this.selectMetafieldType(metaFieldType);
		this.clickUpdateButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click directCustomer
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage selectAccountName(String accountTypeName)
			throws Exception {
		GlobalController.brw.selectListItem(this.TAB_ACCOUNT_TYPES_LIST_NAMES,
				accountTypeName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage configurePluginPermissions(String userName,
			String testDataSetName, String category) throws Exception {
		boolean plugin = TextUtilities.compareValue(TestData.read(
				"PageConfiguration.xml", testDataSetName, "plugin", category),
				"true", true, TextComparators.equals);
		this.clickSpecifiedUserInUsersTable(userName);
		this.clickUsersPermissionsButton();
		this.changeUserPermissionForEditPlugin(plugin);
		this.clickSaveChangesToUsersPermissions();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage setConfigurationPreferenceJQGrid(
			String testDataSetName, String category) throws Exception {
		String notification = TestData.read("PageConfiguration.xml",
				testDataSetName, "notification", category);
		String preferenceValue = TestData.read("PageConfiguration.xml",
				testDataSetName, "preferenceValue", category);

		this.isJQGridPageLoaded();
		this.selectConfigurationPreferenceJQGrid(notification);
		this.setConfigurationalPreferenceValue(preferenceValue);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will verify Mandatory field message for New Enumeration
	 * Creation Form
	 * 
	 * @param newEnumerationMsg
	 * @param testDataSetName
	 * @param category
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	public ConfigurationPage verifyMandatoryFieldMessageForNewEnumeration(
			NewEnumerationMsg newEnumerationMsg, String testDataSetName,
			String category) throws Exception {
		String NameMessage = TestData.read("PageConfiguration.xml",
				testDataSetName, "NameMessage", category);
		String ValueMessage = TestData.read("PageConfiguration.xml",
				testDataSetName, "ValueMessage", category);
		System.out.println(ValueMessage);
		System.out.println(NameMessage);
		switch (newEnumerationMsg) {
		case ALL:
			String NameMsg = GlobalController.brw
					.getText(this.TXT_ENUMERATION_MESSAGE_NAME);
			String ValueMsg = GlobalController.brw
					.getText(this.TXT_ENUMERATION_MESSAGE_VALUE);
			System.out.println(NameMsg);
			System.out.println(ValueMsg);
			ConfigurationPage.logger.debug(NameMsg);
			if (TextUtilities.contains(NameMsg, NameMessage)
					&& (TextUtilities.contains(ValueMsg, ValueMessage))) {
				Assert.assertTrue(true);
			} else {
				Assert.assertTrue(false);
			}
			break;

		case NAME:
			String onlyNameMsg = GlobalController.brw
					.getText(this.TXT_ENUMERATION_MESSAGE);
			if (TextUtilities.contains(onlyNameMsg, NameMessage)) {
				Assert.assertTrue(true);
			} else {
				Assert.assertTrue(false);
			}
			break;

		case VALUE:
			String onlyValueMsg = GlobalController.brw
					.getText(this.TXT_ENUMERATION_MESSAGE);
			if (TextUtilities.contains(onlyValueMsg, ValueMessage)) {
				Assert.assertTrue(true);
			} else {
				Assert.assertTrue(false);
			}
			break;

		default:
			throw new Exception(
					"Invalid Message Enum provided to work on. Not defined in enumeration");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Enumeration Name
	 * 
	 * @param enumerationName
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	protected ConfigurationPage setEnumerationName(String enumerationName)
			throws Exception {
		GlobalController.brw.setText(this.TB_ENUMERATION_NAME, enumerationName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage clickOnCustomerInMetaField() throws Exception {
		GlobalController.brw.clickLinkText(this.SELECT_CUSTOMER_IN_META_FIELD);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setEnglishName(String englishNameValue)
			throws Exception {
		GlobalController.brw.setText(this.TB_ENGLISH_NAME, englishNameValue);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setQuantity(String quantity) throws Exception {
		GlobalController.brw.setText(this.TB_QUANTITY, quantity);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setCyclePeriod(String cyclePeriod)
			throws Exception {
		GlobalController.brw.setText(this.TB_CYCLE_PERIOD, cyclePeriod);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setConsumptionFirstbox(String consumption)
			throws Exception {
		GlobalController.brw.setText(this.TB_CONSUMPTION1, consumption);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setConsumptionSecondbox(String consumption)
			throws Exception {
		GlobalController.brw.setText(this.TB_CONSUMPTION2, consumption);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setProductID(String Id) throws Exception {
		GlobalController.brw.setText(this.TB_PRODUCT_ID, Id);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Set Enumeration Value
	 * 
	 * @param setEnumValues
	 * @param enumerationValue
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	protected ConfigurationPage setEnumerationValue(
			SetEnumValues setEnumValues, String enumerationValue)
			throws Exception {

		switch (setEnumValues) {
		case ZERO:
			GlobalController.brw.setText(this.TB_ZERO_ENUMERATION_VALUE,
					enumerationValue);
			break;
		case ONE:
			GlobalController.brw.setText(this.TB_FIRST_ENUMERATION_VALUE,
					enumerationValue);
			break;
		default:
			throw new Exception(
					"Invalid Message Enum provided to work on. Not defined in enumeration");
		}

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This Method will Validate Given Enumeration Name in Enumeration Table
	 * 
	 * @param enumerationName
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	public ConfigurationPage validateEnumerationsSavedData(
			String enumerationName) throws Exception {
		GlobalController.brw.validateSavedTestData(this.TAB_ENUMERATION,
				enumerationName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This Method will select Available Enumeration from Enumeration Table
	 * 
	 * @param enumerationName
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	public ConfigurationPage selectEnumerationsFromTable(String enumerationName)
			throws Exception {
		GlobalController.brw.selectListItem(this.TAB_ENUMERATION_FIRST_CELL,
				enumerationName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will clicks on edit button
	 * 
	 * @throws Exception
	 * @author Shashank
	 */
	protected ConfigurationPage clickEditButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_EDIT_BUTTON);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click on + image button on Create Enumeration Form Page
	 * 
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	protected ConfigurationPage clickImageAddMoreEnumerationValueField()
			throws Exception {
		GlobalController.brw.clickLinkText(this.IMG_ADD_MORE_ENUMERATION_VALUE);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will ckick on Cancel Button
	 * 
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	protected ConfigurationPage clickCancelButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_CANCEL);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Method to Verify Error Message on Saving Enumeration with Duplicate
	 * Values
	 * 
	 * @param testDataSetName
	 * @param category
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */

	public ConfigurationPage verifyErrorMsgForDuplicateEnumValue(
			String errorMessage) throws Exception {
		String NameMsg = GlobalController.brw
				.getText(this.TXT_ERROR_MESSAGE_ENUMERATION);
		System.out.println("Error Message Present On UI" + NameMsg);
		System.out
				.println("Error Message Provided in Test Data" + errorMessage);
		ConfigurationPage.logger.debug("Error Message Present On UI" + NameMsg);
		if (TextUtilities.contains(NameMsg, errorMessage)) {
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(false);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This Method will Click on X Image icon to Remove Value Field on Create
	 * Enumeration Page
	 * 
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	protected ConfigurationPage clickImageRemoveEnumerationFirstValueField()
			throws Exception {
		GlobalController.brw.clickLinkText(this.IMG_REMOVE_ENUMERATION_VALUE);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Create Enumeration
	 * 
	 * @param addNewEnumeration
	 * @param testDataSetName
	 * @param category
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	public String createEnumeration(AddNewEnumeration addNewEnumeration,
			String testDataSetName, String category) throws Exception {
		String enumerationName = TestData.read("PageConfiguration.xml",
				testDataSetName, "enumerationName", category);
		String enumerationValue = TestData.read("PageConfiguration.xml",
				testDataSetName, "enumerationValue", category);

		switch (addNewEnumeration) {
		case VERIFY_MANDATORY_FIELDS:
			this.clickAddNewButton();
			this.clickSaveChangesButton();
			this.verifyMandatoryFieldMessageForNewEnumeration(
					NewEnumerationMsg.ALL, testDataSetName, category);
			this.setEnumerationName(enumerationName);
			this.clickSaveChangesButton();
			this.verifyMandatoryFieldMessageForNewEnumeration(
					NewEnumerationMsg.VALUE, testDataSetName, category);
			this.setEnumerationValue(SetEnumValues.ZERO, enumerationValue);
			this.clickSaveChangesButton();
			break;

		default:
			throw new Exception(
					"Invalid Message Enum provided to work on. Not defined in enumeration");
		}
		return enumerationName;
	}

	/**
	 * This method will click on edit configuration method
	 * 
	 * @param existEnumName
	 * @param testDataSetName
	 * @param category
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	public String editConfiguration(String existEnumName,
			String testDataSetName, String category) throws Exception {
		String enumerationValue = TestData.read("PageConfiguration.xml",
				testDataSetName, "editEnumerationValue", category);
		this.selectEnumerationsFromTable(existEnumName);
		this.clickEditButton();
		this.clickImageAddMoreEnumerationValueField();
		this.clickImageRemoveEnumerationFirstValueField();
		this.setEnumerationValue(SetEnumValues.ZERO, enumerationValue);
		this.clickSaveChangesButton();
		return enumerationValue;
	}

	/**
	 * This method will Verify Duplicate Value not acceptable message for Adding
	 * Enumeration
	 * 
	 * @param existEnumName
	 * @param testDataSetName
	 * @param category
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	public ConfigurationPage verifyDuplicateValueForEnumeration(
			String existEnumName, String testDataSetName, String category)
			throws Exception {
		String enumerationValue = TestData.read("PageConfiguration.xml",
				testDataSetName, "editEnumerationValue", category);
		String errorMessage = TestData.read("PageConfiguration.xml",
				testDataSetName, "errorMessage", category);

		this.selectEnumerationsFromTable(existEnumName);
		this.clickEditButton();
		this.clickImageAddMoreEnumerationValueField();
		this.setEnumerationValue(SetEnumValues.ONE, enumerationValue);
		this.clickSaveChangesButton();
		this.verifyErrorMsgForDuplicateEnumValue(errorMessage);
		this.clickCancelButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click on NO button in Confirm Delete Popup
	 * 
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */

	public ConfigurationPage clickConfirmPopupNOButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_CONFIRM_NO);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public String AddFreeUsagePool(String testDataSetName, String category,
			String ID, String productCategory, String products)
			throws Exception {

		String englishName = TestData.read("PageConfiguration.xml",
				testDataSetName, "englishName", category);
		String quantity = TestData.read("PageConfiguration.xml",
				testDataSetName, "quantity", category);
		String cyclePeriod = TestData.read("PageConfiguration.xml",
				testDataSetName, "cyclePeriod", category);
		String consumption1 = TestData.read("PageConfiguration.xml",
				testDataSetName, "consumption1", category);
		String consumption2 = TestData.read("PageConfiguration.xml",
				testDataSetName, "consumption2", category);
		String usagePoolConsumptionNotification = TestData.read(
				"PageConfiguration.xml", testDataSetName,
				"usagePoolConsumptionNotification", category);
		String usagePoolConsumptionFee = TestData.read("PageConfiguration.xml",
				testDataSetName, "usagePoolConsumptionFee", category);
		String usagePoolConsumptionNotification_Invoice = TestData.read(
				"PageConfiguration.xml", testDataSetName,
				"usagePoolConsumptionNotification_Invoice", category);
		String usagePoolConsumptionNotification_Invoice_mail = TestData.read(
				"PageConfiguration.xml", testDataSetName,
				"usagePoolConsumptionNotification_Invoice_mail", category);

		this.clickAddNewButton();
		this.clickPluginAddMoreNameParametersIcon();
		this.setEnglishName(englishName);
		this.setQuantity(quantity);
		this.setCyclePeriod(cyclePeriod);
		this.selectProductCategory(productCategory);
		this.selectProduct(products);

		this.selectUsagePoolConsumptionNotification(usagePoolConsumptionNotification);
		this.selectusagePoolConsumptionNotification_Invoice(usagePoolConsumptionNotification_Invoice);
		this.selectusagePoolConsumptionNotification_Invoice_mail(usagePoolConsumptionNotification_Invoice_mail);

		this.setConsumptionFirstbox(consumption1);
		this.clickPluginAddMoreParametersIcon();
		this.selectUsagePoolConsumptionFee(usagePoolConsumptionFee);
		this.setConsumptionSecondbox(consumption2);
		this.setProductID(ID);
		this.clickPluginAddMoreParametersIcon();
		this.clickSaveChangesButton();

		return englishName;
	}

	/**
	 * This method will Perform Delete Operation For Yes and No Scenario
	 * 
	 * @param delete
	 * @param existEnumName
	 * @return
	 * @throws Exception
	 * @author Shashank
	 */
	public ConfigurationPage checkDeleteYesNo(String delete) throws Exception {
		switch (delete) {
		case "YES":
			this.clickDeleteButton();
			this.clickConfirmPopupYesButton();
			break;
		case "NO":
			this.clickDeleteButton();
			this.clickConfirmPopupNOButton();
			break;
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Selects 100 National Call Minutes Free from Usage Pool Name
	 * 
	 * @param configItemName
	 * @throws Exception
	 */
	public ConfigurationPage selectUsagePoolName(String usagePoolName)
			throws Exception {

		GlobalController.brw.selectListItem(this.LV_USAGE_POOL_NAME,
				usagePoolName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Select UsagePool Consumption Notification
	 * 
	 * @param usagePoolConsumption
	 * @throws Exception
	 */
	protected ConfigurationPage selectUsagePoolConsumptionNotification(
			String usagePoolConsumptionNotification) throws Exception {
		GlobalController.brw.selectDropDown(
				this.DD_usagePoolConsumptionNotification,
				usagePoolConsumptionNotification);
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	protected ConfigurationPage selectusagePoolConsumptionNotification_Invoice(
			String usagePoolConsumptionNotification_Invoice) throws Exception {
		GlobalController.brw.selectDropDown(
				this.DD_usagePoolConsumptionNotification_Invoice,
				usagePoolConsumptionNotification_Invoice);
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	protected ConfigurationPage selectusagePoolConsumptionNotification_Invoice_mail(
			String usagePoolConsumptionNotification_Invoice_mail)
			throws Exception {
		GlobalController.brw.selectDropDown(
				this.DD_usagePoolConsumptionNotification_Invoice_mail,
				usagePoolConsumptionNotification_Invoice_mail);
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	/**
	 * Select UsagePool Consumption Fee
	 * 
	 * @param usagePoolConsumption
	 * @throws Exception
	 */
	protected ConfigurationPage selectUsagePoolConsumptionFee(
			String usagePoolConsumptionFee) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_usagePoolConsumptionFee,
				usagePoolConsumptionFee);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage selectUsagePool(String usagePoolName)
			throws Exception {

		this.selectUsagePoolName(usagePoolName);
		this.checkDeleteYesNo("NO");
		this.checkDeleteYesNo("YES");
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage valdationMessageDisplay(String message)
			throws Exception {
		String msg = GlobalController.brw
				.getText(ConfigurationPage.STRONG_ERROR_MESSAGE1);
		if (TextUtilities.contains(msg, message)) {
			Assert.assertTrue(true);
		} else {
			throw new Exception("Test Case failed: ");
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	public ConfigurationPage clickOnAddInformationType() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADD_NEW_INFORMATION);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Enter shipping address name
	 * 
	 * @param secondRowTestData
	 * @throws Exception
	 */
	ConfigurationPage setAccountName(String OrderTestData) throws Exception {
		GlobalController.brw.setText(this.TB_METAFIELD_NAME, OrderTestData);
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	/**
	 * This method will validate Account Informtion type saved test data
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage validateAccountInformationTypeSavedTestData(
			String data) throws Exception {
		GlobalController.brw.validateSavedTestData(
				this.TAB_ACCOUNT_TYPES_NAMES, data);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will validate Account type saved test data
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage validateAccountTypeSavedTestData(String data)
			throws Exception {
		GlobalController.brw.validateSavedTestData(
				this.TAB_ACCOUNT_TYPES_NAMES, data);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Click on added Account type
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage clickAddedAccountType(String accountType)
			throws Exception {
		GlobalController.brw.selectListItem(this.TAB_ACCOUNT_TYPES_LIST_NAMES,
				accountType);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Click on Meta Filed link
	 * 
	 * @throws Exception
	 */

	public ConfigurationPage ClickOnMetaFieldLink() throws Exception {
		GlobalController.brw.clickLinkText(this.METAFIELD_LINK);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Click on MetaFiled Bar
	 * 
	 * @throws Exception
	 */

	public ConfigurationPage ClickOnMetaFieldBar() throws Exception {
		GlobalController.brw.clickLinkText(this.METAFIELD_BAR);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Click on Meta Field category
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage clickMetaFieldCategory(String categoryName)
			throws Exception {
		GlobalController.brw.selectListItem(this.METAFIELD_CATEGORY,
				categoryName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will get validation Rules by default selected value
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String getMetaFieldValidartionRuleValue() throws Exception {
		String metaFiledValue = GlobalController.brw
				.getText(this.DD_MD_VALIDATION);
		return metaFiledValue;
	}

	/**
	 * This method will validate validation rule should be blank
	 * 
	 * @param testDataSetName
	 * @param category
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage validateValidationRules() throws Exception {
		GlobalController.brw.getDropDownOptionIsSelected(this.DD_MD_VALIDATION,
				"--");
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public String addAccountType(String testDataSetName, String category)
			throws Exception {
		String accountTypeName = TestData.read("PageConfiguration.xml",
				testDataSetName, "AccountTypeName", category);
		this.clickOnAccountTypeLink();
		this.clickAddNewButton();
		this.setAccountTypeName(accountTypeName);
		this.clickSaveChangesButton();
		this.verifySavedAccountType();
		return accountTypeName;

	}

	public String addACHAccountType(String testDataSetName, String category,
			String TC_115_PAYMENT_METHOD_ACH) throws Exception {
		String accountTypeName = TestData.read("PageConfiguration.xml",
				testDataSetName, "accountTypeName", category);
		this.clickAddNewButton();
		this.setAccountTypeName(accountTypeName);
		this.selectPaymentMethodTypes(TC_115_PAYMENT_METHOD_ACH);
		this.clickSaveChangesButton();
		this.verifySavedAccountType();
		return accountTypeName;
	}

	public void activateMetaFieldEditor() throws Exception {
		String cssClass = GlobalController.brw.getAttribute(
				this.LI_METAFIELDTAB_ACTIVATOR, "class");
		if (!cssClass.contains("active")) {
			GlobalController.brw.clickLinkText(this.SPAN_NEW_METAFIELDTAB);
		}
	}

	public String addAITMetaFieldToAccountType(String testDataSetName,
			String category, String accountType) throws Exception {

		String accountName = TestData.read("PageConfiguration.xml",
				testDataSetName, "AccountName", category);
		String nameEmail = TestData.read("PageConfiguration.xml",
				testDataSetName, "NameEmail", category);
		String metaFieldSelection = TestData.read("PageConfiguration.xml",
				testDataSetName, "MetafieldSelection", category);
		this.clickAddedAccountType(accountType);
		this.clickOnAddInformationType();
		this.setAccountTypeInformationTypeName(accountName);
		this.clickAddNewMetaField();
		this.selectMetaFieldType(metaFieldSelection);
		this.setAccountName(nameEmail);
		this.clickUpdateButton();
		this.clickSaveChangesButton();
		this.verifySavedAccountType();
		return accountName;
	}

	public ConfigurationPage validateMetaFieldSelection() throws Exception {
		this.ClickOnMetaFieldLink();
		this.clickOnCustomerInMetaField();
		this.clickAddNewButton();
		this.validateValidationRules();

		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	public String addrecurringPaymentMethodDetails(String testDataSetName,
			String category) throws Exception {
		String methodName = TestData.read("PageConfiguration.xml",
				testDataSetName, "methodName", category);
		String cardErrorMsg = TestData.read("PageConfiguration.xml",
				testDataSetName, "cardErrorMsg", category);
		String dateErrorMsg = TestData.read("PageConfiguration.xml",
				testDataSetName, "dateErrorMsg", category);

		this.setMethodName(methodName);
		GlobalController.brw.check(this.CHK_ISRECURRING);
		ConfigurationPage.logger
				.debug("Add CC Number Meta Field Error Message");
		this.createValRulOnCCNumberMF(cardErrorMsg);
		ConfigurationPage.logger
				.debug("Add Expiry Date Meta Field Error Message");
		this.createValRulOnExpiryDateMF(dateErrorMsg);
		this.clickSaveChangesButton();

		return methodName;
	}

	public String accounttype(String testDataSetName, String category,
			String method_types) throws Exception {

		String accountTypeName = TestData.read("PageConfiguration.xml",
				testDataSetName, "accountTypeName", category);
		this.clickAddNewButton();
		this.setAccountTypeName(accountTypeName);
		this.selectPaymentMethodTypes(method_types);
		GlobalController.brw.click(this.LT_MD_SAVECHANGES);
		return accountTypeName;

	}

	public ConfigurationPage SelectPaymentMethodTemplate(
			String testDataSetName, String category) throws Exception {
		String paymentTemplate = TestData.read("PageConfiguration.xml",
				testDataSetName, "paymentTemplate", category);

		this.clickAddNewButton();
		this.selectPaymentTemplate(paymentTemplate);
		this.clickSelectButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will validate Validation Rule drop down is available
	 * 
	 * @param testDataSetName
	 * @param category
	 * @return
	 * @throws Exception
	 */
	public void validateValidationRuleDropDown() throws Exception {
		if (GlobalController.brw.isElementPresent(this.DD_VALIDATION_RULE)) {
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(false);
		}

	}

	/**
	 * This method will get all Values from Drop down
	 * 
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage verifyvalueFromDropDown(
			String metfieldAccountName) throws Exception {
		GlobalController.brw.getDropDownOptionIsPresent(
				this.DD_INCLUDE_NOTIFICATION, metfieldAccountName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Enter shipping address name
	 * 
	 * @param secondRowTestData
	 * @throws Exception
	 */

	/**
	 * This method will Enter shipping address name
	 * 
	 * @param secondRowTestData
	 * @throws Exception
	 */
	ConfigurationPage setAccountNameOnSecondForm(String OrderTestData)
			throws Exception {
		GlobalController.brw.setText(this.TB_METAFIELD_NAME2, OrderTestData);
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	/**
	 * This method will select option from MetaField Drop down
	 * 
	 * @param billingPeriod
	 * @return
	 * @throws Exception
	 */
	public ConfigurationPage selectMetaFieldType(String metaFieldType)
			throws Exception {
		GlobalController.brw.selectDropDown(this.DD_METAFIELD_TYPE,
				metaFieldType);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will select option from MetaField Drop down
	 * 
	 * @param billingPeriod
	 * @return
	 * @throws Exception
	 */
	public ConfigurationPage selectMetaFieldTypeOnSecondForm(
			String metaFieldType) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_METAFIELD_TYPE2,
				metaFieldType);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Verify Created Account Type
	 * 
	 * @return
	 * @throws Exception
	 */
	ConfigurationPage verifySavedAccountType() throws Exception {
		MessagesPage.isIntermediateSuccessMessageAppeared();
		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	/**
	 * This method will Click on MetaFiled Bar
	 * 
	 * @throws Exception
	 */

	public ConfigurationPage ClickOnMetaFieldBaronSecond() throws Exception {
		GlobalController.brw.clickLinkText(this.METAFIELD_BAR2);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Update meta field information
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickUpdateButtonOnSecondForm()
			throws Exception {
		GlobalController.brw.clickLinkText(this.LT_UPDATE_METAFIELD2);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will verify Error Message for Second Order for One Per Order
	 * Plan
	 * 
	 * @throws Exception
	 */
	ConfigurationPage verifySuccessfulErrorMessage() throws Exception {
		MessagesPage.isErrorMessageAppeared();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage verifyVelidityRulesDropDown(
			String testDataSetName, String category, String accountType)
			throws Exception {
		String accountName = TestData.read("PageConfiguration.xml",
				testDataSetName, "AccountName", category);
		this.clickOnAccountTypeLink();
		this.clickAddedAccountType(accountType);
		this.clickOnAddInformationType();
		this.setAccountTypeInformationTypeName(accountName);
		this.clickAddNewMetaField();
		this.validateValidationRuleDropDown();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage verifyAddedMetafieldIsDisplayingIncludeNotificationDropDown(
			String accountType, String metfieldAccountName) throws Exception {
		this.clickOnAccountTypeLink();
		this.clickAddedAccountType(accountType);
		this.clickEditButton();
		this.verifyvalueFromDropDown(metfieldAccountName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage verifyUsedMetafieldTypeIsDisplayedInDD(
			String testDataSetName, String category, String accountType)
			throws Exception {
		String nameEmail = TestData.read("PageConfiguration.xml",
				testDataSetName, "NameEmail", category);
		String metaFieldSelection = TestData.read("PageConfiguration.xml",
				testDataSetName, "MetafieldSelection", category);
		// this.clickAccountTypeLink();
		// this.clickAddedAccountType(accountType);
		// this.clickOnMetafieldID();
		// this.clickOnMetafieldEditButton();
		this.clickEditButton();
		this.clickAddNewMetaField();
		this.selectMetaFieldTypeOnSecondForm(metaFieldSelection);
		// this.ClickOnMetaFieldBaronSecond();
		this.setAccountNameOnSecondForm(nameEmail);
		this.selectMetaFieldTypeOnSecondForm(metaFieldSelection);
		this.clickUpdateButtonOnSecondForm();
		this.clickSaveChangesButton();
		this.verifySuccessfulErrorMessage();

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage validateMetaFieldSelectionForCustomer()
			throws Exception {
		this.ClickOnMetaFieldLink();
		this.clickOnCustomerInMetaField();
		this.clickAddNewButton();
		// String metaFieldvalue = this.getMetaFieldValidartionRuleValue();
		this.validateValidationRules();

		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	public ConfigurationPage validateMetaFieldSelectionForOrder()
			throws Exception {
		this.ClickOnMetaFieldLink();
		this.clickOnPaymentInMetaField();
		this.clickAddNewButton();
		// String metaFieldvalue = this.getMetaFieldValidartionRuleValue();
		this.validateValidationRules();

		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	public ConfigurationPage validateMetaFieldSelectionForPayment()
			throws Exception {
		this.ClickOnMetaFieldLink();
		this.clickOnOrderInMetaField();
		this.clickAddNewButton();
		// String metaFieldvalue = this.getMetaFieldValidartionRuleValue();
		this.validateValidationRules();

		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	protected ConfigurationPage clickOnPaymentInMetaField() throws Exception {
		GlobalController.brw.clickLinkText(this.SELECT_PAYMENT_IN_META_FIELD);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage clickOnOrderInMetaField() throws Exception {
		GlobalController.brw.clickLinkText(this.SELECT_ORDER_IN_META_FIELD);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage verifyUserCannotSelectBothPaymentMethod(
			String accountType, String paymentAddress1, String paymentAddress2)
			throws Exception {
		this.clickOnAccountTypeLink();
		this.clickAddedAccountType(accountType);
		this.clickEditButton();
		this.selectPaymentAddress(paymentAddress1);
		this.clickConfirmPopupYesButton();
		this.selectPaymentAddress(paymentAddress2);
		this.clickConfirmPopupYesButton();
		this.verifySelectedPaymentAddressOption(paymentAddress2);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will select option from Include In Notification DropDown
	 * 
	 * @throws Exception
	 */
	ConfigurationPage selectPaymentAddress(String paymentAddress)
			throws Exception {
		GlobalController.brw.selectDropDown(this.DD_INCLUDE_NOTIFICATION,
				paymentAddress);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will verify selected option from Include In Notification
	 * DropDown
	 * 
	 * @throws Exception
	 */
	ConfigurationPage verifySelectedPaymentAddressOption(String paymentAddress)
			throws Exception {
		GlobalController.brw.getDropDownOptionIsSelected(
				this.DD_INCLUDE_NOTIFICATION, paymentAddress);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage createCloneAccountType(String testDataSetName,
			String category, String accountTypeName) throws Exception {
		String cloneAccountName = TestData.read("PageConfiguration.xml",
				testDataSetName, "cloneAccountName", category);
		this.clickOnAccountTypeLink();
		this.selectAccountTypeName(accountTypeName);
		this.clickOnCloneButton();
		this.clickSaveChangesButton();
		this.verifySuccessfulErrorMessage();
		this.setAccountTypeName(cloneAccountName);
		this.clickSaveChangesButton();
		this.verifySavedAccountType();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public String addAccountTypeWithInvoiceDesign(String testDataSetName,
			String category) throws Exception {
		String accountTypeName = TestData.read("PageConfiguration.xml",
				testDataSetName, "AccountTypeName", category);
		String InvoiceDesign = TestData.read("PageConfiguration.xml",
				testDataSetName, "InvoiceDesign", category);

		this.clickOnAccountTypeLink();
		this.clickAddNewButton();
		this.setAccountTypeName(accountTypeName);
		this.setAccountTypeInvoiceDesign(InvoiceDesign);
		this.clickSaveChangesButton();
		this.verifySavedAccountType();
		return accountTypeName;

	}

	public ConfigurationPage reUpdatePreference(String testDataSetName,
			String category) throws Exception {
		String preference = TestData.read("PageConfiguration.xml",
				testDataSetName, "preference", category);
		String preferenceVal = TestData.read("PageConfiguration.xml",
				testDataSetName, "preferenceValue", category);

		this.reUpdateReference63();
		this.setConfigurationalPreferenceValue(preferenceVal);
		this.clickSaveChangesButton();
		this.verifyUpdatedPreference();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set value in description of preference 63 table saved
	 * test data
	 * 
	 * @throws Exception
	 */
	public ConfigurationPage reUpdateReference63() throws Exception {
		GlobalController.brw.click(this.TN_RECONFIGURE);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage adddNewOrderStatus(String testDataSetName,
			String category, boolean appliedCheckBox) throws Exception {
		String OrderName = TestData.read("PageConfiguration.xml",
				testDataSetName, "OrderName", category);
		String OrderQauntity = TestData.read("PageConfiguration.xml",
				testDataSetName, "OrderQauntity", category);
		this.clickOnOrderChangeStatuses();
		this.setOrderStatusName(OrderName);
		this.setOrderStatusOrder(OrderQauntity);
		this.checkApplieOrderdCheckBox(appliedCheckBox);
		this.clickOnSaveChanges();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click Order Change Status
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickOnOrderChangeStatuses() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_CHANGEORDER_STATUSES);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click Order Change Status
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickOnOrderChangeType() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_CHANGEORDER_TYPE);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will put value in Order Name on Order Status Change page
	 * 
	 * @param secondRowTestData
	 * @throws Exception
	 */
	public void setOrderStatusName(String OrderName) throws Exception {
		// fill ORDER
		GlobalController.brw.setText(this.TB_ORDERSTATUS_ORDERNAME, OrderName);

	}

	/**
	 * This method will put value in Order on Order Status Change page
	 * 
	 * @param secondRowTestData
	 * @throws Exception
	 */
	public void setOrderStatusOrder(String OrderQauntity) throws Exception {
		// fill ORDER
		GlobalController.brw.setText(this.TB_ORDERSTATUS_ORDERNUMBER,
				OrderQauntity);

	}

	/**
	 * Check Order Check box
	 * 
	 * @param appliedCheckbox
	 * @throws Exception
	 */
	protected ConfigurationPage checkApplieOrderdCheckBox(
			boolean appliedCheckBox) throws Exception {
		GlobalController.brw.check(this.CHK_APPLIEDORDER);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will click Save Changes
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickOnSaveChanges() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_SAVECHANGES);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public String getIDAddedCategory(String categoryName) throws Exception {
		this.clickEdit();
		String id = this.getCategoryID();

		this.clickEditSaveChanges();
		System.out.println("id>>>>>>" + id);
		System.out.println("text::::::" + categoryName);
		String product = categoryName.concat(" (Id: ");
		String productName1 = product.concat(id);
		String category = productName1.concat(")");
		System.out.println("productName" + category);
		return category;

	}

	/**
	 * This method will click on Edit button
	 * 
	 * @throws Exception
	 */
	ProductsPage clickEdit() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_Edit);
		return GlobalController.brw.initElements(ProductsPage.class);

	}

	public String getCategoryID() throws Exception {
		String text = GlobalController.brw.getText(this.PRODUCT_ID);
		return text;

	}

	/**
	 * This method will click Save Changes button After Editing Any Product
	 * 
	 * @throws Exception
	 */
	ProductsPage clickEditSaveChanges() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_EDIT_SAVECHANGES);
		return GlobalController.brw.initElements(ProductsPage.class);
	}

	public ConfigurationPage configureOrderChangeTypeWithMulitpleProduct(
			String testdataset, String category, boolean allowOrder,
			String selectProduct1, String selectProduct2) throws Exception {
		String orderChangeName = TestData.read("PageConfiguration.xml",
				testdataset, "OrderName", category);
		this.clickOnOrderChangeType();
		this.clickAddNewButton();
		System.out.println("orderChangeName" + orderChangeName);
		this.setOrderChangeName(orderChangeName);
		this.allowOrderStatusChangeCheckbox(allowOrder);
		System.out.println("selectProduct1" + selectProduct1);
		System.out.println("selectProduct2" + selectProduct2);
		this.selectProductCategory_OrderChangeType(selectProduct1);
		GlobalController.brw.pressControlKey();
		this.selectProductCategory_OrderChangeType(selectProduct2);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Invoice Name
	 * 
	 * @param invoiceName
	 * @throws Exception
	 */
	protected ConfigurationPage setOrderChangeName(String orderChangeName)
			throws Exception {
		GlobalController.brw.setText(this.TB_ORDERENGLISHNAME, orderChangeName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will check/uncheck View Plugin checkbox
	 * 
	 * @param viewPlugin
	 * @return
	 * @throws Exception
	 */
	protected ConfigurationPage allowOrderStatusChangeCheckbox(
			Boolean allowOrder) throws Exception {
		if (allowOrder) {
			GlobalController.brw.check(this.CB_ALLOWORDER);
		} else {
			GlobalController.brw.uncheck(this.CB_ALLOWORDER);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage selectProductCategory_OrderChangeType(
			String selectProduct) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_SELECTPRODUCTCATEGORY,
				selectProduct);

		return GlobalController.brw.initElements(ConfigurationPage.class);

	}

	public ConfigurationPage configureOrderChangeTypeWithoutAllowStatusChange(
			String testdataset, String category, String selectProduct1,
			String selectProduct2) throws Exception {
		String orderChangeName = TestData.read("PageConfiguration.xml",
				testdataset, "OrderName", category);
		this.clickOnOrderChangeType();
		this.clickAddNewButton();
		this.setOrderChangeName(orderChangeName);
		System.out.println("selectProduct1" + selectProduct1);
		System.out.println("selectProduct2" + selectProduct2);
		this.selectProductCategory_OrderChangeType(selectProduct1);
		GlobalController.brw.pressControlKey();
		this.selectProductCategory_OrderChangeType(selectProduct2);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Click on order period
	 * 
	 * @throws Exception
	 */

	public ConfigurationPage ClickOnOrderPeriod() throws Exception {
		GlobalController.brw.clickLinkText(this.ORDER_PERIOD_LINK);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public String createOrderPeriod(String testDataSetName, String category)
			throws Exception {
		String description = TestData.read("PageConfiguration.xml",
				testDataSetName, "description", category);
		String value = TestData.read("PageConfiguration.xml", testDataSetName,
				"value", category);
		String billingPeriod = TestData.read("PageConfiguration.xml",
				testDataSetName, "billingPeriod", category);

		this.ClickOnOrderPeriod();
		this.clickAddNewButton();
		this.setOrderPeriodDescription(description);
		this.selectBillingProcessPeriod(billingPeriod);
		this.setOrderPeriodValue(value);
		this.clickSaveChangesButton();
		return description;
	}

	public String getDefaultCurrencyValue() throws Exception {
		String currentCurrency = GlobalController.brw
				.getDropDownSelectedValue(this.DD_DEFAULT_CURRENCY);
		return currentCurrency;
	}

	protected ConfigurationPage selectEuroCurrenyCheckBox(boolean currencyName)
			throws Exception {
		if (currencyName) {
			GlobalController.brw.check(this.CB_EURO_CURRENCY);
		} else {
			GlobalController.brw.uncheck(this.CB_EURO_CURRENCY);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setDefaultCurrencyValue(String targetValue)
			throws Exception {
		GlobalController.brw.selectDropDown(this.DD_DEFAULT_CURRENCY,
				targetValue);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage setCurrency(String testDataSetName, String category)
			throws Exception {
		String currencyName = TestData.read("PageConfiguration.xml",
				testDataSetName, "currencyName", category);
		boolean CurrencyNameCheckBox = TextUtilities.compareValue(TestData
				.read("PageConfiguration.xml", testDataSetName,
						"EnableCurrencyName", category), "true", true,
				TextComparators.equals);
		this.selectEuroCurrenyCheckBox(CurrencyNameCheckBox);
		this.clickSaveChangesButton();
		this.setDefaultCurrencyValue(currencyName);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will Click on Currency link
	 * 
	 * @throws Exception
	 */

	public ConfigurationPage ClickOnCurrencyLink() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_CURRENCY);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage resetCurrecncy(String testDataSetName,
			String category, String lastCurrencyName) throws Exception {
		boolean CurrencyNameCheckBox = TextUtilities.compareValue(TestData
				.read("PageConfiguration.xml", testDataSetName,
						"DisableCurrencyName", category), "true", true,
				TextComparators.equals);
		// this.selectEuroCurrenyCheckBox(CurrencyNameCheckBox);
		// this.clickSaveChangesButton();
		this.setDefaultCurrencyValue(lastCurrencyName);
		this.clickSaveChangesButton();
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage clickOnMetafieldID() throws Exception {
		GlobalController.brw.clickLinkText(this.METAFIELD_ID);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage clickEditAIT() throws Exception {
		GlobalController.brw.clickLinkText(this.bttn_Edit_AITType);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage clickUserNotification(boolean checkbox)
			throws Exception {
		GlobalController.brw.check(this.chk_UserNotification_AccntType);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage popupyesait() throws Exception {
		GlobalController.brw.click(this.bttn_yes_popup_ait);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public ConfigurationPage verufydropdownIncludeinNotifications(String value)
			throws Exception {
		GlobalController.brw.isValuePresentInDropDown(
				this.drp_Include_in_Notifications, value);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}
}
