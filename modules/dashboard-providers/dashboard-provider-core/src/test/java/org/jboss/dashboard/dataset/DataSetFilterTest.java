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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.provider.DataFilter;
import org.jboss.dashboard.provider.DefaultDataSetFilter;
import org.jboss.dashboard.test.ShrinkWrapHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.dashboard.dataset.Assertions.*;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class DataSetFilterTest {

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
    public void testFilterBySingleNumber() throws Exception {
        DefaultDataSetFilter filter = new DefaultDataSetFilter(dataSet);
        filter.addProperty("id", null, false, null, false, Arrays.asList(25), DataFilter.ALLOW_ANY);
        DataSet result = dataSet.filter(filter);
        assertDataSetValue(result, 0, 0, "25");
    }

    @Test
    public void testFilterBySingleString() throws Exception {
        DefaultDataSetFilter filter = new DefaultDataSetFilter(dataSet);
        filter.addProperty("employee", null, false, null, false, Arrays.asList("Jerri Preble"), DataFilter.ALLOW_ANY);
        DataSet result = dataSet.filter(filter);
        assertDataSetValue(result, 0, 0, "27");
        assertDataSetValue(result, 1, 0, "28");
    }

    @Test
    public void testFilterBySingleDate() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm");
        Date date = df.parse("07/23/11 12:00");
        DefaultDataSetFilter filter = new DefaultDataSetFilter(dataSet);
        filter.addProperty("date", null, false, null, false, Arrays.asList(date), DataFilter.ALLOW_ANY);
        DataSet result = dataSet.filter(filter);
        assertDataSetValue(result, 0, 0, "19");
    }

    @Test
    public void testFilterByDateInterval() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm");
        Date from = df.parse("01/01/11 00:00");
        Date to = df.parse("12/31/11 23:59");
        DefaultDataSetFilter filter = new DefaultDataSetFilter(dataSet);
        filter.addProperty("date", from, true, to, true, null, DataFilter.ALLOW_ANY);
        DataSet result = dataSet.filter(filter);
        assertThat(result.getRowCount()).isEqualTo(11);
        assertDataSetValue(result, 0, 0, "16");
    }

    @Test
    public void testFilterByDateInterval2() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm");
        DefaultDataSetFilter filter = new DefaultDataSetFilter(dataSet);
        filter.addProperty("date", "*/11 *");
        DataSet result = dataSet.filter(filter);
        assertThat(result.getRowCount()).isEqualTo(11);
        assertDataSetValue(result, 0, 0, "16");
    }


    @Test
    public void testFilterByWildcard() throws Exception {
        DefaultDataSetFilter filter = new DefaultDataSetFilter(dataSet);
        filter.addProperty("city", "B*");
        filter.addProperty("department", "*Services*");
        DataSet result = dataSet.filter(filter);
        assertThat(result.getRowCount()).isEqualTo(3);
        assertDataSetValue(result, 0, 0, "4");
    }

    @Test
    public void testFilterByNumberInterval() throws Exception {
        DefaultDataSetFilter filter = new DefaultDataSetFilter(dataSet);
        filter.addProperty("amount", 1000, false, null, false, null, DataFilter.ALLOW_ANY);
        DataSet result = dataSet.filter(filter);
        assertThat(result.getRowCount()).isEqualTo(2);
        assertDataSetValue(result, 0, 0, "2");
    }

    @Test
    public void testFilterMultiple() throws Exception {
        DefaultDataSetFilter filter = new DefaultDataSetFilter(dataSet);
        filter.addProperty("amount", null, false, 500, false, null, DataFilter.ALLOW_ANY);
        filter.addProperty("date", "*/11 *");
        filter.addProperty("department", "Engineering");
        DataSet result = dataSet.filter(filter);
        assertThat(result.getRowCount()).isEqualTo(7);
    }
}