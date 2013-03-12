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
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="org.jboss.dashboard.profiler.ThreadProfile" %>
<%@ page import="org.jboss.dashboard.commons.misc.Chronometer" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%
    List<ThreadProfile> allTxs = (List<ThreadProfile>) request.getAttribute("threadList");
    String filterProp = (String) request.getAttribute("filterProperty");
    int width = Integer.parseInt((String) request.getAttribute("width"));
%>
<div id='threadsDiv' class="skn-table_border" style="width:<%=width%>px;height:500px;overflow:auto;">
<table border="0" cellspacing="1" cellpadding="0" width="100%">
    <tr class="skn-table_header">
        <td align="center">&nbsp;</td>
        <td width="150px" align="center">STATUS</td>
        <td width="250px" align="center">THREAD ID</td>
        <td width="150px" align="center">ELAPSED TIME</td>
        <td width="220px" align="center">BEGIN</td>
        <td width="220px" align="center">END</td>
        <%  if (!StringUtils.isBlank(filterProp)) { %>
        <td align="center" width="250px"><%= filterProp.toUpperCase() %></td>
        <%  } %>
    </tr>
<%
    String css = "skn-odd_row";
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    for (ThreadProfile t : allTxs) {
        if (t.getThread() != null && t.getThread() == Thread.currentThread()) continue;

        long tpHtmlId = (long) (System.currentTimeMillis() * Math.random());
        if (css.equals("skn-odd_row")) css = "skn-even_row";
        else css = "skn-odd_row";

        String threadId = "<a href=\"threadprofile.jsp?hash=" + t.hashCode() + "&context=true\">" + t.getId() + "</a>";
        String elapsedTime = t.getNumberOfSamples()> 1 ? "<a href=\"threadprofile.jsp?hash=" + t.hashCode() + "&context=true&length=100\">" + Chronometer.formatElapsedTime(t.getElapsedTime()) + "</a>" : Chronometer.formatElapsedTime(t.getElapsedTime());
        String filterPropValue = (t.getContextProperty(filterProp) == null ? "" : t.getContextProperty(filterProp).toString());
        String state = t.getState();
%>
    <tr class="<%= css %>">
        <td width="20px" align="center">
            <%  if (!t.isRunning()) { %>
            <input type="checkbox" name="selectThread_<%= t.hashCode() %>" value="true" onclick="onThreadSelected();"/>
            <%  } %>
        </td>
        <td width="150px" align="center">
        <%  if (t.isRunning()) { %>
            <a href="#" onclick="showContext_<%=tpHtmlId%>(); return false;" title="View context"><%= state %></a>
            <div id="context_<%=tpHtmlId%>" style="display:none;border:black">
                <textarea rows="15" cols="120" readonly="true"><%=t.printContext()%></textarea>
            </div>
            <script language="JavaScript">
                function showContext_<%=tpHtmlId%>() {
                    elem = document.getElementById('context_<%=tpHtmlId%>');
                    if (elem.style.display == 'none') elem.style.display='block';
                    else elem.style.display='none'
                }
            </script>
            <%  } else { %>
            <%= state %>
        <%  } %>
        </td>
        <td width="250px" align="center"><%=threadId%></td>
        <td width="150px" align="center"><%= elapsedTime %></td>
        <td width="220px" align="center" >&nbsp;&nbsp;&nbsp;<%= dateFormat.format(t.getBeginDate()) %>&nbsp;&nbsp;&nbsp;</td>
        <td width="220px" align="center">&nbsp;&nbsp;&nbsp;<%=  t.isRunning() ? "" : dateFormat.format(t.getEndDate()) %>&nbsp;&nbsp;&nbsp;</td>
        <%  if (!StringUtils.isBlank(filterProp)) { %>
        <td width="250px" align="left">
            <div style="width:240px;height:14px;text-align:left;overflow:hidden;vertical-align:middle;" title="<%=StringEscapeUtils.escapeHtml(filterPropValue) %>)">
                <%= StringEscapeUtils.escapeHtml(filterPropValue) %></div>
        </td>
        <%  } %>
    </tr>
<%
    }
%>
</table>
</div>

