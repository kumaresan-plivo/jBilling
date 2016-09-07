package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserIsAbleToCancelPurchaseOrder extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	String graceId = null;
	ITestResult result;

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	@Test(description = "TC 97 : Verify that user is able to see all Order details of Respective customer")
	public void VerifyUserIsAbleToCancelPurchasingOrder() throws Exception {
		Reporter.log("<br> Test Begins");

		this.confPage = this.navPage.navigateToConfigurationPage();
		this.confPage = this.confPage.clickOnPaymentMethodLink();
		String methodName = this.confPage.addPaymentMethod("paymentTypeWithPaymentCard", "pt");
		this.confPage = this.confPage.clickOnAccountTypeLink();
		String accountType = this.confPage.createAccount(methodName, "accountCreate", "ac");
		this.customerPage = this.navPage.navigateToCustomersPage();
		String customerName = this.customerPage.addCustomerWithMakePayment("customerCreate", "cc", accountType);
		this.customerPage = this.customerPage.createOrderForCancel("createOrderMonthlyPrepaid", "comp");
		this.ordersPage = this.ordersPage.verifyOrderPage();
	}
}
