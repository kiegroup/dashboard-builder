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
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.components.permissions.PermissionsPropertiesHandler;
import org.jboss.dashboard.security.SectionPermission;

public class AllSectionsPermissionsNode extends AbstractNode {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(AllSectionsPermissionsNode.class.getName());

    private PermissionsPropertiesHandler permissionsPropertiesHandler;

    public PermissionsPropertiesHandler getPermissionsPropertiesHandler() {
        return permissionsPropertiesHandler;
    }

    public void setPermissionsPropertiesHandler(PermissionsPropertiesHandler permissionsPropertiesHandler) {
        this.permissionsPropertiesHandler = permissionsPropertiesHandler;
    }

    public String getId() {
        return "allsections.permissions";
    }

    public boolean onEdit() {
        try {
            getPermissionsPropertiesHandler().reset();
            getPermissionsPropertiesHandler().setPermissionClass(SectionPermission.class);
            getPermissionsPropertiesHandler().setResourceName(SectionPermission.getResourceName(((WorkspaceNode) getAncestor("org.jboss.dashboard.ui.config.treeNodes.WorkspaceNode")).getWorkspace()));
            TreeNode parent = getParent().getParent();
            if (parent instanceof WorkspaceNode) {
                getPermissionsPropertiesHandler().setWorkspaceId(((WorkspaceNode) parent).getWorkspaceId());
            }
        } catch (Exception e) {
            AllSectionsPermissionsNode.log.error("Error: ", e);
            return false;
        }
        return true;
    }

}
