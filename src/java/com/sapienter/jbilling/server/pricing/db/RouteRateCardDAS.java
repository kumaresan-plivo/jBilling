package com.sapienter.jbilling.server.pricing.db;

import org.hibernate.Criteria;
import java.util.regex.Pattern;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;

import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class RouteRateCardDAS extends AbstractDAS<RouteRateCardDTO> {
	
    private static Pattern ValidSqlIdentifierPattern = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    private void validateSqlIdentifier (String sqlIdentifierName) {
        if (!ValidSqlIdentifierPattern.matcher(sqlIdentifierName).matches()) {
            throw new IllegalArgumentException("Invalid SQL identifier (table or column name, etc...): " + sqlIdentifierName);
        }
    }

    public ScrollableResults getRouteRateCardTableRows(String tableName) {
        validateSqlIdentifier(tableName);
        Query query = getSession().createSQLQuery("select * from " + tableName);
		return query.scroll();
	}
    
    /**
     * Gets the longest value in the given rate card table for the given matching field.
     * @param tableName
     * @param matchingField
     * @return
     */
    public Integer getLongestValue(String tableName, String matchingField) {
        validateSqlIdentifier(tableName);
        validateSqlIdentifier(matchingField);
		Query query = getSession().createSQLQuery(
				new StringBuffer("select max(char_length(")
						.append(matchingField).append(")) from ")
						.append(tableName).toString());
		Number longestVal = (Number) query.uniqueResult();
		return longestVal.intValue();
    }
    
    public Integer getSmallestValue(String tableName, String matchingField) {
        validateSqlIdentifier(tableName);
        validateSqlIdentifier(matchingField);
		Query query = getSession().createSQLQuery(
				new StringBuffer("select max(char_length(")
						.append(matchingField).append(")) from ")
						.append(tableName).toString());
		Number smallestVal = (Number) query.uniqueResult();
		return smallestVal.intValue();
    }

    public List<RouteRateCardDTO> getRouteRateCardsByEntity(Integer entityId) {
        Criteria criteria = getSession().createCriteria(RouteRateCardDTO.class)
                .createAlias("company", "company")
                .add(Restrictions.eq("company.id", entityId))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }
}
