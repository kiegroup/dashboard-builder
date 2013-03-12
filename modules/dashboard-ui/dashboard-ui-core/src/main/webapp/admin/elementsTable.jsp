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
                 org.jboss.dashboard.ui.resources.GraphicElement" %>
<%@ page import="org.jboss.dashboard.workspace.GraphicElementManager" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>

<%
    try {
        GraphicElement[] elements = (GraphicElement[]) request.getAttribute("elements");
        boolean workspaceChangeable = "true".equals(request.getAttribute("workspaceChangeable"));
        boolean sectionChangeable = "true".equals(request.getAttribute("sectionChangeable"));
        boolean panelChangeable = "true".equals(request.getAttribute("panelChangeable"));

        if (elements != null && elements.length > 0) {
            String graphicElement = (String) request.getAttribute("graphicElement");
            String graphicElementClassName = (String) request.getAttribute("graphicElementClassName");
            GraphicElementManager manager = (GraphicElementManager) request.getAttribute("manager");
            String previewJSP = (String) request.getAttribute("previewElementJSP");
            boolean showPreview = previewJSP != null && !"".equals(previewJSP);
            String skin = (String) request.getAttribute("skin");
%>

<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>
<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.global"%>' id="global">!!!Global</i18n:message>
<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.confirmDelete"+graphicElementClassName%>' id="msgDelete">!!!Sure?</i18n:message>

<table width="95%" align="center" cellpadding="2" cellspacing="1" class="skn-table_border" >
    <tr class="skn-table_header">
        <script defer type="text/javascript" language="javascript">
            function checkDelete(id) {
                doit = confirm('<%=msgDelete%>');
                if (doit) {
                    eval('document.delete' + id + '.submit()');
                }
            }
        </script>

        <td width="20px" colspan="3">
            <i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.actions"%>'>!!!Actions</i18n:message>
        </td>
        <td >
            <i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.identifier"%>'>!!!Id</i18n:message>
        </td>
        <td >
            <i18n:message key='<%="ui.admin.workarea."+graphicElement+"s."+graphicElement%>'>!!!Element</i18n:message>
        </td>
        <%if (showPreview && false) {%>
        <td >
            <i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.preview"%>'>!!!Preview</i18n:message>
        </td>
        <%}%>
    </tr>

    <%
        for (int i = 0; i < elements.length; i++) {
            GraphicElement element = elements[i];
    %>
    <tr class="<%=i%2==0?"skn-odd_row":"skn-even_row"%>">
        <td nowrap="nowrap" align="center" valign="top" width="10px">
            <% if (showPreview) { %>
            <a href="#"
               title="<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.preview"%>'>!!!Preview</i18n:message>"
               onclick="window.open('<factory:url bean="org.jboss.dashboard.ui.components.AdminHandler" action="previewGraphicElement"/>&elementId=<%=element.getDbid()%>&elementCategoryName=<%=element.getCategoryName()%>&previewPage=<%=previewJSP%>','graphicElementPreviewPopupWindow','width=400,height=260,dependent=yes');return false">
                <img src="<static:image relativePath="general/16x16/preview.png"/>" border="0" />
            </a>
            <% } %>
        </td>
        <td nowrap="nowrap" align="center" valign="top" width="10px">
            <%--<form name="delete<%=element.getDbid()%>" method="POST"
          action="<mvc:link action="DeleteElement" handler="admin"/>"
          onsubmit="return confirm('<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.confirmDelete"+graphicElementClassName%>'>!!!Sure?</i18n:message>');">--%>
            <form name="delete<%=element.getDbid()%>" method="POST"
                  action="<factory:url bean="org.jboss.dashboard.ui.components.AdminHandler" action="DeleteElement"/>"
                  onsubmit="if (confirm('<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.confirmDelete"+graphicElementClassName%>'>!!!Sure?</i18n:message>')) this.submit(); else return false;">
                <input type="hidden" name="<%=graphicElement%>Id" value="<%=element.getDbid()%>">
                <input type="hidden" name="graphicElement" value="<%=graphicElement%>">
            </form>
            <a href="<factory:url bean="org.jboss.dashboard.ui.components.ResourcesHandler"
                  action="download"><factory:param name="dbid" value="<%=element.getDbid()%>"/></factory:url>"
               title="<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.download"%>'>!!!download</i18n:message>">
                <img src="<static:image relativePath="general/16x16/ico-actions_go-jump.png"/>" border="0" />
            </a>
        </td>
        <td nowrap="nowrap" align="center" valign="top" width="10px">
            <% if ((sectionChangeable && element.getWorkspaceId() != null && element.getPanelId() == null) ||
                    (panelChangeable && element.getWorkspaceId() != null && element.getSectionId() != null && element.getPanelId() == null) ||
                    (panelChangeable && element.getWorkspaceId() != null && element.getSectionId() == null && element.getPanelId() != null) ||
                    (workspaceChangeable) ||
                    (element.getWorkspaceId() != null && element.getPanelId() != null) ||
                    (element.getWorkspaceId() != null && element.getSectionId() != null)
                    )
                if (!manager.isBaseElement(element)) {%>
            <%--<a href="#" onclick="checkDelete(<%=element.getDbid()%>);return false;"
               title="<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.delete"%>'>!!!Delete</i18n:message>">
                <resource:image category="resourceGallery" categoryId="icons" resourceId="SMALL_x-directory-trash" border="0">
                    X</resource:image></a>--%>
            <a href="#" onclick="eval('document.delete'+<%=element.getDbid()%> +'.onsubmit()');return false;"
               title="<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.delete"%>'>!!!Delete</i18n:message>">
                <img src="<static:image relativePath="general/16x16/ico-directory-trash.png"/>" border="0" ></a>
            <%}%>
        </td>
        <td nowrap="nowrap" align="left" valign="top">
            <div style="max-width:300px; overflow:hidden;" title="<%=element.getId()%>"><%=element.getId()%></div>
        </td>
        <td nowrap="nowrap" align="left" valign="top">
            <div style="max-width:400px; overflow:hidden;"
                 title="<panel:localize data="<%=element.getDescription()%>" />"><panel:localize
                    data="<%=element.getDescription()%>"/></div>
        </td>
        <%if (showPreview && false) {
            request.setAttribute("previewElement", element);

        %>
        <td align="center">
            <jsp:include page="<%=previewJSP%>" flush="true"/>
        </td>
        <%
                request.removeAttribute("previewElement");
            }
        %>

    </tr>
    <%
        }
    %>
</table>
<br><br>
<%}
}
catch (Exception e) {
    e.printStackTrace();
}%>