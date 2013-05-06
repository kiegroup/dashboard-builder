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
<%@ page import="java.util.Locale"%>
<%@ page import="org.jboss.dashboard.domain.label.LabelDomain" %>
<%@ page import="org.jboss.dashboard.domain.date.DateDomain" %>
<%@ page import="org.jboss.dashboard.domain.numeric.NumericDomain" %>
<%@ page import="org.jboss.dashboard.domain.AbstractDomain" %>
<%@ page import="org.jboss.dashboard.ui.components.AbstractChartDisplayerEditor" %>
<%@ page import="org.jboss.dashboard.domain.DomainConfiguration" %>
<%@ page import="org.jboss.dashboard.domain.Domain" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    Locale locale = LocaleManager.currentLocale();
    DomainConfiguration domainConfig = (DomainConfiguration) request.getAttribute("domainConfig");
    Domain domain = domainConfig.getDomainProperty().getDomain();
    String hideDescription = (String) request.getAttribute("hideDescription");
    if (hideDescription == null || !hideDescription.equals("true")) {
%>
       <tr>
            <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                    <i18n:message key='<%= AbstractDomain.I18N_PREFFIX + "description"%>'>!!Descripcion</i18n:message>:
            </td>
            <td align="left">
                <input class="skn-input" name="descripDomainDetails" type="text" value="<%= domainConfig.getPropertyName(locale) %>">
            </td>
        </tr>
<% } %>
        <tr>
            <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                    <i18n:message key='<%= AbstractDomain.I18N_PREFFIX + "maxNumberOfIntervals"%>'>!!Numero maximo de intervalos</i18n:message>:
            </td>
            <td align="left">
                <input class="skn-input" name="domainMaxNumberOfIntervals" type="text" value="<%= domainConfig.getMaxNumberOfIntervals() %>">
            </td>
        </tr>
<%
    if (domain instanceof LabelDomain) {
%>
        <mvc:include page="label_domain_details.jsp" flush="true" />
<%
    } else if (domain instanceof  DateDomain) {
%>
        <mvc:include page="date_domain_details.jsp" flush="true" />
<%
    } else if (domain instanceof NumericDomain) {
%>
        <mvc:include page="numeric_domain_details.jsp" flush="true" />
<%
    }
    String hideButtons = (String) request.getAttribute("hideButtons");
    if (hideButtons == null || !hideButtons.equals("true")) {
%>
        <tr>
            <td align="center" colspan="2" style="padding-bottom:10px; padding-top:10px">
            <input type="hidden" name="<%=AbstractChartDisplayerEditor.DOMAIN_SAVE_BUTTON_PRESSED%>" id="<factory:encode name="updateDomainDetails"/>" value="false">
            <input class= "skn-button" type="button" value="<i18n:message key="dataProviderComponent.save">!!Guardar</i18n:message>" onclick="document.getElementById('<factory:encode name="updateDomainDetails"/>').value='true'; bam_kpiedit_submitProperties(this.form);">
            <input class= "skn-button" type="button" value="<i18n:message key="dataProviderComponent.cancel">!!Cancelar</i18n:message>" onclick="window.<factory:encode name="editDomain"/>(); return false;">
            </td>
        </tr>
<%
    }
%>