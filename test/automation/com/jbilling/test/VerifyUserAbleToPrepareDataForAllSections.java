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

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserAbleToPrepareDataForAllSections extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	@Test(description = "TC 04 : Data preparation for all the section")
	public void TC04_dataPreprationForAllSections() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Users);
		logger.debug("Select Users from Configuration list");

		this.confPage = this.confPage.addUserPluginPermission("pluginAndAgentPermissions", "pap");
		logger.debug("Add User Plugin Permissions");

		this.loginPage = this.navPage.logoutApplication();
		logger.debug("Logout From The Application");

		this.runTimeVariables.put("ENVIRONMENT_UNDER_TEST", this.pr.readConfig("EnvironmentUnderTest"));
		this.runTimeVariables.put("ENVIRONMENT_URL_UNDER_TEST",
				this.pr.readConfig(this.runTimeVariables.get("ENVIRONMENT_UNDER_TEST") + "_URL"));

		this.homePage = this.loginPage.login(this.runTimeVariables.get("ENVIRONMENT_UNDER_TEST"));
		logger.debug("Login Into Application");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Login Into Application And Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Roles);
		logger.debug("Select plugins from Configuration list");

		this.confPage = this.confPage.setRolePermission("pluginAndAgentPermissions", "pap");
		logger.debug("Set Permissions For Roles");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Updated role", "successfully.", TextComparators.contains);
		logger.debug("Verify Message For Restricted Plugin Permissions");

		this.loginPage = this.navPage.logoutApplication();
		logger.debug("Logout From The Application");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
