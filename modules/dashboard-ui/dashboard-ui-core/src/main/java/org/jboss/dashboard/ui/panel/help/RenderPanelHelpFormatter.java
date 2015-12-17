/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui.panel.help;

import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelProviderParameter;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.ui.utils.forms.RenderUtils;
import org.jboss.dashboard.users.UserStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RenderPanelHelpFormatter extends Formatter {

    /**
     * Perform the required logic for this Formatter. Inside, the methods
     * setAttribute and renderFragment are intended to be used to generate the
     * output and set parameters for this output. Method getParameter is
     * intended to retrieve input parameters by name. <p/> Exceptions are to be
     * catched inside the method, and not to be thrown, normally, formatters
     * could use a error fragment to be displayed when an error happens in
     * displaying. But if the error is unexpected, it can be wrapped inside a
     * FormatterException.
     *
     * @param request  user request
     * @param response response to the user
     * @throws org.jboss.dashboard.ui.taglib.formatter.FormatterException
     *          in case of an unexpected exception.
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        Panel panel = getPanel();
        UserStatus userStatus = UserStatus.lookup();
        boolean canEdit = userStatus.hasPermission(PanelPermission.newInstance(getPanel(), PanelPermission.ACTION_EDIT));
        boolean canViewParams = userStatus.hasPermission(WorkspacePermission.newInstance(getPanel().getWorkspace(), WorkspacePermission.ACTION_ADMIN));
        renderHelp(panel.getInstance(), canEdit, canViewParams);
    }

    public void renderHelp(PanelInstance instance, boolean canEdit, boolean canViewParams) {
        PanelProvider provider = instance.getProvider();
        PanelHelp help = instance.getProvider().getPanelHelp();
        if (help == null) {
            setAttribute("panelName", provider.getResource(provider.getDescription(), getLocale()));
            renderFragment("empty");
        } else {
            setAttribute("panelName", provider.getResource(provider.getDescription(), getLocale()));
            renderFragment("outputStart");
            String usage = help.getUsage(getLocale());
            if (usage != null) {
                setAttribute("message", usage);
                renderFragment("outputUsage");
            }
            if (canEdit) {
                String helpUsage = help.getEditModeUsage(getLocale());
                if (helpUsage != null) {
                    setAttribute("message", helpUsage);
                    renderFragment("outputUsage");
                }
            }
            if (canViewParams) {
                String[] paramNames = help.getParameterNames();
                PanelProviderParameter[] params = instance.getCustomParameters();
                if (paramNames != null && paramNames.length > 0) {
                    renderFragment("paramsStart");
                    for (int i = 0; i < paramNames.length; i++) {
                        String paramId = paramNames[i];
                        for (int j = 0; j < params.length; j++) {
                            PanelProviderParameter param = params[j];
                            if (param.getId().equals(paramId)) {
                                setAttribute("paramName", param.getDescription(getLocale()));
                                setAttribute("paramValue", RenderUtils.noNull(help.getParameterUsage(paramId, getLocale())));
                                renderFragment("outputParam");
                                break;
                            }
                        }
                    }
                    renderFragment("paramsEnd");
                }
            }
            /*
             * About is no help mode, but ABOUT mode PanelAbout about =
             * help.getAbout(); if(about != null){ renderFragment("aboutStart");
             * String[] props = about.getProperties(); for (int i = 0; i <
             * props.length; i++) { String prop = props[i]; String val =
             * about.getProperty(prop); setAttribute("name",prop);
             * setAttribute("value",val); renderFragment("about"); }
             * renderFragment("aboutEnd"); }
             */
            renderFragment("outputEnd");
        }
    }
}
