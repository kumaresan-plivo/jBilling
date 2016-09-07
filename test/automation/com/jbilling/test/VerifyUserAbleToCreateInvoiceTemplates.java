package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

public class VerifyUserAbleToCreateInvoiceTemplates extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(description = "TC 75 : Verify that user is able to create new invoice templates.", groups = { "globalRegressionPack" })
	public void addInvoiceTemplate() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Login Into Application And Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.InvoiceTemplates);
		logger.debug("Select Invoice Templates from Configuration list");

		// Add New Invoice Template with name "Template-1"
		String AddinvoiceTemplates1 = this.confPage.addInvoiceTemplate("addInvoiceTemplateName", "additn");
		logger.debug("Add New Invoice Templates");

		// Add New Invoice Template with name "Template-2"
		String AddinvoiceTemplates2 = this.confPage.addInvoiceTemplate("addInvoiceTemplateName", "additn");
		logger.debug("Add New Invoice Templates");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Invoice Template ", "Created Invoice Template", TextComparators.contains);
		logger.debug("Verify Message For Created Invoice Template");

		this.confPage = this.confPage.validateInvoiceSavedTestData(AddinvoiceTemplates1);
		logger.debug("Validate Saved Invoice Templates Test Data");

		this.confPage = this.confPage.validateInvoiceSavedTestData(AddinvoiceTemplates2);
		logger.debug("Validate Saved Invoice Templates Test Data");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
