package com.sapienter.jbilling.server.mediation;/*
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

import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.test.framework.TestBuilder;
import com.sapienter.jbilling.test.framework.TestEnvironment;
import com.sapienter.jbilling.test.framework.TestEnvironmentBuilder;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
* Created by marcomanzi on 5/30/14.
*/
@Test(groups = {"integration", "mediation"})
public class SampleMediationTests  {

    @Test
    public void testTriggerGlobalMediationWithOneValidCDR() {
        TestBuilder.
                newTest().given(environmentCreator -> {
            JbillingAPI api = environmentCreator.getPrancingPonyApi();
            environmentCreator.customerBuilder(api).withUsername("testCustomer").build();
            Integer testCategoryId = environmentCreator.itemBuilder(api).itemType().withCode("testCategory").build();
            environmentCreator.itemBuilder(api).item().withType(testCategoryId).withCode("testItem").build();
            environmentCreator.mediationConfigBuilder(api).withName("TestConfiguration")
                    .withLauncher("sampleMediationJob").build();
        }).test(environment -> {
            JbillingAPI api = environment.getPrancingPonyApi();
            String callDataRecord = validCallDataRecord(environment);
            UUID processId = api.processCDR(environment.idForCode("TestConfiguration"), Arrays.asList(callDataRecord));
            MediationProcess mediationProcess = api.getMediationProcess(processId);
            assertEquals(new Integer(1), mediationProcess.getDoneAndBillable());
        });
    }

    @Test
    public void testTriggerGlobalMediationWithOneUnvalidCDR() {
        TestBuilder.
                newTest().given(environmentCreator -> {
            JbillingAPI api = environmentCreator.getPrancingPonyApi();
            environmentCreator.mediationConfigBuilder(api).withName("TestConfiguration")
                    .withLauncher("sampleMediationJob").build();
        }).test(environment -> {
            JbillingAPI api = environment.getPrancingPonyApi();
            //No customer or item are created, this mediation will fail
            String callDataRecord = validCallDataRecord(environment);
            UUID processId = api.processCDR(environment.idForCode("TestConfiguration"), Arrays.asList(callDataRecord));
            MediationProcess mediationProcess = api.getMediationProcess(processId);
            assertEquals(new Integer(1), mediationProcess.getErrors());
        });
    }

    @Test
    public void testOrderCreationWithMediationForFlatItem() {
        TestBuilder.
                newTest().given(environmentCreator -> {
            defaultEnvironmentForSempleMediationTest(environmentCreator);
        }).test(environment -> {
            JbillingAPI api = environment.getPrancingPonyApi();
            String callDataRecord = validCallDataRecord(environment);
            api.processCDR(environment.idForCode("TestConfiguration"), Arrays.asList(callDataRecord));
            checkLastTestCustomerOrder(environment, api, 10, 100);
        });
    }

    private void checkLastTestCustomerOrder(TestEnvironment environment, JbillingAPI api, int quantity, int amount) {
        Integer[] lastOrders = api.getLastOrders(environment.idForCode("testCustomer"), 1);
        assertEquals(1, lastOrders.length);
        OrderWS order = api.getOrder(lastOrders[0]);
        assertEquals(new BigDecimal(quantity).setScale(2), order.getOrderLines()[0].getQuantityAsDecimal().setScale(2));
        assertEquals(new BigDecimal(amount).setScale(2), order.getTotalAsDecimal().setScale(2));
    }

    private void defaultEnvironmentForSempleMediationTest(TestEnvironmentBuilder environmentCreator) {
        defaultEnvironmentForSempleMediationTest(environmentCreator, true);
    }

    private void defaultEnvironmentForSempleMediationTest(TestEnvironmentBuilder environmentCreator, boolean customerCreation) {
        JbillingAPI api = environmentCreator.getPrancingPonyApi();
        if (customerCreation) environmentCreator.customerBuilder(api).withUsername("testCustomer").build();
        Integer testCategoryId = environmentCreator.itemBuilder(api).itemType().withCode("testCategory").build();
        environmentCreator.itemBuilder(api).item().withType(testCategoryId).withCode("testItem").withFlatPrice("10").build();
        environmentCreator.mediationConfigBuilder(api).withName("TestConfiguration")
                .withLauncher("sampleMediationJob").build();
    }

    @Test
    public void testMultipleProcessUpdateRightlyOrders() {
        TestBuilder.
                newTest().given(environmentCreator -> {
            defaultEnvironmentForSempleMediationTest(environmentCreator);
        }).test(environment -> {
            JbillingAPI api = environment.getPrancingPonyApi();
            api.processCDR(environment.idForCode("TestConfiguration"), Arrays.asList(validCallDataRecord(environment)));
            api.processCDR(environment.idForCode("TestConfiguration"), Arrays.asList(validCallDataRecord(environment)));
            checkLastTestCustomerOrder(environment, api, 20, 200);
        });
    }

