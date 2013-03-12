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
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="org.jboss.dashboard.database.hibernate.HibernateSessionFactoryProvider" %>
<%@ page import="java.text.Collator" %>
<%@ page import="org.hibernate.stat.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.jboss.dashboard.factory.ComponentsContextManager" %>
<%@ page import="org.jboss.dashboard.database.hibernate.HibernateInitializer" %>
<%@ page import="org.jboss.dashboard.CoreServices" %>
<%
    if (request.getSession().getAttribute("accessGranted") == null) {
        request.setAttribute("redirect", "hibernateconsole.jsp");
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
<h2><a href="console.jsp">System</a>&nbsp;&gt;&nbsp;Hibernate</h2>
<%!
    static final String VIEW_QUERY_STATS = "queryStats";
    static final String VIEW_ENTITY_STATS = "entityStats";
    static final String VIEW_COLLECTION_STATS = "collectionStats";
    static final String VIEW_2LEVELCACHE_STATS = "2lcacheStats";

    static Date lastUpdate;
    static List<Long> generalStatistics = Collections.synchronizedList(new ArrayList<Long>(9));
    static Map<String,String> viewJSPMap = new HashMap<String,String>();
    static {
        viewJSPMap.put(VIEW_QUERY_STATS, "hbn_querystats.jsp");
        viewJSPMap.put(VIEW_ENTITY_STATS, "hbn_entitystats.jsp");
        viewJSPMap.put(VIEW_COLLECTION_STATS, "hbn_collectionstats.jsp");
        viewJSPMap.put(VIEW_2LEVELCACHE_STATS, "hbn_2lcachestats.jsp");

        for (int i = 0; i < 9; i++) {
            generalStatistics.add(new Long(-1));
        }
    }
%>
<%
    ComponentsContextManager.startContext();
    try {
        String action = request.getParameter("action");
        String view = request.getParameter("view");
        if (view == null) view = VIEW_QUERY_STATS;

        StringBuilder errors = new StringBuilder(512);
        StringBuilder warnings = new StringBuilder(512);
        StringBuilder info = new StringBuilder(512);

        HibernateSessionFactoryProvider hsfp = CoreServices.lookup().getHibernateSessionFactoryProvider();
        SessionFactory sessionFactory = hsfp.getSessionFactory();
        Statistics statistics = sessionFactory.getStatistics();

        if ("activate".equals(action) && !statistics.isStatisticsEnabled()) {
            statistics.setStatisticsEnabled(true);
            info.append("Statistics enabled\n");
        }
        if ("deactivate".equals(action) && statistics.isStatisticsEnabled()) {
            statistics.setStatisticsEnabled(false);
            info.append("Statistics disabled\n");
        }
        if ("freeCaches".equals(action)) {
            HibernateInitializer hibernateInitializer = CoreServices.lookup().getHibernateInitializer();
            hibernateInitializer.evictAllCaches();
            info.append("All caches cleared.\n");
        }

        if (errors.length() > 0) {
%>
        <div class="error"><%=StringEscapeUtils.escapeHtml(errors.toString())%></div><br>
<%
        }
        if (warnings.length() > 0) {
%>
        <div class="warn"><%=StringEscapeUtils.escapeHtml(warnings.toString())%></div><br>
<%
        }
        if (info.length() > 0) {
%>
        <div class="success"><%=StringEscapeUtils.escapeHtml(info.toString())%></div><br>
<%
        }
        boolean active = statistics.isStatisticsEnabled();
        if (active) {
            lastUpdate = new Date();
            generalStatistics.set(0, statistics.getConnectCount());
            generalStatistics.set(1, statistics.getFlushCount());
            generalStatistics.set(2, statistics.getPrepareStatementCount());
            generalStatistics.set(3, statistics.getCloseStatementCount());
            generalStatistics.set(4, statistics.getSessionCloseCount());
            generalStatistics.set(5, statistics.getSessionOpenCount());
            generalStatistics.set(6, statistics.getTransactionCount());
            generalStatistics.set(7, statistics.getSuccessfulTransactionCount());
            generalStatistics.set(8, statistics.getOptimisticFailureCount());
        }
%>
    <table>
        <tr>
            <td align="left">Last update:</td>
            <td align="left"><%=(lastUpdate != null ? lastUpdate.toLocaleString() : "NONE") %></td>
        </tr>
        <tr>
            <td align="left">Current view:</td>
            <td align="left">
                <select class="skn-input" name="view" onchange="window.location='hibernateconsole.jsp?view=' + this.options[selectedIndex].value">
                    <option value="<%=VIEW_QUERY_STATS%>" <%= view.equals(VIEW_QUERY_STATS) ? "selected" : "" %>>Query Cache Statistics</option>
                    <option value="<%=VIEW_ENTITY_STATS%>" <%= view.equals(VIEW_ENTITY_STATS) ? "selected" : "" %>>Entity Cache Statistics</option>
                    <option value="<%=VIEW_COLLECTION_STATS%>" <%= view.equals(VIEW_COLLECTION_STATS) ? "selected" : "" %>>Collection Cache Statistics</option>
                    <option value="<%=VIEW_2LEVELCACHE_STATS%>" <%= view.equals(VIEW_2LEVELCACHE_STATS) ? "selected" : "" %>>2nd Level Cache Statistics</option>
                </select>
            </td>
        </tr>
        <tr>
            <td align="left">Switch Statistics:</td>
            <td align="left">
                <a href="hibernateconsole.jsp?view=<%=view%>&action=<%= active ? "deactivate" : "activate"%>"><%= active ? "OFF" : "ON" %></a>
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
    </table>
<%
        boolean hasGeneral = false;
        for (int i = 0; i < 9; i++) {
            if (generalStatistics.get(i).longValue() > -1) {
                hasGeneral = true;
                break;
            }
        }
        if (hasGeneral) {
%>
    <table>
        <tr>
            <td align="left" width="200px">Connects</td>
            <td align="left">= <%=generalStatistics.get(0)%></td>
        </tr>
        <tr>
            <td align="left">Flushes</td>
            <td align="left">= <%=generalStatistics.get(1)%></td>
        </tr>
        <tr>
            <td align="left">Prepare statements</td>
            <td align="left">= <%=generalStatistics.get(2)%></td>
        </tr>
        <tr>
            <td align="left">Close statements</td>
            <td align="left">= <%=generalStatistics.get(3)%></td>
        </tr>
        <tr>
            <td align="left">Session opens</td>
            <td align="left">= <%=generalStatistics.get(5)%></td>
        </tr>
        <tr>
            <td align="left">Session closes</td>
            <td align="left">= <%=generalStatistics.get(4)%></td>
        </tr>
        <tr>
            <td align="left">Total Transactions</td>
            <td align="left">= <%=generalStatistics.get(6)%></td>
        </tr>
        <tr>
            <td align="left">Successful Transactions</td>
            <td align="left">= <%=generalStatistics.get(7)%></td>
        </tr>
        <tr>
            <td align="left">Optimistic failures</td>
            <td align="left">= <%=generalStatistics.get(8)%></td>
        </tr>
<%
        }
%>
    </table>
    <br>
<%
        if (active) {
%>
        <a style="text-decoration:underline" href="hibernateconsole.jsp?view=<%=view%>">Refresh</a>&nbsp;&nbsp;
<%
        }
%>
    <a style="text-decoration:underline" href="hibernateconsole.jsp?view=<%=view%>&action=freeCaches" onclick="return window.confirm('Are you sure&#63;');">Clear All Caches</a>
<%
        String viewJSP = viewJSPMap.get(view);
        if (viewJSP != null) {
%>
        <jsp:include page="<%=viewJSP%>" flush="true" />
<%
        }
    } finally {
        ComponentsContextManager.clearContext();
    }
%>
</body>
</html>
