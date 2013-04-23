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
<%@ page import="org.jboss.dashboard.ui.components.AbstractXAxisDisplayerEditor" %>
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractXAxisDisplayer" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.components.AbstractChartDisplayerEditor" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayerRenderer" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayerFeature" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<i18n:bundle baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    AbstractXAxisDisplayerEditor editor = (AbstractXAxisDisplayerEditor) request.getAttribute("editor");
    AbstractXAxisDisplayer displayer = (AbstractXAxisDisplayer) editor.getDataDisplayer();
    DataDisplayerRenderer renderer = displayer.getDataDisplayerRenderer();
%>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SHOW_HIDE_LABELS)) { %>
<tr>
  <td height="15" align="left" nowrap="nowrap">
    <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "showLabels"%>'>!!Show labels</i18n:message>:
  </td>
  <td height="15" align="left">
    <input name="showLabelsXAxis" type="checkbox" value="true" <%=displayer.isShowLabelsXAxis() ? "checked" : ""%>
    onChange="return bam_kpiedit_submitProperties(this);"
    >
  </td>
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_LABELS_ANGLE)) { %>
<tr>
  <td height="15" align="left" nowrap="nowrap">
    <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "labelAngleXAxis"%>'>!!Label angle</i18n:message>:
  </td>
  <td  align="left">
    <input class="skn-input" name="labelAngleXAxis" type="text" size="14" value="<%= displayer.getLabelAngleXAxis()%>"
        onChange="return bam_kpiedit_submitProperties(this);">
  </td>
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SHOW_LINES_AREA)) { %>
<tr>
    <td height="15" align="left" width="33%" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "showLinesArea"%>'>!!Show Lines Area</i18n:message>:
    </td>
    <td height="15" width="66%" align="left">
        <%
            boolean showLinesArea = false;
            if (displayer.isShowLinesArea()) showLinesArea = true;
        %>
        <input name="showLinesAreas" type="checkbox" value="true" <%=showLinesArea ? "checked" : ""%>
               onClick="return bam_kpiedit_submitProperties(this);">
    </td>
</tr>
<% } %>

