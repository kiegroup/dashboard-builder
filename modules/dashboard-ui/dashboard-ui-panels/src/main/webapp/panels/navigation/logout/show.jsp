<%@ page import="org.jboss.dashboard.ui.controller.requestChain.EmbeddedRequestProcessor" %>
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
<%@ taglib uri="factory.tld" prefix="factory" %>
<%
    // This is the case when embedding, for instance, the jBPM process dashboard as an UF panel because the login/logout is handled by the J2EE container & UF.
    // See org.jboss.dashboard.ui.controller.requestChain.EmbeddedRequestProcessor
    Boolean embeddedMode = (Boolean) session.getAttribute("dashbuilder_embedded");
    if (embeddedMode == null || !embeddedMode) embeddedMode = (Boolean) session.getAttribute("dashbuilder_hideLogout");
    if (embeddedMode == null || !embeddedMode) {
%>
<factory:useComponent bean="org.jboss.dashboard.ui.components.LogoutComponent"/>
<%
    }
%>
