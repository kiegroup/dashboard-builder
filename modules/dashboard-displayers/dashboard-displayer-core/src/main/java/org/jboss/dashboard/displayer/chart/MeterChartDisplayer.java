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
package org.jboss.dashboard.displayer.chart;

import org.jboss.dashboard.LocaleManager;

import java.util.*;
import java.awt.*;

/**
 * Meter chart displayer implementation.
 */
public class MeterChartDisplayer extends AbstractChartDisplayer {

    // Common properties
    protected String subtitle;

    // Position of the meters: vertical or horizontal.
    protected String positionType;

    // Meter properties.
    protected int maxMeterTicks;
    protected double minDatasetValue;
    protected double maxDatasetValue;
    protected double minValue;
    protected double maxValue;
    protected double criticalThreshold;
    protected double warningThreshold;
    protected Map<Locale, String> descripCriticalIntervalI18nMap;
    protected Color colorCriticalInterval;
    protected Map<Locale, String> descripWarningIntervalI18nMap;
    protected Color colorWarningInterval;
    protected Map<Locale, String> descripNormalIntervalI18nMap;
    protected Color colorNormalInterval;

    // Thermometer properties.
    protected Color mercuryColor;
    protected Color valueColor;
    protected Color thermometerColor;
    protected double thermoLowerBound;
    protected double thermoUpperBound;
    protected double warningThermoThreshold;
    protected double criticalThermoThreshold;

    // Dial properties.
    protected String pointerType;
    protected double dialLowerBound;
    protected double dialUpperBound;
    protected int maxTicks;
    protected int minorTickCount;
    protected String dialValue;

    /** The locale manager. */
    protected LocaleManager localeManager;

    // Constructor of the class
    public MeterChartDisplayer() {
        super();
        // New width and height.
        width = 180;
        height = 180;

        // Common properties.
        type = "meter";
        positionType = "horizontal";
        subtitle = null;

        // Meter properties.
        maxMeterTicks = 10;
        colorNormalInterval = Color.green;
        colorWarningInterval = Color.yellow;
        colorCriticalInterval = Color.red;
        descripNormalIntervalI18nMap = new HashMap<Locale, String>();
        descripWarningIntervalI18nMap = new HashMap<Locale, String>();
        descripCriticalIntervalI18nMap = new HashMap<Locale, String>();

        // Thresholds.
        warningThreshold = -1;
        criticalThreshold = -1;

        // Thermometer properties.
        mercuryColor = Color.decode("#00CCCC");
        valueColor = Color.decode("#FFFFFF");
        thermometerColor = Color.decode("#000000");

        // Thresholds.
        warningThermoThreshold = -1;
        criticalThermoThreshold = -1;

        // Dial properties.
        pointerType = "pin";
        dialLowerBound = getMinValue();
        dialUpperBound = getMaxValue();
        maxTicks = 5;
        minorTickCount = 5;

        localeManager = LocaleManager.lookup();
    }

    // Meter properties.

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    public int getMaxMeterTicks() {
        return maxMeterTicks;
    }

    public void setMaxMeterTicks(int maxMeterTicks) {
        this.maxMeterTicks = maxMeterTicks;
    }

    public double getMinDatasetValue() {
        return minDatasetValue;
    }

    public void setMinDatasetValue(double minDatasetValue) {
        this.minDatasetValue = minDatasetValue;
    }

    public double getMaxDatasetValue() {
        return maxDatasetValue;
    }

    public void setMaxDatasetValue(double maxDatasetValue) {
        this.maxDatasetValue = maxDatasetValue;
    }

