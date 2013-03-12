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
<%@ page import="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler" %>
<%@ page import="org.jboss.dashboard.ui.SessionManager" %>
<%@ page import="org.jboss.dashboard.ui.utils.forms.FormStatus" %>
<%@ page import="org.jboss.dashboard.workspace.GraphicElementManager" %>
<%@ page import="org.jboss.dashboard.ui.resources.GraphicElement" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="factory.tld" prefix="factory" %>

<mvc:formatter name="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesFormatter">
<mvc:fragment name="outputResources">
    <mvc:fragmentValue name="graphicElement" id="graphicElement">
        <mvc:fragmentValue name="graphicElementClassName" id="graphicElementClassName">
            <mvc:fragmentValue name="manager" id="manager">
                <mvc:fragmentValue name="elements" id="elements">
                    <script defer type="text/javascript" language="Javascript">
                        function showAlertMessage(text) {
                            setTimeout("alert('" + text + "')", 10);
                        }
                    </script>
                    <%
                        try {
                            ResourcesPropertiesHandler handler = (ResourcesPropertiesHandler) Factory.lookup("org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler");
                            request.getSession().setAttribute("gElm", (String) graphicElement);
                            request.getSession().setAttribute("graphicElement", (String) graphicElement);
                            request.setAttribute("graphicElement", (String) graphicElement);
                            request.setAttribute("graphicElementClassName", (String) graphicElementClassName);
                            request.setAttribute("manager", (GraphicElementManager) manager);


                            FormStatus status = SessionManager.getCurrentFormStatus();
                            String[] messages = status.getMessages();
                            if (messages != null && messages.length > 0) {
                                for (int i = 0; i < messages.length; i++) {
                    %>
                    <script defer type="text/javascript" language="Javascript">
                        showAlertMessage('<i18n:message key="<%=messages[i]%>">!!!<%=messages[i]%></i18n:message>');
                    </script>
                    <%
                            }
                        }
                        status.clearMessages();
                        if (((GraphicElement[]) elements != null) && (((GraphicElement[]) elements).length > 0)) {
                            request.setAttribute("elements", (GraphicElement[]) elements);
                            if (handler.getWorkspaceId() == null) request.setAttribute("workspaceChangeable", "true");
                            else
                                request.setAttribute("workspaceChangeable", "false");
                            if ((handler.getSectionId() == null) && (handler.getPanelId() == null))
                                request.setAttribute("sectionChangeable", "true");
                            else
                                request.setAttribute("sectionChangeable", "false");
                            if (!"envelope".equals(graphicElement) && !"layout".equals(graphicElement)) {
                                if (handler.getPanelId() == null)
                                    request.setAttribute("panelChangeable", "true");
                                else
                                    request.setAttribute("panelChangeable", "false");
                            }
                            request.setAttribute("skin", request.getSession().getAttribute("adminSkinToUse"));
                            if ("skin".equals(graphicElement))
                                request.setAttribute("previewElementJSP", "previews/skin.jsp");
                            if ("resourceGallery".equals(graphicElement))
                                request.setAttribute("previewElementJSP", "previews/resourceGallery.jsp");
                            if ("layout".equals(graphicElement))
                                request.setAttribute("previewElementJSP", "previews/layoutPreview.jsp");
                    %>

                    <jsp:include page="../../../admin/elementsTable.jsp"/>

                    <%  } else { %>
                    <p class="skn-error"><i18n:message key="ui.admin.configuration.tree.noDataFound">!!No hay
                        datos</i18n:message></p>

                    <%  }
                    } catch (Exception e) {
                        e.getMessage();
                    }%>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputUploadResourceStart">
    <mvc:fragmentValue name="type" id="type">
        <br/>
        <form style="margin:0px;" action="<factory:formUrl friendly="false"/>" method="POST" enctype="multipart/form-data">
        <factory:handler bean="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler"
                         action="startPreview"/>
        <table width="95%" border="0" align="center" cellpadding="2" cellspacing="1" class="skn-table_border">
        <tr>
            <td colspan="3" class="skn-table_header">
                <i18n:message key='<%="ui.admin.workarea."+type%>'>!!!Global</i18n:message>
            </td>
        </tr>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputUploadResourceStartRow">
    <tr>
