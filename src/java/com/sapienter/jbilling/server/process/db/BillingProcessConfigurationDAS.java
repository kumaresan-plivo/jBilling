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

package com.sapienter.jbilling.server.process.db;

import java.util.Date;
import java.util.List;

import com.sapienter.jbilling.server.util.Context;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.server.user.db.CompanyDTO;
import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author abimael
 */
public class BillingProcessConfigurationDAS extends AbstractDAS<BillingProcessConfigurationDTO> {


    public BillingProcessConfigurationDTO create(CompanyDTO entity,
                                                 Date nextRunDate, Integer generateReport) {
        BillingProcessConfigurationDTO nuevo = new BillingProcessConfigurationDTO();
        nuevo.setEntity(entity);
        nuevo.setNextRunDate(nextRunDate);
        nuevo.setGenerateReport(generateReport);

        return save(nuevo);
    }

    public BillingProcessConfigurationDTO findByEntity(CompanyDTO entity) {
        Criteria criteria = getSession().createCriteria(BillingProcessConfigurationDTO.class);
        criteria.add(Restrictions.eq("entity", entity));
        return (BillingProcessConfigurationDTO) criteria.uniqueResult();
    }

    public List<BillingProcessConfigurationDTO> findAllByEntity(CompanyDTO entity) {
        Criteria criteria = getSession().createCriteria(BillingProcessConfigurationDTO.class);
        criteria.add(Restrictions.eq("entity", entity));
        return criteria.list();
    }

    public void copyBillingProcessConfiguration(Integer entityId, Integer targetEntityId) {
        String hql = "INSERT INTO billing_process_configuration (id,entity_id,only_recurring,next_run_date, generate_report, review_status," +
                " retries, days_for_retry, days_for_report, df_fm, " +
                "   due_date_unit_id, due_date_value, invoice_date_process, optlock, maximum_periods, auto_payment_application)" +
                "   (select  " +
                "   (select max(id) + 1 from billing_process_configuration) as id , " +
                targetEntityId + "," +
                "    billing_process_configuration.only_recurring," +
                "    billing_process_configuration.next_run_date," +
                "    billing_process_configuration.generate_report," +
                "    billing_process_configuration.review_status," +
                "    billing_process_configuration.retries," +
                "    billing_process_configuration.days_for_retry," +
                "    billing_process_configuration.days_for_report," +
                "    billing_process_configuration.df_fm," +
                "    billing_process_configuration.due_date_unit_id," +
                "     billing_process_configuration.due_date_value," +
                "  billing_process_configuration.invoice_date_process," +
                "   billing_process_configuration.optlock," +
                "    billing_process_configuration.maximum_periods," +
                "     billing_process_configuration.auto_payment_application" +
                "     from billing_process_configuration where entity_id=" + entityId + ");";


        JdbcTemplate jdbcTemplate = (JdbcTemplate) Context.getBean(Context.Name.JDBC_TEMPLATE);

        jdbcTemplate.execute(hql);
    }

}
