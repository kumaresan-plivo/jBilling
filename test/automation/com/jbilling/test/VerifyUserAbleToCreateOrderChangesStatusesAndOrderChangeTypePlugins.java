package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

public class VerifyUserAbleToCreateOrderChangesStatusesAndOrderChangeTypePlugins extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "TC 51 : Verify User Able To Create Order Changes Statuses And Order Change Type Plugins", groups = {
			"globalRegressionPack" })
	public void createOrderChangesStatusesandorderchangetypeplugins() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Login Into Application And Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.OrderChangeStatuses);
		logger.debug("Select OrderChangeStatuses from Configuration list");

		this.confPage = this.confPage.setNumberOfRowsToTwo();
		logger.debug("On Order Change Statuses page Data is entered with data");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Order Change Statuses updated", "", TextComparators.contains);
		logger.debug("Verify Message For Edit Payment Method Type");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Plugins);
		logger.debug("Select plugins from Configuration list");

		this.confPage = this.confPage.ClickOnEventListner();
		logger.debug("Select plugins from Configuration list");

		this.confPage = this.confPage.verifyAddPluginPageHeader();
		logger.debug("Add Plugin Page Header Verified");

		this.confPage = this.confPage.enterTestDataInOnPlugnin("OrderPluginPageInfo", "oi");
		logger.debug("Select plugins from Configuration list");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
