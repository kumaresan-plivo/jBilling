/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2013] Enterprise jBilling Software Ltd.
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

package com.sapienter.jbilling.server.usagePool;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.item.db.PlanDAS;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.order.db.OrderPeriodDAS;
import com.sapienter.jbilling.server.order.db.OrderPeriodDTO;
import com.sapienter.jbilling.server.system.event.EventManager;
import com.sapienter.jbilling.server.usagePool.db.CustomerUsagePoolDAS;
import com.sapienter.jbilling.server.usagePool.db.CustomerUsagePoolDTO;
import com.sapienter.jbilling.server.usagePool.db.UsagePoolDAS;
import com.sapienter.jbilling.server.usagePool.db.UsagePoolDTO;
import com.sapienter.jbilling.server.usagePool.event.CustomerUsagePoolConsumptionEvent;
import com.sapienter.jbilling.server.user.db.CustomerDAS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.MapPeriodToCalendar;

/**
 * CustomerUsagePoolBL
 * Server side code for handling of Customer Usage Pool association. 
 * It has functions for CRUD of customer usage pool, calculation of cycle end date, 
 * and a business method for evaluation task that updates the customer usage pool 
 * from scheduled batch program.
 * @author Amol Gadre
 * @since 01-Dec-2013
 */

public class CustomerUsagePoolBL {

