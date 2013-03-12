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
package org.jboss.dashboard.users;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.dashboard.annotation.config.Config;

/**
 * Manager class for the platform roles.
 */
@ApplicationScoped
public class RolesManagerImpl implements RolesManager {

    @Inject @Config("anonymous=Anonymous,admin=Administrator,user=User")
    protected String[] enabledRoles;

    protected transient Map<String, Role> roles;

    @PostConstruct
    public void init() throws Exception {
        int numRoles = enabledRoles != null ? enabledRoles.length : 0;
        roles = new HashMap<String, Role>(numRoles);
        for (int i = 0; i < numRoles; i++) {
            final String[] arr = enabledRoles[i].split("=");
            if (arr.length != 2) throw new IllegalArgumentException("Error: illegal role definition");
            roles.put(arr[0], new Role() {
                public String getName() {
                    return arr[0];
                }
                public String getDescription(Locale l) {
                    return arr[1];
                }
            });
        }
    }

    public Role getRoleById(String id) {
        return roles.get(id);
    }

    public Set<Role> getAllRoles() {
        Set<Role> _roles = new HashSet<Role>(roles.size());
        _roles.addAll(roles.values());
        return _roles;
    }
}
