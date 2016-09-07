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

package com.sapienter.jbilling.server.pricing.tasks;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.ItemBL;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.PlanItemDTO;
import com.sapienter.jbilling.server.item.tasks.IPricing;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.order.OrderBL;
import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.order.UsageBL;
import com.sapienter.jbilling.server.order.db.OrderDTO;
import com.sapienter.jbilling.server.order.db.OrderLineDTO;
import com.sapienter.jbilling.server.pluggableTask.PluggableTask;
import com.sapienter.jbilling.server.pluggableTask.TaskException;
import com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription;
import com.sapienter.jbilling.server.pricing.PriceModelBL;
import com.sapienter.jbilling.server.pricing.db.PriceModelDTO;
import com.sapienter.jbilling.server.pricing.db.PriceModelStrategy;
import com.sapienter.jbilling.server.user.CustomerPriceBL;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.AccountTypePriceBL;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.pricing.util.AttributeUtils;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

import static com.sapienter.jbilling.server.pluggableTask.admin.ParameterDescription.Type.*;

/**
 * Pricing plug-in that calculates prices using the customer price map and PriceModelDTO
 * pricing strategies. This plug-in allows for complex pricing strategies to be applied
 * based on a customers subscribed plans, quantity purchased and the current usage.
 *
 * @author Brian Cowdery
 * @since 16-08-2010
 */
public class PriceModelPricingTask extends PluggableTask implements IPricing {

    /**
     * Type of usage calculation
     */
    private enum UsageType {
        /** Count usage from the user making the pricing request */
        USER,

        /** Count usage from the user that holds the price */
        PRICE_HOLDER;

