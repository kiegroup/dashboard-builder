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

import java.util.Collections;
import java.util.List;

/**
 * A region inside a section
 */
public class SectionRegion {

    private Section section;
    private LayoutRegion layoutRegion;
    private List<Panel> regionPanels;


    public SectionRegion(Section section, LayoutRegion layoutRegion, List<Panel> panels) {
        this.section = section;
        this.layoutRegion = layoutRegion;
        regionPanels = panels;
        recalculatePanelsPosition();
    }

    public Section getSection() {
        return section;
    }

    public LayoutRegion getLayoutRegion() {
        return layoutRegion;
    }

    public boolean containsPanel(Panel panel) {
        return regionPanels.contains(panel);
    }

    /**
     * @param panel
     */
    public void addPanel(Panel panel) {
        if (layoutRegion != null) {
            if (panel.getPosition() == -1) {
                panel.setPosition(getPanelsCount());
            } else
                recalculatePanelsPosition();
            panel.setLayoutRegionId(layoutRegion.getId());
        }
    }

    public void removePanel(Panel panel) {
        if (panel != null && layoutRegion != null) {
            while (regionPanels.contains(panel)) {
                regionPanels.remove(panel);
                panel.setRegion(null);
                panel.setPosition(-1);
            }
            recalculatePanelsPosition();
        }
    }

    private void recalculatePanelsPosition() {
        int posIdx = 0;
        for (Panel panel : regionPanels) {
            panel.setPosition(posIdx++);
        }
        // Sort panels list according to their position
        Collections.sort(regionPanels);
    }

    /**
     * Moves the panel backwards in the panels list for its region
     */
    public void moveBackInRegion(Panel panel) {
        if (panel == null) return;
        if (layoutRegion == null) return;

        // Ensure panel can be moved back
        if (regionPanels == null || regionPanels.isEmpty()) return;
        if (regionPanels.get(0).equals(panel)) return;

        // Move panel back
        int currentPos = panel.getPosition();
        for (Panel regionPanel : regionPanels) {
            // Exchange position between panel and the porlet placed into the new panel position
            if (regionPanel.getPosition() == currentPos - 1) {
                panel.setPosition(currentPos - 1);
                regionPanel.setPosition(currentPos);
                break;
            }
        }
        // Sort panels list according to their position
        Collections.sort(regionPanels);
    }

    /**
     * Moves the panel forward in the panels list for its region
     */
    public void moveForwardInRegion(Panel panel) {
        if (panel == null) return;
        if (layoutRegion == null) return;

        // Ensure panel can be moved back
        if (regionPanels == null || regionPanels.isEmpty()) return;
        if (regionPanels.get(regionPanels.size() - 1).equals(panel)) return;

        // Move panel back
        int currentPos = panel.getPosition();
        for (Panel regionPanel : regionPanels) {
            // Exchange position between panel and the porlet placed into the new panel position
            if (regionPanel.getPosition() == currentPos + 1) {
                panel.setPosition(currentPos + 1);
                regionPanel.setPosition(currentPos);
                break;
            }
        }
        // Sort panels list according to their position
        Collections.sort(regionPanels);
    }

    /**
     * Returns true is it's the first panel in a region
     */
    public boolean isFirstPanelInRegion(Panel panel) {
        if (regionPanels.isEmpty()) return false;
        return regionPanels.get(0).equals(panel);
    }

    /**
     * Returns true is it's the first panel in a region
     */
    public boolean isLastPanelInRegion(Panel panel) {
        if (regionPanels.isEmpty()) return false;
        return regionPanels.get(regionPanels.size() - 1).equals(panel);
    }

    /**
     * Returns true is it's the only panel in a region
     */
    public boolean isOnlyPanelInRegion(Panel panel) {
        if (regionPanels.size() != 1) return false;
        return regionPanels.get(0).equals(panel);
    }

    public int getPanelsCount() {
        if (layoutRegion == null) return 0;
        return regionPanels.size();
    }

    /**
     * Returns all panels in region
     */
    public Panel[] getPanels() {
        if (layoutRegion == null) return new Panel[0];
        return regionPanels.toArray(new Panel[regionPanels.size()]);
    }
}
