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

import org.jboss.dashboard.dataset.DataSet;

import java.util.Locale;
import java.util.Map;

/**
 * A DataProvider knows how to retrieve data sets from a given source: a database, a CSV file, ...
 */
public interface DataProvider {

    /**
     * The type of this data provider.
     */
    DataProviderType getDataProviderType();

    /**
     * The loader used to retrieve data.
     */
    DataLoader getDataLoader();
    void setDataLoader(DataLoader dataLoader);

    /**
     * The object identifier.
     */
    Long getId();
    void setId(Long newId);

    /**
     * The code is an unique identifier that is universal and is not tied to the persistent storage.
     */
    String getCode();
    void setCode(String code);

    /**
     * The provider description.
     */
    String getDescription(Locale l);
    void setDescription(String  descr, Locale l);


    /**
     * The localized descriptions.
     * @return A map of (Locale instance, localized description).
     */
    Map getDescriptionI18nMap();

    /**
     * Check if the provider is deleteable.
     */
    boolean isCanDelete();
    void setCanDelete(boolean canDelete);

    /**
     * Check if the provider is editable.
     */
    boolean isCanEdit();
    void setCanEdit(boolean canEdit);

    /**
     * Check if the provider properties are editable.
     */
    boolean isCanEditProperties();
    void setCanEditProperties(boolean canEditProperties);

    /**
     * Returns if the provider is ready to build its data set. This implies provider is well configured.
     */
    boolean isReady();

    /**
     * Get a dataset. Cache of data might be implemented by the provider.<br>
     * IMPORTANT NOTE: this method is called several times by the UI components.
     */
    DataSet getDataSet() throws Exception;
    void setDataSet(DataSet s);

    /**
     * Get the most up to date data.<br>
     * IMPORTANT NOTE: Avoid the usage of caches.
     */
    DataSet refreshDataSet() throws Exception;

    /**
     * Same as <code>getDataSet</code> by the data set retrieved satisfy the specified filter criteria.
     */
    DataSet filterDataSet(DataFilter filter) throws Exception;

    // Persist services

    boolean save() throws Exception;
    boolean delete() throws Exception;
    boolean isPersistent() throws Exception;
}


