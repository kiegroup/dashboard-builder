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

import java.lang.reflect.Constructor;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.*;

public class DefaultPermission extends Permission {

    // Permission grant codes
    //

    public static final int PERMISSION_GRANTED = 1;
    public static final int PERMISSION_NOT_GRANTED = 0;
    public static final int PERMISSION_DENIED = -1;

    // Properties
    //

    private Permission basicPerm;
    private List<String> actionGrantedList;
    private List<String> actionDeniedList;

    /**
     * @link aggregationByValue
     * @supplierCardinality 1
     * @directed
     */
    private BasicPermission lnkBasicPermission;
    // Constructor(s)
    //

    /**
     * Creates a permission over the specified resource.
     *
     * @param resourceName A path to a resource. The naming
     *                     convention follows the hierarchical property naming convention.
     *                     The path format is the following: workspace{workspaceId}>.section{sectionId}.panel{panelId}.<br>
     *                     An asterisk may appear at the end of the name, following a ".", or by itself, to
     *                     signify a wildcard equals. For example: java.*, "workspace101.section102.*"
     */
    public DefaultPermission(String resourceName, String actions) {
        super(resourceName);
        basicPerm = new BasicPermissionImpl(resourceName);
        actionGrantedList = new ArrayList<String>();
        actionDeniedList = new ArrayList<String>();
        setActions(actions);
    }

    public void setResourceName(String resourceName) {
        basicPerm = new BasicPermissionImpl(resourceName);
    }

    public String getResourcePath() {
        if (basicPerm == null) return "";
        else return basicPerm.getName();
    }

    public void setActions(String actions) {
        if (actions == null) return;
        actionGrantedList = toActionGrantedList(actions);
        actionDeniedList = toActionDeniedList(actions);
    }

    public void grantAction(String action) {
        if (!actionGrantedList.contains(action)) actionGrantedList.add(action);
        actionDeniedList.remove(action);
    }

    public void denyAction(String action) {
        if (!actionDeniedList.contains(action)) actionDeniedList.add(action);
        actionGrantedList.remove(action);
    }

    public void removeAction(String action) {
        actionGrantedList.remove(action);
        actionDeniedList.remove(action);
    }

    public boolean isActionGranted(String action) {
        return actionGrantedList.contains(action);
    }

    public boolean isActionDenied(String action) {
        return actionDeniedList.contains(action);
    }

    public boolean isActionUndefined(String action) {
        return !actionGrantedList.contains(action) &&
                !actionDeniedList.contains(action);
    }

    /**
     * If no actions are defined for this permission then assume as empty.
     */
    public boolean isEmpty() {
        return actionGrantedList.isEmpty() && actionDeniedList.isEmpty();
    }

    /**
     * Check both if this permission implies or denies the specified one.
     *
     * @param p The permission to check.
     * @return A grant code. See constants definition at top of this class.
     */
    public int impliesOrDenies(Permission p) {
        // Check instances
        if (p == null || !(p instanceof DefaultPermission)) return PERMISSION_NOT_GRANTED;
        DefaultPermission that = (DefaultPermission) p;

        // Check name
        if (!basicPerm.implies(that.basicPerm)) return PERMISSION_NOT_GRANTED;

        // Check granted actions.
        // This permission must grant at least same actions as 'that'
        for (String thatAction : that.actionGrantedList) {
            if (this.isActionDenied(thatAction)) return PERMISSION_DENIED;
            else if (this.isActionUndefined(thatAction)) return PERMISSION_NOT_GRANTED;
        }

        // Check denied actions.
        // This permission must not grant any denied action in 'that'
        for (String thatAction : that.actionDeniedList) {
            if (this.isActionGranted(thatAction)) return PERMISSION_DENIED;
            else if (this.isActionUndefined(thatAction)) return PERMISSION_NOT_GRANTED;
        }

        // All checks satisfied
        return PERMISSION_GRANTED;
    }

    // java.security.Permission interface
    //

    public boolean implies(Permission p) {
        switch (impliesOrDenies(p)) {
            case PERMISSION_GRANTED:
                return true;
            default:
                return false;
        }
    }

    public PermissionCollection newPermissionCollection() {
        return new DefaultPermissionCollection();
    }

