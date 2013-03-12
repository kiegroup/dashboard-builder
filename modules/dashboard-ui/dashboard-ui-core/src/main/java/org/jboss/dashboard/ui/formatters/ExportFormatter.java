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

import org.jboss.dashboard.ui.components.export.ExportHandler;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ExportFormatter extends Formatter {

    protected ExportHandler exportHandler;
    protected String thumbnail = "adminHeader/new-workspace.png";
    protected String openedIcon = "general/open.png";
    protected String closedIcon = "general/close.png";

    public String getClosedIcon() {
        return closedIcon;
    }

    public void setClosedIcon(String closedIcon) {
        this.closedIcon = closedIcon;
    }

    public String getOpenedIcon() {
        return openedIcon;
    }

    public void setOpenedIcon(String openedIcon) {
        this.openedIcon = openedIcon;
    }

    public ExportHandler getExportHandler() {
        return exportHandler;
    }

    public void setExportHandler(ExportHandler exportHandler) {
        this.exportHandler = exportHandler;
    }


    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            List<WorkspaceImpl> workspaces = exportHandler.getWorkspacesWithKPIs();
            Collections.sort(workspaces, newWorkspaceComparatorByName());
            String selWorkspaceId = exportHandler.getSelectedWorkspaceId();
            renderFragment("start");
            for (WorkspaceImpl workspace: workspaces) {
                setAttribute("expandAction", getExpandAction(workspace));
                setAttribute("expandIcon", getExpandIcon(workspace));
                setAttribute("workspaceId", workspace.getId());
                setAttribute("workspaceName", getLocalizedValue(workspace.getName()));
                setAttribute("thumbnail", thumbnail);
                renderFragment("workspaceSelector");

                if (workspace.getId().equals(selWorkspaceId)) {
                    List<Section> sections = exportHandler.getSectionsWithKPIs(workspace);
                    Collections.sort(sections, newSectionComparatorByName());
                    setAttribute("workspaceId", workspace.getId());
                    setAttribute("nsections", sections.size());
                    renderFragment("workspaceStart");

                    for (Section section: sections) {
                        Long sectionId = section.getId();
                        setAttribute("sectionId", sectionId);
                        setAttribute("sectionName", getLocalizedValue(section.getTitle()));
                        setAttribute("checked", exportHandler.isSectionSelected(sectionId));
                        renderFragment("workspaceSection");
                    }

                    setAttribute("workspaceId", workspace.getId());
                    renderFragment("workspaceEnd");
                }
            }
            renderFragment("end");
        } catch (Exception e) {
            throw new FormatterException(e);
        }
    }

    protected String getExpandAction(Workspace workspace) {
        String selWorkspaceId = exportHandler.getSelectedWorkspaceId();
        if (workspace.getId().equals(selWorkspaceId)) return "action.collapse";
        return "action.expand";
    }

    protected String getExpandIcon(Workspace workspace) {
        String selWorkspaceId = exportHandler.getSelectedWorkspaceId();
        if (workspace.getId().equals(selWorkspaceId)) return openedIcon;
        return closedIcon;
    }

    protected String getLocalizedValue(Map m) {
        return (String) LocaleManager.lookup().localize(m);
    }

    protected Comparator newWorkspaceComparatorByName() {
        return new Comparator() {
            public int compare(Object o1, Object o2) {
                WorkspaceImpl s1 = (WorkspaceImpl) o1;
                WorkspaceImpl s2 = (WorkspaceImpl) o2;
                return getLocalizedValue(s1.getName()).compareTo(getLocalizedValue(s2.getName()));
            }
        };
    }

    protected Comparator newSectionComparatorByName() {
        return new Comparator() {
            public int compare(Object o1, Object o2) {
                Section s1 = (Section) o1;
                Section s2 = (Section) o2;
                return getLocalizedValue(s1.getTitle()).compareTo(getLocalizedValue(s2.getTitle()));
            }
        };
    }
}
