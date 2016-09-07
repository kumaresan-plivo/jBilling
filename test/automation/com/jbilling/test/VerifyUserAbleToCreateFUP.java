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
public class VerifyUserAbleToCreateFUP extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "TC 113: Verify user is able to create FUP.", groups = { "globalRegressionPack" })

	public void TC113_VerifyCreateFUP() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Login Into Application And Navigate to Product Page");

		// Initiate And Complete The Process To Create A Product Category
		// 'National Mobile Calls'
		String productCategory = this.productsPage.addCategoryNationalMobileCalls("productCategoryNationMobileCalls", "pnmc");
		logger.debug("Create A Product Category");

		this.productsPage = this.productsPage.validateCategoriesSavedTestData(productCategory);
		logger.debug("Validate Saved New Product category successfully");

		// Initiate And Complete The Process To Add The Product "Roming Call Rates"
		String products = this.productsPage.addProductNationalRomingcall("addProductRomingCallRates", "aprcr");
		this.propReader.updatePropertyInFile("TC_113.PRODUCT_NAME", products, "testData");
		logger.debug("Add The Product");

		this.productsPage = this.productsPage.validateProductSavedTestData(products);
		logger.debug("Validate Saved New Product successfully");

		String id = this.productsPage.getIDOfAddedProduct();
		logger.debug("Get Product ID");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.FreeUsagePools);
		logger.debug("Select Free Usage Pool from Configuration list");

		// Add free usage Pool "100 National Call Minutes Free"
		String freeUsagePoolName = this.confPage.AddFreeUsagePool("addFreeUsagePool", "afup", id, productCategory, products);
        this.propReader.updatePropertyInFile("TC_113,CATEGORY_NAME", freeUsagePoolName, "testData");
		logger.debug("add FreeUsagePool");

		this.plansPage = this.navPage.navigateToPlanPage();
		logger.debug("Navigate to Plan Page");

		// Initiate And Complete The Process To Add New Plan
		String addPlan = this.plansPage.addPlanForMobileCalls(freeUsagePoolName, productCategory, products, "addPlanForMobileCall",
				"apfmc");
        this.propReader.updatePropertyInFile("TC_113,PLAN_NAME", addPlan, "testData");
		logger.debug("Add The Plan");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved New plan", "successfully", TextComparators.contains);
		logger.debug("Verify Message For plan saved successfully");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
