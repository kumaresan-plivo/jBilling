package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

public class VerifyCreateOrderDifferentCurrency extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	String graceId = null;
	ITestResult result;
	String ProductID = "null";
	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	/**
	 * TC_361 Verify correct currency conversion is made while creating order
	 * with plan
	 **/

	@Test(description = "TC_361  :  Verify correct currency conversion is made while creating order with plan.")
	public void TC_361CreateOrder() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909926");

		String customername = this.propReader.readPropertyFromFile("TC_356_CustomerName", "testData");
		String planname = this.propReader.readPropertyFromFile("TC_360_planDescription", "testData");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customers Page");

		this.customerPage = this.customerPage.selectCustomerRecent(customername);
		logger.debug("Select Customer");

		this.customerPage.clickCreateOrder();
		logger.debug("Click Create Order button");

		this.customerPage.createOrderwithdifferentCurrency(planname, "OrderWithDifferentPeriodsCurr", "curr4");
		logger.debug("Add Plan");

		this.msgsPage.verifyDisplayedMessageText("Created new order", "successfully", TextComparators.contains);
		logger.debug("Verify Success message");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();

	}
}