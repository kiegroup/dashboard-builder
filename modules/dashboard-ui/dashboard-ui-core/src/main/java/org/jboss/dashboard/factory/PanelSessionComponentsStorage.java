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
package org.jboss.dashboard.factory;

import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.workspace.Panel;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class PanelSessionComponentsStorage implements ComponentsStorage {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PanelSessionComponentsStorage.class.getName());
    public static final String ATTR_PREFFIX = "factory://";
    private static ThreadLocal backup = new ThreadLocal();

    public void setComponent(String s, Object o) {
        HttpSession session = getSession();
        if (session != null) {
            session.setAttribute(ATTR_PREFFIX + s, o);
        } else {
            Map m = (Map) backup.get();
            if (m == null) {
                backup.set(m = new HashMap());
            }
            m.put(ATTR_PREFFIX + s, o);
        }
    }

    public Object getComponent(String s) {
        HttpSession session = getSession();
        if (session != null) {
            return session.getAttribute(ATTR_PREFFIX + s);
        } else {
            Map m = (Map) backup.get();
            if (m == null) {
                backup.set(m = new HashMap());
            }
            return m.get(ATTR_PREFFIX + s);
        }
    }

    public void clear() {
        Map m = (Map) backup.get();
        if (m != null) {
            m.clear();
        }
    }


    protected HttpSession getSession() {
        RequestContext reqCtx = RequestContext.getCurrentContext();
        if (reqCtx != null) {
            CommandRequest request = reqCtx.getRequest();
            if (request != null) {
                Panel currentPanel = (Panel) request.getRequestObject().getAttribute(Parameters.RENDER_PANEL);
                if (currentPanel != null) {
                    return SessionManager.getPanelSession(currentPanel);
                } else {
                    if (log.isDebugEnabled())
                        log.debug("Using a panelSession component outside a panel. Will default to session component.");
                    return request.getSessionObject();
                }
            }
        }
        return null;
    }

    public Object getSynchronizationObject() {
        HttpSession session = getSession();
        if (session != null) return session;
        return new Object();
    }

}