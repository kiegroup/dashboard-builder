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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.domain.CompositeInterval;
import org.jboss.dashboard.domain.Interval;

import javax.inject.Inject;
import java.util.*;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Install
public class DataPropertyFormatterImpl implements DataPropertyFormatter {

    /**
     * Get the set of property ids this formatter is addressed to.
     * @return null if this is a general purpose formatter.
     */
    public String[] getSupportedPropertyIds() {
        return null;
    }

    /**
     * Allowed date patterns during the parsing of input values for date properties.
     * WARNING: Do not alter the order of patterns.
     */
    private static SimpleDateFormat[] DATE_FORMATS = new SimpleDateFormat[] {
            /* 0 */ new SimpleDateFormat("dd/MM/yy"),
            /* 1 */ new SimpleDateFormat("MM/dd/yy"),
            /* 2 */ new SimpleDateFormat("yy/MM/dd"),
            /* 3 */ new SimpleDateFormat("yy/dd/MM"),
            /* 4 */ new SimpleDateFormat("dd/MM/yyyy"),
            /* 5 */ new SimpleDateFormat("MM/dd/yyyy"),
            /* 6 */ new SimpleDateFormat("yyyy/MM/dd"),
            /* 7 */ new SimpleDateFormat("yyyy/dd/MM"),
            /* 8 */ new SimpleDateFormat("MM/yy"),
            /* 9 */ new SimpleDateFormat("yy/MM"),
            /* 10 */ new SimpleDateFormat("MM/yyyy"),
            /* 11 */ new SimpleDateFormat("yyyy/MM"),
            /* 12 */ new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"), // Format for calendar picker js
            /* 13 */ new SimpleDateFormat("yy"),
            /* 14 */ new SimpleDateFormat("yyyy")};

    /** Empty value string */
    @Inject @Config("---")
    protected String emptyValueString;

    public String getEmptyValueString() {
        return emptyValueString;
    }

    public void setEmptyValueString(String emptyValueString) {
        this.emptyValueString = emptyValueString;
    }

    public Class getPropertyClass(DataProperty property) {
        return property.getDomain().getValuesClass();
    }

    public String formatName(DataProperty dp, Locale l) {
        return formatName(dp.getPropertyId(), l);
    }

    public String formatName(String propertyId, Locale l) {
        return propertyId;
    }

    public String formatValue(DataProperty property, Object value, Locale l) {
        return formatValue(property.getPropertyId(), value, l);
    }

    /**
     * Formats a value for a given property.
     * <p>The default formatting technique applied is quite simple. The value instance class
     * (Integer, Long, Boolean, Date, ...) is used to format the value as string.
     * The following list it shows which formatting mechanism is applied for a given
     * value class:
     * <ul>
     * <li>For null values the <code>nullValueFormat</code> is returned.
     * <li>Collection: comma-separated string is returned.
     * <li>Date: The internal <code>DateFormatter</code> class is used.
     * <li>For all other values the default <code>toString</code> method is used.
     * </ul>
     */
    public String formatValue(String propertyId, Object value, Locale l) {
        if (value == null) return "---";

        String toString = value.toString().trim();
        if (toString.equals("")) return "---";

        if (value instanceof String) {
            return toString;
        }
        if (value instanceof Number) {
            NumberFormat _numberFormat = NumberFormat.getNumberInstance(l);
            return _numberFormat.format(value);
        }
        if (value instanceof Date) {
            DateFormat _dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, l);
            String dateStr = _dateTimeFormat.format((Date) value);
            if (dateStr.indexOf("/") == 1) dateStr = "0" + dateStr;
            return dateStr;
        }
        if (value instanceof Collection) {
            if (((Collection) value).isEmpty()) return emptyValueString;
            return formatCollection(propertyId, (Collection) value, ", ", null, null, l);
        }
        if (value instanceof CompositeInterval) {
            CompositeInterval ci = (CompositeInterval) value;
            return formatCollection(propertyId, ci.getIntervals(), ", ", null, null, l);
        }
        if (value instanceof Interval) {
            Interval interval = (Interval) value;
            return interval.getDescription(l);
        }
        return toString;
    }

    /**
     * Convert the specified list of objects to a string format where each object
     * is transformed into a string and separated from the other objects by comma.
     * NOTE: The Object toString method is used to transform the object into a string.
     * @param separator The separator between elements in the collection.
     * @param prefix A string to be appended to the beginning of each token.
     * @param postfix A string to be appended to the end of each token.
     * @return A string containing all objects separated by <i>separator</i>.
     */
    public String formatCollection(String propertyId, Collection c, String separator, String prefix, String postfix, Locale l) {
        if (c == null || c.isEmpty()) return emptyValueString;
        StringBuffer buf = new StringBuffer();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next == null) continue;

            if (buf.length() > 0) buf.append(separator);
            if (prefix != null) buf.append(prefix);
            buf.append(formatValue(propertyId, next, l));
            if (postfix != null) buf.append(postfix);
        }
        return buf.toString();
    }

    /**
     * Parses a given string and returns a instance representing the java object value.
     */
    public Object parsePropertyValue(DataProperty property, String value) throws Exception {
        return parsePropertyValue(getPropertyClass(property), value);
    }

    /**
     * Parses a given string and returns a instance representing the java object value.
     */
    public Object parsePropertyValue(Class type, String value) throws Exception {
        if (value == null) return null;

        if (type.equals(Boolean.class)) {
            return Boolean.valueOf(value);
        }
        if (Number.class.isAssignableFrom(type)) {
            NumberFormat _numberFormat = NumberFormat.getNumberInstance(LocaleManager.currentLocale());
            return _numberFormat.parse(value);
        }
        if (type.equals(Date.class))    {
            try {
                DateFormat _dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, LocaleManager.currentLocale());
                return _dateTimeFormat.parse(value);
            } catch (ParseException e) {
                for (int i = 0; i < DATE_FORMATS.length; i++) {
                    try {
                        SimpleDateFormat allowedPattern = DATE_FORMATS[i];
                        allowedPattern.setLenient(false);
                        return allowedPattern.parse(value);
                    } catch (ParseException ee) {
                        // Try with the next date pattern ...
                        continue;
                    }
                }
                return null;
            }
        }
        // All other property types are considered string values and no parsing is needed.
        return value;
    }
}
