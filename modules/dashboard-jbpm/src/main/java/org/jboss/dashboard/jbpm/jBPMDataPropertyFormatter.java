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
package org.jboss.dashboard.jbpm;

import java.util.*;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.provider.DataPropertyFormatterImpl;

@Install
public class jBPMDataPropertyFormatter extends DataPropertyFormatterImpl {

    public String[] getSupportedPropertyIds() {
        return new String[] {"duration"};
    }
    
    public String formatValue(String propertyId, Object value, Locale l) {
        // If the value is null or the value has no a predefined format string invoke its custom format method (if exists).
        Method m = null;
        try { m = this.getClass().getMethod("formatValue_" + propertyId, new Class[] {Object.class, Locale.class}); } catch (NoSuchMethodException e) { /* Ignore */}
        try { if (m != null) return (String) m.invoke(this, new Object[] {value, l}); } catch (Exception e2) { /* Ignore */ }

        // If no custom format method is found then apply a default format.
        return super.formatValue(propertyId, value, l);
    }

    public String formatValue_duration(Object value, Locale l) throws Exception {
        if (value == null || !(value instanceof Number)) return "---";
        Number lengthInSeconds = (Number) value;
        long millis = lengthInSeconds.longValue() * 1000;
        if (millis < 0) millis = 0;
        return formatElapsedTime(millis);
    }

    public String formatElapsedTime(long millis) {
        long milliseconds = millis;
        long seconds = milliseconds / 1000; milliseconds %= 1000;
        long minutes = seconds / 60; seconds %= 60;
        long hours = minutes / 60; minutes %= 60;
        long days = hours / 24; hours %= 24;
        long weeks = days / 7; days %= 7;

        ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.jbpm.messages");
        String pattern = "ellapsedtime.hours";
        if (days > 0) pattern = "ellapsedtime.days";
        if (weeks > 0) pattern = "ellapsedtime.weeks";
        return MessageFormat.format(i18n.getString(pattern), new Long[] {new Long(seconds), new Long(minutes), new Long(hours), new Long(days), new Long(weeks)});
    }
}

