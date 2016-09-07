package com.sapienter.jbilling.server.mediation.converter.db;

import com.sapienter.jbilling.server.filter.Filter;
import com.sapienter.jbilling.server.filter.FilterConstraint;
import com.sapienter.jbilling.server.mediation.JbillingMediationRecord;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marcolin on 04/11/15.
 */
public class JMRRepositoryDASImpl implements JMRRepositoryDAS{

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<JbillingMediationRecordDao> findMediationRecordsByFilters(Integer page, Integer size, List<Filter> filters) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JbillingMediationRecordDao> criteriaQuery = criteriaBuilder.createQuery(JbillingMediationRecordDao.class);
        Root<JbillingMediationRecordDao> root = criteriaQuery.from(JbillingMediationRecordDao.class);
        List<Predicate> predicates = filters.stream().map(f -> createPredicate(f, criteriaBuilder, root)).filter(p -> p != null).collect(Collectors.toList());
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        criteriaQuery.orderBy(criteriaBuilder.desc(getJbillingRecordPathForString("eventDate", root)));
        return entityManager.createQuery(criteriaQuery).setFirstResult(page).setMaxResults(size).getResultList();
    }


    @Override
    public List<JbillingMediationErrorRecordDao> findMediationErrorRecordsByFilters(Integer page, Integer size, List<Filter> filters) {
        filters = new ArrayList<>(filters);
        filters.add(new Filter("errorCodes", FilterConstraint.NOT_LIKE, "JB-DUPLICATE"));
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JbillingMediationErrorRecordDao> criteriaQuery = criteriaBuilder.createQuery(JbillingMediationErrorRecordDao.class);
        Root<JbillingMediationErrorRecordDao> root = criteriaQuery.from(JbillingMediationErrorRecordDao.class);
        List<Predicate> predicates = filters.stream().map(f -> createErrorPredicate(f, criteriaBuilder, root)).filter(p -> p != null).collect(Collectors.toList());
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        return entityManager.createQuery(criteriaQuery).setFirstResult(page).setMaxResults(size).getResultList();
    }

    @Override
    public List<JbillingMediationErrorRecordDao> findMediationDuplicateRecordsByFilters(Integer page, Integer size, List<Filter> filters) {
        filters = new ArrayList<>(filters);
        filters.add(new Filter("errorCodes", FilterConstraint.LIKE, "JB-DUPLICATE"));
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JbillingMediationErrorRecordDao> criteriaQuery = criteriaBuilder.createQuery(JbillingMediationErrorRecordDao.class);
        Root<JbillingMediationErrorRecordDao> root = criteriaQuery.from(JbillingMediationErrorRecordDao.class);
        List<Predicate> predicates = filters.stream().map(f -> createErrorPredicate(f, criteriaBuilder, root)).filter(p -> p != null).collect(Collectors.toList());
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        return entityManager.createQuery(criteriaQuery).setFirstResult(page).setMaxResults(size).getResultList();
    }



    private Predicate createPredicate(Filter filter, CriteriaBuilder criteriaBuilder, Root root) {
        Path objectPath = getJbillingRecordPathForString(filter.getFieldString(), root);
        return createPredicate(filter, criteriaBuilder, objectPath);
    }

    private Predicate createErrorPredicate(Filter filter, CriteriaBuilder criteriaBuilder, Root root) {
        Path objectPath = getJbillingErrorRecordPathForString(filter.getFieldString(), root);
        return createPredicate(filter, criteriaBuilder, objectPath);
    }

    private Predicate createPredicate(Filter filter, CriteriaBuilder criteriaBuilder, Path objectPath) {
        Predicate predicate = null;
        switch (filter.getConstraint()) {
            case EQ:
                if (filter.getValue() != null)
                    predicate = criteriaBuilder.equal(objectPath, filter.getValue());
                break;
            case GREATER_THAN:
                if (filter.getValue() != null)
                    predicate = criteriaBuilder.gt(objectPath, (Integer) filter.getValue());
                break;
            case DATE_BETWEEN:
                if (filter.getStartDate() != null && filter.getEndDate() != null)
                    predicate = criteriaBuilder.between(objectPath, filter.getStartDate(), filter.getEndDate());
                break;
            case NOT_LIKE:
                predicate = criteriaBuilder.notLike(objectPath, "%" + filter.getValue() + "%" );
                break;
            case LIKE:
                predicate = criteriaBuilder.like(objectPath, "%" + filter.getValue() + "%");
                break;
        }
        return predicate;
    }

    private Path getJbillingRecordPathForString(String path, Root root) {
        return getPathConsideringIdClassAttributes(path, root, Arrays.asList("eventDate", "recordKey", "recordKey"));
    }

    private Path getJbillingErrorRecordPathForString(String path, Root root) {
        return getPathConsideringIdClassAttributes(path, root, Arrays.asList("jBillingCompanyId", "mediationCfgId", "recordKey"));
    }

    private Path getPathConsideringIdClassAttributes(String path, Root<Object> root, List<String> idClassAttributesNames) {
        if (idClassAttributesNames.stream().anyMatch(s -> s.equals(path))) {
            return root.get(root.getModel().getIdClassAttributes().stream()
                    .filter(singularAttribute -> singularAttribute.getName().equals(path)).findFirst().get());
        } else{
            return root.get(path);
        }
    }

}
