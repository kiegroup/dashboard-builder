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
package org.jboss.dashboard.ui.utils.forms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holds the intermediate status of a form along its submission process.
 * It keeps track of validated/wrong fields, and messages and errors to show.
 */
public class FormStatus {

    /**
     * Set of text messages identifiers that must be shown
     */
    private Set messages = new HashSet();

    /**
     * Set of errors that must be shown
     */
    private Set errors = new HashSet();

    /**
     * Set holding all the fields identifiers which are wrong
     */
    private Set wrongFields = new HashSet();

    /**
     * Map
     */
    private Map fieldsValues = new HashMap();

    public FormStatus() {
    }

    public void clear() {
        clearMessages();
        clearErrors();
        clearWrongFields();
        clearValues();
    }

    public void clearMessages() {
        messages.clear();
    }

    public void addMessage(String messageId) {
        messages.add(messageId);
    }

    public void removeMessage(String messageId) {
        messages.remove(messageId);
    }

    public String[] getMessages() {
        return (String[]) messages.toArray(new String[messages.size()]);
    }

    public void addError(String errorId) {
        errors.add(errorId);
    }

    public void removeError(String errorId) {
        errors.remove(errorId);
    }

    public String[] getErrors() {
        return (String[]) errors.toArray(new String[errors.size()]);
    }

    public void clearErrors() {
        errors.clear();
    }


    public void addWrongField(String fieldId) {
        wrongFields.add(fieldId);
    }

    public void removeWrongField(String fieldId) {
        wrongFields.remove(fieldId);
    }

    public boolean isValidated(String fieldId) {
        return !wrongFields.contains(fieldId);
    }

    public void clearWrongFields() {
        wrongFields.clear();
    }

    public String[] getWrongFields() {
        return (String[]) wrongFields.toArray(new String[wrongFields.size()]);
    }

    public boolean isValidated() {
        return wrongFields.size() == 0;
    }

    public void clearValues() {
        fieldsValues.clear();
    }

    public void setValue(String fieldId, Object value) {
        if (value == null)
            fieldsValues.remove(fieldId);
        else
            fieldsValues.put(fieldId, value);
    }

    public void setValue(String fieldId, int value) {
        fieldsValues.put(fieldId, new Integer(value));
    }

    public void setValue(String fieldId, boolean value) {
        fieldsValues.put(fieldId, new Boolean(value));
    }

    public void removeValue(String fieldId) {
        fieldsValues.remove(fieldId);
    }

    public Object getValue(String fieldId) {
        return fieldsValues.get(fieldId);
    }

    public String getValueAsString(String fieldId) {
        Object value = fieldsValues.get(fieldId);
        return value == null ? "" : value.toString();
    }

    public int getValueAsInt(String fieldId, int defaultValue) {
        Number value = (Number) fieldsValues.get(fieldId);
        return value == null ? defaultValue : value.intValue();
    }

    public boolean getValueAsBoolean(String fieldId, boolean defaultValue) {
        Boolean value = (Boolean) fieldsValues.get(fieldId);
        return value == null ? defaultValue : value.booleanValue();
    }
}
