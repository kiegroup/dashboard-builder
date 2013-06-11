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
package org.jboss.dashboard.domain.label;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.dataset.index.DistinctValue;
import org.jboss.dashboard.domain.AbstractInterval;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.provider.DataFormatterRegistry;
import org.jboss.dashboard.provider.DataPropertyFormatter;
import org.jboss.dashboard.provider.DataProperty;

import java.util.*;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A label interval always matchs a given value of the domain.<br/>
 * f.i: an address, a person's name, ...
 */
public class LabelInterval extends AbstractInterval {

    protected DistinctValue holder;

    public LabelInterval() {
        super();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(getLabel()).toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;

        return contains(obj);
    }

    public DistinctValue getHolder() {
        return holder;
    }

    public void setHolder(DistinctValue holder) {
        this.holder = holder;
    }

    public String getLabel() {
        return holder.value.toString();
    }

    public String getDescription(Locale l) {
        DataFormatterRegistry dfr = DataFormatterRegistry.lookup();
        DataProperty property = getDomain().getProperty();
        if (property == null) return getLabel();

        DataPropertyFormatter dpf = dfr.getPropertyFormatter(property.getPropertyId());
        return dpf.formatValue(property, getLabel(), l);
    }

    public List getValues(DataProperty p) {
        List results = new ArrayList();
        List targetValues = p.getValues();
        for (Integer targetRow : holder.rows) {
            results.add(targetValues.get(targetRow));
        }
        return results;
    }

    public boolean contains(Object value) {
        if (value == null) return false;

        Object other = value;
        if (other instanceof LabelInterval) {
            other = ((LabelInterval) value).getLabel();
        }
        if (getLabel() == null) return other == null;
        if (other == null) return getLabel() == null;
        return getLabel().equals(other.toString());
    }

    public String toString() {
        return getDescription(LocaleManager.currentLocale());
    }
}
