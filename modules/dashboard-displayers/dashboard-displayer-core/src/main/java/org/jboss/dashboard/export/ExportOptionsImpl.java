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
package org.jboss.dashboard.export;

import java.util.ArrayList;
import java.util.List;

import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.provider.DataProvider;

/**
 * Export options.
 */
public class ExportOptionsImpl implements ExportOptions {

    protected boolean ignoreKPIs;
    protected boolean ignoreDataProviders;
    protected List<KPI> KPIs;
    protected List<DataProvider> dataProviders;

    public ExportOptionsImpl() {
        ignoreKPIs = false;
        ignoreDataProviders = false;
        this.KPIs = new ArrayList<KPI>();
        this.dataProviders = new ArrayList<DataProvider>();
    }

    public boolean ignoreKPIs() {
        return ignoreKPIs;
    }

    public void setIgnoreKPIs(boolean ignoreKPIs) {
        this.ignoreKPIs = ignoreKPIs;
    }

    public boolean ignoreDataProviders() {
        return ignoreDataProviders;
    }

    public void setIgnoreDataProviders(boolean ignoreDataProviders) {
        this.ignoreDataProviders = ignoreDataProviders;
    }

    public List<KPI> getKPIs() {
        return KPIs;
    }

    public void setKPIs(List<KPI> KPIs) {
        this.KPIs = KPIs;
    }

    public List<DataProvider> getDataProviders() {
        return dataProviders;
    }

    public void setDataProviders(List<DataProvider> dataProviders) {
        this.dataProviders = dataProviders;
    }
}