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
package org.jboss.dashboard.ui.config.components.permissions;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.jboss.dashboard.ui.components.permissions.PermissionsHandler;

@SessionScoped
public class PermissionsPropertiesHandlerConnector extends PermissionsPropertiesHandler {

    @Inject
    private PermissionsHandler permissionsHandler;

    public PermissionsHandler getPermissionsHandler() {
        return permissionsHandler;
    }

    public void setPermissionsHandler(PermissionsHandler permissionsHandler) {
        this.permissionsHandler = permissionsHandler;
    }


    public void setWorkspaceId(String workspaceId) {
        // Do nothing with this for now
    }

    public void setPermissionClass(Class aClass) {
        getPermissionsHandler().setPermissionClass(aClass);
    }

    public void setResourceName(String resourceName) {
        getPermissionsHandler().setResourceName(resourceName);
    }

    public void reset() {
        getPermissionsHandler().reset();
    }
}
