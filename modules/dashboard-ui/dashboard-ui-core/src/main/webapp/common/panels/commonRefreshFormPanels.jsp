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
<%@ page import="org.jboss.dashboard.ui.panel.AjaxRefreshManager"%>
<%@ page import="org.jboss.dashboard.workspace.Panel"%>
<%@ page import="org.jboss.dashboard.ui.SessionManager"%>
<%@ page import="org.jboss.dashboard.ui.controller.RequestContext" %>
<%@ page import="org.jboss.dashboard.workspace.Parameters" %>
<%@ page import="org.jboss.dashboard.workspace.Panel" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%
    Panel p = (Panel) RequestContext.getCurrentContext().getRequest().getRequestObject().getAttribute(Parameters.RENDER_PANEL);
%>
<a name="p<%=p.getPanelId()%>" style="display:none"></a>
<form id="<%=AjaxRefreshManager.FORM_IDENTIFIER_PREFFIX + p.getPanelId()%>" action="<panel:link action="_refreshPanel"/>" method="post" style="margin:0px">
    <panel:hidden action="_refreshPanel"/>
</form>

<script  language="Javascript" defer>
    setAjax("<%=AjaxRefreshManager.FORM_IDENTIFIER_PREFFIX + p.getPanelId()%>");
</script>