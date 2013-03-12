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

import java.util.List;

import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.provider.DataProvider;

/**
 * Export options.
 */
public interface ExportOptions {

    boolean ignoreKPIs();
    void setIgnoreKPIs(boolean ignoreKPIs);

    boolean ignoreDataProviders();
    void setIgnoreDataProviders(boolean ignoreDataProviders);

    List<KPI> getKPIs();
    void setKPIs(List<KPI> KPIs);

    List<DataProvider> getDataProviders();
    void setDataProviders(List<DataProvider> dataProviders);
}