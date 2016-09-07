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
package com.sapienter.jbilling.server.order.db;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class OrderProcessDAS extends AbstractDAS<OrderProcessDTO> {
    
    //used to check of the order has any invoices (non deleted not cancelled)
    public List<Integer> findActiveInvoicesForOrder(Integer orderId) {

        String hql = "select pr.invoice.id" +
                     "  from OrderProcessDTO pr " +
                     "  where pr.purchaseOrder.id = :orderId" +
                     "    and pr.invoice.deleted = 0" + 
                     "    and pr.isReview = 0";

        List<Integer> data = getSession()
                        .createQuery(hql)
                        .setParameter("orderId", orderId)
                        .setComment("OrderProcessDAS.findActiveInvoicesForOrder " + orderId)
                        .list();
        return data;
    }

    public List<Integer> findByBillingProcess(Integer processId) {

        String hql = "select pr.id" +
                "  from OrderProcessDTO pr " +
                "  where pr.billingProcess.id =:processId";

        List<Integer> data = getSession()
                .createQuery(hql)
                .setParameter("processId", processId)
                .list();
        return data;
    }
    
    /**
     * Get Minimum Period start date of order from order_process table when isReview flag is 0.
     * @param orderId
     * @return
     */
    public Date getFirstInvoicePeriodStartDateByOrderId(Integer orderId) {
        
        String hql = "select min(pr.periodStart) from OrderProcessDTO pr " +
        "where pr.isReview = 0 " +
        "and pr.invoice.deleted = 0 " +
        "and pr.purchaseOrder.deleted = 0 " +
        "and pr.purchaseOrder.id = :orderId";
       
        Query query = getSession().createQuery(hql);
        query.setInteger("orderId", orderId);

        return (Date) query.uniqueResult();
       }

}
