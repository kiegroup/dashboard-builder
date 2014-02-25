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
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.Workspace;

import java.lang.reflect.Field;
import java.security.Permission;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A permission over a panel.
 */
public class PanelPermission extends UIPermission {

    // Panel permissions
    //

    public static final String ACTION_VIEW = "view";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_MAXIMIZE = "maximize";
    public static final String ACTION_MINIMIZE = "minimize";
    public static final String ACTION_EDIT_PERMISSIONS = "edit perm";

    /**
     * Actions supported by this permission.
     */
    public static final List<String> LIST_OF_ACTIONS = Collections.unmodifiableList(Arrays.asList(new String[]{
        ACTION_VIEW,
        ACTION_EDIT,
        //ACTION_MAXIMIZE,
        //ACTION_MINIMIZE,
        ACTION_EDIT_PERMISSIONS
    }));

    // Factory method(s)
    //

    public static String getResourceName(Object resource) {
        String resourceName = getPolicy().getResourceName(resource);
        if (resource != null) {

            // All workspace or section panels
            if (resource instanceof Workspace) resourceName += ".*";
            if (resource instanceof Section) resourceName += ".*";
        }
        return resourceName;
    }

    public static PanelPermission newInstance(Object resource, String actions) {
        return new PanelPermission(getResourceName(resource), actions);
    }

    public static PanelPermission getInstance(Principal prpal, Object resource) {
        Policy policy = SecurityServices.lookup().getSecurityPolicy();
        PanelPermission perm = (PanelPermission) policy.getPermission(prpal, PanelPermission.class, getResourceName(resource));
        if (perm == null) perm = PanelPermission.newInstance(resource, null);
        return perm;
    }

    // Constructor(s)
    //

    public PanelPermission(String panelPath, String actions) {
        super(panelPath, actions);
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
        return super.implies(p) && (p instanceof PanelPermission);
    }

    public void grantAllActions() {
        for (String action : LIST_OF_ACTIONS) {
            grantAction(action);
        }
    }

    public static void main(String[] args) throws Exception {
        Field f = PanelPermission.class.getField("LIST_OF_ACTIONS");
        List listOfActions = (List) f.get(PanelPermission.class);
        Iterator it = listOfActions.iterator();
        while (it.hasNext()) System.out.println(it.next());
    }
}
