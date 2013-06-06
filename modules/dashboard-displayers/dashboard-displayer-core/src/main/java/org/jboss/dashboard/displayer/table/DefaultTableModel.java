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

import org.jboss.dashboard.commons.comparator.ComparatorByCriteria;

/**
 */
public class DefaultTableModel extends AbstractTableModel {

    protected javax.swing.table.DefaultTableModel _defaultTableModel;

    public DefaultTableModel() {
        super();
        _defaultTableModel = new javax.swing.table.DefaultTableModel();
    }

    public void sort(ComparatorByCriteria comparator) {
        // Not supported
    }

    public int getColumnCount() {
        return _defaultTableModel.getColumnCount();
    }

    public int getRowCount() {
        return _defaultTableModel.getRowCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return _defaultTableModel.getValueAt(rowIndex, columnIndex);
    }

    public int getColumnPosition(String columnName) {
        for (int i = 0; i < getColumnCount(); i ++) {
            if (_defaultTableModel.getColumnName(i).equals(columnName)) return i;
        }
        return super.getColumnPosition(columnName);
    }

    public String getColumnName(int index) {
        return _defaultTableModel.getColumnName(index);
    }
}
