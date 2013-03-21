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
<%@ page import="org.jboss.dashboard.ui.UIServices" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.NVD3ChartViewer" %>
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractChartDisplayer" %>

<%
      String id = request.getParameter("chartId");
      NVD3ChartViewer viewer = (NVD3ChartViewer) session.getAttribute("chartId_iframe_viewer"+id);
      AbstractChartDisplayer displayer = (AbstractChartDisplayer) viewer.getDataDisplayer();

      String basehref = UIServices.lookup().getUrlMarkupGenerator().getBaseHref(request);
      String reqContext = request.getContextPath();
%>

<html>
<head>
    <base href="<%=basehref%>">
    <script src="<%=reqContext%>/components/bam/displayer/chart/nvd3/lib/d3.v2.min.js" type="text/javascript"></script>
    <script src="<%=reqContext%>/components/bam/displayer/chart/nvd3/nv.d3.min.js"     type="text/javascript"></script>
    <script src="<%=reqContext%>/components/bam/displayer/chart/nvd3/src/tooltip.js"   type="text/javascript"></script>
    <script src="<%=reqContext%>/components/bam/displayer/chart/nvd3/src/utils.js"     type="text/javascript"></script>
    <script src="<%=reqContext%>/components/bam/displayer/chart/nvd3/src/models/axis.js" type="text/javascript"></script>
    <script src="<%=reqContext%>/components/bam/displayer/chart/nvd3/src/models/legend.js" type="text/javascript"></script>
    <script src="<%=reqContext%>/components/bam/displayer/chart/nvd3/src/models/scatter.js" type="text/javascript"></script>
    <script src="<%=reqContext%>/components/bam/displayer/chart/nvd3/src/models/pie.js" type="text/javascript"></script>
    <script src="<%=reqContext%>/components/bam/displayer/chart/nvd3/src/models/pieChart.js" type="text/javascript"></script>
    <link  href="<%=reqContext%>/components/bam/displayer/chart/nv.d3.css" rel="stylesheet" type="text/css">

<style type="text/css">
/* Chart related styles */
.skn-chart-table {  padding:10px; margin:5px;border:1px solid #eeeeee; }
.skn-chart-title { text-align:center; font-size: 120%; font-weight: bold;padding-bottom:10px; }
.skn-chart-tooltip { text-align:center; font-size: 100%; font-weight: bold; height:25px}
</style>

</head>

<body>

<%@include file="nvd3_chart_common.jspi"%>
<%@include file="nvd3_chart_wrapper.jspi"%>
<%@include file="nvd3_piechart_script.jspi"%>

</body>
</html>
