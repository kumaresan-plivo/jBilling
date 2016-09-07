package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestCustomerStatusChangeAsCollection extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 12.1: Verify customer status changed as per the collection")
	public void testCustomerStatusChangeAsCollection() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909922");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(GlobalEnumsPage.PageConfigurationItems.Collections);
		logger.debug("Select Configuration Collections Option");

		this.confPage = this.confPage.runCollectionsForDate("03/01/2001");
		logger.debug("Run Collections for date 03/01/2001");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigating to Customers Page");

		this.customerPage = this.customerPage.statusCycle("customerInformationForCollectionCycleOne_One",
				"customerInformationForCollectionCycleOne_Two", "ci");
		logger.debug("Run Cycle One for Customer Status Verification");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(GlobalEnumsPage.PageConfigurationItems.Collections);
		logger.debug("Select Configuration Collections Option");

		this.confPage = this.confPage.runCollectionsForDate("03/20/2001");
		logger.debug("Run Collections for date 03/20/2001");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigating to Customers Page");

		this.customerPage.statusCycle("customerInformationForCollectionCycleTwo_One", "customerInformationForCollectionTwo_Two", "ci");
		logger.debug("Run Cycle Two for Customer Status Verification");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(GlobalEnumsPage.PageConfigurationItems.Collections);
		logger.debug("Select Configuration Collections Option");

		this.confPage = this.confPage.runCollectionsForDate("03/25/2001");
		logger.debug("Run Collections for date 03/25/2001");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigating to Customers Page");

		this.customerPage.statusCycle("customerInformationForCollectionCycleThree_One", "customerInformationForCollectionCycleThree_Two",
				"ci");
		logger.debug("Run Cycle Three for Customer Status Verification");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
