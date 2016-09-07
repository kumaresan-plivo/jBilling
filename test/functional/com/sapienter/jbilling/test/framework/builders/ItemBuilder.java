package com.sapienter.jbilling.test.framework.builders;

import com.sapienter.jbilling.server.item.*;
import com.sapienter.jbilling.server.metafields.MetaFieldWS;
import com.sapienter.jbilling.server.pricing.PriceModelWS;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.api.JbillingAPI;
import com.sapienter.jbilling.test.framework.TestEnvironment;
import com.sapienter.jbilling.test.framework.TestEntityType;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Arrays;

/**
 * Created by marcolin on 06/11/15.
 */
public class ItemBuilder extends AbstractBuilder {

    private ItemBuilder(JbillingAPI api, TestEnvironment testEnvironment) {
        super(api, testEnvironment);
    }

    public static ItemBuilder getBuilder(JbillingAPI api, TestEnvironment testEnvironment) {
        return new ItemBuilder(api, testEnvironment);
    }

    public ItemTypeBuilder itemType() {
        return new ItemTypeBuilder();
    }

    public ProductBuilder item() {
        return new ProductBuilder();
    }

    public ItemDependencyBuilder itemDependency(){return new ItemDependencyBuilder();}

    public class ItemTypeBuilder {
        private String code;
        private boolean global = false;
        private Integer allowAssetManagement = 0;
        private Integer[] entities;
        private boolean onePerOrder = false;
        private boolean onePerCustomer = false;

        public ItemTypeBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public ItemTypeBuilder global(boolean global){
            this.global = global;
            return this;
        }

        public ItemTypeBuilder withOnePerOrder(boolean onePerOrder){
            this.onePerOrder = onePerOrder;
            return this;
        }

        public ItemTypeBuilder withOnePerCustomer(boolean onePerCustomer){
            this.onePerCustomer = onePerCustomer;
            return this;
        }

        public ItemTypeBuilder allowAssetManagement(Integer allowAssetManagement){
            this.allowAssetManagement = allowAssetManagement;
            return this;
        }

        public ItemTypeBuilder withEntities(Integer... entities){

            this.entities = entities;
            return this;
        }

        public Integer build() {
            ItemTypeWS itemType = new ItemTypeWS();
            itemType.setDescription("TestCategory: " + System.currentTimeMillis());
            itemType.setEntityId(api.getCallerCompanyId());
            itemType.setEntities(null == entities ? Arrays.asList(api.getCallerCompanyId()) : Arrays.asList(entities));
            itemType.setOrderLineTypeId(Constants.ORDER_LINE_TYPE_ITEM);
            itemType.setGlobal(global);
            itemType.setOnePerOrder(onePerOrder);
            itemType.setOnePerCustomer(onePerCustomer);
            itemType.setAllowAssetManagement(allowAssetManagement);
            if(allowAssetManagement ==  1){

                Set<AssetStatusDTOEx> assetStatusDTOExes = new HashSet<>();
                AssetStatusDTOEx assetStatusDTOEx  = new AssetStatusDTOEx();
                assetStatusDTOEx.setDescription("Available");
                assetStatusDTOEx.setIsDefault(Integer.valueOf(1));
                assetStatusDTOEx.setIsOrderSaved(Integer.valueOf(0));
                assetStatusDTOEx.setIsAvailable(Integer.valueOf(1));
                assetStatusDTOExes.add(assetStatusDTOEx);

                assetStatusDTOEx = new AssetStatusDTOEx();
                assetStatusDTOEx.setDescription("InOrder");
                assetStatusDTOEx.setIsDefault(Integer.valueOf(0));
                assetStatusDTOEx.setIsOrderSaved(Integer.valueOf(1));
                assetStatusDTOEx.setIsAvailable(Integer.valueOf(0));
                assetStatusDTOExes.add(assetStatusDTOEx);
                itemType.setAssetStatuses(assetStatusDTOExes);
            }

            Integer itemCategory = api.createItemCategory(itemType);
            testEnvironment.add(code, itemCategory, itemType.getDescription(), api, TestEntityType.PRODUCT_CATEGORY);
            return itemCategory;
        }
    }

