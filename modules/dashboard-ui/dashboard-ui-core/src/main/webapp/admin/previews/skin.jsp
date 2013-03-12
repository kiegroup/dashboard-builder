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
<%@ page import="org.jboss.dashboard.ui.resources.Skin" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%
    try{
    Skin skin = (Skin) request.getAttribute("previewElement");
    request.getSession().setAttribute("previewElement", skin);
%>
<link rel="stylesheet" href='<resource:link workspaceId="<%=skin.getWorkspaceId()%>" sectionId="<%=skin.getSectionId()%>" panelId="<%=skin.getPanelId()%>" category="skin" categoryId="<%=skin.getId()%>" resourceId="CSS" useDefaults="false"/>' type="text/css">

<TABLE border="0" cellPadding="0" cellSpacing="0" width="100%" align="center" height="100%">
    <TR>
        <TD align=left nowrap valign="bottom"><resource:image workspaceId="<%=skin.getWorkspaceId()%>"
                                                              sectionId="<%=skin.getSectionId()%>"
                                                              panelId="<%=skin.getPanelId()%>" category="skin"
                                                              categoryId="<%=skin.getId()%>" title="HEADER_LEFT"
                                                              border="0" align="absmiddle" resourceId="HEADER_LEFT"
                                                              useDefaults="false"></resource:image></TD>
        <TD width="50%" nowrap align=left
            background="<resource:link workspaceId="<%=skin.getWorkspaceId()%>" sectionId="<%=skin.getSectionId()%>" panelId="<%=skin.getPanelId()%>" category="skin" categoryId="<%=skin.getId()%>" resourceId="HEADER_BG" useDefaults="false"/>">
            <b><panel:localize data="<%=skin.getDescription()%>"/></b>&nbsp;</TD>
        <%
            String[] resources = {"DOWN", "UP", "LEFT", "RIGHT", "PROPERTIES", "RESTORE", "MINIMIZE", "MAXIMIZE", "CLOSE"};
            String[] resourceAlts = {"v", "^", "<", ">", "P", "R", "E", "_", "oO", "_", "O", "X", ""};
        %>
        <TD nowrap valign="bottom"
            background="<resource:link workspaceId="<%=skin.getWorkspaceId()%>" sectionId="<%=skin.getSectionId()%>" panelId="<%=skin.getPanelId()%>" category="skin" categoryId="<%=skin.getId()%>" resourceId="HEADER_BG" useDefaults="false"/>"><%for (int i = 0; i < resources.length; i++) {%><a
                href="#" align="absmiddle" onclick="return false">
            <resource:image workspaceId="<%=skin.getWorkspaceId()%>" sectionId="<%=skin.getSectionId()%>"
                            panelId="<%=skin.getPanelId()%>" border="0" category="skin"
                            categoryId="<%=skin.getId()%>" title="<%=resources[i]%>" align="absmiddle"
                            useDefaults="false"
                            resourceId="<%=resources[i]%>"><%=resourceAlts[i]%></resource:image></a><%}%>...</TD>
        <td><resource:image workspaceId="<%=skin.getWorkspaceId()%>"
                            sectionId="<%=skin.getSectionId()%>"
                            panelId="<%=skin.getPanelId()%>" category="skin"
                            categoryId="<%=skin.getId()%>" title="HEADER_RIGHT"
                            border="0" align="absmiddle" resourceId="HEADER_RIGHT"
                            useDefaults="false"></resource:image></td>
    </TR>
    <tr height="100%">
        <td colspan="15" height="100%" valign="top">
            <div style="border:solid; border-width:1px; height:95%; padding:5px">
                <table border="0" cellpadding="5" cellspacing="0" width="100%"><tr><td>
                <%-- TODO add here texts with all styles available in skin --%>
                Normal<br>
                <%
                    String[] textClasses = new String[]
                            {"skn-title1","skn-title2","skn-title3","skn-important","skn-disabled","skn-error","skn-alt","skn-background","skn-background_alt"};
                    for(int i = 0; i< textClasses.length; i++){
                        String textClass = textClasses[i];
                        %>
                        <span class="<%=textClass%>"><%=textClass%></span><br>
                <%
                    }

                %>
                <a href="#">Link</a><br>

    </td>
    <td align="right">
                <table class="skn-table_border" border="0" cellpadding="4" cellspacing="1" width="100%">
                    <tr class="skn-table_header"><td>skn-table_header</td></tr>
                    <tr class="skn-odd_row" onmouseover="className='skn-row_on'" onmouseout="className='skn-odd_row'"><td>skn-odd_row</td></tr>
                    <tr class="skn-even_row" onmouseover="className='skn-row_on'" onmouseout="className='skn-even_row'"><td>skn-even_row</td></tr>
                    <tr class="skn-odd_row_alt" onmouseover="className='skn-row_on'" onmouseout="className='skn-odd_row_alt'"><td>skn-odd_row_alt</td></tr>
                    <tr class="skn-even_row_alt" onmouseover="className='skn-row_on'" onmouseout="className='skn-even_row_alt'"><td>skn-even_row_alt</td></tr>
                    <tr class="skn-row_on"><td>skn-row_on</td></tr>
                </table>
        </td></tr><tr><td colspan="2" align="center"><input class="skn-input" value="skn-input">&nbsp;
                    <button class="skn-button">skn-button</button>&nbsp;
                <button class="skn-button_alt">skn-button_alt</button>
             </td></tr></table>
            </div>
        </td>
    </tr>
</TABLE>
<%
    } catch(Exception e){
        e.printStackTrace();
    }
%>
