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
    int dpsSize = dataProviders.size() * 20;
    int kpiSize = kpis.size() * 20;
    if (dpsSize > 200) dpsSize = 200;
    if (kpiSize > 200) kpiSize = 200;

    if (dataProviders.isEmpty()) {
%>
<div class="skn-background_alt" style="width:100%; padding-top:5px; padding-bottom:5px;">
    <table width="100%">
        <tr>
            <td style="text-align:center;vertical-align:top;width: 32px;"><img src="<static:image relativePath="general/32x32/info.png"/>"></td>
            <td align="left"><i18n:message key="export.info">!!!Seleccione los cuadros de mando a exportar</i18n:message></td>
        </tr>
    </table>
</div>
<%
    } else {
%>
<table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td align="left"><img src="<static:image relativePath="general/header_left.gif"/>"/></td>
        <td height="18" align="center" class="skn-table_header" nowrap background="<static:image relativePath="general/header_bg.gif"/>">
            <i18n:message key="export.dataproviders">!!!Proveedores de datos</i18n:message>
        </td>
        <td align="right"><img src="<static:image relativePath="general/header_right.gif"/>"/></td>
    </tr>
</table>
<div class="skn-table_border" style="height:<%= dpsSize %>px; overflow:auto;">
    <table>
        <% for (DataProvider dp : dataProviders) { %>
        <tr>
            <td title="<%=dp.getDescription(LocaleManager.currentLocale())%>"><%=dp.getDescription(LocaleManager.currentLocale())%></td>
        </tr>
        <% } %>
    </table>
</div>
<br/>
<table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td align="left"><img src="<static:image relativePath="general/header_left.gif"/>"/></td>
        <td height="18" align="center" class="skn-table_header" nowrap background="<static:image relativePath="general/header_bg.gif"/>">
            <i18n:message key="export.kpis">!!!Indicadores de negocio</i18n:message>
        </td>
        <td align="right"><img src="<static:image relativePath="general/header_right.gif"/>"/></td>
    </tr>
</table>
<div class="skn-table_border" style="height:<%= kpiSize %>px; overflow:auto;">
    <table>
        <% for (KPI kpi: kpis) { %>
        <tr>
            <td title="<%=kpi.getDescription(LocaleManager.currentLocale())%>"><%=kpi.getDescription(LocaleManager.currentLocale())%></td>
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
                <input type="submit" class="skn-button" value="<i18n:message key="action.export">!!!Exportar</i18n:message>" />
            </form>
        </td>
        <td align="left">
            <form action="<factory:formUrl/>" id="<panel:encode name="clearForm"/>" method="post" enctype="multipart/form-data">
            <factory:handler action="clearSelectedKPIs"/>
                <input type="submit" class="skn-button" value="<i18n:message key="action.clean">!!!Limpiar</i18n:message>" />
            </form>
            <script defer="true">
                setAjax('<panel:encode name="clearForm"/>');
            </script>
        </td>
    </tr>
<%
    }
%>

