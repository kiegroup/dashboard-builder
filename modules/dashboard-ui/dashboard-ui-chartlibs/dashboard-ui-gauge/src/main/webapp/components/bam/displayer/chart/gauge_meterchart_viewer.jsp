<%--

    Copyright (C) 2012 JBoss Inc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page import="org.jboss.dashboard.factory.Factory"%>
<%@ page import="org.jboss.dashboard.displayer.chart.MeterChartDisplayer" %>
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractChartDisplayer" %>
<%@ page import="org.jboss.dashboard.ui.components.AbstractChartDisplayerEditor" %>
<%@ page import="org.jboss.dashboard.dataset.DataSet" %>
<%@ page import="java.util.List" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.GaugeMeterChartViewer" %>
<%@ page import="org.jboss.dashboard.function.ScalarFunction" %>
<%@ page import="org.jboss.dashboard.domain.Interval" %>
<%@ page import="org.jboss.dashboard.DataDisplayerServices" %>
<%@ page import="org.jboss.dashboard.function.MinFunction" %>
<%@ page import="org.jboss.dashboard.function.MaxFunction" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.DecimalFormat" %>
<%@taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<panel:defineObjects/>
<%
    Locale locale = LocaleManager.currentLocale();
    GaugeMeterChartViewer viewer = (GaugeMeterChartViewer) Factory.lookup("org.jboss.dashboard.ui.components.MeterChartViewer_gauge");
    MeterChartDisplayer displayer = (MeterChartDisplayer) viewer.getDataDisplayer();
    AbstractChartDisplayerEditor editor = (AbstractChartDisplayerEditor) request.getAttribute("editor");

    DataSet xyDataSet =  displayer.buildXYDataSet();
    List intervals = xyDataSet.getProperties()[0].getValues();
    List yvalues = xyDataSet.getValuesAt(1);
    DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
    numberFormat.setGroupingUsed(false);
    if (displayer.isAxisInteger()) {
        numberFormat.setMaximumFractionDigits(0);
    }

    // Set the minimum and maximum dataset values to the meter chart displayer.
    ScalarFunction minFunction = DataDisplayerServices.lookup().getScalarFunctionManager().getScalarFunctionByCode(MinFunction.CODE);
    ScalarFunction maxFunction = DataDisplayerServices.lookup().getScalarFunctionManager().getScalarFunctionByCode(MaxFunction.CODE);
    double minDsValue = minFunction.scalar(yvalues);
    double maxDsValue = maxFunction.scalar(yvalues);
    double minValue = displayer.getMinValue();
    double criticalValue = displayer.getCriticalThreshold();
    double warningValue = displayer.getWarningThreshold();
    double maxValue = displayer.getMaxValue();
    if (minValue > minDsValue) minValue = minDsValue;
    if (maxValue < maxDsValue) maxValue = maxDsValue;

    int suffix = viewer.hashCode();
    if (suffix < 0) suffix *= -1;

    // Calculate colspan correctly
    int nCols;
    if( displayer.getPositionType().equals("horizontal") ) {
       nCols = intervals.size();
       if(  editor != null && nCols > 2 ) nCols = 2;
    }
    else {
       nCols=1;
    }
%>
<table class="skn-chart-table" align="<%= displayer.getGraphicAlign() %>">
<% if( displayer.isShowTitle() && displayer.getTitle() != null) { %>
    <tr>
       <td colspan="<%=nCols%>">
            <div id="title<%="gauge" + suffix%>" class="skn-chart-title"><%=displayer.getTitle()%></div>
        </td>
    </tr>
<% } %>

    <% if (displayer.getPositionType().equals("horizontal")) { %>
    <tr>
        <%
            for (int i = 0; i < intervals.size(); i++) {
                Interval interval = (Interval) intervals.get(i);
                String chartId = "gauge" + suffix + i;
        %>
        <td>
               <% request.setAttribute("meterValue", numberFormat.format(yvalues.get(i))); %>
                <% request.setAttribute("meterMin", numberFormat.format(minValue)); %>
                <% request.setAttribute("meterMax", numberFormat.format(maxValue)); %>
                <% request.setAttribute("meterTitle", interval.getDescription(locale)); %>
                <% request.setAttribute("meterUnits", ""); %>
                <% request.setAttribute("chartId", chartId); %>
                <% request.setAttribute("chartIndex", i); %>
                <mvc:include page="gauge_chart.jsp"  flush="true" />
        </td>
        <%
               if( editor != null && i > 0 ) break; // In edit mode just display 2 gauge
            }
        %>
    </tr>
    <%
    } else {
        for (int i = 0; i < intervals.size(); i++) {
            Interval interval = (Interval) intervals.get(i);
            String chartId = "gauge" + suffix + i;
    %>
    <tr>
        <td>
                <% request.setAttribute("meterValue", numberFormat.format(yvalues.get(i))); %>
                <% request.setAttribute("meterMin", numberFormat.format(minValue)); %>
                <% request.setAttribute("meterMax", numberFormat.format(maxValue)); %>
                <% request.setAttribute("meterTitle", interval.getDescription(locale)); %>
                <% request.setAttribute("meterUnits", ""); %>
                <% request.setAttribute("chartId", chartId); %>
                <% request.setAttribute("chartIndex", i); %>
                <mvc:include page="gauge_chart.jsp"  flush="true" />
        </td>
    </tr>
    <%
            if( editor != null && i > 0) break; // In edit mode just display 2 gauge
            }
        }
    %>
</table>