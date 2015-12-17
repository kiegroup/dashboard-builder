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

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.workspace.LayoutRegion;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Section;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class UnassignedPanelsFormatter extends Formatter {

    @Inject
    private NavigationManager navigationManager;

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        Section page = navigationManager.getCurrentSection();
        if (page == null) return;
        Panel[] unassignedPanels = page.getUnassignedPanels();
        if (unassignedPanels != null && unassignedPanels.length > 0) {
            Arrays.sort(unassignedPanels);
            renderFragment("outputStart");
            LayoutRegion[] regions = page.getLayout().getRegions();
            for (int i = 0; i < unassignedPanels.length; i++) {
                Panel unassignedPanel = unassignedPanels[i];
                setAttribute("index", i);
                setAttribute("panel", unassignedPanel);
                setAttribute("panelId", unassignedPanel.getPanelId());
                setAttribute("panelTitle", getLocaleManager().localize(unassignedPanel.getTitle()));
                setAttribute("regions", regions);
                setAttribute("providerType", unassignedPanel.getProvider().getResource(unassignedPanel.getProvider().getDescription(), getLocaleManager().getCurrentLocale()));
                renderFragment("outputPanel");
            }
            renderFragment("outputEnd");
        }
    }
}
