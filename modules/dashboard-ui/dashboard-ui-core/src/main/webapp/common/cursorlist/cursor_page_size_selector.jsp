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
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ page import="org.jboss.dashboard.commons.collections.CursoredList" %>
<%@ page import="java.util.StringTokenizer" %>
<%@ include file="../global.jsp" %>
<%
    // Retrieve view parameters
    CursoredList cursor = (CursoredList) request.getAttribute("cursorList"); // The cursor to display
    String handler = (String) request.getAttribute("cursorHandler"); // The handler of cursor action
    String action = (String) request.getAttribute("cursorAction"); // The handler action that captures cursor events
    String params = (String) request.getAttribute("cursorParams"); // Some action params (optional)
    String htmlTagId = (String) request.getAttribute("htmlTagId"); // The HTML tag identifier where cursor action result will be inserted via remote scripting.
    String allowedSizes = (String) request.getAttribute("pageSizes"); // Page sizes allowed (comma-separated string)
%>
<input type='hidden' id='_currentFormField'>
<i18n:message key="ui.cursor.pageSize">!!! Registros por p&aacute;gina</i18n:message>
<input type="hidden" name="pageSize" value="<%=cursor.getPageSize()%>">
<select name="pageSizeSelect" class="skn-input"
        onchange="pageSize.value=this[this.selectedIndex].value;url = '<mvc:link handler="<%= handler %>" action="<%= action %>"/>'; params = getFormFieldsInHttpGetFormat(this.form) + '&<%= params %>' + '&page=1'; return httpRequest('POST', url, params, '<%= htmlTagId %>');">
    <%
        if (allowedSizes == null) allowedSizes = "10, 30, 50";
        StringTokenizer strtok = new StringTokenizer(allowedSizes, " ,");
        while (strtok.hasMoreTokens()) {
            int allowedSize = Integer.parseInt(strtok.nextToken());
    %>
    <option value="<%= allowedSize %>" <%= cursor.getPageSize() == allowedSize ? "selected" : "" %>><%= allowedSize %></option>
    <%
        }
    %>
</select>