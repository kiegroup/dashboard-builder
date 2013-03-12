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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.ui.panel.PanelProvider;
import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.dashboard.ui.panel.PanelProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Properties;

/**
 * Definition for parameters supplied to panels
 */
public abstract class PanelProviderParameter implements Cloneable {

    public static int SCOPE_INSTANCE = 1;
    public static int SCOPE_PANEL = 2;
    public static int SCOPE_BOTH = 0;

    private String id = null;
    private String defaultValue = null;
    private boolean required = false;
    private boolean systemParameter = false;
    private boolean i18n = false;
    private int scope = SCOPE_INSTANCE;
    private Properties defaultI18nValues = new Properties();

    private PanelProvider provider = null;

    /**
     * Create en empty parameter for given provider
     *
     * @param provider
     */
    public PanelProviderParameter(PanelProvider provider) {
        this.provider = provider;
    }

    /**
     * Constructs a  PanelProviderParameter
     *
     * @param provider Panel provider where the parameter applies.
     * @param id       Parameter identifiear. Panels can obtain the parameter value using this id.
     * @param required Indicates if the parameter is required, that is, the panel will not be properly configured is this parameter is not set.
     * @param i18n     Indicates if parameter supports internationalization, that is, different values for different languages
     */
    public PanelProviderParameter(PanelProvider provider, String id, boolean required, boolean i18n) {
        this(provider, id, required, null, i18n);
    }

    /**
     * Constructs a  PanelProviderParameter
     *
     * @param provider     Panel provider where the parameter applies.
     * @param id           Parameter identifiear. Panels can obtain the parameter value using this id.
     * @param required     Indicates if the parameter is required, that is, the panel will not be properly configured is this parameter is not set.
     * @param i18n         Indicates if parameter supports internationalization, that is, different values for different languages
     * @param defaultValue Default value for the parameter
     */
    public PanelProviderParameter(PanelProvider provider, String id, boolean required, String defaultValue, boolean i18n) {
        this(provider, id, required, defaultValue, i18n, SCOPE_INSTANCE);
    }

    /**
     * @deprecated scope is not implemented
     */
    public PanelProviderParameter(PanelProvider provider, String id, boolean required, String defaultValue, boolean i18n, int scope) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.required = required;
        this.provider = provider;
        this.i18n = i18n;
        this.scope = scope;
    }

    /**
     * Parameter is required
     *
     * @return Parameter is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Parameter is required
     *
     * @param required Parameter is required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Parameter identifier
     *
     * @return Parameter identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Parameter identifier
     *
     * @param id Parameter identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Parameter description
     *
     * @return Parameter description
     */
    public String getDescription() {
        return provider.getResource("parameter." + getId());
    }

    /**
     * Parameter description in a given language
     *
     * @param locale Language to get the description in.
     * @return Parameter description
     */
    public String getDescription(Locale locale) {
        return provider.getResource("parameter." + getId(), locale);
    }

    /**
     * Parameter default value
     *
     * @return Parameter default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Parameter default value
     *
     * @param defaultValue Parameter default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Parameter default value in given lang
     *
     * @param lang language for the parameter
     * @return Parameter default value
     */
    public String getDefaultValue(String lang) {
        return isI18n() ? defaultI18nValues.getProperty(lang) : defaultValue;
    }

    /**
     * Parameter default value
     *
     * @param defaultValue Parameter default value
     * @param lang         language for the parameter
     */
    public void setDefaultValue(String defaultValue, String lang) {
        if (isI18n()) {
            defaultI18nValues.setProperty(lang, defaultValue);
        } else {
            this.defaultValue = defaultValue;
        }
    }


    /**
     * True if this parameter is system, that is, applies to all panel instances
     *
     * @return * True if this parameter is system, that is, applies to all panel instances
     */
    public boolean isSystemParameter() {
        return systemParameter;
    }

    /**
     * True if this parameter is system, that is, applies to all panel instances
     *
     * @param systemParameter True if this parameter is system, that is, applies to all panel instances
     */
    public void setSystemParameter(boolean systemParameter) {
        this.systemParameter = systemParameter;
    }

    /**
     * True if this parameter is internationalizable
     *
     * @return True if this parameter is internationalizable
     */
    public boolean isI18n() {
        return i18n;
    }

    /**
     * True if this parameter is internationalizable
     *
     * @param i18n True if this parameter is internationalizable
     */
    public void setI18n(boolean i18n) {
        this.i18n = i18n;
    }

    /**
     * @deprecated scope is not implemented
     */
    public int getScope() {
        return scope;
    }

    /**
     * @deprecated scope is not implemented
     */
    public void setScope(int scope) {
        this.scope = scope;
    }

    /**
     * Determines if a String value is null or blank
     *
     * @param value value to check
     * @return true if value is null or blank
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().equals("");
    }

    /**
     * Escapes given string for including in a JSP page
     *
     * @param value value to scape
     * @return the scaped value
     */
    protected static String escapeParameterValue(String value) {
        return StringEscapeUtils.escapeHtml(value);
    }

    /**
     * Provider this parameter applies to
     *
     * @return Provider this parameter applies to
     */
    public PanelProvider getProvider() {
        return provider;
    }

    /**
     * Validates a string entered as a parameter value
     *
     * @param value value to validate
     * @return true if value is valid
     */
    public abstract boolean isValid(String value);

    /**
     * Renders a form field to enter this parameter's value
     *
     * @param req      Http request in use
     * @param instance panel instance
     * @param param    this same parameter
     * @param value    parameter value
     * @return The HTML markup to render this parameter
     */
    public abstract String renderHTML(HttpServletRequest req, PanelInstance instance, PanelProviderParameter param, String value);

    /**
     * Reads the value from the request submitted
     *
     * @param req
     * @return The parameter value read from the request
     */
    public abstract String readFromRequest(HttpServletRequest req);

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PanelProviderParameter that = (PanelProviderParameter) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }
}
