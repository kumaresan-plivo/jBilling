package com.sapienter.jbilling.server.invoice;

import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @author Juan Vidal - 08Jan16
 */
public class InvoiceTemplateDAS extends AbstractDAS<InvoiceTemplateDTO> {

    @SuppressWarnings("unchecked")
    public List<InvoiceTemplateDTO> findAllInvoiceTemplateByEntity(Integer entityId) {
        DetachedCriteria query = DetachedCriteria.forClass(getPersistentClass());
        query.createAlias("entity", "e")
                .add(Restrictions.eq("e.id", entityId))
                .add(Restrictions.eq("e.deleted", 0));

        return (List<InvoiceTemplateDTO>) getHibernateTemplate().findByCriteria(query);
    }

}
