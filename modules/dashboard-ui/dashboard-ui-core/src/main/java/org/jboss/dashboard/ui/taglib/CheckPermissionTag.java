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
package org.jboss.dashboard.ui.taglib;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.users.UserStatus;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.security.Permission;

public class CheckPermissionTag extends BaseTag {

    /**
     * The permission to check
     */
    private Permission permission;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    protected UserStatus getUserStatus() {
        return UserStatus.lookup();
    }

    /**
     * @see javax.servlet.jsp.tagext.BodyTagSupport
     */
    public int doAfterBody() throws JspException {
        BodyContent body = getBodyContent();
        String html = body.getString();
        body.clearBody();

        try {
            boolean hasPerm = getUserStatus().hasPermission(permission);
            if (hasPerm) getPreviousOut().print(html);
            return SKIP_BODY;
        } catch (Exception ex) {
            throw new JspException("I/O exception " , ex);
        }
    }
}
