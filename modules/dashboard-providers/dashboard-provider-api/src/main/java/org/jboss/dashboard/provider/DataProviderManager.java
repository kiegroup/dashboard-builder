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
package org.jboss.dashboard.provider;

import java.util.List;
import java.util.Set;

/**
 * Manager class that it allows for the retrieval of DataProvider instances.
 */
public interface DataProviderManager {

    /**
     * Get installed data providers.
     */
    DataProviderType[] getDataProviderTypes();

    /**
     * Get a provider type by its UID.
     */
    DataProviderType getProviderTypeByUid(String uid);

    /**
     * Factory method for the creation of a DataProvider brand new instance.
     */
    DataProvider createDataProvider();

    /**
     * Get all the data providers instances created.
     * @return A set of DataProvider instances.
     */
    Set<DataProvider> getAllDataProviders() throws Exception;

    /**
     * Get a persistent provider by its identifier.
     */
    DataProvider getDataProviderById(Long id) throws Exception;

    /**
     * Get a persistent provider by its universal code.
     */
    DataProvider getDataProviderByCode(String code) throws Exception;

    /**
     * Remove data provider
     */
    void removeDataProvider(DataProvider dataProvider) throws Exception;

    /**
     * Sort a list of data provider by description.
     */
    void sortDataProvidersByDescription(List<DataProvider> propList, boolean ascending);

    /**
     * Sort a list of properties by name.
     */
    void sortDataPropertiesByName(List<DataProperty> propList, boolean ascending);
}

