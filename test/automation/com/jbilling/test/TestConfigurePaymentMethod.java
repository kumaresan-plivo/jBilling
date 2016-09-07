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
public class TestConfigurePaymentMethod extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 2.3 : Verify ability to configure a Payment Method Configure 'Credit Card' payment method for Account Type: Direct Customer")
	public void testConfigurePaymentMethod() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047244");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(GlobalEnumsPage.PageConfigurationItems.PaymentMethod);
		logger.debug("Select Configuration");

		this.confPage = this.confPage.configurePaymentMethod("configurePaymentMethod", "pm");
		logger.debug("Configure Payment Method to Account");

		String paymentMethodName = this.confPage.addPaymentMethodDetails("secondTestPaymentType", "pt");
		this.propReader.updatePropertyInFile("TC_2.3_METHOD_NAME", paymentMethodName, "testData");
		logger.debug("Add Payment Details");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Payment Method Type", "created successfully", TextComparators.contains);
		logger.debug("Verify Payment Method Type Is Created Successfully");

		String methodName = this.propReader.readPropertyFromFile("TC_2.3_METHOD_NAME", "testData");

		this.confPage = this.confPage.validatePeriodsSavedTestData(methodName);
		logger.debug("Validate Saved Account Type Test Data");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
