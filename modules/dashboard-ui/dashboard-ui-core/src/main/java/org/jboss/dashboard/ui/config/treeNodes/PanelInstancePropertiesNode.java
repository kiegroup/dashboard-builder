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
package org.jboss.dashboard.ui.config.treeNodes;

import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.components.panelInstance.PanelInstancePropertiesHandler;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.utils.forms.FormStatus;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.WorkspacesManager;

public abstract class PanelInstancePropertiesNode extends AbstractNode {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelInstancePropertiesNode.class.getName());
    private PanelInstancePropertiesHandler handler;
    private WorkspacesManager workspacesManager;
    private boolean multilanguage = false;

    public boolean isMultilanguage() {
        return multilanguage;
    }

    public void setMultilanguage(boolean multilanguage) {
        this.multilanguage = multilanguage;
    }

    public WorkspacesManager getWorkspacesManager() {
        return workspacesManager;
    }

    public void setWorkspacesManager(WorkspacesManager workspacesManager) {
        this.workspacesManager = workspacesManager;
    }

    public PanelInstancePropertiesHandler getHandler() {
        return handler;
    }

    public void setHandler(PanelInstancePropertiesHandler handler) {
        this.handler = handler;
    }

    public boolean onEdit() {
        try {
            PanelInstanceNode parent = (PanelInstanceNode) getParent();
            getHandler().setWorkspaceId(parent.getWorkspaceId());
            getHandler().setPanelInstanceId(parent.getPanelInstanceId());
            getHandler().clearFieldErrors();
            prepareConfigure(parent.getPanelInstance());
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }

    private void prepareConfigure(PanelInstance instance) {
        // We'll work on a cloned copy to
        PanelInstance panel = instance.getPartialClonedCopy();

        // Store panel in session
        SessionManager.setCurrentPanel(panel);

        FormStatus formStatus = new FormStatus();
        formStatus.setValue("lang", SessionManager.getLang());
        formStatus.setValue("multilanguage", isMultilanguage());

        getHandler().setFormStatus(formStatus);
    }
}
