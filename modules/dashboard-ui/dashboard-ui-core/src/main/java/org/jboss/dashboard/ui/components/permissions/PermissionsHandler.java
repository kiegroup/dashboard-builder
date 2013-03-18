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
package org.jboss.dashboard.ui.components.permissions;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.security.*;
import org.jboss.dashboard.security.principals.ComplementaryRolePrincipal;
import org.jboss.dashboard.security.principals.RolePrincipal;
import org.jboss.dashboard.ui.components.UIComponentHandlerFactoryElement;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.users.Role;
import org.jboss.dashboard.users.RolesManager;

import java.lang.reflect.Constructor;
import java.security.Principal;
import java.util.*;

public class PermissionsHandler extends UIComponentHandlerFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PermissionsHandler.class.getName());
    private String componentIncludeJSP;
    private Class permissionClass;
    private String resourceName;

    private List selectedIds = new ArrayList();

    public static final String PARAM_OBJECT_ID = "objid";
    public static final String PARAM_ACTION_SELECT_OBJECT = "selectObject";
    public static final String PARAM_ACTION_SELECT_ALL_OBJECTS = "selectAllObjects";
    public static final String PARAM_ACTION_UNSELECT_ALL_OBJECTS = "unselectAllObjects";
    public static final String PARAM_ACTION_DELETE_OBJECT = "deleteObject";
    public static final String PARAM_ACTION_DELETE_SELECTED_OBJECTS = "deleteSelectedObjects";
    public static final String PARAM_ACTION_DELETE_ALL_OBJECTS = "deleteAllObjects";

    public static PermissionsHandler lookup() {
        return (PermissionsHandler) Factory.lookup("org.jboss.dashboard.ui.components.permissions.PermissionsHandler");
    }

    public PermissionManager getPermissionsManager() {
        return SecurityServices.lookup().getPermissionManager();
    }

    public Policy getPolicy() {
        return SecurityServices.lookup().getSecurityPolicy();
    }

    public RolesManager getRolesManager() {
        return SecurityServices.lookup().getRolesManager();
    }

    public Class getPermissionClass() {
        return permissionClass;
    }

    public void setPermissionClass(Class permissionClass) {
        this.permissionClass = permissionClass;
    }

    public String getPermissionClassName() {
        return permissionClass.getName();
    }

    public void setPermissionClassName(String permissionClassName) {
        try {
            this.permissionClass = Class.forName(permissionClassName);
        } catch (ClassNotFoundException e) {
            log.error("Error: ", e);
        }
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getComponentIncludeJSP() {
        return componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public List<PermissionDescriptor> getPermissions() {
        return getPermissionsManager().find(permissionClass.getName(), resourceName);
    }

    public void actionSelectObject(CommandRequest request) throws Exception {
        String sid = request.getParameter(PARAM_OBJECT_ID);
        if (!StringUtils.isEmpty(sid)) {
            selectObject(Long.decode(sid));
        }
    }

    public void actionSelectAllObjects(CommandRequest request) throws Exception {
        selectedIds.addAll(getPermissionsManager().getPermissionIds(permissionClass.getName(), resourceName));
    }

    public void actionUnselectAllObjects(CommandRequest request) throws Exception {
        selectedIds.clear();
    }

    public void actionDeleteObject(CommandRequest request) throws Exception {
        String sid = request.getParameter(PARAM_OBJECT_ID);
        if (!StringUtils.isEmpty(sid)) {
            Long id = Long.decode(sid);
            PermissionDescriptor pd = getPermissionsManager().findPermissionDescriptorById(id);
            if (pd != null) {
                getPolicy().removePermission(pd.getPrincipal(), pd.getPermission());
                getPolicy().save();
            }
        }
    }

    public void actionDeleteSelectedObjects(CommandRequest request) throws Exception {
        for (Iterator<PermissionDescriptor> pdIt = getPermissionsManager().find(selectedIds).iterator(); pdIt.hasNext(); ) {
            PermissionDescriptor pd = pdIt.next();
            getPolicy().removePermission(pd.getPrincipal(), pd.getPermission());
        }
        getPolicy().save();
    }

    public void actionDeleteAllObjects(CommandRequest request) throws Exception {
        getPolicy().removePermissions(getResourceName());
        getPolicy().save();
    }

    public boolean isPermissionSelected(Long id) {
        return selectedIds.contains(id);
    }

    public int getSelectedPermissionsAmount() {
        return selectedIds.size();
    }

    public void actionAddNewPermissions(CommandRequest req) throws Exception {
        String roleName = req.getParameter("roleName");
        if (StringUtils.isNotBlank(roleName)) {
            Role role = getRolesManager().getRoleById(roleName);
            Boolean invert = Boolean.valueOf(req.getParameter("invert"));
            //Calculate actions
            Set paramNames = req.getParameterNames();
            List grantedActions = new ArrayList();
            List deniedActions = new ArrayList();
            for (Iterator iterator = paramNames.iterator(); iterator.hasNext();) {
                String paramName = (String) iterator.next();
                if (paramName.startsWith("action_")) {
                    String paramValue = req.getParameter(paramName);
                    String actionName = paramName.substring("action_".length());
                    if ("true".equals(paramValue)) {
                        grantedActions.add(actionName);
                    } else if ("false".equals(paramValue)) {
                        deniedActions.add(actionName);
                    }
                }
            }

            Principal principal = invert ? new ComplementaryRolePrincipal(role) : new RolePrincipal(role);

            //Add all permissions
            Constructor constructor = permissionClass.getConstructor(new Class[]{String.class, String.class});
            DefaultPermission perm = (DefaultPermission) constructor.newInstance(new Object[]{resourceName, null});
            grantActionsToPermission(perm, grantedActions, deniedActions);

            DefaultPermission permission = (DefaultPermission) getPolicy().getPermission(principal, getPermissionClass(), getResourceName());
            if (permission != null) {
                grantActionsToPermission(permission, grantedActions, deniedActions);
            } else {
                permission = perm;
            }
            getPolicy().addPermission(principal, permission);
            getPolicy().save();
            reset();
        } else log.error("Error: roleName cannot be a null, empty or blank String");
    }

    public void reset() {
        selectedIds.clear();
    }

    protected void grantActionsToPermission(DefaultPermission perm, List grantedActions, List deniedActions) {
        for (int i = 0; i < grantedActions.size(); i++) {
            String grantedAction = (String) grantedActions.get(i);
            perm.grantAction(grantedAction);
        }
        for (int i = 0; i < deniedActions.size(); i++) {
            String deniedAction = (String) deniedActions.get(i);
            perm.denyAction(deniedAction);
        }
    }

    protected void selectObject(Long id) {
        if (!selectedIds.remove(id)) {
            selectedIds.add(id);
        }
    }
}
