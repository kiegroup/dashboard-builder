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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.ui.NavigationManager;

/**
 * Class holding a section status for each session
 */
public class LayoutRegionStatus {

    /**
     * Region
     */
    private LayoutRegion region = null;

    /**
     * Panel selected for this region
     */
    private String idSelectedPanel = null;

    public LayoutRegionStatus(LayoutRegion region) {
        this.region = region;
    }

    public void setRegion(LayoutRegion region) {
        this.region = region;
    }

    /**
     * Returns which panel is currently selected for this region
     */
    public Panel getSelectedPanel() {
        Section section = NavigationManager.lookup().getCurrentSection();
        // Check conditions where can't be a selected panel
        if (region == null || section == null || section.getPanelsCount(region) == 0) {
            idSelectedPanel = null;
            return null;
        }

        Panel panel = null;
        if (idSelectedPanel != null) {
            panel = section.getPanel(idSelectedPanel);
        }

        if (panel != null && panel.getLayoutRegionId() != null && panel.getLayoutRegionId().equals(region.getId())) {
            return panel;
        } else {
            Panel[] panels = section.getPanels(region);
            if (panels != null && panels.length > 0)
                idSelectedPanel = panels[0].getId();
            return panels[0];
        }
    }

    public boolean isSelected(Panel panel) {
        if (panel == null)
            return false;

        if (panel.getPanelId().toString().equals(idSelectedPanel)) {
            return true;
        } else {
            return false;
        }
    }

    public void setSelectedPanel(Panel selectedPanel) {
        if (selectedPanel != null)
            this.idSelectedPanel = selectedPanel.getId();
        else
            this.idSelectedPanel = null;
    }
}
