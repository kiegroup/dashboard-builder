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
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="factory.tld" prefix="factory" %>

<%@ include file="../common/global.jsp" %>
<link rel="StyleSheet" media="screen" type="text/css" href="<mvc:context uri="/templates/css/administration_bar.css"/>">

<div id="menuShow">
<%-- <fieldset id="fieldSetMenu">  --%>
    <table id="administrationMenuTable">
                    <tr>
                       <jsp:include page="workspace_selector.jsp" flush="true"/>
                        <jsp:include page="section_selector.jsp" flush="true"/>
                        <td class="Right">
                          <div class="select">
                            <form action="<factory:formUrl/>" method="POST">
                                    <factory:handler bean="org.jboss.dashboard.ui.config.TreeShortcutHandler" action="changeLanguage"/>
                                    <mvc:formatter name="org.jboss.dashboard.ui.formatters.ForFormatter">
                                        <mvc:formatterParam name="factoryElement" value="org.jboss.dashboard.LocaleManager"/>
                                        <mvc:formatterParam name="property" value="platformAvailableLocales"/>
                                        <mvc:fragment name="outputStart">
                                           <div class="select"><select class="select" onchange="this.form.submit();" name="lang" >

                                        </mvc:fragment>
                                        <mvc:fragment name="output">
                                            <mvc:fragmentValue name="index" id="index">
                                                <mvc:fragmentValue name="element" id="locale">
                                                    <%  String selected;
                                                        if (((Locale) locale).toString().equals(LocaleManager.currentLang())) selected = "selected class=\"skn-important\"";
                                                        else selected="";
                                                    %>
                                                    <option <%=selected%>  value="<%=((Locale)locale).toString()%>">
                                                        <%=StringUtils.capitalize(((Locale)locale).getDisplayName((Locale)locale))%>
                                                    </option>
                                                </mvc:fragmentValue>
                                            </mvc:fragmentValue>
                                        </mvc:fragment>
                                        <mvc:fragment name="outputEnd">
                                            </select></div>
                                        </mvc:fragment>
                                    </mvc:formatter>
                            <i18n:message key="ui.configuration" id="configModeMsg"/>
                            <a href="<factory:url friendly="false" bean="org.jboss.dashboard.ui.NavigationManager" action='config'/>">
                            <img  border="0" src="<static:image relativePath="adminHeader/processes.png"/>" title="<%=configModeMsg%>"/>
                            </a>
								<i18n:message key="ui.logout" id="loginLogoutMsg"/>
								<a href="<factory:url bean="org.jboss.dashboard.ui.components.LogoutComponent" action="logout"/>" onclick="return confirm('<i18n:message key="ui.workspace.confirmLogout">!!!Desea desconectarse</i18n:message>')">
									<img src="<static:image relativePath="adminHeader/login-logout.png"/>"   border="0" title="<%=loginLogoutMsg%>"/>
                            </a>
                       </form>
                       </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="shadow" colspan="3"><a href="#" onClick="moveOffMenu();moveOnSelector();return false;"><img src="<static:image relativePath="adminHeader/admin_up.png"/>" title="<i18n:message key="ui.hideAdminMenu"/>"></a></td>
                    </tr>
    </table>
<%-- </fieldset> --%>
</div>

<div id="menuSelect"></div>

<div id="menuOn">
<table>
    <tr>
        <td class="shadow"><a href="#" onClick="moveOnMenu();moveOffSelector();return false;"><img src="<static:image relativePath="adminHeader/admin_up.png"/>" title="<i18n:message key="ui.showAdminMenu"/>"></a></td>
    </tr>
</table>
</div>


<SCRIPT LANGUAGE="JavaScript">
    var spacerNode = document.createElement("div");
    spacerNode.style.height="24px";
    spacerNode.style.padding="0px";
    spacerNode.style.margin="6px";
    spacerNode.style.border="none 0px";
    spacerNode.id="spacer";
    document.body.insertBefore(spacerNode, document.body.childNodes[0]);
    document.body.insertBefore(document.getElementById("menuShow"),document.body.childNodes[0]);
