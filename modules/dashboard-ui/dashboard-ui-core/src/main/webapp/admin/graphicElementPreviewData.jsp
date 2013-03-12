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
<%@ page import="org.jboss.dashboard.ui.SessionManager,
                 org.jboss.dashboard.workspace.GraphicElementManager,
                 org.jboss.dashboard.workspace.WorkspaceImpl,
                 org.jboss.dashboard.ui.resources.GraphicElementPreview,
                 java.lang.reflect.Method" %>
<%@ page import="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler"%>
<%@ page import="org.jboss.dashboard.factory.Factory"%>
<%@ page import="org.jboss.dashboard.ui.UIServices" %>
<%@ page import="org.jboss.dashboard.workspace.WorkspaceImpl" %>
<%@ page import="org.jboss.dashboard.workspace.GraphicElementManager" %>

<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>
<%
    try {
        ResourcesPropertiesHandler handler = (ResourcesPropertiesHandler) Factory.lookup(ResourcesPropertiesHandler.class.getName());
        String resultMessage = null;
        String graphicElement = (String) request.getAttribute("graphicElement");
        String graphicElementClassName = graphicElement.substring(0, 1).toUpperCase() + graphicElement.substring(1);
        Class graphicElementClass = Class.forName("org.jboss.dashboard.ui.resources." + graphicElementClassName);
        Method managerGetter = graphicElementClass.getMethod("getManager", new Class[]{});
        GraphicElementManager manager = (GraphicElementManager) managerGetter.invoke(null, new Object[]{});
        GraphicElementPreview preview = (GraphicElementPreview) request.getSession().getAttribute(ResourcesPropertiesHandler.PREVIEW_ATTRIBUTE);
        request.setAttribute("previewElement",preview.toElement());
        switch (preview.getStatus()) {
            case GraphicElementPreview.STATUS_MISSING_DESCRIPTOR:
                resultMessage = "ui.admin.workarea." + graphicElement + "s.missingDescriptor";
                break;
            case GraphicElementPreview.STATUS_DESCRIPTOR_CORRUPT:
                resultMessage = "ui.admin.workarea." + graphicElement + "s.corruptDescriptor";
                break;
            case GraphicElementPreview.STATUS_ZIP_CORRUPT:
                resultMessage = "ui.admin.workarea." + graphicElement + "s.corruptZip";
                break;
            case GraphicElementPreview.STATUS_JSP_INSECURE:
                resultMessage = "ui.admin.workarea." + graphicElement + "s.insecure";
                break;
        }
        if (handler.isZipHasError()) resultMessage = "ui.admin.workarea." + graphicElement + "s.errorImport";
        if (resultMessage != null) {
%>
<span class="skn-error">
                <br>
                <p align="center"> <i18n:message key="<%=resultMessage%>">!!!Error</i18n:message></p>
            </span>
<%
} else {
%>
<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.global"%>' id="global">!!!Global</i18n:message>
<br>
<table width="470" align="left" cellpadding="4" cellspacing="1" class="skn-table_border" border="0">
    <tr class="skn-table_header">
        <td nowrap="nowrap" colspan="2"><i18n:message
                key='<%="ui.admin.workarea."+graphicElement+"s."+graphicElement+"Details"%>'>!!!Element
            details</i18n:message></td>
    </tr>
    <tr class="skn-odd_row">
        <td width="100" nowrap="nowrap" align="left"><i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.size"%>'>
            !!!Size</i18n:message></td>
        <td nowrap="nowrap" align="left"><%=preview.getZipSize()%> bytes</td>
    </tr>
    <tr class="skn-even_row">
        <td nowrap="nowrap" align="left"><i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.identifier"%>'>
            !!!Id</i18n:message></td>
        <td nowrap="nowrap" align="left"><%=preview.getId()%></td>
    </tr>
    <tr class="skn-odd_row">
        <td nowrap="nowrap" align="left"><i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.scope"%>'>
            !!!Scope</i18n:message></td>
        <td nowrap="nowrap" align="left">
            <% if (preview.getWorkspaceId() == null) {%>
            <%=global%>
            <% } else {%>
            <i18n:message key='<%="ui.workspace"%>'>!!!Workspace</i18n:message>: <panel:localize
                data="<%=UIServices.lookup().getWorkspacesManager().getWorkspace(preview.getWorkspaceId()).getName()%>"/>
            <% }
                if (preview.getSectionId() != null) { %>
            - <i18n:message key='<%="ui.sections.section"%>'>!!!Section</i18n:message>: <panel:localize
                data="<%=((WorkspaceImpl)UIServices.lookup().getWorkspacesManager().getWorkspace(preview.getWorkspaceId())).getSection(preview.getSectionId()).getTitle()%>"/>
            <% } %>
            <% if (preview.getPanelId() != null) {
                if (manager.getElementScopeDescriptor().isAllowedPanel()) {
            %>
            - Panel: <panel:localize
                data="<%=((WorkspaceImpl)UIServices.lookup().getWorkspacesManager().getWorkspace(preview.getWorkspaceId())).getSection(preview.getSectionId()).getPanel(preview.getPanelId().toString()).getTitle()%>"/>
            <%
            } else if (manager.getElementScopeDescriptor().isAllowedInstance()) {
            %>
            - Panel: <panel:localize
                data="<%=((WorkspaceImpl)UIServices.lookup().getWorkspacesManager().getWorkspace(preview.getWorkspaceId())).getPanelInstance(preview.getPanelId().toString()).getTitle()%>"/>
            <%
                }
            %>
            <%  } %>
        </td>
    </tr>
    <tr class="skn-even_row">
        <td nowrap="nowrap" align="left"><i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.description"%>'>
            !!!Description</i18n:message></td>
        <td nowrap="nowrap" align="left"><panel:localize data="<%=preview.getDescription()%>"/></td>
    </tr>
    <tr><td colspan="2" valign="top">
    <%
        if ("skin".equals(graphicElement)) {
    %>
    <jsp:include page="previews/skinPreview.jsp" flush="true"/>
    <%
    } else if ("resourceGallery".equals(graphicElement)) { //Extra information
    %>
    <jsp:include page="previews/resourceGalleryPreview.jsp" flush="true"/>
    <%
        } else if ("layout".equals(graphicElement)) { //Extra information
    %>
        <jsp:include page="previews/layoutPreviewConfirm.jsp" flush="true"/>
        <%
            }
        %>


</td></tr>
</table>
<%}

}
catch (Exception e) {
    e.printStackTrace();
    //ResourcesPropertiesHandler handler = (ResourcesPropertiesHandler) Factory.lookup(ResourcesPropertiesHandler.class.getName());
    //handler.setErrorOnZipFile();
}%>
