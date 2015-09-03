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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelProviderParameter;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.PanelProviderParameter;

/**
 * Combo list selection parameter. Presents a list of items
 */
public class ComboListParameter extends PanelProviderParameter {
    private ComboListParameterDataSupplier dataSupplier = null;

    /**
     * Constructs a  PanelProviderParameter
     *
     * @param provider     Panel provider where the parameter applies.
     * @param dataSupplier Object that supplies data to construct the list of values shown.
     */
    public ComboListParameter(PanelProvider provider, ComboListParameterDataSupplier dataSupplier) {
        super(provider);
        this.dataSupplier = dataSupplier;
    }

    /**
     * Constructs a  PanelProviderParameter
     *
     * @param provider     Panel provider where the parameter applies.
     * @param id           Parameter identifiear. Panels can obtain the parameter value using this id.
     * @param required     Indicates if the parameter is required, that is, the panel will not be properly configured is this parameter is not set.
     * @param i18n         Indicates if parameter supports internationalization, that is, different values for different languages.
     * @param dataSupplier Object that supplies data to construct the list of values shown.
     */
    public ComboListParameter(PanelProvider provider, String id, boolean required, ComboListParameterDataSupplier dataSupplier, boolean i18n) {
        super(provider, id, required, i18n);
        this.dataSupplier = dataSupplier;
    }

    /**
     * Constructs a  PanelProviderParameter
     *
     * @param provider     Panel provider where the parameter applies.
     * @param id           Parameter identifiear. Panels can obtain the parameter value using this id.
     * @param required     Indicates if the parameter is required, that is, the panel will not be properly configured is this parameter is not set.
     * @param i18n         Indicates if parameter supports internationalization, that is, different values for different languages.
     * @param dataSupplier Object that supplies data to construct the list of values shown.
     * @param defaultValue Default value for the parameter
     */
    public ComboListParameter(PanelProvider provider, String id, boolean required, String defaultValue, ComboListParameterDataSupplier dataSupplier, boolean i18n) {
        super(provider, id, required, defaultValue, i18n);
        this.dataSupplier = dataSupplier;
    }

    public boolean isValid(String value) {
        return !(isRequired() && isEmpty(value));
    }

    public String renderHTML(HttpServletRequest req, PanelInstance instance, PanelProviderParameter param, String value) {
        StringBuffer buf = new StringBuffer();

        dataSupplier.init(instance);
        List values = dataSupplier.getValues();
        List keys = dataSupplier.getKeys();

        // Map choices = panel.getProvider().getDriver().getChoicesFor(param, panel);

        if (values != null && keys != null) {
            buf.append("<select class='skn-input' name='param_").append(getId()).append("'>");
            buf.append("<option value=''>").append(getDefaultValueText()).append("</option>");
            Iterator it = keys.iterator();
            Iterator itValues = values.iterator();

            while (it.hasNext() && itValues.hasNext()) {
                String key = (String) it.next();
                String val = (String) itValues.next();
                String selected = (key.equals(instance.getParameterValue(param.getId()))) ? "selected" : "";
                buf.append("<option value='").append(StringEscapeUtils.ESCAPE_HTML4.translate(key)).append("' ").append(selected).append(">").append(StringEscapeUtils.ESCAPE_HTML4.translate(val)).append("</option>");
            }
            buf.append("</select>");
        }

        return buf.toString();
    }

    public String readFromRequest(HttpServletRequest req) {
        String param = req.getParameter("param_" + getId());
        return getDefaultValueText().equals(param) ? "" : param;
    }

    protected String getDefaultValueText() {
        return "-- " + getDescription(LocaleManager.currentLocale()) + " --";
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
