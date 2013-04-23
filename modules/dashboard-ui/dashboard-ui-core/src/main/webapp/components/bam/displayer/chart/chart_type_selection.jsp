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
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.components.AbstractChartDisplayerEditor" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractChartDisplayer" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayerRenderer" %>
<%@ page import="java.util.List" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayerFeature" %>
<i18n:bundle baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    Locale locale = LocaleManager.currentLocale();
    AbstractChartDisplayerEditor editor = (AbstractChartDisplayerEditor) request.getAttribute("editor");
    AbstractChartDisplayer displayer = (AbstractChartDisplayer) editor.getDataDisplayer();
    DataDisplayerRenderer renderer = displayer.getDataDisplayerRenderer();
    List<String> chartTypes = renderer.getAvailableChartTypes(displayer);
    if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_CHART_TYPE)) {
%>
<tr>
    <td align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "type"%>'>!!Type</i18n:message>:
    </td>
    <td align="left">
        <select title="<%= renderer.getChartTypeDescription(displayer.getType(), locale) %>" name='chartType' id='<factory:encode name="chartType"/>'
          class='skn-input' style="width:95px;"
          onChange="return bam_kpiedit_submitProperties(this);"
        >
        <%
            for (String type : chartTypes) {
                String selected = "";
                String typeDescr = renderer.getChartTypeDescription(type, locale);
                if (type.equals(displayer.getType())) selected = "selected";
        %>
        <option title="<%= typeDescr %>" value="<%= type %>" <%= selected %>><%= typeDescr %></option>
        <%
            }
        %>
        </select>
    </td>
</tr>
<%  } %>
