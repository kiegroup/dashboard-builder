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

import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Panel;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Formatter that renders the simple version of panel rendering, with menu.
 */
public class RenderRegionFormatter extends RegionFormatter {

    @Inject
    private transient Logger log;

    /**
     * Perform the required logic for this Formatter. Inside, the methods
     * setAttribute and renderFragment are intended to be used to generate the
     * output and set parameters for this output.
     * Method getParameter is intended to retrieve input parameters by name.
     * <p/>
     * Exceptions are to be catched inside the method, and not to be thrown, normally,
     * formatters could use a error fragment to be displayed when an error happens
     * in displaying. But if the error is unexpected, it can be wrapped inside a
     * FormatterException.
     *
     * @param request  user request
     * @param response response to the user
     * @throws org.jboss.dashboard.ui.taglib.formatter.FormatterException
     *          in case of an unexpected exception.
     */
    @Override
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

        if(regionPanels.size() == 0  && !userAdmin) return;

        if (currentRegion.isTabbedRegion()) {
            setAttribute("regionId", HTTPSettings.AJAX_AREA_PREFFIX + currentRegion.getId());
            renderFragment("outputTabbedRegion");
        } else {
            renderRegionStart();
            renderRegionPanelsStart();
            renderNewLineStart();
            if (userAdmin && currentRegion.isRowRegion()) {
                renderPanelDropRegion(0, !regionPanels.isEmpty());
            }
            if (userAdmin && currentRegion.isColumnRegion()) {
                renderPanelDropRegion(0, !regionPanels.isEmpty());
                renderNewLineEnd();
                renderNewLineStart();
            }
            if (regionPanels.size() > 0) {
                for (int i = 0; i < regionPanels.size(); i++) {
                    Panel panel = regionPanels.get(i);
                    int position = panel.getPosition();

                    PanelPermission editPerm = PanelPermission.newInstance(panel, PanelPermission.ACTION_EDIT);
                    boolean canEditPanel = getUserStatus().hasPermission( editPerm);
                    renderPanel(panel, canEditPanel);
                    if (userAdmin && currentRegion.isRowRegion()) {
                        renderPanelDropRegion(position + 1, !regionPanels.isEmpty());
                    }
                    if (currentRegion.isColumnRegion() && i != regionPanels.size()) {
                        renderNewLineEnd();
                        renderNewLineStart();
                    }
                    if (userAdmin && currentRegion.isColumnRegion()) {
                        renderPanelDropRegion(position + 1, !regionPanels.isEmpty());
                        renderNewLineEnd();
                        if (i != regionPanels.size()) renderNewLineStart();
                    }

                }
            } else if (userAdmin) {
                renderThereAreNoPanels();
            } else {
                renderEmptyShowModeRegion();
            }
            renderRegionPanelsEnd();
            renderRegionEnd();
        }

    }
}
