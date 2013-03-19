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
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.jboss.dashboard.CoreServices" %>
<%
    if (request.getSession().getAttribute("accessGranted") == null) {
        request.setAttribute("redirect", "hibernateconsole.jsp");
%>
        <jsp:include page="sso.jsp" flush="true"/>
<%
        return;
    }

    Map<String, QueryStatistics> queryStatistics = Collections.synchronizedMap(new TreeMap<String, QueryStatistics>(Collator.getInstance()));
    HibernateSessionFactoryProvider hsfp = CoreServices.lookup().getHibernateSessionFactoryProvider();
    SessionFactory sessionFactory = hsfp.getSessionFactory();
    Statistics statistics = sessionFactory.getStatistics();
    String[] names = statistics.getQueries();
    if (names != null && names.length > 0) {
        for (int i = 0; i < names.length; i++) {
            queryStatistics.put(names[i], statistics.getQueryStatistics(names[i]));
        }
    }
%>
<table border="0" cellspacing="1" cellpadding="2" class="skn-table_border">
    <tr class="skn-table_header">
        <td width="400px" align="left" nowrap>Query</td>
        <td width="70px" align="center" nowrap>Calls</td>
        <td width="70px" align="center" nowrap>Row count</td>
        <td width="70px" align="center" nowrap>Max dur.</td>
        <td width="70px" align="center" nowrap>Min dur.</td>
        <td width="70px" align="center" nowrap>Avg dur.</td>
        <td width="70px" align="center" nowrap>Total dur.</td>
        <td width="70px" align="center" nowrap>Cache hits</td>
        <td width="70px" align="center" nowrap>Cache miss</td>
        <td width="70px" align="center" nowrap>Cache put</td>
    </tr>
<%
    Iterator<String> queryIter = queryStatistics.keySet().iterator();
    String css = "skn-odd_row";
    while (queryIter.hasNext()) {
        if (css.equals("skn-odd_row")) css = "skn-even_row";
        else css = "skn-odd_row";
        String query = queryIter.next();
        QueryStatistics queryStats = queryStatistics.get(query);
        query = StringUtils.replace(query, "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,", "?,... ,?,");
%>
    <tr class="<%= css %>">
        <td align="left"><%=query%></td>
        <td align="center"><%=queryStats.getExecutionCount()%></td>
        <td align="center"><%=queryStats.getExecutionRowCount()%></td>
        <td align="center"><%=queryStats.getExecutionMaxTime()%></td>
        <td align="center"><%=queryStats.getExecutionMinTime()%></td>
        <td align="center"><%=queryStats.getExecutionAvgTime()%></td>
        <td align="center"><%=queryStats.getExecutionAvgTime() * queryStats.getExecutionCount()%></td>
        <td align="center"><%=queryStats.getCacheHitCount()%></td>
        <td align="center"><%=queryStats.getCacheMissCount()%></td>
        <td align="center"><%=queryStats.getCachePutCount()%></td>
    </tr>
<%
    }
%>
</table>
