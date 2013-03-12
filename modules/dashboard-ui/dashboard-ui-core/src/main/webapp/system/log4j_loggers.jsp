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
<%@ page import="org.apache.log4j.Level" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.jboss.dashboard.log.LoggerSet" %>
<%@ page import="org.jboss.dashboard.log.Log4JManager" %>
<%@ page import="org.jboss.dashboard.CoreServices" %>
<%
    // Process request
    Log4JManager log4JManager = CoreServices.lookup().getLog4JManager();
    LoggerSet editedLoggerSet = null;
    String action = request.getParameter("action");
    if (action != null) {
        if (action.equals("newLoggerSet")) {
            editedLoggerSet = LoggerSet.PROTOTYPE;
        }
        if (action.equals("editLoggerSet")) {
            long id = Long.parseLong(request.getParameter("id"));
            editedLoggerSet = log4JManager.getLoggerSetById(id);
        }
        if (action.equals("createLoggerSet")) {
            String name = request.getParameter("name");
            if (!StringUtils.isBlank(name)) {
                String loggers = request.getParameter("loggers");
                log4JManager.addLoggerSet(new LoggerSet(name, Level.FATAL, loggers, true));
            }
        }
        if (action.equals("updateLoggerSet")) {
            long id = Long.parseLong(request.getParameter("id"));
            LoggerSet loggerSet = log4JManager.getLoggerSetById(id);
            String name = request.getParameter("name");
            if (loggerSet != null && !StringUtils.isBlank(name)) {
                loggerSet.setName(name);
                loggerSet.parseLoggers(request.getParameter("loggers"));
            }
        }
        if (action.equals("deleteLoggerSet")) {
            long id = Long.parseLong(request.getParameter("id"));
            LoggerSet loggerSet = log4JManager.getLoggerSetById(id);
            if (loggerSet != null) log4JManager.removeLoggerSet(loggerSet);
        }
        if (action.equals("changeLevel")) {
            long id = Long.parseLong(request.getParameter("id"));
            LoggerSet loggerSet = log4JManager.getLoggerSetById(id);
            if (loggerSet != null) {
                try {
                    int level = Integer.parseInt(request.getParameter("level"));
                    loggerSet.setLevel(Level.toLevel(level));
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
    }
%>
<table border="0" cellpadding="0" cellspacing="1">
    <tr>
        <td align="left" valign="top">
            <h4>Active Loggers</h4>
            <a href="<%= request.getRequestURI() %>?action=newLoggerSet">+ Add new</a>
            <table border="0" cellpadding="1" cellspacing="2" class="skn-table_border">
<%
                boolean even = true;
                for (LoggerSet loggerSet: log4JManager.getLoggerSets()) {
                    even = !even;
%>
                <tr class="<%= even ? "skn-odd_row" : "skn-even_row" %>">
                    <td width="18px" align="left">
                        <% if (loggerSet.isEditable()) { %>
                        <a href="<%= request.getRequestURI() %>?action=deleteLoggerSet&id=<%= loggerSet.getId() %>"><img src="images/delete.gif" border="0"/></a>
                        <% } else { %>
                        &nbsp;
                        <% } %>
                    </td>
                    <td width="18px" align="left">
                        <a href="<%= request.getRequestURI() %>?action=editLoggerSet&id=<%= loggerSet.getId() %>"><img src="images/edit.gif" border="0"/></a>
                    </td>
                    <td align="left"><%= StringEscapeUtils.escapeHtml(loggerSet.getName()) %></td>
                    <td align="center">
<%
                        request.setAttribute("submitOnChange", Boolean.TRUE);
                        request.setAttribute("submitFunction", "levelChanged");
                        request.setAttribute("fieldName", "level");
                        request.setAttribute("selectedLevel", loggerSet.getLevel());
                        request.setAttribute("levels", loggerSet.getAllowedLevels());
%>
                        <form action="<%= request.getRequestURI() %>" method="post">
                            <input type="hidden" name="action" value="changeLevel"/>
                            <input type="hidden" name="id" value="<%= loggerSet.getId() %>"/>
                            <jsp:include page="log4j_levels.jsp" flush="true" />
                        </form>
                    </td>
                </tr>
<%
                }
%>
            </table>
        </td>
        <td width="50px">&nbsp;</td>
        <% if (editedLoggerSet != null) { %>
        <td align="left" valign="top">
            <% if (editedLoggerSet.isEditable()) { %>
            <form action="<%= request.getRequestURI() %>" method="post">
            <input type="hidden" name="action" value="<%= editedLoggerSet == LoggerSet.PROTOTYPE ? "createLoggerSet" : "updateLoggerSet" %>"/>
            <input type="hidden" name="id" value="<%= editedLoggerSet.getId() %>"/>
            <% } %>
            <h4><%= editedLoggerSet == LoggerSet.PROTOTYPE ? "New Logger form" : editedLoggerSet.getName() %></h4>
            <table border="0" cellpadding="1" cellspacing="2">
                <tr>
                    <td align="left">Name</td>
                    <td><input <%= !editedLoggerSet.isEditable() ? "disabled" : "" %> class="skn-input" id="loggerSetName" name="name" style="width:200px" value="<%= StringEscapeUtils.escapeHtml(editedLoggerSet.getName()) %>"/></td>
                </tr>
                <tr>
                    <td align="left" valign="top">Loggers</td>
                    <td><textarea <%= !editedLoggerSet.isEditable() ? "disabled" : "" %> class="skn-input" name="loggers" cols="60" rows="5"><%= editedLoggerSet.printLoggers("\n") %></textarea></td>
                </tr>
                <% if (editedLoggerSet.isEditable()) { %>
                <tr>
                    <td colspan="2" align="center">
                        <input class="skn-input" type="submit" value="Submit" />    
                    </td>
                </tr>
                <% } %>
            </table>
            <% if (editedLoggerSet.isEditable()) { %>
            </form>
            <% } %>
        </td>
        <% } %>
    </tr>
</table>
<script type="text/javascript">
<% if (editedLoggerSet == LoggerSet.PROTOTYPE) { %>
    document.getElementById('loggerSetName').focus();
<% } %>
    function levelChanged(levelSelect) {
        levelInt = levelSelect.options[levelSelect.selectedIndex].value;
        if (levelInt == <%= Level.DEBUG.toInt() %> || levelInt == <%= Level.TRACE.toInt() %>) {
            if (window.confirm("WARNING: The selected level could generate a lots of logs. Continue\u003F")) {
                levelSelect.form.submit();
            } else {
                window.location = '<%=request.getRequestURI() %>'
            }
        } else {
            levelSelect.form.submit();
        }
    }
</script>