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
public class TestCreateProductCategory extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 3.1 : Verify user is able to create/edit a Category 'New Test "
			+ "Category' is only available to Root Company (jBilling).")
	public void testCreateProductCategory() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047249");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Login Into Application And Navigate to Products Tab");

		this.productsPage.addCategory("CreateProductCategoryData", "pcd");
		logger.debug("Create A Product Category");

		String categoryName = this.productsPage.editCategory("NewProductCategoryData", "pcd");
		logger.debug("Edit The Product Category");
		this.propReader.updatePropertyInFile("TC_3.1_CATEGORY_NAME", categoryName, "testData");

		this.confPage = this.confPage.validateCategoriesSavedTestData(this.propReader.readPropertyFromFile("TC_3.1_CATEGORY_NAME",
				"testData"));
		logger.debug("Validate Saved Account Type Test Data");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
