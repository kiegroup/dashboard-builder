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

import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.BeanHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.hibernate.Session;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.WorkspacesManager;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Enumeration;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

@SessionScoped
public class PanelsTypesPropertiesHandler extends BeanHandler {

    @Inject
    private transient Logger log;

    private String workspaceId;

    public WorkspacesManager getWorkspacesManager() {
        return UIServices.lookup().getWorkspacesManager();
    }

    public void setWorkspace(Workspace p) {
        workspaceId = p.getId();
    }

    public Workspace getWorkspace() throws Exception {
        return getWorkspacesManager().getWorkspace(workspaceId);
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public void actionChangeProviders(final CommandRequest request) {
        try {
            HibernateTxFragment txFragment = new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
                    workspace.setPanelProvidersAllowed(Collections.EMPTY_SET);
                    for (Enumeration paramNames = request.getRequestObject().getParameterNames(); paramNames.hasMoreElements();) {
                        String paramName = (String) paramNames.nextElement();
                        if (!paramName.startsWith("CHKBOX_")) continue;

                        String providerId = paramName.substring("CHKBOX_".length());
                        workspace.addPanelProviderAllowed(providerId);
                    }
                    UIServices.lookup().getWorkspacesManager().store(workspace);
                }
            };

            txFragment.execute();

        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
        }
    }
}
