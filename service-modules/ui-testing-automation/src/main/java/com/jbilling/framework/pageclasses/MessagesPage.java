package com.jbilling.framework.pageclasses;

import org.testng.Assert;

import com.jbilling.framework.globals.GlobalController;
import com.jbilling.framework.globals.GlobalEnumerations.TextComparators;
import com.jbilling.framework.globals.Logger;
import com.jbilling.framework.interfaces.ElementField;
import com.jbilling.framework.interfaces.LocateBy;
import com.jbilling.framework.pageclasses.GlobalEnumsPage.PageSuccessMessages;
import com.jbilling.framework.utilities.textutilities.TextUtilities;

public class MessagesPage {
	// Initialize private logger object
	private static Logger logger = new Logger().getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@LocateBy(xpath = "//div[@id='messages']/div/p")
	private static ElementField SPAN_OPERATION_MESSAGE;

	@LocateBy(xpath = "//div[@class = 'msg-box successfully']")
	private static ElementField INTERMEDIATE_SUCCESS_MESSAGE;

	@LocateBy(xpath = "//div[@id='messages']/div[@class='msg-box error']/strong")
	private static ElementField STRONG_ERROR_MESSAGE;

    @LocateBy(xpath = "//div[@id='messages']/div[@class='msg-box error']/ul")
    private static ElementField ERROR_MESSAGES_LIST;

    @LocateBy(xpath = "(//div[@id='messages']/div[@class='msg-box error']/ul/li)[1]")
    private static ElementField ERROR_MESSAGES_LIST_FIRST_MESSAGE;
    
	public static String isOperationSuccessfulOnMessage(PageSuccessMessages messageToVerify, String additionalMessage,
			TextComparators comparator) throws Exception {
		return MessagesPage.isOperationSuccessfulOnMessage(messageToVerify.GetValue(), additionalMessage, comparator);
	}

	/**
	 * Compares in messages element for both messages to verify in one go with
	 * AND operation.
	 * 
	 * 
	 * @param messageToVerify
	 *            First message to verify
	 * @param additionalMessage
	 *            Second message to verify
	 * @param comparator
	 * @return NULL if result is true <br>
	 *         Message String if result is false
	 * @throws Exception
	 */
	public static String isOperationSuccessfulOnMessage(String messageToVerify, String additionalMessage, TextComparators comparator)
			throws Exception {
		String msg = MessagesPage.isOperationSuccessfulOnMessage(messageToVerify, comparator);
		String msg2 = MessagesPage.isOperationSuccessfulOnMessage(additionalMessage, comparator);

		if (msg == null) {
			if (msg2 == null) {
				return null;
			}
			return msg2;
		}

		return msg;
	}

	public static String isOperationSuccessfulOnMessage(PageSuccessMessages messageToVerify, TextComparators comparator) throws Exception {
		return MessagesPage.isOperationSuccessfulOnMessage(messageToVerify.GetValue(), comparator);
	}

	public static String isOperationSuccessfulOnMessage(String messageToVerify, TextComparators comparator) throws Exception {
		String msg = GlobalController.brw.getText(MessagesPage.SPAN_OPERATION_MESSAGE);
		MessagesPage.logger.info("Message on UI:" + msg);

		msg = TextUtilities.nullToBlank(msg, true);
		boolean result = TextUtilities.compareValue(messageToVerify, msg, true, comparator);

		return (result ? null : msg);
	}

	public static boolean isIntermediateSuccessMessageAppeared() throws Exception {
		return GlobalController.brw.isElementPresent(MessagesPage.INTERMEDIATE_SUCCESS_MESSAGE);
	}

	public static boolean isErrorMessageAppeared() throws Exception {
		return GlobalController.brw.isElementPresent(MessagesPage.STRONG_ERROR_MESSAGE);
	}

    public static boolean isErrorMessagesListAppeared() throws Exception {
        return GlobalController.brw.isElementPresent(MessagesPage.ERROR_MESSAGES_LIST);
    }

	public MessagesPage verifyDisplayedMessageText(String messageToVerify, String additionalMessage, TextComparators comparator)
			throws Exception {

		String rsltMsg = MessagesPage.isOperationSuccessfulOnMessage(messageToVerify, additionalMessage, TextComparators.contains);
		if (rsltMsg != null) {
			throw new Exception("Test Case failed: " + rsltMsg);
		}
		return GlobalController.brw.initElements(MessagesPage.class);

	}

    public static void assertTextInFirstErrorMessage (String expectedText) throws Exception {
        String actualText = GlobalController.brw.getText(MessagesPage.ERROR_MESSAGES_LIST_FIRST_MESSAGE);
        Assert.assertEquals(actualText, expectedText);
    }
}
