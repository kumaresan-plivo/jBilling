package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserAbleToConfigurePluginsForProvisioning extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(groups = { "globalRegressionPack" })
	public void configurePluginProvisionForUser() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Login Into Application And Navigate to Configuration Page");

		this.productsPage = this.productsPage.addPlugin("addPluginOPT", "apOPT");
		logger.debug("Add The Plugin");

		this.productsPage = this.productsPage.addPluginWithProvisionID("addPluginOPTL", "apOPTL");
		logger.debug("Add The Plugin Inside");

		this.productsPage = this.productsPage.addPluginInsidePlugin("addPluginAPT", "apAPT");
		logger.debug("Add The Plugin Inside");

		this.productsPage = this.productsPage.addPluginInsidePlugin("addPluginPPT", "apPPT");
		logger.debug("Add The Plugin Inside");

		this.productsPage = this.productsPage.addPluginInsidePlugin("addPluginPCT", "apPCT");
		logger.debug("Add The Plugin Inside");

		this.loginPage = this.navPage.logoutApplication();
		logger.debug("Logout From The Application");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
