/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.jboss.dashboard.ui;

import java.util.ArrayList;
import java.util.List;

import org.jboss.dashboard.ui.components.DashboardFilterProperty;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DashboardFilterTest extends UITestBase {

    Dashboard mainDashboard;
    Dashboard childDashboard;
    DashboardFilter mainDashboardFilter;
    DashboardFilter childDashboardFilter;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mainDashboardFilter = spy(new DashboardFilter());
        childDashboardFilter = spy(new DashboardFilter());
        mainDashboard = spy(new Dashboard());
        childDashboard = spy(new Dashboard());
        mainDashboard.setDashboardFilter(mainDashboardFilter);
        childDashboard.setDashboardFilter(childDashboardFilter);
    }

    public List<DashboardFilterProperty> setUpFilter(String[] propIds, Dashboard drillDown) {
        List<DashboardFilterProperty> result = new ArrayList<DashboardFilterProperty>();
        for (String propId : propIds) {
            DashboardFilterProperty prop = spy(new DashboardFilterProperty(null, propId, mainDashboardFilter, drillDown != null ? 0L : null, false));
            prop.setVisible(true);
            result.add(prop);

            doReturn(true).when(prop).isPropertyAlive();
            doReturn(prop).when(mainDashboardFilter).getPropertyInFilterComponents(propId);
            doReturn(drillDown).when(prop).getDrillDownDashboard();
        }
        return result;
    }

    @Test
    public void testFilterOnly() throws Exception {
        setUpFilter(new String[]{"prop1", "prop2"}, null);

        DashboardFilter filterRequest = spy(new DashboardFilter());
        filterRequest.addProperty("prop1", "val1");
        filterRequest.addProperty("prop2", "val2");
        boolean drillDown = mainDashboard.filter(filterRequest);
        assertEquals(drillDown, false);

        String[] filteredIds = mainDashboardFilter.getPropertyIds();
        assertEquals(filteredIds.length, 2);
        assertEquals(filteredIds[0], "prop1");
        assertEquals(filteredIds[1], "prop2");
        verify(mainDashboard).filter();
        verify(childDashboard, never()).filter();
    }

    @Test
    public void test_BZ_1282861_Fix() throws Exception {
        setUpFilter(new String[]{"prop1", "prop2"}, childDashboard);

        DashboardFilter filterRequest = spy(new DashboardFilter());
        filterRequest.addProperty("prop1", "val1");
        filterRequest.addProperty("prop2", "val2");
        boolean drillDown = mainDashboard.filter(filterRequest);
        assertEquals(drillDown, true);

        String[] childPropIds = childDashboardFilter.getPropertyIds();
        assertEquals(childPropIds.length, 2);
        assertEquals(childPropIds[0], "prop1");
        assertEquals(childPropIds[1], "prop2");
        verify(childDashboard).filter();
    }
}
