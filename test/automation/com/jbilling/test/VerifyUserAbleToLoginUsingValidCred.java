package com.jbilling.test;

import java.util.HashMap;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.LoginField;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class })
@Test(groups = { "automation" })
public class VerifyUserAbleToLoginUsingValidCred extends BrowserApp {

	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	ITestResult result;

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	@Test(description = "Test Case 1.1: Verify that users can login into the JBilling System using valid credential")
	public void TC01_1_loginWithInvalidCredentials() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.loginPage = this.navPage.logoutApplication();
		logger.debug("Logout From The Application");

		this.loginPage = this.loginPage.invalidLogin(LoginField.ALL, "InvalidCredentials", "ul");
		logger.debug("Invalid Credentials Entered");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("Sorry, we were not able to find a user",
				"that login id and password for the selected company", TextComparators.contains);
		logger.debug("Verify Message For Login With Invalid Credentials");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}

	@Test(description = "Test Case 1.2: Verify that users can login into the JBilling System using valid credential")
	public void TC01_2_loginWithValidCredentials() throws Exception {
		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.runTimeVariables.put("ENVIRONMENT_UNDER_TEST", this.pr.readConfig("EnvironmentUnderTest"));
		this.runTimeVariables.put("ENVIRONMENT_URL_UNDER_TEST",
				this.pr.readConfig(this.runTimeVariables.get("ENVIRONMENT_UNDER_TEST") + "_URL"));

		this.homePage = this.loginPage.login(this.runTimeVariables.get("ENVIRONMENT_UNDER_TEST"));
		logger.debug("Login Into Application");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
