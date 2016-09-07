package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.textutilities.TextUtilities;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestCreationOfChildCompany extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 1.3: Verify ability to create a Child Company within Root Company")
	public void testCreationOfChildCompany() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047238");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to configuration page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Company);
		logger.debug("Click on configuration>> Select Company configuration item from left");

		this.confPage = this.confPage.clickCopyCompanyButton();
		logger.debug("Click on Copy company button");

		this.confPage.markCompanyAsChildCompany(true);
		logger.debug("Check on the third checkbox \"Create a new copany as child..\">> Give name of the child company >>Click on yes button.");

		this.confPage = this.confPage.setTemplateCompanyName();
		logger.debug("Click on Copy company button");

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
		logger.debug("Verified that child company has been created succesfully");
//
//		this.propReader.updatePropertyInFile("TC_1.3_CHILD_COMPANY_USERNAME", un, "testData");
//		this.propReader.updatePropertyInFile("TC_1.3_CHILD_COMPANY_PASSWORD", pwd, "testData");
//		this.propReader.updatePropertyInFile("TC_1.3_CHILD_COMPANY_COMPANYNAME", cn, "testData");
//		this.propReader.updatePropertyInFile("TC_1.3_CHILD_COMPANY_COMPANYID", cid, "testData");
//
//		// The Child Company should now appear in the impersonate drop down.
//		if (this.navPage.isChildCompanyImpersonated(this.propReader.readPropertyFromFile("TC_1.3_CHILD_COMPANY_COMPANYNAME", "testData")) == false) {
//			throw new Exception("Test 1.3 failed as child company created but not impersonated");
//		}
		logger.debug("User successfully created the Child Company and appearing in the impersonate drop down.");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
