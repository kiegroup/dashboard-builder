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
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.components.panels.PanelsPropertiesHandler;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Panel;
import org.slf4j.Logger;

import java.util.*;
import javax.inject.Inject;

public class PanelsNode extends AbstractNode {

    @Inject
    private transient Logger log;

    @Inject
    private PanelsPropertiesHandler panelsPropertiesHandler;

    public PanelsPropertiesHandler getPanelsPropertiesHandler() {
        return panelsPropertiesHandler;
    }

    public void setPanelsPropertiesHandler(PanelsPropertiesHandler panelsPropertiesHandler) {
        this.panelsPropertiesHandler = panelsPropertiesHandler;
    }

    protected List listChildren() {
        List childrenNodes = new ArrayList();
        try {
            List panels = new ArrayList(getPanels());
            Collections.sort(panels);
            for (int i = 0; i < panels.size(); i++) {
                Panel panel = (Panel) panels.get(i);
                childrenNodes.add(getNewPanelNode(panel));
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return childrenNodes;
    }

    protected boolean hasDynamicChildren() {
        try {
            return !getPanels().isEmpty();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return false;
    }

    protected TreeNode listChildrenById(String id) {
        try {
            String panelId = id.substring(id.indexOf("_")+1);
            String pageId = id.substring(0,id.indexOf("_"));
            Collection panels = getPanels();
            for (Iterator iterator = panels.iterator(); iterator.hasNext();) {
                Panel panel = (Panel) iterator.next();
                if (panelId.equals(panel.getPanelId().toString()) && pageId.equals(panel.getSection().getId().toString()))
                    return getNewPanelNode(panel);
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    public Collection getPanels() throws Exception {
        TreeNode parent = getParent();
        if (parent instanceof SectionNode) {
            String workspaceId = ((SectionNode) parent).getWorkspaceId();
            Long sectionId = ((SectionNode) parent).getSectionId();
            return ((WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId)).getSection(sectionId).getPanels();
        } else if (parent instanceof PanelInstanceNode) {
            String workspaceId = ((PanelInstanceNode) parent).getWorkspaceId();
            Long instanceId = ((PanelInstanceNode) parent).getPanelInstanceId();
            return Arrays.asList(((WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId)).getPanelInstance(instanceId).getAllPanels());
        }
        log.warn("Panels hang inside a panels node");
        return null;
    }

    protected PanelNode getNewPanelNode(Panel panel) {
        PanelNode sNode = CDIBeanLocator.getBeanByType(PanelNode.class);
        sNode.setTree(getTree());
        sNode.setParent(this);
        sNode.setWorkspaceId(panel.getWorkspace().getId());
        sNode.setSectionId(panel.getSection().getId());
        sNode.setPanelId(panel.getPanelId());
        return sNode;
    }

    public String getId() {
        return "panels";
    }

    public String getIconId() {
        return "16x16/ico-menu_panel.png";
    }

    public boolean isEditURIAjaxCompatible() {
        return false;
    }

    public boolean onEdit() {
        try {
            TreeNode parent = getParent();
            getPanelsPropertiesHandler().clearFieldErrors();
            getPanelsPropertiesHandler().setIsNewInstance(Boolean.FALSE);
            String workspaceId = null;
            if (parent instanceof SectionNode) {
                workspaceId = ((SectionNode) parent).getWorkspaceId();
                Long sectionId = ((SectionNode) parent).getSectionId();
                getPanelsPropertiesHandler().setSectionId(sectionId);
                getPanelsPropertiesHandler().setDefaultValues();
                getPanelsPropertiesHandler().setInstanceId(null);
            } else if (parent instanceof PanelInstanceNode) {
                workspaceId = ((PanelInstanceNode) parent).getWorkspaceId();
                Long instanceId = ((PanelInstanceNode) parent).getPanelInstanceId();
                getPanelsPropertiesHandler().setInstanceId(instanceId);
                getPanelsPropertiesHandler().setSectionId(null);
            }
            getPanelsPropertiesHandler().setWorkspaceId(workspaceId);
            getPanelsPropertiesHandler().clearFieldErrors();
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }

}
