package com.jbilling.test;

import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddProductField;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserIsAbleToSetUpFeeProductAndRelatedPlugInToBeUsedAsCancellationFee extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Test(description = "TC 53 : Verify User Is Able To Set Up Fee Product And Related Plug In To Be Used As CancellationFee", groups = { "globalRegressionPack" })
	public void SetUpFeeProductAndRelatedPlugInToBeUsedAsCancellationFee() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Product Page");

		String category = this.productsPage.addCategory("productCategory", "pcat");

		logger.debug("Create A Product Category");

		this.confPage = this.confPage.validateCategoriesSavedTestData(category);
		logger.debug("Validate Saved New Product successfully");

		this.productsPage.addProducts(AddProductField.FLAT, "addProductOneToAddDependencies", "pou");
		logger.debug("Add Different Product From Existing");

		this.productsPage.Createanotherproduct(AddProductField.FLAT, "addProductTwoToAddDependencies", "pok");
		logger.debug("Add Different Product From Existing");

		this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Plugins);
		logger.debug("Select plugins from Configuration list");

		this.confPage = this.confPage.verifyPluginscategoriesPageHeader();
		logger.debug("Add Plugin Page Header Verified");

		this.confPage = this.confPage.ClickOnEventListner();
		logger.debug("Select plugins from Configuration list");

		this.confPage = this.confPage.verifyAddPluginPageHeader();
		logger.debug("Add Plugin Page Header Verified");

		this.confPage = this.confPage.enterTestDataInOnPlugnin("OrderPluginPageInfo", "oi");
		logger.debug("Select plugins from Configuration list");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.OrderChangeStatuses);
		logger.debug("Select OrderChangeStatuses from Configuration list");

		this.confPage = this.confPage.CheckboxOrderChangeStatuses();
		logger.debug("Select OrderChangeStatuses from Configuration list");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
