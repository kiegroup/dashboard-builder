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
<%@ page import="org.jboss.dashboard.CoreServices" %>
<%
    if (request.getSession().getAttribute("accessGranted") == null) {
        request.setAttribute("redirect", "hibernateconsole.jsp");
%>
        <jsp:include page="sso.jsp" flush="true"/>
<%
        return;
    }

    Map<String, SecondLevelCacheStatistics> secondLevelCacheStatistics = Collections.synchronizedMap(new TreeMap<String, SecondLevelCacheStatistics>(Collator.getInstance()));
    HibernateSessionFactoryProvider hsfp = CoreServices.lookup().getHibernateSessionFactoryProvider();
    SessionFactory sessionFactory = hsfp.getSessionFactory();
    Statistics statistics = sessionFactory.getStatistics();
    String[] names = statistics.getSecondLevelCacheRegionNames();
    if (names != null && names.length > 0) {
        for (int i = 0; i < names.length; i++) {
            secondLevelCacheStatistics.put(names[i], statistics.getSecondLevelCacheStatistics(names[i]));
        }
    }
%>
<table border="0" cellspacing="1" cellpadding="2" class="skn-table_border">
    <tr class="skn-table_header">
        <td width="250px" align="left" nowrap>Region name</td>
        <td width="100px" align="center" nowrap>Puts</td>
        <td width="100px" align="center" nowrap>Hits</td>
        <td width="100px" align="center" nowrap>Misses</td>
        <td width="100px" align="center" nowrap>Elements in memory</td>
        <td width="100px" align="center" nowrap>Size in memory</td>
        <td width="100px" align="center" nowrap>Elements on disk</td>
    </tr>
<%
    Iterator<String> cacheIter = secondLevelCacheStatistics.keySet().iterator();
    String css = "skn-odd_row";
    while (cacheIter.hasNext()) {
        if (css.equals("skn-odd_row")) css = "skn-even_row";
        else css = "skn-odd_row";
        String cache = cacheIter.next();
        SecondLevelCacheStatistics cacheStats = secondLevelCacheStatistics.get(cache);
%>
    <tr class="<%= css %>">
        <td align="left"><%=cache%></td>
        <td align="center"><%=cacheStats.getPutCount()%></td>
        <td align="center"><%=cacheStats.getHitCount()%></td>
        <td align="center"><%=cacheStats.getMissCount()%></td>
        <td align="center"><%=cacheStats.getElementCountInMemory()%></td>
        <td align="center"><%=cacheStats.getSizeInMemory()%></td>
        <td align="center"><%=cacheStats.getElementCountOnDisk()%></td>
    </tr>
<%
    }
%>
</table>
