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
package org.jboss.dashboard.provider.csv;

import java.io.InputStream;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.commons.misc.Chronometer;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.dataset.DataSetException;
import org.jboss.dashboard.dataset.DataSetSettings;
import org.jboss.dashboard.profiler.memory.MemoryProfiler;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.DataProviderManager;
import org.jboss.dashboard.provider.DataProviderType;
import org.jboss.dashboard.test.ShrinkWrapHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fest.assertions.api.Assertions.*;

@RunWith(Arquillian.class)
public class CSVPerformanceTest {

    private static transient Logger log = LoggerFactory.getLogger(CSVPerformanceTest.class.getName());

    @Deployment
    public static Archive<?> createTestArchive()  {
        return ShrinkWrapHelper.createJavaArchive()
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    protected BeanManager beanManager;

    @Inject
    protected DataProviderManager dataProviderManager;

    @Inject
    protected MemoryProfiler memoryProfiler;

    protected DataProvider dataProvider;
    protected CSVDataLoader dataLoader;

    @Before
    public void setUp() throws Exception {
        CDIBeanLocator.beanManager = beanManager;
        DataProviderType dataPoviderType = dataProviderManager.getProviderTypeByUid(CSVDataProviderType.UID);
        dataProvider = dataProviderManager.createDataProvider();
        dataLoader = (CSVDataLoader) dataPoviderType.createDataLoader();
    }

    /**
     * Build a 500-row (120 Kb aprox) CSV data set in less than 1 second and using less than 1 Mb Kb of memory.
     */
    @Test
    public void test500Rows() throws Exception {
        DataSetSettings dataSetSettings = DataProviderServices.lookup().getDataSetSettings();
        dataSetSettings.setMaxDataSetSizeInBytes(1024 * 1024);
        dataSetSettings.setMaxDataSetLoadTimeInMillis(1000);

        try {
            memoryProfiler.freeMemory();
            long timeBegin = System.currentTimeMillis();
            long memBegin = memoryProfiler.getMemoryUsedInBytes();

            int n = 10;
            DataSet dataSet = buildDataSet(n, dataProvider, dataLoader);

            memoryProfiler.freeMemory();
            long timeEnd = System.currentTimeMillis();
            long memEnd = memoryProfiler.getMemoryUsedInBytes();
            long mem = memEnd-memBegin;
            log.info("500 rows - Load time = " + Chronometer.formatElapsedTime(timeEnd-timeBegin));
            log.info("500 rows - Size of = " + MemoryProfiler.formatSize(dataSet.sizeOf()));
            log.info("500 rows - Memory consumption = " + MemoryProfiler.formatSize(mem));

            assertThat(mem).isLessThan(1024 * 1024);
            assertThat(dataSet).isNotNull();
            assertThat(dataSet.getProperties().length).isGreaterThan(0);
            assertThat(dataSet.getRowCount()).isEqualTo(50*n);
        } catch (DataSetException e) {
            fail("Load constraints violated.", e);
        }
    }

    /**
     * Build a 50000-row (12 Mb aprox) data set in less than 5 seconds and using less than 50 Mb of memory (conservative settings).
     */
    @Test
    public void test50000Rows() throws Exception {
        DataSetSettings dataSetSettings = DataProviderServices.lookup().getDataSetSettings();
        dataSetSettings.setMaxDataSetLoadTimeInMillis(5000);
        dataSetSettings.setMaxDataSetSizeInBytes(50 * 1024 * 1024);

        try {
            memoryProfiler.freeMemory();
            long timeBegin = System.currentTimeMillis();
            long memBegin = memoryProfiler.getMemoryUsedInBytes();

            int n = 1000;
            DataSet dataSet = buildDataSet(n, dataProvider, dataLoader);

            memoryProfiler.freeMemory();
            long timeEnd = System.currentTimeMillis();
            long memEnd = memoryProfiler.getMemoryUsedInBytes();
            long mem = memEnd-memBegin;
            log.info("50000 rows - Load time = " + Chronometer.formatElapsedTime(timeEnd - timeBegin));
            log.info("50000 rows - Size of = " + MemoryProfiler.formatSize(dataSet.sizeOf()));
            log.info("50000 rows - Memory consumption = " + MemoryProfiler.formatSize(mem));


            assertThat(mem).isLessThan(50 * 1024 * 1024);
            assertThat(dataSet).isNotNull();
            assertThat(dataSet.getProperties().length).isGreaterThan(0);
            assertThat(dataSet.getRowCount()).isEqualTo(50*n);
        } catch (DataSetException e) {
            fail("Load constraints violated.", e);
        }
    }

    /**
     * Failure test case: Build a 500-row data set in less than 1 millisecond.
     */
    @Test
    public void testLoadTimeExceeded() throws Exception {
        DataSetSettings dataSetSettings = DataProviderServices.lookup().getDataSetSettings();
        dataSetSettings.setMaxDataSetLoadTimeInMillis(1);
        dataSetSettings.setMaxDataSetSizeInBytes(-1); // Disable memory usage checking.

        try {
            buildDataSet(10, dataProvider, dataLoader);
            failBecauseExceptionWasNotThrown(DataSetException.class);
        } catch (DataSetException e) {
            // It's expected
        }
    }

    /**
     * Failure test case: Build a 500-row CSV data set using less than 1 byte of memory.
     */
    @Test
    public void testDataSetSizeExceeded() throws Exception {
        DataSetSettings dataSetSettings = DataProviderServices.lookup().getDataSetSettings();
        dataSetSettings.setMaxDataSetLoadTimeInMillis(-1); // Disable load time checking.
        dataSetSettings.setMaxDataSetSizeInBytes(1);

        try {
            buildDataSet(10, dataProvider, dataLoader);
            failBecauseExceptionWasNotThrown(DataSetException.class);
        } catch (DataSetException e) {
            // It's expected
        }
    }

    public DataSet buildDataSet(int n, DataProvider dataProvider, CSVDataLoader dataLoader) throws Exception {
        CompositeInputStream dataStream = new CompositeInputStream();
        try {
            dataStream.addPart(Thread.currentThread().getContextClassLoader().getResourceAsStream("data_header.csv"));
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("data_chunk.csv");
            for (int i = 0; i < n; i++) dataStream.addPart(is);
            return dataLoader.load(dataProvider, dataStream);
        } finally {
            dataStream.close();
        }
    }
}
