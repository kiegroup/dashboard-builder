<%--

    Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.

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
<%@ page import="org.jboss.dashboard.ui.components.chart.GoogleChartViewer" %>
<%@ page import="org.jboss.dashboard.ui.UIServices" %>
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractChartDisplayer" %>
<%@ page import="org.jboss.dashboard.dataset.DataSet" %>
<%@ page import="org.jboss.dashboard.displayer.map.MapDisplayerType" %>
<%@ page import="org.jboss.dashboard.displayer.chart.PieChartDisplayerType" %>
<%@ page import="org.jboss.dashboard.displayer.chart.LineChartDisplayerType" %>
<%@ page import="org.jboss.dashboard.commons.text.StringUtil" %>
<%@ page import="org.jboss.dashboard.domain.Interval" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.AbstractChartDisplayerEditor" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"  %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n" %>
<%
  GoogleChartViewer viewer = (GoogleChartViewer) request.getAttribute("viewer");
  AbstractChartDisplayerEditor editor = (AbstractChartDisplayerEditor) request.getAttribute("editor");
  AbstractChartDisplayer displayer = (AbstractChartDisplayer) viewer.getDataDisplayer();
  DataSet xyDataSet = displayer.buildXYDataSet();
  if (xyDataSet == null) {
%>
  <span class="skn-error">The data cannot be displayed due to an unexpected problem.</span>
<%
    return;
  }

  Locale locale = LocaleManager.currentLocale();
  DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
  numberFormat.setGroupingUsed(false);
  List<String> xvalues = new ArrayList<String>();
  List<String> yvalues = new ArrayList<String>();
  double minDsValue = -1;
  double maxDsValue = -1;

  //String unitPattern = displayer.getUnit(locale);
  for (int i=0; i< xyDataSet.getRowCount(); i++) {
    String xvalue = ((Interval) xyDataSet.getValueAt(i, 0)).getDescription(locale);
    double yvalue = ((Number) xyDataSet.getValueAt(i, 1)).doubleValue();

    //String formattedValue = StringUtils.replace(unitPattern, "{value}", numberFormat.format(yvalue));
    String formattedValue = numberFormat.format(yvalue);
    xvalues.add(StringUtil.escapeQuotes(xvalue));
    yvalues.add(formattedValue);

    // Get the minimum and the maximum value of the dataset.
    if ((minDsValue == -1) || (yvalue < minDsValue)) minDsValue = yvalue;
    if ((maxDsValue == -1) || (yvalue > maxDsValue)) maxDsValue = yvalue;
  }

  String barColor = displayer.getColor();
  if (barColor == null || barColor.equals(displayer.getBackgroundColor())) {
    barColor = "#0000FF"; // Default blue if not changed
  }

  // Google chart type
  String chartLib = "corechart";
  String chartType = "BarChart";
  String chartTypeUID = displayer.getDataDisplayerType().getUid();
  if (LineChartDisplayerType.UID.equals(chartTypeUID)) chartType = "LineChart";
  if (PieChartDisplayerType.UID.equals(chartTypeUID)) chartType = "PieChart";
  if (MapDisplayerType.UID.equals(chartTypeUID)) {
    chartLib = displayer.getType().toLowerCase();
    chartType = displayer.getType();
  }
  // Every chart must have a different identifier so as to do not merge tooltips.
  // Chart identifier is composed by producerId and this suffix.
  int suffix = viewer.hashCode();
  if (suffix < 0) suffix *= -1;
  String chartId = viewer.getBeanName() + suffix;
%>
<script type="text/javascript">

  google.load('visualization', '1', {'packages':['<%=chartLib%>']});
  google.setOnLoadCallback(drawChart_<%=chartId%>);

  function drawChart_<%=chartId%>() {
    // Create the data table.
    var data_<%=chartId%> = new google.visualization.DataTable();
    data_<%=chartId%>.addColumn('string', '<%= displayer.getDomainProperty().getName(locale)%>');
    data_<%=chartId%>.addColumn('number', '<%= displayer.getRangeProperty().getName(locale)%>');
    data_<%=chartId%>.addRows([<% for(int i=0; i < xvalues.size(); i++) { if( i != 0 ) out.print(", "); %>['<%=xvalues.get(i)%>', <%=yvalues.get(i)%>]<% } %>]);

    // Set chart options
    var options_<%=chartId%> = {'displayMode': 'markers',/*'title':'<%= displayer.isShowTitle() ? (StringUtil.escapeQuotes(displayer.getTitle()))  : "" %>',*/
      'width':<%= displayer.getWidth() %>,
      'height':<%= displayer.getHeight() %>};

    // Instantiate and draw our chart, passing in some options.
    var chart_<%=chartId%> = new google.visualization.<%=chartType%>(document.getElementById('div_chart_<%=chartId%>'));
    chart_<%=chartId%>.draw(data_<%=chartId%>, options_<%=chartId%>);
  }
  <% if (editor != null) { %>
    drawChart_<%=chartId%>();
  <% } %>
</script>
<table class="skn-chart-table" width="100%" >
  <tbody>
  <tr>
    <td height="<%= displayer.getHeight() %>" align="<%=displayer.getGraphicAlign()%>" style="">
      <% if( displayer.isShowTitle() && displayer.getTitle() != null) { %>
      <div id="title<%=chartId%>" class="skn-chart-title" style="width:<%= displayer.getWidth() %>px"><%=displayer.getTitle()%></div>
      <% } %>
      <div class="skn-chart-wrapper" style="width:<%= displayer.getWidth() %>px;height:<%= displayer.getHeight() %>px" id="div_chart_<%=chartId%>"></div>
    </td>
  </tr>
  </tbody>
</table>

