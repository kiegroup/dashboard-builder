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
import org.jboss.dashboard.provider.DataProvider;

/**
 * Runtime constraints addressed to guarantee that data set filter operations never exceed the max. time set.
 */
public class DataSetFilterConstraints implements RuntimeConstraint {

    protected WeakReference<DataSet> dataSetRef;
    protected long startTime;

    public DataSetFilterConstraints(DataSet dataSet) {
        dataSetRef = new WeakReference<DataSet>(dataSet);
        startTime = System.currentTimeMillis();
    }

    /**
     * @throws DataSetException In case any it doesn't meet the thresholds set.
     */
    public void validate() throws Exception {
        DataSet dataSet = dataSetRef.get();
        DataProvider dataProvider = dataSet.getDataProvider();
        if (dataProvider == null) return;

        long elapsedTime = System.currentTimeMillis() - startTime;
        long maxTime = dataProvider.getDataLoader().getMaxDataSetFilterTimeInMillis();

        if (maxTime > 0 && elapsedTime > maxTime) {
            String time = Chronometer.formatElapsedTime(maxTime);
            throw new DataSetException("Data set filter time has been exceeded = " + time);
        }
    }
}
