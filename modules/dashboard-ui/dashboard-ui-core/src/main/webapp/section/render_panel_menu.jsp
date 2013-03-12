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
<%@ page import="org.jboss.dashboard.ui.SessionManager" %>
<%@ page import="org.jboss.dashboard.workspace.Panel" %>
<%@ page import="org.jboss.dashboard.ui.HTTPSettings" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>
<% Panel panel = (Panel) request.getAttribute("panel"); %>

<mvc:formatter name="org.jboss.dashboard.ui.formatters.RenderPanelMenuFormatter">
<mvc:formatterParam name="panel" value="<%=panel%>"/>
<mvc:fragment name="movePanel">
    <script type="text/javascript" defer>
        setTimeout("doDraggable('Move_Panel<%=panel.getPanelId()%>')",10);
    </script>
</mvc:fragment>
<mvc:fragment name="menuLink">
    <table cellpadding="0" cellspacing="0" border="0" align="right">
        <tr>
           <td>
           <a href="#" id="Move_Panel<%=panel.getPanelId()%>" class="popupDraggable" style="cursor:<mvc:fragmentValue name="cursorStyle"/>"
           onMouseOver="document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = '#CB2127'"
           onMouseOut="if( !menuIsOpen<%=panel.getPanelId()%> )document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = '#CCCCCC'"
           onclick="return false;" title="<mvc:fragmentValue name="title"/>"><span style="display:none">Panel:<%=panel.getPanelId()%></span><img  border="0" src="<static:image relativePath="general/movePanel.gif"/>" alt="Move"></a>
           </td>
           <td>
           <a href="#" id="Menu_For_Panel<%=panel.getPanelId()%>"
           onMouseOver="document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = '#CB2127'"
           onMouseOut="if( !menuIsOpen<%=panel.getPanelId()%> )document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = '#CCCCCC'"
           onclick="panelMenuShow<%=panel.getPanelId()%>(event); return false;" title="<mvc:fragmentValue name="title"/>"><span style="display:none">Panel:<%=panel.getPanelId()%></span><img border="0" src="<static:image relativePath="general/PopMenu.gif"/>" alt="Menu"></a>
           </td>
        </tr>
    </table>
</mvc:fragment>
<mvc:fragment name="menuStart">
    <script type="text/javascript" defer>
        var menuIsOpen<%=panel.getPanelId()%> = false;
        function panelMenuShow<%=panel.getPanelId()%>(event) {
            var menudiv = document.getElementById("<%=HTTPSettings.AJAX_AREA_PREFFIX + "Menu_For_Panel_" + panel.getPanelId() %>");
            var menuLinkDiv = document.getElementById("Region_Panel_Menu_Link<%=panel.getPanelId()%>");
            var containerDiv = document.getElementById("Region_Panel_Container_<%=panel.getPanelId()%>");
            var linkToMenu = document.getElementById("Menu_For_Panel<%=panel.getPanelId()%>");
            if (menudiv.style.visibility == 'hidden' && event) { //Show menu
                var x,y;
                if(event.clientX){x=event.clientX;}else if(event.pageX){x=event.pageX;}
                if(event.clientY){y=event.clientY;}else if(event.pageY){y=event.pageY};

                // Correct document scroll
                if(window.pageYOffset){ y+=window.pageYOffset; }
                else if( document.documentElement.scrollTop ){ y+=document.documentElement.scrollTop; }
                if(window.pageXOffset){ x+=window.pageXOffset; }
                else if( document.documentElement.scrollLeft){ x+= document.documentElement.scrollLeft; }

                menudiv.style.top = y+"px";
                if(x>menudiv.offsetWidth) x -=  menudiv.offsetWidth;
                menudiv.style.left = x+"px";
                //Hide all other divs
                var alldivs = document.getElementsByTagName("div");
                for (i = 0; i < alldivs.length; i++) {
                    if (alldivs[i].id.indexOf("Region_Panel_Menu_Link") == 0
                            &&
                        !( alldivs[i].id == ("Region_Panel_Menu_Link<%=panel.getPanelId()%>") )
                            ) {
                        alldivs[i].style.visibility = "hidden";
                    }
                }
                document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = 'gray';
                menuIsOpen<%=panel.getPanelId()%> = true;
                menudiv.style.visibility = 'visible';
                menudiv.style.display = 'block';
                menudiv.style.zIndex = 3000;
            } else {
                //Hide menu
                menudiv.style.visibility = 'hidden';
                var alldivs = document.getElementsByTagName("div");
                for (i = 0; i < alldivs.length; i++) {
                    if (alldivs[i].id.indexOf("Region_Panel_Menu_Link") == 0) {
                        alldivs[i].style.visibility = "visible";
                    }
                }
                document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = '#ffd700';
                menuIsOpen<%=panel.getPanelId()%> = false;
            }
            return false;
        }
    </script>

    <%--  MENU START --%>
    <div id="<%=HTTPSettings.AJAX_AREA_PREFFIX + "Menu_For_Panel_" + panel.getPanelId() %>" style="visibility:hidden;" class="PrincipalShadow">
    <table>
