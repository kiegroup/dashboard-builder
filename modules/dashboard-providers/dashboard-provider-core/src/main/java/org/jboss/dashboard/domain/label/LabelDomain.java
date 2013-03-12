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

import org.jboss.dashboard.domain.AbstractDomain;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.domain.CompositeInterval;
import org.jboss.dashboard.domain.Domain;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.LocaleManager;

import java.util.*;

import org.apache.commons.lang.StringUtils;

/**
 * This domain is used for properties that contains string values (addresses, names, cities, products, ...).
 * Every different value is itself an interval of the domain.
 */
public class LabelDomain extends AbstractDomain {

    public static final String I18N_PREFFIX = "labelDomain.";

    protected List labelIntervals;
    protected Map<Locale, String> labelIntervalsToHideI18nMap;
    protected boolean convertedFromNumeric;
    protected String wildcard;

    public LabelDomain() {
        super();
        labelIntervals = new ArrayList();
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

    public boolean isIntervalHidden(LabelInterval interval) {
        // An interval is hidden if has been declared so for some locale. 
        for (Locale locale : labelIntervalsToHideI18nMap.keySet()) {
            String descrsToHide = labelIntervalsToHideI18nMap.get(locale);
            if (descrsToHide == null || descrsToHide.trim().equals("")) continue;
            String descr = interval.getDescription(locale);
            if (descr == null || descr.trim().equals("")) continue;

            for (String descrToHide : StringUtils.split(descrsToHide, ",")) {
                if (descrsToHide.indexOf("*") != -1) {
                    // Wildcard comparison
                    String target = StringUtils.replace(descrsToHide, wildcard, "");
                    if (descrsToHide.startsWith(wildcard) && descrsToHide.endsWith(wildcard) && descr.indexOf(target) != -1) return true;
                    if (descrsToHide.endsWith(wildcard) && descr.startsWith(target)) return true;
                    if (descrsToHide.startsWith(wildcard) && descr.endsWith(target)) return true;
                } else {
                    // Match case comparison
                    if (descr.equals(descrToHide)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Interval[] getIntervals() {
        labelIntervals.clear();
        List domainValues = property.getValues();
        if (domainValues != null && !domainValues.isEmpty()) {
            for (int i = 0; i < domainValues.size(); i++) {
                Object value = domainValues.get(i);
                String label = value == null ? null : value.toString();
                if (getInterval(label) == null) {
                    LabelInterval lr = new LabelInterval();
                    lr.setLabel(label);
                    lr.setDomain(this);
                    labelIntervals.add(lr);
                }
            }
        }

        // Don't show the label intervals to hide
        List intervalsToShow = new ArrayList();
        for (int i = 0; i < labelIntervals.size(); i++) {
            LabelInterval labelInterval = (LabelInterval) labelIntervals.get(i);
            if (!isIntervalHidden(labelInterval)) intervalsToShow.add(labelInterval);
        }
        // Don't exceed the maximum number of intervals.
        // In case there are more intervals than the maximum number, group the rest in a new interval.
        if (intervalsToShow.size() > maxNumberOfIntervals) {
            Interval[] results = new Interval[maxNumberOfIntervals + 1];
            for (int i = 0; i < maxNumberOfIntervals; i++) results[i] = (Interval) intervalsToShow.get(i);

            // Create a composite interval with the rest values.
            CompositeInterval compositeInterval = new CompositeInterval();
            ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.messages", LocaleManager.currentLocale());
            compositeInterval.setDescription(i18n.getString(AbstractDomain.I18N_PREFFIX + "finalInterval"), LocaleManager.currentLocale());
            Set otherIntervals = new HashSet();
            for (int i = maxNumberOfIntervals; i < intervalsToShow.size(); i++) otherIntervals.add(intervalsToShow.get(i));
            compositeInterval.setIntervals(otherIntervals);
            compositeInterval.setDomain(this);
            results[maxNumberOfIntervals] = compositeInterval;
            return results;
        } else {
            LabelInterval[] results = new LabelInterval[intervalsToShow.size()];
            for (int i = 0; i < intervalsToShow.size(); i++) results[i] = (LabelInterval) intervalsToShow.get(i);
            return results;
        }
    }

    public LabelInterval getInterval(String label) {
        Iterator it = labelIntervals.iterator();
        while (it.hasNext()) {
            LabelInterval interval = (LabelInterval) it.next();
            if (interval.contains(label)) return interval;
        }
        return null;
    }

    public Domain cloneDomain() {
        LabelDomain clone = (LabelDomain) super.cloneDomain();
        clone.labelIntervals = new ArrayList();
        clone.labelIntervalsToHideI18nMap = new HashMap(labelIntervalsToHideI18nMap);
        return clone;
    }
}
