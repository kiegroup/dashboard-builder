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
package org.jboss.dashboard;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.dashboard.security.AccessController;
import org.jboss.dashboard.security.PermissionManager;
import org.jboss.dashboard.security.Policy;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.users.RolesManager;

@ApplicationScoped
@Named("securityServices")
public class SecurityServices {

    public static SecurityServices lookup() {
        return (SecurityServices) CDIBeanLocator.getBeanByName("securityServices");
    }

    @Inject
    protected RolesManager rolesManager;

    @Inject
    protected PermissionManager permissionManager;

    @Inject
    protected AccessController accessController;

    @Inject
    protected Policy securityPolicy;

    public RolesManager getRolesManager() {
        return rolesManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public AccessController getAccessController() {
        return accessController;
    }

    public Policy getSecurityPolicy() {
        return securityPolicy;
    }
}
