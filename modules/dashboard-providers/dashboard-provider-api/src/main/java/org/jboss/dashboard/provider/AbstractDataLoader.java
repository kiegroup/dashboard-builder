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
package org.jboss.dashboard.provider;

import org.jboss.dashboard.DataProviderServices;

/**
 * Base class for the implementation of custom data loaders.
 */
public abstract class AbstractDataLoader implements DataLoader {

    protected DataProviderType dataProviderType;
    protected Long maxMemoryUsedInDataLoad = null;
    protected Long maxDataSetSizeInBytes = null;
    protected Long maxDataSetLoadTimeInMillis = null;
    protected Long maxDataSetFilterTimeInMillis = null;
    protected Long maxDataSetGroupTimeInMillis = null;
    protected Long maxDataSetSortTimeInMillis = null;

    public DataProviderType getDataProviderType() {
        return dataProviderType;
    }

    public void setDataProviderType(DataProviderType dataProviderType) {
        this.dataProviderType = dataProviderType;
    }

    public Long getMaxDataSetSizeInBytes() {
        if (maxDataSetLoadTimeInMillis == null) {
            return DataProviderServices.lookup().getDataSetSettings().getMaxDataSetSizeInBytes();
        }
        return maxDataSetSizeInBytes;
    }

    public void setMaxDataSetSizeInBytes(Long maxDataSetSizeInBytes) {
        this.maxDataSetSizeInBytes = maxDataSetSizeInBytes;
    }

    public Long getMaxDataSetLoadTimeInMillis() {
        if (maxDataSetLoadTimeInMillis == null) {
            return DataProviderServices.lookup().getDataSetSettings().getMaxDataSetLoadTimeInMillis();
        }
        return maxDataSetLoadTimeInMillis;
    }

    public void setMaxDataSetLoadTimeInMillis(Long maxDataSetLoadTimeInMillis) {
        this.maxDataSetLoadTimeInMillis = maxDataSetLoadTimeInMillis;
    }

    public Long getMaxMemoryUsedInDataLoad() {
        if (maxMemoryUsedInDataLoad == null) {
            return DataProviderServices.lookup().getDataSetSettings().getMaxMemoryUsedInDataLoad();
        }
        return maxMemoryUsedInDataLoad;
    }

    public void setMaxMemoryUsedInDataLoad(Long maxMemoryUsedInDataLoad) {
        this.maxMemoryUsedInDataLoad = maxMemoryUsedInDataLoad;
    }

    public Long getMaxDataSetFilterTimeInMillis() {
        if (maxDataSetFilterTimeInMillis == null) {
            return DataProviderServices.lookup().getDataSetSettings().getMaxDataSetFilterTimeInMillis();
        }
        return maxDataSetFilterTimeInMillis;
    }

    public void setMaxDataSetFilterTimeInMillis(Long maxDataSetFilterTimeInMillis) {
        this.maxDataSetFilterTimeInMillis = maxDataSetFilterTimeInMillis;
    }

    public Long getMaxDataSetGroupTimeInMillis() {
        if (maxDataSetGroupTimeInMillis == null) {
            return DataProviderServices.lookup().getDataSetSettings().getMaxDataSetGroupTimeInMillis();
        }
        return maxDataSetGroupTimeInMillis;
    }

    public void setMaxDataSetGroupTimeInMillis(Long maxDataSetGroupTimeInMillis) {
        this.maxDataSetGroupTimeInMillis = maxDataSetGroupTimeInMillis;
    }

    public Long getMaxDataSetSortTimeInMillis() {
        if (maxDataSetSortTimeInMillis == null) {
            return DataProviderServices.lookup().getDataSetSettings().getMaxDataSetSortTimeInMillis();
        }
        return maxDataSetSortTimeInMillis;
    }

    public void setMaxDataSetSortTimeInMillis(Long maxDataSetSortTimeInMillis) {
        this.maxDataSetSortTimeInMillis = maxDataSetSortTimeInMillis;
    }
}
