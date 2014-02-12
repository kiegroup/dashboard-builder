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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.users.UserStatus;
import org.apache.commons.lang.StringEscapeUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * This formatter iterates through the workspaces available to the user, and displays them.
 * <p/>
 * This class extends Formatter to provide support for iterating through the
 * workspaces available to an user, and display them.
 * <p/>
 * It serves the following output fragments, with given output attributes:
 * <ul>
 * <li> outputStart. At the beginning of the iteration, if the list is not empty
 * <li> output. For every item in the list. It receives the following attributes:
 * <ul>
 * <li> index. 0-based position of item in the list.
 * <li> count. 1-based position of item in the list.
 * <li> workspace. Workspace being displayed.
 * <li> workspaceId. Workspace id being displayed.
 * <li> workspaceName. Workspace name being displayed.
 * </ul>
 * <li> outputEnd. At the end of the iteration, if the list is not empty.
 * <li> empty. If the list is empty.
 * <li> error. In case of an error
 * </ul>
 */
public class RenderWorkspacesFormatter extends Formatter {

    @Inject
    private NavigationManager navigationManager;

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        List availableWorkspaces = new ArrayList();
        try {
            TreeSet workspaceIds = new TreeSet(UIServices.lookup().getWorkspacesManager().getAllWorkspacesIdentifiers());
            for (Iterator it = workspaceIds.iterator(); it.hasNext();) {
                String workspaceId = (String) it.next();
                Workspace workspace = UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);

                boolean finish = false;
                int index = 0;
                while (!finish && index < WorkspacePermission.LIST_OF_ACTIONS.size()) {
                    Permission perm = WorkspacePermission.newInstance(workspace, WorkspacePermission.LIST_OF_ACTIONS.get(index++));
                    if (UserStatus.lookup().hasPermission(perm)) {
                        availableWorkspaces.add(workspace);
                        finish = true;
                    }
                }
            }
        } catch (Exception e) {
            renderFragment("error");
            throw new FormatterException("Error in formatter: ", e);
        }

        if (availableWorkspaces.isEmpty()) {
            renderFragment("empty");
        } else {
            renderFragment("outputStart");
            for (int i = 0; i < availableWorkspaces.size(); i++) {
                Workspace workspace = (Workspace) availableWorkspaces.get(i);
                setAttribute("index", i);
                setAttribute("count", i + 1);
                setAttribute("workspace", workspace);
                setAttribute("workspaceId", workspace.getId());
                setAttribute("workspaceName", StringEscapeUtils.escapeHtml((String) LocaleManager.lookup().localize(workspace.getName())));
                setAttribute("current", workspace.getId().equals(navigationManager.getCurrentWorkspaceId()));
                renderFragment("output");
            }
            renderFragment("outputEnd");
        }
    }
}
