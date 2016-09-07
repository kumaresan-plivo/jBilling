package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestConfigurePluginPreferencesForCommission extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 16.3 : Verify that user can configure the plug-in "
			+ "and preference required for running a comission process")
	public void testConfigurePluginPreferencesForCommission() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909929");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.productsPage = this.productsPage.addPlugin("addPlugin", "ap");
		logger.debug("Add The Plugin");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.All);
		logger.debug("Select Configuration All ");

		this.confPage = this.confPage.updatePreference("updatePreference", "up");
		logger.debug("Update And Verify Preference");

		this.productsPage = this.productsPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
