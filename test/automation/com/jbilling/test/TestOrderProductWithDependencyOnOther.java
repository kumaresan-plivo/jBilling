package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestOrderProductWithDependencyOnOther extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 15.2 :  Verify that Products with dependencies on other products can be ordered.")
	public void testOrderProductWithDependencyOnOther() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909926");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customers Page");

		this.customerPage = this.customerPage.selectCustomer(this.pr.readTestData("TC_6.2_CHILD_CUSTOMER_NAME"));
		this.customerPage = this.customerPage.clickCreateOrder();
		logger.debug("Select Customer And Click Create Order");

		this.ordersPage = this.customerPage.orderProductHavingDependency("createOrderSecond", "co");
		logger.debug("Order The Product Having Dependency");

		this.ordersPage = this.ordersPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
