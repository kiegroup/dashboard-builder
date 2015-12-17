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
package org.jboss.dashboard.displayer;

import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.dataset.DataSetComparator;
import org.jboss.dashboard.dataset.DataSetManager;
import org.jboss.dashboard.displayer.chart.BarChartDisplayer;
import org.jboss.dashboard.displayer.chart.BarChartDisplayerType;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.function.ScalarFunctionManager;
import org.jboss.dashboard.function.SumFunction;
import org.jboss.dashboard.provider.*;
import org.jboss.dashboard.provider.csv.CSVDataLoader;
import org.jboss.dashboard.provider.csv.CSVDataProviderType;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.fest.assertions.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dashboard.test.ShrinkWrapHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.InputStream;

import static org.fest.assertions.api.Assertions.*;
import static org.jboss.dashboard.dataset.Assertions.*;

@RunWith(Arquillian.class)
public class BarChartDisplayerCSVTest {

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
    protected ScalarFunctionManager scalarFunctionManager;

    @Inject
    protected DataDisplayerManager dataDisplayerManager;

    protected DataSetManager dataSetManager;
    protected DataSet dataSet;
    protected DataProviderType dataPoviderType;

    @Before
    public void setUp() throws Exception {
        CDIBeanLocator.beanManager = beanManager;
        dataSetManager = DataProviderServices.lookup().getDataSetManager();

        dataPoviderType = dataProviderManager.getProviderTypeByUid(CSVDataProviderType.UID);
        DataProvider dataProvider = dataProviderManager.createDataProvider();
        CSVDataLoader csvDataLoader = (CSVDataLoader) dataPoviderType.createDataLoader();
        dataProvider.setDataLoader(csvDataLoader);

        InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("data.csv");
        dataSet = csvDataLoader.load(dataProvider, dataStream);
        dataSetManager.registerDataSet(dataProvider, dataSet);
    }

    @Test
    public void buildDataSet() throws Exception {
        // Read properties
        Assertions.assertThat(dataSet).isNotNull();
        DataProperty propDept = dataSet.getPropertyById("department");
        DataProperty propAmount = dataSet.getPropertyById("amount");
        assertThat(propDept).isNotNull();
        assertThat(propAmount).isNotNull();

        // Init a bar chart displayer
        BarChartDisplayerType barChartDisplayerType = (BarChartDisplayerType) dataDisplayerManager.getDisplayerTypeByUid(BarChartDisplayerType.UID);
        BarChartDisplayer barChartDisplayer = (BarChartDisplayer) barChartDisplayerType.createDataDisplayer();
        barChartDisplayer.setDataProvider(dataSet.getDataProvider());
        barChartDisplayer.setDomainProperty(propDept);
        barChartDisplayer.setRangeProperty(propAmount);

        // Calculate the data set to display.
        // Set the scalar function to apply for the calculations of the chart values.
        ScalarFunction scalarFunction = scalarFunctionManager.getScalarFunctionByCode(SumFunction.CODE);
        barChartDisplayer.setRangeScalarFunction(scalarFunction);
        DataSet xyDataSet = barChartDisplayer.buildXYDataSet();

        // Check the expected results matrix
        assertDataSetValues(xyDataSet, new String[][] {
                new String[] {"Engineering", "7,650.162"},
                new String[] {"Services", "2,504.5"},
                new String[] {"Sales", "3,213.53"},
                new String[] {"Support", "3,345.6"},
                new String[] {"Management", "6,017.47"}}, 0);


        // Sort by department and check the expected results.
        DataSetComparator comp = new DataSetComparator();
        comp.addSortCriteria("0", DataSetComparator.ORDER_DESCENDING);
        xyDataSet = xyDataSet.sort(comp);
        assertDataSetValues(xyDataSet, new String[][] {
                new String[] {"Support", "3,345.6"}}, 0);

        // Sort by amount and check the expected results.
        comp = new DataSetComparator();
        comp.addSortCriteria("1", DataSetComparator.ORDER_ASCENDING);
        xyDataSet = xyDataSet.sort(comp);
        assertDataSetValues(xyDataSet, new String[][]{
                new String[]{"Services", "2,504.5"}}, 0);

    }
}