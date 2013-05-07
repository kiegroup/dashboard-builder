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
package org.jboss.dashboard.security;

import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.workspace.export.WorkspaceVisitor;
import org.jboss.dashboard.workspace.export.Visitable;

import java.security.Principal;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class UIPermission extends DefaultPermission implements Visitable {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(UIPermission.class.getName());

    private Principal relatedPrincipal;

    private String resourceName;
    private String actions;
    private boolean readOnly;

    public UIPermission(String resourceName, String actions) {
        super(resourceName, actions);
        this.resourceName = resourceName;
        this.actions = actions;
        readOnly = false;
    }

    /**
     * Set the related principal, useful only for visitor operations.
     */
    public void setRelatedPrincipal(Principal relatedPrincipal) {
        this.relatedPrincipal = relatedPrincipal;
    }

    /**
     * Get the related principal, useful only for visitor operations.
     */
    public Principal getRelatedPrincipal() {
        return relatedPrincipal;
    }

    public Object acceptVisit(WorkspaceVisitor visitor) throws Exception {
        visitor.visitPermission(this, relatedPrincipal);
        return visitor.endVisit();
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        try {
            final UIPermission that = (UIPermission) o;
            if (relatedPrincipal != null ? !relatedPrincipal.equals(that.relatedPrincipal) : that.relatedPrincipal != null) return false;
            if (resourceName != null ? !resourceName.equals(that.resourceName) : that.resourceName != null) return false;
            return super.equals(o);
        }
        catch (ClassCastException cce) {
            return false;   // Do not compare getClass() because it is slower
        }
    }

    public static UIPolicy getPolicy() {
        return (UIPolicy) SecurityServices.lookup().getSecurityPolicy();
    }

    public static String getActionName(String permissionClass, String action, Locale locale) {
        try {
            ResourceBundle messages = ResourceBundle.getBundle("org.jboss.dashboard.security.messages", locale);
            return messages.getString(permissionClass+".action." + action.replace(' ','_'));
        }
        catch (MissingResourceException mse) {
            log.warn("Can't find description for " + action + " in locale " + locale);
            return action;
        }
    }
}
