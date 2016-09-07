package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

public class VerifyUserAbleToConfigureOrderChangeStatuses extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "TC 85 : Verify that user is able to Configure Order Change Statuses", groups = { "globalRegressionPack" })
	public void configureOrderChangeStatus() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Login Into Application And Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.OrderChangeStatuses);
		logger.debug("Select Order Change Statuses from Configuration list");

		this.confPage.enterDataStatus("DataStatusInEnglishBox", "dsieb");
		logger.debug("Entered Order Change Statuses Processing, Suspended and Finished");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Order Change Statuses updated", "Statuses updated",
				TextComparators.contains);
		logger.debug("Verify Message For Order status when Not check applied ");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
