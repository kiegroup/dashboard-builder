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
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.PanelProviderParameter;

import javax.servlet.http.HttpServletRequest;

/**
 * String parameter
 */
public class StringParameter extends PanelProviderParameter {
    public StringParameter(PanelProvider provider) {
        super(provider);
    }

    public StringParameter(PanelProvider provider, String id, boolean required, boolean i18n) {
        super(provider, id, required, i18n);
    }

    public StringParameter(PanelProvider provider, String id, boolean required, String defaultValue, boolean i18n) {
        super(provider, id, required, defaultValue, i18n);
    }

    public boolean isValid(String value) {
        return !(isRequired() && isEmpty(value));
    }

    public String renderHTML(HttpServletRequest req, PanelInstance instance, PanelProviderParameter param, String value) {
        return "<input type=\"text\" class='skn-input' size='40' name='param_" + getId() + "' value=\"" + escapeParameterValue(value) + "\">";
    }

    public String readFromRequest(HttpServletRequest req) {
        return req.getParameter("param_" + getId());
    }

    /**
     * Returns the parameter value as a String, given the parameter value.
     *
     * @param value parameter value
     * @return parameter value as a String
     */
    public static String value(String value) {
        return value;
    }
}
