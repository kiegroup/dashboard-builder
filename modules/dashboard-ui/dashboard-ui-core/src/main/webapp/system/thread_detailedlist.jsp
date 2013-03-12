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
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%
    List<ThreadProfile> allTxs = (List<ThreadProfile>) request.getAttribute("threadList");
    List<String> allPropNames = new ArrayList(ThreadProfile.getAllContextPropertyNames());
    int width = Integer.parseInt((String) request.getAttribute("width"));
%>
<div class="skn-table_border" style="width:<%=width%>px;height:500px;overflow:scroll;">
<table border="0" cellspacing="1" cellpadding="0">
    <tr>
        <td align="left">
            <table border="0" cellspacing="1" cellpadding="0" width="225px">
                <tr>
                    <td style="background-color:#C6D8EB;height:20px;color:#465F7D;font-weight:Bold;" align="left">STATUS</td>
                </tr>
                <tr>
                    <td style="background-color:#C6D8EB;height:20px;color:#465F7D;font-weight:Bold;" align="left">ELAPSED TIME</td>
                </tr>
<%
            for (String propName : allPropNames) {
%>
                <tr>
                    <td style="background-color:#C6D8EB;height:20px;color:#465F7D;font-weight:Bold;" align="left"><%= propName.toUpperCase() %></td>
                </tr>
<%
            }
%>
            </table>
        </td>
        <td align="left">
            <table align="left" border="0" cellspacing="1" cellpadding="0">
                <tr>
<%
                String css = "skn-odd_row";
                for (ThreadProfile t : allTxs) {
                    if (t.getThread() != null && t.getThread() == Thread.currentThread()) continue;
                    long tpHtmlId = (long) (System.currentTimeMillis() * Math.random());
                    if (css.equals("skn-odd_row")) css = "skn-even_row";
                    else css = "skn-odd_row";
                    String state = t.getState();
%>
                    <td class="<%= css %>" align="center">
                    <%  if (t.isRunning()) { %>
                        <a href="#" onclick="showContext_<%=tpHtmlId%>(); return false;" title="View context"><%= state%></a>
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
<%
                }
%>
                </tr>
                <tr>
<%
                css = "skn-odd_row";
                for (ThreadProfile t : allTxs) {
                    if (t.getThread() != null && t.getThread() == Thread.currentThread()) continue;
                    if (css.equals("skn-odd_row")) css = "skn-even_row";
                    else css = "skn-odd_row";
%>
                    <td class="<%= css %>" align="center">
                        <a href="threadprofile.jsp?hash=<%= t.hashCode() %>&context=true>"><%= Chronometer.formatElapsedTime(t.getElapsedTime()) %></a>
                    </td>
<%
                }
%>
                </tr>
<%
            for (String propName : allPropNames) {
%>
                <tr>
<%
                css = "skn-odd_row";
                for (ThreadProfile t : allTxs) {
                    if (t.getThread() != null && t.getThread() == Thread.currentThread()) continue;
                    if (css.equals("skn-odd_row")) css = "skn-even_row";
                    else css = "skn-odd_row";
                    String propValue = (t.getContextProperty(propName) == null ? "" : t.getContextProperty(propName).toString());
%>
                    <td width="150px" class="<%= css %>" align="center" valign="center">
                        <div style="width:140px;height:14px;text-align:left;overflow:hidden;vertical-align:middle;" title="<%= StringEscapeUtils.escapeHtml(propValue) %>">
                            <%= StringEscapeUtils.escapeHtml(propValue) %></div>
                    </td>
<%
                }
%>
                </tr>
<%
            }
%>
            </table>
        </td>
    </tr>
</table>
</div>