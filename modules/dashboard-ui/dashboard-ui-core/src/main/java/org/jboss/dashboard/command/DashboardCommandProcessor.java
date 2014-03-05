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

import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.ui.controller.RequestContext;

import javax.enterprise.inject.Specializes;

/**
 * Command processor that gives commands access to the current dashboard filter.
 */
@Specializes
public class DashboardCommandProcessor extends CommandProcessorImpl {

    @Override
    protected Command createCommand(String commandName) {
        Command command = super.createCommand(commandName);
        if (command != null && RequestContext.lookup() != null) {

            // Pass the current dashboard filter to the command.
            Dashboard dashboard = DashboardHandler.lookup().getCurrentDashboard();
            command.setDataFilter(dashboard.getDashboardFilter());

        }
        return command;
    }
}
