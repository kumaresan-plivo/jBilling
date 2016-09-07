package com.jbilling.test;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyCreatedAITMetaFieldInIncludeInNotificationDropDown extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Test(description = "TC 342 :Verify that created AIT meta fields are displayed in 'Include in notifications' droplist after editing.")
	public void addAITMetafieldToAccountType() throws Exception {

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
		logger.debug("Click on config Page");

		this.confPage = this.confPage.verifyAddedMetafieldIsDisplayingIncludeNotificationDropDown(accountTypeName, accountName);
		logger.debug("Verify added Metafiled account is preset in Include in Notification");
	}
}
