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
package org.jboss.dashboard.security;

import org.jboss.dashboard.SecurityServices;

import java.security.Permission;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A permission over a workspace.
 */
public class WorkspacePermission extends UIPermission {

    // Workspace permissions
    //

    public static final String ACTION_ADMIN = "admin";
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_CREATE_PAGE = "createPage";
    public static final String ACTION_EDIT_PERMISSIONS = "edit perm";
    public static final String ACTION_ADMIN_PROVIDERS = "admin providers";


    /**
     * Actions supported by this permission.
     */
    public static final List<String> LIST_OF_ACTIONS = Collections.unmodifiableList(Arrays.asList(new String[]{
        ACTION_LOGIN,
        ACTION_ADMIN,
        ACTION_CREATE_PAGE,
        ACTION_EDIT,
        ACTION_DELETE,
        ACTION_EDIT_PERMISSIONS,
        ACTION_ADMIN_PROVIDERS,
    }));

    // Factory method(s)
    //

    public static String getResourceName(Object resource) {
        return getPolicy().getResourceName(resource);
    }

    public static WorkspacePermission newInstance(Object resource, String actions) {
        return new WorkspacePermission(getResourceName(resource), actions);
    }

    public static WorkspacePermission getInstance(Principal prpal, Object resource) {
        Policy policy = SecurityServices.lookup().getSecurityPolicy();
        WorkspacePermission perm = (WorkspacePermission) policy.getPermission(prpal, WorkspacePermission.class, getResourceName(resource));
        if (perm == null) perm = WorkspacePermission.newInstance(resource, null);
        return perm;
    }

    // Constructor(s)
    //

    public WorkspacePermission(String workspacePath, String actions) {
        super(workspacePath, actions);
        //checkActions(actions);
    }

    /**
     * @throws IllegalArgumentException if actions contains action identifier which is not in {@link #LIST_OF_ACTIONS}
     */
    private void checkActions(String actions) throws IllegalArgumentException {
        checkActions(actions, LIST_OF_ACTIONS);
    }

    // java.security.Permission interface
    //

    @Override
    public boolean implies(Permission p) {
        return super.implies(p) && (p instanceof WorkspacePermission);
    }

    public void grantAllActions() {
        for (String action : LIST_OF_ACTIONS) {
            grantAction(action);
        }
    }

    public String toString() {
        return "WorkspacePermission (" + super.getResourcePath() + "): " + super.getActions();
    }
}
