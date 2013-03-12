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
import org.jboss.dashboard.ui.components.HandlerFactoryElement;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.WorkspacesManager;

public class PanelInstancesPropertiesHandler extends HandlerFactoryElement {

    public String workspaceId;

    public WorkspacesManager getWorkspacesManager() {
        return UIServices.lookup().getWorkspacesManager();
    }

    public void actionDeletePanel(CommandRequest request) throws Exception {
        WorkspaceImpl workspace = (WorkspaceImpl) getWorkspacesManager().getWorkspace(workspaceId);
        String panelId = request.getRequestObject().getParameter("panelId");
        getWorkspacesManager().removeInstance(workspace.getPanelInstance(panelId));
    }

    public void actionDeleteUselessPanelInstances(CommandRequest request) throws Exception {
        getWorkspacesManager().deleteUselessPanelInstances(workspaceId);
    }

    public void actionDeleteUselessPanelsAndInstances(CommandRequest request) throws Exception {
        getWorkspacesManager().deleteUselessPanelsAndInstances(workspaceId);
    }
}
