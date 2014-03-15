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

import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.domain.DomainConfiguration;
import org.jboss.dashboard.function.CountFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The table component adapted for the display of data sets.
 */
public class DataSetTable extends Table {

    /** Logger */
    private transient static Logger log = LoggerFactory.getLogger(DataSetTable.class);

    protected transient DataProvider dataProvider;
    protected transient DataSet dataSet;
    protected transient int dataSetRowCount;
    protected transient DataProperty groupByProperty;
    protected transient DomainConfiguration groupByConfig;

    protected Map<Integer, String> groupByFunctions;
    protected boolean groupByShowTotals;
    protected String groupByTotalsHtmlStyle;

    public DataSetTable() {
        super();
        maxRowsPerPage = 10;
        htmlStyle = null;
        dataProvider = null;
        dataSet = null;
        dataSetRowCount = 0;
        groupByProperty = null;
        groupByFunctions = new HashMap<Integer, String>();
        groupByConfig = null;
        groupByShowTotals = false;
        groupByTotalsHtmlStyle = null;
    }

    public TableColumn createColumn() {
        TableColumn column = new TableColumn();
        column.setTable(this);
        column.setHeaderHtmlStyle("text-align:center; width:100px; overflow:hidden; background-color:#C6D8EB; height:20px; color:#465F7D; font-weight:Bold;  white-space:nowrap;");
        column.setCellHtmlStyle("width:100px; height:20px;  white-space:nowrap;");
        column.setHtmlValue("{value}");
        column.setSelectable(false);
        column.setSortable(true);
        return column;
    }

    /**
     * Be always aware of changes in the dataset every time the table model is requested in order to ensure
     * that the table feeds from the most up to date data.
     */
    public TableModel getModel() {
        catchDataSetChanges();
        return super.getModel();
    }

    public void setDataProvider(DataProvider provider) {
        boolean providerNotInit = (dataProvider == null && provider != null);
        boolean providerChanged = (dataProvider != null && !dataProvider.equals(provider));
        dataProvider = provider;

        // Init the provider and apply the group by configuration if required.
        if (providerNotInit) {
            DataSetTableModel dataSetModel = (DataSetTableModel) super.getModel();
            dataSetModel.setDataSet(getOriginalDataSet());
            if (getColumnCount() == 0) initFromDataSet();
            DataProperty groupByProp = getGroupByProperty();
            if (groupByProp != null) switchGroupByOn(groupByProp);
        }
        // Clear the current table configuration if the data provider is changed.
        if (providerChanged) {
            initFromDataSet();
            setGroupByProperty(null);
        }
    }

    protected void initFromDataSet() {
        for (int i=getColumnCount()-1; i>=0; i--) removeColumn(i);
        DataProperty[] props = getDataSet().getProperties();
        for (int i = 0; i < props.length && i < 10; i++) {
            DataProperty prop = props[i];
            TableColumn column = createColumn();
            column.setPropertyId(prop.getPropertyId());
            column.setNameI18nMap(new HashMap<Locale,String>(prop.getNameI18nMap()));
            column.setHintI18nMap(new HashMap<Locale,String>(prop.getNameI18nMap()));
            addColumn(column);
            column.setSortable(true);
            column.setSelectable(false);
        }
    }

    public DataSet getDataSet() {
        DataSetTableModel model = (DataSetTableModel) getModel();
        return model.getDataSet();
    }

    public DataProperty getDataProperty(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= getColumnCount()) return null;

        TableColumn column = getColumn(columnIndex);
        DataProperty[] properties = getDataSet().getProperties();

