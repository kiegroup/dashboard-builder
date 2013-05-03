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
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler"%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="org.jboss.dashboard.security.WorkspacePermission"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="security_taglib.tld" prefix="security" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<static:image relativePath="general/16x16/ico-page.png" id="panelURI"/>
<static:image relativePath="general/16x16/ico-folder_closed.png" id="folderClosedURI"/>
<static:image relativePath="general/16x16/ico-folder_open.png" id="folderOpenURI"/>

<factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
                     property="i18nBundle" propValue="org.jboss.dashboard.ui.messages" />
<factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
                     property="clearAfterRender" propValue="true" />
<factory:useComponent bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"/>

<mvc:formatter name="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesFormatter">
    <mvc:fragment name="outputErrorCommands">
        <mvc:fragmentValue name="errorCommand" id="errorCommand">

            <i18n:message key='<%="ui.sections."+errorCommand%>' id="errorMessage">!!Error Command</i18n:message>
<script type="text/javascript" language="Javascript" defer>
    alert("<%=errorMessage%>");
</script>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputCreateSection">
        <jsp:include page="SectionsNodeEditCreate.jsp" flush="true"/>
    </mvc:fragment>
    <mvc:fragment name="outputDuplicateSection">
        <jsp:include page="SectionsNodeEditDuplicate.jsp" flush="true"/>
    </mvc:fragment>
<%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
<form method="POST" name="config" action="<factory:formUrl friendly="false"/>" id="sectionsManagementForm">
    <factory:handler bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" action="manageSection"/>
    <input type="hidden" name="<factory:bean bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" property="selectedSectionId"/>" id="id" value="">
    <input type="hidden" name="<factory:bean bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" property="action"/>" id="action" value="">
    <input name="MoveSelected" type="hidden" class="skn-button" value="MoveSelected">
</form>
<script defer>
    setAjax("sectionsManagementForm");
</script>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
    </mvc:fragment>
<%-------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputCommandsBarStart">
    <tr>
        <td>
            <fieldset style="vertical-align:middle;padding:0px;">
                <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="margin:0px">
                    <tr>
                        <td width="85px" nowrap >
                            <a href="#" onclick="return submitFormAction('<%=SectionsPropertiesHandler.ACTION_MOVE_UP%>');" border="0"
                               title="<i18n:message key="ui.moveUp"/>">
                                <img src="<static:image relativePath="general/16x16/ico-actions_go-up.png"/>" border="0" />

                            </a>
                            <a href="#" onclick="return submitFormAction('<%=SectionsPropertiesHandler.ACTION_MOVE_DOWN%>');" border="0"
                               title="<i18n:message key="ui.moveDown"/>">
                                <img src="<static:image relativePath="general/16x16/ico-actions_go-down.png"/>" border="0" />
                            </a>
                            <a href="#" onclick="return submitFormAction('<%=SectionsPropertiesHandler.ACTION_PROMOVE_PARENT%>');" border="0"
                               title="<i18n:message key="ui.doParent"/>">
                                <img src="<static:image relativePath="general/16x16/ico-actions_go-first.png"/>" border="0" />
                            </a>
                            <a href="#" onclick="return submitFormAction('<%=SectionsPropertiesHandler.ACTION_SET_PARENT%>');" border="0"
                               title="<i18n:message key="ui.doChild"/>">
                                <img src="<static:image relativePath="general/16x16/ico-actions_go-last.png"/>" border="0" />
                            </a>
                        </td>
    </mvc:fragment>
<%------------------- SELECT TO MOVE SECTIONS ------------------------%>
    <mvc:fragment name="outputStartSelect">
                        <td>
                            <select id="sectionSelect" class="skn-input" name="sectionSelected"
                                onchange="if(this.options[this.selectedIndex].value){
                                                document.config.MoveSelected.value = this.options[this.selectedIndex].value;
                                                doMoveSelectedSection();
                                        };
                                        this.selectedIndex = 0;">
    </mvc:fragment>
    <mvc:fragment name="outputSelect">
                                <option value="<mvc:fragmentValue name="id"/>"><mvc:fragmentValue name="title"/></option>
    </mvc:fragment>
<%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputNoneSelected">
                                <option selected  class="skn-disabled" value="">-- <i18n:message key="ui.moveSelected"/> --</option>
    </mvc:fragment>
<%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputEndSelect">
                            </select>
                        </td>
    </mvc:fragment>
