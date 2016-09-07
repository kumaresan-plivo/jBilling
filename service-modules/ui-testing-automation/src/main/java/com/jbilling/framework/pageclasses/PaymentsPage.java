package com.jbilling.framework.pageclasses;

import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PaymentMethodField;
import com.jbilling.framework.utilities.xmlutils.TestData;

public class PaymentsPage {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='refund_cb']")
	private ElementField CHK_RefundPayment;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='payment.currencyId']")
	private ElementField Dropdown_Currency;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//a[@onclick='addPaymentInstrument()']")
	private ElementField LT_AddInstrument;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//a[@class='submit cancel']")
	private ElementField LT_Cancel;

	@LocateBy(xpath = "//button/span[text()='Yes']")
	private ElementField LT_CONFIRM_YES;

	@LocateBy(xpath = "//span[text()='Delete']")
	private ElementField LT_DELETE;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Make Payment']")
	private ElementField LT_MakePayment;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='processNow']")
	private ElementField LT_ProcesPayment;

	@LocateBy(xpath = "//span[text()='Review Payment']")
	private ElementField LT_ReviewPayment;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='heading']/strong")
	private ElementField PageHeader_NewPayment;

	@LocateBy(xpath = "//table[@id='orders']//strong[contains(text(),'customera')]")
	private ElementField TAB_ORDER;

	@LocateBy(xpath = "//table[@id='payments']/tbody//strong[contains(text(),'Cutomer_B')]")
	private ElementField TAB_PAYMENT;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//label[text()='cc.cardholder.name']/following::div/input")
	private ElementField TB_CardholderName;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//label[text()='cc.number']/following::div/input")
	private ElementField TB_CCNumber;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//label[text()='cc.expiry.date']/following::div/input")
	private ElementField TB_ExpiryDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='payment_amountAsDecimal']")
	private ElementField TB_InputID;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='payment.paymentDate']")
	private ElementField TB_PaymentDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='paymentMethod_0.paymentMethodTypeId']")
	private ElementField TB_PaymentMethod;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//a[@id='payment.paymentNotes']")
	private ElementField TB_PaymentNote;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='paymentMethod_0.processingOrder']")
	private ElementField TB_ProcessingOrder;

	@LocateBy(xpath = "//table[@id='payments']/tbody/tr/td/a/strong")
	private ElementField TAB_LIST_CUSTOMER_NAME;

	@LocateBy(id = "payment_amountAsDecimal")
	private ElementField TB_PAYMENT_AMOUNT;

	@LocateBy(xpath = "//label[text()='cheque.bank.name']/../../div[1]/div/input")
	private ElementField TB_CHEQUE_NAME;

	@LocateBy(xpath = "//label[text()='cheque.bank.name']/../../div[2]/div/input")
	private ElementField TB_CHEQUE_NUMBER;

	@LocateBy(xpath = "//label[text()='cheque.bank.name']/../../div[3]/div/input")
	private ElementField TB_CHEQUE_DATE;

	@LocateBy(xpath = "//a[@class='submit payment']/span")
	private ElementField LT_REVIEW_PAYMENT;

	@LocateBy(xpath = "//label[text()='Processing Order']/../div/input")
	private ElementField TB_PAYMENT_ORDER;

	@LocateBy(xpath = "//table[@id='payments']")
	private ElementField TAB_PAYMENTS;

	@LocateBy(xpath = "//select[@id='paymentMethod_0.paymentMethodTypeId']")
	private ElementField DD_PAYMENTMETHOD;

	@LocateBy(xpath = "//span[text()='Add New']")
	private ElementField LT_ADD_NEW_BUTTON;

	@LocateBy(xpath = "//select[@id='templateId']")
	private ElementField DD_PAYMENTTEMPLATE;

	@LocateBy(xpath = "//div[@class = 'buttons']//a[contains(@onclick,'submit')]")
	private ElementField BT_SELECT;

	@LocateBy(id = "methodName")
	private ElementField TB_METHODNAME;

	@LocateBy(id = "allAccount")
	private ElementField CHK_ALLACCOUNTTYPES;

	@LocateBy(id = "isRecurring")
	private ElementField CHK_ISRECURRING;

	@LocateBy(xpath = "//span[text()='Save Changes']")
	private ElementField LT_SAVE_CHANGES;

	/**
	 * This method will click on Delete Button
	 * 
	 * @throws Exception
	 */
	PaymentsPage clickConfirmYes() throws Exception {
		PaymentsPage.logger.enterMethod();
		GlobalController.brw.clickLinkText(this.LT_CONFIRM_YES);

		PaymentsPage.logger.exitMethod();
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

	/**
	 * This method will click on Delete Button
	 * 
	 * @throws Exception
	 */
	PaymentsPage clickDelete() throws Exception {
		PaymentsPage.logger.enterMethod();

		GlobalController.brw.clickLinkText(this.LT_DELETE);

		PaymentsPage.logger.exitMethod();
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

	public OrdersPage deleteOrders() throws Exception {
		this.selectSuccessfulOrders();
		this.clickDelete();
		this.clickConfirmYes();
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	public PaymentsPage deletePayment() throws Exception {
		this.selectSuccessfulPayment();
		this.clickDelete();
		// this.clickConfirmYes();
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

	/**
	 * This method will click on customer
	 * 
	 * @throws Exception
	 */
	public PaymentsPage selectcustomer(String user) throws Exception {
		GlobalController.brw.selectListItem(this.TAB_LIST_CUSTOMER_NAME, user);
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

	public boolean isReviewPaymentButtonAppeared() throws Exception {
		PaymentsPage.logger.enterMethod();
		boolean isLoaded = GlobalController.brw.isElementPresent(this.LT_ReviewPayment);
		PaymentsPage.logger.exitMethod();
		return isLoaded;
	}

	/**
	 * This method will select Successful payment
	 */
	OrdersPage selectSuccessfulOrders() throws Exception {
		GlobalController.brw.clickLinkText(this.TAB_ORDER);
		return GlobalController.brw.initElements(OrdersPage.class);
	}

	/**
	 * This method will select Successful payment
	 */
	PaymentsPage selectSuccessfulPayment() throws Exception {
		GlobalController.brw.clickLinkText(this.TAB_LIST_CUSTOMER_NAME);
		return GlobalController.brw.initElements(PaymentsPage.class);
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
	protected PaymentsPage setPaymentAmount(String paymentAmount) throws Exception {
		GlobalController.brw.setText(this.TB_PAYMENT_AMOUNT, paymentAmount);
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

	/**
	 * This method will select Payment Type Dropdown
	 * 
	 * @param paymentMethod
	 * @throws Exception
	 */
	protected PaymentsPage selectPaymentType(String paymentMethod) throws Exception {
		PaymentsPage.logger.enterMethod();

		GlobalController.brw.selectDropDown(this.DD_PAYMENTMETHOD, paymentMethod);
		PaymentsPage.logger.exitMethod();
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

	/**
	 * This method will set Cheque Holder name for payment
	 * 
	 * @param cardHolderName
	 * @throws Exception
	 */
	protected PaymentsPage ChequeHolderName(String chequeHolderName) throws Exception {
		PaymentsPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_CHEQUE_NAME, chequeHolderName);
		PaymentsPage.logger.exitMethod();
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

	/**
	 * This method will set Cheque Number for Payment
	 * 
	 * @param cardNumber
	 * @throws Exception
	 */
	protected PaymentsPage ChequeNumber(String chequeNumber) throws Exception {
		PaymentsPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_CHEQUE_NUMBER, chequeNumber);
		PaymentsPage.logger.exitMethod();
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

	/**
	 * This method will set Cheque Number for Payment
	 * 
	 * @param cardNumber
	 * @throws Exception
	 */
	protected PaymentsPage ChequeDate(String chequeDate) throws Exception {
		PaymentsPage.logger.enterMethod();

		GlobalController.brw.setText(this.TB_CHEQUE_DATE, chequeDate);
		PaymentsPage.logger.exitMethod();
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

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
	 * This method will select Payment Template
	 * 
	 * @param paymentCard
	 * @throws Exception
	 */
	protected ConfigurationPage selectPaymentTemplate(String paymentCard) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PAYMENTTEMPLATE, paymentCard);
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
	 * This method will set method name
	 * 
	 * @param methodName
	 * @throws Exception
	 */
	protected ConfigurationPage setMethodName(String methodName) throws Exception {
		GlobalController.brw.setText(this.TB_METHODNAME, methodName);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Check All Account Type checkbox
	 * 
	 * @param allAccountTypes
	 * @throws Exception
	 */
	protected ConfigurationPage checkAllAccountTypes(boolean allAccountTypes) throws Exception {
		if (allAccountTypes) {
			GlobalController.brw.check(this.CHK_ALLACCOUNTTYPES);
		} else {
			GlobalController.brw.uncheck(this.CHK_ALLACCOUNTTYPES);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * Check/uncheck Is Recurring check box
	 * 
	 * @param isRecurring
	 * @throws Exception
	 */
	protected ConfigurationPage checkIsRecurring(boolean isRecurring) throws Exception {
		if (isRecurring) {
			GlobalController.brw.check(this.CHK_ISRECURRING);
		} else {
			GlobalController.brw.uncheck(this.CHK_ISRECURRING);
		}
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

	protected PaymentsPage clickReviewPayment() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_REVIEW_PAYMENT);
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

	protected PaymentsPage setPaymentOrder(String PaymentOrder) throws Exception {
		GlobalController.brw.setText(this.TB_PAYMENT_ORDER, PaymentOrder);
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

	public PaymentsPage MakePayment(String testDataSetName, String category, String PaymentMethod) throws Exception {
		String chequeHolderName = TestData.read("PaymentsPage.xml", testDataSetName, "chequeHolderName", category);
		String chequeNumber = TestData.read("PaymentsPage.xml", testDataSetName, "chequeNumber", category);
		String chequeDate = TestData.read("PaymentsPage.xml", testDataSetName, "chequeDate", category);
		String paymentAmount = TestData.read("PaymentsPage.xml", testDataSetName, "paymentAmount", category);
		String PaymentOrder = TestData.read("PaymentsPage.xml", testDataSetName, "PaymentOrder", category);

		this.setPaymentAmount(paymentAmount);
		this.selectPaymentType(PaymentMethod);
		this.ChequeHolderName(chequeHolderName);
		this.ChequeNumber(chequeNumber);
		this.ChequeDate(chequeDate);
		this.setPaymentOrder(PaymentOrder);
		this.clickReviewPayment();
		this.clickReviewPayment();
		return GlobalController.brw.initElements(PaymentsPage.class);
	}

	public String addPaymentMethodWithoutMetaFields(PaymentMethodField paymentMethodFields, String testDataSetName, String category)
			throws Exception {
		String paymentTemplate = TestData.read("PaymentsPage.xml", testDataSetName, "paymentTemplate", category);
		String methodName = TestData.read("PaymentsPage.xml", testDataSetName, "methodName", category);
		boolean allAccountTypes = TestData.read("PaymentsPage.xml", testDataSetName, "allAccountTypes", category).equals("true");
		boolean isRecurring = TestData.read("PaymentsPage.xml", testDataSetName, "isRecurring", category).equals("true");

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
			throw new Exception("Invalid Step Provided. Not defined in enumeration");

		}
		this.clickSaveChangesButton();
		return methodName;
	}

}
