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
import org.jboss.dashboard.displayer.AbstractDataDisplayer;
import org.jboss.dashboard.displayer.exception.DataDisplayerInvalidConfiguration;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Table displayer implementation.
 */
public class TableDisplayer extends AbstractDataDisplayer {

    /** Logger */
    private transient static Logger log = LoggerFactory.getLogger(TableDisplayer.class);
    
    /**
     * The table component.
     */
    protected DataSetTable table;

    public DataSetTable getTable() {
        return table;
    }

    public void setTable(DataSetTable table) {
        this.table = table;
    }

    public void setDataProvider(DataProvider dp) throws DataDisplayerInvalidConfiguration {
        super.setDataProvider(dp);
        table.setDataProvider(dp);
    }

    /**
     * Check if data provider definition (all properties) match with the serialized in the current displayer.
     * @param provider The data provider.
     * @throws org.jboss.dashboard.displayer.exception.DataDisplayerInvalidConfiguration Current displayer configuration is invalid.
     */
    @Override
    public void validate(DataProvider provider) throws DataDisplayerInvalidConfiguration {
        if (provider != null) {
            int columnCount = table.getColumnCount();
            for (int x = 0; x < columnCount; x++) {
                TableColumn column = table.getColumn(x);
                String propId = column.getPropertyId();
                try {
                    if ( hasProviderPropertiesChanged(propId, provider.getDataSet()) ) {
                        throw new DataDisplayerInvalidConfiguration();
                    }
                } catch (DataDisplayerInvalidConfiguration e) {
                    throw e;
                } catch (Exception e) {
                    log.error("Cannot obtain data set.", e);
                }
                
            }
        }
    }

    /**
     * BZ-1100635: Check if a data provider property match with the serialized in the displayer.
     * @param propertyId The data property id from this displayer to check if exist in the data provider.
     * @param dataSet The new data set definition.
     * @return If the data displayer property exists in current data provider.
     */
    public boolean hasProviderPropertiesChanged(String propertyId, DataSet dataSet) {
        if (propertyId == null || propertyId.trim().length() == 0) return false;

        // DataSet dataSet = dataProvider.getDataSet();
        DataProperty[] datasetProperties = dataSet.getProperties();
        if (datasetProperties != null && datasetProperties.length > 0) {
            for (DataProperty datasetProperty : datasetProperties) {
                String datasetPropertyId = datasetProperty.getPropertyId();
                if (datasetPropertyId.equals(propertyId)) return false;
            }
        }
        return true;
    }
}
