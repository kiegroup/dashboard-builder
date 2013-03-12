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
<%@ page import="org.jboss.dashboard.factory.Factory" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.OFC2ChartViewer" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%
    OFC2ChartViewer viewer = (OFC2ChartViewer) Factory.lookup("org.jboss.dashboard.ui.components.PieChartViewer_ofc2");
    request.setAttribute("viewer", viewer);
%>
<mvc:include page="ofc2_chart.jsp" flush="true"/>
