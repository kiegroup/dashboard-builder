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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.users.UserStatus;
import org.slf4j.Logger;

@ApplicationScoped
@Named("sections_handler")
public class SectionsHandler extends BeanHandler {
    @Inject
    private transient Logger log;

    private String operationName;

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public synchronized void actionOnSection(CommandRequest request) throws Exception {
        if (operationName != null) {
            if (operationName.equals("delete")) {
                deleteSection(getNavigationManager().getCurrentWorkspace(), getNavigationManager().getCurrentSection());
            }
        }
    }

    public NavigationManager getNavigationManager() {
        return NavigationManager.lookup();
    }

    public synchronized void deleteSection(final WorkspaceImpl workspace, final Section section) {
        SectionPermission sectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_DELETE);
        try {
            if (getUserStatus().hasPermission(sectionPerm)) {
                NavigationManager navigationManager = getNavigationManager();
                if (navigationManager.getCurrentSectionId() != null) {
                    if (navigationManager.getCurrentSectionId().equals(section.getDbid())) {
                        navigationManager.setCurrentSection(null);
                    }
                    navigationManager.setCurrentWorkspace(workspace);
                }

                workspace.removeSection(section);
                UIServices.lookup().getWorkspacesManager().store(workspace);
            }
        } catch (Exception e) {
            log.error("Error deleting section " + section.getId() + ": ", e);
        }
    }

    public UserStatus getUserStatus() {
        return UserStatus.lookup();
    }
}
