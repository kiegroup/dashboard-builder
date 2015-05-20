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
package org.jboss.dashboard.displayer.chart;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.export.ImportResults;
import org.jboss.dashboard.LocaleManager;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@ApplicationScoped
@Alternative
public class MeterChartDisplayerXMLFormat extends ChartDisplayerXMLFormat {

    /** Logger */
    private transient static Logger log = LoggerFactory.getLogger(ChartDisplayerXMLFormat.class);

    protected void parseDisplayer(AbstractChartDisplayer displayer, Node item, ImportResults results) throws Exception {
        if (item.hasChildNodes() && (item.getNodeName().equals("meter") ||
                item.getNodeName().equals("thermometer") ||
                item.getNodeName().equals("dial"))) {
            try {
                parseMeterProperties(displayer, item, results);
            } catch (Exception e) {
                log.error("Can't parse meter displayer properties" + e);
            }
        }
        else {
            super.parseDisplayer(displayer, item, results);
        }
    }

    protected void parseMeterProperties(DataDisplayer displayer, Node item, ImportResults results) throws Exception {
        Locale locale = LocaleManager.currentLocale();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        MeterChartDisplayer meterDisplayer = (MeterChartDisplayer) displayer;
        if (item.getNodeName().equals("meter") && item.hasChildNodes()) {
            NodeList meterNodes = item.getChildNodes();
            for (int k = 0; k < meterNodes.getLength(); k++) {
                Node meterItem = meterNodes.item(k);
                if (meterItem.getNodeName().equals("positionType") && meterItem.hasChildNodes()) {
                    meterDisplayer.setPositionType(StringEscapeUtils.unescapeXml(meterItem.getFirstChild().getNodeValue()));
                }
                if (meterItem.getNodeName().equals("minValue") && meterItem.hasChildNodes()) {
                    meterDisplayer.setMinValue(numberFormat.parse(StringEscapeUtils.unescapeXml(meterItem.getFirstChild().getNodeValue())).doubleValue());
                }
                if (meterItem.getNodeName().equals("maxValue") && meterItem.hasChildNodes()) {
                    meterDisplayer.setMaxValue(numberFormat.parse(StringEscapeUtils.unescapeXml(meterItem.getFirstChild().getNodeValue())).doubleValue());
                }
                if (meterItem.getNodeName().equals("maxMeterTicks") && meterItem.hasChildNodes()) {
                    meterDisplayer.setMaxMeterTicks(numberFormat.parse(StringEscapeUtils.unescapeXml(meterItem.getFirstChild().getNodeValue())).intValue());
                }
                // Thresholds.
                if (meterItem.getNodeName().equals("warningThreshold") && meterItem.hasChildNodes()) {
                    meterDisplayer.setWarningThreshold(numberFormat.parse(StringEscapeUtils.unescapeXml(meterItem.getFirstChild().getNodeValue())).doubleValue());
                }
                if (meterItem.getNodeName().equals("criticalThreshold") && meterItem.hasChildNodes()) {
                    meterDisplayer.setCriticalThreshold(numberFormat.parse(StringEscapeUtils.unescapeXml(meterItem.getFirstChild().getNodeValue())).doubleValue());
                }
                // Critical interval.
                if (meterItem.getNodeName().equals("descripCriticalInterval") && meterItem.hasChildNodes()) {
                    String descripCriticalInterval = meterItem.getFirstChild().getNodeValue();
                    Node languageNode = meterItem.getAttributes().getNamedItem("language");
                    if (languageNode != null) locale = new Locale(languageNode.getNodeValue());
                    meterDisplayer.setDescripCriticalInterval(StringEscapeUtils.unescapeXml(descripCriticalInterval), locale);
                }
                // Warning interval.
                if (meterItem.getNodeName().equals("descripWarningInterval") && meterItem.hasChildNodes()) {
                    String descripWarningInterval = meterItem.getFirstChild().getNodeValue();
                    Node languageNode = meterItem.getAttributes().getNamedItem("language");
                    if (languageNode != null) locale = new Locale(languageNode.getNodeValue());
                    meterDisplayer.setDescripWarningInterval(StringEscapeUtils.unescapeXml(descripWarningInterval), locale);
                }
                // Normal interval.
                if (meterItem.getNodeName().equals("descripNormalInterval") && meterItem.hasChildNodes()) {
                    String descripNormalInterval = meterItem.getFirstChild().getNodeValue();
                    Node languageNode = meterItem.getAttributes().getNamedItem("language");
                    if (languageNode != null) locale = new Locale(languageNode.getNodeValue());
                    meterDisplayer.setDescripNormalInterval(StringEscapeUtils.unescapeXml(descripNormalInterval), locale);
                }
            }
        } else if (item.getNodeName().equals("thermometer") && item.hasChildNodes()) {
            NodeList thermoNodes = item.getChildNodes();
            for (int k = 0; k < thermoNodes.getLength(); k++) {
                Node thermoItem = thermoNodes.item(k);
                if (thermoItem.getNodeName().equals("positionType") && thermoItem.hasChildNodes()) {
                    meterDisplayer.setPositionType(StringEscapeUtils.unescapeXml(thermoItem.getFirstChild().getNodeValue()));
                }
                if (thermoItem.getNodeName().equals("thermoLowerBound") && thermoItem.hasChildNodes()) {
                    meterDisplayer.setThermoLowerBound(numberFormat.parse(StringEscapeUtils.unescapeXml(thermoItem.getFirstChild().getNodeValue())).doubleValue());
                }
                if (thermoItem.getNodeName().equals("thermoUpperBound") && thermoItem.hasChildNodes()) {
                    meterDisplayer.setThermoUpperBound(numberFormat.parse(StringEscapeUtils.unescapeXml(thermoItem.getFirstChild().getNodeValue())).doubleValue());
                }
                // Thresholds.
                if (thermoItem.getNodeName().equals("warningThermoThreshold") && thermoItem.hasChildNodes()) {
                    meterDisplayer.setWarningThermoThreshold(numberFormat.parse(StringEscapeUtils.unescapeXml(thermoItem.getFirstChild().getNodeValue())).doubleValue());
                }
                if (thermoItem.getNodeName().equals("criticalThermoThreshold") && thermoItem.hasChildNodes()) {
                    meterDisplayer.setCriticalThermoThreshold(numberFormat.parse(StringEscapeUtils.unescapeXml(thermoItem.getFirstChild().getNodeValue())).doubleValue());
                }
            }
        } else if (item.getNodeName().equals("dial")) {
            NodeList dialNodes = item.getChildNodes();
            for (int k = 0; k < dialNodes.getLength(); k++) {
                Node dialItem = dialNodes.item(k);
                if (dialItem.getNodeName().equals("positionType") && dialItem.hasChildNodes()) {
                    meterDisplayer.setPositionType(StringEscapeUtils.unescapeXml(dialItem.getFirstChild().getNodeValue()));
                }
                if (dialItem.getNodeName().equals("pointerType") && dialItem.hasChildNodes()) {
                    meterDisplayer.setPointerType(StringEscapeUtils.unescapeXml(dialItem.getFirstChild().getNodeValue()));
                }
                if (dialItem.getNodeName().equals("dialLowerBound") && dialItem.hasChildNodes()) {
                    meterDisplayer.setDialLowerBound(numberFormat.parse(StringEscapeUtils.unescapeXml(dialItem.getFirstChild().getNodeValue())).doubleValue());
                }
                if (dialItem.getNodeName().equals("dialUpperBound") && dialItem.hasChildNodes()) {
                    meterDisplayer.setDialUpperBound(numberFormat.parse(StringEscapeUtils.unescapeXml(dialItem.getFirstChild().getNodeValue())).doubleValue());
                }
                if (dialItem.getNodeName().equals("maxTicks") && dialItem.hasChildNodes()) {
                    meterDisplayer.setMaxTicks(numberFormat.parse(StringEscapeUtils.unescapeXml(dialItem.getFirstChild().getNodeValue())).intValue());
                }
                if (dialItem.getNodeName().equals("minorTickCount") && dialItem.hasChildNodes()) {
                    meterDisplayer.setMinorTickCount(numberFormat.parse(StringEscapeUtils.unescapeXml(dialItem.getFirstChild().getNodeValue())).intValue());
                }
            }
        }
    }


