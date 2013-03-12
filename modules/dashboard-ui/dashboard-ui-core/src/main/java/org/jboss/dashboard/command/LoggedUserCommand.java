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
package org.jboss.dashboard.command;

import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.users.UserStatus;

/**
 * Command for the access to the logged user information.
 */
public class LoggedUserCommand extends AbstractCommand {

    public static final String LOGGED_USER_LOGIN     = "logged_user_login";
    public static final String LOGGED_USER_NAME      = "logged_user_name";
    public static final String LOGGED_USER_EMAIL     = "logged_user_email";

    public LoggedUserCommand(String commandName) {
        super(commandName);
    }

    public String execute() throws Exception {
        NavigationManager navMgr = NavigationManager.lookup();
        UserStatus userCtx = navMgr.getUserStatus();
        String commandName = getName();

        if (LOGGED_USER_LOGIN.equals(commandName)) return userCtx.getUserLogin();
        if (LOGGED_USER_NAME.equals(commandName)) return userCtx.getUserName();
        if (LOGGED_USER_EMAIL.equals(commandName)) return userCtx.getUserEmail();
        return null;
    }
}