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

package com.sapienter.jbilling.server.pricing.db;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.item.CurrencyBL;
import com.sapienter.jbilling.server.item.db.ItemDAS;
import com.sapienter.jbilling.server.item.tasks.PricingResult;
import com.sapienter.jbilling.server.item.PricingField;
import com.sapienter.jbilling.server.order.Usage;
import com.sapienter.jbilling.server.order.db.*;
import com.sapienter.jbilling.server.pricing.PriceModelWS;
import com.sapienter.jbilling.server.pricing.strategy.PricingStrategy;
import com.sapienter.jbilling.server.usagePool.CustomerUsagePoolBL;
import com.sapienter.jbilling.server.usagePool.db.CustomerUsagePoolDTO;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.db.CurrencyDTO;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Brian Cowdery
 * @since 30-07-2010
 */
@Entity
@Table(name = "price_model")
@TableGenerator(
        name = "price_model_GEN",
        table = "jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue = "price_model",
        allocationSize = 1
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PriceModelDTO implements Serializable {

	private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(PriceModelDTO.class));
    public static final String ATTRIBUTE_WILDCARD = "*";

    private Integer id;
    private PriceModelStrategy type;
    private SortedMap<String, String> attributes = new TreeMap<String, String>();
    private BigDecimal rate;
    private CurrencyDTO currency;

    // price model chaining
    private PriceModelDTO next;

    public PriceModelDTO() {
    }

    public PriceModelDTO(PriceModelStrategy type, BigDecimal rate, CurrencyDTO currency) {
        this.type = type;
        this.rate = rate;
        this.currency = currency;
    }

    public PriceModelDTO(PriceModelWS ws, CurrencyDTO currency) {
        setId(ws.getId());
        setType(PriceModelStrategy.valueOf(ws.getType()));
        setAttributes(new TreeMap<String, String>(ws.getAttributes()));
        setRate(ws.getRateAsDecimal());
        setCurrency(currency);
    }

    /**
     * Copy constructor.
     *
     * @param model model to copy
     */
    public PriceModelDTO(PriceModelDTO model) {
        this.id = model.getId();
        this.type = model.getType();
        this.attributes = new TreeMap<String, String>(model.getAttributes());
        this.rate = model.getRate();
        this.currency = model.getCurrency();

        if (model.getNext() != null) {
            this.next = new PriceModelDTO(model.getNext());
        }
    }


    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "price_model_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "strategy_type", nullable = false, length = 25)
    public PriceModelStrategy getType() {
        return type;
    }

    public void setType(PriceModelStrategy type) {
        this.type = type;
    }

    @Transient
    public PricingStrategy getStrategy() {
        return getType() != null ? getType().getStrategy() : null;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "price_model_attribute", joinColumns = @JoinColumn(name = "price_model_id"))
    @MapKeyColumn(name="attribute_name", nullable = true)
    @Column(name = "attribute_value", nullable = true, length = 255)
    @Sort(type = SortType.NATURAL)
    @Fetch(FetchMode.SELECT)
    public SortedMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(SortedMap<String, String> attributes) {
        this.attributes = attributes;
        setAttributeWildcards();
    }

    /**
     * Sets the given attribute. If the attribute is null, it will be persisted as a wildcard "*".
     *
     * @param name attribute name
     * @param value attribute value
     */
    public void addAttribute(String name, String value) {
        this.attributes.put(name, (value != null ? value : ATTRIBUTE_WILDCARD));
    }

    /**
     * Replaces null values in the attribute list with a wildcard character. Null values cannot be
     * persisted using the @CollectionOfElements, and make for uglier 'optional' attribute queries.
     */
    public void setAttributeWildcards() {
        if (getAttributes() != null && !getAttributes().isEmpty()) {
            for (Map.Entry<String, String> entry : getAttributes().entrySet())
                if (entry.getValue() == null)
                    entry.setValue(ATTRIBUTE_WILDCARD);
        }
    }

    /**
     * Returns the pricing rate. If the strategy type defines an overriding rate, the
     * strategy rate will be returned.
     *
     * @return pricing rate.
     */
    @Column(name = "rate", nullable = true, precision = 10, scale = 22)
    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = true)
    public CurrencyDTO getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyDTO currency) {
        this.currency = currency;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "next_model_id", nullable = true)
    public PriceModelDTO getNext() {
        return next;
    }

    public void setNext(PriceModelDTO next) {
        this.next = next;
    }

    /**
     * Applies this pricing to the given PricingResult.
     *
     * This method will automatically convert the calculated price to the currency of the given
     * PricingResult if the set currencies differ.
     *
     * @see com.sapienter.jbilling.server.pricing.strategy.PricingStrategy
     * @param pricingOrder target order for this pricing request (may be null)
     * @param quantity quantity of item being priced
     * @param result pricing result to apply pricing to
     * @param usage total item usage for this billing period
     * @param singlePurchase true if pricing a single purchase/addition to an order, false if pricing a quantity that already exists on the pricingOrder.
     * @param pricingDate pricing date 
     */
    @Transient
    public void applyTo(OrderDTO pricingOrder, OrderLineDTO orderLine, BigDecimal quantity, PricingResult result, List<PricingField> fields,
                        Usage usage, boolean singlePurchase, Date pricingDate) {

    	OrderDTO currentOrder = null != orderLine && null != orderLine.getPurchaseOrder() ? 
    			orderLine.getPurchaseOrder() : pricingOrder;
    	BigDecimal freeUsageQuantity = BigDecimal.ZERO;
    	// check that FUP is not used for ZERO pricing products
    	if (null != quantity && null != this && !this.getType().equals(PriceModelStrategy.ZERO)) {

        	if (null == result.getQuantity()) {
        		result.setQuantity(quantity);
        	}
        	
        	LOG.debug("PriceModelDTO.applyTo orderLine: " + orderLine);
        	if (null != orderLine) {
        		// before applying pricing, apply the free usage pools for this customer
        		if (null == orderLine.getMediatedQuantity()) {
        			orderLine.setMediatedQuantity(BigDecimal.ZERO);
        		}
        		if (orderLine.isMediated()) {
        			if (orderLine.getMediatedQuantity().compareTo(BigDecimal.ZERO) > 0) {
            			applyFreeUsagePools(currentOrder, orderLine, usage, result);
            		}
        		} else {
        			LOG.debug("Before applyFreeUsagePools: " + usage);
        			applyFreeUsagePools(currentOrder, orderLine, usage, result);
        		}
        		
        	}
	    	
			freeUsageQuantity = quantity.subtract(result.getQuantity());
    		
    		if (freeUsageQuantity.compareTo(BigDecimal.ZERO) < 0) {
    			freeUsageQuantity = BigDecimal.ZERO;
    		}
    		if (null != usage) {
				usage.setFreeUsageQuantity(null != orderLine && null != currentOrder ? 
						currentOrder.getFreeUsagePoolsTotalQuantity() : BigDecimal.ZERO);
			}
			result.setFreeUsageQuantity(null != orderLine && null != currentOrder ? 
					currentOrder.getFreeUsagePoolsTotalQuantity() : BigDecimal.ZERO);
	    	
	    	quantity = result.getQuantity();
    	}
    	
    	// To handle Mediation and UI Scenario of FUP for Volume pricing.
    	if (null != orderLine && orderLine.hasOrderLineUsagePools() &&  this.getType().equals(PriceModelStrategy.VOLUME_PRICING)) {
    		if (orderLine.isMediated()) {
    			usage.setQuantity(quantity);
    		} else {
    			usage.setQuantity(usage.getQuantity().subtract(result.getFreeUsageQuantity()));
    		}
    	}
		this.getType().getStrategy().applyTo(pricingOrder, result, fields, this, quantity, usage, singlePurchase);

        // convert currency if necessary
        if (result.getUserId() != null
                && result.getCurrencyId() != null
                && result.getPrice() != null
                && this.getCurrency() != null
                && this.getCurrency().getId() != result.getCurrencyId()
                && !result.isPerCurrencyRateCard()) {

            Integer entityId = new UserBL().getEntityId(result.getUserId());
            if (pricingDate == null) {
                pricingDate = new Date();
            }

            //pricingDate will be equal to event_date when called from Mediation
            final BigDecimal converted = result.isPercentage()
                                            ? result.getPrice()
                                            : new CurrencyBL().convert(
                                                                this.getCurrency().getId(),
                                                                result.getCurrencyId(),
                                                                result.getPrice(),
                                                                pricingDate,
                                                                entityId);
            
            LOG.debug("price: " + converted);
            result.setPrice(converted);
        }
    }
    
    @Transient
    private void applyFreeUsagePools(OrderDTO pricingOrder, OrderLineDTO orderLine, Usage usage, PricingResult result) {

    	BigDecimal quantity = result.getQuantity();
    	UserDTO user = null;
    	if (null != usage) {
    		user = new UserDAS().find(usage.getUserId());
    	} else {
    		// usage will be null in case of pricing strategies that are not usage based
    		if (null != result) {
    			user = new UserDAS().find(result.getUserId());
    		}
    	}
    	
    	if (null == user) {
    		// if user is still not found, lets not apply free usage pools
    		LOG.debug("Cannot apply free usage pool, no user found.");
    		return;
    	} else {
    		LOG.debug("User Id: " + user.getId());
    	}
    	
    	CustomerDTO customer = user.getCustomer();
    	LOG.debug("Customer Id: " + customer.getId());

    	if (customer.hasCustomerUsagePools()) {

    		List<CustomerUsagePoolDTO> freeUsagePools = customer.getCustomerUsagePools();
	    	if (freeUsagePools.size() > 1) {
	    		if (orderLine.getId() > 0 && !orderLine.isMediated()) {
	    			OrderLineDTO orderLineDto = new OrderLineDAS().find(orderLine.getId());
	    			quantity = result.getQuantity().subtract(orderLineDto.getQuantity());
	    			result.setQuantity(quantity);
	    		}
	    		// sort based on preference or created date if preference is same
	    		Collections.sort(freeUsagePools, CustomerUsagePoolDTO.CustomerUsagePoolsByPrecedenceOrCreatedDateComparator);
	    	}
	    	
	    	CustomerUsagePoolBL bl = new CustomerUsagePoolBL();
	    	ItemDAS itemDas = new ItemDAS();
	    	LOG.debug("Before for ....");
	    	for (CustomerUsagePoolDTO freeUsagePool : freeUsagePools) {
	    		if(freeUsagePool.getCycleEndDate().compareTo(new Date()) >= 0) {
		    		if (quantity.compareTo(BigDecimal.ZERO) > 0) {
		    			LOG.debug("freeUsagePool quantity: " + freeUsagePool.getQuantity());
			    		BigDecimal releasedFreeUsageQuantity = BigDecimal.ZERO;
			    		if (null != orderLine && orderLine.getDeleted() == 1) {
			    			// the order line is being removed, we need to release any free usage 
			    			// from this order line for usage by other lines, or release it back to pool
			    			releasedFreeUsageQuantity = orderLine.getFreeUsagePoolQuantity();
			    		}
			    		LOG.debug("releasedFreeUsageQuantity: " + releasedFreeUsageQuantity);
			    		if (freeUsagePool.isActive() && 
			    			freeUsagePool.getQuantity().add(releasedFreeUsageQuantity).compareTo(BigDecimal.ZERO) > 0 && 
			    			freeUsagePool.getAllItems().contains(itemDas.find(result.getItemId()))) {
			    			
			    			LOG.debug("Inside if .....");
			    			BigDecimal persistedFreeUsageQuantity =BigDecimal.ZERO;
			    			
			    			OrderLineDTO currentOrderLine = null;
			    			if (null != pricingOrder && null != pricingOrder.getId() && pricingOrder.getId() > 0) {
			    				OrderDTO order = new OrderDAS().find(pricingOrder.getId());
			    				persistedFreeUsageQuantity = order.getFreeUsagePoolsTotalQuantity(freeUsagePool.getId());
			    				//persistedFreeUsageQuantity = (null != order ? order.getFreeUsageQuantity() : BigDecimal.ZERO);
			    				for (OrderLineDTO line : order.getLines()) {
			    					if (line.getId() == orderLine.getId()) {
			    						currentOrderLine = line;
			    						break;
			    					}
			    				}
			    			}
			    			
			    			LOG.debug("currentOrderLine: " + currentOrderLine);
			    			LOG.debug("pricingOrder::::::::::::: " + pricingOrder);
			    			LOG.debug("persistedFreeUsageQuantity :::::"+ persistedFreeUsageQuantity);
			    			BigDecimal nonPersistedFreeUsageQuantity = pricingOrder.getFreeUsagePoolsTotalQuantity(freeUsagePool.getId()).
			    					subtract(persistedFreeUsageQuantity);
			    			if (nonPersistedFreeUsageQuantity.compareTo(BigDecimal.ZERO) == 0 && result.isChained()) {
			    				// check if order line has ol usage pools populated, scnario for chained pricing
			    				nonPersistedFreeUsageQuantity = orderLine.getFreeUsagePoolQuantity(freeUsagePool.getId());
			    			}
			    			if(nonPersistedFreeUsageQuantity.compareTo(BigDecimal.ZERO) < 0) {
			    				nonPersistedFreeUsageQuantity = BigDecimal.ZERO;
			    			}
			    			LOG.debug("freeUsagePool.getQuantity() :::"+ freeUsagePool.getQuantity());
			    			LOG.debug("nonPersistedFreeUsageQuantity :::"+ nonPersistedFreeUsageQuantity);
			    			BigDecimal availableFreeQuantity = BigDecimal.ZERO;
			    			/* 
			    			 * To handle Create order with quantity is less than Free usage pool quantity & 
			    			 * Edit order line, save with increase in quantity scenario.
			    			 */
			    			if (orderLine.getId() > 0) {
			    				if (freeUsagePools.size() > 1 && !orderLine.isMediated()) {
			    					availableFreeQuantity = freeUsagePool.getQuantity().subtract(nonPersistedFreeUsageQuantity);
			    				} else {
			    					availableFreeQuantity = freeUsagePool.getQuantity().
			    											add(orderLine.getFreeUsagePoolQuantity(freeUsagePool.getId())).
			    											subtract(nonPersistedFreeUsageQuantity);
			    				}
			    			} else {
			    				availableFreeQuantity = freeUsagePool.getQuantity().subtract(nonPersistedFreeUsageQuantity);
			    			}
			    			if (availableFreeQuantity.compareTo(BigDecimal.ZERO) < 0 && !result.isChained()) {
			    				availableFreeQuantity = freeUsagePool.getQuantity();
			    			}
			    			LOG.debug("availableFreeQuantity :::::"+ availableFreeQuantity);
			    			if (quantity.compareTo(availableFreeQuantity) <= 0) {
			    				quantity = BigDecimal.ZERO;
			    			} else {
			    				quantity = quantity.subtract(availableFreeQuantity);
			    			}
			    			
			    			BigDecimal freeUsageQuantity = result.getQuantity().subtract(quantity);
			    			LOG.debug("freeUsageQuantity: " + freeUsageQuantity);
			    			LOG.debug("result.getQuantity(): " + result.getQuantity());
			    			LOG.debug("quantity: " + quantity);
			    			
			    			if (freeUsageQuantity.compareTo(BigDecimal.ZERO) > 0 && orderLine.isMediated() || 
				    				((null != orderLine && orderLine.getDeleted() == 1) ||
				    				(null == currentOrderLine || 
				    				(null != currentOrderLine && 
				    					currentOrderLine.getQuantity().compareTo(result.getQuantity()) != 0)))) {
				    			// create or update order line usage pools association
			    				LOG.debug("create or update order line usage pools association");
				    			setOrderLineUsagePoolMap(orderLine, freeUsagePool, freeUsageQuantity, result);
			    			}
			    		} else {
			    			BigDecimal freeUsageQuantity = BigDecimal.ZERO;
							if (quantity.compareTo(orderLine.getFreeUsagePoolQuantity()) < 0 && freeUsagePool.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
								freeUsageQuantity = quantity;
								setOrderLineUsagePoolMap(orderLine, freeUsagePool, freeUsageQuantity, result);
							} else if (quantity.compareTo(orderLine.getFreeUsagePoolQuantity()) < 0 && freeUsagePools.size() == 1) {
								freeUsageQuantity = quantity;
								setOrderLineUsagePoolMap(orderLine, freeUsagePool, freeUsageQuantity, result);
							}
			    		}
		    		}
		    		orderLine.setMediatedQuantity(BigDecimal.ZERO);
		    		result.setQuantity(quantity);
		    	}
	    	}
    	}
    }
    
    /**
     * When a free usage pool gets used, store the free quantity used in order line usage pool association map.
     * @param orderLine
     * @param freeUsagePool
     * @param freeUsageQuantity
     */
    @Transient
    private void setOrderLineUsagePoolMap(OrderLineDTO orderLine, CustomerUsagePoolDTO freeUsagePool, BigDecimal freeUsageQuantity, PricingResult result) {
    	
    	if (orderLine.getDeleted() == 0) {
    		LOG.debug("OrderLine :::::::"+ orderLine);
    		ItemDAS itemDas = new ItemDAS();
    		CustomerDTO customer = freeUsagePool.getCustomer();
    		List<CustomerUsagePoolDTO> freeUsagePools = customer.getCustomerUsagePools();
    		
	    	if (orderLine.getCustomerUsagePoolQuantity() != null && orderLine.getId() > 0) {
	    		Boolean usagePoolExist = false;
	    		for (OrderLineUsagePoolDTO orderLineUsagePool : orderLine.getOrderLineUsagePools()) {
	    			LOG.debug("freeUsageQuantity :::::::"+ freeUsageQuantity);
	    			// set the quantity for free usage, matching by customer usage pool id.
	    			if (orderLineUsagePool.hasCustomerUsagePool() && orderLineUsagePool.getCustomerUsagePool().getId() == freeUsagePool.getId() ) {
	    				//for multiple Customer Usage pools
	    				if (freeUsagePools.size() > 1 && !orderLine.isMediated()) {
	    					orderLineUsagePool.setQuantity(orderLineUsagePool.getQuantity().add(freeUsageQuantity));
	    				} else {
	    					orderLineUsagePool.setQuantity(freeUsageQuantity);
	    				}
	    				usagePoolExist = true;
	    				break;
	    			} 
	    		}
	    		if (!usagePoolExist && freeUsagePool.getAllItems().contains(itemDas.find(result.getItemId()))) {
		    		if (orderLine.getOrderLineUsagePools() == null ) orderLine.setOrderLineUsagePools(new HashSet<>());
                    orderLine.getOrderLineUsagePools().add(new OrderLineUsagePoolDTO(0, orderLine, freeUsageQuantity, freeUsagePool));
	    		}
	    	} else {
//    			orderLine.getOrderLineUsagePools().clear();
	  //  		if (!orderLine.hasOrderLineUsagePools()) {
		    		// no order line usage pools exist, so create new one for free usage
	    		if (freeUsageQuantity.compareTo(BigDecimal.ZERO) > 0) {
                    if (orderLine.getOrderLineUsagePools() == null ) orderLine.setOrderLineUsagePools(new HashSet<>());
                    orderLine.getOrderLineUsagePools().add(new OrderLineUsagePoolDTO(0, orderLine, freeUsageQuantity, freeUsagePool));
	    		}
	    //		}
	    	}
    	} else {
    		// order line is removed
    		// orderLine.getOrderLineUsagePools().clear();
    	}
    }

    public boolean equalsModel(PriceModelDTO that) {
        if (this == that) return true;
        if (that == null) return false;

        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (rate != null ? !rate.equals(that.rate) : that.rate != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceModelDTO that = (PriceModelDTO) o;

        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (rate != null ? !rate.equals(that.rate) : that.rate != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PriceModelDTO{"
               + "id=" + id
               + ", type=" + type
               + ", attributes=" + attributes
               + ", rate=" + rate
               + ", currencyId=" + (currency != null ? currency.getId() : null)
               + ", next=" + next
               + '}';
    }

    public String getAuditKey(Serializable id) {
        // todo: needs some back-references so that we can log the owning entity id and item id (or whatever its attached to)
        return id.toString();
    }
}
