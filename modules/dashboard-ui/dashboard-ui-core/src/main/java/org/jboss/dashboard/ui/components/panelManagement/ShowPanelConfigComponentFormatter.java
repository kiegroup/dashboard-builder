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
package org.jboss.dashboard.ui.components.panelManagement;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelProviderParameter;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShowPanelConfigComponentFormatter extends Formatter {

    @Inject
    private transient Logger log;

    @Inject
    private ShowPanelConfigComponent showPanelConfigComponent;

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            PanelInstance instance = showPanelConfigComponent.getPanelInstance();
            if (instance != null) {
                renderFragment("outputStart");

                renderPanelParameters(httpServletRequest, instance, instance.getSystemParameters());
                renderPanelParameters(httpServletRequest, instance, instance.getCustomParameters());

                renderFragment("outputEnd");
            } else {
                log.warn("Error: panelInstance is null");
            }
        } catch (Exception e) {
            log.error("Error rendering panel properties: ", e);
        }
    }

    protected void renderPanelParameters(HttpServletRequest request, PanelInstance instance, PanelProviderParameter[] params) {
        if (params != null && params.length > 0) {
            for (PanelProviderParameter param : params) {
                if (param.getProvider().getProperties().containsKey("parameter." + param.getId())) {
                    continue;
                }
                setAttribute("param", param);
                setAttribute("html", param.renderHTML(request, instance, param, instance.getParameterValue(param.getId(), LocaleManager.currentLang())));
                setAttribute("formStatus", showPanelConfigComponent.getFormStatus());
                renderFragment("outputParam");
            }
        }
    }

    public ShowPanelConfigComponent getShowPanelConfigComponent() {
        return showPanelConfigComponent;
    }

    public void setShowPanelConfigComponent(ShowPanelConfigComponent showPanelConfigComponent) {
        this.showPanelConfigComponent = showPanelConfigComponent;
    }
}
