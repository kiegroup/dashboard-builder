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
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.components.panelInstance.PanelInstancePropertiesHandler;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.utils.forms.FormStatus;
import org.jboss.dashboard.workspace.Panel;

public abstract class PanelPropertiesNode extends AbstractNode {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PanelPropertiesNode.class.getName());

    private PanelInstancePropertiesHandler handler;
    private boolean multilanguage = false;

    public boolean isMultilanguage() {
        return multilanguage;
    }

    public void setMultilanguage(boolean multilanguage) {
        this.multilanguage = multilanguage;
    }

    public PanelInstancePropertiesHandler getHandler() {
        return handler;
    }

    public void setHandler(PanelInstancePropertiesHandler handler) {
        this.handler = handler;
    }

    public boolean onEdit() {
        try {
            TreeNode parent = getParent();
            if (parent instanceof PanelNode) {
                getHandler().setWorkspaceId(((PanelNode)parent).getWorkspaceId());
                getHandler().setPanelInstanceId(((PanelNode) parent).getPanel().getInstanceId());
                getHandler().clearFieldErrors();
                prepareConfigure(((PanelNode)parent).getPanel());
            } else if (parent instanceof PanelInstanceNode) {
                getHandler().setWorkspaceId(((PanelInstanceNode)parent).getWorkspaceId());
                getHandler().setPanelInstanceId(((PanelInstanceNode) parent).getPanelInstanceId());
                getHandler().clearFieldErrors();
            }
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }

    private void prepareConfigure(Panel panel) {
        // Store panel in session
        // TODO What the fuck is this shit
        SessionManager.setCurrentPanel(panel);

        FormStatus formStatus = new FormStatus();
        formStatus.setValue("lang", SessionManager.getLang());
        formStatus.setValue("multilanguage", isMultilanguage());

        getHandler().setFormStatus(formStatus);
        // Store form status in session
        //SessionManager.setCurrentFormStatus(formStatus);

    }

}
