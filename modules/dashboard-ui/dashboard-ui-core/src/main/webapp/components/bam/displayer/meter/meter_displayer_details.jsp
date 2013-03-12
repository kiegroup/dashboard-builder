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
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="org.jboss.dashboard.displayer.chart.MeterChartDisplayer" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.MeterChartEditor" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    MeterChartDisplayer meterDisplayer = (MeterChartDisplayer) request.getAttribute("meterDisplayer");
    if (meterDisplayer.getType().equals("meter")) {
%>
        <mvc:include page="meter_details.jsp" flush="true" />
<%
    } else if (meterDisplayer.getType().equals("thermometer")) {
%>
        <mvc:include page="thermometer_details.jsp" flush="true" />
<%
    } if (meterDisplayer.getType().equals("dial")) {
%>
        <mvc:include page="dial_details.jsp" flush="true" />
<%
    }
%>
    <tr>
        <td align="center" colspan="2" style="padding-bottom:10px; padding-top:10px">
            <input type="hidden" name="<%=MeterChartEditor.METER_SAVE_BUTTON_PRESSED%>" id="<factory:encode name="updateMeterDetails"/>" value="false">
            <input class= "skn-button" type="button" value="Guardar" onclick="document.getElementById('<factory:encode name="updateMeterDetails"/>').value='true'; submitAjaxForm(this.form);">
            <input class= "skn-button" type="button" value="Cancelar" onclick="window.<factory:encode name="editMeterProperties"/>(); return false;">
        </td>
    </tr>