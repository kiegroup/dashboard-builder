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
package org.jboss.dashboard;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    private static transient Log log = LogFactory.getLog(LocaleManager.class.getName());

    /**
     * The list of locales supported.
     */
    @Inject @Config("en,es")
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

    @PostConstruct
    public void init() {
        List availableLocalesList = new ArrayList();
        for (int i = 0; i < installedLocaleIds.length; i++) {
            Locale locale = getLocaleById(installedLocaleIds[i]);
            if (locale != null) availableLocalesList.add(locale);
        }
        availableLocales = (Locale[]) availableLocalesList.toArray(new Locale[availableLocalesList.size()]);
        defaultLocale = getPlatformLocale(Locale.getDefault());
        if (defaultLocale == null) {
            defaultLocale = getLocaleById(defaultLocaleId);
        }
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
        this.defaultLocaleId = defaultLocale;
        this.defaultLocale = getLocaleById(defaultLocale);
    }

    /**
     * Get a Locale by its id.
     *
     * @return a Locale whose toString() equals given localeId, or null if it doesn't exist
     */
    public Locale getLocaleById(String localeId) {
        Locale[] allLocales = getAllLocales();
        for (int i = 0; i < allLocales.length; i++) {
            Locale locale = allLocales[i];
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
     * Current locale for editing contents
     */
    public Locale getCurrentEditLocale() {
        return currentEditLocale == null ? defaultLocale : currentEditLocale;
    }

    public void setCurrentEditLocale(Locale l) {
        currentEditLocale = defaultLocale;
        Locale platformLocale = getPlatformLocale(l);
        if (platformLocale != null) {
            // Avoid setting a non supported locale.
            currentEditLocale = platformLocale;
        }
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
        for (int i = 0; i < availableLocales.length; i++) {
            Locale locale = availableLocales[i];
            String lang = locale.getLanguage();
            if (lang.equals(l.getLanguage())) return locale;
        }
        return null;
    }

    /**
     * Default locale for the application
     */
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    // Language methods

    protected String[] localeToString(Locale[] locales) {
        List langs = new ArrayList();
        for (int i = 0; i < locales.length; i++) {
            Locale locale = locales[i];
            String s = locale.toString();
            langs.add(s);
        }
        return (String[]) langs.toArray(new String[langs.size()]);
    }

    /**
     * Get all language identifiers
     */
    public String[] getAllLanguages() {
        return localeToString(getAllLocales());
    }

    /**
     * Langs supported.
     */
    public String[] getPlatformAvailableLangs() {
        return localeToString(getPlatformAvailableLocales());
    }

    /**
     * Langs supported.
     */
    public String[] getLangs() {
        return getPlatformAvailableLangs();
    }

    /**
     * Get the language in which the system is editing contents.
     */
    public String getCurrentEditLang() {
        return getCurrentEditLocale().toString();
    }

    /**
     * Set the language in which the system is editing contents.
     */
    public void setCurrentEditLang(String langId) {
        Locale locale = getLocaleById(langId);
        if (locale != null) setCurrentEditLocale(locale);
        else log.error("Can't set edit lang to " + langId);
    }

    /**
     * Get the current language for displaying contents
     */
    public String getCurrentLang() {
        return getCurrentLocale().toString();
    }

    /**
     * Set the current language for displaying contents
     */
    public void setCurrentLang(String langId) {
        Locale locale = getLocaleById(langId);
        if (locale != null) setCurrentLocale(locale);
        else log.error("Can't set current lang to " + langId);
    }

    /**
     * Get the default language for the platform
     */
    public String getDefaultLang() {
        return getDefaultLocale().toString();
    }

    /**
     * Given a map of locale->value or language->value, it returns the
     * appropiate value for the current locale. If such value doesn't exist,
     * it uses the default locale.
     *
     * @param localizedData
     * @return appropiate value for given locale.
     */
    public Object localize(Map localizedData) {
        if (localizedData == null) return null;
        String lang = getCurrentLang();

        Object data = localizedData.get(lang);
        if (data != null && (!(data instanceof String) || !"".equals(data)))
            return data;

        Locale locale = getCurrentLocale();
        data = localizedData.get(locale);
        if (null != data && (!(data instanceof String) || !"".equals(data)))
            return data;

        data = localizedData.get(getDefaultLang());
        if (null != data && (!(data instanceof String) || !"".equals(data)))
            return data;

        return localizedData.get(getDefaultLocale());
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
}
