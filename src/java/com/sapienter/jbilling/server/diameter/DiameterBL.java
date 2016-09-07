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

package com.sapienter.jbilling.server.diameter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import com.sapienter.jbilling.client.util.Constants;
import com.sapienter.jbilling.server.mediation.CallDataRecord;
import com.sapienter.jbilling.server.mediation.IMediationSessionBean;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.mediation.JbillingMediationRecord;
import com.sapienter.jbilling.server.mediation.MediationService;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.pricing.strategy.RateCardPricingStrategy;
import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.diameter.db.ChargeSessionDAS;
import com.sapienter.jbilling.server.diameter.db.ChargeSessionDTO;
import com.sapienter.jbilling.server.diameter.db.ReservedAmountDAS;
import com.sapienter.jbilling.server.diameter.db.ReservedAmountDTO;
import com.sapienter.jbilling.server.diameter.event.ReservationCreatedEvent;
import com.sapienter.jbilling.server.diameter.event.ReservationReleasedEvent;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.OrderLineBL;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.pricing.cache.FreePriceBigDecimal;
import com.sapienter.jbilling.server.pricing.cache.UnavailablePriceBigDecimal;
import com.sapienter.jbilling.server.system.event.EventManager;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.PreferenceBL;

public class DiameterBL {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(DiameterBL.class));

    public static final String ATTRIBUTE_DESTINATION_REALM = "Destination-Realm";
    private static final String ATTRIBUTE_CALLED_URI = "Called-Party-Address";
    private static final String ATTRIBUTE_CALLED_NUMBER = "Called-Party-Number";
    private static final String CURRENCY_CODE_FIELD = "REQ_CURRENCY_CODE";

    private static final String INVOCATION_CONTEXT = "REQ_INVOCATION_CONTEXT";

    private DiameterUserLocator userLocator;
    private DiameterItemLocator itemLocator;

    private Integer entityId;

    private String diameterRealm;
    private int quotaThreshold = 0;
    private int sessionExpiration = 0;

    public DiameterBL (DiameterUserLocator userLocator, DiameterItemLocator itemLocator,
                       Integer entityId) {

        this.userLocator = userLocator;
        this.itemLocator = itemLocator;
        this.entityId = entityId;

        PreferenceBL preferenceRealm = new PreferenceBL(this.entityId,
                CommonConstants.PREFERENCE_DIAMETER_DESTINATION_REALM);
        diameterRealm = preferenceRealm.getString();

        PreferenceBL quotaPref = new PreferenceBL(this.entityId,
                CommonConstants.PREFERENCE_DIAMETER_QUOTA_THRESHOLD);
        quotaThreshold = quotaPref.isNull() ? 0 : quotaPref.getInt();

        PreferenceBL expirationPref = new PreferenceBL(this.entityId,
                CommonConstants.PREFERENCE_DIAMETER_SESSION_GRACE_PERIOD_SECONDS);
        sessionExpiration = expirationPref.isNull() ? 0 : expirationPref.getInt();
    }

    public DiameterResultWS createSession (String sessionId, Date timestamp, BigDecimal units,
                                           List<PricingField> pricingFields) {
        return createSession(sessionId, timestamp, units, pricingFields, true);
    }

    private DiameterResultWS createSession (String sessionId, Date timestamp, BigDecimal units,
                                            List<PricingField> priceFields, boolean binarySearch) {

        try {
            LOG.debug("Parameters received: sessionId - %s, timestamp - %s, units - %s, binarySearch - %s", sessionId, timestamp, units, binarySearch);
            for(PricingField pricingField : priceFields) {
                LOG.debug("PricingField: %s - Value: %s", pricingField.getName(), pricingField.getStrValue());
            }

            PricingFieldsHelper fieldsHelper = new PricingFieldsHelper(priceFields);
            String realm = (String) fieldsHelper.getValue(ATTRIBUTE_DESTINATION_REALM);
            fieldsHelper.add(INVOCATION_CONTEXT, "createSession");
            fieldsHelper.addIfNotBlank(ATTRIBUTE_CALLED_NUMBER, SipNumberExtractor.extract(
                    (String) fieldsHelper.getValue(ATTRIBUTE_CALLED_URI)));

            // Validate that the realm passed as parameter to the function equals the realm 
            // configured as preference into jBilling. If not, return error code 3003.
            if (realm == null || !realm.equals(diameterRealm)) {
                LOG.error("Received a request for unknown realm [%s], expected [%s]",
                        realm, diameterRealm);
                return new DiameterResultWS(DiameterResultWS.DIAMETER_REALM_NOT_SERVED);
            }

            ChargeSessionDAS chargeSessionDAS = new ChargeSessionDAS();

            // Validate that no session with the session token passed as parameter exists.
            // If there is already a session with that ID in the database, return error code 5012.
            if (chargeSessionDAS.findByToken(sessionId) != null) {
                LOG.error("Received a createSession call for a session that already exists [%s]",
                        sessionId);
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
            }

            // Instantiate DiameterUserLocatorByUserName and call its findUserFromParameters()
            // function. If return value is null, return error code 5030.
            DiameterUserLocator userLocator = getDiameterUserLocator();

            Integer userId = userLocator.findUserFromParameters(entityId, fieldsHelper.getFields());

            if (userId == null) {
                LOG.error("Unable to determine user from input data");
                return new DiameterResultWS(DiameterResultWS.DIAMETER_USER_UNKNOWN);
            }

            LOG.debug("User id found in input data: %s", userId);

            UserDTO user = new UserDAS().find(userId);
            if (user.getCurrency() != null) {
                fieldsHelper.add(CURRENCY_CODE_FIELD, user.getCurrency().getCode());
                LOG.debug("User's currency to be sent in the Pricing Fields: %s", fieldsHelper.getField(CURRENCY_CODE_FIELD).getStrValue());
            }

            // find the item
            ItemDTO item = getDiameterItemLocator().findItem(fieldsHelper.getFields(), this.entityId);

            if (item == null) {
                LOG.error("Unable to determine item for charge");
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
            }

            LOG.debug("Item determined for charge: %s", item.getId());

            // Instantiate the simple implementation of DiameterPriceLocator and call its method.
            DiameterPriceLocator priceLocator = getDiameterPriceLocator(entityId, user);

            // Calculate grantedUnits.
            BinarySearchUnits bsu = new BinarySearchUnits(entityId, user, priceLocator);
            BigDecimal grantedTime = bsu.calculateUnits(item.getId(), units, fieldsHelper);

            if (bsu.getRatedUnitPrice() instanceof FreePriceBigDecimal) {
                LOG.info("Price rating resolved to a \"free\" service, credit control not applicable");
                return new DiameterResultWS(DiameterResultWS.DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE);
            } else if (bsu.getRatedUnitPrice() instanceof UnavailablePriceBigDecimal) {
                LOG.info("Price rating resolved to \"n/a\" service, denying service request");
                return new DiameterResultWS(DiameterResultWS.DIAMETER_END_USER_SERVICE_DENIED);
            }

            // Validate if rated amount is lower than user balance
            if (BigDecimal.ZERO.compareTo(grantedTime) == 0) {
                LOG.info("User [%d] has insufficient funds to cover requested units [%s], " +
                        "credit limit reached", user.getId(), units.toPlainString());
                return new DiameterResultWS(DiameterResultWS.DIAMETER_CREDIT_LIMIT_REACHED);
            }

            LOG.debug("Granted time to the user: %s", grantedTime);

            ChargeSessionDTO chargeSessionDTO = new ChargeSessionDTO(user, sessionId);
            chargeSessionDTO.setStarted(timestamp);
            chargeSessionDTO.setLastAccessed(timestamp);

            // Create a new ChargeSessionDTO for the user and persist it in the database.
            chargeSessionDTO = chargeSessionDAS.save(chargeSessionDTO);

            // Create a new ReservedAmountDTO object with the session as parameter and 
            // the returned rated price as amount and persist it to database.
            ReservedAmountDTO reservedAmountDTO = new ReservedAmountDTO(
                    chargeSessionDTO, user.getCurrency(), bsu.getRatedTotalPrice(),
                    units, item, fieldsHelper.getFieldsAsArray());
            reservedAmountDTO = new ReservedAmountDAS().save(reservedAmountDTO);
            //Fire Event
            ReservationCreatedEvent event = new ReservationCreatedEvent(entityId, reservedAmountDTO);
            EventManager.process(event);

            boolean terminateWhenConsumed = grantedTime.compareTo(units) < 0 ? true : false;

            // Return a 2001 record passing as quotaThreshold the value of the preference 
            // created earlier.

            return new DiameterResultWS(DiameterResultWS.DIAMETER_SUCCESS, grantedTime,
                    terminateWhenConsumed, quotaThreshold);

        } catch (PriceNotFoundException ex){
            return new DiameterResultWS(DiameterResultWS.DIAMETER_INVALID_AVP_VALUE);
        } catch (Throwable ex) {
            LOG.error("Unhandled error while processing Diameter createSession request", ex);
            return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
        }
    }

    public DiameterResultWS updateSession (String sessionId, Date timestamp, BigDecimal usedUnits,
                                           BigDecimal reqUnits, List<PricingField> priceFields)
            throws SessionInternalError {
        try {
            LOG.debug("Parameters received: sessionId - %s, timestamp - %s, usedUnits - %s, reqUnits - %s", sessionId, timestamp, usedUnits, reqUnits);
            for(PricingField pricingField : priceFields) {
                LOG.debug("PricingField: %s - Value: %s", pricingField.getName(), pricingField.getStrValue());
            }

            PricingFieldsHelper fieldsHelper = new PricingFieldsHelper(priceFields);
            fieldsHelper.add(INVOCATION_CONTEXT, "updateSession");
            fieldsHelper.addIfNotBlank(ATTRIBUTE_CALLED_NUMBER, SipNumberExtractor.extract(
                    (String) fieldsHelper.getValue(ATTRIBUTE_CALLED_URI)));

            ChargeSessionDAS chargeSessionDAS = new ChargeSessionDAS();
            ChargeSessionDTO chargeSession = chargeSessionDAS.findByToken(sessionId);

            // Validate that a session with the session ID passed as parameter exists.
            // If there is no session with the ID in the database, return error code 5012.
            if (!checkSessionValid(chargeSession)) {
                LOG.error("Session with id=[%s] not found", sessionId);
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNKNOWN_SESSION_ID);
            }

            // Validate that the session has a ReservedAmountDTO associated.
            if (chargeSession.getReservations() == null || chargeSession.getReservations().size() == 0) {
                LOG.error("No reservation for the given session");
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
            }

            // find the item
            ItemDTO reqItem = getDiameterItemLocator().findItem(fieldsHelper.getFields(), this.entityId);

            if (reqItem == null) {
                LOG.error("Unable to determine item for charge");
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
            }

            UserDTO user = chargeSession.getBaseUser();

            PreferenceBL preferenceQuotaThreshold = new PreferenceBL(entityId,
                    CommonConstants.PREFERENCE_DIAMETER_QUOTA_THRESHOLD);

            if (usedUnits.compareTo(BigDecimal.ZERO) < 0) {
                LOG.error("Invalid usedUnits amount.");
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
            }

            //SETTLES BALANCE
            ReservedAmountDTO reservedAmount = chargeSession.findReservationByItem(reqItem);

            if (reservedAmount != null) {
                // Validate usedUnits is less than or equal to the reserved quantity
                if (usedUnits.compareTo(reservedAmount.getQuantity()) > 0) {
                    LOG.error("Used units is greater than the reserved units.");
                    return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
                }

                if (usedUnits.compareTo(BigDecimal.ZERO) > 0) {
                    //Update currentOrder
                    settleReservation(chargeSession, reqItem.getId(), usedUnits, user, entityId, fieldsHelper, false);
                }
            }

            if (reqUnits.compareTo(BigDecimal.ZERO) > 0) {
                //RESERVE BALANCE
                // Calculate grantedUnits.
                DiameterPriceLocator priceLocator = getDiameterPriceLocator(entityId, user);
                BinarySearchUnits bsu = new BinarySearchUnits(entityId, user, priceLocator);
                BigDecimal grantedTime = bsu.calculateUnits(reqItem.getId(), reqUnits, fieldsHelper);

                // Validate if rated amount is lower than user balance
                if (BigDecimal.ZERO.compareTo(grantedTime) == 0) {
                    LOG.info("User [%d] has insufficient funds to cover requested units [%s], " +
                            "credit limit reached", user, reqUnits.toPlainString());
                    return new DiameterResultWS(DiameterResultWS.DIAMETER_CREDIT_LIMIT_REACHED);
                }

                // Create a new ReservedAmountDTO object with the session as parameter
                // and the returned rated price as amount and persist it to database.
                ReservedAmountDTO reservedAmountDTO = new ReservedAmountDTO(chargeSession,
                        user.getCurrency(), bsu.getRatedTotalPrice(), reqUnits,
                        reqItem, fieldsHelper.getFieldsAsArray());
                reservedAmountDTO = new ReservedAmountDAS().save(reservedAmountDTO);
                //Fire Event
                ReservationCreatedEvent event = new ReservationCreatedEvent(entityId, reservedAmountDTO);
                EventManager.process(event);

                boolean terminateWhenConsumed = grantedTime.compareTo(reqUnits) < 0 ? true : false;

                //Return a 2001 record passing as quotaThreshold the value of the
                // preference created earlier.
                return new DiameterResultWS(DiameterResultWS.DIAMETER_SUCCESS, grantedTime,
                        terminateWhenConsumed, preferenceQuotaThreshold.getInt().intValue());
            } else if (BigDecimal.ZERO.compareTo(reqUnits) == 0) {

                if (reservedAmount == null) {
                    LOG.error("No reservation found for the given item.");
                    return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
                }

                //Remove the ReservedAmountDTO in the session.
                chargeSession.getReservations().remove(reservedAmount);
                //Fire Event
                ReservationReleasedEvent event = new ReservationReleasedEvent(entityId, reservedAmount);
                EventManager.process(event);
                new ReservedAmountDAS().delete(reservedAmount);
                chargeSession.setLastAccessed(timestamp);
                chargeSession = new ChargeSessionDAS().save(chargeSession);

                return new DiameterResultWS(DiameterResultWS.DIAMETER_SUCCESS, BigDecimal.ZERO,
                        false, preferenceQuotaThreshold.getInt().intValue());
            } else {
                LOG.error("less than zero requested units.");
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
            }

        } catch (PriceNotFoundException ex){
            return new DiameterResultWS(DiameterResultWS.DIAMETER_INVALID_AVP_VALUE);
        } catch (Exception ex) {
            // Any unexpected exception will create a 5012 error return.
            return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
        }
    }

    public DiameterResultWS extendSession (String sessionId, Date timestamp,
                                           BigDecimal usedUnits, BigDecimal reqUnits) throws SessionInternalError {
        try {
            LOG.debug("Parameters received: sessionId - %s, timestamp - %s, usedUnits - %s, reqUnits - %s", sessionId, timestamp, usedUnits, reqUnits);

            ChargeSessionDAS chargeSessionDAS = new ChargeSessionDAS();

            ChargeSessionDTO chargeSession = chargeSessionDAS.findByToken(sessionId);

            // Validate that a session with the session ID passed as parameter exists. 
            // If there is no session with the ID in the database, return error code 5012.
            if (!checkSessionValid(chargeSession)) {
                LOG.error("Session with id=[%s] not found", sessionId);
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNKNOWN_SESSION_ID);
            }

            // Validate that the session has a ReservedAmountDTO associated.
            if (chargeSession.getReservations() == null || chargeSession.getReservations().size() != 1) {
                LOG.error("No reservation for the given session");
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
            }
            ReservedAmountDTO reservedAmount = chargeSession.getReservations().iterator().next();

            // Validate usedUnits is less than or equal to the reserved quantity
            if (usedUnits.compareTo(reservedAmount.getQuantity()) > 0) {
                LOG.error("Used units is greater than the reserved units");
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
            }

            UserDTO user = chargeSession.getBaseUser();
            ItemDTO item = reservedAmount.getItem();

            PricingFieldsHelper fieldsHelper = new PricingFieldsHelper(reservedAmount.getDataAsFields());

            //Update currentOrder
            settleReservation(chargeSession, item.getId(), usedUnits, user, entityId, fieldsHelper, false);

            //Remove the ReservedAmountDTO in the session.
            chargeSession.getReservations().clear();
            chargeSession.setLastAccessed(timestamp);
            //Fire Event
            ReservationReleasedEvent event = new ReservationReleasedEvent(entityId, reservedAmount);
            EventManager.process(event);
            new ReservedAmountDAS().delete(reservedAmount);
            new ChargeSessionDAS().save(chargeSession);

            DiameterPriceLocator priceLocator = getDiameterPriceLocator(entityId, user);
            BinarySearchUnits bsu = new BinarySearchUnits(entityId, user, priceLocator);
            BigDecimal grantedTime = bsu.calculateUnits(item.getId(), reqUnits, fieldsHelper);

            // Validate if rated amount is lower than user balance
            if (BigDecimal.ZERO.compareTo(grantedTime) == 0) {
                LOG.info("User [%d] has insufficient funds to cover requested units [%s], " +
                        "credit limit reached", user.getId(), reqUnits.toPlainString());
                return new DiameterResultWS(DiameterResultWS.DIAMETER_CREDIT_LIMIT_REACHED);
            }

            // Create a new ReservedAmountDTO object with the session as parameter 
            // and the returned rated price as amount and persist it to database.
            ReservedAmountDTO reservedAmountDTO = new ReservedAmountDTO(chargeSession,
                    user.getCurrency(), bsu.getRatedTotalPrice(), reqUnits, item, fieldsHelper.getFieldsAsArray());
            new ReservedAmountDAS().save(reservedAmountDTO);

            //Fire Event
            ReservationCreatedEvent event2 = new ReservationCreatedEvent(entityId, reservedAmount);
            EventManager.process(event2);

            PreferenceBL preferenceQuotaThreshold = new PreferenceBL(entityId,
                    CommonConstants.PREFERENCE_DIAMETER_QUOTA_THRESHOLD);

            boolean terminateWhenConsumed = grantedTime.compareTo(reqUnits) < 0 ? true : false;

            //Return a 2001 record passing as quotaThreshold the value of the 
            // preference created earlier.
            return new DiameterResultWS(DiameterResultWS.DIAMETER_SUCCESS, grantedTime,
                    terminateWhenConsumed, preferenceQuotaThreshold.getInt().intValue());

        } catch (PriceNotFoundException ex){
            return new DiameterResultWS(DiameterResultWS.DIAMETER_INVALID_AVP_VALUE);
        } catch (Exception ex) {
            // Any unexpected exception will create a 5012 error return.
            return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
        }
    }

    public DiameterResultWS
    endSession (String sessionId, Date timestamp, BigDecimal usedUnits,
                                        int causeCode) throws SessionInternalError {
        try {
            LOG.debug("Parameters received: sessionId - %s, timestamp - %s, usedUnits - %s, causeCode - %s", sessionId, timestamp, usedUnits, causeCode);

            ChargeSessionDAS chargeSessionDAS = new ChargeSessionDAS();

            ChargeSessionDTO chargeSession = chargeSessionDAS.findByToken(sessionId);

            if (!checkSessionValid(chargeSession)) {
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
            }

            // Validate that the session has a ReservedAmountDTO associated.
            if (chargeSession.getReservations() == null || chargeSession.getReservations().size() != 1) {
                if (chargeSession.getReservations() == null) {
                    LOG.error("chargeSession.getReservations() == null");
                } else {
                    LOG.error("chargeSession.getReservations().size() = " + chargeSession.getReservations().size());
                }
                LOG.error("Unexpected number of reservations for the given session");
                return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
            }

            ReservedAmountDTO reservedAmount = chargeSession.getReservations().iterator().next();

            UserDTO user = chargeSession.getBaseUser();
            ItemDTO item = reservedAmount.getItem();

            PricingFieldsHelper fieldsHelper = new PricingFieldsHelper(reservedAmount.getDataAsFields());

            //Update currentOrder
	        SettleReservationResult result = settleReservation(
			        chargeSession, item.getId(), usedUnits, user, entityId, fieldsHelper, true);

            //Save mediation record
            saveMediationRecord(sessionId, user, item.getId(), Arrays.asList(reservedAmount.getDataAsFields()), result);

            //Remove the ReservedAmountDTO in the session.
            chargeSession.getReservations().clear();
            //Fire Event
            ReservationReleasedEvent event = new ReservationReleasedEvent(entityId, reservedAmount);
            EventManager.process(event);
            new ReservedAmountDAS().delete(reservedAmount);

            chargeSessionDAS.delete(chargeSession);
            return new DiameterResultWS(DiameterResultWS.DIAMETER_SUCCESS);
        } catch (Exception ex) {
            // Any unexpected exception will create a 5012 error return.
            return new DiameterResultWS(DiameterResultWS.DIAMETER_UNABLE_TO_COMPLY);
        }
    }

    private boolean checkSessionValid (ChargeSessionDTO chargeSession) {
        boolean result = true;
        if (chargeSession == null) {
            result = false;
        } else if (sessionExpiration > 0) {
            GregorianCalendar cutoff = new GregorianCalendar();
            cutoff.setTime(chargeSession.getLastAccessed());
            cutoff.add(GregorianCalendar.SECOND, sessionExpiration);
            result = cutoff.after(new GregorianCalendar());
        }
        return result;
    }

    public DiameterResultWS reserveUnits (String sessionId, Date timestamp, int units,
                                          List<PricingField> pricingFields) throws SessionInternalError {
        return createSession(sessionId, timestamp, BigDecimal.valueOf(units), pricingFields, false);
    }

    public DiameterResultWS consumeReservedUnits (String sessionId, Date timestamp,
                                                  int usedUnits, int causeCode) throws SessionInternalError {
        return endSession(sessionId, timestamp, BigDecimal.valueOf(usedUnits), causeCode);
    }

    private DiameterUserLocator getDiameterUserLocator () {
        return userLocator;
    }

    private DiameterPriceLocator getDiameterPriceLocator (Integer entityId, UserDTO user) {
        return new DefaultPriceLocator(entityId, user);
    }

    private DiameterItemLocator getDiameterItemLocator () {
        return itemLocator;
    }
    
    private CdrKeyResolver getCdrKeyResolver (){
        return new BasicCdrKeyResolver();
    }

    private SettleReservationResult settleReservation(ChargeSessionDTO chargeSession, Integer itemId, BigDecimal quantity, UserDTO user, Integer entityId,
                                   PricingFieldsHelper fieldsHelper, boolean roundQuantity) {

        //BigDecimal.divide throws ArithmeticException (Bugs #4660)
        BigDecimal start = chargeSession.getCarriedUnits().setScale(0, RoundingMode.CEILING);
        chargeSession.setCarriedUnits(chargeSession.getCarriedUnits().add(quantity));
        LOG.debug("Carried Units: %s", chargeSession.getCarriedUnits());

        // We should only round the quantity when we end the session. The rounding should only add the units that are needed to round
        // the total quantity used to the next available minute.
        if (roundQuantity) {
            BigDecimal diff = chargeSession.getCarriedUnits().setScale(0, RoundingMode.CEILING).subtract(chargeSession.getCarriedUnits());
            LOG.debug("diff to add to the quantity to round to the next minute: %s. Carried Units (%s) + diff (%s) = %s",
                    diff, chargeSession.getCarriedUnits(), diff, chargeSession.getCarriedUnits().add(diff));
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                quantity = quantity.add(diff);
            }
        }

        OrderBL orderBL = new OrderBL();
        orderBL.set(OrderBL.getOrCreateCurrentOrder(user.getId(), new Date(),
                user.getCurrency().getId(), true));

        List<OrderLineDTO> oldLines = OrderLineBL.copy(orderBL.getDTO().getLines());

        String sipUri = (String) fieldsHelper.getValue(ATTRIBUTE_CALLED_URI);
        List<PricingField> fields = fieldsHelper.getFields();
        PricingField fromSettleReservation = new PricingField(RateCardPricingStrategy.FROM_SETTLE_RESERVATION, Boolean.TRUE);
        PricingField.add(fields, fromSettleReservation);

        // We send the PricingField list to get a correct calculation of the new order line.
        CallDataRecord record = new CallDataRecord();
        record.setFields(fields);
        ArrayList<CallDataRecord> records = new ArrayList<CallDataRecord>(Arrays.asList(record));

        orderBL.addItem(orderBL.getEntity(), itemId, quantity,
                user.getLanguage().getId(), user.getId(), entityId,
                user.getCurrencyId(), records, null, false, sipUri, new Date());

        orderBL.checkOrderLineQuantities(oldLines, orderBL.getDTO().getLines(),
                entityId, orderBL.getDTO().getId(), true);

        fields.remove(fromSettleReservation);
        fieldsHelper.setFields(records.get(0).getFields());

	    SettleReservationResult result = new SettleReservationResult(
			    chargeSession.getCarriedUnits(), orderBL.getDTO());
        return result;
    }
    
    private void saveMediationRecord(String sessionId, UserDTO user, Integer itemId,
		                            List<PricingField> fields, SettleReservationResult settleResult) {
        MediationService mediationService = Context.getBean(MediationService.BEAN_NAME);

	    OrderDTO currentOrder = settleResult.getCurrentOrder();
	    BigDecimal usedUnits = settleResult.getCarriedUnits();
        //todo: fill description
        String description = "";
        //On HBase we need to save the reserved unit during the
        //session and not the current quantity that is on the line
        //(that is previous quantity + session used quantity)
        OrderLineDTO lineToSave = currentOrder.getLine(itemId);
        BigDecimal quantityAfterReservation = lineToSave.getQuantity();
        BigDecimal amountAfterReservation = lineToSave.getAmount();

        JbillingMediationRecord diameterEvent = new JbillingMediationRecord(JbillingMediationRecord.STATUS.PROCESSED,
                JbillingMediationRecord.TYPE.DIAMETER, user.getEntity().getId(), 0,
                getCdrKeyResolver().resolve(sessionId, fields), user.getId(), new Date(), usedUnits, description,
                user.getEntity().getCurrencyId(), itemId, currentOrder.getId(), lineToSave.getId(),
                PricingField.setPricingFieldsValue(fields), lineToSave.getAmount().divide(quantityAfterReservation).multiply(usedUnits),
                null, null);

        mediationService.saveDiameterEventAsJMR(diameterEvent);
        lineToSave.setQuantity(quantityAfterReservation);
        lineToSave.setAmount(amountAfterReservation);
    }

	private static class SettleReservationResult {
		private BigDecimal carriedUnits;
		private OrderDTO currentOrder;

		private SettleReservationResult(BigDecimal carriedUnits, OrderDTO currentOrder) {
			this.carriedUnits = carriedUnits;
			this.currentOrder = currentOrder;
		}

		public BigDecimal getCarriedUnits() {
			return carriedUnits;
		}

		public OrderDTO getCurrentOrder() {
			return currentOrder;
		}
	}
}
