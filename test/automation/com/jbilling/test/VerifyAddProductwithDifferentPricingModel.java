package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddProductField;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

public class VerifyAddProductwithDifferentPricingModel extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "60.1", groups = { "globalRegressionPack" })
	public void VerifyAddProductwithDifferentPricing() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Product Page");

		this.productsPage.addCategory("CreateProductCategoryData", "pcd");
		logger.debug("Add a Category");

		this.productsPage.addProduct(AddProductField.DESCRIPTION, "addProductCategoryOne", "ap");
		logger.debug("Add a Product with Description");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product", "successfully", TextComparators.contains);
		logger.debug("Verify Message for Add product Successfully");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}

	@Test(description = "60.2", dependsOnMethods = "VerifyAddProductwithDifferentPricing")
	public void VerifyAddProductwithFlatPrice() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage.addProduct(AddProductField.FLATPRICE, "addProductWithFlatPricing", "ap");
		logger.debug("Add a Product");

        this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product", "successfully", TextComparators.contains);
        logger.debug("Verify Message for Add product Successfully");

        Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}

	@Test(description = "60.3", dependsOnMethods = "VerifyAddProductwithDifferentPricing")
	public void VerifyAddProductwithGraduatePrice() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage.addProduct(AddProductField.GRADUATEDPRICE, "addProductWithGraduatePricing", "ap");
		logger.debug("Add a Product with garduate price and quantity");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product", "successfully", TextComparators.contains);
		logger.debug("Verify Message for Add product Successfully");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}

	@Test(description = "60.4", dependsOnMethods = "VerifyAddProductwithDifferentPricing")
	public void VerifyAddProductwithGraduatedCapPrice() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage.addProduct(AddProductField.GRADUATECAPPRICE, "addProductWithGraduateCapPricing", "ap");
		logger.debug("Add a Product with garduate cap price and maximum");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product", "successfully", TextComparators.contains);
		logger.debug("Verify Message for Add product Successfully");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}

	@Test(description = "60.5", dependsOnMethods = "VerifyAddProductwithDifferentPricing")
	public void VerifyAddProductwithTimeOfDayPrice() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage.addProduct(AddProductField.TIMEOFDAY, "addProductWithTimeOfDayPricing", "ap");
		logger.debug("Add a Product with Time of day price and quantity");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product", "successfully", TextComparators.contains);
		logger.debug("Verify Message for Add product Successfully");
	}

	@Test(description = "60.6", dependsOnMethods = "VerifyAddProductwithDifferentPricing")
	public void VerifyAddProductwithTieredPricing() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage.addProduct(AddProductField.TIERED, "addProductWithTieredPricing", "ap");
		logger.debug("Add a Product with Time of day price and quantity");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product", "successfully", TextComparators.contains);
		logger.debug("Verify Message for Add product Successfully");
	}

	@Test(description = "60.7", dependsOnMethods = "VerifyAddProductwithDifferentPricing")
	public void VerifyAddProductwithVolumePricing() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage.addProduct(AddProductField.VOLUME, "addProductWithVolumePricing", "ap");
		logger.debug("Add a Product with Time of day price and quantity");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product", "successfully", TextComparators.contains);
		logger.debug("Verify Message for Add product Successfully");
	}

	@Test(description = "60.8", dependsOnMethods = "VerifyAddProductwithDifferentPricing")
	public void VerifyAddwithPooledPricing() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage.addProduct(AddProductField.POOLED, "addProductWithPooledPricing", "ap");
		logger.debug("Add a Product with Time of day price and quantity");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product", "successfully", TextComparators.contains);
		logger.debug("Verify Message for Add product Successfully");
	}

	@Test(description = "60.10", dependsOnMethods = "VerifyAddProductwithDifferentPricing")
	public void VerifyAddwithItemPercantageSelector() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage.addProduct(AddProductField.ITEMPAGESELECTOR, "addProductWithItemPercantageSelector", "ap");
		logger.debug("Add a Product with Time of day price and quantity");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product", "successfully", TextComparators.contains);
		logger.debug("Verify Message for Add product Successfully");
	}

	@Test(description = "60.9", dependsOnMethods = "VerifyAddProductwithDifferentPricing")
	public void VerifyAddItemItemSelector() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage.addProduct(AddProductField.ITEMSELECTOR, "addProductWithItemSelector", "ap");
		logger.debug("Add a Product with Time of day price and quantity");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product", "successfully", TextComparators.contains);
		logger.debug("Verify Message for Add product Successfully");
	}

	@Test(description = "60.11", dependsOnMethods = "VerifyAddProductwithDifferentPricing")
	public void VerifyAddItemQuantityAdd() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage.addProduct(AddProductField.QUANTITYADON, "addProductWithQuantityAdOn", "ap");
		logger.debug("Add a Product with Time of day price and quantity");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new product", "successfully", TextComparators.contains);
		logger.debug("Verify Message for Add product Successfully");
	}
}
