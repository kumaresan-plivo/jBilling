package com.sapienter.jbilling.server.filter;

import jbilling.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marcolin on 04/11/15.
 */
public class JbillingFilterConverter {

    public static List<com.sapienter.jbilling.server.filter.Filter> convert(List<jbilling.Filter> filters) {
        return filters.stream().map(JbillingFilterConverter::convert)
                .filter(f -> f.getValue() != null).collect(Collectors.toList());
    }

    public static com.sapienter.jbilling.server.filter.Filter convert(jbilling.Filter filter) {
        switch (filter.getType()) {
            case MEDIATIONPROCESS:
                return convertMediationProcess(filter);
            default:
                return new Filter(filter.getField(), FilterConstraint.valueOf(filter.getConstraintType().name()),
                        filter.getValue() != null ? "" + filter.getValue() : null);
        }
    }

    public static com.sapienter.jbilling.server.filter.Filter convertMediationProcess(jbilling.Filter filter) {
        if (filter.getField().equals("errors") && filter.getValue() != null) {
            if (filter.getIntegerValue() == 1) return new Filter(filter.getField(), FilterConstraint.GREATER_THAN, 0);
            else return new Filter(filter.getField(), FilterConstraint.EQ, 0);
        }
        return new Filter(filter.getField(), FilterConstraint.valueOf(filter.getConstraintType().name()),
                filter.getValue() != null ? "" + filter.getValue() : null);
    }
}
