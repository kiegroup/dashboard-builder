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
<%@ page import="org.jboss.dashboard.ui.SessionManager" %>
<%@ page import="org.jboss.dashboard.ui.panel.export.ExportDriver" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.panel.export.messages"
             locale="<%=SessionManager.getCurrentLocale()%>"/>
<form style="margin:0px; padding:0px;" method="POST" id="<panel:encode name="exportForm"/>" action="<panel:link
    action="startExport"/>"><panel:hidden action="startExport"/>
    <mvc:formatter
            name="org.jboss.dashboard.ui.formatters.RenderWorkspacesFormatter">
    <mvc:fragment name="outputStart">
    <div style="text-align:left;"><i18n:message key="workspacesToExport">!!!Select workspaces to export</i18n:message>:</div>
    <div align="left" class="skn-table_border" style="width:300px; height:100px; overflow:auto; padding:5px;background-color:#FFFFFF;">
        </mvc:fragment> <mvc:fragment name="output"><input type="checkbox" checked
                                                           name="<%=ExportDriver.WORKSPACE_PREFFIX%><mvc:fragmentValue name="index"/>"
                                                           value="<mvc:fragmentValue name="workspaceId"/>">
        <mvc:fragmentValue name="workspaceName"/> <br></mvc:fragment>
        <mvc:fragment name="outputEnd"> </div>

    <p> </mvc:fragment>
        <mvc:fragment name="empty">
            <span class="skn-error"><i18n:message key="noWorkspacesToExport">!!!There are no workspaces to export</i18n:message></span>
        </mvc:fragment> <mvc:fragment name="error">
            <span class="skn-error"><i18n:message key="errorShowingWorkspaces">!!!There has been an error displaying the workspaces to export</i18n:message></span>
        </mvc:fragment></mvc:formatter> <br> <mvc:formatter
            name="org.jboss.dashboard.ui.formatters.RenderResourcesFormatter"><mvc:formatterParam
            name="includeBase"
            value="false"/>
        <mvc:fragment name="outputStart">
            <div style="text-align:left;"><i18n:message key="resourcesToExport">!!!Select resources to export</i18n:message>:</div>
            <div align="left" class="skn-table_border" style="width:300; overflow:auto; border:1 solid #808080;padding:5px; background-color:#FFFFFF;">
        </mvc:fragment> <mvc:fragment name="output">
        <input type="checkbox" checked name="<%=ExportDriver.RESOURCE_PREFFIX%><mvc:fragmentValue name="index"/>"
               value="<mvc:fragmentValue name="class"/> <mvc:fragmentValue name="id"/>">
        <mvc:fragmentValue name="description"/>&nbsp;<b>(<mvc:fragmentValue name="type"/>)</b> <br></mvc:fragment>
        <mvc:fragment name="outputEnd"> </div><br> </mvc:fragment> <mvc:fragment name="empty"> <span class="skn-error">
        <i18n:message key="noResourcesToExport">!!!There are no resources to export</i18n:message><br></mvc:fragment>
        </span>
        <mvc:fragment name="error"><span class="skn-error"><i18n:message key="errorShowingResources">!!!There has been an error displaying the resources to export</i18n:message></span></mvc:fragment></mvc:formatter></p>
    <input style="bottom:100%; vertical-align:bottom; margin-top:10px;" type="submit" class="skn-button" value="<i18n:message key="export">!!!Export</i18n:message>">
</form>
<script type="text/javascript" defer>
    setAjax('<panel:encode name="exportForm"/>');
</script>
