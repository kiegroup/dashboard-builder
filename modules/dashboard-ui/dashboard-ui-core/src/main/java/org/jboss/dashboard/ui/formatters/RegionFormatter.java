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
package org.jboss.dashboard.ui.formatters;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.users.UserStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class RegionFormatter extends Formatter {
    protected Section currentSection;
    protected LayoutRegion currentRegion;
    protected SectionRegion currentSectionRegion;
    protected boolean userAdmin = false;
    protected int cellspacingPanels;
    protected List<Panel> regionPanels;
    protected int panelRecommendedWidth;

    protected String maxDropSize = "100%";
    protected String minDropSize = "10px";

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        currentSection = NavigationManager.lookup().getCurrentSection();
        cellspacingPanels = currentSection.getPanelsCellSpacing().intValue();
        String regionID = (String) request.getAttribute(Parameters.RENDER_IDREGION);
        currentRegion = currentSection.getLayout().getRegion(regionID);
        currentSectionRegion = currentSection.getSectionRegion(currentRegion.getId());

        regionPanels = getRegionPanels();

        panelRecommendedWidth = regionPanels.isEmpty() ? 100 : (currentRegion.isRowRegion() ? (100 / regionPanels.size()) : 100);

        WorkspaceImpl workspace = NavigationManager.lookup().getCurrentWorkspace();

        if (workspace != null) {
            WorkspacePermission permToCheck = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_ADMIN);
            userAdmin = getUserStatus().hasPermission( permToCheck);
        }
    }

    protected void renderEmptyShowModeRegion() {
        renderFragment("No Panels In Region (normal mode)");
    }

    protected void renderThereAreNoPanels() {
        renderFragment("No Panels In Region");
    }

    protected void renderRegionPanelsStart() {
        renderFragment("Region Panels Start");
    }

    protected void renderRegionPanelsEnd() {
        renderFragment("Region Panels End");
    }

    protected void renderNewLineStart() {
        renderFragment("Region Panels New Line Start");
    }

    protected void renderNewLineEnd() {
        renderFragment("Region Panels New Line End");
    }

    protected void renderRegionEnd() {
        setAttribute("regionId", currentRegion.getId());
        renderFragment("regionEnd");
    }

    protected void renderRegionStart() {
        //Render region start
        if (userAdmin) {
            setAttribute("regionId", currentRegion.getId());
            setAttribute("numPanels", regionPanels.size());
            setAttribute("regionDescription", currentRegion.getDescription());
            setAttribute("cellspacingPanels", cellspacingPanels);
            renderFragment("regionStartWithTitle");
        } else {
            setAttribute("cellspacingPanels", cellspacingPanels);
            renderFragment("regionStartWithoutTitle");
        }
    }

    protected void renderPanelDropRegion(int index, boolean hasPanels) {
        if (hasPanels) {
            renderFragment("panelOutputStart");
            renderDropRegion(index);
            renderFragment("panelOutputEnd");
        }
    }

    protected void renderDropRegion(int index) {
        String width = currentRegion.isColumnRegion() ? getMaxDropSize() : getMinDropSize();
        String height = currentRegion.isColumnRegion() ? getMinDropSize() : getMaxDropSize();
        setAttribute("width", width);
        setAttribute("height", height);
        setAttribute("index", index);
        setAttribute("regionId", currentRegion.getId());
        renderFragment("panelDropRegion");
    }

    protected void renderPanel(Panel panel, boolean canEditPanel) {
        if (!isPanelOk(panel)) return;

        setAttribute("panel", panel);
        setAttribute("recommendedWidth", panelRecommendedWidth + "%");
        renderFragment("panelOutputStart");
        if (panel.isPaintTitle() ||
                panel.getPanelSession().isMinimized() ||
                (userAdmin) ||
                (panel.getProvider().getDriver().supportsEditMode(panel) && canEditPanel)) {
            setAttribute("panel", panel);
            setAttribute("administratorMode", userAdmin);
            setAttribute("panelTitle", StringEscapeUtils.ESCAPE_HTML4.translate(getLocalizedValue(panel.getTitle())));
            setAttribute("recommendedWidth", panelRecommendedWidth + "%");
            setAttribute("editMode", panel.getPanelSession().isEditMode());
            renderFragment("panelContentWithMenu");
        } else {
            setAttribute("panel", panel);
            setAttribute("recommendedWidth", panelRecommendedWidth + "%");
            renderFragment("panelContentWithoutMenu");
        }
        setAttribute("panel", panel);
        renderFragment("panelOutputEnd");
    }

    protected boolean isPanelOk(Panel panel) {
        if (panel == null) return false;
        if (panel.getInstance() == null) return false;
        if (panel.getProvider() == null) return false;
        return true;
    }

    private List<Panel> getRegionPanels() {
        Panel[] allPanels = currentSectionRegion.getPanels();
        List<Panel> panels = new ArrayList<Panel>();

        boolean onlyOnePanel = false;
        // When a panel is maximized in region, only show it.
        for (int i = 0; i < allPanels.length; i++) {
            Panel panel = allPanels[i];
            PanelSession status = panel.getPanelSession();
            if (status.isMaximizedInRegion()) {
                PanelPermission viewPerm = PanelPermission.newInstance(panel, PanelPermission.ACTION_VIEW);
                boolean canViewPanel = getUserStatus().hasPermission( viewPerm);
                if (canViewPanel) {
                    panels.add(panel);
                    onlyOnePanel = true;
                    break;
                }
            }
        }
        if (!onlyOnePanel)
            for (int i = 0; i < allPanels.length; i++) {
                Panel panel = allPanels[i];
                PanelPermission viewPerm = PanelPermission.newInstance(panel, PanelPermission.ACTION_VIEW);
                boolean canViewPanel = getUserStatus().hasPermission( viewPerm);
                if (canViewPanel) {
                    panels.add(panel);
                }
            }

        return panels;
    }

    public String getMaxDropSize() {
        return maxDropSize;
    }

    public void setMaxDropSize(String maxDropSize) {
        this.maxDropSize = maxDropSize;
    }

    public String getMinDropSize() {
        return minDropSize;
    }

    public void setMinDropSize(String minDropSize) {
        this.minDropSize = minDropSize;
    }

    public UserStatus getUserStatus() {
        return UserStatus.lookup();
    }
}
