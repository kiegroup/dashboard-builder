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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.workspace.CopyManager;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.security.BackOfficePermission;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.users.UserStatus;

import java.util.Map;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class WorkspaceHandler extends BeanHandler {

    private String workspaceId;
    private String operationName;

    public CopyManager getCopyManager() {
        return UIServices.lookup().getCopyManager();
    }

    public UserStatus getUserStatus() {
        return UserStatus.lookup();
    }

    public NavigationManager getNavigationManager() {
        return NavigationManager.lookup();
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public synchronized void actionOnWorkspace(CommandRequest request) throws Exception {
        if ("navigate".equals(operationName)) {
            Workspace currentWorkspace = getNavigationManager().getCurrentWorkspace();
            if (currentWorkspace != null && currentWorkspace.getId().equals(workspaceId)) {
                return; //Nothing to do, workspace is the same !!
            }
            WorkspaceImpl workspaceToNavigateTo = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
            getNavigationManager().setCurrentWorkspace(workspaceToNavigateTo);
        } else if ("delete".equals(operationName)) {
            deleteWorkspace();
        } else if ("duplicate".equals(operationName)) {
            duplicateWorkspace();
        }
    }

    public void duplicateWorkspace() throws Exception {
        WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
        BackOfficePermission workspacePerm = BackOfficePermission.newInstance(null, BackOfficePermission.ACTION_CREATE_WORKSPACE);
        if (!getUserStatus().hasPermission(workspacePerm)) return;
        WorkspaceImpl workspaceCopy = getCopyManager().copy(workspace);
        Map<String, String> name = workspace.getName();
        for (String lang : name.keySet()) {
            String desc = name.get(lang);
            String prefix = "Copia de ";
            prefix = lang.equals("en") ? "Copy of " : prefix;
            workspaceCopy.setName(prefix + desc, lang);
        }
        UIServices.lookup().getWorkspacesManager().store(workspaceCopy);
    }

    public void deleteWorkspace() throws Exception {
        final WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
        WorkspacePermission workspacePerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_DELETE);
        if (!getUserStatus().hasPermission(workspacePerm)) return;
        UIServices.lookup().getWorkspacesManager().delete(workspace);
        getNavigationManager().setCurrentWorkspace(null);
    }

    public synchronized void actionNavigateToWorkspace(CommandRequest request) throws Exception {
        Workspace currentWorkspace = getNavigationManager().getCurrentWorkspace();
        if (currentWorkspace != null && currentWorkspace.getId().equals(workspaceId)) {
            return; //Nothing to do, workspace is the same !!
        }
        WorkspaceImpl workspaceToNavigateTo = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
        getNavigationManager().setCurrentWorkspace(workspaceToNavigateTo);
    }

}
