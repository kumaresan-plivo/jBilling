package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.CollectionAgeingStep;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.testrails.TestRailsListener;
import com.jbilling.framework.utilities.browserutils.BrowserApp;
import com.jbilling.framework.utilities.xmlutils.ConfigPropertiesReader;

@Listeners({ TestRailsListener.class }) @Test(groups = {"automation"})
public class TestConfigureCollection extends BrowserApp {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ConfigPropertiesReader pr = new ConfigPropertiesReader();
	String graceId = null;
	ITestResult result;

	@Test(description = "Test Case 2.5 : Verify ability to configure Collections")
	public void testConfigureCollection() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "11047246");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.Collections);
		logger.debug("Click Collections in Configuration");

		this.confPage = this.confPage.addCollectionsAgeingStep(CollectionAgeingStep.FIRST, "collectionsStepOne", "ccd");
		logger.debug("Add Collection ID One");

		this.confPage = this.confPage.addCollectionsAgeingStep(CollectionAgeingStep.SECOND, "collectionsStepTwo", "ccd");
		logger.debug("Add Collection ID Two");

		this.confPage = this.confPage.addCollectionsAgeingStep(CollectionAgeingStep.THIRD, "collectionsStepThree", "ccd");
		logger.debug("Add Collection ID Three");

		this.confPage = this.confPage.addCollectionsAgeingStep(CollectionAgeingStep.FOURTH, "collectionsStepFour", "ccd");
		logger.debug("Add Collection ID Four");

		this.confPage = this.confPage.clickSaveChangesToCollections();
		logger.debug("Click Save Changes Button to Collections steps");

		this.graceId = this.confPage.getStep2ID();

		this.propReader.updatePropertyInFile("TC_2.5_GRACE_ID", this.graceId, "testData");

		Reporter.log("<br> Test Passed");
		logger.exitMethod();
	}
}
