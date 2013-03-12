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
<%@ page import="org.jboss.dashboard.ui.HTTPSettings" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="resources.tld" prefix="resource"%>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib prefix="i18n" uri="http://jakarta.apache.org/taglibs/i18n-1.0" %>

<link rel="StyleSheet" media="screen" type="text/css" href="<mvc:context uri="/components/modalDialogComponent/css/PopUps.css" />" />

<i18n:bundle baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>" />

<factory:property bean="org.jboss.dashboard.ui.components.ModalDialogComponent" property="modalDialogFormatter" id="modalDialogFormatter">
    <mvc:formatter name="<%=(String) modalDialogFormatter%>">
        <mvc:fragment name="outputHead">
            <mvc:fragmentValue name="title" id="title">
                <mvc:fragmentValue name="width" id="width">
                    <mvc:fragmentValue name="height" id="height">
                        <mvc:fragmentValue name="isDraggable" id="isDraggable">
                            <div id="ModalDialogPopUp"
                                class="PopUpLightbox"
                                style=" width:<%=((Integer)width).intValue() + 40%>px; height:<%=((Integer)height).intValue() +40%>px; display: none;">

                            <div id="<factory:encode name="modalDialogHeader"/>" class="PopUpLBHeader"
                                    <%
                                        if (Boolean.TRUE.equals(isDraggable)) {
                                    %>
                                 style="cursor:move;"
                                 onmouseover="enableModalDialogDraggable()"
                                    <%
                                        }
                                    %>
                                    >
                                <table style="width:100%;" border="0" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td align="left" style="font-weight:bold; padding-left:10px;"><%=title%></td>
                                        <td align="right"  style="padding:3px 5px 3px 0;">
                                            <a href="<factory:url bean="org.jboss.dashboard.ui.components.ModalDialogComponent" action="close"/>" id="<factory:encode name="closeModalComponent"/>"><img border="0" title="Close" src="<static:image relativePath="general/ClosePopUp.png"/>"/></a>
                                            <script defer="defer">setAjax("<factory:encode name="closeModalComponent"/>")</script>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            <div id="modalAjaxLoadingDiv" style="position:absolute;position: absolute; left: 50%; top: 50%; z-index: 6000; opacity: 0.6; display: none;">
                                <img src="<static:image relativePath="general/loading.gif"/>" title="<i18n:message key="ui.admin.configuration.tree.loading"/>">
                            </div>
                            <div id="PopUpContent">
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragment>

        <mvc:fragment name="output">
            <mvc:fragmentValue name="componentName" id="componentName">
                <div id="<%=HTTPSettings.AJAX_AREA_PREFFIX + "modal_component_" + componentName%>">
                    <factory:useComponent bean="<%=(String)componentName%>"/>
                </div>
            </mvc:fragmentValue>
        </mvc:fragment>

        <mvc:fragment name="outputEnd">
            <mvc:fragmentValue name="isModal" id="isModal">
                <mvc:fragmentValue name="isDraggable" id="isDraggable">
                    </div>
                    </div>
<%
    if (Boolean.TRUE.equals(isModal)) {
%>
                    <div id="vellumShade" ></div>
<%
} else {
%>
                    <div id="vellumNoShade"></div>
<%
    }
%>
                    <script type="text/javascript" defer="defer">

                        centerModalDiv();
<%
    if (Boolean.TRUE.equals(isDraggable)) {
%>
                        var modalDialogDraggable = false;
                        function enableModalDialogDraggable() {
                            if (!modalDialogDraggable) {
                                new Draggable('ModalDialogPopUp', {ghosting: !IE,revert:false,handle:'<factory:encode name="modalDialogHeader"/>'});
                                modalDialogDraggable = true;
                            }
                        }
<%
    }
%>
                    </script>

                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragment>
    </mvc:formatter>
</factory:property>