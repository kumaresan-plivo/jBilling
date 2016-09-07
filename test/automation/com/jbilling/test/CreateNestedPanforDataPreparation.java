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
@Test(groups = {"automation"})
public class CreateNestedPanforDataPreparation extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "87", groups = { "globalRegressionPack" })
	public void CreateNestedPanforDataPreparationTest() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to configuration Page");

		this.confPage = this.confPage.adddNewOrderStatus("OrderChangeStatus", "ocs", true);
		logger.debug("Create a New Order Status");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug(" Navigate to Product Page");

		String AddedCategory = this.productsPage.addCategory("CreateProductCategoryServices", "pcd");
		logger.debug("Add a Category");

		String CategoryID = this.productsPage.getIDAddedCategory(AddedCategory);

		this.productsPage.addProduct(AddProductField.FLATPRICE, "addProductWithNameSMS", "ap");
		logger.debug("Add a Product with Description");

		this.productsPage.addProduct(AddProductField.FLATPRICE, "addProductWithNameGPRS", "ap");
		logger.debug("Add a Product with Description");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Login Into Application And Navigate to Product Page");

		String AddedCategory1 = this.productsPage.addCategory("CreateProductCategoryMSISDN", "pcd");
		logger.debug("Add a category with description");

		String CategoryID1 = this.productsPage.getIDAddedCategory(AddedCategory1);
		this.productsPage.addProduct(AddProductField.FLATPRICE, "addProductWithNameMSISDN", "ap");
		logger.debug("Add a Product with Description");

		this.productsPage.addProduct(AddProductField.FLATPRICE, "addProductWithNameMSISDN_2", "ap");
		logger.debug("Add a Product with Description");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Product Page");

		String AddedCategory2 = this.productsPage.addCategory("CreateProductCategoryTarrifPlans", "pcd");
		logger.debug("Add a Category");
		String CategoryID2 = this.productsPage.getIDAddedCategory(AddedCategory2);
		String AddedCategory3 = this.productsPage.addCategory("CreateProductCategoryRates", "pcd");
		logger.debug("Add a Category");
		String CategoryID3 = this.productsPage.getIDAddedCategory(AddedCategory3);
		this.productsPage.addProduct(AddProductField.FLATPRICE, "addProductWithNameSMS2", "ap");
		logger.debug("Add a Product with Description");

		this.productsPage.addProduct(AddProductField.FLATPRICE, "addProductWithNameEU", "ap");
		logger.debug("Add a Product with Description");

		this.productsPage.addProduct(AddProductField.FLATPRICE, "addProductWithNameConnectionFee", "ap");
		logger.debug("Add a Product with Description");

		String productCategory9 = this.productsPage.addProduct(AddProductField.FLATPRICE, "addProductWithNameSMS3", "ap");
		logger.debug("Add a Product with Description");
		String ID = this.productsPage.getIDAddedProduct(productCategory9);
		logger.debug("Get a Product ID ");

		this.productsPage.addProduct(AddProductField.FLATPRICE, "addProductWithNameSMS1", "ap");
		logger.debug("Add a Product with Description");

		this.productsPage = this.productsPage.editProductWithDependency("editProductWithDependency", "ap", AddedCategory3, ID);
		logger.debug("edit a Product with Deependency");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to configuration Page");

		this.confPage = this.confPage.configureOrderChangeTypeWithMulitpleProduct("NewOrderChangeStatus", "ocs", true, CategoryID1,
				CategoryID2);
		logger.debug("Create a New Order Status");

		this.confPage = this.confPage.configureOrderChangeTypeWithoutAllowStatusChange("NewOrderChangeStatusWithoutAllowOrder", "ocs",
				CategoryID, CategoryID3);
		logger.debug("Create a New Order Status without allow Order Status");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Order Change Type created", "successfully", TextComparators.contains);
		logger.debug("Verify Message for Add product Successfully");

		Reporter.log("<br> Test Ends Here");
	}
}
