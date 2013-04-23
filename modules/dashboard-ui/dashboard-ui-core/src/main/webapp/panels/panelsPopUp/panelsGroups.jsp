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
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="org.jboss.dashboard.ui.panel.PopupPanelsHandler" %>
<%@taglib uri="mvc_taglib.tld" prefix="mvc" %>

<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>


<static:image relativePath="general/16x16/ico-page.png" id="panelURI"/>
<static:image relativePath="general/16x16/ico-folder_closed.png" id="folderClosedURI"/>
<static:image relativePath="general/16x16/ico-folder_open.png" id="folderOpenURI"/>
<static:image relativePath="general/16x16/ico-new_page.png" id="newPanelURI"/>

<static:image relativePath="general/16x16/ico-new_page.png" id="unknownURI"/>
<static:image relativePath="general/16x16/ico-help.png" id="unknownURI"/>
<mvc:formatter name="org.jboss.dashboard.ui.panel.PopupPanelsGroupFormatter">
    <%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <table class="TableGroup">
    </mvc:fragment>
    <%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputCategoryStart">
        <mvc:fragmentValue name="uid" id="uid">
            <mvc:fragmentValue name="groupId" id="groupId">
                <mvc:fragmentValue name="groupThumbnail" id="groupThumbnail">
                    <tr>
                        <td class="Img"><a id="<%="categLink_image_"+uid%>" href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup">
                <factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=groupId%>"/>
                </factory:url>" class="popupTreeLink"><img width="24" height="25" src="<static:image relativePath="<%=(String)groupThumbnail%>" />" class="popupTreeImage"></a></td>
                        <td class="Group"><a id="<%="categLink_name_"+uid%>"
                                             href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup"><factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=groupId%>"/></factory:url>" class="popupTreeLink">
                            <mvc:fragmentValue name="name"/>
                        </a></td>
                        <td class="MAxMin"><a id="<%="categLink_plus_"+uid%>" href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup">
                <factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=groupId%>"/>
                </factory:url>" class="popupTreeLink"><img border="0" title="<mvc:fragmentValue name="name"/>" src="<static:image relativePath="/panels/groups/groupPanelMax.png" />"></a></td>
                    </tr>
                    <script type="text/javascript" defer="defer">
                        setAjax("<%="categLink_image_"+uid%>");
                        setAjax("<%="categLink_name_"+uid%>");
                        setAjax("<%="categLink_plus_"+uid%>");
                    </script>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputOpenedCategory">
        <mvc:fragmentValue name="uid" id="uid">
            <mvc:fragmentValue name="groupId" id="groupId">
                <mvc:fragmentValue name="groupThumbnail" id="groupThumbnail">
                    <tr>
                        <td class="Img"><a id="<%="categLink_image_"+uid%>" href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup">
                <factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=groupId%>"/>
                </factory:url>" class="popupTreeLink"><img width="24" height="25" src="<static:image relativePath="<%=(String)groupThumbnail%>" />" class="popupTreeImage"></a></td>
                        <td class="Group"><b><a id="<%="categLink_name_"+uid%>"
                                                href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup"><factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=groupId%>"/></factory:url>" class="popupTreeLink">
                            <mvc:fragmentValue name="name"/>
                        </a></b></td>
                        <td class="MAxMin"><a id="<%="categLink_plus_"+uid%>" href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup">
                <factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=groupId%>"/>
                </factory:url>" class="popupTreeLink"><img border="0" title="<mvc:fragmentValue name="name"/>" src="<static:image relativePath="/panels/groups/groupPanelMin.png" />" ></a></td>
                    </tr>
                    <script type="text/javascript" defer="defer">
                        setAjax("<%="categLink_image_"+uid%>");
                        setAjax("<%="categLink_name_"+uid%>");
                        setAjax("<%="categLink_plus_"+uid%>");
                    </script>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputProviderStart">
        <mvc:fragmentValue name="uid" id="uid">
            <mvc:fragmentValue name="categoryId" id="categoryId">
                <mvc:fragmentValue name="providerId" id="providerId">
                    <mvc:fragmentValue name="thumbnail" id="thumbnail">
                        <mvc:fragmentValue name="isSelected" id="isSelected">

                                <tr>
                                    <td>&nbsp;</td>
                                    <td colspan="2">
                                        <table  class="TableGroup02">
                                            <tr>
                                                <td class="Img02">
                                                    <img src="<static:image relativePath="<%=(String)thumbnail%>" />" class="popupTreeImage" width="24" height="24">
                                                </td>
                                                <td class="Group02">
<%
    if(((Boolean)isSelected).booleanValue()) {
%>
                                                    <b>
<%
    }
%>
                                                        <a id="<factory:encode name='<%= "panelGroup_" + categoryId + "_" + providerId %>'/>"
                                                           href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup"><factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=categoryId%>"/><factory:param name="<%=PopupPanelsHandler.PANEL_INSTANCE_ID%>" value="<%=providerId%>"/></factory:url>">
                                                            <mvc:fragmentValue name="name"/>
                                                        </a>
<%
    if(((Boolean)isSelected).booleanValue()) {
%>
                                                    </b>
<%
    }
%>
                                                </td>
                                            </tr>
                                        </table>
                                        <script type="text/javascript" defer="defer">
                                            setAjax("<factory:encode name='<%= "panelGroup_" + categoryId + "_" + providerId %>'/>");
                                        </script>
                                    </td>
                                </tr>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputGroup">
        <mvc:fragmentValue name="categoryId" id="categoryId">
            <mvc:fragmentValue name="providerId" id="providerId">
                <mvc:fragmentValue name="subcategoryId" id="subcatId">
                    <mvc:fragmentValue name="isSelected" id="isSelected">

                    <mvc:fragmentValue name="uid" id="uid">
                        <mvc:fragmentValue name="name" id="name">
                            <tr>
                                <td>&nbsp;</td>
                                <td colspan="2">
                                    <table  class="TableGroup02">
                                        <tr>
                                            <td class="Img02">
                                                &nbsp;
                                            </td>
                                            <td class="Img02">
                                                <img src="<%=folderClosedURI%>" class="popupTreeImage">
                                            </td>
                                            <td class="Group02">

                                                <div id="<%="parent_group_" + providerId+ "_" + name%>" class="popupTreeNode" style="overflow:hidden; height:18px" title="<%=name%>">
                                                    <%  if(((Boolean)isSelected).booleanValue()){%> <b><% }%>
                                                    <a href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup">
                <factory:param name="<%=PopupPanelsHandler.PANEL_INSTANCE_ID%>" value="<%=providerId%>"/>
                <factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=categoryId%>"/>
                <factory:param name="<%=PopupPanelsHandler.PANEL_SUBCATEGORY_ID%>" value="<%=subcatId%>"/>
                </factory:url>" class="popupTreeLink"
                                                            >
                                                        <%=name%>
                                                    </a><%  if(((Boolean)isSelected).booleanValue()){%> </b><% }%>
                                                </div>

                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputProviderEnd">
        </td>
        </tr>
        </table>
        </td>
        </tr>
    </mvc:fragment>
    <%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputCategoryEnd">
        </div>
    </mvc:fragment>
    <%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        </table>
    </mvc:fragment>
    <%----------------------------------------------------------------------------%>
    <mvc:fragment name="empty">
        <span class="skn-important"><i18n:message key="ui.panels.thereAreNoPanels"/></span>
    </mvc:fragment>
    <%----------------------------------------------------------------------------%>
</mvc:formatter>
