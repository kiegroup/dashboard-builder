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
<%@ page import="org.jboss.dashboard.domain.DomainConfiguration" %>
<%@ page import="org.jboss.dashboard.domain.label.LabelDomain" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    DomainConfiguration domainConfig =  (DomainConfiguration) request.getAttribute("domainConfig");
%>
<tr>
    <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
            <i18n:message key='<%= LabelDomain.I18N_PREFFIX + "labelIntervalsToHide"%>'>!!Ocultar intervalos (separados por comas)</i18n:message>:
    </td>
    <td nowrap="nowrap" align="left">
        <input class="skn-input" name="labelIntervalsToHide" type="text" value="<%= domainConfig.getLabelIntervalsToHide(LocaleManager.currentLocale()) %>">
    </td>
</tr>