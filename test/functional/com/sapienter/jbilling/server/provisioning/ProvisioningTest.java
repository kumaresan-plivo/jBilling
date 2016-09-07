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

package com.sapienter.jbilling.server.provisioning;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.item.AssetWS;
import com.sapienter.jbilling.server.metafields.MetaFieldValueWS;
import com.sapienter.jbilling.server.metafields.DataType;
import com.sapienter.jbilling.server.order.*;
import com.sapienter.jbilling.server.order.db.OrderChangeStatusDTO;
import com.sapienter.jbilling.server.payment.PaymentAuthorizationDTOEx;
import com.sapienter.jbilling.server.payment.PaymentInformationWS;
import com.sapienter.jbilling.server.payment.PaymentWS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskTypeWS;
import com.sapienter.jbilling.server.pluggableTask.admin.PluggableTaskWS;
import com.sapienter.jbilling.server.provisioning.ProvisioningCommandStatus;
import com.sapienter.jbilling.server.provisioning.ProvisioningCommandWS;
import com.sapienter.jbilling.server.provisioning.db.ProvisioningCommandDTO;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.InternationalDescriptionWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.server.util.api.JbillingAPIFactory;
import com.sapienter.jbilling.test.Asserts;

import junit.framework.TestCase;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.*;

import static com.sapienter.jbilling.test.Asserts.*;
import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

@Test(groups = { "integration", "provisioning" })
public class ProvisioningTest {

    private static final Integer GANDALF_USER_ID = 2;

    private static final int           ORDER_LINES_COUNT  = 6;
    private static final int           USER_ID            = 1000;
    private static Integer[]           itemIds            = {
        1, 2, 3, 24, 240, 14
    };
    private final static int ORDER_CHANGE_STATUS_APPLY_ID = 3;
    private final static int ORDER_LINE_PROVISIONING_TASK_ID = 128;
    private final static int EXAMPLE_PROVISIONING_TASK_ID = 100;
    private static Integer[] provisioningStatus = new Integer[6];
    private final static int ORDER_PERIOD_MONTHLY = 2;
    JbillingAPI api;

    /**
     *
     * @see junit.framework.TestCase#setUp()
     */
    @BeforeClass
    protected void setUp() throws Exception {
        api = JbillingAPIFactory.getAPI();
    }

    private void pause(long t) {
        System.out.println("pausing for " + t);
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test001NewQuantityEvent() {
        Integer orderId = null;

        try {
            provisioningStatus[0] = Constants.PROVISIONING_STATUS_ACTIVE;
            provisioningStatus[1] = Constants.PROVISIONING_STATUS_INACTIVE;
            provisioningStatus[2] = null;
            provisioningStatus[3] = Constants.PROVISIONING_STATUS_PENDING_ACTIVE;
            provisioningStatus[4] = Constants.PROVISIONING_STATUS_PENDING_INACTIVE;
            provisioningStatus[5] = null;

            OrderWS newOrder = createMockOrder(USER_ID, ORDER_LINES_COUNT, new BigDecimal("77"));

            // create order through api
            orderId = api.createOrder(newOrder, OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID));
            pause(2000);
            // update provisioning status in order lines because it is not allowed to point it during lines create
            OrderWS createdOrder = api.getOrder(orderId);
            for (int i = 0; i < createdOrder.getOrderLines().length; i++) {
                OrderLineWS targetLine = findOrderLineWithItem(createdOrder.getOrderLines(), itemIds[i]);
                targetLine.setProvisioningStatusId(provisioningStatus[i]);
            }
            api.updateOrder(createdOrder, new OrderChangeWS[]{});

            System.out.println("Created order." + orderId);
            assertNotNull("The order was not created", orderId);
            System.out.println("running provisioning batch process..");
            //pause(2000);
            api.triggerProvisioning();
            pause(2000);
            System.out.println("Getting back new quantity provisioning order " + orderId);

            OrderWS retOrder = api.getOrder(orderId);

            System.out.println("got order: " + retOrder);

            OrderLineWS[] retLine = retOrder.getOrderLines();

            for (int i = 0; i < retLine.length; i++) {
                OrderLineWS targetLine = findOrderLineWithItem(retLine, itemIds[i]);
                if (i == 0) {
                    assertEquals("order line " + (i + 1) + "", targetLine.getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_ACTIVE);
                }

                if (i == 1) {
                    assertEquals("order line " + (i + 1) + "", targetLine.getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_INACTIVE);
                }

                if (i == 2) {
                    assertEquals("order line " + (i + 1) + "", targetLine.getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_INACTIVE); // default
                }

                if (i == 3) {
                    assertEquals("order line " + (i + 1) + "", targetLine.getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_PENDING_ACTIVE);
                }

                if (i == 4) {
                    assertEquals("order line " + (i + 1) + "", targetLine.getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_PENDING_INACTIVE);
                }

                if (i == 5) {
                    assertEquals("order line " + (i + 1) + "", targetLine.getProvisioningStatusId(),
                                 Constants.PROVISIONING_STATUS_INACTIVE); // default

                }
            }
        } catch (SessionInternalError e) {
            e.printStackTrace();
            fail("Exception!" + e.getMessage());
        }
        finally {
            if (orderId != null) {api.deleteOrder(orderId);}
        }
    }

    private static OrderWS createMockOrder(int userId, int orderLinesCount, BigDecimal linePrice) {
        OrderWS order = new OrderWS();

        order.setUserId(userId);
        order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
        order.setPeriod(ORDER_PERIOD_MONTHLY);   //order with monthly plan
        order.setCurrencyId(1);
        order.setActiveSince(new Date());

        ArrayList<OrderLineWS> lines = new ArrayList<OrderLineWS>(orderLinesCount);

        for (int i = 0; i < orderLinesCount; i++) {
            OrderLineWS nextLine = new OrderLineWS();

            nextLine.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            nextLine.setDescription("Order line: " + i);
            nextLine.setItemId(itemIds[i]);
            nextLine.setQuantity(1);
            nextLine.setPrice(linePrice);
            nextLine.setAmount(nextLine.getQuantityAsDecimal().multiply(linePrice));
            nextLine.setProvisioningStatusId(provisioningStatus[i]);
            lines.add(nextLine);
        }

        order.setOrderLines(lines.toArray(new OrderLineWS[lines.size()]));

        return order;
    }

