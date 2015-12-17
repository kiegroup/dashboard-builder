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
package org.jboss.dashboard.ui.components.permissions;

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

public class PermissionsAssignerFormatter extends Formatter {

    @Inject
    private transient Logger log;

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            renderFragment("tableStart");
            renderActions();
            renderFragment("tableEnd");
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    protected void renderActions() throws Exception {
        renderFragment("rolesSelection");
        Class permissionClass = PermissionsHandler.lookup().getPermissionClass();
        List actionList = (List) permissionClass.getField("LIST_OF_ACTIONS").get(permissionClass);
        Method actionNameMethod = null;
        try {
            actionNameMethod = permissionClass.getMethod("getActionName", new Class[]{String.class, String.class, Locale.class});
        } catch (NoSuchMethodException cnfe) {
            log.warn("Permission class doesn't provide method getActionName(String, String, Locale) to get action names.");
        }
        for (int i = 0; i < actionList.size(); i++) {
            String actionName = (String) actionList.get(i);
            String actionDescription = actionName;
            if (actionNameMethod != null) {
                actionDescription = (String) actionNameMethod.invoke(null, new Object[]{permissionClass.getName(), actionName, getLocale()});
            }
            setAttribute("actionDescription", actionDescription);
            setAttribute("actionName", actionName);
            //setAttribute("className", i % 2 == 1 ? "skn-even_row" : "skn-odd_row");
            renderFragment("outputAction");
        }
    }
}
