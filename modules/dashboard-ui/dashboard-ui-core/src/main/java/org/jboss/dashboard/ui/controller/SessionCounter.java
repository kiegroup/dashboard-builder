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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionCounter implements ControllerListener {

    /** Logger */
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SessionCounter.class.getName());

    private static SessionCounter singleton;

    public static SessionCounter getInstance() {
        return singleton == null ? (singleton = new SessionCounter()) : singleton;
    }

    private int activeSessions = 0;

    public int getActiveSessions() {
        return activeSessions;
    }

    /**
     * Called when a session expires.
     *
     * @param session Description of the Parameter
     */
    public void expireSession(HttpSession session) {
        if (activeSessions > 0) activeSessions--;
        log.info("Session " + session.getId() + " expired. " + activeSessions + " active sessions.");
    }

    /**
     * Called when a new session is created.
     *
     * @param request  Object that encapsulates the request to the servlet
     * @param response Object that encapsulates the response from the servlet
     */
    public void initSession(HttpServletRequest request, HttpServletResponse response) {
        activeSessions++;

        log.debug("Request " + request.getRequestURI() + "?" + request.getQueryString() + " caused session to be created.");
        log.info("Session " + request.getSession(false).getId() + " created. " + activeSessions + " active sessions.");
    }
}