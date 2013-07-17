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
package org.jboss.dashboard.ui.components.panelManagement;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.components.MessagesComponentHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.utils.forms.FormStatus;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.ui.utils.forms.RenderUtils;
import org.jboss.dashboard.users.UserStatus;
import org.slf4j.Logger;
import org.hibernate.Session;

import java.util.ResourceBundle;

public class ShowPanelConfigComponent extends PanelManagementPanel {
    private static transient Logger log = org.slf4j.LoggerFactory.getLogger(ShowPanelConfigComponent.class.getName());

    private FormStatus formStatus;
    private String messagesComponentHandler;

    public static final String PROPERTIES_STORED = "ui.alert.panelProperties.OK";
    public static final String PROPERTIES_NOT_STORED = "ui.alert.panelProperties.KO";

    private String showPanelConfigComponentFormatter;

    @Override
    public boolean openDialog(Panel panel, CommandRequest request, String title, int width, int height) {
        formStatus = new FormStatus();
        clearFieldErrors();

        ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.ui.components.panelManagement.messages", LocaleManager.currentLocale());
        title = i18n.getString("title.properties");

        ((MessagesComponentHandler) Factory.lookup(messagesComponentHandler)).clearAll();

        return super.openDialog(panel, request, title, width, height);
    }

    public void actionSaveProperties(CommandRequest request) throws Exception {
        Workspace workspace = getWorkspace();
        WorkspacePermission workspacePerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_ADMIN);
        UserStatus.lookup().checkPermission(workspacePerm);

        resetFormStatus();

        PanelInstance instance = getPanelInstance();

        if (instance != null) {
            ((MessagesComponentHandler) Factory.lookup(messagesComponentHandler)).clearAll();
            boolean propertiesOk = setSystemParameters(request, instance) && setCustomParameters(request, instance);
            if (propertiesOk) {
                resetFormStatus();
                if (!closeDialog(request)) {
                    ((MessagesComponentHandler) Factory.lookup(messagesComponentHandler)).addMessage(PROPERTIES_STORED);
                } else {
                    reset();
                }
            } else {
                ((MessagesComponentHandler) Factory.lookup(messagesComponentHandler)).addError(PROPERTIES_NOT_STORED);
            }
        }
    }

    protected boolean setCustomParameters(CommandRequest request, PanelInstance instance) throws Exception {
        return setParameters(request, instance.getCustomParameters(), instance, true);
    }

    protected boolean setSystemParameters(CommandRequest request, PanelInstance instance) throws Exception {
        return setParameters(request, instance.getSystemParameters(), instance, false);
    }

    protected boolean setParameters(final CommandRequest request, final PanelProviderParameter[] params, final PanelInstance instance, final boolean specificParameters) throws Exception {
        final boolean anyParamWritten[] = new boolean[]{false};

        new HibernateTxFragment(){
            @Override
            protected void txFragment(Session session) throws Throwable {
                super.txFragment(session);
                String language = LocaleManager.lookup().getCurrentLang();

                if (params != null && params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        if (params[i].getProvider().getProperties().containsKey("parameter." + params[i].getId())) {
                            continue;
                        }
                        String value = params[i].readFromRequest(request.getRequestObject());
                        log.debug("Panel " + instance.getInstanceId() + " field:" + params[i].getId() + " = " + value);
                        if (params[i].isI18n()) {
                            String oldValue = instance.getParameterValue(params[i].getId(), language);
                            if (!RenderUtils.noNull(oldValue).equals(RenderUtils.noNull(value)) && params[i].isValid(value)) {
                                instance.setParameterValue(params[i].getId(), value, language);
                                anyParamWritten[0] = true;
                            }
                        } else {
                            String oldValue = instance.getParameterValue(params[i].getId());
                            if (!RenderUtils.noNull(oldValue).equals(RenderUtils.noNull(value)) && params[i].isValid(value)) {
                                instance.setParameterValue(params[i].getId(), value);
                                anyParamWritten[0] = true;
                            }
                        }

                        if (!params[i].isValid(value)) formStatus.addWrongField(params[i].getId());
                    }
                }
            }
        }.execute();

        if (formStatus.getWrongFields().length == 0) {
            if (anyParamWritten[0]) {
                if (specificParameters) instance.saveCustomProperties();
                else instance.saveProperties();
            }
            return true;
        }
        return false;
    }

    public void resetFormStatus() {
        formStatus.clearWrongFields();
    }

    public static ShowPanelConfigComponent lookup() {
        return (ShowPanelConfigComponent) Factory.lookup(ShowPanelConfigComponent.class.getName());
    }

    public FormStatus getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(FormStatus formStatus) {
        this.formStatus = formStatus;
    }

    public String getMessagesComponentHandler() {
        return messagesComponentHandler;
    }

    public void setMessagesComponentHandler(String messagesComponentHandler) {
        this.messagesComponentHandler = messagesComponentHandler;
    }

    public String getShowPanelConfigComponentFormatter() {
        return showPanelConfigComponentFormatter;
    }

    public void setShowPanelConfigComponentFormatter(String showPanelConfigComponentFormatter) {
        this.showPanelConfigComponentFormatter = showPanelConfigComponentFormatter;
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
