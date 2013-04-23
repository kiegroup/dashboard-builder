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
<%@ page import="org.jboss.dashboard.ui.panel.kpi.KPIDriver" %>
<%@ page import="org.jboss.dashboard.DataDisplayerServices" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.jboss.dashboard.provider.DataProvider" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<i18n:bundle baseName="org.jboss.dashboard.ui.panel.kpi.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<form method="post"  id="<panel:encode name="createKPI"/>" action="<panel:link action="createKPI"/>">
<panel:hidden action="createKPI" />
  <table border="0" style="margin:0px;" cellspacing="0" width="650px">
    <tr>
        <td>
            <table align="center" width="100%" cellspacing="0" cellpadding="4" border="0">
                <tr style="display:table-row; width:12px;">
                    <td class="skn-table_border">
                        <div style="vertical-align:middle; text-align:left;" class="skn-title3">
                            <i18n:message key='<%= KPIDriver.I18N_PREFFIX + "dataProviderSelectionTitle" %>'>!!!Seleccion de provider</i18n:message>
                       </div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>

    <tr>
        <td>
            <table border="0" cellpadding="0" cellspacing="10">
                <tr>
                    <td>
                        <%
                            Set dataProviders = DataDisplayerServices.lookup().getDataProviderManager().getAllDataProviders();
                            if (dataProviders.isEmpty()) {
                        %>
                            <span class="skn-error"><i18n:message key='<%=KPIDriver.I18N_PREFFIX + "noProviders"%>'>!!No hay providers</i18n:message>
                        <%
                            } else {
                        %>
                        <i18n:message key='<%=KPIDriver.I18N_PREFFIX + "dataProviderSelection"%>'>!!Seleccione la instancia</i18n:message>:&nbsp;
                        <select name=initialProvider id="<panel:encode name="initialProvider"/>" width="20" class="skn-input"
                                onChange="submitAjaxForm(this.form); return false;">
                            <option value="-1" selected>
                                <i18n:message key='<%= KPIDriver.I18N_PREFFIX + "chooseDataProviderInstance" %>'>!!!Escoger provider</i18n:message>
                            </option>
                        <%
                                Iterator dpi = dataProviders.iterator();
                                while (dpi.hasNext()) {
                                    DataProvider dataProvider = (DataProvider) dpi.next();
                                    String providerDescrip = dataProvider.getDescription(LocaleManager.currentLocale());
                        %>
                                    <option value="<%= dataProvider.getCode() %>">
                                        <%= providerDescrip %>
                                    </option>
                        <%
                                }
                            }
                        %>
                        </select>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    </table>
</form>
<script defer="true">
    // Bug 3116: Disable AJAX to force the repaint of the panel icon bar.
    //setAjax("<panel:encode name="createKPI"/>");
</script>