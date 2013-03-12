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
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.security.WorkspacePermission;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class PageSelectionFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PageSelectionFormatter.class.getName());

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        /*String numCols = (String) getParameter("numCols");
        numCols = numCols == null ? "3" : numCols;
        int cols = Integer.parseInt(numCols);*/

        try {
            Set workspaceIds = UIServices.lookup().getWorkspacesManager().getAllWorkspacesIdentifiers();
            List workspaces = new ArrayList();
            for (Iterator it = workspaceIds.iterator(); it.hasNext();) {
                String workspaceId = (String) it.next();
                Workspace workspace = UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
                WorkspacePermission perm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_LOGIN);
                if (UserStatus.lookup().hasPermission(perm)) {
                    workspaces.add(workspace);
                }
            }
            if (!workspaces.isEmpty()) {
                renderFragment("outputStart");
                for (int i = 0; i < workspaces.size(); i++) {
                    WorkspaceImpl workspace = (WorkspaceImpl) workspaces.get(i);
                    setAttribute("workspace", workspace);
                    setAttribute("workspaceId", workspace.getId());
                    setAttribute("workspaceName", getLocalizedValue(workspace.getTitle()));
                    Map params = new HashMap();
                    params.put(NavigationManager.WORKSPACE_ID, workspace.getId());
                    String workspaceURL = UIServices.lookup().getUrlMarkupGenerator().getPermanentLink("org.jboss.dashboard.ui.NavigationManager", "NavigateToWorkspace", params);
                    if (workspaceURL.startsWith(request.getContextPath())) {
                        workspaceURL = workspaceURL.substring((request.getContextPath()).length());
                    }
                    while (workspaceURL.startsWith("/")) workspaceURL = workspaceURL.substring(1);
                    setAttribute("url", workspaceURL);
                    renderFragment("workspaceOutput");
                }
                renderFragment("outputEnd");
            } else {
                renderFragment("empty");
            }
        } catch (Exception e) {
            throw new FormatterException(e);
        }
    }

}