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
package org.jboss.dashboard.ui.components.table;

import org.jboss.dashboard.displayer.table.Table;
import org.jboss.dashboard.displayer.table.TableColumn;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.LocaleManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * A table component formatter.
 */
public class TableFormatter extends Formatter {

    protected TableHandler tableHandler;
    protected static final String ICON_ORDER_UP = "order_up.gif";
    protected static final String ICON_ORDER_DOWN = "order_down.gif";
    protected static final String ICON_ORDER_UNKNOWN = "order.gif";

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        tableHandler = (TableHandler) getParameter("tableHandler");
        Table table = tableHandler.getTable();

        if (table == null) {
            renderFragment("notable");
            return;
        }

        // Table start.
        setTableAttributes(tableHandler);
        renderFragment("tablestart");

        setTableAttributes(tableHandler);
        renderFragment("tablestarthead");

        setTableAttributes(tableHandler);
        renderFragment("tablestartcontents");

        setTableAttributes(tableHandler);
        renderFragment("tablestartcompleted");

        renderFragment("outputtableend");

        renderFragment("tableoutput");

        // Table header at top.
        if (table.getHeaderPosition().equals("top")) {
            renderTableHeader(tableHandler);
        }

        // Table empty
        if (table.getRowCount() == 0) {
            renderFragment("tableempty");
        }
        // Body start.
        else {
            setTableAttributes(tableHandler);
            renderFragment("bodystart");

            // Current page rows.
            int currentPage = table.getCurrentPage() - 1;
            int pageSize = table.getMaxRowsPerPage();
            int currentPageBegin = currentPage * pageSize;
            for (int i=currentPageBegin; i < currentPageBegin + pageSize && i < table.getRowCount(); i++) {

                // Row start.
                setTableAttributes(tableHandler);
                setAttribute("rowindex", i);
                renderFragment("rowstart");

                // Row value at column.
                for (int j = 0; j < table.getColumnCount(); j++) {
                    TableColumn column = table.getColumn(j);
                    setTableAttributes(tableHandler);
                    setColumnAttributes(column, j);
                    setAttribute("rowindex", i);
                    setAttribute("rowvalue", formatCellValue(table, i, j));
                    setAttribute("columnhtmlvalue", formatHtmlCellValue(table, column, i,j));
                    renderFragment("rowcolumn");
                }

                // Row end.
                setTableAttributes(tableHandler);
                setAttribute("rowindex", i);
                renderFragment("rowend");
            }
            // Body end.
            setTableAttributes(tableHandler);
            renderFragment("bodyend");

            // Table header at bottom.
            if (table.getHeaderPosition().equals("bottom")) {
                renderTableHeader(tableHandler);
            }
        }
        // Table end.
        setTableAttributes(tableHandler);
        renderFragment("tableend");
    }

    protected void renderTableHeader(TableHandler tableHandler) {
        // Table header.
        Table table = tableHandler.getTable();
        setTableAttributes(tableHandler);
        renderFragment("headerstart");

        // Header columns.
        for (int j = 0; j < table.getColumnCount(); j++) {
            TableColumn column = table.getColumn(j);
            setTableAttributes(tableHandler);
            setColumnAttributes(column, j);
            Integer selectedIdx = tableHandler.getSelectedColumnIndex();
            if (selectedIdx != null && selectedIdx.intValue() == j) renderFragment("headerselected");
            else renderFragment("headercolumn");
        }

        // Header end.
        setTableAttributes(tableHandler);
        renderFragment("headerend");
    }

    protected void setTableAttributes(TableHandler tableHandler) {
        Table table = tableHandler.getTable();
        setAttribute("currentpage", table.getCurrentPage());
        setAttribute("rowcount", table.getRowCount());
        setAttribute("maxrowspage", table.getMaxRowsPerPage());
        setAttribute("headerposition", table.getHeaderPosition());
        setAttribute("htmlstyleedit", StringUtils.defaultString(StringEscapeUtils.escapeHtml(table.getHtmlStyle())));
        setAttribute("rowevenstyleedit",StringUtils.defaultString(StringEscapeUtils.escapeHtml(table.getRowEvenStyle())));
        setAttribute("rowoddstyleedit",StringUtils.defaultString(StringEscapeUtils.escapeHtml(table.getRowOddStyle())));
        setAttribute("rowhoverstyleedit",StringUtils.defaultString(StringEscapeUtils.escapeHtml(table.getRowHoverStyle())));
        setAttribute("htmlclass",StringUtils.defaultString(StringEscapeUtils.escapeHtml(table.getHtmlClass())));
        setAttribute("rowevenclass",StringUtils.defaultString(StringEscapeUtils.escapeHtml(table.getRowEventClass())));
        setAttribute("rowoddclass",StringUtils.defaultString(StringEscapeUtils.escapeHtml(table.getRowOddClass())));
        setAttribute("rowhoverclass",StringUtils.defaultString(StringEscapeUtils.escapeHtml(table.getRowHoverClass())));
        setAttribute("htmlstyleview", table.getHtmlStyle());
    }

    protected void setColumnAttributes(TableColumn column, int columnIndex) {
        Locale locale = LocaleManager.currentLocale();
        setAttribute("column", column);
        setAttribute("columnindex", columnIndex);
        setAttribute("columnmodel", column.getPropertyId());
        setAttribute("columnname", column.getName(locale));
        setAttribute("columnhint", column.getHint(locale));
        setAttribute("columnselectable", column.isSelectable());
        setAttribute("columnsortable", isColumnSortable(column));
        String icon = getSortIcon(column, columnIndex);
        setAttribute("iconId", icon);
        setAttribute("iconTextId",getSortKeyText(icon));

        String headerHTML = column.getHeaderHtmlStyle();
        String cellsHTML = column.getCellHtmlStyle();
        if (headerHTML == null) headerHTML = cellsHTML;
        if (headerHTML != null) {
            setAttribute("columnheaderhtmlstyle", StringUtils.defaultString(headerHTML));
            setAttribute("columnheaderstyleedit", StringUtils.defaultString(StringEscapeUtils.escapeHtml(headerHTML)));
        }
        if (cellsHTML != null) {
            setAttribute("columncellhtmlstyle", StringUtils.defaultString(cellsHTML));
            setAttribute("columncellstyleedit", StringUtils.defaultString(StringEscapeUtils.escapeHtml(cellsHTML)));
        }
        String htmlValue = column.getHtmlValue();
        if (htmlValue != null) {
            setAttribute("columnhtmlvalue", StringUtils.defaultString(htmlValue));
            setAttribute("columnhtmlvalueedit", StringUtils.defaultString(StringEscapeUtils.escapeHtml(htmlValue)));
        }
    }

    protected String formatCellValue(Table table, int row, int column) {
        Object value = table.getValueAt(row, column);
        if (value == null) return "";
        return StringEscapeUtils.escapeHtml(value.toString());
    }

    protected String formatHtmlCellValue(Table table, TableColumn tableColumn, int row, int column) {
        if (StringUtils.isBlank(tableColumn.getHtmlValue())) return "";
        String result = tableColumn.getHtmlValue();
        return StringUtils.replace(result, TableColumn.DEFAULT_HTMLVALUE, formatCellValue(table,row,column));
    }

    protected String getSortIcon(TableColumn column, int columnIndex) {
        return ICON_ORDER_UNKNOWN;
    }

    protected String getSortKeyText(String iconId) {
        if (iconId == null) return null;
        if (iconId.equals(ICON_ORDER_UNKNOWN)) return "table.sort";
        if (iconId.equals(ICON_ORDER_DOWN)) return "table.sortDesc";
        if (iconId.equals(ICON_ORDER_UP)) return "table.sortAsc";
        return null;
    }

    protected boolean isColumnSortable(TableColumn column) {
        return column.isSortable();
    }
}