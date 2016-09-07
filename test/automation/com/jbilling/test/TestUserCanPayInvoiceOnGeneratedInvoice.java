package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

public class TestUserCanPayInvoiceOnGeneratedInvoice extends BrowserApp {
    ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	// N.B.  Depends on TestReportForInvoice.testReportForInvoice

	@Test(description = "Test Case 13.1 : Verify user can pay invoice on a billing process generated invoice")
	public void testUserCanPayInvoiceOnGeneratedInvoice() throws Exception {

		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909923");

		this.navPage.navigateToInvoicesPage();
		String customerName = this.pr.readTestData("TC_14.1_CUSTOMER_NAME");
		this.invoicePage.payInvoice(customerName, "payInvoice", "aa");
		this.ordersPage.verifyUIComponent();
		Reporter.log("<br> Test Passed");
	}
}
