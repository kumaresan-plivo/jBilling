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
public class TestCreateDiscount extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 6.3 : Verify user is able to create discounts to be availed while making purchase.")
	public void testCreateDiscount() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909909");

		this.discountsPage = this.navPage.navigateToDiscountsPage();
		logger.debug("Navigate to Discounts Page");

		this.discountsPage = this.discountsPage.clickAddNewButton();
		logger.debug("Click On Add New Discount Button");

		this.discountsPage = this.discountsPage.clickSaveChangesButton();
		logger.debug("Click On Save Changes Button");

		this.discountsPage = this.discountsPage.isValidationErrorAppeared();
		logger.debug("Validate Presence Of Displayed Error");

		String discountCodeDescription = this.discountsPage.createNewDiscount("addDiscount", "ad");
		this.propReader.updatePropertyInFile("TC_6.3_DISCOUNT_CODE_DESCRIPTION", discountCodeDescription, "testData");
		logger.debug("Create Discount");

		this.discountsPage = this.discountsPage.isDiscountCreatedSuccessfully();
		logger.debug("Verify That Discount Is Created Successfully");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customers Page");

		this.customerPage =this.customerPage.selectCustomer(this.pr.readTestData("TC_6.2_CHILD_CUSTOMER_NAME"));
		this.customerPage = this.customerPage.clickCreateOrder();
		logger.debug("Select Customer And Click Create Order");

		this.ordersPage = this.ordersPage.createOrder("createOrder", "co", this.pr.readTestData("TC_6.3_DISCOUNT_CODE_DESCRIPTION"));
		logger.debug("Create The Order For The Customer");

		this.ordersPage = this.ordersPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
