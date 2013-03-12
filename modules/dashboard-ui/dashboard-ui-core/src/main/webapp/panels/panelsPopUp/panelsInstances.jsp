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
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>


<mvc:formatter name="org.jboss.dashboard.ui.panel.PopupPanelsInstanceFormatter">
    <mvc:fragment name="outputNewPanels">
        <mvc:fragmentValue name="uid" id="uid">
            <mvc:fragmentValue name="providerId" id="providerId">
                <mvc:fragmentValue name="id" id="id">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%"><tr><td height="290" valign="top">
                    <table class="TableInstances" id="<%=id+"_displayable_"+uid%>">
                    <tr><td class="Header"></td>
                        <td class="Header" width="90%">Components</td>
                        <td class="Header">inst.</td>
                    </tr>
                    <tr>
                        <td class="Img_odd"><img src="<static:image relativePath="panels/NewPanel.png"/>" class="popupTreeImage"></td>
                        <td class="Inst_odd"><div class="popupDraggable"
                                                  onmouseover="checkDraggable('<%=id%>','<%=uid%>')"
                                                  id="<%=id+"_draggable_"+uid%>">
                            <span style="display:none"><%=providerId%></span>
                            <i18n:message key="ui.panel.new">!!!Nuevo panel</i18n:message> </div>
                        </td>
                        <td class="NumInst_odd">&nbsp;</td>
                    </tr>

                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputInstance">

        <mvc:fragmentValue name="uid" id="uid">
            <mvc:fragmentValue name="id" id="id">
                <mvc:fragmentValue name="position" id="position">
                    <mvc:fragmentValue name="title" id="title">
                        <mvc:fragmentValue name="instancesCount" id="instancesCount">
                            <tr>
                                <td class="<%="Img_"+position%>" id="<%=id+"_displayable_"+uid%>" title="<mvc:fragmentValue name="name"/>"><img src="<static:image relativePath="panels/Panel.png"/>" class="popupTreeImage"></td>
                                <td class="<%="Inst_"+position%>"><div class="popupDraggable" onmouseover="checkDraggable('<%=id%>','<%=uid%>')" id="<%=id+"_draggable_"+uid%>" title="<%=title%>">
                                    <span style="display:none"><mvc:fragmentValue name="instanceId"/></span>

                                    <mvc:fragmentValue name="name"/>
                                </div>
                                </td>
                                <td class="<%="NumInst_"+position%>"><%= instancesCount %></td>
                            </tr>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputEndDiv">
        <mvc:fragmentValue name="maxUid" id="maxUid">
            <mvc:fragmentValue name="id" id="id">
                </table>
                <input type="hidden" id="<%=id+"_maxUid"%>" value="<%=maxUid%>">
                <input type="hidden" id="<%=id+"_current_section"%>" value="1">

            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%----------------------------------------------------------------------------%>

    <mvc:fragment name="endWithoutPagination">
        </table></td></tr>

    </mvc:fragment>
    <mvc:fragment name="startPagination">
        </table></td></tr><tr><td align="center">
        <table width="70%" cellpadding="0" cellspacing="0" border="0" style="margin:0px;" >
        <tr>

    </mvc:fragment>
    <mvc:fragment name="endPagination">
        </tr>
        </table>
        </td></tr>



    </mvc:fragment>

    <mvc:fragment name="outputPreviousPageDisabled">
        <td align="left">
            <img src="<static:image relativePath="general/12x12/ico-page_first.png"/>" border="0" style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50);" title="<i18n:message key="ui.panel.popup.first">!!!Primera</i18n:message>"/>
            <img src="<static:image relativePath="general/12x12/ico-page_previous.png"/>" border="0" style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50);" title="<i18n:message key="ui.panel.popup.previous">!!!Siguiente</i18n:message>"/>
        </td>
    </mvc:fragment>
    <mvc:fragment name="outputPreviousPageEnabled">
        <mvc:fragmentValue name="page" id="p">
            <mvc:fragmentValue name="categoryId" id="categoryId">
                <mvc:fragmentValue name="subCategoryId" id="subcatId">
                    <mvc:fragmentValue name="providerId" id="providerId">
                        <td align="left">
                            <a href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup">
                <factory:param name="<%=PopupPanelsHandler.PANEL_INSTANCE_ID%>" value="<%=providerId%>"/>
                <factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=categoryId%>"/>
                <% if(subcatId !=null){ %>
                <factory:param name="<%=PopupPanelsHandler.PANEL_SUBCATEGORY_ID%>" value="<%=subcatId%>"/>
                <% }%>
                <factory:param name="<%=PopupPanelsHandler.PANEL_INSTANCE_PAGE_ID%>" value="-1"/>
                </factory:url>" ><img src="<static:image relativePath="general/12x12/ico-page_first.png"/>" border="0" title="<i18n:message key="ui.panel.popup.first">!!!Primera</i18n:message>"/>
                            </a>
                            <a href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup">
                <factory:param name="<%=PopupPanelsHandler.PANEL_INSTANCE_ID%>" value="<%=providerId%>"/>
                <factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=categoryId%>"/>
                <% if(subcatId !=null){ %>
                <factory:param name="<%=PopupPanelsHandler.PANEL_SUBCATEGORY_ID%>" value="<%=subcatId%>"/>
                <% }%>
                <factory:param name="<%=PopupPanelsHandler.PANEL_INSTANCE_PAGE_ID%>" value="<%= p%>"/>
                </factory:url>" > <img src="<static:image relativePath="general/12x12/ico-page_previous.png"/>" border="0" title="<i18n:message key="ui.panel.popup.previous">!!!Siguiente</i18n:message>"/>
                            </a>
                        </td>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputNextPageDisabled">
        <td align="right">
            <img src="<static:image relativePath="general/12x12/ico-page_following.png"/>" border="0" style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50);" title="<i18n:message key="ui.panel.popup.next">!!!Siguiente</i18n:message>"/>
            <img src="<static:image relativePath="general/12x12/ico-page_last.png"/>" border="0" style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50);" title="<i18n:message key="ui.panel.popup.last">!!!&Uacute;ltima</i18n:message>"/>
        </td>
    </mvc:fragment>

    <mvc:fragment name="outputNextPageEnabled">
        <mvc:fragmentValue name="page" id="p">
            <mvc:fragmentValue name="categoryId" id="categoryId">
                <mvc:fragmentValue name="subCategoryId" id="subcatId">
                    <mvc:fragmentValue name="providerId" id="providerId">
                        <td align="right">
                            <a href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup">
                <factory:param name="<%=PopupPanelsHandler.PANEL_INSTANCE_ID%>" value="<%=providerId%>"/>
                <factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=categoryId%>"/>
                <% if(subcatId !=null){ %>
                <factory:param name="<%=PopupPanelsHandler.PANEL_SUBCATEGORY_ID%>" value="<%=subcatId%>"/>
                <% }%>
                <factory:param name="<%=PopupPanelsHandler.PANEL_INSTANCE_PAGE_ID%>" value="<%= p %>"/>
                </factory:url>" ><img src="<static:image relativePath="general/12x12/ico-page_following.png"/>" border="0" title="<i18n:message key="ui.panel.popup.next">!!!Siguiente</i18n:message>"/></a>
                            <a href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="redrawPopup">
                <factory:param name="<%=PopupPanelsHandler.PANEL_INSTANCE_ID%>" value="<%=providerId%>"/>
                <factory:param name="<%=PopupPanelsHandler.GROUPID%>" value="<%=categoryId%>"/>
                <% if(subcatId !=null){ %>
                <factory:param name="<%=PopupPanelsHandler.PANEL_SUBCATEGORY_ID%>" value="<%=subcatId%>"/>
                <% }%>
                <factory:param name="<%=PopupPanelsHandler.PANEL_INSTANCE_PAGE_ID%>" value="1000"/>
                </factory:url>" ><img src="<static:image relativePath="general/12x12/ico-page_last.png"/>" border="0" title="<i18n:message key="ui.panel.popup.last">!!!&Uacute;ltima</i18n:message>"/>
                            </a>
                        </td>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

</mvc:formatter>