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
package org.jboss.dashboard.export;

import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.commons.message.MessageList;
import org.jboss.dashboard.provider.DataProvider;

import java.util.Set;

/**
 * Import results.
 */
public interface ImportResults {

    MessageList getMessages();

    Set<KPI> getKPIs();
    void addKPI(KPI k);
    void removeKPI(KPI k);
    void replaceKPI(KPI oldKPI, KPI newKPI);
    KPI getKPIByCode(String code);

    Set<DataProvider> getDataProviders();
    void addDataProvider(DataProvider p);
    void removeDataProvider(DataProvider p);
    void replaceDataProvider(DataProvider oldProv, DataProvider newProv);
    DataProvider getDataProviderByCode(String code);
}
