package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.pageclasses.MessagesPage;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyCreateChildCompany extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	String graceId = null;
	ITestResult result;
	String ProductID = "null";
	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	/**
	 * TC_297 Verify that user is able to create Child Company
	 */

	@Test(description = "TC_297 Verify that user is able to create Child Company,", groups = { "globalRegressionPack" }, priority = 0)

	public void createchildcompanydata() throws Exception {
		VerifyCreateChildCompany.logger.enterMethod();
		Reporter.log("<br> Test Begins");
		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		VerifyCreateChildCompany.logger.debug("Login Into Application And Navigate to Agent Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Company);
		VerifyCreateChildCompany.logger.debug("Switching to Currencies configuration item");

		this.confPage = this.confPage.clickCopyCompanyButton();
		logger.debug("Click on Copy company button");

		this.confPage.markCompanyAsChildCompany(true);
		logger.debug("Check on the third checkbox \"Create a new copany as child..\">> Give name of the child company >>Click on yes button.");

		this.confPage = this.confPage.setTemplateCompanyName();
		logger.debug("Click on Copy company button");

		this.confPage = this.confPage.clickConfirmPopupYesButton();
		logger.debug("Accept The Confirm Popup");
		
		MessagesPage.isIntermediateSuccessMessageAppeared();
		VerifyCreateChildCompany.logger.debug("Switching to Currencies configuration item");

		Reporter.log("<br> Test Passed");
		VerifyCreateChildCompany.logger.exitMethod();

	}
}