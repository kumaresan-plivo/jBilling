package com.sapienter.jbilling.api.automation.mediation;

import com.sapienter.jbilling.api.automation.EnvironmentHelper;
import com.sapienter.jbilling.api.automation.utils.FileHelper;
import com.sapienter.jbilling.common.Util;
import com.sapienter.jbilling.server.mediation.JbillingMediationErrorRecord;
import com.sapienter.jbilling.server.mediation.JbillingMediationRecord;
import com.sapienter.jbilling.server.mediation.MediationProcess;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.test.framework.TestBuilder;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.Arrays;
import java.util.Random;

import static com.sapienter.jbilling.server.mediation.JbillingMediationRecord.STATUS;
import static com.sapienter.jbilling.server.mediation.JbillingMediationRecord.TYPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Vojislav Stanojevikj
 * @since 05-Jul-2016.
 */
@Test(groups = {"api-automation"})
public class MediationTest {

    private static final String MEDIATION_CONFIG_NAME = "Test Mediation 4.0";
    private static final String MEDIATION_CONFIG_NAME_GLOBAL = "Test Global Mediation 4.0";
    private static final String MEDIATION_JOB_LAUNCHER_NAME = "sampleMediationJob";
    private static final String TEST_CATEGORY_CODE = "testCategory";
    private static final String TEST_CATEGORY_CODE_GLOBAL = "testGlobalCategory";
    private static final String TEST_ITEM_CODE = "testItem";
    private static final String TEST_ITEM_CODE_GLOBAL = "testGlobalItem";
    private static final String CUSTOMER_CODE = "testCustomer";
    private static final String CUSTOMER_PARENT_CODE = "testParentCustomer";
    private static final String CUSTOMER_CHILD_CODE = "testChildCustomer";
    private static final String ACCOUNT_TYPE_CHILD_CODE = "testChildAccountType";
    private static final String CUSTOMER_ERROR_RECORD_CODE = "JB-USER-NOT-RESOLVED";
    private static final String ITEM_ERROR_RECORD_CODE = "ERR-ITEM_NOT-FOUND";

    private EnvironmentHelper environmentHelper;
    private TestBuilder mediationConfigBuilder;
    private TestBuilder mediationConfigGlobalBuilder;
    private final Random ID_GEN = new Random(Integer.MAX_VALUE - 1);

    @BeforeClass
    public void init(){
        mediationConfigBuilder = getMediationSetup();
        JbillingAPI parentApi = mediationConfigBuilder.getTestEnvironment().getPrancingPonyApi();
        JbillingAPI childApi = mediationConfigBuilder.getTestEnvironment().getResellerApi();
        environmentHelper = EnvironmentHelper.getInstance(parentApi, childApi);
        mediationConfigGlobalBuilder = getGlobalMediationSetup();
    }

    @AfterClass
    public void tearDown(){
        mediationConfigBuilder.removeEntitiesCreatedOnJBillingForMultipleTests();
        mediationConfigGlobalBuilder.removeEntitiesCreatedOnJBillingForMultipleTests();
        if (null != mediationConfigBuilder){
            mediationConfigBuilder = null;
        }
        if (null != mediationConfigGlobalBuilder){
            mediationConfigGlobalBuilder = null;
        }
        if (null != environmentHelper){
            environmentHelper = null;
        }
    }

    private TestBuilder getMediationSetup(){
        return TestBuilder.newTest().givenForMultiple(envBuilder -> {
            final JbillingAPI api = envBuilder.getPrancingPonyApi();
            envBuilder.mediationConfigBuilder(api)
                    .withName(MEDIATION_CONFIG_NAME).withLauncher(MEDIATION_JOB_LAUNCHER_NAME).build();

            envBuilder.itemBuilder(api).item()
                    .withType(
                            envBuilder.itemBuilder(api).itemType()
                                    .withCode(TEST_CATEGORY_CODE).build()
                    )
                    .withCode(TEST_ITEM_CODE).withFlatPrice("10").build();

            envBuilder.customerBuilder(api).addTimeToUsername(false).withUsername(CUSTOMER_CODE).build();
        });
    }

    private TestBuilder getGlobalMediationSetup(){
        return TestBuilder.newTest().givenForMultiple(envBuilder -> {
            final JbillingAPI api = envBuilder.getPrancingPonyApi();
            final JbillingAPI childApi = envBuilder.getResellerApi();
            envBuilder.mediationConfigBuilder(api)
                    .withName(MEDIATION_CONFIG_NAME_GLOBAL).withLauncher(MEDIATION_JOB_LAUNCHER_NAME).global(true).build();

            envBuilder.itemBuilder(api).item()
                    .withType(
                            envBuilder.itemBuilder(api).itemType()
                                    .withCode(TEST_CATEGORY_CODE_GLOBAL).global(true).build()
                    )
                    .withCode(TEST_ITEM_CODE_GLOBAL).global(true).withFlatPrice("10").build();

            envBuilder.customerBuilder(childApi).addTimeToUsername(false).withUsername(CUSTOMER_CHILD_CODE)
                    .withAccountTypeId(
                            envBuilder.accountTypeBuilder(childApi)
                                    .withName(ACCOUNT_TYPE_CHILD_CODE)
                                    .withCreditLimit("10000")
                                    .withMainSubscription(environmentHelper.getOrderPeriodMonth(childApi), 1)
                                    .withEntityId(childApi.getCallerCompanyId())
                                    .build().getId()
                    ).build();
            envBuilder.customerBuilder(api).addTimeToUsername(false).withUsername(CUSTOMER_PARENT_CODE).build();
        });
    }

