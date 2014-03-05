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
package org.jboss.dashboard.ui;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.security.BackOfficePermission;
import org.jboss.dashboard.security.UIPermission;
import org.jboss.dashboard.ui.components.BeanHandler;
import org.jboss.dashboard.ui.config.ConfigurationTree;
import org.jboss.dashboard.ui.config.ConfigurationTreeStatus;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.config.Tree;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.TreeStatus;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.users.LogoutSurvivor;
import org.jboss.dashboard.users.UserStatus;
import org.hibernate.Session;
import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.slf4j.Logger;

import java.util.*;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Manager class that handles the user navigation. It is used to store in which workspace and page is located the user
 */
@SessionScoped
@Named("navigation_manager")
public class NavigationManager extends BeanHandler implements LogoutSurvivor {

    /**
     * Retrieves the NavigationManager instance for the current session
     */
    public static NavigationManager lookup() {
        return CDIBeanLocator.getBeanByType(NavigationManager.class);
    }

    @Inject
    private transient Logger log;

    public static final String WORKSPACE_ID = "workspace";
    public static final String PAGE_ID = "page";

    // Used to cache in request current workspace and page
    public static final String CURRENT_WORKSPACE_ATTR = "currentWorkspace";
    public static final String CURRENT_PAGE_ATTR = "currentPage";

    private String currentWorkspaceId;
    private Long currentSectionId;
    private boolean showingConfig = false;
    private boolean userRequiresLoginBackdoor = false;

    public boolean isUserRequiresLoginBackdoor() {
        return userRequiresLoginBackdoor;
    }

    public void setUserRequiresLoginBackdoor(boolean userRequiresLoginBackdoor) {
        this.userRequiresLoginBackdoor = userRequiresLoginBackdoor;
    }

    public boolean isShowingConfig() {
        return showingConfig;
    }

    public void setShowingConfig(boolean showingConfig) {
        this.showingConfig = showingConfig;
    }

    public UserStatus getUserStatus() {
        return UserStatus.lookup();
    }

    public String getCurrentWorkspaceId() {
        return currentWorkspaceId;
    }

    public Long getCurrentSectionId() {
        return currentSectionId;
    }

    protected void setCurrentSectionId(Long id) {
        this.currentSectionId = id;
    }

    public synchronized void setCurrentWorkspace(WorkspaceImpl workspace) {
        setCurrentWorkspaceId(workspace == null ? null : workspace.getDbid());
    }

    protected synchronized void setCurrentWorkspaceId(String workspaceId) {
        boolean workspaceChanged = false;
        if (workspaceId != null) {
            if (!workspaceId.equals(getCurrentWorkspaceId())) {
                pageLeft(doGetCurrentSection());
                currentWorkspaceId = workspaceId;
                setCurrentSectionId(null);
                repositionSection(); // Set a current section by default.
                if (!isValidUbication()) setCurrentSectionId(null);
                clearRequestCache();
                workspaceChanged = true;
            }
        } else {
            pageLeft(doGetCurrentSection());
            currentWorkspaceId = null;
            setCurrentSectionId(null);
            clearRequestCache();
            workspaceChanged = true;
        }
        if (workspaceChanged) {
            getUserStatus().invalidateUserPrincipals();
        }
    }

    public boolean userIsAdminInCurrentWorkspace() {
        if (getUserStatus().isRootUser()) return true;
        Workspace currentWorkspace = getCurrentWorkspace();
        if (currentWorkspace == null) return false;

        WorkspacePermission workspacePerm = WorkspacePermission.newInstance(currentWorkspace, WorkspacePermission.ACTION_ADMIN);
        return getUserStatus().hasPermission(workspacePerm);

    }

