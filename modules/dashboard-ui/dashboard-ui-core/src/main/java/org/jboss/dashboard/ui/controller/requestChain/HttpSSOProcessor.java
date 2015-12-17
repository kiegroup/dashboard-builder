/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jboss.dashboard.ui.controller.requestChain;

import java.util.HashSet;
import java.util.Set;

import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.users.Role;
import org.jboss.dashboard.users.RolesManager;
import org.jboss.dashboard.users.UserStatus;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;

@ApplicationScoped
public class HttpSSOProcessor extends AbstractChainProcessor {

    public boolean processRequest() throws Exception {
        HttpServletRequest request = getHttpRequest();
        String login = request.getRemoteUser();
        UserStatus us = UserStatus.lookup();

        // Catch J2EE container login requests.
        if (!StringUtils.isBlank(login) && us.isAnonymous()) {

            // Login as root.
            if (us.getRootLogin().equals(login)) {
                us.initSessionAsRoot();
            }
            // Login as normal user.
            else {
                Set<String> roleIds = new HashSet<String>();
                Set<Role> roles = getRolesManager().getAllRoles();
                for (Role role : roles) {
                    String roleId = role.getName();
                    if (request.isUserInRole(roleId)) roleIds.add(roleId);
                }
                us.initSession(login, roleIds);
            }
        }
        return true;
    }

    public RolesManager getRolesManager() {
        return SecurityServices.lookup().getRolesManager();
    }
}

