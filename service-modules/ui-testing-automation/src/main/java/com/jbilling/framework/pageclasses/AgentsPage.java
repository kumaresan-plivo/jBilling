package com.jbilling.framework.pageclasses;

import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.utilities.xmlutils.TestData;

public class AgentsPage {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@LocateBy(xpath = "//div[@class='btn-box']/a/span")
	private ElementField LT_ADDNEW;

	@LocateBy(id = "user.userName")
	private ElementField TB_LOGINNAME;

	@LocateBy(id = "contact.email")
	private ElementField TB_EMAIL;

	@LocateBy(xpath = "//select[@id = 'type']")
	private ElementField DD_AGENTTYPE;

	@LocateBy(xpath = "//select[@id = 'commissionType']")
	private ElementField DD_COMMISSION;

	@LocateBy(xpath = "//table[@id = 'users']")
	private ElementField TT_TABLETEXT;

	@LocateBy(xpath = "//div[@class = 'buttons']/ul/li/a")
	private ElementField BT_SaveChanges;

	@LocateBy(xpath = "//table[@id='users']/tbody/tr/td/a/strong")
	private ElementField LT_AGENT;

	@LocateBy(xpath = "//span[text()='Show Commissions']")
	private ElementField BT_ShowCommission;

	@LocateBy(xpath = "//table[@id='users']/tbody//td/a/strong")
	private ElementField TAB_LIST_AGENT;

	@LocateBy(xpath = "//div[@id='breadcrumbs']")
	private ElementField BREADCRUMBS;

	/**
	 * This method will click Add New button
	 */
	protected AgentsPage clickAddNewButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADDNEW);
		return GlobalController.brw.initElements(AgentsPage.class);
	}

	/**
	 * This method will Enter Login name for new agent
	 * 
	 * @param name
	 * @throws Exception
	 */
	protected AgentsPage setLoginName(String name) throws Exception {
		GlobalController.brw.setText(this.TB_LOGINNAME, name);
		return GlobalController.brw.initElements(AgentsPage.class);
	}

	/**
	 * This method will Enter email id for new agent
	 * 
	 * @param name
	 * @throws Exception
	 */
	protected AgentsPage setEmailID(String email) throws Exception {
		GlobalController.brw.setText(this.TB_EMAIL, email);
		return GlobalController.brw.initElements(AgentsPage.class);
	}

	/**
	 * This method will select Agent Type
	 * 
	 * @param agent
	 * @throws Exception
	 */
	protected AgentsPage selectAgentType(String agent) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_AGENTTYPE, agent);
		return GlobalController.brw.initElements(AgentsPage.class);
	}

	/**
	 * This method will select Commission
	 * 
	 * @param commission
	 * @throws Exception
	 */
	protected AgentsPage selectCommission(String commission) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_COMMISSION, commission);
		return GlobalController.brw.initElements(AgentsPage.class);
	}

	/**
	 * This method will click Save Changes button
	 * 
	 * @throws Exception
	 */
	protected AgentsPage clickSaveChanges() throws Exception {
		GlobalController.brw.click(this.BT_SaveChanges);
		return GlobalController.brw.initElements(AgentsPage.class);
	}

	/**
	 * This method will select text from table
	 * 
	 * @throws Exception
	 */
	protected AgentsPage selectTextFromTable(String text) throws Exception {
		GlobalController.brw.selectTableRowItem(this.TT_TABLETEXT, text);
		return GlobalController.brw.initElements(AgentsPage.class);
	}

	/**
	 * This method will click on Agent Name
	 * 
	 * @throws Exception
	 */
	public AgentsPage clickAgentItem() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_AGENT);
		return GlobalController.brw.initElements(AgentsPage.class);
	}

	/**
	 * This method will click on show commission button
	 * 
	 * @throws Exception
	 */
	public AgentsPage showCommission() throws Exception {
		GlobalController.brw.clickLinkText(this.BT_ShowCommission);
		return GlobalController.brw.initElements(AgentsPage.class);
	}

	public String addAgent(String testDataSetName, String category) throws Exception {
		AgentsPage.logger.enterMethod();
		String login = TestData.read("PageAgents.xml", testDataSetName, "login", category);
		String email = TestData.read("PageAgents.xml", testDataSetName, "email", category);
		String agent = TestData.read("PageAgents.xml", testDataSetName, "agent", category);
		String commission = TestData.read("PageAgents.xml", testDataSetName, "commission", category);

		this.clickAddNewButton();
		this.setLoginName(login);
		this.setEmailID(email);
		this.selectAgentType(agent);
		this.selectCommission(commission);

		this.clickSaveChanges();

		AgentsPage.logger.exitMethod();
		return agent;
	}

	/**
	 * This method will select Agent from table list
	 * 
	 * @throws Exception
	 */
	public AgentsPage selectAgent(String agent) throws Exception {
		GlobalController.brw.selectListItem(this.TAB_LIST_AGENT, agent);
		return GlobalController.brw.initElements(AgentsPage.class);
	}

	/**
	 * This method will verify breadcrumbs ui component
	 * 
	 * @throws Exception
	 */
	public AgentsPage verifyUIComponent() throws Exception {
		GlobalController.brw.verifyUIComponent(this.BREADCRUMBS);
		return GlobalController.brw.initElements(AgentsPage.class);
	}
}