    public double getMinValue() {
        // Avoid minimum values greater than the minimum dataset value.
        if (minValue > minDatasetValue) this.minValue = minDatasetValue;

        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        // Avoid maximum values lower than the maximum dataset value.
        if (maxValue < maxDatasetValue) this.maxValue = maxDatasetValue;

        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getCriticalThreshold() {
        // Initialize the warning threshold to the 33%.
        if (criticalThreshold == -1) criticalThreshold = ((getMaxValue() - getMinValue()) * 66) / 100;

        return criticalThreshold;
    }

    public void setCriticalThreshold(double criticalThreshold) {
        this.criticalThreshold = criticalThreshold;
    }

    public double getWarningThreshold() {
        // Initialize the warning threshold to the 33%.
        if (warningThreshold == -1) warningThreshold = ((getMaxValue() - getMinValue()) * 33) / 100;

        return warningThreshold;
    }

    public void setWarningThreshold(double warningThreshold) {
        this.warningThreshold = warningThreshold;
    }

    // Critical interval.

    public Map<Locale, String> getDescripCriticalIntervalI18nMap() {
        return descripCriticalIntervalI18nMap;
    }

    public void setDescripCriticalIntervalI18nMap(Map<Locale, String> descripCriticalIntervalI18nMap) {
        this.descripCriticalIntervalI18nMap.clear();
        this.descripCriticalIntervalI18nMap.putAll(descripCriticalIntervalI18nMap);
    }

    public String getDescripCriticalInterval(Locale l) {
        String result = descripCriticalIntervalI18nMap.get(l);
        if (result == null) {
            ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.displayer.messages", LocaleManager.currentLocale());
            descripCriticalIntervalI18nMap.put(l, result = i18n.getString("meterChartDisplayer.criticalDefault"));
        }
        return result;
    }

    public void setDescripCriticalInterval(String description, Locale l) {
        descripCriticalIntervalI18nMap.put(l, description);
    }

    public Color getColorCriticalInterval() {
        return colorCriticalInterval;
    }

    public void setColorCriticalInterval(Color colorCriticalInterval) {
        this.colorCriticalInterval = colorCriticalInterval;
    }

    // Warning interval.

    public Map<Locale, String> getDescripWarningIntervalI18nMap() {
        return descripWarningIntervalI18nMap;
    }

    public void setDescripWarningIntervalI18nMap(Map<Locale, String> descripWarningIntervalI18nMap) {
        this.descripWarningIntervalI18nMap.clear();
        this.descripWarningIntervalI18nMap.putAll(descripWarningIntervalI18nMap);
    }

    public String getDescripWarningInterval(Locale l) {
        String result = descripWarningIntervalI18nMap.get(l);
        if (result == null) {
            ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.displayer.messages", LocaleManager.currentLocale());
            descripWarningIntervalI18nMap.put(l, result = i18n.getString("meterChartDisplayer.warningDefault"));
        }
        return result;
    }

    public void setDescripWarningInterval(String description, Locale l) {
        descripWarningIntervalI18nMap.put(l, description);
    }

    public Color getColorWarningInterval() {
        return colorWarningInterval;
    }

    public void setColorWarningInterval(Color colorWarningInterval) {
        this.colorWarningInterval = colorWarningInterval;
    }

    // Normal interval.

    public Map<Locale, String> getDescripNormalIntervalI18nMap() {
        return descripNormalIntervalI18nMap;
    }

    public void setDescripNormalIntervalI18nMap(Map<Locale, String> descripNormalIntervalI18nMap) {
        this.descripNormalIntervalI18nMap.clear();
        this.descripNormalIntervalI18nMap.putAll(descripNormalIntervalI18nMap);
    }

    public String getDescripNormalInterval(Locale l) {
        String result = descripNormalIntervalI18nMap.get(l);
        if (result == null) {
            ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.displayer.messages", LocaleManager.currentLocale());
            descripNormalIntervalI18nMap.put(l, result = i18n.getString("meterChartDisplayer.normalDefault"));
        }
        return result;
    }

    public void setDescripNormalInterval(String description, Locale l) {
        descripNormalIntervalI18nMap.put(l, description);
    }

    public Color getColorNormalInterval() {
        return colorNormalInterval;
    }

    public void setColorNormalInterval(Color colorNormalInterval) {
        this.colorNormalInterval = colorNormalInterval;
    }

    // Thermometer properties.

    public Color getMercuryColor() {
        return mercuryColor;
    }

    public void setMercuryColor(Color mercuryColor) {
        this.mercuryColor = mercuryColor;
    }

    public Color getValueColor() {
        return valueColor;
    }

    public void setValueColor(Color valueColor) {
        this.valueColor = valueColor;
    }

    public Color getThermometerColor() {
        return thermometerColor;
    }

    public void setThermometerColor(Color thermometerColor) {
        this.thermometerColor = thermometerColor;
    }

    public double getThermoLowerBound() {
        return thermoLowerBound;
    }

    public void setThermoLowerBound(double thermoLowerBound) {
        this.thermoLowerBound = thermoLowerBound;
    }

    public double getThermoUpperBound() {
        return thermoUpperBound;
    }

    public void setThermoUpperBound(double thermoUpperBound) {
        this.thermoUpperBound = thermoUpperBound;
    }

    public double getWarningThermoThreshold() {
        return warningThermoThreshold;
    }

    public void setWarningThermoThreshold(double warningThermoThreshold) {
        this.warningThermoThreshold = warningThermoThreshold;
    }

    public double getCriticalThermoThreshold() {
        return criticalThermoThreshold;
    }

    public void setCriticalThermoThreshold(double criticalThermoThreshold) {
        this.criticalThermoThreshold = criticalThermoThreshold;
    }

    // Dial properties.

    public String getPointerType() {
        return pointerType;
    }

    public void setPointerType(String pointerType) {
        this.pointerType = pointerType;
    }

    public double getDialLowerBound() {
        // Avoid minimum values greater than the minimum dataset value.
        if (dialLowerBound > minDatasetValue) this.dialLowerBound = minDatasetValue;

        return dialLowerBound;
    }

    public void setDialLowerBound(double dialLowerBound) {
        this.dialLowerBound = dialLowerBound;
    }

    public double getDialUpperBound() {
        // Avoid maximum values lower than the maximum dataset value.
        if (dialUpperBound < maxDatasetValue) this.dialUpperBound = maxDatasetValue;
        
        return dialUpperBound;
    }

    public void setDialUpperBound(double dialUpperBound) {
        this.dialUpperBound = dialUpperBound;
    }

    public int getMaxTicks() {
        return maxTicks;
    }

    public void setMaxTicks(int maxTicks) {
        this.maxTicks = maxTicks;
    }

    public int getMinorTickCount() {
        return minorTickCount;
    }

    public void setMinorTickCount(int minorTickCount) {
        this.minorTickCount = minorTickCount;
    }

    public String getDialValue() {
        return dialValue;
    }

    public void setDialValue(String dialValue) {
        this.dialValue = dialValue;
    }
}
