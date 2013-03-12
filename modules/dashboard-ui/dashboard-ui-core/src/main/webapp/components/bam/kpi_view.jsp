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
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ page import="org.jboss.dashboard.ui.components.KPIViewer"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.UIBeanLocator" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayer" %>
<%@ page import="org.jboss.dashboard.kpi.KPI" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<i18n:bundle baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    KPIViewer kpiViewer = KPIViewer.lookup();
    KPI kpi = kpiViewer.getKpi();
    DataDisplayer displayer = kpi.getDataDisplayer();
    String viewerPath = UIBeanLocator.lookup().getViewer(displayer).getName();
%>

<% if (kpiViewer.isReady()) { %>
    <factory:useComponent bean="<%= viewerPath %>"/>
<% } else { %>
    <span class="skn-error"><i18n:message key="kpiViewerComponent.notWellConfigured">!!!Component not configured well</i18n:message></span>
<% } %>