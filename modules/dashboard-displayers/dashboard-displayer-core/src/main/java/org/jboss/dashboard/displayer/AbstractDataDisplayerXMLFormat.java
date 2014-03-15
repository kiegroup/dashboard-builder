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
package org.jboss.dashboard.displayer;

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.domain.Domain;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.Map;
import java.util.Locale;

import org.jboss.dashboard.domain.DomainConfiguration;
import org.jboss.dashboard.domain.RangeConfiguration;
import org.jboss.dashboard.domain.date.DateDomain;
import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.domain.numeric.NumericDomain;
import org.jboss.dashboard.export.DataDisplayerXMLFormat;
import org.jboss.dashboard.export.ImportResults;
import org.jboss.dashboard.LocaleManager;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Base class for the implementation of custom data displayer XML formatters.
 */
public abstract class AbstractDataDisplayerXMLFormat implements DataDisplayerXMLFormat {

    protected abstract void parseDisplayer(DataDisplayer displayer, NodeList xmlNodes, ImportResults results) throws Exception;
    protected abstract void formatDisplayer(DataDisplayer displayer, PrintWriter out, int indent) throws Exception;

    public void printIndent(PrintWriter out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.print("  ");
        }
    }

    public DataDisplayer parse(String xml, ImportResults results) throws Exception {
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
        dFactory.setIgnoringComments(true);
        StringReader isr = new StringReader(xml);
        Document doc = dBuilder.parse(new InputSource(isr));
        isr.close();
        return parse(doc.getChildNodes(), results);
    }

    public DataDisplayer parse(NodeList xmlNodes, ImportResults results) throws Exception {
        for (int i = 0; i < xmlNodes.getLength(); i++) {
            Node item = xmlNodes.item(i);
            if (item.getNodeName().equals("displayer") && item.hasAttributes() && item.hasChildNodes()) {
                String typeUid = item.getAttributes().getNamedItem("type").getNodeValue();
                DataDisplayerType type = DataDisplayerServices.lookup().getDataDisplayerManager().getDisplayerTypeByUid(typeUid);
                DataDisplayerRenderer renderer = null;

                Node rendererNode = item.getAttributes().getNamedItem("renderer");
                if (rendererNode != null) {
                    String rendUid = rendererNode.getNodeValue();
                    renderer = DataDisplayerServices.lookup().getDataDisplayerManager().getDisplayerRendererByUid(rendUid);
                }
                
                DataDisplayer displayer = type.createDataDisplayer();
                displayer.setDataDisplayerRenderer(renderer);
                parseDisplayer(displayer, item.getChildNodes(), results);
                return displayer;
            }
        }
        throw new IllegalArgumentException("Missing <displayer> tag.");
    }

    protected DomainConfiguration parseDomain(NodeList domainNodes) {
        DomainConfiguration domainConfig = new DomainConfiguration();
        for (int k = 0; k < domainNodes.getLength(); k++) {
            Node item = domainNodes.item(k);
            if (item.getNodeName().equals("propertyid") && item.hasChildNodes()) {
                domainConfig.setPropertyId(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("name") && item.hasChildNodes()) {
                String name = item.getFirstChild().getNodeValue();
                Locale locale = LocaleManager.currentLocale();
                Node languageNode = item.getAttributes().getNamedItem("language");
                if (languageNode != null) locale = new Locale(languageNode.getNodeValue());
                domainConfig.setPropertyName(StringEscapeUtils.unescapeXml(name), locale);
            }
            if (item.getNodeName().equals("maxnumberofintervals") && item.hasChildNodes()) {
                domainConfig.setMaxNumberOfIntervals(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            // Label domain.
            if (item.getNodeName().equals("intervalstohide") && item.hasChildNodes()) {
                String interval = item.getFirstChild().getNodeValue();
                Locale locale = LocaleManager.currentLocale();
                Node languageNode = item.getAttributes().getNamedItem("language");
                if (languageNode != null) locale = new Locale(languageNode.getNodeValue());
                domainConfig.setLabelIntervalsToHide(StringEscapeUtils.unescapeXml(interval), locale);
            }
            // Date domain.
            if (item.getNodeName().equals("taminterval") && item.hasChildNodes()) {
                domainConfig.setDateTamInterval(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("mindate") && item.hasChildNodes()) {
                domainConfig.setDateMinDate(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("maxdate") && item.hasChildNodes()) {
                domainConfig.setDateMaxDate(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            // Numeric domain.
            if (item.getNodeName().equals("taminterval") && item.hasChildNodes()) {
                domainConfig.setNumericTamInterval(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("minvalue") && item.hasChildNodes()) {
                domainConfig.setNumericMinValue(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("maxvalue") && item.hasChildNodes()) {
                domainConfig.setNumericMaxValue(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
        }
        return domainConfig;
    }

    public String format(DataDisplayer displayer) throws Exception {
        if (!displayer.getDataProvider().isReady()) return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        format(displayer, pw, 0);
        return sw.toString();
    }

    public void format(DataDisplayer displayer, PrintWriter out, int indent) throws Exception {
        DataDisplayerType displayerType = displayer.getDataDisplayerType();
        DataDisplayerRenderer displayerRenderer  = displayer.getDataDisplayerRenderer();

        printIndent(out, indent++);
        out.println("<displayer type=\"" + StringEscapeUtils.escapeXml(displayerType.getUid()) +"\" renderer=\"" + StringEscapeUtils.escapeXml(displayerRenderer.getUid()) + "\">");

        formatDisplayer(displayer, out, indent);

        printIndent(out, --indent);
        out.println("</displayer>");
    }

    protected void formatDomain(DomainConfiguration config, PrintWriter out, int indent) {
        printIndent(out, indent);
        out.print("<propertyid>");
        out.print(StringEscapeUtils.escapeXml(config.getPropertyId()));
        out.println("</propertyid>");

        Map<Locale,String> domainNames = config.getPropertyNameI18nMap();
        for (Locale locale : domainNames.keySet()) {
            printIndent(out, indent);
            out.print("<name language");
            out.print("=\"" + StringEscapeUtils.escapeXml(locale.toString()) + "\">");
            out.print(StringEscapeUtils.escapeXml(domainNames.get(locale)));
            out.println("</name>");
        }

        printIndent(out, indent);
        out.print("<maxnumberofintervals>");
        out.print(StringEscapeUtils.escapeXml(String.valueOf(config.getMaxNumberOfIntervals())));
        out.println("</maxnumberofintervals>");

        // Label domain specifics.
        Domain domain = config.getDomainProperty().getDomain();
        if (domain instanceof LabelDomain) {
            Map<Locale,String> intervalsToHide = config.getLabelIntervalsToHideI18nMap();
            for (Locale locale : intervalsToHide.keySet()) {
                printIndent(out, indent);
                out.print("<intervalstohide language");
                out.print("=\"" + StringEscapeUtils.escapeXml(locale.toString()) + "\">");
                out.print(StringEscapeUtils.escapeXml(intervalsToHide.get(locale)));
                out.println("</intervalstohide>");
            }
        }
        else if (domain instanceof DateDomain) {

            if (config.getDateTamInterval() != null) {
                printIndent(out, indent);
                out.print("<taminterval>");
                out.print(StringEscapeUtils.escapeXml(String.valueOf(config.getDateTamInterval())));
                out.println("</taminterval>");
            }
            if (config.getDateMinDate() != null) {
                printIndent(out, indent);
                out.print("<mindate>");
                out.print(StringEscapeUtils.escapeXml(config.getDateMinDate()));
                out.println("</mindate>");
            }
            if (config.getDateMaxDate() != null) {
                printIndent(out, indent);
                out.print("<maxdate>");
                out.print(StringEscapeUtils.escapeXml(config.getDateMaxDate()));
                out.println("</maxdate>");
            }
        }
        // Numeric domain specifics.
        else if (domain instanceof NumericDomain) {
            if (config.getNumericTamInterval() != null) {
                printIndent(out, indent);
                out.print("<taminterval>");
                out.print(StringEscapeUtils.escapeXml(String.valueOf(config.getNumericTamInterval())));
                out.println("</taminterval>");
            }
            if (config.getNumericMinValue() != null) {
                printIndent(out, indent);
                out.print("<minvalue>");
                out.print(StringEscapeUtils.escapeXml(String.valueOf(config.getNumericMinValue())));
                out.println("</minvalue>");
            }
            if (config.getNumericMaxValue() != null) {
                printIndent(out, indent);
                out.print("<maxvalue>");
                out.print(StringEscapeUtils.escapeXml(String.valueOf(config.getNumericMaxValue())));
                out.println("</maxvalue>");
            }
        }
    }

    protected RangeConfiguration parseRange(NodeList rangeNodes) {
        RangeConfiguration rangeConfig = new RangeConfiguration();
        for (int k = 0; k < rangeNodes.getLength(); k++) {
            Node item = rangeNodes.item(k);
            if (item.getNodeName().equals("propertyid") && item.hasChildNodes()) {
                rangeConfig.setPropertyId(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("name") && item.hasChildNodes()) {
                String name = item.getFirstChild().getNodeValue();
                Locale locale = LocaleManager.currentLocale();
                Node languageNode = item.getAttributes().getNamedItem("language");
                if (languageNode != null) locale = new Locale(languageNode.getNodeValue());
                rangeConfig.setName(StringEscapeUtils.unescapeXml(name), locale);
            }
            if (item.getNodeName().equals("scalarfunction") && item.hasChildNodes()) {
                rangeConfig.setScalarFunctionCode(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("unit") && item.hasChildNodes()) {
                String unit = item.getFirstChild().getNodeValue();
                Locale locale = LocaleManager.currentLocale();
                Node languageNode = item.getAttributes().getNamedItem("language");
                if (languageNode != null) locale = new Locale(languageNode.getNodeValue());
                rangeConfig.setUnit(StringEscapeUtils.unescapeXml(unit), locale);
            }
        }
        return rangeConfig;
    }

    protected void formatRange(RangeConfiguration config, PrintWriter out, int indent) {
        printIndent(out, indent);
        out.print("<propertyid>");
        out.print(StringEscapeUtils.escapeXml(config.getPropertyId()));
        out.println("</propertyid>");

        // Range properties.
        Map<Locale,String> rangeDescriptions = config.getNameI18nMap();
        if (rangeDescriptions != null) {
            for (Locale rangeKey : rangeDescriptions.keySet()) {
                printIndent(out, indent);
                out.print("<name language");
                out.print("=\"" + StringEscapeUtils.escapeXml(rangeKey.toString()) + "\">");
                out.print(StringEscapeUtils.escapeXml(rangeDescriptions.get(rangeKey)));
                out.println("</name>");
            }
        }
        String scalarFunctionCode = config.getScalarFunctionCode();
        if (scalarFunctionCode != null) {
            printIndent(out, indent);
            out.print("<scalarfunction>");
            out.print(StringEscapeUtils.escapeXml(String.valueOf(scalarFunctionCode)));
            out.println("</scalarfunction>");
        }

        // Unit
        Map<Locale,String> unitDescriptions = config.getUnitI18nMap();
        if (unitDescriptions != null) {
            for (Locale unitKey : unitDescriptions.keySet()) {
                printIndent(out, indent);
                out.print("<unit language");
                out.print("=\"" + StringEscapeUtils.escapeXml(unitKey.toString()) + "\">");
                out.print(StringEscapeUtils.escapeXml(unitDescriptions.get(unitKey)));
                out.println("</unit>");
            }
        }
    }
}
