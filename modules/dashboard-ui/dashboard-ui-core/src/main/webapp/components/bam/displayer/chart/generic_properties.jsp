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
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractChartDisplayer"%>
<%@ page import="org.jboss.dashboard.ui.components.AbstractChartDisplayerEditor"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.jboss.dashboard.domain.Domain" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayerRenderer" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayerFeature" %>
<%@taglib uri="factory.tld" prefix="factory"%>
<%@taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    Locale locale = LocaleManager.currentLocale();
    ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.messages", locale);

    AbstractChartDisplayerEditor editor = (AbstractChartDisplayerEditor) request.getAttribute("editor");
    AbstractChartDisplayer displayer = (AbstractChartDisplayer) editor.getDataDisplayer();
    int intervalsSortCriteria = displayer.getIntervalsSortCriteria();
    int intervalsSortOrder = displayer.getIntervalsSortOrder();
    DataDisplayerRenderer renderer = displayer.getDataDisplayerRenderer();
%>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SHOW_TITLE)) { %>
<tr>
    <td height="15" align="left" width="33%" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "showTitle"%>'>!!Mostrar titulo</i18n:message>:
    </td>
    <td height="15" width="66%" align="left">
        <%
            boolean showTitle = false;
            if (displayer.isShowTitle()) showTitle = true;
        %>
        <input name="showTitle" id="<factory:encode name="showTitle"/>" type="checkbox" value="true" <%=showTitle ? "checked" : ""%>
          onChange="return bam_kpiedit_submitProperties(this);"
        >
    </td>
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.ROUND_TO_INTEGER)) { %>
<tr>
    <td height="15" align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "axisInteger"%>'>!!Valores enteros</i18n:message>:
    </td>
    <td height="15" width="66%" align="left">
        <%
            boolean axisInteger = false;
            if (displayer.isAxisInteger()) axisInteger = true;
        %>
        <input name="axisInteger" id="<factory:encode name="axisInteger"/>" type="checkbox" value="true" <%=axisInteger ? "checked" : ""%>
          onChange="return bam_kpiedit_submitProperties(this);"
        >
    </td>
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_CHART_WIDTH)) { %>
<tr>
    <td height="15" align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "width"%>'>!!Anchura</i18n:message>:
    </td>
    <td align="left">
        <input class="skn-input" name="width" type="text" size="14" value="<%= displayer.getWidth() %>"
          onChange="return bam_kpiedit_submitProperties(this);"
        >
    </td>
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_CHART_HEIGHT)) { %>
<tr>
    <td height="15" align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "height"%>'>!!Altura</i18n:message>:
    </td>
    <td align="left">
        <input class="skn-input" name="height" type="text" size="14" value="<%= displayer.getHeight() %>"
          onChange="return bam_kpiedit_submitProperties(this);"
        >
    </td>
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_FOREGRND_COLOR)) { %>
<tr>
    <td height="15" align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "color"%>'>!!Color externo</i18n:message>:
    </td>
    <td  align="left">
        <input class="skn-input" name="color" type="text" size="14" value="<%=displayer.getColor()%>" onClick="startColorPicker(this);" onkeyup="maskedHex(this)"
          onChange="return bam_kpiedit_submitProperties(this);"
        >
    </td>
</tr>
<!-- TODO: Background color hidden. Resolve this bug in the future. 
<tr>
    <td align="left">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "backgroundColor"%>'>!!Color de fondo</i18n:message>:
    </td>
    <td>
        <input class="skn-input" name="backgroundColor" type="text" onClick="startColorPicker(this);" onkeyup="maskedHex(this)"
                size="14" value="<%=displayer.getBackgroundColor()%>" onChange="submitAjaxForm(this.form);">
    </td>
</tr>
-->
<% } %>

<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SHOW_LEGEND)) { %>
<tr>
    <td height="15" align="left" width="33%" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "showLegend"%>'>!!Show legend</i18n:message>:
    </td>
    <td height="15" width="66%" align="left">
        <%
            boolean showLegend = false;
            if (displayer.isShowLegend()) showLegend = true;
        %>
        <input name="showLegend" id="<factory:encode name="showLegend"/>" type="checkbox" value="true" <%=showLegend ? "checked" : ""%>
          onChange="return bam_kpiedit_submitProperties(this);"
        >
    </td>
</tr>
<% } %>

