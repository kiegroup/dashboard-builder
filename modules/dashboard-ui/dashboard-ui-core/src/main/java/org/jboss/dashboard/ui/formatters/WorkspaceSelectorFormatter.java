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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.security.BackOfficePermission;
import org.jboss.dashboard.users.UserStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WorkspaceSelectorFormatter extends Formatter {

    private NavigationManager navigationManager;

    public UserStatus getUserStatus() {
        return UserStatus.lookup();
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        renderFragment("outputStart");
        renderFragment("workspacesSelect");
        if (getNavigationManager().isAdminBarVisible()) try {
            Workspace currentWorkspace = getNavigationManager().getCurrentWorkspace();
            BackOfficePermission createPerm = BackOfficePermission.newInstance(null, BackOfficePermission.ACTION_CREATE_WORKSPACE);
            WorkspacePermission editPerm = WorkspacePermission.newInstance(currentWorkspace, WorkspacePermission.ACTION_EDIT);
            WorkspacePermission deletePerm = WorkspacePermission.newInstance(currentWorkspace, WorkspacePermission.ACTION_DELETE);
            boolean canAddWorkspace = getUserStatus().hasPermission(createPerm);
            if (canAddWorkspace) {
                renderFragment("createNewButton");
            }
            if (getUserStatus().hasPermission(editPerm)) {
                renderFragment("editButton");
            }
            if (UIServices.lookup().getWorkspacesManager().getAvailableWorkspacesIds().size() > 1 && getUserStatus().hasPermission(deletePerm)) {
                renderFragment("deleteButton");
            }
            if (canAddWorkspace) {
                renderFragment("duplicateButton");
            }
        } catch (Exception e) {
            throw new FormatterException(e);
        }
        renderFragment("outputEnd");
    }
}
