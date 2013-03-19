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

import org.jboss.dashboard.factory.BasicFactoryElement;
import org.jboss.dashboard.factory.Component;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.SendStreamResponse;
import org.jboss.dashboard.profiler.*;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

public abstract class HandlerFactoryElement extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HandlerFactoryElement.class.getName());

    private List propertyErrors = new ArrayList();
    private Set wrongFields = new HashSet();
    private Properties actionsShortcuts = new Properties();
    private Properties reverseActionsShortcuts = new Properties();
    private boolean useActionShortcuts = true;
    private boolean enableDoubleClickControl = false;
    private boolean enabledForActionHandling = false;

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

    public void start() throws Exception {
        super.start();
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
        if (log.isDebugEnabled()) log.debug("Entering handle " + getComponentName() + " action: " + action);

        // Calculate the action to invoke.
        action = StringUtils.capitalize(action);
        action = reverseActionsShortcuts.getProperty(action, action);

        // Double click control.
        if (isEnableDoubleClickControl() && !isEnabledForActionHandling() && isScopeAdequateForDoubleClickControl()) {
            // Duplicates can only be prevented in session or panel session components, and if they are enabled for that purpose
            log.warn("Discarding duplicated execution in component " + getComponentName() + ", action: " + action + ". User should be advised not to double click!");
            return null;
        }

        // Invoke the component's action and keep track of the execution.
        CodeBlockTrace trace = new HandlerTrace(this, action).begin();
        try {
            String methodName = "action" + action;
            beforeInvokeAction(request, action);
            CommandResponse response = null;
            Method handlerMethod = this.getClass().getMethod(methodName, new Class[]{CommandRequest.class});
            if (log.isDebugEnabled()) log.debug("Invoking method " + methodName + " on " + getComponentName());
            response = (CommandResponse) handlerMethod.invoke(this, new Object[]{request});
            afterInvokeAction(request, action);

            if (response == null || !(response instanceof SendStreamResponse)) {
                setEnabledForActionHandling(false);
            }
            if (log.isDebugEnabled()) log.debug("Leaving handle " + getComponentName() + " - " + action);
            return response;
        } finally {
            trace.end();
        }
    }

    protected boolean isScopeAdequateForDoubleClickControl() {
        return Component.SCOPE_SESSION.equals(getComponentScope()) || Component.SCOPE_PANEL_SESSION.equals(getComponentScope());
    }

    protected void beforeInvokeAction(CommandRequest request, String action) throws Exception {
    }

    protected void afterInvokeAction(CommandRequest request, String action) throws Exception {
    }

    public void addFieldError(FactoryURL property, Exception e, Object propertyValue) {
        propertyErrors.add(new FieldException("Error setting property to component", property, propertyValue, e));
        wrongFields.add(property.getPropertyName());
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

    static class HandlerTrace extends CodeBlockTrace {

        protected String bean;
        protected String action;
        protected String scope;
        protected Map<String,Object> context;

        public HandlerTrace(HandlerFactoryElement handler, String action) {
            super(handler.getComponentName());
            this.bean = handler.getComponentName();
            this.scope = handler.getComponentScope();
            this.action = action;
            this.context = buildContext();
        }

        public CodeBlockType getType() {
            return CoreCodeBlockTypes.UI_COMPONENT;
        }

        public String getDescription() {
            return bean + "." + action + "()";
        }

        public Map<String,Object> getContext() {
            return context;
        }

        protected Map<String,Object> buildContext() {
            Map<String,Object> ctx = new LinkedHashMap<String,Object>();
            ctx.put("Bean name", bean);
            ctx.put("Bean scope", scope);
            ctx.put("Bean action", action);

            ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
            if (threadProfile != null) threadProfile.addContextProperties(ctx);                        
            return ctx;
        }
    }
}
