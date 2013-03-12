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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.security.WorkspacePermission;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PanelsPropertiesFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PanelsPropertiesFormatter.class.getName());

    private PanelsPropertiesHandler panelsPropertiesHandler;

    public PanelsPropertiesHandler getPanelsPropertiesHandler() {
        return panelsPropertiesHandler;
    }

    public void setPanelsPropertiesHandler(PanelsPropertiesHandler panelsPropertiesHandler) {
        this.panelsPropertiesHandler = panelsPropertiesHandler;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        renderFragment("outputStart");
        renderFragment("outputStartRow");
        renderFragment("outputHeaderDelete");
        setAttribute("value", "ui.title");
        renderFragment("outputHeaders");
        if (getPanelsPropertiesHandler().getSectionId() == null) {
            setAttribute("value", "ui.sections.section");
            renderFragment("outputHeaders");
        }
        setAttribute("value", "ui.region");
        renderFragment("outputHeaders");
        renderFragment("outputEndRow");

        int n = 0;
        WorkspaceImpl workspace;
        Section section;
        boolean canAdminWorkspace = false;
        try {
            workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(getPanelsPropertiesHandler().getWorkspaceId());
            Panel[] panels;
            if (getPanelsPropertiesHandler().getSectionId() == null) {
                panels = ((WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspace.getId())).getPanelInstance(getPanelsPropertiesHandler().getInstanceId()).getAllPanels();
                section = null;
            } else {
                section = workspace.getSection(getPanelsPropertiesHandler().getSectionId());
                panels = section.getAllPanels();
            }

            if (panels != null && panels.length == 0) {
                renderFragment("empty");
            }

            WorkspacePermission adminPerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_ADMIN);

            for (int i = 0; i < panels.length; i++) {
                String estilo;
                if (n % 2 == 0) estilo = "skn-odd_row";
                else
                    estilo = "skn-even_row";
                renderFragment("outputStartRow");
                setAttribute("dbid", String.valueOf(panels[i].getDbid()));
                setAttribute("estilo", estilo);
                if (section == null) setAttribute("sectionId", String.valueOf(panels[i].getSection().getId()));
                else
                    setAttribute("sectionId", PanelsPropertiesHandler.PARAM_NO_SECTION);
                renderFragment("outputDelete");
                setAttribute("value", StringEscapeUtils.escapeHtml((String) LocaleManager.lookup().localize(panels[i].getTitle())));
                setAttribute("estilo", estilo);
                renderFragment("outputTitle");
                if (section == null) {
                    setAttribute("value", panels[i].getSection().getTitle().get(SessionManager.getLang()));
                    setAttribute("estilo", estilo);
                    renderFragment("outputSection");
                }
                if (panels[i].getRegion() != null)
                    setAttribute("value", panels[i].getRegion().getDescription());
                else
                    setAttribute("value", "");
                setAttribute("estilo", estilo);
                renderFragment("outputRegion");
                renderFragment("outputEndRow");
                n++;
            }
            renderFragment("outputEnd");

        } catch (Exception e) {
            PanelsPropertiesFormatter.log.error("Error:", e);
        }

    }
}