    protected void formatDisplayer(DataDisplayer displayer, PrintWriter out, int indent) throws Exception {
        super.formatDisplayer(displayer, out, indent);

        Locale locale = LocaleManager.currentLocale();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        MeterChartDisplayer meterDisplayer = (MeterChartDisplayer) displayer;
        if (meterDisplayer.getType().equals("meter")) {
            printIndent(out, indent++);
            out.println("<meter>");
            // Meter properties.
            // Position type
            printIndent(out, indent);
            out.print("<positionType>");
            out.print(StringEscapeUtils.escapeXml(meterDisplayer.getPositionType()));
            out.println("</positionType>");
            // Min value
            printIndent(out, indent);
            out.print("<minValue>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getMinValue())));
            out.println("</minValue>");
            // Thresholds.
            printIndent(out, indent);
            out.print("<warningThreshold>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getWarningThreshold())));
            out.println("</warningThreshold>");
            printIndent(out, indent);
            out.print("<criticalThreshold>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getCriticalThreshold())));
            out.println("</criticalThreshold>");
            // Normal interval
            // Interval descriptions. Hide them until the global legend will be available.
            /*
                Map descripNormalInterval = meterDisplayer.getDescripNormalIntervalI18nMap();
                Iterator descripNormalIntervalKeys = descripNormalInterval.keySet().iterator();
                while (descripNormalIntervalKeys.hasNext()) {
                    Locale l = (Locale) descripNormalIntervalKeys.next();
                    printIndent(out, indent);
                    out.print("<descripNormalInterval language");
                    out.print("=\"" + StringEscapeUtils.escapeXml(l.toString()) + "\">");
                    out.print(StringEscapeUtils.escapeXml((String) descripNormalInterval.get(l)));
                    out.println("</descripNormalInterval>");
                }
            */
            // Warning interval.
            // Interval descriptions. Hide them until the global legend will be available.
            /*
                Map descripWarningInterval = meterDisplayer.getDescripWarningIntervalI18nMap();
                Iterator descripWarningIntervalKeys = descripWarningInterval.keySet().iterator();
                while (descripWarningIntervalKeys.hasNext()) {
                    Locale l = (Locale) descripWarningIntervalKeys.next();
                    printIndent(out, indent);
                    out.print("<descripWarningInterval language");
                    out.print("=\"" + StringEscapeUtils.escapeXml(l.toString()) + "\">");
                    out.print(StringEscapeUtils.escapeXml((String) descripWarningInterval.get(l)));
                    out.println("</descripWarningInterval>");
                }
            */
            // Critical interval.
            // Interval descriptions. Hide them until the global legend will be available.
            /*
                Map descripCriticalInterval = meterDisplayer.getDescripCriticalIntervalI18nMap();
                Iterator descripCriticalIntervalKeys = descripCriticalInterval.keySet().iterator();
                while (descripCriticalIntervalKeys.hasNext()) {
                    Locale l = (Locale) descripCriticalIntervalKeys.next();
                    printIndent(out, indent);
                    out.print("<descripCriticalInterval language");
                    out.print("=\"" + StringEscapeUtils.escapeXml(l.toString()) + "\">");
                    out.print(StringEscapeUtils.escapeXml((String) descripCriticalInterval.get(l)));
                    out.println("</descripCriticalInterval>");
                }
            */
            // Max value
            printIndent(out, indent);
            out.print("<maxValue>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getMaxValue())));
            out.println("</maxValue>");
            // Maximum number of ticks.
            printIndent(out, indent);
            out.print("<maxMeterTicks>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getMaxMeterTicks())));
            out.println("</maxMeterTicks>");
            printIndent(out, --indent);
            out.println("</meter>");
        } else if (meterDisplayer.getType().equals("thermometer")) {
            printIndent(out, indent++);
            out.println("<thermometer>");

            // Position type
            printIndent(out, indent);
            out.print("<positionType>");
            out.print(StringEscapeUtils.escapeXml(meterDisplayer.getPositionType()));
            out.println("</positionType>");
            // Lower bound
            printIndent(out, indent);
            out.print("<thermoLowerBound>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getThermoLowerBound())));
            out.println("</thermoLowerBound>");
            // Thresholds.
            printIndent(out, indent);
            out.print("<warningThermoThreshold>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getWarningThermoThreshold())));
            out.println("</warningThermoThreshold>");
            printIndent(out, indent);
            out.print("<criticalThermoThreshold>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getCriticalThermoThreshold())));
            out.println("</criticalThermoThreshold>");
            // Upper bound
            printIndent(out, indent);
            out.print("<thermoUpperBound>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getThermoUpperBound())));
            out.println("</thermoUpperBound>");
            printIndent(out, --indent);
            out.println("</thermometer>");
        } else if (meterDisplayer.getType().equals("dial")) {
            printIndent(out, indent++);
            out.println("<dial>");
            // Position type
            printIndent(out, indent);
            out.print("<positionType>");
            out.print(StringEscapeUtils.escapeXml(meterDisplayer.getPositionType()));
            out.println("</positionType>");
            // Pointer type.
            printIndent(out, indent);
            out.print("<pointerType>");
            out.print(StringEscapeUtils.escapeXml(meterDisplayer.getPointerType()));
            out.println("</pointerType>");
            // Lower bound.
            printIndent(out, indent);
            out.print("<dialLowerBound>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getDialLowerBound())));
            out.println("</dialLowerBound>");
            // Upper bound.
            printIndent(out, indent);
            out.print("<dialUpperBound>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getDialUpperBound())));
            out.println("</dialUpperBound>");
            // Max ticks.
            printIndent(out, indent);
            out.print("<maxTicks>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getMaxTicks())));
            out.println("</maxTicks>");
            // Minor tick count.
            printIndent(out, indent);
            out.print("<minorTickCount>");
            out.print(StringEscapeUtils.escapeXml(numberFormat.format(meterDisplayer.getMinorTickCount())));
            out.println("</minorTickCount>");
            printIndent(out, --indent);
            out.println("</dial>");
        }
    }
}
