package com.jbilling.test;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyActiveSinceDateOfDiscountSubOrderCreatedIsSameSsActiveSinceOfTheParentOrder extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Test(description = "TC 58 : Verify a Customer CC number is masked on customer 'Details page','Edit Customer' page and blacklist page.")
	public void verifyActiveDateOfSunOrderSameAsActiveSinceOfParentOrder() throws Exception {

		this.discountsPage = this.navPage.navigateToDiscountsPage();
		logger.debug("Click on Discount Menu");

		String discountName = this.discountsPage.createNewDiscountWithPercentage("addDiscountWithPercentage", "adwp");
		logger.debug("Create a discount with percentage");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration page");

		this.confPage = this.confPage.clickOnPaymentMethodLink();
		logger.debug("Click on Payment Method link");

		String methodName = this.confPage.addPaymentMethod("testPaymentType", "pt");
		logger.debug("Add Payment Method");

		this.confPage = this.confPage.clickOnAccountTypeLink();
		logger.debug("Click on Account type Link");

		String accountType = this.confPage.createAccount(methodName, "accountCreate", "ac");
		logger.debug("Add account type");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Click on Product Page");

		String categoryName = this.productsPage.createCategoryWithOneCustomer("addCategoryForMinimumTimePeriod", "ac");
		logger.debug("Add category");

		String productDescription = this.productsPage.addProductInOnePerCustomerCategory("addProductForMinimumTime", "ap", categoryName);
		logger.debug("Add product in category with one per order");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Click on Customer Tab");

		String customerName = this.customerPage.createCustomerWithPaymentType("AddCustomerWithPaymentCard", "acwpc", accountType,
				methodName);
		logger.debug("Click on Customer Tab");

		this.customerPage = this.customerPage.createOrderWithDiscount("createOrderwithDiscount", "cowd", productDescription, discountName);
		logger.debug("Create an Order");

		String appliedDiscount = this.customerPage.getAppliedDiscount();
		logger.debug("Get Applied Discount");

		String calculatedDiscount = this.customerPage.calculatedDiscount("calculateDiscount", "cd");
		this.customerPage = this.customerPage.verifyAppliedDiscount(appliedDiscount, calculatedDiscount);

		this.ordersPage = this.ordersPage.verifySubOrderActiveSinceDate(customerName);
		logger.debug("Verify Sub Order Active Date");
	}
}
