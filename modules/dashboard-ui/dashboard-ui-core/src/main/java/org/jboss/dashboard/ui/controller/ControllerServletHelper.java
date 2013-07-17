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

import org.jboss.dashboard.factory.FactoryLifecycle;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@ApplicationScoped
@Named("controllerServletHelper")
public class ControllerServletHelper {

    public static ControllerServletHelper lookup() {
        return (ControllerServletHelper) CDIBeanLocator.getBeanByName("controllerServletHelper");
    }

    private static transient Logger log = LoggerFactory.getLogger(ControllerServletHelper.class.getName());

    public CommandRequest initThreadLocal(HttpServletRequest request, HttpServletResponse response) {
        // Initialize threadLocal with request object
        CommandRequest cmdReq = new CommandRequestImpl(request, response);
        RequestContext.init(cmdReq);
        return cmdReq;
    }

    public void clearThreadLocal(HttpServletRequest request, HttpServletResponse response) {
        Enumeration en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            Object obj = request.getAttribute(name);
            if (obj instanceof FactoryLifecycle) {
                try {
                    ((FactoryLifecycle) obj).shutdown();
                } catch (Exception e) {
                    log.error("Error: ", e);
                }
            }
        }
        RequestContext.destroy();
    }
}