	private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(CustomerUsagePoolBL.class));
	private CustomerUsagePoolDAS customerUsagePoolDas = null;
	private CustomerUsagePoolDTO customerUsagePool = null;
	
	public CustomerUsagePoolBL() {
	
	}

	public CustomerUsagePoolBL(Integer customerUsagePoolId) throws SessionInternalError {
        try {
            init();
            set(customerUsagePoolId);
        } catch (Exception e) {
            throw new SessionInternalError("Setting Usage Pool", CustomerUsagePoolBL.class, e);
        }
    }
	
	private void init() {
		customerUsagePoolDas = new CustomerUsagePoolDAS();
	}
	
	public void set(Integer customerUsagePoolId) {
		customerUsagePool = customerUsagePoolDas.find(customerUsagePoolId);
	}
	
	/**
	 * Parametrized constructor that returns a CustomerUsagePoolWS
	 * object instance from the CustomerUsagePoolDTO parameter given to it.
	 * This constructor is useful to return the ws object after converting it from dto.
	 * @param dto
	 */
	public CustomerUsagePoolWS getWS(CustomerUsagePoolDTO dto) {
    	if (customerUsagePool == null) {
    		customerUsagePool = dto;
        }
        return getCustomerUsagePoolWS(dto);
    }
	
	public static final CustomerUsagePoolWS getCustomerUsagePoolWS(CustomerUsagePoolDTO dto) {
    	
    	CustomerUsagePoolWS ws = new CustomerUsagePoolWS();
    	ws.setId(dto.getId());
		ws.setQuantity(null != dto.getQuantity() ? dto.getQuantity().toString() : "");
		if (dto.getCustomer() != null) {
			ws.setCustomerId(dto.getCustomer().getId());
			ws.setUserId(dto.getCustomer().getBaseUser().getId());
		}
		if (dto.getUsagePool() != null) {
			ws.setUsagePoolId(dto.getUsagePool().getId());
		}
		if (dto.getPlan() != null) {
			ws.setPlanId(dto.getPlan().getId());
		}
		ws.setCycleEndDate(dto.getCycleEndDate());
		ws.setVersionNum(dto.getVersionNum());
        ws.setUsagePool(UsagePoolBL.getUsagePoolWS(dto.getUsagePool()));
        return ws;
    }
	
	public CustomerUsagePoolDTO getEntity() {
		return customerUsagePool;
	}
	
	/**
	 * This method converts this CustomerUsagePoolWS object instance
	 * into CustomerUsagePoolDTO and returns the same
	 * @return CustomerUsagePoolDTO
	 */
	public static final CustomerUsagePoolDTO getDTO(CustomerUsagePoolWS ws) {
		CustomerUsagePoolDTO dto = new CustomerUsagePoolDTO();
        if (ws.getId() != null) {
        	dto.setId(ws.getId());
        }
        dto.setCustomer(new CustomerDAS().find(ws.getCustomerId()));
        dto.setUsagePool(new UsagePoolDAS().find(ws.getUsagePoolId()));
        dto.setPlan(new PlanDAS().find(ws.getPlanId()));
        dto.setQuantity(null != ws.getQuantity() && !ws.getQuantity().isEmpty() ? new BigDecimal(ws.getQuantity()) : null);
        dto.setCycleEndDate(ws.getCycleEndDate());
        dto.setVersionNum(ws.getVersionNum());
        return dto;
    }
	
	/**
	 * Persists the customer usage pool, after getting the dto object that needs to be saved.
     * The same method can be used to create a new customer usage pool or update an existing one.
	 * @param customerUsagePoolDto
	 * @return customerUsagePoolDto
	 */
	public CustomerUsagePoolDTO createOrUpdateCustomerUsagePool(CustomerUsagePoolDTO customerUsagePoolDto) {
    	
    	if (customerUsagePoolDto.getId() > 0 ) {
    		this.customerUsagePool = new CustomerUsagePoolDAS().findForUpdate(customerUsagePoolDto.getId());
    	} else {
    		this.customerUsagePool = new CustomerUsagePoolDTO();
    	}
    	
    	if (null != customerUsagePoolDto.getCustomer()) {
    		this.customerUsagePool.setCustomer(customerUsagePoolDto.getCustomer());
    	}
    	
    	if (null != customerUsagePoolDto.getUsagePool()) {
    		this.customerUsagePool.setUsagePool(customerUsagePoolDto.getUsagePool());
    	}
    	
    	if (null != customerUsagePoolDto.getPlan()) {
    		this.customerUsagePool.setPlan(customerUsagePoolDto.getPlan());
    	}
    	
    	if (null != customerUsagePoolDto.getQuantity()) {
    		this.customerUsagePool.setQuantity(customerUsagePoolDto.getQuantity());
    	}
    	
    	if (null != customerUsagePoolDto.getQuantity()) {
    		this.customerUsagePool.setInitialQuantity(customerUsagePoolDto.getInitialQuantity());
    	}
    	
    	if (null != customerUsagePoolDto.getCycleEndDate()) {
    		this.customerUsagePool.setCycleEndDate(customerUsagePoolDto.getCycleEndDate());
    	}
    	
    	this.customerUsagePool.setVersionNum(customerUsagePoolDto.getVersionNum());
    	
    	this.customerUsagePool = new CustomerUsagePoolDAS().save(this.customerUsagePool);
    	
    	return this.customerUsagePool != null ? this.customerUsagePool : null;
    }
	
	/**
	 * This method returns a list of CustomerUsagePoolDTO based on customer id.
	 * @return List<CustomerUsagePoolDTO>
	 */
	public List<CustomerUsagePoolDTO> getCustomerUsagePoolsByCustomerId() {
        return customerUsagePoolDas.findAllCustomerUsagePoolsByCustomerId(customerUsagePool.getCustomer().getId());
    }
	
	/**
	 * This method returns a list of CustomerUsagePoolDTO based on 
	 * customer id provided to it as an input parameter.
	 * @return List<CustomerUsagePoolDTO>
	 */
	public List<CustomerUsagePoolDTO> getCustomerUsagePoolsByCustomerId(Integer customerId) {
        return new CustomerUsagePoolDAS().getCustomerUsagePoolsByCustomerId(customerId);
    }
	
	/**
	 * This method calculates the cycle end date for a customer usage pool 
	 * based on cycle period unit and cycle period value specified on the 
	 * system level usage pool. In case the cycle period unit is specified as
	 * 'Billing Periods', then the order period is added to period start date.
	 * The cycle end date is calculated from the period start date provided to it.
	 * @param cyclePeriodUnit
	 * @param cyclePeriodValue
	 * @param periodStartDate
	 * @param orderPeriod
	 * @return Date - cycleEndDate of CustomerUsagePool
	 */
	public Date getCycleEndDateForPeriod(String cyclePeriodUnit, Integer cyclePeriodValue, Date periodStartDate, OrderPeriodDTO orderPeriod) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(periodStartDate);
		if (cyclePeriodUnit.equals(Constants.USAGE_POOL_CYCLE_PERIOD_DAYS)) {
			cal.add(Calendar.DATE, cyclePeriodValue);
		} else if (cyclePeriodUnit.equals(Constants.USAGE_POOL_CYCLE_PERIOD_MONTHS)) {
			cal.add(Calendar.MONTH, cyclePeriodValue);
		} else if (cyclePeriodUnit.equals(Constants.USAGE_POOL_CYCLE_PERIOD_BILLING_PERIODS)) {
			if (null != orderPeriod) {
				Integer orderPeriodValue = orderPeriod.getValue();
				cal.add(MapPeriodToCalendar.map(orderPeriod.getUnitId()), orderPeriodValue);
			}
		}
		cal.set(Calendar.MILLISECOND, 999);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.HOUR_OF_DAY,23);
		return cal.getTime();
	}
	
	/**
	 * This method provides the CustomerUsagePoolDTO based on the various input fields given below.
	 * The CustomerUsagePoolDTO returned by this method is to be used for persisting it to the db.
	 * @param usagePoolId
	 * @param customerId
	 * @param subscriptionStartDate
	 * @param orderPeriod
	 * @return CustomerUsagePoolDTO
	 */
	public CustomerUsagePoolDTO getCreateCustomerUsagePoolDto(Integer usagePoolId, Integer customerId, 
			Date subscriptionStartDate, OrderPeriodDTO orderPeriod, PlanDTO plan, Date orderActiveUntilDate, Date orderCreatedDate) {
		
		CustomerUsagePoolDTO customerUsagePoolDTO = new CustomerUsagePoolDTO();
		UsagePoolDTO usagePoolDto = new UsagePoolDAS().find(usagePoolId);
		customerUsagePoolDTO.setCustomer(new CustomerDAS().find(customerId));
		customerUsagePoolDTO.setUsagePool(usagePoolDto);
		customerUsagePoolDTO.setPlan(plan);
		customerUsagePoolDTO.setQuantity(usagePoolDto.getQuantity());
		customerUsagePoolDTO.setInitialQuantity(usagePoolDto.getQuantity());
		
		Date cycleEndDate = getCycleEndDateForPeriod(usagePoolDto.getCyclePeriodUnit(), 
				usagePoolDto.getCyclePeriodValue(), subscriptionStartDate, orderPeriod);
		
		if (cycleEndDate.compareTo(new Date()) < 0) {
			cycleEndDate = getCycleEndDateForPeriod(usagePoolDto.getCyclePeriodUnit(), 
					usagePoolDto.getCyclePeriodValue(), orderCreatedDate, orderPeriod);
		}
		
		if (null != orderActiveUntilDate && cycleEndDate.compareTo(orderActiveUntilDate) > 0) {
				cycleEndDate = orderActiveUntilDate;
				Calendar cal = Calendar.getInstance();
				cal.setTime(cycleEndDate);
				cal.set(Calendar.MILLISECOND, 999);
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.HOUR_OF_DAY,23);
				cycleEndDate = cal.getTime();
		}
		
		customerUsagePoolDTO.setCycleEndDate(cycleEndDate);
		customerUsagePoolDTO.setVersionNum(1);
        return customerUsagePoolDTO;
	}
	
	/**
	 * This is a method that gets called from CustomerUsagePoolEvaluationTask.
	 * It evaluates and updates the customer usage pools by looking at all customer usage pool records
	 * that are eligible for update. The update is done for 2 fields: cycle end date and quantity.
	 */
	public void triggerCustomerUsagePoolEvaluation(Integer entityId) {
		
		List<Integer> customerUsagePools = new CustomerUsagePoolDAS().findCustomerUsagePoolsForEvaluation(entityId);
		
		LOG.debug("customerUsagePools:"+ customerUsagePools); 
		
		if (null != customerUsagePools && !customerUsagePools.isEmpty()) {
			
			CustomerUsagePoolDAS das = new CustomerUsagePoolDAS();
			
			for (Integer customerUsagePoolId : customerUsagePools) {
				
				CustomerUsagePoolDTO customerUsagePool = das.findForUpdate(customerUsagePoolId);
				LOG.debug("customerUsagePool quantity: "+ customerUsagePool.getQuantity()); 
				String resetValue = customerUsagePool.getUsagePool().getUsagePoolResetValue().getResetValue();
				LOG.debug("resetValue: "+ resetValue);
				
				if (resetValue.equals(UsagePoolResetValueEnum.ZERO.toString())) {
					
					CustomerUsagePoolConsumptionEvent qtyChangeEvent = 
							new CustomerUsagePoolConsumptionEvent(
									entityId, 
									customerUsagePool.getId(), 
									customerUsagePool.getQuantity(), 
									BigDecimal.ZERO);
					
					customerUsagePool.setQuantity(BigDecimal.ZERO);
					
					EventManager.process(qtyChangeEvent);
					
				} else  {
					
					Date cycleEndDate = customerUsagePool.getCycleEndDate();
					Date subscriptionStartDate = cycleEndDate;
					String cyclePeriodUnit = customerUsagePool.getUsagePool().getCyclePeriodUnit();
					LOG.debug("cyclePeriodUnit: "+ cyclePeriodUnit); 
					BigDecimal usagePoolQuantity = customerUsagePool.getUsagePool().getQuantity();
					
					customerUsagePool.setCycleEndDate(getCycleEndDateForPeriod(cyclePeriodUnit, 
							customerUsagePool.getUsagePool().getCyclePeriodValue(), subscriptionStartDate, 
							customerUsagePool.getCustomer().getMainSubscription().getSubscriptionPeriod()));
					
					LOG.debug("CycleEndDate: "+ customerUsagePool.getCycleEndDate()); 
					
					if (resetValue.equals(UsagePoolResetValueEnum.ADD_THE_INITIAL_VALUE.toString())) {
						
						BigDecimal newPoolQuantity = customerUsagePool.getQuantity().add(usagePoolQuantity);
						
						CustomerUsagePoolConsumptionEvent qtyChangeEvent = 
								new CustomerUsagePoolConsumptionEvent(
										entityId, 
										customerUsagePool.getId(), 
										customerUsagePool.getQuantity(), 
										newPoolQuantity);
						
						customerUsagePool.setQuantity(newPoolQuantity);
						
						EventManager.process(qtyChangeEvent);
						
					} else if(resetValue.equals(UsagePoolResetValueEnum.RESET_TO_INITIAL_VALUE.toString())) {
						
						CustomerUsagePoolConsumptionEvent qtyChangeEvent = 
								new CustomerUsagePoolConsumptionEvent(
										entityId, 
										customerUsagePool.getId(), 
										customerUsagePool.getQuantity(), 
										usagePoolQuantity);
						
						customerUsagePool.setQuantity(usagePoolQuantity);
						
						EventManager.process(qtyChangeEvent);
					}
				}
				LOG.debug("Quantity: "+ customerUsagePool.getQuantity());
				das.save(customerUsagePool);
			}
		}
	}

}