    public boolean isAdminBarVisible() {
        if (getUserStatus().isRootUser()) return true;

        Workspace currentWorkspace = getCurrentWorkspace();
        if (currentWorkspace == null) return false;

        UserStatus us = getUserStatus();
        UIPermission perm = WorkspacePermission.newInstance(currentWorkspace, WorkspacePermission.ACTION_ADMIN);
        if (us.hasPermission(perm)) return true;

        perm = WorkspacePermission.newInstance(currentWorkspace, WorkspacePermission.ACTION_CREATE_PAGE);
        if (us.hasPermission(perm)) return true;

        perm = WorkspacePermission.newInstance(currentWorkspace, WorkspacePermission.ACTION_EDIT);
        if (us.hasPermission(perm)) return true;

        perm = WorkspacePermission.newInstance(currentWorkspace, WorkspacePermission.ACTION_DELETE);
        if (us.hasPermission(perm)) return true;

        perm = BackOfficePermission.newInstance(currentWorkspace, BackOfficePermission.ACTION_CREATE_WORKSPACE);
        if (us.hasPermission(perm)) return true;

        perm  = BackOfficePermission.newInstance(currentWorkspace, BackOfficePermission.ACTION_USE_GRAPHIC_RESOURCES);
        if (us.hasPermission(perm)) return true;

        perm = BackOfficePermission.newInstance(currentWorkspace, BackOfficePermission.ACTION_USE_PERMISSIONS);
        if (us.hasPermission(perm)) return true;

        return false;
    }

    public synchronized void setCurrentSection(Section section) {
        if (section != null) {
            if (section.getId() != null && !section.getDbid().equals(getCurrentSectionId())) {
                pageLeft(doGetCurrentSection());
                setCurrentWorkspace(section.getWorkspace());
            }
            setCurrentSectionId(section.getDbid());
        } else {
            pageLeft(doGetCurrentSection());
            setCurrentSectionId(null);
            currentWorkspaceId = null;
        }
        clearRequestCache();
        //logCurrentStatus("setCurrentSection");
    }

    protected void pageLeft(Section oldSection) {
        if (oldSection != null) {
            if (log.isDebugEnabled())
                log.debug("Invalidating panelSessions in section " + oldSection.getId() + " from workspace " + oldSection.getWorkspace().getId());
            for (Panel panel : oldSection.getPanels()) {
                panel.pageLeft();
            }
        }
    }

