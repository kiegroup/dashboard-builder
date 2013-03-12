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
<%@ page import="org.jboss.dashboard.ui.SessionManager"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<%@ taglib prefix="static" uri="static-resources.tld" %>

<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>

<mvc:formatter name="org.jboss.dashboard.ui.config.components.panelstypes.PanelsTypesPropertiesFormatter">
    <mvc:fragment name="outputStart">

        <form name="adminProvidersForm" action="<factory:formUrl friendly="false"/>" method="POST">
        <factory:handler bean="org.jboss.dashboard.ui.config.components.panelstypes.PanelsTypesPropertiesHandler" action="changeProviders"/>
        <p align="left"><i18n:message key="ui.workspace.selectedPanelsMayBeAdded"/></p>
        <table width="100%" border="0" align="center" cellpadding="1" cellspacing="0" class="skn-table_border">
            <tr>
                <td width="50%" nowrap="nowrap">


                        <i18n:message key="ui.workspace.porlets.select"/>&nbsp;&nbsp;

                    <a href="#"
                       onclick='for (var i = 1; i < document.adminProvidersForm.elements.length; i++) {
                                if (document.adminProvidersForm.elements[i].id != "CHKBOX_*")
                                    eval("document.adminProvidersForm.elements[" + i + "].checked = true");
                                }
                                return false;'>
                        <i18n:message key="ui.workspace.porlets.allowAll"/>
                    </a>
                    &nbsp;&nbsp;
                    <a href="#"
                       onclick='var divObjects = document.getElementsByTagName("div");
                                for (var i = 0; i < divObjects.length; i++){
                                    if (divObjects[i].style.display=="block"){
                                        for (var j = 0; j < document.adminProvidersForm.elements.length; j++){
                                            relatedDivId = "DIV_" + document.adminProvidersForm.elements[j].id;
                                            if (relatedDivId == divObjects[i].id)
                                                eval("document.adminProvidersForm.elements[" + j + "].checked = true");
                                        }
                                    }
                                }
                                return false;'>
                        <i18n:message key="ui.workspace.panels.allowOpenGroups"/>
                    </a>
                    &nbsp;&nbsp;
                    <a href="#"
                       onclick='for (var i = 1; i < document.adminProvidersForm.elements.length; i++) {
                                if (document.adminProvidersForm.elements[i].id != "CHKBOX_*")
                                    eval("document.adminProvidersForm.elements[" + i + "].checked = false");
                                }
                                return false;'>
                        <i18n:message key="ui.workspace.porlets.allowNone"/>
                    </a>
                </td>
                <td width="50%" align="right">&nbsp;</td>
            </tr>
        </table>
        <br>
        <table cellpadding="0" cellspacing="0" border="0" align="left">
    </mvc:fragment>
    <%------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputProvidersGroupsStart">
        <tbody>
    </mvc:fragment>
    <mvc:fragment name="outputGroupName">
        <mvc:fragmentValue name="groupName" id="groupName">
        <mvc:fragmentValue name="nProviders" id="nProviders">
        <mvc:fragmentValue name="providerGroup" id="providerGroup">
            <tr>
                <td nowrap width="18">
                    <a href="#"
                       onclick='var div = document.getElementById("DIV_<%=providerGroup%>");
                                var img = document.getElementById("IMG_<%=providerGroup%>");
                                if (div.style.display=="block"){
                                    div.style.display = "none";
                                    img.src = "<static:image relativePath="general/16x16/ico-folder_closed.png"/>";
                                }
                                else {
                                    div.style.display = "block";
                                    img.src = "<static:image relativePath="general/16x16/ico-folder_open.png"/>";
                                }
                                return false;'>
                        <img id="IMG_<%=providerGroup%>" width="18" height="18" border="0" align="absBottom" src="<static:image relativePath="general/16x16/ico-folder_closed.png"/>"/>
                    </a>
                </td>
                <td align="left" width="100%">
                    <b>&nbsp;<%=groupName%> (<%=nProviders%>)</b>
                </td>
            </tr>

        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputDescriptionStart">
        <mvc:fragmentValue name="providerGroup" id="providerGroup">
            <tr>
                <td colspan="2" align="left">
                    <div style="display:none" id="DIV_<%=providerGroup%>">
                        <table cellSpacing="0" cellPadding="0" border="0">
                        <tbody>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputDescription">
        <mvc:fragmentValue name="providerAllowed" id="providerAllowed">
        <mvc:fragmentValue name="providerDescription" id="providerDescription">
        <mvc:fragmentValue name="providerId" id="providerId">
        <mvc:fragmentValue name="providerGroup" id="providerGroup">
                        <tr>
                            <td nowrap width="18"></td>
                            <td align="left">
                                <table cellSpacing="0" cellPadding="0" border="0">
                                <tbody>
                                    <tr>
                                        <td nowrap>
                                            <input id="<%=providerGroup%>"
                                                type="checkbox" <%=((Boolean)providerAllowed).booleanValue() ? "checked" : ""%>
                                                name="<%="CHKBOX_"+providerId%>" value="true">
                                        </td>
                                        <td nowrap>
                                            &nbsp;<mvc:fragmentValue name="providerDescription"/>
                                        </td>
                                    </tr>
                                </tbody>
                                </table>
                            </td>
                        </tr>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputDescriptionEnd">
                    </tbody>
                    </table>
                </div>
            </td>
         </tr>
    </mvc:fragment>
    <mvc:fragment name="outputProvidersGroupsEnd">
        <mvc:fragmentValue name="providerAllowed" id="providerAllowed">
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2" align="left">
                    <table cellSpacing="0" cellPadding="0" border="0">
                    <tbody>
                        <tr>
                            <td nowrap align="center">
                                <p>------------------------------------------------------------------------------------<br>
                                    <input type="checkbox" id="CHKBOX_*" <%=((Boolean)providerAllowed).booleanValue() ? "checked" : ""%>
                                    name="<%="CHKBOX_*"%>" value="true"> <i18n:message key="ui.workspace.allowAllPanels"/>
                                <br>------------------------------------------------------------------------------------<br>
                                </p>
                                <p>
                                    <input class="skn-button" type="submit" name="Submit" value="<i18n:message key="ui.saveChanges">!!! Guardar Cambios</i18n:message>">
                                    &nbsp;&nbsp;&nbsp;
                                    <input class="skn-button" type="reset" name="Reset" value="<i18n:message key="ui.admin.workarea.cancel">!!Cancelar</i18n:message>">
                                </p>
                            </td>
                        </tr>
                    </tbody>
                    </table>
                    <br><br><br>
                </td>
            </tr>
        </tbody>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        </table>
        </form>
    </mvc:fragment>
