package com.jbilling.test;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddMetaDataFields;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddMetaDataGroupFields;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageConfigurationItems;
import com.jbilling.framework.utilities.browserutils.BrowserApp;

public class VerifyUserAbleToCreateAssetMgmtMetaFieldsAndGroup extends BrowserApp {
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	ITestResult result;

	@Test(groups = { "globalRegressionPack" })
	public void checkCreateAssetMgmtMetaDataAndGroupForUser() throws Exception {

		logger.enterMethod();
		Reporter.log("<br> Test Begins");

		this.result = Reporter.getCurrentTestResult();
		this.result.setAttribute("tcid", "");

		this.confPage = this.navPage.navigateToConfigurationPage();
		logger.debug("Login Into Application And Navigate to Configuration Page");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.MetaFields);
		logger.debug("Select Meta Data from the list");

		this.confPage = this.confPage.clickMetaDataFieldValue("ASSET");
		logger.debug("Select Meta Data from the list");

		this.confPage = this.confPage.clickAddNewButton();
		logger.debug("Click on +NEW button");

		// Provide data in New Meta Field
		String strMetaFieldName = this.confPage.setNewMetaData(AddMetaDataFields.DATA_FIELD, "strMetaField", "anmf");
        this.propReader.updatePropertyInFile("strMetaField", strMetaFieldName, "testData");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("MetaField", "created successfully.", TextComparators.contains);
		logger.debug("Verify Message For Created Account Type");

		this.confPage = this.confPage.validateMetaSavedTestData(strMetaFieldName);
		logger.debug("Validate Saved Account Type Test Data");

		this.confPage = this.confPage.clickAddNewButton();
		logger.debug("Click on +NEW button");

		// Provide data in New Meta Field
		String intMetaFieldName = this.confPage.setNewMetaData(AddMetaDataFields.DATA_TYPE, "intMetaField", "asmf");
        this.propReader.updatePropertyInFile("intMetaField", intMetaFieldName, "testData");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("MetaField", "created successfully.", TextComparators.contains);
		logger.debug("Verify Message For Created Account Type");

		this.confPage = this.confPage.validateMetaSavedTestData(intMetaFieldName);
		logger.debug("Validate Saved Account Type Test Data");

		this.confPage = this.confPage.clickAddNewButton();
		logger.debug("Click on +NEW button");

		// Provide data in New Meta Field
		String boolMetaFieldName = this.confPage.setNewMetaData(AddMetaDataFields.DATA_DEFAULT_VALUE, "boolMetaField", "atmf");
        this.propReader.updatePropertyInFile("boolMetaField", boolMetaFieldName, "testData");

		this.msgsPage = this.msgsPage.verifyDisplayedMessageText("MetaField", "created successfully.", TextComparators.contains);
		logger.debug("Verify Message For Created Account Type");

		this.confPage = this.confPage.validateMetaSavedTestData(boolMetaFieldName);
		logger.debug("Validate Saved Account Type Test Data");

		this.confPage = this.confPage.selectConfiguration(PageConfigurationItems.MetaFieldGroups);
		logger.debug("Select Meta Data from the list");

		this.confPage = this.confPage.clickMetaDataFieldValue("ASSET");
		logger.debug("Select Meta Data from the list");

		this.confPage = this.confPage.clickAddNewButton();
		logger.debug("Click on +NEW button");

		// Select multiple values and click on save changes button
		this.confPage = this.confPage.setNewMetaDataGroup(
                AddMetaDataGroupFields.GROUP_DATA_FIELD, "addMetaGroupName", "amdg", intMetaFieldName, boolMetaFieldName);
	}
}
