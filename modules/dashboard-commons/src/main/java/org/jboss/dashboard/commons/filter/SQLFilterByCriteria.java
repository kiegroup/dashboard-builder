/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.commons.filter;

import org.apache.commons.lang.StringUtils;
import java.text.SimpleDateFormat;
import java.text.MessageFormat;
import java.util.*;

/**
 * This helper class is addressed to be used by dynamic SQL generation custom algorithms.
 * The main goal is to automate the generation of the SQL criteria part.
 */
public class SQLFilterByCriteria {

    /**
     * The filter to decorate.
     */
    protected FilterByCriteria filter;

    /**
     * The SQL wildcard.
     */
    protected String sqlWildcard;

    public SQLFilterByCriteria() {
        this(new AbstractFilter() {
            protected String formatForDisplay(String propertyId, Object value) {
                return null;
            }
            protected String formatForComparison(String propertyId, Object value) {
                return null;
            }
            protected Object getPropertyValue(String propertyId, Object obj) {
                return null;
            }
        });
    }

    public SQLFilterByCriteria(FilterByCriteria filter) {
        this.filter = filter;
        sqlWildcard = "\u0025";
    }

    public String getSqlWildcard() {
        return sqlWildcard;
    }

    /**
     * An SQL date formatter.
     * TODO: check the cross-compatibility between distinct DBMS and add support for hour-scope search.
     */
    private static SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String SQL_formatValue(Object value) {
        if (value instanceof Date) {
            // Search dates by similarity.
            return SQL_formatValue(SQL_DATE_FORMAT.format(value) + getSqlWildcard());
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Collection) {
            Collection c = (Collection) value;
            if (c.isEmpty()) return "";

            StringBuffer buf = new StringBuffer();
            Iterator it = c.iterator();
            while (it.hasNext()) {
                if (buf.length()> 0) buf.append(",");
                buf.append(SQL_formatValue(it.next()));
            }
            return buf.toString();
        }
        if (value instanceof String) {
            String allowedString = (String) value;
            if (allowedString.indexOf(getWildcard()) != -1) {
                // Catch the wildcard for string inputs and replace it by the SQL wildcard.
                allowedString = StringUtils.replace(allowedString, getWildcard(), getSqlWildcard());
            }
            return "'" + allowedString + "'";
        }
        return value.toString();
    }

    public String SQL_getWhereClause(String propertyId, String propertySQLName) {
        return SQL_getWhereClause(propertyId, propertySQLName, false);
    }

    /**
     * Retrieves the SQL condition that defines the contraints set for the given property.
     * The filter constraints defined for the property determines the structure of the SQL clause.
     * @param propertyId The property identifier.
     * @param sqlColumn The SQL literal identifying the property within the SQL query.
     * @param ignoreCase In case on an String, case sentitiveness control.
     * @return An SQL fragment for usage in a WHERE clause of an SQL query statement.
     * Null if property is not included in the filter.
     * <p>E.g: <code>getSQLWhereClause(TEST_STRING, "p.testString")</code> might return "p.testString IN ('s1','s2')"
     */
    public String SQL_getWhereClause(String propertyId, String sqlColumn, boolean ignoreCase) {
        if (!containsProperty(propertyId)) return null;
        List clause = SQL_getWhereClause(propertyId, sqlColumn, ignoreCase, false);
        if (clause == null) return null;
        return (String) clause.get(0);
    }

