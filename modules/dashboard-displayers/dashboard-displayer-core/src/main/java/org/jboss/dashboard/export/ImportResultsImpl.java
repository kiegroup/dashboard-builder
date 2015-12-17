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

import java.util.HashSet;
import java.util.Set;

import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.commons.message.MessageList;

/**
 * Import results.
 */
public class ImportResultsImpl implements ImportResults {

    protected Set<KPI> KPIs;
    protected Set<DataProvider> dataProviders;
    protected MessageList messages;

    public ImportResultsImpl() {
        this.KPIs = new HashSet<KPI>();
        this.dataProviders = new HashSet<DataProvider>();
        this.messages = new MessageList();
    }

    public Set<KPI> getKPIs() {
        return new HashSet<KPI>(KPIs);
    }

    public void addKPI(KPI k) {
        KPIs.add(k);
    }

    public void removeKPI(KPI k) {
        KPIs.remove(k);
    }

    public Set<DataProvider> getDataProviders() {
        return new HashSet<DataProvider>(dataProviders);
    }

    public void addDataProvider(DataProvider p) {
        dataProviders.add(p);
    }

    public void removeDataProvider(DataProvider p) {
        dataProviders.remove(p);
    }

    public void replaceDataProvider(DataProvider oldProv, DataProvider newProv) {
        if (dataProviders.remove(oldProv)) {
            // Update KPIs references if needed
            for (KPI kpi : KPIs) {
                if (kpi.getDataProvider().equals(oldProv)) {
                    kpi.setDataProvider(newProv);
                }
            }
        }
        dataProviders.add(newProv);
    }

    public void replaceKPI(KPI oldKPI, KPI newKPI) {
        KPIs.remove(oldKPI);
        KPIs.add(newKPI);
    }

    public KPI getKPIByCode(String code) {
        for (KPI kpi : KPIs) {
            if (kpi.getCode().equals(code)) {
                return kpi;
            }
        }
        return null;
    }


    public DataProvider getDataProviderByCode(String code) {
        for (DataProvider provider : dataProviders) {
            if (provider.getCode().equals(code)) {
                return provider;
            }
        }
        return null;
    }

    public MessageList getMessages() {
        return messages;
    }        
}
