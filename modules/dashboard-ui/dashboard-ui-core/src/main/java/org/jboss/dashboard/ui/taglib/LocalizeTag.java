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
package org.jboss.dashboard.ui.taglib;

import org.jboss.dashboard.LocaleManager;
import org.apache.commons.lang.StringEscapeUtils;


import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.Map;

/**
 */
public class LocalizeTag extends BaseTag {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LocalizeTag.class.getName());

    private Map data;

    private String locale;

    private boolean valueIsHTML = false;

    private boolean useDefaults = true;

    public boolean isValueIsHTML() {
        return valueIsHTML;
    }

    public void setValueIsHTML(boolean valueIsHTML) {
        this.valueIsHTML = valueIsHTML;
    }

    protected static LocaleManager getLocaleManager(){
        return LocaleManager.lookup();
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        String locale = getLocaleManager().getCurrentLang();
        String result = getLocalizedValue(locale);

        if (useDefaults) {
            if (result == null || result.trim().length() == 0) {
                locale = getLocaleManager().getDefaultLang();
                result = getLocalizedValue(locale);
            }
        }

        try {
            pageContext.getOut().print(valueIsHTML ? result : StringEscapeUtils.escapeHtml(result));
        } catch (IOException e) {
            log.error("", e);
        }

        setData(null);
        setLocale(null);
        setUseDefaults(true);

        return EVAL_PAGE;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean isUseDefaults() {
        return useDefaults;
    }

    public void setUseDefaults(boolean useDefaults) {
        this.useDefaults = useDefaults;
    }

    private String getLocalizedValue(String locale) {
        String result = "";
        if (data != null && !data.isEmpty() && locale != null && locale.trim().length() > 0) {
            result = (String) data.get(locale);
            if (result == null)
                result = "";
        }

        return result;
    }

    /**
     * @deprecated Use the LocaleManager to access localization of resources
     */
    public static String getLocalizedValue(Map values, String locale, boolean useDefaults) {
        String result = "";
        if (values != null && !values.isEmpty() && locale != null && locale.trim().length() > 0) {
            result = (String) values.get(locale);
            if (result == null)
                result = "";
        }

        if (useDefaults) {
            if (result == null || result.trim().length() == 0) {
                locale = getLocaleManager().getDefaultLang();
                if (locale != null)
                    locale = locale.toLowerCase();

                if (values != null && !values.isEmpty() && locale != null && locale.trim().length() > 0) {
                    result = (String) values.get(locale);
                    if (result == null)
                        result = "";
                }
            }
        }

        return result;
    }
}
