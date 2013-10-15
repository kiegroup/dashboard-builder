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
package org.jboss.dashboard.dataset;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.profiler.CodeBlockHelper;
import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.profiler.CodeBlockType;
import org.jboss.dashboard.profiler.CoreCodeBlockTypes;
import org.jboss.dashboard.provider.DataFilter;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.DataProviderImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * In memory based implementation which holds a session scoped cache of all the datasets loaded and filtered within
 * the user's session. This is not a highly scalable implementation since memory usage is not optimized at all.
 *
 * TODO: provide production-ready optimized DataSetManager implementations.
 */
@SessionScoped
public class DataSetManagerImpl implements DataSetManager, Serializable {

    protected Map<String, DataSetHolder> dataSetMap = new HashMap<String, DataSetHolder>();

    // Public services

    public DataSet getDataSet(DataProvider dataProvider) throws Exception {
        DataSetHolder dataSetHolder = getDataSetHolder(dataProvider);
        return dataSetHolder.getDataSet();
    }

    public void registerDataSet(DataProvider dataProvider, DataSet dataSet) throws Exception {
        createDataSetHolder(dataProvider, dataSet);
    }

    public DataSet refreshDataSet(DataProvider dataProvider) throws Exception {
        removeDataSetHolder(dataProvider);
        return getDataSet(dataProvider);
    }

    public DataSet filterDataSet(DataProvider dataProvider, DataFilter dataFilter) throws Exception {
        CodeBlockTrace trace = CodeBlockHelper.newCodeBlockTrace(CoreCodeBlockTypes.DATASET_FILTER,
                "dataset-filter-" + dataProvider.getCode(),
                "Data set filter - " + dataProvider.getDescription(LocaleManager.currentLocale()),
                createDataProviderContext(dataProvider)).begin();
        try {
            DataSetHolder dataSetHolder = getDataSetHolder(dataProvider);
            dataSetHolder.filteredDataSet = dataSetHolder.originalDataSet.filter(dataFilter);
            if (dataSetHolder.filteredDataSet != null) {
                ((DataProviderImpl)dataProvider).deserializeDataProperties(dataSetHolder.filteredDataSet);
            }
            return dataSetHolder.getDataSet();
        } finally {
            trace.end();
        }
    }

    // Internal stuff

    protected DataSetHolder getDataSetHolder(DataProvider dataProvider) throws Exception {
        String providerCode = dataProvider.getCode();
        DataSetHolder dataSetHolder = dataSetMap.get(providerCode);
        if (dataSetHolder == null) {
            DataSet dataSet = loadDataSet(dataProvider);
            dataSetHolder = createDataSetHolder(dataProvider, dataSet);
        }
        return dataSetHolder;
    }

    protected DataSetHolder createDataSetHolder(DataProvider dataProvider, DataSet dataSet) throws Exception {
        String providerCode = dataProvider.getCode();
        DataSetHolder dataSetHolder = new DataSetHolder();
        dataSetHolder.dataProviderCode = providerCode;
        dataSetHolder.originalDataSet = dataSet;
        dataSetMap.put(providerCode, dataSetHolder);
        return dataSetHolder;
    }

    protected DataSetHolder removeDataSetHolder(DataProvider dataProvider) throws Exception {
        String providerCode = dataProvider.getCode();
        return dataSetMap.remove(providerCode);
    }

    protected DataSet loadDataSet(DataProvider dataProvider) throws Exception {
        CodeBlockTrace trace = CodeBlockHelper.newCodeBlockTrace(CoreCodeBlockTypes.DATASET_LOAD,
                                "dataset-load-" + dataProvider.getCode(),
                                "Data set load - " + dataProvider.getDescription(LocaleManager.currentLocale()),
                                createDataProviderContext(dataProvider)).begin();
        try {
            DataSet dataSet = dataProvider.getDataLoader().load(dataProvider);
            dataSet.setDataProvider(dataProvider);
            ((DataProviderImpl)dataProvider).deserializeDataProperties(dataSet);
            return dataSet;
        } finally {
            trace.end();
        }
    }

    protected Map createDataProviderContext(DataProvider dataProvider) {
        Map m = new HashMap();
        m.put("Provider code", dataProvider.getCode());
        m.put("Provider description", dataProvider.getDescription(LocaleManager.currentLocale()));
        return m;
    }

    private class DataSetHolder {

        String dataProviderCode;
        DataSet originalDataSet;
        DataSet filteredDataSet;

        DataSet getDataSet() {
            if (filteredDataSet != null) return filteredDataSet;
            return originalDataSet;
        }
    }
}
