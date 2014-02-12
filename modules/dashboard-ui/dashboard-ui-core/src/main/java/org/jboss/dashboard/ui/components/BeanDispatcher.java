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

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.commons.misc.ReflectionUtils;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BeanDispatcher {

    @Inject
    private transient Logger log;

    public CommandResponse handleRequest(CommandRequest request) throws Exception {
        // Set bean values
        setBeanValues(request);

        // Handle it
        return performRequestHandling(request);
    }

    protected void setBeanValues(CommandRequest request) throws Exception {
        Map paramsMap = request.getRequestObject().getParameterMap();
        Map filesMap = request.getFilesByParamName();
        Set beansModified = new HashSet();

        // Normal parameters
        for (Iterator it = paramsMap.keySet().iterator(); it.hasNext();) {
            String paramName = (String) it.next();
            try {
                FactoryURL fUrl = new FactoryURL(paramName);
                boolean newBeanProcessed = beansModified.add(fUrl.getBeanName());
                String[] paramValues = (String[]) paramsMap.get(paramName);
                setBeanValue(fUrl, paramValues, newBeanProcessed);
            } catch (ParseException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Parameter " + paramName + " is not for setting a bean field.");
                }
            }
        }

        // File parameters
        for (Iterator it = filesMap.keySet().iterator(); it.hasNext();) {
            String paramName = (String) it.next();
            try {
                FactoryURL fUrl = new FactoryURL(paramName);
                boolean newBeanProcessed = beansModified.add(fUrl.getBeanName());
                File file = (File) filesMap.get(paramName);
                setBeanValue(fUrl, file, newBeanProcessed);
            } catch (ParseException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Parameter " + paramName + " is not for setting a bean field.");
                }
            }
        }
    }

    protected void setBeanValue(FactoryURL fUrl, Object value, boolean firstTime) throws Exception {
        Object bean = CDIBeanLocator.getBeanByNameOrType(fUrl.getBeanName());
        if (bean == null) {
            throw new Exception("Cannot write to component " + fUrl.getBeanName() + " as it doesn't exist.");
        }
        if (firstTime) {
            if (bean instanceof BeanHandler) {
                ((BeanHandler) bean).clearFieldErrors();
            }
        }
        try {
            if (value instanceof String) {
                ReflectionUtils.parseAndSetFieldValue(bean, fUrl.getFieldName(), (String) value);
            } else if (value instanceof String[]) {
                ReflectionUtils.parseAndSetFieldValues(bean, fUrl.getFieldName(), (String[]) value);
            } else if (value instanceof File) {
                ReflectionUtils.setFieldFile(bean, fUrl.getFieldName(), (File) value);
            }
        }
        catch (Exception e) {
            if (bean instanceof BeanHandler) {
                ((BeanHandler) bean).addFieldError(fUrl, e, value);
            } else {
                log.warn("Error setting value to " + fUrl + " is ignored.");
            }
        }
    }

    protected CommandResponse performRequestHandling(CommandRequest request) throws Exception {
        String beanName = request.getRequestObject().getParameter(FactoryURL.PARAMETER_BEAN);
        String beanAction = request.getRequestObject().getParameter(FactoryURL.PARAMETER_ACTION);
        if (!StringUtils.isEmpty(beanName) && !StringUtils.isEmpty(beanAction)) {
            BeanHandler handlerComponent = null;
            try {
                handlerComponent = (BeanHandler) CDIBeanLocator.getBeanByNameOrType(beanName);
            } catch (ClassCastException cce) {
                log.error("Bean " + beanName + " is not a BeanHandler.");
            }
            if (handlerComponent != null) {
                return handle(handlerComponent, beanAction, request);
            } else {
                log.error("Unexistant bean specified for request handling: " + beanName);
            }
        }
        return null;
    }

    protected CommandResponse handle(BeanHandler handlerComponent, String property, CommandRequest request) throws Exception {
        return handlerComponent.handle(request, property);
    }
}
