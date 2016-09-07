package com.jbilling.framework.interfaces.impl.selenium;

import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.jbilling.framework.globals.GlobalConsts;
import com.jbilling.framework.globals.GlobalEnumerations.BrowsersTypes;
import com.jbilling.framework.globals.GlobalEnumerations.LogicalOperators;
import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.IBrowser;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.utilities.textutilities.ArrayUtilities;
import com.jbilling.framework.utilities.textutilities.TextUtilities;

/**
 * Class to implement all IBrowser methods. Every time, we need to change
 * automation tool, we just need to change this class with a new class.
 * 
 * @author Aishwarya Dwivedi
 * @since 1.0
 * 
 * @version 1.0
 */
public class BrowserSelenium implements IBrowser {

	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	private static <T> T _instantiatePage(Class<T> pageClassToProxy) {
		try {
			try {
				Constructor<T> constructor = pageClassToProxy.getConstructor();
				return constructor.newInstance();
			} catch (NoSuchMethodException e) {
				return pageClassToProxy.newInstance();
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	BrowsersTypes browserType = null;

	WebDriver driver = null;

	private String _extendedTextToXPath = "";

	public BrowserSelenium(BrowsersTypes brwType) {
		this.browserType = brwType;
		this.driver = _getBrowserWebDriver(browserType);
	}


    private void _moveToAndClick (WebElement we) throws Exception {
        this._getWebDriverWait(GlobalConsts.IMPLICIT_TIME_LIMIT).until(ExpectedConditions.elementToBeClickable(we));
        try {
            new Actions(driver).moveToElement(we).perform();
            we.click();
        } catch (org.openqa.selenium.interactions.MoveTargetOutOfBoundsException e) {
            // Special case for partially visible elements
            _clickUsingJavaScript(we);
        }
    }

	private void _clickUsingJavaScript(WebElement we) {
		BrowserSelenium.logger.enterMethod();
		((JavascriptExecutor) this.driver).executeScript("arguments[0].click();", we);
		BrowserSelenium.logger.exitMethod();
	}

	private Object _executeJavaScript(String jsCode) {
		return ((JavascriptExecutor) this.driver).executeScript(jsCode);
	}

	/**
	 * 
	 * @param ef
	 * @return
	 * @throws Exception
	 */
	private WebElement _findElement(ElementField ef) throws Exception {
		if (ef == null) {
			throw new IllegalArgumentException("_findElement called with null ElementField ef");
		}

		WebElement we = null;
		try {
			we = this.driver.findElement(this._getByLocator(ef));
		} catch (NoSuchElementException nse) {
		    throw new Exception("Element not found for provided element field: " + ef);
		}

		if (we == null) {
			throw new Exception("Element not found: " + ef);
		}

		return we;
	}

	private WebDriver _getBrowserWebDriver(BrowsersTypes browserType) {
		BrowserSelenium.logger.enterMethod();
		if (this.driver != null) {
			return this.driver;
		}

		DesiredCapabilities capabilities = null;

		switch (browserType) {
		case InternetExplorer:
			capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
			System.setProperty("webdriver.ie.driver", "./resources/brwDrivers/IEDriverServer.exe");
			this.driver = new InternetExplorerDriver(capabilities);

			break;
		case Firefox:
			capabilities = DesiredCapabilities.firefox();
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			this.driver = new FirefoxDriver(capabilities);

			break;
		case Chrome:
			capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			System.setProperty("webdriver.ie.driver", "./resources/brwDrivers/chromedriver.exe");
			this.driver = new ChromeDriver(capabilities);
			break;
		}

        Timeouts timeouts = this.driver.manage().timeouts();
        timeouts.pageLoadTimeout(GlobalConsts.IMPLICIT_TIME_LIMIT, TimeUnit.SECONDS);
        timeouts.setScriptTimeout(GlobalConsts.IMPLICIT_TIME_LIMIT, TimeUnit.SECONDS);
        timeouts.implicitlyWait(GlobalConsts.IMPLICIT_TIME_LIMIT, TimeUnit.SECONDS);

		BrowserSelenium.logger.exitMethod();
		return this.driver;
	}

	
	/**
	 * 
	 * @param ef
	 * @return
	 * @throws Exception
	 */
	private By _getByLocator(ElementField ef) throws Exception {
		BrowserSelenium.logger.enterMethod();
		By biLocator = null;
		BrowserSelenium.logger.info("Getting locator for " + ef);

		try {
			if (TextUtilities.isBlank(ef.elementLocatorId) == false) {
				BrowserSelenium.logger.info("Locator info found for ID attribute as: " + ef.elementLocatorId);
				biLocator = By.id(ef.elementLocatorId);
			} else if (TextUtilities.isBlank(ef.elementLocatorName) == false) {
				BrowserSelenium.logger.info("Locator info found for NAME attribute as: " + ef.elementLocatorName);
				biLocator = By.name(ef.elementLocatorName);
			} else if (TextUtilities.isBlank(ef.elementLocatorXpath) == false) {
				BrowserSelenium.logger.info("Locator info found for XPATH attribute as: " + ef.elementLocatorXpath);
				biLocator = this._parseElementOnXpath(ef);
			} else if (TextUtilities.isBlank(ef.elementLocatorCss) == false) {
				BrowserSelenium.logger.info("Locator info found for CSS attribute as: " + ef.elementLocatorCss);
				biLocator = By.cssSelector(ef.elementLocatorCss);
			} else {
				throw new Exception("No field information provided");
			}
		} catch (Exception e) {
			BrowserSelenium.logger.error(e);
			throw e;
		}

		BrowserSelenium.logger.exitMethod();
		return biLocator;
	}

	private String _getExtendedTextToXPath() {
		BrowserSelenium.logger.info(this._extendedTextToXPath + " value asked");
		return this._extendedTextToXPath;
	}

	private WebElement _getListItem(ElementField ef, String targetValue) throws Exception {
		BrowserSelenium.logger.enterMethod();
		this._setExtendedTextToXPath("");

		List<WebElement> liList = this.driver.findElements(this._getByLocator(ef));

		// To use "USE_EXTENDED_TEXT_TO_XPATH", make sure _GetByLocator gets
		// called before its use
		if (targetValue == "USE_EXTENDED_TEXT_TO_XPATH") {
			targetValue = TextUtilities.replaceAllEscapeRegEx(this._getExtendedTextToXPath(), GlobalConsts.PRE_POST_EXTENDED_TEXTS_MARKER,
					"");
		}
		int targetValueIndex = 0;

		for (WebElement we : liList) {
			BrowserSelenium.logger.info("targetValue to find in listbox: \"" + targetValue + "\"");
			BrowserSelenium.logger.info("current element text by we.getText(): \"" + we.getText() + "\"");
			if (TextUtilities.compareValue(targetValue.trim(), we.getText().trim(), true, TextComparators.equals)) {
				break;
			}
			targetValueIndex++;
		}

		if (targetValueIndex >= liList.size()) {
			throw new Exception("No list item found with value \"" + targetValue + "\" in its list element");
		}
		BrowserSelenium.logger.info("targetValue found at index: " + targetValueIndex);
		BrowserSelenium.logger.info(liList.get(targetValueIndex).getText());
		WebElement targetElement = liList.get(targetValueIndex);

		BrowserSelenium.logger.exitMethod();
		return targetElement;
	}

	private WebElement _getTable(ElementField ef) throws Exception {
		this._waitForElement(ef);

		WebElement tableElement = this.driver.findElement(this._getByLocator(ef));

		return tableElement;
	}

	private WebElement _getTableCell(ElementField efTable, int row, int col) throws Exception {
		BrowserSelenium.logger.enterMethod();
		WebElement table = this._getTable(efTable);
		List<WebElement> tableRows = this._getTableRows(table);

		if (tableRows.size() > 0) {
			WebElement firstRow = tableRows.get(tableRows.size() - 1);
			List<WebElement> tableCols = this._getTableRowCols(firstRow);

			if (tableCols.size() > 0) {
				if ((row >= 0) && (row <= tableRows.size()) && (col >= 0) && (col <= tableCols.size())) {
					WebElement rowEle = tableRows.get(row);
					return this._getTableRowCols(rowEle).get(col);
				}
			}
		}

		return null;
	}

	private List<WebElement> _getTableCells(ElementField efTable) throws Exception {
		BrowserSelenium.logger.enterMethod();
        BrowserSelenium.logger.info("getting table cells for ElementField: " + efTable);
		WebElement table = this._getTable(efTable);
		List<WebElement> tableCells = this._getTableCells(table);

		return tableCells;
	}

	private List<WebElement> _getTableCells(WebElement tableElement) throws Exception {
		Assert.assertTrue(tableElement.isDisplayed());
		Assert.assertTrue(tableElement.isEnabled());

		List<WebElement> cellElements = tableElement.findElements(By.tagName("td"));

		BrowserSelenium.logger.info("Number of cells in table: " + cellElements.size());

		return cellElements;
	}

	private List<WebElement> _getTableRowCols(WebElement tableRow) {
		if (tableRow == null) {
			return null;
		}

		List<WebElement> cols = tableRow.findElements(By.tagName("td"));

		BrowserSelenium.logger.info("Number of cols in table: " + cols.size());

		return cols;
	}

	private List<WebElement> _getTableRows(WebElement tableElement) throws Exception {
		Assert.assertTrue(tableElement.isDisplayed());
		Assert.assertTrue(tableElement.isEnabled());

		List<WebElement> rowsElements = tableElement.findElements(By.tagName("tr"));

		BrowserSelenium.logger.info("Number of rows in table: " + rowsElements.size());

		return rowsElements;
	}

	private WebDriverWait _getWebDriverWait(int timeoutInSeconds) {
		return new WebDriverWait(this.getCurrentWebDriver(), timeoutInSeconds);
	}

	private By _parseElementOnXpath(ElementField ef) {
		if (ef == null) {
			return null;
		}
		BrowserSelenium.logger.enterMethod();

		ef.elementLocatorXpath = TextUtilities.nullToBlank(ef.elementLocatorXpath, false);
		if (TextUtilities.contains(ef.elementLocatorXpath, GlobalConsts.XPathToTextSeparator)) {
			int lastIndex = TextUtilities.indexOf(ef.elementLocatorXpath, GlobalConsts.XPathToTextSeparator);
			String xpathString = TextUtilities.substring(ef.elementLocatorXpath, 0, lastIndex);
			String extendedDataToXpath = TextUtilities.substring(ef.elementLocatorXpath,
					lastIndex + GlobalConsts.XPathToTextSeparator.length(), ef.elementLocatorXpath.length());
			BrowserSelenium.logger.info("Xpath: " + xpathString + " || Extended Text: " + extendedDataToXpath);
			this._setExtendedTextToXPath(extendedDataToXpath);

			ef.elementLocatorXpath = xpathString;
		}

		BrowserSelenium.logger.info(ef.elementLocatorXpath + " is used");
		By loc = By.xpath(ef.elementLocatorXpath);

		BrowserSelenium.logger.exitMethod();
		return loc;
	}

	private void _setExtendedTextToXPath(String str) {
		this._extendedTextToXPath = str;
		BrowserSelenium.logger.info(this._extendedTextToXPath + " value set");
	}

    private Clipboard _getOrCreateClipboard () {
        return (GraphicsEnvironment.isHeadless())
                ? new Clipboard("HeadlessClipboard")
                : Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    private void _setText(ElementField ef, String text, boolean trimText, boolean allCaps) throws Exception {

        BrowserSelenium.logger.info("Set text : " + text + " in element: " + ef);
        this.clearTextBox(ef);
        if (trimText) {
            text = text.trim();
        }
        if (allCaps) {
            text = text.toUpperCase();
        }
        StringSelection selection = new StringSelection(text);
        this._getOrCreateClipboard().setContents(selection, null);

        WebElement textBox = this._findElement(ef);
        _moveToAndClick(textBox);
        textBox.sendKeys(Keys.CONTROL + "v");

        List<String> dataList = new ArrayList<>();
        dataList.add(this.getAttribute(ef, "text"));
        dataList.add(this.getAttribute(ef, "value"));

        if ((TextUtilities.compareValue(text, dataList, true, TextComparators.equals, LogicalOperators.OR) == false)) {
            this.clearTextBox(ef);
            textBox.sendKeys(text);
        }
        BrowserSelenium.logger.info("Resulted in text : " + this.getAttribute(ef, "text") + " in element: " + ef);
        BrowserSelenium.logger.info("Resulted in value : " + this.getAttribute(ef, "value") + " in element: " + ef);
    }

	/**
	 * Wait for element enabled
	 * 
	 * @param element
	 * @param timeout
	 * @throws Exception
	 */
	private void _waitForAjaxElementVisibilityAndEnabled(By byEle, int timeoutInSeconds) throws Exception {
		if (byEle != null) {
			this._getWebDriverWait(timeoutInSeconds).until(ExpectedConditions.elementToBeClickable(byEle));
		} else {

		}
	}

	/**
	 * Wait for element enabled
	 * 
	 * @param element
	 * @param timeout
	 * @throws Exception
	 */
	private void _waitForAjaxElementVisibilityAndEnabled(ElementField element, int timeoutInSeconds) throws Exception {
		By ele = null;
		try {
			ele = this._getByLocator(element);
		} catch (Exception e) {
			throw e;
		}

		if (ele != null) {
			this._waitForAjaxElementVisibilityAndEnabled(ele, timeoutInSeconds);
		}
	}

	private void _waitForElement(ElementField element) throws Exception {
		BrowserSelenium.logger.enterMethod("Waiting for element: " + element);
		this.waitForElement(element, GlobalConsts.LEAST_TIMEOUT_MILLISECONDS);
		BrowserSelenium.logger.exitMethod();
	}

    // wait for jQuery to load
    private final ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
        @Override
        public Boolean apply(WebDriver theDriver) {
            try {
                return ((Long) BrowserSelenium.this._executeJavaScript("return jQuery.active") == 0);
            } catch (Exception e) {
                return true;
            }
        }
    };
    // wait for JavaScript to load
    private final ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
        @Override
        public Boolean apply(WebDriver theDriver) {
            Object rsltJs = BrowserSelenium.this._executeJavaScript("return document.readyState");
            if (rsltJs == null) {
                rsltJs = "";
            }
            return rsltJs.toString().equals("complete") || rsltJs.toString().equals("loaded");
        }
    };

	private boolean _waitForJStoLoad() {
		BrowserSelenium.logger.enterMethod();

		WebDriverWait wait = this._getWebDriverWait(GlobalConsts.LEAST_TIMEOUT_MILLISECONDS);
		boolean waitDone = wait.until(jQueryLoad) && wait.until(jsLoad);

		BrowserSelenium.logger.exitMethod("DOM load wait done: " + waitDone);
		return waitDone;
	}

	@Override
	public void check(ElementField ef) throws Exception {
		this._waitForElement(ef);

		WebElement ele = this._findElement(ef);
		if (ele.isSelected() == false) {
            _moveToAndClick(ele);
		}
	}

	@Override
	public boolean checkCheckBoxUnChecked(ElementField ef) throws Exception {
		this._waitForElement(ef);
        return ! this._findElement(ef).isSelected();
	}

	@Override
	public boolean checkCheckBoxChecked(ElementField ef) throws Exception {
		this._waitForElement(ef);
		return this._findElement(ef).isSelected();
	}

	@Override
	public void clearCookies() throws Exception {
		this.driver.manage().deleteAllCookies();
	}

	@Override
	public void clearTextBox(ElementField ef) throws Exception {
		this._waitForElement(ef);

		this._findElement(ef).clear();
	}

	@Override
	public void click(ElementField ef) throws Exception {
		this._waitForElement(ef);
        BrowserSelenium.logger.info("clicking " + ef);
        _moveToAndClick(this._findElement(ef));
	}

	@Override
	public void clickElementForElement(ElementField ef, ElementField efToAppear) throws Exception {
		this._waitForElement(ef);
		this._waitForJStoLoad();

		int attempts = 0;
		boolean eleFound = false;
		while ((eleFound == false) && (attempts <= 5)) {
	        _moveToAndClick(this._findElement(ef));
			this._waitForJStoLoad();

			BrowserSelenium.logger.info(efToAppear + " field appearance check");
			WebElement we = this._findElement(efToAppear);
			eleFound = we.isDisplayed() && we.isEnabled();
			attempts++;
		}
	}

	@Override
	public void clickLinkText(ElementField ef) throws Exception {
		this.clickLinkText(ef, false);
	}

    private ExpectedCondition<Boolean> elementHasStoppedMoving(final By locator) {
        return new ExpectedCondition<Boolean>() {
            private int counter = 0;
            @Override
            public Boolean apply(WebDriver theDriver) {
                Point initialLocation;
                try {
                    initialLocation = ((Locatable) theDriver.findElement(locator)).getCoordinates().inViewPort();
                }catch (org.openqa.selenium.StaleElementReferenceException e) {
                    counter += 1;
                    return counter < 10;
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Point finalLocation;
                try {
                    finalLocation = ((Locatable) theDriver.findElement(locator)).getCoordinates().inViewPort();
                }catch (org.openqa.selenium.StaleElementReferenceException e) {
                    counter += 1;
                    return counter < 10;
                }
                return  initialLocation.equals(finalLocation);
            }
        };
    }

    @Override
    public boolean waitUntilElementStopMoving (ElementField element) throws Exception {
        By locator = this._getByLocator(element);
        _getWebDriverWait(GlobalConsts.IMPLICIT_TIME_LIMIT).until(ExpectedConditions.visibilityOfElementLocated(locator));
        WebDriverWait wait = new WebDriverWait(driver, GlobalConsts.IMPLICIT_TIME_LIMIT, 100);
        return wait.until(elementHasStoppedMoving(locator));
    }

    @Override
    public void waitForAjaxElement (ElementField ef) throws Exception {
        _waitForJStoLoad();
        _waitForAjaxElementVisibilityAndEnabled(ef, GlobalConsts.IMPLICIT_TIME_LIMIT);
    }

	@Override
	public void clickLinkText(ElementField ef, boolean optionallyWait) throws Exception {
		BrowserSelenium.logger.enterMethod();
		if (optionallyWait) {
			try {
				this._waitForElement(ef);
			} catch (Exception e) {
			}
		} else {
			this._waitForElement(ef);
		}

        WebElement we = this._findElement(ef);
        _moveToAndClick(we);
		BrowserSelenium.logger.exitMethod();
	}

	@Override
	public void clickRowWithText(ElementField ef, String text) throws Exception {
		List<WebElement> rows = this._getTableRows(this._getTable(ef));
		for (WebElement row : rows) {
			String textAttrVal = row.getAttribute("text");
			if (TextUtilities.compareValue(textAttrVal, text, true, TextComparators.equals)) {
                _moveToAndClick(row);
				break;
			}
		}
	}

	@Override
	public void clickTableCell(ElementField efTable, int row, int col) throws Exception {
		BrowserSelenium.logger.enterMethod();
		WebElement cell = this._getTableCell(efTable, row, col);
        _moveToAndClick(cell);
		BrowserSelenium.logger.exitMethod();
	}

    @Override
    public void clickTableCellWithText(ElementField efTable, String text) throws Exception {
        BrowserSelenium.logger.enterMethod();
        BrowserSelenium.logger.info("Finding text: " + text);
        List<WebElement> cells = this._getTableCells(efTable);
        for (WebElement cell : cells) {
            BrowserSelenium.logger.info(cell.getText() + " -- " + text);
            if (TextUtilities.compareValue(text, cell.getText(), true, TextComparators.contains)) {
                this._clickUsingJavaScript(cell);
                break;
            } else {
                WebElement cellHavingText = cell.findElement(By.xpath("//strong[contains(text(), '" + text + "')]"));
                if (cellHavingText != null) {
                    BrowserSelenium.logger.info("eleme found");
                    _moveToAndClick(cellHavingText);
                }
            }
        }
        BrowserSelenium.logger.exitMethod();
    }

	@Override
	public void clickTableRow(ElementField efTable, int rowNum) throws Exception {
		List<WebElement> tableRows = this._getTableRows(this._getTable(efTable));

		for (int rn = 0; rn < tableRows.size(); rn++) {
			if (rn == rowNum) {
                _moveToAndClick(tableRows.get(rn));
			}
		}
	}

	@Override
	public String getAttribute(ElementField ef, String attributeName) throws Exception {
		this._waitForElement(ef);

		String attributeValue = this._findElement(ef).getAttribute(attributeName);
		return TextUtilities.nullToBlank(attributeValue, false);
	}

	@Override
	public WebDriver getCurrentWebDriver() {
		return this.driver;
	}

	@Override
	public WebElement getListItem(ElementField ef, String targetValue) throws Exception {
		BrowserSelenium.logger.enterMethod();
		this._setExtendedTextToXPath("");
		WebElement targetElement = this._getListItem(ef, targetValue);

		BrowserSelenium.logger.exitMethod();
		return targetElement;
	}

	@Override
	public String getText(ElementField ef) throws Exception {
		this._waitForElement(ef);

		String text = this._findElement(ef).getText();
		return text;
	}

	@Override
	public <T> T initElements(Class<T> pageClassToProxy) throws IllegalArgumentException, IllegalAccessException {
		BrowserSelenium.logger.enterMethod();
		BrowserSelenium.logger.info(pageClassToProxy.getName());
		T page = BrowserSelenium._instantiatePage(pageClassToProxy);

		Field[] fld = page.getClass().getDeclaredFields();
		if (ArrayUtilities.isEmpty(fld) == false) {
			for (Field f : fld) {
				if (f.isAnnotationPresent(LocateBy.class)) {
					LocateBy l = f.getAnnotation(LocateBy.class);

					ElementField ef = new ElementField();
					if (TextUtilities.isBlank(l.id()) == false) {
						ef.elementLocatorId = l.id();
					}
					if (TextUtilities.isBlank(l.name()) == false) {
						ef.elementLocatorName = l.name();
					}
					if (TextUtilities.isBlank(l.css()) == false) {
						ef.elementLocatorCss = l.css();
					}
					if (TextUtilities.isBlank(l.xpath()) == false) {
						ef.elementLocatorXpath = l.xpath();
					}

					f.setAccessible(true);
					f.set(page, ef);
				}
			}
		}
		BrowserSelenium.logger.exitMethod();
		return page;
	}

	/**
	 * Verify Element is Present
	 * 
	 * @param locator
	 * @throws Exception
	 */
	@Override
	public boolean isElementPresent(ElementField ef) throws Exception {
		boolean elePresent = true;

		try {
			this._waitForElement(ef);
		} catch (Exception e) {
			return false;
		}

		return elePresent;
	}

	@Override
	public boolean isSelected(ElementField ef) throws Exception {
		this._waitForElement(ef);

		WebElement ele = this._findElement(ef);
		return ele.isSelected();
	}

	@Override
	public void maximize() throws Exception {
		this.driver.manage().window().maximize();
	}

	@Override
	public void navigateToUrl(String url) throws Exception {
		this.driver.get(url);
	}

	@Override
	public void openFileUsingOpenFileDialog(String fileName) {
		try {
			StringSelection stringSelection = new StringSelection(fileName);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, stringSelection);

			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (Exception e) {
			// eat exception
		}
	}

	@Override
	public void pressEnter(ElementField ef) throws Exception {
		this._waitForElement(ef);

		WebElement el = this._findElement(ef);
		el.sendKeys(Keys.ENTER);
	}

	@Override
	public void pressTab(ElementField ef) throws Exception {
		this._waitForElement(ef);

		WebElement el = this._findElement(ef);
		el.sendKeys(Keys.TAB);
	}

	@Override
	public void selectDropDown(ElementField ef, String targetValue) throws Exception {
		this._setExtendedTextToXPath("");
		this._waitForElement(ef);

        WebElement element = _getWebDriverWait(GlobalConsts.IMPLICIT_TIME_LIMIT).until(
                ExpectedConditions.visibilityOfElementLocated(_getByLocator(ef)));
		Select se = new Select(element);
		if (targetValue == "USE_EXTENDED_TEXT_TO_XPATH") {
			targetValue = TextUtilities.replaceAllEscapeRegEx(this._getExtendedTextToXPath(), GlobalConsts.PRE_POST_EXTENDED_TEXTS_MARKER,
					"");
		}
        BrowserSelenium.logger.info("selectDropDown["+ ef +"] to " + targetValue);
        new Actions(driver).moveToElement(element).perform();
		se.selectByVisibleText(targetValue);
	}

	@Override
	public void deSelectDropDown(ElementField ef, String targetValue) throws Exception {
		this._setExtendedTextToXPath("");
		this._waitForElement(ef);

        WebElement element = _getWebDriverWait(GlobalConsts.IMPLICIT_TIME_LIMIT).until(
                ExpectedConditions.visibilityOfElementLocated(_getByLocator(ef)));
		Select se = new Select(element);
		if (targetValue == "USE_EXTENDED_TEXT_TO_XPATH") {
			targetValue = TextUtilities.replaceAllEscapeRegEx(this._getExtendedTextToXPath(), GlobalConsts.PRE_POST_EXTENDED_TEXTS_MARKER,
					"");
		}
        BrowserSelenium.logger.info("deselectDropDown["+ ef +"] to " + targetValue);
        new Actions(driver).moveToElement(element).perform();
		se.deselectByVisibleText(targetValue);
	}

	@Override
	public boolean isValuePresentInDropDown(ElementField ef, String valueToVerify) throws Exception {
		this._waitForElement(ef);
		WebElement element = this._findElement(ef);

		Select se = new Select(element);
		List<WebElement> efValues = se.getOptions();
		for (int i = 0; i < efValues.size(); i++) {
			String optionText = efValues.get(i).getText();
			if (TextUtilities.contains(optionText.toLowerCase(), valueToVerify.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void selectListItem(ElementField ef, String targetValue) throws Exception {
		BrowserSelenium.logger.enterMethod();
		BrowserSelenium.logger.info(targetValue + " to find");
		this._waitForJStoLoad();

		_moveToAndClick(this._getListItem(ef, targetValue));
		BrowserSelenium.logger.exitMethod();
	}

	@Override
	public void selectTableRowItem(ElementField ef, String data) throws Exception {
		this._waitForElement(ef);
		WebElement table = this._findElement(ef);
		List<WebElement> cells = null;
		String text;
		// Now get all the TR elements from the table
		List<WebElement> allRows = table.findElements(By.xpath("/thead/tbody/tr"));
		// And iterate over them, getting the cells
		for (WebElement row : allRows) {
			cells = row.findElements(By.xpath("/td[1]"));
		}
		for (WebElement cell : cells) {
			text = cell.getText();
			BrowserSelenium.logger.info("content >>:::::::::::::::   " + cell.getText());
			Assert.assertTrue(text.equals(data));
			WebElement webElem = this.driver.findElement(By.xpath("//strong[contains(text(),'" + text + "')]"));
			_clickUsingJavaScript(webElem);
		}
	}

	@Override
	public void setcurrentDate(ElementField el) throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		String dates = dateFormat.format(date);
		this.setText(el, dates);
	}

	@Override
	public void setText(ElementField ef, String text) throws Exception {
        this._waitForElement(ef);
        this._setText(ef, text, false, false);
	}

	@Override
	public void takeScreenShot(String methodName) {
		File scrFile = ((TakesScreenshot) this.getCurrentWebDriver()).getScreenshotAs(OutputType.FILE);
		// The below method will save the screen shot in drive with test method
		// name
		try {
			String fileNameWithPath = GlobalConsts.getScreenShotsFolderPath() + methodName + ".png";
			FileUtils.copyFile(scrFile, new File(fileNameWithPath));
			BrowserSelenium.logger.info("***Placed screen shot in [" + fileNameWithPath + "] ***");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void uncheck(ElementField ef) throws Exception {
		this._waitForElement(ef);
		WebElement ele = this._findElement(ef);
		if (ele.isSelected()) {
            _moveToAndClick(ele);
		}
	}

    @Override
    public void waitForElement(ElementField element, int timeoutInMilliSeconds) throws Exception {
        BrowserSelenium.logger.enterMethod();

        this._waitForJStoLoad();
        this._getWebDriverWait(timeoutInMilliSeconds / 1000).until(ExpectedConditions.visibilityOfElementLocated(this._getByLocator(element)));

        BrowserSelenium.logger.exitMethod();
    }

    @Override
    public void validateSavedTestData(ElementField efTable, String text) throws Exception {
        BrowserSelenium.logger.enterMethod();
        BrowserSelenium.logger.info("Finding text: " + text + " in table: " + efTable);
        List<WebElement> cells = this._getTableCells(efTable);
        for (WebElement cell : cells) {
            BrowserSelenium.logger.info(cell.getText() + " -- " + text);
            if (TextUtilities.compareValue(cell.getText(), text, true, TextComparators.contains)) {
                break;
            } else {
                WebElement cellHavingText = cell.findElement(By.xpath("//*[contains(text(), '" + text + "')]"));
                if (cellHavingText != null) {
                    BrowserSelenium.logger.info("eleme found");
                    Assert.assertTrue(cellHavingText.isDisplayed());
                }
            }
        }
        BrowserSelenium.logger.exitMethod();
    }

	@Override
	public void verifyUIComponent(ElementField ef) throws Exception {
		this._waitForElement(ef);
		WebElement ele = this._findElement(ef);
		Assert.assertTrue(ele.isDisplayed());
	}

	@Override
	public void quit() {
		this.driver.quit();
	}

	@Override
	public void acceptAlert() {

		final Alert alert = this.driver.switchTo().alert();
		alert.accept();

		BrowserSelenium.logger.debug("Accept the Alert Pop up");
	}

	@Override
	public void getDropDownOptionIsSelected(ElementField ef, String targetValue) throws Exception {
		this._setExtendedTextToXPath("");
		this._waitForElement(ef);
		String cells = "";
		int i;
		WebElement element = this._findElement(ef);
		Select se = new Select(element);
		if (targetValue == "USE_EXTENDED_TEXT_TO_XPATH") {
			targetValue = TextUtilities.replaceAllEscapeRegEx(this._getExtendedTextToXPath(), GlobalConsts.PRE_POST_EXTENDED_TEXTS_MARKER,
					"");
		}
		BrowserSelenium.logger.info("Target Value >>:::::::::::::::   " + targetValue);
		List<WebElement> options = se.getAllSelectedOptions();
		BrowserSelenium.logger.info("Selected Options >>:::::::::::::::   " + options);
		int optionSize = options.size();
		BrowserSelenium.logger.info("Size of Options List >>:::::::::::::::   " + optionSize);
		for (i = 0; i <= optionSize; i++) {
			cells = options.get(i).getText();

			// for (WebElement row : options) {
			// cells = row.getText();
			BrowserSelenium.logger.info("content >>:::::::::::::::   " + cells);
			if (TextUtilities.compareValue(cells, targetValue, true, TextComparators.equals)) {
				Assert.assertTrue(true);
				break;
			} else if (i == optionSize) {
				Assert.assertTrue(TextUtilities.compareValue(cells, targetValue, true, TextComparators.equals), "Given Target Value"
						+ targetValue + " Is Not Default Selected");
			}
		}
	}

	@Override
	public void getDropDownOptionIsPresent(ElementField ef, String targetValue) throws Exception {
		this._setExtendedTextToXPath("");
		this._waitForElement(ef);
		String cells = "";

		WebElement element = this._findElement(ef);
		Select se = new Select(element);
		if (targetValue == "USE_EXTENDED_TEXT_TO_XPATH") {
			targetValue = TextUtilities.replaceAllEscapeRegEx(this._getExtendedTextToXPath(), GlobalConsts.PRE_POST_EXTENDED_TEXTS_MARKER,
					"");
		}
		BrowserSelenium.logger.info("Target Value >>:::::::::::::::   " + targetValue);
		List<WebElement> options = se.getOptions();
		BrowserSelenium.logger.info("Selected Options >>:::::::::::::::   " + options);
		for (WebElement row : options) {
			cells = row.getText();
			BrowserSelenium.logger.info("content >>:::::::::::::::   " + cells);
			if (TextUtilities.compareValue(cells, targetValue, true, TextComparators.equals)) {
				break;
			}
		}
	}
	
	@Override
	public void pressControlKey() {
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
		} catch (Exception e) {
			// eat exception
		}
	}

	@Override
	public void releaseControlKey() {
		try {
			Robot robot = new Robot();
			robot.keyRelease(KeyEvent.VK_CONTROL);
		} catch (Exception e) {
			// eat exception
		}
	}
	
	@Override
	public String getDropDownSelectedValue(ElementField ef) throws Exception {
		this._setExtendedTextToXPath("");
		this._waitForElement(ef);
		String value = "";
		WebElement element = this._findElement(ef);
		Select se = new Select(element);
		WebElement option = se.getFirstSelectedOption();
		return value = option.getText();

	}
}
