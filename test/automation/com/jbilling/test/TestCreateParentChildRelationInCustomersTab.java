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
public class TestCreateParentChildRelationInCustomersTab extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 6.2 : Verify user can create a Parent/Child relationship	within the Customer tab")
	public void testCreateParentChildRelationInCustomersTab() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909908");

        this.customerPage = this.navPage.navigateToCustomersPage();
        this.customerPage = this.customerPage.selectCustomer(this.pr.readTestData("TC_6.1_CUSTOMER_NAME"));

		String childCustomer = this.customerPage.addChildCustomer(this.pr.readTestData("TC_2.1_ACCOUNT_NAME_ONE"),
				this.pr.readTestData("TC_2.1.1_METHOD_NAME_ONE"), "addSecondCustomer", "ac");
		this.propReader.updatePropertyInFile("TC_6.2_CHILD_CUSTOMER_NAME", childCustomer, "testData");
		logger.debug("Create a Child Customer");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new customer", "successfully.", TextComparators.contains);
		logger.debug("Customer Is Saved Successfully");

		this.customerPage = this.customerPage.validateSavedTestDataInTable(this.pr.readTestData("TC_6.2_CHILD_CUSTOMER_NAME"));
		logger.debug("Validate Saved New Customer Test Data");

		this.customerPage = this.customerPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
