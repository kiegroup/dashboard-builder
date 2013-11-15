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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.dashboard.annotation.config.Config;

@ApplicationScoped
public class DataSetSettingsImpl implements DataSetSettings {

    @Inject @Config("209715200") // 200Mb
    protected long maxMemoryUsedInDataLoad;

    @Inject @Config("104857600") // 100Mb
    protected long maxDataSetSizeInBytes;

    @Inject @Config("10000") // 10 seconds
    protected long maxDataSetLoadTimeInMillis;

    @Inject @Config("10000") // 10 seconds
    protected long maxDataSetFilterTimeInMillis;

    @Inject @Config("10000") // 10 seconds
    protected long maxDataSetGroupTimeInMillis;

    @Inject @Config("10000") // 10 seconds
    protected long maxDataSetSortTimeInMillis;

    public long getMaxDataSetSizeInBytes() {
        return maxDataSetSizeInBytes;
    }

    public void setMaxDataSetSizeInBytes(long maxDataSetSizeInBytes) {
        this.maxDataSetSizeInBytes = maxDataSetSizeInBytes;
    }

    public long getMaxDataSetLoadTimeInMillis() {
        return maxDataSetLoadTimeInMillis;
    }

    public void setMaxDataSetLoadTimeInMillis(long maxDataSetLoadTimeInMillis) {
        this.maxDataSetLoadTimeInMillis = maxDataSetLoadTimeInMillis;
    }

    public long getMaxMemoryUsedInDataLoad() {
        return maxMemoryUsedInDataLoad;
    }

    public void setMaxMemoryUsedInDataLoad(long maxMemoryUsedInDataLoad) {
        this.maxMemoryUsedInDataLoad = maxMemoryUsedInDataLoad;
    }

    public long getMaxDataSetFilterTimeInMillis() {
        return maxDataSetFilterTimeInMillis;
    }

    public void setMaxDataSetFilterTimeInMillis(long maxDataSetFilterTimeInMillis) {
        this.maxDataSetFilterTimeInMillis = maxDataSetFilterTimeInMillis;
    }

    public long getMaxDataSetGroupTimeInMillis() {
        return maxDataSetGroupTimeInMillis;
    }

    public void setMaxDataSetGroupTimeInMillis(long maxDataSetGroupTimeInMillis) {
        this.maxDataSetGroupTimeInMillis = maxDataSetGroupTimeInMillis;
    }

    public long getMaxDataSetSortTimeInMillis() {
        return maxDataSetSortTimeInMillis;
    }

    public void setMaxDataSetSortTimeInMillis(long maxDataSetSortTimeInMillis) {
        this.maxDataSetSortTimeInMillis = maxDataSetSortTimeInMillis;
    }
}
