/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.ui.formatters.FactoryURL;

public class FieldException extends Exception {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FieldException.class.getName());

    private FactoryURL property;
    private Object propertyValue;
    private Exception cause;

    public FieldException(String message, FactoryURL property, Object propertyValue, Exception cause) {
        super(message, cause);
        this.property = property;
        this.propertyValue = propertyValue;
        this.cause = cause;
    }

    public FactoryURL getProperty() {
        return property;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    public Throwable getCause() {
        return cause;
    }
}
