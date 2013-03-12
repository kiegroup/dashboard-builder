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
<%
    String mttParam = (String) request.getAttribute("mintracetime");
    long minTraceTime = (mttParam != null ? Long.parseLong(mttParam) : 0);
    CodeBlockTraces codeBlockTraces = (CodeBlockTraces) request.getAttribute("codetraces");
    boolean expandAll = request.getAttribute("expandAll") != null;
    if (codeBlockTraces.size() > 0) {
%>
    <ul style="list-style-type:none;margin-left:15px;padding-left:0px;">
<%
        // Iterate traces
        for (int i=0; i<codeBlockTraces.size(); i++) {
            CodeBlockTrace trace = codeBlockTraces.get(i);
            CodeBlockTraces children = trace.getChildren();
            long totalTime = trace.getElapsedTimeMillis();
            long selfTime = trace.getSelfTimeMillis();
            String traceType = trace.getType().getId();
            String traceDescr = trace.getDescription();
            if (traceDescr != null && traceDescr.length() > 60) traceDescr = "..." + traceDescr.substring(traceDescr.length()-60);
            traceDescr = "&quot;" + traceDescr + "&quot;";
            long traceHtmlId = (long) (System.currentTimeMillis() * Math.random());
            if (children == null && selfTime < minTraceTime) continue; // Ignore insignificant traces

            // Calculate the children to be displayed.
            CodeBlockTraces childrenToDisplay = children;
            boolean childrenSummaryOn = false;
            long childrenElapsedTime = trace.getChildrenElapsedTimeMillis();
            if (children != null) {
                Map<String,CodeBlockTraces> childrenById = children.groupById();
                childrenSummaryOn = (childrenById.size() < children.size());
                if (childrenSummaryOn) {
                    // If summary is on then leaf children must not be displayed again.
                    childrenToDisplay = new CodeBlockTraces();
                    for (int j=0; j<children.size(); j++) {
                        CodeBlockTrace child = children.get(j);
                        if  (child.getChildren() != null) childrenToDisplay.add(child);
                     }
                 }
            }
            boolean showChildren = childrenToDisplay != null && childrenToDisplay.size() > 0
                                    && (trace.isRunning() || childrenElapsedTime >= minTraceTime);
%>
        <li>
<%
            if (!showChildren) {
%>
            <span style="font-size:large;">&middot;</span>
<%
            } else {
%>
            <a style="font-size:large;" href="#" onclick="expandCodeTrace<%=traceHtmlId%>();return false;" id="expand_<%=traceHtmlId%>"><%= expandAll ? "-" : "+"%></a>
<%
            }
%>                    
            <%= trace.isRunning() ? "<span class='skn-error'>RUNNING</span>" : "" %>&nbsp;
            <%= selfTime>500 ? "<span class='skn-error'>" + Chronometer.formatElapsedTime(totalTime) + "</span>" : Chronometer.formatElapsedTime(totalTime)%>&nbsp;-&nbsp;<%= traceType %>&nbsp;-&nbsp;
            <a href="#" onclick="showTraceDetails_<%=traceHtmlId%>();return false;" title="<%= trace.getDescription() %>"><%= traceDescr %></a>
            <div id="trace_<%=traceHtmlId%>" style="display:none;font-size:x-small;">
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Total time=<%= Chronometer.formatElapsedTime(totalTime) %><br>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Self time=<%= selfTime>500 ? "<span class='skn-error'>" + Chronometer.formatElapsedTime(selfTime) + "</span>" : Chronometer.formatElapsedTime(selfTime) %><br>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style="font-style:italic;"><%= trace.printContext(false, "=", "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", 2) %></span></div>
            <script defer="true">
                function showTraceDetails_<%=traceHtmlId%>() {
                    elem = document.getElementById('trace_<%=traceHtmlId%>');
                    if (elem.style.display == 'none') elem.style.display='block';
                    else elem.style.display='none'
                }
                function expandCodeTrace<%=traceHtmlId%>() {
                    expandElem = document.getElementById('expand_<%=traceHtmlId%>');
                    childrenElem = document.getElementById('children_<%=traceHtmlId%>');
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
            // Show the trace children (if any).
            if (childrenSummaryOn) {
%>
                <% request.setAttribute("codetraces", children); %>
                <% request.setAttribute("hidesingles", "true"); %>
                <jsp:include page="codetracesbytype.jsp" flush="true"/>
                <% request.removeAttribute("hidesingles"); %>
                <% request.removeAttribute("codetraces"); %>
<%
             }
             if (showChildren) {
%>
                <div id="children_<%=traceHtmlId%>" style="display:<%= expandAll ? "block" : "none"%>">
                <% request.setAttribute("codetraces", childrenToDisplay); %>
                <jsp:include page="codetracestree.jsp" flush="true"/>
                <% request.removeAttribute("codetraces"); %>
                </div>
<%
            }
        }
%>
    </ul>
<%
    }
%>
