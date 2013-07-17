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
import org.jboss.dashboard.commons.text.Base64;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class PanelInstancesGroupNode extends AbstractNode {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelInstancesGroupNode.class.getName());
    public static final String GROUP_PREFFIX = "group_";

    private String workspaceId;
    private String groupName;
    private String providerId;

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getId() {
        try {
            HTTPSettings fr = HTTPSettings.lookup();
            return GROUP_PREFFIX + Base64.encode(groupName.getBytes(fr.getEncoding()));
        } catch (UnsupportedEncodingException e) {
            log.error("Error: ", e);
        }
        return GROUP_PREFFIX + Base64.encode(groupName.getBytes());
    }

    protected List listChildren() {
        List children = new ArrayList();
        String language = SessionManager.getLang();
        try {
            WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(getWorkspaceId());
            PanelInstance[] instances = workspace.getPanelInstances();
            TreeSet instancias = new TreeSet(new Comparator() {
                public int compare(Object o1, Object o2) {
                    PanelInstance p1 = (PanelInstance) o1;
                    PanelInstance p2 = (PanelInstance) o2;
                    return p1.getTitle(SessionManager.getLang()).compareToIgnoreCase(p2.getTitle(SessionManager.getLang()));
                }
            });
            for (int i = 0; i < instances.length; i++) {
                PanelInstance instance = instances[i];
                String groupName = instance.getParameterValue(PanelInstance.PARAMETER_GROUP, language);
                String provider = instance.getProvider().getGroup();
                if (getGroupName().equals(groupName) && getProviderId().equals(provider)) {
                    instancias.add(instance);
                }
            }
            for (Iterator iterator = instancias.iterator(); iterator.hasNext();) {
                PanelInstance panelInstance = (PanelInstance) iterator.next();
                children.add(getNewInstanceNode(panelInstance));
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

    public String getName(Locale l) {
        return groupName;
    }

    public String getDescription(Locale l) {
        return getName(l);
    }
}
