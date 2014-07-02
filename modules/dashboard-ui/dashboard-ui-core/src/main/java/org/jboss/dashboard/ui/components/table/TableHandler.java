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

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.displayer.table.*;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.commons.filter.FilterByCriteria;
import org.jboss.dashboard.ui.components.UIBeanHandler;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.commons.comparator.ComparatorByCriteria;
import org.jboss.dashboard.ui.controller.responses.SendStreamResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * The table component handler.
 */
@PanelScoped
@Named("table_handler")
public class TableHandler extends UIBeanHandler {

    public static final String EXPORT_FORMAT = "dataExportFormat";

    @Inject
    private transient Logger log;

    protected TableFormatter tableFormatter;
    protected Table table;
    protected ComparatorByCriteria tableComparator;
    protected boolean editMode;
    protected String viewModeJsp;
    protected String editModeJsp;
    protected Integer selectedColumnIndex;
    protected boolean structuralChangesAllowed;

    // Constructor of the class
    public TableHandler() {
        table = new Table();
        selectedColumnIndex = null;
        editMode = true;
        viewModeJsp = "/components/table/view.jsp";
        editModeJsp = "/components/table/edit.jsp";
        tableFormatter = null;
        structuralChangesAllowed = true;
        tableComparator = null;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public ComparatorByCriteria getTableComparator() {
        return tableComparator;
    }

    public TableFormatter getTableFormatter() {
        return tableFormatter;
    }

    public void setTableFormatter(TableFormatter tableFormatter) {
        this.tableFormatter = tableFormatter;
    }

    public TableColumn getSelectedColumn() {
        if (selectedColumnIndex == null) return null;
        return table.getColumn(selectedColumnIndex.intValue());
    }

    public Integer getSelectedColumnIndex() {
        return selectedColumnIndex;
    }

    public void setSelectedColumnIndex(Integer selectedColumnIndex) {
        this.selectedColumnIndex = selectedColumnIndex;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isStructuralChangesAllowed() {
        return structuralChangesAllowed;
    }

    public void setStructuralChangesAllowed(boolean structuralChangesAllowed) {
        this.structuralChangesAllowed = structuralChangesAllowed;
    }

    // UIBeanHandler interface

    public String getBeanJSP() {
        if (isEditMode()) return editModeJsp;
        return viewModeJsp;
    }

    public CommandResponse actionExecAction(CommandRequest request) throws Exception {
        String action = request.getRequestObject().getParameter("tableaction");
        if ("saveTable".equals(action)) actionSaveTable(request);
        else if ("newColumn".equals(action)) actionNewColumn(request);
        else if ("moveColumn".equals(action)) actionMoveColumn(request);
        else if ("removeColumn".equals(action)) actionRemoveColumn(request);
        else if ("selectColumn".equals(action)) actionSelectColumn(request);
        else if ("deselectColumn".equals(action)) actionDeselectColumn(request);
        else if ("saveColumn".equals(action)) actionSaveColumn(request);
        else if ("nextPage".equals(action)) actionNextPage(request);
        else if ("previousPage".equals(action)) actionPreviousPage(request);
        else if ("firstPage".equals(action)) actionFirstPage(request);
        else if ("lastPage".equals(action)) actionLastPage(request);
        else if ("gotoPage".equals(action)) actionGotoPage(request);
        else if ("selectCellValue".equals(action)) return actionSelectCellValue(request);
        else if ("sortByColumn".equals(action)) actionSortByColumn(request);
        return null;
    }

    public void actionSaveTable(CommandRequest request) throws Exception {
        // Capture the changes in the table properties
        int maxRows = 0;
        try {
            maxRows = Integer.parseInt(request.getRequestObject().getParameter("maxrowspage"));
            table.setMaxRowsPerPage(maxRows);
        } catch (NumberFormatException e) {
            log.warn("Cannot parse max rows per page value for table as a number.");
        }
        String headerPos = request.getRequestObject().getParameter("headerposition");
        String htmlStyle = request.getRequestObject().getParameter("htmlstyle");
        String rowHoverStyle = request.getRequestObject().getParameter("rowhoverstyle");
        String rowOddStyle = request.getRequestObject().getParameter("rowoddstyle");
        String rowEvenStyle = request.getRequestObject().getParameter("rowevenstyle");
        String htmlClass = request.getRequestObject().getParameter("htmlclass");
        String rowEvenClass = request.getRequestObject().getParameter("rowevenclass");
        String rowOddClass = request.getRequestObject().getParameter("rowoddclass");
        String rowHoverClass = request.getRequestObject().getParameter("rowhoverclass");

        if (StringUtils.isNotBlank(headerPos)) table.setHeaderPosition(headerPos);
        if (StringUtils.isNotBlank(htmlStyle)) table.setHtmlStyle(htmlStyle);
        if (StringUtils.isNotBlank(rowEvenStyle)) table.setRowEvenStyle(rowEvenStyle);
        if (StringUtils.isNotBlank(rowOddStyle)) table.setRowOddStyle(rowOddStyle);
        if (StringUtils.isNotBlank(rowHoverStyle)) table.setRowHoverStyle(rowHoverStyle);
        if (StringUtils.isNotBlank(htmlClass)) table.setHtmlClass(htmlClass);
        if (StringUtils.isNotBlank(rowEvenClass)) table.setRowEventClass(rowEvenClass);
        if (StringUtils.isNotBlank(rowHoverClass)) table.setRowHoverClass(rowHoverClass);
        if (StringUtils.isNotBlank(rowOddClass)) table.setRowOddClass(rowOddClass);
        table.setCurrentPage(1);

        // Capture the changes in the selected column (if any).
        TableColumn selectedColumn = getSelectedColumn();
        if (selectedColumn != null) saveColumn(selectedColumn, request);
    }

    public void actionNewColumn(CommandRequest request) throws Exception {
        int columnIndex = Integer.parseInt(request.getRequestObject().getParameter("columnindex"));
        int position = Integer.parseInt(request.getRequestObject().getParameter("position"));

        // Create a new column.
        TableColumn newColumn = table.createColumn();

        newColumn.setPropertyId(table.getColumn(columnIndex).getPropertyId());

        // Add the new column to the table in the specified position.
        int newColumnIndex = table.getColumnCount();
        int newPosition = columnIndex + position;
        if (newPosition < 0) newPosition = 0;
        table.addColumn(newColumn);
        table.moveColumn(newColumnIndex, newPosition);
        selectedColumnIndex =  new Integer(newPosition);
    }

    public void actionMoveColumn(CommandRequest request) throws Exception {
        int columnIndex = Integer.parseInt(request.getRequestObject().getParameter("columnindex"));
        int position = Integer.parseInt(request.getRequestObject().getParameter("position"));
        table.moveColumn(columnIndex, columnIndex + position);
        actionDeselectColumn(request);
    }

    public void actionSelectColumn(CommandRequest request) throws Exception {
        int columnIndex = Integer.parseInt(request.getRequestObject().getParameter("columnindex"));
        if (table.getColumn(columnIndex) != null) selectedColumnIndex = new Integer(columnIndex);
    }

    public void actionDeselectColumn(CommandRequest request) throws Exception {
        selectedColumnIndex = null;
    }

    public void actionRemoveColumn(CommandRequest request) throws Exception {
        int columnIndex = Integer.parseInt(request.getRequestObject().getParameter("columnindex"));
        table.removeColumn(columnIndex);
        actionDeselectColumn(request);
    }

    public void actionSaveColumn(CommandRequest request) throws Exception {
        int index = Integer.parseInt(request.getRequestObject().getParameter("columnindex"));
        saveColumn(table.getColumn(index), request);
        actionDeselectColumn(request);
    }

    protected void saveColumn(TableColumn column, CommandRequest request) throws Exception {
        if (column != null) {
            String name = request.getRequestObject().getParameter("columnname");
            String hint = request.getRequestObject().getParameter("columnhint");
            String headerHTML = request.getRequestObject().getParameter("columnheaderhtmlstyle");
            String cellHTML = request.getRequestObject().getParameter("columncellhtmlstyle");
            String htmlValue = request.getRequestObject().getParameter("htmlvalue");
            if (htmlValue == null || htmlValue.indexOf(TableColumn.DEFAULT_HTMLVALUE) == -1) htmlValue = TableColumn.DEFAULT_HTMLVALUE;
            boolean selectable = Boolean.valueOf(request.getRequestObject().getParameter("columnselectable")).booleanValue();
            boolean sortable = Boolean.valueOf(request.getRequestObject().getParameter("columnsortable")).booleanValue();
            Locale locale = LocaleManager.currentLocale();
            column.setName(name, locale);
            column.setHint(hint, locale);
            column.setHeaderHtmlStyle(headerHTML);
            column.setCellHtmlStyle(cellHTML);
            column.setHtmlValue(htmlValue);
            column.setSelectable(selectable);
            column.setSortable(sortable);

            if (isStructuralChangesAllowed()) {
                String model = request.getRequestObject().getParameter("columnmodel");
                column.setPropertyId(model);
            }
            selectedColumnIndex = null;
        }
    }

    public void actionNextPage(CommandRequest request) throws Exception {
        table.setCurrentPage(table.getCurrentPage() + 1);
    }

    public void actionPreviousPage(CommandRequest request) throws Exception {
        table.setCurrentPage(table.getCurrentPage() - 1);
    }

    public void actionFirstPage(CommandRequest request) throws Exception {
        table.setCurrentPage(1);
    }

    public void actionLastPage(CommandRequest request) throws Exception {
        table.setCurrentPage(table.getNumberOfPages());
    }

    public void actionGotoPage(CommandRequest request) throws Exception {
        try {
            table.setCurrentPage(Integer.parseInt(request.getRequestObject().getParameter("pagenumber")));
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    public void actionSortByColumn(CommandRequest request) throws Exception {
        int tableColumnIdx = Integer.parseInt(request.getRequestObject().getParameter("columnindex"));
        TableColumn tableColumn = getTable().getColumn(tableColumnIdx);
        AbstractTableModel model = (AbstractTableModel) getTable().getModel();

        if (tableComparator != null) {
            // Get the current order.
            String modelColumnIdx = Integer.toString(model.getColumnPosition(tableColumn.getPropertyId()));
            int currentOrdering = ComparatorByCriteria.ORDER_ASCENDING;
            if (tableComparator.existCriteria(modelColumnIdx)) currentOrdering = tableComparator.getSortCriteriaOrdering(modelColumnIdx);

            // Reverse that order
            if (currentOrdering == ComparatorByCriteria.ORDER_UNSPECIFIED) currentOrdering = ComparatorByCriteria.ORDER_ASCENDING;
            else if (currentOrdering == ComparatorByCriteria.ORDER_ASCENDING) currentOrdering = ComparatorByCriteria.ORDER_DESCENDING;
            else currentOrdering = ComparatorByCriteria.ORDER_ASCENDING;

            // Sort
            tableComparator.removeAllSortCriteria();
            tableComparator.addSortCriteria(modelColumnIdx, currentOrdering);
            model.sort(tableComparator);
        }
    }

    public CommandResponse actionSelectCellValue(CommandRequest request) throws Exception {
        table.setCurrentPage(1);

        int rowIndex = Integer.parseInt(request.getRequestObject().getParameter("rowindex"));
        int columnIndex = Integer.parseInt(request.getRequestObject().getParameter("columnindex"));

        DataSetTable dataSetTable = (DataSetTable) getTable();
        DataProperty selectedProperty = dataSetTable.getDataProperty(columnIndex);
        Dashboard dashboard = DashboardHandler.lookup().getCurrentDashboard();
        Object selectedValue = dataSetTable.getValueAt(rowIndex, columnIndex);
        if (selectedValue instanceof Interval) {
            if (dashboard.filter(selectedProperty.getPropertyId(), (Interval) selectedValue, FilterByCriteria.ALLOW_ANY)) {
                // If drill-down then force the whole screen to be refreshed.
                return new ShowCurrentScreenResponse();
            }
        } else {
            Collection values = new ArrayList();
            values.add(selectedValue);
            if (dashboard.filter(selectedProperty.getPropertyId(), null, false, null, false, values, FilterByCriteria.ALLOW_ANY)) {
                // If drill-down then force the whole screen to be refreshed.
                return new ShowCurrentScreenResponse();
            }
        }
        return null;
    }

    public CommandResponse actionExportData(String format) throws Exception {
        if (ExportTool.FORMAT_EXCEL.equalsIgnoreCase(format))
            return new SendStreamResponse(new ExportTool().exportExcel(getTable().getModel()), "inline;filename=data.xlsx;");
        else if (ExportTool.FORMAT_CSV.equalsIgnoreCase(format))
            return new SendStreamResponse(new ExportTool().exportCSV(getTable().getModel()), "inline;filename=data.csv;");
        else {
            throw new IllegalArgumentException("Null or unsupported export format!");
        }
    }
}
