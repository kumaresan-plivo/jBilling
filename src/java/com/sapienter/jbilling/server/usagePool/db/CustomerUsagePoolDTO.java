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

package com.sapienter.jbilling.server.usagePool.db;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sapienter.jbilling.server.item.db.ItemDTO;
import com.sapienter.jbilling.server.item.db.PlanDTO;
import com.sapienter.jbilling.server.order.db.OrderLineUsagePoolDTO;
import com.sapienter.jbilling.server.user.db.CustomerDTO;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import javax.persistence.*;

/**
 * CustomerUsagePoolDTO
 * The domain object representing the Customer Usage Pool association. 
 * A customer can have one-to-many CustomerUsagePoolDTOs.
 * @author Amol Gadre
 * @since 01-Dec-2013
 */

@Entity
@TableGenerator(
        name = "customer_usage_pool_GEN",
        table = "jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue = "customer_usage_pool_map",
        allocationSize = 100
)
@Table(name = "customer_usage_pool_map")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CustomerUsagePoolDTO implements Serializable {
	
	private int id;
	private CustomerDTO customer;
	private UsagePoolDTO usagePool;
	private PlanDTO plan;	// this is the plan that created this customer usage pool
	private BigDecimal quantity;
	private BigDecimal initialQuantity;
	private Date cycleEndDate;
	private int versionNum;
	
	public CustomerUsagePoolDTO() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "customer_usage_pool_GEN")
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public boolean hasId() {
		return getId() > 0;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usage_pool_id", nullable = false)
	public UsagePoolDTO getUsagePool() {
		return this.usagePool;
	}
	
	public void setUsagePool(UsagePoolDTO usagePool) {
		this.usagePool = usagePool;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
	public PlanDTO getPlan() {
		return plan;
	}

	public void setPlan(PlanDTO plan) {
		this.plan = plan;
	}

	@Column(name="quantity", precision=17, scale=17)
	public BigDecimal getQuantity() {
		return quantity;
	}
	
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	
	@Column(name="initial_quantity", precision=17, scale=17)
	public BigDecimal getInitialQuantity() {
		return initialQuantity;
	}
	
	public void setInitialQuantity(BigDecimal initialQuantity) {
		this.initialQuantity = initialQuantity;
	}
	
	@Column(name="cycle_end_date")
	public Date getCycleEndDate() {
		return this.cycleEndDate;
	}
	
	public void setCycleEndDate(Date cycleEndDate) {
		this.cycleEndDate = cycleEndDate;
	}

	@Version
    @Column(name = "OPTLOCK")
    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    @Transient
    public List<ItemDTO> getAllItems() {
    	return this.getUsagePool().getAllItems();
    }
    
    @Transient
    public boolean isActive() {
    	Date today = new Date();
    	return this.getCycleEndDate().after(today) || this.getCycleEndDate().equals(today);
    }
    
    @Transient
    public boolean isActiveWithQuantity() {
    	return isActive() && this.getQuantity().compareTo(BigDecimal.ZERO) > 0;
    }
    
    @Transient
    public boolean isExpired() {
    	return this.getCycleEndDate().before(new Date());
    }
	
	@Override
	public String toString() {
		return "CustomerUsagePoolDTO={id=" + this.id + 
				",usagePool=" + this.usagePool +
				",customer=" + this.customer.getId() + 
				",plan=" + this.plan.getId() +
				",quantity=" + this.quantity +
				",cycleEndDate=" + this.cycleEndDate +
				",versionNum=" + this.versionNum +
			"}";
	}
	
	/**
	 * A comparator that is used to sort customer usage pools based on precedence provided at system level usage pools.
	 * If precedence at usage pool level is same, then created date for system level usage pools is considered.
	 */
	@Transient
	public static final Comparator<CustomerUsagePoolDTO> CustomerUsagePoolsByPrecedenceOrCreatedDateComparator = new Comparator<CustomerUsagePoolDTO> () {
		public int compare(CustomerUsagePoolDTO customerUsagePool1, CustomerUsagePoolDTO customerUsagePool2) {
			
			Integer precedence1 = customerUsagePool1.getUsagePool().getPrecedence();
            Integer precedence2 =  customerUsagePool2.getUsagePool().getPrecedence();
            if(precedence1.intValue() == precedence2.intValue()) {
            	
            	Date createDate1 = customerUsagePool1.getUsagePool().getCreatedDate();
                Date createDate2 =  customerUsagePool2.getUsagePool().getCreatedDate();
                
               return createDate1.compareTo(createDate2);
            }
            return precedence1.compareTo(precedence2);
		}
	};

}
