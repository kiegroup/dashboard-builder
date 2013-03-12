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
<%@ page import="org.jboss.dashboard.profiler.Profiler" %>
<%@ page import="org.jboss.dashboard.profiler.*" %>
<%@ page import="org.jboss.dashboard.factory.ComponentsContextManager" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.util.*" %>
<%
    if (request.getSession().getAttribute("accessGranted") == null) {
        request.setAttribute("redirect", "threadconsole.jsp");
%>
        <jsp:include page="sso.jsp" flush="true"/>
<%
        return;
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
<h2><a href="console.jsp">System</a>&nbsp;&gt;&nbsp;Thread Profiler</h2>
<%
    ComponentsContextManager.startContext();
    try {
        Profiler profiler = Profiler.lookup();
        ThreadProfileFilter threadFilter = profiler.getCompletedThreadsFilter();
        String sortProp = ThreadProfileComparator.BEGIN_DATE;
        int sortOrder = ThreadProfileComparator.ORDER_DESCENDING;
        boolean settingsOn = false;
        boolean detailsOn = false;
        boolean refreshOn = false;
        String filterProp = threadFilter.getPropertyIds().length == 0 ? null : threadFilter.getPropertyIds()[0];
        String filterValue = filterProp != null ? threadFilter.getExtraInfo(filterProp) : "";
        List<String> allPropNamesSorted = new ArrayList(ThreadProfile.getAllContextPropertyNames());
        Collections.sort(allPropNamesSorted);

        // Get properties status from session
        String sortPropParam = (String) session.getAttribute("tc_sortProperty");
        Integer sortOrderParam = (Integer) session.getAttribute("tc_sortOrder");
        String settingsOnParam = (String) session.getAttribute("tc_settingsOn");
        String detailsOnParam = (String) session.getAttribute("tc_detailsOn");
        String refreshOnParam = (String) session.getAttribute("tc_refreshOn");
        if (sortPropParam != null) sortProp = sortPropParam;
        if (sortOrderParam != null) sortOrder = sortOrderParam;
        if (detailsOnParam != null) detailsOn = true;
        if (refreshOnParam != null) refreshOn = true;
        if (settingsOnParam != null) settingsOn = true;

        // Process request
        String action = request.getParameter("action");
        if (action != null) {
            if (action.equals("removeAll")) {
                profiler.removeAllThreads();
            }
            if (action.equals("removeSelected")) {
                for (ThreadProfile tp : profiler.getCompletedThreads()) {
                    boolean isSelected = Boolean.valueOf(request.getParameter("selectThread_" + tp.hashCode()));
                    if (isSelected) profiler.removeThread(tp);
                }
            }
            if (action.equals("viewMixedLogs")) {
                Set<ThreadProfile> mixedLogThreads = new HashSet<ThreadProfile>();
                for (ThreadProfile tp : profiler.getCompletedThreads()) {
                    boolean isSelected = Boolean.valueOf(request.getParameter("selectThread_" + tp.hashCode()));
                    if (isSelected) mixedLogThreads.add(tp);
                }
                if (mixedLogThreads.size() < 2) {
%>
                <script type="text/javascript">
                    alert('<%= mixedLogThreads.size() %> threads selected.\nPlease select at least two or more threads.');
                </script>
<%
                } else {
                    session.setAttribute("targetThreads", mixedLogThreads);
                    session.removeAttribute("mixedLogs");
%>
                <script type="text/javascript">
                    window.location = 'mixedlogs.jsp';
                </script>
<%
                }
            }
            if (action.equals("switchON")) {
                if (!profiler.isRunning()) profiler.turnOn();
            }
            if (action.equals("switchOFF")) {
                if (profiler.isRunning()) profiler.turnOff();
            }
            if (action.equals("settingsON")) {
                settingsOn = true;
                session.setAttribute("tc_settingsOn", "true");
            }
            if (action.equals("settingsOFF")) {
                settingsOn = false;
                session.removeAttribute("tc_settingsOn");
            }
            if (action.equals("refreshON")) {
                refreshOn = true;
                session.setAttribute("tc_refreshOn", "true");
            }
            if (action.equals("refreshOFF")) {
                refreshOn = false;
                session.removeAttribute("tc_refreshOn");
            }
            if (action.equals("detailsON")) {
                detailsOn = true;
                session.setAttribute("tc_detailsOn", "true");
            }
            if (action.equals("detailsOFF")) {
                detailsOn = false;
                session.removeAttribute("tc_detailsOn");
            }
            if (action.startsWith("minThreadTime")) {
                try {
                    long millis = Long.parseLong(action.substring("minThreadTime".length()));
                    if (millis >= 0) profiler.setCompletedThreadsMinTimeMillis(millis);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            if (action.startsWith("completedThreadsSize")) {
                try {
                    int size = Integer.parseInt(action.substring("completedThreadsSize".length()));
                    if (size > 0) profiler.setCompletedThreadsMaxSize(size);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            if (action.startsWith("storeThreadsWithErrors")) {
                if (profiler.isCompletedThreadsErrorsEnabled()) profiler.setCompletedThreadsErrorsEnabled(false);
                else profiler.setCompletedThreadsErrorsEnabled(true);
            }
            if (action.startsWith("sortProperty")) {
                sortProp = request.getParameter("sortProperty");
                session.setAttribute("tc_sortProperty", sortProp);
            }
            if (action.startsWith("sortOrder")) {
                try {
                    sortOrder = Integer.parseInt(request.getParameter("sortOrder"));
                    session.setAttribute("tc_sortOrder", sortOrder);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            if (action.startsWith("filterProp")) {
                String filterPropParam = request.getParameter("filterProperty");
                threadFilter.removeAllProperty();
                filterProp = null;
                filterValue = null;
                if (!filterPropParam.equals("_resetFilter")) {
                    threadFilter.addProperty(filterProp = filterPropParam, filterValue = "*");
                    threadFilter.setExtraInfo(filterProp, filterValue);
                }
            }
            if (action.startsWith("filterValue")) {
                filterValue = request.getParameter("filterValue");
                threadFilter.addProperty(filterProp, filterValue);
                threadFilter.setExtraInfo(filterProp, filterValue);
            }
            if (action.startsWith("samplingInterval")) {
                try {
                    long millis = Long.parseLong(action.substring("samplingInterval".length()));
                    if (millis >= 50) profiler.setIdleTimeInMillis(millis);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        // Render the view.
        String status = profiler.isRunning() ? "OFF" : "ON";

        // Sort the thread list
        List<ThreadProfile> allTxs = profiler.getFilteredThreads();
        ThreadProfileComparator comp = new ThreadProfileComparator();
        comp.addSortCriteria(sortProp, sortOrder);
        Collections.sort(allTxs, comp);
%>
<% if (refreshOn) { %>
<script type="text/javascript">
    setTimeout('window.location=\'threadconsole.jsp\'', '1000');
</script>
<% } %>
<% if (settingsOn) { %>
<table border="0" cellspacing="1" cellpadding="2">
    <tr><td align="left" valign="top">
        <a href="threadconsole.jsp?action=settingsOFF">&lt;&lt; Hide settings</a><br/>
        <table width="300px" border="0" cellspacing="0" cellpadding="2" class="skn-table_border">
            <tr>
                <td colspan="2" align="left"><b>Target threads</b></td>
            </tr>
            <tr>
                <td align="left">With errors</td>
                <td align="left">
                    <form action="threadconsole.jsp" method="post">
                        <input type="hidden" name="action" value="storeThreadsWithErrors<%= profiler.isCompletedThreadsErrorsEnabled() ? "OFF" : "ON" %>"/>
                        <input type="checkbox" class="skn-input" <%= profiler.isCompletedThreadsErrorsEnabled() ? "checked" : "" %> onchange="this.form.submit();" />
                    </form>
                </td>
            </tr>
            <tr>
                <td align="left">Longer than (&gt;0ms)</td>
                <td><input style="width:100px" class="skn-input" type="text" value="<%=profiler.getCompletedThreadsMinTimeMillis()%>" onchange="window.location='threadconsole.jsp?action=minThreadTime' + this.value"></td>
            </tr>
            <tr>
                <td align="left">
                    <form action="threadconsole.jsp?action=filterProp" method="post">
                        <select class="skn-input" name="filterProperty" onchange="this.form.submit();" style="width:150px">
                            <option value="_resetFilter">- Filter by -</option>
                            <% for (String propName : allPropNamesSorted) { %>
                            <option value="<%= propName %>" <%= propName.equals(filterProp) ? "selected" : "" %>><%= propName %></option>
                            <% } %>
                        </select>
                    </form>
                </td>
                <td>
                    <% if (filterProp != null) { %>
                    <form action="threadconsole.jsp?action=filterValue" method="post">
                        <input type="text" class="skn-input" style="width:100px" name="filterValue" value="<%= StringUtils.isBlank(filterValue) ? "" : filterValue %>" onchange="this.form.submit();">
                    </form>
                    <% } %>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="left"><b>Display options</b></td>
            </tr>
            <tr>
                <td align="left">Full context mode</td>
                <td align="left">
                    <form action="threadconsole.jsp" method="post">
                        <input type="hidden" name="action" value="<%= detailsOn ? "detailsOFF" : "detailsON" %>"/>
                        <input type="checkbox" class="skn-input" <%= detailsOn ? "checked" : "" %> onchange="this.form.submit();" />
                    </form>
                </td>
            </tr>
            <tr>
                <td align="left">Thread list size (&gt;0)</td>
                <td><input style="width:100px" class="skn-input" type="text" value="<%=profiler.getCompletedThreadsMaxSize()%>" onchange="window.location='threadconsole.jsp?action=completedThreadsSize' + this.value"></td>
            </tr>
            <tr>
                <td align="left">Sort by</td>
                <td align="left">
                    <form action="threadconsole.jsp?action=sortProperty" method="post">
                        <select class="skn-input" name="sortProperty" onchange="this.form.submit();" style="width:100px">
                            <option value="<%=ThreadProfileComparator.BEGIN_DATE%>" <%= ThreadProfileComparator.BEGIN_DATE.equals(sortProp) ? "selected" : "" %>>Begin date</option>
                            <option value="<%=ThreadProfileComparator.END_DATE%>" <%= ThreadProfileComparator.END_DATE.equals(sortProp) ? "selected" : "" %>>End date</option>
                            <option value="<%=ThreadProfileComparator.ELAPSED_TIME%>" <%= ThreadProfileComparator.ELAPSED_TIME.equals(sortProp) ? "selected" : "" %>>Elapsed time</option>
                            <% for (String propName : allPropNamesSorted) { %>
                            <option value="<%=propName%>" <%= propName.equals(sortProp) ? "selected" : "" %>><%= propName %></option>
                            <% } %>
                        </select>
                    </form>
                </td>
            </tr>
            <tr>
                <td align="left">Sort order</td>
                <td align="left">
                    <form action="threadconsole.jsp?action=sortOrder" method="post">
                        <select class="skn-input" name="sortOrder" onchange="this.form.submit();" style="width:100px">
                            <% if (sortProp == null) { %><option value="_none">- Select -</option><% } %>
                            <option value="<%=ThreadProfileComparator.ORDER_ASCENDING%>" <%= ThreadProfileComparator.ORDER_ASCENDING == sortOrder ? "selected" : "" %>>Ascending</option>
                            <option value="<%=ThreadProfileComparator.ORDER_DESCENDING%>" <%= ThreadProfileComparator.ORDER_DESCENDING == sortOrder ? "selected" : "" %>>Descending</option>
                        </select>
                    </form>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="left"><b>Profiler options</b></td>
            </tr>
            <tr>
                <td align="left">Sampling enabled</td>
                <td align="left">
                    <form action="threadconsole.jsp" method="post">
                        <input type="hidden" name="action" value="switch<%=status%>"/>
                        <input type="checkbox" class="skn-input" <%= profiler.isRunning() ? "checked" : "" %> onchange="this.form.submit();" />
                    </form>
                </td>
            </tr>
            <tr>
                <td align="left">Sampling interval (&gt;50ms)</td>
                <td><input style="width:100px" class="skn-input" type="text" value="<%=profiler.getIdleTimeInMillis()%>" onchange="window.location='threadconsole.jsp?action=samplingInterval' + this.value"></td>
            </tr>
        </table>
    </td>
    <td valign="top">
<% } %>
        <table border="0" cellspacing="1" cellpadding="2">
            <% if (!settingsOn) { %>
            <tr>
                <td>
                    <a href="threadconsole.jsp?action=settingsON">&gt;&gt; Edit settings</a><br/>
                </td>
            </tr>
            <% } %>
<%
            String width = settingsOn ? "950" : "1200";
            String threadListPage = "thread_compactlist.jsp";
            if (detailsOn) threadListPage = "thread_detailedlist.jsp";
            request.setAttribute("threadList", allTxs);
            request.setAttribute("filterProperty", filterProp);
            request.setAttribute("filter", allTxs);
            request.setAttribute("width", width);
%>
            <tr>
                <td width="100%" valign="top">
                    <form action="threadconsole.jsp" method="post">
                        <input type="hidden" name="action" value="<%= refreshOn ? "refreshOFF" : "refreshON" %>"/>
                        <%= allTxs.size() %> thread(s) listed.&nbsp;
                        Auto&nbsp;<a href="threadconsole.jsp" style="text-decoration:underline">Refresh</a>&nbsp;<input type="checkbox" class="skn-input" <%= refreshOn ? "checked" : "" %> onchange="this.form.submit();" />&nbsp;&nbsp;
                        <% if (!allTxs.isEmpty()) { %><a href="#" onclick="selectAllThreads(); return false;" style="text-decoration:underline">Select All</a>&nbsp;&nbsp;<a href="threadconsole.jsp?action=removeAll" style="text-decoration:underline">Remove All</a><% } %>                        
                    </form>
                    <form action="threadconsole.jsp" method="post" id="threadListForm">
                        <input type="hidden" name="action" value="" id="threadListAction"/>
                        <% if (!allTxs.isEmpty()) { %><jsp:include page="<%= threadListPage %>" flush="true" /><% } %>
                    </form>
                </td>
            </tr>
            <tr id="selectedThreadsSection" style="display:none;">
                <td width="100%" valign="top">
                    <a href="#" onclick="removeSelectedThreads(); return false;" style="text-decoration:underline">Remove Selected</a>&nbsp;
                    <a href="#" onclick="viewMixedLogs(); return false;" style="text-decoration:underline">View mixed logs</a>&nbsp;
                </td>
            </tr>
         </table>
<%
        if (settingsOn) {
%>
    </td></tr>
    </table>
<%
        }
    } finally {
        ComponentsContextManager.clearContext();
    }
%>
<script type="text/javascript">
    function onThreadSelected() {
        document.getElementById('selectedThreadsSection').style.display = 'block';
    }
    
    function selectAllThreads() {
        form = document.getElementById('threadListForm');
        for (var i = 0; i < form.length; i++) {
            field = form[i];
            if (!field.name || field.name=='') continue;
            if (field.type == 'checkbox') {
                field.checked = true;
                onThreadSelected();
            }
        }
    }

    function removeSelectedThreads() {
        form = document.getElementById('threadListForm');
        document.getElementById('threadListAction').value = 'removeSelected';
        form.submit();
    }

    function viewMixedLogs() {
        form = document.getElementById('threadListForm');
        document.getElementById('threadListAction').value = 'viewMixedLogs';
        form.submit();
    }
</script>
</body>
</html>
