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
package org.jboss.dashboard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.dashboard.displayer.DataDisplayerManager;
import org.jboss.dashboard.export.ExportManager;
import org.jboss.dashboard.export.ImportManager;
import org.jboss.dashboard.function.ScalarFunctionManager;
import org.jboss.dashboard.kpi.KPIManager;
import org.jboss.dashboard.provider.DataProviderManager;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

@ApplicationScoped
@Named("dataDisplayerServices")
public class DataDisplayerServices {

    public static DataDisplayerServices lookup() {
        return (DataDisplayerServices) CDIBeanLocator.getBeanByName("dataDisplayerServices");
    }

    @Inject
    protected KPIManager kpiManager;

    @Inject
    protected DataProviderManager dataProviderManager;

    @Inject
    protected DataDisplayerManager dataDisplayerManager;

    @Inject
    protected ScalarFunctionManager scalarFunctionManager;

    @Inject
    protected ExportManager exportManager;

    @Inject
    protected ImportManager importManager;

    public KPIManager getKPIManager() {
        return kpiManager;
    }

    public DataProviderManager getDataProviderManager() {
        return dataProviderManager;
    }

    public DataDisplayerManager getDataDisplayerManager() {
        return dataDisplayerManager;
    }

    public ScalarFunctionManager getScalarFunctionManager() {
        return scalarFunctionManager;
    }

    public ExportManager getExportManager() {
        return exportManager;
    }

    public ImportManager getImportManager() {
        return importManager;
    }
}