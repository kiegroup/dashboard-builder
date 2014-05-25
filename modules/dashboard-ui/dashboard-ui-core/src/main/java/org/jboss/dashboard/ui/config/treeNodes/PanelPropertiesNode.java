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

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.components.panelInstance.PanelInstancePropertiesHandler;
import org.jboss.dashboard.ui.utils.forms.FormStatus;
import org.jboss.dashboard.workspace.Panel;
import org.slf4j.Logger;

public abstract class PanelPropertiesNode extends AbstractNode {

    @Inject
    private transient Logger log;

    @Inject @Config("false")
    private boolean multilanguage;

    public boolean isMultilanguage() {
        return multilanguage;
    }

    public void setMultilanguage(boolean multilanguage) {
        this.multilanguage = multilanguage;
    }

    public abstract PanelInstancePropertiesHandler getHandler();

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
        FormStatus formStatus = new FormStatus();
        formStatus.setValue("lang", LocaleManager.currentLang());
        formStatus.setValue("multilanguage", isMultilanguage());

        getHandler().setFormStatus(formStatus);
    }
}
