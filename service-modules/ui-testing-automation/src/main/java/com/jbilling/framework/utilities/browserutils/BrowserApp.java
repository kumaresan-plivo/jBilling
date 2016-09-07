package com.jbilling.framework.utilities.browserutils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.impl.selenium.BrowserInst;
import com.jbilling.framework.pageclasses.AccountTypePage;
import com.jbilling.framework.pageclasses.AgentsPage;
import com.jbilling.framework.pageclasses.ConfigurationPage;
import com.jbilling.framework.pageclasses.CustomersPage;
import com.jbilling.framework.pageclasses.DiscountsPage;
import com.jbilling.framework.pageclasses.FiltersPage;
import com.jbilling.framework.pageclasses.HomePage;
import com.jbilling.framework.pageclasses.InvoicePage;
import com.jbilling.framework.pageclasses.LoginPage;
import com.jbilling.framework.pageclasses.MessagesPage;
import com.jbilling.framework.pageclasses.NavigatorPage;
import com.jbilling.framework.pageclasses.OrdersPage;
import com.jbilling.framework.pageclasses.PaymentsPage;
import com.jbilling.framework.pageclasses.PlansPage;
import com.jbilling.framework.pageclasses.ProductsPage;
import com.jbilling.framework.pageclasses.ReportsPage;
import com.jbilling.framework.utilities.textutilities.TextUtilities;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;
import com.jbilling.framework.utilities.xmlutils.PropertyFileReaderUpdater;

/**
 * Class to initialize all application page objects and manage WebDriver browser
 * object. Each and every test script class must extend this. This class does
 * not use any of the Selenium APIs directly, and adds support to make this
 * framework tool independent.
 * 
 * @author Aishwarya Dwivedi
 * @since 1.0
 * 
 * @version 1.0
 */
@org.testng.annotations.Test(groups = {"automation"})
public abstract class BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();

	HashMap<String, String> runTimeVariables = new HashMap<String, String>();

	protected LoginPage loginPage;
	protected NavigatorPage navPage;
	protected HomePage homePage;
	protected ConfigurationPage confPage;
	protected AgentsPage agentsPage;
	protected CustomersPage customerPage;
	protected ProductsPage productsPage;
	protected PlansPage plansPage;
	protected OrdersPage ordersPage;
	protected InvoicePage invoicePage;
	protected ReportsPage reportsPage;
	protected DiscountsPage discountsPage;
	protected FiltersPage filtersPage;
	protected PaymentsPage paymentsPage;
	protected MessagesPage msgsPage;
	protected PropertyFileReaderUpdater propReader;
	protected AccountTypePage accountTypePage;

	// Define objects
	private final Runtime runtime = Runtime.getRuntime();
	private static final long MEGABYTE = 1024L * 1024L;

	@BeforeClass
	public void Starter() throws Exception {
		BrowserApp.logger.enterMethod();
		Reporter.log("<br> BeforeClass Begins");

		this.runTimeVariables.clear();
		GlobalController.brw = new BrowserInst().getFirefoxSeleniumInstance();

		// Initialize Page Objects after Browser/Drivers object initialization
		this.loginPage = GlobalController.brw.initElements(LoginPage.class);
		this.navPage = GlobalController.brw.initElements(NavigatorPage.class);
		this.homePage = GlobalController.brw.initElements(HomePage.class);
		this.confPage = GlobalController.brw.initElements(ConfigurationPage.class);
		this.agentsPage = GlobalController.brw.initElements(AgentsPage.class);
		this.customerPage = GlobalController.brw.initElements(CustomersPage.class);
		this.productsPage = GlobalController.brw.initElements(ProductsPage.class);
		this.plansPage = GlobalController.brw.initElements(PlansPage.class);
		this.ordersPage = GlobalController.brw.initElements(OrdersPage.class);
		this.invoicePage = GlobalController.brw.initElements(InvoicePage.class);
		this.reportsPage = GlobalController.brw.initElements(ReportsPage.class);
		this.discountsPage = GlobalController.brw.initElements(DiscountsPage.class);
		this.filtersPage = GlobalController.brw.initElements(FiltersPage.class);
		this.paymentsPage = GlobalController.brw.initElements(PaymentsPage.class);
		this.msgsPage = GlobalController.brw.initElements(MessagesPage.class);
		this.propReader = GlobalController.brw.initElements(PropertyFileReaderUpdater.class);
		this.accountTypePage = GlobalController.brw.initElements(AccountTypePage.class);

		this.runTimeVariables.put("ENVIRONMENT_UNDER_TEST", this.pr.readConfig("EnvironmentUnderTest"));
		this.runTimeVariables.put("ENVIRONMENT_URL_UNDER_TEST",
				this.pr.readConfig(this.runTimeVariables.get("ENVIRONMENT_UNDER_TEST") + "_URL"));

		// Login to the application
		this.homePage = this.loginPage.login(this.runTimeVariables.get("ENVIRONMENT_UNDER_TEST"));
		BrowserApp.logger.debug("Login Into Application");

		Reporter.log("<br> BeforeClass Ends");
		BrowserApp.logger.exitMethod();
	}

	@AfterClass(alwaysRun = true)
	public void tearDownPrintRunTimeValues() throws IOException {

		Iterator<Entry<String, String>> it = this.runTimeVariables.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pair = it.next();

			BrowserApp.logger.info(pair.getKey() + " = " + pair.getValue());
			// it.remove(); // avoids a ConcurrentModificationException
		}

		BrowserApp.logger.debug("Disposing all page objects");
		this.loginPage = null;
		this.navPage = null;
		this.homePage = null;
		this.confPage = null;
		this.agentsPage = null;
		this.customerPage = null;
		this.productsPage = null;
		this.plansPage = null;
		this.ordersPage = null;
		this.invoicePage = null;
		this.reportsPage = null;
		this.discountsPage = null;
		this.filtersPage = null;
		this.paymentsPage = null;
		this.msgsPage = null;
		this.propReader = null;

		BrowserApp.logger.debug("Execution completed for test\n");
		BrowserApp.logger.debug("*****************************************************************************");

		BrowserApp.logger.debug("Memory after execution (in MB) " + (this.runtime.totalMemory() / BrowserApp.MEGABYTE));

		// Run the garbage collector
		this.runtime.gc();

		BrowserApp.logger.debug("Memory freed after execution (in MB) " + (this.runtime.freeMemory() / BrowserApp.MEGABYTE));

		// Calculate the used memory
		final long memory = this.runtime.totalMemory() - this.runtime.freeMemory();
		BrowserApp.logger.debug("Used memory (in MB) " + (memory / BrowserApp.MEGABYTE));

		try {

			if (!(GlobalController.brw == null)) {
				GlobalController.brw.quit();
			}
		} catch (final Exception e) {
			// this.runtime.exec("taskkill /F /IM firefox.exe");
		}

		// Runtime.getRuntime().exec("taskkill /F /IM firefox.exe");
		// this.runtime.exec("taskkill /F /IM firefox.exe");
		System.out.println("After" + Runtime.getRuntime().freeMemory());

	}

    protected String appendRandomChars (String inputString) {
        return inputString + TextUtilities.getRandomString(3);
    }

    protected String appendRandomDigits (String inputString) {
        return inputString + TextUtilities.getRandomNumber(3);
    }
}
