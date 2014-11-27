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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.resources.Layout;
import org.hibernate.Session;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;

import java.util.Locale;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PanelsHandler extends BeanHandler {

    @Inject
    private transient Logger log;

    /**
     * Moves a panel to the specified region
     *
     * @param request
     * @return
     * @throws Exception
     */
    public void actionMoveToRegion(final CommandRequest request) throws Exception {

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Section section = NavigationManager.lookup().getCurrentSection();
                if (section != null) {
                    log.debug("Moving panel to region");
                    String sposition = request.getParameter("position");
                    int position = -1;
                    try {
                        position = Integer.parseInt(sposition);
                    } catch (Exception e) {
                    }
                    String panelId = request.getParameter("panelId");
                    Panel panel = section.getPanel(panelId);

                    // Check region
                    String region = request.getParameter("region");
                    log.debug("Moving panel " + panelId + " to region " + region + ", position " + position);
                    String currentPanelRegion = null;
                    if (panel != null && panel.getRegion() != null)
                        currentPanelRegion = panel.getRegion().getId();
                    boolean sectionUpdated = false;
                    if (panel != null && region != null && !region.equals(currentPanelRegion)) {
                        panel.getProvider().getDriver().move(panel, region);
                        sectionUpdated = true;
                    }
                    if (position != -1) {
                        SectionRegion sr = panel.getSection().getSectionRegion(region);
                        int maxLoops = 30;
                        while (panel.getPosition() != position && (maxLoops--) > 0) {
                            if (panel.getPosition() > position)
                                sr.moveBackInRegion(panel);
                            else
                                sr.moveForwardInRegion(panel);
                            sectionUpdated = true;
                        }
                    }
                    if (sectionUpdated)
                        UIServices.lookup().getSectionsManager().store(section);
                }
            }
        };

        txFragment.execute();
    }


    /**
     * Shows the screen for a new panel creation
     *
     * @param request
     * @return
     * @throws Exception
     */
    public void actionCreatePanel(final CommandRequest request) throws Exception {
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Section section = NavigationManager.lookup().getCurrentWorkspace().getSection((Long) request.getSessionObject().getAttribute("editing_section"));
                String regionId = (String) request.getSessionObject().getAttribute("editing_region");
                String instanceId = request.getParameter("id");

                Panel newPanel = new Panel();
                newPanel.setInstanceId(new Long(instanceId));
                newPanel.setSection(section);
                UIServices.lookup().getPanelsManager().store(newPanel);

                Layout layout = section.getLayout();
                if (layout != null)
                    newPanel.getProvider().getDriver().fireBeforePanelPlacedInRegion(newPanel, layout.getRegion(regionId));

                // Assign panel & save changes
                section.assignPanel(newPanel, regionId);
                UIServices.lookup().getSectionsManager().store(section);
                newPanel.getProvider().getDriver().fireAfterPanelPlacedInRegion(newPanel, null);
            }
        };

        txFragment.execute();
    }

    /**
     * Create a panel of given instance in given region.
     *
     * @param request
     * @return
     * @throws Exception
     */
    public void actionPutInstanceToRegion(final CommandRequest request) throws Exception {
        String id = request.getParameter("panelId");
        final String region = request.getParameter("region");
        String sposition = request.getParameter("position");
        int iposition = -1;
        try {
            iposition = Integer.parseInt(sposition);
        } catch (Exception e) {
        }
        final int position = iposition;

        log.debug("Putting PanelInstance " + id + " to region " + region + ", position " + position);

        final WorkspaceImpl workspace = NavigationManager.lookup().getCurrentWorkspace();
        final Section section = NavigationManager.lookup().getCurrentSection();
        final PanelProvider provider = UIServices.lookup().getPanelsProvidersManager().getProvider(id != null ? id.trim() : null);
        if (provider == null) { //Create new panel, instance is given in id
            final PanelInstance instance = workspace.getPanelInstance(id);
            if (instance != null) {
                final Panel newPanel = new Panel();

                HibernateTxFragment txFragment = new HibernateTxFragment() {
                    protected void txFragment(Session session) throws Exception {
                        newPanel.setInstance(instance);
                        newPanel.setSection(section);
                        Layout layout = section.getLayout();
                        if (layout != null)
                            newPanel.getProvider().getDriver().fireBeforePanelPlacedInRegion(newPanel, layout.getRegion(region));
                        newPanel.setLayoutRegionId(region);
                        UIServices.lookup().getPanelsManager().store(newPanel);
                        // Assign panel & save changes
                        section.assignPanel(newPanel, region);
                        if (position != -1) {
                            SectionRegion sr = newPanel.getSection().getSectionRegion(region);
                            int maxLoops = 30;
                            while (newPanel.getPosition() != position && (maxLoops--) > 0) {
                                if (newPanel.getPosition() > position)
                                    sr.moveBackInRegion(newPanel);
                                else
                                    sr.moveForwardInRegion(newPanel);
                            }
                        }

                        UIServices.lookup().getSectionsManager().store(section);
                        newPanel.getProvider().getDriver().fireAfterPanelPlacedInRegion(newPanel, null);
                        hideModalDialog();
                    }
                };

                txFragment.execute();
            } else {
                log.error("Instance with id <" + id + "> not found in current workspace. Cannot put panel to region " + region);
                log.error("Also, provider with id <" + id + "> is not found in current installation.");
            }
        } else {//Create new instance and new panel, provider is given in id.
            final PanelInstance newInstance = new PanelInstance();
            HibernateTxFragment txFragment = new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {

                    newInstance.setProvider(provider);
                    newInstance.setWorkspace(workspace);
                    String[] langs = LocaleManager.lookup().getPlatformAvailableLangs();
                    for (int i = 0; i < langs.length; i++) {
                        String lang = langs[i];
                        String panelTitle = provider.getResource(provider.getDescription(), new Locale(lang));
                        if (panelTitle != null)
                            newInstance.setTitle(StringEscapeUtils.unescapeHtml(panelTitle), lang);
                    }
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
                    if (layout != null)
                        newPanel.getProvider().getDriver().fireBeforePanelPlacedInRegion(newPanel, layout.getRegion(region));
                    newPanel.setLayoutRegionId(region);

                    UIServices.lookup().getPanelsManager().store(newPanel);
                    // Assign panel & save changes
                    section.assignPanel(newPanel, region);
                    if (position != -1) {
                        SectionRegion sr = newPanel.getSection().getSectionRegion(region);
                        int maxLoops = 30;
                        while (newPanel.getPosition() != position && (maxLoops--) > 0) {
                            if (newPanel.getPosition() > position)
                                sr.moveBackInRegion(newPanel);
                            else
                                sr.moveForwardInRegion(newPanel);
                        }
                    }
                    UIServices.lookup().getSectionsManager().store(section);
                    newPanel.getProvider().getDriver().fireAfterPanelPlacedInRegion(newPanel, null);

                    hideModalDialog();
                }
            };

            txFragment.execute();
        }
    }

    public void hideModalDialog() {
        ModalDialogComponent mdc = getModalDialogComponent();
        if (mdc.isShowing()) mdc.hide();
    }

    public ModalDialogComponent getModalDialogComponent() {
        return CDIBeanLocator.getBeanByType(ModalDialogComponent.class);
    }
}
