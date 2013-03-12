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

import org.jboss.dashboard.domain.AbstractInterval;
import org.jboss.dashboard.provider.DataFormatterRegistry;
import org.jboss.dashboard.provider.DataPropertyFormatter;
import org.jboss.dashboard.provider.DataProperty;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A label interval always matchs a given value of the domain.<br/>
 * f.i: an address, a person's name, ...
 */
public class LabelInterval extends AbstractInterval {

    protected String label;

    public LabelInterval() {
        super();
        label = null;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(getLabel()).toHashCode();
    }

    public boolean equals(Object obj) {
        try {
            if (obj == null) return false;
            if (obj == this) return true;

            LabelInterval other = (LabelInterval)obj;
            return contains(other.label);
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription(Locale l) {
        DataFormatterRegistry dfr = DataFormatterRegistry.lookup();
        DataProperty property = getDomain().getProperty();
        if (property == null) return label;

        DataPropertyFormatter dpf = dfr.getPropertyFormatter(property.getPropertyId());
        return dpf.formatValue(property, label, l);
    }

    public boolean contains(Object value) {
        if (value == null) return label == null;
        if (label == null) return false;
        return label.equals(value.toString());
    }
}