    @Test
    public void test004ExternalProvisioning() {
        Integer orderId = null;

        try {
            // create the order
            OrderWS order = new OrderWS();
            order.setUserId(USER_ID);
            order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
            order.setPeriod(1);
            order.setCurrencyId(1);

            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(2008, 9, 3);
            order.setActiveSince(cal.getTime());

            Date now = new Date();
            cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            order.setActiveUntil(cal.getTime());

            OrderLineWS line = new OrderLineWS();
            line.setItemId(251);
            line.setQuantity(1); // trigger 'external_provisioning_test' rule
            line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            line.setUseItem(true);
            line.setProvisioningStatusId(Constants.PROVISIONING_STATUS_INACTIVE);

            order.setOrderLines(new OrderLineWS[] { line });

            orderId = api.createOrder(order, OrderChangeBL.buildFromOrder(order, ORDER_CHANGE_STATUS_APPLY_ID));
            System.out.println("Created order ..." + orderId);
            assertNotNull("The order was not created", orderId);

            pause(10000); // wait for MDBs to complete
            System.out.println("Getting back external provisioning order " + orderId);

            OrderWS retOrder = api.getOrder(orderId);
            ProvisioningCommandWS[] commands = retOrder.getProvisioningCommands();

            Arrays.sort(commands, new Comparator<ProvisioningCommandWS>() {
                @Override
                public int compare(ProvisioningCommandWS c1, ProvisioningCommandWS c2) {
                    return c1.getExecutionOrder().compareTo(c2.getExecutionOrder());
                }
            });
            
            assertNotNull("Commands should not be null", commands);
            assertEquals("There is no provisioning command generated for the order", 3, commands.length);

            ProvisioningCommandWS command = commands[0];
            assertNotNull("Command does not exist", command);

            assertEquals("Command status should be SUCCESSFUL", ProvisioningCommandStatus.SUCCESSFUL,
                    command.getCommandStatus());
            assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
            assertEquals("Command name not matching", "activate_user", command.getName());

            ProvisioningRequestWS[] requests = command.getProvisioningRequests();
            assertNotNull("Requests should not be null", requests);
            assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

            command = commands[1];
            assertNotNull("Command does not exist", command);

            assertEquals("Command status should be FAILED", ProvisioningCommandStatus.FAILED,
                    command.getCommandStatus());
            assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
            assertEquals("Command name not matching", "result_test", command.getName());

            requests = command.getProvisioningRequests();
            assertNotNull("Requests should not be null", requests);
            assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

            command = commands[2];
            assertNotNull("Command does not exist", command);

            assertEquals("Command status should be PROCESSED", ProvisioningCommandStatus.PROCESSED,
                    command.getCommandStatus());
            assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
            assertEquals("Command name not matching", "result_test", command.getName());

            requests = command.getProvisioningRequests();
            assertNotNull("Requests should not be null", requests);
            assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
        finally {
            if (orderId != null) {api.deleteOrder(orderId);}
        }
    }

    @Test
    public void test005CAIProvisioning() {
        Integer orderId = null;

        try {
            // create the order
            OrderWS order = new OrderWS();
            order.setUserId(USER_ID);
            order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
            order.setPeriod(1);
            order.setCurrencyId(1);

            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(2008, 9, 3);
            order.setActiveSince(cal.getTime());

            Date now = new Date();
            cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            order.setActiveUntil(cal.getTime());

            OrderLineWS line = new OrderLineWS();
            line.setItemId(251);
            line.setQuantity(2); // trigger 'cai_test' rule
            line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            line.setUseItem(true);
            line.setProvisioningStatusId(Constants.PROVISIONING_STATUS_INACTIVE);

            order.setOrderLines(new OrderLineWS[] { line });

            System.out.println("Creating order ...");
            orderId = api.createOrder(order, OrderChangeBL.buildFromOrder(order, ORDER_CHANGE_STATUS_APPLY_ID));
            assertNotNull("The order was not created", orderId);

            pause(10000); // wait for MDBs to complete
            System.out.println("Getting back cia provisioning order " + orderId);

            // check TestExternalProvisioningMDB was successful
            OrderWS retOrder = api.getOrder(orderId);

            ProvisioningCommandWS[] commands = retOrder.getProvisioningCommands();

            assertNotNull("Commands should not be null", commands);
            assertEquals("There is no provisioning command generated for the order", 2, commands.length);

            Arrays.sort(commands, new Comparator<ProvisioningCommandWS>() {
    			@Override
    			public int compare(ProvisioningCommandWS c1, ProvisioningCommandWS c2) {
    				return c1.getExecutionOrder().compareTo(c2.getExecutionOrder()); 
    			}
    		});
            
            ProvisioningCommandWS command = commands[0];
            assertNotNull("Command does not exist", command);

            assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                    command.getCommandStatus());
            assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
            assertEquals("Command name not matching", "activate_user", command.getName());

            ProvisioningRequestWS[] requests = command.getProvisioningRequests();
            assertNotNull("Requests should not be null", requests);
            assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

            command = commands[1];
            assertNotNull("Command does not exist", command);

            assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                    command.getCommandStatus());
            assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
            assertEquals("Command name not matching", "cai_test", command.getName());

            requests = command.getProvisioningRequests();
            assertNotNull("Requests should not be null", requests);
            assertEquals("There should be 1 provisioning requests generated per command", 1, requests.length);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
        finally {
            if (orderId != null) {api.deleteOrder(orderId);}
        }
    }

    @Test
    public void test006MMSCProvisioning() {
        Integer orderId = null;

        try {
            // create the order
            OrderWS order = new OrderWS();
            order.setUserId(USER_ID);
            order.setBillingTypeId(Constants.ORDER_BILLING_PRE_PAID);
            order.setPeriod(1);
            order.setCurrencyId(1);

            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(2008, 9, 3);
            order.setActiveSince(cal.getTime());

            Date now = new Date();
            cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            order.setActiveUntil(cal.getTime());

            OrderLineWS line = new OrderLineWS();
            line.setItemId(251);
            line.setQuantity(3); // trigger 'mmsc_test' rule
            line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            line.setUseItem(true);
            line.setProvisioningStatusId(Constants.PROVISIONING_STATUS_INACTIVE);

            order.setOrderLines(new OrderLineWS[] { line });

            System.out.println("Creating order ...");
            orderId = api.createOrder(order, OrderChangeBL.buildFromOrder(order, ORDER_CHANGE_STATUS_APPLY_ID));
            assertNotNull("The order was not created", orderId);

            pause(10000); // wait for MDBs to complete
            System.out.println("Getting back mmsc provisioning order " + orderId);

            // check TestExternalProvisioningMDB was successful
            OrderWS retOrder = api.getOrder(orderId);
            ProvisioningCommandWS[] commands = retOrder.getProvisioningCommands();

            assertNotNull("Commands should not be null", commands);
            assertEquals("There is no provisioning command generated for the order", 2, commands.length);

            Arrays.sort(commands, new Comparator<ProvisioningCommandWS>() {
    			@Override
    			public int compare(ProvisioningCommandWS c1, ProvisioningCommandWS c2) {
    				return c1.getExecutionOrder().compareTo(c2.getExecutionOrder()); 
    			}
    		});
            
            ProvisioningCommandWS command = commands[0];
            assertNotNull("Command does not exist", command);

            assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                    command.getCommandStatus());
            assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
            assertEquals("Command name not matching", "activate_user", command.getName());

            ProvisioningRequestWS[] requests = command.getProvisioningRequests();
            assertNotNull("Requests should not be null", requests);
            assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

            command = commands[1];
            assertNotNull("Command does not exist", command);

            assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                    command.getCommandStatus());
            assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
            assertEquals("Command name not matching", "mmsc_test", command.getName());