    private TestBuilder addItemToInitialSetup(String itemCode, String flatPrice){
        return mediationConfigBuilder.given(envBuilder -> {
            final JbillingAPI api = envBuilder.getPrancingPonyApi();
            envBuilder.itemBuilder(api).item()
                    .withType(envBuilder.env().idForCode(TEST_CATEGORY_CODE))
                    .withCode(itemCode).withFlatPrice(flatPrice).build();
        });
    }

    @Test
    public void testSingleCustomerMediationRun(){
        final String[] cdr = new String[1];
        final Date eventDate = new DateTime().withTimeAtStartOfDay().toDate();
        mediationConfigBuilder.given(envBuilder -> cdr[0] = buildCDR(ID_GEN.nextInt(), "11111", "22222", Integer.valueOf(100),
                envBuilder.env().idForCode(TEST_ITEM_CODE), CUSTOMER_CODE, eventDate))
                .test(testEnvironment -> {

                    final JbillingAPI api = testEnvironment.getPrancingPonyApi();
                    final Integer customerId = testEnvironment.idForCode(CUSTOMER_CODE);
                    final Integer configId = testEnvironment.idForCode(MEDIATION_CONFIG_NAME);
                    final Integer itemId = testEnvironment.idForCode(TEST_ITEM_CODE);
                    UUID processId = api.processCDR(configId, Arrays.asList(cdr));
                    validateMediationProcess(api.getMediationProcess(processId), 1, 1, 0, 0, configId, eventDate);

                    OrderWS currentOrder = api.getLatestOrder(customerId);
                    validateCurrentOrder(currentOrder, environmentHelper.getOrderPeriodOneTime(api), customerId, eventDate,
                            BigDecimal.valueOf(1000), Integer.valueOf(1));

                    OrderLineWS orderLine = currentOrder.getOrderLines()[0];
                    validateOrderLine(orderLine, itemId, BigDecimal.valueOf(100),
                            BigDecimal.valueOf(1000));

                    JbillingMediationRecord[] records = api.getMediationRecordsByMediationProcess(processId, Integer.valueOf(0),
                            Integer.valueOf(100), eventDate, new DateTime(eventDate).plusMonths(1).toDate());
                    assertNotNull(records, "records expected!");
                    assertEquals(Integer.valueOf(records.length), Integer.valueOf(1), "Invalid number of records!");
                    validateMediationRecord(records[0], STATUS.PROCESSED, eventDate, itemId,
                            customerId, currentOrder.getId(), orderLine.getId());

                    api.undoMediation(processId);
                });
    }

    @Test
    public void testSingleCustomerMediationRunFromFile(){
        final Date eventDate = new DateTime().withTimeAtStartOfDay().toDate();
        final File file = new File("./resources/mediation/api-test.csv");
        mediationConfigBuilder.given(envBuilder ->
                FileHelper.write(file.getPath(), buildCDR(ID_GEN.nextInt(), "11111", "22222", Integer.valueOf(100),
                        envBuilder.env().idForCode(TEST_ITEM_CODE), CUSTOMER_CODE, eventDate)))
                .test(testEnvironment -> {

                    final JbillingAPI api = testEnvironment.getPrancingPonyApi();
                    final Integer customerId = testEnvironment.idForCode(CUSTOMER_CODE);
                    final Integer configId = testEnvironment.idForCode(MEDIATION_CONFIG_NAME);
                    final Integer itemId = testEnvironment.idForCode(TEST_ITEM_CODE);

                    UUID processId = api.launchMediation(configId, MEDIATION_JOB_LAUNCHER_NAME, file);
                    validateMediationProcess(api.getMediationProcess(processId), 1, 1, 0, 0, configId, eventDate);

                    OrderWS currentOrder = api.getLatestOrder(customerId);
                    validateCurrentOrder(currentOrder, environmentHelper.getOrderPeriodOneTime(api), customerId, eventDate,
                            BigDecimal.valueOf(1000), Integer.valueOf(1));

                    OrderLineWS orderLine = currentOrder.getOrderLines()[0];
                    validateOrderLine(orderLine, itemId, BigDecimal.valueOf(100),
                            BigDecimal.valueOf(1000));

                    JbillingMediationRecord[] records = api.getMediationRecordsByMediationProcess(processId, Integer.valueOf(0),
                            Integer.valueOf(100), eventDate, new DateTime(eventDate).plusMonths(1).toDate());
                    assertNotNull(records, "records expected!");
                    assertEquals(Integer.valueOf(records.length), Integer.valueOf(1), "Invalid number of records!");
                    validateMediationRecord(records[0], STATUS.PROCESSED, eventDate, itemId,
                            customerId, currentOrder.getId(), orderLine.getId());

                    api.undoMediation(processId);
                    FileHelper.deleteFile(file.getPath());
                });
    }

