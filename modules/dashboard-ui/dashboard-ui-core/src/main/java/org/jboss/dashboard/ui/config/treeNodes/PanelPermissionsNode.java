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

import javax.inject.Inject;

import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.components.permissions.PermissionsPropertiesHandler;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.users.UserStatus;
import org.slf4j.Logger;

public class PanelPermissionsNode extends AbstractNode {

    @Inject
    private transient Logger log;

    @Inject
    private PermissionsPropertiesHandler permissionsPropertiesHandler;

    public PermissionsPropertiesHandler getPermissionsPropertiesHandler() {
        return permissionsPropertiesHandler;
    }

    public boolean isEditable() {
        try {
            PanelNode parent = (PanelNode) getParent();
            PanelPermission editPerm = PanelPermission.newInstance(parent.getPanel(), PanelPermission.ACTION_EDIT_PERMISSIONS);
            return UserStatus.lookup().hasPermission(editPerm);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return false;
    }

    public String getId() {
        return "permissions";
    }

    public String getIconId() {
        return "16x16/ico-menu_permission.png";
    }

    public boolean isEditURIAjaxCompatible() {
        return false;
    }

    public boolean onEdit() {
        try {
            getPermissionsPropertiesHandler().reset();
            getPermissionsPropertiesHandler().setPermissionClass(PanelPermission.class);
            getPermissionsPropertiesHandler().setResourceName(PanelPermission.getResourceName(((PanelNode) getParent()).getPanel()));
            if (getParent() instanceof PanelNode) {
                getPermissionsPropertiesHandler().setWorkspaceId(((PanelNode) getParent()).getWorkspaceId());
            }
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }

}