    public class ProductBuilder {
        private String code;
        private List<Integer> types = new ArrayList<>();
        private SortedMap<Date, PriceModelWS> prices = new TreeMap<>();
        private boolean global = false;
        private Integer assetManagementEnabled = Integer.valueOf(0);
        private Integer[] entities;
        private ItemDependencyDTOEx[] dependencies;
        private Date activeSince;
        private Date activeUntil;
        private Integer companyId;
        private List<MetaFieldWS> orderLineMetaFields = new ArrayList<>();

        public ProductBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public ProductBuilder withType(Integer type) {
            this.types.add(type);
            return this;
        }

        public ProductBuilder withActiveSince (Date activeSince) {
            this.activeSince = activeSince;
            return this;
        }

        public ProductBuilder withActiveUntil (Date activeUntil) {
            this.activeUntil = activeUntil;
            return this;
        }

        public ProductBuilder withFlatPrice(String flatPrice) {
            prices.put(new Date(),
                    new PriceModelWS(PriceModelStrategy.FLAT.name(),
                            new BigDecimal(flatPrice), api.getCallerCurrencyId()));
            return this;
        }

        public ProductBuilder withChainPrice (String flatPrice) {

            PriceModelWS flatPrices = new PriceModelWS();
            PriceModelWS chainedPrice = new PriceModelWS();

            chainedPrice.setCurrencyId(api.getCallerCurrencyId());
            chainedPrice.setType(PriceModelStrategy.PERCENTAGE.name());
            chainedPrice.setRate("0.10");
            chainedPrice.addAttribute("percentage", "0.10");

            flatPrices.setCurrencyId(api.getCallerCurrencyId());
            flatPrices.setType(PriceModelStrategy.FLAT.name());
            flatPrices.setRate(flatPrice);
            flatPrices.setNext(chainedPrice);
            prices.put(new Date(), flatPrices);
            return this;
        }

        public ProductBuilder withGraduatedPrice(String graduatedPrice , String includedUnits){

            PriceModelWS graduatedPrices = new PriceModelWS();
            graduatedPrices.setCurrencyId(api.getCallerCurrencyId());
            graduatedPrices.setType(PriceModelStrategy.GRADUATED.name());
            graduatedPrices.setRate(graduatedPrice);
            graduatedPrices.addAttribute("included", includedUnits);
            prices.put(new Date(), graduatedPrices);
            return this;
        }

        public ProductBuilder withPooledPrice (String pooledPrice , String poolingItemId, String multiplier) {

            PriceModelWS pooledPrices = new PriceModelWS();
            pooledPrices.setCurrencyId(api.getCallerCurrencyId());
            pooledPrices.setType(PriceModelStrategy.POOLED.name());
            pooledPrices.setRate(pooledPrice);
            pooledPrices.addAttribute("pool_item_id", poolingItemId);
            pooledPrices.addAttribute("multiplier", multiplier);
            prices.put(new Date(), pooledPrices);
            return this;
        }

        public ProductBuilder withCompanyPooledPrice (String pooledPrice , String poolItemCategoryId, String includedQuantity) {

            PriceModelWS pooledPrices = new PriceModelWS();
            pooledPrices.setCurrencyId(api.getCallerCurrencyId());
            pooledPrices.setType(PriceModelStrategy.COMPANY_POOLED.name());
            pooledPrices.setRate(pooledPrice);
            pooledPrices.addAttribute("pool_item_category_id", poolItemCategoryId);
            pooledPrices.addAttribute("included_quantity", includedQuantity);
            prices.put(new Date(), pooledPrices);
            return this;
        }

