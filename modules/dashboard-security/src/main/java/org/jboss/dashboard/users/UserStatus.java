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

import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.security.AccessController;
import org.jboss.dashboard.security.SecurityCache;
import org.jboss.dashboard.security.principals.ComplementaryRolePrincipal;
import org.jboss.dashboard.security.principals.RolePrincipal;
import org.jboss.dashboard.security.principals.UserPrincipal;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.security.Permission;
import java.security.Principal;
import java.util.*;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.auth.Subject;

/**
 * This session Factory component stores the actual user information. This component provides configuration and methods to manage the platform user access.
 */
@SessionScoped
@Named("userStatus")
public class UserStatus implements LogoutSurvivor, Serializable {

    public static UserStatus lookup() {
        return (UserStatus) CDIBeanLocator.getBeanByName("userStatus");
    }

    private static transient Logger log = LoggerFactory.getLogger(UserStatus.class.getName());

    @Inject @Config("root")
    protected String rootLogin;

    @Inject @Config("root")
    protected String rootUserName;

    @Inject
    protected SecurityCache securityCache;

    @Inject @Install
    protected Instance<UserStatusListener> statusChangedListeners;

    protected transient String userLogin;
    protected transient String userName;
    protected transient String userEmail;
    protected transient Set<String> userRoleIds;
    protected transient Subject userAuth;

    public UserStatus() {
        userRoleIds = new HashSet<String>();
        clear();
        userRoleIds.add(Role.ANONYMOUS);
    }

    public String getRootLogin() {
        return rootLogin;
    }

    public void setRootLogin(String rootLogin) {
        this.rootLogin = rootLogin;
    }

    public String getRootUserName() {
        return rootUserName;
    }

    public void setRootUserName(String rootUserName) {
        this.rootUserName = rootUserName;
    }

    protected void notifyStatusChanged() {
        for (UserStatusListener statusChangedListener : statusChangedListeners) {
            statusChangedListener.statusChanged(this);
        }
    }

    public AccessController getAccessController() {
        return SecurityServices.lookup().getAccessController();
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserName() {
        return isLoggedIn() ? userName : "--";
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Set<String> getUserRoleIds() {
        return userRoleIds;
    }

    public void setUserRoleIds(Set<String> userRoleIds) {
        this.userRoleIds = userRoleIds;
    }

    /**
     * Determine if currently logged user is root user.
     *
     * @return true if currently logged user is root user.
     */
    public boolean isRootUser() {
        return userLogin != null && userLogin.equals(rootLogin);
    }

    /**
     * @return true if the user login is not a blank String
     */
    public boolean isLoggedIn() {
        return !StringUtils.isBlank(userLogin);
    }

    /**
     * Determine if there is no currently logged in user.
     *
     * @return true if there is no currently logged in user.
     */
    public boolean isAnonymous() {
        return StringUtils.isBlank(userLogin);
    }

    /**
     * Init a user session with the give credentials.
     */
    public synchronized void initSession(String login, Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            throw new IllegalArgumentException("User session initialization failed: the list of roles is empty.");
        }
        clear();
        userLogin = login;
        userRoleIds.addAll(roleIds);
        invalidateUserPrincipals();
        notifyStatusChanged();
    }

    /**
     * Init a user session as root.
     */
    public synchronized void initSessionAsRoot() {
        clear();
        userLogin = rootLogin;
        userName = rootUserName;
        invalidateUserPrincipals();
        notifyStatusChanged();
    }

    /**
     * Close the user session.
     */
    public synchronized void closeSession() {
        clear();
        userRoleIds.add(Role.ANONYMOUS);
        invalidateUserPrincipals();
        notifyStatusChanged();
    }

    /**
     * Invalidates the user principals due to external changes affecting them.
     */
    public synchronized void invalidateUserPrincipals() {
        if (log.isDebugEnabled()) {
            log.debug("Security information is obsolete. Clearing.");
        }
        securityCache.clear();
        Set<Principal> userPrincipals = calculateUserPrincipals();
        userAuth =  new Subject(false, userPrincipals, new HashSet(), new HashSet());
    }

    /**
     * Determine if current user has given permission.
     *
     * @param perm permission to check
     * @throws SecurityException if permission is denied
     */
    public void checkPermission(Permission perm) throws SecurityException {
        if (!hasPermission(perm))
            throw new SecurityException("Permission denied.\r\n" +
                    "permission=" + perm.toString() + "\r\n");
    }

    /**
     * Determine if current user has given permission.
     *
     * @param perm permission to check
     * @return true if current user has given permission.
     */
    public boolean hasPermission(Permission perm) {
        if (isRootUser()) return true;
        if (securityCache.isCacheEnabled()) {
            Boolean b = securityCache.getValue(perm, perm.getActions());
            if (b != null) {
                return b.booleanValue();
            }
        }
        boolean result = evaluatePermission(perm);
        if (securityCache.isCacheEnabled()) {
            securityCache.setValue(perm, perm.getActions(), result);
        }
        return result;
    }

    // Protected stuff

    protected void clear() {
        userLogin = null;
        userName = null;
        userEmail = null;
        userAuth = null;
        userRoleIds.clear();
    }

    protected boolean evaluatePermission(Permission perm) {
        if (userAuth == null) return false;

        if (log.isDebugEnabled()) log.debug("Invoking accessController to determine if permission " + perm + " is granted.");
        boolean result = getAccessController().hasPermission(userAuth, perm);
        if (log.isDebugEnabled()) log.debug("AccessController determines that permission " + perm + " is " + (result ? "" : "NOT ") + "granted.");
        return result;
    }

    protected Set<Principal> calculateUserPrincipals() {
        if (log.isDebugEnabled()) log.debug("Calculating principals for current user.");

        Set<Principal> principals = new HashSet<Principal>();
        if (userLogin != null) principals.add(new UserPrincipal(userLogin));
        if (!isRootUser()) {
            RolesManager rolesManager = SecurityServices.lookup().getRolesManager();
            for (Role role : rolesManager.getAllRoles()) {
                Principal rolePrincipal = null;
                if (userRoleIds.contains(role.getName())) rolePrincipal = new RolePrincipal(role);
                else rolePrincipal = new ComplementaryRolePrincipal(role);
                principals.add(rolePrincipal);
            }
        }
        return principals;
    }
}
