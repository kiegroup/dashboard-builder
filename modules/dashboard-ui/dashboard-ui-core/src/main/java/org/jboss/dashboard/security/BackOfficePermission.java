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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.SecurityServices;

import java.security.Permission;
import java.security.Principal;
import java.util.*;

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
    public static final List LIST_OF_ACTIONS = new ArrayList();

    static {
        LIST_OF_ACTIONS.add(ACTION_USE_GRAPHIC_RESOURCES);
        LIST_OF_ACTIONS.add(ACTION_CREATE_WORKSPACE);
        LIST_OF_ACTIONS.add(ACTION_USE_PERMISSIONS);
    }

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
     * Check the integrity of the specified actions parameter.
     * Only allowed action identifiers defined into the <code>BackOfficePermission.LIST_OF_ACTIONS</code> constant
     * are supported. If this contraint is not satisfied then an exception will be thrown.
     *
     * @param actions List of action identifiers separated by comma.
     * @throws IllegalArgumentException If actions string is invalid.
     */
    private void checkActions(String actions) throws IllegalArgumentException {
        if (actions == null) return;

        List grantedList = super.toActionGrantedList(actions);
        List deniedList = super.toActionDeniedList(actions);
        List all = new ArrayList();
        all.addAll(grantedList);
        all.addAll(deniedList);
        Iterator it = all.iterator();
        while (it.hasNext()) {
            String action = (String) it.next();
            if (!LIST_OF_ACTIONS.contains(action)) {
                throw new IllegalArgumentException("Action list invalid (" + actions + ").");
            }
        }
    }

    // java.security.Permission interface
    //

    public boolean implies(Permission p) {
        // Check name
        if (!super.implies(p)) return false;

        // Check instances
        if (p == null || !(p instanceof BackOfficePermission)) return false;

        // All checks satisfied
        return true;
    }

    public void grantAllActions() {
        Iterator it = LIST_OF_ACTIONS.iterator();
        while (it.hasNext()) {
            String action = (String) it.next();
            this.grantAction(action);
        }
    }

    public String toString() {
        return "BackOfficePermission (" + super.getResourcePath() + "): " + super.getActions();
    }
}
