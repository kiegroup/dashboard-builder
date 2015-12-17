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

import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.dataset.DataSetManager;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.DataProviderManager;
import org.jboss.dashboard.provider.DataProviderType;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
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

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class CSVDataSetTest {

    @Deployment
    public static Archive<?> createTestArchive()  {
        return ShrinkWrapHelper.createJavaArchive()
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    protected BeanManager beanManager;

    @Inject
    protected DataProviderManager dataProviderManager;

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

        InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("data.csv");
        dataSet = csvDataLoader.load(dataProvider, dataStream);
        dataSetManager.registerDataSet(dataProvider, dataSet);
    }

    @Test
    public void checkDataSet() {
        assertThat(dataSet).isNotNull();
        assertThat(dataProviderManager).isNotNull();
        assertThat(dataProviderManager.getDataProviderTypes().length > 0);
        assertThat(dataPoviderType).isNotNull();

        assertThat(dataSet.getProperties().length>0);
        assertThat(dataSet.getRowCount()==50);
    }
}
