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
package org.jboss.dashboard.ui.config.components.sections;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.TreeSet;

public class SectionCopyFormatter extends SectionsPropertiesFormatter {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SectionCopyFormatter.class.getName());

    private SectionsPropertiesHandler sectionsPropertiesHandler;

    public SectionsPropertiesHandler getSectionsPropertiesHandler() {
        return sectionsPropertiesHandler;
    }

    public void setSectionsPropertiesHandler(SectionsPropertiesHandler sectionsPropertiesHandler) {
        this.sectionsPropertiesHandler = sectionsPropertiesHandler;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {


        try {
            WorkspaceImpl workspace = (WorkspaceImpl) getSectionsPropertiesHandler().getWorkspace();
            setAttribute("sectionTitle", getLocalizedValue(workspace.getSection(Long.decode(getSectionsPropertiesHandler().getSelectedSectionId())).getTitle()));
            renderFragment("outputStart");

            WorkspacePermission sectionPerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_CREATE_PAGE);
            if (UserStatus.lookup().hasPermission(sectionPerm)) {
                Panel[] panels = workspace.getSection(Long.decode(getSectionsPropertiesHandler().getSelectedSectionId())).getAllPanels();
                TreeSet panelInstances = new TreeSet();
                for (int i = 0; i < panels.length; i++) {
                    Panel panel = panels[i];
                    panelInstances.add(panel.getInstanceId());
                }
                if (!panelInstances.isEmpty()) {
                    setAttribute("sectionTitle", LocaleManager.lookup().localize(workspace.getSection(Long.decode(getSectionsPropertiesHandler().getSelectedSectionId())).getTitle()));
                    renderFragment("outputMode");

                    renderFragment("outputHeaders");
                    Iterator it = panelInstances.iterator();
                    int counter = 0;
                    while (it.hasNext()) {
                        String instanceId = it.next().toString();
                        PanelInstance instance = workspace.getPanelInstance(instanceId);
                        setAttribute("instanceId", instanceId);
                        setAttribute("group", instance.getResource(instance.getProvider().getGroup(), getLocale()));
                        setAttribute("description", instance.getResource(instance.getProvider().getDescription(), getLocale()));
                        setAttribute("title", getLocalizedValue(instance.getTitle()));
                        setAttribute("counter", counter);
                        counter++;
                        renderFragment("outputOpt");
                    }
                    renderFragment("outputOptEnd");
                    renderFragment("outputHeadersEnd");
                } else {
                    renderFragment("outputEmpty");
                    getSectionsPropertiesHandler().setDuplicateSection(Boolean.FALSE);
                }
            }
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error rendering section copy form: ", e);
        }
    }
}
