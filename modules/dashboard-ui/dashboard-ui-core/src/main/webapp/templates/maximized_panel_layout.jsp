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
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.taglib.LocalizeTag"%>
<%@ page import="org.jboss.dashboard.ui.UISettings" %>
<%@ page import="org.jboss.dashboard.ui.controller.RequestContext" %>
<%@ include file="../common/global.jsp" %>
<%@ taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%
    Panel maximizedPanel = currentSection.getMaximizedPanel(session);
    Panel panel = maximizedPanel;
    String panelTitle = LocalizeTag.getLocalizedValue(panel.getTitle(), LocaleManager.currentLang(),true);
%>
<div id="Region_Panel_Container_<%= panel.getPanelId()%>" style=" border: solid; border-width: 1px; border-color: gray; width: 100%; height: 100%; position: relative; text-align:left; top:0; left:0;
             <%=panel.getHeight()>0?"height: "+panel.getHeight():""%>">
    <% RequestContext.lookup().activatePanel(panel); %>
    <mvc:include page="render_panel_content.jsp" flush="true"/>
    <% RequestContext.lookup().deactivatePanel(panel); %>
    <div id="Region_Panel_Menu_Link<%=panel.getPanelId()%>"
         style="text-align: right; height:0; width:100%; visibility: visible; border:none; position: absolute; top:0; left:0">
        <%
            request.setAttribute("panel", panel);
            String configString = UISettings.lookup().getPanelMenuRenderPage();
        %>
        <jsp:include page="<%=configString%>" flush="true">
            <jsp:param name="title" value="<%=String.valueOf(panelTitle)%>"/>
        </jsp:include>
        <% request.removeAttribute("panel"); %>
    </div>
</div>
