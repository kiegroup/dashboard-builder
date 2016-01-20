<%--
    Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.

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
<%@ page import="org.jboss.dashboard.ui.panel.jspincluder.JspIncluderDriver" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n" %>

<panel:defineObjects/>
<%
    JspIncluderDriver jspDriver = (JspIncluderDriver) panelDriver;
    String jspPath = jspDriver.getJspPath(currentPanel);
%>
<jsp:include page="<%= jspPath %>" flush="true" />