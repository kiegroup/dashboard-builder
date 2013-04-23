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
<i18n:bundle baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    Locale locale = LocaleManager.currentLocale();
    AbstractChartDisplayerEditor editor = (AbstractChartDisplayerEditor) request.getAttribute("editor");
    AbstractChartDisplayer displayer = (AbstractChartDisplayer) editor.getDataDisplayer();
    List<DataDisplayerRenderer> supportedRenderers = displayer.getDataDisplayerType().getSupportedRenderers();
    DataDisplayerRenderer currentRenderer = displayer.getDataDisplayerRenderer();
%>
<tr>
    <td align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "renderer"%>'>!!Renderer</i18n:message>:
    </td>
    <td align="left">
        <select title="<%= currentRenderer.getDescription(locale) %>" name='rendererUid' id='<factory:encode name="renderer"/>'
                class='skn-input' style="width:95px;"
                onChange="return bam_kpiedit_submitProperties(this);"
                >
            <%
                for (DataDisplayerRenderer renderer : supportedRenderers) {
                    String selected = "";
                    String descr = renderer.getDescription(locale);
                    if (renderer.equals(currentRenderer)) selected = "selected";
            %>
            <option title="<%= descr %>" value="<%= renderer.getUid() %>" <%= selected %>><%= descr %></option>
            <%
                }
            %>
        </select>
    </td>
</tr>
