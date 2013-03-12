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

import java.security.Permission;
import java.security.PermissionCollection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.auth.Subject;

@ApplicationScoped
public class DefaultAccessController implements AccessController {

    @Inject
    private Policy securityPolicy;

    public Policy getPolicy() {
        return securityPolicy;
    }

    /**
     * Check the specified permission for the specified subject.
     * @param usr The subject for which permission is checked.
     * @param perm The permission to check.
     * @throws SecurityException if permission is not granted.
     */
    public void checkPermission(Subject usr, Permission perm) {
        if (hasPermission(usr, perm)) return;

        // Permission not granted.
        StringBuffer buf = new StringBuffer();
        buf.append("Permission ");
        buf.append(perm.toString()).append(" ");
        buf.append("not granted for the Subject.");
        throw new SecurityException(buf.toString());
    }

    /**
     * Check the specified permission for the specified subject.
     * @param usr The subject for which permission is checked.
     * @param perm The permission to check.
     */
    public boolean hasPermission(Subject usr, Permission perm) {
        PermissionCollection pc = securityPolicy.getPermissions(usr);
        if (pc.implies(perm)) return true;
        else return false;
    }
}
