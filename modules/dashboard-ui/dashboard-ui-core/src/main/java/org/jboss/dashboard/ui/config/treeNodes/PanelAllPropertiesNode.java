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

import javax.inject.Inject;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.utils.forms.FormStatus;
import org.jboss.dashboard.ui.components.panelManagement.ShowPanelConfigComponent;
import org.slf4j.Logger;

public class PanelAllPropertiesNode extends AbstractNode {

    @Inject
    private transient Logger log;

    @Inject
    private ShowPanelConfigComponent handler;

    public String getId() {
        return "PanelAllPropertiesNode";
    }

    public ShowPanelConfigComponent getHandler() {
        return handler;
    }

    public void setHandler(ShowPanelConfigComponent handler) {
        this.handler = handler;
    }

    public String getIconId() {
        return "16x16/ico-menu_properties.png";
    }

    public boolean isEditURIAjaxCompatible() {
        return false;
    }

    public boolean onEdit() {
        try {
            if (getParent() instanceof PanelNode) {
                PanelNode parent = (PanelNode) getParent();
                initHandler(parent.getWorkspaceId(), parent.getPanel().getDbid(), parent.getPanel().getInstanceId());
            } else {
                PanelInstanceNode parent = (PanelInstanceNode) getParent();
                initHandler(parent.getWorkspaceId(), null, parent.getPanelInstanceId());
            }
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }

    protected void initHandler(String workspaceId, Long panelId, Long panelInstanceId) {
        FormStatus formStatus = new FormStatus();
        formStatus.setValue("lang", LocaleManager.lookup().getCurrentLang());
        getHandler().setWorkspaceId(workspaceId);
        getHandler().setPanelId(panelId);
        getHandler().setPanelInstanceId(panelInstanceId);
        getHandler().clearFieldErrors();
        getHandler().setFormStatus(formStatus);
        getHandler().getMessagesComponentHandler().clearAll();
    }
}
