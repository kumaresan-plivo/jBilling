package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestConfigureBillingProcess extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 2.7 : Test Data Preparation for Billing Process")
	public void testConfigureBillingProcess() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047248");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.BillingProcess);
		logger.debug("Switching to Billing Process configuration item");

		this.confPage = this.confPage.addBillingProcess("BillingProcess", "cbp");
		logger.debug("Add Billing Process");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Billing configuration", "saved successfully", TextComparators.contains);
		logger.debug("Billing configuration saved successfully");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
