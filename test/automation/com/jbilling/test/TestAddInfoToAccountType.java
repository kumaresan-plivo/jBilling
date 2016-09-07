package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class })
@Test(groups = {"automation"})
public class TestAddInfoToAccountType extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 2.2 : Verify ability to successfully add an Information Type to an Account Type")
	public void testAddInfoToAccountType() throws Exception {

		logger.enterMethod();
		Reporter.log("<br>Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047243");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(GlobalEnumsPage.PageConfigurationItems.AccountType);
		this.confPage = this.confPage.selectAccountTypeName(this.pr.readTestData("TC_2.1_ACCOUNT_NAME_ONE"));
		logger.debug("Add Information to Account Type");

		String infoTypeName = this.confPage.addNewInformationToSelectedAccountType("AddInfoToAccountType", "aitac");
		this.propReader.updatePropertyInFile("TC_2.2_NAME", infoTypeName, "testData");
		logger.debug("Enter information type name for account type");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Account Information Type", "created successfully",
				TextComparators.contains);
		logger.debug("Account Information Type Is Created Successfully");

		String accountInfo = this.pr.readTestData("TC_2.2_NAME");
		this.confPage = this.confPage.validatePeriodsSavedTestData(accountInfo);
		logger.debug("Validate Saved Account Information Type Test Data");

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		logger.debug("Verifying if account information type created successfully or not");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