</mvc:formatter>
<script defer>
    function allowAll(allow) {
        for (var i = 1; i < document.adminProvidersForm.elements.length; i++) {
            if (document.adminProvidersForm.elements[i].id != "CHKBOX_*")
                eval("document.adminProvidersForm.elements[" + i + "].checked = " + allow);
        }
    }
    function allowOpenGroups(allow){
        var divObjects = document.getElementsByTagName("div");
        for (var i = 0; i < divObjects.length; i++){
            if (divObjects[i].style.display=="block"){
                for (var j = 0; j < document.adminProvidersForm.elements.length; j++){
                    relatedDivId = "DIV_" + document.adminProvidersForm.elements[j].id;
                    if (relatedDivId == divObjects[i].id)
                        eval("document.adminProvidersForm.elements[" + j + "].checked = " + allow);
                }
            }
        }
    }
    function invert() {
        for (var i = 1; i < document.adminProvidersForm.elements.length; i++) {
            if (document.adminProvidersForm.elements[i].id != "CHKBOX_*")
                eval("document.adminProvidersForm.elements[" + i + "].checked = !document.adminProvidersForm.elements[" + i + "].checked");
        }
    }
    function collapseOrExpand(group) {
        var div = document.getElementById("DIV_"+group);
        alert(div.value);
        var img = document.getElementById("IMG_"+group);
        alert(img.value);
        if (div.style.display=="block"){
            div.style.display = "none";
            img.src = "<static:image relativePath="general/16x16/ico-folder_closed.png"/>";
        }
        else {
            div.style.display = "block";
            img.src = "<static:image relativePath="general/16x16/ico-folder_open.png"/>";
        }
        return false;
    }
</script>
