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
<%@ page import="org.jboss.dashboard.factory.ComponentsContextManager" %>
<%
    if (request.getSession().getAttribute("accessGranted") == null) {
        request.setAttribute("redirect", "log4jconsole.jsp");
%>
        <jsp:include page="sso.jsp" flush="true"/>
<%
        return;
    }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>System Console</title>
    <link rel="stylesheet" href="styles.css" type="text/css">
</head>
<body>
<h2><a href="console.jsp">System</a>&nbsp;&gt;&nbsp;Logs</h2>
<%!
    static final String TAB_LOGS = "logs";
    static final String TAB_APPENDERS = "appenders";
    static final String TAB_LOGGERS = "loggers";
    static String[] TABS = new String[] {TAB_LOGS, TAB_LOGGERS, TAB_APPENDERS};
%>
<%
    ComponentsContextManager.startContext();
    try {
        String currentTab = TAB_LOGS;
        String currentTabParam = (String) session.getAttribute("lc_currentTab");
        if (currentTabParam != null) currentTab = currentTabParam;

        // Process request
        String action = request.getParameter("action");
        if (action != null) {
            if (action.equals("changeTab")) {
                currentTab = request.getParameter("tab");
                session.setAttribute("lc_currentTab", currentTab);
            }
        }

        String currentJsp = "log4j_" + currentTab + ".jsp";
        for (int i=0;i<TABS.length;i++) {
            String tab = TABS[i];
            if (currentTab.equals(tab)) {
%>
        <%= tab.toUpperCase() %>&nbsp;    
<%
        } else {
%>
        <a href="log4jconsole.jsp?action=changeTab&tab=<%=tab%>"><%= tab.toUpperCase() %></a>&nbsp;
<%
        }
    }
%>
    <table border="0" cellpadding="0" cellspacing="0" class="skn-table_border" <%= !currentTab.equals(TAB_LOGS) ? "width=\"100%\"" : "" %>>
        <tr>
            <td valign="top">
                <jsp:include page="<%= currentJsp %>" flush="true" />
            </td>
        </tr>
    </table>
<%
    } finally {
        ComponentsContextManager.clearContext();
    }
%>
</body>
</html>
