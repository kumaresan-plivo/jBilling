package com.sapienter.jbilling.server.filter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;

/**
 * Created by marcolin on 27/10/15.
 */
public class Filter {
    private FilterType type;
    private Expression<?> field;
    private String fieldString;
    private FilterConstraint constraint;

    private Boolean booleanValue;
    private String stringValue;
    private Integer integerValue;
    private BigDecimal decimalValue;
    private BigDecimal decimalHighValue;
    private UUID uuid;
    private Date startDate;
    private Date endDate;
    private String fieldKeyData;

    @PersistenceContext
    private EntityManager entityManager;

    public Filter(Expression<?> field, FilterConstraint constraint, Boolean booleanValue, String stringValue, Integer integerValue, BigDecimal decimalValue, BigDecimal decimalHighValue, Date startDate, Date endDateValue, String fieldKeyData) {
        this.field = field;
        this.constraint = constraint;
        this.booleanValue = booleanValue;
        this.stringValue = stringValue;
        this.integerValue = integerValue;
        this.decimalValue = decimalValue;
        this.decimalHighValue = decimalHighValue;
        this.startDate = startDate;
        this.endDate = endDateValue;
        this.fieldKeyData = fieldKeyData;
    }

    public Filter(Expression<?> field, FilterConstraint constraint) {
        this.field = field;
        this.constraint = constraint;
    }

    public Filter(String fieldString, FilterConstraint constraint, String stringValue) {
        this.fieldString = fieldString;
        this.constraint = constraint;
        this.stringValue = stringValue;
    }

    public Filter(String fieldString, FilterConstraint constraint, Integer integerValue) {
        this.fieldString = fieldString;
        this.constraint = constraint;
        this.integerValue = integerValue;
    }

    public Filter(String fieldString, FilterConstraint constraint, UUID uuid) {
        this.fieldString = fieldString;
        this.constraint = constraint;
        this.uuid = uuid;
    }

    public Filter(String fieldString, FilterConstraint constraint, Date startDate, Date endDate) {
        this.fieldString = fieldString;
        this.constraint = constraint;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Object getValue() {
        if (booleanValue != null)
            return booleanValue;

        if (stringValue != null)
            return stringValue;

        if (integerValue != null)
            return integerValue;

        if (decimalValue != null)
            return decimalValue;

        if (decimalHighValue != null)
            return decimalHighValue;

        if (startDate != null)
            return startDate;

        if (endDate != null)
            return endDate;

        if (uuid != null)
            return uuid;

        return null;
    }

    public void clear() {
        booleanValue = null;
        stringValue = null;
        integerValue = null;
        decimalValue = null;
        decimalHighValue = null;
        startDate = null;
        endDate = null;
        fieldKeyData = null;
    }

    public Predicate getRestrictions() {
        if (getValue() == null) {
            return null;
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        switch (constraint) {
            case EQ:
                return criteriaBuilder.equal(field, getValue());

            /*case FilterConstraint.LIKE:
                return (Restrictions.ilike(field, stringValue, MatchMode.ANYWHERE))
            break

            case FilterConstraint.DATE_BETWEEN:
                if (startDate != null && endDate != null) {
                    return Restrictions.between(field, startDate, endDate);

                } else if (startDate != null) {
                    return Restrictions.ge(field, startDate);

                } else if (endDate != null) {
                    return Restrictions.le(field, endDate);
                }
                break

            case FilterConstraint.NUMBER_BETWEEN:
                if (decimalValue != null && decimalHighValue != null) {
                    if(type == FilterType.MEDIATIONPROCESS && field.equals("totalRecords")){
                        return Restrictions.between(field, decimalValue.toLong(), decimalHighValue.toLong())
                    }
                    return Restrictions.between(field, decimalValue, decimalHighValue)

                } else if (decimalValue != null) {
                    if(type == FilterType.MEDIATIONPROCESS && field.equals("totalRecords")){
                        return Restrictions.ge(field, decimalValue.toLong())
                    }
                    return Restrictions.ge(field, decimalValue)

                } else if (decimalHighValue != null) {
                    if(type == FilterType.MEDIATIONPROCESS && field.equals("totalRecords")){
                        return Restrictions.le(field, decimalHighValue.toLong())
                    }
                    return Restrictions.le(field, decimalHighValue)
                }
                break

            case FilterConstraint.SIZE_BETWEEN:
                if (decimalValue != null && decimalHighValue != null) {
                    return Restrictions.and(
                            Restrictions.sizeGe(field, decimalValue.intValue()),
                            Restrictions.sizeLe(field, decimalHighValue.intValue())
                    )

                } else if (decimalValue != null) {
                    return Restrictions.sizeGe(field, decimalValue.intValue())

                } else if (decimalHighValue != null) {
                    return Restrictions.sizeLe(field, decimalHighValue.intValue())
                }
                break

            case FilterConstraint.IS_EMPTY:
                if (booleanValue) {
                    return Restrictions.isEmpty(field)
                }
                break

            case FilterConstraint.IS_NOT_EMPTY:
                if (booleanValue) {
                    return Restrictions.isNotEmpty(field)
                }
                break
            case FilterConstraint.IS_NULL:
                if (booleanValue) {
                    return Restrictions.isNull(field)
                }
                break

            case FilterConstraint.IS_NOT_NULL:
                if (booleanValue) {
                    return Restrictions.isNotNull(field)
                }
                break
            case FilterConstraint.IN:
                if(stringValue){
                    return Restrictions.in(field, stringValue.split(',').collect {Integer.parseInt(it)})
                }
                break*/
        }

        return null;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "field='" + field + '\'' +
                ", type=" + type +
                ", constraint=" + constraint +
                ", value=" + getValue() +
                '}';
    }

    public String getFieldString() {
        return fieldString;
    }

    public FilterConstraint getConstraint() {
        return constraint;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
