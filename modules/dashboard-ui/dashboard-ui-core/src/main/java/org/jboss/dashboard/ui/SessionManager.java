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

import org.jboss.dashboard.ui.utils.forms.FormStatus;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.controller.RequestContext;

import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Simple interface to some objects stored in session
 */
public class SessionManager {

    private final static String ATTRIBUTE_PANEL = "current_panel";
    private final static String ATTRIBUTE_FORM_STATUS = "current_form_status";

    /**
     * Returns current form status
     */
    public static FormStatus getCurrentFormStatus() {
        RequestContext reqCtx = RequestContext.getCurrentContext();
        HttpSession session = reqCtx.getRequest().getSessionObject();
        FormStatus formStatus = (FormStatus) session.getAttribute(ATTRIBUTE_FORM_STATUS);
        if (formStatus == null) {
            formStatus = new FormStatus();
            session.setAttribute(ATTRIBUTE_FORM_STATUS, formStatus);
        }
        return formStatus;
    }

    /**
     * Returns panel stored in session
     *
     * @deprecated
     */
    public static Object getCurrentPanel() {
        RequestContext reqCtx = RequestContext.getCurrentContext();
        HttpSession session = reqCtx.getRequest().getSessionObject();
        Object panel = reqCtx.getRequest().getRequestObject().getAttribute(ATTRIBUTE_PANEL);
        if (panel != null) return panel;
        return session.getAttribute(ATTRIBUTE_PANEL);
    }

    /**
     * @deprecated
     */
    public static void setCurrentPanel(Panel panel) {
        RequestContext reqCtx = RequestContext.getCurrentContext();
        HttpSession session = reqCtx.getRequest().getSessionObject();
        session.setAttribute(ATTRIBUTE_PANEL, panel);
        reqCtx.getRequest().getRequestObject().setAttribute(ATTRIBUTE_PANEL, panel);
    }

    /**
     * @deprecated
     */
    public static void setCurrentPanel(PanelInstance instance) {
        RequestContext reqCtx = RequestContext.getCurrentContext();
        HttpSession session = reqCtx.getRequest().getSessionObject();
        session.setAttribute(ATTRIBUTE_PANEL, instance);
    }

    /**
     * Returns the panel status object for a given panel. If it doesn't exist,
     * creates and stores it in the session.
     */
    public static PanelSession getPanelSession(Panel panel) {
        RequestContext reqCtx = RequestContext.getCurrentContext();
        HttpSession session = reqCtx.getRequest().getSessionObject();
        String key = "_panel_status_" + panel.getWorkspace().getId() + "." + panel.getSection().getId() + "." + panel.getPanelId();
        PanelSession panelStatus = (PanelSession) session.getAttribute(key);

        if (panelStatus == null) {
            panelStatus = new PanelSession(panel);
            panelStatus.init(session);
            if (panel.isInitiallyMaximized()) {
                panelStatus.setStatus(PanelSession.STATUS_MAXIMIZED);
            }
            session.setAttribute(key, panelStatus);
        }
        return panelStatus;
    }

    /**
     * Returns the section status object for a given region. If it doesn't exist,
     * creates and stores it in the session.
     */
    public static LayoutRegionStatus getRegionStatus(Section section, LayoutRegion region) {
        RequestContext reqCtx = RequestContext.getCurrentContext();
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
        RequestContext reqCtx = RequestContext.getCurrentContext();
        String defaultLang = reqCtx.getRequest().getRequestObject().getParameter(Parameters.FORCE_LANGUAGE);
        LocaleManager localeManager = LocaleManager.lookup();
        if (defaultLang != null) localeManager.setCurrentLang(defaultLang);
        return localeManager.getCurrentLocale();
    }

    /**
     * @deprecated Use LocaleManager instead
     */
    public static String getLang() {
        RequestContext reqCtx = RequestContext.getCurrentContext();
        String defaultLang = reqCtx.getRequest().getRequestObject().getParameter(Parameters.FORCE_LANGUAGE);
        LocaleManager localeManager = LocaleManager.lookup();
        if (defaultLang != null) localeManager.setCurrentLang(defaultLang);
        return localeManager.getCurrentLang();
    }
}