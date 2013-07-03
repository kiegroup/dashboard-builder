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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.components.panelInstance.PanelInstanceHandler;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import java.util.Locale;

public class PanelInstanceNode extends AbstractNode {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PanelInstanceNode.class.getName());

    private PanelInstanceHandler instanceHandler;
    private String workspaceId;
    private Long panelInstanceId;

    public PanelInstanceHandler getInstanceHandler() {
        return instanceHandler;
    }

    public void setInstanceHandler(PanelInstanceHandler instanceHandler) {
        this.instanceHandler = instanceHandler;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getPanelInstanceId() {
        return panelInstanceId;
    }

    public void setPanelInstanceId(Long panelInstanceId) {
        this.panelInstanceId = panelInstanceId;
    }

    public String getId() {
        return panelInstanceId.toString();
    }

    protected PanelInstanceSpecificPropertiesNode getNewPanelInstanceSpecificPropertiesNode() {
        return (PanelInstanceSpecificPropertiesNode) getNewPanelInstancePropertiesNode(PanelInstanceSpecificPropertiesNode.class.getName());
    }

    protected PanelInstanceI18nPropertiesNode getNewPanelInstanceI18nPropertiesNode() {
        return (PanelInstanceI18nPropertiesNode) getNewPanelInstancePropertiesNode(PanelInstanceI18nPropertiesNode.class.getName());
    }

    private PanelInstancePropertiesNode getNewPanelInstancePropertiesNode(String className) {
        PanelInstancePropertiesNode pNode = (PanelInstancePropertiesNode) Factory.lookup(className);
        pNode.setTree(getTree());
        pNode.setParent(this);
        return pNode;
    }

    public PanelInstance getPanelInstance() throws Exception {
        return ((WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId)).getPanelInstance(panelInstanceId);
    }

    public String getName(Locale l) {
        try {
            return (String) LocaleManager.lookup().localize(getPanelInstance().getTitle());
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    public String getDescription(Locale l) {
        return getName(l);
    }

    public boolean onEdit() {
        getInstanceHandler().setWorkspaceId(workspaceId);
        getInstanceHandler().setPanelInstanceId(panelInstanceId);
        return super.onEdit();
    }
}
