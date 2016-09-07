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
package com.sapienter.jbilling.server.user.contact.db;

import org.hibernate.Query;

import com.sapienter.jbilling.server.util.Constants;
import com.sapienter.jbilling.server.util.db.AbstractDAS;

public class ContactDAS extends AbstractDAS<ContactDTO> {


    public static final String FIND_CONTACT_HQL =
        "SELECT c " +
        "  FROM ContactDTO c, JbillingTable d " +
        " WHERE c.contactMap.jbillingTable.id = d.id " +
        "   AND d.name = :tableName " +
        "   AND c.contactMap.foreignId = :userId " +
        "   AND c.userId = :cUserId ";

    public ContactDTO findContact(Integer userId) {
        Query query = getSession().createQuery(FIND_CONTACT_HQL);
        query.setParameter("userId", userId);
        query.setParameter("cUserId", userId);
        query.setParameter("tableName", Constants.TABLE_BASE_USER);
        return (ContactDTO) query.uniqueResult();
    }


    public static final String FIND_SIMPLE_CONTACT_HQL =
        "SELECT c " +
        "  FROM ContactDTO c, JbillingTable d " +
        " WHERE c.contactMap.jbillingTable.id = d.id " +
        "   AND d.name = :tableName " +
        "   AND c.contactMap.foreignId = :id ";

    public ContactDTO findEntityContact(Integer entityId) {
        Query query = getSession().createQuery(FIND_SIMPLE_CONTACT_HQL);
        query.setParameter("id", entityId);
        query.setParameter("tableName", Constants.TABLE_ENTITY);
        query.setCacheable(true);
        return (ContactDTO) query.uniqueResult();
    }

    public ContactDTO findInvoiceContact(Integer invoiceId) {
        Query query = getSession().createQuery(FIND_SIMPLE_CONTACT_HQL);
        query.setParameter("id", invoiceId);
        query.setParameter("tableName", Constants.TABLE_INVOICE);
        return (ContactDTO) query.uniqueResult();
    }

}
