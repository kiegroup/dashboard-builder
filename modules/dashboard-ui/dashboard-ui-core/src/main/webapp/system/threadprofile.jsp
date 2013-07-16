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
<%@ page import="org.jboss.dashboard.commons.misc.Chronometer" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.jboss.dashboard.profiler.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%
    if (request.getSession().getAttribute("accessGranted") == null) {
        request.setAttribute("redirect", "threadconsole.jsp");
%>
        <jsp:include page="sso.jsp" flush="true"/>
<%
        return;
    }
%>
<%!
    static final String TAB_CONTEXT = "context";
    static final String TAB_CODETRACES = "code traces";
    static final String TAB_TIMETRACES = "time traces";
    static String[] TABS = new String[] {TAB_CONTEXT, TAB_CODETRACES, TAB_TIMETRACES};
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>System Console</title>
    <link rel="stylesheet" href="styles.css" type="text/css">
</head>
<body>
<%
ComponentsContextManager.startContext();
try {
    Profiler profiler = Profiler.lookup();
    String threadHash = request.getParameter("hash");
    ThreadProfile tp = profiler.getThreadProfile(Integer.parseInt(threadHash));
    if (tp == null) {
%>
<h2><a href="console.jsp">System</a>&nbsp;&gt;&nbsp;<a href="threadconsole.jsp">Threads</a></h2>
<br/>Thread not found.
<%
        return;
    }

    // Catch request parameters
    String currentTab = TAB_CONTEXT;
    long traceLength = 0;
    String logPattern = "%-5p %C{6} (%F:%L) - %m [%X{ADDS}]%n";
    boolean showCodeBlockEvents = true;
    boolean showEmptyCodeBlocks = false;

    String currentTabParam = (String) session.getAttribute("tp_currentTab");
    Long lengthParam = (Long) session.getAttribute("tp_traceLength");
    String patternParam = (String) session.getAttribute("tp_logPattern");
    Boolean showCodeBlocksParam = (Boolean) session.getAttribute("tp_showCodeBlocks");
    Boolean showEmptyBlocksParam = (Boolean) session.getAttribute("tp_showEmptyBlocks");

    if (lengthParam != null) traceLength = lengthParam;
    if (currentTabParam != null) currentTab = currentTabParam;
    if (patternParam != null) logPattern = patternParam;
    if (showCodeBlocksParam != null) showCodeBlockEvents = showCodeBlocksParam;
    if (showEmptyBlocksParam != null) showEmptyCodeBlocks = showEmptyBlocksParam;

    // Process request
    String action = request.getParameter("action");
    if (action != null) {
        if (action.equals("changeTab")) {
            currentTab = request.getParameter("tab");
            session.setAttribute("tp_currentTab", currentTab);
        }
        if (action.equals("clearStackTraces")) {
            tp.clearStackTraces();
        }
        if (action.equals("changeSettings")) {
            String traceLengthParam = request.getParameter("traceLength");
            try {
                traceLength = Long.parseLong(traceLengthParam);
                session.setAttribute("tp_traceLength", traceLength);
            } catch (NumberFormatException e) {
                // Ignore
            }
            logPattern = request.getParameter("logPattern");
            showCodeBlockEvents = request.getParameter("showCodeBlocks") != null;
            showEmptyCodeBlocks = request.getParameter("showEmptyBlocks") != null;
            session.setAttribute("tp_logPattern", logPattern);
            session.setAttribute("tp_showCodeBlocks", showCodeBlockEvents);
            session.setAttribute("tp_showEmptyBlocks", showEmptyCodeBlocks);
        }
    }
    request.setAttribute("mintracetime", Long.toString(traceLength));
    request.setAttribute("expandAll", "true");
%>
<h2><a href="console.jsp">System</a>&nbsp;&gt;&nbsp;<a href="threadconsole.jsp">Threads</a>&nbsp;&gt;&nbsp;<%= tp.getId() %></h2>
<form action="threadprofile.jsp" method="post">
    <input type="hidden" name="hash" value="<%=threadHash%>">
    <input type="hidden" name="action" value="changeSettings">
    <table border="0" cellspacing="1" cellpadding="2">
    <tr>
        <td align="left" width="120px" height="25px">Thread status</td>
        <td align="left">= <%= tp.isRunning() ? "RUNNING" : "COMPLETED" %></td>
    </tr>
    <tr>
        <td align="left" height="25px">Thread length</td>
        <td align="left">= <%= Chronometer.formatElapsedTime(tp.getElapsedTime()) %></td>
    </tr>
    <% if (!currentTab.equals(TAB_CONTEXT)) { %>
    <tr>
        <% if (currentTab.equals(TAB_CODETRACES) || currentTab.equals(TAB_TIMETRACES)) { %>
            <td align="left" height="25px">Min. trace time</td>
            <td align="left"><input name="traceLength" onchange="this.form.submit();" class="skn-input" style="width:100px" value="<%= traceLength %>">&nbsp;(&gt; 0 ms)</td>
        <% } %>
    </tr>
    <% } %>
</table>
</form>
<br>
<%
    for (int i=0;i<TABS.length;i++) {
        String tab = TABS[i];
        if (currentTab.equals(tab)) {
%>
        <%= tab.toUpperCase() %>&nbsp;
<%
        } else {
%>
        <a href="threadprofile.jsp?action=changeTab&tab=<%=tab%>&hash=<%=threadHash%>&length=<%=traceLength%>"><%= tab.toUpperCase() %></a>&nbsp;
<%
        }
    }
    // CONTEXT
    if (currentTab.equals(TAB_CONTEXT)) {
%>
<table border="0" cellpadding="0" cellspacing="0" class="skn-table_border" width="100%">
    <tr>
        <td width="100%" valign="top">
        <h4>Thread&#39;s context</h4>
        <pre><table>
<%
        Set<String> propNames = tp.getContextPropertyNames();
        for (String propName : propNames) {
            if (tp.getContextProperty(propName) == null) continue;
            String propValue = tp.getContextProperty(propName).toString();
%>
            <tr>
                <td valign="top" align="left"><%=propName%></td>
                <td valign="top" align="left">= <%=StringEscapeUtils.escapeHtml(propValue) %></td>
            </tr>
<%
        }
%>
        </table></pre>
        </td>
    </tr>
</table>
<%
    // CODE TRACES
    } else if (currentTab.equals(TAB_CODETRACES)) {
        CodeBlockTraces allCodeTraces = tp.getRootCodeBlock().toPlainList();
        CodeBlockTraces rootCodeTraces = new CodeBlockTraces();
        rootCodeTraces.add(tp.getRootCodeBlock());
%>
<table border="0" cellpadding="0" cellspacing="0" class="skn-table_border" width="100%">
    <tr>
        <td width="100%" valign="top">
        <h4>Code traces summary</h4>
        <% request.setAttribute("codetraces", allCodeTraces); %>
        <% request.setAttribute("showheader", "true"); %>
        <jsp:include page="codetracesbytype.jsp" flush="true"/>
        <% request.removeAttribute("showheader"); %>
        <% request.removeAttribute("codetraces"); %>

        <h4>Code traces tree</h4>
        <% request.setAttribute("codetraces", rootCodeTraces); %>
        <jsp:include page="codetracestree.jsp" flush="true"/>
        <% request.removeAttribute("codetraces"); %>
        </td>
    </tr>
</table>
<%
    // TIME TRACES
    } else if (currentTab.equals(TAB_TIMETRACES)) {
        List<TimeTrace> timeTraces = tp.calculateTimeTraces();
%>
<table border="0" cellpadding="0" cellspacing="0" class="skn-table_border" width="100%">
    <tr>
        <td width="100%" valign="top">
<%
        if (timeTraces == null) {
%>
        <br><br><span class="skn-error">No method samples captured for the thread.</span><br>
<%
        } else if (timeTraces.isEmpty()) {
%>
        <br><br><span class="skn-error">No relevant method calls found for the thread.</span><br>
<%
        } else {
            if (tp.isElapsedTimeExceeded()) {
%>
        <br><br><span class="skn-error">WARNING: The profiling was interrupted because the thread was longest than
            <%=Chronometer.formatElapsedTime(Profiler.lookup().getMaxThreadProfilingTimeMillis())%>.</span><br>

<%
            } if (tp.isStackTraceTooLarge()) {
%>
        <br><br><span class="skn-error">WARNING: The profiling was interrupted because the thread contains a stack trace too long
            (more than <%=Profiler.lookup().getMaxThreadStackTraceLength()%> lines).</span><br>
<%
            }
%>
            <h4>Time consuming methods</h4>
            <table border="0" cellspacing="1" cellpadding="2">
                <tr>
                    <td align="left" width="100px">N. samples</td>
                    <td align="left">= <%= tp.getNumberOfSamples() %>&nbsp;
                        <a href="threadprofile.jsp?action=clearStackTraces&hash=<%=threadHash%>">Clear</a>&nbsp;
                    </td>
                </tr>
                <tr>
                <td align="left" width="100px">Sample time</td>
                <td align="left">= <%=Chronometer.formatElapsedTime(tp.getSampleAverageTimeMillis())%></td>
            </tr>
            </table>
            <% request.setAttribute("timetraces", timeTraces); %>
            <jsp:include page="timetraces.jsp" flush="true"/>
            <% request.removeAttribute("timetraces"); %>
<%
        }
%>
        </td>
    </tr>
</table>
<%
    }
} finally {
    ComponentsContextManager.clearContext();
}
%>
</body>
</html>