    /**
     * Retrieves the SQL condition that defines the contraints set for the given property.
     * The filter constraints defined for the property determines the structure of the SQL clause.
     * @param propertyId The property identifier.
     * @param sqlColumn The SQL literal identifying the property within the SQL query.
     * @param ignoreCase In case on an String, case sentitiveness control.
     * @param forPreparedStatement The SQL fragment is generated to be used in a JDBC prepared statement.
     * @return A list of: SQL fragment plus a set of object instances representing the property contrains.
     * <p>E.g: <code>getPreparedStatementFragment(TEST_STRING, "p.testString", false, true)</code> might
     * return "(p.testString<? and p.testString>?) or p.testString=?"
     */
    public List SQL_getWhereClause(String propertyId, String sqlColumn, boolean ignoreCase, boolean forPreparedStatement) {
        if (!containsProperty(propertyId)) return null;
        List results = new ArrayList();

        // Min. value constraint.
        StringBuffer minValueBuf = new StringBuffer();
        Comparable minValue = getPropertyMinValue(propertyId);
        boolean minValueIncluded = minValueIncluded(propertyId);
        if (minValue != null) {
            minValueBuf.append(sqlColumn);
            if (minValueIncluded) minValueBuf.append(">=");
            else minValueBuf.append(">");

            if (forPreparedStatement) minValueBuf.append("?");
            else minValueBuf.append(SQL_formatValue(minValue).replaceAll(getSqlWildcard(), ""));
            results.add(minValue);
        }
        // Max. value constraint.
        StringBuffer maxValueBuf = new StringBuffer();
        Comparable maxValue = getPropertyMaxValue(propertyId);
        boolean maxValueIncluded = maxValueIncluded(propertyId);
        if (maxValue != null) {
            maxValueBuf.append(sqlColumn);
            if (maxValueIncluded) maxValueBuf.append("<=");
            else maxValueBuf.append("<");

            if (forPreparedStatement) maxValueBuf.append("?");
            else maxValueBuf.append(SQL_formatValue(maxValue).replaceAll(getSqlWildcard(), ""));
            results.add(maxValue);
        }
        // Allowed values constraint.
        StringBuffer allowBuf = new StringBuffer();
        Collection allowedValues = getPropertyAllowedValues(propertyId);
        int allowMode = getPropertyAllowMode(propertyId);
        if (allowedValues != null && !allowedValues.isEmpty()) {
            String equalOperator = ""; // For equality based search.
            String likeOperator = ""; // For wildcard based search.
            String conjunction = "";
            switch(allowMode) {
                case FilterByCriteria.ALLOW_ALL:
                    equalOperator = " = ";
                    likeOperator = " like ";
                    conjunction = " and ";
                    break;
                case FilterByCriteria.ALLOW_ANY:
                    equalOperator = " = ";
                    likeOperator = " like ";
                    conjunction = " or ";
                    break;
                case FilterByCriteria.ALLOW_NONE:
                    equalOperator = " <> ";
                    likeOperator = " not like ";
                    conjunction = " and ";
                    break;
                }
            Iterator it = allowedValues.iterator();
            for (int i=0; it.hasNext(); i++) {
                Object allowedSQLValue = it.next();
                boolean isString = (allowedSQLValue instanceof String);
                boolean useLike = false;
                String allowedSQLStr = null;

                if (forPreparedStatement) {
                    allowedSQLStr = "?";
                    Object arg = allowedSQLValue;
                    if (isString && arg.toString().indexOf(getWildcard()) != -1) {
                        useLike = true;
                        arg = StringUtils.replace(arg.toString(), getWildcard(), getSqlWildcard());
                    }
                    results.add(arg);
                } else {
                    allowedSQLStr = SQL_formatValue(allowedSQLValue);
                    useLike = (allowedSQLStr.indexOf(getSqlWildcard()) != -1);
                }

                if (i > 0) allowBuf.append (conjunction);

                if (ignoreCase && isString) allowBuf.append(" lower(");
                allowBuf.append(sqlColumn);
                if (ignoreCase && isString) allowBuf.append(") ");

                if (useLike && isString) allowBuf.append(likeOperator);
                else allowBuf.append(equalOperator);

                if (ignoreCase && isString) allowBuf.append(" lower(");
                allowBuf.append(allowedSQLStr);
                if (ignoreCase && isString) allowBuf.append(") ");
            }
        }

        String criteriaWhere = MessageFormat.format("(({0}) and ({1})) or ({2})", new Object[] {minValueBuf.toString(), maxValueBuf.toString(), allowBuf.toString()});
        criteriaWhere = StringUtils.replace(criteriaWhere, "(() and ()) or ()", "");
        criteriaWhere = StringUtils.replace(criteriaWhere, "(() and ()) or ", "");
        criteriaWhere = StringUtils.replace(criteriaWhere, "() and ", "");
        criteriaWhere = StringUtils.replace(criteriaWhere, "and ()", "");
        criteriaWhere = StringUtils.replace(criteriaWhere, " or ()", "");

        if (criteriaWhere.length() == 0) return null;
        results.add(0, criteriaWhere);
        return results;
    }

