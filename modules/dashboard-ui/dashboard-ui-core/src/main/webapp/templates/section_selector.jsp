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
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<td>
    <form action="<factory:formUrl friendly="false"/>" method="POST" style="margin:0px"
        name="sectionSelectorForm" id="sectionSelectorForm" >
        <factory:handler bean="org.jboss.dashboard.ui.components.SectionsHandler" action="onSection"/>
        <input type="hidden"
               name="<factory:bean property="operationName" bean="org.jboss.dashboard.ui.components.SectionsHandler"/>"
               value="">
    </form>
     <div class="select">
    <form action="" method="post">
<mvc:formatter name="org.jboss.dashboard.ui.formatters.RenderIndentedSectionsFormatter">
    <mvc:formatterParam name="preffix" value="&nbsp;&nbsp;"/>
    <mvc:fragment name="empty">
        <span class="skn-title3 skn-error">
            <i18n:message key="ui.sections.noSections"/>
        </span>
    </mvc:fragment>
    <mvc:fragment name="outputStart">
    <i18n:message key="ui.sections.section"/>:&nbsp;
    <select id="sectionSelect" class="select" name="section"
        onchange="if(this.options[this.selectedIndex].value){
            this.form.action = this.options[this.selectedIndex].value;
            this.form.submit();
            return false;
        } else return false;">
    </mvc:fragment>
    <mvc:fragment name="output">
            <option value="<mvc:fragmentValue name="url"/>"><mvc:fragmentValue name="title"/></option>
    </mvc:fragment>
    <mvc:fragment name="outputSelected">
            <option selected class="skn-important" value=""><mvc:fragmentValue name="title"/></option>
    </mvc:fragment>
    <mvc:fragment name="outputNoneSelected">
            <option selected disabled class="skn-disabled" value="">-- <i18n:message key="ui.noOne"/> --</option>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </select>&nbsp;&nbsp;
    </mvc:fragment>
</mvc:formatter>
<%
    if (adminMode) {
        WorkspacePermission createPerm = WorkspacePermission.newInstance(currentWorkspace, WorkspacePermission.ACTION_CREATE_PAGE);

%>
<security:checkpermission permission="<%= createPerm %>">
    <i18n:message key="ui.sections.createNew" id="newSectionMsg"/>
    <a target=_top href="<factory:url bean="org.jboss.dashboard.ui.config.TreeShortcutHandler" friendly="false" action="NewPage"/>"><img src="<static:image relativePath="adminHeader/new-section.png"/>" title="<%=newSectionMsg%>"></a>
</security:checkpermission>
<%
    if (currentSection != null) {
        SectionPermission editPerm = SectionPermission.newInstance(currentSection, SectionPermission.ACTION_EDIT);
        SectionPermission delPerm = SectionPermission.newInstance(currentSection, SectionPermission.ACTION_DELETE);
%>
<security:checkpermission permission="<%= editPerm %>">
    <i18n:message key="ui.sections.editProperties" id="editSectionMsg"/>
    <a target=_top href="<factory:url bean="org.jboss.dashboard.ui.config.TreeShortcutHandler" friendly="false" action="PageConfig"/>"><img src="<static:image relativePath="adminHeader/edit.png"/>" title="<%=editSectionMsg%>"></a>
</security:checkpermission>
<security:checkpermission permission="<%= delPerm %>">
    <i18n:message key="ui.sections.deleteCurrent" id="deleteSectionMsg"/>
    <i18n:message key="ui.sections.confirmDeleteCurrent" id="confirmDeleteMsg"/>
    <a target=_top
       href="<factory:url bean='org.jboss.dashboard.ui.components.SectionsHandler'  action="delete" friendly="false">
       <factory:param name="id" value="<%=currentSection.getId()%>"/>
       </factory:url>"
       onClick="if( window.confirm('<%=confirmDeleteMsg%>' ) ){ var form=document.forms['sectionSelectorForm']; form.elements['<factory:bean property="operationName" bean="org.jboss.dashboard.ui.components.SectionsHandler"/>'].value='delete'; form.submit();  } return false;"><img src="<static:image relativePath="adminHeader/remove.png"/>" title="<%=deleteSectionMsg%>"></a>
</security:checkpermission>

<security:checkpermission permission="<%= createPerm %>">
    <i18n:message key="ui.sections.duplicateCurrent" id="duplicateSectionMsg"/>
    <a target=_top href="<factory:url bean="org.jboss.dashboard.ui.config.TreeShortcutHandler" friendly="false" action="duplicatePage"/><%="&id=" + currentSection.getId() %>"><img src="<static:image relativePath="adminHeader/duplicate-section.png"/>" title="<%=duplicateSectionMsg%>"></a>
</security:checkpermission>

<a title="<i18n:message key="ui.panels.createNewInstanceInSection"/>" id="<factory:encode name="addNewComponentToSection"/>" href="<factory:url bean="org.jboss.dashboard.ui.panel.PopupPanelsHandler" action="getPanelsPopupPage" friendly="false"/>">
    <img src="<static:image relativePath="adminHeader/new-panel.png"/>" border=0>
</a>
<script type="text/javascript" defer="defer">
    setAjax("<factory:encode name="addNewComponentToSection"/>");
</script>
<%
        }
    }
%>
</form>
    </div>
    <form method="POST" name="instanceDragAndDropToRegion" style="display:none"
          action="<factory:url bean='org.jboss.dashboard.ui.components.PanelsHandler'  action='putInstanceToRegion'/>">
        <input type='hidden' name="panelId">
        <input type='hidden' name="region">
        <input type='hidden' name="position">
    </form>
    <form method="POST" name="panelDragAndDropToRegion" style="display:none"
          action="<factory:url bean='org.jboss.dashboard.ui.components.PanelsHandler'  action='moveToRegion'/>">
        <input type='hidden' name="panelId">
        <input type='hidden' name="region">
        <input type='hidden' name="position">
    </form>

</td>
