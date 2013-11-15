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

/**
 * Sometimes, the nature and location of data may lead to unexpected bad performance situations like: high-volume data
 * load or connectivity issues. To cope with these situations the system needs detection mechanisms.
 *
 * <p>This interface defines a bunch of settings that the system uses to know what are the thresholds allowed for some
 * data set operations like: load, filter or group. Such operations are cpu or memory intensive prone and could
 * compromise the system stability.</p>
 */
public interface DataSetSettings {

    /**
     * Maximum memory in bytes a data set load operation may consume.
     * <p>Notice that, however this is a general setting, an individual DataSetLoader instance might set its own threshold.</p>
     */
    long getMaxMemoryUsedInDataLoad();
    void setMaxMemoryUsedInDataLoad(long maxMemoryUsedInDataLoad);

    /**
     * Maximum size in bytes a data set may have.
     * <p>Notice that, however this is a general setting, an individual DataSetLoader instance might set its own threshold.</p>
     */
    long getMaxDataSetSizeInBytes();
    void setMaxDataSetSizeInBytes(long maxDataSetSizeInBytes);

    /**
     * Maximum time in milliseconds a data set load operation may last.
     * <p>Notice that, however this is a general setting, an individual DataSetLoader instance might set its own threshold.</p>
     */
    long getMaxDataSetLoadTimeInMillis();
    void setMaxDataSetLoadTimeInMillis(long maxDataSetLoadTimeInMillis);

    /**
     * Maximum time in milliseconds a data set filter operation may last.
     * <p>Notice that, however this is a general setting, an individual DataSetLoader instance might set its own threshold.</p>
     */
    long getMaxDataSetFilterTimeInMillis();
    void setMaxDataSetFilterTimeInMillis(long maxDataSetLoadTimeInMillis);

    /**
     * Maximum time in milliseconds a data set group operation may last.
     * <p>Notice that, however this is a general setting, an individual DataSetLoader instance might set its own threshold.</p>
     */
    long getMaxDataSetGroupTimeInMillis();
    void setMaxDataSetGroupTimeInMillis(long maxDataSetLoadTimeInMillis);

    /**
     * Maximum time in milliseconds a data set sort operation may last.
     * <p>Notice that, however this is a general setting, an individual DataSetLoader instance might set its own threshold.</p>
     */
    long getMaxDataSetSortTimeInMillis();
    void setMaxDataSetSortTimeInMillis(long maxDataSetSortTimeInMillis);
}
