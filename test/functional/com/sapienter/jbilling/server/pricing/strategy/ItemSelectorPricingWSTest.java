package com.sapienter.jbilling.server.pricing.strategy;

import com.sapienter.jbilling.common.CommonConstants;
import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.order.OrderChangeBL;
import com.sapienter.jbilling.server.order.OrderChangeWS;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.pricing.PriceModelWS;
import com.sapienter.jbilling.server.pricing.PricingTestHelper;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.sapienter.jbilling.test.Asserts.assertEquals;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Brian Cowdery
 * @since 10-Jul-2012
 */
@Test(groups = { "web-services", "pricing", "item-selector" })
public class ItemSelectorPricingWSTest {

    private static JbillingAPI api;

	private static Integer CURRENCY_ID;
	private static Integer ACCOUNT_TYPE;
	private static Integer ORDER_CHANGE_APPLY_STATUS;
	private static Integer MONTHLY_PERIOD;

    @BeforeTest
    public void getAPI() throws Exception {
        api = JbillingAPIFactory.getAPI();
	    CURRENCY_ID = PricingTestHelper.CURRENCY_USD;
	    ACCOUNT_TYPE = PricingTestHelper.PRANCING_PONY_BASIC_ACCOUNT_TYPE;
	    ORDER_CHANGE_APPLY_STATUS = PricingTestHelper.getOrCreateOrderChangeApplyStatus(api);
	    MONTHLY_PERIOD = PricingTestHelper.getOrCreateMonthlyOrderPeriod(api);
    }

    @Test
    public void testItemSelectorStrategy() {
	    String random = String.valueOf(System.currentTimeMillis());
        // new item selector
        PriceModelWS selector = new PriceModelWS(PriceModelStrategy.ITEM_SELECTOR.name(), new BigDecimal("1.00"), CURRENCY_ID);
        
        Integer categoryId = PricingTestHelper.createItemCategory(api);
        Integer firstItem = api.createItem(PricingTestHelper.buildItem("Test-Item-First-" + random, "Item selector test", categoryId));
        Integer secondItem = api.createItem(PricingTestHelper.buildItem("Test-Item-Second-" + random, "Item selector test", categoryId));
        Integer thirdItem = api.createItem(PricingTestHelper.buildItem("Test-Item-Third-" + random, "Item selector test", categoryId));
        selector.addAttribute("typeId", categoryId.toString()); // purchases from new item type trigger items to be added
        selector.addAttribute("1", firstItem.toString());      // add first item when 1 purchased
        selector.addAttribute("10", secondItem.toString());     // add second item when > 10 purchased
        selector.addAttribute("20", thirdItem.toString());     // add third item when > 20 purchased

        ItemDTOEx item = PricingTestHelper.buildItem("TEST-ITEM-SEL-" + random, "Item selector test", categoryId);
        item.addDefaultPrice(CommonConstants.EPOCH_DATE, selector);

        item.setId(api.createItem(item));
        assertNotNull("item created", item.getId());

        // item that we can purchase to trigger the item selector
        ItemDTOEx triggerItem = PricingTestHelper.buildItem("TRIGGER-" + random, "Trigger item", categoryId);
        triggerItem.addDefaultPrice(CommonConstants.EPOCH_DATE,
            new PriceModelWS(PriceModelStrategy.FLAT.name(), new BigDecimal("1.00"), CURRENCY_ID));

        triggerItem.setId(api.createItem(triggerItem));
        assertNotNull("item created", triggerItem.getId());

        // create user to test pricing with
        UserWS user = PricingTestHelper.buildUser("item-selector-" + random, MONTHLY_PERIOD, ACCOUNT_TYPE);
        user.setUserId(api.createUser(user));
        assertNotNull("customer created", user.getUserId());

        // order to be rated to test pricing
        OrderWS order = PricingTestHelper.buildMonthlyOrder(user.getUserId(), MONTHLY_PERIOD);
        OrderLineWS line = PricingTestHelper.buildOrderLine(item.getId(), 1);
        OrderLineWS triggerLine = PricingTestHelper.buildOrderLine(triggerItem.getId(), 1);
        order.setOrderLines(new OrderLineWS[] { line, triggerLine });

        // 1 trigger item, should result in first item being added
        order = api.rateOrder(order, OrderChangeBL.buildFromOrder(order, ORDER_CHANGE_APPLY_STATUS));

        assertThat(order.getOrderLines().length, is(3));
        assertThat(order.getOrderLines()[2].getItemId(), is(firstItem));

        // 11 trigger items, should result in seconditem being added
        line.setQuantity(11);
        order.setOrderLines(new OrderLineWS[] { line, triggerLine });
        order = api.rateOrder(order, OrderChangeBL.buildFromOrder(order, ORDER_CHANGE_APPLY_STATUS));

        assertThat(order.getOrderLines().length, is(3));
        assertThat(order.getOrderLines()[2].getItemId(), is(secondItem));

        // 21 trigger items, should result in third item being added
        line.setQuantity(21);
        order.setOrderLines(new OrderLineWS[] { line, triggerLine });
        order = api.rateOrder(order, OrderChangeBL.buildFromOrder(order, ORDER_CHANGE_APPLY_STATUS));

        assertThat(order.getOrderLines().length, is(3));
        assertThat(order.getOrderLines()[2].getItemId(), is(thirdItem));

        // cleanup
	    api.deleteItem(triggerItem.getId());
	    api.deleteItem(item.getId());
	    api.deleteItem(firstItem);
	    api.deleteItem(secondItem);
	    api.deleteItem(thirdItem);
	    api.deleteItemCategory(categoryId);
        api.deleteUser(user.getUserId());
    }
}