    @Test
    public void testUndoProcessUpdateRightlyOrders() {
        TestBuilder.
                newTest().given(environmentCreator ->
                        defaultEnvironmentForSempleMediationTest(environmentCreator)
        ).test(environment -> {
            JbillingAPI api = environment.getPrancingPonyApi();
            UUID firstProcess = api.processCDR(environment.idForCode("TestConfiguration"), Arrays.asList(validCallDataRecord(environment)));
            UUID secondProcess = api.processCDR(environment.idForCode("TestConfiguration"), Arrays.asList(validCallDataRecord(environment)));
            checkLastTestCustomerOrder(environment, api, 20, 200);
            api.undoMediation(secondProcess);
            checkLastTestCustomerOrder(environment, api, 10, 100);
            api.undoMediation(firstProcess);
            Integer[] lastOrders = api.getLastOrders(environment.idForCode("testCustomer"), 1);
            assertEquals(0, lastOrders.length);
        });
    }

    @Test
    public void testRecycleCdrAfterCustomerCreationForCallDataRecord() {
        TestBuilder.
                newTest().given(environmentCreator -> {
            boolean customerCreation = false;
            defaultEnvironmentForSempleMediationTest(environmentCreator, customerCreation);
        }).test((environment, environmentCreator) -> {
            JbillingAPI api = environment.getPrancingPonyApi();
            UUID firstProcess = api.processCDR(environment.idForCode("TestConfiguration"),
                    Arrays.asList(validCallDataRecordWithUsername(environment, "testCustomer")));
            assertEquals(new Integer(1), api.getMediationProcess(firstProcess).getErrors());
            environmentCreator.customerBuilder(api).withUsername("testCustomer").addTimeToUsername(false).build();
            UUID recycledProcess = api.runRecycleForProcess(firstProcess);
            assertEquals(new Integer(1), api.getMediationProcess(recycledProcess).getDoneAndBillable());
            checkLastTestCustomerOrder(environment, api, 10, 100);
        });
    }

//  Partitioned Mediation can't be used through the WebServiceSessionSpringBean because messages are not retrieved
    @Test
    public void testPartitionedMediationWithOneValidCDR() {
        TestBuilder.
                newTest().given(environmentCreator -> {
            JbillingAPI api = environmentCreator.getPrancingPonyApi();
            environmentCreator.customerBuilder(api).withUsername("testCustomer").build();
            Integer testCategoryId = environmentCreator.itemBuilder(api).itemType().withCode("testCategory").build();
            environmentCreator.itemBuilder(api).item().withType(testCategoryId).withCode("testItem").build();
            environmentCreator.mediationConfigBuilder(api).withName("TestConfiguration")
                    .withLauncher("partitionedSampleMediationJob").build();
        }).test(environment -> {
            JbillingAPI api = environment.getPrancingPonyApi();
            String callDataRecord = validCallDataRecord(environment);
            UUID processId = api.processCDR(environment.idForCode("TestConfiguration"), Arrays.asList(callDataRecord));
            MediationProcess mediationProcess = api.getMediationProcess(processId);
            assertEquals(new Integer(1), mediationProcess.getDoneAndBillable());
        });
    }

    private String validCallDataRecord(TestEnvironment environment) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String aNumber = "1234";
        String bNumber = "5789";
        return StringUtils.join(Arrays.asList(
                "testId" + System.currentTimeMillis(),
                aNumber,
                bNumber,
                format.format(new Date()),
                10,
                environment.idForCode("testItem"),
                environment.jBillingCode("testCustomer")
        ), ",");
    }

    private String validCallDataRecordWithUsername(TestEnvironment environment, String username) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String aNumber = "1234";
        String bNumber = "5789";
        return StringUtils.join(Arrays.asList(
                "testId" + System.currentTimeMillis(),
                aNumber,
                bNumber,
                format.format(new Date()),
                10,
                environment.idForCode("testItem"),
                username
        ), ",");
    }


    private String unvalidCallDataRecord(TestEnvironment environment) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String aNumber = "1234";
        String bNumber = "5789";
        return StringUtils.join(Arrays.asList(
                "testId" + System.currentTimeMillis(),
                aNumber,
                bNumber,
                format.format(new Date()),
                10,
                environment.idForCode("testItem"),
                "failure-username"
        ), ",");
    }



}
