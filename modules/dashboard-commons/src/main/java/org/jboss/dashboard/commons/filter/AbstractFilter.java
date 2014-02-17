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

import org.jboss.dashboard.commons.text.StringUtil;
import org.jboss.dashboard.commons.comparator.ComparatorUtils;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * An abstract that defines those basic services a entity filter.
 */
public abstract class AbstractFilter implements FilterByCriteria, Cloneable {

    /**
     * Filter list.
     * Each entry contains a filter properties array of Object[9]
     * (property id, filter max/min interval, max/min values included, set of allowed values and allow mode, property alias, extra info).
     * The position in the list determines the filter priority in descending order:
     * the first filter into the list is the most prioritary.
     */
    protected List filterProperties;

    /**
     * The filter condition logical expression.
     */
    protected String filterCondition;

    /**
     * Wildcard symbol.
     */
    protected String wildcard;

    /**
     * Greater than symbol.
     */
    protected String gt;

    /**
     * Less than symbol.
     */
    protected String lt;

    /**
     * Greater or equals than symbol.
     */
    protected String gtOrEq;

    /**
     * Less or equals than symbol.
     */
    protected String ltOrEq;

    /**
     * The criteria locale.
     */
    protected Locale locale;

    /**
     * Map used to store properties filter results when evauating the overall filter condition.
     */
    protected transient Map _filterVarValues;

