/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.ui.config;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.BeanHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.config.components.panels.PanelsPropertiesHandler;
import org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.security.BackOfficePermission;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.users.UserStatus;
import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.workspace.Section;
import org.slf4j.Logger;

@SessionScoped
@Named("tree_shortcut_handler")
public class TreeShortcutHandler extends BeanHandler {

    public static final String WORKSPACE_TOKEN = "$WORKSPACE";
    public static final String PAGE_TOKEN = "$PAGE";
    public static final String PAGE_PATH_TOKEN = "$PAG_PATH";
    public static final String PANEL_TOKEN = "$PANEL";
    public static final String PANEL_INSTANCE_TOKEN = "$PANEL_INSTANCE";

    @Inject
    private transient Logger log;

    @Inject
    private ConfigurationTree tree;

    @Inject
    private ConfigurationTreeStatus treeStatus;

    @Inject
    private SectionsPropertiesHandler sectionsPropertiesHandler;

    @Inject
    private PanelsPropertiesHandler panelsPropertiesHandler;

    @Inject
    private NavigationManager navigationManager;

    private String newWorkspace = "root/workspaces";
    private String workspaceConfigPath = "root/workspaces/$WORKSPACE";
    private String newPagePath = "root/workspaces/$WORKSPACE/sections";
    private String pageConfigPath = "root/workspaces/$WORKSPACE/sections/$PAGE";
    private String childPageConfigPath = "root/workspaces/$WORKSPACE/sections/$PAG_PATH";
    private String newPaneltPath = "root/workspaces/$WORKSPACE/sections/$PAG_PATH/panels";
    private String workspaceId;
    private Long sectionId;
    private Long parentSectionId;
    private Long panelId;

    public UserStatus getUserStatus() {
        return UserStatus.lookup();
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public String getNewPaneltPath() {
        return newPaneltPath;
    }

    public void setNewPaneltPath(String newPaneltPath) {
        this.newPaneltPath = newPaneltPath;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getParentSectionId() {
        return parentSectionId;
    }

    public void setParentSectionId(Long parentSectionId) {
        this.parentSectionId = parentSectionId;
    }


    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getPanelId() {
        return panelId;
    }

    public void setPanelId(Long panelId) {
        this.panelId = panelId;
    }

    public String getNewPagePath() {
        return newPagePath;
    }

    public void setNewPagePath(String newPagePath) {
        this.newPagePath = newPagePath;
    }

    public String getNewWorkspace() {
        return newWorkspace;
    }

    public void setNewWorkspace(String newWorkspace) {
        this.newWorkspace = newWorkspace;
    }

    public String getWorkspaceConfigPath() {
        return workspaceConfigPath;
    }

    public void setWorkspaceConfigPath(String workspaceConfigPath) {
        this.workspaceConfigPath = workspaceConfigPath;
    }

    public String getPageConfigPath() {
        return pageConfigPath;
    }

    public void setPageConfigPath(String pageConfigPath) {
        this.pageConfigPath = pageConfigPath;
    }

    public String getChildPageConfigPath() {
        return childPageConfigPath;
    }

    public void setChildPageConfigPath(String childPageConfigPath) {
        this.childPageConfigPath = childPageConfigPath;
    }

    protected void navigateToConfigPath(String path) {
        navigateToPath(path);
        getNavigationManager().setShowingConfig(true);
        tree.getNodeByPath(path).onEdit();
    }

    public void actionNewWorkspace(CommandRequest request) {
        BackOfficePermission workspacePerm = BackOfficePermission.newInstance(null, BackOfficePermission.ACTION_CREATE_WORKSPACE);
        getUserStatus().checkPermission(workspacePerm);
        navigateToConfigPath(newWorkspace);
    }

    public void actionWorkspaceConfig(CommandRequest request) throws Exception {
        WorkspaceImpl workspace =  getNavigationManager().getCurrentWorkspace();
        WorkspacePermission workspacePerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_EDIT);
        getUserStatus().checkPermission(workspacePerm);
        setWorkspaceValues(request);
        navigateToConfigPath(getParsedValue(workspaceConfigPath));
    }

    public void actionWorkspaceProperties(CommandRequest request) throws Exception {
        WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(request.getParameter(Parameters.DISPATCH_IDWORKSPACE));
        WorkspacePermission workspacePerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_EDIT);
        getUserStatus().checkPermission(workspacePerm);
        setWorkspaceId(request.getParameter(Parameters.DISPATCH_IDWORKSPACE));
        navigateToPath(getParsedValue(workspaceConfigPath));
    }

