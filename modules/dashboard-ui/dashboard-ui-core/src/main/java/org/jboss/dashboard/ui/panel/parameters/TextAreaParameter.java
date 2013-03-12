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
package org.jboss.dashboard.ui.panel.parameters;

import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.PanelProviderParameter;

import javax.servlet.http.HttpServletRequest;

/**
 * Parameter for a HTML text area
 */
public class TextAreaParameter extends StringParameter {
    public TextAreaParameter(PanelProvider provider) {
        super(provider);
    }

    public TextAreaParameter(PanelProvider provider, String id, boolean required, boolean i18n) {
        super(provider, id, required, i18n);
    }

    public TextAreaParameter(PanelProvider provider, String id, boolean required, String defaultValue, boolean i18n) {
        super(provider, id, required, defaultValue, i18n);
    }

    public String renderHTML(HttpServletRequest req, PanelInstance instance, PanelProviderParameter param, String value) {
        if(value == null)
            value = "";

        StringBuffer html = new StringBuffer();
        html.append("<textarea class='skn-input' cols='40' rows='7' name='param_").append(getId()).append("'>").append(escapeParameterValue(value)).append("</textarea>");

        return html.toString();
    }

    public String readFromRequest(HttpServletRequest req) {
        String param = req.getParameter("param_" + getId());
        if (param == null || param.equals(""))
            return null;

        return param;
    }

}
