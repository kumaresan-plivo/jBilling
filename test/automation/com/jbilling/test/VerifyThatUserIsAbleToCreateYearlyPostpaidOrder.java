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
public class VerifyThatUserIsAbleToCreateYearlyPostpaidOrder extends BrowserApp {

	@Test(description = "TC 92 : Verify that user is able to Create 'Yearly Pre-paid' Order")
	public void VerifyUserAbleToCreateMonthlyPostpaidOrder() throws Exception {
		Reporter.log("<br> Test Begins");

		this.confPage = this.navPage.navigateToConfigurationPage();
		String orderPeriod = this.confPage.createOrderPeriod("orderPeriod_Year", "opy");
		this.productsPage = this.navPage.navigateToProductsPage();
		String categoryName = this.productsPage.addCategory("productCategory", "pcat");
		this.productsPage = this.navPage.navigateToProductsPage();
		String description2 = this.productsPage.addProducts(AddProductField.FLAT, "addProductThreeToAddDependencies", "ap");
		this.plansPage = this.navPage.navigateToPlanPage();
		String planName = this.plansPage.addPlanYearly(AddPlanField.BUNDLEDPERIOD, categoryName, description2, "yearlyPostPaid", "ypp",
				orderPeriod);
		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new plan", "successfully", TextComparators.contains);
		this.confPage = this.navPage.navigateToConfigurationPage();
		this.confPage = this.confPage.clickOnPaymentMethodLink();
		String methodName = this.confPage.addPaymentMethod("paymentTypeWithPaymentCard", "pt");
		this.confPage = this.confPage.clickOnAccountTypeLink();
		String accountType = this.confPage.createAccount(methodName, "accountCreate", "ac");
		this.customerPage = this.navPage.navigateToCustomersPage();
		String customerName = this.customerPage.addCustomerWithMakePayment("customerCreate", "cc", accountType);
		this.customerPage = this.customerPage.createOrderYearly("createOrderYearlyPostpaid", "coyp", planName);

		// Verify Order Grand Total
		//this.ordersPage = this.ordersPage.verifyAppliedTotalOnOrder();
		Reporter.log("<br> Test Passed");
	}
}
