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
package org.jboss.dashboard.ui.config.components.panelInstance;

import org.jboss.dashboard.ui.config.ConfigurationTree;
import org.jboss.dashboard.ui.config.ConfigurationTreeStatus;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.config.Tree;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.TreeStatus;
import org.jboss.dashboard.ui.utils.forms.FormStatus;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelProviderParameter;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class PanelInstancePropertiesFormatter extends Formatter {

    public static final String PANEL_INSTANCE_PROPERTIES = "_panelInstanceProperties";
    public static final String PANEL_INSTANCE = "_panelInstance";
    public static final String FORM_STATUS = "_formStatus";

    @Inject
    private transient Logger log;

    @Inject
    private ConfigurationTree configTree;

    @Inject
    private ConfigurationTreeStatus treeStatus;

    private String workspaceId;
    private Long panelInstanceId;

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getPortleInstancetId() {
        return panelInstanceId;
    }

    public void setPanelInstanceId(Long panelId) {
        this.panelInstanceId = panelId;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        try {
            renderFragment("outputStart");
            PanelProviderParameter p[] = getPanelProviderParameters();

            request.setAttribute(PanelInstancePropertiesFormatter.PANEL_INSTANCE_PROPERTIES, p);
            request.setAttribute(PanelInstancePropertiesFormatter.PANEL_INSTANCE, getPanelInstance());
            request.setAttribute(PanelInstancePropertiesFormatter.FORM_STATUS, getFormStatus());

            TreeNode currentNode = treeStatus.getLastEditedNode(configTree);

            setAttribute("title",
                    getPanelInstance().getProvider().getResource(
                            getPanelInstance().getProvider().getDescription())
                            + " - " +
                            currentNode.getDescription(getLocale()));
            renderFragment("output");
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error loading panelParameters", e);
        }
    }

    public abstract PanelProviderParameter[] getPanelProviderParameters() throws Exception;

    public abstract PanelInstance getPanelInstance() throws Exception;

    public abstract FormStatus getFormStatus();

}
