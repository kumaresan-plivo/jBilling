package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = {"automation"})
public class VerifySearchingandSortingforDiscount extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "112", groups = { "globalRegressionPack" })
	public void VerifySearchingandSortingforDiscountTest() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configure Page");

		this.confPage = this.confPage.setConfigurationPreference("setJQLPreferenceValue", "pc");
		logger.debug("Update Perference Number");
		this.discountsPage = this.navPage.navigateToDiscountsPage();
		logger.debug("Navigate to discount page");

		this.discountsPage = this.discountsPage.verifyDiscountTable();
		logger.debug("Verify Discount table");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Login Into Application And Navigate to Configure Page");

		this.confPage = this.confPage.reUpdatePreference("reSetJQLPreferenceValue", "pc");
		logger.debug("Update Perference Number");
		Reporter.log("<br> Test Passed");
	}
}
