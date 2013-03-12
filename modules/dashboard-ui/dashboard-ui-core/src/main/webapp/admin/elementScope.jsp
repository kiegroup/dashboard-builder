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
                 org.jboss.dashboard.workspace.*,
                 org.jboss.dashboard.ui.taglib.LocalizeTag,
                 org.jboss.dashboard.ui.resources.GraphicElement,
                 org.jboss.dashboard.ui.resources.GraphicElementScopeDescriptor,
                 java.util.Iterator,
                 java.util.Set" %>
<%@ page import="org.jboss.dashboard.ui.UIServices" %>
<%@ page import="org.jboss.dashboard.workspace.*" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>

<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>
<%
    String graphicElement = (String) request.getAttribute("graphicElement");
    String graphicElementClassName = (String) request.getAttribute("graphicElementClassName");
    String panelI18n = "Panel";
    String instanceI18n = "PanelInstance";
    GraphicElementManager manager = (GraphicElementManager) request.getAttribute("manager");
    GraphicElementScopeDescriptor scopeDescriptor = manager.getElementScopeDescriptor();
    GraphicElement element = (GraphicElement) request.getAttribute("element");
    boolean workspaceChangeable = "true".equals(request.getAttribute("workspaceChangeable"));
    boolean sectionChangeable = "true".equals(request.getAttribute("sectionChangeable"));
    boolean panelChangeable = "true".equals(request.getAttribute("panelChangeable"));
%>
<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.global"%>' id="global">!!!Global</i18n:message>
<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.confirmChange"+graphicElementClassName+"Scope"%>'
              id="msgChangeScope">!!!Sure?</i18n:message>
<i18n:message key='<%="ui.workspace"%>' id="workspaceI18n">!!!Workspace</i18n:message>
<i18n:message key='<%="ui.sections.section"%>' id="sectionI18n">!!!Section</i18n:message>
<i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.base"+graphicElementClassName%>' id="base">
    !!!Base</i18n:message>


<script defer type="text/javascript" language="javascript">
    function checkChangeScope(id) {
        doit = confirm('<%=msgChangeScope%>');
        if (doit) {
            eval('document.changeScopeForElement' + id + '.submit()');
        }
        else {
            document.location.href = '<factory:url bean="org.jboss.dashboard.ui.components.AdminHandler"  action="cancel"/>';
        }
    }