<%-----------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputCommandsBarEnd">
        <mvc:fragmentValue name="workspace" id="workspace">
        <mvc:fragmentValue name="editPanels" id="editPanels">
                        <td  nowrap  align="right">
<%
    if (editPanels!=null && ((Boolean)editPanels).booleanValue()) {
        WorkspacePermission editPanelsPermission = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_ADMIN);
%>
                        <security:checkpermission permission="<%= editPanelsPermission %>">
                            <a href="#" onclick="return submitFormAction('<%=SectionsPropertiesHandler.ACTION_GO_TO_PANELS%>');" border="0"
                                title="<i18n:message key="ui.PagePanels"/>"
                                id="<%=SectionsPropertiesHandler.ACTION_GO_TO_PANELS%>">
                                <img src="<static:image relativePath="general/16x16/ico-menu_panels.png"/>" border="0" />
                            </a>
                            <img src="<static:image relativePath="general/16x16/ico-menu_panels.png"/>"
                                 id="<%=SectionsPropertiesHandler.ACTION_GO_TO_PANELS + "_alt"%>"
                                 title="<i18n:message key="ui.PagePanels"/>"
                                 style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50); display:none" />
                        </security:checkpermission>
<%
    }
%>
                            <a href="#" onclick="return submitFormAction('<%=SectionsPropertiesHandler.ACTION_GO_TO_RESOURCES%>');" border="0"
                                title="<i18n:message key="ui.PageGallery"/>"
                                id="<%=SectionsPropertiesHandler.ACTION_GO_TO_RESOURCES%>">
                                <img src="<static:image relativePath="general/16x16/ico-menu_resources.png"/>" border="0" />
                            </a>
                            <img src="<static:image relativePath="general/16x16/ico-menu_resources.png"/>"
                                 id="<%=SectionsPropertiesHandler.ACTION_GO_TO_RESOURCES + "_alt"%>"
                                 title="<i18n:message key="ui.PageGallery"/>"
                                 style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50); display:none"/>
                            <a href="#" onclick="return submitFormAction('<%=SectionsPropertiesHandler.ACTION_GO_TO_PERMISSIONS%>');" border="0"
                                title="<i18n:message key="ui.PagePermissions"/>"
                                id="<%=SectionsPropertiesHandler.ACTION_GO_TO_PERMISSIONS%>">
                                <img src="<static:image relativePath="general/16x16/ico-menu_permissions.png"/>" border="0" />
                            </a>
                            <img src="<static:image relativePath="general/16x16/ico-menu_permissions.png"/>"
                                 id="<%=SectionsPropertiesHandler.ACTION_GO_TO_PERMISSIONS + "_alt"%>"
                                 title="<i18n:message key="ui.PagePermissions"/>"
                                 style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50); display:none"/>
                        </td>
                        <td width="105px" nowrap align="right">
<%
    WorkspacePermission createPerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_CREATE_PAGE);
    SectionPermission editPerm = SectionPermission.newInstance(workspace, SectionPermission.ACTION_EDIT);
    SectionPermission deletePerm = SectionPermission.newInstance(workspace, SectionPermission.ACTION_DELETE);
