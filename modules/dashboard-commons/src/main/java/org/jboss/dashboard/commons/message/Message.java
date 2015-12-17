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
package org.jboss.dashboard.commons.message;

import java.util.Locale;

/**
 * A message.
 */
public interface Message {

    /**
     * The elements involved in the error (the error context).
     * @return An array of Object instances.
     */
    Object[] getElements();
    void setElements(Object[] elements);

    /**
     * The error code.
     */
    String getMessageCode();
    void setMessageCode(String errorCode);

    /**
     * A string representation of the error for the given locale.
     */
    String getMessage(Locale l);

    /**
     * Error message type.
     */
    int ERROR   = 10;

    /**
     * Warning message type.
     */
    int WARNING = 7;

    /**
     * Informative message type.
     */
    int INFO    = 5;

    /**
     * Get the message type.
     */
    int getMessageType();

    /**
     * Returns true if this message refers to an editable element
     */
    boolean isEditable();
}
