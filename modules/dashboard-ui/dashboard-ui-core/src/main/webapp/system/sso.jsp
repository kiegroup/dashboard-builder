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
<%@ page import="org.jboss.dashboard.users.UserStatus" %>
<%@ page import="org.jboss.dashboard.Application" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
    String copyright = null;
    UserStatus userStatus = null;
    try {
        ComponentsContextManager.startContext();
        userStatus = UserStatus.lookup();
        copyright = Application.lookup().getCopyright();

        // SSO of root login requests.
        String login = request.getRemoteUser();
        if (!StringUtils.isBlank(login)
                && userStatus.isAnonymous()
                && userStatus.getRootLogin().equals(login)) {

            userStatus.initSessionAsRoot();
        }
    } finally {
        ComponentsContextManager.clearContext();
    }

    if (userStatus.isRootUser()) {
        String redirect = (String) request.getAttribute("redirect");
        if (redirect == null) redirect = "console.jsp";
        request.getSession().setAttribute("accessGranted", "true");
%>
        <jsp:include page="<%= redirect %>" flush="true" />
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
<body onload="document.getElementById('password').value='';document.getElementById('password').focus();">
<h2 class="skn-error">Restricted area. Access denied.</h2><br>
<br/>Only the superuser has access granted. Please, login first.
<br/><%= copyright %>
</body>
</html>
