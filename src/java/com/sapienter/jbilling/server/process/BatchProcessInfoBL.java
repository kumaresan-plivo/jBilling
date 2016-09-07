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

package com.sapienter.jbilling.server.process;

import java.util.*;

import org.apache.log4j.Logger;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.process.db.*;
import com.sapienter.jbilling.server.list.ResultList;

public class BatchProcessInfoBL  extends ResultList {
    private BatchProcessInfoDAS processInfoDas = null;
    private BillingProcessDAS billingProcessDas = null;
    private BillingProcessDTO billingProcess = null;
    private BatchProcessInfoDTO processInfo = null;
    
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(BillingProcessRunBL.class));
    
    public BatchProcessInfoBL(Integer processInfoId) {
        init();
        set(processInfoId);
    }
    
    public BatchProcessInfoBL() {
        init();
    }
    
    private void init() {
       billingProcessDas = new BillingProcessDAS();
       processInfoDas = new BatchProcessInfoDAS();
    }

    public BatchProcessInfoDTO getEntity() {
        return processInfo;
    }
    
    public void set(Integer id) {
        processInfo = processInfoDas.find(id);
    }
    
    public BatchProcessInfoDTO create(Integer billingProcessId, Integer jobExecutionId,
            Integer totalFailedUsers, Integer totalSuccessfulUsers) {
    	billingProcess = billingProcessDas.find(billingProcessId);
    	
    	processInfo = processInfoDas.create(billingProcess, jobExecutionId, totalFailedUsers, totalSuccessfulUsers);
    	return processInfo;
    }

    public List<BatchProcessInfoDTO> findByBillingProcessId (Integer billingProcess) {
    	List<BatchProcessInfoDTO> list = processInfoDas.getEntitiesByBillingProcessId(billingProcess);
    	if(list.size()>0) {
    		return list;
    	}
    	BatchProcessInfoDTO dto = new BatchProcessInfoDTO();
    	dto.setTotalFailedUsers(0);
    	dto.setTotalSuccessfulUsers(0);
    	//dto.setBillingProcess(billingProcessDas.find(billingProcess));
    	dto.setJobExecutionId(0);
    	
    	return Arrays.asList(dto);
    }
}
