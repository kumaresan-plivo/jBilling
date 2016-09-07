package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestAssignAccountToProduct extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 3.5 : Verify user can assign an Account Type price to a Product and edit it")
	public void testAssignAccountToProduct() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909904");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigating to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.AccountType);
		logger.debug("Select Configuration");

		String accountName = this.confPage.createAccountType("addAccountType", "aat");
		this.propReader.updatePropertyInFile("TC_3.5_ACCOUNT_NAME_ONE", accountName, "testData");

		this.confPage = this.confPage.selectAccountName(this.pr.readTestData("TC_3.5_ACCOUNT_NAME_ONE"));

		this.confPage = this.confPage.clickAccountTypePrices();
		logger.debug("Select Account Type Add Click On Account Type Prices");

		this.confPage = this.confPage.addAccountTypePriceToSelectedProduct("Flat 1 Priced Product", "addPrice", "ap");
		logger.debug("Add Price to Account Type for selected product");

		this.confPage = this.confPage.updateAccountTypePriceForProduct("editPrice", "ap");
		logger.debug("Edit Price to Account Type for selected product");

		Reporter.log("Test Passed");
		logger.exitMethod();
	}
}