    private void logCurrentStatus(String method) {
        try {
            log.info(method + " set currentWorkspaceId = " + currentWorkspaceId + " (" +
                    (
                            currentWorkspaceId != null ?
                                    LocaleManager.lookup().localize(UIServices.lookup().getWorkspacesManager().getWorkspace(currentWorkspaceId).getName()) : ""
                    )
                    + ") currentSectionId = " + getCurrentSectionId() + "(" +
                    (
                            getCurrentSectionId() != null ? LocaleManager.lookup().localize(doGetCurrentSection().getTitle()) : ""
                    )
                    + ")"
            );
            log.info("From... ", new Exception());
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    protected void clearRequestCache() {
        RequestContext rqctx = RequestContext.lookup();
        rqctx.getRequest().getRequestObject().removeAttribute(CURRENT_WORKSPACE_ATTR);
        rqctx.getRequest().getRequestObject().removeAttribute(CURRENT_PAGE_ATTR);
    }

    protected WorkspaceImpl getCurrentWorkspaceFromCache() {
        RequestContext rqctx = RequestContext.lookup();
        WorkspaceImpl currentWorkspace = (WorkspaceImpl) rqctx.getRequest().getRequestObject().getAttribute(CURRENT_WORKSPACE_ATTR);
        return currentWorkspace;
    }

    protected Section getCurrentPageFromCache() {
        RequestContext rqctx = RequestContext.lookup();
        Section currentPage = (Section) rqctx.getRequest().getRequestObject().getAttribute(CURRENT_PAGE_ATTR);
        return currentPage;
    }

    /**
     * Performance improvement that fixes current navigation status, ensuring it won't change
     * until the end of the request. Current workspace and page are stored as request attributes, and
     * subsequent calls to getCurrentWorkspace and getCurrentSection get the cached version.
     * Called just before rendering the page, as rendering shouldn't
     * modify navigation location
     */
    public void freezeNavigationStatus() {
        RequestContext rqctx = RequestContext.lookup();
        rqctx.getRequest().getRequestObject().setAttribute(CURRENT_WORKSPACE_ATTR, getCurrentWorkspace());
        rqctx.getRequest().getRequestObject().setAttribute(CURRENT_PAGE_ATTR, getCurrentSection());
    }


    /**
     * Get the current workspace, repositioning the navigation in case it is not correct.
     *
     * @return current workspace after checking ubication is correct
     */
    public synchronized WorkspaceImpl getCurrentWorkspace() {
        WorkspaceImpl currentWorkspace = getCurrentWorkspaceFromCache();
        if (currentWorkspace != null) return currentWorkspace;
        if (!isValidUbication()) reposition();
        currentWorkspace = doGetCurrentWorkspace();
        return currentWorkspace;
    }

    /**
     * Get the current page, repositioning the navigation in case it is not correct.
     *
     * @return current page after checking ubication is correct
     */
    public synchronized Section getCurrentSection() {
        // First check in request cache
        Section currentSection = getCurrentPageFromCache();
        if (currentSection != null) return currentSection;
        if (!isValidUbication()) reposition();
        currentSection = doGetCurrentSection();
        return currentSection;
    }

    public WorkspaceImpl doGetCurrentWorkspace() {
        try {
            if (currentWorkspaceId != null) {
                return (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(currentWorkspaceId);
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    public Section doGetCurrentSection() {
        try {
            if (getCurrentSectionId() == null) return null;
            final Section[] sectionToReturn = new Section[] {null};
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                sectionToReturn[0] = (Section) session.get(Section.class, getCurrentSectionId());
            }}.execute();
            return sectionToReturn[0];
        } catch (Exception e) {
            log.error("Error: ", e);
            return null;
        }
    }


    protected boolean isValidUbication() {
        if (userRequiresLoginBackdoor) return true;
        if (getCurrentWorkspaceId() == null) return false;

        WorkspacePermission workspacePerm = WorkspacePermission.newInstance(doGetCurrentWorkspace(), WorkspacePermission.ACTION_LOGIN);
        if (!getUserStatus().hasPermission(workspacePerm)) return false;

        if (getCurrentSectionId() != null) {
            SectionPermission sectionPerm = SectionPermission.newInstance(doGetCurrentSection(), SectionPermission.ACTION_VIEW);
            if (!getUserStatus().hasPermission(sectionPerm)) return false;
        } else {
            // Config is valid when there is no current section, but user can admin workspace.
            WorkspacePermission adminWorkspacePerm = WorkspacePermission.newInstance(doGetCurrentWorkspace(), WorkspacePermission.ACTION_ADMIN);
            if (!getUserStatus().hasPermission(adminWorkspacePerm)) return false;
        }

        // All security checks are met.
        return true;
    }

    /**
     * Calculate new ubication
     */
    protected void reposition() {
        try {
            List workspaceIds = getSortedWorkspacesList();
            for (Iterator iterator = workspaceIds.iterator(); iterator.hasNext();) {
                currentWorkspaceId = (String) iterator.next();
                repositionSection();
                if (isValidUbication()) return;
            }
            // We've tried all workspaces and sections without success. Try all workspaces without sections, in case admin is logging in.
            for (Iterator iterator = workspaceIds.iterator(); iterator.hasNext();) {
                currentWorkspaceId = (String) iterator.next();
                clearRequestCache();
                if (isValidUbication()) return;
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        // We've tried all. No valid ubication. Show login page.
        currentWorkspaceId = null;
        setCurrentSectionId(null);
        clearRequestCache();
        log.warn("Couldn't reposition navigation to a valid ubication.");
    }

    protected void repositionSection() {
        WorkspaceImpl workspace = doGetCurrentWorkspace();
        Section[] pages = workspace.getAllRootSections();

        // First search root sections in order
        for (int i = 0; i < pages.length; i++) {
            Section page = pages[i];
            setCurrentSectionId(page.getDbid());
            clearRequestCache();
            if (isValidUbication()) return;
        }
        // Then, the rest of sections
        pages = workspace.getAllSections();
        for (int i = 0; i < pages.length; i++) {
            Section page = pages[i];
            if (!page.isRoot()) {
                setCurrentSectionId(page.getId());
                clearRequestCache();
                if (isValidUbication()) return;
            }
        }
    }

    protected List getSortedWorkspacesList() throws Exception {
        // Order workspaces as follows: current workspace, default workspace, other sorted by id on order to make home search algorithm determinist.
        Set availableWorkspaces = new TreeSet(UIServices.lookup().getWorkspacesManager().getAllWorkspacesIdentifiers());
        List workspaceIds = new ArrayList(availableWorkspaces.size());
        if (currentWorkspaceId != null) {
            availableWorkspaces.remove(currentWorkspaceId);
            workspaceIds.add(currentWorkspaceId);
        }
        Workspace defaultWorkspace = UIServices.lookup().getWorkspacesManager().getDefaultWorkspace();
        if (defaultWorkspace != null) {
            availableWorkspaces.remove(defaultWorkspace.getId());
            workspaceIds.add(defaultWorkspace.getId());
        }
        workspaceIds.addAll(availableWorkspaces);
        return workspaceIds;
    }

    // ACTIONS

    /**
     * Cancels config mode
     */
    public void actionCancelConfig(CommandRequest request) throws Exception {
        setShowingConfig(false);
    }

    public void actionNavigateToWorkspace(CommandRequest request) throws Exception {
        String workspaceId = request.getParameter(WORKSPACE_ID);
        if (workspaceId != null) {
            setCurrentWorkspaceId(workspaceId);
        }
    }

    public void actionNavigateToPage(CommandRequest request) throws Exception {
        String workspaceId = request.getParameter(WORKSPACE_ID);
        String pageId = request.getParameter(PAGE_ID);
        if (workspaceId != null && pageId != null) {
            WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
            if (workspace != null) {
                setCurrentSection(workspace.getSection(Long.decode(pageId)));
            }
        }
    }

    /**
     * Go to config page
     */
    public void actionConfig(CommandRequest request) throws Exception {
        setShowingConfig(true);
        Tree tree = CDIBeanLocator.getBeanByType(ConfigurationTree.class);
        TreeStatus treeStatus = CDIBeanLocator.getBeanByType(ConfigurationTreeStatus.class);
        TreeNode node = treeStatus.getLastEditedNode(tree);
        if (node != null) node.onEdit();
    }

    /**
     * Determine if it is possible to navigate to given url for current user.
     *
     * @param url Url to go
     * @return true if it is possible to navigate to given url for current user.
     */
    public boolean isLocationReadable(String url) {
        NavigationPoint navigationPoint = new NavigationPoint(url);
        try {
            return isLocationReadable(navigationPoint);
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
    }

    /**
     * Determine if it is possible to navigate to given point for current user.
     *
     * @param navigationPoint point to go
     * @return true if it is possible to navigate to given point for current user.
     */
    public boolean isLocationReadable(NavigationPoint navigationPoint) throws Exception {
        Workspace workspace = navigationPoint.getWorkspace();
        if (workspace != null) {
            WorkspacePermission workspacePerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_LOGIN);
            if (!getUserStatus().hasPermission(workspacePerm)) {
                // No access permission in workspace
                return false;
            }
            Section section = navigationPoint.getPage();
            if (section != null) {
                SectionPermission sectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_VIEW);
                if (!getUserStatus().hasPermission(sectionPerm)) {
                    // No access permission in page
                    return false;
                }
                Panel panel = navigationPoint.getPanel();
                String actionName = navigationPoint.getActionName();
                if (panel != null) {
                    PanelPermission panelPerm = PanelPermission.newInstance(panel, PanelPermission.ACTION_VIEW);
                    if (!getUserStatus().hasPermission(panelPerm)) {
                        // No view permission in panel
                        return false;
                    }
                    if (!StringUtils.isEmpty(actionName)) {
                        // Can I invoke the action?
                        return panel.getProvider().getDriver().canInvokeAction(panel, actionName);
                    } else {
                        // No action? strange anyway
                        return true;
                    }
                } else {
                    // No panel...
                    return true;
                }
            } else {
                // Page doesn't exist !!!
                return false;
            }
        } else {
            // Workspace doesn't exist !!!
            return false;
        }
    }
}
