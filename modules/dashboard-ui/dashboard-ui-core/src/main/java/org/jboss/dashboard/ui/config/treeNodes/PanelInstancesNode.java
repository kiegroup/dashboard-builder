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

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.components.panelInstance.PanelInstancesPropertiesHandler;
import org.jboss.dashboard.workspace.PanelsProvidersManager;
import org.jboss.dashboard.workspace.PanelsProvidersManager;
import org.jboss.dashboard.workspace.WorkspacesManager;
import org.jboss.dashboard.workspace.WorkspacesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PanelInstancesNode extends AbstractNode {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PanelInstancesNode.class.getName());

    private WorkspacesManager workspacesManager;
    private PanelInstancesPropertiesHandler handler;

    public PanelInstancesPropertiesHandler getHandler() {
        return handler;
    }

    public void setHandler(PanelInstancesPropertiesHandler handler) {
        this.handler = handler;
    }

    public PanelsProvidersManager getPanelsProvidersManager() {
        return UIServices.lookup().getPanelsProvidersManager();
    }

    public WorkspacesManager getWorkspacesManager() {
        return workspacesManager;
    }

    public void setWorkspacesManager(WorkspacesManager workspacesManager) {
        this.workspacesManager = workspacesManager;
    }

    protected List listChildren() {
        List children = new ArrayList();
        try {
            String[] groups = getPanelsProvidersManager().enumerateProvidersGroups();
            for (int i = 0; i < groups.length; i++) {
                children.add(getNewPanelInstanceProvidersNode(groups[i]));
            }
            Collections.sort(children, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((PanelInstancesProvidersNode) o1).getProviderName().compareTo(((PanelInstancesProvidersNode) o2).getProviderName());
                }
            });
        } catch (Exception e) {
            log.error("Error: ", e);

        }
        return children;
    }

    protected boolean hasDynamicChildren() {
        try {
            String[] groups = getPanelsProvidersManager().enumerateProvidersGroups();
            return groups != null && groups.length > 0;
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return false;
    }

    protected PanelInstancesProvidersNode getNewPanelInstanceProvidersNode(String provider) {
        PanelInstancesProvidersNode node = (PanelInstancesProvidersNode) Factory.lookup(PanelInstancesProvidersNode.class.getName());
        node.setProviderId(provider);
        node.setProviderName(getPanelsProvidersManager().getGroupDisplayName(provider, SessionManager.getCurrentLocale()));
        node.setParent(this);
        node.setTree(getTree());
        return node;
    }

    public String getId() {
        return "panelInstances";
    }

    public boolean onEdit() {
        WorkspaceNode parent = (WorkspaceNode) getParent();
        try {
            handler.workspaceId = parent.getWorkspace().getId();
        } catch (Exception e) {
            log.error("Error:", e);
        }
        return true;
    }
}
