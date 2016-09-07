package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

public class VerifyUserAbleToSubscribeToTheFUPPlan extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	@Test(description = " TC 115: Verify user is able to subscribe to the FUP plan ", priority = 1)
	public void TC115_AddPaymentMethodForACH() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.PaymentMethod);
		logger.debug("navigate to Paymentmethod");

		String addACHPaymentMethod = this.confPage.addACHPaymentMethod("TC115_CreatePaymentMethodACH", "apm");
		this.runTimeVariables.put("TC_115_PAYMENT_METHOD_ACH", addACHPaymentMethod);
		logger.debug("Add Payment Method for ACH");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Payment Method Type ", "created successfully", TextComparators.contains);
		logger.debug("Verify Message For Created Payment type");

		this.confPage = this.confPage.validatePeriodsSavedTestData(addACHPaymentMethod);
		logger.debug("Validate Saved Payment Type Test Data");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}

	@Test(description = "TC 115: Verify user is able to subscribe to the FUP plan", dependsOnMethods = "TC115_AddPaymentMethodForACH", priority = 2)
	public void TC115_AddAccountType() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

        this.confPage = this.navPage.navigateToConfigurationPage();
        logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.AccountType);
		logger.debug("Select Payment Method from Configuration list");

		String TC_115_PAYMENT_METHOD_ACH = this.runTimeVariables.get("TC_115_PAYMENT_METHOD_ACH");
		String accountName = this.confPage.addACHAccountType("TC115_AccountTypeACH", "atach", TC_115_PAYMENT_METHOD_ACH);
		this.runTimeVariables.put("TC_115_ACCOUNT_NAME", accountName);
		logger.debug("Add Edit Delete Payment Method With Recurring and all account");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Account Type", "created successfully", TextComparators.contains);
		logger.debug("Verify Message For Account Type Created");

		this.confPage = this.confPage.validatePeriodsSavedTestData(accountName);
		logger.debug("Validate Saved Account Type Test Data");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}

	@Test(description = "TC 115: Verify user is able to subscribe to the FUP plan", dependsOnMethods = "TC115_AddAccountType", priority = 3)
	public void TC115_AddCustomer() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customer Page");

		String TC_115_PAYMENT_METHOD_ACH = this.runTimeVariables.get("TC_115_PAYMENT_METHOD_ACH");
		String TC_115_ACCOUNT_NAME = this.runTimeVariables.get("TC_115_ACCOUNT_NAME");

		String customerName = this.customerPage.addACHCustomerType(TC_115_ACCOUNT_NAME, TC_115_PAYMENT_METHOD_ACH,
				"TC115_ACH_Cusotmer_Type", "achct");
		this.runTimeVariables.put("TC_115_CUSTOMER_NAME", customerName);
		logger.debug("Add Edit Delete Payment Method With Recurring and all apyment");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new customer", "successfully", TextComparators.contains);
		logger.debug("Verify Message For New Customer Created");

		this.customerPage = this.customerPage.validateUsersSavedTestData(customerName);
		logger.debug("Validate Saved Customer Test Data");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}

	@Test(description = "TC 115: Verify user is able to subscribe to the FUP plan", dependsOnMethods = "TC115_AddCustomer", priority = 4)
	public void TC115_CreateOrder() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customer Page");

		String TC_115_CUSTOMER_NAME = this.runTimeVariables.get("TC_115_CUSTOMER_NAME");
		this.customerPage = this.customerPage.createOrderForFUPCustomer(TC_115_CUSTOMER_NAME, "TC115_Create_Order", "co");
		logger.debug("Create Customer order");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Created new order", "successfully", TextComparators.contains);
		logger.debug("Verify Message For order Created");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}

	@Test(description = "TC 115: Verify user is able to subscribe to the FUP plan", dependsOnMethods = "TC115_AddCustomer", priority = 4)

	public void TC115_EditOrder() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customer Page");

		String TC_115_CUSTOMER_NAME = this.runTimeVariables.get("TC_115_CUSTOMER_NAME");
		this.customerPage = this.customerPage.EditOrderForFUPCustomer(TC_115_CUSTOMER_NAME, "TC115_Edit_Order", "eco");
		logger.debug("Add Edit order");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