    @Test
    public void testMultipleCustomersMediationRun(){
        final String userName2 = "secondCustomer";
        final String[] cdr = new String[2];
        final Date eventDate = new DateTime(2016, 1, 31, 0, 0).toDate();
        final Date eventDate2 = new DateTime(2016, 2, 14, 0, 0).toDate();
        mediationConfigBuilder.given(envBuilder -> {
            final JbillingAPI api = envBuilder.getPrancingPonyApi();
            envBuilder.customerBuilder(api).addTimeToUsername(false).withUsername(userName2).build();
            final Integer itemId = envBuilder.env().idForCode(TEST_ITEM_CODE);
            cdr[0] = buildCDR(ID_GEN.nextInt(), "11111", "22222", Integer.valueOf(100), itemId, CUSTOMER_CODE, eventDate);
            cdr[1] = buildCDR(ID_GEN.nextInt(), "33333", "44444", Integer.valueOf(150), itemId, userName2, eventDate2);
        }).test(testEnvironment -> {

            final JbillingAPI api = testEnvironment.getPrancingPonyApi();
            final Integer customerId = testEnvironment.idForCode(CUSTOMER_CODE);
            final Integer customerId2 = testEnvironment.idForCode(userName2);
            final Integer configId = testEnvironment.idForCode(MEDIATION_CONFIG_NAME);
            final Integer itemId = testEnvironment.idForCode(TEST_ITEM_CODE);
            UUID processId = api.processCDR(configId, Arrays.asList(cdr));
            validateMediationProcess(api.getMediationProcess(processId), 2, 2, 0, 0, configId, Util.truncateDate(new Date()));

            OrderWS currentOrder = api.getLatestOrder(customerId);
            validateCurrentOrder(currentOrder, environmentHelper.getOrderPeriodOneTime(api), customerId, eventDate,
                    BigDecimal.valueOf(1000), Integer.valueOf(1));

            OrderLineWS orderLine = currentOrder.getOrderLines()[0];
            validateOrderLine(orderLine, itemId, BigDecimal.valueOf(100),
                    BigDecimal.valueOf(1000));

            OrderWS currentOrder2 = api.getLatestOrder(customerId2);
            validateCurrentOrder(currentOrder2, environmentHelper.getOrderPeriodOneTime(api), customerId2, eventDate2,
                    BigDecimal.valueOf(1500), Integer.valueOf(1));

            OrderLineWS orderLine2 = currentOrder2.getOrderLines()[0];
            validateOrderLine(orderLine2, itemId, BigDecimal.valueOf(150),
                    BigDecimal.valueOf(1500));

            JbillingMediationRecord[] records = api.getMediationRecordsByMediationProcess(processId, Integer.valueOf(0),
                    Integer.valueOf(100), eventDate, new DateTime(eventDate).plusMonths(1).toDate());
            assertNotNull(records, "records expected!");
            assertEquals(Integer.valueOf(records.length), Integer.valueOf(2), "Invalid number of records!");
            Arrays.sort(records, (o1, o2) -> o1.getEventDate().compareTo(o2.getEventDate()));
            validateMediationRecord(records[0], STATUS.PROCESSED, eventDate, itemId, customerId,
                    currentOrder.getId(), orderLine.getId());
            validateMediationRecord(records[1], STATUS.PROCESSED, eventDate2, itemId, customerId2,
                    currentOrder2.getId(), orderLine2.getId());

            api.undoMediation(processId);
        });
    }

    @Test
    public void testSingleCustomerDuplicatesAndErrorsMediationRun(){

        final String[] cdr = new String[4];
        final Date eventDate = new DateTime().withTimeAtStartOfDay().toDate();
        mediationConfigBuilder.given(envBuilder -> {
            final Integer itemId = envBuilder.env().idForCode(TEST_ITEM_CODE);
            Integer id = ID_GEN.nextInt();
            cdr[0] = buildCDR(id, "11111", "22222", Integer.valueOf(100), itemId, CUSTOMER_CODE, eventDate);
            cdr[1] = buildCDR(id, "11111", "22222", Integer.valueOf(100), itemId, CUSTOMER_CODE, eventDate);
            cdr[2] = buildCDR(ID_GEN.nextInt(), "33333", "44444", Integer.valueOf(100), itemId, "No Man's land", eventDate);
            cdr[3] = buildCDR(ID_GEN.nextInt(), "55555", "66666", Integer.valueOf(100), Integer.MAX_VALUE, CUSTOMER_CODE, eventDate);
        }).test(testEnvironment -> {

                    final JbillingAPI api = testEnvironment.getPrancingPonyApi();
                    final Integer configId = testEnvironment.idForCode(MEDIATION_CONFIG_NAME);
                    UUID processId = api.processCDR(configId, Arrays.asList(cdr));
                    validateMediationProcess(api.getMediationProcess(processId), 3, 1, 2, 1, configId, eventDate);
                    JbillingMediationErrorRecord[] errorRecords = api.getMediationErrorRecordsByMediationProcess(processId, null);
                    assertNotNull(errorRecords, "Error records expected!");
                    assertEquals(Integer.valueOf(errorRecords.length), Integer.valueOf(2));
                    validateErrorRecord(errorRecords[0], CUSTOMER_ERROR_RECORD_CODE, configId);
                    validateErrorRecord(errorRecords[1], ITEM_ERROR_RECORD_CODE, configId);
                    api.undoMediation(processId);
        });
    }

    @Test
    public void testUndoSingleMediationRun(){

        final String[] cdr = new String[1];
        final Date eventDate = new DateTime().withTimeAtStartOfDay().toDate();
        final UUID[] processId = new UUID[1];
        mediationConfigBuilder.given(envBuilder -> {
            final JbillingAPI api = envBuilder.getPrancingPonyApi();
            final Integer itemId = envBuilder.env().idForCode(TEST_ITEM_CODE);
            cdr[0] = buildCDR(ID_GEN.nextInt(), "11111", "22222", Integer.valueOf(100), itemId, CUSTOMER_CODE, eventDate);
            processId[0] = api.processCDR(envBuilder.env().idForCode(MEDIATION_CONFIG_NAME), Arrays.asList(cdr));
        }).test(testEnvironment -> {
                    final JbillingAPI api = testEnvironment.getPrancingPonyApi();
                    api.undoMediation(processId[0]);

                    MediationProcess mediationProcess = api.getMediationProcess(processId[0]);
                    assertNull(mediationProcess, "Mediation process not expected!");

                    OrderWS currentOrder = api.getLatestOrder(testEnvironment.idForCode(CUSTOMER_CODE));
                    assertNull(currentOrder, "No orders expected!");

                    JbillingMediationRecord[] records = api.getMediationRecordsByMediationProcess(processId[0], Integer.valueOf(0), Integer.valueOf(100),
                            eventDate, null);

                    assertNotNull(records, "Empty array expected!");
                    assertEquals(Integer.valueOf(records.length), Integer.valueOf(0), "Invalid number of records!");
        });
    }