    /**
     * Checks two BasicPermission objects for equality.
     * Checks that <i>obj</i>'s class is the same as this object's class
     * and has the same name as this object.
     * <p/>
     *
     * @param obj the object we are testing for equality with this object.
     * @return true if <i>obj</i> is a BasicPermission, and has the same name
     *         as this BasicPermission object, false otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if ((obj == null) || (obj.getClass() != getClass())) return false;

        DefaultPermission bp = (DefaultPermission) obj;
        return getName().equals(bp.getName());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    /**
     * @return Actions granted/denied by this permission instance.<br>
     *         The string format is a list of words separated each one by a comma-character.<br>
     *         An action is denied if is preceeded by the character "!". Samples are:<br>
     *         "view" or "view, !edit".
     */
    public String getActions() {
        return toActionListString();
    }

    // Action list to string conversion methods
    //

    protected String toActionListString() {
        StringBuffer buf = new StringBuffer();
        for (String action : actionGrantedList) {
            if (buf.length() > 0) buf.append(",");
            buf.append(action);
        }
        for (String action : actionDeniedList) {
            if (buf.length() > 0) buf.append(",");
            buf.append("!").append(action);
        }
        return buf.toString();
    }

    protected List<String> toActionGrantedList(String actionsString) {
        List<String> l = new ArrayList<String>();
        StringTokenizer strtok = new StringTokenizer(actionsString, ",");
        while (strtok.hasMoreTokens()) {
            String action = strtok.nextToken().trim();
            if (!action.startsWith("!")) l.add(action);
        }
        return l;
    }

    protected List<String> toActionDeniedList(String actionsString) {
        List<String> l = new ArrayList<String>();
        StringTokenizer strtok = new StringTokenizer(actionsString, ",");
        while (strtok.hasMoreTokens()) {
            String action = strtok.nextToken().trim();
            if (action.startsWith("!")) l.add(action.substring(1));
        }
        return l;
    }

    public String toString() {
        return this.getClass().getName() + " " + getName() + " " + toActionListString();
    }

    List<DefaultPermission> toSimplePermissionList() {
        List<DefaultPermission> l = new ArrayList<DefaultPermission>();
        try {
            for (String action : actionGrantedList) {
                // Use reflection in order to create instances of the same class of this one.
                Constructor constr = this.getClass().getConstructors()[0];
                DefaultPermission dp = (DefaultPermission) constr.newInstance(new Object[]{this.getName(), null});
                dp.grantAction(action);
                l.add(dp);
            }
            for (String action : actionDeniedList){
                Constructor constr = this.getClass().getConstructors()[0];
                DefaultPermission dp = (DefaultPermission) constr.newInstance(new Object[]{this.getName(), null});
                dp.denyAction(action);
                l.add(dp);
            }
        }
        catch (Exception e) {
            System.out.println("Can't split permission into a simple permission list.");
            System.out.println("Empty permission list is returned and full access is gained.");
            e.printStackTrace();
        }
        return l;
    }
}

/**
 * A BasicPermission implementation.
 * This class is needed because BasicPermission is an abstract class.
 */
final class BasicPermissionImpl extends BasicPermission {

    public BasicPermissionImpl(String name) {
        super(name);
    }
}

/**
 * A DefaultPermissionCollection stores a collection
 * of DefaultPermission permissions. DefaultPermission objects
 * must be stored in a manner that allows them to be inserted in any
 * order, but enable the implies function to evaluate the implies
 * method in an efficient (and consistent) manner.
 * <p/>
 * A DefaultPermissionCollection handles comparing a permission like "a.b.c.d.e"
 * with a Permission such as "a.b.*", or "*".
 *
 * @see java.security.Permission
 * @see java.security.Permissions
 */
