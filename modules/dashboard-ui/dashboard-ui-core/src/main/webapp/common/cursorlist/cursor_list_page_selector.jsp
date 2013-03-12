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
<%@ include file="../global.jsp" %>
<%
    // Retrieve view parameters
    CursoredList cursor = (CursoredList) request.getAttribute("cursorList"); // The cursor to display
    String handler = (String) request.getAttribute("cursorHandler"); // The handler of cursor action
    String action = (String) request.getAttribute("cursorAction"); // The handler action that captures cursor events
    String params = (String) request.getAttribute("cursorParams"); // Some action params (optional)
    String htmlTagId = (String) request.getAttribute("htmlTagId"); // The HTML tag identifier where cursor action result will be inserted via remote scripting.
    int npages = cursor.getNumberOfPages();
    if (npages > 1) {
        // Set page window equals to 5 pages displayed at the same time.
        int currentPage = cursor.getPageNumber();
        int firstNavigablePage = currentPage - 2;
        int lastNavigablePage = currentPage + 2;

        // Window delimiters adjustements.
        // -1 -0 [1 2 3 4 5] 6 7 8 9 10 +11 +12
        if (firstNavigablePage < 1) {
            lastNavigablePage += 1 - firstNavigablePage;
            firstNavigablePage = 1;
        }
        if (lastNavigablePage > npages) {
            firstNavigablePage -= lastNavigablePage - npages;
            if (firstNavigablePage < 1) firstNavigablePage = 1;
            lastNavigablePage = npages;
        }
%>
<i18n:message key="ui.cursor.pages">!!! P&aacute;ginas:</i18n:message>:&nbsp;
<input type='hidden' id='_currentFormField'>
<%
    // First page link (only if needed)
    if (firstNavigablePage > 1) {

        // Important note: the javascript inside the onclick method can not be defined into a new javascript
        // function. The reason is that this page is loaded into the browser after a remote scripting request and
        // you must know that javascript variables/functions defined in a HTML fragment served via remote
        // scripting are not processed by the browser.
%>
<a href="#"
   onclick="currentForm = document.getElementById('_currentFormField').form; url = '<mvc:link handler="<%= handler %>" action="<%= action %>"/>'; params = getFormFieldsInHttpGetFormat(currentForm) + '&<%= params %>' + '&page=1'; return httpRequest('POST', url, params, '<%= htmlTagId %>');">
    &lt;</a>
<%
    }
    // Page window
    for (int i = firstNavigablePage; i <= lastNavigablePage; i++) {
        if (currentPage == i) {
%>
<b><%= i %></b>
<%
} else {
%>
<a href="#"
   onclick="currentForm = document.getElementById('_currentFormField').form; url = '<mvc:link handler="<%= handler %>" action="<%= action %>"/>'; params = getFormFieldsInHttpGetFormat(currentForm) + '&<%= params %>' + '&page=<%= i %>'; return httpRequest('POST', url, params, '<%= htmlTagId %>');">
    <%= i %></a>
<%
        }
    }
    // Last page link (only if needed)
    if (lastNavigablePage < npages) {
%>
<a href="#"
   onclick="currentForm = document.getElementById('_currentFormField').form; url = '<mvc:link handler="<%= handler %>" action="<%= action %>"/>'; params = getFormFieldsInHttpGetFormat(currentForm) + '&<%= params %>' + '&page=<%= npages %>'; return httpRequest('POST', url, params, '<%= htmlTagId %>');">
    &gt;</a>
<%
        }
    }
%>