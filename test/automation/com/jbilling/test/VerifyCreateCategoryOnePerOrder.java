package com.jbilling.test;

import org.testng.annotations.Test;

import com.jbilling.framework.utilities.browserutils.BrowserApp;

public class VerifyCreateCategoryOnePerOrder extends BrowserApp {

	@Test(description = "Test Case 140 : Verify user can Create categories with One Per Order.")
	public void createCategoryWithOnePerOrder() throws Exception {

		this.navPage.navigateToProductsPage();
		String categoryName = this.productsPage.createCategoryWithOneOrder("addCategoryforOnePerOrder", "acopo");

		String description = this.productsPage.addProductInOnePerOrderCategory("addProdutcforOnePerOrder", "apopo", categoryName);
		this.navPage.navigateToProductsPage();
		String description1 = this.productsPage.addProductInOnePerOrderCategory("addProdutcforOnePerOrder", "apopo", categoryName);

		this.navPage.navigateToPlanPage();
		this.plansPage.clickAddNewButton();
		String description2 = this.plansPage.addProductInPlan("addProductOnePerOrder", "ap", categoryName, description);
		this.navPage.navigateToPlanPage();
        this.plansPage.clickAddNewButton();
		String description4 = this.plansPage.addProductInPlan("addProductOnePerOrder", "ap", categoryName, description);

		this.navPage.navigateToConfigurationPage();
		this.confPage.clickOnPaymentMethodLink();
		String methodName = this.confPage.addPaymentMethod("testPaymentType", "pt");

		this.confPage.clickOnAccountTypeLink();
		String accountType = this.confPage.createAccount(methodName, "accountCreate", "ac");

		this.navPage.navigateToCustomersPage();
		this.customerPage.addCustomerWithMakePayment("customerCreate", "cc", accountType);
		this.customerPage.createOrderForOnePerOrder("AddorderPerOrder", "ao", description, description1);
		this.customerPage.verifyAddedProductInPlan(description, description2, description4);
	}
}
