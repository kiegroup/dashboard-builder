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
<%@ page import="org.jboss.dashboard.DataDisplayerServices" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.components.DataProviderHandler" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.jboss.dashboard.provider.DataProviderType" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    DataProviderType[] providerTypes = DataDisplayerServices.lookup().getDataProviderManager().getDataProviderTypes();
    DataProviderHandler dph = DataProviderHandler.lookup();
    String currentTypeUid = dph.getCurrentProviderTypeUid();

    if (providerTypes.length == 0) {
%>
    <i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "noDataProviderTypes"%>'>!!! No existen tipos de proveedores de datos</i18n:message>
<%
    } else {
%>
    <select class="skn-input" name="<factory:bean bean="org.jboss.dashboard.ui.components.DataProviderHandler" property="currentProviderTypeUid"/>"
    id="<factory:encode name="typesSelect"/>" <% if (!dph.isCreate()) {%> disabled <%} %>
    onchange="document.getElementById('<factory:encode name="providerTypeChangedInput"/>').value = 'true'; submitAjaxForm(this.form)">
        <option value="-1" <%if (currentTypeUid == null) {%> selected <%} %> >
            <i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "chooseDataProviderType"%>'>!!!Escoger tipo</i18n:message>
        </option>
<%
    for (int i = 0; i < providerTypes.length; i++) {
        DataProviderType providerType = providerTypes[i];
        String uid = StringEscapeUtils.escapeHtml(providerType.getUid());
        String description = StringEscapeUtils.escapeHtml(providerType.getDescription(LocaleManager.currentLocale()));
        String selected = (currentTypeUid != null && currentTypeUid.equals(uid) ? "selected" : "");
%>
        <option value="<%= uid %>" <%= selected %>><%= description %></option>
<%
    }
%>
    </select>
    <input id="<factory:encode name="providerTypeChangedInput"/>" type="hidden" name="<factory:bean bean="org.jboss.dashboard.ui.components.DataProviderHandler" property="currentProviderTypeChanged"/>" value="false"/>
<%
    }
%>
