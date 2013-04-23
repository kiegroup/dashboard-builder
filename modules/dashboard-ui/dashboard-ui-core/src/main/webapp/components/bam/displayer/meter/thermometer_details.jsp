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
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.displayer.chart.MeterChartDisplayer" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.MeterChartEditor" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    Locale locale = LocaleManager.currentLocale();
    MeterChartDisplayer meterDisplayer = (MeterChartDisplayer) request.getAttribute("meterDisplayer");
    NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
%>
<!-- Lower bound -->
<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "thermometer." + "thermoLowerBound"%>'>!!thermoLowerBound</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "thermometer." + "thermoLowerBound"%>'>!!thermoLowerBound</i18n:message>
        </div>
    </td>
    <td>
        <input class="skn-input" name="thermoLowerBound" type="text" value="<%= numberFormat.format(meterDisplayer.getThermoLowerBound()) %>">
    </td>
</tr>
<!-- Critical and warning thresholds -->
<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "warningThreshold"%>'>!!Umbral warning</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "warningThreshold"%>'>!!Intervalo warning</i18n:message>
        </div>
    </td>
    <td>
        <input class="skn-input" name="thermoWarningThreshold" type="text" value="<%= numberFormat.format(meterDisplayer.getWarningThermoThreshold()) %>">
    </td>
</tr>
<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "criticalThreshold"%>'>!!Umbral critico</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "criticalThreshold"%>'>!!Umbral critico</i18n:message>
        </div>
    </td>
    <td>
        <input class="skn-input" name="thermoCriticalThreshold" type="text" value="<%= numberFormat.format(meterDisplayer.getCriticalThermoThreshold()) %>">
    </td>
</tr>
<!-- Upper bound -->
<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "thermometer." + "thermoUpperBound"%>'>!!thermoUpperBound</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "thermometer." + "thermoUpperBound"%>'>!!thermoUpperBound</i18n:message>
        </div>
    </td>
    <td>
        <input class="skn-input" name="thermoUpperBound" type="text" value="<%= numberFormat.format(meterDisplayer.getThermoUpperBound()) %>">
    </td>
</tr>