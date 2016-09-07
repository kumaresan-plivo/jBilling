package com.sapienter.jbilling.server.pricing.strategy;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.metafields.DataType;
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.metafields.db.MetaFieldValue;
import com.sapienter.jbilling.server.order.OrderChangeBL;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.pricing.PriceModelWS;
import com.sapienter.jbilling.server.pricing.PricingTestHelper;
import com.sapienter.jbilling.server.pricing.RatingUnitWS;
import com.sapienter.jbilling.server.pricing.cache.MatchingFieldType;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import com.sapienter.jbilling.server.user.MatchingFieldWS;
import com.sapienter.jbilling.server.user.RouteRateCardWS;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.sapienter.jbilling.test.Asserts.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * Teaser Pricing Strategy tests
 *
 * @author Gerhard Maree
 * @since 10-Nov-2015
 */
@Test(groups = { "web-services", "pricing", "rate-card" })
public class TeaserPricingStrategyWSTest {

    private static final Integer PRANCING_PONY = Integer.valueOf(1);
    private static final Integer ENABLED = Integer.valueOf(1);
    private static final Integer DISABLED = Integer.valueOf(0);
    private static final Integer US_DOLLAR = Integer.valueOf(1);

    private final static int ORDER_CHANGE_STATUS_APPLY_ID = 3;
	private static Integer PP_ACCOUNT_TYPE = 1;
	private static Integer PP_MONTHLY_PERIOD = 6;
    private static Integer CAT_CALLS = 2201;
    JbillingAPI api;
    ItemDTOEx item;
    RatingUnitWS ratingUnit;
    RouteRateCardWS rateCard;
    MatchingFieldWS mfkeyMf;
    MatchingFieldWS eventDateMf;

    @BeforeClass
    public void getAPI() throws Exception {
        api = JbillingAPIFactory.getAPI();

        ratingUnit = new RatingUnitWS();
        ratingUnit.setName("Single BI");
        ratingUnit.setPriceUnitName("single");
        ratingUnit.setIncrementUnitName("single");
        ratingUnit.setIncrementUnitQuantity("1");
        ratingUnit.setIsCanBeDeleted(true);
        ratingUnit.setId(api.createRatingUnit(ratingUnit));

        rateCard = new RouteRateCardWS();
        rateCard.setRatingUnitId(ratingUnit.getId());
//        rateCard.setTableName();
        rateCard.setEntityId(1);
        rateCard.setName("bitest" + System.currentTimeMillis());
        rateCard.setId(api.createRouteRateCard(rateCard, createRateCardFile()));

        mfkeyMf = new MatchingFieldWS();
        mfkeyMf.setDescription("mfkey");
        mfkeyMf.setMatchingField("mfkey");
        mfkeyMf.setMediationField("mfkey");
        mfkeyMf.setOrderSequence("2");
        mfkeyMf.setRouteRateCardId(rateCard.getId());
        mfkeyMf.setType(MatchingFieldType.EXACT.name());
        mfkeyMf.setRequired(true);
        mfkeyMf.setId(api.createMatchingField(mfkeyMf));

        eventDateMf = new MatchingFieldWS();
        eventDateMf.setDescription("event_date");
        eventDateMf.setMatchingField("active_dates");
        eventDateMf.setMediationField("event_date");
        eventDateMf.setOrderSequence("1");
        eventDateMf.setRouteRateCardId(rateCard.getId());
        eventDateMf.setType(MatchingFieldType.ACTIVE_DATE.name());
        eventDateMf.setRequired(true);
        eventDateMf.setId(api.createMatchingField(eventDateMf));

	    PP_MONTHLY_PERIOD = PricingTestHelper.getOrCreateMonthlyOrderPeriod(api);

        item = createItem(false, false, CAT_CALLS);

        PriceModelWS priceModel = new PriceModelWS(PriceModelStrategy.TEASER_PRICING.name(), new BigDecimal("1.00"), US_DOLLAR);
        priceModel.addAttribute(TeaserPricingStrategy.PARAM_START_DATE, "start_date");
        priceModel.addAttribute(TeaserPricingStrategy.PARAM_END_DATE, "end_date");
        priceModel.addAttribute(TeaserPricingStrategy.PARAM_EVENT_DATE, "event_date");

        priceModel.addAttribute(TeaserPricingStrategy.PARAM_CYCLE_PREFIX+"0", "0");
        priceModel.addAttribute(TeaserPricingStrategy.PARAM_PRICING_STRATEGY_PREFIX+"0", TeaserPricingStrategy.Strategy.FLAT.name());
        priceModel.addAttribute(TeaserPricingStrategy.PARAM_RATE_PREFIX+"0", "1");

        priceModel.addAttribute(TeaserPricingStrategy.PARAM_CYCLE_PREFIX+"1", "3");
        priceModel.addAttribute(TeaserPricingStrategy.PARAM_PRICING_STRATEGY_PREFIX+"1", TeaserPricingStrategy.Strategy.RATE_CARD.name());
        priceModel.addAttribute(TeaserPricingStrategy.PARAM_RATE_CARD_PREFIX+"1", rateCard.getId().toString());
        priceModel.addAttribute(TeaserPricingStrategy.PARAM_METAFIELD_PREFIX+"1.1.name", "mfkey");
        priceModel.addAttribute(TeaserPricingStrategy.PARAM_METAFIELD_PREFIX+"1.1.value", "GROUP1");

        item.addDefaultPrice(CommonConstants.EPOCH_DATE, priceModel);
        item.setId(api.createItem(item));
    }

