package com.jbilling.test;

import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddPlanField;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddProductField;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserIsAbleToEditPurchaseOrder extends BrowserApp {

	@Test(description = "TC 95 : Verify that user is able to Edit Purchase Order")
	public void VerifyUserIsAbleToEditPurchasedOrder() throws Exception {
		Reporter.log("<br> Test Begins");

		this.navPage.navigateToConfigurationPage();
		this.confPage.ClickOnCurrencyLink();
		String defaultCurrency = this.confPage.getDefaultCurrencyValue();
		this.confPage.setCurrency("currencyName", "cn");
		this.navPage.navigateToProductsPage();
		String categoryName = this.productsPage.addCategory("productCategory", "pcat");
		this.navPage.navigateToProductsPage();
		String description2 = this.productsPage.addProducts(AddProductField.FLAT, "addProductThreeToAddDependencies", "ap");
		this.navPage.navigateToPlanPage();
		String planNameWithBundle = this.plansPage.addPlanMonthly(AddPlanField.BUNDLEDPERIOD, categoryName, description2, "withBundle",
				"wb");
		this.navPage.navigateToPlanPage();
		String planNameWithoutBundle = this.plansPage.addPlanMonthly(AddPlanField.BUNDLEDPERIOD, categoryName, description2,
				"withOutBundle", "wob");
		this.msgsPage.verifyDisplayedMessageText("Saved new plan", "successfully", TextComparators.contains);
		this.navPage.navigateToConfigurationPage();
		this.confPage.clickOnPaymentMethodLink();
		String methodName = this.confPage.addPaymentMethod("paymentTypeWithPaymentCard", "pt");
		this.confPage.clickOnAccountTypeLink();
		String accountType = this.confPage.createAccount(methodName, "accountCreate", "ac");
		this.navPage.navigateToCustomersPage();
		String customerName = this.customerPage.addCustomerWithMakePayment("customerCreate", "cc", accountType);
		this.customerPage.createOrderBundleAndWithoutBundle("createOrderMonthlyPrepaid", "comp", planNameWithBundle, planNameWithoutBundle);

		this.ordersPage.verifyAppliedTotalOnOrderFirstLine();
		this.ordersPage.editCreartedOrder("editOrder", "eo", customerName);

		navPage.navigateToConfigurationPage();
		confPage.ClickOnCurrencyLink();
		confPage.resetCurrecncy("currencyName", "cn", defaultCurrency);

		Reporter.log("<br> Test Passed");
	}
}
