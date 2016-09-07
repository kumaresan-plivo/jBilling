package com.jbilling.framework.pageclasses;

import org.testng.Assert;

import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.AddPlanField;
import com.jbilling.framework.utilities.xmlutils.TestData;

public class PlansPage {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//table[@id='plans']/thead/tr/th/a")
	private ElementField PageHeader_Plan;

	@LocateBy(xpath = "//span[text()='Add New']")
	private ElementField LT_AddNew;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@class='download']/a")
	private ElementField LT_DownloadCSVFile;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='product.number']")
	private ElementField PlanDetails_ProductCode;

	@LocateBy(xpath = "//select[@id='plan.periodId']")
	private ElementField Details_Period;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='product.activeSince']")
	private ElementField Details_StartDate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='product.activeUntil']")
	private ElementField Details_EndDate;

	@LocateBy(xpath = "//select[@id='price.currencyId']")
	private ElementField Details_Currency;

	@LocateBy(xpath = "//table[@id='products']/tbody//td/a/strong")
	private ElementField LT_PRODUCT;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='product.types']")
	private ElementField Details_Categories;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//label[@id='price.rateAsDecimal']")
	private ElementField Details_Rate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='plan.usagePoolIds']")
	private ElementField Details_FreeUsagePools;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//textarea[@id='plan.description']")
	private ElementField TB_Note;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Add New Metafield']")
	private ElementField LT_AddNewMeta;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='metaField0.name']")
	private ElementField TB_MetaField;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='metaField0.dataType']")
	private ElementField Dropdown_MetaFieldDataType;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='metaField0.filename']")
	private ElementField TB_MetaFieldFileName;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='mandatoryCheck']")
	private ElementField CHB_MetaFieldMandatory;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='disableCheck']")
	private ElementField CHB_MetaFieldDisable;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='metaField0.displayOrder']")
	private ElementField TB_MetaFieldDisplayOrder;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='defaultValue0']")
	private ElementField TB_MetaFieldDefaultValue;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//label[text()='Dependency']/preceding-sibling::input")
	private ElementField CHB_MetaFieldDependency;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//label[text()='Help']/preceding-sibling::input")
	private ElementField CHB_MetaFieldHelp;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='metaField0.validationRule.ruleType']")
	private ElementField Dropdown_MetaFieldValidation;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Update']")
	private ElementField LT_MetaFieldUpdate;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//span[text()='Remove']")
	private ElementField TB_MetaFieldRemove;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='filterBy']")
	private ElementField Products_FilterBy;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='typeId']")
	private ElementField Products_ProductCategory;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='plansFilterBy']")
	private ElementField Plans_FilterBy;

	@LocateBy(xpath = "//table[@id='plans']/tbody/tr/td/a")
	private ElementField LT_PLANLIST;

	@LocateBy(xpath = "//span[text()='Edit']")
	private ElementField LT_EDIT;

	@LocateBy(xpath = "//input[@id='product.number']")
	private ElementField TB_PRODUCTCODE;

	@LocateBy(xpath = "//select[@id='newDescriptionLanguage']")
	private ElementField DD_ADDDESCRIPTION;

	@LocateBy(xpath = "//a[@onclick='addNewProductDescription()']/img")
	private ElementField IBT_ADDNEWDESCRIPTION;

	@LocateBy(xpath = "//div[@id='descriptions']/div/div/input")
	private ElementField TB_DESCRIPTION;

	@LocateBy(xpath = "//input[@id='product.activeSince']/../img")
	private ElementField BT_STARTDATE;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//input[@id='product.activeUntil']")
	private ElementField BT_ENDDATE;

	@LocateBy(xpath = "//input[@id='price.rateAsDecimal']")
	private ElementField TB_RATE;

    @LocateBy(xpath = "//a[text()='Details']")
    private ElementField LT_DETAILS;

	@LocateBy(xpath = "//a[text()='Products']")
	private ElementField LT_PRODUCTS;

	@LocateBy(xpath = "//table[@id='products']/tbody//td/a/strong")
	private ElementField LT_PRODUCTCODE;

	@LocateBy(xpath = "//select[@id='product.types']")
	private ElementField DD_CATEGORY;

	@LocateBy(xpath = "//input[@id='bundle.quantityAsDecimal']")
	private ElementField TB_BUNDLEQUANTITY;

	@LocateBy(xpath = "//label[text()='Rate']/../div/input")
	private ElementField TB_RATEPRODUCT;

	@LocateBy(xpath = "//span[text()='Update']")
	private ElementField LT_UPDATE;

	@LocateBy(xpath = "//span[text()='Save Changes']")
	private ElementField LT_SAVECHANGES;

	@LocateBy(xpath = "//select[@id = 'model.0.type']")
	private ElementField DD_PRICING;

	@LocateBy(xpath = "//select[@id = 'company-select']")
	private ElementField DD_COMPANY;

	@LocateBy(xpath = "//select[@id = 'product.types']")
	private ElementField DD_PRODUCT_CATEGORY;

	@LocateBy(xpath = "//select[@id = 'plan.usagePoolIds']")
	private ElementField DD_FREE_USAGE_POOL;

	@LocateBy(xpath = "//select[@id = 'typeId']")
	private ElementField DD_PRODUCT;

	@LocateBy(xpath = "//ul[@class = 'top-nav']/li")
	private ElementField CURRENT_COMPANY;

	@LocateBy(xpath = "//input[@id='model.0.rateAsDecimal']")
	private ElementField TB_RATE2;

	@LocateBy(xpath = "//span[text()='Add New']")
	private ElementField LT_ADDNEW;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@id='addDescription']//div/a/img")
	private ElementField IBT5_ADDDESCRIPTION;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//div[@id='descriptions']/div/div/input[1]")
	private ElementField TB_ADDDESCRIPTION;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//label[text()='Availability Start Date']/../div/img")
	private ElementField IBT_STARTDATE;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//label[text()='Availability End Date']/../div/img")
	private ElementField IBT_ENDDATE;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//select[@id='product.types']//option[contains(text(),'New Test category')]")
	private ElementField TXT_CATEGORIES;

	@SuppressWarnings("unused")
	@LocateBy(xpath = "//a[text()='Products']")
	private ElementField LT_PRODUCTSTAB;

	@LocateBy(xpath = "//select[@id='bundle.periodId']")
	private ElementField DD_BUNDELEDPERIOD;

	@LocateBy(xpath = "//input[@id = 'price.precedence']")
	private ElementField TXT_PRECEDENCE;

	@LocateBy(xpath = "//span[contains(text(),'-')]/parent::li")
	private ElementField LI_METAFIELDTAB_ACTIVATOR;

	@LocateBy(xpath = "//span[contains(text(),'-')]")
	private ElementField SPAN_NEW_METAFIELDTAB;

	@LocateBy(xpath = "//span[text()='Save Plug-in']")
	private ElementField LT_SAVEPLUGINS;

	@LocateBy(xpath = "//span[text()='Add Product']")
	private ElementField LT_AddProduct;

	@LocateBy(xpath = "//a[@onclick='addNewProductDescription()']")
	private ElementField LT_ADDDESCRIPTION;

	@LocateBy(xpath = "//label[contains(text(),'English Description')]/../div/input[1]")
	private ElementField TB_ENGLISHDESCRIPTION;

	@LocateBy(id = "global-checkbox")
	private ElementField CB_GLOBLE;

	@LocateBy(xpath = "//input[@id='product.activeSince']")
	private ElementField TB_START_DATE;

	@LocateBy(xpath = "//input[@id='product.activeUntil']")
	private ElementField TB_END_DATE;

	@LocateBy(xpath = "//a[text()='Products']")
	private ElementField PRODUCT_TAB;

	@LocateBy(xpath = "//table[@id='products']/tbody/tr/td/a/strong")
	private ElementField TAB_PRODUCT;

	@LocateBy(xpath = "//table[@id='products']/tbody/tr/td/a/strong")
	private ElementField TAB_PRODUCT_NAME;
	
	@LocateBy(xpath = "//table[@id='plans']/tbody/tr/td/a/strong")
	private ElementField TAB_PLAN_NAME;
	
    @LocateBy(id = "plans")
    private ElementField TBL_PLANS;

	@LocateBy(xpath = "//div[@class='box-card-hold']/div/ul/li[1]")
	private ElementField CATEGORY_NAME1;

	@LocateBy(xpath = "//div[@class='box-card-hold']/div/ul/li[2]")
	private ElementField CATEGORY_NAME2;

	public PlansPage clickAddNewButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_AddNew);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select first plan in Plans list
	 * 
	 * @throws Exception
	 */
	public PlansPage selectFirstPlanInPlanList() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_PLANLIST);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will click on Edit button
	 * 
	 * @throws Exception
	 */
	public PlansPage clickEdit() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_EDIT);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	public PlansPage addPlan() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADDNEW);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will set Product Code
	 * 
	 * @param productCode
	 * @throws Exception
	 */
	public PlansPage setProductCode(String productCode) throws Exception {
		GlobalController.brw.setText(this.TB_PRODUCTCODE, productCode);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select Add description dropdown option
	 * 
	 * @param description
	 * @throws Exception
	 */
	public PlansPage selectAddDesctiption(String description) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_ADDDESCRIPTION, description);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will click on Add new description + image button
	 * 
	 * @throws Exception
	 */
	public PlansPage clickAddNewDescription() throws Exception {
		GlobalController.brw.click(this.IBT_ADDNEWDESCRIPTION);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will set new English Description
	 * 
	 * @param englishDescription
	 * @throws Exception
	 */
	public PlansPage setNewDescription(String englishDescription) throws Exception {
		GlobalController.brw.setText(this.TB_DESCRIPTION, englishDescription);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select current date
	 * 
	 * @throws Exception
	 */
	public PlansPage setCurrentDate() throws Exception {
		GlobalController.brw.click(this.BT_STARTDATE);
		GlobalController.brw.pressEnter(this.BT_STARTDATE);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select current date
	 * 
	 * @throws Exception
	 */
	public PlansPage setEndtDate() throws Exception {
		// GlobalController.brw.click(this.BT_STARTDATE);
		// GlobalController.brw.pressEnter(this.BT_STARTDATE);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will set Rate
	 * 
	 * @param rate
	 * @throws Exception
	 */
	public PlansPage setRate(String rate) throws Exception {
		GlobalController.brw.setText(this.TB_RATE, rate);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will set Model Rate
	 * 
	 * @param rate
	 * @throws Exception
	 */
	public PlansPage setModelRate(String rate) throws Exception {
		GlobalController.brw.setText(this.TB_RATE2, rate);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select Category from dropdown
	 * 
	 * @param category
	 * @throws Exception
	 */
	public PlansPage selectCategory(String category) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_CATEGORY, category);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select Products tab within plans
	 * 
	 * @throws Exception
	 */
	public PlansPage selectInsideProductsTab() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_PRODUCTS);
        GlobalController.brw.waitForAjaxElement(this.Products_FilterBy);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select product code1 description code
	 * 
	 * @throws Exception
	 */
	public PlansPage selectProductCode(String product) throws Exception {
		GlobalController.brw.selectListItem(this.LT_PRODUCTCODE, product);
        GlobalController.brw.waitForAjaxElement(this.TB_BUNDLEQUANTITY);
		return GlobalController.brw.initElements(PlansPage.class);
	}

	/**
	 * This method will set bundled quantity
	 * 
	 * @param quantity
	 * @throws Exception
	 */
	public PlansPage setBundleQuantity(String quantity) throws Exception {
		GlobalController.brw.setText(this.TB_BUNDLEQUANTITY, quantity);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method set product rate
	 * 
	 * @param rate
	 * @throws Exception
	 */
	public PlansPage setRateProduct(String rate) throws Exception {
		GlobalController.brw.setText(this.TB_RATEPRODUCT, rate);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will click Update button
	 * 
	 * @throws Exception
	 */
	public PlansPage clickUpdate() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_UPDATE);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will click save changes button
	 * 
	 * @throws Exception
	 */
	public PlansPage clickSaveChanges() throws Exception {
        GlobalController.brw.waitForAjaxElement(this.LT_SAVECHANGES);
        GlobalController.brw.waitUntilElementStopMoving(this.LT_SAVECHANGES);
		GlobalController.brw.clickLinkText(this.LT_SAVECHANGES);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	public PlansPage selectPricicngDropdown(String pricing) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PRICING, pricing);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select current company
	 * 
	 * @throws Exception
	 */
	public PlansPage selectCurrentCompany() throws Exception {
		String currentCompany = GlobalController.brw.getText(this.CURRENT_COMPANY);
		GlobalController.brw.selectDropDown(this.DD_COMPANY, currentCompany);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select selectProductCategory dropdown option
	 * 
	 * @param description
	 * @throws Exception
	 */
	public PlansPage selectProductCategory(String category) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PRODUCT_CATEGORY, category);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select selectProductCategory dropdown option
	 * 
	 * @param description
	 * @throws Exception
	 */
	public PlansPage selectFreeUsagePool(String freeUsagePoolName) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_FREE_USAGE_POOL, freeUsagePoolName);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select selectProduct dropdown option
	 * 
	 * @param description
	 * @throws Exception
	 */
	public PlansPage selectProduct(String products) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_PRODUCT, products);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will click on selected product from product section
	 * 
	 * @return
	 * @throws Exception
	 * @author
	 */
	protected ConfigurationPage clickSelectedProduct() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_PRODUCT);
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	/**
	 * This method will select Bundled Period dropdown option
	 * 
	 * @param description
	 * @throws Exception
	 */
	public PlansPage selectBundeledPeriod(String period) throws Exception {
		GlobalController.brw.selectDropDown(this.DD_BUNDELEDPERIOD, period);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will select Plan Period dropdown option
	 * 
	 * @param description
	 * @throws Exception
	 */
	public PlansPage selectPlanPeriod(String period) throws Exception {
		GlobalController.brw.selectDropDown(this.Details_Period, period);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will enter Note
	 * 
	 * @param note
	 * @throws Exception
	 */
	public PlansPage enterNotes(String note) throws Exception {
		GlobalController.brw.setText(this.TB_Note, note);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will enter Precedence
	 * 
	 * @param precedence
	 * @return
	 * @throws Exception
	 */
	public PlansPage enterPrecedence(String precedence) throws Exception {
		GlobalController.brw.setText(this.TXT_PRECEDENCE, precedence);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This Method will add Metafield
	 * 
	 * @param paitad
	 * @throws Exception
	 */
	protected ConfigurationPage verifyMetaField() throws Exception {
		String AttributeValue = GlobalController.brw.getAttribute(this.LI_METAFIELDTAB_ACTIVATOR, "class");
		if (AttributeValue.contains("active") == false) {
			GlobalController.brw.clickLinkText(this.SPAN_NEW_METAFIELDTAB);
		}
		return GlobalController.brw.initElements(ConfigurationPage.class);
	}

	public PlansPage addPlan(AddPlanField addPlanField, String userCategory, String productCategory, String testDataSetName, String category)
			throws Exception {
		PlansPage.logger.enterMethod();

		String productCode = TestData.read("PagePlans.xml", testDataSetName, "productCode", category);
		String description = TestData.read("PagePlans.xml", testDataSetName, "description", category);
		String englishDescription = TestData.read("PagePlans.xml", testDataSetName, "englishDescription", category);
		String rate = TestData.read("PagePlans.xml", testDataSetName, "rate", category);
		String bundleQuantity = TestData.read("PagePlans.xml", testDataSetName, "bundleQuantity", category);
		String rateProduct = TestData.read("PagePlans.xml", testDataSetName, "rateProduct", category);
		String pricing = TestData.read("PagePlans.xml", testDataSetName, "pricing", category);
		String bundledPeriod = TestData.read("PagePlans.xml", testDataSetName, "bundledPeriod", category);
		String note = TestData.read("PagePlans.xml", testDataSetName, "note", category);
		String precedence = TestData.read("PagePlans.xml", testDataSetName, "precedence", category);

		switch (addPlanField) {
		case ALL:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			// this.selectPricicngDropdown(pricing);
			this.setBundleQuantity(bundleQuantity);
			this.setModelRate(rateProduct);
			this.clickUpdate();
			break;
		case PRODUCT:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			this.setBundleQuantity(bundleQuantity);
			this.clickUpdate();
			break;
		case BUNDLEDPERIOD:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			// this.selectPricicngDropdown(pricing);
			this.setBundleQuantity(bundleQuantity);
			this.verifyMetaField();
			this.selectBundeledPeriod(bundledPeriod);
			this.verifyMetaField();
			this.setModelRate(rateProduct);
			this.clickUpdate();
			break;
		case WITHNOTE:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.enterNotes(note);
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			this.verifyMetaField();
			this.enterPrecedence(precedence);
			this.setBundleQuantity(bundleQuantity);
			this.verifyMetaField();
			this.selectBundeledPeriod(bundledPeriod);
			this.verifyMetaField();
			this.setModelRate(rateProduct);
			this.clickUpdate();
			break;

		default:
			throw new Exception("Invalid Step Provided. Not defined in enumeration");
		}

		this.clickSaveChanges();

		PlansPage.logger.exitMethod();
		return GlobalController.brw.initElements(PlansPage.class);
	}
	
    private PlansPage filterProducts(String textForProductFiltering) throws Exception {
        ElementField efTbForFiltering = this.Products_FilterBy;
        GlobalController.brw.setText(efTbForFiltering, textForProductFiltering.replace(" ", "_"));
        GlobalController.brw.pressTab(efTbForFiltering);
        return GlobalController.brw.initElements(PlansPage.class);
	}

	public PlansPage addPlan(String userCategory, String productCategory, String testDataSetName, String category) throws Exception {
		PlansPage.logger.enterMethod();

		String productCode = TestData.read("PagePlans.xml", testDataSetName, "productCode", category);
		String description = TestData.read("PagePlans.xml", testDataSetName, "description", category);
		String englishDescription = TestData.read("PagePlans.xml", testDataSetName, "englishDescription", category);
		String rate = TestData.read("PagePlans.xml", testDataSetName, "rate", category);
		String bundleQuantity = TestData.read("PagePlans.xml", testDataSetName, "bundleQuantity", category);
		String rateProduct = TestData.read("PagePlans.xml", testDataSetName, "rateProduct", category);
		String pricing = TestData.read("PagePlans.xml", testDataSetName, "pricing", category);

		this.clickAddNewButton();
		this.setProductCode(productCode);
		this.selectAddDesctiption(description);
		this.clickAddNewDescription();
		this.setNewDescription(englishDescription);
		this.selectCategory(userCategory);
		this.setRate(rate);
		this.selectCurrentCompany();
		this.selectInsideProductsTab();
		this.filterProducts(productCategory);
		this.selectProductCode(productCategory);
		this.verifyMetaField();
		// this.selectPricicngDropdown(pricing);
		this.setBundleQuantity(bundleQuantity);
		this.verifyMetaField();
		this.setModelRate(rateProduct);
		this.clickUpdate();
		this.clickSaveChanges();

		PlansPage.logger.exitMethod();
		return GlobalController.brw.initElements(PlansPage.class);
	}

	public PlansPage editPlan(String productCategory, String testDataSetName, String category) throws Exception {
		String pricing = TestData.read("PagePlans.xml", testDataSetName, "pricing", category);
		String bundleQuantity = TestData.read("PagePlans.xml", testDataSetName, "bundleQuantity", category);
		String rateProduct = TestData.read("PagePlans.xml", testDataSetName, "rateProduct", category);
		this.selectFirstPlanInPlanList();
		this.clickEdit();
		this.selectInsideProductsTab();
		this.selectProductCode(productCategory);
		// this.selectPricicngDropdown(pricing);
		this.setBundleQuantity(bundleQuantity);
		this.setModelRate(rateProduct);
		this.clickUpdate();
		this.clickSaveChanges();
		return GlobalController.brw.initElements(PlansPage.class);
	}

	public PlansPage addPlan(AddPlanField addPlanField, String userCategory, String productCategory, String planPeriod,
			String testDataSetName, String category) throws Exception {
		PlansPage.logger.enterMethod();

		String productCode = TestData.read("PagePlans.xml", testDataSetName, "productCode", category);
		String description = TestData.read("PagePlans.xml", testDataSetName, "description", category);
		String englishDescription = TestData.read("PagePlans.xml", testDataSetName, "englishDescription", category);
		String rate = TestData.read("PagePlans.xml", testDataSetName, "rate", category);
		String bundleQuantity = TestData.read("PagePlans.xml", testDataSetName, "bundleQuantity", category);
		String rateProduct = TestData.read("PagePlans.xml", testDataSetName, "rateProduct", category);
		String pricing = TestData.read("PagePlans.xml", testDataSetName, "pricing", category);
		String bundledPeriod = TestData.read("PagePlans.xml", testDataSetName, "bundledPeriod", category);
		String bundleQuantity2 = TestData.read("PagePlans.xml", testDataSetName, "bundleQuantity2", category);
		String rateProduct2 = TestData.read("PagePlans.xml", testDataSetName, "rateProduct2", category);
		String note = TestData.read("PagePlans.xml", testDataSetName, "note", category);

		switch (addPlanField) {

		case PLANPERIOD:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.selectPlanPeriod(planPeriod);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			this.verifyMetaField();
			this.setBundleQuantity(bundleQuantity);
			this.verifyMetaField();
			this.setModelRate(rateProduct);
			this.clickUpdate();
			this.clickSaveChanges();
			break;
		case MULTIPLEPLAN:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.selectPlanPeriod(planPeriod);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			this.verifyMetaField();
			this.setBundleQuantity(bundleQuantity);
			this.verifyMetaField();
			this.setModelRate(rateProduct);
			this.clickUpdate();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			this.verifyMetaField();
			this.setBundleQuantity(bundleQuantity2);
			this.verifyMetaField();
			this.setModelRate(rateProduct2);
			this.clickUpdate();
			this.clickSaveChanges();
			break;
		case WITHNOTE:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.selectPlanPeriod(planPeriod);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.enterNotes(note);
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			this.verifyMetaField();
			this.setBundleQuantity(bundleQuantity);
			this.verifyMetaField();
			this.setModelRate(rateProduct);
			this.clickUpdate();
			this.clickSaveChanges();
			break;
		default:
			throw new Exception("Invalid Step Provided. Not defined in enumeration");
		}

		PlansPage.logger.exitMethod();
		return GlobalController.brw.initElements(PlansPage.class);
	}

	/**
	 * This method will click on Add product button
	 * 
	 * @return
	 * @throws Exception
	 */

	PlansPage clickAddProduct() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_AddProduct);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will click image + button
	 * 
	 * @throws Exception
	 */
	PlansPage clickAddDescription() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_ADDDESCRIPTION);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will Product Tab
	 * 
	 * @throws Exception
	 */
	PlansPage clickProductTab() throws Exception {
		GlobalController.brw.clickLinkText(this.PRODUCT_TAB);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will set description
	 * 
	 * @param englishDescription
	 * @throws Exception
	 */
	PlansPage setDescription(String englishDescription) throws Exception {
		GlobalController.brw.setText(this.TB_ENGLISHDESCRIPTION, englishDescription);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will Enable globle checkbox
	 * 
	 * @return
	 * @throws Exception
	 */
	PlansPage enableGlobleCheckBox(boolean globle) throws Exception {
		if (globle) {
			GlobalController.brw.check(this.CB_GLOBLE);
		} else {
			GlobalController.brw.uncheck(this.CB_GLOBLE);
		}
		return GlobalController.brw.initElements(PlansPage.class);
	}

	/**
	 * This method will Set Start Date
	 * 
	 * @throws Exception
	 */
	PlansPage setStartDate(String startDate) throws Exception {
		GlobalController.brw.setText(this.TB_START_DATE, startDate);

		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * This method will Set End Date
	 * 
	 * @throws Exception
	 */
	PlansPage setEndDate(String startDate) throws Exception {
		GlobalController.brw.setText(this.TB_END_DATE, startDate);

		return GlobalController.brw.initElements(PlansPage.class);

	}

	PlansPage clickSaveButton() throws Exception {
		GlobalController.brw.clickLinkText(this.LT_SAVEPLUGINS);
		return GlobalController.brw.initElements(PlansPage.class);

	}

	/**
	 * <<<<<<< .mine This method will click on Product
	 * 
	 * @throws Exception
	 */
	public PlansPage selectAddedProduct(String productName) throws Exception {
		GlobalController.brw.selectListItem(this.TAB_PRODUCT_NAME, productName);
		return GlobalController.brw.initElements(PlansPage.class);
	}

	public String addPlanForMobileCalls(String freeUsagePoolName, String productCategory, String products, String testDataSetName,
			String category) throws Exception {

		String productCode = TestData.read("PagePlans.xml", testDataSetName, "productCode", category);
		String description = TestData.read("PagePlans.xml", testDataSetName, "description", category);
		String englishDescription = TestData.read("PagePlans.xml", testDataSetName, "englishDescription", category);

		this.clickAddNewButton();
		this.setProductCode(productCode);
		this.selectAddDesctiption(description);
		this.clickAddNewDescription();
		this.setNewDescription(englishDescription);
		this.selectProductCategory(productCategory);
		this.selectFreeUsagePool(freeUsagePoolName);

		this.selectCurrentCompany();
		this.selectInsideProductsTab();
		this.selectProduct(productCategory);
		this.clickSelectedProduct();

		this.clickUpdate();
		this.clickSaveChanges();
		return englishDescription;

	}

	public String addProductInPlan(String testDataSetName, String category, String categoryName, String productName) throws Exception {
		String englishDescription = TestData.read("PageProducts.xml", testDataSetName, "englishDescription", category);
		String productCode = TestData.read("PageProducts.xml", testDataSetName, "productCode", category);

        this.setProductCode(productCode);
        this.clickAddNewDescription();
        this.setNewDescription(englishDescription);
        this.selectCategory(categoryName);
        this.selectCurrentCompany();
        this.clickProductTab();
        this.selectAddedProduct(productName);
        this.clickSaveChanges();

		return englishDescription;
	}
	
	public String addProductInMultipleCategoryInPlan(String testDataSetName, String category, String categoryName1, String categoryName2,
			String productName) throws Exception {
		String englishDescription = TestData.read("PagePlans.xml", testDataSetName, "englishDescription", category);
		String productCode = TestData.read("PagePlans.xml", testDataSetName, "productCode", category);
		this.clickAddNewButton();
		this.clickAddDescription();
		this.setDescription(englishDescription);
		this.setProductCode(productCode);
		this.selectCategory(categoryName2);

//		GlobalController.brw.pressControlKey();
		this.selectCategory(categoryName1);
//		GlobalController.brw.releaseControlKey();
		this.selectCurrentCompany();
		this.clickProductTab();
		this.selectAddedProduct(productName);
		this.clickSaveChanges();
		this.validatePlanSavedTestData(englishDescription);
		return englishDescription;
	}
	
	/**
	 * This method will validate Added Plan
	 * 
	 * @throws Exception
	 */
	public PlansPage validatePlanSavedTestData(String data) throws Exception {
		GlobalController.brw.validateSavedTestData(this.TBL_PLANS, data);
		return GlobalController.brw.initElements(PlansPage.class);
	}

	public PlansPage verifyCategoryName(String planDescription, String categoryName1, String categoryName2) throws Exception {
		this.selectAddedPlan(planDescription);
		this.validateCategoryLable(categoryName1, categoryName2);
		return GlobalController.brw.initElements(PlansPage.class);
	}
	
	/**
	 * This method will click on Plan
	 * 
	 * @throws Exception
	 */
	public PlansPage selectAddedPlan(String planDescription) throws Exception {
		GlobalController.brw.selectListItem(this.TAB_PLAN_NAME, planDescription);
		return GlobalController.brw.initElements(PlansPage.class);
	}
	
	/**
	 * This method will click on Plan
	 * 
	 * @throws Exception
	 */
	public PlansPage validateCategoryLable(String CategoryName1, String CategoryName2) throws Exception {
		String category1 = GlobalController.brw.getText(this.CATEGORY_NAME1);
		Assert.assertEquals(category1, CategoryName1);
		String category2 = GlobalController.brw.getText(this.CATEGORY_NAME2);
		Assert.assertEquals(category2, CategoryName2);
		return GlobalController.brw.initElements(PlansPage.class);
	}
	
	public String addPlanYearly(AddPlanField addPlanField, String userCategory, String productCategory, String testDataSetName,
			String category, String planPeriod) throws Exception {
		PlansPage.logger.enterMethod();

		String productCode = TestData.read("PagePlans.xml", testDataSetName, "productCode", category);
		String description = TestData.read("PagePlans.xml", testDataSetName, "description", category);
		String englishDescription = TestData.read("PagePlans.xml", testDataSetName, "englishDescription", category);
		String rate = TestData.read("PagePlans.xml", testDataSetName, "rate", category);
		String bundleQuantity = TestData.read("PagePlans.xml", testDataSetName, "bundleQuantity", category);
		String rateProduct = TestData.read("PagePlans.xml", testDataSetName, "rateProduct", category);
		String pricing = TestData.read("PagePlans.xml", testDataSetName, "pricing", category);
		String bundledPeriod = TestData.read("PagePlans.xml", testDataSetName, "bundledPeriodOneTime", category);
		String note = TestData.read("PagePlans.xml", testDataSetName, "note", category);
		String precedence = TestData.read("PagePlans.xml", testDataSetName, "precedence", category);
		String bundleQuantity2 = TestData.read("PagePlans.xml", testDataSetName, "bundleQuantity2", category);
		String rateProduct2 = TestData.read("PagePlans.xml", testDataSetName, "rateProduct2", category);
		String bundledPeriodOneTime = TestData.read("PagePlans.xml", testDataSetName, "bundledPeriodOneTime", category);
		String type = TestData.read("PagePlans.xml", testDataSetName, "type", category);

		switch (addPlanField) {
		case ALL:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			// this.selectPricicngDropdown(pricing);
			this.setBundleQuantity(bundleQuantity);
			this.setModelRate(rateProduct);
			this.clickUpdate();
			break;
		case PRODUCT:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			this.setBundleQuantity(bundleQuantity);
			this.clickUpdate();
			break;
		case BUNDLEDPERIOD:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.selectPlanPeriod(planPeriod);
			// this.selectOrderType(type);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			// this.selectPricicngDropdown(pricing);
			this.setBundleQuantity(bundleQuantity);
			this.verifyMetaField();
			this.selectBundeledPeriod(bundledPeriodOneTime);
			this.verifyMetaField();
			this.setModelRate(rateProduct);
			this.clickUpdate();
			break;
		case WITHNOTE:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.enterNotes(note);
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			this.verifyMetaField();
			this.enterPrecedence(precedence);
			this.setBundleQuantity(bundleQuantity);
			this.verifyMetaField();
			this.selectBundeledPeriod(bundledPeriod);
			this.verifyMetaField();
			this.setModelRate(rateProduct);
			this.clickUpdate();

			break;

		default:
			throw new Exception("Invalid Step Provided. Not defined in enumeration");
		}

		this.clickSaveChanges();

		PlansPage.logger.exitMethod();
		return englishDescription;
	}
	public String addPlanMonthly(AddPlanField addPlanField, String userCategory, String productCategory, String testDataSetName,
			String category) throws Exception {
		PlansPage.logger.enterMethod();

		String productCode = TestData.read("PagePlans.xml", testDataSetName, "productCode", category);
		String description = TestData.read("PagePlans.xml", testDataSetName, "description", category);
		String englishDescription = TestData.read("PagePlans.xml", testDataSetName, "englishDescription", category);
		String rate = TestData.read("PagePlans.xml", testDataSetName, "rate", category);
		String bundleQuantity = TestData.read("PagePlans.xml", testDataSetName, "bundleQuantity", category);
		String rateProduct = TestData.read("PagePlans.xml", testDataSetName, "rateProduct", category);
		String pricing = TestData.read("PagePlans.xml", testDataSetName, "pricing", category);
		String bundledPeriod = TestData.read("PagePlans.xml", testDataSetName, "bundledPeriodOneTime", category);
		String note = TestData.read("PagePlans.xml", testDataSetName, "note", category);
		String precedence = TestData.read("PagePlans.xml", testDataSetName, "precedence", category);
		String bundleQuantity2 = TestData.read("PagePlans.xml", testDataSetName, "bundleQuantity2", category);
		String rateProduct2 = TestData.read("PagePlans.xml", testDataSetName, "rateProduct2", category);

		switch (addPlanField) {
		case ALL:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			// this.selectPricicngDropdown(pricing);
			this.setBundleQuantity(bundleQuantity);
			this.setModelRate(rateProduct);
			this.clickUpdate();
			break;
		case PRODUCT:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			this.setBundleQuantity(bundleQuantity);
			this.clickUpdate();
			break;
		case BUNDLEDPERIOD:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			// this.selectPricicngDropdown(pricing);
			this.setBundleQuantity(bundleQuantity);
			this.verifyMetaField();
			this.selectBundeledPeriod(bundledPeriod);
			this.verifyMetaField();
			this.setModelRate(rateProduct);
			this.clickUpdate();
			break;
		case WITHNOTE:
			this.clickAddNewButton();
			this.setProductCode(productCode);
			this.selectAddDesctiption(description);
			this.clickAddNewDescription();
			this.setNewDescription(englishDescription);
			this.setRate(rate);
			this.selectCategory(userCategory);
			this.selectCurrentCompany();
			this.enterNotes(note);
			this.selectInsideProductsTab();
			this.selectProductCode(productCategory);
			this.verifyMetaField();
			this.enterPrecedence(precedence);
			this.setBundleQuantity(bundleQuantity);
			this.verifyMetaField();
			this.selectBundeledPeriod(bundledPeriod);
			this.verifyMetaField();
			this.setModelRate(rateProduct);
			this.clickUpdate();

			break;

		default:
			throw new Exception("Invalid Step Provided. Not defined in enumeration");
		}

		this.clickSaveChanges();

		PlansPage.logger.exitMethod();
		return englishDescription;
	}
	
	public PlansPage setdifferentcurrency(String currency) throws Exception {
		  GlobalController.brw.selectDropDown(this.Details_Currency, currency);
		  return GlobalController.brw.initElements(PlansPage.class);
		 }
	
	public String addPlanWithDifferentCurrency(String userCategory, String productCategory, String testDataSetName, String category)
			   throws Exception {
			  PlansPage.logger.enterMethod();

			  String productCode = TestData.read("PagePlans.xml", testDataSetName, "productCode", category);
			  String description = TestData.read("PagePlans.xml", testDataSetName, "description", category);
			  String englishDescription = TestData.read("PagePlans.xml", testDataSetName, "englishDescription", category);
			  String differentcurrency = TestData.read("PagePlans.xml", testDataSetName, "currencydiffer", category);
			  String rate = TestData.read("PagePlans.xml", testDataSetName, "rate", category);

			  this.clickAddNewButton();
			  this.setProductCode(productCode);
			  this.selectAddDesctiption(description);
			  this.clickAddNewDescription();
			  this.setNewDescription(englishDescription);
			  this.setRate(rate);
			  this.selectCategory(userCategory);
			  this.selectCurrentCompany();
			  this.setdifferentcurrency(differentcurrency);
			  this.selectInsideProductsTab();
			  this.selectProductCode(productCategory);
			  this.clickSaveChanges();

			  PlansPage.logger.exitMethod();
			  return englishDescription;
			 }
}