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
package org.jboss.dashboard.ui.panel.parameters;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.Workspace;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Supplies a list
 */
public class WorkspaceDataSupplier implements ComboListParameterDataSupplier {
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(WorkspaceDataSupplier.class.getName());

    public void init(PanelInstance instance) {
        //TODO: Implement construction of values and keys here
    }

    public List getValues() {
        List values = new ArrayList();
        List workspaces = getWorkspaces();
        for (int i = 0; i < workspaces.size(); i++) {
            Workspace workspace = (Workspace) workspaces.get(i);
            values.add(getTitle(workspace.getId()));
        }
        return values;
    }

    public List getKeys() {
        List keys = new ArrayList();
        List workspaces = getWorkspaces();
        for (int i = 0; i < workspaces.size(); i++) {
            Workspace workspace = (Workspace) workspaces.get(i);
            keys.add(workspace.getId());
        }
        return keys;
    }


    private String getTitle(String workspaceId) {
        try {
            Workspace workspace = UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
            return (String) (LocaleManager.lookup()).localize(workspace.getTitle());
        } catch (Exception e) {
            return workspaceId;
        }
    }

    private List getWorkspaces() {
        List workspaces = new ArrayList();

        try {
            Set workspaceSet = UIServices.lookup().getWorkspacesManager().getAvailableWorkspacesIds();
            if (workspaceSet != null && !workspaceSet.isEmpty()) {
                Iterator workspaceList = workspaceSet.iterator();
                while (workspaceList.hasNext()) {
                    String workspaceId = (String) workspaceList.next();
                    workspaces.add(UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId));
                }
            }
        } catch (Exception e) {
            log.error("Can't retrieve portlas", e);
        }

        return workspaces;
    }
}
