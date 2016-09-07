package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.MessagesPage;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

public class VerifyStatusofPreExistingandNewOrder extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "107", groups = { "globalRegressionPack" })
	public void verifyStatusOfPreExistingandNewOrder() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configure Page");
		this.confPage = this.confPage.VerifyOrderStatus("VerifyOrderStatuses", "OS");

		MessagesPage.isErrorMessageAppeared();

		logger.debug("Verify Message for Error for Flag Field");
		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
