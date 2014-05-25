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
import org.jboss.dashboard.commons.text.Base64;
import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.inject.Inject;
import org.jboss.dashboard.LocaleManager;

public class PanelInstancesGroupNode extends AbstractNode {

    @Inject
    private transient Logger log;

    public static final String GROUP_PREFFIX = "group_";

    private String workspaceId;
    private String groupName;
    private String providerId;

    public String getIconId() {
        return "16x16/ico-menu_panel.png";
    }

    public boolean isEditable() {
        return false;
    }

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
        List<PanelInstanceNode> children = new ArrayList<PanelInstanceNode>();
        final String language = LocaleManager.currentLang();
        try {
            Set<PanelInstance> panelInstances = new TreeSet<PanelInstance>(new Comparator<PanelInstance>() {
                public int compare(PanelInstance p1, PanelInstance p2) {
                    return p1.getTitle(language).compareToIgnoreCase(p2.getTitle(language));
                }
            });

            WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(getWorkspaceId());
            for (PanelInstance instance : workspace.getPanelInstances()) {
                String groupName = instance.getParameterValue(PanelInstance.PARAMETER_GROUP, language);
                String provider = instance.getProvider().getGroup();
                if (getGroupName().equals(groupName) && getProviderId().equals(provider)) {
                    panelInstances.add(instance);
                }
            }
            for (PanelInstance pi : panelInstances) {
                children.add(getNewInstanceNode(pi));
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

    public String getName(Locale l) {
        return groupName;
    }

    public String getDescription(Locale l) {
        return getName(l);
    }
}
