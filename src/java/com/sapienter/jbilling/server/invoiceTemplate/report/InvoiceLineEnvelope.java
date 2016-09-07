package com.sapienter.jbilling.server.invoiceTemplate.report;

import com.sapienter.jbilling.server.invoice.db.InvoiceLineDTO;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.ItemTypeDTO;
import com.sapienter.jbilling.server.item.db.PlanDTO;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;
import java.util.*;

import static java.util.Map.Entry;

/**
 * @author elmot
 */
public class InvoiceLineEnvelope {

    @Field(description = "Product Code")
    private String productCode;

    @Field(description = "Category Name")
    private String productCategoryName;

    @Field(description = "Category Title")
    private String productCategoryTitle;

    @Field(description = "Plan ID", valueClass = String.class)
    private Integer planId;

    @Field(description = "Plan Title")
    private String planTitle;

    @Field(description = "Product Description")
    private final String description;

    @Field(description = "Quantity")
    private final BigDecimal quantity;

    @Field(description = "Price")
    private final BigDecimal price;

    @Field(description = "Total")
    private final BigDecimal total;

    @Field(description = "Asset Type")
    private final String assetType;

    @Field(description = "Asset IDs")
    private final String assetId;

    @Field(description = "Asset Detail")
    private final String assetDetail;

    public InvoiceLineEnvelope(InvoiceLineDTO invoiceLine, Iterable<AssetEnvelope> assets, String defaultAssetIdLabel) {
        description = invoiceLine.getDescription();
        ItemDTO item = invoiceLine.getItem();
        if (item != null) {
            productCode = item.getInternalNumber();
            PlanDTO plan = findPlan(item);
            if (plan == null) {
                planId = null;
                planTitle = "Products";
                productCategoryName = findCategory(item);
                productCategoryTitle = productCategoryName;
            } else {
                planId = plan.getId();
                planTitle = plan.getDescription();
                productCategoryName = "";
                productCategoryTitle = "Plans";
            }
        }
        quantity = invoiceLine.getQuantity();
        price = invoiceLine.getPrice();
        total = invoiceLine.getAmount();

        StringBuilder assetIdBuilder = new StringBuilder();

        Map<ItemTypeDTO, Collection<AssetEnvelope>> assetsByType = new HashMap<ItemTypeDTO, Collection<AssetEnvelope>>();

        for (Iterator<AssetEnvelope> i = assets.iterator(); i.hasNext(); ) {
            AssetEnvelope asset = i.next();
            String assetIdentifier = asset.getIdentifier();
            assetIdBuilder.append(assetIdentifier);
            if (i.hasNext()) {
                assetIdBuilder.append("\n\r");
            }
            ItemTypeDTO assetItemType = asset.getItemType();
            if (!assetsByType.containsKey(assetItemType)) {
                assetsByType.put(assetItemType, new TreeSet<AssetEnvelope>(new Comparator<AssetEnvelope>() {
                    @Override
                    public int compare(AssetEnvelope o1, AssetEnvelope o2) {
                        return o1.getIdentifier().compareTo(o2.getIdentifier());
                    }
                }));
            }
            assetsByType.get(assetItemType).add(asset);
        }

        StringBuilder assetTypeBuilder = new StringBuilder();
        StringBuilder assetDetailBuilder = new StringBuilder();

        for (Iterator<Entry<ItemTypeDTO, Collection<AssetEnvelope>>> iterator = assetsByType.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<ItemTypeDTO, Collection<AssetEnvelope>> entry = iterator.next();
            String assetType = entry.getKey().getDescription();
            String assetIdLabel = entry.getKey().getAssetIdentifierLabel();
            if (assetIdLabel == null || assetIdLabel.isEmpty()) {
                assetIdLabel = defaultAssetIdLabel;
            }
            Collection<AssetEnvelope> assetEnvelopeCollection = entry.getValue();
            assetDetailBuilder.append(assetType).append(" - ").append(assetIdLabel).append(assetEnvelopeCollection.size() > 1 ? "s" : "").append(": ");
            for (Iterator<AssetEnvelope> i = assetEnvelopeCollection.iterator(); i.hasNext(); ) {
                assetDetailBuilder.append(i.next().getIdentifier());
                if (i.hasNext()) {
                    assetDetailBuilder.append(", ");
                }
            }
            assetTypeBuilder.append(assetType);
            if (iterator.hasNext()) {
                assetTypeBuilder.append("\n\r");
                assetDetailBuilder.append("\n\r");
            }
        }

        assetId = assetIdBuilder.toString();
        assetType = assetTypeBuilder.toString();
        assetDetail = assetDetailBuilder.toString();
    }

    public boolean isSameOrBigger(BigDecimal minimalTotal) {
        return minimalTotal == null || total == null || total.subtract(minimalTotal).signum() >= 0;
    }

    private String strVal(Number bigDecimal) {
        return bigDecimal == null ? null : String.valueOf(bigDecimal);
    }

    private String findCategory(ItemDTO item) {
        Set<ItemTypeDTO> itemTypes = item.getItemTypes();
        for (ItemTypeDTO itemType : itemTypes) {
            if (!itemType.isInternal()) {
                return itemType.getDescription();
            }
        }
        return null;
    }

    private PlanDTO findPlan(ItemDTO item) {
        Set<PlanDTO> plans = item.getPlans();
        if (plans != null && !plans.isEmpty()) {
            return plans.iterator().next();
        }
        return null;
    }


    public String getProductCode() {
        return productCode;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getProductCategoryTitle() {
        return productCategoryTitle;
    }

    public Integer getPlanId() {
        return planId;
    }

    public String getPlanTitle() {
        return planTitle;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getAssetDetail() {
        return assetDetail;
    }
}
