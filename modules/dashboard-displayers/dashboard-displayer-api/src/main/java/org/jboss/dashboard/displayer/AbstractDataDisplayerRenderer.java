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
package org.jboss.dashboard.displayer;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;
import java.util.Locale;

public abstract class AbstractDataDisplayerRenderer implements DataDisplayerRenderer {

    public int hashCode() {
        return new HashCodeBuilder().append(getUid()).toHashCode();
    }

    public boolean equals(Object obj) {
        try {
            if (obj == null) return false;
            if (obj == this) return true;
            if (getUid() == null) return false;

            AbstractDataDisplayerRenderer other = (AbstractDataDisplayerRenderer) obj;
            return getUid().equals(other.getUid());
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    public boolean isFeatureSupported(DataDisplayer displayer, DataDisplayerFeature feature) {
        return true;
    }

    public List<String> getAvailableChartTypes(DataDisplayer displayer) {
        // N/A
        return null;
    }

    public String getDefaultChartType(DataDisplayer displayer) {
        // N/A
        return null;
    }

    public String getChartTypeDescription(String chartType, Locale locale) {
        // N/A
        return null;
    }

    public void setDefaultSettings(DataDisplayer displayer) {
    }
}