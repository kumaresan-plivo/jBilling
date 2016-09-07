package com.jbilling.test;

import org.testng.annotations.Test;

import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddProductField;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

public class VerifyUserAbleToCreateGlobleProductWithAnAsset extends BrowserApp {

	@Test(description = "TC 48 : Verify that user can create a global product with an asset")
	private void createNewChildCompany() throws Exception {

		this.navPage.navigateToProductsPage();
		String globalCategoryWithAssetManagement = this.productsPage.addNewCategory("assetName", "name");
		this.productsPage.selectCategory(globalCategoryWithAssetManagement);
		String description = this.productsPage.addProduct(AddProductField.ASSETMANAGEMENT, "product.tc-48", "ap");
		this.productsPage.validateAddedProduct(description);
		String identifier = this.productsPage.addAssetinProduct("assetDetail", "ad");
		this.productsPage.validateAddedAsset(identifier);
		String childIdentifier = this.productsPage.addChildAsset("assetDetail", "ad");
		this.productsPage.validateAddedAsset(childIdentifier);
	}
}
