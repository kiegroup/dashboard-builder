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
package org.jboss.dashboard.displayer.table;

import org.jboss.dashboard.displayer.AbstractDataDisplayerXMLFormat;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.domain.DomainConfiguration;
import org.jboss.dashboard.export.ImportResults;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.LocaleManager;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Iterator;
import java.util.Locale;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.commons.lang.StringEscapeUtils;

import javax.enterprise.context.ApplicationScoped;

/**
 * This class offers both Table displayer XML parsing and formatting services.
 */
@ApplicationScoped
public class TableDisplayerXMLFormat extends AbstractDataDisplayerXMLFormat {

    public TableDisplayerXMLFormat() {
        super();
    }

    protected void parseDisplayer(DataDisplayer dd, NodeList subNodes, ImportResults results) throws Exception {
        TableDisplayer displayer = (TableDisplayer) dd;
        DataSetTable table = displayer.getTable();
        
        // First of all look if group by is enabled.
        for (int j = 0; j < subNodes.getLength(); j++) {
            Node item = subNodes.item(j);
            if (item.getNodeName().equals("groupby") && item.hasChildNodes()) {
                NodeList groupByNodes = item.getChildNodes();
                table.setGroupByConfiguration(parseDomain(groupByNodes));
                for (int k=0; k<groupByNodes.getLength(); k++) {
                    item = groupByNodes.item(k);
                    if (item.getNodeName().equals("showtotals") && item.hasChildNodes()) {
                        table.setGroupByShowTotals(Boolean.valueOf(item.getFirstChild().getNodeValue()).booleanValue());
                    }
                    if (item.getNodeName().equals("totalshtmlstyle") && item.hasChildNodes()) {
                        table.setGroupByTotalsHtmlStyle(item.getFirstChild().getNodeValue());
                    }
                }
            }
        }
        // Parse the rest.
        for (int j = 0; j < subNodes.getLength(); j++) {
            Node item = subNodes.item(j);
            if (item.getNodeName().equals("rowsperpage") && item.hasChildNodes()) {
                table.setMaxRowsPerPage(Integer.parseInt(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("headerposition") && item.hasChildNodes()) {
                table.setHeaderPosition(item.getFirstChild().getNodeValue());
            }
            if (item.getNodeName().equals("htmlstyle") && item.hasChildNodes()) {
                table.setHtmlStyle(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("rowevenstyle") && item.hasChildNodes()) {
                table.setRowEvenStyle(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("rowoddstyle") && item.hasChildNodes()) {
                table.setRowOddStyle(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("rowhoverstyle") && item.hasChildNodes()) {
                table.setRowHoverStyle(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("rowevenstyle") && item.hasChildNodes()) {
                table.setRowEvenStyle(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("htmlclass") && item.hasChildNodes()) {
                table.setHtmlClass(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("rowevenclass") && item.hasChildNodes()) {
                table.setRowEventClass(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("rowoddclass") && item.hasChildNodes()) {
                table.setRowOddClass(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            if (item.getNodeName().equals("rowhoverclass") && item.hasChildNodes()) {
                table.setRowHoverClass(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
            }
            else if (item.getNodeName().equals("column") && item.hasChildNodes()) {
                NodeList columnNodes = item.getChildNodes();
                TableColumn column = table.createColumn();
                int columnIndex = table.getColumnCount();
                for (int k = 0; k < columnNodes.getLength(); k++) {
                    item = columnNodes.item(k);
                    if (item.getNodeName().equals("name") && item.hasChildNodes()) {
                        String name = item.getFirstChild().getNodeValue();
                        Locale locale = LocaleManager.currentLocale();
                        Node languageNode = item.getAttributes().getNamedItem("language");
                        if (languageNode != null) locale = new Locale(languageNode.getNodeValue());
                        column.setName(StringEscapeUtils.unescapeXml(name), locale);
                    }
                    if (item.getNodeName().equals("hint") && item.hasChildNodes()) {
                        String name = item.getFirstChild().getNodeValue();
                        Locale locale = LocaleManager.currentLocale();
                        Node languageNode = item.getAttributes().getNamedItem("language");
                        if (languageNode != null) locale = new Locale(languageNode.getNodeValue());
                        column.setHint(StringEscapeUtils.unescapeXml(name), locale);
                    }
                    if (item.getNodeName().equals("modelproperty") && item.hasChildNodes()) {
                        column.setPropertyId(item.getFirstChild().getNodeValue());
                    }
                    else if (item.getNodeName().equals("headerhtmlstyle") && item.hasChildNodes()) {
                        column.setHeaderHtmlStyle(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
                    }
                    else if (item.getNodeName().equals("cellhtmlstyle") && item.hasChildNodes()) {
                        column.setCellHtmlStyle(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
                    }
                    else if (item.getNodeName().equals("htmlvalue") && item.hasChildNodes()) {
                        column.setHtmlValue(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
                    }
                    else if (item.getNodeName().equals("selectable") && item.hasChildNodes()) {
                        column.setSelectable(false);
                        if ("true".equals(item.getFirstChild().getNodeValue().trim().toLowerCase())) column.setSelectable(true);
                    }
                    else if (item.getNodeName().equals("selectable") && item.hasChildNodes()) {
                        column.setSelectable(false);
                        if ("true".equals(item.getFirstChild().getNodeValue().trim().toLowerCase())) column.setSelectable(true);
                    }
                    else if (item.getNodeName().equals("sortable") && item.hasChildNodes()) {
                        column.setSortable(false);
                        if ("true".equals(item.getFirstChild().getNodeValue().trim().toLowerCase())) column.setSortable(true);
                    }
                    else if (item.getNodeName().equals("groupbyfunction") && item.hasChildNodes()) {
                        table.setGroupByFunctionCode(columnIndex, StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
                    }
                }
                table.addColumn(column);
            }
        }
    }

    protected void formatDisplayer(DataDisplayer displayer, PrintWriter out, int indent) throws Exception {
        TableDisplayer tableDisplayer = (TableDisplayer) displayer;
        DataSetTable table = tableDisplayer.getTable();

        printIndent(out, indent);
        out.print("<rowsperpage>"); out.print(table.getMaxRowsPerPage()); out.println("</rowsperpage>");

        if (table.getHeaderPosition() != null) {
            printIndent(out, indent);
            out.print("<headerposition>"); out.print(table.getHeaderPosition()); out.println("</headerposition>");
        }

        if (table.getHtmlStyle() != null) {
            printIndent(out, indent);
            out.print("<htmlstyle>"); out.print(StringEscapeUtils.escapeXml(table.getHtmlStyle())); out.println("</htmlstyle>");
        }

        if (table.getRowEvenStyle() != null) {
            printIndent(out, indent);
            out.print("<rowevenstyle>"); out.print(StringEscapeUtils.escapeXml(table.getRowEvenStyle())); out.println("</rowevenstyle>");
        }

        if (table.getRowOddStyle() != null) {
            printIndent(out, indent);
            out.print("<rowoddstyle>"); out.print(StringEscapeUtils.escapeXml(table.getRowOddStyle())); out.println("</rowoddstyle>");
        }

        if (table.getRowHoverStyle() != null) {
            printIndent(out, indent);
            out.print("<rowhoverstyle>"); out.print(StringEscapeUtils.escapeXml(table.getRowHoverStyle())); out.println("</rowhoverstyle>");
        }

        if (table.getHtmlClass() != null) {
            printIndent(out, indent);
            out.print("<htmlclass>"); out.print(StringEscapeUtils.escapeXml(table.getHtmlClass())); out.println("</htmlclass>");
        }

        if (table.getRowEventClass() != null) {
            printIndent(out, indent);
            out.print("<rowevenclass>"); out.print(StringEscapeUtils.escapeXml(table.getRowEventClass())); out.println("</rowevenclass>");
        }

        if (table.getRowOddClass() != null) {
            printIndent(out, indent);
            out.print("<rowoddclass>"); out.print(StringEscapeUtils.escapeXml(table.getRowOddClass())); out.println("</rowoddclass>");
        }

        if (table.getRowHoverClass() != null) {
            printIndent(out, indent);
            out.print("<rowhoverclass>"); out.print(StringEscapeUtils.escapeXml(table.getRowHoverClass())); out.println("</rowhoverclass>");
        }
        // Group by configuration (optional).
        DataProperty groupByProperty = table.getGroupByProperty();
        if (groupByProperty != null) {
            printIndent(out, indent++);
            out.println("<groupby>");
            DomainConfiguration domainConfig = new DomainConfiguration(groupByProperty);
            table.setGroupByConfiguration(domainConfig);
            formatDomain(domainConfig, out, indent);

            printIndent(out, indent);
            out.print("<showtotals>"); out.print(table.showGroupByTotals()); out.println("</showtotals>");

            printIndent(out, indent);
            out.print("<totalshtmlstyle>"); out.print(table.getGroupByTotalsHtmlStyle()); out.println("</totalshtmlstyle>");

            printIndent(out, --indent);
            out.println("</groupby>");
        }

        // Columns to display.
        for (int columnIndex=0; columnIndex<table.getColumnCount(); columnIndex++) {
            DataProperty columnProperty = table.getOriginalDataProperty(columnIndex);
            if (columnProperty == null) continue;
            
            TableColumn column = table.getColumn(columnIndex);
            printIndent(out, indent++);
            out.println("<column>");

            printIndent(out, indent);
            out.print("<modelproperty>"); out.print(column.getPropertyId()); out.println("</modelproperty>");

            printIndent(out, indent);
            out.print("<viewindex>"); out.print(columnIndex); out.println("</viewindex>");

            Map columnName = column.getNameI18nMap();
            Iterator it = columnName.keySet().iterator();
            while (it.hasNext()) {
                Locale l = (Locale) it.next();
                printIndent(out, indent);
                out.print("<name language");
                out.print("=\"" + StringEscapeUtils.escapeXml(l.toString()) + "\">");
                out.print(StringEscapeUtils.escapeXml((String) columnName.get(l)));
                out.println("</name>");
            }

            Map columnHint = column.getHintI18nMap();
            it = columnHint.keySet().iterator();
            while (it.hasNext()) {
                Locale l = (Locale) it.next();
                printIndent(out, indent);
                out.print("<hint language");
                out.print("=\"" + StringEscapeUtils.escapeXml(l.toString()) + "\">");
                out.print(StringEscapeUtils.escapeXml((String) columnHint.get(l)));
                out.println("</hint>");
            }

            if (column.getHeaderHtmlStyle() != null) {
                printIndent(out, indent);
                out.print("<headerhtmlstyle>"); out.print(StringEscapeUtils.escapeXml(column.getHeaderHtmlStyle())); out.println("</headerhtmlstyle>");
            }

            if (column.getCellHtmlStyle() != null) {
                printIndent(out, indent);
                out.print("<cellhtmlstyle>"); out.print(StringEscapeUtils.escapeXml(column.getCellHtmlStyle())); out.println("</cellhtmlstyle>");
            }

            if (column.getHtmlValue() != null) {
                printIndent(out, indent);
                out.print("<htmlvalue>"); out.print(StringEscapeUtils.escapeXml(column.getHtmlValue())); out.println("</htmlvalue>");
            }

            String selectable = "false";
            if (column.isSelectable()) selectable = "true";
            printIndent(out, indent);
            out.print("<selectable>"); out.print(selectable); out.println("</selectable>");

            String sortable = "false";
            if (column.isSortable()) sortable = "true";
            printIndent(out, indent);
            out.print("<sortable>"); out.print(sortable); out.println("</sortable>");

            // Group by configuration (optional).
            if (groupByProperty != null) {
                String functionCode = table.getGroupByFunctionCode(columnIndex);
                if (functionCode != null && !groupByProperty.equals(columnProperty)) {
                    printIndent(out, indent);
                    out.print("<groupbyfunction>");out.print(functionCode);out.println("</groupbyfunction>");
                }
            }
            printIndent(out, --indent);
            out.println("</column>");
        }
    }
}