            requests = command.getProvisioningRequests();
            assertNotNull("Requests should not be null", requests);
            assertEquals("There should be 1 provisioning requests generated per command", 1, requests.length);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception caught:" + e);
        }
        finally {
            if (orderId != null) {api.deleteOrder(orderId);}
        }
    }

    @Test
    public void testPaymentSuccessfulEvent() throws Exception
    {

        // create the payment
        PaymentWS payment = new PaymentWS();
        payment.setAmount(new BigDecimal("5.00"));
        payment.setIsRefund(new Integer(0));
        payment.setMethodId(Constants.PAYMENT_METHOD_VISA);
        payment.setPaymentDate(Calendar.getInstance().getTime());
        payment.setCurrencyId(new Integer(1));
        payment.setUserId(GANDALF_USER_ID);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 5);
        PaymentInformationWS cc = com.sapienter.jbilling.server.user.WSTest.createCreditCard("Provisioning test Payment", 
        		"4111111111111152", cal.getTime());
        cc.setPaymentMethodId(Constants.PAYMENT_METHOD_VISA);
   
        payment.getPaymentInstruments().clear();
        payment.getPaymentInstruments().add(cc);

        System.out.println("processing payment.");
        PaymentAuthorizationDTOEx authInfo = api.processPayment(payment, null);
        // wait for the provisioning to be processed
        pause(2000);

        // check payment successful
        assertNotNull("Payment result not null", authInfo);
        assertNotNull("Auth id not null", authInfo.getId());
        assertTrue("Payment Authorization result should be OK", authInfo.getResult().booleanValue());

        // check payment was made
        PaymentWS lastPayment = api.getLatestPayment(GANDALF_USER_ID);
        assertNotNull("payment can not be null", lastPayment);
        assertNotNull("auth in payment can not be null", lastPayment.getAuthorizationId());
        assertEquals("payment ids match", lastPayment.getId(), authInfo.getPaymentId().intValue());
        Asserts.assertEquals("correct payment amount", new BigDecimal("5"), lastPayment.getAmountAsDecimal());
        Asserts.assertEquals("correct payment balance", BigDecimal.ZERO, lastPayment.getBalanceAsDecimal());

        assertTrue("provisioning commands created", lastPayment.getProvisioningCommands().length > 0);

        ProvisioningCommandWS[] commands = lastPayment.getProvisioningCommands();
        assertEquals("There is no provisioning command generated for the payment", 1, commands.length);

        ProvisioningCommandWS command = commands[0];

        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be payment command", ProvisioningCommandType.PAYMENT, command.getCommandType());
        assertEquals("Command name not matching", "payment_successful_provisioning_command", command.getName());

        ProvisioningRequestWS[] requests = command.getProvisioningRequests();
            Arrays.sort(requests, new Comparator<ProvisioningRequestWS>() {
                @Override
                public int compare(ProvisioningRequestWS o1, ProvisioningRequestWS o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });        
		assertNotNull("Requests should not be null", requests);
        assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        ProvisioningRequestWS request1 = requests[0];
        assertNotNull("Single request should not be null", request1);
        assertEquals(1, request1.getExecutionOrder().intValue());
        assertEquals("test", request1.getProcessor());
        assertEquals("Provisioning request 1 should belong to the command", command.getId(), request1.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 1 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request1.getRequestStatus());
        assertNotNull("Request 1 should have a result received date", request1.getResultReceivedDate());
        assertFalse("Result 1 should have received a result parameter map", request1.getResultMap().isEmpty());

        ProvisioningRequestWS request2 = requests[1];
        assertNotNull("Single request should not be null", request2);
        assertEquals(2, request2.getExecutionOrder().intValue());
        assertEquals("test", request2.getProcessor());
        assertEquals("Provisioning request 2 should belong to the command", command.getId(), request2.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 2 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request2.getRequestStatus());
        assertNotNull("Request 2 should have a result received date", request2.getResultReceivedDate());
        assertFalse("Result 2 should have received a result parameter map", request2.getResultMap().isEmpty());

        // clean up
        api.deletePayment(lastPayment.getId());
    }

/*    @Test
    public void testAssetCreatedEvent() throws Exception
    {
        AssetWS asset_ws_1 = getAssetWS();
        asset_ws_1.setIdentifier("Asset Create Event Test" + System.currentTimeMillis());

        System.out.println("creating asset");
        Integer ret_asset_1 = api.createAsset(asset_ws_1);
        // wait for the provisioning to be processed
        pause(2000);
        assertNotNull("The asset was not created", ret_asset_1);

        AssetWS assetWS = api.getAsset(ret_asset_1);
        assertTrue("provisioning commands created", assetWS.getProvisioningCommands().length > 0);

        ProvisioningCommandWS[] commands = assetWS.getProvisioningCommands();
        assertEquals("There is no provisioning command generated for the asset", 1, commands.length);

        ProvisioningCommandWS command = commands[0];

        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandDTO.ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be asset command", ProvisioningCommandType.ASSET, command.getCommandType());
        assertEquals("Command name not matching", "new_asset_command", command.getName());

        ProvisioningRequestWS[] requests = command.getProvisioningRequests();
        assertNotNull("Requests should not be null", requests);
        assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        ProvisioningRequestWS request1 = requests[0];
        assertNotNull("Single request should not be null", request1);
        assertEquals(1, request1.getExecutionOrder().intValue());
        assertEquals("test", request1.getProcessor());
        assertEquals("Provisioning request 1 should belong to the command", command.getId(), request1.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 1 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request1.getRequestStatus());
        assertNotNull("Request 1 should have a result received date", request1.getResultReceivedDate());
        assertFalse("Result 1 should have received a result parameter map", request1.getResultMap().isEmpty());

        ProvisioningRequestWS request2 = requests[1];
        assertNotNull("Single request should not be null", request2);
        assertEquals(2, request2.getExecutionOrder().intValue());
        assertEquals("test", request2.getProcessor());
        assertEquals("Provisioning request 2 should belong to the command", command.getId(), request2.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 2 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request2.getRequestStatus());
        assertNotNull("Request 2 should have a result received date", request2.getResultReceivedDate());
        assertFalse("Result 2 should have received a result parameter map", request2.getResultMap().isEmpty());

        // clean up
        api.deleteAsset(ret_asset_1);
    }*/

