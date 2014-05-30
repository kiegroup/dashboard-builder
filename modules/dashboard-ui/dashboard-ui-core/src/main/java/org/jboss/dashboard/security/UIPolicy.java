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
import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.users.Role;
import org.jboss.dashboard.users.RolesManager;
import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.security.principals.DefaultPrincipal;
import org.jboss.dashboard.security.principals.RolePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.auth.Subject;
import java.lang.reflect.Method;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Principal;
import java.util.*;

/**
 * Security policy for the UI.
 */
@ApplicationScoped
public class UIPolicy implements Policy, Startable {
    private static final transient Logger log = LoggerFactory.getLogger(UIPolicy.class);

    /**
     * Unspecified principal applies to all users.
     */
    private static final Principal UNSPECIFIED_PRINCIPAL = new DefaultPrincipal("UnspecifiedPrincipal");

    /**
     * Permissions defined for this policy grouped by principal.
     * <p>Each principal has a set of permissions granted.
     * The key of the map is an instance of Principal and the value is the set of Permissions
     * granted to that principal.
     *
     * @link aggregation
     * @clientCardinality 1
     */
    protected Map<Principal, Permissions> permissionMap = new HashMap<Principal, Permissions>();

    /**
     * Hard-coded permissions. Granted by default.
     */
    private final List<Object[]> defaultPermissions = new ArrayList<Object[]>();

    // Buffers containing permissions added or removed.
    // Persistent operations (save, load and delete) flush these buffers.
    private final List<PermissionDescriptor> updateBuffer = new ArrayList<PermissionDescriptor>();
    private final List<PermissionDescriptor> deleteBuffer = new ArrayList<PermissionDescriptor>();

    @Inject
    protected LocaleManager localeManager;

    public Priority getPriority() {
        return Priority.HIGH;
    }

    public synchronized void start() throws Exception {
        log.debug("Init policy.");

        // Load state from persistent storage.
        this.load();

        // Grant default permissions
        this.grantDefaultPermissions();

        // Save policy
        this.save();
    }

    /**
     * Generates a name for any resource related with the security subsystem. Currently supported resources
     * are: Workspace, Section, Panel and PanelInstance.
     *
     * @return The resource name has the following format.<ul>
     *         <li>* or &lt;workspaceId&gt; for Workspace instances.
     *         <li>* or &lt;workspaceId&gt;.&lt;sectionId&gt; for Section instances.
     *         <li>* or &lt;workspaceId&gt;.*.&lt;panelInstanceId&gt; for Portet and PanelInstance instances.</ul>
     */
    public String getResourceName(Object resource) {
        String resourceName = "*";
        if (resource != null) {
            if (resource instanceof Workspace) {
                Workspace workspace = (Workspace) resource;
                resourceName = workspace.getId();
            } else if (resource instanceof Section) {
                Section section = (Section) resource;
                resourceName = section.getWorkspace().getId() + "." + section.getId();
            } else if (resource instanceof PanelInstance) {
                PanelInstance panel = (PanelInstance) resource;
                resourceName = panel.getWorkspace().getId() + ".*." + panel.getInstanceId();
            } else if (resource instanceof Panel) {
                Panel panel = (Panel) resource;
                resourceName = panel.getWorkspace().getId() + ".*." + panel.getInstanceId();
            } else {
                throw new IllegalArgumentException("Resource type not supported.");
            }
        }
        return resourceName;
    }

