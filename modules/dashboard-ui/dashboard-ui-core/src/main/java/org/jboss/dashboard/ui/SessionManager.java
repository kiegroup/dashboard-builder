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
package org.jboss.dashboard.ui;

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.utils.forms.FormStatus;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Simple interface to some objects stored in session
 */
@ApplicationScoped
public class SessionManager {

    private final static String ATTRIBUTE_FORM_STATUS = "current_form_status";

    public static SessionManager lookup() {
        return CDIBeanLocator.getBeanByType(SessionManager.class);
    }

    @Inject /** Logger */
    private transient Logger log;

    /**
     * Returns current form status
     */
    public static FormStatus getCurrentFormStatus() {
        RequestContext reqCtx = RequestContext.lookup();
        HttpSession session = reqCtx.getRequest().getSessionObject();
        FormStatus formStatus = (FormStatus) session.getAttribute(ATTRIBUTE_FORM_STATUS);
        if (formStatus == null) {
            formStatus = new FormStatus();
            session.setAttribute(ATTRIBUTE_FORM_STATUS, formStatus);
        }
        return formStatus;
    }

    /**
     * @deprecated Use Panel.getPanelSession() instead.
     */
    public static PanelSession getPanelSession(Panel panel) {
        return panel.getPanelSession();
    }

    /**
     * Returns the section status object for a given region. If it doesn't exist,
     * creates and stores it in the session.
     */
    public static LayoutRegionStatus getRegionStatus(Section section, LayoutRegion region) {
        RequestContext reqCtx = RequestContext.lookup();
        HttpSession session = reqCtx.getRequest().getSessionObject();
        if (section == null || region == null) return null;

        String key = "_region_status_" + section.getKey() + "_" + region.getId();
        LayoutRegionStatus sectionStatus = (LayoutRegionStatus) session.getAttribute(key);
        if (sectionStatus == null) {
            sectionStatus = new LayoutRegionStatus(region);
            session.setAttribute(key, sectionStatus);
        }

        return sectionStatus;
    }

    /**
     * @deprecated Use LocaleManager instead
     */
    public static Locale getCurrentLocale() {
        return LocaleManager.currentLocale();
    }

    /**
     * @deprecated Use LocaleManager instead
     */
    public static String getLang() {
        return LocaleManager.currentLang();
    }
}