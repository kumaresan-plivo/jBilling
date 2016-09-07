package com.jbilling.framework.pageclasses;

import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;

public class BillingPage {
	// Initialize private logger object
	@SuppressWarnings("unused")
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//table[@id='processes']/tbody/tr")
	private ElementField Table_Billing;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Show Orders']")
	private ElementField BT_ShowOrders;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Show Invoices']")
	private ElementField BT_ShowInvoices;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//table[@id='invoices']/thead/tbody/tr/td/a/strong")
	private ElementField BT_Approve;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Approve']")
	private ElementField BT_PAYINVOICE;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Disapprove']")
	private ElementField BT_Disapprove;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//strong[contains(text(),'Disapproved')]")
	private ElementField TX_ReviewStatus;

}
