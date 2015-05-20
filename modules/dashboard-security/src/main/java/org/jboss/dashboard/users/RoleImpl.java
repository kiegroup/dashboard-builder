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
package org.jboss.dashboard.users;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jboss.dashboard.commons.comparator.ComparatorUtils;

import java.util.Locale;

public class RoleImpl implements Role {

    protected String name;
    protected String description;

    public RoleImpl() {
    }

    public RoleImpl(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription(Locale locale) {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(getName()).toHashCode();
    }

    public boolean equals(Object obj) {
        try {
            if (obj == null) return false;
            if (obj == this) return true;
            if (name == null) return false;

            RoleImpl other = (RoleImpl) obj;
            return name.equals(other.name);
        }
        catch (ClassCastException e) {
            return false;
        }
    }

}
