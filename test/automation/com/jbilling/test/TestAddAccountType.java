package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AccountTypeField;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = {"automation"})
public class TestAddAccountType extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 2.1 : Verify ability to successfully configure and edit Account Types "
			+ "\"Direct Customer\" & \"Distributor Account\" >> Verify the ability to set Jasper"
			+ " invoice design as default for invoice download")
	public void testAddAccountType() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047241");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.setConfigurationPreference("setPreferenceValue", "pc");
		logger.debug("Set Configuration Preference Value");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.AccountType);
		logger.debug("Select Configuration");

		String accountName = this.confPage.createAccountType("addAccountType", "aat");
		this.propReader.updatePropertyInFile("TC_2.1_ACCOUNT_NAME_ONE", accountName, "testData");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.AccountType);
		logger.debug("Select Configuration");

		accountName = this.confPage.createAccountType("addSecondAccountType", "aat");
		this.propReader.updatePropertyInFile("TC_2.1_ACCOUNT_NAME_TWO", accountName, "testData");

		accountName = this.confPage.editAccountType(AccountTypeField.ACCOUNT_NAME, "addAccountType", "aat");
		logger.debug("Edit Second Account Type");

		this.propReader.updatePropertyInFile("TC_2.1_ACCOUNT_NAME_TWO_EDIT", accountName, "testData");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Account Type", "updated successfully", TextComparators.contains);
		logger.debug("Verify Text: Account Type Updated Successfully");

		String accountType = this.propReader.readPropertyFromFile("TC_2.1_ACCOUNT_NAME_TWO_EDIT", "testData");
		this.confPage = this.confPage.validatePeriodsSavedTestData(accountType);
		logger.debug("Validate Saved Account Type Test Data");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
