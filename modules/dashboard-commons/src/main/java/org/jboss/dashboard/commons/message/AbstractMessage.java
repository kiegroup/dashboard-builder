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

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Base implementation of the Message interface.
 */
public abstract class AbstractMessage extends Exception implements Message {

    /**
     * The process elements involved in the message.
     */
    protected Object[] elements;

    /**
     * The message code.
     */
    protected String messageCode;

    /**
     * Message type;
     */
    protected int messageType;

    /**
     * Default constructor.
     * @param messageCode The code of the message.
     * @param elements The process element that produces the message.
     */
    public AbstractMessage(String messageCode, Object[] elements) {
        super();
        this.elements = elements;
        this.messageCode = messageCode;
        this.messageType = INFO;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Message)) return false;

        Message other = (Message)obj;
        if (this.getMessageType() != other.getMessageType()) return false;
        if (!this.getMessageCode().equals(other.getMessageCode())) return false;
        return true;
    }

    public Object[] getElements() {
        return elements;
    }

    public void setElements(Object[] elements) {
        this.elements = elements;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessage(Locale l) {
        StringBuffer messageStr = new StringBuffer(getMessage(messageCode, l));
        if (elements == null) return messageStr.toString();
        for (int i = 0; i < elements.length; i++) {
            Object element = elements[i];
            if (element == null) continue;
            String elementStr =  toString(element, l);
            if (!elementStr.equals("")) {
                if (i == 0) messageStr.append(" ");
                if (i > 0) messageStr.append(", ");
                messageStr.append(elementStr);
            }
        }
        //avoid ending message with >1 full-stop (due to call toString(element, l)), so check that first!
        if (!messageStr.substring(messageStr.length()-1).equals(".")) messageStr.append(".");
        return messageStr.toString();
    }

    /**
     * Process element to string converter.
     */
    public String toString(Object element, Locale l) {
        if (element == null) return "";
        if (element instanceof Collection) {
            StringBuilder buf = new StringBuilder();
            buf.append("[");
            for (Object o : (Collection) element) {
                buf.append(toString(o, l)).append(" ");
            }
            buf.append("]");
            return buf.toString();
        }
        if (element instanceof Map) {
            StringBuilder buf = new StringBuilder();
            buf.append("[");
            Map m = (Map) element;
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry row = (Map.Entry) it.next();
                buf.append(toString(row.getKey(), l)).append("=").append(toString(row.getValue(), l)).append(" ");
            }
            buf.append("]");
            return buf.toString();
        }
        return element.toString();
    }

    public boolean isEditable() {
        return (elements != null && elements.length > 0);
    }

    /**
     * To be implemented by the subclass.
     */
    public abstract String getMessage(String messageCode, Locale l);
}
