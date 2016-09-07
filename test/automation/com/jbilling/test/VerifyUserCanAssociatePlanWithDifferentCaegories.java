package com.jbilling.test;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserCanAssociatePlanWithDifferentCaegories extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Test(description = "TC 143: Verify user can associate plan with different (OPO/OPC) caegories.")
	public void validateMetaFiledtypeSelection() throws Exception {

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Click on the Product Tab");

		String categoryName1 = this.productsPage.createCategoryWithOneCustomer("addCategoryOnePerCustomer", "acopc");
		logger.debug("Add new category");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Click on the Product Tab");

		String categoryName2 = this.productsPage.createCategoryWithOneOrder("addCategoryforOnePerOrder", "acopo");
		logger.debug("Add new category");

		String description = this.productsPage.addProductInOnePerOrderCategory("addProdutcforOnePerOrder", "apopo", categoryName1);
		logger.debug("Add product in category with one per order");

		this.plansPage = this.navPage.navigateToPlanPage();
		logger.debug("Click on Plan Menu");

		String planDescription = this.plansPage.addProductInMultipleCategoryInPlan("addProductOnePerOrder", "ap", categoryName1,
				categoryName2, description);
		logger.debug("Create product in Plan");

		this.plansPage = this.plansPage.verifyCategoryName(planDescription, categoryName1, categoryName2);
		logger.debug("Validate Added Category in plan");
	}
}
