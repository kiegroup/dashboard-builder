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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.LayoutRegionStatus;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.users.UserStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RenderTabbedRegionFormatter extends RegionFormatter {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RenderTabbedRegionFormatter.class.getName());

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        super.service(request, response);

        if (currentSection == null) {
            log.error("Rendering a region, and current page is null.");
            return;
        }

        if (currentSectionRegion == null) {
            log.error("Cannot find region named " + currentRegion.getId() + " in current page!");
            return;
        }

        if(regionPanels.size() == 0  && !userAdmin || !currentRegion.isTabbedRegion()) return;

        renderRegionStart();
        renderRegionPanelsStart();

        if (regionPanels.size() > 0) {
            renderTabbedRegionHeader();
            for (int i = 0; i < regionPanels.size(); i++) {
                Panel panel = regionPanels.get(i);
                if (!SessionManager.getRegionStatus(currentSection, currentRegion).isSelected(panel))
                    continue;
                PanelPermission editPerm = PanelPermission.newInstance(panel, PanelPermission.ACTION_EDIT);
                boolean canEditPanel = getUserStatus().hasPermission( editPerm);
                renderPanel(panel, canEditPanel);
            }
        }
        renderRegionPanelsEnd();
        renderRegionEnd();
    }

    protected void renderTabbedDropRegion(int index, boolean hasPanels) {
        if (hasPanels) {
            renderFragment("outputStartTab");
            renderDropRegion(index);
            renderFragment("outputEndTab");
        }
    }

    protected void renderTabbedRegionHeader() {
        renderFragment("beforeTabs");

        if (userAdmin)
            renderTabbedDropRegion(0, !regionPanels.isEmpty());
        LayoutRegionStatus regionStatus = SessionManager.getRegionStatus(currentSection, currentRegion);
        if (regionStatus.getSelectedPanel() == null && !regionPanels.isEmpty()) {
            regionStatus.setSelectedPanel((Panel) regionPanels.get(0));
        }
        for (int i = 0; i < regionPanels.size(); i++) {
            Panel panel = regionPanels.get(i);
            setAttribute("tabTitle", LocaleManager.lookup().localize(panel.getTitle()));
            setAttribute("panel", panel);
            setAttribute("dragEnabled", userAdmin);
            setAttribute("url", UIServices.lookup().getUrlMarkupGenerator().getLinkToPanelAction(panel, "_select", true));
            setAttribute("selected", regionStatus.isSelected(panel));
            renderFragment("outputTab");
            if (userAdmin)
                renderTabbedDropRegion(i + 1, !regionPanels.isEmpty());
        }
        renderFragment("afterTabs");
    }
}
