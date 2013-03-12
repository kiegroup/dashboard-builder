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

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * A filter designed to configure filter criteria for objects properties.
 */
public interface FilterByCriteria extends Filter {

    /**
     * The criteria always belongs to a locale.
     */
    Locale getLocale();
    void setLocale(Locale locale);

    /**
     * Specifies a property for the filter.
     * @param propertyId The property to set.
     * @param minValue The minimun value allowed for the property.
     * @param minValueIncluded The minimun value is considered as a valid value.
     * @param maxValue The maximum value allowed for the property.
     * @param maxValueIncluded The maximum value is considered as a valid value.
     * @param allowedValues A set of values allowed.
     * @param allowMode  <ul><li>ALLOW_ALL=All values must be satisfied.
     * <li>ALLOW_ANY=At least one value must be satisfied.
     * <li>ALLOW_NONE=Any value must be satisfied.</ul>
     */
    void addProperty(String propertyId,
                     Object minValue, boolean minValueIncluded,
                     Object maxValue, boolean maxValueIncluded,
                     Collection allowedValues, int allowMode);


    /**
     * Specifies a property for the filter in a unstructured way.
     * @param propertyId The property to set.
     * @param filterCriteria The criteria for the property.
     * Operators are allowed here: wildcard, greater than, less than and comma. e.g: ">1000",
     * "Rev*", "1000, 1002, 1003".
     * @return Property added or not. e.g: If criteria string is empty then it returns false.
     */
    boolean addProperty(String propertyId, String filterCriteria);

    /**
     * Add all the specified filter properties.
     */
    void addProperties(FilterByCriteria filter);

    /**
     * Retgrieve the property ids. specified for this filter.
     * @return An array of ids.
     */
    String[] getPropertyIds();

    /**
     * Check if the given property filter has been defined.
     */
    boolean containsProperty(String propertyId);

    /**
     * Check if a filter is defined for any of the property identifiers given.
     * @param propIds A collection of property identifiers.
     */
    boolean containsProperty(Collection propIds);

    /**
     * Remove property from the filter.
     */
    void removeProperty(String propertyId);

    /**
     * Remove all the specified filter properties.
     */
    void removeProperties(FilterByCriteria filter);

    /**
     * Clear filter.
     */
    void removeAllProperty();

    /**
     * Get the prioority for a given property defined in the filter.
     * @param propertyId
     * @return A priority ordinal.
     */
    int getPropertyPriority(String propertyId);

    /**
     * Get the min. value allowed for the property in the filter.
     */
    Comparable getPropertyMinValue(String propertyId);

    /**
     * Check if min. value defined must be considered as valid.
     * To be applied only when min. value is defined.
     */
    boolean minValueIncluded(String propertyId);

    /**
     * Get the max. value allowed for the property in the filter.
     */
    Comparable getPropertyMaxValue(String propertyId);

    /**
     * Check if max. value defined must be considered as valid.
     * To be applied only when max. value is defined.
     */
    boolean maxValueIncluded(String propertyId);

    /**
     * Get the set of allowed values for the property.
     */
    List getPropertyAllowedValues(String propertyId);

    /**
     * Allow only if all values are satisfied.
     */
    int ALLOW_ALL  = 0;

    /**
     * Allow if some value is satisfied.
     */
    int ALLOW_ANY  = 1;

    /**
     * Allow if none of the specified values are satisfied.
     */
    int ALLOW_NONE = 2;

    /**
     * Get the allow mode for a property.
     * @return See <code>ALLOW_</code> constants defined.
     */
    int getPropertyAllowMode(String propertyId);

    /**
     * An alias or variable to be assigned to the property . Useful in the definition of custom filter conditions.
     * Such variable it models <i>the boolean result obtained after applying the property filter for a given object</i>.
     * This variable can be used in the definition of custom filter conditions for the filter (as a boolean expressions).
     * <p>For more details see <code>setFilterCondition(String logicalExpression)</code> method.
     */
    void setVariableName(String propertyId, String varName);
    String getVariableName(String propertyId);

    /**
     * Set additional information regarding the property.
     * f.i: the text an user typed as a search criteria for a given property.
     */
    void setExtraInfo(String propertyId, String extraInfo);
    String getExtraInfo(String propertyId);

    /**
     * Define the condition to be applied to the set of property filters when executing the
     * <code>pass(Object)</code> method.
     * @param booleanExpression A logical expression where we can combine the different property
     * <i>pass results</i> in order to calculate the overall filter result. Boolean AND, OR and
     *  NOT operators are supported.
     * <p>Next some logical expressions
     * samples:
     * <table align="left">
     * First of all, define variables for properties.
     * <ul><li>setVariableName("processId", "a");
     * <li>setVariableName("stepName", "b");
     * <li>setVariableName("processDescrCode", "c");
     * <br>Second step, define filter logical expression:
     * <li>Sample 1: setFilterCondition("NOT a OR b");
     * <li>Sample 2: setFilterCondition("c AND (a OR b)");
     * </ul></code></table>
     * <p>As you can see, powerful and complex boolean expressions
     * can be defined.
     */
    void setFilterCondition(String booleanExpression);

    /**
     * The wildcard symbol used by the filter to compare properties.
     */
    String getWildcard();

    /**
     * The greater than symbol.
     */
    String getGt();

    /**
     * The greater or equals than symbol.
     */
    String getGtOrEq();

    /**
     * The less than symbol.
     */
    String getLt();

    /**
     * The less or equals than symbol.
     */
    String getLtOrEq();

    /**
     * Check if a value satisfies a defined property filter.
     * @param propertyId The property to pass.
     * @param value The value to check.
     */
    boolean pass(String propertyId, Object value);

    /**
     * Create an exact copy of this filter instance.
     */
    FilterByCriteria cloneFilter();
}
