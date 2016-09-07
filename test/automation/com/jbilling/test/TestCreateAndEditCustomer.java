package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestCreateAndEditCustomer extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 6.1 : Verify if user is able to create/Edit a new customer into the system.")
	public void testCreateAndEditCustomer() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909907");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customers Page");

		String customerName = this.customerPage.addCustomer(this.pr.readTestData("TC_2.1_ACCOUNT_NAME_ONE"),
				this.pr.readTestData("TC_2.1.1_METHOD_NAME_ONE"), "new_customer", "nc");

		this.propReader.updatePropertyInFile("TC_6.1_CUSTOMER_NAME", customerName, "testData");		
		logger.debug("Add a New Customer");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new customer", "successfully.", TextComparators.contains);
		logger.debug("Customer Is Saved Successfully");

		this.customerPage = this.customerPage.validateSavedTestDataInTable(this.propReader.readPropertyFromFile("TC_6.1_CUSTOMER_NAME",
				"testData"));
		logger.debug("Validate Saved New Customer Test Data");

		this.customerPage = this.customerPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
