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
import javax.inject.Named;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.BeanHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.WorkspacesManager;

@SessionScoped
@Named("pip_handler")
public class PanelInstancesPropertiesHandler extends BeanHandler {

    private String workspaceId;

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

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
