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

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.components.workspace.WorkspacesPropertiesHandler;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.security.WorkspacePermission;
import org.slf4j.Logger;

import java.util.*;
import javax.inject.Inject;

public class WorkspacesNode extends AbstractNode {

    @Inject
    private transient Logger log;

    @Inject
    private WorkspacesPropertiesHandler workspacesPropertiesHandler;

    public WorkspacesPropertiesHandler getWorkspacesPropertiesHandler() {
        return workspacesPropertiesHandler;
    }

    public void setWorkspacesPropertiesHandler(WorkspacesPropertiesHandler workspacesPropertiesHandler) {
        this.workspacesPropertiesHandler = workspacesPropertiesHandler;
    }

    protected List listChildren() {
        try {
            Set<String> workspaceIds = UIServices.lookup().getWorkspacesManager().getAllWorkspacesIdentifiers();
            TreeSet<String> sortedWorkspaceIds = new TreeSet<String>(workspaceIds);
            ArrayList list = new ArrayList();
            for (String wsId : sortedWorkspaceIds) {
                Workspace workspace = UIServices.lookup().getWorkspacesManager().getWorkspace(wsId);
                WorkspacePermission viewPerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_LOGIN);
                boolean canLogin = UserStatus.lookup().hasPermission(viewPerm);
                if (canLogin)
                    list.add(getNewWorkspaceNode(workspace));
            }
            return list;
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    protected TreeNode listChildrenById(String id) {
        try {
            Set<String> workspaceIds = UIServices.lookup().getWorkspacesManager().getAllWorkspacesIdentifiers();
            for (String wsId : workspaceIds) {
                if (wsId.equals(id))
                    return getNewWorkspaceNode(UIServices.lookup().getWorkspacesManager().getWorkspace(id));
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    protected boolean hasDynamicChildren() {
        try {
            return !UIServices.lookup().getWorkspacesManager().getAllWorkspacesIdentifiers().isEmpty();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return false;
    }

    protected WorkspaceNode getNewWorkspaceNode(Workspace workspace) {
        WorkspaceNode sNode = CDIBeanLocator.getBeanByType(WorkspaceNode.class);
        sNode.setTree(getTree());
        sNode.setParent(this);
        sNode.setWorkspaceId(workspace.getId());
        WorkspacePermission editPerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_EDIT);
        WorkspacePermission adminPerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_ADMIN);
        sNode.setEditable(UserStatus.lookup().hasPermission(editPerm));
        sNode.setExpandible(UserStatus.lookup().hasPermission(adminPerm));
        return sNode;
    }

    public String getId() {
        return "workspaces";
    }

    public String getIconId() {
        return "22x22/ico-menu_go-home.png";
    }

    public boolean onEdit() {
        try {
            getWorkspacesPropertiesHandler().clearFieldErrors();
            getWorkspacesPropertiesHandler().setName(null);
            getWorkspacesPropertiesHandler().setTitle(null);
            getWorkspacesPropertiesHandler().getMessagesComponentHandler().clearAll();
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }
}