final class DefaultPermissionCollection extends PermissionCollection
        implements java.io.Serializable {

    /**
     * Stores permission classified by permission name.
     * This it allows to quickly fetch permission instances using its name.
     * Key is a permission name: <i>DefaulPermission.getName()</i>.
     * Value is a list of permission instances.
     */
    private Hashtable permissionMap;

    /**
     * Stores permission instances
     */
    private Vector permissionList;

    /**
     * Create an empty DefaultPermissions object.
     */
    public DefaultPermissionCollection() {
        permissionMap = new Hashtable(11);
        permissionList = new Vector();
    }

    /**
     * Adds a permission to the DefaultPermissions. The key for the hash is
     * permission.path.
     *
     * @param permission the Permission object to add.
     * @throws IllegalArgumentException - if the permission is not a
     *                                  DefaultPermission
     * @throws SecurityException        - if this DefaultPermissionCollection object
     *                                  has been marked readonly
     */

    public void add(Permission permission) {
        if (!(permission instanceof DefaultPermission)) {
            throw new IllegalArgumentException("Invalid permission: " + permission);
        }
        if (isReadOnly()) {
            throw new SecurityException("Attempt to add a Permission to a readonly PermissionCollection");
        }

        DefaultPermission bp = (DefaultPermission) permission;
        if (!permissionMap.containsKey(bp.getName())) permissionMap.put(bp.getName(), new Vector());
        Vector permInstances = (Vector) permissionMap.get(bp.getName());
        addPermissionToVector(permInstances, permission);
        addPermissionToVector(permissionList, permission);
    }

    /**
     * Add a permission to a vector.
     * If the permission is already stored into the vector or "implied"  by any of its elements
     * then is discarded.
     *
     * @return A flag indicating if the permission has been added or not to the vector.
     */
    private boolean addPermissionToVector(Vector list, Permission permission) {
        // If the permission is not stored into the collection the store it.
        return list.add(permission);
    }

    /**
     * Check and see if this set of permissions implies the permissions
     * expressed in "permission".
     *
     * @param permission the Permission object to compare
     * @return true if "permission" is a proper subset of a permission in
     *         the set, false if not.
     */
    public boolean implies(Permission permission) {
        try {
            DefaultPermission dp = (DefaultPermission) permission;

            // Split specified permission in to a list of simple permission instances.
            // Each one of this instances talks only about an action.
            // The permission is implied by the collection only if all its simple permissions are implied.
            Iterator it = dp.toSimplePermissionList().iterator();
            while (it.hasNext()) {
                DefaultPermission simplePerm = (DefaultPermission) it.next();
                int result = impliesOrDenies(simplePerm);

                // If simple permission is denied or not granted then abort.
                if (result == DefaultPermission.PERMISSION_DENIED) return false;
                if (result == DefaultPermission.PERMISSION_NOT_GRANTED) return false;
            }
            // If all checks are ok return true.
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    private int impliesOrDenies(DefaultPermission simplePerm) {

        // Strategy:
        // Check for full equals first. Then work our way up the
        // path looking for matches on a.b..*
        String path = simplePerm.getName();

        // Check if we have a direct hit.
        int result = impliesOrDenies(simplePerm, path);
        if (result == DefaultPermission.PERMISSION_DENIED) return DefaultPermission.PERMISSION_DENIED;
        if (result == DefaultPermission.PERMISSION_GRANTED) return DefaultPermission.PERMISSION_GRANTED;

        // Work our way up the tree...
        int last, offset;
        offset = path.length() - 1;
        while ((last = path.lastIndexOf(".", offset)) != -1) {
            path = path.substring(0, last + 1) + "*";
            result = impliesOrDenies(simplePerm, path);
            if (result == DefaultPermission.PERMISSION_DENIED) return DefaultPermission.PERMISSION_DENIED;
            if (result == DefaultPermission.PERMISSION_GRANTED) return DefaultPermission.PERMISSION_GRANTED;
            offset = last - 1;
        }

        // Last chance: check *
        result = impliesOrDenies(simplePerm, "*");
        if (result == DefaultPermission.PERMISSION_DENIED) return DefaultPermission.PERMISSION_DENIED;
        if (result == DefaultPermission.PERMISSION_GRANTED) return DefaultPermission.PERMISSION_GRANTED;

        // Otherwise return false
        return DefaultPermission.PERMISSION_NOT_GRANTED;
    }

    private int impliesOrDenies(DefaultPermission simplePerm, String path) {
        List permInstances = (List) permissionMap.get(path);
        if (permInstances == null) return DefaultPermission.PERMISSION_NOT_GRANTED;

        // It can exits more than one permission instance for the same resource.
        boolean permissionGranted = false;
        Iterator permIt = permInstances.iterator();
        while (permIt.hasNext()) {
            DefaultPermission x = (DefaultPermission) permIt.next();
            if (x != null) {
                int result = x.impliesOrDenies(simplePerm);

                // Only one permission instance deny is required to deny the permission.
                if (result == DefaultPermission.PERMISSION_DENIED) return DefaultPermission.PERMISSION_DENIED;

                // If permission is granted continue the analysis because a deny can occurs.
                if (result == DefaultPermission.PERMISSION_GRANTED) permissionGranted = true;
            }
        }
        // Return result.
        if (permissionGranted) return DefaultPermission.PERMISSION_GRANTED;
        else return DefaultPermission.PERMISSION_NOT_GRANTED;
    }

    /**
     * Returns an enumeration of all the DefaultPermission objects in the
     * container.
     *
     * @return an enumeration of all the DefaultPermission objects.
     */
    public Enumeration elements() {
        return permissionList.elements();
    }
}