    @Test
    public void testUndoSpecificMediationRun(){

        final String testItem2 = "testItem2";
        final String[] cdr = new String[2];
        final Date eventDate = new DateTime().withTimeAtStartOfDay().toDate();
        final UUID[] processId = new UUID[2];
        addItemToInitialSetup(testItem2, "5").given(envBuilder -> {
            final JbillingAPI api = envBuilder.getPrancingPonyApi();
            cdr[0] = buildCDR(ID_GEN.nextInt(), "11111", "22222", Integer.valueOf(100),
                    envBuilder.env().idForCode(TEST_ITEM_CODE), CUSTOMER_CODE, eventDate);
            cdr[1] = buildCDR(ID_GEN.nextInt(), "33333", "44444", Integer.valueOf(20), envBuilder.env().idForCode(testItem2),
                    CUSTOMER_CODE, eventDate);
            processId[0] = api.processCDR(envBuilder.env().idForCode(MEDIATION_CONFIG_NAME), Arrays.asList(cdr[0]));
            processId[1] = api.processCDR(envBuilder.env().idForCode(MEDIATION_CONFIG_NAME), Arrays.asList(cdr[1]));
        }).test(testEnvironment -> {
                    final JbillingAPI api = testEnvironment.getPrancingPonyApi();
                    final Integer customerId = testEnvironment.idForCode(CUSTOMER_CODE);
                    OrderWS currentOrder = api.getLatestOrder(customerId);
                    validateCurrentOrder(currentOrder, environmentHelper.getOrderPeriodOneTime(api), customerId,
                            eventDate, BigDecimal.valueOf(1100), Integer.valueOf(2));
                    OrderLineWS[] orderLines = currentOrder.getOrderLines();
                    Arrays.sort(orderLines, (o1, o2) -> o1.getAmountAsDecimal().compareTo(o2.getAmountAsDecimal()));
                    validateOrderLine(orderLines[0], testEnvironment.idForCode(testItem2), BigDecimal.valueOf(20),
                            BigDecimal.valueOf(100));
                    validateOrderLine(orderLines[1], testEnvironment.idForCode(TEST_ITEM_CODE), BigDecimal.valueOf(100),
                            BigDecimal.valueOf(1000));

                    JbillingMediationRecord[] records = api.getMediationRecordsByMediationProcess(processId[0], Integer.valueOf(0),
                            Integer.valueOf(100), eventDate, null);
                    assertEquals(Integer.valueOf(records.length), Integer.valueOf(1), "Invalid number of records!");

                    records = api.getMediationRecordsByMediationProcess(processId[1], Integer.valueOf(0),
                            Integer.valueOf(100), eventDate, null);
                    assertEquals(Integer.valueOf(records.length), Integer.valueOf(1), "Invalid number of records!");

                    api.undoMediation(processId[1]);

                    currentOrder = api.getLatestOrder(customerId);
                    validateCurrentOrder(currentOrder, environmentHelper.getOrderPeriodOneTime(api), customerId,
                            eventDate, BigDecimal.valueOf(1000), Integer.valueOf(1));
                    orderLines = currentOrder.getOrderLines();
                    validateOrderLine(orderLines[0], testEnvironment.idForCode(TEST_ITEM_CODE), BigDecimal.valueOf(100),
                            BigDecimal.valueOf(1000));

                    records = api.getMediationRecordsByMediationProcess(processId[0], Integer.valueOf(0),
                            Integer.valueOf(100), eventDate, null);
                    assertEquals(Integer.valueOf(records.length), Integer.valueOf(1), "Invalid number of records!");

                    records = api.getMediationRecordsByMediationProcess(processId[1], Integer.valueOf(0),
                            Integer.valueOf(100), eventDate, null);
                    assertEquals(Integer.valueOf(records.length), Integer.valueOf(0), "Invalid number of records!");

                    api.undoMediation(processId[0]);
        });
    }

