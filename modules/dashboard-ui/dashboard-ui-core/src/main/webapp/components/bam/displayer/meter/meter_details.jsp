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
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.MeterChartEditor" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    Locale locale = LocaleManager.currentLocale();
    MeterChartDisplayer meterDisplayer = (MeterChartDisplayer) request.getAttribute("meterDisplayer");
    NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);%>
<!-- Minimum value -->
<tr>
    <td height="15" nowrap="nowrap" align="left"  class="skn-even_row">

                <i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "minValue"%>'>!!Valor minimo</i18n:message>:

    </td>
    <td nowrap="nowrap" align="left">
        <input class="skn-input" name="minValue" type="text" value="<%= numberFormat.format(meterDisplayer.getMinValue()) %>">
    </td>
</tr>
<!-- Critical and warning thresholds -->
<tr>
    <td height="15" nowrap="nowrap" align="left" class="skn-even_row">

                <i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "warningThreshold"%>'>!!Intervalo warning</i18n:message>:

    </td>
    <td nowrap="nowrap" align="left">
        <input class="skn-input" name="meterWarningThreshold" type="text" value="<%= numberFormat.format(meterDisplayer.getWarningThreshold()) %>">
    </td>
</tr>
<tr>
    <td height="15" nowrap="nowrap" align="left" class="skn-even_row">

                <i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "criticalThreshold"%>'>!!Umbral critico</i18n:message>:

    </td>
    <td nowrap="nowrap" align="left">
        <input class="skn-input" name="meterCriticalThreshold" type="text" value="<%= numberFormat.format(meterDisplayer.getCriticalThreshold()) %>">
    </td>
</tr>
<!-- Maximum value -->
<tr>
    <td height="15" nowrap="nowrap" align="left" class="skn-even_row">

                <i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "maxValue"%>'>!!Valor maximo</i18n:message>:

    </td>
    <td nowrap="nowrap" align="left">
        <input class="skn-input" name="maxValue" type="text" value="<%= numberFormat.format(meterDisplayer.getMaxValue()) %>">
    </td>
</tr>
<!-- Maximum number of ticks. -->
<tr>
    <td height="15" nowrap="nowrap" align="left" class="skn-even_row">

                <i18n:message key='<%= MeterChartEditor.I18N_METER + "dial." + "maxTicks"%>'>!!maxTicks</i18n:message>:
       
    </td>
    <td nowrap="nowrap" align="left">
        <input class="skn-input" name="maxMeterTicks" type="text" value="<%= numberFormat.format(meterDisplayer.getMaxMeterTicks()) %>">
    </td>
</tr>
<!-- Intervals descriptions. Hide them until the global legend will be available. -->
<!--<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "descripNormalInterval"%>'>!!Descripcion del intervalo Normal</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "descripNormalInterval"%>'>!!Descripcion del intervalo Normal</i18n:message>
        </div>
    </td>
    <td>
        <input class="skn-input" name="descripNormalInterval" type="text" value="<%= meterDisplayer.getDescripNormalInterval(locale) %>">
    </td>
</tr>
<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "descripWarningInterval"%>'>!!Descripcion del intervalo Warning</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "descripWarningInterval"%>'>!!Descripcion del intervalo Warning</i18n:message>
        </div>
    </td>
    <td>
        <input class="skn-input" name="descripWarningInterval" type="text" value="<%= meterDisplayer.getDescripWarningInterval(locale) %>">
    </td>
</tr>
<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "descripCriticalInterval"%>'>!!Descripcion del intervalo critico</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "meter." + "descripCriticalInterval"%>'>!!Descripcion del intervalo critico</i18n:message>
        </div>
    </td>
    <td>
        <input class="skn-input" name="descripCriticalInterval" type="text" value="<%= meterDisplayer.getDescripCriticalInterval(locale) %>">
    </td>
</tr>
-->