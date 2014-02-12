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

import java.util.HashMap;
import java.util.Map;

public class SessionComponentsStorage {
    public static final String ATTR_PREFFIX = "factory://";
    private static ThreadLocal backup = new ThreadLocal();

    public void setComponent(String s, Object o) {
        RequestContext context = RequestContext.getCurrentContext();
        boolean setInRequest = false;
        if (context != null) {
            CommandRequest request = context.getRequest();
            if (request != null) {
                request.getRequestObject().getSession().setAttribute(ATTR_PREFFIX + s, o);
                setInRequest = true;
            }
        }
        if (!setInRequest) {
            Map m = (Map) backup.get();
            if (m == null) {
                backup.set(m = new HashMap());
            }
            m.put(ATTR_PREFFIX + s, o);
        }
    }

    public Object getComponent(String s) {
        RequestContext context = RequestContext.getCurrentContext();
        if (context != null) {
            CommandRequest request = context.getRequest();
            if (request != null) {
                // Session components are considered out of a panelSession, thus making dependent
                // panelSession components be looked up outside a panelSession, and making it coherent
                // with panel session components falling back to session when looked up outside a panelSessionCC
                Object currentPanel = request.getRequestObject().getAttribute(Parameters.RENDER_PANEL);
                request.getRequestObject().removeAttribute(Parameters.RENDER_PANEL);
                Object object = request.getRequestObject().getSession().getAttribute(ATTR_PREFFIX + s);
                request.getRequestObject().setAttribute(Parameters.RENDER_PANEL, currentPanel);
                return object;
            }
        }
        Map m = (Map) backup.get();
        if (m == null) {
            backup.set(m = new HashMap());
        }
        return m.get(ATTR_PREFFIX + s);
    }

    public void clear() {
        Map m = (Map) backup.get();
        if (m != null) {
            m.clear();
        }
    }

    public Object getSynchronizationObject() {
        RequestContext context = RequestContext.getCurrentContext();
        if (context != null) {
            CommandRequest request = context.getRequest();
            return request.getSessionObject() != null ? request.getSessionObject() : new Object();
        }
        return new Object();
    }
}