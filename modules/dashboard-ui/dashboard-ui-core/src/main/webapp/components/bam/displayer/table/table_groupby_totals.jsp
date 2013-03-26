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
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.displayer.table.DataSetTable" %>
<%@ page import="org.jboss.dashboard.ui.components.table.DataSetTableHandler" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.jboss.dashboard.provider.*" %>
<%@ page import="org.jboss.dashboard.function.ScalarFunction" %>
<%@ page import="org.jboss.dashboard.DataDisplayerServices" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jboss.dashboard.displayer.table.messages" locale="<%= LocaleManager.currentLocale() %>" />
<%
    DataSetTableHandler tableHandler = (DataSetTableHandler) request.getAttribute("tableHandler");
    DataSetTable table = (DataSetTable) tableHandler.getTable();
    Locale locale = LocaleManager.currentLocale();
    if (table.showGroupByTotals()) {
%>
    <tr>
<%
        // Get the total function value for each table column. Avoid the groupby column(s).
        for (int columnIndex=0; columnIndex<table.getColumnCount(); columnIndex++) {
            if (table.isNonGroupByColumn(columnIndex)) {
                DataProperty columnProperty = table.getOriginalDataProperty(columnIndex);
                ScalarFunction columnFunction = DataDisplayerServices.lookup().getScalarFunctionManager().getScalarFunctionByCode(table.getGroupByFunctionCode(columnIndex));
                double value = columnFunction.scalar(columnProperty.getValues());
                DataPropertyFormatter formatter = DataFormatterRegistry.lookup().getPropertyFormatter(columnProperty.getPropertyId());
                String scalar = formatter.formatValue(columnProperty, value, locale);
%>
        <td title="<%= scalar %>" height="15" nowrap>
            <div style="<%= table.getGroupByTotalsHtmlStyle() %> height:18px; font:100-family: variant: font-style:italic; overflow:hidden; vertical-align:middle" title="<%= scalar %>">
            <%= StringEscapeUtils.escapeHtml(scalar) %></div>
        </td>
            <% } else { %>
        <td title="<i18n:message key="tableDisplayer.groupByTotal" />" height="15" nowrap>
            <div style="<%= table.getGroupByTotalsHtmlStyle() %> height:18px; font:100-family: variant: font-style:italic; overflow:hidden; vertical-align:middle" title="<i18n:message key="tableDisplayer.groupByTotal" />">
            <i18n:message key="tableDisplayer.groupByTotal" /></div>
        </td>
    <% }} %>
    </tr>
<% } %>
