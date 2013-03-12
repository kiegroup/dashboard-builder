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

import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.components.HandlerFactoryElement;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.jboss.dashboard.ui.utils.forms.FormStatus;
import org.jboss.dashboard.workspace.PanelProviderParameter;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.ui.utils.forms.RenderUtils;
import org.jboss.dashboard.users.UserStatus;

public abstract class PanelInstancePropertiesHandler extends HandlerFactoryElement {

    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PanelInstancePropertiesHandler.class.getName());
    private Long panelInstanceId;
    private String workspaceId;
    private FormStatus formStatus;

    public FormStatus getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(FormStatus formStatus) {
        this.formStatus = formStatus;
    }

    public Long getPanelInstanceId() {
        return panelInstanceId;
    }

    public void setPanelInstanceId(Long panelInstanceId) {
        this.panelInstanceId = panelInstanceId;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public CommandResponse actionSave(CommandRequest request) throws Exception {
        WorkspaceImpl workspace = NavigationManager.lookup().getCurrentWorkspace();
        WorkspacePermission workspacePerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_ADMIN);
        UserStatus.lookup().checkPermission(workspacePerm);

        PanelInstance instance = getPanelInstance();

        request.getRequestObject().setAttribute(PanelInstanceGeneralPropertiesFormatter.PANEL_INSTANCE, instance);
        PanelProviderParameter[] params = getPanelProviderParameters(instance);
        String language = formStatus.getValueAsString("lang");

        boolean anyParamWritten = false;

        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                String value = params[i].readFromRequest(request.getRequestObject());
                log.debug("Panel " + instance.getInstanceId() + " field:" + params[i].getId() + " = " + value);
                if (params[i].isI18n()) {
                    String oldValue = instance.getParameterValue(params[i].getId(), language);
                    if (!RenderUtils.noNull(oldValue).equals(RenderUtils.noNull(value)) && params[i].isValid(value)) {
                        instance.setParameterValue(params[i].getId(), value, language);
                        anyParamWritten = true;
                    }
                } else {
                    String oldValue = instance.getParameterValue(params[i].getId());
                    if (!RenderUtils.noNull(oldValue).equals(RenderUtils.noNull(value)) && params[i].isValid(value)) {
                        instance.setParameterValue(params[i].getId(), value);
                        anyParamWritten = true;
                    }
                }

                if (!params[i].isValid(value)) formStatus.addWrongField(params[i].getId());
            }
        }
        if (instance != null && anyParamWritten) {
            if (formStatus.getWrongFields().length == 0) {
                savePanelInstanceProperties(instance);
                formStatus.clearWrongFields();
            }
        }

        String lang = request.getParameter("editing");
        if (lang != null && lang.trim().length() > 0) formStatus.setValue("lang", lang);

        return new ShowCurrentScreenResponse();
    }

    public abstract PanelProviderParameter[] getPanelProviderParameters(PanelInstance instance);

    public abstract void savePanelInstanceProperties(PanelInstance instance) throws Exception;

    public abstract PanelInstance getPanelInstance(String workspaceId, Long panelId) throws Exception;

    public abstract PanelInstance getPanelInstance() throws Exception;
}
