package com.jbilling.framework.pageclasses;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.utilities.xmlutils.TestData;

public class ReportsPage {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//table[@id='report-types']/thead/tr/th[2]")
	private ElementField PageReport_PageHeader;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//table[@id='report-types']/tbody")
	private ElementField Table_Report;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@id='viewport']/div[2]/div/div[1]/div/table/tbody")
	private ElementField Table_ReportNameTable;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='results']")
	private ElementField LT_NoOfReports;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@id='viewport']/div[2]/div/div[2]/div[2]")
	private ElementField LT_ReportsPages;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Show All']")
	private ElementField BT_Showall;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "(//strong[text()='Billing Register - Summary'])[2]")
	private ElementField Header_BillingSummary;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='start_date']")
	private ElementField DT_StartDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='end_date']")
	private ElementField DT_EndDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='itemType']")
	private ElementField DD_Commodity;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='item']")
	private ElementField DD_Product;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='state']")
	private ElementField DD_State;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='division']")
	private ElementField DD_Division;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Run Report']")
	private ElementField BT_RunReport;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='format']")
	private ElementField DD_ReportFormat;

	@LocateBy(xpath = "//strong[text()='Invoice Reports']")
	private ElementField LT_REPORT;

	@LocateBy(xpath = "//strong[text() = 'Total Amount Invoiced']")
	private ElementField LT_REPORTNAME;

	@LocateBy(xpath = "//select[@id = 'period']")
	private ElementField DD_PERIOD;

	@LocateBy(xpath = "//select[@id = 'format']")
	private ElementField DD_VIEW;

	@LocateBy(xpath = "//div[@class = 'btn-box']/a/span[text()='Run Report']")
	private ElementField BT_RUNREPORT;

	/**
	 * This method selects report type from the available grid
	 * 
	 * @throws Exception
	 */
	protected ReportsPage selectReportType() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_REPORT);
		return GlobalController.brw.initElements(ReportsPage.class);

	}

	/**
	 * This method selects report name for selected report type
	 * 
	 * @throws Exception
	 */
	ReportsPage selectReportName() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_REPORTNAME);
		return GlobalController.brw.initElements(ReportsPage.class);

	}

	/**
	 * This method selects period breakdown
	 * 
	 * @throws Exception
	 */
	ReportsPage selectPeriodBreakdown(String period) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PERIOD, period);
		return GlobalController.brw.initElements(ReportsPage.class);

	}

	/**
	 * This method selects Report View
	 * 
	 * @throws Exception
	 */
	ReportsPage selectReportView(String view) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_VIEW, view);
		return GlobalController.brw.initElements(ReportsPage.class);

	}

	/**
	 * This method clicks on Run Report button
	 * 
	 * @throws Exception
	 */
	ReportsPage clickOnRunReport() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_RUNREPORT);
		return GlobalController.brw.initElements(ReportsPage.class);

	}

	ReportsPage switchToAndVerifyWindow() throws Exception {
		// Store the current window handle

		String winHandleBefore = GlobalController.brw.getCurrentWebDriver().getWindowHandle();
		ReportsPage.logger.debug("Old Window handle is : " + winHandleBefore);
		System.out.println("Old Window handle is : " + winHandleBefore);
		// Perform the click operation that opens new window
		this.clickOnRunReport();
		System.out.println("Click Run Peport");
		ReportsPage.logger.debug("Click Run Peport");
		Thread.sleep(5000);
		// Switch to new window opened
		for (String winHandle : GlobalController.brw.getCurrentWebDriver().getWindowHandles()) {
			GlobalController.brw.getCurrentWebDriver().switchTo().window(winHandle);
			ReportsPage.logger.debug("New Window handle is : " + winHandle);
			System.out.println("New Window handle is : " + winHandle);
		}

		// Perform the actions on new window

		// Close the new window, if that window no more required
		// GlobalController.brw.getCurrentWebDriver().close();
		Thread.sleep(5000);
		// Switch back to original browser (first window)
		GlobalController.brw.getCurrentWebDriver().switchTo().window(winHandleBefore);

		// TODO: Continue with original browser (first window)

		return GlobalController.brw.initElements(ReportsPage.class);

	}

	ReportsPage closeWindowPopup() throws Exception {
		// Perform the click operation that opens window popup
		this.clickOnRunReport();

		// creating instance of Robot class (A java based utility)
		Robot rb = new Robot();
		// pressing keys with the help of keyPress and keyRelease events
		rb.keyPress(KeyEvent.VK_ENTER);
		rb.keyRelease(KeyEvent.VK_ENTER);
		return GlobalController.brw.initElements(ReportsPage.class);

	}

	public ReportsPage getReportsView(String testDataSetName, String category) throws Exception {
		ReportsPage.logger.enterMethod();

		String period = TestData.read("PageReports.xml", testDataSetName, "period", category);
		String view = TestData.read("PageReports.xml", testDataSetName, "view", category);
		String view2 = TestData.read("PageReports.xml", testDataSetName, "view2", category);
		String view3 = TestData.read("PageReports.xml", testDataSetName, "view3", category);

		this.selectReportType();
		this.selectReportName();
		this.selectPeriodBreakdown(period);
		this.selectReportView(view);
		/* TODO 2016-09-03 igor.poteryaev@gmail.com: implement normal work with report windows
		this.switchToAndVerifyWindow();
		this.selectReportView(view2);
		this.closeWindowPopup();
		this.selectReportView(view3);
		this.closeWindowPopup();
		*/

		ReportsPage.logger.exitMethod();
		return GlobalController.brw.initElements(ReportsPage.class);
	}
}
