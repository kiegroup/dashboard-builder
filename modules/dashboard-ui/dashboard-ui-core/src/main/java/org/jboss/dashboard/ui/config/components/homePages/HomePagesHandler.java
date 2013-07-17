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
package org.jboss.dashboard.ui.config.components.homePages;

import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.components.HandlerFactoryElement;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.users.Role;
import org.apache.commons.lang.StringUtils;

import java.util.*;

import org.hibernate.Session;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceHome;

public class HomePagesHandler extends HandlerFactoryElement {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HomePagesHandler.class.getName());

    private String workspaceId;

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Workspace getWorkspace() throws Exception {
        return UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
    }

    public Section getDefaultSectionForRole(Role role) throws Exception {
        Workspace workspace = getWorkspace();
        Set<WorkspaceHome> homePages = workspace.getWorkspaceHomes();
        for (WorkspaceHome homePage : homePages) {
            if (homePage.getRoleId().equals(role.getName())) {
                if (homePage.getSectionId() == null) return null;
                return workspace.getSection(homePage.getSectionId());
            }
        }
        return null;
    }

    public void actionSaveHomePages(final CommandRequest request) throws Exception {
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            Map rolesPages = new HashMap();
            Set<Role> allRoles = SecurityServices.lookup().getRolesManager().getAllRoles();
            for (Role role : allRoles) {
                String param = request.getParameter("defaultPageFor_"+role.getName());
                if (!StringUtils.isBlank(param)) rolesPages.put(role.getName(), Long.decode(param));
                else rolesPages.put(role.getName(), null);
            }

            Workspace workspace = getWorkspace();
            Set<WorkspaceHome> homePages = workspace.getWorkspaceHomes();
            for (WorkspaceHome page : homePages) {
                Long sectionId = (Long) rolesPages.get(page.getRoleId());
                page.setSectionId(sectionId);
                rolesPages.remove(page.getRoleId());
            }

            Iterator it = rolesPages.keySet().iterator();
            while (it.hasNext()) {
                String roleName = (String) it.next();
                Long sectionId = (Long) rolesPages.get(roleName);
                WorkspaceHome newPage = new WorkspaceHome();
                newPage.setWorkspace(workspace);
                newPage.setRoleId(roleName);
                newPage.setSectionId(sectionId);
                workspace.getWorkspaceHomes().add(newPage);
            }

            // Save the changes made to the workspace.
            UIServices.lookup().getWorkspacesManager().store(workspace);
        }}.execute();
    }
}
