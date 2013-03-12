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

import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.hibernate.Session;

import java.lang.reflect.Constructor;
import java.security.Permission;
import java.security.Principal;

public class PermissionDescriptor {

    private static final transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PermissionDescriptor.class);

    // Constructor types

    private static final Class[] _prpalConstructorTypes = new Class[]{String.class};
    private static final Object[] _permConstructorsTypes = {new Class[]{String.class, String.class},
            new Class[]{String.class},
            new Class[]{}};

    // Properties

    private Long dbid;
    private String principalClass;
    private String principalName;
    private String permissionClass;
    private String permissionResource;
    private String permissionActions;

    public PermissionDescriptor() {
        this.dbid = null;
        this.principalClass = null;
        this.principalName = null;
        this.permissionClass = null;
        this.permissionResource = null;
        this.permissionActions = null;
    }

    public PermissionDescriptor(Principal prpal, Permission perm) {
        this();
        this.setPrincipal(prpal);
        this.setPermission(perm);
    }

    public String getItemClassName() {
        return PermissionDescriptor.class.getName();
    }

    public Long getDbid(){
        return dbid;
    }

    public void setDbid(Long id){
        dbid = id;
    }

    public String getPrincipalClass() {
        return principalClass;
    }

    public void setPrincipalClass(String principalClass) {
        this.principalClass = principalClass;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getPermissionClass() {
        return permissionClass;
    }

    public void setPermissionClass(String permissionClass) {
        this.permissionClass = permissionClass;
    }

    public String getPermissionResource() {
        return permissionResource;
    }

    public void setPermissionResource(String permissionResource) {
        this.permissionResource = permissionResource;
    }

    public String getPermissionActions() {
        return permissionActions;
    }

    public void setPermissionActions(String permissionActions) {
        this.permissionActions = permissionActions;
    }

    public Principal getPrincipal() throws InstantiationException {
        if (principalClass == null) return null;
        try {
            Class prpalClass = Class.forName(principalClass);
            Constructor constr = prpalClass.getConstructor(_prpalConstructorTypes);
            return (Principal) constr.newInstance(new Object[]{principalName});
        } catch (Exception ignored) {
        }

        // If Permission can't be instantiated then throw an exception.
        throw new InstantiationException("Principal class not supported (" + principalClass + ").");
    }

    public void setPrincipal(Principal prpal) {
        principalClass = null;
        principalName = null;

        if (prpal != null) {
            principalClass = prpal.getClass().getName();
            principalName = prpal.getName();
        }
    }

    public Permission getPermission() throws InstantiationException {
        if (permissionClass == null) return null;
        for (int i = 0; i < _permConstructorsTypes.length; i++) {
            try {
                Class permClass = Class.forName(permissionClass);
                Class[] constrTypes = (Class[]) _permConstructorsTypes[i];
                Constructor constr = permClass.getConstructor(constrTypes);
                Object[] constrParams = new Object[]{permissionResource, permissionActions};
                return (Permission) constr.newInstance(constrParams);
            } catch (Exception ignored) {
                log.error("Permission class not supported (" + permissionClass + ").");
            }
        }

        // If Permission can't be instantiated then throw an exception.
        throw new InstantiationException("Permission class not supported (" + permissionClass + ").");
    }

    public void setPermission(Permission perm) {
        permissionClass = null;
        permissionResource = null;
        permissionActions = null;

        if (perm != null) {
            permissionClass = perm.getClass().getName();
            permissionResource = perm.getName();
            permissionActions = perm.getActions();
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PermissionDescriptor)) return false;

        PermissionDescriptor that = (PermissionDescriptor) obj;

        if (permissionClass == null && that.permissionClass != null) return false;
        if (permissionResource == null && that.permissionResource != null) return false;
        if (principalClass == null && that.principalClass != null) return false;
        if (principalName == null && that.principalName != null) return false;

        if (permissionClass != null && !permissionClass.equals(that.permissionClass)) return false;
        if (permissionResource != null && !permissionResource.equals(that.permissionResource)) return false;
        if (principalClass != null && !principalClass.equals(that.principalClass)) return false;
        if (principalName != null && !principalName.equals(that.principalName)) return false;

        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<PermissionDescriptor: ");
        sb.append(" idPermission: " + dbid);
        sb.append(" principalClass: " + principalClass);
        sb.append(" principalName: " + principalName);
        sb.append(" permissionClass: " + permissionClass);
        sb.append(" permissionResource: " + permissionResource);
        sb.append(" permissionActions: " + permissionActions);
        sb.append(" >");
        return sb.toString();
    }

    // Persistent stuff

    public boolean isPersistent() {
        return dbid != null;
    }

    public boolean save() throws Exception {
        final boolean isTransient = !isPersistent();
        new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                if (isTransient) persist(0);
                else persist(1);
            }}.execute();
        return isTransient;
    }

    public boolean delete() throws Exception {
        if (!isPersistent()) return false;
        persist(2);
        return true;
    }

    protected void persist(final int op) throws Exception {
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            switch(op) {
                case 0: session.save(PermissionDescriptor.this);
                    break;
                case 1: session.update(PermissionDescriptor.this);
                    break;
                case 2: session.delete(PermissionDescriptor.this); break;
            }
            session.flush();
        }}.execute();
    }
}
