package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.setProductCategoryWithAssetMgmt;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

public class VerifyUserAbleToCreateAssetMgmtWithDiffCategories extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
    ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	@Test(groups = { "globalRegressionPack" })
	public void checkCreateAssetMgmtWithDiffCategories() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Plugins);
		logger.debug("Select Plug in from the list");

		this.confPage = this.confPage.selectPluginCategory("selectPluginCategory", "pc");
		logger.debug("Select Plugin Category");

		this.confPage = this.confPage.addNewPluginInCategory("addPlugin", "ap", this.runTimeVariables);
		logger.debug("Add new plugin");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Products Page");

        String savedCategoryName = this.pr.readTestData("TC_3.2_CATEGORY_NAME");
        String strMetaFieldName  = this.pr.readTestData("strMetaField");
        String intMetaFieldName  = this.pr.readTestData("intMetaField");
        String boolMetaFieldName = this.pr.readTestData("boolMetaField");

		this.productsPage.addProductCategoryWithAssetMgmt(savedCategoryName, boolMetaFieldName,
		        setProductCategoryWithAssetMgmt.PCWAMG2, "CreateProductCategory", "ac");
		this.productsPage.addProductCategoryWithAssetMgmt(savedCategoryName, intMetaFieldName,
		        setProductCategoryWithAssetMgmt.PCWAMG3, "CreateProductCategory", "ac");
		this.productsPage.addProductCategoryWithAssetMgmt(savedCategoryName, boolMetaFieldName,
		        setProductCategoryWithAssetMgmt.PCWAMG4, "CreateProductCategory", "ac");
	}
}