    @Test
    public void testUndoRedoMediationRun(){

        final String testItem2 = "testItem2";
        final String[] cdr = new String[2];
        final Date eventDate = new DateTime().withTimeAtStartOfDay().toDate();
        final UUID[] processId = new UUID[2];
        addItemToInitialSetup(testItem2, "5").given(envBuilder -> {
            final JbillingAPI api = envBuilder.getPrancingPonyApi();
            cdr[0] = buildCDR(ID_GEN.nextInt(), "11111", "22222", Integer.valueOf(100),
                    envBuilder.env().idForCode(TEST_ITEM_CODE), CUSTOMER_CODE, eventDate);
            cdr[1] = buildCDR(ID_GEN.nextInt(), "33333", "44444", Integer.valueOf(20), envBuilder.env().idForCode(testItem2),
                    CUSTOMER_CODE, eventDate);
            processId[0] = api.processCDR(envBuilder.env().idForCode(MEDIATION_CONFIG_NAME), Arrays.asList(cdr[0]));
            processId[1] = api.processCDR(envBuilder.env().idForCode(MEDIATION_CONFIG_NAME), Arrays.asList(cdr[1]));
        }).test(testEnvironment -> {
            final JbillingAPI api = testEnvironment.getPrancingPonyApi();
            final Integer customerId = testEnvironment.idForCode(CUSTOMER_CODE);
            OrderWS currentOrder = api.getLatestOrder(customerId);
            validateCurrentOrder(currentOrder, environmentHelper.getOrderPeriodOneTime(api), customerId,
                    eventDate, BigDecimal.valueOf(1100), Integer.valueOf(2));
            OrderLineWS[] orderLines = currentOrder.getOrderLines();
            Arrays.sort(orderLines, (o1, o2) -> o1.getAmountAsDecimal().compareTo(o2.getAmountAsDecimal()));
            validateOrderLine(orderLines[0], testEnvironment.idForCode(testItem2), BigDecimal.valueOf(20),
                    BigDecimal.valueOf(100));
            validateOrderLine(orderLines[1], testEnvironment.idForCode(TEST_ITEM_CODE), BigDecimal.valueOf(100),
                    BigDecimal.valueOf(1000));

            JbillingMediationRecord[] records = api.getMediationRecordsByMediationProcess(processId[0], Integer.valueOf(0),
                    Integer.valueOf(100), eventDate, null);
            assertEquals(Integer.valueOf(records.length), Integer.valueOf(1), "Invalid number of records!");

            records = api.getMediationRecordsByMediationProcess(processId[1], Integer.valueOf(0),
                    Integer.valueOf(100), eventDate, null);
            assertEquals(Integer.valueOf(records.length), Integer.valueOf(1), "Invalid number of records!");

            api.undoMediation(processId[1]);

            currentOrder = api.getLatestOrder(customerId);
            validateCurrentOrder(currentOrder, environmentHelper.getOrderPeriodOneTime(api), customerId,
                    eventDate, BigDecimal.valueOf(1000), Integer.valueOf(1));
            orderLines = currentOrder.getOrderLines();
            validateOrderLine(orderLines[0], testEnvironment.idForCode(TEST_ITEM_CODE), BigDecimal.valueOf(100),
                    BigDecimal.valueOf(1000));

            records = api.getMediationRecordsByMediationProcess(processId[0], Integer.valueOf(0),
                    Integer.valueOf(100), eventDate, null);
            assertEquals(Integer.valueOf(records.length), Integer.valueOf(1), "Invalid number of records!");

            records = api.getMediationRecordsByMediationProcess(processId[1], Integer.valueOf(0),
                    Integer.valueOf(100), eventDate, null);
            assertEquals(Integer.valueOf(records.length), Integer.valueOf(0), "Invalid number of records!");

            processId[1] = api.processCDR(testEnvironment.idForCode(MEDIATION_CONFIG_NAME), Arrays.asList(cdr[1]));

            currentOrder = api.getLatestOrder(customerId);
            validateCurrentOrder(currentOrder, environmentHelper.getOrderPeriodOneTime(api), customerId,
                    eventDate, BigDecimal.valueOf(1100), Integer.valueOf(2));
            orderLines = currentOrder.getOrderLines();
            Arrays.sort(orderLines, (o1, o2) -> o1.getAmountAsDecimal().compareTo(o2.getAmountAsDecimal()));
            validateOrderLine(orderLines[0], testEnvironment.idForCode(testItem2), BigDecimal.valueOf(20),
                    BigDecimal.valueOf(100));
            validateOrderLine(orderLines[1], testEnvironment.idForCode(TEST_ITEM_CODE), BigDecimal.valueOf(100),
                    BigDecimal.valueOf(1000));

            records = api.getMediationRecordsByMediationProcess(processId[0], Integer.valueOf(0),
                    Integer.valueOf(100), eventDate, null);
            assertEquals(Integer.valueOf(records.length), Integer.valueOf(1), "Invalid number of records!");

            records = api.getMediationRecordsByMediationProcess(processId[1], Integer.valueOf(0),
                    Integer.valueOf(100), eventDate, null);
            assertEquals(Integer.valueOf(records.length), Integer.valueOf(1), "Invalid number of records!");

            api.undoMediation(processId[0]);
            api.undoMediation(processId[1]);
        });
    }

    @Test
    public void testRecycleCustomerError() {

        final String recycleCustomer = "recycleCustomer";
        final String[] cdr = new String[1];
        final Date eventDate = new DateTime().withTimeAtStartOfDay().toDate();
        final UUID[] processId = new UUID[2];
        mediationConfigBuilder.given(envBuilder -> {
            final JbillingAPI api = envBuilder.getPrancingPonyApi();
            cdr[0] = buildCDR(ID_GEN.nextInt(), "11111", "22222", Integer.valueOf(100),
                    envBuilder.env().idForCode(TEST_ITEM_CODE), recycleCustomer, eventDate);
            processId[0] = api.processCDR(envBuilder.env().idForCode(MEDIATION_CONFIG_NAME), Arrays.asList(cdr));
        }).test((testEnvironment, envBuilder) -> {

            final JbillingAPI api = testEnvironment.getPrancingPonyApi();
            final Integer configId = testEnvironment.idForCode(MEDIATION_CONFIG_NAME);

            validateMediationProcess(api.getMediationProcess(processId[0]), 1, 0, 1, 0, configId, eventDate);

            JbillingMediationErrorRecord[] errorRecords = api.getMediationErrorRecordsByMediationProcess(processId[0], null);
            assertEquals(Integer.valueOf(errorRecords.length), Integer.valueOf(1), "Invalid error records!");
            validateErrorRecord(errorRecords[0], CUSTOMER_ERROR_RECORD_CODE, configId);

            envBuilder.customerBuilder(api).addTimeToUsername(false).withUsername(recycleCustomer).build();

            processId[1] = api.runRecycleForProcess(processId[0]);

            validateMediationProcess(api.getMediationProcess(processId[1]), 1, 1, 0, 0, configId, eventDate);

            api.undoMediation(processId[1]);
        });
    }

