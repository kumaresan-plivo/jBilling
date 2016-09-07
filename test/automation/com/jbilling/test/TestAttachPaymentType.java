package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestAttachPaymentType extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 2.1.1 : Verify ability to attach a Payment Method to " + "an Account Type")
	public void testAttachPaymentType() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047242");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(GlobalEnumsPage.PageConfigurationItems.PaymentMethod);
		logger.debug("Select Configuration");

		this.confPage = this.confPage.configurePaymentMethod("configurePaymentMethod", "pm");
		logger.debug("Configure Payment Method to Account");

		String methodName = this.confPage.addPaymentMethodDetails("testPaymentType", "pt");

		this.propReader.updatePropertyInFile("TC_2.1.1_METHOD_NAME_ONE", methodName, "testData");
		logger.debug("Add Payment Details");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Payment Method Type", "created successfully", TextComparators.contains);
		logger.debug("Verify Payment Method Type Is Created Successfully");

		String paymentMethod = this.propReader.readPropertyFromFile("TC_2.1.1_METHOD_NAME_ONE", "testData");
		this.confPage = this.confPage.validatePeriodsSavedTestData(paymentMethod);
		logger.debug("Validate Saved Account Type Test Data");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
