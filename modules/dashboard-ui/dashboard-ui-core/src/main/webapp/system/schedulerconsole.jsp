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
<%@ page import="org.jboss.dashboard.scheduler.*" %>
<%@ page import="org.jboss.dashboard.factory.ComponentsContextManager" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Collections" %>
<%
    if (request.getSession().getAttribute("accessGranted") == null) {
        request.setAttribute("redirect", "schedulerconsole.jsp");
%>
        <jsp:include page="sso.jsp" flush="true"/>
<%
        return;
    }
%>
<%!
  public static final String VIEW_RUNNING = "running";
  public static final String VIEW_WAITING = "waiting";
  public static final String VIEW_MISFIRED = "misfired";
  public static final String VIEW_ALL = "all";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>System Console</title>
    <link rel="stylesheet" href="styles.css" type="text/css">
</head>
<body>
<h2><a href="console.jsp">System</a>&nbsp;&gt;&nbsp;Scheduler</h2>
<%
    ComponentsContextManager.startContext();
    try {
        Scheduler scheduler = Scheduler.lookup();
        String view = VIEW_ALL;
        String sortProp = SchedulerTaskComparator.TIME_TO_FIRE;
        int sortOrder = SchedulerTaskComparator.ORDER_ASCENDING;
        boolean refreshOn = false;
        String viewParam = (String) session.getAttribute("sc_view");
        String sortPropParam = (String) session.getAttribute("sc_sortProperty");
        Integer sortOrderParam = (Integer) session.getAttribute("sc_sortOrder");
        String refreshOnParam = (String) session.getAttribute("sc_refreshOn");
        if (viewParam != null) view = viewParam;
        if (sortPropParam != null) sortProp = sortPropParam;
        if (sortOrderParam != null) sortOrder = sortOrderParam;
        if (refreshOnParam != null) refreshOn = true;

        // Process request
        String action = request.getParameter("action");
        if (action != null) {
            if (action.equals("switchON")) {
                if (scheduler.isPaused()) scheduler.resume();
            }
            if (action.equals("switchOFF")) {
                if (!scheduler.isPaused()) scheduler.pause();
            }
            if (action.equals("refreshON")) {
                refreshOn = true;
                session.setAttribute("sc_refreshOn", "true");
            }
            if (action.equals("refreshOFF")) {
                refreshOn = false;
                session.removeAttribute("sc_refreshOn");
            }
            if (action.equals("changeView")) {
                view = request.getParameter("view");
                session.setAttribute("sc_view", view);
            }
            if (action.startsWith("poolSize")) {
                try {
                    int size = Integer.parseInt(action.substring("poolSize".length()));
                    scheduler.setMaxThreadPoolSize(size);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            if (action.startsWith("cancel")) {
                String key = request.getParameter("key");
                if (key != null) scheduler.unschedule(key);
            }
            if (action.startsWith("fire")) {
                String key = request.getParameter("key");
                if (key != null) scheduler.fireTask(key);
            }
            if (action.startsWith("cancelAll")) {
                scheduler.unscheduleAll();
            }
            if (action.startsWith("sortBy")) {
                try {
                    sortProp = request.getParameter("sortProperty");
                    sortOrder = Integer.parseInt(request.getParameter("sortOrder"));
                    session.setAttribute("sc_sortProperty", sortProp);
                    session.setAttribute("sc_sortOrder", sortOrder);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        String paused = scheduler.isPaused() ? "ON" : "OFF";
        List<SchedulerTask> tasks = null;
        if (view.equals(VIEW_MISFIRED)) tasks = scheduler.getMisfiredTasks();
        else if (view.equals(VIEW_RUNNING)) tasks = scheduler.getRunningTasks();
        else if (view.equals(VIEW_WAITING)) tasks = scheduler.getWaitingTasks();
        else tasks = scheduler.getScheduledTasks();

        // Sort the tasks
        SchedulerTaskComparator comp = new SchedulerTaskComparator();
        comp.addSortCriteria(sortProp, sortOrder);
        Collections.sort(tasks, comp);
%>
    <% if (refreshOn) { %>
    <script type="text/javascript">
        setTimeout('window.location=\'schedulerconsole.jsp\'', '1000');
    </script>
    <% } %>
    <table border="0" cellpadding="0" cellspacing="5">
        <tr>
            <td align="left">Scheduler enabled</td>
            <td align="left">
                <form action="schedulerconsole.jsp" method="post">
                    <input type="hidden" name="action" value="switch<%=paused%>"/>
                    <input type="checkbox" class="skn-input" <%= !scheduler.isPaused() ? "checked" : "" %> onchange="this.form.submit();" />
                </form>
            </td>
        </tr>
        <tr>
            <td>Scheduler pool size</td>
            <td><input style="width:150px" class="skn-input" type="text" value="<%=scheduler.getMaxThreadPoolSize()%>" onchange="window.location='schedulerconsole.jsp?action=poolSize' + this.value">(<100)</td>
        </tr>
        <tr><td>Tasks to view</td>
            <td>
                <form action="schedulerconsole.jsp?action=changeView" method="post">
                <select class="skn-input" name="view" onchange="this.form.submit();" style="width:150px">
                    <option value="<%=VIEW_ALL%>" <%= view.equals(VIEW_ALL) ? "selected" : "" %>>All</option>
                    <option value="<%=VIEW_RUNNING%>" <%= view.equals(VIEW_RUNNING) ? "selected" : "" %>>RUNNING</option>
                    <option value="<%=VIEW_WAITING%>" <%= view.equals(VIEW_WAITING) ? "selected" : "" %>>WAITING</option>
                    <option value="<%=VIEW_MISFIRED%>" <%= view.equals(VIEW_MISFIRED) ? "selected" : "" %>>MISFIRED</option>
                </select>
                </form>
            </td>
        </tr>
        <tr>
            <td align="left">Sort tasks by</td>
            <td align="left">
                <form action="schedulerconsole.jsp?action=sortBy" method="post">
                    <select class="skn-input" name="sortProperty" onchange="this.form.submit();" style="width:150px">
                        <option value="<%=SchedulerTaskComparator.TIME_TO_FIRE%>" <%= SchedulerTaskComparator.TIME_TO_FIRE.equals(sortProp) ? "selected" : "" %>>Time to fire</option>
                    </select>
                    <select class="skn-input" name="sortOrder" onchange="this.form.submit();">
                        <option value="<%=SchedulerTaskComparator.ORDER_ASCENDING%>" <%= SchedulerTaskComparator.ORDER_ASCENDING == sortOrder ? "selected" : "" %>>Ascending</option>
                        <option value="<%=SchedulerTaskComparator.ORDER_DESCENDING%>" <%= SchedulerTaskComparator.ORDER_DESCENDING == sortOrder ? "selected" : "" %>>Descending</option>
                    </select>
                </form>
            </td>
        </tr>
        <tr>
            <td align="left">Auto&nbsp;<a href="schedulerconsole.jsp?view=<%=view%>">Refresh</a></td>
            <td align="left">
                <form action="schedulerconsole.jsp" method="post">
                    <input type="hidden" name="action" value="<%= refreshOn ? "refreshOFF" : "refreshON" %>"/>
                    <input type="checkbox" class="skn-input" <%= refreshOn ? "checked" : "" %> onchange="this.form.submit();" />
                </form>
            </td>
        </tr>
    </table><br/>
Task scheduled <%= tasks.size() %> (in queue <%= scheduler.getNumberOfScheduledTasksInQueue() %>).&nbsp;
<% if (tasks.size() > 0) { %> <a href="schedulerconsole.jsp?action=cancelAll&view=<%=view%>" onclick="return window.confirm('Will cancel all the scheduled tasks. Are you sure&#63;');">Cancel All</a><% } %>
<%
    if (tasks.size() > 0) {
%>
    <table border="0" cellspacing="1" cellpadding="2" class="skn-table_border">
        <tr class="skn-table_header">
            <td width="100px" align="center">STATUS</td>
            <td width="200px" align="center">TIME TO FIRE</td>
            <td width="150px" align="center">FIRE DATE</td>
            <td width="500px" align="left">TASK</td>
            <td width="100px" align="center"></td>
        </tr>
<%
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        String css = "skn-odd_row";
        for (SchedulerTask task : tasks) {
            if (css.equals("skn-odd_row")) css = "skn-even_row";
            else css = "skn-odd_row";
            String status = "WAITING";
            if (task.isRunning()) status = "RUNNING";
            else if (task.isCancelled()) status = "CANCELED";
            else if (task.isDone()) status = "COMPLETED";
            else if (task.isMisfired()) status = "MISFIRED";
%>
        <tr class="<%= css %>">
            <td align="center"><%= status%></td>
            <td align="center"><%= status.equals("WAITING")  || status.equals("MISFIRED") ? task.printTimeToFire() : "" %></td>
            <td align="center"><%= status.equals("WAITING")  || status.equals("MISFIRED") ? dateFormat.format(new Date(System.currentTimeMillis() + task.getMillisTimeToFire())) : "" %></td>
            <td align="left"><div style="width:490px;height:14px;text-align:left;overflow:hidden;vertical-align:middle;" title="<%= task %>"><%= task %></div></td>
            <td align="center"><% if (status.equals("WAITING") || status.equals("MISFIRED")) { %>&nbsp;
                <a href="schedulerconsole.jsp?action=fire&key=<%= task.getKey() %>" onclick="return window.confirm('Are you sure?');">Fire</a>&nbsp;
                <a href="schedulerconsole.jsp?action=cancel&key=<%= task.getKey() %>" onclick="return window.confirm('Are you sure?');">Cancel</a>
                <% } %>
            </td>
        </tr>
<%      } %>
    </table>
<%
        }
    } finally {
        ComponentsContextManager.clearContext();
    }
%>
</body>
</html>
