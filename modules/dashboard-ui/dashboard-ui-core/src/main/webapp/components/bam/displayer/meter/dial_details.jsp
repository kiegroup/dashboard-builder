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
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.MeterChartEditor" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    Locale locale = LocaleManager.currentLocale();
    MeterChartDisplayer meterDisplayer = (MeterChartDisplayer) request.getAttribute("meterDisplayer");
    NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
%>
<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "dial." + "pointerType"%>'>!!pointerType</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "dial." + "pointerType"%>'>!!pointerType</i18n:message>
        </div>
    </td>
    <td>
        <select name="pointerType" id="<factory:encode name="pointerType"/>" class="skn-input" style="width:70px;">
        <%
            String[] pointerType = new String[] {"pin", "pointer"};
            // i18n
            ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.messages", LocaleManager.currentLocale());
            String[] pointerTypeDescrip = new String[] {
                    i18n.getString(MeterChartEditor.I18N_METER + "dial." + "pointerType." + "pin"),
                    i18n.getString(MeterChartEditor.I18N_METER + "dial." + "pointerType." + "pointer")};
            for (int i = 0; i < pointerType.length; i++) {
                String selected = "";
                if (pointerType[i].equals(meterDisplayer.getPointerType())) selected = "selected";
        %>
                <option value="<%= pointerType[i] %>" <%= selected %>>
                    <%= pointerTypeDescrip[i] %>
                </option>
        <%
            }
        %>
        </select>
    </td>
</tr>
<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "dial." + "dialLowerBound"%>'>!!dialLowerBound</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "dial." + "dialLowerBound"%>'>!!dialLowerBound</i18n:message>
        </div>
    </td>
    <td>
        <input class="skn-input" name="dialLowerBound" type="text" value="<%= numberFormat.format(meterDisplayer.getDialLowerBound()) %>">
    </td>
</tr>
<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "dial." + "dialUpperBound"%>'>!!dialUpperBound</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "dial." + "dialUpperBound"%>'>!!dialUpperBound</i18n:message>
        </div>
    </td>
    <td>
        <input class="skn-input" name="dialUpperBound" type="text" value="<%= numberFormat.format(meterDisplayer.getDialUpperBound()) %>">
    </td>
</tr>
<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "dial." + "maxTicks"%>'>!!maxTicks</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "dial." + "maxTicks"%>'>!!maxTicks</i18n:message>
        </div>
    </td>
    <td>
        <input class="skn-input" name="maxTicks" type="text" value="<%= numberFormat.format(meterDisplayer.getMaxTicks()) %>">
    </td>
</tr>
<tr>
    <td width="160px" height="15" nowrap>
        <div style="width:160px; height:18px; text-align:right; overflow:hidden; vertical-align:middle"
             title="<i18n:message key='<%= MeterChartEditor.I18N_METER + "dial." + "minorTickCount"%>'>!!minorTickCount</i18n:message>">
                <i18n:message key='<%= MeterChartEditor.I18N_METER + "dial." + "minorTickCount"%>'>!!minorTickCount</i18n:message>
        </div>
    </td>
    <td>
        <input class="skn-input" name="minorTickCount" type="text" value="<%= meterDisplayer.getMinorTickCount() %>">
    </td>
</tr>