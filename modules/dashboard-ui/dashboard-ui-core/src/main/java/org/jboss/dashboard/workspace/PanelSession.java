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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.ui.UIServices;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.*;

/**
 * Stores and controls the session status for a given panel instance for each session.
 */
public class PanelSession implements HttpSession {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelSession.class.getName());

     // Panel status

    public static final int STATUS_REGULAR_SIZE = 0;
    public static final int STATUS_MAXIMIZED = 1;
    public static final int STATUS_MINIMIZED = 2;
    public static final int STATUS_MAXIMIZED_IN_REGION = 3;

     // Status of this region

    private int status = STATUS_REGULAR_SIZE;

     // Panel's work modes

    public static final int SHOW_MODE = 0;
    public static final int EDIT_MODE = 1;
    public static final int HELP_MODE = 3;
    public static final int CONFIGURATION_MODE = 5;

    private int workMode = SHOW_MODE;

    /**
     * Page being currently shown by this panel
     */
    private String currentPageId = null;

    private Long panelDbId;

    /**
     * Stored values
     */
    private Map<String, Object> values = new HashMap<String, Object>();

    /**
     * Reference to current http session
     */
    private HttpSession session = null;

    private String attributePrefix;

    public PanelSession() {
    }

    public PanelSession(Panel panel) {
        panelDbId = panel.getDbid();
        String workspaceId = panel.getWorkspace().getId();
        Long pageId = panel.getSection().getId();
        attributePrefix = "_panel_" + workspaceId + "." + pageId + "." + panelDbId + ".";
    }

    public Panel getPanel() {
        try {
            return UIServices.lookup().getPanelsManager().getPanelByDbId(panelDbId);
        } catch (Exception e) {
            log.error("Error: ", e);
            return null;
        }
    }

    public String getCurrentPageId() {
        return currentPageId;
    }

    public void setCurrentPageId(String currentScreenId) {
        this.currentPageId = currentScreenId;
    }

    public int getWorkMode() {
        return workMode;
    }

    public boolean isShowMode() {
        return workMode == SHOW_MODE;
    }

    public boolean isEditMode() {
        return workMode == EDIT_MODE;
    }

    public boolean isHelpMode() {
        return workMode == HELP_MODE;
    }

    public void setWorkMode(int workMode) {
        this.workMode = workMode;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public Object getValue(String idValue) {
        return values.get(idValue);
    }

    public void setValue(String idValue, Object value) {
        values.put(idValue, value);
    }

    public void removeValue(String idValue) {
        values.remove(idValue);
    }

    public boolean isMaximized() {
        return status == STATUS_MAXIMIZED;
    }

    public boolean isMaximizedInRegion() {
        return status == STATUS_MAXIMIZED_IN_REGION;
    }

    public boolean isMinimized() {
        return status == STATUS_MINIMIZED;
    }

    public boolean isRegularSize() {
        return status == STATUS_REGULAR_SIZE;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Called on session initialization
     */
    public void init(HttpSession session) {
        Panel panel = getPanel();
        PanelInstance instance = panel.getInstance();
        try {
            // IMPORTANT NOTE: make sure HTTP session is initialized before invoking PanelProvider.initSession()
            this.session = session;
            if (instance != null) instance.getProvider().initSession(this, session);
        } catch (Exception e) {
            String providerStr = "";
            if (instance != null) providerStr = " Provider: " + instance.getProvider().getId();
            log.error("Error initializing panel status: " + panel.getPanelId() + providerStr, e);
        }
    }


    // Session wrapper methods below

    public long getCreationTime() {
        return session.getCreationTime();
    }

    public String getId() {
        return session.getId();
    }

    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    public ServletContext getServletContext() {
        return session.getServletContext();
    }

    public HttpSessionContext getSessionContext() {
        return session.getSessionContext();
    }

    public void setMaxInactiveInterval(int i) {
        session.setMaxInactiveInterval(i);
    }

    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    public Object getAttribute(String s) {
        log.debug("Panel Session attribute " + s + " is stored in session as " + attributePrefix + s);
        return session.getAttribute(attributePrefix + s);
    }

    public Enumeration<String> getAttributeNames() {
        return session.getAttributeNames();
    }

    public String[] getValueNames() {
        Set<String> keys = values.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    public void setAttribute(String s, Object o) {
        if (log.isDebugEnabled()) log.debug("Panel Session attribute " + s + " will be stored in session as " + attributePrefix + s);
        session.setAttribute(attributePrefix + s, o);
    }

    public void putValue(String s, Object o) {
        values.put(s, o);
    }

    public void removeAttribute(String s) {
        session.removeAttribute(attributePrefix + s);
    }

    public void invalidate() {
        session.invalidate();
    }

    public boolean isNew() {
        return session.isNew();
    }

    public synchronized void clear() {
        values = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        for (Enumeration<String> en = session.getAttributeNames(); en.hasMoreElements();) {
            String attrName = en.nextElement();
            if (attrName != null && attrName.startsWith(attributePrefix)) {
                list.add(attrName);
            }
        }
        for (String attrName : list) {
            session.removeAttribute(attrName);
        }
    }
}