%>
                <security:checkpermission permission="<%= editPerm %>">
                            <a href="#" onclick="return submitFormAction('<%=SectionsPropertiesHandler.ACTION_GO_TO_PROPERTIES%>');" border="0"
                                title="<i18n:message key="ui.edit"/>"
                                id="<%=SectionsPropertiesHandler.ACTION_GO_TO_PROPERTIES%>">
                                <img src="<static:image relativePath="general/16x16/ico-edit_page.png"/>" border="0" />
                            </a>
                            <img src="<static:image relativePath="general/16x16/ico-edit_page.png"/>"
                                 id="<%=SectionsPropertiesHandler.ACTION_GO_TO_PROPERTIES + "_alt"%>"
                                 title="<i18n:message key="ui.edit"/>"
                                 style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50); display:none"/>
                </security:checkpermission>
                <security:checkpermission permission="<%= createPerm %>">
                            <a href="#" onclick="return submitFormAction('<%=SectionsPropertiesHandler.ACTION_CLONE%>');" border="0"
                                title="<i18n:message key="ui.sections.duplicate"/>"
                                id="<%=SectionsPropertiesHandler.ACTION_CLONE%>">
                                <img src="<static:image relativePath="general/16x16/ico-clone_page.png"/>" border="0" />
                            </a>
                            <a href="#" onclick="return submitFormAction('<%=SectionsPropertiesHandler.ACTION_CREATE%>');" border="0"
                                title="<i18n:message key="ui.sections.createNew"/>"
                                id="<%=SectionsPropertiesHandler.ACTION_CREATE%>">
                                 <img src="<static:image relativePath="general/16x16/ico-new_page.png"/>" border="0" />
                            </a>
                </security:checkpermission>
                <security:checkpermission permission="<%= deletePerm %>">
                            <a href="#" onclick="return doDelete();" border="0"
                                title="<i18n:message key="ui.delete"/>"
                                id="<%=SectionsPropertiesHandler.ACTION_DELETE%>">
                                 <img src="<static:image relativePath="general/16x16/ico-trash.png"/>" border="0" />
                            </a>
                            <img src="<static:image relativePath="general/16x16/ico-trash.png"/>"
                                 id="<%=SectionsPropertiesHandler.ACTION_DELETE + "_alt"%>"
                                 title="<i18n:message key="ui.delete"/>"
                                 style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50); display:none"/>
                </security:checkpermission>
                        </td>
                    </tr>
                </table>
            </fieldset>
        </td>
    </tr>
</table>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
<%-------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputTreeStart">

<fieldset style="padding:10px;">
    <legend>
        <span class="skn-title3"><i18n:message key="ui.sections.organizeSections"/></span>
    </legend>
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr>
            <td>
                &nbsp;
            </td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="outputEmptySections">
        <tr>
            <td align="center">
                <span class="skn-error"><i18n:message key="ui.sections.noDefinedSections"/></span>
                <br>
            </td>
        </tr>
    </mvc:fragment>
<%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputTreeBody">
        <mvc:fragmentValue name="workspaceId" id="workspaceId">
        <tr>
            <td>

        <mvc:formatter name="org.jboss.dashboard.ui.formatters.RenderNestedSectionsFormatter">
            <mvc:formatterParam name="checkPermissions" value="true"/>
            <mvc:formatterParam name="showHiddenPages" value="true"/>
            <mvc:formatterParam name="workspaceId" value="<%=workspaceId%>"/>

            <mvc:fragment name="outputStart">
                <div style=" ">
            </mvc:fragment>
            <mvc:fragment name="sectionStart">
            </mvc:fragment>
            <mvc:fragment name="outputSection">
                <mvc:fragmentValue name="hasChildren" id="hasChildren">
                <mvc:fragmentValue name="isRoot" id="isRoot">
                <mvc:fragmentValue name="editSection" id="editSection">
                <mvc:fragmentValue name="editPermissions" id="editPermissions">
                <mvc:fragmentValue name="deleteSection" id="deleteSection">
                    <div id="parent_section_<mvc:fragmentValue name="id"/>">
                        <div id="parent_section_name_<mvc:fragmentValue name="id"/>">
                            <div style="vertical-align:middle;display:inline;">
                                <img style="vertical-align:middle;" src="<%= hasChildren != null && ((Boolean)hasChildren).booleanValue() ? folderClosedURI : panelURI%>" id="<mvc:fragmentValue name="id"/>_img">
                            </div>
                            <div style="vertical-align:middle;display:inline;">
                                <a href="#" onclick="selectItem('<mvc:fragmentValue name="id"/>', <%=((Boolean)isRoot).booleanValue()%>, <%=((Boolean)editSection).booleanValue()%>, <%=((Boolean)editPermissions).booleanValue()%>, <%=((Boolean)deleteSection).booleanValue()%>); return false">
                                    <mvc:fragmentValue name="name"/>
                                </a>
                            </div>
                        </div>
                </mvc:fragmentValue>
                </mvc:fragmentValue>
                </mvc:fragmentValue>
                </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragment>
            <mvc:fragment name="sectionEnd">
                    </div>
            </mvc:fragment>
            <mvc:fragment name="outputChildStart">
                    <div id="child_sections_<mvc:fragmentValue name="id"/>" style="padding-left: 20px; display:none">
            </mvc:fragment>
            <mvc:fragment name="outputChildEnd">
                    </div>
            </mvc:fragment>
            <mvc:fragment name="outputEnd">
                </div>
            </mvc:fragment>
        </mvc:formatter>
            </td>
        </tr>
        </mvc:fragmentValue>
    </mvc:fragment>
