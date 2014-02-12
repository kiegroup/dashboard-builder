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

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.profiler.Profiler;
import org.jboss.dashboard.ui.components.ControllerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@ApplicationScoped
public class ControllerServletHelper {

    public static ControllerServletHelper lookup() {
        return CDIBeanLocator.getBeanByType(ControllerServletHelper.class);
    }

    public CommandRequest beforeRequestBegins(HttpServletRequest request, HttpServletResponse response) {
        // Start the profiling of the request.
        Profiler.lookup().beginThreadProfile();

        // Initialize the request.
        CommandRequest cmdReq = updateRequestContext(request, response);
        ControllerStatus.lookup().setRequest(cmdReq);
        return cmdReq;
    }

    public CommandRequest updateRequestContext(HttpServletRequest request, HttpServletResponse response) {
        CommandRequest cmdReq = new CommandRequestImpl(request, response);
        RequestContext.init(cmdReq);
        return cmdReq;
    }

    public void afterRequestEnds(HttpServletRequest request, HttpServletResponse response) {
        // Clear the request context.
        RequestContext.destroy();

        // Finish the profiling of the request.
        Profiler.lookup().finishThreadProfile();
    }
}