    private File createRateCardFile() throws Exception {
        File file = File.createTempFile("ratecard", "csv");
        List<String> rows = Arrays.asList("id,name,surcharge,initial_increment,subsequent_increment,charge,active_dates,mfkey",
                "1,JERSYCITY NJ,0,0,1,0.11,11/01/2015-11/30/2015,GROUP1",
                "2,JERSYCITY NJ,0,0,1,0.12,12/01/2015-12/31/2015,GROUP1",
                "3,JERSYCITY NJ,0,0,1,0.13,01/01/2016-01/31/2015,GROUP1",
                "4,JERSYCITY NJ,0,0,1,0.14,02/01/2016-02/28/2015,GROUP1",
                "5,JERSYCITY NJ,0,0,1,0.21,11/01/2015-11/30/2015,GROUP2",
                "6,JERSYCITY NJ,0,0,1,0.22,12/01/2015-12/31/2015,GROUP2",
                "7,JERSYCITY NJ,0,0,1,0.23,01/01/2016-01/31/2015,GROUP2",
                "8,JERSYCITY NJ,0,0,1,0.24,02/01/2016-02/28/2015,GROUP2");

        FileUtils.writeLines(file, rows);
        return file;
    }

    @AfterClass
    public void cleanup() throws Exception {
        api.deleteItem(item.getId());
        api.deleteMatchingField(mfkeyMf.getId());
        api.deleteMatchingField(eventDateMf.getId());
        api.deleteRouteRateCard(rateCard.getId());
        api.deleteRatingUnit(ratingUnit.getId());
    }

    @Test
    public void test001TeaserPricingStrategy() throws Exception {
        api = JbillingAPIFactory.getAPI();
        // long distance call uses the rate card
        // see the test db for details
        final int LONG_DISTANCE_CALL = 2800;

        // create user to test pricing with
        List<MetaFieldValueWS> metaFields = new ArrayList<>();
        metaFields.add(new MetaFieldValueWS(FileConstants.CUSTOMER_LAST_ENROLLMENT_METAFIELD_NAME, null, DataType.DATE, false, new Date(115, 8, 1))); //1 september 2015
        UserWS user = PricingTestHelper.buildUser("teaser", PP_MONTHLY_PERIOD, PP_ACCOUNT_TYPE, metaFields);
        user.setUserId(api.createUser(user));
        assertNotNull("customer created", user.getUserId());


        //In first 3 months. Use flat pricing strategy
        PricingField[] pf1 = {
            new PricingField("start_date", "09/15/2015"),
            new PricingField("end_date", "09/15/2015")
        };

        OrderWS order = PricingTestHelper.buildMonthlyOrder(user.getUserId(), PP_MONTHLY_PERIOD);

        order.setPricingFields(PricingField.setPricingFieldsValue(pf1));

        OrderLineWS line = PricingTestHelper.buildOrderLine(item.getId(), 100);
        order.setOrderLines(new OrderLineWS[] { line });

        order = api.rateOrder(order, OrderChangeBL.buildFromOrder(order, ORDER_CHANGE_STATUS_APPLY_ID));
        assertThat(order.getOrderLines().length, is(1));
        assertEquals(new BigDecimal("100.00"), order.getOrderLines()[0].getAmountAsDecimal());

        //rate order after 3 months. Use rate card
        PricingField[] pf2 = {
                new PricingField("start_date", "12/15/2015"),
                new PricingField("end_date", "12/15/2015")
        };
        order.setPricingFields(PricingField.setPricingFieldsValue(pf2));
        order.setOrderLines(new OrderLineWS[] { line });

        order = api.rateOrder(order, OrderChangeBL.buildFromOrder(order, ORDER_CHANGE_STATUS_APPLY_ID));
        assertThat(order.getOrderLines().length, is(1));
        assertEquals(new BigDecimal("12.00"), order.getOrderLines()[0].getAmountAsDecimal());

        //Use blended rate for a period spanning 2 periods
        PricingField[] pf3 = {
                new PricingField("start_date", "11/20/2015"),
                new PricingField("end_date", "12/20/2015")
        };
        order.setPricingFields(PricingField.setPricingFieldsValue(pf3));
        order.setOrderLines(new OrderLineWS[] { line });

        order = api.rateOrder(order, OrderChangeBL.buildFromOrder(order, ORDER_CHANGE_STATUS_APPLY_ID));
        assertThat(order.getOrderLines().length, is(1));
        assertEquals(new BigDecimal("44.27"), order.getOrderLines()[0].getAmountAsDecimal());

        // clean up
        api.deleteUser(user.getUserId());
    }

    protected static ItemDTOEx createItem(boolean allowAssetManagement, boolean global, Integer... types){
        ItemDTOEx item = new ItemDTOEx();
        item.setDescription("TestItem: " + System.currentTimeMillis());
        item.setNumber("TestWS-" + System.currentTimeMillis());
        item.setTypes(types);
        if(allowAssetManagement){
            item.setAssetManagementEnabled(ENABLED);
        }
        item.setExcludedTypes(new Integer[]{});
        item.setHasDecimals(DISABLED);
        if(global) {
            item.setGlobal(global);
        } else {
            item.setGlobal(false);
        }
        item.setDeleted(DISABLED);
        item.setEntityId(PRANCING_PONY);
        ArrayList<Integer> entities = new ArrayList<Integer>();
        entities.add(PRANCING_PONY);
        item.setEntities(entities);
        return item;
    }
}
