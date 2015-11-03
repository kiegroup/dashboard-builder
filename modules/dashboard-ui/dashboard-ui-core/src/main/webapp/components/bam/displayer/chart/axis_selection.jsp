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
<%@ page import="org.jboss.dashboard.ui.components.chart.AbstractChartDisplayerEditor"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.jboss.dashboard.domain.Domain" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayerRenderer" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayerFeature" %>
<%@taglib uri="factory.tld" prefix="factory"%>
<%@taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n"%>

<i18n:bundle baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
	AbstractChartDisplayerEditor editor = (AbstractChartDisplayerEditor) request.getAttribute("editor");
    AbstractChartDisplayer displayer = (AbstractChartDisplayer) editor.getDataDisplayer();
	DataDisplayerRenderer renderer = displayer.getDataDisplayerRenderer();
%>
<!-- Domain axis selection and edition -->
<tr>
    <td align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "domain"%>'>!!Domain</i18n:message>:
    </td>
    <mvc:include page="../domain/domain_selector_component.jsp"  flush="true" />
</tr>
<!-- Range axis selection and edition -->
<tr>
    <td align="left" nowrap="nowrap">
        <i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "range"%>'>!!Range</i18n:message>:
    </td>
    <mvc:include page="../range/range_selector_component.jsp"  flush="true" />
</tr>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_RANGE2)) { %>
<tr>
	<td align="left" nowrap="nowrap">
		<i18n:message key='<%= AbstractChartDisplayerEditor.I18N_PREFFIX + "range"%>'>!!Range</i18n:message> 2:
	</td>
	<mvc:include page="../range/range2_selector_component.jsp"  flush="true" />
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_STARTDATE_PROP)) { %>
<tr>
	<td align="left" nowrap="nowrap">
		Start Date:
	</td>
	<mvc:include page="../domain/fieldStartDate_selector_component.jsp"  flush="true" />
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_ENDDATE_PROP)) { %>
<tr>
	<td align="left" nowrap="nowrap">
		End Date:
	</td>
	<mvc:include page="../domain/fieldEndDate_selector_component.jsp"  flush="true" />
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_SIZE_PROP)) { %>
<tr>
	<td align="left" nowrap="nowrap">
		Size:
	</td>
	<mvc:include page="../domain/fieldSize_selector_component.jsp"  flush="true" />
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_DONE_PROP)) { %>
<tr>
	<td align="left" nowrap="nowrap">
		Done:
	</td>
	<mvc:include page="../domain/fieldDone_selector_component.jsp"  flush="true" />
</tr>
<% } %>
<% if (renderer.isFeatureSupported(displayer, DataDisplayerFeature.SET_PROGRESS_PROP)) { %>
<tr>
	<td align="left" nowrap="nowrap">
		Progress:
	</td>
	<mvc:include page="../domain/fieldProgress_selector_component.jsp"  flush="true" />
</tr>
<% } %>