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
package org.jboss.dashboard.ui.components.chart;

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.displayer.chart.MeterChartDisplayer;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.function.MaxFunction;
import org.jboss.dashboard.function.MinFunction;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.LocaleManager;

import java.awt.*;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.laures.cewolf.ChartPostProcessor;
import de.laures.cewolf.links.LinkGenerator;
import de.laures.cewolf.tooltips.ToolTipGenerator;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;

public class JFreeMeterChartViewer extends JFreeAbstractChartViewer implements ChartPostProcessor, Serializable {

    protected int intervalToShow;

    public JFreeMeterChartViewer () {
        producerId = "MeterChartViewer_DatasetProducer_ID";
    }

    public int getIntervalToShow() {
        return intervalToShow;
    }

    public void setIntervalToShow(int intervalToShow) {
        this.intervalToShow = intervalToShow;
    }

    public Object produceDataset(Map params) {
        try {
            Locale locale = LocaleManager.currentLocale();
            MeterChartDisplayer displayer = (MeterChartDisplayer) getDataDisplayer();
            DataSet xyDataSet = displayer.buildXYDataSet();
            NumberFormat numberFormat = NumberFormat.getNumberInstance(LocaleManager.currentLocale());
            numberFormat.setGroupingUsed(false);
            if (displayer.isAxisInteger()) {
                numberFormat.setMaximumFractionDigits(0);
            }

            DefaultValueDataset valueds = new DefaultValueDataset();
            for (int i=0; i< xyDataSet.getRowCount(); i++) {
                String xvalue = ((Interval) xyDataSet.getValueAt(i, 0)).getDescription(locale);
                double yvalue = ((Number) xyDataSet.getValueAt(i, 1)).doubleValue();
                if (intervalToShow == i) {
                    displayer.setSubtitle(xvalue);
                    displayer.setDialValue(numberFormat.format(yvalue));
                    valueds.setValue(new Double(yvalue));
                }
            }

            // Set the minimum and maximum dataset values to the meter chart displayer.
            ScalarFunction minFunction = DataDisplayerServices.lookup().getScalarFunctionManager().getScalarFunctionByCode(MinFunction.CODE);
            ScalarFunction maxFunction = DataDisplayerServices.lookup().getScalarFunctionManager().getScalarFunctionByCode(MaxFunction.CODE);
            List yvalues = xyDataSet.getPropertyValues(displayer.getRangeProperty());
            double minValue = minFunction.scalar(yvalues);
            double maxValue = maxFunction.scalar(yvalues);
            displayer.setMinDatasetValue(minValue);
            displayer.setMaxDatasetValue(maxValue);
            return valueds;
        } catch (Exception e) {
            return new DefaultValueDataset();
        }
    }

    public LinkGenerator getLinkGenerator() {
        // Not supported in meters
        return null;
    }

    public ToolTipGenerator getToolTipGenerator() {
        // Not supported in meters
        return null;
    }

    // ChartPostProcessor interface.

    public void processChart(Object chart, Map params) {
        MeterChartDisplayer displayer = (MeterChartDisplayer) getDataDisplayer();
        Locale locale = LocaleManager.currentLocale();
        JFreeChart localChart = (JFreeChart) chart;

        // Add the subtitle.
        if (displayer.getSubtitle() != null) {
            TextTitle tt = new TextTitle(displayer.getSubtitle());
            localChart.addSubtitle(tt);
        }
        // Process the properties chart.
        Plot plot = localChart.getPlot();
        if (displayer.getType().equals("meter")) {
            MeterPlot meterPlot = (MeterPlot) plot;
            // Meter units are the range units.
            meterPlot.setUnits(StringUtils.replace(displayer.getUnit(locale), MeterChartDisplayer.UNIT_VALUE_TAG, ""));
            meterPlot.setRange(new Range(displayer.getMinValue(), displayer.getMaxValue()));
            MeterInterval normalInterval = new MeterInterval(displayer.getDescripNormalInterval(locale), new Range(displayer.getMinValue(), displayer.getWarningThreshold()), Color.green, new BasicStroke(2.0f), null);
            meterPlot.addInterval(normalInterval);
            MeterInterval warningInterval = new MeterInterval(displayer.getDescripWarningInterval(locale),new Range(displayer.getWarningThreshold(), displayer.getCriticalThreshold()), Color.yellow, new BasicStroke(2.0f), null);
            meterPlot.addInterval(warningInterval);
            MeterInterval criticalInterval = new MeterInterval(displayer.getDescripCriticalInterval(locale),new Range(displayer.getCriticalThreshold(), displayer.getMaxValue()), Color.red, new BasicStroke(2.0f), null);
            meterPlot.addInterval(criticalInterval);
            // Maximum number of ticks is limited to (maxValue - minValue) / maxMeterTicks.
            meterPlot.setTickSize((displayer.getMaxValue() - displayer.getMinValue()) / displayer.getMaxMeterTicks());
        }
        else if (displayer.getType().equals("thermometer")) {
            ThermometerPlot thermoPlot = (ThermometerPlot) plot;
            // Original units of the thermometer disabled (NONE). Apply here the same pattern as in meter type.
            thermoPlot.setUnits(ThermometerPlot.UNITS_NONE);
            thermoPlot.setUseSubrangePaint(false);
            thermoPlot.setThermometerPaint(displayer.getThermometerColor());
            thermoPlot.setMercuryPaint(displayer.getMercuryColor());
            thermoPlot.setValuePaint(displayer.getValueColor());
            thermoPlot.setUpperBound(displayer.getThermoUpperBound());
            thermoPlot.setLowerBound(displayer.getThermoLowerBound());
            // Build the subranges.
            thermoPlot.setSubrange(thermoPlot.NORMAL, displayer.getThermoLowerBound(), displayer.getWarningThermoThreshold());
            thermoPlot.setSubrange(thermoPlot.WARNING, displayer.getWarningThermoThreshold(), displayer.getCriticalThermoThreshold());
            thermoPlot.setSubrange(thermoPlot.CRITICAL, displayer.getCriticalThermoThreshold(), displayer.getThermoUpperBound());
        }
        else if (displayer.getType().equals("dial")) {
            DialPlot dialPlot = (DialPlot) plot;
            if ("pin".equals(displayer.getPointerType())) {
                dialPlot.removePointer(0);
                dialPlot.addPointer(new DialPointer.Pin());
            }
            else if ("pointer".equals(displayer.getPointerType())) {
                dialPlot.removePointer(0);
                dialPlot.addPointer(new DialPointer.Pointer());
            }
            StandardDialScale scale = (StandardDialScale) dialPlot.getScale(0);
            scale.setLowerBound(displayer.getDialLowerBound());
            scale.setUpperBound(displayer.getDialUpperBound());

            // Calculate the majorTickIncrement
            double majorTickIncrement = (displayer.getDialUpperBound() - displayer.getDialLowerBound()) / displayer.getMaxTicks();
            scale.setMajorTickIncrement(majorTickIncrement);
            scale.setMinorTickCount(displayer.getMinorTickCount());
            String dialText = StringUtils.replace(displayer.getUnit(locale), MeterChartDisplayer.UNIT_VALUE_TAG, displayer.getDialValue());
            DialTextAnnotation annotation = new DialTextAnnotation(dialText);
            annotation.setFont(new Font("Dialog", Font.BOLD, 10));
            annotation.setRadius(0.35);
            annotation.setAngle(90.0);
            dialPlot.addLayer(annotation);
        }
    }
}

