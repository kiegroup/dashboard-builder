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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelSession;
import org.jboss.dashboard.workspace.SectionRegion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * This class extends Formatter to provide support for the rendering of a panel header.
 */
public class RenderPanelHeaderFormatter extends Formatter {

    private Panel panel = null;

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        panel = (Panel) getParameter("panel");
        Boolean administratorMode = (Boolean) getParameter("administratorMode");
        if (panel != null) {
            boolean tabbedMode = panel.getRegion().isTabbedRegion();
            if (panel != null && panel.getRegion() != null) {
                if (tabbedMode)
                    serviceTabbedHeader(administratorMode != null && administratorMode.booleanValue());
                else
                    servicePanelHeader(administratorMode != null && administratorMode.booleanValue());
            }
        }
    }

    protected void servicePanelHeader(boolean administratorMode) {
        setAttribute("panelId", panel.getPanelId());
        setAttribute("panel", panel);
        renderFragment("outputStart");
        setAttribute("panelTitle", panel.getTitle());
        setAttribute("panelId", panel.getPanelId());
        setAttribute("panel", panel);
        renderFragment(administratorMode ? "panelTitle (Edit Mode)" : "panelTitle (Normal)");
        renderButtons(administratorMode);
        renderFragment("outputEnd");
    }

    protected void renderButtons(boolean administratorMode) {
        UserStatus userStatus = UserStatus.lookup();
        PanelSession status = SessionManager.getPanelSession(panel);
        SectionRegion sectionRegion = panel.getSection().getSectionRegion(panel.getRegion().getId());
        PanelPermission editPanelPerm = PanelPermission.newInstance(panel, PanelPermission.ACTION_EDIT);
        PanelPermission minPanelPerm = PanelPermission.newInstance(panel, PanelPermission.ACTION_MINIMIZE);
        PanelPermission maxPanelPerm = PanelPermission.newInstance(panel, PanelPermission.ACTION_MAXIMIZE);
        boolean userCanEditPanel = userStatus.hasPermission(editPanelPerm);
        boolean userCanMinimizePanel = userStatus.hasPermission(minPanelPerm);
        boolean userCanMaximizePanel = userStatus.hasPermission(maxPanelPerm);
        boolean columnRegion = panel.getRegion().isColumnRegion();
        setAttribute("panelId", panel.getPanelId());
        setAttribute("panel", panel);
        renderFragment("beforePanelButtons");
        //Invalidate cache button.
        if (panel.getCacheTime() > 0) {
            renderButtonOutput("ui.panel.refresh", getPanelActionLink("_invalidate-cache"), "REFRESH", "R");
        }
        //Arrow buttons.
        if (administratorMode && !status.isMaximized()) {
            if (!sectionRegion.isFirstPanelInRegion(panel) && !sectionRegion.isOnlyPanelInRegion(panel)) {
                renderButtonOutput("ui.panel.moveBack", getPanelActionLink("_move-back"), columnRegion ? "UP" : "LEFT", columnRegion ? "^" : "&lt;");
            }
            if (!sectionRegion.isLastPanelInRegion(panel) && !sectionRegion.isOnlyPanelInRegion(panel)) {
                renderButtonOutput("ui.panel.moveForward", getPanelActionLink("_move-forward"), columnRegion ? "DOWN" : "RIGHT", columnRegion ? "v" : "&gt;");
            }
        }
        //Edit button.
        if (!status.isEditMode() && panel.supportsEditMode() && userCanEditPanel) {
            renderButtonOutput("ui.panel.editMode", getPanelActionLink("_edit-mode"), "EDIT_MODE", "L");
        }

        //Show mode.
        if (!status.isShowMode()) {
            renderButtonOutput("ui.panel.showMode", getPanelActionLink("_show-mode"), "SHOW", "S");
        }
        //Properties mode
        if (administratorMode) {
            renderButtonOutput("ui.panel.editProperties", "#print here link to treeshortcuthandler", "PROPERTIES", "P");
        }
        //Minimize button
        if (!status.isMinimized() && (panel.isMinimizable() || administratorMode) && userCanMinimizePanel) {
            renderButtonOutput("ui.panel.minimize", getPanelActionLink("_minimize"), "MINIMIZE", "_");
        }
        //Restore button
        if (status.isMinimized() || status.isMaximized() || status.isMaximizedInRegion()) {
            renderButtonOutput("ui.panel.restore", getPanelActionLink("_restore"), "RESTORE", "oO");
        }
        //Maximize button
        if (!status.isMaximized() && (panel.isMaximizable() || administratorMode) && userCanMaximizePanel) {
            if (status.isMaximizedInRegion())
                renderButtonOutput("ui.panel.maximize", getPanelActionLink("_maximize"), "MAXIMIZE", "O");
            else
                renderButtonOutput("ui.panel.maximizeInRegion", getPanelActionLink("_maximize-in-region"), "MAXIMIZE", "O");
        }
        //Close button
        if (administratorMode) {
            renderButtonOutput("ui.panel.close", getPanelActionLink("_close"), "CLOSE", "X");
        }
        renderFragment("afterPanelButtons");
    }

    protected void serviceTabbedHeader(boolean administratorMode) {
        setAttribute("panelId", panel.getPanelId());
        setAttribute("panel", panel);
        renderFragment("outputStart");
        Panel[] allPanels = panel.getSection().getSectionRegion(panel.getRegion().getId()).getPanels();
        for (int i = 0; i < allPanels.length; i++) {
            Panel currentPanel = allPanels[i];
            setAttribute("panelTitle", currentPanel.getTitle());
            setAttribute("panelId", currentPanel.getPanelId());
            setAttribute("panel", currentPanel);
            if (SessionManager.getRegionStatus(panel.getSection(), panel.getRegion()).isSelected(currentPanel))
                renderFragment(administratorMode ? "panelTitle (Tabbed Edit Mode Selected)" : "panelTitle (Tabbed Normal Selected)");
            else
                renderFragment(administratorMode ? "panelTitle (Tabbed Edit Mode)" : "panelTitle (Tabbed Normal)");
        }
        renderButtons(administratorMode);
        renderFragment("outputEnd");
    }

    protected void renderButtonOutput(String buttonMessage, String link, String imageId, String imageAlt) {
        setAttribute("buttonMessage", buttonMessage);
        setAttribute("panelId", panel.getPanelId());
        setAttribute("link", link);
        setAttribute("imageId", imageId);
        setAttribute("imageAlternative", imageAlt);
        setAttribute("panelUID", HTTPSettings.AJAX_AREA_PREFFIX + "content_panel_" + panel.getPanelId());
        renderFragment("panelButton");
    }

    protected String getPanelActionLink(String action) {
        URLMarkupGenerator markupGenerator = UIServices.lookup().getUrlMarkupGenerator();
        return markupGenerator.getLinkToPanelAction(panel, action, false);
    }

}