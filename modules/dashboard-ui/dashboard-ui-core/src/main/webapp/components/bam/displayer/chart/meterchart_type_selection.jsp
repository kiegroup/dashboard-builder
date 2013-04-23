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
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"  %>

<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.components.AbstractChartDisplayerEditor" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayerRenderer" %>
<%@ page import="java.util.List" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayerFeature" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.MeterChartEditor" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.jboss.dashboard.displayer.chart.MeterChartDisplayer" %>
<i18n:bundle baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    AbstractChartDisplayerEditor editor = (AbstractChartDisplayerEditor) request.getAttribute("editor");
    MeterChartDisplayer displayer = (MeterChartDisplayer) editor.getDataDisplayer();
    DataDisplayerRenderer renderer = displayer.getDataDisplayerRenderer();
    Locale locale = LocaleManager.currentLocale();
    ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.messages", locale);
    if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_CHART_TYPE)) {
        List<String> chartTypes = renderer.getAvailableChartTypes(displayer);
%>
<tr>
    <td align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "type"%>'>!!Type</i18n:message>:
    </td>
    <td align="left">
        <select title="<%= renderer.getChartTypeDescription(displayer.getType(), locale) %>"
                name="chartType" id='<factory:encode name="chartType"/>'
                class="skn-input" style="width:95px;"
                onChange="return bam_kpiedit_submitProperties(this);"
                >
        <%
            for (String type : chartTypes) {
                String selected = "";
                String typeDescr = renderer.getChartTypeDescription(type, locale);
                if (type.equals(displayer.getType())) selected = "selected";
        %>
            <option title="<%= typeDescr %>" value="<%= type %>" <%= selected %>><%= typeDescr %></option>
        <%  } %>
        </select>
        &nbsp;
        <a style="border:0" href="#" onclick='window.<factory:encode name="editMeterProperties"/>(); return false;'>
        <img src='<static:image relativePath="general/16x16/ico-edit.png"/>' border="0"
            title="<i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "edit_meter_properties"%>'>!!Editar meter properties</i18n:message>"></a>
        <div id='<factory:encode name="idMeterProperties"/>' align="center" style="width:400px;height:300px; overflow:-moz-scrollbars-horizontal; overflow-x:hidden;overflow-y:auto; display:none;position:absolute;vertical-align:middle;z-index:11;">
        <table width="100%" align="left" border="0" cellpadding="4"  bgcolor="#FFFFFF" cellspacing="1" class="skn-table_border">
            <tr class="skn-table_header">
                <td colspan="2">
                    <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "edit_meter_properties"%>'>!!Editar meter properties</i18n:message>
                </td>
            </tr>
            <%  request.setAttribute("meterDisplayer", displayer); %>
            <mvc:include page="../meter/meter_displayer_details.jsp" flush="true" />
            <% request.removeAttribute("meterDisplayer"); %>
        </table>
        </div>
        <script defer="true">
            window.<factory:encode name="editMeterProperties"/> = function() {
                var element = document.getElementById('<factory:encode name="idMeterProperties"/>');
                if (element.style.display == "none") element.style.display = "block";
                else element.style.display = "none";
            }
        </script>
    </td>
</tr>
<%  } %>
<tr>
    <td align="left">
        <i18n:message key="meterChartDisplayer.positionType">!!Position</i18n:message>
    </td>
    <td>
        <select name="positionType" id='<factory:encode name="positionType"/>' class="skn-input" style="width:95px;" onChange="return bam_kpiedit_submitProperties(this);">
        <%
            String[] positionType = new String[] {"vertical", "horizontal"};
            String[] positionTypeDescrip = new String[] {
                    i18n.getString(MeterChartEditor.I18N_METER + "positionType." + "vertical"),
                    i18n.getString(MeterChartEditor.I18N_METER + "positionType." + "horizontal")};

            for (int i = 0; i < positionType.length; i++) {
                String selected = "";
                if (positionType[i].equals(displayer.getPositionType())) selected = "selected";
        %>
        <option value="<%= positionType[i] %>" <%= selected %>>
            <%= positionTypeDescrip[i] %>
        </option>
        <%
            }
        %>
        </select>
    </td>
</tr>
