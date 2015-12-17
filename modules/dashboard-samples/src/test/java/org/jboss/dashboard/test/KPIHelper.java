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
package org.jboss.dashboard.test;

import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.displayer.table.TableDisplayer;
import org.jboss.dashboard.kpi.KPI;

public class KPIHelper {

    public static DataSet getDataSet(KPI kpi) {
        DataDisplayer displayer = kpi.getDataDisplayer();
        if (displayer instanceof AbstractChartDisplayer) {
            AbstractChartDisplayer cdisplayer = (AbstractChartDisplayer) kpi.getDataDisplayer();
            return cdisplayer.buildXYDataSet();
        }
        if (displayer instanceof TableDisplayer) {
            TableDisplayer tdisplayer = (TableDisplayer) kpi.getDataDisplayer();
            return tdisplayer.getTable().getDataSet();
        }
        return null;
    }
}
