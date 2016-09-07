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
public class VerifyUserAbleToEditDeletePaymentMethod extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "TC 10 : Verify that user can edit and delete the created payment method")
	public void TC10_EditDeletePaymentMethodForCheque() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.PaymentMethod);
		logger.debug("Select Payment Method from Configuration list");

		this.confPage = this.confPage.addEditDeletePaymentMethod("TC10_AddEditDeletePaymentMethod", "aedpm");
		logger.debug("Add Edit Delete Payment Method With Recurring");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Deleted Payment Method Type", "successfully", TextComparators.contains);
		logger.debug("Verify Message For Delete Method");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
