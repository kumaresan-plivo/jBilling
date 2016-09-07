package com.sapienter.jbilling.server.pricing.strategy;

import com.sapienter.jbilling.common.Constants;
import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.pricing.PriceModelBL;
import com.sapienter.jbilling.server.pricing.db.ChainPosition;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.util.AttributeDefinition;
import org.apache.log4j.Logger;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.sapienter.jbilling.server.pricing.util.AttributeDefinition.Type.STRING;

/**
 * The teaser rate pricing strategy allows the user to specify different pricing strategies based on the time since
 * they enrolled. It can also blend the rates if pricing fields are specified indicating a period is covered.
 * For each 'cycle' we have to specify
 *   strategy - either FLAT or RATE_CARD
 *
 *  if the strategy is FLAT we have to specify
 *    rate - the rate per unit
 *
 *  if the strategy is RATE CARD we have to specify
 *    ratecard - the id of the rate card
 *    metafields - meta fields that must be passed to the rate card
 *
 * In addition we specify
 *   start_date - name of pricing field containing the period start date. Defaults to order active from
 *   end_date - name of pricing field containing the period end date. Defaults to order active until.
 *   event_date - If specified the start and end date will be copied to a pricing field with name event_date.
 *
 * The strategy to store the variables for each pricing strategy is
 *   [variable name].[cycle index]
 * For each meta field we store
 *   metafield.[cycle index].[meta field index].name - contains meta field name
 *   metafield.[cycle index].[meta field index].value - contains meta field value
 *
 * @author Gerhard Maree
 * @since 22-12-2015
 */
public class TeaserPricingStrategy extends RouteBasedRateCardPricingStrategy {

