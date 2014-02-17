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
package org.jboss.dashboard.users;

import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;

import javax.enterprise.context.ApplicationScoped;
import java.util.Iterator;
import java.util.Set;

@ApplicationScoped @Install
public class NavigationUpdater implements UserStatusListener {

    public void statusChanged(UserStatus us) {
        if (us.isRootUser()) return;

        WorkspaceImpl currentWorkspace = NavigationManager.lookup().getCurrentWorkspace();
        if (currentWorkspace != null && currentWorkspace.getHomeSearchMode() == Workspace.SEARCH_MODE_ROLE_HOME_PREFERENT) {
            Section section = currentWorkspace.getDefaultHomePageForRole(us.getUserRoleIds().iterator().next());
            if (section != null) {
                NavigationManager.lookup().setCurrentSection(section);
            }
        }

        if (us.isAnonymous()) {
            // Reposition, and invoke page left in _destination_ page
            Section currentPage = NavigationManager.lookup().getCurrentSection();
            if (currentPage != null) {
                Set<Panel> panels = currentPage.getPanels();
                if (panels != null) {
                    for (Panel panel : panels) {
                        panel.pageLeft();
                    }
                }
            }

        }
    }
}
