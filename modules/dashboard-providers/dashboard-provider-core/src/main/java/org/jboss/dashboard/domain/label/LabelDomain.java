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
package org.jboss.dashboard.domain.label;

import org.jboss.dashboard.dataset.AbstractDataSet;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.dataset.index.DistinctValue;
import org.jboss.dashboard.domain.AbstractDomain;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.domain.CompositeInterval;
import org.jboss.dashboard.domain.Domain;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.LocaleManager;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.provider.DataProperty;

/**
 * This domain is used for properties that contains string values (addresses, names, cities, products, ...).
 * Every different value is itself an interval of the domain.
 */
public class LabelDomain extends AbstractDomain {

    public static final String I18N_PREFFIX = "labelDomain.";

    protected List<Interval> labelIntervals;
    protected Map<Locale, String> labelIntervalsToHideI18nMap;
    protected boolean convertedFromNumeric;
    protected String wildcard;

    public LabelDomain() {
        super();
        labelIntervals = null;
        labelIntervalsToHideI18nMap = new HashMap<Locale, String>();
        convertedFromNumeric = false;
        wildcard = "*";
    }

    public boolean isConvertedFromNumeric() {
        return convertedFromNumeric;
    }

    public void setConvertedFromNumeric(boolean convertedFromNumeric) {
        this.convertedFromNumeric = convertedFromNumeric;
    }

    public Class getValuesClass() {
        return java.lang.String.class;
    }

    public Map<Locale, String> getLabelIntervalsToHideI18nMap() {
        return labelIntervalsToHideI18nMap;
    }

    public void setLabelIntervalsToHideI18nMap(Map<Locale, String> labelIntervalsToHideI18nMap) {
        this.labelIntervalsToHideI18nMap = labelIntervalsToHideI18nMap;
    }

    public String getWildcard() {
        return wildcard;
    }

    public void setWildcard(String wildcard) {
        this.wildcard = wildcard;
    }

    public boolean isScalarFunctionSupported(ScalarFunction sf) {
        return sf.isTypeSupported(String.class);
    }

    public boolean isIntervalHidden(Interval interval) {
        // An interval is hidden if has been declared so for some locale. 
        for (Locale locale : labelIntervalsToHideI18nMap.keySet()) {
            String descrsToHide = labelIntervalsToHideI18nMap.get(locale);
            if (StringUtils.isBlank(descrsToHide)) continue;
            String descr = interval.getDescription(locale);
            if (StringUtils.isBlank(descr)) continue;

            for (String intervalPattern : StringUtils.split(descrsToHide, ",")) {
                if (intervalPattern.indexOf("*") != -1) {
                    // Wildcard comparison
                    String target = StringUtils.replace(intervalPattern, wildcard, "");
                    if (intervalPattern.startsWith(wildcard) && intervalPattern.endsWith(wildcard) && descr.indexOf(target) != -1) return true;
                    if (intervalPattern.endsWith(wildcard) && descr.startsWith(target)) return true;
                    if (intervalPattern.startsWith(wildcard) && descr.endsWith(target)) return true;
                } else {
                    // Match case comparison
                    if (descr.equals(intervalPattern)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Interval> getIntervals() {
        AbstractDataSet dataSet = (AbstractDataSet) property.getDataSet();
        int column = dataSet.getPropertyColumn(property);
        List<DistinctValue> distinctValues = dataSet.getDataSetIndex().getDistinctValues(column);
        return getIntervals(distinctValues);
    }

    public List<Interval> getIntervals(List<DistinctValue> distinctValues) {
        if (labelIntervals == null) labelIntervals = buildIntervals(distinctValues);
        return labelIntervals;
    }

    public List<Interval> buildIntervals(List<DistinctValue> distinctValues) {

        // Get all the intervals
        List<Interval> results = buildAllIntervals(distinctValues);

        // Don't show the label intervals to hide
        List<Interval> intervalsVisible = new ArrayList<Interval>();
        for (int i = 0; i < results.size(); i++) {
            LabelInterval labelInterval = (LabelInterval) results.get(i);
            if (!isIntervalHidden(labelInterval)) intervalsVisible.add(labelInterval);
        }
        // Make sure the maximum number of intervals is not exceeded .
        results = cutIntervals(intervalsVisible);
        return results;
    }

    public List<Interval> buildAllIntervals(List<DistinctValue> distinctValues) {
        List<Interval> all = new ArrayList<Interval>();
        if (distinctValues != null && !distinctValues.isEmpty()) {
            for (DistinctValue distinctValue : distinctValues) {
                LabelInterval interval = new LabelInterval();
                interval.setDomain(this);
                interval.setHolder(distinctValue);
                all.add(interval);
            }
        }
        return all;
    }

    public List<Interval> cutIntervals(List<Interval> intervals) {
        List<Interval> results = new ArrayList<Interval>();
        if (maxNumberOfIntervals < 1 || intervals.size() <= maxNumberOfIntervals) {
            return intervals;
        }

        // Leave out the intervals that exceed the maximum.
        for (int i = 0; i < maxNumberOfIntervals; i++) {
            results.add(intervals.get(i));
        }

        // ... and group the rest in a new aggregated interval.
        CompositeInterval compositeInterval = new CompositeInterval();
        ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.messages", LocaleManager.currentLocale());
        compositeInterval.setDescription(i18n.getString(AbstractDomain.I18N_PREFFIX + "finalInterval"), LocaleManager.currentLocale());
        compositeInterval.setDomain(this);

        // Include the aggregated interval only if visible.
        if (!isIntervalHidden(compositeInterval)) {
            Set otherIntervals = new HashSet();
            for (int i = maxNumberOfIntervals; i < intervals.size(); i++) otherIntervals.add(intervals.get(i));
            compositeInterval.setIntervals(otherIntervals);
            results.add(compositeInterval);
        }
        return results;
    }

    public Domain cloneDomain() {
        LabelDomain clone = (LabelDomain) super.cloneDomain();
        clone.labelIntervals = null;
        clone.labelIntervalsToHideI18nMap = new HashMap(labelIntervalsToHideI18nMap);
        return clone;
    }

    public List getValues(Set<Interval> intervals, DataProperty p) {
        List results = new ArrayList();
        if (p.equals(getProperty())) {
            for (Interval interval : intervals) {
                LabelInterval li = (LabelInterval) interval;
                results.add(li.getLabel());
            }
        } else {
            List targetValues = p.getValues();
            Set<Integer> targetRows = getRowNumbers(intervals);
            for (Integer targetRow : targetRows) {
                results.add(targetValues.get(targetRow));
            }
        }
        return results;
    }

    public Set<Integer> getRowNumbers(Set<Interval> intervals) {
        Set<Integer> results = new HashSet<Integer>();
        for (Interval interval : intervals) {
            LabelInterval labelInterval = (LabelInterval) interval;
            results.addAll(labelInterval.holder.rows);
        }
        return results;
    }
}
