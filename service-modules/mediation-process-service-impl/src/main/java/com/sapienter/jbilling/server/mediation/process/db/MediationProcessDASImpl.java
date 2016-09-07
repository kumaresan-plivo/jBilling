package com.sapienter.jbilling.server.mediation.process.db;

import com.sapienter.jbilling.server.filter.Filter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bilal on 10/29/15
 */
public class MediationProcessDASImpl implements MediationProcessDAS {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<MediationProcessDAO> findMediationProcessByFilters(int page, int size, String sort, String order, List<Filter> filters) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MediationProcessDAO> criteriaQuery = criteriaBuilder.createQuery(MediationProcessDAO.class);
        Root<MediationProcessDAO> root = criteriaQuery.from(MediationProcessDAO.class);
        List<Predicate> predicates = filters.stream().map(f -> createPredicate(f, criteriaBuilder, root)).collect(Collectors.toList());
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        if(order.equals("desc")) {
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get(sort)));
        }else{
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get(sort)));
        }
        return entityManager.createQuery(criteriaQuery).setFirstResult(page).setMaxResults(size).getResultList();
    }

    private Predicate createPredicate(Filter filter, CriteriaBuilder criteriaBuilder, Root<MediationProcessDAO> root) {
        Predicate predicate = null;
        switch (filter.getConstraint()) {
            case EQ:
                predicate = criteriaBuilder.equal(root.get(filter.getFieldString()), filter.getValue());
                break;
            case GREATER_THAN:
                predicate = criteriaBuilder.gt(root.get(filter.getFieldString()), (Integer) filter.getValue());
                break;
            case DATE_BETWEEN:
                predicate = criteriaBuilder.between(root.get(filter.getFieldString()), filter.getStartDate(), filter.getEndDate());
                break;
        }
        return predicate;
    }
}
