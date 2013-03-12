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
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import java.util.*;

/**
 * 
 */
public class PanelInstancesProvidersNode extends AbstractNode {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PanelInstancesProvidersNode.class.getName());

    private String providerId;
    private String providerName;

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

    public Map getName() {
        try {
            Map name = new HashMap();
            name.put(SessionManager.getCurrentLocale().getLanguage(), StringEscapeUtils.unescapeHtml(providerName));
            return name;
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    protected List listChildren() {
        List children = new ArrayList();
        PanelInstancesNode parent = (PanelInstancesNode) getParent();
        String language = SessionManager.getLang();
        try {
            String workspaceId = parent.getHandler().workspaceId;
            WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
            PanelInstance[] instances = workspace.getPanelInstancesInGroup(providerId);
            if (instances != null) {
                TreeSet instancias = new TreeSet(new Comparator() {
                    public int compare(Object o1, Object o2) {
                        PanelInstance p1 = (PanelInstance) o1;
                        PanelInstance p2 = (PanelInstance) o2;
                        return p1.getTitle(SessionManager.getLang()).compareToIgnoreCase(p2.getTitle(SessionManager.getLang()));
                    }
                });
                TreeSet grupos = new TreeSet();

                for (int i = 0; i < instances.length; i++) {
                    PanelInstance instance = instances[i];
                    String grupo = instance.getParameterValue(PanelInstance.PARAMETER_GROUP, language);
                    if (grupo != null && !"".equals(grupo.trim())) {
                        grupos.add(grupo);
                    } else {
                        instancias.add(instance);
                    }
                }
                for (Iterator iterator = grupos.iterator(); iterator.hasNext();) {
                    String grupo = (String) iterator.next();
                    children.add(getNewGroupNode(workspaceId, grupo));
                }
                for (Iterator iterator = instancias.iterator(); iterator.hasNext();) {
                    PanelInstance panelInstance = (PanelInstance) iterator.next();
                    children.add(getNewInstanceNode(panelInstance));
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return children;
    }

    protected PanelInstanceNode getNewInstanceNode(PanelInstance instance) {
        PanelInstanceNode instanceNode = (PanelInstanceNode) Factory.lookup("org.jboss.dashboard.ui.config.treeNodes.PanelInstanceNode");
        instanceNode.setWorkspaceId(instance.getWorkspace().getId());
        instanceNode.setPanelInstanceId(instance.getInstanceId());
        instanceNode.setParent(this);
        instanceNode.setTree(getTree());
        return instanceNode;
    }

    protected PanelInstancesGroupNode getNewGroupNode(String workspaceId, String groupName) {
        PanelInstancesGroupNode groupNode = (PanelInstancesGroupNode) Factory.lookup("org.jboss.dashboard.ui.config.treeNodes.PanelInstancesGroupNode");
        groupNode.setWorkspaceId(workspaceId);
        groupNode.setGroupName(groupName);
        groupNode.setParent(this);
        groupNode.setTree(getTree());
        groupNode.setProviderId(providerId);
        return groupNode;
    }

    public String getId() {
        return providerId + "_node";
    }
}