</script>
<%
    String elementWorkspaceId = element.getWorkspaceId() == null ? "" : element.getWorkspaceId();
    String elementSectionId = element.getSectionId() == null ? "" : element.getSectionId().toString();
    String elementPanelId = element.getPanelId() == null ? "" : element.getPanelId().toString();

    WorkspaceImpl elementWorkspace = null;
    Section elementSection = null;
    Panel elementPanel = null;
    PanelInstance elementInstance = null;

    if (element.getWorkspaceId() != null) {
        elementWorkspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(element.getWorkspaceId());
        if (elementWorkspace != null && element.getSectionId() != null) {
            elementSection = ((WorkspaceImpl) elementWorkspace).getSection(element.getSectionId());
            if (elementSection != null && element.getPanelId() != null && scopeDescriptor.isAllowedPanel()) {
                elementPanel = elementSection.getPanel(element.getPanelId().toString());
            }
        }
        if (elementWorkspace != null && element.getPanelId() != null && scopeDescriptor.isAllowedInstance()) {
            elementInstance = elementWorkspace.getPanelInstance(element.getPanelId());
        }
    }

    String elementWorkspaceName = (String) (elementWorkspace == null ? global : (workspaceI18n + ":" + LocalizeTag.getLocalizedValue(elementWorkspace.getName(), SessionManager.getCurrentLocale().getLanguage(), true)));
    String elementSectionName = (String) (elementSection == null ? global : (sectionI18n + ":" + LocalizeTag.getLocalizedValue(elementSection.getTitle(), SessionManager.getCurrentLocale().getLanguage(), true)));
    String elementPanelName = (String) (elementPanel == null ? global : (panelI18n + ":" + LocalizeTag.getLocalizedValue(elementPanel.getTitle(), SessionManager.getCurrentLocale().getLanguage(), true)));
    String elementInstanceName = (String) (elementInstance == null ? global : (instanceI18n + ":" + LocalizeTag.getLocalizedValue(elementInstance.getTitle(), SessionManager.getCurrentLocale().getLanguage(), true)));

    if (manager.isBaseElement(element)) {
%>
<%=global%> (<%=base%>)
<%
} else {
%>
<%--<form action="<mvc:link action="ChangeScopeForElement" handler="admin"/>" method="POST"
      name="changeScopeForElement<%=element.getDbid()%>">--%>
<form action="<factory:url bean="org.jboss.dashboard.ui.components.AdminHandler" action="ChangeScopeForElement"  />" method="POST"
      name="changeScopeForElement<%=element.getDbid()%>"
      onsubmit="if (confirm('<%=msgChangeScope%>')) this.submit(); else document.location.href = '<<factory:url bean="org.jboss.dashboard.ui.components.AdminHandler" action="cancel"/>';">
<input type="hidden" name="<%=graphicElement%>Id" value="<%=element.getDbid()%>">
<input type="hidden" name="graphicElement" value="<%=graphicElement%>">
<%
    if (workspaceChangeable && scopeDescriptor.isAllowedWorkspace()) {
        String[] workspaceIds = (String[]) UIServices.lookup().getWorkspacesManager().getAllWorkspacesIdentifiers().toArray(new String[]{});
%>
<%--<select name="workspaceId" class="skn-input" onchange=" checkChangeScope(<%=element.getDbid()%>); ">--%>
    <select name="workspaceId" class="skn-input" onchange="eval('document.changeScopeForElement<%=element.getDbid()%>.onsubmit()');">
    <option value="" <%="".equals(elementWorkspaceId) ? "selected" : ""%>><%=global%></option>
    <%
        for (int j = 0; j < workspaceIds.length; j++) {
            String workspaceId = workspaceIds[j];
    %>
    <option value="<%=workspaceId%>" <%=workspaceId.equals(elementWorkspaceId) ? "selected" : ""%>>
        <%=workspaceI18n%>: <panel:localize data="<%=UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId).getName()%>"/>
    </option>
    <%}%>
    </select>

<%} else {%>
<input type="hidden" name="workspaceId" value="<%=elementWorkspaceId%>">
<%=elementWorkspaceName%>
<%}
   if (elementWorkspace != null) {
        if (scopeDescriptor.isAllowedWorkspace() && scopeDescriptor.isAllowedSection() && !scopeDescriptor.isAllowedInstance()) {//Element only for section
            if (sectionChangeable) {
                Set sections = ((WorkspaceImpl) elementWorkspace).getSections();
%>
&nbsp;<%--<select name="sectionId" class="skn-input" onchange=" checkChangeScope(<%=element.getDbid()%>); ">--%>
      <br><br><select name="sectionId" class="skn-input" onchange="eval('document.changeScopeForElement<%=element.getDbid()%>.onsubmit()');">
    <option value="" <%="".equals(elementSectionId) ? "selected" : ""%>><%=global%></option>
    <%
                for (Iterator it = sections.iterator(); it.hasNext();) {
                    Section section = (Section) it.next();
                    Long sectionId = section.getId();
    %>
    <option value="<%=sectionId%>" <%=sectionId.toString().equals(elementSectionId) ? "selected" : ""%>>
        <%=sectionI18n%>: <panel:localize data="<%=section.getTitle()%>"/>
    </option>
    <%
                }
    %>
    </select>
<%
            } else {//Section is read only
%>
<input type="hidden" name="sectionId" value="<%=elementSectionId%>">
&nbsp;<%=elementSectionName%>
<%
            }
    } else if (!scopeDescriptor.isAllowedSection() && scopeDescriptor.isAllowedInstance()) {//Element only for instance
        if (panelChangeable) {
            PanelInstance[] instances = ((WorkspaceImpl) elementWorkspace).getPanelInstances();
%>
&nbsp;<%--<select name="panelId" class="skn-input" onchange=" checkChangeScope(<%=element.getDbid()%>); ">--%>
    <select name="panelId" class="skn-input" onchange="eval('document.changeScopeForElement<%=element.getDbid()%>.onsubmit()');">
    <option value="" <%="".equals(elementPanelId) ? "selected" : ""%>><%=global%></option>
<%
            for (int i = 0; i < instances.length; i++) {
                PanelInstance instance = instances[i];
                Long instanceId = instance.getInstanceId();
%>
    <option value="<%=instanceId%>" <%=instanceId.toString().equals(elementPanelId)?"selected":""%> >
        <%=instanceI18n%>:
        <panel:localize data="<%=instance.getTitle()%>"/>
<%
            }
%>
</select>
<%
        } else {
%>
<input type="hidden" name="panelId" value="<%=elementPanelId%>">
&nbsp;<%=elementInstanceName%>
<%
        }
    } else if (scopeDescriptor.isAllowedSection() && scopeDescriptor.isAllowedInstance()) {//Element for instance or section
        if (sectionChangeable && panelChangeable) {
            PanelInstance[] instances = ((WorkspaceImpl) elementWorkspace).getPanelInstances();
            Set sections = ((WorkspaceImpl) elementWorkspace).getSections();
%>
<input type="hidden" name="sectionId" value="<%=elementSectionId%>">
<input type="hidden" name="panelId" value="<%=elementPanelId%>">
&nbsp;<%--<select name="sectionOrInstanceId" class="skn-input" onchange="
            if(value.indexOf('i')!=-1){
                sectionId.value='';
                panelId.value=sectionOrInstanceId.value.substring(1);
            } else if(value.indexOf('s')!=-1){
                panelId.value='';
                sectionId.value=sectionOrInstanceId.value.substring(1);
            } else{
                panelId.value='';
                sectionId.value='';
            }
            checkChangeScope(<%=element.getDbid()%>);">--%>
    <br><br><select name="sectionOrInstanceId" class="skn-input" onchange="
            if(value.indexOf('i')!=-1){
                sectionId.value='';
                panelId.value=sectionOrInstanceId.value.substring(1);
            } else if(value.indexOf('s')!=-1){
                panelId.value='';
                sectionId.value=sectionOrInstanceId.value.substring(1);
            } else{
                panelId.value='';
                sectionId.value='';
            }
            eval('document.changeScopeForElement<%=element.getDbid()%>.onsubmit()');">
    <option value="" <%="".equals(elementSectionId) && "".equals(elementPanelId) ? "selected" : ""%>><%=global%></option>
    <%
            for (Iterator it = sections.iterator(); it.hasNext();) {
                Section section = (Section) it.next();
                Long sectionId = section.getId();
    %>
    <option value="s<%=sectionId%>" <%=sectionId.toString().equals(elementSectionId) ? "selected" : ""%>>
        <%=sectionI18n%>: <panel:localize data="<%=section.getTitle()%>"/>
    </option>
    <%
            }
            for (int i = 0; i < instances.length; i++) {
                PanelInstance instance = instances[i];
                Long instanceId = instance.getInstanceId();
    %>
    <option value="i<%=instanceId%>" <%=instanceId.toString().equals(elementPanelId)?"selected":""%> >
        <%=instanceI18n%>:
        <panel:localize data="<%=instance.getTitle()%>"/>
<%
            }
%>
</select>
<%
        } else {
%>
<input type="hidden" name="panelId" value="<%=elementPanelId%>">
<input type="hidden" name="sectionId" value="<%=elementSectionId%>">
<%
            if (element.getSectionId() != null) {
%><br><%=elementSectionName%><%
            } else if (element.getPanelId() != null) {
%><br><%=elementInstanceName%><%
            } else {
%><br><%=global%><%
            }
%>
<%      }
    }
}
    if (elementSection != null) {
        if (panelChangeable && scopeDescriptor.isAllowedPanel() && !scopeDescriptor.isAllowedInstance()) {//select
            Set panels = elementSection.getPanels();
%>
<br><%--<select name="panelId" class="skn-input" onchange=" checkChangeScope(<%=element.getDbid()%>); ">--%>
    <select name="panelId" class="skn-input" onchange="eval('document.changeScopeForElement<%=element.getDbid()%>.onsubmit()');">
    <option value="" <%="".equals(elementPanelId) ? "selected" : ""%>><%=global%></option>
    <%
        for (Iterator it = panels.iterator(); it.hasNext();) {
            Panel panel = (Panel) it.next();
            Long panelId = panel.getPanelId();
    %>
    <option value="<%=panelId%>" <%=panelId.toString().equals(elementPanelId) ? "selected" : ""%>>
        <%=panelI18n%>: <panel:localize data="<%=panel.getTitle()%>"/>
    </option>
    <%
        }
    %>
</select>
<%
} else if (scopeDescriptor.isAllowedPanel() && !scopeDescriptor.isAllowedInstance()) {//hidden and text
%>
<input type="hidden" name="panelId" value="<%=elementPanelId%>">
<br><%=elementPanelName%>
<%
        }
    }
        %>
</form>
<%
 }
%>
