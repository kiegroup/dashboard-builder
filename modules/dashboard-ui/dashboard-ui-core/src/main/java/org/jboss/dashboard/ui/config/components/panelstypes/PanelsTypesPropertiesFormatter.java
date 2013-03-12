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
package org.jboss.dashboard.ui.config.components.panelstypes;

import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.PanelsProvidersManager;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.PanelsProvidersManager;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.WorkspacesManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PanelsTypesPropertiesFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PanelsTypesPropertiesFormatter.class.getName());

    private PanelsTypesPropertiesHandler panelsTypesPropertiesHandler;

    public WorkspacesManager getWorkspacesManager() {
        return UIServices.lookup().getWorkspacesManager();
    }

    public PanelsTypesPropertiesHandler getPanelsTypesPropertiesHandler() {
        return panelsTypesPropertiesHandler;
    }

    public void setPanelsTypesPropertiesHandler(PanelsTypesPropertiesHandler panelsTypesPropertiesHandler) {
        this.panelsTypesPropertiesHandler = panelsTypesPropertiesHandler;
    }

    public PanelsProvidersManager getPanelsProvidersManager() {
        return UIServices.lookup().getPanelsProvidersManager();
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            renderFragment("outputStart");
            renderFragment("outputProvidersGroupsStart");
            WorkspaceImpl workspace = (WorkspaceImpl) getWorkspacesManager().getWorkspace(getPanelsTypesPropertiesHandler().getWorkspaceId());
            String[] groups = getPanelsProvidersManager().enumerateProvidersGroups();
            for (int i = 0; i < groups.length; i++) {
                String group = groups[i];
                String groupName = getPanelsProvidersManager().getGroupDisplayName(group, SessionManager.getCurrentLocale());
                PanelProvider[] providers = getPanelsProvidersManager().getProvidersInGroup(group);
                int nProviders = providers.length;
                setAttribute("groupName", groupName);
                setAttribute("nProviders", nProviders);
                setAttribute("providerGroup", group);
                renderFragment("outputGroupName");
                setAttribute("providerGroup", group);
                renderFragment("outputDescriptionStart");
                for (int j = 0; j < providers.length; j++) {
                    PanelProvider provider = providers[j];
                    String providerDescription = provider.getResource(provider.getDescription(), SessionManager.getCurrentLocale());
                    setAttribute("providerAllowed", workspace.isProviderAllowed(provider.getId()));
                    setAttribute("providerDescription", providerDescription);
                    setAttribute("providerId", provider.getId());
                    setAttribute("providerGroup", provider.getGroup());
                    renderFragment("outputDescription");
                }
                renderFragment("outputDescriptionEnd");
            }
            setAttribute("providerAllowed", workspace.isProviderAllowed("*"));
            renderFragment("outputProvidersGroupsEnd");
            renderFragment("outputEnd");
        } catch (Exception e) {
            PanelsTypesPropertiesFormatter.log.error("Error:", e);
        }
    }
}