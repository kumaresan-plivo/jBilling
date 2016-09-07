package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddProductField;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestCreateProductWithAsset extends BrowserApp {
	ITestResult result;

	@Test(description = "Test Case 3.4 : Verify that a user can create a product with an asset")
	public void testCreateProductWithAsset() throws Exception {
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047252");

		this.confPage = this.navPage.navigateToConfigurationPage();
		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Plugins);
		this.productsPage = this.productsPage.addPlugin("addPlugin", "ap");
		this.productsPage = this.navPage.navigateToProductsPage();
        this.productsPage.selectAssetCategory1();
		String englishDescription = this.productsPage.addProduct(AddProductField.ASSETMANAGEMENT, "product.tc-3.4", "ap");
		this.propReader.updatePropertyInFile("TC_3.4_ENGLISH_DESC", englishDescription, "testData");
		this.productsPage = this.productsPage.addAsset();
		String assetIdentifier1 = this.productsPage.addAsset("addAssetOne", "ap");
		this.propReader.updatePropertyInFile("TC_3.4_IDENTIFIER_ONE", assetIdentifier1, "testData");
		this.productsPage = this.productsPage.clickAddNew();
		String assetIdentifier2 = this.productsPage.addAsset("addAssetTwo", "ap");
		this.propReader.updatePropertyInFile("TC_3.4_IDENTIFIER_TWO", assetIdentifier2, "testData");

		Reporter.log("<br> Test Passed");
	}
}
