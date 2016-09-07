package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

public class TestVerifyInvoiceGeneration extends BrowserApp {

    ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 10.1 : Generating an Invoice (Manually) ")
	public void testVerifyInvoiceGeneration() throws Exception {

		Reporter.log("Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909915");

        String customerName = appendRandomChars("Customer-tc_10_1-");
        String accountTypeName = this.pr.readTestData("TC_3.5_ACCOUNT_NAME_ONE");

        this.navPage.navigateToCustomersPage();
        this.customerPage.addCustomerWithAccountType(customerName, accountTypeName);
        this.customerPage.selectCustomerToAddOrder(customerName);
        this.customerPage.createOrderForInvoice ("Customer A", "ca");

		this.navPage.navigateToOrdersPage();
		this.filtersPage.filterOnLoginNameOrCustomerName(customerName);
		this.ordersPage.verifyInvoiceGenerationForChoosenCustomer(customerName);

		Reporter.log("Test Passed");
	}
}