/*
    @Test
    public void testAssetAddedToOrderEvent() throws Exception
    {
        AssetWS asset_ws_1 = getAssetWS();
        asset_ws_1.setIdentifier("Asset Added to Order Event Test" + System.currentTimeMillis());

        System.out.println("creating asset");
        Integer ret_asset_1 = api.createAsset(asset_ws_1);
        // wait for the provisioning to be processed
        pause(2000);
        assertNotNull("The asset was not created", ret_asset_1);

        AssetWS assetWS = api.getAsset(ret_asset_1);
        assertTrue("provisioning commands created", assetWS.getProvisioningCommands().length > 0);

        ProvisioningCommandWS[] commands = assetWS.getProvisioningCommands();
        assertEquals("There is no provisioning command generated for the asset", 1, commands.length);

        ProvisioningCommandWS command = commands[0];

        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandDTO.ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be asset command", ProvisioningCommandType.ASSET, command.getCommandType());
        assertEquals("Command name not matching", "new_asset_command", command.getName());

        OrderWS newOrder = getOrderWS();

        OrderLineWS lines[] = new OrderLineWS[1];

        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setQuantity(new Integer(1));
        line.setItemId(new Integer(1250));
        line.setUseItem(new Boolean(true));
        line.setAssetIds(new Integer[]{ret_asset_1});
        lines[0] = line;

        newOrder.setOrderLines(lines);

        System.out.println("Creating order ... " + newOrder);
        Integer orderId_1 = api.createOrder(newOrder, OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID));
        // wait for the provisioning to be processed
        pause(2000);

        OrderWS orderWS = api.getOrder(orderId_1);
        commands = orderWS.getProvisioningCommands();

        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order", 1, commands.length);

        command = commands[0];
        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandDTO.ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
        assertEquals("Command name not matching", "activate_user", command.getName());

        assertEquals("Order should contain 1 line", 1, orderWS.getOrderLines().length);

        assetWS = api.getAsset(ret_asset_1);
        assertTrue("provisioning commands created", assetWS.getProvisioningCommands().length > 0);

        commands = assetWS.getProvisioningCommands();
        assertEquals("There is no provisioning command generated for the asset", 3, commands.length);

        Arrays.sort(commands, new Comparator<ProvisioningCommandWS>() {
			@Override
			public int compare(ProvisioningCommandWS c1, ProvisioningCommandWS c2) {
				return Integer.valueOf(c1.getId()).compareTo(Integer.valueOf(c2.getId())); 
			}
		});
        
        command = commands[1];

        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandDTO.ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be asset command", ProvisioningCommandType.ASSET, command.getCommandType());
        assertEquals("Command name not matching", "asset_assigned_command", command.getName());

        ProvisioningRequestWS[] requests = command.getProvisioningRequests();
        Arrays.sort(requests, new Comparator<ProvisioningRequestWS>() {
            @Override
            public int compare(ProvisioningRequestWS o1, ProvisioningRequestWS o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        assertNotNull("Requests should not be null", requests);
        assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        ProvisioningRequestWS request1 = requests[0];
        assertNotNull("Single request should not be null", request1);
        assertEquals(1, request1.getExecutionOrder().intValue());
        assertEquals("test", request1.getProcessor());
        assertEquals("Provisioning request 1 should belong to the command", command.getId(), request1.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 1 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request1.getRequestStatus());
        assertNotNull("Request 1 should have a result received date", request1.getResultReceivedDate());
        assertFalse("Result 1 should have received a result parameter map", request1.getResultMap().isEmpty());

        ProvisioningRequestWS request2 = requests[1];
        assertNotNull("Single request should not be null", request2);
        assertEquals(2, request2.getExecutionOrder().intValue());
        assertEquals("test", request2.getProcessor());
        assertEquals("Provisioning request 2 should belong to the command", command.getId(), request2.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 2 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request2.getRequestStatus());
        assertNotNull("Request 2 should have a result received date", request2.getResultReceivedDate());
        assertFalse("Result 2 should have received a result parameter map", request2.getResultMap().isEmpty());

        command = commands[2];

        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandDTO.ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be asset command", ProvisioningCommandType.ASSET, command.getCommandType());
        assertEquals("Command name not matching", "updated_asset_command", command.getName());

        // clean up
        System.out.println("Cleaning asset " + ret_asset_1);
        api.deleteAsset(ret_asset_1);
        System.out.println("Cleaning order " + orderId_1);
        api.deleteOrder(orderId_1);
    }
*/

