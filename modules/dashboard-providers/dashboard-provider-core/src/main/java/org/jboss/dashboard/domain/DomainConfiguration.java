/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.domain.date.DateDomain;
import org.jboss.dashboard.domain.numeric.NumericDomain;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.LocaleManager;

import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;

/**
 * The configuration for a given domain property.
 */
public class DomainConfiguration {

    /** Logger */
    private transient static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DomainConfiguration.class);

    protected DataProperty domainProperty;
    protected String propertyId;
    protected Map<Locale,String> propertyNameI18nMap;
    protected String maxNumberOfIntervals;
    protected Map<Locale,String> labelIntervalsToHideI18nMap;
    protected String dateTamInterval;
    protected String dateMinDate;
    protected String dateMaxDate;
    protected String numericTamInterval;
    protected String numericMinValue;
    protected String numericMaxValue;

    protected transient SimpleDateFormat dateFormat;
    protected transient NumberFormat numberFormat;

    public DomainConfiguration() {
        propertyNameI18nMap = new HashMap<Locale,String>();
        labelIntervalsToHideI18nMap = new HashMap<Locale,String>();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        numberFormat = NumberFormat.getInstance(new Locale("es"));
    }

    public DomainConfiguration(DataProperty property) {
        this();
        read(property);
    }

    public DataProperty getDomainProperty() {
        return domainProperty;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public Map<Locale,String> getPropertyNameI18nMap() {
        return propertyNameI18nMap;
    }

    public void setPropertyNameI18nMap(Map<Locale,String> domainPropDisplayNameI18nMap) {
        propertyNameI18nMap.clear();
        propertyNameI18nMap.putAll(domainPropDisplayNameI18nMap);
    }

    public String getPropertyName(Locale l) {
        Object result = propertyNameI18nMap.get(l);
        if (result == null && domainProperty != null) result = domainProperty.getName(l);
        if (result == null) result = LocaleManager.lookup().localize(propertyNameI18nMap);
        return (String) result;
    }

    public void setPropertyName(String description, Locale l) {
        propertyNameI18nMap.put(l, description);
    }

    public Map<Locale,String> getLabelIntervalsToHideI18nMap() {
        return labelIntervalsToHideI18nMap;
    }

    public void setLabelIntervalsToHideI18nMap(Map<Locale,String> labelIntervalsToHideMap) {
        labelIntervalsToHideI18nMap.clear();
        labelIntervalsToHideI18nMap.putAll(labelIntervalsToHideMap);
    }

    public String getLabelIntervalsToHide(Locale l) {
        Object result = labelIntervalsToHideI18nMap.get(l);
        if (result == null) result = LocaleManager.lookup().localize(labelIntervalsToHideI18nMap);
        if (result == null) return "";
        return (String) result;
    }

    public void setLabelIntervalsToHide(String intervals, Locale l) {
        labelIntervalsToHideI18nMap.put(l, intervals);
    }

    public String getMaxNumberOfIntervals() {
        return maxNumberOfIntervals;
    }

    public void setMaxNumberOfIntervals(String maxNumberOfIntervals) {
        this.maxNumberOfIntervals = maxNumberOfIntervals;
    }

    public String getDateTamInterval() {
        return dateTamInterval;
    }

    public void setDateTamInterval(String dateTamInterval) {
        this.dateTamInterval = dateTamInterval;
    }

    public String getDateMinDate() {
        return dateMinDate;
    }

    public void setDateMinDate(String dateMinDate) {
        this.dateMinDate = dateMinDate;
    }

    public String getDateMaxDate() {
        return dateMaxDate;
    }

    public void setDateMaxDate(String dateMaxDate) {
        this.dateMaxDate = dateMaxDate;
    }

    public String getNumericTamInterval() {
        return numericTamInterval;
    }

    public void setNumericTamInterval(String numericTamInterval) {
        this.numericTamInterval = numericTamInterval;
    }

    public String getNumericMinValue() {
        return numericMinValue;
    }

    public void setNumericMinValue(String numericMinValue) {
        this.numericMinValue = numericMinValue;
    }

    public String getNumericMaxValue() {
        return numericMaxValue;
    }

    public void setNumericMaxValue(String numericMaxValue) {
        this.numericMaxValue = numericMaxValue;
    }

    /**
     * Update the domain instance with the current configuration.
     */
    public void apply(DataProperty property) {
        if (property == null) return;
        Domain domain = property.getDomain();

        if (propertyId != null) property.setPropertyId(propertyId);
        property.setNameI18nMap(new HashMap<Locale,String>(propertyNameI18nMap));
        if (maxNumberOfIntervals != null) domain.setMaxNumberOfIntervals(Integer.parseInt(maxNumberOfIntervals));

        // Label domain specifics.
        if (domain instanceof LabelDomain) {
            if (labelIntervalsToHideI18nMap != null) {
                LabelDomain labelDomain = (LabelDomain) domain;
                labelDomain.setLabelIntervalsToHideI18nMap(new HashMap<Locale,String>(labelIntervalsToHideI18nMap));
            }
        }
        // Date domain specifics.
        else if (domain instanceof DateDomain) {
            DateDomain dateDomain = (DateDomain) domain;
            dateDomain.setMinDate(null);
            dateDomain.setMaxDate(null);
            if (dateTamInterval != null) dateDomain.setTamInterval(Integer.parseInt(dateTamInterval));
            if (dateMinDate != null && !dateMinDate.trim().equals("")) {
                try {
                    dateDomain.setMinDate(dateFormat.parse(dateMinDate));
                } catch (ParseException e) {
                    // Ignore.
                }
            }
            if (dateMaxDate != null && !dateMaxDate.trim().equals("")) {
                try {
                    dateDomain.setMaxDate(dateFormat.parse(dateMaxDate));
                } catch (ParseException e) {
                    // Ignore.
                }
            }
        }
        // Numeric domain specifics.
        else if (domain instanceof NumericDomain) {
            NumericDomain numericDomain = (NumericDomain) domain;
            numericDomain.setMinValue(null);
            numericDomain.setMaxValue(null);
            if (numericTamInterval != null) numericDomain.setTamInterval(Integer.parseInt(numericTamInterval));
            if (numericMinValue != null && !numericMinValue.trim().equals("")) {
                try {
                    numericDomain.setMinValue(numberFormat.parse(numericMinValue));
                } catch (ParseException e) {
                    // Ignore.
                }
            }
            if (numericMaxValue != null && !numericMaxValue.trim().equals("")) {
                try {
                    numericDomain.setMaxValue(numberFormat.parse(numericMaxValue));
                } catch (ParseException e) {
                    // Ignore.
                }
            }
        }
    }

    /**
     * Get the configuration from the property instance specified.
     */
    public void read(DataProperty property) {
        clear();
        domainProperty = property;
        if (property != null) propertyId = property.getPropertyId();
        if (property != null) propertyNameI18nMap.putAll(property.getNameI18nMap());
        Domain domain = (property != null ? property.getDomain() : null);
        if (domain == null) return;

        maxNumberOfIntervals = String.valueOf(domain.getMaxNumberOfIntervals());
        if (domain instanceof LabelDomain) {
            LabelDomain labelDomain = (LabelDomain) domain;
            labelIntervalsToHideI18nMap = new HashMap<Locale,String>(labelDomain.getLabelIntervalsToHideI18nMap());
        }
        else if (domain instanceof DateDomain) {
            DateDomain dateDomain = (DateDomain) domain;
            dateTamInterval = String.valueOf(dateDomain.getTamInterval());
            Date minDate = dateDomain.getMinDate();
            if (minDate != null) dateMinDate = dateFormat.format(minDate);
            Date maxDate = dateDomain.getMaxDate();
            if (maxDate != null) dateMaxDate = dateFormat.format(maxDate);
        }
        else if (domain instanceof NumericDomain) {
            NumericDomain numericDomain = (NumericDomain) domain;
            numericTamInterval = String.valueOf(numericDomain.getTamInterval());
            Number minValue = numericDomain.getMinValue();
            if (minValue != null) numericMinValue = numberFormat.format(minValue);
            else numericMinValue = null;
            Number maxValue = numericDomain.getMaxValue();
            if (maxValue != null) numericMaxValue = numberFormat.format(maxValue);
            else numericMaxValue = null;
        }
    }

    public void clear() {
        domainProperty = null;
        propertyId = null;
        propertyNameI18nMap.clear();
        maxNumberOfIntervals = null;
        labelIntervalsToHideI18nMap.clear();
        dateTamInterval = null;
        dateMinDate = null;
        dateMaxDate = null;
        numericTamInterval = null;
        numericMinValue = null;
        numericMaxValue = null;
    }
}
