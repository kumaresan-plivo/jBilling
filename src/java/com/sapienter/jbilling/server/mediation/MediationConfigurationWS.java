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

package com.sapienter.jbilling.server.mediation;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import com.sapienter.jbilling.server.security.WSSecured;
import org.hibernate.validator.constraints.Range;

/**
 * MediationConfigurationWS
 * 
 * @author Brian Cowdery
 * @since 21-10-2010
 */

public class MediationConfigurationWS implements WSSecured, Serializable {

	private Integer id;
	private Integer entityId;
	private Integer processorTaskId;
	//@NotNull(message = "validation.error.notnull")
	private Integer pluggableTaskId;
	@NotNull(message="validation.error.notnull")
	@NotEmpty(message = "validation.error.notnull")
	@Size(min = 0, max = 150, message = "validation.error.size,0,150")
	private String name;
	@Digits(integer = 10, fraction = 0, message="mediation.validation.error.not.a.integer")
	private String orderValue;
	private Date createDatetime;
	private Integer versionNum;
	private String mediationJobLauncher;
	private Boolean global= Boolean.FALSE;
	private Integer rootRoute;
	private String localInputDirectory;

	public MediationConfigurationWS() {
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public Integer getPluggableTaskId() {
		return pluggableTaskId;
	}

	public void setPluggableTaskId(Integer pluggableTaskId) {
		this.pluggableTaskId = pluggableTaskId;
	}

	public Integer getProcessorTaskId() {
		return processorTaskId;
	}

	public void setProcessorTaskId(Integer processorTaskId) {
		this.processorTaskId = processorTaskId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrderValue() {
		Integer value = null;
		try {
			if (StringUtils.trimToNull(orderValue) != null) {
				value = Integer.parseInt(orderValue);
			}
		} catch (NumberFormatException nfe) {
			value = null;
		}
		return value;
	}

	public void setOrderValue(String orderValue) {
		this.orderValue = orderValue;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

    public Integer getOwningEntityId() {
        return getEntityId();
    }

    /**
     * Unsupported, web-service security enforced using {@link #getOwningEntityId()}
     * @return null
     */
    public Integer getOwningUserId() {
        return null;
    }

	public String getMediationJobLauncher() {
		return mediationJobLauncher;
	}

	public void setMediationJobLauncher(String mediationJobLauncher) {
		this.mediationJobLauncher = mediationJobLauncher;
	}

	public Boolean getGlobal() {
		return global;
	}

	public void setGlobal(Boolean global) {
		this.global = global;
	}

	public Integer getVersionNum() {
		return versionNum;
	}

	public void setVersionNum(Integer versionNum) {
		this.versionNum = versionNum;
	}

	public Integer getRootRoute() {
		return rootRoute;
	}

	public void setRootRoute(Integer rootRoute) {
		this.rootRoute = rootRoute;
	}

	public String getLocalInputDirectory() {
		return localInputDirectory;
	}

	public void setLocalInputDirectory(String localInputDirectory) {
		this.localInputDirectory = localInputDirectory;
	}

	@Override
	public String toString() {
		return "MediationConfigurationWS{" + "id=" + id + ", entityId="
				+ entityId + ", pluggableTaskId=" + pluggableTaskId
				+ ", processorTaskId=" + processorTaskId + ", name='" + name
				+ '\'' + ", orderValue=" + orderValue + ", createDatetime="
				+ createDatetime + ", mediationJobLauncher="
				+ mediationJobLauncher + ", global=" + global + ", versionNum="
				+ versionNum + ", rootRoute=" + rootRoute + '}';
	}

}
