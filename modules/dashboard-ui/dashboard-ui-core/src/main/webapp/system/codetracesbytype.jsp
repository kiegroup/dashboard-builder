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
    CodeBlockTraces codeBlockTraces = (CodeBlockTraces) request.getAttribute("codetraces");
    boolean showHeader = request.getAttribute("showheader") != null;
    boolean hideSingles = request.getAttribute("hidesingles") != null;
    String mttParam = (String) request.getAttribute("mintracetime");
    long minTraceTime = (mttParam != null ? Long.parseLong(mttParam) : 0);
    long tracesHtmlId = (long) (System.currentTimeMillis() * Math.random());

    Map<CodeBlockType,CodeBlockTraces> byType = codeBlockTraces.groupByType();
    boolean showTypes = false;
    for (CodeBlockType type : byType.keySet()) {
        CodeBlockTraces typeTraces = byType.get(type);
        long total = typeTraces.getElapsedTimeMillis();

        // Ignore insignificant traces.
        if (total >= minTraceTime) showTypes = true;
    }
    if (showTypes) {
%>
<table class="skn-table_border" cellpadding="0" cellspacing="0" border="0">
<%
    if (showHeader) {
%>
    <tr class="skn-table_header">
        <td align="left"></td>
        <td align="center" align="left">&nbsp;&nbsp;#Traces&nbsp;&nbsp;</td>
        <td align="left" align="left">&nbsp;&nbsp;Total&nbsp;&nbsp;</td>
        <td align="left" align="left">&nbsp;&nbsp;Self&nbsp;&nbsp;</td>
        <td align="left" align="left">&nbsp;&nbsp;Min.&nbsp;&nbsp;</td>
        <td align="left" align="left">&nbsp;&nbsp;Max.&nbsp;&nbsp;</td>
    </tr>
<%
    }
    if (codeBlockTraces.size() == 0) {
%>
    <tr>
        <td align="left" colspan="5"><span class="skn-error">No traces</span></td>
    </tr>
<%
    } else {
        for (CodeBlockType type : byType.keySet()) {
            CodeBlockTraces typeTraces = byType.get(type);
            long typeTracesHtmlId = (long) (System.currentTimeMillis() * Math.random());
            long self = typeTraces.getSelfTimeMillis();
            long total = showHeader ? typeTraces.getElapsedTimeMillis(type, true) : typeTraces.getElapsedTimeMillis();

            // Ignore insignificant traces.
            if (total < minTraceTime) continue;

            // Ignore single traces.
            if (hideSingles) {
                boolean isSingle = (typeTraces.size() == 1 && typeTraces.get(0).getChildren() != null);
                if (isSingle) continue;
            }
%>
    <tr>
        <td valign="center" align="left">&nbsp;&nbsp;
            <a href="#" <%= showHeader ? "style=\"font-size:large;\"" : "" %>onclick="showTraces_<%=tracesHtmlId%>('<%=typeTracesHtmlId%>');return false;" id="expand_<%=typeTracesHtmlId%>">+</a>&nbsp;
            <% if (!showHeader) { %>
                <%= self>500 ? "<span class='skn-error'>" + Chronometer.formatElapsedTime(self) + "</span>" : Chronometer.formatElapsedTime(self)%>&nbsp;-&nbsp;
                <%= typeTraces.size() %>&nbsp;
            <% } %>
            <%= type.getDescription() %>&nbsp;&nbsp;
            <div id="typeTraces_<%=typeTracesHtmlId%>" style="display:none;font-size:xx-small;">
                <% request.setAttribute("codetraces", typeTraces); %>
                <jsp:include page="codetracesbyid.jsp" flush="true"/>
                <% request.removeAttribute("codetraces"); %>
            </div>&nbsp;&nbsp;
        </td>
        <% if (showHeader) {%>
            <td valign="center" align="center">&nbsp;&nbsp;<%= typeTraces.size() %>&nbsp;&nbsp;</td>
            <td valign="center" align="left">&nbsp;&nbsp;<%= Chronometer.formatElapsedTime(total) %>&nbsp;&nbsp;</td>
            <td valign="center" align="left">&nbsp;&nbsp;<%=!showHeader ? "Self=" : "" %><%= self>500 ? "<span class='skn-error'>" + Chronometer.formatElapsedTime(self) + "</span>" : Chronometer.formatElapsedTime(self)%>&nbsp;&nbsp;</td>
        <% } %>
        <% if (typeTraces.size() > 1) { %>
        <td valign="center" align="left">&nbsp;&nbsp;<%=!showHeader ? "Min=" : "" %><%= Chronometer.formatElapsedTime(typeTraces.min().getSelfTimeMillis()) %>&nbsp;&nbsp;</td>
        <td valign="center" align="left">&nbsp;&nbsp;<%=!showHeader ? "Max=" : "" %><%= Chronometer.formatElapsedTime(typeTraces.max().getSelfTimeMillis()) %>&nbsp;&nbsp;</td>
        <% } %>
    </tr>
<%
        }
    }
%>
</table>
<br>
<div title="" id="traces_<%=tracesHtmlId %>_details" style="display:none;font-size:x-small;"></div>
<script defer="true">
    function showTraces_<%= tracesHtmlId %>(key) {
        detailsElem = document.getElementById('traces_<%= tracesHtmlId %>_details');
        expandElem = document.getElementById('expand_' + key);
        if (detailsElem.title == key) {
            if (detailsElem.style.display == 'none') {
                detailsElem.style.display='block';
                expandElem.innerHTML = '-';
            } else {
                detailsElem.style.display='none';
                expandElem.innerHTML = '+';
            }
        } else {
            oldKey = detailsElem.title;
            if (oldKey != "") document.getElementById('expand_' + oldKey).innerHTML = "+";
            expandElem.innerHTML = '-';
            detailsElem.style.display='block';
            detailsElem.title = key;
            detailsElem.innerHTML = document.getElementById('typeTraces_' + key).innerHTML;
        }
    }
</script>
<% } %>
