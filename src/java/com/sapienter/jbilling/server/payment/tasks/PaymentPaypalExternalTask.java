/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */
package com.sapienter.jbilling.server.payment.tasks;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.apache.log4j.Logger;

import com.paypal.sdk.exceptions.PayPalException;
import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.metafields.MetaFieldType;
import com.sapienter.jbilling.server.payment.IExternalCreditCardStorage;
import com.sapienter.jbilling.server.payment.PaymentAuthorizationBL;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.payment.PaymentInformationBL;
import com.sapienter.jbilling.server.payment.db.PaymentAuthorizationDTO;
import com.sapienter.jbilling.server.payment.db.PaymentDAS;
import com.sapienter.jbilling.server.payment.db.PaymentDTO;
import com.sapienter.jbilling.server.payment.db.PaymentInformationDAS;
import com.sapienter.jbilling.server.payment.db.PaymentInformationDTO;
import com.sapienter.jbilling.server.payment.db.PaymentMethodDAS;
import com.sapienter.jbilling.server.payment.db.PaymentResultDAS;
import com.sapienter.jbilling.server.payment.tasks.paypal.PaypalApi;
import com.sapienter.jbilling.server.payment.tasks.paypal.dto.CreditCard;
import com.sapienter.jbilling.server.payment.tasks.paypal.dto.Payer;
import com.sapienter.jbilling.server.payment.tasks.paypal.dto.Payment;
import com.sapienter.jbilling.server.payment.tasks.paypal.dto.PaypalResult;
import com.sapienter.jbilling.server.pluggableTask.PaymentTaskWithTimeout;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskException;
import com.sapienter.jbilling.server.user.ContactBL;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.contact.db.ContactDTO;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.payment.tasks.paypal.dto.*;

import org.joda.time.format.DateTimeFormat;

/**
 * Created by Roman Liberov, 03/02/2010
 */
