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
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractChartDisplayer" %>
<%@ page import="org.jboss.dashboard.displayer.chart.PieChartDisplayer" %>
<%@ page import="org.jboss.dashboard.displayer.chart.BarChartDisplayer" %>
<%@ page import="org.jboss.dashboard.displayer.chart.LineChartDisplayer" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="org.jboss.dashboard.dataset.DataSet" %>
<%@ page import="org.jboss.dashboard.domain.Interval" %>
<%@ page import="org.jboss.dashboard.ui.components.AbstractChartDisplayerEditor" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.OFC2ChartViewer" %>
<%@ page import="org.jboss.dashboard.commons.text.StringUtil" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"  %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%
    AbstractChartDisplayerEditor editor = (AbstractChartDisplayerEditor) request.getAttribute("editor");
    OFC2ChartViewer viewer = (OFC2ChartViewer) request.getAttribute("viewer");
    boolean animateChart = (editor == null);
    AbstractChartDisplayer displayer = (AbstractChartDisplayer) viewer.getDataDisplayer();
    DataSet xyDataSet = displayer.buildXYDataSet();
    if (xyDataSet == null) {
%>
  <span class="skn-error">The data cannot be displayed due to an unexpected problem.</span>
<%
  }

  Locale locale = LocaleManager.currentLocale();
  DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
  numberFormat.setGroupingUsed(false);
  List<String> xvalues = new ArrayList<String>();
  List<String> yvalues = new ArrayList<String>();
  double minDsValue = -1;
  double maxDsValue = -1;

  for (int i=0; i< xyDataSet.getRowCount(); i++) {
    String xvalue = ((Interval) xyDataSet.getValueAt(i, 0)).getDescription(locale);
    double yvalue = ((Number) xyDataSet.getValueAt(i, 1)).doubleValue();

    xvalues.add(StringUtil.escapeQuotes(xvalue));
    yvalues.add(numberFormat.format(yvalue));

    // Get the minimum and the maximum value of the dataset.
    if ((minDsValue == -1) || (yvalue < minDsValue)) minDsValue = yvalue;
    if ((maxDsValue == -1) || (yvalue > maxDsValue)) maxDsValue = yvalue;
  }

  // Every chart must have a different identifier so as to do not merge tooltips.
  // Chart identifier is composed by producerId and this suffix.
  int suffix = viewer.hashCode();
  if (suffix < 0) suffix *= -1;
  String chartId = viewer.getComponentAlias() + suffix;

  String barColor = displayer.getColor();
  if (barColor == null || barColor.equals(displayer.getBackgroundColor())) {
    barColor = "#0000FF"; // Default blue if not changed
  }

  String userAgent = request.getHeader("user-agent");
  boolean isIE = userAgent != null && userAgent.indexOf("MSIE") != -1;

  double minRange = minDsValue < 0 ? minDsValue : 0;
  int ySteps = (int) ((maxDsValue - minRange) / 10.0);
  String yStepStr = numberFormat.format(ySteps);
  String minRangeStr = numberFormat.format(minRange);
  String maxDsValueStr = numberFormat.format(maxDsValue);

  // Format the display values according to the representation format defined.
  String unitPattern = displayer.getUnit(locale);
  String valuePattern = StringUtils.replace(unitPattern, "{value}", "#val#");
  String totalPattern = StringUtils.replace(unitPattern, "{value}",  "#total#");
