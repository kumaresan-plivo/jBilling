package com.jbilling.test;

import org.testng.annotations.Test;

import com.jbilling.framework.utilities.browserutils.BrowserApp;

public class VerifyCreateCategoryOnePerCustomer extends BrowserApp {

	@Test(description = "TC 141 : Verify user can Create categories with One Per Customer ")
	public void createCategoryWithOnePerOrder() throws Exception {

		this.navPage.navigateToProductsPage();

		String categoryName = this.productsPage.createCategoryWithOneCustomer("addCategoryOnePerCustomer", "acopc");
		String productDescription = this.productsPage.addProductInOnePerCustomerCategory("addFirstProductOnePerCustomer", "apopc", categoryName);

		this.navPage.navigateToProductsPage();
		String productDescription1 = this.productsPage.addProductInOnePerCustomerCategory("addProductOnePerCustomer", "apopc", categoryName);
		this.navPage.navigateToProductsPage();
		this.productsPage.addProductInOnePerCustomerCategory("addProductOnePerCustomer", "apopc", categoryName);

		this.navPage.navigateToConfigurationPage();
		this.confPage.clickOnPaymentMethodLink();
		String methodName = this.confPage.addPaymentMethod("testPaymentType", "pt");
		this.confPage.clickOnAccountTypeLink();
		String accountType = this.confPage.createAccount(methodName, "accountCreate", "ac");

		this.navPage.navigateToCustomersPage();
		String loginName = this.customerPage.addCustomerWithMakePayment("customerCreate", "cc", accountType);
		this.customerPage.createOrderForOnePerCustomer("AddorderPerCustomer", "aopc", productDescription);

		this.navPage.navigateToCustomersPage();
		this.customerPage.addProductInExistingCustomer("AddorderPerCustomer", "aopc", loginName, productDescription1);
	}
}
