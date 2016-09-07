package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddProductField;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class TestAddAndEditProduct extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 3.3 : Verify user is able to add and edit a Product.")
	public void testAddAndEditProduct() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047251");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Products Tab");

		this.productsPage.addProduct(AddProductField.FLAT, "addProductOne", "ap");
		logger.debug("Add The Product");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Products Page");

		this.productsPage.addProduct(AddProductField.LINEPERCENTAGE, "addProductTwo", "ap");
		logger.debug("Add Different Product From Existing");

		this.productsPage = this.productsPage.editProduct("editProduct", "ap");
		logger.debug("Edit Existing Product");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Updated product", "successfully.", TextComparators.contains);
		logger.debug("Verify Text:Updated New Product successfully");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
