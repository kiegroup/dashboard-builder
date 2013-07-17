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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.factory.Component;
import org.jboss.dashboard.factory.ComponentsTree;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.factory.LookupException;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FactoryRequestHandler {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FactoryRequestHandler.class.getName());

    public CommandResponse handleRequest(CommandRequest request) throws Exception {
        // Set factory bean values
        setBeanValues(request);

        // Handle it
        return performRequestHandling(request);
    }

    protected void setBeanValues(CommandRequest request) {
        Map paramsMap = request.getRequestObject().getParameterMap();
        Map filesMap = request.getFilesByParamName();
        Set beansModified = new HashSet();

        //Normal parameters
        for (Iterator it = paramsMap.keySet().iterator(); it.hasNext();) {
            String paramName = (String) it.next();
            try {
                FactoryURL fUrl = FactoryURL.getURL(paramName);
                boolean newBeanProcessed = beansModified.add(fUrl.getComponentName());
                String[] paramValues = (String[]) paramsMap.get(paramName);
                setBeanValue(fUrl, paramValues, newBeanProcessed);
            } catch (ParseException e) {
                if (log.isDebugEnabled()) log.debug("Parameter " + paramName + " is not for setting a bean property.");
            } catch (LookupException e) {
                log.error("Error: ", e);
            }
        }

        //File parameters
        for (Iterator it = filesMap.keySet().iterator(); it.hasNext();) {
            String paramName = (String) it.next();
            try {
                FactoryURL fUrl = FactoryURL.getURL(paramName);
                boolean newBeanProcessed = beansModified.add(fUrl.getComponentName());
                File file = (File) filesMap.get(paramName);
                setBeanValue(fUrl, file, newBeanProcessed);
            } catch (ParseException e) {
                if (log.isDebugEnabled()) log.debug("Parameter " + paramName + " is not for setting a bean property.");
            } catch (LookupException e) {
                log.error("Error: ", e);
            }
        }
    }

    protected void setBeanValue(FactoryURL fUrl, File file, boolean firstTime) throws LookupException {
        ComponentsTree componentsTree = Application.lookup().getGlobalFactory().getTree();
        Component component = componentsTree.getComponent(fUrl.getComponentName());
        if (component == null) {
            log.error("Cannot write to component " + fUrl.getComponentName() + " as it doesn't exist.");
        } else {
            if (firstTime) {
                Object o = component.getObject();
                if (o instanceof HandlerFactoryElement) {
                    ((HandlerFactoryElement) o).clearFieldErrors();
                }
            }
            try {
                component.setProperty(fUrl.getPropertyName(), file);
            }
            catch (Exception e) {
                Object o = component.getObject();
                if (o instanceof HandlerFactoryElement) {
                    ((HandlerFactoryElement) o).addFieldError(fUrl, e, file);
                } else {
                    log.warn("Error setting value to " + fUrl + " is ignored.");
                }
            }
        }
    }

    protected void setBeanValue(FactoryURL fUrl, String[] paramValues, boolean firstTime) throws LookupException {
        ComponentsTree componentsTree = Application.lookup().getGlobalFactory().getTree();
        Component component = componentsTree.getComponent(fUrl.getComponentName());
        if (component == null) {
            log.error("Cannot write to component " + fUrl.getComponentName() + " as it doesn't exist.");
        } else {
            if (firstTime) {
                Object o = component.getObject();
                if (o instanceof HandlerFactoryElement) {
                    ((HandlerFactoryElement) o).clearFieldErrors();
                }
            }
            try {
                component.setProperty(fUrl.getPropertyName(), paramValues);
            }
            catch (Exception e) {
                Object o = component.getObject();
                if (o instanceof HandlerFactoryElement) {
                    ((HandlerFactoryElement) o).addFieldError(fUrl, e, paramValues);
                } else {
                    log.warn("Error setting value to " + fUrl + " is ignored.");
                }
            }
        }
    }

    protected CommandResponse performRequestHandling(CommandRequest request) throws Exception {
        String handlerComponentName = request.getRequestObject().getParameter(FactoryURL.PARAMETER_BEAN);
        String handlerComponentProperty = request.getRequestObject().getParameter(FactoryURL.PARAMETER_PROPERTY);
        if (!StringUtils.isEmpty(handlerComponentName) && !StringUtils.isEmpty(handlerComponentProperty)) {
            HandlerFactoryElement handlerComponent = null;
            try {
                handlerComponent = (HandlerFactoryElement) Factory.lookup(handlerComponentName);
            } catch (ClassCastException cce) {
                log.error("Component "+handlerComponentName+" is not a HandlerFactoryElement. It cannot process factory actions. ");
            }
            if (handlerComponent != null) {
                return handle(handlerComponent, handlerComponentProperty, request);
            } else {
                log.error("Unexistant component specified for request handling: " + handlerComponentName);
            }
        }
        return null;
    }

    protected CommandResponse handle(HandlerFactoryElement handlerComponent, String property, CommandRequest request) throws Exception {
        return handlerComponent.handle(request, property);
    }
}
