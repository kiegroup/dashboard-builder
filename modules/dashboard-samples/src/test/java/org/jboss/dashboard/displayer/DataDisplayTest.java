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
package org.jboss.dashboard.displayer;

import org.jboss.dashboard.dataset.DataSet;
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
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.InputStream;
import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class DataDisplayTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage("org.jboss.dashboard.commons.message")
                .addPackage("org.jboss.dashboard")
                .addPackage("org.jboss.dashboard.security")
                .addPackage("org.jboss.dashboard.users")
                .addPackage("org.jboss.dashboard.database")
                .addPackage("org.jboss.dashboard.database.hibernate")
                .addPackage("org.jboss.dashboard.log")
                .addPackage("org.jboss.dashboard.profiler")
                .addPackage("org.jboss.dashboard.scheduler")
                .addPackage("org.jboss.dashboard.error")
                .addPackage("org.jboss.dashboard.filesystem")
                .addPackage("org.jboss.dashboard.command")
                .addPackage("org.jboss.dashboard.dataset")
                .addPackage("org.jboss.dashboard.displayer")
                .addPackage("org.jboss.dashboard.displayer.annotation")
                .addPackage("org.jboss.dashboard.displayer.chart")
                .addPackage("org.jboss.dashboard.displayer.table")
                .addPackage("org.jboss.dashboard.displayer.nvd3")
                .addPackage("org.jboss.dashboard.displayer.gauge")
                .addPackage("org.jboss.dashboard.displayer.ofc2")
                .addPackage("org.jboss.dashboard.domain")
                .addPackage("org.jboss.dashboard.domain.date")
                .addPackage("org.jboss.dashboard.domain.label")
                .addPackage("org.jboss.dashboard.domain.numeric")
                .addPackage("org.jboss.dashboard.export")
                .addPackage("org.jboss.dashboard.function")
                .addPackage("org.jboss.dashboard.kpi")
                .addPackage("org.jboss.dashboard.provider")
                .addPackage("org.jboss.dashboard.provider.csv")
                .addPackage("org.jboss.dashboard.provider.sql")
                .addPackage("org.jboss.dashboard.annotation")
                .addPackage("org.jboss.dashboard.annotation.config")
                .addPackage("org.jboss.dashboard.pojo")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    protected BeanManager beanManager;

    @Inject
    protected DataProviderManager dataProviderManager;

    @Inject
    protected DataSetManager dataSetManager;

    @Inject
    protected ScalarFunctionManager scalarFunctionManager;

    @Inject
    protected DataDisplayerManager dataDisplayerManager;

    protected DataSet dataSet;
    protected DataProviderType dataPoviderType;

    @Before
    public void setUp() throws Exception {
        CDIBeanLocator.beanManager = beanManager;

        dataPoviderType = dataProviderManager.getProviderTypeByUid(CSVDataProviderType.UID);
        DataProvider dataProvider = dataProviderManager.createDataProvider();
        CSVDataLoader csvDataLoader = (CSVDataLoader) dataPoviderType.createDataLoader();
        dataProvider.setDataLoader(csvDataLoader);

        InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("data.csv");
        dataSet = csvDataLoader.load(dataProvider, dataStream);
        dataSetManager.registerDataSet(dataProvider, dataSet);
    }

    @Test
    public void buildDataSet() {
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

        // Set the scalar function to apply for the calculations of the chart values.
        ScalarFunction scalarFunction = scalarFunctionManager.getScalarFunctionByCode(SumFunction.CODE);
        barChartDisplayer.setRangeScalarFunction(scalarFunction);

        // Calculate the data set to display
        DataSet xyDataSet = barChartDisplayer.buildXYDataSet();
        assertThat(xyDataSet.getProperties().length==2);
        assertThat(xyDataSet.getRowCount()==5);
        
        // Check all the calculations done.
        Locale locale = Locale.ENGLISH;
        DataFormatterRegistry dataFormatterRegistry = DataFormatterRegistry.lookup();
        DataPropertyFormatter formatterDept = dataFormatterRegistry.getPropertyFormatter("department");
        DataPropertyFormatter formatterAmount = dataFormatterRegistry.getPropertyFormatter("amount");

        // First row: the departments
        String value = formatterDept.formatValue(propDept, xyDataSet.getValueAt(0, 0), locale);
        assertThat(value).isEqualTo("Engineering");
        value = formatterDept.formatValue(propDept, xyDataSet.getValueAt(1, 0), locale);
        assertThat(value).isEqualTo("Services");
        value = formatterDept.formatValue(propDept, xyDataSet.getValueAt(2, 0), locale);
        assertThat(value).isEqualTo("Sales");
        value = formatterDept.formatValue(propDept, xyDataSet.getValueAt(3, 0), locale);
        assertThat(value).isEqualTo("Support");
        value = formatterDept.formatValue(propDept, xyDataSet.getValueAt(4, 0), locale);
        assertThat(value).isEqualTo("Management");

        // Second row: the values
        value = formatterAmount.formatValue(propAmount, xyDataSet.getValueAt(0, 1), locale);
        assertThat(value).isEqualTo("7,650.162");
        value = formatterAmount.formatValue(propAmount, xyDataSet.getValueAt(1, 1), locale);
        assertThat(value).isEqualTo("2,504.5");
        value = formatterAmount.formatValue(propAmount, xyDataSet.getValueAt(2, 1), locale);
        assertThat(value).isEqualTo("3,213.53");
        value = formatterAmount.formatValue(propAmount, xyDataSet.getValueAt(3, 1), locale);
        assertThat(value).isEqualTo("3,345.6");
        value = formatterAmount.formatValue(propAmount, xyDataSet.getValueAt(4, 1), locale);
        assertThat(value).isEqualTo("6,017.47");
    }
}
