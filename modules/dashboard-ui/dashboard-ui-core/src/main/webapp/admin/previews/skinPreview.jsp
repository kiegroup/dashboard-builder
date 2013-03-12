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
                 org.jboss.dashboard.ui.resources.GraphicElementPreview,
                 org.jboss.dashboard.ui.resources.Skin,
                 java.util.Arrays,
                 java.util.Set" %>
<%@ page import="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%
    String graphicElement = (String) request.getAttribute("graphicElement");
    GraphicElementPreview preview = (GraphicElementPreview) request.getSession().getAttribute(ResourcesPropertiesHandler.PREVIEW_ATTRIBUTE);
%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>
<tr>
    <td nowrap="nowrap" align="left" valign="top"><i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.preview"%>'>!!!Preview</i18n:message>
    </td>
    <td>
 <!--<TABLE border=1 cellPadding=0 cellSpacing=0 width="80%">
            <TBODY>
                <TR style=" Font-Family: Tahoma, Verdana; Font-Size: 10 px; Font-Weight: bold;background-image: url(<resource:link category="skinPreview" categoryId="default" resourceId="HEADER_BG"/>)">
                    <TD align=left><resource:image category="skinPreview" categoryId="default"
                                                   resourceId="HEADER_LEFT"></resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="BULLET"
                                                   resourceId="BULLET"></resource:image></TD>
                    <TD width="30%" nowrap align=left>&nbsp;<panel:localize data="<%=preview.getDescription()%>"/>
                        &nbsp;</TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="DOWN"
                                                   resourceId="DOWN">v</resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="UP" resourceId="UP">
                        ^</resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="LEFT"
                                                   resourceId="LEFT">&lt;</resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="RIGHT"
                                                   resourceId="RIGHT">&gt;</resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="PROPERTIES"
                                                   resourceId="PROPERTIES">P</resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="RESOURCES_MODE"
                                                   resourceId="RESOURCES_MODE">R</resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="EDIT_MODE"
                                                   resourceId="EDIT_MODE">L</resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="SHOW"
                                                   resourceId="SHOW">_</resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="RESTORE"
                                                   resourceId="RESTORE">oO</resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="MINIMIZE"
                                                   resourceId="MINIMIZE">_</resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="MAXIMIZE"
                                                   resourceId="MAXIMIZE">O</resource:image></TD>
                    <TD align=left><resource:image category="skinPreview" categoryId="default" title="CLOSE"
                                                   resourceId="CLOSE">X</resource:image></TD>
                    <TD align=right><resource:image category="skinPreview" categoryId="default"
                                                    resourceId="HEADER_RIGHT"></resource:image></TD>
                </TR>
                <tr>
                    <td colspan="40">
                        <div nowrap
                             style="width:100%; overflow-x: auto; overflow-y: visible; border:solid; border-width:1px; border-color:#666666;">
                            <%
                                Set resourcesSet = preview.getResources();
                                String[] extraResources = (String[]) resourcesSet.toArray(new String[resourcesSet.size()]);
                                Arrays.sort(extraResources);
                                for (int i = 0; i < extraResources.length; i++) {
                                    String resource = extraResources[i];
                                    if (Skin.DEFAULT_PROPERTIES.containsKey("resource." + resource)) {
                                        //System.out.println("Ignoring resource "+resource);
                                        continue;
                                    } else {
                                        //System.out.println("Processing resource "+resource);
                                    }
                            %><resource:image category="skinPreview" title="<%=resource%>" categoryId="default"
                                              resourceId="<%=resource%>"></resource:image><%
                            }
                        %>
                        </div>
                    </td>
                </tr>

            </TBODY>
        </TABLE>-->

<%
    Skin skin = (Skin) request.getSession().getAttribute("previewElement");
//    request.getSession().removeAttribute("previewElement");
%>

<TABLE border="0" cellPadding="0" cellSpacing="0" width="100%" align="center">
    <TR>
        <TD align=left nowrap valign="bottom"><resource:image workspaceId="<%=preview.getWorkspaceId()%>"
                                                              sectionId="<%=preview.getSectionId()%>"
                                                              panelId="<%=preview.getPanelId()%>" category="skinPreview"
                                                              categoryId="<%=preview.getId()%>" title="HEADER_LEFT"
                                                              border="0" align="absmiddle" resourceId="HEADER_LEFT"
                                                              useDefaults="false"></resource:image></TD>
        <TD width="50%" nowrap align=left  background="<resource:link workspaceId="<%=preview.getWorkspaceId()%>" sectionId="<%=preview.getSectionId()%>" panelId="<%=preview.getPanelId()%>" category="skinPreview" categoryId="<%=preview.getId()%>" resourceId="HEADER_BG" useDefaults="false"/>"><b><panel:localize data="<%=preview.getDescription()%>"/></b>&nbsp;</TD>
        <%
            String[] resources = {"DOWN", "UP", "LEFT", "RIGHT", "PROPERTIES", "RESTORE", "MINIMIZE", "MAXIMIZE", "CLOSE"};
            String[] resourceAlts = {"v", "^", "<", ">", "P", "R", "E", "_", "oO", "_", "O", "X", ""};
        %>
        <TD nowrap valign="bottom"  background="<resource:link workspaceId="<%=preview.getWorkspaceId()%>" sectionId="<%=preview.getSectionId()%>" panelId="<%=preview.getPanelId()%>" category="skinPreview" categoryId="<%=preview.getId()%>" resourceId="HEADER_BG" useDefaults="false"/>"><%for (int i = 0; i < resources.length; i++) {%><a href="#" align="absmiddle" onclick="return false">
            <resource:image workspaceId="<%=preview.getWorkspaceId()%>" sectionId="<%=preview.getSectionId()%>"
                            panelId="<%=preview.getPanelId()%>" border="0" category="skinPreview"
                            categoryId="<%=preview.getId()%>" title="<%=resources[i]%>" align="absmiddle" useDefaults="false"
                            resourceId="<%=resources[i]%>"><%=resourceAlts[i]%></resource:image></a><%}%>...</TD>
        <td><resource:image workspaceId="<%=preview.getWorkspaceId()%>"
                                                              sectionId="<%=preview.getSectionId()%>"
                                                              panelId="<%=preview.getPanelId()%>" category="skinPreview"
                                                              categoryId="<%=preview.getId()%>" title="HEADER_RIGHT"
                                                              border="0" align="absmiddle" resourceId="HEADER_RIGHT"
                                                              useDefaults="false"></resource:image></td>
    </TR>
</TABLE>




    </td>
</tr>
<%--tr>
    <td nowrap><i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.defaultStyleSheet"%>'>!!!Default style
        sheet</i18n:message></td>
    <td><a target="_blank" href="<resource:link category="skinPreview" categoryId="default" resourceId="DEFAULT_CSS"/>">
        <i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.preview"%>'>!!!Preview</i18n:message></a></td>
</tr>
<tr>
    <td nowrap><i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.panelStyleSheet"%>'>!!!Panels style
        sheet</i18n:message></td>
    <td><a target="_blank" href="<resource:link category="skinPreview" categoryId="default" resourceId="PANEL_CSS"/>">
        <i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.preview"%>'>!!!Preview</i18n:message></a></td>
</tr--%>