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

import org.jboss.dashboard.users.LogoutSurvivor;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

@SessionScoped
@Named("sessionContext")
public class SessionContext implements LogoutSurvivor, Serializable {

    public static SessionContext lookup() {
        return (SessionContext) CDIBeanLocator.getBeanByName("sessionContext");
    }

    private Locale currentLocale;
    private Locale currentEditLocale;

    public SessionContext() {
    }

    public Locale getCurrentEditLocale() {
        return currentEditLocale;
    }

    public void setCurrentEditLocale(Locale currentEditLocale) {
        this.currentEditLocale = currentEditLocale;
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }

}
