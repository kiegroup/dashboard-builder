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
<%@ taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.UIBeanLocator" %>
<%@ page import="org.jboss.dashboard.ui.components.DataDisplayerViewer" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.MapChartEditor" %>
<%@ page import="org.jboss.dashboard.displayer.map.MapDisplayer" %>
<i18n:bundle baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    org.jboss.dashboard.ui.components.chart.MapChartEditor editor = (org.jboss.dashboard.ui.components.chart.MapChartEditor) UIBeanLocator.lookup().getCurrentBean(request);
    request.setAttribute("editor", editor);

    MapDisplayer displayer = (MapDisplayer) editor.getDataDisplayer();
    DataDisplayerViewer viewer = UIBeanLocator.lookup().getViewer(displayer);
%>
<table width="100%" cellspacing="2">
    <tr>
        <!-- Include the properties -->
        <td align="right" valign="top">
            <table cellspacing="2" width="250px">
                <!-- Domain and axis selection -->
                <mvc:include page="axis_selection.jsp"  flush="true" />
                <!-- Select the renderer -->
                <mvc:include page="renderer_selection.jsp"  flush="true" />
                <!-- Select the type of the bar -->
                <mvc:include page="chart_type_selection.jsp"  flush="true" />
                <!-- Include the width, height, etc properties -->
                <mvc:include page="generic_properties.jsp"  flush="true" />
            </table>
        </td>
        <td valign="top">
            <!-- Include the viewer  -->
            <factory:useComponent bean="<%= viewer %>"/>
        </td>
    </tr>
</table>
