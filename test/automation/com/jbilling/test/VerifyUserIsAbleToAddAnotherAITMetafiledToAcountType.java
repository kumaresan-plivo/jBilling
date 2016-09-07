package com.jbilling.test;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserIsAbleToAddAnotherAITMetafiledToAcountType extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Test(description = "TC 341 : Verify that user is able to add another AIT meta-field  to the account type ")
	public void addAnotherAITMetafieldToExistingAccountType() throws Exception {

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration page");

		this.confPage = this.confPage.clickOnPaymentMethodLink();
		logger.debug("Click on Payment Method link");

		this.confPage.addPaymentMethod("testPaymentType", "pt");
		logger.debug("Add Payment Method");

		String accountTypeName = this.confPage.addAccountType("addAccountInformationType", "aait");
		logger.debug("Add an Account informationType");

		this.confPage = this.confPage.validateAccountTypeSavedTestData(accountTypeName);
		logger.debug("Validate Added Account type Data");

		String accountName = this.confPage.addAITMetaFieldToAccountType("addAccountInformationType", "aait", accountTypeName);
		logger.debug("Add Metafield to an Account Type");

		this.confPage = this.confPage.validateAccountInformationTypeSavedTestData(accountName);
		logger.debug("Validate metaield in an account information type");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration page");

        this.confPage.clickOnAccountTypeLink();

		String accountName1 = this.confPage.addAITMetaFieldToAccountType("addAccountInformationType", "aait", accountTypeName);

		this.confPage = this.confPage.validateAccountInformationTypeSavedTestData(accountName1);
		logger.debug("VAlidate metaield in an account information type");
	}
}
