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
package org.jboss.dashboard.ui.config.treeNodes;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.MessagesComponentHandler;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.components.workspace.WorkspacePropertiesHandler;
import org.jboss.dashboard.workspace.Workspace;
import org.slf4j.Logger;

import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class WorkspaceNode extends AbstractNode {

    @Inject
    private transient Logger log;

    @Inject
    private WorkspacePropertiesHandler workspacePropertiesHandler;

    @Inject
    private SectionsNode sectionsNode;

    @Inject
    private PanelInstancesNode panelInstancesNode;

    @Inject
    private WorkspacePermissionsNode workspacePermissionsNode;

    @Inject
    private PanelsTypesNode panelsTypesNode;

    @Inject
    private HomePagesNode homePagesNode;

    private String workspaceId;

    @PostConstruct
    protected void init() {
        super.setSubnodes(new TreeNode[] {sectionsNode, panelInstancesNode,
            workspacePermissionsNode, panelsTypesNode, homePagesNode});
    }

    public String getIconId() {
        return "16x16/ico-menu_go-home.png";
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }


    public WorkspacePropertiesHandler getWorkspacePropertiesHandler() {
        return workspacePropertiesHandler;
    }

    public void setWorkspacePropertiesHandler(WorkspacePropertiesHandler workspacePropertiesHandler) {
        this.workspacePropertiesHandler = workspacePropertiesHandler;
    }

    public Workspace getWorkspace() throws Exception {
        return UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
    }

    public String getId() {
        return workspaceId;
    }

    public String getName(Locale l) {
        try {
            return (String) LocaleManager.lookup().localize(getWorkspace().getName());
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    public String getDescription(Locale l) {
        return getName(l);
    }

    public boolean onEdit() {
        try {
            getWorkspacePropertiesHandler().clearFieldErrors();
            getWorkspacePropertiesHandler().setCurrentWorkspace(getWorkspace());
            MessagesComponentHandler.lookup().clearAll();
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }
}
