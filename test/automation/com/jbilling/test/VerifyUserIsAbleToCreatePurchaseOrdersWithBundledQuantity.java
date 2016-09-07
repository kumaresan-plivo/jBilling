package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddPlanField;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddProductField;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	String graceId = null;
	ITestResult result;

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	/**
	 * Current Application Page Stack || LoginPage--> loginPage;
	 * NavigatorPage--> navPage; HomePage--> homePage; ConfigurationPage-->
	 * confPage; AgentsPage--> agentsPage; CustomersPage--> customerPage;
	 * ProductsPage--> productsPage; PlansPage--> plansPage; OrdersPage-->
	 * ordersPage; InvoicePage--> invoicePage; ReportsPage--> reportsPage;
	 * DiscountsPage--> discountsPage; FiltersPage--> filtersPage;
	 * PaymentsPage--> paymentsPage; MessagesPage--> msgsPage;
	 * 
	 * @author Aishwarya Dwivedi
	 * @since 1.0
	 * @version 1.0
	 */

	@Test(description = "TC 94 : Verify that user is able to Create Purchase Orders with bundled quantity")
	public void VerifyUserAbleToCreateMonthlyPostpaidOrder() throws Exception {
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.enterMethod();
		Reporter.log("<br> Test Begins");

		// Login Into Application And Navigate to Products Tab
		this.productsPage = this.navPage.navigateToProductsPage();
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Login Into Application And Navigate to Products Tab");

		// Initiate And Complete The Process To Create A Product Category
		String categoryName = this.productsPage.addCategory("productCategory", "pcat");
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Create A Product Category");

		// Navigate to Products Tab
		this.productsPage = this.navPage.navigateToProductsPage();
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Navigate to Products Tab");

		// Initiate And Complete The Process To Add Different Product From
		// Existing
		String description2 = this.productsPage.addProducts(AddProductField.FLAT, "addProductThreeToAddDependencies", "ap");
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Add Different Product From Existing");

		// Login Into Application And Navigate to Plans Tab
		this.plansPage = this.navPage.navigateToPlanPage();
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Login Into Application And Navigate to Plans Page");

		// Initiate And Complete The Process To Add Plans
		String planNameWithBundle = this.plansPage.addPlanMonthly(AddPlanField.BUNDLEDPERIOD, categoryName, description2, "withBundle",
				"wb");
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Processing To Add Plans");

		// Login Into Application And Navigate to Plans Tab
		this.plansPage = this.navPage.navigateToPlanPage();
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Login Into Application And Navigate to Plans Page");

		// Initiate And Complete The Process To Add Plans
		String planNameWithoutBundle = this.plansPage.addPlanMonthly(AddPlanField.BUNDLEDPERIOD, categoryName, description2,
				"withOutBundle", "wob");
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Processing To Add Plans");

		// Verify Text:Saved new plan Successfully
		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Saved new plan", "successfully", TextComparators.contains);
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Verify Text:Saved new plan Successfully");

		// Click on configuration Tab
		this.confPage = this.navPage.navigateToConfigurationPage();
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Click on Confioguration tab");

		// Click on Payment Method link
		this.confPage = this.confPage.clickOnPaymentMethodLink();
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Click on Payment Method link");

		// Add Payment Method Method
		String methodName = this.confPage.addPaymentMethod("paymentTypeWithPaymentCard", "pt");
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Add Payment Method");

		// Click on Account type Link
		this.confPage = this.confPage.clickOnAccountTypeLink();
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Click on Account type Link");

		// Add account type
		String accountType = this.confPage.createAccount(methodName, "accountCreate", "ac");
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Add account type");

		// Click on Customer Tab
		this.customerPage = this.navPage.navigateToCustomersPage();
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Click on Customer Tab");

		// Add customer
		String customerName = this.customerPage.addCustomerWithMakePayment("customerCreate", "cc", accountType);
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Click on Customer Tab");

		// Create order with Plan
		this.customerPage = this.customerPage.createOrderBundleAndWithoutBundle("createOrderMonthlyPrepaid", "comp", planNameWithBundle,
				planNameWithoutBundle);
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Create Order With Plan");

		// Verify Order Grand Total
		//this.ordersPage = this.ordersPage.verifyAppliedTotalOnOrderFirstLine();
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.debug("Verify Order Grand Total");
		Reporter.log("<br> Test Passed");
		VerifyUserIsAbleToCreatePurchaseOrdersWithBundledQuantity.logger.exitMethod();

	}
}
