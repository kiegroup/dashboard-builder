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
<%@ page import="org.jboss.dashboard.ui.components.chart.NVD3ChartViewer" %>
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractChartDisplayer" %>
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractXAxisDisplayer" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.AbstractChartDisplayerEditor" %>
<%@ page import="org.jboss.dashboard.dataset.DataSet" %>
<%@ page import="org.jboss.dashboard.domain.Interval" %>
<%@ page import="org.jboss.dashboard.provider.DataProperty"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.commons.text.StringUtil" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"  %>
<%@ taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jboss.dashboard.displayer.nvd3.messages" locale="<%= LocaleManager.currentLocale() %>" />
<%

    DataSet xyDataSet = null;
    if( displayer != null ) {
        xyDataSet = displayer.buildXYDataSet();
    }

    AbstractChartDisplayerEditor editor = (AbstractChartDisplayerEditor) request.getAttribute("editor");
    boolean animateChart = (editor == null);
    boolean enableDrillDown = (editor == null);
    boolean enableTooltips  = (editor == null);

    if (xyDataSet == null) {
%>
    <span class="skn-error">
        <i18n:message key="nvd3.error">The data cannot be displayed due to an unexpected problem</i18n:message>
    </span>
<%
       return;
    }
    if (xyDataSet.getRowCount() == 0) {
%>
    <table width="<%= displayer.getWidth()%>" height="<%= displayer.getHeight()%>">
        <tr><td align="center" valign="center"><i18n:message key="nvd3.noData">NO DATA</i18n:message></td></tr>
    </table>
<%
        return;
    }

    DataProperty domainProperty = displayer.getDomainProperty();
    DataProperty rangeProperty = displayer.getRangeProperty();
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
    int suffix = viewer.hashCode();
    if (suffix < 0) suffix *= -1;
    String chartId = viewer.getBeanName() + suffix;

    String selectedColor = displayer.getColor();
    if (selectedColor == null || selectedColor.equals(displayer.getBackgroundColor())) {
       selectedColor = "#0000FF"; // Default blue if not changed
    }
%>
