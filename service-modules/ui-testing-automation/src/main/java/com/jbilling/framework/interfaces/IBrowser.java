package com.jbilling.framework.interfaces;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;

/**
 * IBrowser interface is a parent interface of all the classes/interfaces in
 * Automation tool independence layer. This interface has all methods
 * declarations which could be valid for any automation tool. E.g. setText,
 * selectText, etc. All automation tool related objects would be created using
 * IBrowser interface only.
 * 
 * @author Aishwarya Dwivedi
 * @since 1.0
 * 
 * @version 1.0
 */
public interface IBrowser {
	<T> T initElements(Class<T> pageClassToProxy) throws IllegalArgumentException, IllegalAccessException;

	void check(ElementField ef) throws Exception;

	void clearCookies() throws Exception;

	void clearTextBox(ElementField ef) throws Exception;

	void click(ElementField ef) throws Exception;

	void clickElementForElement(ElementField ef, ElementField efToAppear) throws Exception;

	void clickLinkText(ElementField ef) throws Exception;

	void clickLinkText(ElementField ef, boolean optionallyWait) throws Exception;

	void clickRowWithText(ElementField ef, String text) throws Exception;

	void clickTableCell(ElementField efTable, int row, int col) throws Exception;

	void clickTableCellWithText(ElementField efTable, String text) throws Exception;

	void clickTableRow(ElementField efTable, int rowNum) throws Exception;

	String getAttribute(ElementField ef, String attributeName) throws Exception;

	WebDriver getCurrentWebDriver();

	WebElement getListItem(ElementField ef, String targetValue) throws Exception;

	String getText(ElementField ef) throws Exception;

	boolean isElementPresent(ElementField element) throws Exception;

	boolean isSelected(ElementField ef) throws Exception;

	void maximize() throws Exception;

	void navigateToUrl(String url) throws Exception;

	void openFileUsingOpenFileDialog(String fileName);

	void pressEnter(ElementField ef) throws Exception;

	void pressTab(ElementField ef) throws Exception;

	void selectDropDown(ElementField ef, String targetValue) throws Exception;

	boolean isValuePresentInDropDown(ElementField ef, String valueToVerify) throws Exception;

	void selectListItem(ElementField ef, String targetValue) throws Exception;

	void selectTableRowItem(ElementField elem, String data) throws Exception;

	void setcurrentDate(ElementField el) throws Exception;

	void setText(ElementField ef, String text) throws Exception;

	void takeScreenShot(String methodName);

	void uncheck(ElementField ef) throws Exception;

	void waitForElement(ElementField element, int timeoutInMilliSeconds) throws Exception;

	void validateSavedTestData(ElementField efTable, String text) throws Exception;

	void verifyUIComponent(ElementField ef) throws Exception;

	void quit();

	void acceptAlert();

	void deSelectDropDown(ElementField ef, String targetValue) throws Exception;

	void getDropDownOptionIsSelected(ElementField ef, String targetValue) throws Exception;

	void getDropDownOptionIsPresent(ElementField ef, String targetValue) throws Exception;

	boolean checkCheckBoxChecked(ElementField ef) throws Exception;

	boolean checkCheckBoxUnChecked(ElementField ef) throws Exception;

	boolean waitUntilElementStopMoving (ElementField ef) throws Exception;
	
	void waitForAjaxElement (ElementField ef) throws Exception;
	
	public void pressControlKey() throws Exception;

	void releaseControlKey();
	
	public String getDropDownSelectedValue(ElementField ef) throws Exception;

}
