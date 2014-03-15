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

import org.jboss.dashboard.LocaleManager;

import java.util.*;

/**
 * Table component model.
 */
public class Table {

    protected List<TableColumn> tableColumns;
    protected TableModel model;
    protected int maxRowsPerPage;
    protected int currentPage;
    protected String headerPosition;
    protected String htmlStyle;
    protected String htmlClass;
    protected String rowEvenStyle;
    protected String rowEventClass;
    protected String rowOddStyle;
    protected String rowOddClass;
    protected String rowHoverStyle;
    protected String rowHoverClass;

    public Table() {
        super();
        maxRowsPerPage = 10;
        currentPage = 1;
        headerPosition = "top";
        htmlStyle = null;
        htmlClass = null;
        rowEvenStyle = null;
        rowOddStyle = null;
        rowEventClass = null;
        rowOddClass = null;
        rowHoverClass = null;
        rowHoverStyle = null;
        tableColumns = new ArrayList<TableColumn>();
    }

    public String getHeaderPosition() {
        return headerPosition;
    }

    public void setHeaderPosition(String headerPosition) {
        this.headerPosition = headerPosition;
    }


    public String getHtmlClass() {
        return htmlClass;
    }

    public void setHtmlClass(String htmlClass) {
        this.htmlClass = htmlClass;
    }

    public String getRowEventClass() {
        return rowEventClass;
    }

    public void setRowEventClass(String rowEventClass) {
        this.rowEventClass = rowEventClass;
    }

    public String getRowHoverClass() {
        return rowHoverClass;
    }

    public void setRowHoverClass(String rowHoverClass) {
        this.rowHoverClass = rowHoverClass;
    }

    public String getRowOddClass() {
        return rowOddClass;
    }

    public void setRowOddClass(String rowOddClass) {
        this.rowOddClass = rowOddClass;
    }

    public String getRowEvenStyle() {
        return rowEvenStyle;
    }

    public void setRowEvenStyle(String rowEvenStyle) {
        this.rowEvenStyle = rowEvenStyle;
    }

    public String getRowHoverStyle() {
        return rowHoverStyle;
    }

    public void setRowHoverStyle(String rowHoverStyle) {
        this.rowHoverStyle = rowHoverStyle;
    }

    public String getRowOddStyle() {
        return rowOddStyle;
    }

    public void setRowOddStyle(String rowOddStyle) {
        this.rowOddStyle = rowOddStyle;
    }

    public String getHtmlStyle() {
        return htmlStyle;
    }

    public void setHtmlStyle(String htmlStyle) {
        this.htmlStyle = htmlStyle;
    }

    public int getMaxRowsPerPage() {
        return maxRowsPerPage;
    }

    public void setMaxRowsPerPage(int maxRowsPerPage) {
        this.maxRowsPerPage = maxRowsPerPage;
    }

    public int getNumberOfPages() {
        return ((getRowCount() - 1) / getMaxRowsPerPage() + 1);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int newPage) {
        if (newPage > 0 && newPage <= getNumberOfPages()) this.currentPage = newPage;
    }

    public TableColumn createColumn() {
        TableColumn column = new TableColumn();
        column.setTable(this);
        return column;
    }

    public void addColumn(TableColumn column) {
        if (column == null && tableColumns.contains(column)) return;
        tableColumns.add(column);
    }

    public TableColumn getColumn(int columnIndex) {
        return tableColumns.get(columnIndex);
    }

    public void removeColumn(int columnIndex) {
        tableColumns.remove(columnIndex);
    }

    public String getColumnName(int columnIndex) {
        TableColumn column = getColumn(columnIndex);
        if (column != null) return column.getName(LocaleManager.currentLocale());
        return null;
    }

    public int getRowCount() {
        return getModel().getRowCount();
    }

    public int getColumnCount() {
        return tableColumns.size();
    }

    public TableModel getModel() {
        return model;
    }

    public void setModel(TableModel model) {
        this.model = model;
    }

    public void moveColumn(int from, int to) {
        if ((from < 0) || (from >= getColumnCount()) || (to < 0) || (to >= getColumnCount()))
            throw new IllegalArgumentException("Impossible to move colum from " + from + " to " + to + ": index out of range.");
        TableColumn column = tableColumns.remove(from);
        tableColumns.add(to, column);
    }

    public Object getValueAt(int row, int column) {
        if ((row < 0) || (row >= getRowCount()) || (column < 0) || (column >= getColumnCount())) {
            throw new IllegalArgumentException("Impossible to get value from position " + row + ", " + column + ": index out of range.");
        }
        TableColumn tableColumn = getColumn(column);
        String tablePropertyId = tableColumn.getPropertyId();
        if (column < model.getColumnCount()) {
            String modelPropertyId = model.getColumnId(column);
            if (tablePropertyId.equals(modelPropertyId)) {
                return model.getValueAt(row, column);
            }
        }
        return model.getValue(row, tablePropertyId);
    }
}
