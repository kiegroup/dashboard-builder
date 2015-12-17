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
import org.jboss.dashboard.workspace.Workspace;

import java.security.Permission;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A permission over a section (workspace page).
 */
public class SectionPermission extends UIPermission {

    // Section actions
    //

    public static final String ACTION_VIEW = "view";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_EDIT_PERMISSIONS = "edit perm";


    /**
     * Actions supported by this permission.
     */
    public static final List<String> LIST_OF_ACTIONS = Collections.unmodifiableList(Arrays.asList(new String[]{
        ACTION_VIEW,
        ACTION_EDIT,
        ACTION_DELETE,
        ACTION_EDIT_PERMISSIONS,
    }));

    // Factory methods(s)
    //

    public static String getResourceName(Object resource) {
        String resourceName = getPolicy().getResourceName(resource);

        // All workspace sections
        if (resource != null && resource instanceof Workspace) resourceName += ".*";

        return resourceName;
    }

    public static SectionPermission newInstance(Object resource, String actions) {
        return new SectionPermission(getResourceName(resource), actions);
    }

    public static SectionPermission getInstance(Principal prpal, Object resource) {
        Policy policy = SecurityServices.lookup().getSecurityPolicy();
        SectionPermission perm = (SectionPermission) policy.getPermission(prpal, SectionPermission.class, getResourceName(resource));
        if (perm == null) perm = SectionPermission.newInstance(resource, null);
        return perm;
    }

    // Constructor(s)
    //

    public SectionPermission(String sectionPath, String actions) {
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
        return super.implies(p) && (p instanceof SectionPermission);
    }

    public void grantAllActions() {
        for (String action : LIST_OF_ACTIONS) {
            grantAction(action);
        }
    }
}
