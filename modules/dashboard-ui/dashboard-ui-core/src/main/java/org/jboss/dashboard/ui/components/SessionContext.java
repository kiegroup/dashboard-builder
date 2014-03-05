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
package org.jboss.dashboard.ui.components;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.users.LogoutSurvivor;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

@SessionScoped
public class SessionContext implements LogoutSurvivor, Serializable {

    public static SessionContext lookup() {
        return CDIBeanLocator.getBeanByType(SessionContext.class);
    }

    private Locale currentLocale;
    private Locale currentEditLocale;

    public Locale getCurrentEditLocale() {
        return currentEditLocale;
    }

    public void setCurrentEditLocale(Locale l) {
        // Reset the locale to the default one.
        LocaleManager lm = LocaleManager.lookup();
        currentEditLocale = lm.getDefaultLocale();

        // Check the target locale is available.
        // Avoid setting a non supported locale.
        Locale platformLocale = lm.getPlatformLocale(l);
        if (platformLocale != null) {
            currentLocale = platformLocale;
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(Locale l) {
        // Reset the current locale to the default one.
        LocaleManager lm = LocaleManager.lookup();
        currentLocale = lm.getDefaultLocale();

        // Check the target locale is available.
        // Avoid setting a non supported locale.
        Locale platformLocale = lm.getPlatformLocale(l);
        if (platformLocale != null) {
            currentLocale = platformLocale;
        }
    }
}
