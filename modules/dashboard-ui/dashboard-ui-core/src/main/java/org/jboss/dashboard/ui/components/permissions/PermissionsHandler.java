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
package org.jboss.dashboard.ui.components.permissions;

import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.security.*;
import org.jboss.dashboard.security.principals.ComplementaryRolePrincipal;
import org.jboss.dashboard.security.principals.RolePrincipal;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.jboss.dashboard.ui.components.UIBeanHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.users.Role;
import org.jboss.dashboard.users.RolesManager;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.security.Principal;
import java.util.*;
import javax.inject.Inject;
import javax.inject.Named;

@PanelScoped
@Named("permission_handler")
public class PermissionsHandler extends UIBeanHandler {

    public static final String PARAM_OBJECT_ID = "objid";
    public static final String PARAM_ACTION_SELECT_OBJECT = "selectObject";
    public static final String PARAM_ACTION_SELECT_ALL_OBJECTS = "selectAllObjects";
    public static final String PARAM_ACTION_UNSELECT_ALL_OBJECTS = "unselectAllObjects";
    public static final String PARAM_ACTION_DELETE_OBJECT = "deleteObject";
    public static final String PARAM_ACTION_DELETE_SELECTED_OBJECTS = "deleteSelectedObjects";
    public static final String PARAM_ACTION_DELETE_ALL_OBJECTS = "deleteAllObjects";

    public static PermissionsHandler lookup() {
        return CDIBeanLocator.getBeanByType(PermissionsHandler.class);
    }

    @Inject
    private transient Logger log;

    @Inject
    private PermissionManager permissionManager;

    @Inject
    protected Policy securityPolicy;

    @Inject
    protected RolesManager rolesManager;

    private Class permissionClass;
    private String resourceName;
    private List<Long> selectedIds = new ArrayList<Long>();

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

    public String getBeanJSP() {
        return "/components/permissions/managePermissions.jsp";
    }

    public List<PermissionDescriptor> getPermissions() {
        return permissionManager.find(permissionClass.getName(), resourceName);
    }

    // UI handler methods

    /*
     * Select a permission
     */
    public void actionSelectObject(CommandRequest request) throws Exception {
        String sid = request.getParameter(PARAM_OBJECT_ID);
        if (!StringUtils.isEmpty(sid)) {
            selectObject(Long.decode(sid));
        }
    }

    /*
     * Select all editable permissions
     */
    public void actionSelectAllObjects(CommandRequest request) throws Exception {
        List<PermissionDescriptor> allPerms = permissionManager.find(permissionClass.getName(), resourceName, Boolean.FALSE);
        for (PermissionDescriptor pd : allPerms) {
            selectedIds.add(pd.getDbid());
        }
    }

    /*
     * Deselect all selected permissions
     */
    public void actionUnselectAllObjects(CommandRequest request) throws Exception {
        selectedIds.clear();
    }

    /*
     * Delete a permission (only if not marked as readonly)
     */
    public void actionDeleteObject(CommandRequest request) throws Exception {
        String sid = request.getParameter(PARAM_OBJECT_ID);
        if (!StringUtils.isEmpty(sid)) {
            Long id = Long.decode(sid);
            PermissionDescriptor pd = permissionManager.findPermissionDescriptorById(id);
            if (pd != null) {
                securityPolicy.removePermission(pd.getPrincipal(), pd.getPermission());
                securityPolicy.save();
            }
        }
    }

    /*
     * Delete all selected permissions
     */
    public void actionDeleteSelectedObjects(CommandRequest request) throws Exception {
        for (PermissionDescriptor pd : permissionManager.find(selectedIds)) {
            securityPolicy.removePermission(pd.getPrincipal(), pd.getPermission());
        }
        securityPolicy.save();
    }

    /*
     * Delete all permissions (only permission that are not marked as readonly)
     */
    public void actionDeleteAllObjects(CommandRequest request) throws Exception {
        securityPolicy.removePermissions(getResourceName());
        securityPolicy.save();
    }

    public boolean isPermissionSelected(Long id) {
        return selectedIds.contains(id);
    }

    public int getSelectedPermissionsAmount() {
        return selectedIds.size();
    }

    /*
     * Add a new permissions (or modify an existing one, if it's not marked as readonly)
     */
    public void actionAddNewPermissions(CommandRequest req) throws Exception {
        Map<String, String[]> params = req.getRequestObject().getParameterMap();
        String roleName = params.get("roleName")[0];
        Boolean invert = params.containsKey("invert");
        if (StringUtils.isNotBlank(roleName)) {
            Role role = rolesManager.getRoleById(roleName);
            //Calculate actions
            Set<String> paramNames = req.getParameterNames();
            List<String> grantedActions = new ArrayList<String>();
            List<String> deniedActions = new ArrayList<String>();
            for (String paramName : paramNames) {
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

            DefaultPermission permission = (DefaultPermission) securityPolicy.getPermission(principal, getPermissionClass(), getResourceName());
            if (permission != null) {
                grantActionsToPermission(permission, grantedActions, deniedActions);
            } else {
                permission = perm;
            }
            securityPolicy.addPermission(principal, permission);
            securityPolicy.save();
            reset();
        } else log.error("Error: roleName cannot be a null, empty or blank String");
    }

    public void reset() {
        selectedIds.clear();
    }

    protected void grantActionsToPermission(DefaultPermission perm, List<String> grantedActions, List<String> deniedActions) {
        for (String grantedAction : grantedActions) {
            perm.grantAction(grantedAction);
        }
        for (String deniedAction : deniedActions) {
            perm.denyAction(deniedAction);
        }
    }

    protected void selectObject(Long id) {
        if (!selectedIds.remove(id)) {
            selectedIds.add(id);
        }
    }
}
