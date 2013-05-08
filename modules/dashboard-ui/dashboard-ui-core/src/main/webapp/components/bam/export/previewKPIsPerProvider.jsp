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
<%@ page import="org.jboss.dashboard.ui.components.export.ExportHandler" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.provider.DataProvider" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.jboss.dashboard.kpi.KPI" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.components.export.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    ExportHandler exportHandler = ExportHandler.lookup();
    List<DataProvider> dataProviders = exportHandler.getSelectedDataProviders();
    List<KPI> kpis = exportHandler.getSelectedKPIs();
    if (dataProviders.isEmpty()) {
%>
<div class="skn-background_alt">
    <table width="100%">
        <tr>
            <td style="text-align:center;vertical-align:top;width: 32px;"><img src="<static:image relativePath="general/32x32/info.png"/>"></td>
            <td align="left"><i18n:message key="export.info">!!!Select the dahboards to export</i18n:message></td>
        </tr>
    </table>
</div>
<%
} else {
%>
<div class="skn-background_alt">
    <table width="100%">
        <tr>
            <td style="text-align:center;vertical-align:top;width: 32px;"><img src="<static:image relativePath="general/32x32/info.png"/>"></td>
            <td align="left"><%= dataProviders.size() %> <i18n:message key="selected.providers">!!!Data providers</i18n:message><br/>
                <%= kpis.size() %> <i18n:message key="selected.kpis">!!!Business indicators</i18n:message></td>
        </tr>
    </table>
</div>
<div class="skn-table_border" style="height:400px; overflow:auto; margin: 7px;">
    <table width="100%">
        <% for (DataProvider dp : dataProviders) { %>
        <tr>
            <td width="100%" align="left" class="skn-even_row" title="<%=dp.getDescription(LocaleManager.currentLocale())%>">
                <%=dp.getDescription(LocaleManager.currentLocale())%>
            </td>
        </tr>
        <% for (KPI kpi : exportHandler.getSelectedKPIs(dp)) { %>
        <tr>
            <td align="left"><%= kpi.getDescription(LocaleManager.currentLocale()) %></td>
        </tr>
        <% } %>
        <tr>
            <td align="left">&nbsp;</td>
        </tr>
        <% } %>
    </table>
</div>
<br/>
<table width="100%">
    <tr>
        <td align="right">
            <form action="<factory:formUrl/>"  method="post" enctype="multipart/form-data">
                <factory:handler action="exportSelectedKPIs"/>
                <input type="submit" class="skn-button" value="<i18n:message key="action.export">!!!Export</i18n:message>" />
            </form>
        </td>
        <td align="left">
            <form action="<factory:formUrl/>" id="<panel:encode name="clearForm"/>" method="post" enctype="multipart/form-data">
                <factory:handler action="clearSelectedKPIs"/>
                <input type="submit" class="skn-button" value="<i18n:message key="action.clean">!!!Clean</i18n:message>" />
            </form>
            <script defer="true">
                setAjax('<panel:encode name="clearForm"/>');
            </script>
        </td>
    </tr>
        <%
    }
%>

