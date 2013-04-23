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
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.components.AbstractChartDisplayerEditor" %>
<i18n:bundle baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<!-- Domain axis selection and edition -->
<tr>
    <td align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "domain"%>'>!!Domain</i18n:message>:
    </td>
    <mvc:include page="../domain/domain_selector_component.jsp"  flush="true" />
</tr>
<!-- Range axis selection and edition -->
<tr>
    <td align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "range"%>'>!!Range</i18n:message>:
    </td>
    <mvc:include page="../range/range_selector_component.jsp"  flush="true" />
</tr>