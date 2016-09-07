package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestCreateCategoryWithAssetManagement extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 3.2 : Verify user is able to create a Category that uses "
			+ "Asset Management (and meta fields) 'Asset Category 1' is available to all Companies")
	public void testCreateCategoryWithAssetManagement() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047250");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Products Tab");

		String assetCategory = this.productsPage.addProductCategoryWithAssetMgmt("CreateProductCategory", "ac");
		this.propReader.updatePropertyInFile("TC_3.2_CATEGORY_NAME", assetCategory, "testData");
		logger.debug("Create Product Category with Asset Management");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product category", "successfully", TextComparators.contains);
		logger.debug("Verify Text:Saved New Product successfully");

		String savedCategoryName = this.propReader.readPropertyFromFile("TC_3.2_CATEGORY_NAME", "testData");
		this.confPage = this.confPage.validateCategoriesSavedTestData(savedCategoryName);
		logger.debug("Validate Saved New Product successfully");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
