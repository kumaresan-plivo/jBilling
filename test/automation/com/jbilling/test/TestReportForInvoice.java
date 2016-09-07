package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

public class TestReportForInvoice extends BrowserApp {
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 14.1 : Verify correct report is displayed.")
	public void testReportForInvoice() throws Exception {

		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909924");

		this.navPage.navigateToCustomersPage();

		String CustomerName = this.customerPage.addCustomerWithMakePayment("S_TC14.1_ReportForInvoices", "rfi",
				this.pr.readTestData("TC_3.5_ACCOUNT_NAME_ONE"));
		this.propReader.updatePropertyInFile("TC_14.1_CUSTOMER_NAME", CustomerName, "testData");

		this.customerPage.selectCustomerToAddOrder(CustomerName);
		this.customerPage.generateInvoice("Customer A", "ca");

		this.navPage.navigateToCustomersPage();
		this.customerPage.selectCustomerToMakePayment(CustomerName);
		this.invoicePage.makePayment("payInvoice", "aa");
		this.navPage.navigateToReportsPage();
		this.reportsPage.getReportsView("selectReportType", "sra");
		this.ordersPage.verifyUIComponent();

		Reporter.log("<br> Test Passed");
	}
}
