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
package org.jboss.dashboard.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.ui.controller.RequestContext;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Listener that clears the unnecessary session components when changes something on the platform user status.
 */
@ApplicationScoped @Install
public class SessionClearerUserStatusListener implements UserStatusListener {

    private static transient Logger log = LoggerFactory.getLogger(SessionClearerUserStatusListener.class.getName());

    public void statusChanged(UserStatus us) {
        if (us.isAnonymous()) { // just logout
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpSession session = ctx.getRequest().getSessionObject();
            Enumeration en = session.getAttributeNames();
            Set attributesToDelete = new HashSet();
            while (en.hasMoreElements()) {
                String attrName = (String) en.nextElement();
                Object obj = session.getAttribute(attrName);
                if (obj == null || !(obj instanceof LogoutSurvivor)) {
                    attributesToDelete.add(attrName);
                }
            }
            for (Iterator iterator = attributesToDelete.iterator(); iterator.hasNext();) {
                String attrName = (String) iterator.next();
                session.removeAttribute(attrName);
            }
            try {
                ctx.getRequest().getRequestObject().logout();
                ctx.getRequest().getRequestObject().getSession().invalidate();
            } catch (ServletException e) {
                log.error("Error logging out", e);
            }
        }
    }
}
