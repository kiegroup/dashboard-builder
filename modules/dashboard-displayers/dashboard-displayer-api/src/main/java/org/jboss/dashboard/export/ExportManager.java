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
package org.jboss.dashboard.export;

import java.io.PrintWriter;

/**
 * BAM export manager.
 */
public interface ExportManager {

    ExportOptions createExportOptions();

    String format(ExportOptions options) throws Exception;

    void format(ExportOptions options, PrintWriter out, int indent) throws Exception;

    /**
     * Formats a set ok KPIs.
     * <p>Below is a sample KPI formatted as XML:<br><br>
     * <font size="-1">
     * &lt;kpi code="kpi_1234999999"&gt;<br>
     * &nbsp;&nbsp;&lt;description language="es"&gt;Nota de gastos por departamento&lt;/description&gt;<br>
     * &nbsp;&nbsp;&lt;description language="en"&gt;Expense reports by department&lt;/description&gt;<br>
     * &nbsp;&nbsp;&lt;dataprovider code="dataprovider_1234999999" /&gt;<br>
     * &nbsp;&nbsp;&lt;datadisplayer class="org.jboss.dashboard.displayer.chart.BarChartDisplayer"&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;barchartdisplayer&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;width&gt;300&lt;/width&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;height&gt;200&lt;/height&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/barchartdisplayer&gt;<br>
     * &nbsp;&nbsp;&lt;/datadisplayer&gt;<br>
     * &lt;/kpi&gt;<br><br>
     * </font>
     */
    void formatKPIs(ExportOptions options, PrintWriter out, int indent) throws Exception;

    /**
     * Formats a set of DataProviders.
     * <p>Below is a sample DataProvider formatted as XML:<br><br>
     * <font size="-1">
     * &lt;dataprovider code="dataprovider_1234999999" type="sql"&gt;<br>
     * &nbsp;&nbsp;&lt;description language="es"&gt;Notas de gastos&lt;/description&gt;<br>
     * &nbsp;&nbsp;&lt;description language="en"&gt;Expense reports&lt;/description&gt;<br>
     * &nbsp;&nbsp;&lt;sqlprovider&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;datasource&gt;local&lt;/datasource&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;query&gt;SELECT * from EXPENSE_REPORTS&lt;/query&gt;<br>
     * &nbsp;&nbsp;&lt;/sqlprovider&gt;<br>
     * &lt;/dataprovider&gt;<br>
     * </font>
     */
    void formatDataProviders(ExportOptions options, PrintWriter out, int indent) throws Exception;
}