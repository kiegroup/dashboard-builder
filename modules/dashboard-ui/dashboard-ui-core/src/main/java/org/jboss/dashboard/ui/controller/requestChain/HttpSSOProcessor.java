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

