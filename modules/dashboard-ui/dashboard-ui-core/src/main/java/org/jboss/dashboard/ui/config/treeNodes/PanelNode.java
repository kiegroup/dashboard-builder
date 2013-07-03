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
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.components.panelInstance.PanelInstanceHandler;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import java.util.*;

public class PanelNode extends AbstractNode {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PanelNode.class.getName());

    private String workspaceId;
    private Long sectionId;
    private Long panelId;
    private PanelInstanceHandler instanceHandler;

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getPanelId() {
        return panelId;
    }

    public void setPanelId(Long panelId) {
        this.panelId = panelId;
    }

    public String getId() {
        return sectionId.toString() + "_" + panelId.toString();
    }

    public PanelInstanceHandler getInstanceHandler() {
        return instanceHandler;
    }

    public void setInstanceHandler(PanelInstanceHandler instanceHandler) {
        this.instanceHandler = instanceHandler;
    }

    public Panel getPanel() throws Exception {
        return ((WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId)).getSection(sectionId).getPanel(panelId.toString());
    }

    public PanelInstance getPanelInstance() throws Exception {
        return ((WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId)).getPanelInstance(getPanel().getInstanceId());
    }

    public String getName(Locale l) {
        try {
            TreeNode grandFather = getParent().getParent();
            Panel panel = getPanel();
            if (grandFather instanceof PanelInstanceNode) {
                Map sectionTitle = panel.getSection().getTitle();
                String pageName = (String) LocaleManager.lookup().localize(sectionTitle);
                if (panel.getRegion() != null) pageName += " (" + panel.getRegion().getId() + ")";
                return pageName;
            } else {
                // Use instance title
                return (String) LocaleManager.lookup().localize(panel.getTitle());
            }
        } catch (Exception e) {
            log.error("Error: ", e);
            return "";
        }
    }

    public String getDescription(Locale l) {
        return getName(l);
    }

    public boolean onEdit() {
        try {
            getInstanceHandler().setPanelInstanceId(getPanelInstance().getInstanceId());
            getInstanceHandler().setWorkspaceId(workspaceId);
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return super.onEdit();
    }
}
