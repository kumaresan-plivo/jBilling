package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class TestGeneratedCommissionPostInvoiceGeneration extends BrowserApp {

    ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	@Test(description = "Test Case 16.4 : Verify that correct commission is generated for the " + "Agent after order invoice is generated")
	public void testGeneratedCommissionPostInvoiceGeneration() throws Exception {

		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "10909930");

		this.customerPage = this.navPage.navigateToCustomersPage();
		this.customerPage = this.customerPage.selectCustomer(this.pr.readTestData("TC_6.2_CHILD_CUSTOMER_NAME"));
		this.customerPage = this.customerPage.clickCreateOrder();
		this.customerPage.generateInvoice("Customer A", "ca");

		this.confPage = this.navPage.navigateToConfigurationPage();
		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.AgentCommissionProcess);
		this.confPage = this.confPage.addBillingProcess("BillingProcess", "cbp");
		this.confPage = this.confPage.clickRunCommmisionToBillingProcess();
		this.confPage = this.confPage.verifySavedCommision();

		this.agentsPage = this.navPage.navigateToAgentsPage();
		this.agentsPage = this.agentsPage.clickAgentItem();
		this.agentsPage = this.agentsPage.showCommission();
		this.agentsPage = this.agentsPage.verifyUIComponent();

		Reporter.log("<br> Test Passed");
	}
}