%>
<script type="text/javascript" defer="defer">

  function get_data_<%=chartId%>() {

  <% if (displayer instanceof PieChartDisplayer) { %>

    var data_<%=chartId%> =
        '{ "elements": [ '
            + '{ "type": "pie", "alpha": 0.5 <% if (false) { %>, "animate": [ { "type": "fade" }, { "type": "bounce", "distance": 5 } ]<% } %>, "start-angle": 0, "tip": "#label#<br><%=valuePattern%> / <%=totalPattern%><br>#percent#", '
            + '"colours": [ "#FF9E11", "#DF5511", "#DF0011", "#C2008F", "#9E00D5", "#7000B8", "#4500B8", "#00A70D", "#9BDC00"], '
            + '"values": [ <% for(int i=0; i < xvalues.size(); i++) { if( i != 0 ) out.print(", "); %>{ "value": <%=yvalues.get(i)%>, "label": "<%=xvalues.get(i)%>", "on-click":"click_<%=chartId%>(<%= i %>)" }<% } %> ] } ], '
            + '"title": { "text": "<%= displayer.isShowTitle() ? (StringUtil.escapeQuotes(displayer.getTitle()) + "<br>")  : "" %>", "style": "color: #000000;  font-size: 20px" }, '
            + '"num_decimals": <%=displayer.isAxisInteger() ? 0 : 2%>, "is_fixed_num_decimals_forced": true, "is_decimal_separator_comma": false, "is_thousand_separator_disabled": false,'
            + '"bg_colour": "<%= displayer.getBackgroundColor() %>" }';

  <%
      } else if (displayer instanceof BarChartDisplayer) {
          BarChartDisplayer barDisplayer  = (BarChartDisplayer) displayer;
  %>

    var data_<%=chartId%> = '{ "elements": [ { "type": "<%=displayer.getType()%>", '
        + '"values": [ '
        + '<% for(int i=0; i < xvalues.size(); i++) { if( i != 0 ) out.print(", "); %>{ "top": "<%=yvalues.get(i)%>", "bottom": 0, "tip":"<%=xvalues.get(i) %><br><%=valuePattern%>", "on-click": "click_<%=chartId%>(<%= i %>)" }<% } %>'
        + '], "colour": "<%= barColor %>" <% if (animateChart) { %> , "on-show":{"type":"grow-up","pop-up":1,"delay":0.1,"cascade":1} <% } %> } ], "title": { "text": "<%= displayer.isShowTitle() ? (StringUtil.escapeQuotes(displayer.getTitle()) + "<br>") : "" %>", "style": "color: #000000;  font-size: 20px"  }, '
        + '"num_decimals": <%=displayer.isAxisInteger() ? 0 : 2%>, "is_fixed_num_decimals_forced": true, "is_decimal_separator_comma": false, "is_thousand_separator_disabled": false, "bg_colour": "<%= displayer.getBackgroundColor() %>", '
        + '"y_axis": { "min": "<%=minRangeStr%>", "max": "<%=maxDsValueStr%>" , "steps": <%=yStepStr%>}, '
        + '"x_axis": { "labels": { "rotate": <%=barDisplayer.getLabelAngleXAxis()%>, "labels": [ <% for(int i=0; i < xvalues.size(); i++) { if( i != 0 ) out.print(", "); out.print("\"" + (barDisplayer.isShowLabelsXAxis() ? xvalues.get(i) : "") + "\""); } %> '
        + '] } } }';
  <%
      } else if (displayer instanceof LineChartDisplayer) {
          LineChartDisplayer lineDisplayer  = (LineChartDisplayer) displayer;
  %>

    var data_<%=chartId%> = '{"elements": [ { "type": "<%=displayer.getType()%>", "dot-size":3,'
        + '"values": [ '
        + '<% for(int i=0; i < xvalues.size(); i++) { if( i != 0 ) out.print(", "); %>{ "y":"<%=yvalues.get(i)%>", "tip":"<%=xvalues.get(i) %><br><%=valuePattern%>", "on-click":"click_<%=chartId%>(<%= i %>)" }<% } %>'
        + '], "colour": "<%= barColor %>" <% if (animateChart) { %> , "on-show":{"type":"grow-up","pop-up":1,"delay":0.1,"cascade":1} <% } %> } ], "title": { "text": "<%= displayer.isShowTitle() ? (StringUtil.escapeQuotes(displayer.getTitle()) + "<br>") : "" %>", "style": "color: #000000;  font-size: 20px"  }, '
        + '"num_decimals": <%=displayer.isAxisInteger() ? 0 : 2%>, "is_fixed_num_decimals_forced": true, "is_decimal_separator_comma": false, "is_thousand_separator_disabled": false, "bg_colour": "<%= displayer.getBackgroundColor() %>", '
        + '"y_axis": { "min": "<%=minRangeStr%>", "max": "<%=maxDsValueStr%>" , "steps": <%=yStepStr%>}, '
        + '"x_axis": { "labels": { "rotate": <%=lineDisplayer.getLabelAngleXAxis()%>, "labels": [ <% for(int i=0; i < xvalues.size(); i++) { if( i != 0 ) out.print(", "); out.print("\"" + (lineDisplayer.isShowLabelsXAxis() ? xvalues.get(i) : "") +"\""); } %> '
        + '] } } }';
  <% } %>
    return data_<%=chartId%>;
  }

  function click_<%=chartId%>(index) {
    form = document.getElementById('<panel:encode name="chartForm"/>');
    form.<%= OFC2ChartViewer.PARAM_NSERIE %>.value = index;
    submitAjaxForm(form);
  }

  setAjax('<panel:encode name="chartForm"/>');
</script>
<form method="post" action='<factory:formUrl friendly="false"/>' id='<panel:encode name="chartForm"/>'>
  <factory:handler bean="<%=viewer.getComponentName()%>" action="<%= OFC2ChartViewer.PARAM_ACTION %>"/>
  <input type="hidden" name="<%= OFC2ChartViewer.PARAM_NSERIE %>" value="0" />
</form>
<table class="chartTable" border="0" cellpadding="10" cellspacing="0" width="100%">
  <tbody>
  <tr>
    <td height="<%= displayer.getHeight() %>" align="<%=displayer.getGraphicAlign()%>">
      <div style="width:<%= displayer.getWidth() %>px">
        <% if (isIE) { %>
        <OBJECT style="VISIBILITY: visible" id="div_chart_<%=chartId%>"
                classid=clsid:D27CDB6E-AE6D-11cf-96B8-444553540000 width="<%= displayer.getWidth() %>"
                height="<%= displayer.getHeight() %>">
          <PARAM NAME="FlashVars" value="get-data=get_data_<%=chartId%>">
          <PARAM NAME="Movie" VALUE="<mvc:context uri="/components/bam/displayer/chart/open-flash-chart.swf" />" >
          <PARAM NAME="Src" VALUE="<mvc:context uri="/components/bam/displayer/chart/open-flash-chart.swf" />" >
          <param NAME="wmode" VALUE="transparent">
        </OBJECT>
        <% } else { %>
        <object height="<%= displayer.getHeight() %>" width="<%= displayer.getWidth() %>"
                type="application/x-shockwave-flash"
                data="<mvc:context uri="/components/bam/displayer/chart/open-flash-chart.swf" />"
                id="div_chart_<%=chartId%>"
                style="visibility: visible;">
          <param name="flashvars" value="get-data=get_data_<%=chartId%>">
          <param name="wmode" value="transparent">
        </object>
        <% } %>
      </div>
    </td>
  </tr>
  </tbody>
</table>