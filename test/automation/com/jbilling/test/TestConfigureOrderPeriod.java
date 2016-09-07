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

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestConfigureOrderPeriod extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 2.4 : Verify ability to configure Order Periods")
	public void testConfigureOrderPeriod() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047245");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.OrderPeriods);
		logger.debug("Select Configuration");

		String orderPeriod1 = this.confPage.createNewOrderPeriod("firstOrderPeriod", "op");

		this.propReader.updatePropertyInFile("TC_2.4_ORDER_PERIOD1", orderPeriod1, "testData");
		logger.debug("Configuring First Order period");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Order Period", "created successfully", TextComparators.contains);
		logger.debug("Verify Text:Order Period Is Created Successfully");

		String description1 = this.propReader.readPropertyFromFile("TC_2.4_ORDER_PERIOD1", "testData");
		this.confPage = this.confPage.validatePeriodsSavedTestData(description1);
		logger.debug("Validate Saved Account Type Test Data");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		String orderPeriod2 = this.confPage.createNewOrderPeriod("secondOrderPeriod", "op");
		this.propReader.updatePropertyInFile("TC_2.4_ORDER_PERIOD2", orderPeriod2, "testData");
		logger.debug("Configuring Second Order period");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Order Period", "created successfully", TextComparators.contains);
		logger.debug("Verify Text:Order Period Is Created Successfully");

		String description2 = this.propReader.readPropertyFromFile("TC_2.4_ORDER_PERIOD2", "testData");
		this.confPage = this.confPage.validatePeriodsSavedTestData(description2);
		logger.debug("Validate Saved Account Type Test Data");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		String orderPeriod3 = this.confPage.createNewOrderPeriod("thirdOrderPeriod", "op");
		this.propReader.updatePropertyInFile("TC_2.4_ORDER_PERIOD3", orderPeriod3, "testData");
		logger.debug("Configuring Third Order period");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Order Period", "created successfully", TextComparators.contains);
		logger.debug("Verify Text:Order Period Is Created Successfully");

		String description3 = this.propReader.readPropertyFromFile("TC_2.4_ORDER_PERIOD3", "testData");
		this.confPage = this.confPage.validatePeriodsSavedTestData(description3);
		logger.debug("Validate Saved Account Type Test Data");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
