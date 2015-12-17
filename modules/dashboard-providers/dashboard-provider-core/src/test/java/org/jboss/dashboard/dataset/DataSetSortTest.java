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
package org.jboss.dashboard.dataset;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.test.ShrinkWrapHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.dashboard.dataset.Assertions.*;

@RunWith(Arquillian.class)
public class DataSetSortTest {

    @Deployment
    public static Archive<?> createTestArchive()  {
        return ShrinkWrapHelper.createJavaArchive()
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    protected BeanManager beanManager;

    protected DataSet dataSet;

    @Before
    public void setUp() throws Exception {
        CDIBeanLocator.beanManager = beanManager;
        dataSet = RawDataSetSamples.EXPENSE_REPORTS.toDataSet();
    }

    @Test
    public void testSortByNumber() {
        // Sort by amount and check the expected results.
        DataSetComparator comp = new DataSetComparator();
        comp.addSortCriteria("5", DataSetComparator.ORDER_DESCENDING);
        DataSet result  = dataSet.sort(comp);
        assertDataSetValue(result, 0, 0, "2");
        assertDataSetValue(result, 1, 0, "15");
        assertDataSetValue(result, 2, 0, "20");
    }

    @Test
    public void testSortByDate() {
        // Sort by date and check the expected results.
        DataSetComparator comp = new DataSetComparator();
        comp.addSortCriteria("4", DataSetComparator.ORDER_ASCENDING);
        DataSet result  = dataSet.sort(comp);
        assertDataSetValue(result, 0, 0, "50");

    }

    @Test
    public void testSortCombined() {
        // Sort by dept, author, amount and check the expected results.
        DataSetComparator comp = new DataSetComparator();
        comp.addSortCriteria("2", DataSetComparator.ORDER_ASCENDING);
        comp.addSortCriteria("3", DataSetComparator.ORDER_ASCENDING);
        comp.addSortCriteria("5", DataSetComparator.ORDER_ASCENDING);
        DataSet result  = dataSet.sort(comp);
        assertDataSetValue(result, 0, 0, "46");

    }
}