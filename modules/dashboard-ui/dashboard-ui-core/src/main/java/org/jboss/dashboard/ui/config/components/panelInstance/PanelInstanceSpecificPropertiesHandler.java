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
package org.jboss.dashboard.ui.config.components.panelInstance;

import javax.enterprise.context.SessionScoped;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.WorkspacesManager;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelProviderParameter;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.WorkspacesManager;

@SessionScoped
public class PanelInstanceSpecificPropertiesHandler extends PanelInstancePropertiesHandler {

    public WorkspacesManager getWorkspacesManager() {
        return UIServices.lookup().getWorkspacesManager();
    }

    public PanelProviderParameter[] getPanelProviderParameters(PanelInstance instance) {
        return instance.getCustomParameters();
    }

    public void savePanelInstanceProperties(PanelInstance instance) throws Exception {
        instance.saveCustomProperties();
    }

    public PanelInstance getPanelInstance() throws Exception {
        return ((WorkspaceImpl) getWorkspacesManager().getWorkspace(getWorkspaceId())).getPanelInstance(getPanelInstanceId());
    }

    public PanelInstance getPanelInstance(String workspaceId, Long panelId) throws Exception {
        return ((WorkspaceImpl) getWorkspacesManager().getWorkspace(workspaceId)).getPanelInstance(panelId);
    }
}
