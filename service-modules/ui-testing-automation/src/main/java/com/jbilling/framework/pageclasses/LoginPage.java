package com.jbilling.framework.pageclasses;

import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.LoginField;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;
import com.jbilling.framework.utilities.xmlutils.TestData;

public class LoginPage {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@LocateBy(xpath = "//input[@id='j_username']")
	private ElementField username;

	@LocateBy(xpath = "//input[@id='j_password']")
	private ElementField passwordField;

	@LocateBy(xpath = "//input[@id='j_client_id']")
	private ElementField companyID;

	@LocateBy(xpath = "//input[@id='_spring_security_remember_me']")
	private ElementField rememberMeLoc;

	@LocateBy(xpath = "//span[text()='Login']")
	private ElementField loginBtn;

	ConfigPropertiesReader propReader = new ConfigPropertiesReader();

	/**
	 * This method will check Remember Me checkbox
	 * 
	 * @param rememberMe
	 * @throws Exception
	 */
	protected void checkRememberMe(Boolean rememberMe) throws Exception {
		if (rememberMe) {
			GlobalController.brw.check(this.rememberMeLoc);
		} else {
			GlobalController.brw.uncheck(this.rememberMeLoc);
		}
	}

	/**
	 * This method will set UserName
	 * 
	 * @param userName
	 * @throws Exception
	 */
	protected void setUserName(String userName) throws Exception {
		if (this.username == null) {
			System.exit(1);
		}
		GlobalController.brw.setText(this.username, userName);
	}

	/**
	 * This method will set Password
	 * 
	 * @param password
	 * @throws Exception
	 */
	protected void setPassword(String password) throws Exception {
		GlobalController.brw.setText(this.passwordField, password);
	}

	/**
	 * This method will set Company ID
	 * 
	 * @param companyName
	 * @throws Exception
	 */
	protected void setCompanyID(String companyName) throws Exception {
		GlobalController.brw.setText(this.companyID, companyName);
	}

	/**
	 * This method will click on Login Button
	 * 
	 * @throws Exception
	 */
	protected void clickLoginButton() throws Exception {
		GlobalController.brw.clickLinkText(this.loginBtn);
	}

	/**
	 * Login to Application for EnvironmentUnderTest provided in
	 * Config.properties file
	 * 
	 * @return
	 * @throws Exception
	 */
	protected HomePage login() throws Exception {
		return this.login(this.propReader.readConfig("EnvironmentUnderTest"));
	}

	/**
	 * Login to Application
	 * 
	 * @throws Exception
	 */
	public HomePage login(String environment) throws Exception {
		this.login(environment, true);
		return GlobalController.brw.initElements(HomePage.class);
	}

	/**
	 * Login to Application
	 * 
	 * @throws Exception
	 */
	public HomePage login(String environment, boolean rememberMe) throws Exception {
		LoginPage.logger.enterMethod();

		String URLDevelop = this.propReader.readConfig(environment + "_URL");
		String userName = this.propReader.readConfig(environment + "_Username");
		String password = this.propReader.readConfig(environment + "_Password");
		String companyId = this.propReader.readConfig(environment + "_CompanyID");

		this.login(URLDevelop, userName, password, companyId, rememberMe);
		LoginPage.logger.exitMethod();
		return GlobalController.brw.initElements(HomePage.class);
	}

	/**
	 * Login to Application
	 * 
	 * @throws Exception
	 */
	protected HomePage login(String url, String userName, String password, String companyId) throws Exception {
		this.login(url, userName, password, companyId, true);
		return GlobalController.brw.initElements(HomePage.class);
	}

	/**
	 * Login to Application
	 * 
	 * @throws Exception
	 */
	public HomePage login(String url, String userName, String password, String companyId, boolean rememberMe) throws Exception {
		LoginPage.logger.enterMethod();

		GlobalController.brw.navigateToUrl(url);
		this.setUserName(userName);
		this.setPassword(password);
		this.setCompanyID(companyId);
		this.checkRememberMe(rememberMe);
		this.clickLoginButton();

		LoginPage.logger.exitMethod();
		return GlobalController.brw.initElements(HomePage.class);
	}

	public LoginPage invalidLogin(LoginField loginField, String testDataSetName, String category) throws Exception {
		// String url = this.propReader.readConfig(environment + "_URL");
		String userName = TestData.read("LoginPage.xml", testDataSetName, "username", category);
		String password = TestData.read("LoginPage.xml", testDataSetName, "password", category);
		String companyId = TestData.read("LoginPage.xml", testDataSetName, "companyid", category);

		// GlobalController.brw.navigateToUrl(url);
		switch (loginField) {
		case USERNAME:
			this.setUserName(userName);
			break;
		case PASSWORD:
			this.setPassword(password);
			break;
		case COMPANY_ID:
			this.setCompanyID(companyId);
			break;
		case ALL:
			this.setUserName(userName);
			this.setPassword(password);
			this.setCompanyID(companyId);
			break;

		default:
			throw new Exception("Invalid Step Provided. Not defined in enumeration");

		}
		this.clickLoginButton();

		return GlobalController.brw.initElements(LoginPage.class);
	}
}
