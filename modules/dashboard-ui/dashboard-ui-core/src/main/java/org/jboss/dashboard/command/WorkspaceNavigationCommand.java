/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.users.UserStatus;

/**
 * Command for the access to the current workspace navigation context.
 */
public class WorkspaceNavigationCommand extends AbstractCommand {

    public static final String WORKSPACE_ID     = "navigation_workspace_id";
    public static final String WORKSPACE_TITLE  = "navigation_workspace_title";
    public static final String PAGE_ID          = "navigation_page_id";
    public static final String PAGE_TITLE       = "navigation_page_title";
    public static final String LANGUAGE         = "navigation_language";
    public static final String USER_LOGIN       = "navigation_user_login";
    public static final String USER_NAME        = "navigation_user_name";
    public static final String USER_EMAIL       = "navigation_user_email";

    public WorkspaceNavigationCommand(String commandName) {
        super(commandName);
    }

    public String execute() throws Exception {
        LocaleManager localeMgr = LocaleManager.lookup();
        NavigationManager navMgr = NavigationManager.lookup();
        UserStatus userCtx = navMgr.getUserStatus();
        String commandName = getName();

        if (WORKSPACE_ID.equals(commandName)) return navMgr.getCurrentWorkspaceId();
        if (WORKSPACE_TITLE.equals(commandName)) return localeMgr.localize(navMgr.getCurrentWorkspace().getTitle()).toString();
        if (PAGE_ID.equals(commandName)) return navMgr.getCurrentSectionId().toString();
        if (PAGE_TITLE.equals(commandName)) return localeMgr.localize(navMgr.getCurrentSection().getTitle()).toString();
        if (LANGUAGE.equals(commandName)) return localeMgr.getCurrentLang();
        if (USER_LOGIN.equals(commandName)) return userCtx.getUserLogin();
        if (USER_NAME.equals(commandName)) return userCtx.getUserName();
        if (USER_EMAIL.equals(commandName)) return userCtx.getUserEmail();
        return null;
    }
}
