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

import java.util.*;

/**
 * Data property formatter.
 */
public interface DataPropertyFormatter {

    /**
     * Get the set of property ids this formatter is addressed to.
     * @return null if this is a general purpose formatter.
     */
    String[] getSupportedPropertyIds();

    Class getPropertyClass(DataProperty property);

    String formatName(DataProperty dp, Locale l);

    String formatName(String propertyId, Locale l);

    String formatValue(DataProperty property, Object value, Locale l);

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
    String formatValue(String propertyId, Object value, Locale l);

    /**
     * Parses a given string and returns a instance representing the java object value.
     */
    Object parsePropertyValue(DataProperty property, String value) throws Exception;

    /**
     * Parses a given string and returns a instance representing the java object value.
     */
    Object parsePropertyValue(Class type, String value) throws Exception;
}
