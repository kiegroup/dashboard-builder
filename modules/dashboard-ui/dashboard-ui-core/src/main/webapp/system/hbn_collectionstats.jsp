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

    Map<String, CollectionStatistics> collectionStatistics = Collections.synchronizedMap(new TreeMap<String, CollectionStatistics>(Collator.getInstance()));
    HibernateSessionFactoryProvider hsfp = CoreServices.lookup().getHibernateSessionFactoryProvider();
    SessionFactory sessionFactory = hsfp.getSessionFactory();
    Statistics statistics = sessionFactory.getStatistics();
    String[] names = statistics.getCollectionRoleNames();
    if (names != null && names.length > 0) {
        for (int i = 0; i < names.length; i++) {
            collectionStatistics.put(names[i], statistics.getCollectionStatistics(names[i]));
        }
    }
%>
<table border="0" cellspacing="1" cellpadding="2" class="skn-table_border">
    <tr class="skn-table_header">
        <td width="250px" align="left" nowrap>Collection</td>
        <td width="100px" align="center" nowrap>Loads</td>
        <td width="100px" align="center" nowrap>Fetches</td>
        <td width="100px" align="center" nowrap>Updates</td>
        <td width="100px" align="center" nowrap>Recreate</td>
        <td width="100px" align="center" nowrap>Remove</td>
    </tr>
<%
    String css = "skn-odd_row";
    Iterator<String> collectionIter = collectionStatistics.keySet().iterator();
    while (collectionIter.hasNext()) {
        if (css.equals("skn-odd_row")) css = "skn-even_row";
        else css = "skn-odd_row";
        String collection = collectionIter.next();
        CollectionStatistics collectionStats = collectionStatistics.get(collection);
%>
    <tr class="<%= css %>">
        <td align="left"><%=collection%></td>
        <td align="center"><%=collectionStats.getLoadCount()%></td>
        <td align="center"><%=collectionStats.getFetchCount()%></td>
        <td align="center"><%=collectionStats.getUpdateCount()%></td>
        <td align="center"><%=collectionStats.getRecreateCount()%></td>
        <td align="center"><%=collectionStats.getRemoveCount()%></td>
    </tr>
<%
    }
%>
</table>
