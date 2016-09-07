package com.sapienter.jbilling.test.framework.builders;

import com.sapienter.jbilling.server.metafields.DataType;
import com.sapienter.jbilling.server.metafields.EntityType;
import com.sapienter.jbilling.server.metafields.MetaFieldWS;
import com.sapienter.jbilling.server.user.AccountInformationTypeWS;
import com.sapienter.jbilling.server.user.AccountTypeWS;
import com.sapienter.jbilling.server.user.MainSubscriptionWS;
import com.sapienter.jbilling.server.util.InternationalDescriptionWS;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.test.framework.TestEnvironment;
import com.sapienter.jbilling.test.framework.helpers.ApiBuilderHelper;
import com.sapienter.jbilling.test.framework.TestEntityType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by marcomanzicore on 26/11/15.
 */
public class AccountTypeBuilder extends AbstractBuilder {

    private String description;
    private String invoiceDesign;
    private String creditLimit;
    private String creditNotificationLimit1;
    private String creditNotificationLimit2;
    private Integer[] paymentMethodTypeIds;
    private Integer entityId;
    private MainSubscriptionWS mainSubscription;
    public static final Integer MONTHLY_PERIOD = Integer.valueOf(2);//fixed constant for now
    public static final Integer DEFAULT_INVOICE_DELIVERY_METHOD = Integer.valueOf(1);
    private Map<String, List<MetaFieldWS>> accountInformationTypeName = new HashMap<>();
    private List<MetaFieldWS> accountInformationTypeMetaFields = new ArrayList<>();

    private AccountTypeBuilder(JbillingAPI api, TestEnvironment testEnvironment) {
        super(api, testEnvironment);
    }

    public static AccountTypeBuilder getBuilder(JbillingAPI api, TestEnvironment testEnvironment) {
        return new AccountTypeBuilder(api, testEnvironment);
    }

    public AccountTypeBuilder withName(String description) {
        this.description = description;
        return this;
    }

    public AccountTypeBuilder withInvoiceDesign(String invoiceDesign){
        this.invoiceDesign = invoiceDesign;
        return this;
    }

    public AccountTypeBuilder withCreditLimit(String creditLimit){
        this.creditLimit = creditLimit;
        return this;
    }

    public AccountTypeBuilder withCreditNotificationLimit1(String creditNotificationLimit1){
        this.creditNotificationLimit1 = creditNotificationLimit1;
        return this;
    }

    public AccountTypeBuilder withCreditNotificationLimit2(String creditNotificationLimit2){
        this.creditNotificationLimit2 = creditNotificationLimit2;
        return this;
    }

    public AccountTypeBuilder withPaymentMethodTypeIds(Integer[] paymentMethodTypeIds){
        this.paymentMethodTypeIds = paymentMethodTypeIds;
        return this;
    }

    public AccountTypeBuilder withEntityId(Integer entityId){
        this.entityId = entityId;
        return this;
    }

    public AccountTypeBuilder addAccountInformationType(String accountInformationTypeName, HashMap<String, DataType> informationTypeMetaFields) {
        this.accountInformationTypeName.put(accountInformationTypeName, informationTypeMetaFields.entrySet().stream().map(entry ->
                ApiBuilderHelper.getMetaFieldWS(entry.getKey(), entry.getValue(), EntityType.ACCOUNT_TYPE, api.getCallerCompanyId()))
                .collect(Collectors.toList()));
        return this;
    }

    public AccountTypeBuilder withMainSubscription(Integer periodId, Integer dayOfPeriod){
        this.mainSubscription = new MainSubscriptionWS(periodId, dayOfPeriod);
        return this;
    }

    public AccountTypeWS build() {
        AccountTypeWS accountType = new AccountTypeWS();
        accountType.setEntityId(api.getCallerCompanyId());
        accountType.setLanguageId(api.getCallerLanguageId());
        accountType.setCurrencyId(api.getCallerCurrencyId());
        accountType.setEntityId(entityId);
        accountType.setInvoiceDeliveryMethodId(DEFAULT_INVOICE_DELIVERY_METHOD);
        accountType.setMainSubscription(null == mainSubscription ? new MainSubscriptionWS(MONTHLY_PERIOD, 1)
                : mainSubscription);
        accountType.setDescriptions(createDescriptions());
        accountType.setInvoiceDesign(invoiceDesign);
        accountType.setCreditLimit(creditLimit);
        accountType.setCreditNotificationLimit1(creditNotificationLimit1);
        accountType.setCreditNotificationLimit2(creditNotificationLimit2);
        accountType.setPaymentMethodTypeIds(paymentMethodTypeIds);
        Integer accountTypeId = api.createAccountType(accountType);
        AccountTypeWS accountTypeCreated = api.getAccountType(accountTypeId);
        testEnvironment.add(description, accountTypeId, accountTypeCreated.getDescription(accountType.getLanguageId()).getContent(), api, TestEntityType.ACCOUNT_TYPE);

        accountInformationTypeName.forEach((accountInformationTypeName, metaFields) ->  {
            if (metaFields.size() == 0) throw new IllegalArgumentException("Account type with Information type should have at least one metafield");
            createInformationType(accountTypeCreated, accountInformationTypeName, metaFields);
        });

        return accountTypeCreated;
    }

    private void createInformationType(AccountTypeWS accountTypeCreated, String accountInformationTypeName, List<MetaFieldWS> metaFields) {
        AccountInformationTypeWS accountInformationTypes = new AccountInformationTypeWS();
        accountInformationTypes.setEntityId(api.getCallerCompanyId());
        accountInformationTypes.setName(accountInformationTypeName);
        accountInformationTypes.setAccountTypeId(accountTypeCreated.getId());
        accountInformationTypes.setMetaFields(metaFields.toArray(new MetaFieldWS[0]));
        Integer accountInformationTypeId = api.createAccountInformationType(accountInformationTypes);

        testEnvironment.add(accountInformationTypeName, accountInformationTypeId, accountInformationTypeName, api,
                TestEntityType.ACCOUNT_INFORMATION_TYPE);
        accountTypeCreated.setInformationTypeIds(Arrays.asList(accountInformationTypeId).toArray(new Integer[0]));
        api.updateAccountType(accountTypeCreated);
    }

    private List<InternationalDescriptionWS> createDescriptions() {
        Integer apiLanguageId = api.getCallerLanguageId();
        return Arrays.asList(new InternationalDescriptionWS(apiLanguageId, description + System.currentTimeMillis()));
    }
}
