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
package org.jboss.dashboard.security.principals;

import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.users.Role;

import java.io.Serializable;
import java.security.Principal;

public class DefaultPrincipal implements Principal, Serializable {

    private String name;

    public DefaultPrincipal() {
        this.name = null;
    }

    public DefaultPrincipal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        String desc = "";
        Role role = SecurityServices.lookup().getRolesManager().getRoleById(this.getName().substring(this.getName().indexOf('-') + 1));
        if (role != null) desc = role.getName();
        return StringUtils.isNotBlank(desc) ? desc : this.getName();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultPrincipal that = (DefaultPrincipal) o;
        if (!name.equals(that.name)) return false;
        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

}
