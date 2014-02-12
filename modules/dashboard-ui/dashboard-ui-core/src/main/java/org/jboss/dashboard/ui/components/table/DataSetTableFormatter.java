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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.dashboard.displayer.table.Table;
import org.jboss.dashboard.displayer.table.TableColumn;
import org.jboss.dashboard.provider.DataFormatterRegistry;
import org.jboss.dashboard.provider.DataPropertyFormatter;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.commons.comparator.ComparatorByCriteria;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.displayer.table.DataSetTable;
import org.jboss.dashboard.displayer.table.DataSetTableModel;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Table formatter which formats cell values using the data set properties.
 */
public class DataSetTableFormatter extends TableFormatter {

    protected String formatCellValue(Table table, int row, int column) {
        DataSetTable dataSetTable = (DataSetTable) table;
        DataProperty property = dataSetTable.getDataProperty(column);
        if (property == null) return "";
        
        DataPropertyFormatter formatter = DataFormatterRegistry.lookup().getPropertyFormatter(property.getPropertyId());
        return StringEscapeUtils.escapeHtml(formatter.formatValue(property, table.getValueAt(row, column), LocaleManager.currentLocale()));
    }

    protected void renderFragment(String fragment) {
        super.renderFragment(fragment);

        DataSetTable table = (DataSetTable) tableHandler.getTable();
        if ("bodyend".equals(fragment) && table.showGroupByTotals()) includePage("/components/bam/displayer/table/table_groupby_totals.jsp");
        else if ("tablestartcontents".equals(fragment) && tableHandler.isEditMode()) includePage("/components/bam/displayer/table/table_groupby_selector.jsp");
    }


    protected String getSortIcon(TableColumn column, int columnIndex) {
        // Sorting in case of a Data Set Table Model.
        DataSetTable dst = (DataSetTable) tableHandler.getTable();
        if (isColumnSortable(column)) {
            DataSetTableModel dsModel = (DataSetTableModel) dst.getModel();
            String modelIndex = Integer.toString(dsModel.getColumnPosition(column.getPropertyId()));
            ComparatorByCriteria comparator = tableHandler.getTableComparator();
            int order = 0;
            if (comparator.existCriteria(modelIndex)) order = comparator.getSortCriteriaOrdering(modelIndex);
            switch(order) {
                case 1: return ICON_ORDER_DOWN;
                case -1: return ICON_ORDER_UP;
            }
        }
        return ICON_ORDER_UNKNOWN;
    }
}
