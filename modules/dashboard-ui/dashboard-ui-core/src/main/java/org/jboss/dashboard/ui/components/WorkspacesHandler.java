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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.HandlerFactoryElement;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;

public class WorkspacesHandler extends HandlerFactoryElement {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WorkspacesHandler.class.getName());
    private String navigationManager;

    public String getNavigationManager(){
        return navigationManager;
    }

    public void setNavigationManager(String navigationManager) {
        this.navigationManager = navigationManager;
    }

    protected NavigationManager getNavigator() {
        return (NavigationManager) Factory.lookup(navigationManager);
    }

    public CommandResponse actionShowScreen(CommandRequest request) {
        return null;
    }


    public CommandResponse actionDispatch(CommandRequest request) {
        return null;
    }

    protected void beforeInvokeAction(CommandRequest request, String action) {
        //Old commands depended on these parameters to affect navigation status
        String workspaceId = request.getRequestObject().getParameter("idWorkspace");
        String pageId = request.getRequestObject().getParameter("idSection");
        try {
            if (!StringUtils.isEmpty(workspaceId) && !StringUtils.isEmpty(pageId)) {
                WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
                Section page = workspace.getSection(Long.decode(pageId));
                getNavigator().setCurrentSection(page);
            }
        } catch (Exception e) {
            log.error("Error in beforeInvokeAction: ", e);
        }
    }
}
