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
<%@ page import="java.util.Properties" %>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<%@ include file="../common/global.jsp" %>

<mvc:formatter name="org.jboss.dashboard.ui.formatters.RenderRegionFormatter">
    <mvc:fragment name="outputTabbedRegion">
        <div style="width: 100%;" id="<mvc:fragmentValue name="regionId"/>">
            <jsp:include page="render_tabbed_region.jsp" flush="true"/>
        </div>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="regionStartWithoutTitle">
        <table cellpadding="0" cellspacing="<mvc:fragmentValue name="cellspacingPanels"/>" border="0" width="100%" >
        <!-- Table for regionStartWithoutTitle -->
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
                            setTimeout("doDropable('regionDropZone_<%=regionId%>', '<%=regionId%>', <%=numPanels%>)",10);
                        </script>
                    </div>
                    <table cellpadding="0" cellspacing="<mvc:fragmentValue name="cellspacingPanels"/>" border="0" width="100%" >
                    <!-- Table for regionStartWithTitle -->
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="No Panels In Region (normal mode)"><td><!--No panels here--></td></mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="No Panels In Region"><td><!--No panels here--></td></mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="Region Panels Start">
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="Region Panels New Line Start">
        <tr valign="top" height="100%"><!-- Region panels New Line Start -->
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="panelOutputStart">
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
                    setTimeout("doDropable('<%="panelDropZone_" + regionId + "_" + index%>', '<%=regionId%>', <%=index%>)",10);
                </script>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="panelOutputEnd">
        </td>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="Region Panels New Line End">
        </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="Region Panels End">
    </mvc:fragment>
    <%------------------------------------------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="regionEnd">
        </table>
    </mvc:fragment>

</mvc:formatter>