</mvc:fragment>
<mvc:fragment name="menuTitle">
    <tr>
        <td colspan="2" class="Header">
        <mvc:fragmentValue name="title"/>
        </td>
    </tr>
    <tr>
        <td colspan="2" class="HeaderShadow">
        </td>
    </tr>
</mvc:fragment>
<mvc:fragment name="menuEntry">
    <mvc:fragmentValue name="menukey" id="menukey">
    <mvc:fragmentValue name="isAjax" id="isAjax">
    <tr onmouseOver="this.style.background='#CCCCCC';"
        onmouseOut="this.style.background='#F1F1F1';">
        <td class="Left">
            <mvc:fragmentValue name="imageKey" id="imageKey">
                <mvc:fragmentValue name="imageAlt" id="alt">
                    <img src="<static:image relativePath="<%=imageKey.toString()%>"/>" border="0" alt="<%=alt%>"/>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </td>
        <td class="Right">
            <a href="<mvc:fragmentValue name="url"/>" id="<%="Link_for_panel_action_" + panel.getPanelId() + "_" + menukey%>">
                    <i18n:message key='<%=menukey.toString()+".menu"%>'>!!!<%=menukey + ".menu"%></i18n:message>
            </a>
<%
    if (Boolean.TRUE.equals(isAjax)) {
%>
            <script type="text/javascript" defer="defer">
                setAjax("<%="Link_for_panel_action_" + panel.getPanelId() + "_" + menukey%>");
            </script>
<%
    }
%>
        </td>
    </tr>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="menuEntryDisabled">
    <tr>
        <td class="Left">
            <mvc:fragmentValue name="imageKey" id="imageKey">
                <mvc:fragmentValue name="imageAlt" id="alt">
                    <img src="<static:image relativePath="<%=imageKey.toString()%>"/>" border="0" alt="<%=alt%>"/>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </td>
        <td class="Right">
            <mvc:fragmentValue name="menukey" id="menukey">
                <i18n:message key='<%=menukey.toString()+".menu"%>'>!!!<%=menukey + ".menu"%></i18n:message>
            </mvc:fragmentValue>
        </td></tr>
</mvc:fragment>
<mvc:fragment name="menuSeparator">
</mvc:fragment>
<mvc:fragment name="menuEnd">
    <tr onmouseOver="this.style.background='#CCCCCC';"
        onmouseOut="this.style.background='#F1F1F1';">
        <td class="Left">
            <mvc:fragmentValue name="imageKey" id="imageKey">
                <img src="<static:image relativePath="<%=imageKey.toString()%>"/>" border="0" alt="X"/>
            </mvc:fragmentValue>
        </td>
        <td class="Right">
            <a href="#"
               onclick="panelMenuShow<%=((Panel)panel).getPanelId()%>(null); return false;">
                <mvc:fragmentValue name="menukey" id="menukey">
                    <i18n:message key="ui.panel.cancel.menu">!!!Cancelar</i18n:message>
                </mvc:fragmentValue>
            </a>
        </td>
    </tr>
    </table>
    </div>
    <script language="Javascript" defer>
        function createIframeFor<%=((Panel) panel).getPanelId()%>() {
            var masterDiv = document.getElementById("<%=HTTPSettings.AJAX_AREA_PREFFIX + "Menu_For_Panel_" + panel.getPanelId() %>");
            var ifr = document.createElement("iframe");
            ifr.style.zIndex = -1;
            ifr.style.width = "100%";
            ifr.style.height = masterDiv.scrollHeight;
            ifr.style.position = "absolute";
            ifr.style.top = 0;
            ifr.style.left = 0;
            masterDiv.appendChild(ifr);
        };
        function moveDivToBody<%=((Panel) panel).getPanelId()%>() {
            var element = document.getElementById("<%=HTTPSettings.AJAX_AREA_PREFFIX + "Menu_For_Panel_" + panel.getPanelId() %>");
            if (IE && document.readyState != 'complete') {
                setTimeout('moveDivToBody<%=((Panel) panel).getPanelId()%>()', 100);
            } else {
                element.parentNode.removeChild(element);
                document.body.appendChild(element);
                if (IE && navigatorVersion >= 6) {
                    createIframeFor<%=((Panel) panel).getPanelId()%>();
                }
            }
        };
        setTimeout('moveDivToBody<%=((Panel) panel).getPanelId()%>()', 100);
    </script>
</mvc:fragment>
</mvc:formatter>
