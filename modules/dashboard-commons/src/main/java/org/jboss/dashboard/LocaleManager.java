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
package org.jboss.dashboard;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Manager that holds the list of languages supported.
 */
@ApplicationScoped
@Named("localeManager")
public class LocaleManager {

    public static LocaleManager lookup() {
        return (LocaleManager) CDIBeanLocator.getBeanByName("localeManager");
    }

    private static transient Logger log = LoggerFactory.getLogger(LocaleManager.class.getName());

    /**
     * The list of locales supported.
     */
    @Inject @Config("en,es,de,fr,pt,ja")
    protected String[] installedLocaleIds;

    /**
     * The default localeId.
     */
    @Inject @Config("en")
    protected String defaultLocaleId;

    private transient Locale[] availableLocales;
    private transient Locale currentLocale;
    private transient Locale currentEditLocale;
    private transient Locale defaultLocale;

    /** The fallback locale control.*/
    private ResourceBundle.Control fallbackControl;

    @PostConstruct
    public void init() {

        fallbackControl =  ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);

        List<Locale> availableLocalesList = new ArrayList<Locale>();
        for (String locId : installedLocaleIds) {
            Locale locale = getLocaleById(locId);
            if (locale != null) availableLocalesList.add(locale);
        }
        availableLocales = availableLocalesList.toArray(new Locale[availableLocalesList.size()]);
        defaultLocale = getLocaleById(defaultLocaleId);
    }

    public String[] getInstalledLocaleIds() {
        return installedLocaleIds;
    }

    public void setInstalledLocaleIds(String[] installedLocaleIds) {
        this.installedLocaleIds = installedLocaleIds;
    }

    public String getDefaultLocaleId() {
        return defaultLocaleId;
    }

    public void setDefaultLocaleId(String defaultLocale) {
        Locale l = getLocaleById(defaultLocale);
        if (l != null) setDefaultLocale(l);
    }

    public void setDefaultLocale(Locale defaultLocale) {
        Locale l = getPlatformLocale(defaultLocale);
        if (l != null) {
            this.defaultLocaleId = l.toString();
            this.defaultLocale = l;
        }
    }

    /**
     * Get a Locale by its id.
     *
     * @return a Locale whose toString() equals given localeId, or null if it doesn't exist
     */
    public Locale getLocaleById(String localeId) {
        for (Locale locale : getAllLocales()) {
            if (locale.toString().equals(localeId)) return locale;
        }
        return null;
    }

    /**
     * Locales supported by the VM
     */
    public Locale[] getAllLocales() {
        return Locale.getAvailableLocales();
    }

    /**
     * Locales supported by the platform
     */
    public Locale[] getPlatformAvailableLocales() {
        return availableLocales;
    }

    /**
     * Current locale for viewing contents
     */
    public Locale getCurrentLocale() {
        return currentLocale == null ? defaultLocale : currentLocale;
    }

    public void setCurrentLocale(Locale l) {
        currentLocale = defaultLocale;
        Locale platformLocale = getPlatformLocale(l);
        if (platformLocale != null) {
            // Avoid setting a non supported locale.
            currentLocale = platformLocale;
        }
    }

    public Locale getPlatformLocale(Locale l) {
        Locale result = null;
        for (Locale locale : availableLocales) {
            if (locale.equals(l)) {
                return locale;
            }
            String lang = locale.getLanguage();
            if (lang.equals(l.getLanguage())) {
                result = locale;
            }
        }
        return result;
    }

    /**
     * Default locale for the application
     */
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    // Language methods

    protected String[] getLanguages(Locale[] locales) {
        List<String> langs = new ArrayList<String>();
        for (Locale locale : locales) {
            String s = locale.getLanguage();
            if (!langs.contains(s)) {
                langs.add(s);
            }
        }
        return langs.toArray(new String[langs.size()]);
    }

    /**
     * Return the display name (in current locale) of the specified lang
     */
    public String getLangDisplayName(String lang) {
        return new Locale(lang).getDisplayName(getCurrentLocale());
    }

    /**
     * Get all language identifiers
     */
    public String[] getAllLanguages() {
        return getLanguages(getAllLocales());
    }

    /**
     * Langs supported.
     */
    public String[] getPlatformAvailableLangs() {
        return getLanguages(getPlatformAvailableLocales());
    }

    /**
     * Get the current language for displaying contents
     */
    public String getCurrentLang() {
        return getCurrentLocale().getLanguage().toString();
    }

    /**
     * Set the current language for displaying contents
     */
    public void setCurrentLang(String langId) {
        Locale locale = getPlatformLocale(getLocaleById(langId));
        if (locale != null) setCurrentLocale(locale);
        else log.error("Can't set current lang to " + langId);
    }

    /**
     * Get the default language for the platform
     */
    public String getDefaultLang() {
        return getDefaultLocale().getLanguage();
    }

    /**
     * Given a map of locale->value or language->value, it returns the
     * appropriate value for the current locale. If such value doesn't exist,
     * it uses the default locale.
     *
     * @return Appropriate value for given locale.
     */
    public Object localize(Map localizedData) {
        if (localizedData == null || localizedData.isEmpty()) {
            return null;
        }

        // Get the entry (if any) for the current language.
        Object data = localizedData.get(getCurrentLang());
        if (data != null && (!(data instanceof String) || !"".equals(data))) {
            return data;
        }

        data = localizedData.get(getCurrentLocale());
        if (null != data && (!(data instanceof String) || !"".equals(data))) {
            return data;
        }

        data = localizedData.get(new Locale(getCurrentLang()));
        if (null != data && (!(data instanceof String) || !"".equals(data))) {
            return data;
        }

        // Get the entry (if any) for the default system language.
        data = localizedData.get(getDefaultLang());
        if (null != data && (!(data instanceof String) || !"".equals(data))) {
            return data;
        }

        data = localizedData.get(getDefaultLocale());
        if (null != data && (!(data instanceof String) || !"".equals(data))) {
            return data;
        }

        // Get the first entry in the map.
        Set<Map.Entry> entries = localizedData.entrySet();
        return entries.iterator().next().getValue();
    }

    /**
     * Static getter for current Locale.
     */
    public static Locale currentLocale() {
        return LocaleManager.lookup().getCurrentLocale();
    }

    /**
     * Static getter for current lang.
     */
    public static String currentLang() {
        return LocaleManager.lookup().getCurrentLang();
    }


    /**
     * ==================================================================
     * BZ-1031080: RESOURCE BUNDLE CUSTOM HANDLING
     * Use another NoFallbackControl implementation than the default one.
     * ==================================================================
     * **/

    public ResourceBundle getBundle(String baseName) {
        return ResourceBundle.getBundle(baseName, fallbackControl);
    }

    public ResourceBundle getBundle(String baseName, Locale targetLocale) {
        return ResourceBundle.getBundle(baseName, targetLocale, fallbackControl);
    }

    public ResourceBundle getBundle(String baseName, Locale targetLocale,
                                    ClassLoader loader) {
        return ResourceBundle.getBundle(baseName, targetLocale, loader, fallbackControl);
    }
}
