package com.jbilling.test;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserIsAbleToCloneTheAccountType extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Test(description = "TC 58 : Verify a Customer CC number is masked on customer 'Details page','Edit Customer' page and blacklist page.")
	public void verifyCustomerCCNumberOnDetailPage() throws Exception {

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration page");

		this.confPage = this.confPage.clickOnPaymentMethodLink();
		logger.debug("Click on Payment Method link");

		this.confPage.addPaymentMethod("testPaymentType", "pt");
		logger.debug("Add Payment Method");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration page");

		this.confPage = this.confPage.setConfigurationPreference("setPreferenceValue", "pc");
		logger.debug("Set Preference Value");

		String accountTypeName = this.confPage.addAccountTypeWithInvoiceDesign("addAccountWithInvoiceDesign", "aaid");
		logger.debug("Add an Account informationType");

		this.confPage = this.confPage.validateAccountTypeSavedTestData(accountTypeName);
		logger.debug("Validate Added Account type Data");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration page");

		this.confPage = this.confPage.createCloneAccountType("cloneAccountType", "cat", accountTypeName);
		logger.debug("Create a Clone Account Type");
	}
}
