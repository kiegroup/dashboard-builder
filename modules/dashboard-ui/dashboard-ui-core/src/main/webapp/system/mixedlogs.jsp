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
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.jboss.dashboard.profiler.*" %>
<%@ page import="org.apache.log4j.PatternLayout" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
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
    static String[] COLORS = new String[] {"#ffffff","#00ffff","#ffe4c4","#ff1493","#8a2be2","#a52a2a","#7fff00","#dc143c","#ff8c00","#adff2f","#ffd700","#ff0000","#d3d3d3","#ffe4e1","#afeeee","#ff00ff","#1e90ff","#f0e68c","#daa520","#e9967a"};
    static Map<ThreadProfile,String> colorMap = new HashMap();

    static String getStyle(ThreadProfile tp) {
        String color = colorMap.get(tp);
        if (color == null) colorMap.put(tp, color = COLORS[colorMap.size() % COLORS.length]);
        return "font-size:x-small;color:" + color;
    }

    static String formatEvent(ThreadProfile.LogEvent event, Map<String,Object> formatProps) {
        ThreadProfile tp = event.getCodeBlockTrace().getThreadProfile();
        String eventStr = event.format(formatProps);
        String threadId = tp.getId();
        String style = getStyle(tp);

        StringBuffer buf = new StringBuffer();
        buf.append("<span style=\"").append(style).append("\">");
        buf.append(threadId).append(" - ").append(eventStr);
        buf.append("</span>");
        return buf.toString();
    }

    static void sortLogEventsByTimestamp(List<ThreadProfile.LogEvent> mixedLogs) {
        Collections.sort(mixedLogs, new Comparator() {
            public int compare(Object o1, Object o2) {
                ThreadProfile.LogEvent e1 = (ThreadProfile.LogEvent) o1;
                ThreadProfile.LogEvent e2 = (ThreadProfile.LogEvent) o2;
                if (e1.getTimestamp() > e2.getTimestamp()) return 1;
                if (e1.getTimestamp() < e2.getTimestamp()) return -1;
                return 0;
            }
        });
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
<%
ComponentsContextManager.startContext();
try {
    Set<ThreadProfile> targetThreads = (Set<ThreadProfile>) session.getAttribute("targetThreads");
    if (targetThreads.size() < 2) {
%>
<h2><a href="console.jsp">System</a>&nbsp;&gt;&nbsp;<a href="threadconsole.jsp">Threads</a>&nbsp;&gt;&nbsp;Mixed Logs</h2><br/>
<%= targetThreads.size() %> threads selected.<br/>
Please select at least two or more threads.<br/>
<%
        return;
    }

    // Retrieve status from session.
    String logPattern = "[%d{dd/MM/yy HH:mm:ss}] %-5p %C{6} (%F:%L) - %m [%X{ADDS}]%n";
    boolean showCodeBlockEvents = true;

    String patternParam = (String) session.getAttribute("tp_logPattern");
    Boolean showCodeBlocksParam = (Boolean) session.getAttribute("tp_showCodeBlocks");
    if (patternParam != null) logPattern = patternParam;
    if (showCodeBlocksParam != null) showCodeBlockEvents = showCodeBlocksParam;

    // Process request
    String action = request.getParameter("action");
    if (!StringUtils.isBlank(action)) {
        if (action.equals("")) {
        }
    }

    // Get the mixed log list from the target threads.
    List<ThreadProfile.LogEvent> mixedLogs = (List<ThreadProfile.LogEvent>) session.getAttribute("mixedLogs");
    if (mixedLogs == null) {
        mixedLogs = new ArrayList<ThreadProfile.LogEvent>();
        for (ThreadProfile tp : targetThreads) mixedLogs.addAll(tp.getLogEvents(showCodeBlockEvents, true));
        sortLogEventsByTimestamp(mixedLogs);
        session.setAttribute("mixedLogs", mixedLogs);
        colorMap.clear();
    }

    // Display the event list.
    Map<String,Object> formatProps = new HashMap<String, Object>();
    formatProps.put(ThreadProfile.Log4JEvent.LAYOUT, new PatternLayout(logPattern));
%>
<h2><a href="console.jsp">System</a>&nbsp;&gt;&nbsp;<a href="threadconsole.jsp">Threads</a>&nbsp;&gt;&nbsp;Mixed Logs for <%=targetThreads.size()%> threads</h2>
<table border="0" cellpadding="1" cellspacing="2" class="skn-table_border">
<tr>
<td valign="top" align="left">
    <div id='logsDiv' style="background-color:black;color:skyblue;width:1200px;height:600px;overflow:auto;"><pre style="font:message-box;font-size:small;"><%
        for (int i=0; i<mixedLogs.size(); i++) {
            ThreadProfile.LogEvent event = mixedLogs.get(i);
            String eventStr = formatEvent(event, formatProps); %><%= eventStr %><%
        } %>
    </pre></div>
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
