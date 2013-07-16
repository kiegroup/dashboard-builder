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
<%@ page import="org.jboss.dashboard.Application" %>
<%@ page import="org.jboss.dashboard.factory.ComponentsContextManager" %>
<%
    if (request.getSession().getAttribute("accessGranted") == null) {
        request.setAttribute("redirect", "console.jsp");
%>
        <jsp:include page="sso.jsp" flush="true"/>
<%
        return;
    }
%>
<%
    ComponentsContextManager.startContext();
    try {
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>System Console</title>
    <link rel="stylesheet" href="styles.css" type="text/css">
</head>
<body>
<h2>System Console</h2>
<ul>
    <li><a href="threadconsole.jsp">Thread profiler</a></li>
    <li><a href="schedulerconsole.jsp">Scheduler tasks</a></li>
    <li><a href="hibernateconsole.jsp">Hibernate stats</a></li>
</ul>
<br/><br/>
<%= Application.lookup().getCopyright() %>
</body>
</html>
<%
    } finally {
        ComponentsContextManager.clearContext();
    }
%>