        for (DataProperty property : properties) {
            if (property.getPropertyId().equals(column.getPropertyId())) return property;
        }
        return null;
    }

    public DataSet getOriginalDataSet() {
        catchDataSetChanges();
        return dataSet;
    }

    public DataProperty getOriginalDataProperty(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= getColumnCount()) return null;

        // If group by is deactivated then get the property from the current (original) dataset.
        DataProperty currentProp = getDataProperty(columnIndex);
        if (groupByProperty == null) return currentProp;

        // If not then get the property from the grouped data set and then get the corresponding property from the original one.
        DataProperty[] properties = getOriginalDataSet().getProperties();
        for (DataProperty property : properties) {
            if (property.equals(currentProp)) return property;
        }
        return null;
    }

    public DataProperty getGroupByProperty() {
        DataSet originalDataSet = getOriginalDataSet();
        if (originalDataSet == null) return null;
        
        // If the group by property is null or it disappears from the data set then reload it from persistence.
        if (groupByProperty == null || originalDataSet.getPropertyById(groupByProperty.getPropertyId()) == null)  {
            if (groupByConfig != null) {
                groupByProperty = originalDataSet.getPropertyById(groupByConfig.getPropertyId());
                if (groupByProperty != null) {
                    groupByProperty = groupByProperty.cloneProperty();
                    groupByConfig.apply(groupByProperty);
                }
            }
        }
        return groupByProperty;
    }

    public void setGroupByProperty(DataProperty property) {
        boolean changed = false;
        if (groupByProperty == null) changed = (property != null);
        else changed = !groupByProperty.equals(property);

        if (changed) {
            groupByFunctions.clear();
            groupByShowTotals = (property != null);
            groupByConfig = null;

            switchGroupByOff(); // Restore the table model to its original data set.
            if (property != null) switchGroupByOn(property);
        }
    }

   public String getGroupByFunctionCode(int columnIndex) {
        if (columnIndex >= getColumnCount()) return null;
        Integer index = new Integer(columnIndex);
        String code = groupByFunctions.get(index);
        if (code == null) groupByFunctions.put(index, code = CountFunction.CODE);
        return code;
    }

    public void setGroupByFunctionCode(int columnIndex, String functionCode) {
        Integer key = new Integer(columnIndex);
        String currentCode = groupByFunctions.get(key);
        if (currentCode == null || !currentCode.equals(functionCode)) {
            groupByFunctions.put(key, functionCode);
            refreshGroupBy();
        }
    }

    public boolean showGroupByTotals() {
        return groupByShowTotals;
    }

    public void setGroupByShowTotals(boolean groupByShowTotals) {
        this.groupByShowTotals = groupByShowTotals;
    }

    public String getGroupByTotalsHtmlStyle() {
        return groupByTotalsHtmlStyle;
    }

    public void setGroupByTotalsHtmlStyle(String groupByTotalsHtmlStyle) {
        this.groupByTotalsHtmlStyle = groupByTotalsHtmlStyle;
    }

    public boolean isNonGroupByColumn(int columnIndex) {
        if (groupByProperty == null) return true;

        DataProperty prop = getDataProperty(columnIndex);
        if (prop == null) return true;

        return !groupByProperty.equals(prop);
    }

    public int[] getNonGroupByColumnIndexes() {
        if (groupByProperty == null) return new int[] {};

        List<Integer> temp = new ArrayList<Integer>();
        for (int i=0; i<getColumnCount(); i++) {
            DataProperty prop = getDataProperty(i);
            if (prop != null && !groupByProperty.equals(prop)) {
                temp.add(Integer.valueOf(i));
            }
        }
        int [] results = new int[temp.size()];
        for (int i = 0; i < results.length; i++) results[i] = temp.get(i).intValue();
        return results;
    }

    protected void switchGroupByOn(DataProperty property) {
        if (property == null) return;

        groupByProperty = property;
        DataSetTableModel model = (DataSetTableModel) super.getModel();
        model.setDataSet(groupByDataSet(groupByProperty));

        // Link the table columns with the new group by data set properties.
        for (int i=0; i<getColumnCount(); i++) {
            getColumn(i).setPropertyId(getDataProperty(i).getPropertyId());
        }
    }

    protected void switchGroupByOff() {
        if (groupByProperty == null) return;

        // Restore the table columns links to the original data set.
        for (int i=0; i<getColumnCount(); i++) {
            TableColumn column = getColumn(i);

            column.setPropertyId(getOriginalDataProperty(i).getPropertyId());
        }

        // Reset the group by and set the original data set as active. 
        DataSetTableModel model = (DataSetTableModel) super.getModel();
        model.setDataSet(dataSet);
        groupByProperty = null;
    }

    public void refreshGroupBy() {
        if (groupByProperty == null) return;
        DataProperty gbp = groupByProperty;
        switchGroupByOff();

        // Refresh the group by property in order the get the intervals from the current dataset.
        switchGroupByOn(gbp);
    }

    protected void catchDataSetChanges() {
        if (dataProvider == null) return;
        try {
            // Be aware of the dataset changes.
            DataSet uptodateDataSet = dataProvider.getDataSet();
            boolean dataSetChanged = (dataSet != null && (dataSet != uptodateDataSet || dataSetRowCount != uptodateDataSet.getRowCount()));
            dataSet = uptodateDataSet;

            // If the data set is updated then refresh the current group by (if any).
            if (dataSetChanged) {
                currentPage = 1;
                dataSetRowCount = dataSet.getRowCount();
                if (getGroupByProperty() != null) refreshGroupBy();
                else ((DataSetTableModel) super.getModel()).setDataSet(dataSet);
            }
        } catch (Exception e) {
            // Errors on dataset retrieval must be propagated as a unexpected situation.
            throw new RuntimeException(e);
        }
    }

    protected DataSet groupByDataSet(DataProperty groupByProperty) {
        // Performance log.
        log.debug("Creating the group by data set.");

        DataSet originalDataSet = getOriginalDataSet();
        int[] columns = new int [getColumnCount()];
        String[] functions = new String[getColumnCount()];
        for (int i=0; i<getColumnCount(); i++) {
            columns[i] = originalDataSet.getPropertyColumn(getOriginalDataProperty(i));
            functions[i] = getGroupByFunctionCode(i);
        }
        return originalDataSet.groupBy(groupByProperty, columns, functions);
    }

    // For internal use only.
    void setGroupByConfiguration(DomainConfiguration groupByConfig) {
        this.groupByConfig = groupByConfig;
    }
}
