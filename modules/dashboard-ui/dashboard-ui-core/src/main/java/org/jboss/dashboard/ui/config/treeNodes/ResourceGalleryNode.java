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

import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler;

public class ResourceGalleryNode extends AbstractNode {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ResourceGalleryNode.class.getName());

    private ResourcesPropertiesHandler resourcesPropertiesHandler;

    public ResourcesPropertiesHandler getResourcesPropertiesHandler() {
        return resourcesPropertiesHandler;
    }

    public void setResourcesPropertiesHandler(ResourcesPropertiesHandler resourcesPropertiesHandler) {
        this.resourcesPropertiesHandler = resourcesPropertiesHandler;
    }


    public String getId() {
        return "resourceGalley";
    }

    public boolean onEdit() {
        resourcesPropertiesHandler.setResourceType("resourceGallery");
        resourcesPropertiesHandler.setInserted(false);
        resourcesPropertiesHandler.setPreview(false);
        resourcesPropertiesHandler.setResourceId(null);
        resourcesPropertiesHandler.setFile(null);
        resourcesPropertiesHandler.getFieddErrors().clear();
        if (this.getParent().getParent() instanceof RootNode) {
            resourcesPropertiesHandler.setWorkspaceId(null);
            resourcesPropertiesHandler.setPanelId(null);
            resourcesPropertiesHandler.setSectionId(null);
        } else if (this.getParent().getParent() instanceof WorkspaceNode) {
            WorkspaceNode workspaceNode = (WorkspaceNode) this.getParent().getParent();
            resourcesPropertiesHandler.setWorkspaceId(workspaceNode.getId());
            resourcesPropertiesHandler.setPanelId(null);
            resourcesPropertiesHandler.setSectionId(null);
        } else if (this.getParent().getParent() instanceof SectionNode) {
            AbstractNode parent = (AbstractNode) this.getParent();
            while (!(parent instanceof WorkspaceNode)) parent = (AbstractNode) parent.getParent();
            WorkspaceNode workspaceNode = (WorkspaceNode) parent;
            SectionNode sectionNode = (SectionNode) this.getParent().getParent();
            resourcesPropertiesHandler.setWorkspaceId(workspaceNode.getId());
            resourcesPropertiesHandler.setSectionId(Long.decode(sectionNode.getId()));
            resourcesPropertiesHandler.setPanelId(null);
        } else if (this.getParent().getParent() instanceof PanelNode) {
            AbstractNode parent = (AbstractNode) this.getParent();
            while (!(parent instanceof WorkspaceNode)) parent = (AbstractNode) parent.getParent();
            WorkspaceNode workspaceNode = (WorkspaceNode) parent;
            parent = (AbstractNode) this.getParent();
            while (!(parent instanceof SectionNode)) parent = (AbstractNode) parent.getParent();
            SectionNode sectionNode = (SectionNode) parent;
            PanelNode panelNode = (PanelNode) this.getParent().getParent();
            resourcesPropertiesHandler.setWorkspaceId(workspaceNode.getId());
            resourcesPropertiesHandler.setSectionId(Long.decode(sectionNode.getId()));
            resourcesPropertiesHandler.setPanelId( panelNode.getPanelId() );
        } else {
            //Unsuported Node
            throw new UnsupportedOperationException();
        }
        return super.onEdit();
    }
}
