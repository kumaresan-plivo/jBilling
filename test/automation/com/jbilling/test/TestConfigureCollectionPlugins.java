package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestConfigureCollectionPlugins extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	@Test(description = "Test Case 2.6 : Verify ability to configure Collections Plugins")
	public void testConfigureCollectionPlugins() throws Exception {
		logger.enterMethod();
		Reporter.log("Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047247");

		Reporter.log("<br> Get ID Generated for Second Step");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Users);
		logger.debug("Select Configuration");

		String userName = this.pr.readTestData("TC_1.1_CREDENTIALS_USERNAME");
		this.confPage = this.confPage.configurePluginPermissions(userName, "plugin", "pid");
		logger.debug("Configure Permissions for Plugins");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Plugins);
		logger.debug("Add Plugin in Collections");

		this.confPage = this.confPage.selectPluginCategory("selectPluginCategory", "pc");
		logger.debug("Select Plugin Category");

		this.confPage = this.confPage.addNewPluginInCategory("addPlugin", "ap", this.runTimeVariables);
		logger.debug("Add Plugin Under Generic Category");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("The new plug-in with id", "has been saved.", TextComparators.contains);
		logger.debug("Verify Text:The new plugin with id has been saved successfully");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
