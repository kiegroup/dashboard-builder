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
package org.jboss.dashboard.domain;

import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.domain.date.DateInterval;
import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.domain.label.LabelInterval;
import org.jboss.dashboard.domain.numeric.NumericInterval;
import org.jboss.dashboard.provider.DataProperty;

import java.util.*;

/**
 * A composite interval is an especial class of intervals which is composed by a set of single intervals.
 */
public class CompositeInterval extends AbstractInterval {

    protected Set<Interval> intervals;
    protected Map descriptionI18nMap;

    public CompositeInterval() {
        super();
        descriptionI18nMap = new HashMap();
        intervals = new HashSet();
    }

    public Map getDescriptionI18nMap() {
        return Collections.unmodifiableMap(descriptionI18nMap);
    }

    public String getDescription(Locale l) {        
        return (String) descriptionI18nMap.get(l);
    }

    public void setDescription(String descr, Locale l) {
        descriptionI18nMap.put(l, descr);
    }

    public Set getIntervals() {
        return Collections.unmodifiableSet(intervals);
    }

    public void setIntervals(Set intervals) {
        this.intervals = intervals;
    }

    public void addInterval(Interval interval) {
        intervals.add(interval);
    }

    public void removeInterval(Interval interval) {
        intervals.remove(interval);
    }

    public void clearIntervals(Interval interval) {
        intervals.clear();
    }

    public boolean contains(Object value) {
        if (intervals.contains(value)) {
            return true;
        }
        Iterator it = intervals.iterator();
        while (it.hasNext()) {
            Interval interval = (Interval) it.next();
            if (interval.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public Object getMinValue() {
        if (intervals.size() == 0) return null;
        Interval interval = (Interval) intervals.toArray()[0];
        if (interval instanceof NumericInterval) return ((NumericInterval)interval).getMinValue();
        if (interval instanceof DateInterval) return ((DateInterval)interval).getMinDate();
        return null;
    }

    public Object getMaxValue() {
        if (intervals.size() == 0) return null;
        int intervalsSize = intervals.size();
        Interval interval = (Interval) intervals.toArray()[intervalsSize-1];
        if (interval instanceof NumericInterval) return ((NumericInterval)interval).getMaxValue();
        if (interval instanceof DateInterval) return ((DateInterval)interval).getMaxDate();
        return null;
    }

    public boolean isMinValueIncluded() {
        if (intervals.size() == 0) return false;
        Interval interval = (Interval) intervals.toArray()[0];
        if (interval instanceof NumericInterval) {
            if (((NumericInterval)interval).getMinValue() != null) return true;
        }
        if (interval instanceof DateInterval) {
            if (((DateInterval)interval).getMaxDate() != null) return true;
        }
        return false;
    }

    public boolean isMaxValueIncluded() {
        if (intervals.size() == 0) return false;
        int intervalsSize = intervals.size();
        Interval interval = (Interval) intervals.toArray()[intervalsSize-1];
        if (interval instanceof NumericInterval) {
            if (((NumericInterval)interval).getMaxValue() != null) return true;
        }
        if (interval instanceof DateInterval) {
            if (((DateInterval)interval).getMaxDate() != null) return true;
        }
        return false;
    }

    public List getValues(DataProperty p) {
        if (domain instanceof LabelDomain) {
            LabelDomain labelDomain = (LabelDomain) domain;
            return labelDomain.getValues(intervals, p);
        }
        return super.getValues(p);
    }
}