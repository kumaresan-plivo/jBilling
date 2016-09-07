package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

public class VerifyUserUnableToDeleteFUPThatUsed extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "TC 117 : Verify user is unable to delete an FUP that is in use", groups = { "globalRegressionPack" })
	public void TC117_UserUnableToDeleteFUPThatUsed() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Login Into Application And Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.FreeUsagePools);
		logger.debug("Select FreeUsagePools from Configuration list");

		this.confPage = this.confPage.selectUsagePool(this.pr.readTestData("TC_113,CATEGORY_NAME"));
		logger.debug("Select FreeUsagePools from Configuration list");

		this.confPage = this.confPage.valdationMessageDisplay("cannot be deleted, it is in use");
		logger.debug("Verify The Usage Pool has an error in the Id field: Usage Pool YYY cannot be deleted, it is in use");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