    /**
     * Retrieves a resource instance from its resource security name.
     *
     * @param resourceName    The resource name used to identify resource within the security subsystem.
     * @param permissionClass The permission class of the resource.
     * @throws Exception If any error occurs when retrieving resource.
     * @see <i>getResourceName</i> method explains the resource naming format.
     */
    public Object getResource(Class<? extends Permission> permissionClass, String resourceName) throws Exception {
        if (permissionClass.equals(WorkspacePermission.class)) {
            // All workspace
            if (resourceName.equals("*")) return null;

            // Concrete workspace
            return UIServices.lookup().getWorkspacesManager().getWorkspace(resourceName);
        } else if (permissionClass.equals(SectionPermission.class)) {
            // All sections
            if (resourceName.equals("*")) return null;

            // All workspace's sections
            int dot = resourceName.indexOf(".");
            if (dot == -1) return UIServices.lookup().getWorkspacesManager().getWorkspace(resourceName);
            String workspaceId = resourceName.substring(0, dot);
            String sectionId = resourceName.substring(dot + 1);
            if (sectionId.endsWith("*")) return UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);

            // Concrete section
            return UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId).getSection(new Long(sectionId));
        } else if (permissionClass.equals(PanelPermission.class)) {
            // All panels
            if (resourceName.equals("*")) return null;

            // All workspace's panels
            int dot = resourceName.indexOf(".");
            if (dot == -1) return UIServices.lookup().getWorkspacesManager().getWorkspace(resourceName);
            String workspaceId = resourceName.substring(0, dot);
            dot = resourceName.indexOf(".", dot + 1);
            String panelInstanceId = resourceName.substring(dot + 1);
            if (panelInstanceId.endsWith("*")) return UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);

            // Concrete panel
            return ((WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId)).getPanelInstance(new Long(panelInstanceId));
        } else {
            throw new IllegalArgumentException("Resource class not supported.");
        }
    }

    /**
     * Below is a list of permissions granted by default.
     */
    public synchronized void grantDefaultPermissions() {
        log.debug("Grant default permissions.");

        RolesManager rolesManager = SecurityServices.lookup().getRolesManager();
        WorkspacesManager workspacesManager = UIServices.lookup().getWorkspacesManager();
        SectionPermission sectionPerm = new SectionPermission("*", SectionPermission.ACTION_VIEW);
        sectionPerm.setReadOnly(true);
        PanelPermission panelPerm = new PanelPermission("*", PanelPermission.ACTION_VIEW);
        panelPerm.setReadOnly(true);

        // All roles can view all sections and panels
        for (Role role : rolesManager.getAllRoles()) {
            RolePrincipal rolePrincipal = new RolePrincipal(role);
            defaultPermissions.add(new Object[] {rolePrincipal, sectionPerm});
            defaultPermissions.add(new Object[] {rolePrincipal, panelPerm});

            // Give users with pure role "admin" some global permissions
            if (role.getName().equals(Role.ADMIN)) {
                BackOfficePermission bPerm = new BackOfficePermission(BackOfficePermission.getResourceName(null), null);
                bPerm.setReadOnly(true);
                bPerm.grantAction(BackOfficePermission.ACTION_USE_GRAPHIC_RESOURCES);
                bPerm.grantAction(BackOfficePermission.ACTION_CREATE_WORKSPACE);
                defaultPermissions.add(new Object[]{rolePrincipal, bPerm});

                for (WorkspaceImpl workspace : workspacesManager.getWorkspaces()) {
                    for (Permission permission : createDefaultPermissions(workspace)) {
                        defaultPermissions.add(new Object[] {rolePrincipal, permission});
                    }
                }
            }
        }

        for (Object[] objects : defaultPermissions) {
            this.addPermission((Principal) objects[0], (Permission) objects[1]);
        }
    }

    public List<Permission> createDefaultPermissions(Workspace workspace) {
        List<Permission> result = new ArrayList<Permission>();
        WorkspacePermission workspacePerm = new WorkspacePermission(getResourceName(workspace), null);
        workspacePerm.grantAllActions();
        workspacePerm.setReadOnly(true);
        result.add(workspacePerm);

        SectionPermission adminSectionPerm = new SectionPermission(getResourceName(workspace) + ".*", null);
        adminSectionPerm.grantAllActions();
        adminSectionPerm.setReadOnly(true);
        result.add(adminSectionPerm);

        PanelPermission adminPanelPerm = new PanelPermission(getResourceName(workspace) + ".*", null);
        adminPanelPerm.grantAllActions();
        adminPanelPerm.setReadOnly(true);
        result.add(adminPanelPerm);
        return result;
    }

    public boolean isPermissionGrantedByDefault(PermissionDescriptor permissionDescriptor) {
        for (Object[] objects : defaultPermissions) {
            try {
                if (objects[0].equals(permissionDescriptor.getPrincipal())) {
                    if ((objects[1]).getClass().getName().equals(permissionDescriptor.getPermissionClass())) {
                        if (((Permission) objects[1]).getName().equals(permissionDescriptor.getPermissionResource())) {
                            return true;
                        }
                    }
                }
            } catch (InstantiationException e) {
                log.error("Error: ", e);
            }
        }
        return false;
    }

    public String describeActionName(String permissionClass, String action, Locale locale) {
        try {
            ResourceBundle messages = localeManager.getBundle("org.jboss.dashboard.security.messages", locale);
            return messages.getString("action." + permissionClass + "." + action.replace(' ', '_'));
        }
        catch (MissingResourceException mre) {
            log.warn("Can't find description for " + action + " in locale " + locale);
            return action;
        }
    }

    public void addPermission(Permission newPerm) {
        this.addPermission(null, newPerm);
    }

    public synchronized void addPermission(Principal prpal, Permission perm) {
        try {

            // No principal specified then use unspecified principal
            Principal key = prpal;
            if (key == null) key = UNSPECIFIED_PRINCIPAL;

            log.debug("Adding permission " + perm + " for principal " + prpal);
            Permissions prpalPermissions = permissionMap.get(key);
            if (prpalPermissions == null) {
                prpalPermissions = new Permissions();
                permissionMap.put(key, prpalPermissions);
            }
            // If the permission is already granted then the new permission will be ignored when calling the following method,
            // So we don't have to implement any redundancy control.
            prpalPermissions.add(perm);

            // Update the persistent descriptor.
            PermissionDescriptor pd = PermissionManager.lookup().find(key, perm);
            if (pd == null) pd = PermissionManager.lookup().createNewItem();
            pd.setPrincipal(key);
            pd.setPermission(perm);
            pd.setReadonly(((UIPermission) perm).isReadOnly());

            // If the update buffer already contains the permission descriptor then remove it.
            int pos = updateBuffer.indexOf(pd);
            if (pos != -1) updateBuffer.remove(pos);
            updateBuffer.add(pd);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public void removePermissions(Principal p, String resourceName) {

        Permissions prpalPermissions = permissionMap.get(p);
        if (prpalPermissions != null && resourceName != null) {

            // Search for permissions related with the specified resource.
            List<Permission> toRemove = new ArrayList<Permission>();
            Enumeration<Permission> en = prpalPermissions.elements();
            DefaultPermission resPerm = new DefaultPermission(resourceName, null);
            DefaultPermission regPerm = new DefaultPermission(resourceName, null);
            while (en.hasMoreElements()) {
                Permission permission = en.nextElement();
                regPerm.setResourceName(permission.getName());
                if (resPerm.implies(regPerm)) toRemove.add(permission);
            }

            // Remove permissions
            Iterator<Permission> it = toRemove.iterator();
            while (it.hasNext()) this.removePermission(p, it.next());
        }
    }

    public void removePermissions(String resourceName) {
        for (Principal targetPrpal : permissionMap.keySet()) {
            this.removePermissions(targetPrpal, resourceName);
        }
    }

    public synchronized void removePermission(Principal p, Permission perm) {
        // Update buffers
        PermissionDescriptor pd = PermissionManager.lookup().find(p, perm);
        if (pd != null && !pd.isReadonly()) {
            int pos = updateBuffer.indexOf(pd);
            if (pos != -1) updateBuffer.remove(pos);
            pos = deleteBuffer.indexOf(pd);
            if (pos == -1) deleteBuffer.add(pd);

            // Remove the permission from memory
            if (log.isDebugEnabled()) log.debug("Removing permission " + perm + " for principal " + p);
            Permissions prpalPermissions = permissionMap.get(p);
            if (prpalPermissions != null) {
                Permissions newPermissions = new Permissions();
                Enumeration<Permission> en = prpalPermissions.elements();
                while (en.hasMoreElements()) {
                    Permission permission = en.nextElement();
                    if (!perm.equals(permission)) newPermissions.add(permission);
                }
                permissionMap.put(p, newPermissions);
            }
        }
    }

    public void removePermission(Permission oldPerm) {
        for (Principal targetPrpal : permissionMap.keySet()) {
            this.removePermission(targetPrpal, oldPerm);
        }
    }

    public PermissionCollection getPermissions(Subject usr) {
        Permissions userPermissions = new Permissions();
        for (Principal principal : usr.getPrincipals()) {
            Permissions permissions = permissionMap.get(principal);
            if (permissions != null) {
                Enumeration<Permission> permEnum = permissions.elements();
                while (permEnum.hasMoreElements()) {
                    Permission perm = permEnum.nextElement();
                    userPermissions.add(perm);
                }
            }
        }

        // Also retrieve permission assigned to the unspecified principal
        Permissions permissions = permissionMap.get(UNSPECIFIED_PRINCIPAL);
        if (permissions != null) {
            Enumeration<Permission> permEnum = permissions.elements();
            while (permEnum.hasMoreElements()) {
                Permission perm = permEnum.nextElement();
                userPermissions.add(perm);
            }
        }

        return userPermissions;
    }

    public PermissionCollection getPermissions(Principal prpal) {
        Principal principal = prpal;
        if (principal == null) principal = UNSPECIFIED_PRINCIPAL;
        return permissionMap.get(principal);
    }

    public Permission getPermission(Principal prpal, Class<? extends Permission> permClass, String permName) {
        PermissionCollection permCollection = getPermissions(prpal);
        if (permCollection != null) {
            Enumeration<Permission> en = permCollection.elements();
            while (en.hasMoreElements()) {
                Permission perm = en.nextElement();
                if (perm.getName().equals(permName) && perm.getClass().getName().equals(permClass.getName())) {
                    return perm;
                }
            }
        }
        return null;
    }

    public Map<Principal, Permission> getPermissions(Object resource, Class<? extends Permission>  permClass) throws Exception {
        final Map<Principal, Permission> results = new HashMap<Principal, Permission>();

        Method getResName = permClass.getMethod("getResourceName", new Class<?>[]{Object.class});
        String resourceName = (String) getResName.invoke(permClass, new Object[]{resource});

        for (Map.Entry<Principal, Permissions> entry : permissionMap.entrySet()) {
            Permissions perms = entry.getValue();
            for (Enumeration<Permission> en = perms.elements(); en.hasMoreElements();) {
                Permission perm = en.nextElement();
                if (perm.getName().equals(resourceName) && permClass.equals(perm.getClass())) {
                    results.put(entry.getKey(), perm);
                }
            }
        }
        return results;
    }

    public synchronized void removePermissions(final Object resource) throws Exception {
        // Retrieve permission related with resource.
        final String resourceName = getResourceName(resource);
        log.debug("Removing all permissions for resource named " + resourceName);
        final List<PermissionDescriptor> results = PermissionManager.lookup().find(resourceName);
        for (PermissionDescriptor pd : results) {
            int pos = updateBuffer.indexOf(pd);
            if (pos != -1) updateBuffer.remove(pos);
            pos = deleteBuffer.indexOf(pd);
            if (pos == -1) deleteBuffer.add(pd);
        }

        // Remove all resource-related permissions from policy
        removePermissions(resourceName);
        removePermissions(resourceName + ".*");
    }

    public synchronized void clear() {
        permissionMap.clear();
        updateBuffer.clear();
        deleteBuffer.clear();
    }

    // Persistent interface implementation
    //

    public boolean isPersistent() {
        return true;
    }

    public synchronized void save() throws Exception {
        if (log.isDebugEnabled()) log.debug("Save policy with updateBuffer=" + updateBuffer);
        if (!updateBuffer.isEmpty() || !deleteBuffer.isEmpty()) {

            // Flush update buffer
            for (PermissionDescriptor descriptor : updateBuffer) {
                descriptor.save();
            }

            // Flush delete buffer
            for (PermissionDescriptor descriptor : deleteBuffer) {
                descriptor.delete();
            }

            // Clear the buffers and notify to the cluster the policy changes.
            updateBuffer.clear();
            deleteBuffer.clear();
        }
    }

    public void update() throws Exception {
        this.save();
    }

    public synchronized void load() throws Exception {
        // Load permission descriptors from persistent storage
        log.debug("Load policy.");
        List<PermissionDescriptor> results = PermissionManager.lookup().getAllInstances();

        // Initialize policy
        clear();
        for (PermissionDescriptor pd : results) {
            if (pd != null) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Adding permission " + pd.getPermission() + " for principal " + pd.getPrincipal());
                    }
                    Principal prpal = pd.getPrincipal();
                    UIPermission perm = (UIPermission) pd.getPermission();
                    perm.setReadOnly(pd.isReadonly());
                    addPermission(prpal, perm);
                } catch (InstantiationException ie) {
                    log.error("Ignoring permission descriptor " + pd);
                }
            }
        }
    }

    public synchronized void delete() throws Exception {
        clear();
    }
}
