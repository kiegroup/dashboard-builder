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
package org.jboss.dashboard.ui.utils.forms;

import org.jboss.dashboard.workspace.Panel;

import javax.servlet.http.HttpServletRequest;

public class RenderUtils {

    /*
     * These are some useful functions to render panels
     */
    public static String noNull(Object str) {
        return str == null ? "" : str.toString();
    }

    /*
     * These are some useful functions to render panels
     */
    public static String noNull(Object str, String defaultValue) {
        return str == null ? defaultValue : str.toString();
    }

    // Renders a form title according to its validation status
    public static String field(HttpServletRequest req, String field, String desc, FormStatus form, boolean isMandatory) {
        desc = isMandatory ? "* " + desc : desc;
        if (!form.isValidated(field)) {
            return "<b class=\"skn-error\">" + desc + "</b>:";
        } else {
            return desc + ":";
        }
    }

    // Renders a form title according to its validation status
    public static String field(HttpServletRequest req, String field, String desc, FormStatus form) {
        return field(req, field, desc, form, false);
    }

    // Renders a form title according to its validation status
    public static String field(String field, String desc, FormStatus form, boolean isMandatory) {
        String mandatory = isMandatory ? " (*)" : "";
        if (!form.isValidated(field)) {
            return "<b class=\"skn-error\">" + desc + "</b>" + mandatory + ":";
        } else {
            return desc + mandatory + ":";
        }
    }

    // Renders a form field name, retrieving its display value from the panel's properties file
    public static String field(Panel panel, String field, FormStatus form, boolean isMandatory) {
        String desc = panel.getProperties().getProperty("field." + field);
        if (desc == null) {
            desc = field;
        }
        return field(field, desc, form, isMandatory);
    }

    // Renders a form field name, retrieving its display value from the panel's properties file
    public static String field(Panel panel, String field) {
        String desc = panel.getProperties().getProperty("field." + field);
        if (desc == null) {
            desc = field;
        }
        return desc;
    }
}
