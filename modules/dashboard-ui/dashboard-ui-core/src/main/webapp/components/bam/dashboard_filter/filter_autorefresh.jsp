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
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.components.DashboardFilterHandler" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.components.filter.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    String componentCode = (String) request.getAttribute("componentCode");
    DashboardFilterHandler handler = DashboardFilterHandler.lookup(componentCode);
    boolean  refreshEnabled = handler.isRefreshEnabled();
%>
<table cellpadding="4" cellspacing="0" border="0" class="skn-table_border" width="">
    <tr class="skn-table_header">
        <td width="10px">
            <%  if (refreshEnabled) { %>
            <img src="<static:image relativePath="general/10x10/play.gif"/>" title="Play" style="border: 0px; opacity: 0.5; -moz-opacity: 0.5; filter: alpha( opacity = 50 );">
            <% } else { %>
            <a id="<panel:encode name="refreshPlayLink"/>" href="<factory:url action="play" bean="<%=handler.getComponentPath()%>"/>">
                <img src="<static:image relativePath="general/10x10/play.gif"/>" title="Play" style="border: 0px;">
            </a>
            <script defer="defer">
                setAjax('<panel:encode name="refreshPlayLink"/>');
            </script>
            <% } %>
        </td>
        <td width="10px">
            <% if (!handler.isRefreshEnabled()) { %>
            <img src="<static:image relativePath="general/10x10/stop.gif"/>" title="Stop" style="border: 0px; opacity: 0.5; -moz-opacity: 0.5; filter: alpha( opacity = 50 );">
            <% } else { %>
            <a id="<panel:encode name="refreshStopLink"/>" onclick="clearTimeout(window.<panel:encode name="clearTimeout"/>);" href="<factory:url action="stop" bean="<%=handler.getComponentPath()%>"/>">
                <img src="<static:image relativePath="general/10x10/stop.gif"/>" title="Stop" style="border: 0px;">
            </a>
            <script defer="defer">
                setAjax('<panel:encode name="refreshStopLink"/>');
            </script>
            <% } %>

        </td>
        <td nowrap="nowrap">
            <input type="text" class="skn-input" size="3" value="<%=handler.getAutoRefreshTimeout()%>" onchange="
                    document.getElementById('<panel:encode name="refreshTimeOut"/>').value = this.value;
                    submitAjaxForm(document.getElementById('<panel:encode name="refreshForm"/>')); return false;">
        </td>
        <td nowrap="nowrap">
            <span id="<panel:encode name="p_timeout"/>" style="color:#FFFFFF;"><%=handler.getAutoRefreshTimeout()%></span>&nbsp;''
        </td>
    </tr>
</table>
<script type="text/javascript" defer="defer">
    function <panel:encode name="refresh_timeout"/>() {
        if (<panel:encode name="timeout_value"/> == 0) {
            submitAjaxForm(document.getElementById('<panel:encode name="refreshForm"/>'));
        } else {
            <panel:encode name="timeout_value"/> -= 1;
            document.getElementById('<panel:encode name="p_timeout"/>').innerHTML =  <panel:encode name="timeout_value"/>;
            <panel:encode name="clearTimeout"/> = setTimeout('<panel:encode name="refresh_timeout"/>()', 1000);
        }
    }

    if (window.<panel:encode name="clearTimeout"/>) clearTimeout(window.<panel:encode name="clearTimeout"/>);
    var <panel:encode name="timeout_value"/> = <%=handler.getAutoRefreshTimeout()%>;
    window.<panel:encode name="clearTimeout"/> = 0;
    <% if (refreshEnabled) {%>
    window.<panel:encode name="clearTimeout"/> = setTimeout('<panel:encode name="refresh_timeout"/>()', 1000);
    <% }%>
</script>