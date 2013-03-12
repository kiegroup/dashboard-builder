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
<%@ include file="../common/global.jsp" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>


<mvc:formatter name="org.jboss.dashboard.ui.formatters.WorkspaceSelectorFormatter">
    <%--------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <td  class="Left">
        <div class="select">
        <form action="<factory:formUrl friendly="false"/>" method="POST" name="workspaceSelectorForm" id="workspaceSelectorForm" >
        <factory:handler bean="org.jboss.dashboard.ui.components.WorkspaceHandler" action="onWorkspace"/>
        <input type="hidden"
               name="<factory:bean property="operationName" bean="org.jboss.dashboard.ui.components.WorkspaceHandler"/>"
               value="navigate">
    </mvc:fragment>
    <%--------------------------------------------------------------------------------%>
    <mvc:fragment name="workspacesSelect">
        <mvc:formatter name="org.jboss.dashboard.ui.formatters.RenderWorkspacesFormatter">
            <mvc:fragment name="outputStart">
                <i18n:message key="ui.workspace"/>
                :&nbsp;
                <select class="select"
                name="<factory:bean property="workspaceId" bean="org.jboss.dashboard.ui.components.WorkspaceHandler"/>"
                onChange="this.form.submit();">
            </mvc:fragment>
            <mvc:fragment name="output">
                <mvc:fragmentValue name="current" id="selected">
                    <option value="<mvc:fragmentValue name="workspaceId"/>" <%=((Boolean) selected).booleanValue() ? "selected" : ""%>>
                        <mvc:fragmentValue name="workspaceName"/>
                    </option>
                </mvc:fragmentValue>
            </mvc:fragment>
            <mvc:fragment name="outputEnd">
                </select>
            </mvc:fragment>
        </mvc:formatter>
    </mvc:fragment>
    <%--------------------------------------------------------------------------------%>
    <mvc:fragment name="createNewButton">
        <i18n:message key="ui.workspace.createNew" id="createNewWorkspaceMsg"/>
        <a target="_top"
           href="<factory:url bean="org.jboss.dashboard.ui.config.TreeShortcutHandler" friendly="false" action="NewWorkspace"/>">
            <img src="<static:image relativePath="adminHeader/new-workspace.png"/>" border="0"
                 title="<%=createNewWorkspaceMsg%>" alt="<%=createNewWorkspaceMsg%>">
        </a>
    </mvc:fragment>
    <%--------------------------------------------------------------------------------%>
    <mvc:fragment name="editButton">
        <i18n:message key="ui.workspace.editProperties" id="editPropertiesMsg"/>
        <a target="_top"
           href="<factory:url bean="org.jboss.dashboard.ui.config.TreeShortcutHandler" friendly="false" action="WorkspaceConfig"/>">
            <img src="<static:image relativePath="adminHeader/edit.png"/>" border="0"
                 title="<%=editPropertiesMsg%>" alt="<%=editPropertiesMsg%>">
        </a>
    </mvc:fragment>
    <%--------------------------------------------------------------------------------%>
    <mvc:fragment name="deleteButton">
        <i18n:message key="ui.workspace.delete" id="deleteWorkspaceMsg"/>
        <a target="_top" href="#"
           onclick="if( window.confirm('<%=deleteWorkspaceMsg%>') ) { var form=document.forms['workspaceSelectorForm']; form.elements['<factory:bean property="operationName" bean="org.jboss.dashboard.ui.components.WorkspaceHandler"/>'].value='delete'; form.submit(); }  return false;  ">
            <img src="<static:image relativePath="adminHeader/remove.png"/>" border="0"
                 title="<%=deleteWorkspaceMsg%>" alt="<%=deleteWorkspaceMsg%>">
        </a>
    </mvc:fragment>
    <%--------------------------------------------------------------------------------%>
    <mvc:fragment name="duplicateButton">
        <i18n:message key="ui.workspace.duplicate" id="duplicateWorkspaceMsg"/>
        <i18n:message key="ui.workspace.confirmDuplicate" id="confirmDuplicateMsg"/>
        <a target="_top" href="#"
           onclick="if( window.confirm('<%=confirmDuplicateMsg%>') ) { var form=document.forms['workspaceSelectorForm']; form.elements['<factory:bean property="operationName" bean="org.jboss.dashboard.ui.components.WorkspaceHandler"/>'].value='duplicate'; form.submit(); }  return false; ">
            <img src="<static:image relativePath="adminHeader/duplicate-workspace.png"/>" border="0"
                 title="<%=duplicateWorkspaceMsg%>" alt="<%=duplicateWorkspaceMsg%>">
        </a>
    </mvc:fragment>
    <%--------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        </form>
            </div>
        </td>
    </mvc:fragment>
</mvc:formatter>
