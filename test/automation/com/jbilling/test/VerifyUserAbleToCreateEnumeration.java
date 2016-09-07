package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddNewEnumeration;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserAbleToCreateEnumeration extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "Test Case 84: Verify that user is able to create Enumeration.", priority = 1)
	public void TC84_CreateEnumerations() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Enumerations);
		logger.debug("navigate to Enumerations");

		String enumerationName = this.confPage.createEnumeration(AddNewEnumeration.VERIFY_MANDATORY_FIELDS, "TC84_EnumValidateMessage",
				"evm");
		logger.debug("Create Enumeration With a Verification of Mandatory Fields");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Enumeration with Id", "saved successfully.", TextComparators.contains);
		logger.debug("Verify Message For Created Enumeration");

		this.confPage = this.confPage.validateEnumerationsSavedData(enumerationName);
		logger.debug("Validate Data Saved For Created Enumeration");

		// Edit Created Enumeration Value
		this.confPage.editConfiguration(enumerationName, "TC84_EnumValidateMessage", "evm");

		this.confPage = this.confPage.verifyDuplicateValueForEnumeration(enumerationName, "TC84_EnumValidateMessage", "evm");
		logger.debug("Verify that error message displayed For saving Enumeration with another Duplicate Value");

		this.confPage = this.confPage.selectEnumerationsFromTable(enumerationName);
		logger.debug("Select Created Enumeration From Enumeration Table");

		this.confPage = this.confPage.checkDeleteYesNo("NO");
		logger.debug("click on NO for Delete Enumeration");

		this.confPage = this.confPage.validateEnumerationsSavedData(enumerationName);
		logger.debug("Validate Data Saved For Created Enumeration Presence");
	}
}