    /**
     * BSH interpreter per thread cache.
     */
    protected transient ThreadLocal _bshIntepreterThread;

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AbstractFilter.class);

    public AbstractFilter() {
        filterProperties = new ArrayList();
        filterCondition = null;
        wildcard = "\u002a";
        gt = "\u003e";
        lt = "\u003c";
        gtOrEq = "\u003e\u003d";
        ltOrEq = "\u003c\u003d";
        locale = Locale.getDefault();
        _filterVarValues = new HashMap();
        _bshIntepreterThread = new ThreadLocal();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getWildcard() {
        return wildcard;
    }

    public void setWildcard(String wildcard) {
        this.wildcard = wildcard;
    }

    public String getGt() {
        return gt;
    }

    public void setGt(String gt) {
        this.gt = gt;
    }

    public String getGtOrEq() {
        return gtOrEq;
    }

    public void setGtOrEq(String gtOrEq) {
        this.gtOrEq = gtOrEq;
    }

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }

    public String getLtOrEq() {
        return ltOrEq;
    }

    public void setLtOrEq(String ltOrEq) {
        this.ltOrEq = ltOrEq;
    }

    public void addProperties(FilterByCriteria filter) {
        if (filter == null) return;
        String[] propIds = filter.getPropertyIds();
        for (int i = 0; i < propIds.length; i++) {
            String propId = propIds[i];
            Object minValue = filter.getPropertyMinValue(propId);
            Object maxValue = filter.getPropertyMaxValue(propId);
            boolean minInc = filter.minValueIncluded(propId);
            boolean maxInc = filter.maxValueIncluded(propId);
            Collection allowed = filter.getPropertyAllowedValues(propId);
            int allowMode = filter.getPropertyAllowMode(propId);
            addProperty(propId, minValue, minInc, maxValue, maxInc, allowed, allowMode);
            setExtraInfo(propId, filter.getExtraInfo(propId));
            setVariableName(propId, getVariableName(propId));
        }
    }

    public void addProperty(String propertyId,
                            Object minValue, boolean minValueIncluded,
                            Object maxValue, boolean maxValueIncluded,
                            Collection allowedValues, int allowMode) {

        // Remove old values.
        Iterator it = filterProperties.iterator();
        while (it.hasNext()) {
            Object[] filterProps = (Object[]) it.next();
            if (filterProps[0].equals(propertyId.trim())) it.remove();
        }
        // Register new ones.
        Object[] filterProps = new Object[] {propertyId.trim(), // Identifier
                                    minValue, minValueIncluded, // Min. Value
                                    maxValue, maxValueIncluded, // Max. value
                                    allowedValues == null ? allowedValues : new ArrayList(allowedValues), // Allowed values
                                    new Integer(allowMode), // Allowed values mode
                                    StringUtil.toJavaIdentifier(propertyId.trim()),  // Variable name
                                    propertyId.trim()}; // Extra info.
        filterProperties.add(filterProps);

        // If property set is modified then the current filter condition must be discarded.
        setFilterCondition(null);
    }

    public boolean addProperty(String propertyId, String filterCriteria) {
        if (filterCriteria != null) filterCriteria = filterCriteria.trim();
        String[] filterValues = StringUtils.split(filterCriteria, ",");
        if (filterValues.length == 0) return false;

        // Construct property filter.
        Object minValue = null;
        boolean minIncl = false;
        Object maxValue = null;
        boolean maxIncl = false;
        Set allowedValues = new HashSet();
        for (int i = 0; i < filterValues.length; i++) {
            // Catch min. value.
            String filterValue = filterValues[i];
            if (filterValue.startsWith(getGt())) {
                if (filterValue.startsWith(getGtOrEq())) minIncl = true;
                if (minIncl) filterValue = filterValue.substring(getGtOrEq().length()).trim();
                else filterValue = filterValue.substring(getGt().length()).trim();
                minValue = filterValue;
            }
            // Catch max. value.
            else if (filterValue.startsWith(getLt())) {
                if (filterValue.startsWith(getLtOrEq())) maxIncl = true;
                if (maxIncl) filterValue = filterValue.substring(getLtOrEq().length()).trim();
                else filterValue = filterValue.substring(getLt().length()).trim();
                maxValue = filterValue;
            }
            // Catch allowed value.
            else {
                allowedValues.add(filterValue);
            }
        }
        // Register property.
        addProperty(propertyId, minValue, minIncl, maxValue, maxIncl, allowedValues, ALLOW_ANY);
        return true;
    }

    public void removeProperty(String propertyId) {
        Object[] filterProps = getProperty(propertyId);
        if (filterProps != null) {
            filterProperties.remove(filterProps);

            // If property set is modified then the current filter condition must be discarded.
            setFilterCondition(null);
        }
    }

    public void removeProperties(FilterByCriteria filter) {
        if (filter == null) return;
        String[] propIds = filter.getPropertyIds();
        for (int i = 0; i < propIds.length; i++) {
            String propId = propIds[i];
            removeProperty(propId);
        }
    }

    public void removeAllProperty() {
        filterProperties.clear();

        // If property set is modified then the current filter condition must be discarded.
        setFilterCondition(null);

    }

    public int getPropertyPriority(String propertyId) {
        Object[] filterProps = getProperty(propertyId);
        if (filterProps != null) return filterProperties.indexOf(filterProps) + 1;
        return 0;
    }

    public Comparable getPropertyMinValue(String propertyId) {
        Object[] prop = getProperty(propertyId);
        if (prop == null) return null;
        return (Comparable)prop[1];
    }

    public boolean minValueIncluded(String propertyId) {
        Object[] prop = getProperty(propertyId);
        if (prop == null) return false;
        return ((Boolean)prop[2]).booleanValue();
    }

    public Comparable getPropertyMaxValue(String propertyId) {
        Object[] prop = getProperty(propertyId);
        if (prop == null) return null;
        return (Comparable)prop[3];
    }

    public boolean maxValueIncluded(String propertyId) {
        Object[] prop = getProperty(propertyId);
        if (prop == null) return false;
        return ((Boolean)prop[4]).booleanValue();
    }

    public List getPropertyAllowedValues(String propertyId) {
        Object[] prop = getProperty(propertyId);
        if (prop == null) return new ArrayList();
        return (List)prop[5];
    }

    public int getPropertyAllowMode(String propertyId) {
        Object[] prop = getProperty(propertyId);
        if (prop == null) return ALLOW_ANY;
        return ((Integer)prop[6]).intValue();
    }

    public void setVariableName(String propertyId, String varName) {
        Object[] propArray = getProperty(propertyId);
        if (propArray != null) propArray[7] = varName;
    }

    public String getVariableName(String propertyId) {
        Object[] prop = getProperty(propertyId);
        if (prop == null) return null;
        return (String)prop[7];
    }

    public String getExtraInfo(String propertyId) {
        Object[] prop = getProperty(propertyId);
        if (prop == null) return null;
        return (String)prop[8];
    }

    public void setExtraInfo(String propertyId, String extraInfo) {
        Object[] propArray = getProperty(propertyId);
        if (propArray != null) propArray[8] = extraInfo;
    }

    public void setFilterCondition(String logicalExpression) {
        filterCondition = logicalExpression;
    }

    public String getFilterCondition() {
        if (filterCondition != null) return filterCondition;

        // By default an AND condition for all properties is calculated.
        StringBuffer buf = new StringBuffer();
        Iterator it = filterProperties.iterator();
        while (it.hasNext()) {
            Object[] filterProp = (Object[]) it.next();
            if (buf.length() > 0) buf.append(" AND ");
            String varName = (String) filterProp[7];
            buf.append(StringUtil.toJavaIdentifier(varName));
        }
        return buf.toString();
    }

    public String[] getPropertyIds() {
        String[] ids = new String[filterProperties.size()];
        for (int i = 0; i < ids.length; i++) {
            Object[] filterProps = (Object[]) filterProperties.get(i);
            ids[i] = (String) filterProps[0];
        }
        return ids;
    }

    public boolean containsProperty(String propertyId) {
        return getProperty(propertyId) != null;
    }

    public boolean containsProperty(Collection propIds) {
        if (propIds == null || propIds.isEmpty()) return false;
        Iterator it = propIds.iterator();
        while (it.hasNext()) {
            if (containsProperty(it.next().toString())) return true;
        }
        return false;
    }


    protected Object[] getProperty(String propertyId) {
        Iterator it = filterProperties.iterator();
        while (it.hasNext()) {
            Object[] filterProps = (Object[]) it.next();
            if (filterProps[0].equals(propertyId.trim())) return filterProps;
        }
        return null;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        Iterator it = filterProperties.iterator();
        while (it.hasNext()) {
            Object[] filterProp = (Object[]) it.next();
            buf.append("Property ").append(filterProp[0]).append(": ");
            for (int i = 1; i < filterProp.length; i++) buf.append(filterProp[i]).append(" ");
            buf.append("\r\n");
        }
        buf.append("Filter condition: ").append(filterCondition).append("");
        buf.append("\r\n");
        return buf.toString();
    }

    /**
     * Check if a value satisfies a property filter.
     * @param propertyId The property to pass.
     * @param value The value to check.
     */
    public boolean pass(String propertyId, Object value) throws IllegalArgumentException {
        // Check min. value.
        Object[] propertyFilter = getProperty(propertyId);
        Object minValue = propertyFilter[1];
        if (minValue != null) {
            boolean minValueIncluded = ((Boolean) propertyFilter[2]).booleanValue();
            if (value == null) return false;
            switch (compareBySimilarity(propertyId, minValue, value)) {
                case 1: return false;
                case 0: if (!minValueIncluded) return false;
            }
        }
        //log.debug("Min. value for property " + propertyId + " satisfied: " + minValue);

        // Check max. value.
        Object maxValue = propertyFilter[3];
        if (maxValue != null) {
            if (value == null) return false;
            boolean maxValueIncluded = ((Boolean) propertyFilter[4]).booleanValue();
            switch (compareBySimilarity(propertyId, maxValue, value)) {
                case -1: return false;
                case 0: if (!maxValueIncluded) return false;
            }
        }
        //log.debug("Max. value for property " + propertyId + " satisfied: " + maxValue);

        // Check allowed values.
        List allowedValues = (List) propertyFilter[5];
        int allowMode = ((Integer) propertyFilter[6]).intValue();
        if (!pass(propertyId, value, allowedValues, allowMode)) return false;
        //log.debug("Allowed values for property " + propertyId + " satisfied: " + allowedValues);

        // All checks satisfied.
        return true;
    }

    /**
     * Check if a given value (either a single object or a collection) satisfies a collection of allowed values.
     * @param value
     * @param allowedValues
     * @param allowMode See <code>ALLOW_</code> constants defined.
     */
    public boolean pass(String propertyId, Object value, List allowedValues, int allowMode) {
        // If the value is a collection , iterate over it to check all values.
        if (value instanceof Collection) {
            Collection values = (Collection) value;
            Iterator it = values.iterator();
            while (it.hasNext()) {
                Object aValue = it.next();
                boolean pass = passValue(propertyId, aValue, allowedValues, allowMode);
                if (pass) return true;
            }
            return false;
        }
        // The value is a single object.
        return passValue(propertyId, value, allowedValues, allowMode);
    }

    /**
     * Check if a given value satisfies a collection of allowed values.
     * @param value
     * @param allowedValues
     * @param allowMode See <code>ALLOW_</code> constants defined.
     */
    public boolean passValue(String propertyId, Object value, List allowedValues, int allowMode) {
        if (allowedValues == null || allowedValues.isEmpty()) return true;
        boolean anySatisfied = false;
        for (int i = 0; i < allowedValues.size(); i++) {
            Object allowedObj = allowedValues.get(i);
            boolean valueSatisfied = (compareBySimilarity(propertyId, allowedObj, value) == 0);
            switch(allowMode) {
                case ALLOW_ALL: if (!valueSatisfied) return false;
                case ALLOW_ANY: if (valueSatisfied) anySatisfied = true; break;
                case ALLOW_NONE: if (valueSatisfied) return false;
            }
        }
        // Checks satisfied.
        if (allowMode == ALLOW_ANY && !anySatisfied) return false;
        return true;
    }

    /**
     * Check if the given object instance matchs the specified pattern.
     * @param propertyId The property to compare.
     * @param pattern The pattern. In Strings wildcard is accepted.
     * @param value The object to check.
     * @return Equality comparison if pattern has no wildcards.
     */
    protected int compareBySimilarity(String propertyId, Object pattern, Object value) {
        // If pattern is wildcard-based then compare by similarity.
        // The values are formatted as the user views them.
        if (pattern instanceof String && ((String) pattern).indexOf(getWildcard()) != -1) {
            String s1 = formatForDisplay(propertyId, pattern).trim();
            String s2 = formatForDisplay(propertyId, value).trim();

            String target = StringUtil.replaceAll(s1, wildcard, "");
            if (s1.startsWith(wildcard) && s1.endsWith(wildcard) && s2.indexOf(target) != -1) return 0;
            if (s1.endsWith(wildcard) && s2.startsWith(target)) return 0;
            if (s1.startsWith(wildcard) && s2.endsWith(target)) return 0;
            return compare(target, s2, 1);
        }
        // If pattern is value-based then compare by equality.
        else {
            if (value instanceof String) {
                // Performance improvement: strings do not require formatting.
                String s1 = (String) value;
                String s2 = pattern.toString();
                return compare(s1, s2, 1);
            } else {
                // Non-string values need to be formatted for comparison first.
                String s1 = formatForComparison(propertyId, pattern).trim();
                String s2 = formatForComparison(propertyId, value).trim();
                return compare(s1, s2, 1);
            }
        }
    }

    // Filter interface

    public synchronized boolean pass(Map obj) {
        // If the object is null then return false;
        if (obj == null || obj.isEmpty()) return false;

        // If the filter is not based on a custom logical expression then simply ensure the filter is satisfied for all its properties.
        String[] propIdArray = getPropertyIds();
        if (filterCondition == null) {
            for (int i = 0; i < propIdArray.length; i++) {
                String propertyId = propIdArray[i];
                Object propertyValue = obj.get(propertyId);
                if (!pass(propertyId, propertyValue)) return false;
            }
            return true;
        }

        // If the filter is based on a logical expression then get the results of combine that properties as specified by that expression.
        String filterCondition = getFilterCondition();
        filterCondition = StringUtil.replaceAll(filterCondition, "AND", "\u0026\u0026");
        filterCondition = StringUtil.replaceAll(filterCondition, "OR", "\u007C\u007C");
        filterCondition = StringUtil.replaceAll(filterCondition, "NOT", "\u0021");
        _filterVarValues.clear();
        try {
            for (int i = 0; i < propIdArray.length; i++) {
                String propertyId = propIdArray[i];
                String propertyVar = getVariableName(propertyId);
                Object propertyValue = obj.get(propertyId);
                Boolean propertyPassed = Boolean.valueOf(pass(propertyId, propertyValue));
                _filterVarValues.put(propertyVar, propertyPassed);
            }
            Object result = executeBeanShellScript(filterCondition, _filterVarValues);
            if (result == null) return false;
            return ((Boolean)result).booleanValue();
        }
        catch (EvalError evalError) {
            log.error("Logical beanshell expression cannot be evaluated: " + filterCondition, evalError);
            return false;
        }
    }

    public synchronized boolean pass(Object obj) {
        // If the object is null then return false;
        if (obj == null) return false;

        // If the filter is not based on a custom logical expression then simply ensure the filter is satisfied for all its properties.
        String[] propIdArray = getPropertyIds();
        if (filterCondition == null) {
            for (int i = 0; i < propIdArray.length; i++) {
                String propertyId = propIdArray[i];
                Object propertyValue = getPropertyValue(propertyId, obj);
                if (!pass(propertyId, propertyValue)) return false;
            }
            return true;
        }

        // If the filter is based on a logical expression then get the results of combine that properties as specified by that expression.
        String filterCondition = getFilterCondition();
        filterCondition = StringUtil.replaceAll(filterCondition, "AND", "\u0026\u0026");
        filterCondition = StringUtil.replaceAll(filterCondition, "OR", "\u007C\u007C");
        filterCondition = StringUtil.replaceAll(filterCondition, "NOT", "\u0021");
        _filterVarValues.clear();
        try {
            for (int i = 0; i < propIdArray.length; i++) {
                String propertyId = propIdArray[i];
                String propertyVar = getVariableName(propertyId);
                Object propertyValue = getPropertyValue(propertyId, obj);
                Boolean propertyPassed = Boolean.valueOf(pass(propertyId, propertyValue));
                _filterVarValues.put(propertyId, propertyPassed);
                _filterVarValues.put(propertyVar, propertyPassed);
            }
            Object result = executeBeanShellScript(filterCondition, _filterVarValues);
            if (result == null) return false;
            return ((Boolean)result).booleanValue();
        }
        catch (EvalError evalError) {
            log.error("Logical beanshell expression cannot be evaluated: " + filterCondition, evalError);
            return false;
        }
    }

    /**
     * Executes a BeanShell script.
     * @param beanShellScript The script to execute in BeanShell format.
     * @param context A map of arguments to be passed to the script.
     * @return The script may return any object as result.
     */
    protected Object executeBeanShellScript(String beanShellScript, Map context) throws EvalError {
        // Ensure there is something to execute.
        if (beanShellScript == null || beanShellScript.trim().equals("")) return null;

        // Initialize the BeanShell interpreter.
        Interpreter bshInterpreter = (Interpreter) _bshIntepreterThread.get();
        if (bshInterpreter == null) {
            bshInterpreter = new Interpreter();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader != null) bshInterpreter.setClassLoader(loader);
            _bshIntepreterThread.set(bshInterpreter);
        }

        // Set context.
        Iterator contextIt = context.keySet().iterator();
        while (contextIt.hasNext()) {
            String contextVar = (String) contextIt.next();
            bshInterpreter.set(contextVar, context.get(contextVar));
        }

        // Launch the BeanShell script.
        return bshInterpreter.eval(beanShellScript);
    }

    /**
     * Compares two comparable objects.
     * @param ordering: 1=ascending, -1=descending
     */
    public int compare(Comparable o1, Comparable o2, int ordering) {
        return ComparatorUtils.compare(o1, o2, ordering);
    }

    public FilterByCriteria cloneFilter() {
        try {
            AbstractFilter clone = (AbstractFilter) super.clone();
            clone._filterVarValues = new HashMap();
            clone._bshIntepreterThread = new ThreadLocal();
            clone.filterProperties = new ArrayList();
            Iterator it = filterProperties.iterator();
            while (it.hasNext()) {
                Object[] prop = (Object[]) it.next();
                Collection allowedValues = (Collection)prop[5];
                clone.filterProperties.add(new Object[] {prop[0], prop[1] , prop[2], prop[3], prop[4],
                        allowedValues == null ? null : new ArrayList(allowedValues), prop[6], prop[7], prop[8]});
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            log.error("Clone exception.", e);
            return null;
        }
    }

    public boolean equals(Object obj) {
        try {
            AbstractFilter other = (AbstractFilter) obj;
            if (filterProperties.size() != other.filterProperties.size()) return false;

            for (int i=0; i < filterProperties.size(); i++) {
                Object[] tprop = (Object[]) filterProperties.get(i);
                String propId = (String) tprop[0];
                Object[] oprop = other.getProperty(propId);
                for (int j=0; j < tprop.length; j++) {
                    if (ComparatorUtils.compare(tprop[j], oprop[j], 1) != 0) {
                        return false;
                    }
                }
                // Allowed values must be strictly equals.
                Collection thisAllowed = getPropertyAllowedValues(propId);
                Collection otherAllowed = other.getPropertyAllowedValues(propId);
                if (CollectionUtils.intersection(thisAllowed, otherAllowed).size() != thisAllowed.size()) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * Format a given property value as displayed for the the user.
     * @param propertyId The property identifier.
     * @param value The value to format.
     */
    protected abstract String formatForDisplay(String propertyId, Object value);

    /**
     * Format a given property value valid for string comparfison against other values.
     * @param propertyId The property identifier.
     * @param value The value to format.
     */
    protected abstract String formatForComparison(String propertyId, Object value);

    /**
     * Retrieve the value for a given property.
     * @param propertyId The property to search value for.
     * @param obj The object instance to ask for.
     * @return The value.
     */
    protected abstract Object getPropertyValue(String propertyId, Object obj);
}
