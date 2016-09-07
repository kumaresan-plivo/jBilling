package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserAbleGetMandatoryFieldMessageForAccountType extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "TC 11.2 : Verify User able to get mandatory field validation message")
	public void TC11_2_VerifyMandatoryFieldMessageForAccountType() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.AccountType);
		logger.debug("Select Payment Method from Configuration list");

		this.confPage = this.confPage.verifyMandatoryFieldMessages("mandatoryFieldAccountType", "mfat");
		logger.debug("Verify Mandatory Field Message Appears");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
