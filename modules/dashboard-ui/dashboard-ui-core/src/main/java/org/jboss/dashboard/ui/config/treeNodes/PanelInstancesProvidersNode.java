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
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;

import java.util.*;
import javax.inject.Inject;

public class PanelInstancesProvidersNode extends AbstractNode {

    @Inject
    private transient Logger log;

    private String providerId;
    private String providerName;

    public String getId() {
        return providerId + "_node";
    }

    public String getIconId() {
        return "16x16/ico-menu_panel.png";
    }

    public boolean isEditable() {
        return false;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getName(Locale l) {
        return StringEscapeUtils.unescapeHtml(providerName);
    }

    protected List listChildren() {
        List children = new ArrayList();
        PanelInstancesNode parent = (PanelInstancesNode) getParent();
        final String language = SessionManager.getLang();
        try {
            String workspaceId = parent.getHandler().getWorkspaceId();
            WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
            PanelInstance[] instances = workspace.getPanelInstancesInGroup(providerId);
            if (instances != null) {
                TreeSet<PanelInstance> panelInstances = new TreeSet<PanelInstance>(new Comparator<PanelInstance>() {
                    public int compare(PanelInstance p1, PanelInstance p2) {
                        return p1.getTitle(language).compareToIgnoreCase(p2.getTitle(language));
                    }
                });
                TreeSet<String> groups = new TreeSet<String>();
                for (PanelInstance pi : instances) {
                    String group = pi.getParameterValue(PanelInstance.PARAMETER_GROUP, language);
                    if (group != null && !"".equals(group.trim())) {
                        groups.add(group);
                    } else {
                        panelInstances.add(pi);
                    }
                }
                for (String gr : groups) {
                    children.add(getNewGroupNode(workspaceId, gr));
                }
                for (PanelInstance pi : panelInstances) {
                    children.add(getNewInstanceNode(pi));
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return children;
    }

    protected PanelInstanceNode getNewInstanceNode(PanelInstance instance) {
        PanelInstanceNode instanceNode = CDIBeanLocator.getBeanByType(PanelInstanceNode.class);
        instanceNode.setWorkspaceId(instance.getWorkspace().getId());
        instanceNode.setPanelInstanceId(instance.getInstanceId());
        instanceNode.setParent(this);
        instanceNode.setTree(getTree());
        return instanceNode;
    }

    protected PanelInstancesGroupNode getNewGroupNode(String workspaceId, String groupName) {
        PanelInstancesGroupNode groupNode = CDIBeanLocator.getBeanByType(PanelInstancesGroupNode.class);
        groupNode.setWorkspaceId(workspaceId);
        groupNode.setGroupName(groupName);
        groupNode.setParent(this);
        groupNode.setTree(getTree());
        groupNode.setProviderId(providerId);
        return groupNode;
    }
}
