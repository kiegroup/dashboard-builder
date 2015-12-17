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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.controller.RequestContext;

import java.util.Locale;
import javax.enterprise.inject.Specializes;

@Specializes
public class SessionAwareLocaleManager extends LocaleManager {

    protected SessionContext getSessionContext() {
        return (RequestContext.lookup() != null ? SessionContext.lookup() : null);
    }

    public Locale getCurrentLocale() {
        SessionContext ctx = getSessionContext();
        Locale locale = ctx != null ? ctx.getCurrentLocale() : super.getCurrentLocale();
        return (locale != null) ? locale : getDefaultLocale();
    }

    public void setCurrentLocale(Locale currentLocale) {
        SessionContext ctx = getSessionContext();
        if (ctx != null) ctx.setCurrentLocale(currentLocale);
        else super.setCurrentLocale(currentLocale);
    }
}
