package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class TestCreateOrderWithDifferentTypedandPeriods extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 11.1 :  Verify user is able to create orders belonging "
			+ "to different periods/type to be processed in billing process later.")
	public void testCreateOrderWithDifferentTypedandPeriods() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909918");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customers Page");

		this.customerPage.addCustomer(this.pr.readTestData("TC_2.1_ACCOUNT_NAME_ONE"),
				this.pr.readTestData("TC_2.1.1_METHOD_NAME_ONE"), "new_customer", "nc");
		logger.debug("Add a Customer");

		this.customerPage.addCustomer(this.pr.readTestData("TC_2.1_ACCOUNT_NAME_ONE"),
				this.pr.readTestData("TC_2.1.1_METHOD_NAME_ONE"), "new_customer", "nc");
		logger.debug("Add The Other Customer");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Products Tab");

		this.productsPage.addCategory("productCategoryWithDifferentPeriods", "pcat");
		logger.debug("Create A Product Category");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customers Page");

		this.customerPage = this.customerPage.selectCustomer(this.pr.readTestData("TC_6.2_CHILD_CUSTOMER_NAME"));
		this.customerPage = this.customerPage.clickCreateOrder();

		this.ordersPage = this.customerPage.createOrderForCustomer("OrderWithDifferentPeriods", "co");
		logger.debug("Create Order For Selected Customer");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customers Page");

		this.customerPage = this.customerPage.selectCustomer(this.pr.readTestData("TC_6.2_CHILD_CUSTOMER_NAME"));
		this.customerPage = this.customerPage.clickCreateOrder();
		logger.debug("Select Customer And Click Create Order");

		this.ordersPage = this.customerPage.createOrderForCustomer("OrderTwoWithDifferentPeriods", "co");
		logger.debug("Create Order For Selected Customer");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customers Page");

		this.customerPage = this.customerPage.selectCustomer(this.pr.readTestData("TC_6.2_CHILD_CUSTOMER_NAME"));
		this.customerPage = this.customerPage.clickCreateOrder();
		logger.debug("Select Customer And Click Create Order");

		this.ordersPage = this.customerPage.createOrderForCustomer("OrderThreeWithDifferentPeriods", "co");
		logger.debug("Create Order For Selected Customer");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customers Page");

		this.customerPage = this.customerPage.selectCustomer(this.pr.readTestData("TC_6.2_CHILD_CUSTOMER_NAME"));
		this.customerPage = this.customerPage.clickCreateOrder();
		logger.debug("Select Customer And Click Create Order");

		this.ordersPage = this.customerPage.createOrderForCustomer("OrderfourWithDifferentPeriods", "co");
		logger.debug("Create Order For Selected Customer");

		this.ordersPage = this.ordersPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
