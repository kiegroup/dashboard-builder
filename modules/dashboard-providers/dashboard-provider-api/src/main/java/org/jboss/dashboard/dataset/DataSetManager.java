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

import org.jboss.dashboard.provider.DataFilter;
import org.jboss.dashboard.provider.DataProvider;

/**
 * Main interface for accessing data sets. It acts as a data set cache.
 */
public interface DataSetManager {

    /**
     * Get the current data set for the given provider.
     * The data set returned might vary depending whether there are active filters applied on the provider or not.
     */
    DataSet getDataSet(DataProvider dataProvider) throws Exception;

    /**
     * Registers the specified DataSet instance as the given DataProvider's data set.
     */
    void registerDataSet(DataProvider dataProvider, DataSet dataSet) throws Exception;

    /**
     * Discard any active filter and ensure the most up to date data is loaded and returned.
     */
    DataSet refreshDataSet(DataProvider dataProvider) throws Exception;

    /**
     * Apply a filter on the specified DataProvider's data set and save a reference to the resulting data set.
     *
     * @param dataFilter The filter to apply on current provider's data set.
     *                   If null or empty then the current filter is unset and the original data set is returned.data set is
     */
    DataSet filterDataSet(DataProvider dataProvider, DataFilter dataFilter) throws Exception;
}
