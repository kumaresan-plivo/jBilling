package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

/**
 * Current Application Page Stack || LoginPage--> loginPage; NavigatorPage-->
 * navPage; HomePage--> homePage; ConfigurationPage--> confPage; AgentsPage-->
 * agentsPage; CustomersPage--> customerPage; ProductsPage--> productsPage;
 * PlansPage--> plansPage; OrdersPage--> ordersPage; InvoicePage--> invoicePage;
 * ReportsPage--> reportsPage; DiscountsPage--> discountsPage; FiltersPage-->
 * filtersPage; PaymentsPage--> paymentsPage; MessagesPage--> msgsPage;
 *
 *
 **/
public class VerifyAccountTypeNotification extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	String graceId = null;
	ITestResult result;
	static String category = "";

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	/*
	 * TC_346 Verify that when user check Use In Notification checkbox at AIT
	 * metafield page then it is automatically selected at account type")
	 **/

	@Test(description = "Verify that when user check Use In Notification checkbox at AIT metafield page then it is automatically selected at account type", groups = {
			"globalRegressionPack" })

	public void TC_346addAITMetafieldToAccountType() throws Exception {

		Reporter.log("<br> Test Passed");
		logger.exitMethod();

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.PaymentMethod);
		logger.debug("Click on Payment Method link");

		this.confPage.addPaymentMethod("testPaymentType", "pt");
		logger.debug("Add Payment Method");

		String accountTypeName = this.confPage.addAccountType("addAccountInformationType", "aait");
		logger.debug("Add an Account informationType");

		this.confPage = this.confPage.validateAccountTypeSavedTestData(accountTypeName);
		logger.debug("Validate Added Account type Data");

		String accountName = this.confPage.addAITMetaFieldToAccountType("addAccountInformationType", "aait", accountTypeName);
		this.propReader.updatePropertyInFile("metafield", accountName, "testData");
		logger.debug("Add Metafield to an Account Type");

		this.confPage = this.confPage.validateAccountInformationTypeSavedTestData(accountName);
		logger.debug("VAlidate metaield in an account information type");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.AccountType);
		logger.debug("Naviagate To account Type Page");

		this.confPage.clickRecentlyCreatedAccountType();
		logger.debug("Clicking Recently Customer ");

		this.confPage.clickOnMetafieldID();
		logger.debug("Clicking Recently Customer ");

		this.confPage.clickEditAIT();
		logger.debug("Clicking Edit Account button ");

		this.confPage.clickUserNotification(true);
		logger.debug("Click in Uer Notification ");

		this.confPage.popupyesait();
		logger.debug("Click Yes in AIT Meta field");

		this.confPage.clickSaveChangesButton();
		logger.debug("Saving MetaField");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.AccountType);
		logger.debug("Naviagate To account Type Page");

		this.confPage.clickRecentlyCreatedAccountType();
		logger.debug("Clicking Recently Account Type  ");

		this.confPage.clickEditAccountTypeButton();
		logger.debug("Clicking Edit Account Type Button ");

		this.confPage.verufydropdownIncludeinNotifications(accountName);
		logger.debug("Verify dropdown value");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}