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
package org.jboss.dashboard.domain.numeric;

import org.jboss.dashboard.domain.AbstractInterval;
import java.util.Locale;


/**
 * A numeric interval has a min and a max values.
 */
public class NumericInterval extends AbstractInterval {

    protected Number minValue;
    protected Number maxValue;
    protected boolean minValueIncluded;
    protected boolean maxValueIncluded;

    public NumericInterval() {
        minValue = null;
        maxValue = null;
        minValueIncluded = true;
        maxValueIncluded = false;
    }

    public Number getMinValue() {
        return minValue;
    }

    public void setMinValue(Number minValue) {
        this.minValue = minValue;
    }

    public Number getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Number maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isMinValueIncluded() {
        return minValueIncluded;
    }

    public void setMinValueIncluded(boolean minValueIncluded) {
        this.minValueIncluded = minValueIncluded;
    }

    public boolean isMaxValueIncluded() {
        return maxValueIncluded;
    }

    public void setMaxValueIncluded(boolean maxValueIncluded) {
        this.maxValueIncluded = maxValueIncluded;
    }

    public String getDescription(Locale l) {
        // TODO: complete
        return String.valueOf(minValue.longValue());
    }

    public boolean contains(Object value) {
        try {
            if (value == null) return false;
            Number n = (Number) value;
            if (minValue != null && minValueIncluded && n.doubleValue() < minValue.doubleValue()) return false;
            if (minValue != null && !minValueIncluded && n.doubleValue() <= minValue.doubleValue()) return false;
            if (maxValue != null && maxValueIncluded && n.doubleValue() > maxValue.doubleValue()) return false;
            if (maxValue != null && !maxValueIncluded && n.doubleValue() >= maxValue.doubleValue()) return false;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}