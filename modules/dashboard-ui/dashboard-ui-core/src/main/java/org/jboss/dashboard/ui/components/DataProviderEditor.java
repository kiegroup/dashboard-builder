/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.kpi.KPIManager;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;

import java.util.Set;

public abstract class DataProviderEditor extends DataProviderViewer {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataProviderEditor.class);
    
    public abstract CommandResponse actionSubmit(CommandRequest request) throws Exception;
    public abstract CommandResponse actionCancel(CommandRequest request) throws Exception;
    public abstract boolean isConfiguredOk();
    
    // Check if the new applied provider definition modifies existing dataset properties.
    protected boolean hasDefinitionChanged(DataSet oldDs, DataSet newDs) {
        if (oldDs != null && newDs == null) return true;
        if (newDs != null && oldDs == null) return true;

        DataProperty[] oldDatasetProperties = oldDs.getProperties();

        if (oldDatasetProperties != null && oldDatasetProperties.length > 0) {
            for (DataProperty datasetProperty : oldDatasetProperties) {
                String datasetPropertyId = datasetProperty.getPropertyId();
                if (newDs.getPropertyById(datasetPropertyId) == null) return true;
            }
        }
        return false;
    }

    /**
     * Removes all KPI instances that are using this data provider (currently edited).
     */
    protected void removeKPIs() {
        String dataProviderCode = (dataProvider != null ? dataProvider.getCode() : null);
        if (dataProviderCode != null) {
            try {
                // Remove the KPI instances that are using this data provider.
                KPIManager kpiManager = DataDisplayerServices.lookup().getKPIManager();
                Set<KPI> kpis = kpiManager.getAllKPIs();
                for (KPI kpi : kpis) {
                    if (kpi.getDataProvider().getCode().equals(dataProviderCode)) {
                        // Remove the related KPI.
                        kpi.delete();
                    }
                }
            } catch (Exception e) {
                log.error("Cannot delete KPI instances used by provider with code " + dataProvider.getCode(), e);
            }
        }
    }
}
