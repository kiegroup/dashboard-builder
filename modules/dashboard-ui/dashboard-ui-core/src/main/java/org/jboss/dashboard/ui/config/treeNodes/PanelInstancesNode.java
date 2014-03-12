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
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.components.panelInstance.PanelInstancesPropertiesHandler;
import org.jboss.dashboard.workspace.PanelsProvidersManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class PanelInstancesNode extends AbstractNode {

    @Inject
    private transient Logger log;

    @Inject
    private PanelInstancesPropertiesHandler handler;

    @Inject
    private PanelsProvidersManager panelsProvidersManager;

    public PanelInstancesPropertiesHandler getHandler() {
        return handler;
    }

    public PanelsProvidersManager getPanelsProvidersManager() {
        return panelsProvidersManager;
    }

    public String getId() {
        return "panelInstances";
    }

    public String getIconId() {
        return "16x16/ico-menu_panel.png";
    }

    protected List listChildren() {
        List<PanelInstancesProvidersNode> children = new ArrayList<PanelInstancesProvidersNode>();
        try {
            String[] groups = getPanelsProvidersManager().enumerateProvidersGroups();
            for (String gr : groups) {
                children.add(getNewPanelInstanceProvidersNode(gr));
            }
            Collections.sort(children, new Comparator<PanelInstancesProvidersNode>() {
                public int compare(PanelInstancesProvidersNode o1, PanelInstancesProvidersNode o2) {
                    return o1.getProviderName().compareTo(o2.getProviderName());
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
        PanelInstancesProvidersNode node = CDIBeanLocator.getBeanByType(PanelInstancesProvidersNode.class);
        node.setProviderId(provider);
        node.setProviderName(getPanelsProvidersManager().getGroupDisplayName(provider, SessionManager.getCurrentLocale()));
        node.setParent(this);
        node.setTree(getTree());
        return node;
    }

    public boolean onEdit() {
        WorkspaceNode parent = (WorkspaceNode) getParent();
        try {
            handler.setWorkspaceId(parent.getWorkspace().getId());
        } catch (Exception e) {
            log.error("Error:", e);
        }
        return true;
    }
}
