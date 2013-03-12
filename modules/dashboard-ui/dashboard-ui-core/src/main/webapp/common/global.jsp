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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="security_taglib.tld" prefix="security" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>

<%@ page import="org.jboss.dashboard.ui.SessionManager,                 org.jboss.dashboard.workspace.*,
                 org.jboss.dashboard.security.WorkspacePermission,
                 java.security.AllPermission,
                 java.security.Permission,
                 java.util.Locale" %>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="org.jboss.dashboard.ui.NavigationManager"%>
<%@ page import="org.jboss.dashboard.factory.Factory"%>
<%
    LocaleManager localeManager = LocaleManager.lookup();
    NavigationManager navigationManager = NavigationManager.lookup();
    Section currentSection = navigationManager.getCurrentSection();
    Workspace currentWorkspace = navigationManager.getCurrentWorkspace();
    Locale currentLocale = localeManager.getCurrentLocale();
    boolean adminMode = navigationManager.userIsAdminInCurrentWorkspace();
%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=localeManager.getCurrentLocale()%>"/>