        public static UsageType valueOfIgnoreCase(String value) {
            return UsageType.valueOf(value.trim().toUpperCase());
        }
    }

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PriceModelPricingTask.class));

    private static final Integer MAX_RESULTS = 1;

    private static ParameterDescription USE_ATTRIBUTES = new ParameterDescription("use_attributes", false, BOOLEAN);
    private static ParameterDescription USE_WILDCARDS = new ParameterDescription("use_wildcards", false, BOOLEAN);
    private static ParameterDescription USAGE_TYPE = new ParameterDescription("usage_type", false, STR);
    private static ParameterDescription SUB_ACCOUNT_USAGE = new ParameterDescription("include_sub_account_usage", false, BOOLEAN);
    private static ParameterDescription USE_NEXT_INVOICE_DATE = new ParameterDescription("use_next_invoice_date", false, BOOLEAN);

    private static final boolean DEFAULT_USE_ATTRIBUTES = false;
    private static final boolean DEFAULT_USE_WILDCARDS = false;
    private static final String DEFAULT_USAGE_TYPE = UsageType.PRICE_HOLDER.name();
    private static final boolean DEFAULT_SUB_ACCOUNT_USAGE = false;
    private static final boolean DEFAULT_USE_NEXT_INVOICE_DATE = false;

    {
        descriptions.add(USE_ATTRIBUTES);
        descriptions.add(USE_WILDCARDS);
        descriptions.add(USAGE_TYPE);
        descriptions.add(SUB_ACCOUNT_USAGE);
    }


    public BigDecimal getPrice(ItemDTO item,
                               BigDecimal quantity,
                               Integer userId,
                               Integer currencyId,
                               List<PricingField> fields,
                               BigDecimal defaultPrice,
                               OrderDTO pricingOrder,
                               OrderLineDTO orderLine,
                               boolean singlePurchase,
                               Date eventDate) throws TaskException {

        Date pricingDate = (null == eventDate) ? getPricingDate(pricingOrder) : eventDate;

        LOG.debug("Calling PriceModelPricingTask with pricing order: %s, for date %s", pricingOrder, pricingDate);
        LOG.debug("Pricing item %s, quantity %s - for user %s", item.getId(), quantity, userId);

        if (userId != null) {
            // get customer pricing model, use fields as attributes
            Map<String, String> attributes = getAttributes(fields);
   
            // price for customer making the pricing request depending on the product pricing hierarchy
            SortedMap<Date, PriceModelDTO> models = getPricesByHierarchy(userId, item.getId(), pricingDate, attributes);

            // iterate through parents until a price is found.
            UserBL user = new UserBL(userId);
            CustomerDTO customer = user.getEntity() != null ? user.getEntity().getCustomer() : null;
            if (customer != null && customer.useParentPricing()) {
                while (customer.getParent() != null && (models == null || models.isEmpty())) {
                    customer = customer.getParent();

                    LOG.debug("Looking for price from parent user %s", customer.getBaseUser().getId());
                    models = getPricesByHierarchy(customer.getBaseUser().getId(), item.getId(), pricingDate, attributes);
                    if (models != null && !models.isEmpty()) LOG.debug("Found price from parent user: %s", models);
                }
            }

            LOG.debug("Prices found by hierarchy: %s", models);

            // no customer price, this means the customer has not subscribed to a plan affecting this
            // item, or does not have a customer specific price set. Use the item default price.
            if (!PriceModelBL.containsEffectiveModel(models, pricingDate)) {
                LOG.debug("No customer price found, using item default price model.");
                models = new ItemBL(item.getId()).getEntity().getDefaultPricesByCompany(getEntityId());
                // if company specific price is not found. Try global.
                if(models == null) {
                	LOG.debug("No company specific price model was found. Searching for global price.");
                	models = new ItemBL(item.getId()).getEntity().getGlobalDefaultPrices();
                }
            }

            LOG.debug("Price date: %s", pricingDate);

            // apply price model
            if (models != null && !models.isEmpty()) {
                PriceModelDTO model = PriceModelBL.getPriceForDate(models, pricingDate);

                if(model != null) {
                	LOG.debug("Applying price model %s", model);
                	
                	Usage usage = null;
                	PricingResult result = new PricingResult(item.getId(), quantity, userId, currencyId);
                	for (PriceModelDTO next = model; next != null; next = next.getNext()) {
                		// fetch current usage of the item if the pricing strategy requires it
                		if (next.getStrategy().requiresUsage()) {
                			UsageType type = UsageType.valueOfIgnoreCase(getParameter(USAGE_TYPE.getName(), DEFAULT_USAGE_TYPE));
                			Integer priceUserId = customer != null ? customer.getBaseUser().getId() : userId;
                			usage = getUsage(type, item.getId(), userId, priceUserId, pricingOrder);
                			
                			LOG.debug("Current usage of item %s : %s", item.getId(), usage);
                		} else {
                			LOG.debug("Pricing strategy %s does not require usage.", next.getType());
                		}
                		
                		if (null != next.getNext()) {
                			result.setIsChained(true);
                		}
                		LOG.debug("Call Before apply");
                		next.applyTo(pricingOrder, orderLine, result.getQuantity(), result, fields, usage, singlePurchase, pricingDate);
                		LOG.debug("Price discovered: %s", result.getPrice());
                	}

                    if (result.isPercentage() ) item.setPercentage(result.getPrice());
                    item.setIsPercentage(result.isPercentage());

                	if (needToRecalculate(model, orderLine, result)) {
                		recalculatePrice(pricingOrder, orderLine, result, model);
                	}
                	
                	return result.getPrice();
                } else {
                	LOG.debug("No price model found, using default price.");
                    return defaultPrice;
                }
            }
        }

        LOG.debug("No price model found, using default price.");
        return defaultPrice;
    }
    
    /**
     * Checks if price needs to be recalculated because of use of Free Usage Pools
     * In certain scenarios such as tiered and graduated, recalculation is not required when editing an order.
     * @param model
     * @param orderLine
     * @return true or false
     */
    private boolean needToRecalculate(PriceModelDTO model, OrderLineDTO orderLine, PricingResult result) {
    	boolean needToRecalculate = false;
		if (null != model && null != model.getType() && null != orderLine) {
            if ( (null != orderLine.getItem()) && new ItemDAS().isPlan(orderLine.getItem().getId())) {
                return false;
            }
			if (orderLine.isMediated()) {
				needToRecalculate = true;
			} else {
				BigDecimal customerUsagePoolQuantity = orderLine.getCustomerUsagePoolQuantity();
				if (((model.getType().equals(PriceModelStrategy.TIERED)) || 
					(model.getType().equals(PriceModelStrategy.GRADUATED)) || 
					(model.getType().equals(PriceModelStrategy.POOLED)) || 
					(model.getType().equals(PriceModelStrategy.QUANTITY_ADDON))	) && 
					(customerUsagePoolQuantity.compareTo(BigDecimal.ZERO) <= 0)
					) {
					
					if (orderLine.getId() == 0) {
						needToRecalculate = true;
					}
					
				} else if (model.getType().equals(PriceModelStrategy.QUANTITY_ADDON) || 
							  (null != model.getNext() && model.getNext().getType().equals(PriceModelStrategy.PERCENTAGE))
						    ) {
					needToRecalculate = false;
				} else {
					needToRecalculate = true;
				}
			}
		}
    	return needToRecalculate;
	}
    
    private void recalculatePriceForCapped(OrderDTO pricingOrder, OrderLineDTO line, PricingResult result, PriceModelDTO model) {
    	LOG.debug("recalculatePriceForCapped called, line: " + line);
    	BigDecimal price = result.getPrice();
    	LOG.debug("recalculatePriceForCapped: Pricing Price: " + price);
    	if (null != line) {
			BigDecimal freeUsageQuantity = line.getFreeUsagePoolQuantity();
			LOG.debug("recalculatePriceForCapped: freeUsageQuantity: " + freeUsageQuantity);
			if (freeUsageQuantity.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal maximum = AttributeUtils.getDecimal(model.getAttributes(), "max");
				BigDecimal chargeableQuantity = line.getQuantity().subtract(freeUsageQuantity);
				BigDecimal lineAmount = chargeableQuantity.multiply(price).setScale(Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND);
				if (lineAmount.compareTo(maximum) >= 0) {
	                price = maximum.divide(line.getQuantity(), Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND);
	                result.setPrice(price);
	            } else {
	            	price = lineAmount.divide(line.getQuantity(), Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND);
	                result.setPrice(price);
	            }
			}
    	}
    }
    
    /**
     * Recalculates the price after deducting free usage quantity. This is required because 
     * the pricing strategy gives the price over the chargeable quantity after deducting free usage.
     * The recalculate here finds out the average price over the total quantity (of the line).
     * @param pricingOrder
     * @param line
     * @param result
     */
    private void recalculatePrice(OrderDTO pricingOrder, OrderLineDTO line, PricingResult result, PriceModelDTO model) {
    	if (null != line && null != model && 
    			line.isMediated() && model.getType().equals(PriceModelStrategy.CAPPED_GRADUATED)) {
    		LOG.debug("recalculatePriceForCapped called, line: " + line);
    		recalculatePriceForCapped(pricingOrder, line, result, model);
    	} else if (!model.getType().equals(PriceModelStrategy.CAPPED_GRADUATED)
                && !model.getType().equals(PriceModelStrategy.BLOCK_AND_INDEX)
                && !model.getType().equals(PriceModelStrategy.ROUTE_BASED_RATE_CARD)) {
	    	LOG.debug("recalculatePrice called, line: " + line);
	    	BigDecimal price = result.getPrice();
	    	LOG.debug("recalculatePrice: Pricing Price: " + price);
	    	if (null != line) {
				BigDecimal freeUsageQuantity = line.getFreeUsagePoolQuantity();
				LOG.debug("recalculatePrice: freeUsageQuantity: " + freeUsageQuantity);
				if (freeUsageQuantity.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal chargeableQuantity = line.getQuantity().subtract(freeUsageQuantity);
					BigDecimal lineAmount = chargeableQuantity.multiply(price).setScale(Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND);
					BigDecimal recalculatedPrice = lineAmount.divide(line.getQuantity(), MathContext.DECIMAL128);
					recalculatedPrice = recalculatedPrice.setScale(Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND);
					result.setPrice(recalculatedPrice);
				}
	    	}
	    	
    	}

    	/*
		 * DONT REMOVE COMMENTED CODE.
		 * This code is not being removed as it tries to handle one scenario of release of
		 * free quantity to usage pools if line containing the free quantity is removed.
		 * Currently this scenario is not supported, but we may look at it in future if required.
		 * 
		if (null != line && line.getDeleted() == 1 && freeUsageQuantity.compareTo(BigDecimal.ZERO) > 0) {
			// Since the line is deleted and it is using free usage quantity, 
			// lets recalculate entire order so that the deleting lines free quantity can be given to any other lines
			for (OrderLineDTO ol : line.getPurchaseOrder().getLines()) {
				if (null != ol.getItem() && ol.getItemId() == line.getItemId()) {
					if (ol.getDeleted() == 0 && ol.getQuantity().compareTo(ol.getFreeUsagePoolQuantity()) > 0) {
						BigDecimal potentialFreeQuantity = ol.getQuantity().subtract(ol.getFreeUsagePoolQuantity());
						if (potentialFreeQuantity.compareTo(freeUsageQuantity) <= 0) {
							ol.setPrice(BigDecimal.ZERO);
						} else {
							// the potential quantity that can be made free is higher than freeUsageQuantity made free by the deleting order line.
							BigDecimal chargeableQuantity = potentialFreeQuantity.subtract(freeUsageQuantity);
							BigDecimal lineAmount = chargeableQuantity.multiply(price);
							BigDecimal recalculatedPrice = lineAmount.divide(line.getQuantity().setScale(Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND));
							recalculatedPrice = recalculatedPrice.setScale(Constants.BIGDECIMAL_SCALE, Constants.BIGDECIMAL_ROUND);
					    	ol.setPrice(recalculatedPrice);
						}
						
						// transfer the free usage quantity from one line to the other
						for (OrderLineUsagePoolDTO olUsagePool : line.getOrderLineUsagePools()) {
							if (olUsagePool.getQuantity().compareTo(potentialFreeQuantity) <= 0) {
								ol.getOrderLineUsagePools().add(new OrderLineUsagePoolDTO(0, ol, olUsagePool.getQuantity(), olUsagePool.getCustomerUsagePool()));
								potentialFreeQuantity = potentialFreeQuantity.subtract(olUsagePool.getQuantity());
								freeUsageQuantity = freeUsageQuantity.subtract(olUsagePool.getQuantity());
							} else {
								if (potentialFreeQuantity.compareTo(BigDecimal.ZERO) > 0) {
									ol.getOrderLineUsagePools().add(new OrderLineUsagePoolDTO(0, ol, potentialFreeQuantity, olUsagePool.getCustomerUsagePool()));
								}
								freeUsageQuantity = freeUsageQuantity.subtract(potentialFreeQuantity);
								potentialFreeQuantity = BigDecimal.ZERO;
							}
						}
					}
				}
			}
			
			line.getOrderLineUsagePools().clear();
		}
		*/
    }
    
    /**
     * Fetches a price model for the given pricing request.
     *
     * If the parameter "use_attributes" is set, the given pricing fields will be used as
     * query attributes to determine the pricing model.
     *
     * If the parameter "use_wildcards" is set, the price model lookup will allow matches
     * on wildcard attributes (stored in the database as "*").
     *
     * @param userId id of the user pricing the item
     * @param itemId id of the item to price
     * @param attributes attributes from pricing fields
     * @param planPricingOnly determines which customer pricing to retrieve (only customer, only plan or both)
     * @return found list of dated pricing models, or null if none found
     */
    private SortedMap<Date, PriceModelDTO> getCustomersPlanPriceModel(Integer userId, Integer itemId, Date pricingDate, Map<String, String> attributes, Boolean planPricingOnly) {
        CustomerPriceBL customerPriceBl = new CustomerPriceBL(userId);

		//Issue #5873 - At this point we only return one PriceModel despite of the possibility that there could be more than one.
        if (getParameter(USE_ATTRIBUTES.getName(), DEFAULT_USE_ATTRIBUTES) && !attributes.isEmpty()) {
            if (getParameter(USE_WILDCARDS.getName(), DEFAULT_USE_WILDCARDS)) {
                LOG.debug("Fetching customer price using wildcard attributes: %s", attributes);
                List<PlanItemDTO> items = customerPriceBl.getPricesByWildcardAttributes(itemId, attributes, planPricingOnly, MAX_RESULTS, pricingDate);
                
                if (!items.isEmpty() && items.size() > 1) {
                    LOG.warn(items.size() + "Price Models were retrieved but only one is returned for pricing.");
                }                

                return !items.isEmpty() ? items.get(0).getModels() : null;
            } else {
                LOG.debug("Fetching customer price using attributes: %s", attributes);
                List<PlanItemDTO> items = customerPriceBl.getPricesByAttributes(itemId, attributes, planPricingOnly, MAX_RESULTS, pricingDate);

                if (!items.isEmpty() && items.size() > 1) {
                    LOG.warn(items.size() + "Price Models were retrieved but only one is returned for pricing.");
                }
                return !items.isEmpty() ? items.get(0).getModels() : null;
            }
        } else {
            // not configured to query prices with attributes, or no attributes given
            // determine customer price normally
            LOG.debug("Fetching customer price without attributes (no PricingFields given or 'use_attributes' = false)");

            PlanItemDTO item = customerPriceBl.getPriceForDate(itemId, planPricingOnly, pricingDate);

            LOG.warn("Only one Price Model is retrieved from the DB. There could be more but only that one is returned for pricing.");

            return item != null ? item.getModels() : null;
        }
    }

    /**
     *  Fetches the account type prices for provided item id
     *  and account type that the specified user(userId) belongs to
     *
     * @param userId id of the user
     * @param itemId id of the item to price
     * @param attributes attributes from pricing fields
     * @return found list of dated pricing models, or null if none found
     */
    private SortedMap<Date, PriceModelDTO> getAccountTypePriceModel(Integer userId, Integer itemId, Date pricingDate, Map<String, String> attributes) {

        UserBL userBL = new UserBL(userId);
        CustomerDTO customer = userBL.getEntity().getCustomer();
        if (customer == null || customer.getAccountType() == null) {
            LOG.debug("Account Type Pricing not available for user: " + userId);
            return null;
        }
        AccountTypePriceBL accountTypePriceBL = new AccountTypePriceBL(customer.getAccountType());

        LOG.debug("Fetching account type pricing for account type: %s and item: %s", accountTypePriceBL.getAccountTypeId(), itemId);
        List<PlanItemDTO> items = accountTypePriceBL.getPricesForItemAndPricingDate(itemId, pricingDate);

        SortedMap<Date, PriceModelDTO> models= new TreeMap<>();
        for (PlanItemDTO item: items) {
            models.putAll(item.getModels());
        }

        return models.size() > 0 ? models : null;
    }

    /**
     * Fetches a price model for the given pricing request.
     *
     * If the parameter "use_attributes" is set, the given pricing fields will be used as
     * query attributes to determine the pricing model.
     *
     * If the parameter "use_wildcards" is set, the price model lookup will allow matches
     * on wildcard attributes (stored in the database as "*").
     *
     * @param userId id of the user pricing the item
     * @param itemId id of the item to price
     * @param attributes attributes from pricing fields
     * @return found list of dated pricing models, or null if none found
     */
    private SortedMap<Date, PriceModelDTO> getCustomerPriceModel(Integer userId, Integer itemId, Date pricingDate, Map<String, String> attributes) {
        CustomerPriceBL customerPriceBl = new CustomerPriceBL(userId);
        List<PlanItemDTO> items = new ArrayList<>(0);

        //Issue #5873 - At this point we only return one PriceModel despite of the possibility that there could be more than one.
        if (getParameter(USE_ATTRIBUTES.getName(), DEFAULT_USE_ATTRIBUTES) && !attributes.isEmpty()) {
            if (getParameter(USE_WILDCARDS.getName(), DEFAULT_USE_WILDCARDS)) {
                LOG.debug("Fetching customer price using wildcard attributes: %s", attributes);
                items = customerPriceBl.getPricesByWildcardAttributes(itemId, attributes, Boolean.FALSE, null, pricingDate);

                if (!items.isEmpty() && items.size() > 1) {
                    LOG.warn(items.size() + "Price Models were retrieved but only one is returned for pricing.");
                }

                return !items.isEmpty() ? items.get(0).getModels() : null;
            } else {
                LOG.debug("Fetching customer price using attributes: %s", attributes);
                items = customerPriceBl.getPricesByAttributes(itemId, attributes, Boolean.FALSE, null, pricingDate);

                if (!items.isEmpty() && items.size() > 1) {
                    LOG.warn(items.size() + "Price Models were retrieved but only one is found.");
                }

            }
        } else {
            // not configured to query prices with attributes, or no attributes given
            // determine customer price normally
            LOG.debug("Fetching customer price without attributes (no PricingFields given or 'use_attributes' = false)");
            items = customerPriceBl.getAllCustomerPricesForDate(itemId, Boolean.FALSE, pricingDate);
        }

        // Customer prices today are saved one model (having a startDate) per planItem. Each price creates a new planItem
        // Therefore it is important to get all the prices and sort them by their dates planItem.priceModel.startDate
        // TODO - Every new Customer Price or Account Type should not create a new Plan Item, instead, only add a priceModel with a new startDate

        SortedMap<Date, PriceModelDTO> models= new TreeMap<>();
        for (PlanItemDTO item: items) {
            models.putAll(item.getModels());
        }

        return models.size() > 0 ? models : null;
    }

    /**
     *  Retrieves the price model considering the pricing resolution hierarhy.
     *  If the pricing is found for the higher pricing resolution (ex. customer pricing), the search stops and those pricings are retrieved.
     *  Otherwise, the pricing search for the next pricing resolution steps in the hierarchy until pricings are found.
     *
     *  Pricing Resolution Hierararhy:
     *  <ul>
     *      <li>
     *          Customer Pricing Resolution
     *      </li>
     *      <li>
     *          Account Type pricing resolution
     *      </li>
     *      <li>
     *          Plan Pricing resolution - Note that the Plan pricing are resolved
     *          from the customer pricing that have a plan attached to the PlanItemDTO
     *      </li>
     *  </ul>
     *
     *
     * @param userId id of the user
     * @param itemId id of the item to price
     * @param attributes attributes from pricing fields
     * @return found list of dated pricing models, or null if none found
     */
    public SortedMap<Date, PriceModelDTO> getPricesByHierarchy(Integer userId, Integer itemId, Date pricingDate, Map<String, String> attributes) {

        // TODO (pai) make the implementation generic - separate the customer pricing from the plan pricing
        // 1. Customer pricing resolution
        SortedMap<Date, PriceModelDTO> models = getCustomerPriceModel(userId, itemId, pricingDate, attributes);
        if (models != null && !models.isEmpty()) {
            return models;
        }

        // 2. Account Type pricing resolution
        models = getAccountTypePriceModel(userId, itemId, pricingDate, attributes);
        if (models != null && !models.isEmpty()) {
            return models;
        }

        // 3. Plan Pricing resolution - consider only the plan prices from the customer pricing
        models = getCustomersPlanPriceModel(userId, itemId, pricingDate, attributes, true);
        if (models != null && !models.isEmpty()) {
            return models;
        }

        return null;

    }

    /**
     * Returns the total usage of the given item for the set UsageType, and optionally include charges
     * made to sub-accounts in the usage calculation.
     *
     * @param type usage type to query, may use either USER or PRICE_HOLDER to determine usage
     * @param itemId item id to get usage for
     * @param userId user id making the price request
     * @param priceUserId user holding the pricing plan
     * @param pricingOrder working order (order being edited/created)
     * @return usage for customer and usage type
     */
    private Usage getUsage(UsageType type, Integer itemId, Integer userId, Integer priceUserId, OrderDTO pricingOrder) {
        UsageBL usage;
        switch (type) {
            case USER:
                usage = new UsageBL(userId, pricingOrder);
                break;

            default:
            case PRICE_HOLDER:
                usage = new UsageBL(priceUserId, pricingOrder);
                break;
        }

        // include usage from sub account?
        if (getParameter(SUB_ACCOUNT_USAGE.getName(), DEFAULT_SUB_ACCOUNT_USAGE)) {
            return usage.getSubAccountItemUsage(itemId);
        } else {
            return usage.getItemUsage(itemId);
        }
    }

    /**
     * Convert pricing fields into price model query attributes.
     *
     * @param fields pricing fields to convert
     * @return map of string attributes
     */
    public Map<String, String> getAttributes(List<PricingField> fields) {
        Map<String, String> attributes = new HashMap<String, String>();
        if (fields != null) {
            for (PricingField field : fields)
                attributes.put(field.getName(), field.getStrValue());
        }
        return attributes;
    }

    /**
     * Return the date of this pricing request. The pricing date will be the "active" date of the pricing
     * order, or the next invoice date if the "use_next_invoice_date" parameter is set to true.
     *
     * If pricing order is null, then today's date will be used.
     *
     * @param pricingOrder pricing order
     * @return date to use for this pricing request
     */
    public Date getPricingDate(OrderDTO pricingOrder) {
        if (pricingOrder != null) {
            if (getParameter(USE_NEXT_INVOICE_DATE.getName(), DEFAULT_USE_NEXT_INVOICE_DATE)) {
                // use next invoice date of this order
                return new OrderBL(pricingOrder).getInvoicingDate();

            } else {
                // use order active since date, or created date if no active since
                return pricingOrder.getPricingDate();
            }

        } else {
            // no pricing order, use today
            return new Date();
        }
    }
}
