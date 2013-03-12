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
package org.jboss.dashboard.provider;

import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.domain.Domain;
import org.jboss.dashboard.LocaleManager;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.*;

/**
 * Base class for the implementation of custom data properties.
 */
public class AbstractDataProperty implements DataProperty {

    protected String id;
    protected Map nameI18nMap;
    protected DataSet dataSet;
    protected Domain domain;

    public AbstractDataProperty() {
        nameI18nMap = new HashMap();
        dataSet = null;
        domain = null;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }

    public boolean equals(Object obj) {
        try {
            if (obj == null) return false;
            if (obj == this) return true;
            if (id == null) return false;

            AbstractDataProperty other = (AbstractDataProperty) obj;
            return id.equals(other.id);
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    public String getPropertyId() {
        return id;
    }

    public void setPropertyId(String id) {
        this.id = id;
    }

    public String getName(Locale l) {
        // Get name in specified locale.
        String name = (String) nameI18nMap.get(l);
        if (name != null) return name;

        // Get name in default locale.
        LocaleManager lm = LocaleManager.lookup();
        name = (String) lm.localize(nameI18nMap);
        if (name != null) return name;

        // Use formatter to get the name.
        DataPropertyFormatter df = DataFormatterRegistry.lookup().getPropertyFormatter(id);
        if (df != null) return df.formatName(this, l);
        return id;
    }

    public void setName(String name, Locale l) {
        nameI18nMap.put(l, name);
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
        if (domain != null) domain.setProperty(this);
    }

    public List getValues() {
        if (dataSet == null) return new ArrayList();
            return dataSet.getPropertyValues(this);
    }

    public Map getNameI18nMap() {
        return nameI18nMap;
    }

    public void setNameI18nMap(Map nameI18nMap) {
        this.nameI18nMap = nameI18nMap;
    }

    public DataProperty cloneProperty() {
        try {
            AbstractDataProperty clone = (AbstractDataProperty) super.clone();
            clone.setDomain(domain.cloneDomain());
            clone.setNameI18nMap(new HashMap());
            Iterator it = nameI18nMap.keySet().iterator();
            while (it.hasNext()) {
                Locale locale = (Locale) it.next();
                String name = getName(locale);
                clone.setName(name, locale);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
