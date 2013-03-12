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
<%@ page import="org.jboss.dashboard.profiler.*" %>
<%@ page import="org.jboss.dashboard.commons.misc.Chronometer" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%
    CodeBlockTraces codeBlockTraces = (CodeBlockTraces) request.getAttribute("codetraces");
    String mttParam = (String) request.getAttribute("mintracetime");
    long minTraceTime = (mttParam != null ? Long.parseLong(mttParam) : 0);

    Map<String,CodeBlockTraces> byId = codeBlockTraces.groupById();
    List<CodeBlockTraces> idGroups = new ArrayList(byId.values());
    CodeBlockTracesComparator comp = new CodeBlockTracesComparator();
    comp.addSortCriteria(CodeBlockTracesComparator.CRITERIA_SELF_TIME, CodeBlockTracesComparator.ORDER_DESCENDING);
    Collections.sort(idGroups, comp);

    long ignored = 0;
    for (CodeBlockTraces idTraces : idGroups) {
        long time = idTraces.getSelfTimeMillis();
        if (time < minTraceTime && idGroups.size() > 1) {
            // Ignore insignificant traces.
            ignored += idTraces.size();
            continue;
        }

        String timeStr = Chronometer.formatElapsedTime(time);
        CodeBlockTrace trace = idTraces.get(0);
%>
        [<%= time>500 ? "<span class='skn-error'>" + timeStr + "</span>" : timeStr%>&nbsp;-&nbsp;<%= trace.getType().getDescription() %>
<%
        if (idTraces.size() == 1) {
%>
        , Total=<%=Chronometer.formatElapsedTime(idTraces.getElapsedTimeMillis())%>]<br>
        <span style="font-style:italic;"><%= trace.printContext(false, "=", "<br>")%></span><br><br>
<%
        } else {
%>
        &nbsp;#Traces=<%=idTraces.size()%>, Total=<%=Chronometer.formatElapsedTime(idTraces.getElapsedTimeMillis())%>, Min=<%=Chronometer.formatElapsedTime(idTraces.min().getElapsedTimeMillis())%>, Max=<%=Chronometer.formatElapsedTime(idTraces.max().getElapsedTimeMillis())%>]<br>
        <span style="font-style:italic;"><%=trace.getId() %></span><br><br>
<%
        }
    }
    if (ignored > 0) {
%>
    [<%= ignored%> trace(s) lower than <%=Chronometer.formatElapsedTime(minTraceTime)%> ignored]<br><br>
<%
    }
%>