</SCRIPT>

<!--
Put the following script immediately *after* the
<div>'s (above) in your page. Set the variables as
indicated in the script.
//-->

<SCRIPT LANGUAGE="JavaScript">

Show ="yes";

var OffX = -100;

// Set the PosX and PosY variables
// to the location on the screen where the
// menu should position (in pixels) when stopped.

var PosX =  0;
var PosY =  0;

// Usually, use the settings shown; but you can
// change the speed and the increment of motion
// across the screen, below.

var speed        = 1;
var increment    = 5;
var incrementNS4 = 25; // for slower NS4 browsers

// do not edit below this line
// ===========================

var is_NS = navigator.appName=="Netscape";
var is_Ver = parseInt(navigator.appVersion);
var is_NS4 = is_NS&&is_Ver>=4&&is_Ver<5;
var is_NS5up = is_NS&&is_Ver>=5;

var MenuX = OffX;
var SelX = PosX;
var sPosX = PosX;
var sOffX = OffX;

if (Show == "yes") {
sPosX = OffX;
sOffX = PosX;
MenuX = sOffX;
SelX = sPosX;
}

if (is_NS4) {
increment = incrementNS4;
Lq = "document.layers.";
Sq = "";
eval(Lq+'menuSelect'+Sq+'.left=sPosX');
eval(Lq+'menuShow'+Sq+'.left=sOffX');
eval(Lq+'menuSelect'+Sq+'.top=PosY');
eval(Lq+'menuShow'+Sq+'.top=PosY');
} else {
Lq = "document.all.";
Sq = ".style";
document.getElementById('menuSelect').style.left = sPosX+"px";
document.getElementById('menuShow').style.left = sOffX+"px";
document.getElementById('menuSelect').style.top = PosY+"px";
document.getElementById('menuShow').style.top = PosY+"px";
}

function moveOnMenu() {
if (MenuX < PosX) {
MenuX = MenuX + increment;
if (is_NS5up) {
document.getElementById('menuShow').style.top = MenuX+"px";
} else {
eval(Lq+'menuShow'+Sq+'.top=MenuX');
}
setTimeout('moveOnMenu()',speed);
   }
}

function moveOffMenu() {
if (MenuX > OffX) {
MenuX = MenuX - increment;
if (is_NS5up) {
document.getElementById('menuShow').style.top = MenuX+"px";
} else {
eval(Lq+'menuShow'+Sq+'.top=MenuX');
}
setTimeout('moveOffMenu()',speed);
   }
}

function moveOffSelector() {
    if (SelX > OffX) {
        SelX = SelX - increment;
        if (is_NS5up) {
            document.getElementById('menuSelect').style.top = SelX+"px";
            //document.getElementById('spacer').style.height += increment;
        } else {
            eval(Lq+'menuSelect'+Sq+'.top=SelX');
            var spacer = document.getElementById('spacer');
            spacer.style.pixelHeight += increment;
            if( spacer.style.pixelHeight > 0)
                spacer.style.display='block';
            else
                spacer.style.display='none';
        }
        setTimeout('moveOffSelector()',speed);
    }
    else{
        document.getElementById('spacer').style.display='block';
    }
}

function moveOnSelector() {
    if (SelX < PosX) {
        SelX = SelX + increment;
        if (is_NS5up) {
            document.getElementById('menuSelect').style.top = SelX+"px";
            //document.getElementById('spacer').style.height -= increment;
        } else {
            eval(Lq+'menuSelect'+Sq+'.top=SelX');
            var spacer = document.getElementById('spacer');
            spacer.style.pixelHeight -= increment;
            if( spacer.style.pixelHeight > 0)
                spacer.style.display='block';
            else
                spacer.style.display='none';
        }
        setTimeout('moveOnSelector()',speed);
    }
    else{
        document.getElementById('spacer').style.display='none';
    }
}
//  End -->
</script>
