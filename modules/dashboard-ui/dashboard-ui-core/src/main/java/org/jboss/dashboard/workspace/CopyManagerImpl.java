/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.workspace;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.security.*;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.workspace.copyoptions.CopyOption;
import org.jboss.dashboard.workspace.copyoptions.SectionCopyOption;
import org.jboss.dashboard.workspace.events.EventConstants;
import org.jboss.dashboard.workspace.events.WorkspaceDuplicationEvent;
import org.jboss.dashboard.workspace.events.WorkspaceListener;

import javax.enterprise.context.ApplicationScoped;
import java.lang.reflect.Method;
import java.security.Permission;
import java.security.Principal;
import java.util.*;

@ApplicationScoped
public class CopyManagerImpl implements CopyManager {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CopyManagerImpl.class.getName());

    public PanelInstance copy(final PanelInstance panelInstance, final WorkspaceImpl workspace) throws Exception {

        final PanelInstance[] results = new PanelInstance[]{null};
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                log.debug("Copying PanelInstance " + panelInstance.getId() + " to Workspace " + workspace.getId());
                PanelInstance panelInstanceCopy = (PanelInstance) panelInstance.clone();
                panelInstanceCopy.setWorkspace(workspace);
                if (workspace.getId().equals(panelInstance.getWorkspace().getId())) {//Instance in same workspace-> can't reuse the id
                    panelInstanceCopy.setInstanceId(null);
                }

                // Store panel instance.
                if (log.isDebugEnabled()) log.debug("Storing panelInstance copy " + panelInstanceCopy);
                UIServices.lookup().getPanelsManager().store(panelInstanceCopy);

                log.debug("Setting PanelInstance parameters.");
                Set<PanelParameter> panelParams = panelInstance.getPanelParams();
                if (panelParams != null) {
                    Set<PanelParameter> paramsClone = new HashSet<PanelParameter>();
                    for (PanelParameter param : panelParams) {
                        log.debug("Copying parameter " + param);
                        PanelParameter paramClone = (PanelParameter) param.clone();
                        paramClone.setPanelInstance(panelInstanceCopy);
                        paramsClone.add(paramClone);
                    }
                    panelInstanceCopy.setPanelParams(paramsClone);
                }

                // Store panel instance with parameters.
                log.debug("Storing panelInstance copy with parameters");
                UIServices.lookup().getPanelsManager().store(panelInstanceCopy);

                panelInstanceCopy.init();

                copyPermissions(panelInstance, panelInstanceCopy);
                copyResources(panelInstance, panelInstanceCopy);

                //Internal data
                PanelDriver driver = panelInstance.getProvider().getDriver();
                try {
                    driver.replicateData(panelInstance, panelInstanceCopy);
                } catch (Exception e) {
                    log.warn("Cannot replicate data for PanelInstance " + panelInstance.getId() + ". Method replicateData(PanelInstance src, PanelInstance des), throwed exception.", e);
                    //TODO: Think of a way to inform the cloner which panels failed to copy, and prompt to accept the clonated workspace or delete it.
                }
                workspace.addPanelInstance(panelInstanceCopy);
                UIServices.lookup().getWorkspacesManager().store(workspace);
                log.debug("PanelInstance with id " + panelInstance.getId() + " has been copied to id " + panelInstanceCopy.getId());
                results[0] = panelInstanceCopy;
            }
        };

        txFragment.execute();
        return results[0];
    }

    public Panel copy(final Panel panel, final Section section, final LayoutRegion region) throws Exception {
        return copy(panel, section, region, panel.getInstance());
    }

    public Panel copy(final Panel panel, final Section section, final LayoutRegion region, final PanelInstance instance) throws Exception {
        log.debug("Copying panel " + panel.getPanelId());

        final Panel[] results = new Panel[]{null};
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Section panelSection = section;
                LayoutRegion panelRegion = region;
                if (section == null) panelSection = panel.getSection();//Same section
                if (region == null) panelRegion = panel.getRegion(); //Same region

                // Clone panel
                Panel panelCopy = (Panel) panel.clone();
                panelCopy.setSection(panelSection);
                panelCopy.setInstanceId(instance.getInstanceId());
                panelCopy.getProvider().getDriver().fireBeforePanelPlacedInRegion(panelCopy, panelRegion);
                if (region != null) panelCopy.setLayoutRegionId(panelRegion.getId());

                if (section.getId().equals(panel.getSection().getId()) && section.getWorkspace().getId().equals(panel.getWorkspace().getId())) {//Panel in same section -> can't reuse the id
                    panelCopy.setPanelId(null);
                }

                // Store the panel in order to ensure it an id is generated for it before assign the panel to the section.
                UIServices.lookup().getPanelsManager().store(panelCopy);

                // Add panel to section
                panelSection.assignPanel(panelCopy, panelRegion);
                UIServices.lookup().getSectionsManager().store(section);
                panelCopy.getProvider().getDriver().fireAfterPanelPlacedInRegion(panelCopy, null);
                log.debug("Panel " + panel.getPanelId() + " was copied to panel " + panelCopy.getPanelId());
                results[0] = panelCopy;

                //Copy resources
                copyResources(panel, panelCopy);
            }
        };

        txFragment.execute();
        return results[0];
    }

    public Section copy(final Section section, final WorkspaceImpl workspace, final SectionCopyOption sco) throws Exception {


        final Section[] results = new Section[]{null};
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                SectionCopyOption copyOption = sco;
                log.debug("Copying section " + section.getId() + " to Workspace " + workspace.getId());
                if (copyOption == null) {
                    if (workspace.getId().equals(section.getWorkspace().getId()))
                        copyOption = CopyOption.DEFAULT_SECTION_COPY_OPTION_SAME_WORKSPACE;
                    else
                        copyOption = CopyOption.DEFAULT_SECTION_COPY_OPTION_OTHER_WORKSPACE;
                }
                Section sectionCopy = (Section) section.clone();
                sectionCopy.setPosition(-1); //Let the workspace decide the position later
                sectionCopy.setWorkspace(workspace);
                boolean copyingToSameWorkspace = workspace.getId().equals(section.getWorkspace().getId());
                if (copyingToSameWorkspace) {//Section in same workspace-> can't reuse the id
                    sectionCopy.setId(null);
                } else {//Section in different workspace-> can reuse the url
                    sectionCopy.setFriendlyUrl(section.getFriendlyUrl());
                }
                if (log.isDebugEnabled())
                    log.debug("Storing basic section copy to workspace " + workspace.getId());
                UIServices.lookup().getSectionsManager().store(sectionCopy);

                // Add to destination workspace
                if (log.isDebugEnabled())
                    log.debug("Adding cloned section (" + sectionCopy.getId() + ") to workspace " + workspace.getId());
                workspace.addSection(sectionCopy);
                UIServices.lookup().getWorkspacesManager().store(workspace);

                //Resources
                copyResources(section, sectionCopy);

                // Panels inside section
                LayoutRegion[] regions = section.getLayout().getRegions();
                log.debug("Regions in section are " + Arrays.asList(regions));
                Map<String, PanelInstance> panelInstanceMappings = new HashMap<String, PanelInstance>();
                for (LayoutRegion region : regions) {
                    Panel[] panels = section.getPanels(region);
                    if (log.isDebugEnabled())
                        log.debug("Copying " + panels.length + " panels in region " + region);
                    for (int j = 0; panels != null && j < panels.length; j++) {
                        Panel panelClone = null;
                        PanelInstance instanceClone = panels[j].getInstance();
                        String panelInstanceId = panels[j].getInstanceId().toString();
                        if (copyOption.isDuplicatePanelInstance(panelInstanceId)) { //Duplicate Panel instance for this panel.
                            if (panelInstanceMappings.containsKey(panelInstanceId)) {
                                instanceClone = panelInstanceMappings.get(panelInstanceId);
                            } else {
                                instanceClone = copy(panels[j].getInstance(), workspace);
                                panelInstanceMappings.put(panelInstanceId, instanceClone);
                            }
                        }
                        panelClone = copy(panels[j], sectionCopy, null, instanceClone);
                        if (panelClone == null)
                            log.error("Panel " + panels[j].getPanelId() + " failed to copy itself to Section " + sectionCopy.getId());
                    }
                }

                copyPermissions(section, sectionCopy);
                session.flush();
                session.refresh(sectionCopy); // To fix bug 2011
                results[0] = sectionCopy;
            }
        };
        synchronized (("workspace_" + workspace.getDbid()).intern()) {
            txFragment.execute();
        }
        return results[0];
    }

    public WorkspaceImpl copy(final WorkspaceImpl workspace, final String id) throws Exception {

        final WorkspaceImpl[] results = new WorkspaceImpl[]{null};
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                log.debug("Generating copy for Workspace " + workspace.getId() + "->" + id);

                WorkspaceImpl workspaceClone = (WorkspaceImpl) workspace.clone();
                workspaceClone.setId(id);
                UIServices.lookup().getWorkspacesManager().addNewWorkspace(workspaceClone);

                // HOME PAGES
                copyHomePages(workspace, workspaceClone);

                // RESOURCES
                copyResources(workspace, workspaceClone);

                // PERMISSIONS
                log.debug("Setting permissions for " + id);
                copyPermissions(workspace, workspaceClone);

                // PANEL INSTANCES
                log.debug("Copying PanelInstances from Workspace " + workspace.getId() + " to Workspace " + id);
                log.debug("Setting panelInstances for " + id);
                for (PanelInstance pi : workspace.getPanelInstancesSet()) {
                    if (!pi.getWorkspace().getId().equals(workspace.getId())) {
                        log.error("Found a panel instance in workspace, whose workspace id is different!");
                        continue;
                    }

                    log.debug("Copying panelInstance " + pi.getId());
                    copy(pi, workspaceClone);
                }
                log.debug("Storing cloned workspace.");
                UIServices.lookup().getWorkspacesManager().store(workspaceClone);

                // SECTIONS
                log.debug("Copying sections from Workspace " + workspace.getId() + " to Workspace " + id);
                Section[] sections = workspace.getAllSections();
                for (Section originalSection : sections) {
                    Section s = copy(originalSection, workspaceClone, CopyOption.DEFAULT_SECTION_COPY_OPTION_SAME_WORKSPACE);
                    if (s == null)
                        log.error("Cannot copy section " + originalSection.getId());
                    else
                        log.debug("Section " + originalSection.getId() + " copied to " + s.getId());
                }

                // Register workspace
                log.debug("Storing cloned workspace.");
                UIServices.lookup().getWorkspacesManager().store(workspaceClone);
                results[0] = workspaceClone;

                //Fires the workspace duplicated event to all listeners
                List<WorkspaceListener> workspaceListeners = UIServices.lookup().getWorkspacesManager().getListeners(EventConstants.WORKSPACE_DUPLICATED);
                WorkspaceDuplicationEvent event = new WorkspaceDuplicationEvent(EventConstants.WORKSPACE_DUPLICATED, workspace, workspaceClone);
                log.debug("Firing event " + event);
                for (WorkspaceListener listener : workspaceListeners) {
                    try {
                        listener.workspaceDuplicated(event);
                    } catch (Exception e) {
                        log.warn("Error firing event.", e);
                    }
                }
            }
        };

        txFragment.execute();
        return results[0];
    }

    public WorkspaceImpl copy(final WorkspaceImpl workspace) throws Exception {

        final WorkspaceImpl[] results = new WorkspaceImpl[]{null};
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                String generatedId = UIServices.lookup().getWorkspacesManager().generateWorkspaceId();
                results[0] = copy(workspace, generatedId);
            }
        };

        txFragment.execute();
        return results[0];
    }

    // Internal copy methods


    protected void copyHomePages(Workspace workspace, Workspace workspaceClone) throws HibernateException {
        Set<WorkspaceHome> homePages = workspace.getWorkspaceHomes();
        if (homePages != null) {
            for (WorkspaceHome homePage : homePages) {
                WorkspaceHome newHomePage = new WorkspaceHome();
                newHomePage.setWorkspace(workspaceClone);
                newHomePage.setSectionId(homePage.getSectionId());
                newHomePage.setRoleId(homePage.getRoleId());
                workspaceClone.getWorkspaceHomes().add(newHomePage);
            }
        }
    }

    /**
     * Clones Workspace permissions. Copies own to given clone.
     *
     * @param workspace     Workspace whose permissions will be read
     * @param workspaceCopy Workspace whose permissions will be updated with the other ones
     */
    protected void copyPermissions(WorkspaceImpl workspace, WorkspaceImpl workspaceCopy) {
        try {
            log.debug("Copying workspace permissions from Workspace " + workspace.getId() + " to Workspace " + workspaceCopy.getId());

            // Copy WorkspacePermission defined for the given workspace.
            copyPermissions(workspace, workspaceCopy, WorkspacePermission.class, workspaceCopy);

            // Copy SectionPermission defined for all workspace's sections.
            copyPermissions(workspace, workspaceCopy, SectionPermission.class, workspaceCopy);

            // Copy PanelPermission defined for all workspace's panels.
            copyPermissions(workspace, workspaceCopy, PanelPermission.class, workspaceCopy);
        } catch (Exception e) {
            log.error("Processing workspace clone permissions", e);
        }
    }

    /**
     * Clones Panel permissions. Copies own to given clone.
     *
     * @param panelInstance     Panel whose permissions will be read
     * @param panelInstanceCopy Panel whose permissions will be updated with the other ones
     */
    protected void copyPermissions(PanelInstance panelInstance, PanelInstance panelInstanceCopy) {
        try {
            // Copy PanelPermission defined for the given panel.
            log.debug("Copying panel permissions from PanelInstance " + panelInstance.getId() + " to PanelInstance " + panelInstanceCopy.getId());
            copyPermissions(panelInstance, panelInstanceCopy, PanelPermission.class, panelInstanceCopy.getWorkspace());
        } catch (Exception e) {
            log.error("Cannot clone panel instance " + panelInstance.getId() + " permissions.", e);
        }
    }

    /**
     * Clones Section permissions. Copies own to given clone.
     *
     * @param section     Section whose permissions will be read
     * @param sectionCopy Section whose permissions will be updated with the other ones
     */
    protected void copyPermissions(Section section, Section sectionCopy) {
        try {
            // Permissions
            log.debug("Copying section permissions from Section " + section.getId() + " to Section " + sectionCopy.getId());
            copyPermissions(section, sectionCopy, SectionPermission.class, sectionCopy.getWorkspace());
        } catch (Exception e) {
            log.error("Cannot clone section " + section.getId() + " permissions.", e);
        }
    }

    /**
     * Copy permissions define for a given resource and assigns created copies to another resource.
     *
     * @param resource        The resource which permissions are going to be copied.
     * @param resourceCopy    The resource that is going to receive the copied permissions.
     * @param permissionClass The copy is restricted to the given permission class.
     * @param targetWorkspace    The workspace where copied resource will be stored.
     * @throws Exception
     */
    protected void copyPermissions(Object resource, Object resourceCopy, Class<? extends Permission> permissionClass, Workspace targetWorkspace) throws Exception {

        // First retrieve permissions (only permissionClass instances) assigned to resource.
        Policy securityPolicy = SecurityServices.lookup().getSecurityPolicy();
        Map<Principal, Permission> permissionMap = securityPolicy.getPermissions(resource, permissionClass);

        for (Principal principal : permissionMap.keySet()) {
            // Copy permission
            DefaultPermission permission = (DefaultPermission) permissionMap.get(principal);
            Method getInstance = permissionClass.getMethod("getInstance", new Class<?>[]{Principal.class, Object.class});
            DefaultPermission permissionCopy = (DefaultPermission) getInstance.invoke(permissionClass, new Object[]{principal, resourceCopy});
            List<String> actionList = (List) permissionClass.getField("LIST_OF_ACTIONS").get(permissionClass);
            for (String action : actionList) {
                if (permission.isActionDenied(action))
                    permissionCopy.denyAction(action);
                else if (permission.isActionGranted(action))
                    permissionCopy.grantAction(action);
                else if (permission.isActionUndefined(action)) permissionCopy.removeAction(action);
            }

            // Add permission to security policy if values for it have been specified.
            if (!permissionCopy.isEmpty()) {
                securityPolicy.addPermission(principal, permissionCopy);
                log.debug("Set permission for cloned workspace " + principal.getName() + " " + permissionCopy.toString());
            }
            // If permission is empty then ensure that is not registered into the security policy
            else {
                securityPolicy.removePermission(principal, permissionCopy);
                log.debug("Set permission for cloned workspace: " + principal.getName() + " " + permissionCopy.toString());
            }
        }
        // Make policy changes persistent
        securityPolicy.save();
    }

    protected void copyResources(Workspace workspace, Workspace workspaceClone) throws Exception {
        GraphicElementManager[] managers = UIServices.lookup().getGraphicElementManagers();
        for (GraphicElementManager manager : managers) {
            GraphicElement[] elements = manager.getElements(workspace.getId(), null, null);
            if (elements != null) {
                for (GraphicElement element : elements) {
                    GraphicElement elementClone = (GraphicElement) element.clone();
                    elementClone.setWorkspaceId(workspaceClone.getId());
                    manager.createOrUpdate(elementClone);
                }
            }
        }
    }

    protected void copyResources(Section section, Section sectionClone) throws Exception {
        GraphicElementManager[] managers = UIServices.lookup().getGraphicElementManagers();
        for (GraphicElementManager manager : managers) {
            GraphicElement[] elements = manager.getElements(section.getWorkspace().getId(), section.getId(), null);
            if (elements != null) {
                for (GraphicElement element : elements) {
                    GraphicElement elementClone = (GraphicElement) element.clone();
                    elementClone.setWorkspaceId(sectionClone.getWorkspace().getId());
                    elementClone.setSectionId(sectionClone.getId());
                    manager.createOrUpdate(elementClone);
                }
            }
        }
    }

    protected void copyResources(Panel panel, Panel panelClone) throws Exception {
        GraphicElementManager[] managers = UIServices.lookup().getGraphicElementManagers();
        for (GraphicElementManager manager : managers) {
            if (!manager.getElementScopeDescriptor().isAllowedPanel())
                continue;//Ignore manager, as it does not define elements for panels
            GraphicElement[] elements = manager.getElements(panel.getWorkspace().getId(), panel.getSection().getId(), panel.getPanelId());
            if (elements != null) {
                for (GraphicElement element : elements) {
                    GraphicElement elementClone = (GraphicElement) element.clone();
                    elementClone.setWorkspaceId(panelClone.getWorkspace().getId());
                    elementClone.setSectionId(panelClone.getSection().getId());
                    elementClone.setPanelId(panel.getPanelId());
                    manager.createOrUpdate(elementClone);
                }
            }
        }
    }

    protected void copyResources(PanelInstance panel, PanelInstance panelClone) throws Exception {
        GraphicElementManager[] managers = UIServices.lookup().getGraphicElementManagers();
        for (GraphicElementManager manager : managers) {
            if (!manager.getElementScopeDescriptor().isAllowedInstance())
                continue;//Ignore manager, as it does not define elements for panel instances
            GraphicElement[] elements = manager.getElements(panel.getWorkspace().getId(), null, panel.getInstanceId());
            if (elements != null) {
                for (GraphicElement element : elements) {
                    GraphicElement elementClone = (GraphicElement) element.clone();
                    elementClone.setWorkspaceId(panelClone.getWorkspace().getId());
                    elementClone.setSectionId(null);
                    elementClone.setPanelId(panel.getInstanceId());
                    manager.createOrUpdate(elementClone);
                }
            }
        }
    }
}
