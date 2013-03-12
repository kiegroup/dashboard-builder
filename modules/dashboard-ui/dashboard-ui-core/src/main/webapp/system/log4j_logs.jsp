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
<%@ page import="org.jboss.dashboard.log.Log4JManager" %>
<%@ page import="org.jboss.dashboard.log.MemoryAppender" %>
<%@ page import="org.apache.log4j.spi.LoggingEvent" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="org.jboss.dashboard.CoreServices" %>
<%
    Log4JManager log4JManager = CoreServices.lookup().getLog4JManager();
    MemoryAppender appender = log4JManager.getMemoryAppender();
    LinkedList<LoggingEvent> events = appender.getBuffer();

    boolean refreshOn = false;
    Boolean refreshOnParam = (Boolean) session.getAttribute("log_refreshOn");
    if (refreshOnParam != null) refreshOn = refreshOnParam;

    // Keep the log's div horizontal scroll.
    String scrollLeft = request.getParameter("scrollLeft");
    if (scrollLeft == null) scrollLeft = (String) session.getAttribute("log_scrollLeft");
    if (scrollLeft == null) scrollLeft = "0";
    session.setAttribute("log_scrollLeft", scrollLeft);

    // Process request
    String action = request.getParameter("action");
    if (action != null) {
        if (action.equals("changeBufferSize")) {
            try {
                String size = request.getParameter("size");
                appender.setBufferSize(Integer.parseInt(size));
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        if (action.equals("switchRefresh")) {
            refreshOn = !refreshOn;
            session.setAttribute("log_refreshOn", refreshOn);
        }
        if (action.equals("clear")) {
            appender.clear();
            session.removeAttribute("log_scrollLeft");
        }
    }
%>
<table border="0" cellpadding="1" cellspacing="2" align="left">
    <tr>
        <td align="left">
            <table border="0" cellpadding="1" cellspacing="5" align="left">
                <tr>
                    <td align="left" valign="center">
                        <form action="<%= request.getRequestURI() %>" method="post">
                            <input type="hidden" name="action" value="switchRefresh"/>
                            <input type="checkbox" style="height:8" class="skn-input" <%= refreshOn ? "checked" : "" %> onchange="this.form.submit();" />
                            &nbsp;Auto&nbsp;<a href="<%= request.getRequestURI() %>?scrollLeft=0">Refresh</a>.
                        </form>
                    </td>
                    <td align="left" valign="center">
                        <form action="<%=request.getRequestURI()%>" method="post">
                            <input type="hidden" name="action" value="changeBufferSize"/>
                            <%=events.size()%> rows of <input type="text" class="skn-input" name="size" size="10" value="<%= appender.getBufferSize() %>" onchange="this.form.submit();" />&nbsp;
                            <a href="<%= request.getRequestURI() %>?action=clear">Clear</a>.&nbsp; Throughput=<%= log4JManager.getThroughput() %> events/second.
                        </form>
                    </td>
                </tr>
            </table>            
        </td>
    </tr>
    <tr>
        <td width="100%" align="left" valign="top">
        <div id='logsDiv' style="background-color:black;color:skyblue;width:1250px;height:550px;overflow:auto;"><pre style="font:message-box;font-size:small;"><%
            int windowSize = refreshOn ? 33 : appender.getBufferSize();
            int startIndex = events.size() - windowSize;
            if (startIndex < 0) startIndex = 0;
            for (int i=startIndex; i<events.size(); i++) {
                LoggingEvent event = events.get(i);
                String eventStr = appender.getLayout().format(event);%><%= eventStr %><%
            } %></pre></div>
        </td>
    </tr>
</table>
<script type="text/javascript">
    logsDiv = document.getElementById('logsDiv');
    logsDiv.scrollLeft = '<%= scrollLeft %>';
    logsDiv.scrollTop = logsDiv.scrollHeight;

    <% if (refreshOn) { %>
        setTimeout('window.location=\'<%= request.getRequestURI() %>?scrollLeft=\' + document.getElementById(\'logsDiv\').scrollLeft', '500');
    <% } %>
</script>