/*    @Test
    public void testAssetUpdateEvent() throws Exception
    {
        AssetWS asset_ws_1 = getAssetWS();
        asset_ws_1.setIdentifier("Asset Update Event Test" + System.currentTimeMillis());

        System.out.println("creating asset");
        Integer ret_asset_1 = api.createAsset(asset_ws_1);
        // wait for the provisioning to be processed
        pause(2000);
        assertNotNull("The asset was not created", ret_asset_1);

        AssetWS assetWS = api.getAsset(ret_asset_1);
        assertTrue("provisioning commands created", assetWS.getProvisioningCommands().length > 0);

        ProvisioningCommandWS[] commands = assetWS.getProvisioningCommands();
        assertEquals("There is no provisioning command generated for the asset", 1, commands.length);

        assetWS.setNotes("Notes Change");
        api.updateAsset(assetWS);
        // wait for the provisioning to be processed
        pause(2000);

        assetWS = api.getAsset(ret_asset_1);
        commands = assetWS.getProvisioningCommands();
        assertEquals("There is no provisioning command generated for the asset", 2, commands.length);

        Arrays.sort(commands, new Comparator<ProvisioningCommandWS>() {
			@Override
			public int compare(ProvisioningCommandWS c1, ProvisioningCommandWS c2) {
				return Integer.valueOf(c1.getId()).compareTo(Integer.valueOf(c2.getId())); 
			}
		});
        
        ProvisioningCommandWS command = commands[1];

        assertNotNull("Command does not exist", command);
        assertEquals("Command status should be successful", ProvisioningCommandDTO.ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be asset command", ProvisioningCommandType.ASSET, command.getCommandType());
        assertEquals("Command name not matching", "updated_asset_command", command.getName());

        ProvisioningRequestWS[] requests = command.getProvisioningRequests();
        assertNotNull("Requests should not be null", requests);
        assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        ProvisioningRequestWS request1 = requests[0];
        assertNotNull("Single request should not be null", request1);
        assertEquals(1, request1.getExecutionOrder().intValue());
        assertEquals("test", request1.getProcessor());
        assertEquals("Provisioning request 1 should belong to the command", command.getId(), request1.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 1 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request1.getRequestStatus());
        assertNotNull("Request 1 should have a result received date", request1.getResultReceivedDate());
        assertFalse("Result 1 should have received a result parameter map", request1.getResultMap().isEmpty());

        ProvisioningRequestWS request2 = requests[1];
        assertNotNull("Single request should not be null", request2);
        assertEquals(2, request2.getExecutionOrder().intValue());
        assertEquals("test", request2.getProcessor());
        assertEquals("Provisioning request 2 should belong to the command", command.getId(), request2.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 2 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request2.getRequestStatus());
        assertNotNull("Request 2 should have a result received date", request2.getResultReceivedDate());
        assertFalse("Result 2 should have received a result parameter map", request2.getResultMap().isEmpty());

        // clean up
        api.deleteAsset(ret_asset_1);
    }*/

    @Test
    public void testActivateOrderProvisioning() throws Exception
    {
        
        
        OrderWS newOrder = getOrderWS();

        System.out.println("Creating order ... " + newOrder);
        Integer orderId_1 = api.createOrder(newOrder, OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID));
        // wait for the provisioning to be processed
        pause(2000);

        OrderWS orderWS = api.getOrder(orderId_1);
        ProvisioningCommandWS[] commands = orderWS.getProvisioningCommands();

        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order", 1, commands.length);

        ProvisioningCommandWS command = commands[0];
        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
        assertEquals("Command name not matching", "activate_user", command.getName());

        ProvisioningRequestWS[] requests = command.getProvisioningRequests();
        assertNotNull("Requests should not be null", requests);
        assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        ProvisioningRequestWS request1 = requests[0];
        assertNotNull("Single request should not be null", request1);
        assertEquals(1, request1.getExecutionOrder().intValue());
        assertEquals("test", request1.getProcessor());
        assertEquals("Provisioning request 1 should belong to the command", command.getId(), request1.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 1 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request1.getRequestStatus());
        assertNotNull("Request 1 should have a result received date", request1.getResultReceivedDate());
        assertFalse("Result 1 should have received a result parameter map", request1.getResultMap().isEmpty());

        ProvisioningRequestWS request2 = requests[1];
        assertNotNull("Single request should not be null", request2);
        assertEquals(2, request2.getExecutionOrder().intValue());
        assertEquals("test", request2.getProcessor());
        assertEquals("Provisioning request 2 should belong to the command", command.getId(), request2.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 2 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request2.getRequestStatus());
        assertNotNull("Request 2 should have a result received date", request2.getResultReceivedDate());
        assertFalse("Result 2 should have received a result parameter map", request2.getResultMap().isEmpty());

        // cleanup
        System.out.println("Cleaning order " + orderId_1);
        api.deleteOrder(orderId_1);

    }

    @Test
    public void testSuspendOrderProvisioning() throws Exception
    {
        
        
        OrderWS newOrder = getOrderWS();

        System.out.println("Creating order ... " + newOrder);
        Integer orderId_1 = api.createOrder(newOrder, OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID));
        // wait for the provisioning to be processed
        pause(2000);

        OrderWS orderWS = api.getOrder(orderId_1);
        ProvisioningCommandWS[] commands = orderWS.getProvisioningCommands();

        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order", 1, commands.length);

        ProvisioningCommandWS command = commands[0];
        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
        assertEquals("Command name not matching", "activate_user", command.getName());

        // setting order status to SUSPENDED
        System.out.println("Suspending order ... " + orderId_1);
        orderWS.setOrderStatusWS( api.findOrderStatusById( 3 ));
        
        api.updateOrder(orderWS, OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID));
        pause(2000);

        orderWS = api.getOrder(orderId_1);
        commands = orderWS.getProvisioningCommands();

        assertEquals("Order Status is not SUSPENDED", orderWS.getOrderStatusWS().getOrderStatusFlag(), OrderStatusFlag.NOT_INVOICE);
        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order", 2, commands.length);

        // you want to sort and get the deactive request always on the second position.
        Arrays.sort(commands, new Comparator<ProvisioningCommandWS>() {
			@Override
			public int compare(ProvisioningCommandWS c1, ProvisioningCommandWS c2) {
				return Integer.valueOf(c1.getId()).compareTo(Integer.valueOf(c2.getId())); 
			}
		});
        
        command = commands[1];
        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
        assertEquals("Command name not matching", "deactivate_user", command.getName());

        ProvisioningRequestWS[] requests = command.getProvisioningRequests();
        assertNotNull("Requests should not be null", requests);
        assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        ProvisioningRequestWS request1 = requests[0];
        assertNotNull("Single request should not be null", request1);
        assertEquals(1, request1.getExecutionOrder().intValue());
        assertEquals("test", request1.getProcessor());
        assertEquals("Provisioning request 1 should belong to the command", command.getId(), request1.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 1 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request1.getRequestStatus());
        assertNotNull("Request 1 should have a result received date", request1.getResultReceivedDate());
        assertFalse("Result 1 should have received a result parameter map", request1.getResultMap().isEmpty());

        ProvisioningRequestWS request2 = requests[1];
        assertNotNull("Single request should not be null", request2);
        assertEquals(2, request2.getExecutionOrder().intValue());
        assertEquals("test", request2.getProcessor());
        assertEquals("Provisioning request 2 should belong to the command", command.getId(), request2.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 2 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request2.getRequestStatus());
        assertNotNull("Request 2 should have a result received date", request2.getResultReceivedDate());
        assertFalse("Result 2 should have received a result parameter map", request2.getResultMap().isEmpty());

        // cleanup
        System.out.println("Cleaning order " + orderId_1);
        api.deleteOrder(orderId_1);

    }

    @Test
    public void testFinishedOrderProvisioning() throws Exception
    {
        /*
        * Create
        */
        OrderWS newOrder = getOrderWS();

        System.out.println("Creating order ... " + newOrder);
        Integer orderId_1 = api.createOrder(newOrder, OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID));
        // wait for the provisioning to be processed
        pause(2000);

        OrderWS orderWS = api.getOrder(orderId_1);
        ProvisioningCommandWS[] commands = orderWS.getProvisioningCommands();

        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order", 1, commands.length);

        ProvisioningCommandWS command = commands[0];
        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
        assertEquals("Command name not matching", "activate_user", command.getName());

        // setting status to FINISHED
        System.out.println("Finishing order ... " + newOrder);
        orderWS.setOrderStatusWS( api.findOrderStatusById(2) );
        
        api.updateOrder(orderWS, OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID));
        pause(2000);
        orderWS = api.getOrder(orderId_1);
        commands = orderWS.getProvisioningCommands();

        assertEquals("Order Status is not FINISHED", orderWS.getOrderStatusWS().getOrderStatusFlag(), OrderStatusFlag.FINISHED);
        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order", 2, commands.length);

        Arrays.sort(commands, new Comparator<ProvisioningCommandWS>() {
			@Override
			public int compare(ProvisioningCommandWS c1, ProvisioningCommandWS c2) {
				return Integer.valueOf(c1.getId()).compareTo(Integer.valueOf(c2.getId())); 
			}
		});
        
        command = commands[1];
        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
        assertEquals("Command name not matching", "deactivate_user", command.getName());

        ProvisioningRequestWS[] requests = command.getProvisioningRequests();
        assertNotNull("Requests should not be null", requests);
        assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        ProvisioningRequestWS request1 = requests[0];
        assertNotNull("Single request should not be null", request1);
        assertEquals(1, request1.getExecutionOrder().intValue());
        assertEquals("test", request1.getProcessor());
        assertEquals("Provisioning request 1 should belong to the command", command.getId(), request1.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 1 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request1.getRequestStatus());
        assertNotNull("Request 1 should have a result received date", request1.getResultReceivedDate());
        assertFalse("Result 1 should have received a result parameter map", request1.getResultMap().isEmpty());

        ProvisioningRequestWS request2 = requests[1];
        assertNotNull("Single request should not be null", request2);
        assertEquals(2, request2.getExecutionOrder().intValue());
        assertEquals("test", request2.getProcessor());
        assertEquals("Provisioning request 2 should belong to the command", command.getId(), request2.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 2 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request2.getRequestStatus());
        assertNotNull("Request 2 should have a result received date", request2.getResultReceivedDate());
        assertFalse("Result 2 should have received a result parameter map", request2.getResultMap().isEmpty());

        // cleanup
        System.out.println("Cleaning order " + orderId_1);
        api.deleteOrder(orderId_1);

    }

    @Test
    public void testReActivateOrderProvisioning() throws Exception
    {
        
    	 /*
         * Create
         */
        
        OrderWS newOrder = getOrderWS();

        System.out.println("Creating order ... " + newOrder);
        Integer orderId_1 = api.createOrder(newOrder, OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID));
        // wait for the provisioning to be processed
        pause(2000);

        OrderWS orderWS = api.getOrder(orderId_1);
        ProvisioningCommandWS[] commands = orderWS.getProvisioningCommands();

        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order", 1, commands.length);

        ProvisioningCommandWS command = commands[0];
        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
        assertEquals("Command name not matching", "activate_user", command.getName());

        // setting Order Status to SUSPENDED
        System.out.println("Suspending order ... " + orderId_1);
        orderWS.setOrderStatusWS( api.findOrderStatusById(3) );
        api.updateOrder(orderWS, OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID));
        pause(2000);

        orderWS = api.getOrder(orderId_1);
        commands = orderWS.getProvisioningCommands();

        assertEquals("Order Status is not SUSPENDED", orderWS.getOrderStatusWS().getOrderStatusFlag(), OrderStatusFlag.NOT_INVOICE);
        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order", 2, commands.length);

        // setting Order Status to ACTIVE
        System.out.println("Re-activating order ... " + orderId_1);
        orderWS.setOrderStatusWS( api.findOrderStatusById(Integer.valueOf(1)) );
        api.updateOrder(orderWS, OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID));
        pause(2000);

        orderWS = api.getOrder(orderId_1);
        commands = orderWS.getProvisioningCommands();

        assertEquals("Order Status is not ACTIVE", orderWS.getOrderStatusWS().getOrderStatusFlag(), OrderStatusFlag.INVOICE);
        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order", 3, commands.length);

        Arrays.sort(commands, new Comparator<ProvisioningCommandWS>() {
			@Override
			public int compare(ProvisioningCommandWS c1, ProvisioningCommandWS c2) {
				return Integer.valueOf(c1.getId()).compareTo(Integer.valueOf(c2.getId())); 
			}
		});
        
        command = commands[2];
        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
        assertEquals("Command name not matching", "activate_user", command.getName());

        ProvisioningRequestWS[] requests = command.getProvisioningRequests();
        assertNotNull("Requests should not be null", requests);
        assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        ProvisioningRequestWS request1 = requests[0];
        assertNotNull("Single request should not be null", request1);
        assertEquals(1, request1.getExecutionOrder().intValue());
        assertEquals("test", request1.getProcessor());
        assertEquals("Provisioning request 1 should belong to the command", command.getId(), request1.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 1 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request1.getRequestStatus());
        assertNotNull("Request 1 should have a result received date", request1.getResultReceivedDate());
        assertFalse("Result 1 should have received a result parameter map", request1.getResultMap().isEmpty());

        ProvisioningRequestWS request2 = requests[1];
        assertNotNull("Single request should not be null", request2);
        assertEquals(2, request2.getExecutionOrder().intValue());
        assertEquals("test", request2.getProcessor());
        assertEquals("Provisioning request 2 should belong to the command", command.getId(), request2.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 2 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request2.getRequestStatus());
        assertNotNull("Request 2 should have a result received date", request2.getResultReceivedDate());
        assertFalse("Result 2 should have received a result parameter map", request2.getResultMap().isEmpty());

        // cleanup
        System.out.println("Cleaning order " + orderId_1);
        api.deleteOrder(orderId_1);

    }

    @Test
    public void testDeleteOrderProvisioning() throws Exception
    {
        
    	 /*
         * Create
         */
        
        OrderWS newOrder = getOrderWS();

        System.out.println("Creating order ... " + newOrder);
        Integer orderId_1 = api.createOrder(newOrder, OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID));
        System.out.println("Created order ... " + orderId_1);
        
        // wait for the provisioning to be processed
        pause(2000);

        OrderWS orderWS = api.getOrder(orderId_1);
        ProvisioningCommandWS[] commands = orderWS.getProvisioningCommands();

        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order", 1, commands.length);

        ProvisioningCommandWS command = commands[0];
        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
        assertEquals("Command name not matching", "activate_user", command.getName());

        System.out.println("Delete order ... " + orderId_1);
        api.deleteOrder(orderId_1);
        pause(2000);

        List<ProvisioningCommandWS> orderCommands = Arrays.asList(api.getProvisioningCommands(ProvisioningCommandType.ORDER, orderId_1));

        assertNotNull("Commands should not be null", orderCommands);
        assertEquals("There is no provisioning command generated for the order", 2, orderCommands.size());

        // you want to sort and get the deactive request always on the second position.
        Collections.sort(orderCommands, new Comparator<ProvisioningCommandWS>() {
			@Override
			public int compare(ProvisioningCommandWS c1, ProvisioningCommandWS c2) {
				return Integer.valueOf(c1.getId()).compareTo(Integer.valueOf(c2.getId())); 
			}
		});

        command = orderCommands.get(1);
        assertNotNull("Command does not exist", command);
        System.out.println("Command at 2: " + command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
        assertEquals("Command name not matching", "deactivate_user", command.getName());

        ProvisioningRequestWS[] requests = command.getProvisioningRequests();
        assertNotNull("Requests should not be null", requests);
        assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        ProvisioningRequestWS request1 = requests[0];
        assertNotNull("Single request should not be null", request1);
        assertEquals(1, request1.getExecutionOrder().intValue());
        assertEquals("test", request1.getProcessor());
        assertEquals("Provisioning request 1 should belong to the command", command.getId(), request1.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 1 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request1.getRequestStatus());
        assertNotNull("Request 1 should have a result received date", request1.getResultReceivedDate());
        assertFalse("Result 1 should have received a result parameter map", request1.getResultMap().isEmpty());

        ProvisioningRequestWS request2 = requests[1];
        assertNotNull("Single request should not be null", request2);
        assertEquals(2, request2.getExecutionOrder().intValue());
        assertEquals("test", request2.getProcessor());
        assertEquals("Provisioning request 2 should belong to the command", command.getId(), request2.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 2 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request2.getRequestStatus());
        assertNotNull("Request 2 should have a result received date", request2.getResultReceivedDate());
        assertFalse("Result 2 should have received a result parameter map", request2.getResultMap().isEmpty());

        // cleanup
        System.out.println("Cleaning order " + orderId_1);
        api.deleteOrder(orderId_1);

    }

    @Test
    public void testOrderProvisioning() throws Exception
    {
        System.out.println("#testOrderProvisioning");
        JbillingAPI api = JbillingAPIFactory.getAPI();

	    //this test expects the NID of user to be
	    //15-JAN-2010, migration scripts (#12343) changes
	    //that to something else and this test fails
	    UserWS user = api.getUserWS(GANDALF_USER_ID);
	    user.setPassword(null);
	    Calendar cal = Calendar.getInstance();
	    cal.set(2010, Calendar.JANUARY, 15);
	    user.setNextInvoiceDate(cal.getTime());
		api.updateUser(user);

        OrderWS newOrder = getOrderWS();

        System.out.println("Creating order ... " + newOrder);
	    OrderChangeWS[] changes = OrderChangeBL.buildFromOrder(newOrder, ORDER_CHANGE_STATUS_APPLY_ID);
	    for(OrderChangeWS change : changes) change.setStartDate(newOrder.getActiveSince());
        Integer invoiceId_1 = api.createOrderAndInvoice(newOrder, changes);
        InvoiceWS invoice_1 = api.getInvoiceWS(invoiceId_1);
        Integer orderId_1 = invoice_1.getOrders()[0];

        assertNotNull("The order was not created", invoiceId_1);

        // wait for the provisioning to be processed
        pause(2000);

        OrderWS orderWS = api.getOrder(orderId_1);
        ProvisioningCommandWS[] commands = orderWS.getProvisioningCommands();

        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order", 1, commands.length);

        ProvisioningCommandWS command = commands[0];
        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order command", ProvisioningCommandType.ORDER, command.getCommandType());
        assertEquals("Command name not matching", "activate_user", command.getName());

        ProvisioningRequestWS[] requests = command.getProvisioningRequests();
        Arrays.sort(requests, new Comparator<ProvisioningRequestWS>() {
            @Override
            public int compare(ProvisioningRequestWS o1, ProvisioningRequestWS o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        assertNotNull("Requests should not be null", requests);
        assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        ProvisioningRequestWS request1 = requests[0];
        assertNotNull("Single request should not be null", request1);
        assertEquals(1, request1.getExecutionOrder().intValue());
        assertEquals("test", request1.getProcessor());
        assertEquals("Provisioning request 1 should belong to the command", command.getId(), request1.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 1 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request1.getRequestStatus());
        assertNotNull("Request 1 should have a result received date", request1.getResultReceivedDate());
        assertFalse("Result 1 should have received a result parameter map", request1.getResultMap().isEmpty());

        ProvisioningRequestWS request2 = requests[1];
        assertNotNull("Single request should not be null", request2);
        assertEquals(2, request2.getExecutionOrder().intValue());
        assertEquals("test", request2.getProcessor());
        assertEquals("Provisioning request 2 should belong to the command", command.getId(), request2.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 2 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request2.getRequestStatus());
        assertNotNull("Request 2 should have a result received date", request2.getResultReceivedDate());
        assertFalse("Result 2 should have received a result parameter map", request2.getResultMap().isEmpty());

        // cleanup
        System.out.println("Cleaning invoice " + invoiceId_1);
        api.deleteInvoice(invoiceId_1);
        System.out.println("Cleaning order " + orderId_1);
        api.deleteOrder(orderId_1);

    }

    @Test
    public void testOrderLineProvisioning() throws Exception {

        System.out.println("#testOrderLineProvisioning");
        JbillingAPI api = JbillingAPIFactory.getAPI();

        Integer ret_asset_1 = null;
        Integer newStatusId = null;
        Integer processingStatusId = null;
        Integer pluginId = null;
        Integer orderId_1 = null;

        try {

        // 1. create new asset
        AssetWS asset_ws_1 = getAssetWS();
        asset_ws_1.setIdentifier("Test Asset " + System.currentTimeMillis());

        System.out.println("creating asset");
        ret_asset_1 = api.createAsset(asset_ws_1);

        // 2. create order change statuses: NEW, PROCESSING
        // APPLY order change status already exists as part of the test data
        OrderChangeStatusWS newStatus = new OrderChangeStatusWS();
        newStatus.setApplyToOrder(ApplyToOrder.NO);
        newStatus.setDeleted(0);
        newStatus.setOrder(2);
        newStatus.addDescription(new InternationalDescriptionWS(
                com.sapienter.jbilling.server.util.Constants.LANGUAGE_ENGLISH_ID, "NEW"));

        newStatusId = api.createOrderChangeStatus(newStatus);
        assertNotNull("NEW Status should be created", newStatusId);

        OrderChangeStatusWS processingStatus = new OrderChangeStatusWS();
        processingStatus.setApplyToOrder(ApplyToOrder.NO);
        processingStatus.setDeleted(0);
        processingStatus.setOrder(3);
        processingStatus.addDescription(new InternationalDescriptionWS(
                com.sapienter.jbilling.server.util.Constants.LANGUAGE_ENGLISH_ID, "PROCESSING"));

        processingStatusId = api.createOrderChangeStatus(processingStatus);
        assertNotNull("PROCESSING Status should be created", processingStatusId);

        // 3. configure plugins
        PluggableTaskWS plugin = new PluggableTaskWS();
        Map<String, String> parameters = new Hashtable<String, String>();
        parameters.put("provisionable_order_change_status_id", processingStatusId.toString());
        plugin.setParameters((Hashtable) parameters);
        plugin.setProcessingOrder(115);
        //fetch the pluggable_task_type for orderLienProvisioningTask
        PluggableTaskTypeWS olProvisioningTask = api.getPluginTypeWSByClassName("com.sapienter.jbilling.server.provisioning.task.OrderLineProvisioningTask");
        assertNotNull("Task should have been found.", olProvisioningTask);
        plugin.setTypeId(olProvisioningTask.getId());

        pluginId = api.createPlugin(plugin);

        // 4. create new order with ORDER CHANGE status set to NEW
        OrderWS newOrder = getOrderWS();

        OrderLineWS lines[] = new OrderLineWS[1];

        OrderLineWS line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setQuantity(new Integer(1));
        line.setItemId(new Integer(1250));
        line.setUseItem(new Boolean(true));
        line.setAssetIds(new Integer[]{ret_asset_1});
        lines[0] = line;

        newOrder.setOrderLines(lines);

        System.out.println("Creating order ... " + newOrder);
        orderId_1 = api.createOrder(newOrder, OrderChangeBL.buildFromOrder(newOrder, newStatusId));
        System.out.println("Created Order ID " + orderId_1);
        
        // wait for the provisioning to be processed
        pause(2000);

        // 5. no provisioning commands; nor order lines; should be generated for the new order with the NEW order change status
        OrderWS orderWS = api.getOrder(orderId_1);
        assertEquals("Order should not have order lines yet", 0, orderWS.getOrderLines().length);

        // create a order change to modify an order line and set its status to PROCESSING
        List<OrderChangeWS> allOrderChanges = Arrays.asList(api.getOrderChanges(orderId_1));
        assertEquals("There should be only 1 order change for this order", 1, allOrderChanges.size());
        OrderChangeWS orderChangeWS = allOrderChanges.get(0);
        orderChangeWS.setUserAssignedStatusId(processingStatusId);

        api.createUpdateOrder(orderWS, new OrderChangeWS[]{orderChangeWS});
        
        int MAX_RETRIES = 20;
        int numRetries = 0;
        while (numRetries < MAX_RETRIES) {
	        pause(500);
	        // 6. provisioning commands should be generated that will create an order line
	        // via order change update
	        orderWS = api.getOrder(orderId_1);
	        try {
	        	System.out.println("::: testOrderLineProvisioning - Retry Number " + numRetries);
	        	assertEquals("Order should have only 1 order line", 1, orderWS.getOrderLines().length);
	        	break;
	        } catch (AssertionError ae) {
	        	numRetries ++;
	        	if (numRetries == MAX_RETRIES) {
	        		assertEquals("Order should have only 1 order line", 1, orderWS.getOrderLines().length);
	        	}
	        }
        }

        OrderLineWS orderLineWS = orderWS.getOrderLines()[0];
        assertNotNull("Order Line should be created", orderLineWS);
        assertEquals("Order line should be provisioned", Constants.PROVISIONING_STATUS_ACTIVE,
                orderLineWS.getProvisioningStatusId());
        allOrderChanges = Arrays.asList(api.getOrderChanges(orderId_1));
        assertEquals("There should be only 1 order change for this order", 1, allOrderChanges.size());
        orderChangeWS = allOrderChanges.get(0);

        assertEquals("Order change status should be applied", ORDER_CHANGE_STATUS_APPLY_ID,
                orderChangeWS.getStatusId().intValue());

        // 7. commands asserts
        ProvisioningCommandWS[] commands = orderLineWS.getProvisioningCommands();

        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order line", 1, commands.length);

        ProvisioningCommandWS command = commands[0];
        assertNotNull("Command does not exist", command);

        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order line command", ProvisioningCommandType.ORDER_LINE, command.getCommandType());
        assertEquals("Command name not matching", "order_change_status_provisioning_command", command.getName());

        ProvisioningRequestWS[] requests = command.getProvisioningRequests();
        Arrays.sort(requests, new Comparator<ProvisioningRequestWS>() {
            @Override
            public int compare(ProvisioningRequestWS o1, ProvisioningRequestWS o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        assertNotNull("Requests should not be null", requests);
        assertEquals("There should be 2 provisioning requests generated per command", 2, requests.length);

        ProvisioningRequestWS request1 = requests[0];
        assertNotNull("Single request should not be null", request1);
        assertEquals(1, request1.getExecutionOrder().intValue());
        assertEquals("test", request1.getProcessor());
        assertEquals("Provisioning request 1 should belong to the command", command.getId(), request1.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 1 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request1.getRequestStatus());
        assertNotNull("Request 1 should have a result received date", request1.getResultReceivedDate());
        assertFalse("Result 1 should have received a result parameter map", request1.getResultMap().isEmpty());

        ProvisioningRequestWS request2 = requests[1];
        assertNotNull("Single request should not be null", request2);
        assertEquals(2, request2.getExecutionOrder().intValue());
        assertEquals("test", request2.getProcessor());
        assertEquals("Provisioning request 2 should belong to the command", command.getId(), request2.getProvisioningCommandId().intValue());
        assertEquals("Provisioning request 2 should be successful", ProvisioningRequestStatus.SUCCESSFUL, request2.getRequestStatus());
        assertNotNull("Request 2 should have a result received date", request2.getResultReceivedDate());
        assertFalse("Result 2 should have received a result parameter map", request2.getResultMap().isEmpty());


        // 8. modify the order line and trigger provisioning
        OrderChangeWS updateOrderChange = OrderChangeBL.buildFromLine(orderLineWS, orderWS, processingStatusId);
        updateOrderChange.setQuantityAsDecimal(new BigDecimal("1.00"));
        updateOrderChange.setAssetIds(new Integer[]{ret_asset_1, 3});
        api.createUpdateOrder(orderWS, new OrderChangeWS[]{updateOrderChange});
        pause(2000);

        // 9. After the provisioning the order line should be modified
        // via the order change and provisioning command generated for the change
        orderWS = api.getOrder(orderId_1);
        assertEquals("Order should have only 1 order line", 1, orderWS.getOrderLines().length);

        orderLineWS = orderWS.getOrderLines()[0];
        assertNotNull("Order Line should be updated", orderLineWS);
        assertEquals("Order line should be provisioned", Constants.PROVISIONING_STATUS_ACTIVE,
                orderLineWS.getProvisioningStatusId());
        assertEquals("Order line quantity should be updated", new BigDecimal("2.00"), orderLineWS.getQuantityAsDecimal()
                .setScale(2, BigDecimal.ROUND_HALF_UP));

        allOrderChanges = Arrays.asList(api.getOrderChanges(orderId_1));
        assertEquals("There should be 2 order changes for this order", 2, allOrderChanges.size());
        orderChangeWS = allOrderChanges.get(1);

        assertEquals("Order change status should be applied", ORDER_CHANGE_STATUS_APPLY_ID,
                orderChangeWS.getStatusId().intValue());

        // 7. commands asserts
        commands = orderLineWS.getProvisioningCommands();
        assertNotNull("Commands should not be null", commands);
        assertEquals("There is no provisioning command generated for the order line", 2, commands.length);

        command = commands[1];
        assertNotNull("Command does not exist", command);
        assertEquals("Command status should be successful", ProvisioningCommandStatus.SUCCESSFUL,
                command.getCommandStatus());
        assertEquals("Command should be order line command", ProvisioningCommandType.ORDER_LINE, command.getCommandType());
        assertEquals("Command name not matching", "order_change_status_provisioning_command", command.getName());

        } finally {
            // 10. cleanup
            if (pluginId != null) {api.deletePlugin(pluginId);}
            if (orderId_1 != null) {api.deleteOrder(orderId_1);}
            if (ret_asset_1 != null) {api.deleteAsset(ret_asset_1);}
            if (processingStatusId != null) {api.deleteOrderChangeStatus(processingStatusId);}
            if (newStatusId != null) {api.deleteOrderChangeStatus(newStatusId);}
        }
    }


    private OrderLineWS findOrderLineWithItem(OrderLineWS[] lines, Integer itemId) {
        for (OrderLineWS line : lines) {
            if (line.getItemId().equals(itemId)) return line;
        }
        return null;
    }

    private AssetWS getAssetWS() {
        AssetWS asset = new AssetWS();
        asset.setEntityId(1);
        asset.setIdentifier("ASSET1");
        asset.setItemId(1250);
        asset.setNotes("NOTE1");
        asset.setAssetStatusId(101);
        asset.setDeleted(0);
        MetaFieldValueWS mf = new MetaFieldValueWS();
        mf.setFieldName("Tax Exemption Code");
        mf.setDataType(DataType.LIST);
        mf.setListValue(new String[] {"01", "02"});
        asset.setMetaFields(new MetaFieldValueWS[]{mf});
        return asset;
    }

    private OrderWS getOrderWS() {
        OrderWS newOrder = new OrderWS();
        newOrder.setUserId(GANDALF_USER_ID);
        newOrder.setBillingTypeId(Constants.ORDER_BILLING_POST_PAID);
        newOrder.setPeriod(ORDER_PERIOD_MONTHLY); //order with monthly plan
        newOrder.setCurrencyId(new Integer(1));
        // notes can only be 200 long... but longer should not fail
        newOrder.setNotes("At the same time the British Crown began bestowing land grants in Nova Scotia on favored subjects to encourage settlement and trade with the mother country. In June 1764, for instance, the Boards of Trade requested the King make massive land grants to such Royal favorites as Thomas Pownall, Richard Oswald, Humphry Bradstreet, John Wentworth, Thomas Thoroton[10] and Lincoln's Inn barrister Levett Blackborne.[11] Two years later, in 1766, at a gathering at the home of Levett Blackborne, an adviser to the Duke of Rutland, Oswald and his friend James Grant were released from their Nova Scotia properties so they could concentrate on their grants in British East Florida.");

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2008, Calendar.OCTOBER, 3);
        newOrder.setActiveSince(cal.getTime());

        Date now = new Date();
        cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        newOrder.setActiveUntil(cal.getTime());

        // now add some lines
        OrderLineWS lines[] = new OrderLineWS[3];
        OrderLineWS line;

        line = new OrderLineWS();
        line.setPrice(new BigDecimal("10.00"));
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setQuantity(new Integer(1));
        line.setAmount(new BigDecimal("10.00"));
        line.setDescription("Fist line");
        line.setItemId(new Integer(1));
        lines[0] = line;

        // this is an item line
        line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setQuantity(new Integer(1));
        line.setItemId(new Integer(2));
        // take the description from the item
        line.setUseItem(new Boolean(true));
        lines[1] = line;

        // this is an item line
        line = new OrderLineWS();
        line.setTypeId(Constants.ORDER_LINE_TYPE_ITEM);
        line.setQuantity(new Integer(1));
        line.setItemId(new Integer(3));
        line.setUseItem(new Boolean(true));
        lines[2] = line;

        newOrder.setOrderLines(lines);
        return newOrder;
    }
}
