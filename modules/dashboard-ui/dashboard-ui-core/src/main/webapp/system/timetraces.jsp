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
<%@ page import="org.jboss.dashboard.commons.misc.Chronometer" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="org.jboss.dashboard.profiler.TimeTrace" %>
<%@ page import="org.jboss.dashboard.profiler.TimeTraceComparator" %>
<%@ page import="org.jboss.dashboard.profiler.StackTrace" %>
<%@ page import="org.jboss.dashboard.profiler.CodeBlockTraces" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.DateFormat" %>
<%
    boolean expandAll = request.getAttribute("expandAll") != null;
    List<TimeTrace> timeTraces = (List<TimeTrace>)request.getAttribute("timetraces");
    DateFormat df = DateFormat.getDateTimeInstance();
    if (!timeTraces.isEmpty()) {
%>
    <ul style="list-style-type:none;margin-left:15px;padding-left:0px;">
<%
        TimeTraceComparator comp = new TimeTraceComparator();
        comp.addSortCriteria(TimeTraceComparator.CRITERIA_ELAPSED_TIME, TimeTraceComparator.ORDER_DESCENDING);
        Collections.sort(timeTraces, comp);
        for (TimeTrace tt : timeTraces) {
            String totalTime = Chronometer.formatElapsedTime(tt.getElapsedTimeMillis());
            String selfTime = Chronometer.formatElapsedTime(tt.getSelfTimeMillis());
            CodeBlockTraces codeTraces = tt.getCodeBlockTraces();
            List<TimeTrace> children = tt.getChildren();
            long ttHtmlId = (long) (System.currentTimeMillis() * Math.random());
%>
        <li>
<%
            if (children == null || children.isEmpty()) {
%>
        <span style="font-size:large;">&middot;</span>
<%
          } else {
%>
        <a style="font-size:large;" href="#" onclick="expandTimeTrace<%=ttHtmlId%>();return false;" id="expand_<%=ttHtmlId%>"><%= expandAll ? "-" : "+"%></a>
<%
        }
%>
        &nbsp;<%= tt.getSelfTimeMillis() > 500 ? "<span class='skn-error'>" + totalTime + "</span>" : totalTime %>&nbsp;-&nbsp;
            <a href="#" onclick="showTimeTrace<%=ttHtmlId%>();return false;"><%= StackTrace.printStackElement(tt.last(), 60) %></a>
            <% if (codeTraces.size() > 0) { %>
                <% request.setAttribute("codetraces", codeTraces); %>
                <jsp:include page="codetracesbytype.jsp" flush="true"/>
                <% request.removeAttribute("codetraces"); %>
            <% } %>
            <div id="timeTrace<%=ttHtmlId%>" style="display:none;border:black">
                <textarea rows="15" cols="150" readonly="true" style="font-size:x-small;">Total time=<%=totalTime%>&#13;Self time=<%=selfTime%>&#13;Number of samples = <%=tt.getStackTraces().size()%>&#13;Begin=<%=df.format(new Date(tt.getBeginTimeMillis()))%>&#13;End=<%=df.format(new Date(tt.getEndTimeMillis()))%>&#13;&#13;<%= tt.printChildStackTrace() %></textarea>
            </div>
            <script defer="true">
                function showTimeTrace<%=ttHtmlId%>() {
                    elem = document.getElementById('timeTrace<%=ttHtmlId%>');
                    if (elem.style.display == 'none') elem.style.display='block';
                    else elem.style.display='none'
                }
                function expandTimeTrace<%=ttHtmlId%>() {
                    expandElem = document.getElementById('expand_<%=ttHtmlId%>');
                    childrenElem = document.getElementById('children_<%=ttHtmlId%>');
                    if (childrenElem.style.display == 'none') {
                        childrenElem.style.display='block';
                        expandElem.innerHTML = '-';
                    }
                    else {
                        childrenElem.style.display='none';
                        expandElem.innerHTML = '+';
                    }
                }
            </script>
<%
            if (children != null && !children.isEmpty()) {
%>
            <div id="children_<%=ttHtmlId%>" style="display:<%= expandAll ? "block" : "none"%>">
            <% request.setAttribute("timetraces", children); %>
            <jsp:include page="timetraces.jsp" flush="true"/>
            <% request.removeAttribute("timetraces"); %>
            </div>
<%
            }
        }
%>
    </ul>
<%
    }
%>
