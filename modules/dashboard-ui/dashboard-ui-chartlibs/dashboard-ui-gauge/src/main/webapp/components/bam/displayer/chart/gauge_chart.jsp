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
<%@ page import="org.jboss.dashboard.ui.components.chart.GaugeMeterChartViewer" %>
<%@ page import="org.jboss.dashboard.displayer.chart.MeterChartDisplayer" %>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%
    GaugeMeterChartViewer viewer = (GaugeMeterChartViewer) Factory.lookup("org.jboss.dashboard.ui.components.MeterChartViewer_gauge");
    MeterChartDisplayer displayer = (MeterChartDisplayer) viewer.getDataDisplayer();
    String meterValue = (String) request.getAttribute("meterValue");
    String meterMin = (String) request.getAttribute("meterMin");
    String meterMax = (String) request.getAttribute("meterMax");
    String meterTitle = (String) request.getAttribute("meterTitle");
    String meterUnits = (String) request.getAttribute("meterUnits");
    String chartId = (String) request.getAttribute("chartId");
    int chartIndex = (Integer) request.getAttribute("chartIndex");
%>
<div style="width:200px;height:160px;cursor:pointer" id="<%=chartId%>"
    onClick="
    form = document.getElementById('<%="form"+chartId%>');
    form.<%= GaugeMeterChartViewer.PARAM_NSERIE %>.value = <%=chartIndex%>;
    submitAjaxForm(form);"
/>
<form method="post" action='<factory:formUrl friendly="false"/>' id='<%="form"+chartId%>'>
  <factory:handler bean="<%=viewer.getComponentName()%>" action="<%= GaugeMeterChartViewer.PARAM_ACTION %>"/>
  <input type="hidden" name="<%= GaugeMeterChartViewer.PARAM_NSERIE %>" value="0" />
</form>
<script defer="true">
    var g<%=chartId%> = new JustGage({
        id: "<%=chartId%>",
        value: '<%=meterValue%>',
        min: '<%=meterMin%>',
        max: '<%=meterMax%>',
        title: '<%=meterTitle%>',
        label: '<%=meterUnits%>'});

    setTimeout(function() {
        g<%=chartId%>.refresh(<%=meterValue%>);
    }, 200);
</script>
