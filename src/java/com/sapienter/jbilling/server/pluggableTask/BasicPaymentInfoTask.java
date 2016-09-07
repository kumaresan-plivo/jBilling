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

package com.sapienter.jbilling.server.pluggableTask;

import java.text.ParseException;
import java.util.Date;

import com.sapienter.jbilling.common.SessionInternalError;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.metafields.MetaFieldType;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.payment.PaymentInformationBL;
import com.sapienter.jbilling.server.payment.db.PaymentInformationDTO;
import com.sapienter.jbilling.server.payment.db.PaymentMethodDAS;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.util.Constants;

/**
 * This creates payment dto. It now only goes and fetches the credit card
 * of the given user. It doesn't need to initialize the rest of the payment
 * information (amount, etc), only the info for the payment processor,
 * usually cc info but it could be electronic cheque, etc...
 * This task should consider that the user is a partner and is being paid
 * (like a refund) and therefore fetch some other information, as getting
 * paid with a cc seems not to be the norm.
 * @author Emil
 */
public class BasicPaymentInfoTask
        extends PluggableTask implements PaymentInfoTask {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(BasicPaymentInfoTask.class));
    
    /**
     * Gets all the payment instruments of user and after verifying puts them in PaymentDTOEx
     */
    public PaymentDTOEx getPaymentInfo(Integer userId)
            throws TaskException {
        PaymentDTOEx retValue = new PaymentDTOEx();;
        PaymentInformationBL paymentInfoBL = new PaymentInformationBL();
        
        try {
        	UserBL userBL = new UserBL(userId);
        	for(PaymentInformationDTO paymentInformation : userBL.getEntity().getPaymentInstruments()) {
        		LOG.debug("Payment instrument " + paymentInformation.getId());
        		// If its a payment/credit card
        		
        		if(paymentInfoBL.isCreditCard(paymentInformation)) {
        			processCreditCard(retValue, paymentInformation);
        		} else if(paymentInfoBL.isACH(paymentInformation)) {
        			processACH(retValue, paymentInformation);
        		} else if(paymentInfoBL.isCheque(paymentInformation)){
        			processCheque(retValue, paymentInformation);
        		} else {
                    processCustom(retValue, paymentInformation);
                }
        	}
        }catch (Exception e) {
            throw new TaskException(e);
        }
        
        if (retValue.getPaymentInstruments().size() == 0) {
            LOG.debug("Could not find payment instrument for user " + userId);
            return null;
        }
        
        return retValue;
    }

    /**
     * Processes the payment instrument that is a credit card and if its a valid credit card then adds it to payment instruments
     * 
     * @param dto	PaymentDTOEx
     * @param paymentInstrument	PaymentInformationDTO
     * @param piBl	PaymentInformationBL
     * @throws ParseException
     */
    protected void processCreditCard(PaymentDTOEx dto, PaymentInformationDTO paymentInstrument) throws ParseException {
    	PaymentInformationBL piBl = new PaymentInformationBL();
    	// TODO: some fields like gateway key that were being process in old implementation have been removed at all
    	String cardNumber = piBl.getStringMetaFieldByType(paymentInstrument, MetaFieldType.PAYMENT_CARD_NUMBER);
    	Date ccExpiryDate = piBl.getDateMetaFieldByType(paymentInstrument, MetaFieldType.DATE);
        if (cardNumber == null || ccExpiryDate == null) {
            throw new SessionInternalError("Payment Card information not found for customer :" + paymentInstrument.getUser().getId(),
                    new String[] { "PaymentWS,creditCard,validation.payment.card.data.not.found," +paymentInstrument.getUser().getId()});
        }
    	LOG.debug("Expiry date is: " + ccExpiryDate);
    	if(piBl.validateCreditCard(ccExpiryDate, cardNumber)) {
    		LOG.debug("Card is valid");
    		PaymentInformationDTO paymentInformation = paymentInstrument.getDTO();
    		paymentInformation.setPaymentMethod(new PaymentMethodDAS().find(piBl.getPaymentMethod(cardNumber)));
    		
    		dto.getPaymentInstruments().add(paymentInformation);
    	}
    }
    
    /**
     * Process the payment instrument if its ach
     * @param dto	PaymentDTOEx
     * @param paymentInstrument	PaymentInformationDTO
     */
    protected void processACH(PaymentDTOEx dto, PaymentInformationDTO paymentInstrument) {
    	// TODO: some fields like gateway key that were being process in old implementation have been removed at all
    	PaymentInformationDTO paymentInformation = paymentInstrument.getDTO();
		paymentInformation.setPaymentMethod(new PaymentMethodDAS().find(Constants.PAYMENT_METHOD_ACH));
		
		dto.getPaymentInstruments().add(paymentInformation);
    }
    
    /**
     * Process the payment instrument if its neither ach nor credit card
     * 
     * @param dto
     * @param paymentInstrument
     */
    protected void processCheque(PaymentDTOEx dto, PaymentInformationDTO paymentInstrument) {
    	PaymentInformationDTO paymentInformation = paymentInstrument.getDTO();
    	paymentInformation.setPaymentMethod(new PaymentMethodDAS().find(Constants.PAYMENT_METHOD_CHEQUE));
    	
		dto.getPaymentInstruments().add(paymentInformation);
    }

    /**
     * If it's not a credit card, cheque or ach then it's a custom method.
     *
     * @param dto
     * @param paymentInstrument
     */
    protected void processCustom(PaymentDTOEx dto, PaymentInformationDTO paymentInstrument) {
        PaymentInformationDTO paymentInformation = paymentInstrument.getDTO();
        paymentInformation.setPaymentMethod(new PaymentMethodDAS().find(Constants.PAYMENT_METHOD_CUSTOM));

        dto.getPaymentInstruments().add(paymentInformation);
    }
}
