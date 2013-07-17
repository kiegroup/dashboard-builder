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
package org.jboss.dashboard.ui.config.components.panels;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.components.HandlerFactoryElement;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.resources.Layout;
import org.hibernate.Session;
import java.io.File;

public class PanelsPropertiesHandler extends HandlerFactoryElement {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelsPropertiesHandler.class.getName());

    private String workspaceId;
    private Long sectionId;
    private Long instanceId;
    private Boolean newInstance;
    private String provider;
    private String title;
    private String region;
    public static final String PARAM_NO_SECTION = "noSection";

    public Boolean isNewInstance() {
        return newInstance;
    }

    public void setIsNewInstance(Boolean newInstance) {
        this.newInstance = newInstance;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setWorkspace(Workspace p) {
        workspaceId = p.getId();
    }

    public Workspace getWorkspace() throws Exception {
        return UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getWorkspaceId() throws Exception {
        return this.workspaceId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public void actionDeletePanel(final CommandRequest request) {
        try {
            HibernateTxFragment txFragment = new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    final WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();

                    Long dbid = Long.decode(request.getParameter("dbid"));
                    Long sectionId = request.getParameter("sectionId").equals(PARAM_NO_SECTION) ? null : Long.decode(request.getParameter("sectionId"));

                    if (workspace == null) {
                        log.error("Error getting workspace");
                        return;
                    }

                    final Section section;
                    if (sectionId == null)
                        section = ((WorkspaceImpl) getWorkspace()).getSection(getSectionId());
                    else
                        section = workspace.getSection(sectionId);

                    if (section == null) {
                        log.error("Error getting panel section");
                        return;
                    }

                    Panel panel = null;
                    Panel[] panels = section.getAllPanels();

                    for (int i = 0; i < panels.length; i++) {
                        if (panels[i].getDbid().equals(dbid)) panel = panels[i];
                    }
                    section.removePanel(panel);
                    SessionManager.setCurrentPanel(panel);

                    UIServices.lookup().getPanelsManager().delete(panel);
                    //section.removePanel(panel);
                    UIServices.lookup().getSectionsManager().store(section);
                }
            };

            txFragment.execute();
        } catch (Exception e) {
            PanelsPropertiesHandler.log.error("Error: " + e.getMessage());
        }
    }

    public void actionCreatePanel(final CommandRequest request) throws Exception {
        //final Section newSection = new Section();
        if (request.getParameter("title") != null) title = request.getParameter("title");

        if (request.getParameter("instanceId") != null && !"".equals(request.getParameter("instanceId"))) {
            PanelInstance panelInstance = ((WorkspaceImpl) getWorkspace()).getPanelInstance(request.getParameter("instanceId"));
            title = panelInstance.getTitle(LocaleManager.lookup().getCurrentLang());
        }

        String txtNewInstance = request.getParameter("switchForm");
        if (txtNewInstance.equals("newInstance")) {
            this.setIsNewInstance(Boolean.TRUE);
        } else if (txtNewInstance.equals("newPanel")) {
            this.setIsNewInstance(Boolean.FALSE);
        } else if (txtNewInstance.equals("create")) {
            validate();
            if (getFieldErrors().isEmpty()) {
                try {

                    HibernateTxFragment txFragment = new HibernateTxFragment() {
                        protected void txFragment(Session session) throws Exception {
                            Section section = ((WorkspaceImpl) getWorkspace()).getSection(getSectionId());
                            String regionId = getRegion();
                            Long instanceId = new Long(request.getParameter("instanceId"));

                            Panel newPanel = new Panel();
                            newPanel.setInstanceId(instanceId);
                            newPanel.setSection(section);
                            UIServices.lookup().getPanelsManager().store(newPanel);

                            Layout layout = section.getLayout();
                            if (layout != null) newPanel.getProvider().getDriver().fireBeforePanelPlacedInRegion(newPanel, layout.getRegion(regionId));

                            // Assign panel & save changes
                            section.assignPanel(newPanel, regionId);
                            UIServices.lookup().getSectionsManager().store(section);
                            newPanel.getProvider().getDriver().fireAfterPanelPlacedInRegion(newPanel, null);
                        }
                    };

                    txFragment.execute();
                    setProvider("");
                    setRegion("");
                } catch (Exception e) {
                    PanelsPropertiesHandler.log.error("Error: " + e.getMessage());
                }
            }
        }
    }

    public void actionCreatePanelInstance(final CommandRequest request) throws Exception {

        String txtNewInstance = request.getParameter("switchForm");
        if (txtNewInstance.equals("newInstance")) {
            this.setIsNewInstance(new Boolean(true));
        } else if (txtNewInstance.equals("newPanel")) {
            this.setIsNewInstance(new Boolean(false));
        } else if (txtNewInstance.equals("create")) {

            final WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
            final Section section = ((WorkspaceImpl) getWorkspace()).getSection(getSectionId());
            String providerName = getProvider();
            String title = getTitle();

            PanelProvider provider = UIServices.lookup().getPanelsProvidersManager().getProvider(providerName);
            validate();

            if (getFieldErrors().isEmpty()) {
                try {
                    PanelInstance instance = new PanelInstance();
                    instance.setWorkspace(workspace);

                    final PanelInstance newInstance = instance;
                    newInstance.setProvider(provider);
                    newInstance.setTitle(title, LocaleManager.lookup().getDefaultLang());
                    newInstance.setWorkspace(workspace);

                    HibernateTxFragment txFragment = new HibernateTxFragment() {
                        protected void txFragment(Session session) throws Exception {

                            // Add instance to workspace
                            workspace.addPanelInstance(newInstance);
                            UIServices.lookup().getPanelsManager().store(newInstance);

                            // Initialize panel (after creation)
                            newInstance.init();
                            // Save changes
                            UIServices.lookup().getWorkspacesManager().store(workspace);

                            final Panel newPanel = new Panel();
                            newPanel.setInstance(newInstance);
                            newPanel.setSection(section);
                            Layout layout = section.getLayout();
                            if (layout != null) newPanel.getProvider().getDriver().fireBeforePanelPlacedInRegion(newPanel, layout.getRegion(getRegion()));
                            newPanel.setLayoutRegionId(getRegion());

                            UIServices.lookup().getPanelsManager().store(newPanel);
                            // Assign panel & save changes
                            section.assignPanel(newPanel, getRegion());

                            //TODO Store position of the panel in the region.

                            UIServices.lookup().getSectionsManager().store(section);
                            newPanel.getProvider().getDriver().fireAfterPanelPlacedInRegion(newPanel, null);
                        }
                    };

                    txFragment.execute();
                    setIsNewInstance(new Boolean(false));
                    setProvider("");
                    setTitle("");
                    setRegion("");
                } catch (Exception e) {
                    PanelsPropertiesHandler.log.error("Error: " + e.getMessage());
                }
            }
        }
    }

    protected void validate() {

        if (title == null || "".equals(title)) {
            addFieldError(new FactoryURL(getComponentName(), "title"), null, title);
        }
        /*if(!isValidURL(url)){
            addFieldError(new FactoryURL(getComponentName(), "url"), null, url);
        } */

    }


    protected boolean isValidURL(String url) {
        if (url == null || "".equals(url))
            return true;
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.";
        for (int i = 0; i < url.length(); i++)
            if (validChars.indexOf(Character.toUpperCase(url.charAt(i))) == -1)
                return false;
        //Chars are valid

        if (new File(Application.lookup().getBaseAppDirectory() + "/" + url).exists())
            return false;

        //No file or directory exists in root with same name
        try {
            Workspace p = UIServices.lookup().getWorkspacesManager().getWorkspaceByUrl(url);
            if (p == null) return true;//No workspace with same url exists.
            WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
            if (workspace.getId().equals(p.getId())) return true;//It is my own workspace
        } catch (Exception e) {
            PanelsPropertiesHandler.log.error("Error getting workspace", e);
        }
        return false;
    }

    public void setDefaultValues() {
        title = null;
    }
}
