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
package com.sapienter.jbilling.server.payment.blacklist;

import java.util.Collection;
import java.util.ResourceBundle;
import java.util.List;

import com.sapienter.jbilling.server.metafields.MetaFieldType;
import com.sapienter.jbilling.server.payment.PaymentDTOEx;
import com.sapienter.jbilling.server.payment.PaymentInformationBL;
import com.sapienter.jbilling.server.payment.blacklist.db.BlacklistDAS;
import com.sapienter.jbilling.server.payment.blacklist.db.BlacklistDTO;
import com.sapienter.jbilling.server.payment.db.PaymentInformationDTO;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.Util;

import java.util.ArrayList;

/**
 * Filters credit card numbers.
 */
public class CreditCardFilter implements BlacklistFilter {

    public Result checkPayment(PaymentDTOEx paymentInfo) {
    	PaymentInformationBL piBl = new PaymentInformationBL();
        if (paymentInfo.getInstrument() != null && piBl.isCreditCard(paymentInfo.getInstrument())) {
            List<String> creditCards = new ArrayList<String>(1);
            // DB compares encrypted data
            creditCards.add(piBl.getStringMetaFieldByType(paymentInfo.getInstrument(), MetaFieldType.PAYMENT_CARD_NUMBER));

            return checkCreditCard(paymentInfo.getUserId(), 
                    creditCards);
        }
        // not paying by credit card, so accept?
        return new Result(false, null);
    }

    public Result checkUser(Integer userId) {
        UserDTO user = new UserDAS().find(userId);
        return checkCreditCard(userId, getCreditCardNumbers(user.getPaymentInstruments()));
    }

    public Result checkCreditCard(Integer userId, Collection<String> creditCards) {
        if (null == creditCards || creditCards.isEmpty()) {
            return new Result(false, null);
        }

        // create a list of credit card numbers
        List<String> ccNumbers = new ArrayList<String>(creditCards.size());
        for (String cc : creditCards) {
            // it needs the encrypted numbers because it will use a query with them later
            ccNumbers.add(cc);
        }
        
        Integer entityId = new UserBL().getEntityId(userId);
        List<BlacklistDTO> blacklist = new BlacklistDAS().filterByCcNumbers(
                entityId, ccNumbers);

        if (!blacklist.isEmpty()) {
            ResourceBundle bundle = Util.getEntityNotificationsBundle(userId);
            return new Result(true, 
                    bundle.getString("payment.blacklist.cc_number_filter"));
        }

        return new Result(false, null);
    }

    public String getName() {
        return "Credit card number blacklist filter";
    }
    
    /**
     * Receives a list of payment instruments and returns list of credit card numbers
     * 
     * @param instruments	list of payment instruments
     * @return	list of credit card numbers
     */
    private List<String> getCreditCardNumbers(List<PaymentInformationDTO> instruments) {
    	if(instruments == null || instruments.isEmpty()) {
    		return null;
    	}
    	
    	PaymentInformationBL piBl = new PaymentInformationBL();
    	List<String> creditCardNumbers = new ArrayList<String>();
    	for(PaymentInformationDTO instrument : instruments) {
    		String cardNumber = piBl.getStringMetaFieldByType(instrument, MetaFieldType.PAYMENT_CARD_NUMBER);
    		if(cardNumber != null && !cardNumber.isEmpty()) {
    			creditCardNumbers.add(cardNumber);
    		}
    	}
    	
    	return creditCardNumbers;
    }
}
