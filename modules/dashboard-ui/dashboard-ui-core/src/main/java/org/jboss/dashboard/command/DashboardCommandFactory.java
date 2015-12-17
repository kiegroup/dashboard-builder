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

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.function.ScalarFunction;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DashboardCommandFactory implements CommandFactory {

    public Command createCommand(String commandName) {

        // Scalar function commands.
        ScalarFunction[] scalarFunctions = DataDisplayerServices.lookup().getScalarFunctionManager().getAllScalarFunctions();
        for (int i = 0; i < scalarFunctions.length; i++) {
            ScalarFunction scalarFunction = scalarFunctions[i];
            String targetName = "dashboard_" + scalarFunction.getCode();
            if (commandName.equals(targetName)) return new ScalarFunctionCommand(targetName, scalarFunction);
        }

        // Command that gives access to the filter properties in the current dashboard.
        if (commandName.equals(DashboardFilterCommand.FILTER_MIN_VALUE)) return new DashboardFilterCommand(DashboardFilterCommand.FILTER_MIN_VALUE);
        if (commandName.equals(DashboardFilterCommand.FILTER_MAX_VALUE)) return new DashboardFilterCommand(DashboardFilterCommand.FILTER_MAX_VALUE);
        if (commandName.equals(DashboardFilterCommand.FILTER_SELECTED)) return new DashboardFilterCommand(DashboardFilterCommand.FILTER_SELECTED);
        if (commandName.equals(DashboardFilterCommand.FILTER_ALL)) return new DashboardFilterCommand(DashboardFilterCommand.FILTER_ALL);

        // Commands to access the current workspace navigation context.
        if (commandName.equals(WorkspaceNavigationCommand.WORKSPACE_ID)) return new WorkspaceNavigationCommand(WorkspaceNavigationCommand.WORKSPACE_ID);
        if (commandName.equals(WorkspaceNavigationCommand.PAGE_ID)) return new WorkspaceNavigationCommand(WorkspaceNavigationCommand.PAGE_ID);
        if (commandName.equals(WorkspaceNavigationCommand.WORKSPACE_TITLE)) return new WorkspaceNavigationCommand(WorkspaceNavigationCommand.WORKSPACE_TITLE);
        if (commandName.equals(WorkspaceNavigationCommand.PAGE_TITLE)) return new WorkspaceNavigationCommand(WorkspaceNavigationCommand.PAGE_TITLE);
        if (commandName.equals(WorkspaceNavigationCommand.LANGUAGE)) return new WorkspaceNavigationCommand(WorkspaceNavigationCommand.LANGUAGE);
        if (commandName.equals(WorkspaceNavigationCommand.USER_LOGIN)) return new WorkspaceNavigationCommand(WorkspaceNavigationCommand.USER_LOGIN);
        if (commandName.equals(WorkspaceNavigationCommand.USER_NAME)) return new WorkspaceNavigationCommand(WorkspaceNavigationCommand.USER_NAME);
        if (commandName.equals(WorkspaceNavigationCommand.USER_EMAIL)) return new WorkspaceNavigationCommand(WorkspaceNavigationCommand.USER_EMAIL);

        // Commands for the access to the logged user information.
        if (commandName.equals(LoggedUserCommand.LOGGED_USER_LOGIN)) return new LoggedUserCommand(LoggedUserCommand.LOGGED_USER_LOGIN);
        if (commandName.equals(LoggedUserCommand.LOGGED_USER_NAME)) return new LoggedUserCommand(LoggedUserCommand.LOGGED_USER_NAME);
        if (commandName.equals(LoggedUserCommand.LOGGED_USER_EMAIL)) return new LoggedUserCommand(LoggedUserCommand.LOGGED_USER_EMAIL);
        return null;
    }
}