    /**
     * Get and SQL where clause composed of several single property SQL where clauses.
     * @param exclusive If true all the single property SQL criteria must be satisfied.
     * @param propSQLClauses an array of SQL where clauses.
     * @return An SQL fragment for usage in a WHERE clause of an SQL query statement.
     */
    public String SQL_getWhereClause(boolean exclusive, String[] propSQLClauses) {
        StringBuffer criteriaPattern = new StringBuffer();
        String andOr = exclusive ? " and " : " or ";
        for (int i = 0; i < propSQLClauses.length; i++) {
            if (criteriaPattern.length() > 0) criteriaPattern.append(andOr);
            criteriaPattern.append("({").append(i).append("})");
        }
        String criteriaWhere = MessageFormat.format(criteriaPattern.toString(), propSQLClauses);
        criteriaWhere = StringUtils.replace(criteriaWhere, "(null)" + andOr, "");
        criteriaWhere = StringUtils.replace(criteriaWhere, andOr + "(null)", "");
        criteriaWhere = StringUtils.replace(criteriaWhere, "()" + andOr, "");
        criteriaWhere = StringUtils.replace(criteriaWhere, andOr + "()", "");
        criteriaWhere = StringUtils.replace(criteriaWhere, "()", "");
        return criteriaWhere;
    }

    // FilterByProperty interface

    public Locale getLocale() {
        return filter.getLocale();
    }

    public void setLocale(Locale locale) {
        filter.setLocale(locale);
    }

    public void addProperty(String propertyId, Object minValue, boolean minValueIncluded, Object maxValue, boolean maxValueIncluded, Collection allowedValues, int allowMode) {
        filter.addProperty(propertyId, minValue, minValueIncluded, maxValue, maxValueIncluded, allowedValues, allowMode);
    }

    public boolean addProperty(String propertyId, String filterCriteria) {
        return filter.addProperty(propertyId, filterCriteria);
    }

    public String[] getPropertyIds() {
        return filter.getPropertyIds();
    }

    public boolean containsProperty(String propertyId) {
        return filter.containsProperty(propertyId);
    }

    public boolean containsProperty(Collection propIds) {
        return filter.containsProperty(propIds);
    }

    public void removeProperty(String propertyId) {
        filter.removeProperty(propertyId);
    }

    public void removeAllProperty() {
        filter.removeAllProperty();
    }

    public int getPropertyPriority(String propertyId) {
        return filter.getPropertyPriority(propertyId);
    }

    public Comparable getPropertyMinValue(String propertyId) {
        return filter.getPropertyMinValue(propertyId);
    }

    public boolean minValueIncluded(String propertyId) {
        return filter.minValueIncluded(propertyId);
    }

    public Comparable getPropertyMaxValue(String propertyId) {
        return filter.getPropertyMaxValue(propertyId);
    }

    public boolean maxValueIncluded(String propertyId) {
        return filter.maxValueIncluded(propertyId);
    }

    public List getPropertyAllowedValues(String propertyId) {
        return filter.getPropertyAllowedValues(propertyId);
    }

    public int getPropertyAllowMode(String propertyId) {
        return filter.getPropertyAllowMode(propertyId);
    }

    public void setVariableName(String propertyId, String varName) {
        filter.setVariableName(propertyId, varName);
    }

    public String getVariableName(String propertyId) {
        return filter.getVariableName(propertyId);
    }

    public void setExtraInfo(String propertyId, String extraInfo) {
        filter.setExtraInfo(propertyId, extraInfo);
    }

    public String getExtraInfo(String propertyId) {
        return filter.getExtraInfo(propertyId);
    }

    public void setFilterCondition(String booleanExpression) {
        filter.setFilterCondition(booleanExpression);
    }

    public String getWildcard() {
        return filter.getWildcard();
    }

    public String getGt() {
        return filter.getGt();
    }

    public String getGtOrEq() {
        return filter.getGtOrEq();
    }

    public String getLt() {
        return filter.getLt();
    }

    public String getLtOrEq() {
        return filter.getLtOrEq();
    }

    public boolean pass(String propertyId, Object value) {
        return filter.pass(propertyId, value);
    }

    public boolean pass(Object obj) {
        return filter.pass(obj);
    }
}
