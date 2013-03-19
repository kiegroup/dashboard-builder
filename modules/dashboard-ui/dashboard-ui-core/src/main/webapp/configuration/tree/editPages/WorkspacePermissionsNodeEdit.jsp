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
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0"  prefix="i18n" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.components.permissions.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<p style="padding-left:10px;" class="skn-important">
    <i18n:message key="defaultWorkspacePolicy">!!Default workspace policy</i18n:message>
</p>
<jsp:include page="editPermissions.jsp" flush="true"/>
