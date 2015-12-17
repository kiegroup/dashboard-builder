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
package org.jboss.dashboard.security;

import org.jboss.dashboard.SecurityServices;

import java.security.Permission;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A permission inside the back-office area.
 */
public class BackOfficePermission extends UIPermission {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BackOfficePermission.class.getName());

    public static final String ACTION_USE_GRAPHIC_RESOURCES = "manageGraphicResources";
    public static final String ACTION_USE_PERMISSIONS = "managePermissions";
    public static final String ACTION_CREATE_WORKSPACE = "createWorkspace";

    /**
     * Actions supported by this permission.
     */
    public static final List<String> LIST_OF_ACTIONS = Collections.unmodifiableList(Arrays.asList(new String[]{
        ACTION_USE_GRAPHIC_RESOURCES,
        ACTION_CREATE_WORKSPACE,
        ACTION_USE_PERMISSIONS
    }));

    // Factory method(s)
    //

    public static String getResourceName(Object resource) {
        return "Backoffice"; // Only one resource is managed, the backoffice itself.
    }

    public static BackOfficePermission newInstance(Object resource, String actions) {
        return new BackOfficePermission(getResourceName(resource), actions);
    }

    public static BackOfficePermission getInstance(Principal prpal, Object resource) {
        Policy policy = SecurityServices.lookup().getSecurityPolicy();
        BackOfficePermission perm = (BackOfficePermission) policy.getPermission(prpal, BackOfficePermission.class, getResourceName(resource));
        if (perm == null) perm = BackOfficePermission.newInstance(resource, null);
        return perm;
    }

    public BackOfficePermission(String sectionPath, String actions) {
        super(sectionPath, actions);
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
        return super.implies(p) && (p instanceof BackOfficePermission);
    }

    public void grantAllActions() {
        for (String action : LIST_OF_ACTIONS) {
            grantAction(action);
        }
    }

    public String toString() {
        return "BackOfficePermission (" + super.getResourcePath() + "): " + super.getActions();
    }
}
