package com.jbilling.test;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyMetaFieldTypeSelectionISNotAvailableWhileAddingNewMetaFieldExceptAccountType extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Test(description = "TC 343 : Meta field type select is not available while adding the new meta field in expected Account type")
	public void validateMetaFiledtypeSelction() throws Exception {

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration page");

		this.confPage = this.confPage.validateMetaFieldSelectionForOrder();
		logger.debug("Validate the Meta filed by default rule value");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration page");

		this.confPage = this.confPage.validateMetaFieldSelectionForCustomer();
		logger.debug("Validate the Meta filed by default rule value");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Click on Configuration page");

		this.confPage = this.confPage.validateMetaFieldSelectionForPayment();
		logger.debug("Validate the Meta filed by default rule value");
	}
}
