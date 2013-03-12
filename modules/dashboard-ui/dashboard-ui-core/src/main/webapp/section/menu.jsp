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
<%@ page import="org.jboss.dashboard.security.SectionPermission" %>
<%@ include file="../common/global.jsp" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<br>
<table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td width="1px" align="left"><resource:image category="skin" resourceId="HEADER_LEFT"/></td>
        <td width="1px" nowrap align="left"
            style="Background-Image: url('<resource:link category="skin" resourceId="HEADER_BG"/>')">
            <a
                    href="<panel:mvclink handler='section' action='start-edit'/>"><i18n:message
                    key="ui.properties"/></a>
        </td>
        <td width="1px" align="right"><resource:image category="skin" resourceId="HEADER_RIGHT"/></td>
        <%
            SectionPermission editPermissionsPerm = SectionPermission.newInstance(currentSection, SectionPermission.ACTION_EDIT_PERMISSIONS);
        %>
        <security:checkpermission permission="<%= editPermissionsPerm %>">
            <td width="1px" align="left"><resource:image category="skin" resourceId="HEADER_LEFT"/></td>
            <td align="left" style="Background-Image: url('<resource:link category="skin" resourceId="HEADER_BG"/>')">
                <a
                        href="<panel:mvclink handler='section' action='start-manage-permissions'/>"><i18n:message
                        key="ui.permissions"/></a>
            </td>
            <td width="1px" align="right"><resource:image category="skin" resourceId="HEADER_RIGHT"/></td>
        </security:checkpermission>
        <td width="1px" align="left"><resource:image category="skin" resourceId="HEADER_LEFT"/></td>
        <td align="left" style="Background-Image: url('<resource:link category="skin" resourceId="HEADER_BG"/>')">
            <a
                    href="<panel:mvclink handler='section' action='start-manage-resources'/>"><i18n:message
                    key="ui.menu.resources"/></a>
        </td>
        <td width="1px" align="right"><resource:image category="skin" resourceId="HEADER_RIGHT"/></td>
    </tr>
</table>