    @Test
    public void testRecycleItemError() {

        final String[] cdr = new String[1];
        final Date eventDate = new DateTime().withTimeAtStartOfDay().toDate();
        final UUID[] processId = new UUID[2];
        mediationConfigBuilder.given(envBuilder -> {
            final JbillingAPI api = envBuilder.getPrancingPonyApi();
            Integer dummyItemId = envBuilder.itemBuilder(api).item().withType(envBuilder.env().idForCode(TEST_CATEGORY_CODE))
                    .withCode("dummy").withFlatPrice("0").build();
            cdr[0] = buildCDR(ID_GEN.nextInt(), "11111", "22222", Integer.valueOf(100), dummyItemId + 1, CUSTOMER_CODE, eventDate);
            processId[0] = api.processCDR(envBuilder.env().idForCode(MEDIATION_CONFIG_NAME), Arrays.asList(cdr));
        }).test((testEnvironment, envBuilder) -> {

            final JbillingAPI api = testEnvironment.getPrancingPonyApi();
            final Integer configId = testEnvironment.idForCode(MEDIATION_CONFIG_NAME);

            validateMediationProcess(api.getMediationProcess(processId[0]), 1, 0, 1, 0, configId, eventDate);

            JbillingMediationErrorRecord[] errorRecords = api.getMediationErrorRecordsByMediationProcess(processId[0], null);
            assertEquals(Integer.valueOf(errorRecords.length), Integer.valueOf(1), "Invalid error records!");
            validateErrorRecord(errorRecords[0], ITEM_ERROR_RECORD_CODE, configId);

            envBuilder.itemBuilder(api).item().withType(testEnvironment.idForCode(TEST_CATEGORY_CODE)).withCode("testItem2")
                    .withFlatPrice("10").build();

            processId[1] = api.runRecycleForProcess(processId[0]);

            validateMediationProcess(api.getMediationProcess(processId[1]), 1, 1, 0, 0, configId, eventDate);

            api.undoMediation(processId[1]);
        });
    }

    @Test
    public void testGlobalMediationForGlobalProductTwoCustomers(){
        final String[] cdr = new String[2];
        final Date eventDate = new DateTime(2016, 1, 31, 0, 0).toDate();
        final Date eventDate2 = new DateTime(2016, 2, 14, 0, 0).toDate();
        mediationConfigGlobalBuilder.given(envBuilder -> {
            final Integer itemId = envBuilder.env().idForCode(TEST_ITEM_CODE_GLOBAL);
            cdr[0] = buildCDR(ID_GEN.nextInt(), "11111", "22222", Integer.valueOf(100), itemId, CUSTOMER_CHILD_CODE, eventDate);
            cdr[1] = buildCDR(ID_GEN.nextInt(), "33333", "44444", Integer.valueOf(150), itemId, CUSTOMER_PARENT_CODE, eventDate2);
        }).test(testEnvironment -> {

            final JbillingAPI api = testEnvironment.getPrancingPonyApi();
            final JbillingAPI childApi = testEnvironment.getResellerApi();
            final Integer childCustomerId = testEnvironment.idForCode(CUSTOMER_CHILD_CODE);
            final Integer parentCustomerId = testEnvironment.idForCode(CUSTOMER_PARENT_CODE);
            final Integer configId = testEnvironment.idForCode(MEDIATION_CONFIG_NAME_GLOBAL);
            final Integer itemId = testEnvironment.idForCode(TEST_ITEM_CODE_GLOBAL);
            UUID processId = api.processCDR(configId, Arrays.asList(cdr));
            validateMediationProcess(api.getMediationProcess(processId), 2, 2, 0, 0, configId, Util.truncateDate(new Date()));

            OrderWS currentOrder = childApi.getLatestOrder(childCustomerId);
            validateCurrentOrder(currentOrder, environmentHelper.getOrderPeriodOneTime(childApi), childCustomerId, eventDate,
                    BigDecimal.valueOf(1000), Integer.valueOf(1));

            OrderLineWS orderLine = currentOrder.getOrderLines()[0];
            validateOrderLine(orderLine, itemId, BigDecimal.valueOf(100),
                    BigDecimal.valueOf(1000));

            OrderWS currentOrder2 = api.getLatestOrder(parentCustomerId);
            validateCurrentOrder(currentOrder2, environmentHelper.getOrderPeriodOneTime(api), parentCustomerId, eventDate2,
                    BigDecimal.valueOf(1500), Integer.valueOf(1));

            OrderLineWS orderLine2 = currentOrder2.getOrderLines()[0];
            validateOrderLine(orderLine2, itemId, BigDecimal.valueOf(150),
                    BigDecimal.valueOf(1500));

            JbillingMediationRecord[] records = api.getMediationRecordsByMediationProcess(processId, Integer.valueOf(0),
                    Integer.valueOf(100), eventDate, new DateTime(eventDate).plusMonths(1).toDate());
            assertNotNull(records, "records expected!");
            assertEquals(Integer.valueOf(records.length), Integer.valueOf(2), "Invalid number of records!");
            Arrays.sort(records, (o1, o2) -> o1.getEventDate().compareTo(o2.getEventDate()));
            validateMediationRecord(records[0], STATUS.PROCESSED, eventDate, itemId, childCustomerId,
                    currentOrder.getId(), orderLine.getId());
            validateMediationRecord(records[1], STATUS.PROCESSED, eventDate2, itemId, parentCustomerId,
                    currentOrder2.getId(), orderLine2.getId());

            api.undoMediation(processId);
        });
    }

