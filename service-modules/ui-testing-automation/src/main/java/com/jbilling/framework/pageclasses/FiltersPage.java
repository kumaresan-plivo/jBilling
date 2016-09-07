package com.jbilling.framework.pageclasses;

import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.utilities.xmlutils.TestData;

public class FiltersPage {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@LocateBy(id = "filters.ORDER-EQ_ChangeStatus.integerValue")
	private ElementField DD_Status;

	@LocateBy(id = "filters.ORDER-DATE_BETWEEN_CreateDate.startDateValue")
	private ElementField TB_CreatedDateFrom;

	@LocateBy(id = "filters.ORDER-DATE_BETWEEN_CreateDate.endDateValue")
	private ElementField TB_CreatedDateTo;

	@SuppressWarnings("unused")
	@LocateBy(id = "filters.ALL-EQ_Id.integerValue")
	private ElementField TB_Id;

	@LocateBy(id = "filters.ORDER-STATUS_OrderPeriod.integerValue")
	private ElementField DD_OrderPeriod;

	@LocateBy(id = "filters.ORDER-LIKE_Tariff.stringValue")
	private ElementField DD_TariffType;

	@LocateBy(id = "filters.ORDER-EQ_U_id.integerValue")
	private ElementField DD_UserId;

	@LocateBy(xpath = "//div[@id='filters']/div[@class='btn-hold']/a[@class='submit apply']/span")
	private ElementField BTN_ApplyFilters;

	@LocateBy(xpath = "//div[@id='filters']//div[@class='dropdown']/a[@class='submit add open']/span")
	private ElementField DD_AddFilters;

	@LocateBy(xpath = "//div[@id='filters']//div[@class='dropdown active']/div[@class='drop']/ul/li")
	private ElementField DD_AddFilters_Options;

	@LocateBy(id = "filters.ORDER-LIKE_U_userName.stringValue")
	private ElementField TB_LoginName;

	protected void selectChangeStatus(String testDataSetName, String category) throws Exception {
		String changeStatus = TestData.read("PageFilters.xml", testDataSetName, "changeStatus", category);

		GlobalController.brw.selectListItem(this.DD_Status, changeStatus);
	}

	protected void setCreatedDateFrom(String testDataSetName, String category) throws Exception {
		String createdDateFrom = TestData.read("PageFilters.xml", testDataSetName, "createdDateFrom", category);

		GlobalController.brw.setText(this.TB_CreatedDateFrom, createdDateFrom);
	}

	protected void setCreatedDateTo(String testDataSetName, String category) throws Exception {
		String createdDateTo = TestData.read("PageFilters.xml", testDataSetName, "createdDateTo", category);

		GlobalController.brw.setText(this.TB_CreatedDateTo, createdDateTo);
	}

	protected void selectOrderPeriod(String testDataSetName, String category) throws Exception {
		String orderType = TestData.read("PageFilters.xml", testDataSetName, "orderType", category);

		GlobalController.brw.selectListItem(this.DD_OrderPeriod, orderType);
	}

	protected void selectTariffType(String testDataSetName, String category) throws Exception {
		String tariffType = TestData.read("PageFilters.xml", testDataSetName, "tariffType", category);

		GlobalController.brw.selectListItem(this.DD_TariffType, tariffType);
	}

	protected void selectUserId(String testDataSetName, String category) throws Exception {
		String userId = TestData.read("PageFilters.xml", testDataSetName, "userId", category);

		GlobalController.brw.selectListItem(this.DD_UserId, userId);
	}

	protected void clickAddFilters() throws Exception {
		GlobalController.brw.clickLinkText(this.DD_AddFilters);
	}

	protected void clickApplyFilters() throws Exception {
		GlobalController.brw.clickLinkText(this.BTN_ApplyFilters);
	}

	protected void selectFilterToAdd(String filterName, boolean optional) throws Exception {
		if (optional) {
			try {
				this.selectFilterToAdd(filterName);
			} catch (Exception e) {
				// Eat Exception
			}
		} else {
			this.selectFilterToAdd(filterName);
		}
	}

	protected void selectFilterToAdd(String filterName) throws Exception {
		FiltersPage.logger.enterMethod();
		GlobalController.brw.selectListItem(this.DD_AddFilters_Options, filterName);
		FiltersPage.logger.exitMethod();
	}

	protected void setLoginNameOrCustomerName(String loginName) throws Exception {
		GlobalController.brw.setText(this.TB_LoginName, loginName);
	}

	public void filterOnLoginNameOrCustomerName(String customerName) throws Exception {
		this.clickAddFilters();
		this.selectFilterToAdd("Login Name", true);
		this.setLoginNameOrCustomerName(customerName);
		this.clickApplyFilters();
	}
}
