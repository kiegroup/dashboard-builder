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
package org.jboss.dashboard.ui.controller;

import org.jboss.dashboard.factory.ComponentsStorage;

import java.util.HashMap;
import java.util.Map;

public class RequestComponentsStorage implements ComponentsStorage {
    public static final String ATTR_PREFFIX = "factory://";
    private static ThreadLocal backup = new ThreadLocal();

    public void setComponent(String s, Object o) {
        RequestContext context = RequestContext.getCurrentContext();
        boolean setInRequest = false;
        if (context != null) {
            CommandRequest request = context.getRequest();
            if (request != null) {
                request.getRequestObject().setAttribute(ATTR_PREFFIX + s, o);
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
                return request.getRequestObject().getAttribute(ATTR_PREFFIX + s);
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
            return request != null ? request : "";
        }
        return "";
    }

}
