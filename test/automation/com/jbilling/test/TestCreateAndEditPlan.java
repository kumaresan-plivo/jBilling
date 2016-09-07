package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class TestCreateAndEditPlan extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 4.1 : Verify that user is able to create and edit a plan.")
	public void testCreateAndEditPlan() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909905");

		this.plansPage = this.navPage.navigateToPlanPage();
		logger.debug("Login Into Application And Navigate to Plans Page");

		String productCategoryName = this.pr.readTestData("TC_3.2_CATEGORY_NAME");
		String engDescription = this.pr.readTestData("TC_3.4_ENGLISH_DESC");

		this.plansPage = this.plansPage.addPlan(productCategoryName, engDescription, "addplan", "ap");
		logger.debug("Processing To Add Plans");

		this.plansPage = this.plansPage.editPlan(engDescription, "addplan", "ap");
		logger.debug("Processing To Add Plans");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