    @Test
    public void testGlobalMediationForGlobalProductUndo(){
        final String[] cdr = new String[2];
        final UUID[] processId = new UUID[1];
        final Date eventDate = new DateTime(2016, 1, 31, 0, 0).toDate();
        final Date eventDate2 = new DateTime(2016, 2, 14, 0, 0).toDate();
        mediationConfigGlobalBuilder.given(envBuilder -> {
            final JbillingAPI api = envBuilder.getPrancingPonyApi();
            final Integer itemId = envBuilder.env().idForCode(TEST_ITEM_CODE_GLOBAL);
            cdr[0] = buildCDR(ID_GEN.nextInt(), "11111", "22222", Integer.valueOf(100), itemId, CUSTOMER_CHILD_CODE, eventDate);
            cdr[1] = buildCDR(ID_GEN.nextInt(), "33333", "44444", Integer.valueOf(150), itemId, CUSTOMER_PARENT_CODE, eventDate2);
            processId[0] = api.processCDR(envBuilder.env().idForCode(MEDIATION_CONFIG_NAME_GLOBAL), Arrays.asList(cdr));
        }).test(testEnvironment -> {

            final JbillingAPI api = testEnvironment.getPrancingPonyApi();
            final JbillingAPI childApi = testEnvironment.getResellerApi();
            api.undoMediation(processId[0]);

            MediationProcess mediationProcess = api.getMediationProcess(processId[0]);
            assertNull(mediationProcess, "Mediation process not expected!");
            mediationProcess = childApi.getMediationProcess(processId[0]);
            assertNull(mediationProcess, "Mediation process not expected!");

            OrderWS currentOrder = api.getLatestOrder(testEnvironment.idForCode(CUSTOMER_PARENT_CODE));
            assertNull(currentOrder, "No orders expected!");
            currentOrder = childApi.getLatestOrder(testEnvironment.idForCode(CUSTOMER_CHILD_CODE));
            assertNull(currentOrder, "No orders expected!");

            JbillingMediationRecord[] records = api.getMediationRecordsByMediationProcess(processId[0], Integer.valueOf(0), Integer.valueOf(100),
                    eventDate, null);

            assertNotNull(records, "Empty array expected!");
            assertEquals(Integer.valueOf(records.length), Integer.valueOf(0), "Invalid number of records!");

            records = childApi.getMediationRecordsByMediationProcess(processId[0], Integer.valueOf(0), Integer.valueOf(100),
                    eventDate2, null);

            assertNotNull(records, "Empty array expected!");
            assertEquals(Integer.valueOf(records.length), Integer.valueOf(0), "Invalid number of records!");

        });
    }

    @Test
    public void testMediationInActionFromFile(){
        final Date eventDate = new DateTime().withTimeAtStartOfDay().toDate();
        final File file = new File("./resources/mediation/api-test.csv");
        final String recycleCustomer = "recycleCustomer";
        final UUID[] processId = new UUID[2];
        final Integer[] nextItemId = {null};
        mediationConfigBuilder.given(envBuilder ->{
                final JbillingAPI api = envBuilder.getPrancingPonyApi();
                final Integer itemId = envBuilder.env().idForCode(TEST_ITEM_CODE);
                final Integer configId = envBuilder.env().idForCode(MEDIATION_CONFIG_NAME);
                nextItemId[0] = envBuilder.itemBuilder(api).item().withType(envBuilder.env().idForCode(TEST_CATEGORY_CODE))
                    .withCode("dummy").withFlatPrice("0").build() + 1;
                Integer id = ID_GEN.nextInt();
                // Duplicates
                FileHelper.write(file.getPath(),
                        buildCDR(id, "11111", "22222", Integer.valueOf(100), itemId, CUSTOMER_CODE, eventDate),
                        buildCDR(id, "11111", "22222", Integer.valueOf(100), itemId, CUSTOMER_CODE, eventDate),
                        buildCDR(ID_GEN.nextInt(), "33333", "44444", Integer.valueOf(100), itemId, recycleCustomer, eventDate),
                        buildCDR(ID_GEN.nextInt(), "55555", "66666", Integer.valueOf(100), nextItemId[0], CUSTOMER_CODE, eventDate));
                processId[0] = api.launchMediation(configId, MEDIATION_JOB_LAUNCHER_NAME, file);
        }).test((testEnvironment, envBuilder) -> {

            final JbillingAPI api = testEnvironment.getPrancingPonyApi();
            final Integer configId = testEnvironment.idForCode(MEDIATION_CONFIG_NAME);
            validateMediationProcess(api.getMediationProcess(processId[0]), 3, 1, 2, 1, configId, eventDate);
            JbillingMediationErrorRecord[] errorRecords = api.getMediationErrorRecordsByMediationProcess(processId[0], null);
            assertNotNull(errorRecords, "Error records expected!");
            assertEquals(Integer.valueOf(errorRecords.length), Integer.valueOf(2));
            validateErrorRecord(errorRecords[0], CUSTOMER_ERROR_RECORD_CODE, configId);
            validateErrorRecord(errorRecords[1], ITEM_ERROR_RECORD_CODE, configId);

            api.undoMediation(processId[0]);

            final Integer itemId = testEnvironment.idForCode(TEST_ITEM_CODE);
            // Fix the duplicates
            FileHelper.deleteFile(file.getPath());

            FileHelper.write(file.getPath(),
                    buildCDR(ID_GEN.nextInt(), "11111", "22222", Integer.valueOf(100), itemId, CUSTOMER_CODE, eventDate),
                    buildCDR(ID_GEN.nextInt(), "22222", "33333", Integer.valueOf(100), itemId, CUSTOMER_CODE, eventDate),
                    buildCDR(ID_GEN.nextInt(), "33333", "44444", Integer.valueOf(100), itemId, recycleCustomer, eventDate),
                    buildCDR(ID_GEN.nextInt(), "55555", "66666", Integer.valueOf(100), nextItemId[0], CUSTOMER_CODE, eventDate));

            processId[0] = api.launchMediation(configId, MEDIATION_JOB_LAUNCHER_NAME, file);
            validateMediationProcess(api.getMediationProcess(processId[0]), 4, 2, 2, 0, configId, eventDate);

            // Fix errors
            envBuilder.customerBuilder(api).addTimeToUsername(false).withUsername(recycleCustomer).build();
            envBuilder.itemBuilder(api).item().withType(testEnvironment.idForCode(TEST_CATEGORY_CODE)).withFlatPrice("1").withCode("recycleItem").build();

            processId[1] = api.runRecycleForProcess(processId[0]);

            validateMediationProcess(api.getMediationProcess(processId[1]), 2, 2, 0, 0, configId, eventDate);

            api.undoMediation(processId[0]);
            api.undoMediation(processId[1]);
            FileHelper.deleteFile(file.getPath());
        });
    }

