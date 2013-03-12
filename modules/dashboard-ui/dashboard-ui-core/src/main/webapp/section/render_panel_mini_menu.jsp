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
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="org.jboss.dashboard.ui.HTTPSettings" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<% Panel panel = (Panel) request.getAttribute("panel"); %>

<mvc:formatter name="org.jboss.dashboard.ui.formatters.RenderPanelMenuFormatter">
<mvc:formatterParam name="panel" value="<%=panel%>"/>
<mvc:fragment name="movePanel">
    <script type="text/javascript" defer>
        setTimeout("doDraggable('Menu_For_Panel<%=panel.getPanelId()%>')",10);
    </script>
</mvc:fragment>
<mvc:fragment name="menuLink">
    <mvc:fragmentValue name="adminMode" id="adminMode">
<%
    if(Boolean.TRUE.equals(adminMode)) {
%>
    <a href="#"
       id="Move_Panel<%=panel.getPanelId()%>" class="popupDraggable"
       style="cursor:<mvc:fragmentValue name="cursorStyle"/>"
       onMouseOver="document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = '#CB2127'"
       onMouseOut="if( !menuIsOpen<%=panel.getPanelId()%> )document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = '#CCCCCC'"
       onclick="return false;"
       title="<mvc:fragmentValue name="title"/>"><span style="display:none">Panel:<%=panel.getPanelId()%></span><img  border="0" src="<static:image relativePath="general/movePanel.gif"/>" alt="Move"></a>
<%
    }
%>
    <a href="#"
       id="Menu_For_Panel<%=panel.getPanelId()%>"
       onMouseOver="document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = '#CB2127'"
       onMouseOut="if( !menuIsOpen<%=panel.getPanelId()%> )document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = '#CCCCCC'"
       onclick="panelMenuShow<%=panel.getPanelId()%>(event); return false;"
       title="<mvc:fragmentValue name="title"/>"><span style="display:none">Panel:<%=panel.getPanelId()%></span><img border="0" src="<static:image relativePath="general/PopMenu.gif"/>" alt="Menu"></a>
    </mvc:fragmentValue>
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
                document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = '#CCCCCC';
                menuIsOpen<%=panel.getPanelId()%> = true;
                menudiv.style.visibility = 'visible';
                menudiv.style.display = 'block';
                menudiv.style.zIndex = 3000;
            }
            else { //Hide menu
                menudiv.style.visibility = 'hidden';
                var alldivs = document.getElementsByTagName("div");
                for (i = 0; i < alldivs.length; i++) {
                    if (alldivs[i].id.indexOf("Region_Panel_Menu_Link") == 0) {
                        alldivs[i].style.visibility = "visible";
                    }
                }
                document.getElementById('Region_Panel_Container_<%=panel.getPanelId()%>').style.borderColor = '#CB2127';
                menuIsOpen<%=panel.getPanelId()%> = false;
            }
            return false;
        }
    </script>

    <%--  MENU START --%>

    <div id="<%=HTTPSettings.AJAX_AREA_PREFFIX + "Menu_For_Panel_" + panel.getPanelId() %>"
    style="background:#d4d0c8;
    padding:0px; z-index: 3000;
    position:absolute; top: 0; left: 0; visibility:hidden; display:block; width:200px; height:auto; "
    ><div style="border:outset; border-width:3px; ">
    <table width="100%" cellspacing=0 cellpadding=0 style="background: #d4d0c8;z-index: 3">
</mvc:fragment>
<mvc:fragment name="menuTitle">
    <tr  style="cursor: default;  vertical-align:middle; background-color:#EEEEEE">
        <td colspan="50"><b><mvc:fragmentValue name="title"/></b></td>
        <td  onclick="panelMenuShow<%=panel.getPanelId()%>(event);" align="right">
            <img src="<static:image relativePath="general/12x12/ico-no-ok.png"/>" border="0" />
        </td>
    </tr>
    <tr>
</mvc:fragment>
<mvc:fragment name="menuEntry">
    <td nowrap valign="middle">
        <mvc:fragmentValue name="imageKey" id="imageKey">
            <mvc:fragmentValue name="imageAlt" id="alt">
                <a
                        title="<mvc:fragmentValue name="menukey" id="menukey"><i18n:message key='<%=menukey.toString()+".menu"%>'>!!!<%=menukey + ".menu"%></i18n:message></mvc:fragmentValue>"
                        href="<mvc:fragmentValue name="url"/>">
                    <resource:image category="skin" border="0" resourceId="<%=imageKey.toString()%>"><%=alt%>
                    </resource:image>
                </a>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </td>
</mvc:fragment>
<mvc:fragment name="menuEnd">
    <td width="100%"></td></tr></table>
    </div>
    </div>
    <script  language="Javascript" defer>
    moveDivToBody<%=panel.getPanelId()%>();
    function createIframeFor<%=panel.getPanelId()%>() {
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
    function moveDivToBody<%=panel.getPanelId()%>() {
        var element = document.getElementById("<%=HTTPSettings.AJAX_AREA_PREFFIX + "Menu_For_Panel_" + panel.getPanelId() %>");
        if (IE && document.readyState != 'complete') {
            setTimeout('moveDivToBody<%=panel.getPanelId()%>()', 100);
        } else {
            element.parentNode.removeChild(element);
            document.body.appendChild(element);
            if (IE && navigatorVersion >= 6) {
                createIframeFor<%=panel.getPanelId()%>();
            }
        }
    };
    </script>
</mvc:fragment>
</mvc:formatter>