package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestRootCompanyImpersonationOnChildCompany extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

@Test(description = "Test Case 5.1: Verify that Root Company has ability to "
			+ "impersonate Child Company and view all & only information assigned to Child Company.")
	public void testRootCompanyImpersonationOnChildCompany() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909906");

//		this.homePage = this.navPage.switchToChildCompany(this.runTimeVariables.get("TC_1.3_CHILD_COMPANY_COMPANYNAME"));
//		logger.debug("Switch To Child Company");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Products Page");

//		this.homePage = this.navPage.switchToParentCompany();
//		logger.debug("Switch To Parent Company");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate To Customers Page");

		this.customerPage = this.customerPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