public class PaymentPaypalExternalTask extends PaymentTaskWithTimeout implements IExternalCreditCardStorage {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PaymentPaypalExternalTask.class));

    /* Plugin parameters */
    public static final ParameterDescription PARAMETER_PAYPAL_USER_ID =
    	new ParameterDescription("PaypalUserId", true, ParameterDescription.Type.STR, false);
    public static final ParameterDescription PARAMETER_PAYPAL_PASSWORD =
    	new ParameterDescription("PaypalPassword", true, ParameterDescription.Type.STR, true);
    public static final ParameterDescription PARAMETER_PAYPAL_SIGNATURE =
    	new ParameterDescription("PaypalSignature", true, ParameterDescription.Type.STR);
    public static final ParameterDescription PARAMETER_PAYPAL_ENVIRONMENT =
    	new ParameterDescription("PaypalEnvironment", false, ParameterDescription.Type.STR);
    public static final ParameterDescription PARAMETER_PAYPAL_SUBJECT =
    	new ParameterDescription("PaypalSubject", false, ParameterDescription.Type.STR);

    public String getUserId() throws PluggableTaskException {
        return ensureGetParameter(PARAMETER_PAYPAL_USER_ID.getName());
    }

    public String getPassword() throws PluggableTaskException {
        return ensureGetParameter(PARAMETER_PAYPAL_PASSWORD.getName());
    }

    public String getSignature() throws PluggableTaskException {
        return ensureGetParameter(PARAMETER_PAYPAL_SIGNATURE.getName());
    }

    public String getEnvironment() throws PluggableTaskException {
        return getOptionalParameter(PARAMETER_PAYPAL_ENVIRONMENT.getName(), "Live");
    }

    public String getSubject() {
        return getOptionalParameter(PARAMETER_PAYPAL_SUBJECT.getName(), "");
    }

    //initializer for pluggable params
    {
    	descriptions.add(PARAMETER_PAYPAL_USER_ID);
        descriptions.add(PARAMETER_PAYPAL_PASSWORD);
        descriptions.add(PARAMETER_PAYPAL_SIGNATURE);
        descriptions.add(PARAMETER_PAYPAL_ENVIRONMENT);
        descriptions.add(PARAMETER_PAYPAL_SUBJECT);
    }

    private PaypalApi getApi() throws PluggableTaskException, PayPalException {
        return new PaypalApi(getUserId(), getPassword(),  getSignature(),
                    getEnvironment(), getSubject(), getTimeoutSeconds() * 1000);
    }

    /**
     * Prepares a given payment to be processed using an external storage gateway key instead of
     * the raw credit card number. If the associated credit card has been obscured it will be
     * replaced with the users stored credit card from the database, which contains all the relevant
     * external storage data.
     *
     * New or un-obscured credit cards will be left as is.
     *
     * @param payment payment to prepare for processing from external storage
     */
    public void prepareExternalPayment(PaymentDTOEx payment) {
        if (new PaymentInformationBL().useGatewayKey(payment.getInstrument())) {
            LOG.debug("credit card is obscured, retrieving from database to use external store.");
            if(payment.getInstrument().getId() != null) {
            	// load only if its saved in database. Otherwise do not
            	payment.setInstrument(new PaymentInformationDAS().find(payment.getInstrument().getId()));
            }
        } else {
            LOG.debug("new credit card or previously un-obscured, using as is.");
        }
    }

    /**
     * Updates the gateway key of the credit card associated with this payment. PayPal
     * returns a TRANSACTIONID which can be used to start new transaction without specifying
     * payer info.
     *
     * @param payment successful payment containing the credit card to update.
     *  */
    public void updateGatewayKey(PaymentDTOEx payment) {
        PaymentAuthorizationDTO auth = payment.getAuthorization();
        PaymentInformationBL piBl = new PaymentInformationBL();
        // update the gateway key with the returned PayPal TRANSACTIONID
        PaymentInformationDTO card = payment.getInstrument();
        
        piBl.updateStringMetaField(card, auth.getTransactionId(), MetaFieldType.GATEWAY_KEY);

        // obscure new credit card numbers
        if (!com.sapienter.jbilling.common.Constants.PAYMENT_METHOD_GATEWAY_KEY.equals(card.getPaymentMethod().getId()))
            piBl.obscureCreditCardNumber(card);
    }

    /**
     * Utility method to format the given dollar float value to a two
     * digit number in compliance with the PayPal gateway API.
     *
     * @param amount dollar float value to format
     * @return formatted amount as a string
     */
    private static String formatDollarAmount(BigDecimal amount) {
        amount = amount.abs().setScale(2, RoundingMode.HALF_EVEN); // gateway format, do not change!
        return amount.toPlainString();
    }

    /**
     * Utility method to check if a given {@link PaymentDTOEx} payment can be processed
     * by this task.
     *
     * @param payment payment to check
     * @return true if payment can be processed with this task, false if not
     */
    private static boolean isApplicable(PaymentDTOEx payment) {
    	PaymentInformationBL piBl = new PaymentInformationBL();
        if (piBl.isCreditCard(payment.getInstrument()) && piBl.isACH(payment.getInstrument())) {
            LOG.warn("Can't process without a credit card or ach");
            return false;
        }
        return true;
    }

    /**
     * Returns the name of this payment processor.
     * @return payment processor name
     */
    private String getProcessorName() {
        return "PayPal";
    }

    private static boolean isRefund(PaymentDTOEx payment) {
        return BigDecimal.ZERO.compareTo(payment.getAmount()) > 0 || payment.getIsRefund() != 0;
    }

    private static boolean isCreditCardStored(PaymentDTOEx payment) {
        return new PaymentInformationBL().useGatewayKey(payment.getInstrument());
    }

    private PaymentAuthorizationDTO buildPaymentAuthorization(PaypalResult paypalResult) {
        LOG.debug("Payment authorization result of " + getProcessorName() + " gateway parsing....");

        PaymentAuthorizationDTO paymentAuthDTO = new PaymentAuthorizationDTO();
        paymentAuthDTO.setProcessor(getProcessorName());

        String txID = paypalResult.getTransactionId();
        if (txID != null) {
            paymentAuthDTO.setTransactionId(txID);
            paymentAuthDTO.setCode1(txID);
            LOG.debug("transactionId/code1 [" + txID + "]");
        }

        String errorMsg = paypalResult.getErrorCode();
        if (errorMsg != null) {
            paymentAuthDTO.setResponseMessage(errorMsg);
            LOG.debug("errorMessage [" + errorMsg + "]");
        }

        String avs = paypalResult.getAvs();
        if(avs != null) {
            paymentAuthDTO.setAvs(avs);
            LOG.debug("avs [" + avs + "]");
        }

        return paymentAuthDTO;
    }

    private static String convertCreditCardType(int ccType) {
        switch(ccType) {
            case 2: return CreditCardType.VISA.toString();
            case 3: return CreditCardType.MASTER_CARD.toString();
            case 4: return CreditCardType.AMEX.toString();
            case 6: return CreditCardType.DISCOVER.toString();
        }

        return "";
    }

    private static String convertCreditCardExpiration(Date ccExpiry) {
        if(null == ccExpiry){
            throw new IllegalArgumentException("Can not convert null object!!!");
        }
        return DateTimeFormat.forPattern("MMyyyy").print(ccExpiry.getTime());
    }

    private static CreditCard convertCreditCard(PaymentDTOEx payment) {
    	PaymentInformationBL piBl = new PaymentInformationBL();
        return new CreditCard(
                            convertCreditCardType(payment.getInstrument().getPaymentMethod().getId()),
                            piBl.getStringMetaFieldByType(payment.getInstrument(), MetaFieldType.PAYMENT_CARD_NUMBER),
                            convertCreditCardExpiration(piBl.getDateMetaFieldByType(payment.getInstrument(), MetaFieldType.DATE)),
                            null);
    }

    private static Payer convertPayer(PaymentDTOEx payment) {
        ContactBL contactBL = new ContactBL();
        contactBL.set(payment.getUserId());

        ContactDTO contact = contactBL.getEntity();

        Payer payer = new Payer();
        if (contact != null) {
            if (contact.getEmail() != null) payer.setEmail(contact.getEmail());
            if (contact.getFirstName() != null) payer.setFirstName(contact.getFirstName());
            if (contact.getLastName() != null) payer.setLastName(contact.getLastName());
            if (contact.getAddress1() != null) payer.setStreet(contact.getAddress1());
            if (contact.getCity() != null) payer.setCity(contact.getCity());
            if (contact.getStateProvince() != null) payer.setState(contact.getStateProvince());
            if (contact.getCountryCode() != null) payer.setCountryCode(contact.getCountryCode());
            if (contact.getPostalCode() != null) payer.setZip(contact.getPostalCode());
        }

        return payer;
    }

    private void storePaypalResult(PaypalResult result, PaymentDTOEx payment,
                                   PaymentAuthorizationDTO paymentAuthorization, boolean updateKey) {
        if(result.isSucceseeded()) {
            payment.setPaymentResult(new PaymentResultDAS().find(Constants.RESULT_OK));
            new PaymentAuthorizationBL().create(paymentAuthorization, payment.getId());
            payment.setAuthorization(paymentAuthorization);
            if(updateKey) {
                updateGatewayKey(payment);
            }
        } else {
            payment.setPaymentResult(new PaymentResultDAS().find(Constants.RESULT_FAIL));
        }
    }

    private Result doRefund(PaymentDTOEx payment) throws PluggableTaskException {
    	PaymentInformationBL piBl = new PaymentInformationBL();
        try {
            PaypalApi api = getApi();

            PaypalResult result = api.refundTransaction(
                    piBl.getStringMetaFieldByType(payment.getInstrument(), MetaFieldType.GATEWAY_KEY),
                    formatDollarAmount(payment.getAmount()),
                    RefundType.FULL);

            PaymentAuthorizationDTO paymentAuthorization = buildPaymentAuthorization(result);
            storePaypalResult(result, payment, paymentAuthorization, false);

            return new Result(paymentAuthorization, false);

        } catch (PayPalException e) {
            LOG.error("Couldn't handle payment request due to error", e);
            payment.setPaymentResult(new PaymentResultDAS().find(Constants.RESULT_UNAVAILABLE));
            return NOT_APPLICABLE;
        }
    }

    private Result doPaymentWithStoredCreditCard(PaymentDTOEx payment, PaymentAction paymentAction) throws PluggableTaskException {
    	PaymentInformationBL piBl = new PaymentInformationBL();
        try {
            PaypalResult result = getApi().doReferenceTransaction(
            		piBl.getStringMetaFieldByType(payment.getInstrument(), MetaFieldType.GATEWAY_KEY),
                    paymentAction,
                    new Payment(formatDollarAmount(payment.getAmount()), "USD"));

            PaymentAuthorizationDTO paymentAuthorization = buildPaymentAuthorization(result);
            storePaypalResult(result, payment, paymentAuthorization, true);

            return new Result(paymentAuthorization, false);

        } catch (PayPalException e) {
            LOG.error("Couldn't handle payment request due to error", e);
            payment.setPaymentResult(new PaymentResultDAS().find(Constants.RESULT_UNAVAILABLE));
            return NOT_APPLICABLE;
        }
    }

    private Result doPaymentWithoutStoredCreditCard(PaymentDTOEx payment, PaymentAction paymentAction,
                                                    boolean updateKey) throws PluggableTaskException {
        try {
            PaypalResult result = getApi().doDirectPayment(
                    paymentAction,
                    convertPayer(payment),
                    convertCreditCard(payment),
                    new Payment(formatDollarAmount(payment.getAmount()), "USD"));

            PaymentAuthorizationDTO paymentAuthorization = buildPaymentAuthorization(result);
            storePaypalResult(result, payment, paymentAuthorization, updateKey);

            return new Result(paymentAuthorization, false);

        } catch (PayPalException e) {
            LOG.error("Couldn't handle payment request due to error", e);
            payment.setPaymentResult(new PaymentResultDAS().find(Constants.RESULT_UNAVAILABLE));
            return NOT_APPLICABLE;
        }
    }

    private Result doCapture(PaymentDTOEx payment, PaymentAuthorizationDTO auth) throws PluggableTaskException {
        try {
            PaypalResult result = getApi().doCapture(
                    auth.getTransactionId(),
                    new Payment(formatDollarAmount(payment.getAmount()), "USD"),
                    CompleteType.COMPLETE);

            PaymentAuthorizationDTO paymentAuthorization = buildPaymentAuthorization(result);
            storePaypalResult(result, payment, paymentAuthorization, true);

            return new Result(paymentAuthorization, false);

        } catch (PayPalException e) {
            LOG.error("Couldn't handle payment request due to error", e);
            payment.setPaymentResult(new PaymentResultDAS().find(Constants.RESULT_UNAVAILABLE));
            return NOT_APPLICABLE;
        }
    }

    private boolean doProcess(PaymentDTOEx payment, PaymentAction paymentAction, boolean updateKey)
            throws PluggableTaskException {

        if(isRefund(payment)) {
            return doRefund(payment).shouldCallOtherProcessors();
        }

        if(isCreditCardStored(payment)) {
            return doPaymentWithStoredCreditCard(payment, paymentAction)
                    .shouldCallOtherProcessors();
        }

        return doPaymentWithoutStoredCreditCard(payment, paymentAction, updateKey)
                .shouldCallOtherProcessors();
    }

    private void doVoid(PaymentDTOEx payment) throws PluggableTaskException {
        try {
            getApi().doVoid(payment.getAuthorization().getTransactionId());
        } catch (PayPalException e) {
            LOG.error("Couldn't void payment authorization due to error", e);
            throw new PluggableTaskException(e);
        }
    }

    public boolean process(PaymentDTOEx payment) throws PluggableTaskException {
        LOG.debug("Payment processing for " + getProcessorName() + " gateway");

        if (payment.getPayoutId() != null) {
            return true;
        }

        if(!isApplicable(payment)) {
            return NOT_APPLICABLE.shouldCallOtherProcessors();
        }

        prepareExternalPayment(payment);
        return doProcess(payment, PaymentAction.SALE, true /* updateKey */);
    }

    public void failure(Integer userId, Integer retry) {
        // do nothing
    }

    public boolean preAuth(PaymentDTOEx payment) throws PluggableTaskException {
        LOG.debug("Pre-authorization processing for " + getProcessorName() + " gateway");
        prepareExternalPayment(payment);
        return doProcess(payment, PaymentAction.AUTHORIZATION, true /* updateKey */);
    }

    public boolean confirmPreAuth(PaymentAuthorizationDTO auth, PaymentDTOEx payment)
            throws PluggableTaskException {

        LOG.debug("Confirming pre-authorization for " + getProcessorName() + " gateway");
        if (!getProcessorName().equals(auth.getProcessor())) {
            /*  let the processor be called and fail, so the caller can do something
                about it: probably re-call this payment task as a new "process()" run */
            LOG.warn("The processor of the pre-auth is not " + getProcessorName() + ", is " + auth.getProcessor());
        }

        PaymentInformationDTO card = payment.getInstrument();
        if (card == null) {
            throw new PluggableTaskException("Credit card is required, capturing payment: " + payment.getId());
        }

        if (!isApplicable(payment)) {
            LOG.error("This payment can not be captured: " + payment);
            return NOT_APPLICABLE.shouldCallOtherProcessors();
        }

        // process
        prepareExternalPayment(payment);
        return doCapture(payment, auth).shouldCallOtherProcessors();
    }

    public String storeCreditCard(ContactDTO contact, PaymentInformationDTO creditCard) {
        LOG.debug("Storing creadit card info within " + getProcessorName() + " gateway");
        UserDTO user;
        if (contact != null) {
            UserBL bl = new UserBL(contact.getUserId());
            user = bl.getEntity();
            // can not get credit card from db as there may be many
            creditCard = creditCard;
        } else if (creditCard != null && creditCard.getUser() != null) {
            user = creditCard.getUser();
        } else {
            LOG.error("Could not determine user id for external credit card storage");
            return null;
        }

        // new contact that has not had a credit card created yet
        if (creditCard == null) {
            LOG.warn("No credit card to store externally.");
            return null;
        }

        /*  Note, don't use PaymentBL.findPaymentInstrument() as the given creditCard is still being
            processed at the time that this event is being handled, and will not be found.

            PaymentBL()#create() will cause a stack overflow as it will attempt to update the credit card,
            emitting another NewCreditCardEvent which is then handled by this method and repeated.
         */
        PaymentDTO payment = new PaymentDTO();
        payment.setBaseUser(user);
        payment.setCurrency(user.getCurrency());
        payment.setAmount(CommonConstants.BIGDECIMAL_ONE_CENT);
        payment.setCreditCard(creditCard);
        payment.setPaymentMethod(new PaymentMethodDAS().find(Util.getPaymentMethod(new PaymentInformationBL().getStringMetaFieldByType(creditCard, MetaFieldType.PAYMENT_CARD_NUMBER))));
        payment.setIsRefund(0);
        payment.setIsPreauth(1);
        payment.setDeleted(1);
        payment.setAttempt(1);
        payment.setPaymentDate(new Date());
        payment.setCreateDatetime(new Date());

        PaymentDTOEx paymentEx = new PaymentDTOEx(new PaymentDAS().save(payment));
        try {
            doProcess(paymentEx, PaymentAction.AUTHORIZATION, false /* updateKey */);
            doVoid(paymentEx);

            PaymentAuthorizationDTO auth = paymentEx.getAuthorization();
            return auth.getTransactionId();
        } catch (PluggableTaskException e) {
            LOG.error("Could not process external storage payment", e);
            return null;
        }
    }

    /**
     *
     */
    public String deleteCreditCard(ContactDTO contact, PaymentInformationDTO instrument) {
        //noop
        return null;
    }
}
