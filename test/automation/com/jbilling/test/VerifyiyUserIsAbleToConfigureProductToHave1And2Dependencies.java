package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddProductField;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyiyUserIsAbleToConfigureProductToHave1And2Dependencies extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	@Test(description = "TC 76 : Verify User Is Able To Configure Product To Have 1 And 2 Dependencies", groups = { "globalRegressionPack" })
	public void ConfigureaPoductToHave1And2Dependencies() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Products Tab");

		// Initiate And Complete The Process To Create A Product Category
		String firstCatagory = this.productsPage.addCategory("productCategory", "pcet");
		this.runTimeVariables.put("TC_76_CATEGORY_NAME", VerifyUserIsAbleToAddlDependenciesOnProduct.category);
		logger.debug("Create A Product Category");

		this.confPage = this.confPage.validateCategoriesSavedTestData(this.runTimeVariables.get("TC_76_CATEGORY_NAME"));
		logger.debug("Validate Saved New Product successfully");

		this.productsPage.addProducts(AddProductField.FLAT, "addProductTwoToAddDependencies", "kol");
		logger.debug("Add The Product");

		this.productsPage = this.navPage.navigateToProductsPage();
		logger.debug("Navigate to Products Tab");

		String secondCategory = this.productsPage.addCategory("NewProductCategoryData", "per");
		this.runTimeVariables.put("TC_43.1_CATEGORY_NAME", secondCategory);
		logger.debug("Create Second Product Category");

		this.confPage = this.confPage.validateCategoriesSavedTestData(this.runTimeVariables.get("TC_76_CATEGORY_NAME"));
		logger.debug("Validate Saved New Product successfully");

		this.productsPage.addProducts(AddProductField.FLAT, "addProductTwoToAddDependencies", "tgb");
		logger.debug("Add Different Product From Existing");

		this.productsPage.Createanotherproduct(AddProductField.FLAT, "addProductTwoToAddDependencies", "idj");
		logger.debug("Initiate And Complete The Process To Again Add The Product");

		this.productsPage.EditProducts(firstCatagory, "OrderPluginPageInfo1", "oi", secondCategory);
		logger.debug("Click on existing product and Edit the product");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(GlobalEnumsPage.PageConfigurationItems.PaymentMethod);
		logger.debug("Select Configuration");

		this.confPage = this.confPage.SelectPaymentMethodTemplate("configurePaymentTemplateMethod", "pom");
		logger.debug("Configure Payment Method to Account");

		String methodName = this.confPage.addrecurringPaymentMethodDetails("TestPaymentType", "HFG");
		this.runTimeVariables.put("TC_2.1.1_METHOD_NAME_ONE", methodName);
		logger.debug("Add Payment Details");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Payment Method Type", "created successfully", TextComparators.contains);
		logger.debug("Verify Payment Method Type Is Created Successfully");

		this.confPage.selectConfiguration(PageConfigurationItems.AccountType);
		logger.debug("Navigate to account type");

		String accountTypename = this.confPage.accounttype("AccountTypeName", "oii", methodName);
		logger.debug("Create a new account type");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate To Customers Page");

		this.customerPage.addNewCustomer(accountTypename, methodName, "NewCustomerInfo", "ldr");
		logger.debug("Initiate And Complete The Process To Add a New Customer");

		this.customerPage.createOrderCustomer("EditCustomerInfo", "ysk");
		logger.debug("Initiate And Complete The Process to Edit customer");
	}
}
