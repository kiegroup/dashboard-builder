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
package org.jboss.dashboard.ui.annotation.panel;

import java.util.Enumeration;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class PanelScopeListener implements HttpSessionListener, HttpSessionAttributeListener {

    @Override public void sessionCreated(HttpSessionEvent se) {

    }

    @Override public void sessionDestroyed(HttpSessionEvent se) {
        PanelScopeContextHolder panelScopeContextHolder = PanelScopeContextHolder.getInstance();
        for (Enumeration en = se.getSession().getAttributeNames(); en.hasMoreElements();) {
            String attrName = (String) en.nextElement();
            if (panelScopeContextHolder.isPanelScopedBean(attrName)) {
                panelScopeContextHolder.destroyBean(se.getSession(), attrName);
            }
        }
    }

    @Override public void attributeAdded(HttpSessionBindingEvent se) {

    }

    @Override public void attributeRemoved(HttpSessionBindingEvent se) {
        PanelScopeContextHolder panelScopeContextHolder = PanelScopeContextHolder.getInstance();
        if (panelScopeContextHolder.isPanelScopedBean(se.getName())) {
            panelScopeContextHolder.destroyBean(se.getSession(), se.getName());
        }
    }

    @Override public void attributeReplaced(HttpSessionBindingEvent se) {
    }
}