</mvc:fragment>
<mvc:fragment name="outputUploadResourceEndRow">
    </tr>
</mvc:fragment>
<mvc:fragment name="outputUploadResourceScopeStart">
    <mvc:fragmentValue name="graphicElement" id="graphicElement">
        <td align="center">
        <i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.global"%>' id="global">
            !!!Global</i18n:message>
        <i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.scope"%>'>!!!Scope</i18n:message>:

        &nbsp;<select name="<factory:bean
            bean="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler"
            property="socpeWorkspaceId"/>" class="skn-input">
        <option value="" selected><%=global%></option>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputUploadResourceScope">
    <mvc:fragmentValue name="workspaceId" id="workspaceId">
        <mvc:fragmentValue name="workspaceTitle" id="workspaceTitle">
            <option value="<%=workspaceId%>"><%=workspaceTitle%></option>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputUploadResourceScopeEnd">
    </select>
    </td>
</mvc:fragment>

<mvc:fragment name="outputUploadResourceIdentifier">
    <mvc:fragmentValue name="graphicElement" id="graphicElement">
        <mvc:fragmentValue name="error" id="error">
            <td align="center">
                <span class="<%=error%>">*<i18n:message
                        key='<%="ui.admin.workarea."+graphicElement+"s.identifier"%>'>
                    !!!Id</i18n:message>:</span>&nbsp;
                <input name="<factory:bean bean="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler" property="resourceId"/>"
                       value="<factory:property bean="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler" property="resourceId"/>"
                       maxlength="40" type="text" class="skn-input"/>

            </td>

        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputUploadResourceFile">
    <mvc:fragmentValue name="graphicElement" id="graphicElement">
        <mvc:fragmentValue name="error" id="error">
            <td align="center">
                <span class="<%=error%>">*<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.zipFile"%>'>
                    !!!File</i18n:message>:</span>&nbsp;
                <input type="file" class="skn-input"
                       name="<factory:bean bean="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler" property="file"/>"
                       value="<factory:property bean="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler" property="file"/>"/>
            </td>

        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="outputUploadResourceEnd">
    <mvc:fragmentValue name="graphicElement" id="graphicElement">
        <i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.add"%>' id="addMessage">!!!Add</i18n:message>
        <tr>
            <td colspan="3" align="center">
                <input type="submit" value="<%=addMessage%>" class="skn-button"/>
<%--
                &nbsp;&nbsp;
                <input name="Submit23" type="reset" class="skn-button_alt"
                       value="<i18n:message key="ui.admin.workarea.cancel">!!Cancelar</i18n:message>">
--%>
            </td>
        </tr>
        </table>
        <br><br>
        </form>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputPreview">
    <mvc:fragmentValue name="graphicElement" id="graphicElement">
        <mvc:fragmentValue name="graphicElementClassName" id="graphicElementClassName">
            <%
                request.setAttribute("graphicElement", (String) graphicElement);
                request.setAttribute("graphicElementClassName", (String) graphicElementClassName);
            %>
            <table><tr><td>
            <jsp:include page="../../../admin/graphicElementPreviewData.jsp" flush="true"/>
            </td></tr><tr><td>
            <form style="margin:0px;" action="<factory:formUrl friendly="false"/>" method="POST" enctype="multipart/form-data">
            <factory:handler bean="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler"
                             action="confirmNewElement"/>
            <input type="hidden" name="accion"/>

            <table border="0" cellpadding="4" cellspacing="1" width="100%" align="left">
            <tr align="center">

            <td nowrap align="right" width="50%">
                <input type="submit"
                       value="<i18n:message key="ui.admin.workarea.confirm">!!!Confirm</i18n:message>"
                       class="skn-button"
                       name="<factory:bean bean="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler" property="actionCreate"/>"/>
                &nbsp;</td>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputPreviewConfirm">
    <td nowrap align="left" width="50%">&nbsp;
        <input type="submit" value="<i18n:message key="ui.admin.workarea.cancel">!!!Cancel</i18n:message>"
               class="skn-button_alt"
               name="<factory:bean bean="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler" property="actionCancel"/>"/>
    </td>
</mvc:fragment>
<mvc:fragment name="outputPreviewEnd">
    </tr>
    </table>

    </form>
    </td></tr></table>
    <br><br>
</mvc:fragment>

</mvc:formatter>