    public enum Strategy {FLAT, RATE_CARD}

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(RouteBasedRateCardPricingStrategy.class));

    /** name of pricing field containing the period start date*/
    public static final String PARAM_START_DATE = "start_date";
    /** name of pricing field containing the period end date*/
    public static final String PARAM_END_DATE = "end_date";
    /** If specified the start and end date will be copied to a pricing field with name event_date */
    public static final String PARAM_EVENT_DATE = "event_date";

    /** Prefixes used to specify values for cycles */
    public static final String PARAM_CYCLE_PREFIX = "cycle.";
    public static final String PARAM_PRICING_STRATEGY_PREFIX = "strategy.";
    public static final String PARAM_RATE_PREFIX = "rate.";
    public static final String PARAM_RATE_CARD_PREFIX = "ratecard.";
    public static final String PARAM_METAFIELD_PREFIX = "metafield.";


    public TeaserPricingStrategy() {
        setAttributeDefinitions(
//                new AttributeDefinition(PARAM_START_DATE, STRING, false),
//                new AttributeDefinition(PARAM_END_DATE, STRING, false),
                new AttributeDefinition(PARAM_EVENT_DATE, STRING, false)
        );
        setChainPositions(
                ChainPosition.START
        );

        setRequiresUsage(false);
        setUsesDynamicAttributes(false);
    }


    @Override
    public void applyTo(OrderDTO pricingOrder, PricingResult result, List<PricingField> fields, PriceModelDTO planPrice, BigDecimal quantity, Usage usage, boolean singlePurchase) {

        //calculate FUP quantities
        Map<FupKey, BigDecimal> fupResult = calculateFreeUsageQty(pricingOrder, result, quantity);
        quantity = fupResult.get(FupKey.NEW_QTY);

        SortedMap<Integer, CyclePrice> cyclePriceInfo = buildCyclePriceInfo(planPrice.getAttributes(), true);
        if(cyclePriceInfo.isEmpty()) {
            result.setPrice(BigDecimal.ZERO);
            return;
        }

        //get the pricing dates (start and end)
        Date defaultEndDate = (pricingOrder != null && pricingOrder.getActiveUntil() != null) ? pricingOrder.getActiveUntil() : new Date();
        Date defaultStartDate = (pricingOrder != null && pricingOrder.getActiveSince() != null) ? pricingOrder.getActiveSince() : new Date();
        String eventDatePricingFieldName = planPrice.getAttributes().get(PARAM_END_DATE);
        PricingField eventDatePf = PricingField.find(fields, (eventDatePricingFieldName == null) ? "end_date" : eventDatePricingFieldName);
        Date endDate = (eventDatePf != null) ? eventDatePf.getDateValue() : defaultEndDate;
        String startDatePricingFieldName = planPrice.getAttributes().get(PARAM_START_DATE);
        PricingField startDatePf = PricingField.find(fields, (startDatePricingFieldName == null) ? "start_date" : startDatePricingFieldName);
        Date startDate = (startDatePf != null) ? startDatePf.getDateValue() : defaultStartDate;

        LOG.debug("Start Date = [%s], End Date = [%s]", startDate, endDate);

        //get the last enrollment date
        Date lastEnrollmentDate = endDate;
        if(pricingOrder != null) {
            boolean dateFound = false;
            if(pricingOrder.getUser() != null && pricingOrder.getUser().getCustomer() != null) {
                MetaFieldValue<Date> metaField = pricingOrder.getUser().getCustomer().getMetaField(FileConstants.CUSTOMER_LAST_ENROLLMENT_METAFIELD_NAME);
                if(metaField != null) {
                    lastEnrollmentDate = metaField.getValue();
                    dateFound = true;
                }
            }
            if(!dateFound) {
                LOG.debug("User [%s] does not have last enrollment metafield. Using order valid from [%s]", pricingOrder.getUser(), pricingOrder.getActiveUntil());
                if(pricingOrder.getActiveUntil() != null) {
                    lastEnrollmentDate = pricingOrder.getActiveUntil();
                }
            }
        }

        LocalDate lastEnrollmentLD = new LocalDate(lastEnrollmentDate);

        //get the pricing cycles that must be used for start and end date
        CyclePrice appliedStartCyclePrice = cyclePriceInfo.values().iterator().next();
        CyclePrice appliedEndCyclePrice = appliedStartCyclePrice;
        for(Map.Entry<Integer, CyclePrice> entry : cyclePriceInfo.entrySet()) {
            LocalDate cycleStartLD = lastEnrollmentLD.plusMonths(entry.getKey());
            if(cycleStartLD.toDate().before(startDate)) {
                appliedStartCyclePrice = entry.getValue();
            }
            if(cycleStartLD.toDate().before(endDate)) {
                appliedEndCyclePrice = entry.getValue();
            }
        }

        LOG.debug("Applied Start Cycle=%s", appliedStartCyclePrice);
        LOG.debug("End Start Cycle=%s", appliedEndCyclePrice);

        //calculate the price based on the start of the period
        if(appliedStartCyclePrice.strategy.equals(Strategy.FLAT.name())) {
            result.setPrice(appliedStartCyclePrice.rate);
        } else if(appliedStartCyclePrice.strategy.equals(Strategy.RATE_CARD.name())) {
            List<PricingField> pricingFields = appliedStartCyclePrice.metaFieldsToPricingFields();
            String eventDate = planPrice.getAttributes().get(PARAM_EVENT_DATE);
            if(eventDate != null && !eventDate.isEmpty()) {
                pricingFields.add(new PricingField(eventDate, new SimpleDateFormat("MM/dd/yyyy").format(startDate)));
            }
            BigDecimal total = calculatePrice(pricingOrder, result, fields, pricingFields, planPrice, quantity, appliedStartCyclePrice.rateCardAttrName);
            calculateUnitPrice(result, quantity, fupResult.get(FupKey.FREE_QTY), total);
        } else {
            LOG.error("Unknown pricing strategy %s from price model %s", appliedStartCyclePrice.strategy, planPrice);
        }

        LOG.debug("Start Rate=%s", result.getPrice());

        //if the pricing period spans 2 definitions we need to calculate a blended price
        if(appliedEndCyclePrice != appliedStartCyclePrice) {
            LOG.debug("Blended price must be calculated");

            BigDecimal startPrice = result.getPrice();
            BigDecimal endPrice = null;
            if(appliedEndCyclePrice.strategy.equals(Strategy.FLAT.name())) {
                endPrice = new BigDecimal(appliedEndCyclePrice.rate);
            } else if(appliedEndCyclePrice.strategy.equals(Strategy.RATE_CARD.name())) {
                List<PricingField> pricingFields = appliedEndCyclePrice.metaFieldsToPricingFields();
                String eventDate = planPrice.getAttributes().get(PARAM_EVENT_DATE);
                if(eventDate != null && !eventDate.isEmpty()) {
                    pricingFields.add(new PricingField(eventDate, new SimpleDateFormat("MM/dd/yyyy").format(endDate)));
                }
                BigDecimal total = calculatePrice(pricingOrder, result, fields, pricingFields, planPrice, quantity, appliedEndCyclePrice.rateCardAttrName);
                calculateUnitPrice(result, quantity, fupResult.get(FupKey.FREE_QTY), total);
                endPrice = result.getPrice();
            } else {
                LOG.error("Unknown pricing strategy %s from price model %s", appliedEndCyclePrice.strategy, planPrice);
                return;
            }
            LOG.debug("Price for end of cycle = %s", endPrice);

            //pro-rate the prices between the 2 price definitions.
            LocalDate cycleStartLD = lastEnrollmentLD.plusMonths(appliedEndCyclePrice.fromCycle);
            BigDecimal daysPeriod1 = BigDecimal.valueOf(Days.daysBetween(new LocalDate(startDate), cycleStartLD).getDays());
            BigDecimal daysPeriod2 = BigDecimal.valueOf(Days.daysBetween(cycleStartLD, new LocalDate(endDate)).getDays()+1);

            LOG.debug("Days in 1st period = %s", daysPeriod1);
            LOG.debug("Days in 2nd period = %s", daysPeriod2);

            BigDecimal blendedPrice = startPrice.multiply(daysPeriod1).add(endPrice.multiply(daysPeriod2)).divide(daysPeriod1.add(daysPeriod2), Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND);
            result.setPrice(blendedPrice);
        }
    }

    @Override
    public void validate(PriceModelDTO priceModel) {
        SortedMap<Integer, CyclePrice> cyclePriceInfo = buildCyclePriceInfo(priceModel.getAttributes(), true);
        if(cyclePriceInfo.isEmpty()) {
            throw new SessionInternalError("At least one price must be defined",
                    new String[] {"bean.TeaserPricingStrategy.strategy.validation.error.at.least.one.period"});
        }
        if(cyclePriceInfo.values().iterator().next().fromCycle != 0) {
            throw new SessionInternalError("The first cycle must be 0",
                    new String[] {"bean.TeaserPricingStrategy.fromCycle.validation.error.first.cycle.zero"});
        }
        for(CyclePrice cyclePrice : cyclePriceInfo.values()) {
            if(Strategy.FLAT.name().equals(cyclePrice.strategy)) {
                try {
                    PriceModelBL.validateRate(new BigDecimal(cyclePrice.rate), TeaserPricingStrategy.class.getSimpleName());
                } catch (NumberFormatException e) {
                    throw new SessionInternalError("The rate is not a valid number",
                            new String[] {"TeaserPricingStrategy,rate,validation.error.not.a.valid.rate"});
                }
            } else if(Strategy.RATE_CARD.name().equals(cyclePrice.strategy)) {
                if(cyclePrice.rateCard == null || cyclePrice.rateCard.isEmpty()) {
                    throw new SessionInternalError("A rate card must be specified for a period using a Rate Card pricing strategy",
                            new String[] {"TeaserPricingStrategy,rateCard,validation.error.notnull"});
                }
            } else {
                throw new SessionInternalError("Unknown pricing strategy",
                        new String[] {"TeaserPricingStrategy,strategy,validation.error.unknown.value"},
                        new String[] {cyclePrice.strategy});
            }
        }
    }

    /**
     * Take all the price model attributes and convert them into CyclePrice objects for each period.
     * The key returned in the map is the Cycle when this CyclePrice should be used.
     *
     * @param attrs
     * @return
     */
    public static SortedMap<Integer, CyclePrice> buildCyclePriceInfo(Map<String, String> attrs, boolean validate) {
        LOG.debug("Attrs=%s",attrs);
        SortedMap<Integer, CyclePrice> cyclePriceInfo = new TreeMap<>();
        int errorCycleIdx = -1;
        for(Map.Entry<String, String> entry : attrs.entrySet()) {
            if(entry.getKey().startsWith(PARAM_CYCLE_PREFIX)) {
                LOG.debug("Adding cycle %s = %s", entry.getKey(), entry.getValue());
                String idx = entry.getKey().substring(PARAM_CYCLE_PREFIX.length());
                CyclePrice price = new CyclePrice();
                try {
                    price.fromCycle = Integer.valueOf(entry.getValue());
                } catch (Exception e) {
                    if(validate) {
                        throw new SessionInternalError("The from cycle must be an integer",
                            new String[] {"bean.TeaserPricingStrategy.fromCycle.validation.error.not.integer"});
                    }
                    price.fromCycle = errorCycleIdx--;
                    entry.setValue(Integer.toString(price.fromCycle));
                }
                price.rate = attrs.get(PARAM_RATE_PREFIX + idx);
                price.rateCard = attrs.get(PARAM_RATE_CARD_PREFIX + idx);
                price.rateCardAttrName = PARAM_RATE_CARD_PREFIX + idx;
                price.strategy = attrs.get(PARAM_PRICING_STRATEGY_PREFIX + idx);
                if(validate && cyclePriceInfo.containsKey(price.fromCycle)) {
                    throw new SessionInternalError("Duplicate from cycle.",
                            new String[] {"bean.TeaserPricingStrategy.fromCycle.validation.error.cycle.duplicate"});
                }
                cyclePriceInfo.put(price.fromCycle, price);
                LOG.debug("CyclePrice = %s", price);
            }
        }

        for(Map.Entry<String, String> entry : attrs.entrySet()) {
            if(entry.getKey().startsWith(PARAM_METAFIELD_PREFIX) && entry.getKey().contains("name") && !entry.getValue().trim().isEmpty()) {
                String key = entry.getKey();
                String idx = key.substring(PARAM_METAFIELD_PREFIX.length(), key.indexOf('.', PARAM_METAFIELD_PREFIX.length()+1));

                CyclePrice price = cyclePriceInfo.get(Integer.valueOf(attrs.get(PARAM_CYCLE_PREFIX+idx)));
                if(price != null) {
                    String mfName = entry.getValue();
                    String mfValue = attrs.get(key.replace("name","value"));
                    LOG.debug("Adding mf %s = %s", mfName, mfValue);
                    price.metaFields.put(mfName, mfValue);
                } else {
                    LOG.warn("CyclePrice not found for index %s from key %s", idx, key);
                }
            }
        }

        return cyclePriceInfo;
    }

    /**
     * Contains the information we need for 1 period
     */
    public static class CyclePrice {
        public int fromCycle;
        public String rate;
        public String rateCard;
        public String rateCardAttrName;
        public String strategy;
        public Map<String, String> metaFields = new HashMap<>();

        private List<PricingField> metaFieldsToPricingFields() {
            List<PricingField> additionalFields = new ArrayList<>();

            for(Map.Entry<String, String> entry : metaFields.entrySet()) {
                additionalFields.add(new PricingField(entry.getKey(), entry.getValue()));
            }
            return additionalFields;
        }

        @Override
        public String toString() {
            return "CyclePrice{" +
                    "fromCycle=" + fromCycle +
                    ", rate='" + rate + '\'' +
                    ", rateCard='" + rateCard + '\'' +
                    ", rateCardAttrName='" + rateCardAttrName + '\'' +
                    ", strategy='" + strategy + '\'' +
                    ", metaFields=" + metaFields +
                    '}';
        }
    }
}
