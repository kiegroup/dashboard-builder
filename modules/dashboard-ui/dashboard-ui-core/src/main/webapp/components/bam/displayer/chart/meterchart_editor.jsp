<%--

    Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.

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
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ page import="org.jboss.dashboard.ui.components.chart.MeterChartEditor"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.displayer.chart.MeterChartDisplayer" %>
<%@ page import="org.jboss.dashboard.ui.components.DataDisplayerViewer" %>
<%@ page import="org.jboss.dashboard.ui.UIBeanLocator" %>
<i18n:bundle baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    MeterChartEditor editor = (MeterChartEditor) UIBeanLocator.lookup().getCurrentBean(request);
    MeterChartDisplayer displayer = (MeterChartDisplayer) editor.getDataDisplayer();
    DataDisplayerViewer viewer = UIBeanLocator.lookup().getViewer(displayer);
    request.setAttribute("editor", editor);
%>
<table cellspacing="2">
    <tr>
        <!-- Include the properties -->
        <td valign="top">
            <table cellspacing="2"width="250px">
                <!-- Domain and axis selection -->
                <mvc:include page="axis_selection.jsp"  flush="true" />
                <!-- Select the renderer -->
                <mvc:include page="renderer_selection.jsp"  flush="true" />
                <!-- Select the type of the meter -->
                <mvc:include page="meterchart_type_selection.jsp"  flush="true" />
                <!-- Include the width, height, etc properties -->
                <mvc:include page="generic_properties.jsp"  flush="true" />
                 <!-- Submit button -->
                 <%--mvc:include page="../../kpi_submit.jsp"  flush="true" /--%>
            </table>
        </td>
        <!-- Include the viewer -->
        <td valign="top">
            <factory:useComponent bean="<%= viewer %>"/>
        </td>
    </tr>
</table>
