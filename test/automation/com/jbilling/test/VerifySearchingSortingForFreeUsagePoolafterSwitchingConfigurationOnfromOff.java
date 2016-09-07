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

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifySearchingSortingForFreeUsagePoolafterSwitchingConfigurationOnfromOff extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "TC 114 : Verify searching and sorting works as defined for 'Free Usage Pool' after switching configuration 'On' from 'Off'", groups = { "globalRegressionPack" })
	public void configureFreeUsagePoolOnFromOff() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.setConfigurationPreference("addPreferencesvalue1", "ap");
		logger.debug("Add The value 1 in Preferences 63");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Preference 63", "updated successfully", TextComparators.contains);
		logger.debug("Verify Preferences 63 Updated Successfully");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.FreeUsagePools);
		logger.debug("Select Free Usage Pool from Configuration list");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Login Into Application And Navigate to Configuration Page");

		this.confPage = this.confPage.setConfigurationPreferenceJQGrid("addPreferencesvalue0", "ap");
		logger.debug("Add The value 0 in Preferences 63");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Preference 63", "updated successfully", TextComparators.contains);
		logger.debug("Verify Preferences 63 Updated Successfully");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
