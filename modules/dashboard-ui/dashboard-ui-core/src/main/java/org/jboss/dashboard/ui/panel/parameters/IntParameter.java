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
package org.jboss.dashboard.ui.panel.parameters;

import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelProviderParameter;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.PanelProviderParameter;

import javax.servlet.http.HttpServletRequest;


/**
 * Integer parameter
 */
public class IntParameter extends PanelProviderParameter {

    public IntParameter(PanelProvider provider) {
        super(provider);
    }

    public IntParameter(PanelProvider provider, String id, boolean required) {
        super(provider, id, required, false);
    }

    public IntParameter(PanelProvider provider, String id, boolean required, String defaultValue) {
        super(provider, id, required, defaultValue, false);
    }

    public boolean isValid(String value) {
        if (isRequired() && isEmpty(value))
            return false;

        if (isEmpty(value))
            return true;

        try {
            Integer.parseInt(value); // try parsing the string as a number
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public String renderHTML(HttpServletRequest req, PanelInstance instance, PanelProviderParameter param, String value) {
        return "<input type='text' class='skn-input' size='8' name='param_" + getId() + "' value=\"" + escapeParameterValue(value) + "\">";
    }

    public String readFromRequest(HttpServletRequest req) {
        return req.getParameter("param_" + getId());
    }

    /**
     * Returns the parameter value as an int, given the parameter value.
     *
     * @param value parameter value
     * @return parameter value as an int
     */
    public static int value(String value, int defaultValue) {
        if (isEmpty(value))
            return defaultValue;
        return Integer.parseInt(value);
    }
}
