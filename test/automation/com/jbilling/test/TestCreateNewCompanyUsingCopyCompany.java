package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.textutilities.TextUtilities;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestCreateNewCompanyUsingCopyCompany extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	@Test(description = "Test Case 1.1:  testCreateNewCompanyUsingCopyCompany")
	public void testCreateNewCompanyUsingCopyCompany() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047236");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Company);
		logger.debug("Select Company from Configuration list");

		this.confPage = this.confPage.clickCopyCompanyButton();
		logger.debug("Creating Copy of current company");

		this.confPage = this.confPage.clickConfirmPopupYesButton();
		logger.debug("Accept The Confirm Popup");

		String un = this.confPage.extractUserNameFromCompanyCreationMessage();
		String pwd = this.confPage.extractPasswordFromCompanyCreationMessage();
		String cn = this.confPage.extractCompanyNameFromCompanyCreationMessage();
		String cid = this.confPage.extractCompanyIdFromCompanyCreationMessage();

		if (TextUtilities.isBlank(un) || TextUtilities.isBlank(pwd) || TextUtilities.isBlank(cn) || TextUtilities.isBlank(cid)) {
			throw new Exception("Test failed for copying company as no information generated. UserName: " + un + " -> Password: " + pwd
					+ " -> CompanyName: " + cn + " -> CompanyId: " + cid);
		}

		this.propReader.updatePropertyInFile("TC_1.1_CREDENTIALS_USERNAME", un, "testData");
		this.propReader.updatePropertyInFile("TC_1.1_CREDENTIALS_PASSWORD", pwd, "testData");
		this.propReader.updatePropertyInFile("TC_1.1_CREDENTIALS_COMPANYNAME", cn, "testData");
		this.propReader.updatePropertyInFile("TC_1.1_CREDENTIALS_COMPANYID", cid, "testData");

		this.runTimeVariables.put("ENVIRONMENT_UNDER_TEST", this.pr.readConfig("EnvironmentUnderTest"));
		this.runTimeVariables.put("ENVIRONMENT_URL_UNDER_TEST",
				this.pr.readConfig(this.runTimeVariables.get("ENVIRONMENT_UNDER_TEST") + "_URL"));

		this.confPage = this.confPage.verifyUIComponent();
		logger.debug("Verify Current Page UI Component");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
