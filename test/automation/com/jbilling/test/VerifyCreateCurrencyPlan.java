package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddProductField;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

public class VerifyCreateCurrencyPlan extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	String graceId = null;
	ITestResult result;
	String ProductID = "null";
	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	/**
	 * TC_360 Verify, correct currency in UI is displayed while creating plan
	 * with 'Won'
	 */

	@Test(description = "TC_360 Verify, correct currency in UI is displayed while creating plan with 'Euro Currency'", groups = {
			"globalRegressionPack" })

	public void TC_360CreateCurrencyPlan() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Clicking in Recent Order Button");

		String category = this.productsPage.addCategory("Fresh_Category", "cat001");
		logger.debug("Adding Category");

		String product = this.productsPage.addProduct(AddProductField.FLATPRICE, "addProductFresh", "prod");
		logger.debug("Adding Product ");

		this.plansPage = this.navPage.navigateToPlanPage();
		logger.debug("Adding Plan in to it");

		String PlanDesc = this.plansPage.addPlanWithDifferentCurrency(category, product, "addplanwithdiffer", "currplan");
		this.propReader.updatePropertyInFile("TC_360_planDescription", PlanDesc, "testData");
		logger.debug("Add Plan in to it");

		this.msgsPage.verifyDisplayedMessageText("Saved new plan ", "successfully", TextComparators.contains);
		logger.debug("Save Plan");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();

	}

}
