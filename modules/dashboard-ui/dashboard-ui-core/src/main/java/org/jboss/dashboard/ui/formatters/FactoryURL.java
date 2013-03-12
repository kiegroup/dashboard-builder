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
package org.jboss.dashboard.ui.formatters;

import org.apache.commons.lang.StringEscapeUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;

public class FactoryURL {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FactoryURL.class.getName());

    public static final String SCHEMA = "factory";
    public static final String NAME_FORMAT = SCHEMA + "://" + "{0}" + "/" + "{1}";
    protected static final MessageFormat msgf = new MessageFormat(NAME_FORMAT);

    public static final String PARAMETER_BEAN = "_fb";
    public static final String PARAMETER_PROPERTY = "_fp";

    private String componentName;
    private String propertyName;

    public FactoryURL(String componentName, String propertyName) {
        this.componentName = componentName;
        this.propertyName = propertyName;
    }

    public static FactoryURL getURL(String value) throws ParseException {
        ParsePosition pPos = new ParsePosition(0);
        Object[] o = msgf.parse(value, pPos);
        if (o == null)
            throw new ParseException("Cannot parse " + value + ". Error at position " + pPos.getErrorIndex(), pPos.getErrorIndex());
        String componentName = StringEscapeUtils.unescapeHtml((String) o[0]);
        String propertyName = StringEscapeUtils.unescapeHtml((String) o[1]);
        return new FactoryURL(componentName, propertyName);
    }

    public String getComponentName() {
        return componentName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(SCHEMA);
        sb.append("://");
        sb.append(StringEscapeUtils.escapeHtml(componentName));
        sb.append("/");
        sb.append(StringEscapeUtils.escapeHtml(propertyName));
        return sb.toString();
    }
}
