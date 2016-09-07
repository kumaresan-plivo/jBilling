package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class TestCreateProductWithDependencyOnOther extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 15.1 :  Verify that Products with dependencies on other " + "products can be created.")
	public void testCreateProductWithDependencyOnOther() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909925");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Products Tab");

		String assetCategory = this.productsPage.addProductCategoryWithAssetMgmt("CreateProductCategory", "ac");
		this.propReader.updatePropertyInFile("TC_3.2_CATEGORY_NAME", assetCategory, "testData");
		logger.debug("Create Product Category with Asset Management");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product category", "successfully", TextComparators.contains);
		logger.debug("Verify Text:Saved New Product successfully");

		String savedCategoryName = this.pr.readTestData("TC_3.2_CATEGORY_NAME");
		this.confPage = this.confPage.validateCategoriesSavedTestData(savedCategoryName);
		logger.debug("Validate Saved New Product successfully");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Products Tab");

		this.productsPage.addCategory("productCategory", "pcat");
		logger.debug("Create A Product Category");

		this.productsPage = this.productsPage.addProductOnDependency("addProductTwo", "ap", this.pr.readTestData("TC_3.2_CATEGORY_NAME"));
		logger.debug("Add The Product With Dependency");

		this.ordersPage = this.ordersPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
