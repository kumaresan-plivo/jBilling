package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class TestCreateProductWithCommissions extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 16.2 : Verify that products with commisions can be made")
	public void testCreateProductWithCommissions() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909928");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Products Tab");

		this.productsPage.addCategory("NewProductCategoryData", "pcd");
		logger.debug("Create A Product Category");

		this.productsPage = this.productsPage.addProductWithCommission("addProductTwo", "ap");
		logger.debug("Add a Product With Commission");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
