package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class })
@Test(groups = {"automation"})
public class TestAgentsLinkedToCustomer extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	@Test(description = "Test Case 16.1 : Verify that Agents can be made and linked to a customer")
	public void testAgentsLinkedToCustomer() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909927");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.setConfigurationPreference("setPreferenceValue", "pc");
		logger.debug("Set Configuration Preference Value");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.AccountType);
		logger.debug("Select Configuration");

		String accountName = this.confPage.createAccountType("addAccountType", "aat");
		this.runTimeVariables.put("TC_2.1_ACCOUNT_NAME_ONE", accountName);

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		this.confPage = this.confPage.selectConfiguration(GlobalEnumsPage.PageConfigurationItems.PaymentMethod);
		logger.debug("Select Configuration");

		this.confPage = this.confPage.configurePaymentMethod("configurePaymentMethod", "pm");
		logger.debug("Configure Payment Method to Account");

		String methodName = this.confPage.addPaymentMethodDetails("testPaymentType", "pt");
		this.runTimeVariables.put("TC_2.1.1_METHOD_NAME_ONE", methodName);
		logger.debug("Add Payment Details");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Payment Method Type", "created successfully", TextComparators.contains);
		logger.debug("Verify Payment Method Type Is Created Successfully");

		String paymentMethod = this.propReader.readPropertyFromFile("TC_2.1.1_METHOD_NAME_ONE", "testData");
		this.confPage = this.confPage.validatePeriodsSavedTestData(paymentMethod);
		logger.debug("Validate Saved Account Type Test Data");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		this.agentsPage = this.navPage.navigateToAgentsPage();
		logger.debug("Navigate To Agents Page");

		String agent = this.agentsPage.addAgent("addAgent", "aa");

		this.propReader.updatePropertyInFile("TC_16.1_AGENT", agent, "testData");
		logger.debug("Add Agent");

		this.customerPage = this.navPage.navigateToCustomersPage();
		logger.debug("Navigate to Customers Page");

		String customerName = this.customerPage.addCustomer(this.runTimeVariables.get("TC_2.1_ACCOUNT_NAME_ONE"),
		this.runTimeVariables.get("TC_2.1.1_METHOD_NAME_ONE"), "new_customer", "nc");
		this.runTimeVariables.put("TC_6.1_CUSTOMER_NAME", customerName);
		logger.debug("Add a New Customer");

		this.customerPage = this.customerPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
