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
import org.jboss.dashboard.workspace.PanelProviderParameter;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.PanelProviderParameter;

import javax.servlet.http.HttpServletRequest;

/**
 * Boolean parameter. Presents a checkbox.
 */
public class BooleanParameter extends PanelProviderParameter {

    private final static String TRUE = "true";
    private final static String FALSE = "false";

    /**
     * Create en empty parameter for given provider
     *
     * @param provider
     */
    public BooleanParameter(PanelProvider provider) {
        super(provider);
    }

    /**
     * Constructs a  PanelProviderParameter
     *
     * @param provider Panel provider where the parameter applies.
     * @param id       Parameter identifiear. Panels can obtain the parameter value using this id.
     * @param required Indicates if the parameter is required, that is, the panel will not be properly configured is this parameter is not set.
     */
    public BooleanParameter(PanelProvider provider, String id, boolean required) {
        super(provider, id, required, false);
    }

    /**
     * Constructs a  PanelProviderParameter
     *
     * @param provider     Panel provider where the parameter applies.
     * @param id           Parameter identifiear. Panels can obtain the parameter value using this id.
     * @param required     Indicates if the parameter is required, that is, the panel will not be properly configured is this parameter is not set.
     * @param defaultValue Default value for the parameter
     */
    public BooleanParameter(PanelProvider provider, String id, boolean required, boolean defaultValue) {
        super(provider, id, required, defaultValue ? TRUE : FALSE, false);
    }


    public boolean isValid(String value) {
        if (isRequired() && isEmpty(value))
            return false;

        if (isEmpty(value))
            return true;

        return value.equals(TRUE) || value.equals(FALSE);
    }

    public String renderHTML(HttpServletRequest req, PanelInstance instance, PanelProviderParameter param, String value) {
        String checked = (value(value, false)) ? "checked" : "";
        return "<input type='checkbox' name='param_" + getId() + "' " + checked + ">";
    }

    public String readFromRequest(HttpServletRequest req) {
        String p = req.getParameter("param_" + getId());
        if ("on".equalsIgnoreCase(p)) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    /**
     * Get the value as a boolean for a boolean parameter value
     *
     * @param value        Parameter value
     * @param defaultValue default value to use
     * @return parameter value as a boolean
     */
    public static boolean value(String value, boolean defaultValue) {
        if (isEmpty(value))
            return defaultValue;
        return !value.equals(FALSE);
    }
}
