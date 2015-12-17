/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.dashboard.security.BackOfficePermission;
import org.jboss.dashboard.users.UserStatus;
import org.slf4j.Logger;

public class GlobalPermissionsNode extends AbstractNode {

    @Inject
    private transient Logger log;

    @Inject
    private PermissionsPropertiesHandler permissionsPropertiesHandler;

    public PermissionsPropertiesHandler getPermissionsPropertiesHandler() {
        return permissionsPropertiesHandler;
    }

    public String getId() {
        return "globalPermissions";
    }

    public String getIconId() {
        return "22x22/ico-menu_permission.png";
    }

    public boolean isEditURIAjaxCompatible() {
        return false;
    }

    public boolean onEdit() {
        try {
            getPermissionsPropertiesHandler().setPermissionClass(BackOfficePermission.class);
            getPermissionsPropertiesHandler().setResourceName(BackOfficePermission.getResourceName(null));
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }

    public boolean isEditable() {
        BackOfficePermission editPerm = BackOfficePermission.newInstance(null, BackOfficePermission.ACTION_USE_PERMISSIONS);
        return super.isEditable() && UserStatus.lookup().hasPermission(editPerm);
    }
}