<%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputTreeEnd">
    </table>
</fieldset>
<br>
    </mvc:fragment>
<%----------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        <mvc:fragmentValue name="moveLoop" id="moveLoop">
<script defer="defer">

<i18n:message key="ui.error.moveLoop" id="errorMoveLoop"/>
<%
    Boolean isLoop = (Boolean)moveLoop;
%>
    if(<%=isLoop.booleanValue()%>){
        window.alert('<%=StringEscapeUtils.escapeJavaScript(errorMoveLoop)%>');
    }

    function selectItem(id, isRoot, editSection, editPermissions, deleteSection) {
        if (document.getElementById('id').value != id) {
            if (document.getElementById('id').value != "") {
                document.getElementById("parent_section_name_"+document.getElementById('id').value).style.fontWeight = "";
                if (document.getElementById("child_sections_"+document.getElementById('id').value)) {
                    if (isRoot)document.getElementById("child_sections_"+document.getElementById('id').value).style.display = "none"
                    document.getElementById(document.getElementById('id').value+"_img").src = "<%=folderClosedURI%>"
                }
            }
            document.getElementById('id').value = id;
            if (editSection) {
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_PROPERTIES%>").style.display = '';
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_RESOURCES%>").style.display = '';
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_PROPERTIES + "_alt"%>").style.display = 'none';
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_RESOURCES + "_alt"%>").style.display = 'none';
            } else {
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_PROPERTIES%>").style.display = 'none';
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_RESOURCES%>").style.display = 'none';
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_PROPERTIES + "_alt"%>").style.display = '';
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_PROPERTIES + "_alt"%>").style.display = '';
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_RESOURCES + "_alt"%>").style.display = '';
            }

            if (editPermissions) {
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_PERMISSIONS%>").style.display = '';
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_PERMISSIONS + "_alt"%>").style.display = 'none';
            } else {
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_PERMISSIONS%>").style.display = 'none';
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_GO_TO_PERMISSIONS + "_alt"%>").style.display = '';
            }

            if (deleteSection) {
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_DELETE%>").style.display = '';
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_DELETE + "_alt"%>").style.display = 'none';
            } else {
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_DELETE%>").style.display = 'none';
                document.getElementById("<%=SectionsPropertiesHandler.ACTION_DELETE + "_alt"%>").style.display = '';
            }

        }

        document.getElementById("parent_section_name_"+id).style.fontWeight = "bold";
        if (document.getElementById("child_sections_"+id)) {
            if (document.getElementById("child_sections_"+id).style.display == "none") {
                document.getElementById("child_sections_"+id).style.display = "block"
                document.getElementById(id+"_img").src = "<%=folderOpenURI%>"
            } else {
                document.getElementById("child_sections_"+id).style.display = "none"
                document.getElementById(id+"_img").src = "<%=folderClosedURI%>"
            }
        }
    }


    function submitFormAction(action) {
        if (checkSelection(action)) document.config.submit();
        return false;
    }

    <i18n:message key="ui.sections.sameSectionSelected" id="sameSectionsSelected"/>
    function doMoveSelectedSection(){
        if(document.getElementById('id').value != document.config.MoveSelected.value){
            submitFormAction('<%=SectionsPropertiesHandler.ACTION_MOVE_SELECTED%>')
        } else {
            window.alert('<%=StringEscapeUtils.escapeJavaScript(sameSectionsSelected)%>');
            document.config.MoveSelected.value = "";
            return false;
        }
        return false;
    }

    <i18n:message key="ui.sections.confirmDelete" id="confirmDeleteMsg"/>
    function doDelete() {
        if (checkSelection('<%=SectionsPropertiesHandler.ACTION_DELETE%>')) {
            if (confirm('<%=confirmDeleteMsg%>')) document.config.submit();
        }
        return false;
    }

    <i18n:message key="ui.sections.selectASection" id="selectASectionMsg"/>
    function checkSelection(action) {
        if (document.getElementById('id').value != '' || action == '<%=SectionsPropertiesHandler.ACTION_CREATE%>') {
            document.getElementById('action').value = action;
            return true;
        } else {
            window.alert('<%=selectASectionMsg%>');
            document.getElementById('action').value = '';
            return false;
        }
    }
</script>
        </mvc:fragmentValue>
    </mvc:fragment>
<%-------------------------------------------------------------------------------------------%>
</mvc:formatter>

