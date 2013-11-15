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
package org.jboss.dashboard.dataset.profiler;

import java.lang.ref.WeakReference;

import org.jboss.dashboard.commons.misc.Chronometer;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.dataset.DataSetException;
import org.jboss.dashboard.profiler.RuntimeConstraint;
import org.jboss.dashboard.profiler.memory.MemoryProfiler;

/**
 * Runtime constraints addressed to guarantee that data set load operations never exceed the max memory/time thresholds set.
 */
public class DataSetLoadConstraints implements RuntimeConstraint {

    protected WeakReference<DataSet> dataSetRef;
    protected long startMemory;
    protected long startTime;

    public DataSetLoadConstraints(DataSet dataSet) {
        dataSetRef = new WeakReference<DataSet>(dataSet);
        MemoryProfiler memoryProfiler = MemoryProfiler.lookup().freeMemory();
        startMemory = memoryProfiler.getMemoryUsedInBytes();
        startTime = System.currentTimeMillis();
    }

    /**
     * @throws DataSetException In case any it doesn't meet the thresholds set.
     */
    public void validate() throws Exception {
        DataSet dataSet = dataSetRef.get();
        MemoryProfiler memoryProfiler = MemoryProfiler.lookup().freeMemory();
        long memoryUsed = memoryProfiler.getMemoryUsedInBytes() - startMemory;
        long elapsedTime = System.currentTimeMillis() - startTime;
        long sizeInBytes = dataSet.sizeOf();
        long maxSize = dataSet.getDataProvider().getDataLoader().getMaxDataSetSizeInBytes();
        long maxTime = dataSet.getDataProvider().getDataLoader().getMaxDataSetLoadTimeInMillis();
        long maxMemUsed = dataSet.getDataProvider().getDataLoader().getMaxMemoryUsedInDataLoad();

        if (maxMemUsed > 0 && memoryUsed > maxMemUsed) {
            String total = MemoryProfiler.formatSize(maxMemUsed);
            throw new DataSetException("Data set load memory usage has been exceeded = " + total);
        }
        else if (maxTime > 0 && elapsedTime > maxTime) {
            String time = Chronometer.formatElapsedTime(maxTime);
            throw new DataSetException("Data set load time has been exceeded = " + time);
        }
        else if (maxSize > 0 && sizeInBytes > maxSize) {
            String size = MemoryProfiler.formatSize(maxSize);
            throw new DataSetException("Data set size in memory has been exceeded = " + size);
        }
    }
}
