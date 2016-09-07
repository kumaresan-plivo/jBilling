package com.jbilling.test;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyOnlyOneAITMetaFieldAerAccountTypeCanBeEnabledAtTime extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Test(description = "TC 343 : Verify that only one AIT meta-field per account type can be enabled at a time")
	public void validateMetaFiledtypeSelction() throws Exception {

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration Page");

		this.confPage = this.confPage.clickOnPaymentMethodLink();
		logger.debug("Click on Payment Method link");

		this.confPage.addPaymentMethod("testPaymentType", "pt");
		logger.debug("Add Payment Method");

		String accountTypeName = this.confPage.addAccountType("addAccountInformationType", "aait");
		logger.debug("Add an Account informationType");

		String paymentAddress1 = this.confPage.addAITMetaFieldToAccountType("addAccountInformationType", "aait", accountTypeName);
		logger.debug("Add Metafield to an Account Type");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration Page");

		this.confPage = this.confPage.clickOnAccountTypeLink();
		logger.debug("Click on Account Type Link");

		String paymentAddress2 = this.confPage.addAITMetaFieldToAccountType("addAccountInformationType", "aait", accountTypeName);
		logger.debug("Add Metafield to an Account Type");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration Page");

		this.confPage = this.confPage.verifyUserCannotSelectBothPaymentMethod(accountTypeName, paymentAddress1, paymentAddress2);
		logger.debug("Verify User can not select multiple Payment Address");
	}
}
