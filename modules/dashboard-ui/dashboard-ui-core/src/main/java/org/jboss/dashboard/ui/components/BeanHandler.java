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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.SendStreamResponse;
import org.jboss.dashboard.profiler.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;


public abstract class BeanHandler implements Serializable {

    private transient Logger log = LoggerFactory.getLogger(BeanHandler.class);

    private List propertyErrors = new ArrayList();
    private Set wrongFields = new HashSet();
    private Properties actionsShortcuts = new Properties();
    private Properties reverseActionsShortcuts = new Properties();
    private boolean useActionShortcuts = true;
    private boolean enableDoubleClickControl = false;
    private boolean enabledForActionHandling = false;

    public String getBeanName() {
        Named named = this.getClass().getAnnotation(Named.class);
        if (named != null) return named.value();
        return this.getClass().getName();
    }

    public boolean isUseActionShortcuts() {
        return useActionShortcuts;
    }

    public void setUseActionShortcuts(boolean useActionShortcuts) {
        this.useActionShortcuts = useActionShortcuts;
    }

    public boolean isEnableDoubleClickControl() {
        return enableDoubleClickControl;
    }

    public void setEnableDoubleClickControl(boolean enableDoubleClickControl) {
        this.enableDoubleClickControl = enableDoubleClickControl;
    }

    public boolean isEnabledForActionHandling() {
        return enabledForActionHandling;
    }

    public void setEnabledForActionHandling(boolean enabledForActionHandling) {
        this.enabledForActionHandling = enabledForActionHandling;
    }

    @PostConstruct
    public void start() {
        if (isUseActionShortcuts()) {
            calculateActionShortcuts();
        }
    }

    protected void calculateActionShortcuts() {
        Method[] allMethods = this.getClass().getMethods();
        TreeSet actionNames = new TreeSet();
        for (int i = 0; i < allMethods.length; i++) {
            Method method = allMethods[i];
            if (method.getName().startsWith("action")) {
                Class[] classes = method.getParameterTypes();
                if (classes != null && classes.length == 1) {
                    Class paramClass = classes[0];
                    if (paramClass.equals(CommandRequest.class)) {
                        String actionName = method.getName().substring("action".length());
                        actionNames.add(actionName);
                    }
                }
            }
        }
        int index = 0;
        for (Iterator iterator = actionNames.iterator(); iterator.hasNext(); index++) {
            String action = (String) iterator.next();
            actionsShortcuts.put(action, String.valueOf(index));
            reverseActionsShortcuts.put(String.valueOf(index), action);
        }
    }

    /**
     * Get the action name for given action name. If there is a shortcut, it will be returned, otherwise, the same name applies.
     *
     * @param actionName action name
     * @return the action name for given action name
     */
    public String getActionName(String actionName) {
        actionName = StringUtils.capitalize(actionName);
        return actionsShortcuts.getProperty(actionName, actionName);
    }

    /**
     * Get the action name for a given shortcut.
     *
     * @param shortcut the possible shortcut whose action name is to be obtained.
     * @return the action name for the given shortcut if found, or the shortcut itself if not.
     */
    public String getActionForShortcut(String shortcut) {
        String actionName = reverseActionsShortcuts.getProperty(shortcut);
        return actionName != null ? actionName : shortcut;
    }

    public synchronized CommandResponse handle(CommandRequest request, String action) throws Exception {
        if (log.isDebugEnabled()) log.debug("Entering handle " + getBeanName() + " action: " + action);

        // Calculate the action to invoke.
        action = StringUtils.capitalize(action);
        action = reverseActionsShortcuts.getProperty(action, action);

        // Double click control.
        if (isEnableDoubleClickControl() && !isEnabledForActionHandling()) {
            // Duplicates can only be prevented in session components, and if they are enabled for that purpose
            log.warn("Discarding duplicated execution in component " + getBeanName() + ", action: " + action + ". User should be advised not to double click!");
            return null;
        }

        // Invoke the component's action and keep track of the execution.
        CodeBlockTrace trace = new HandlerTrace(this, action).begin();
        try {
            String methodName = "action" + action;
            beforeInvokeAction(request, action);
            CommandResponse response = null;
            Method handlerMethod = this.getClass().getMethod(methodName, new Class[]{CommandRequest.class});
            if (log.isDebugEnabled()) log.debug("Invoking method " + methodName + " on " + getBeanName());
            response = (CommandResponse) handlerMethod.invoke(this, new Object[]{request});
            afterInvokeAction(request, action);

            if (response == null || !(response instanceof SendStreamResponse)) {
                setEnabledForActionHandling(false);
            }

            if (log.isDebugEnabled()) log.debug("Leaving handle " + getBeanName() + " - " + action);
            return response;
        } finally {
            trace.end();
        }
    }

    protected void beforeInvokeAction(CommandRequest request, String action) throws Exception {
    }

    protected void afterInvokeAction(CommandRequest request, String action) throws Exception {
    }

    public void addFieldError(FactoryURL property, Exception e, Object propertyValue) {
        propertyErrors.add(new FieldException("Error setting property to component", property, propertyValue, e));
        wrongFields.add(property.getFieldName());
    }

    public void clearFieldErrors() {
        propertyErrors.clear();
        wrongFields.clear();
    }

    public List getFieldErrors() {
        return Collections.unmodifiableList(propertyErrors);
    }

    public boolean hasError(String fieldName) {
        return wrongFields.contains(fieldName);
    }

    public void actionVoid(CommandRequest request) {
    }

    public static class HandlerTrace extends CodeBlockTrace {

        protected String bean;
        protected String action;
        protected Map<String,Object> context;

        public HandlerTrace(BeanHandler handler, String action) {
            super(handler.getBeanName());
            this.bean = handler.getBeanName();
            this.action = action;
            this.context = buildContext();
        }

        public CodeBlockType getType() {
            return CoreCodeBlockTypes.UI_COMPONENT;
        }

        public String getDescription() {
            return bean + (StringUtils.isBlank(action) ? "" : "." + action + "()");
        }

        public Map<String,Object> getContext() {
            return context;
        }

        protected Map<String,Object> buildContext() {
            Map<String,Object> ctx = new LinkedHashMap<String,Object>();
            ctx.put("Bean name", bean);
            if (!StringUtils.isBlank(action)) {
                ctx.put("Bean action", action);
            }

            ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
            if (threadProfile != null) threadProfile.addContextProperties(ctx);
            return ctx;
        }
    }
}
