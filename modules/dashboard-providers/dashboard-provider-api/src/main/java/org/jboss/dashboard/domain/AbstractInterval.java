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
package org.jboss.dashboard.domain;

import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.LocaleManager;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Base class for the implementation of the different Interval classes. 
 */
public abstract class AbstractInterval implements Interval, Comparable {

    protected Domain domain;

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public List getValues(DataProperty p) {
        List results = new ArrayList();
        List dvalues = domain.getProperty().getValues();
        List pvalues = p.getValues();
        for (int i = 0; i < pvalues.size(); i++) {
            Object dvalue = dvalues.get(i);
            Object pvalue = pvalues.get(i);
            if (this.contains(dvalue)) results.add(pvalue);
        }
        return results;
    }

    // Comparable interface

    public int compareTo(Object o) {
        try {
            if (o == null) return 1;
            Interval other = (Interval) o;
            Locale locale = LocaleManager.currentLocale();
            return getDescription(locale).compareTo(other.getDescription(locale));
        } catch (ClassCastException e) {
            return 1;
        }
    }
}