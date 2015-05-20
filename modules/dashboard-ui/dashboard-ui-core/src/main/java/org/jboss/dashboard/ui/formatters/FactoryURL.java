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

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;

public class FactoryURL {

    public static final String SCHEMA = "bean";
    public static final String NAME_FORMAT = SCHEMA + "://" + "{0}" + "/" + "{1}";
    protected static final MessageFormat msgf = new MessageFormat(NAME_FORMAT);

    public static final String PARAMETER_BEAN = "_fb";
    public static final String PARAMETER_ACTION = "_fp";

    private String beanName;
    private String fieldName;

    public FactoryURL(String beanName, String fieldName) {
        this.beanName = beanName;
        this.fieldName = fieldName;
    }

    public FactoryURL(String value) throws ParseException {
        ParsePosition pPos = new ParsePosition(0);
        Object[] o = msgf.parse(value, pPos);
        if (o == null) throw new ParseException("Cannot parse " + value + ". Error at position " + pPos.getErrorIndex(), pPos.getErrorIndex());

        beanName = StringEscapeUtils.UNESCAPE_HTML4.translate((String) o[0]);
        fieldName = StringEscapeUtils.UNESCAPE_HTML4.translate((String) o[1]);
    }

    public String getBeanName() {
        return beanName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(SCHEMA);
        sb.append("://");
        sb.append(StringEscapeUtils.ESCAPE_HTML4.translate(beanName));
        sb.append("/");
        sb.append(StringEscapeUtils.ESCAPE_HTML4.translate(fieldName));
        return sb.toString();
    }
}