<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SHOW_LEGEND_POSITION)) { %>
<tr>
    <td height="15" align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "legendAnchor"%>'>!!Leyenda</i18n:message>:
    </td>
    <td align="left">
        <select name="legendAnchor" class="skn-input" style="width:95px;"
          onChange="return bam_kpiedit_submitProperties(this);"
        >
            <%
                String sinLeyenda = "";
                if (!displayer.isShowLegend()) sinLeyenda = "selected";
            %>
             <option value="-1" <%= sinLeyenda %>>
                <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "legendAnchor." +  "noLegend" %>'>!!!Sin leyenda</i18n:message>
            </option>
        <%
            String[] legendAnchor = new String[] {"north", "south", "east", "west"};
            String[] legendAnchorDescrip = new String[] {
                    i18n.getString(AbstractChartDisplayerEditor.I18N_PREFFIX + "legendAnchor." + "north"),
                    i18n.getString(AbstractChartDisplayerEditor.I18N_PREFFIX + "legendAnchor." + "south"),
                    i18n.getString(AbstractChartDisplayerEditor.I18N_PREFFIX + "legendAnchor." + "east"),
                    i18n.getString(AbstractChartDisplayerEditor.I18N_PREFFIX + "legendAnchor." + "west")};
            for (int i = 0; i < legendAnchor.length; i++) {
                String selected = "";
                if (displayer.isShowLegend() && legendAnchor[i].equals(displayer.getLegendAnchor())) selected = "selected";
        %>
                <option value="<%= legendAnchor[i] %>" <%= selected %>><%= legendAnchorDescrip[i] %></option>
        <%
            }
        %>
        </select>
    </td>
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.ALIGN_CHART)) { %>
<tr>
    <td height="15" align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "graphicAlign"%>'>!!Alinear grafico a</i18n:message>:
    </td>
    <td align="left">
        <select name="graphicAlign" class="skn-input" style="width:95px;"
          onChange="return bam_kpiedit_submitProperties(this);"
        >
        <%
            String[] graphicAlign = new String[] {"left", "center", "right"};
            String[] graphicAlignDescrip = new String[] {
                    i18n.getString(AbstractChartDisplayerEditor.I18N_PREFFIX + "graphicAlign." + "left"),
                    i18n.getString(AbstractChartDisplayerEditor.I18N_PREFFIX + "graphicAlign." + "center"),
                    i18n.getString(AbstractChartDisplayerEditor.I18N_PREFFIX + "graphicAlign." + "right")};
            for (int i = 0; i < graphicAlign.length; i++) {
                String selected = "";
                if (graphicAlign[i].equals(displayer.getGraphicAlign())) selected = "selected";
        %>
                <option value="<%= graphicAlign[i] %>" <%= selected %>><%= graphicAlignDescrip[i] %></option>
        <%
            }
        %>
        </select>
    </td>
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_MARGIN_LEFT)) { %>
<tr>
    <td height="15" align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "margin_left"%>'>!!Margin Left</i18n:message>:
    </td>
    <td align="left">
        <input class="skn-input" name="marginLeft" type="text" size="14" value="<%= displayer.getMarginLeft() %>"
          onChange="return bam_kpiedit_submitProperties(this);"
        >
    </td>
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_MARGIN_RIGHT)) { %>
<tr>
    <td height="15" align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "margin_right"%>'>!!Margin Right</i18n:message>:
    </td>
    <td align="left">
        <input class="skn-input" name="marginRight" type="text" size="14" value="<%= displayer.getMarginRight() %>"
          onChange="return bam_kpiedit_submitProperties(this);"
        >
    </td>
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_MARGIN_TOP)) { %>
<tr>
    <td height="15" align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "margin_top"%>'>!!Margin Top</i18n:message>:
    </td>
    <td align="left">
        <input class="skn-input" name="marginTop" type="text" size="14" value="<%= displayer.getMarginTop() %>"
          onChange="return bam_kpiedit_submitProperties(this);"
        >
    </td>
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_MARGIN_BOTTOM)) { %>
<tr>
    <td height="15" align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "margin_bottom"%>'>!!Margin Bottom</i18n:message>:
    </td>
    <td align="left">
        <input class="skn-input" name="marginBottom" type="text" size="14" value="<%= displayer.getMarginBottom() %>"
          onChange="return bam_kpiedit_submitProperties(this);"
        >
    </td>
</tr>
<% } %>
<tr>
  <td height="15" nowrap="nowrap" align="left" >
    <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "intervalsSortCriteria"%>'>!!Sort intervals by</i18n:message>:
  </td>
  <td height="15" nowrap="nowrap" align="left">
    <select class="skn-input" name="intervalsSortCriteria" style="width:95px;"
      onChange="return bam_kpiedit_submitProperties(this);"
    >
      <option value="<%= AbstractChartDisplayer.INTERVALS_SORT_CRITERIA_LABEL %>" <%= AbstractChartDisplayer.INTERVALS_SORT_CRITERIA_LABEL == intervalsSortCriteria ? "selected": "" %>><i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "intervalsSortCriteria.label"%>'>!!Label</i18n:message></option>
      <option value="<%= AbstractChartDisplayer.INTERVALS_SORT_CRITERIA_VALUE %>" <%= AbstractChartDisplayer.INTERVALS_SORT_CRITERIA_VALUE == intervalsSortCriteria ? "selected": "" %>><i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "intervalsSortCriteria.value"%>'>!!Value</i18n:message></option>
    </select>
  </td>
</tr>
<tr>
  <td height="15" nowrap="nowrap" align="left" >
    <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "intervalsSortOrder"%>'>!!Sort order</i18n:message>:
  </td>
  <td height="15" nowrap="nowrap" align="left">
    <select class="skn-input" name="intervalsSortOrder" style="width:95px;"
      onChange="return bam_kpiedit_submitProperties(this);"
    >
      <option value="<%= AbstractChartDisplayer.INTERVALS_SORT_ORDER_NONE %>" <%= AbstractChartDisplayer.INTERVALS_SORT_ORDER_NONE == intervalsSortOrder ? "selected": "" %>><i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "intervalsSortOrder.none"%>'>!!None</i18n:message></option>
      <option value="<%= AbstractChartDisplayer.INTERVALS_SORT_ORDER_ASC %>" <%= AbstractChartDisplayer.INTERVALS_SORT_ORDER_ASC == intervalsSortOrder ? "selected": "" %>><i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "intervalsSortOrder.ascendant"%>'>!!Ascendant</i18n:message></option>
      <option value="<%= AbstractChartDisplayer.INTERVALS_SORT_ORDER_DESC %>" <%= AbstractChartDisplayer.INTERVALS_SORT_ORDER_DESC == intervalsSortOrder ? "selected": "" %>><i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "intervalsSortOrder.descendant"%>'>!!Descendant</i18n:message></option>
    </select>
  </td>
</tr>
<script defer="true">
  // On page load ensure color picker is hidden.
	hideColorPicker();
</script>