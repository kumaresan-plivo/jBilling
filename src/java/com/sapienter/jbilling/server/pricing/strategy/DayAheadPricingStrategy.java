package com.sapienter.jbilling.server.pricing.strategy;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.pricing.db.ChainPosition;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.util.AttributeDefinition;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.sapienter.jbilling.server.pricing.util.AttributeDefinition.Type.DECIMAL;
import static com.sapienter.jbilling.server.pricing.util.AttributeDefinition.Type.INTEGER;

/**
 * Created by hitesh on 8/2/16.
 */
public class DayAheadPricingStrategy extends RouteBasedRateCardPricingStrategy {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(DayAheadPricingStrategy.class));

    private static final String EFFCTIVE_DATE="effective_date";
    public static final String ADDER_FEE = "adder_fee";

    public DayAheadPricingStrategy() {
        setAttributeDefinitions(
                new AttributeDefinition(ADDER_FEE, DECIMAL, true),
                new AttributeDefinition(PARAM_ROUTE_RATE_CARD_ID, INTEGER, true)
        );

        setChainPositions(
                ChainPosition.START,
                ChainPosition.MIDDLE,
                ChainPosition.END
        );

        setRequiresUsage(false);
    }

    @Override
    public void applyTo(OrderDTO pricingOrder, PricingResult result, List<PricingField> fields, PriceModelDTO planPrice,
                        BigDecimal quantity, Usage usage, boolean singlePurchase) {

        if (pricingOrder == null || pricingOrder.getUser() == null) {
            LOG.debug("User not found.");
            if (result != null) {
                result.setPrice(BigDecimal.ZERO);
            }
            return;
        }
        CustomerDTO customerDTO = pricingOrder.getUser().getCustomer();

        // Fetch zone meta field from customer
        MetaFieldValue zoneMetaField = customerDTO.getMetaField(FileConstants.CUSTOMER_ZONE_META_FIELD_NAME);
        if (zoneMetaField == null || zoneMetaField.getValue() == null) {
            throw new SessionInternalError("Customer should belongs to a Zone");
        }
        //Fetch date
        Date activeUntilDate = pricingOrder.getActiveUntil();
        if (activeUntilDate == null){
            LOG.debug("activeUntilDate is null, set activeUntilDate as today date");
            activeUntilDate = new Date();
        }
        LOG.debug("activeUntilDate:  " + activeUntilDate);
        Calendar c = Calendar.getInstance();
        c.setTime(activeUntilDate);
        c.add(Calendar.DATE, 1);
        activeUntilDate = c.getTime();
        LOG.debug("activeUntilDate after add one day: " + activeUntilDate);

        //additional fields(effectiveDate,zone) for searching
        List<PricingField> pricingFields = new ArrayList<PricingField>();
        pricingFields.add(new PricingField(zoneMetaField.getField().getName(), zoneMetaField.getValue().toString()));
        pricingFields.add(new PricingField(EFFCTIVE_DATE, new SimpleDateFormat("MM/dd/yyyy").format(activeUntilDate)));

        //[Adjusted Rate] = [Day ahead rate] + [Adder fee]

        /*Day ahead rate will be calculated from route rate card.*/
        BigDecimal dayAheadRate = calculatePrice(pricingOrder, result, fields, pricingFields, planPrice, quantity, PARAM_ROUTE_RATE_CARD_ID);
        LOG.debug("dayAheadRate:"+dayAheadRate);
        BigDecimal adderFee = calculateAdderFee(customerDTO, planPrice);
        LOG.debug("adderFee:"+adderFee);
        BigDecimal adjustedRate;
        if (dayAheadRate != null) {
            adjustedRate = adderFee.add(dayAheadRate);
        } else {
            adjustedRate = adderFee;
        }

        LOG.debug("Adjusted Rate:" + adjustedRate);
        result.setPrice(adjustedRate);
    }

    /*Adder fee is calculated from customer but if not defined for customer then use the one defined at pricing model level.*/
    private BigDecimal calculateAdderFee(CustomerDTO customerDTO, PriceModelDTO planPrice) {

        MetaFieldValue metaFieldValue = customerDTO.getMetaField(FileConstants.ADDER_FEE_META_FIELD);
        //fetch Adder Fee meta field from Customer
        if (metaFieldValue != null && metaFieldValue.getValue() != null) {
            LOG.debug("Adder Fee find at customer level");
            return new BigDecimal(metaFieldValue.getValue().toString());
        }
        //fetch Adder Fee attributes from pricing model level
        if (planPrice.getAttributes().get(ADDER_FEE) != null) {
            LOG.debug("Adder Fee find at pricing model level");
            return new BigDecimal(planPrice.getAttributes().get(ADDER_FEE));
        } else {
            LOG.debug("Adder Fee Not found");
            return BigDecimal.ZERO;
        }
    }
}
