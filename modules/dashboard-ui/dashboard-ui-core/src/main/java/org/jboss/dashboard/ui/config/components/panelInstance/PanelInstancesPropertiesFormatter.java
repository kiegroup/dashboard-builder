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
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class PanelInstancesPropertiesFormatter extends Formatter {

    @Inject
    private transient Logger log;

    @Inject
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
            WorkspaceImpl workspace = ((WorkspaceImpl) getWorkspacesManager().getWorkspace(handler.getWorkspaceId()));
            instances = workspace.getPanelInstances();
            if (instances == null || instances.length == 0) {
                renderFragment("outputStartRow");
                renderFragment("empty");
                renderFragment("outputEndRow");
                return;
            }

            final Map<Long, String> instancesTitles = calculateInstancesTitles(workspace, getLang());

            Arrays.sort(instances, new Comparator<PanelInstance>() {
                public int compare(PanelInstance p1, PanelInstance p2) {
                    return (instancesTitles.get(p1.getInstanceId())).compareToIgnoreCase(
                            instancesTitles.get(p2.getInstanceId())
                    );
                }
            });

            Map<Long, Integer> panelStatistics = calculatePanelsStatistics(workspace);

            for (int i = 0; i < instances.length; i++) {
                renderFragment("outputStartRow");
                PanelInstance instance = instances[i];
                String estilo = (i % 2) == 1 ? "skn-even_row" : "skn-odd_row";
                setAttribute("estilo", estilo);
                setAttribute("value", instance.getId());
                renderFragment("outputDelete");
                setAttribute("estilo", estilo);
                String title = instancesTitles.get(instance.getInstanceId());
                setAttribute("value", StringEscapeUtils.escapeHtml(title));
                renderFragment("outputTitle");
                setAttribute("estilo", estilo);
                Integer panelCount = panelStatistics.get(instance.getInstanceId());
                setAttribute("value", panelCount != null ? panelCount : new Integer(0));
                renderFragment("outputPanelsNumber");
                renderFragment("outputEndRow");

            }
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error getting workspace instances:", e);
        }

    }

    protected Map<Long, String> calculateInstancesTitles(WorkspaceImpl workspace, String lang) {
        HashMap<Long, String> result = new HashMap<Long, String>();
        for (PanelInstance pi : workspace.getPanelInstancesSet()) {
            String title = pi.getTitle(lang);
            title = (title == null || "".equals(title.trim())) ? pi.getTitle(getDefaultLang()) : title;
            result.put(pi.getInstanceId(), title);
        }
        return result;
    }

    protected Map<Long, Integer> calculatePanelsStatistics(WorkspaceImpl workspace) {
        HashMap<Long, Integer> result = new HashMap<Long, Integer>();
        for (Section section : workspace.getSections()) {
            for (Panel panel : section.getPanels()) {
                Long instanceId = panel.getInstanceId();
                Integer instanceCount = result.get(instanceId);
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
