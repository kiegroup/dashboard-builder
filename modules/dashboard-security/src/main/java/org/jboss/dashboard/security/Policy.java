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

import org.jboss.dashboard.database.Persistent;

import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Principal;
import java.util.Map;
import javax.security.auth.Subject;

/**
 * Policy interface is a permissions manager and is used to grant
 * permissions to subjects.
 */
public interface Policy extends Serializable, Persistent {

    void addPermission(Permission perm);
    void addPermission(Principal p, Permission perm);
    void addPermission(Principal p, Permission perm, Boolean readonly);
    void removePermissions(Object resource) throws Exception;
    PermissionCollection getPermissions(Subject user);
    PermissionCollection getPermissions(Principal prpal);

    /**
     * Removes a permission attached to any principal.
     */
    void removePermission(Permission perm);

    /**
     * Removes a permission granted to the specified principal.
     * <p>
     * IMPORTANT NOTE: This policy implementation is based on java.security.PermissionCollection class.
     * Due to the fact that PermissionCollection doesn't offer any method for remove added permissions,
     * our removePermission implementation regenerates the full structure of permissions attached
     * to the principal for all permissions but those 'implied' by the permission to be removed.
     */
    void removePermission(Principal p, Permission perm);

    /**
     * Removes all permission over a resource granted to any principal.
     */
    void removePermissions(String resourceName);

    /**
     * Removes all permission over a resource granted to a specified principal.
     */
    void removePermissions(Principal p, String resourceName);

    /**
     * Retrieve permissions assigned to the specified resource.
     *
     * @param resource  The resource.
     * @param permClass The permission class.
     * @return A map containing the principal and the permission assigned to the resource for that principal.
     */
    Map getPermissions(Object resource, Class permClass) throws Exception;


    /**
     * Retrieve a permission.
     * @param prpal The Principal for which the permission must be assigned.
     * @param permClass The class type of the permission.
     * @param permName The name of the permission.
     * @return
     */
    Permission getPermission(Principal prpal, Class permClass, String permName);
}
