package com.sapienter.jbilling.test.framework;

import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.test.framework.builders.*;

/**
 * Created by marcolin on 06/11/15.
 */
public class TestEnvironmentBuilder extends AbstractTestEnvironment {

    private TestEnvironment testEnvironment;

    TestEnvironmentBuilder(TestEnvironment testEnvironment) {
        this.testEnvironment = testEnvironment;
    }

    public AccountTypeBuilder accountTypeBuilder(JbillingAPI api) {
        return AccountTypeBuilder.getBuilder(api, testEnvironment);
    }

    public ConfigurationBuilder configurationBuilder(JbillingAPI api) {
        return ConfigurationBuilder.getBuilder(api, testEnvironment);
    }

    public CustomerBuilder customerBuilder(JbillingAPI api) {
        return CustomerBuilder.getBuilder(api, testEnvironment);
    }

    public ItemBuilder itemBuilder(JbillingAPI api) {
        return ItemBuilder.getBuilder(api, testEnvironment);
    }

    public MediationConfigBuilder mediationConfigBuilder(JbillingAPI api) {
        return MediationConfigBuilder.getBuilder(api, testEnvironment);
    }

    public OrderBuilder orderBuilder(JbillingAPI api) {
        return OrderBuilder.getBuilder(api, testEnvironment);
    }

    public OrderChangeStatusBuilder orderChangeStatusBuilder(JbillingAPI api) {
        return OrderChangeStatusBuilder.getBuilder(api, testEnvironment);
    }

    public OrderPeriodBuilder orderPeriodBuilder(JbillingAPI api) {
        return OrderPeriodBuilder.getBuilder(api, testEnvironment);
    }

    public PaymentMethodTypeBuilder paymentMethodTypeBuilder(JbillingAPI api, String code) {
        return PaymentMethodTypeBuilder.getBuilder(api, testEnvironment, code);
    }

    public UsagePoolBuilder usagePoolBuilder(JbillingAPI api, String code) {
        return UsagePoolBuilder.getBuilder(api, testEnvironment, code);
    }

    public PlanBuilder planBuilder(JbillingAPI api, String code) {
        return PlanBuilder.getBuilder(api, testEnvironment, code);
    }

    public DiscountBuilder discountBuilder(JbillingAPI api) {
        return DiscountBuilder.getBuilder(api, testEnvironment);
    }

    public AssetBuilder assetBuilder(JbillingAPI api) {
        return AssetBuilder.getBuilder(api, testEnvironment);
    }

    public TestEnvironment env() {
        return testEnvironment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestEnvironmentBuilder that = (TestEnvironmentBuilder) o;

        return !(testEnvironment != null ? !testEnvironment.equals(that.testEnvironment) : that.testEnvironment != null);

    }

    @Override
    public int hashCode() {
        return testEnvironment != null ? testEnvironment.hashCode() : 0;
    }
}