        public ProductBuilder global(boolean global){
            this.global = global;
            return this;
        }

        public ProductBuilder withAssetManagementEnabled(Integer assetManagementEnabled){
            this.assetManagementEnabled = assetManagementEnabled;
            return this;
        }

        public ProductBuilder withCompany (Integer companyId) {
            this.companyId = companyId;
            return this;
        }

        public ProductBuilder withPriceModel(PriceModelWS priceModelWS){

            this.prices.put(new Date(),priceModelWS);
            return  this;
        }

        public ProductBuilder withDependencies(ItemDependencyDTOEx... itemDependencyDTOExes){
            this.dependencies = itemDependencyDTOExes;
            return this;
        }

        public ProductBuilder withEntities(Integer... entities){
            this.entities = entities;
            return this;
        }

        public ProductBuilder addOrderLineMetaField(MetaFieldWS metaField){
            this.orderLineMetaFields.add(metaField);
            return this;
        }

        public ProductBuilder withOrderLineMetaFields(List<MetaFieldWS> orderLineMetaFields){
            this.orderLineMetaFields = orderLineMetaFields;
            return this;
        }

        public Integer build() {
            ItemDTOEx item = new ItemDTOEx();
            item.setDescription("TestItem: " + System.currentTimeMillis());
            item.setNumber("TestItem-" + System.currentTimeMillis());
            item.setTypes(types.toArray(new Integer[types.size()]));
            item.setExcludedTypes(new Integer[0]);
            item.setActiveSince(activeSince);
            item.setActiveUntil(activeUntil);
            item.setGlobal(global);
            item.setEntityId(companyId);
            item.setDeleted(0);
            item.setAssetManagementEnabled(assetManagementEnabled);
            item.setEntities(null == entities ? Arrays.asList(api.getCallerCompanyId()) : Arrays.asList(entities));
            item.setEntityId(api.getCallerCompanyId());
            if (prices.isEmpty()) {
                withFlatPrice("0.0");
            }
            item.setDefaultPrices(prices);
            List<Integer> entities = new ArrayList<>();
            entities.add(api.getCallerCompanyId());
            item.setEntities(entities);
            item.setDependencies(this.dependencies);
            item.setOrderLineMetaFields(orderLineMetaFields.toArray(new MetaFieldWS[orderLineMetaFields.size()]));
            Integer itemId = api.createItem(item);
            testEnvironment.add(code, itemId, item.getNumber(),  api, TestEntityType.PRODUCT);
            return itemId;
        }
    }

    public class ItemDependencyBuilder {

        private Integer itemId;
        private Integer dependentId;
        private Integer minimum;
        private Integer maximum;
        private ItemDependencyType itemDependencyType;


        public ItemDependencyBuilder withItemId(Integer itemId){
            this.itemId = itemId;
            return this;
        }

        public ItemDependencyBuilder withDependentId(Integer dependentId){
            this.dependentId = dependentId;
            return this;
        }

        public ItemDependencyBuilder withMaximum(Integer maximum){
            this.maximum = maximum;
            return this;
        }

        public ItemDependencyBuilder withMinimum(Integer minimum ){
            this.minimum= minimum;
            return this;
        }

        public ItemDependencyBuilder withItemDependencyType (ItemDependencyType itemDependencyType){
            this.itemDependencyType = itemDependencyType;
            return this;
        }

        public ItemDependencyDTOEx build(){

            ItemDependencyDTOEx itemDependencyDTOEx = new ItemDependencyDTOEx();
            itemDependencyDTOEx.setItemId(this.itemId);
            itemDependencyDTOEx.setType(this.itemDependencyType);
            itemDependencyDTOEx.setDependentId(this.dependentId);
            itemDependencyDTOEx.setMaximum(this.maximum);
            itemDependencyDTOEx.setMinimum(this.minimum);

            return itemDependencyDTOEx;
        }
    }
}