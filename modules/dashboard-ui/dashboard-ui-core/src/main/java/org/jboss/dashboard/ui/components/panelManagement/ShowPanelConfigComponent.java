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
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
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
import javax.inject.Inject;
import javax.inject.Named;

@PanelScoped
@Named("showp_config_panel")
public class ShowPanelConfigComponent extends PanelManagementPanel {

    public static final String PROPERTIES_STORED = "ui.alert.panelProperties.OK";
    public static final String PROPERTIES_NOT_STORED = "ui.alert.panelProperties.KO";

    @Inject
    private transient Logger log;

    @Inject /** The locale manager. */
    protected LocaleManager localeManager;

    private FormStatus formStatus;

    public String getBeanJSP() {
        return "/components/showPanelConfig/show.jsp";
    }

    @Override
    public boolean openDialog(Panel panel, CommandRequest request, String title, int width, int height) {
        formStatus = new FormStatus();
        clearFieldErrors();

        ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.ui.components.panelManagement.messages", LocaleManager.currentLocale());
        title = i18n.getString("title.properties");

        MessagesComponentHandler messagesHandler = MessagesComponentHandler.lookup();
        messagesHandler.clearAll();

        return super.openDialog(panel, request, title, width, height);
    }

    @Override
    public void afterRenderBean() {
        try {
            Panel panel = getPanel();
            if (panel != null) super.afterRenderBean();
        } catch (Exception e) {
            getLogger().warn("Error: ", e);
        }
    }

    public void actionSaveProperties(CommandRequest request) throws Exception {
        Workspace workspace = getWorkspace();
        WorkspacePermission workspacePerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_ADMIN);
        UserStatus.lookup().checkPermission(workspacePerm);

        resetFormStatus();

        PanelInstance instance = getPanelInstance();

        if (instance != null) {
            MessagesComponentHandler messagesHandler = MessagesComponentHandler.lookup();
            messagesHandler.clearAll();
            boolean propertiesOk = setSystemParameters(request, instance) && setCustomParameters(request, instance);
            if (propertiesOk) {
                resetFormStatus();
                if (!closeDialog(request)) {
                    messagesHandler.addMessage(PROPERTIES_STORED);
                } else {
                    reset();
                }
            } else {
                messagesHandler.addError(PROPERTIES_NOT_STORED);
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
        return CDIBeanLocator.getBeanByType(ShowPanelConfigComponent.class);
    }

    public FormStatus getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(FormStatus formStatus) {
        this.formStatus = formStatus;
    }

    public MessagesComponentHandler getMessagesComponentHandler() {
        return MessagesComponentHandler.lookup();
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
