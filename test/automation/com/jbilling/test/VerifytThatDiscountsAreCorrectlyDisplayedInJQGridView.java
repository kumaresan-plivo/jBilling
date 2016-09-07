package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

public class VerifytThatDiscountsAreCorrectlyDisplayedInJQGridView extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "TC 76 : Verify User Is Able To Configure Product To Have 1 And 2 Dependencies", groups = {
			"globalRegressionPack" })
	public void ConfigureaPoductToHave1And2Dependencies() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Login Into Application And Navigate to Products Tab");
	}
}
