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
package org.jboss.dashboard.ui.config.components.panelInstance;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.dashboard.workspace.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class PanelInstancesPropertiesFormatter extends Formatter {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelInstancesPropertiesFormatter.class.getName());

    private PanelInstancesPropertiesHandler handler;

    public WorkspacesManager getWorkspacesManager() {
        return UIServices.lookup().getWorkspacesManager();
    }

    public PanelInstancesPropertiesHandler getHandler() {
        return handler;
    }

    public void setHandler(PanelInstancesPropertiesHandler handler) {
        this.handler = handler;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        renderFragment("outputStart");
        renderFragment("outputStartRow");
        renderFragment("outputHeaderDelete");
        setAttribute("value", "ui.title");
        renderFragment("outputHeaders");
        setAttribute("value", "ui.admin.configuration.panelInstances.panelsCount");
        renderFragment("outputHeaders");
        renderFragment("outputEndRow");
        PanelInstance[] instances = null;
        try {
            WorkspaceImpl workspace = ((WorkspaceImpl) getWorkspacesManager().getWorkspace(handler.workspaceId));
            instances = workspace.getPanelInstances();
            if (instances == null || instances.length == 0) {
                renderFragment("outputStartRow");
                renderFragment("empty");
                renderFragment("outputEndRow");
                return;
            }

            final Map instancesTitles = calculateInstancesTitles(workspace, getLang());

            Arrays.sort(instances, new Comparator() {
                public int compare(Object o1, Object o2) {
                    PanelInstance p1 = (PanelInstance) o1;
                    PanelInstance p2 = (PanelInstance) o2;
                    return ((String) instancesTitles.get(p1.getInstanceId())).compareToIgnoreCase(
                            (String) instancesTitles.get(p2.getInstanceId())
                    );
                    //return p1.getTitle(SessionManager.getLang()).compareToIgnoreCase(p2.getTitle(SessionManager.getLang()));
                }
            });

            Map panelStatistics = calculatePanelsStatistics(workspace);

            for (int i = 0; i < instances.length; i++) {
                renderFragment("outputStartRow");
                PanelInstance instance = instances[i];
                String estilo = (i % 2) == 1 ? "skn-even_row" : "skn-odd_row";
                setAttribute("estilo", estilo);
                setAttribute("value", instance.getId());
                renderFragment("outputDelete");
                setAttribute("estilo", estilo);
                String title = (String) instancesTitles.get(instance.getInstanceId());
                setAttribute("value", StringEscapeUtils.escapeHtml(title));
                renderFragment("outputTitle");
                setAttribute("estilo", estilo);
                Integer panelCount = (Integer) panelStatistics.get(instance.getInstanceId());
                setAttribute("value", panelCount != null ? panelCount : new Integer(0));
                renderFragment("outputPanelsNumber");
                renderFragment("outputEndRow");

            }
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error getting workspace instances:", e);
        }

    }

    protected Map calculateInstancesTitles(WorkspaceImpl workspace, String lang) {
        HashMap result = new HashMap();
        for (Iterator it = workspace.getPanelInstancesSet().iterator(); it.hasNext();) {
            PanelInstance instance = (PanelInstance) it.next();
            String title = instance.getTitle(lang);
            title = (title == null || "".equals(title.trim())) ? instance.getTitle(getDefaultLang()) : title;
            result.put(instance.getInstanceId(), title);
        }
        return result;
    }

    protected Map calculatePanelsStatistics(WorkspaceImpl workspace) {
        Set sections = workspace.getSections();
        HashMap result = new HashMap();
        for (Iterator iterator = sections.iterator(); iterator.hasNext();) {
            Section section = (Section) iterator.next();
            Set panels = section.getPanels();
            for (Iterator iterator1 = panels.iterator(); iterator1.hasNext();) {
                Panel panel = (Panel) iterator1.next();
                Long instanceId = panel.getInstanceId();
                Integer instanceCount = (Integer) result.get(instanceId);
                if (instanceCount == null) {
                    result.put(instanceId, new Integer(1));
                } else {
                    result.put(instanceId, new Integer(1 + instanceCount.intValue()));
                }
            }
        }
        return result;
    }
}
