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

package com.sapienter.jbilling.server.user.db;

import com.sapienter.jbilling.server.fileProcessing.FileConstants;
import com.sapienter.jbilling.server.invoice.db.InvoiceDeliveryMethodDAS;
import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.*;

import java.util.List;

public class CustomerDAS extends AbstractDAS<CustomerDTO> {
    public CustomerDTO create() {
        CustomerDTO newCustomer = new CustomerDTO();
        newCustomer.setInvoiceDeliveryMethod(new InvoiceDeliveryMethodDAS()
                .find(Constants.D_METHOD_EMAIL));
        newCustomer.setExcludeAging(0);
        return save(newCustomer);
    }

    public Integer getCustomerId(Integer userId){
        Criteria criteria = getSession().createCriteria(CustomerDTO.class);
        criteria.add(Restrictions.eq("baseUser.id", userId));
        criteria.setProjection(Projections.id());
        return (Integer) criteria.uniqueResult();
    }

    public List<Integer> getCustomerAccountInfoTypeIds(Integer customerId){
        DetachedCriteria atCriteria = DetachedCriteria.forClass(CustomerDTO.class);
        atCriteria.add(Restrictions.idEq(customerId));
        atCriteria.setProjection(Projections.property("accountType.id"));
        atCriteria.addOrder(Order.asc("id"));

        Criteria criteria = getSession().createCriteria(AccountInformationTypeDTO.class);
        criteria.setProjection(Projections.id());
        criteria.add(Subqueries.propertyEq("accountType.id", atCriteria));

        return criteria.list();
    }

    public Long countAllByInvoiceTemplate(Integer templateId){
        Criteria crit = getSession().createCriteria(getPersistentClass());
        crit.setProjection(Projections.rowCount());
        crit.add(Restrictions.eq("invoiceTemplate.id", templateId));
        return (Long)crit.uniqueResult();
    }

    public ScrollableResults findAllByCompanyId(Integer companyId){
        Criteria criteria = getSession().createCriteria(CustomerDTO.class, "customer");
        criteria.createAlias("customer.baseUser", "user");
        criteria.add(Restrictions.eq("user.company.id", companyId));
        return criteria.scroll(ScrollMode.FORWARD_ONLY);
    }

    public Integer getCustomerIdByPrimaryAsset(String assetIdentifier){
        String query = "select id from customer where id = (select customer_id from customer_meta_field_map cmf, meta_field_value mfv, meta_field_name mfn where cmf.meta_field_value_id = mfv.id and mfv.meta_field_name_id = mfn.id and mfn.name = :customerAccountNumber and mfv.string_value = :assetIdentifier)";
        SQLQuery sqlQuery= getSession().createSQLQuery(query);
        sqlQuery.setParameter("customerAccountNumber", FileConstants.UTILITY_CUST_ACCT_NR);
        sqlQuery.setParameter("assetIdentifier", assetIdentifier);
        return (Integer) sqlQuery.uniqueResult();
    }
}
