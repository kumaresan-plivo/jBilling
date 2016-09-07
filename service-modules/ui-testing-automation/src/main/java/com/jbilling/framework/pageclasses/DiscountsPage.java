package com.jbilling.framework.pageclasses;

import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.utilities.textutilities.TextUtilities;
import com.jbilling.framework.utilities.xmlutils.TestData;

public class DiscountsPage {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@LocateBy(xpath = "//div[@class='btn-box']/a/span")
	private ElementField LT_ADDNEW;

	@LocateBy(xpath = "//table[@id='users']/tbody/tr[1]/td/a")
	private ElementField LT_RECENTCUSTOMER;

	@LocateBy(xpath = "//span[text()='Add Sub-Account']")
	private ElementField LT_ALLOWSUBACCOUNT;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//table[@id='discounts']/thead/tr/th/a")
	private ElementField PageHeader_Discounts;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//a[@class='submit add']")
	private ElementField LT_AddNewDiscount;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//table[@id='discounts']/tbody/tr/td/a")
	private ElementField Discounts_AddedDiscount;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//a[@class='submit edit']")
	private ElementField LT_EditDiscount;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//a[@class='submit delete']")
	private ElementField LT_DeleteDiscount;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Confirm']")
	private ElementField Header_DeleteDiscountPopUp;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Yes']")
	private ElementField LT_Yes;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='No']")
	private ElementField LT_No;

	@LocateBy(xpath = "//span[text()='Save Changes']")
	private ElementField LT_SAVECHANGES;

	@LocateBy(xpath = "//input[@id='discount.code']")
	private ElementField TB_DISCOUNTCODE;

	@LocateBy(xpath = "//div[@id='addDescription']/div/select")
	private ElementField DD_ADDDESCRIPTION;

	@LocateBy(xpath = "//div[@id='addDescription']/div/a")
	private ElementField IBT_ADDNEWDESCRIPTION;

	@LocateBy(xpath = "//div[@id='descriptions']/div/div/input")
	private ElementField TB_ADDDESCRIPTION;

	@LocateBy(xpath = "//label[text()='Discount Type']/../select")
	private ElementField DD_DISCOUNTTYPE;

	@LocateBy(xpath = "//input[@id='discount.rate']")
	private ElementField TB_DISCOUNTRATE;

	@LocateBy(xpath = "//table[@id='users']")
	private ElementField TAB_USERS;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//strong[contains(text(),'New Discount')]")
	private ElementField PageHeader_AddNewDiscount;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='discount.code']")
	private ElementField TB_DiscountCode;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//img[@src='/jbilling/static/images/add.png']")
	private ElementField TB_AddDescription;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[contains(@id,'discount.descriptions')]")
	private ElementField TB_DiscountDescription;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='discount.type']")
	private ElementField Dropdown_DiscountType;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='discount.rate']")
	private ElementField TB_DiscountRate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='discount.startDate']")
	private ElementField TB_DiscountStartDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='discount.endDate']")
	private ElementField TB_DiscountEndDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Save Changes']")
	private ElementField LT_SaveChange;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Cancel']")
	private ElementField LT_Cancel;
	
	@LocateBy(xpath = "//*[@id='discount.attribute.2.value']")
	private ElementField TB_PERIOD_VALUE;

	@LocateBy(xpath = "//*[@id='discount.attribute.3.value']")
	private ElementField CB_IS_PERCENTGE;

	@LocateBy(xpath = "//*[@id='discount.startDate']")
	private ElementField TB_START_DATE;

	@LocateBy(xpath = "//*[@id='discount.endDate']")
	private ElementField TB_END_DATE;
	
	@LocateBy(xpath = "//input[@id='gs_code']")
	private ElementField TF_DISCOUNTCODE;

	@LocateBy(xpath = "//div[@class='ui-jqgrid-sortable']/span[@class='s-ico']/span[1]")
	private ElementField TF_DISCOUNTCODESORTASC;

	@LocateBy(xpath = "//div[@class='ui-jqgrid-sortable']/span[@class='s-ico']/span[2]")
	private ElementField TF_DISCOUNTCODESORTDESC;

	@LocateBy(xpath = "//div[@id='jqgh_data_grid_column1_description']")
	private ElementField T_CLICKDESCRIPTION;

	@LocateBy(xpath = "//div[@id='jqgh_data_grid_column1_type']")
	private ElementField TF_DISCOUNTTYPE;

	@LocateBy(xpath = "//div[@id='jqgh_data_grid_column1_type']/span[@class='s-ico']/span[1]")
	private ElementField TF_DISCOUNTTYPESORTASC;

	@LocateBy(xpath = "//div[@id='jqgh_data_grid_column1_type']/span[@class='s-ico']/span[2]")
	private ElementField TF_DISCOUNTTYPESORTDESC;


	/**
	 * This method will click Add New button
	 */
	public DiscountsPage clickAddNewButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADDNEW);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will click on recent customer
	 * 
	 * @throws Exception
	 */
	public DiscountsPage clickRecentCustomer() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_RECENTCUSTOMER);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will click on Allow Sub Account Button
	 * 
	 * @throws Exception
	 */
	public DiscountsPage clickAllowSubAccount() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ALLOWSUBACCOUNT);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	// /////////////////////////

	/**
	 * This method will set Discount Code
	 * 
	 * @throws Exception
	 */
	public DiscountsPage setDiscountCode(String discountCode) throws Exception {
		GlobalController.brw.setText(this.TB_DISCOUNTCODE, discountCode);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will select option in Add Description dropdown
	 * 
	 * @throws Exception
	 */
	public DiscountsPage addDescriptionLanguage(String descriptionLanguage) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_ADDDESCRIPTION, descriptionLanguage);
		GlobalController.brw.click(this.IBT_ADDNEWDESCRIPTION);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will set Description
	 * 
	 * @throws Exception
	 */
	public DiscountsPage setDescription(String description) throws Exception {
		GlobalController.brw.setText(this.TB_ADDDESCRIPTION, description);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will select option in Discount Type
	 * 
	 * @throws Exception
	 */
	public DiscountsPage selectDiscountType(String discountType) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_DISCOUNTTYPE, discountType);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will set Discount Rate
	 * 
	 * @throws Exception
	 */
	public DiscountsPage setDiscountRate(String DiscountRate) throws Exception {
		GlobalController.brw.setText(this.TB_DISCOUNTRATE, DiscountRate);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	public DiscountsPage selectcustomer(String user) throws Exception {
		GlobalController.brw.selectTableRowItem(this.TAB_USERS, user);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will click Save Changes button
	 */
	public DiscountsPage clickSaveChangesButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_SAVECHANGES);
		return GlobalController.brw.initElements(DiscountsPage.class);
	}

	public String createNewDiscount(String testDataSetName, String category) throws Exception {
		DiscountsPage.logger.enterMethod();

		String discountCode = TestData.read("PageDiscounts.xml", testDataSetName, "discountCode", category);
		String descriptionLanguage = TestData.read("PageDiscounts.xml", testDataSetName, "descriptionLanguage", category);
		String description = TestData.read("PageDiscounts.xml", testDataSetName, "description", category);
		String discountType = TestData.read("PageDiscounts.xml", testDataSetName, "discountType", category);
		String discountRate = TestData.read("PageDiscounts.xml", testDataSetName, "discountRate", category);
		String user = TestData.read("PageDiscounts.xml", testDataSetName, "user", category);
		this.setDiscountCode(discountCode);
		this.addDescriptionLanguage(descriptionLanguage);
		this.setDescription(description);
		this.selectDiscountType(discountType);
		this.setDiscountRate(discountRate);
		this.clickSaveChangesButton();

		DiscountsPage.logger.exitMethod();
		return discountCode + " " + "" + "-" + "" + " " + description;
	}

	public DiscountsPage isDiscountCreatedSuccessfully() throws Exception {
		String msg = MessagesPage.isOperationSuccessfulOnMessage("Discount", "created successfully", TextComparators.contains);
		if (msg != null) {
			throw new Exception(msg);
		}

		return GlobalController.brw.initElements(DiscountsPage.class);
	}

	public DiscountsPage isValidationErrorAppeared() throws Exception {
		try {
			MessagesPage.isErrorMessageAppeared();
		} catch (Exception e) {
			throw new Exception("Validation error message field not appeared: " + e.getMessage());
		}

		return GlobalController.brw.initElements(DiscountsPage.class);
	}
	
	public String createNewDiscountWithPercentage(String testDataSetName, String category) throws Exception {
		DiscountsPage.logger.enterMethod();

		String discountCode = TestData.read("PageDiscounts.xml", testDataSetName, "discountCode", category);
		String descriptionLanguage = TestData.read("PageDiscounts.xml", testDataSetName, "descriptionLanguage", category);
		String description = TestData.read("PageDiscounts.xml", testDataSetName, "description", category);
		String discountType = TestData.read("PageDiscounts.xml", testDataSetName, "discountType", category);
		String discountRate = TestData.read("PageDiscounts.xml", testDataSetName, "discountRate", category);
		String periodValue = TestData.read("PageDiscounts.xml", testDataSetName, "periodValue", category);
		String startDate = TestData.read("PageDiscounts.xml", testDataSetName, "startDate", category);
		String endDate = TestData.read("PageDiscounts.xml", testDataSetName, "endDate", category);
		boolean isPercentage = TextUtilities.compareValue(TestData.read("PageDiscounts.xml", testDataSetName, "isPercentage", category),
				"true", true, TextComparators.equals);
		String user = TestData.read("PageDiscounts.xml", testDataSetName, "user", category);
		this.clickAddNewButton();
		this.setDiscountCode(discountCode);
		this.addDescriptionLanguage(descriptionLanguage);
		this.setDescription(description);
		this.selectDiscountType(discountType);
		this.setDiscountRate(discountRate);
		this.setPeriodValue(periodValue);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.enableIsPercentageCheckBox(isPercentage);
		this.clickSaveChangesButton();

		DiscountsPage.logger.exitMethod();
		return discountCode + " " + "" + "-" + "" + " " + description;
	}
	
	/**
	 * This method will set Percentage Value
	 * 
	 * @throws Exception
	 */
	public DiscountsPage setPeriodValue(String discountCode) throws Exception {
		GlobalController.brw.setText(this.TB_PERIOD_VALUE, discountCode);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will set Start Date
	 * 
	 * @throws Exception
	 */
	public DiscountsPage setStartDate(String startDate) throws Exception {
		GlobalController.brw.setText(this.TB_START_DATE, startDate);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will set End Date
	 * 
	 * @throws Exception
	 */
	public DiscountsPage setEndDate(String endDate) throws Exception {
		GlobalController.brw.setText(this.TB_END_DATE, endDate);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}
	
	/**
	 * This method will Enable globle checkbox
	 * 
	 * @return
	 * @throws Exception
	 */
	DiscountsPage enableIsPercentageCheckBox(boolean isPercentage) throws Exception {
		if (isPercentage) {
			GlobalController.brw.check(this.CB_IS_PERCENTGE);
		} else {
			GlobalController.brw.uncheck(this.CB_IS_PERCENTGE);
		}
		return GlobalController.brw.initElements(DiscountsPage.class);
	}
	
	public DiscountsPage verifyDiscountTable() throws Exception {
		DiscountsPage.logger.enterMethod();
		this.verifyDiscountCodeField();
		this.verifyDiscountCodeSortIconASC();
		this.verifyDiscountCodeSortIconDESC();
		this.clickDiscountDescription();
		this.clickDiscountTypeField();
		this.verifyDiscountTypeField();

		this.verifyDiscountTypeSortIconASC();
		this.verifyDiscountTypeSortIconDESC();

		return GlobalController.brw.initElements(DiscountsPage.class);
	}
	
	/**
	 * This method will verify text box in Discount Code Field
	 *
	 * @throws Exception
	 */
	public DiscountsPage verifyDiscountCodeField() throws Exception {
		GlobalController.brw.isElementPresent(this.TF_DISCOUNTCODE);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will verify Sort Icon in Discount Code Field
	 *
	 * @throws Exception
	 */
	public DiscountsPage verifyDiscountCodeSortIconASC() throws Exception {
		boolean resukt = GlobalController.brw.isElementPresent(this.TF_DISCOUNTCODESORTASC);
		System.out.println("Resulkt of res" + resukt);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will verify Sort Icon in Discount Code Field
	 *
	 * @throws Exception
	 */
	public DiscountsPage verifyDiscountCodeSortIconDESC() throws Exception {
		boolean resukt = GlobalController.brw.isElementPresent(this.TF_DISCOUNTCODESORTDESC);
		System.out.println("Resulkt of res" + resukt);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will click Description
	 *
	 * @throws Exception
	 */
	public DiscountsPage clickDiscountDescription() throws Exception {
		GlobalController.brw.click(this.T_CLICKDESCRIPTION);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will verify text box in Discount Type Field
	 *
	 * @throws Exception
	 */
	public DiscountsPage verifyDiscountTypeField() throws Exception {
		GlobalController.brw.isElementPresent(this.TF_DISCOUNTTYPE);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

	/**
	 * This method will click text box in Discount Type Field
	 *
	 * @throws Exception
	 */
	public DiscountsPage clickDiscountTypeField() throws Exception {
		GlobalController.brw.click(this.TF_DISCOUNTTYPE);
		return GlobalController.brw.initElements(DiscountsPage.class);
	}

	/**
	 * This method will verify Sort Icon in Discount type Field
	 *
	 * @throws Exception
	 */
	public DiscountsPage verifyDiscountTypeSortIconASC() throws Exception {
		GlobalController.brw.isElementPresent(this.TF_DISCOUNTTYPESORTASC);
		return GlobalController.brw.initElements(DiscountsPage.class);
	}

	/**
	 * This method will verify Sort Icon in Discount type Field
	 *
	 * @throws Exception
	 */
	public DiscountsPage verifyDiscountTypeSortIconDESC() throws Exception {
		GlobalController.brw.isElementPresent(this.TF_DISCOUNTTYPESORTDESC);
		return GlobalController.brw.initElements(DiscountsPage.class);

	}

}
