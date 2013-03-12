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
import org.jboss.dashboard.commons.comparator.ComparatorByCriteria;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.dataset.DataSetComparator;

/**
 * Table model which feeds from a data set. 
 */
public class DataSetTableModel extends AbstractTableModel {

    /**
     * The data set.
     */
    protected DataSet dataSet;

    public DataSetTableModel() {
        this.dataSet = null;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    // TableModel interface

    public String getColumnName(int columnIndex) {
        if (getColumnCount() == 0) return null;
        if (columnIndex >= getColumnCount()) return null;
        return dataSet.getProperties()[columnIndex].getName(LocaleManager.currentLocale());
    }

    public Class getColumnClass(int columnIndex) {
        if (getColumnCount() == 0) return null;
        if (columnIndex >= getColumnCount()) return null;
        return dataSet.getProperties()[columnIndex].getDomain().getValuesClass();
    }

    public int getColumnCount() {
        if (dataSet == null) return 0;
        return dataSet.getProperties().length;
    }

    public int getRowCount() {
        if (getColumnCount() == 0) return 0;
        return dataSet.getRowCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return dataSet.getValueAt(rowIndex, columnIndex);
    }

    public ComparatorByCriteria getComparator() {
        if (comparator == null) {
            this.comparator = new DataSetComparator();
        }
        return super.getComparator();
    }

    public void sort(ComparatorByCriteria comparator) {
        super.sort(comparator);
        dataSet = dataSet.sort(comparator);
    }

    @Override
    public int getColumnPosition(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (dataSet.getProperties()[i].getPropertyId().equals(columnName)) return i;
        }
        return super.getColumnPosition(columnName);
    }

    @Override
    public String getColumnId(int index) {
        return dataSet.getProperties()[index].getPropertyId();
    }
}
