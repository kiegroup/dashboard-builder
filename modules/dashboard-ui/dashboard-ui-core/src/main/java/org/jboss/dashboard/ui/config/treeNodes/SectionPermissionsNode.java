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
import org.jboss.dashboard.ui.config.components.permissions.PermissionsPropertiesHandler;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.security.SectionPermission;

public class SectionPermissionsNode extends AbstractNode {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PanelPermissionsNode.class.getName());

    private PermissionsPropertiesHandler permissionsPropertiesHandler;

    public PermissionsPropertiesHandler getPermissionsPropertiesHandler() {
        return permissionsPropertiesHandler;
    }

    public void start() throws Exception {
        super.start();
    }

    public boolean isEditable() {
        SectionNode parent = (SectionNode) getParent();
        Section section;
        try {
            section = parent.getSection();
            SectionPermission editPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_EDIT_PERMISSIONS);
            return super.isEditable() && UserStatus.lookup().hasPermission(editPerm);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return false;
    }

    public void setPermissionsPropertiesHandler(PermissionsPropertiesHandler permissionsPropertiesHandler) {
        this.permissionsPropertiesHandler = permissionsPropertiesHandler;
    }

    public String getId() {
        return "permissions";
    }

    public boolean onEdit() {
        try {
            getPermissionsPropertiesHandler().reset();
            getPermissionsPropertiesHandler().setPermissionClass(SectionPermission.class);
            getPermissionsPropertiesHandler().setResourceName(SectionPermission.getResourceName(((SectionNode) getParent()).getSection()));
            if (getParent() instanceof SectionNode) {
                getPermissionsPropertiesHandler().setWorkspaceId(((SectionNode) getParent()).getWorkspaceId());
            }
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }

}