    private String buildCDR(Integer id, String aNumber, String bNumber, Integer duration,
                            Integer itemId, String username, Date eventDate) {
        return StringUtils.join(Arrays.asList(id, aNumber, bNumber,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(eventDate), duration, itemId, username), ",");
    }

    private void validateMediationProcess(MediationProcess mediationProcess, int recordsProcessed,
                                          int doneAndBillable, int errors, int duplicates,
                                          Integer configId, Date eventDate){

        assertNotNull(mediationProcess, "Mediation process expected!");
        assertEquals(mediationProcess.getRecordsProcessed(), Integer.valueOf(recordsProcessed), "Invalid number of processed records!");
        assertEquals(mediationProcess.getDoneAndBillable(), Integer.valueOf(doneAndBillable), "Invalid number of done and billable records!");
        assertEquals(mediationProcess.getErrors(), Integer.valueOf(errors), "Invalid number of error records!");
        assertEquals(mediationProcess.getDuplicates(), Integer.valueOf(duplicates), "Invalid number of error records!");
        assertEquals(Util.truncateDate(mediationProcess.getStartDate()), eventDate, "Invalid event date!");
        assertEquals(mediationProcess.getConfigurationId(), configId, "Invalid config id!");
    }

    private void validateMediationRecord(JbillingMediationRecord record, STATUS status,
                                         Date eventDate, Integer itemId, Integer userId,
                                         Integer orderId, Integer orderLineId){

        assertNotNull(record, "Record can not be null!");
        assertEquals(record.getStatus(), status, "Invalid status!!");
        assertEquals(record.getType(), TYPE.MEDIATION, "Invalid type!!");
        assertEquals(Util.truncateDate(record.getEventDate()), eventDate, "Invalid event date!!");
        assertEquals(record.getItemId(), itemId, "Invalid item id!!");
        assertEquals(record.getUserId(), userId, "Invalid user id!!");
        assertEquals(record.getOrderId(), orderId, "Invalid order id!!");
        assertEquals(record.getOrderLineId(), orderLineId, "Invalid order line id!!");
    }

    private void validateErrorRecord(JbillingMediationErrorRecord errorRecord, String errorCode,
                                     Integer mediationConfigId){

        assertNotNull(errorRecord, "Error record expected!");
        assertTrue(errorRecord.getErrorCodes().contains(errorCode), "Invalid error code!");
        assertEquals(errorRecord.getMediationCfgId(), mediationConfigId, "Invalid config id!");

    }

    private void validateCurrentOrder(OrderWS order, Integer periodId, Integer userId,
                                      Date eventDate, BigDecimal total, Integer orderLinesCount){

        assertNotNull(order, "Order can not be null!");
        assertTrue(order.getNotes().contains("Current order created by mediation process. Do not edit manually."));
        assertEquals(order.getPeriod(), periodId, "Invalid Period!");
        assertEquals(order.getUserId(), userId, "Invalid customer id!!");
        assertEquals(order.getActiveSince(), new DateTime(eventDate).withDayOfMonth(1).toDate(), "Invalid active since!!");
        assertEquals(order.getTotalAsDecimal().setScale(2, BigDecimal.ROUND_CEILING),
                total.setScale(2, BigDecimal.ROUND_CEILING), "Invalid total!!");

        if (null != orderLinesCount){
            OrderLineWS[] orderLines = order.getOrderLines();
            assertNotNull(orderLines, "Order lines expected!");
            assertEquals(Integer.valueOf(orderLines.length), orderLinesCount, "Invalid number of order lines!");
        }
    }

    private void validateOrderLine(OrderLineWS orderLine, Integer itemId, BigDecimal quantity,
                                   BigDecimal total){

        assertNotNull(orderLine, "Order line expected!");
        assertEquals(orderLine.getItemId(), itemId, "Invalid item id!");
        assertEquals(orderLine.getQuantityAsDecimal().setScale(2, BigDecimal.ROUND_CEILING),
                quantity.setScale(2, BigDecimal.ROUND_CEILING), "Invalid quantity!");
        assertEquals(orderLine.getAmountAsDecimal().setScale(2, BigDecimal.ROUND_CEILING),
                total.setScale(2, BigDecimal.ROUND_CEILING), "Invalid total amount!");

    }

}
