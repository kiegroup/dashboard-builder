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
<%@ page import="org.jboss.dashboard.workspace.Panel" %>
<%@ page import="java.util.Properties" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib uri="factory.tld" prefix="factory" %>

<%@ include file="../common/global.jsp" %>

<mvc:formatter name="org.jboss.dashboard.ui.formatters.RenderTabbedRegionFormatter">
    <mvc:fragment name="regionStartWithoutTitle">
<table cellpadding="0" cellspacing="<mvc:fragmentValue name="cellspacingPanels"/>" border="0" width="100%" >
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="regionStartWithTitle">
        <mvc:fragmentValue name="numPanels" id="numPanels">
            <mvc:fragmentValue name="regionId" id="regionId">
                <mvc:fragmentValue name="regionDescription" id="regionDescription">
<div>
    <div style=" display:none;  background-color: #404040;" id="regionDropZoneContainer<%=regionId%>">
        <div id="regionDropZone_<%=regionId%>" style="padding:1px; height:18px; vertical-align:bottom; text-align:left;">
            <b style="padding:4px 0 0 10px; color: #FFFFFF;"><%=regionDescription%></b>
        </div>
        <% if (((Integer) numPanels).intValue() == 0) {%>
        <div style="Background-Color: #ECECEC; height:30px; text-align:center; padding:5px; padding-top:10px;">
            <i18n:message key="ui.sections.noPanelsInRegion"/>
        </div>
        <%}%>
    </div>
    <script type="text/javascript" language="javascript" defer="defer">
        setTimeout("doDropable('regionDropZone_<%=regionId%>', '<%=regionId%>', '<%=numPanels%>');",10);
    </script>
</div>
<table cellpadding="0" cellspacing="<mvc:fragmentValue name="cellspacingPanels"/>" border="0" width="100%" >
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="Region Panels Start">
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="beforeTabs">
    <tr>
        <td>
            <div style="width: 100%;" class="menup">
                <table cellspacing="0" cellpadding="0" border="0">
                    <tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputTab">
        <mvc:fragmentValue name="panel" id="panel">
            <mvc:fragmentValue name="selected" id="selected">
                        <td
<%
    if (((Boolean) selected).booleanValue()) {
%>
                            class="current"
<%
    }
%>
                        >
                            <div id='Tab_For_Panel<%=((Panel)panel).getPanelId()%>'
<%
    if (adminMode) {
%>
                                 class="popupDraggable" style="cursor:move; position:relative;"
<%
    }
%>
                            >
                                <span style="display:none">Panel:<%=((Panel)panel).getPanelId()%></span>
                                <a id="<factory:encode name='<%="region_tab_" + ((Panel)panel).getPanelId()%>'/>"
                                    href="<mvc:fragmentValue name="url"/>"
                                    onclick="if ( window.disableMenuForPanel ) {
                                        window.disableMenuForPanel = false; return false;
                                        } else
                                        return true">
                                    <span><mvc:fragmentValue name="tabTitle"/></span>
                                </a>
                            </div>
                            <script type="text/javascript" defer>
                                if (!IE) setAjax('<factory:encode name='<%="region_tab_" + ((Panel)panel).getPanelId()%>'/>');
<%
    if(adminMode) {
%>
                                setTimeout("doDraggable('Tab_For_Panel<%=((Panel)panel).getPanelId()%>')",10);
<%
    }
%>
                            </script>
                        </td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStartTab">
                        <td>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEndTab">
                        </td>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="afterTabs">
                    </tr>
                </table>
            </div>
        </td>
    </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="Region Panels End">
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="panelOutputStart">
    <tr>
        <td valign="top" align="left" height="100%" width="<mvc:fragmentValue name="recommendedWidth"/>">
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="panelContentWithoutMenu">
        <mvc:fragmentValue name="panel" id="panel">
            <div id="Region_Panel_Container_<%=((Panel)panel).getPanelId()%>"
                    <%=((Panel) panel).getHeight() > 0 ? "style=\"height:" + ((Panel) panel).getHeight() + "\"" : ""%>>
                <%@ include file="render_panel_content.jsp" %>
            </div>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="panelContentWithMenu">
        <mvc:fragmentValue name="panel" id="panel">
            <mvc:fragmentValue name="panelTitle" id="panelTitle">
                <mvc:fragmentValue name="editMode" id="editMode">
                    <div id="Region_Panel_Link_Container<%=((Panel)panel).getPanelId()%>"
                         style="height:0px; position:relative">
                        <div id="Region_Panel_Menu_Link<%=((Panel)panel).getPanelId()%>"
                             style="text-align: right; height:0px; width:100%; border:none; position: absolute; top:-4px; left:4px; opacity: 0.2;"
                             onmouseover="$('Region_Panel_Menu_Link<%=((Panel)panel).getPanelId()%>').setOpacity(1);"
                             onmouseout="$('Region_Panel_Menu_Link<%=((Panel)panel).getPanelId()%>').setOpacity(0.2);">
                            <%
                                request.setAttribute("panel", panel);
                                String configString = ((Properties) Factory.lookup("org.jboss.dashboard.ui.formatters.DisplayConfiguration")).getProperty("panelMenuRenderPage");
                            %>
                            <jsp:include page="<%=configString%>" flush="true">
                                <jsp:param name="title" value="<%=String.valueOf(panelTitle)%>"/>
                            </jsp:include>
                            <%
                                request.removeAttribute("panel");
                            %>
                        </div>
                    </div>
                    <div id="Region_Panel_Container_<%=((Panel)panel).getPanelId()%>"
                         style=" <%=Boolean.TRUE.equals(editMode) ? "" : "border: solid 1px #CCCCCC; "%>margin: 0; <%=((Panel)panel).getHeight()>0?"height: "+((Panel)panel).getHeight():""%>">
                        <%@ include file="render_panel_content.jsp" %>
                    </div>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="panelDropRegion">
        <mvc:fragmentValue name="regionId" id="regionId">
            <mvc:fragmentValue name="index" id="index">
                <div style="overflow:hidden; background-color: #BBBBBB;border: 1px dashed #404040 ;display:none;
                        height:<mvc:fragmentValue name="height"/>;
                        width:<mvc:fragmentValue name="width"/>;
                        " id="<%="panelDropZoneContainer_" + regionId + "_" + index%>">
                    <div id="<%="panelDropZone_" + regionId + "_" + index%>"
                         style="width:100%; height:100%; vertical-align:bottom; text-align:left; overflow:hidden; ">
                        &nbsp;<br>
                    </div>
                </div>
                <script type="text/javascript" language="javascript" defer="defer">
                    setTimeout("doDropable('<%="panelDropZone_" + regionId + "_" + index%>', '<%=regionId%>', '<%=index%>');",10);
                </script>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="panelOutputEnd">
        </td>
    </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="regionEnd">
</table>
    </mvc:fragment>
</mvc:formatter>