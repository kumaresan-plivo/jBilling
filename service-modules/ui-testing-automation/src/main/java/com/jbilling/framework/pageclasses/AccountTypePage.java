package com.jbilling.framework.pageclasses;

import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.utilities.xmlutils.TestData;

public class AccountTypePage {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@LocateBy(xpath = "//span[text()='Add New']")
	private ElementField LT_ADD_NEW_BUTTON;

	@LocateBy(xpath = "//input[@id='description']")
	private ElementField TB_ACCOUNT_TYPE_NAME;

	@LocateBy(xpath = "//input[@id='preference.value']")
	private ElementField TB_CONFIGURATION_PREFERENCE_VALUE;

	@LocateBy(xpath = "//span[text()='Save Changes']")
	private ElementField LT_SAVE_CHANGES;

	@LocateBy(xpath = "//input[@id='mainSubscription.nextInvoiceDayOfPeriod']")
	private ElementField TB_ACCOUNT_TYPE_BILLING_CYCLE;

	@LocateBy(xpath = "//input[@id='invoiceDesign']")
	private ElementField TB_ACCOUNT_TYPE_INVOICE_DESIGN;

	@LocateBy(id = "creditLimitAsDecimal")
	private ElementField TB_CREDIT_LIMIT;

	@LocateBy(id = "creditNotificationLimit1AsDecimal")
	private ElementField TB_CREDIT_LIMIT_NOT_ONE;

	@LocateBy(id = "creditNotificationLimit2AsDecimal")
	private ElementField TB_CREDIT_LIMIT_NOT_TWO;

	@LocateBy(id = "payment-method-select")
	private ElementField DD_PAYMENT_METHOD_TYPE;

	/**
	 * This method will click on Add new button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickAddNewButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADD_NEW_BUTTON);

		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will will set Account Name
	 * 
	 * @param accountName
	 * @throws Exception
	 */
	protected ConfigurationPage setAccountTypeName(String accountName) throws Exception {
		GlobalController.brw.setText(this.TB_ACCOUNT_TYPE_NAME, accountName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Click Save Changes button
	 * 
	 * @throws Exception
	 */
	protected ConfigurationPage clickSaveChangesButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_SAVE_CHANGES);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Billing Cycle
	 * 
	 * @param billingCycle
	 * @throws Exception
	 */
	protected ConfigurationPage setAccountTypeBillingCycle(String billingCycle) throws Exception {
		GlobalController.brw.setText(this.TB_ACCOUNT_TYPE_BILLING_CYCLE, billingCycle);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will set Invoice Design
	 * 
	 * @param invoiceDesign
	 * @throws Exception
	 */
	protected ConfigurationPage setAccountTypeInvoiceDesign(String invoiceDesign) throws Exception {
		GlobalController.brw.setText(this.TB_ACCOUNT_TYPE_INVOICE_DESIGN, invoiceDesign);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setCreditLimitForAccountType(String creditLimit) throws Exception {
		GlobalController.brw.setText(this.TB_CREDIT_LIMIT, creditLimit);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setCreditLimitNotificationOneForAccountType(String creditLimitOne) throws Exception {
		GlobalController.brw.setText(this.TB_CREDIT_LIMIT_NOT_ONE, creditLimitOne);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage setCreditLimitNotificationTwoForAccountType(String creditLimitTwo) throws Exception {
		GlobalController.brw.setText(this.TB_CREDIT_LIMIT_NOT_TWO, creditLimitTwo);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	protected ConfigurationPage selectPaymentMethodTypes(String paymentMethod) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PAYMENT_METHOD_TYPE, paymentMethod);
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
	public String createAccountTypeWithCreditDetails(String testDataSetName, String category, String paymentMethodNameOne) throws Exception {
		String accountName = TestData.read("AccountType.xml", testDataSetName, "accountName", category);
		String billingCycle = TestData.read("AccountType.xml", testDataSetName, "billingCycle", category);
		String invoiceDesign = TestData.read("AccountType.xml", testDataSetName, "invoiceDesign", category);
		String creditLimit = TestData.read("AccountType.xml", testDataSetName, "creditLimit", category);
		String creditLimitOne = TestData.read("AccountType.xml", testDataSetName, "creditLimitOne", category);
		String creditLimitTwo = TestData.read("AccountType.xml", testDataSetName, "creditLimitTwo", category);

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

}