    public void actionNewPage(CommandRequest request) throws Exception {
        Workspace workspace = getNavigationManager().getCurrentWorkspace();
        WorkspacePermission sectionPerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_CREATE_PAGE);
        getUserStatus().checkPermission(sectionPerm);
        setWorkspaceValues(request);
        sectionsPropertiesHandler.setCreateSection(Boolean.TRUE);
        sectionsPropertiesHandler.setDuplicateSection(Boolean.FALSE);
        navigateToConfigPath(getParsedValue(newPagePath));
    }

    public void actionDuplicatePage(CommandRequest request) throws Exception {
        Workspace workspace = getNavigationManager().getCurrentWorkspace();
        WorkspacePermission sectionPerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_CREATE_PAGE);
        getUserStatus().checkPermission(sectionPerm);
        setWorkspaceValues(request);
        sectionsPropertiesHandler.setCreateSection(Boolean.FALSE);
        sectionsPropertiesHandler.setDuplicateSection(Boolean.TRUE);
        sectionsPropertiesHandler.setSelectedSectionId(getNavigationManager().getCurrentSection().getId().toString());
        navigateToConfigPath(getParsedValue(newPagePath));
    }

    public void actionPageConfig(CommandRequest request) throws Exception {
        Section section = getNavigationManager().getCurrentSection();
        SectionPermission sectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_EDIT);
        getUserStatus().checkPermission(sectionPerm);
        setWorkspaceValues(request);
        navigateToConfigPath(getParsedValue(section.getParent() != null ? childPageConfigPath : pageConfigPath));
    }

    public void actionPageConfigFromTree(CommandRequest request, WorkspaceImpl workspace) throws Exception {
        Section section = workspace.getSection(new Long(request.getParameter("id")));
        SectionPermission sectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_EDIT);
        getUserStatus().checkPermission(sectionPerm);
        setWorkspaceValuesFromTree(workspace, section, null);
        navigateToConfigPath(getParsedValue(pageConfigPath));
    }

    public void actionNewPanel(CommandRequest request) throws Exception {
        Workspace workspace = getNavigationManager().getCurrentWorkspace();
        WorkspacePermission panelPerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_ADMIN);
        getUserStatus().checkPermission(panelPerm);
        setWorkspaceValues(request);
        String region = request.getRequestObject().getParameter("region");
        panelsPropertiesHandler.setRegion(region);
        navigateToConfigPath(getParsedValue(newPaneltPath));
    }

    protected void navigateToPath(String path) {
        treeStatus.navigateToPath(tree, path);
    }

    protected String getParsedValue(String path) throws Exception {
        if (workspaceId != null) {
            path = StringUtils.replace(path, WORKSPACE_TOKEN, workspaceId);
        }
        if (workspaceId != null && sectionId != null) {
            path = StringUtils.replace(path, PAGE_PATH_TOKEN, getSectionIds(getWorkspaceId(), getSectionId()));
            path = StringUtils.replace(path, PAGE_TOKEN, getSectionId().toString());
        }
        if (workspaceId != null && sectionId != null && panelId != null) {
            WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
            path = StringUtils.replace(path, PANEL_INSTANCE_TOKEN, workspace.getSection(sectionId).getPanel(panelId.toString()).getInstanceId().toString());
            path = StringUtils.replace(path, PANEL_TOKEN, panelId.toString());
        }
        return path;
    }

    protected String getSectionIds(String workspaceId, Long sectionId) throws Exception {
        Section section = ((WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId)).getSection(sectionId);
        StringBuffer sb = new StringBuffer();
        while (section != null) {
            sb.insert(0, section.getId());
            section = section.getParent();
            if (section != null) {
                sb.insert(0, "/");
            }
        }
        return sb.toString();
    }

    protected void setWorkspaceValues(CommandRequest request) {
        setWorkspaceId(null);
        setParentSectionId(null);
        setSectionId(null);
        setPanelId(null);
        if (getNavigationManager().getCurrentWorkspace() != null) {
            setWorkspaceId(getNavigationManager().getCurrentWorkspace().getId());
        }
        if (getNavigationManager().getCurrentSection() != null) {
            Section section = getNavigationManager().getCurrentSection();
            setSectionId(section.getId());
            if (section.getParent() != null) setParentSectionId(section.getParent().getId());
        }
        if (request.getParameter("panelId") != null) {
            setPanelId(new Long(request.getParameter("panelId")));
        } else {
            setPanelId(null);
        }
    }

    protected void setWorkspaceValuesFromTree(WorkspaceImpl workspace, Section section, Panel panel) {
        if (workspace != null) {
            setWorkspaceId(workspace.getId());
        }
        if (section != null) {
            setSectionId(section.getId());
        }
        if (panel != null) {
            setPanelId(panel.getInstanceId());
        }
    }

    public void actionChangeLanguage(CommandRequest request) {
        String lang = request.getParameter("lang");
        if (lang != null) {
            LocaleManager.lookup().setCurrentLang(lang);
        }
    }

}




