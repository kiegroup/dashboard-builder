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
package org.jboss.dashboard.ui.utils.forms;

import org.jboss.dashboard.ui.controller.CommandRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods to do a very simple form validation, and update a given FormStatus object.
 * <p/>
 * TODO: Add additional methods as needed.
 * TODO: Refactor this
 */
public class SimpleFormHandler {

    /**
     * Form Status to keep updated
     */
    private FormStatus formStatus = null;

    public SimpleFormHandler(FormStatus formStatus) {
        super();
        this.formStatus = formStatus;
    }

    public String getValueAsString(String fieldId) {
        Object value = formStatus.getValue(fieldId);
        if (value == null)
            return null;

        return value.toString();
    }

    public Integer getValueAsInteger(String fieldId) {
        Object value = formStatus.getValue(fieldId);
        if (value == null)
            return null;

        return (Integer) value;
    }

    public int getValueAsInteger(String fieldId, int defaultValue) {
        Integer v = getValueAsInteger(fieldId);
        return v == null ? defaultValue : v.intValue();
    }

    public Long getValueAsLong(String fieldId) {
        Object value = formStatus.getValue(fieldId);
        if (value == null)
            return null;

        return (Long) value;
    }

    public Boolean getValueAsBoolean(String fieldId) {
        Object value = formStatus.getValue(fieldId);
        if (value == null)
            return null;

        return (Boolean) value;
    }

    public boolean getValueAsBoolean(String fieldId, boolean defaultValue) {
        Boolean v = getValueAsBoolean(fieldId);
        return v == null ? defaultValue : v.booleanValue();
    }

    /*
     * Some useful validator methods
     */

    /**
     * Validates a string. Returns the retrieved string.
     */
    public String validateString(CommandRequest request, String fieldId, boolean mandatory) {
        String value = request.getParameter(fieldId);
        if (value == null || "".equals(value)) {
            value = null;
        } else {
            value = value.trim();
        }

        if (mandatory && value == null) {
            formStatus.addWrongField(fieldId);
            formStatus.addError(fieldId);
            formStatus.removeValue(fieldId);
        } else {
            formStatus.removeWrongField(fieldId);
            formStatus.removeError(fieldId);
            formStatus.setValue(fieldId, value);
        }

        return value;
    }

    /**
     * Validates a string. Returns the retrieved string.
     */
    public Map validateI18NString(CommandRequest request, String fieldId, boolean mandatory, String language, Map currentValues) {
        String value = request.getParameter(fieldId);
        if (value == null || "".equals(value)) {
            value = null;
        } else {
            value = value.trim();
        }

        if (currentValues == null)
            currentValues = new HashMap();

        if (mandatory && value == null ||
                language == null || language.trim().length() == 0) {
            formStatus.addWrongField(fieldId);
            formStatus.addError(fieldId);
            formStatus.removeValue(fieldId);
        } else {
            //if (!currentValues.containsValue(value))
            currentValues.put(language, value);
            formStatus.removeWrongField(fieldId);
            formStatus.removeError(fieldId);
            formStatus.setValue(fieldId, currentValues);
        }

        return currentValues;
    }


    /**
     * Validates a pair of string parameters as a password. They must be named
     */
    public String validatePassword(CommandRequest request, String fieldId, int minLength, int maxLength, boolean mandatory) {
        String value1 = validateString(request, fieldId, mandatory);
        String value2 = validateString(request, fieldId + "_confirm", mandatory);

        boolean success = false;

        if (!mandatory && value1 == null && value2 == null) {
            success = true;
        } else if (value1 == null || value2 == null) {
            success = false;
        } else {
            success = value1.equals(value2);
            if (success) {
                success = (value1.length() >= minLength) && (value1.length() <= maxLength);
            }
        }

        if (success) {
            formStatus.removeWrongField(fieldId);
            formStatus.removeError(fieldId);
            formStatus.setValue(fieldId, value1);
            return value1;
        } else {
            formStatus.addWrongField(fieldId);
            formStatus.addError(fieldId);
            formStatus.removeValue(fieldId);
            formStatus.removeValue(fieldId + "_confirm");
            return null;
        }
    }

    /**
     * Validates a string as an identifier name (only digits and characters are allowed)
     */
    public String validateId(CommandRequest request, String fieldId) {
        if (validateString(request, fieldId, true) != null) {
            // Only alphanumeric characters are allowed
            String value = getValueAsString(fieldId);
            if (value != null) {
                for (int i = 0; i < value.length(); i++) {
                    if (!(Character.isLetterOrDigit(value.charAt(i)) || value.charAt(i) == '_') ||
                            Character.isSpaceChar(value.charAt(i))) {
                        formStatus.addWrongField(fieldId);
                        formStatus.addError(fieldId);
                        return null;
                    }
                }

                return value;
            }
        }

        return null;
    }


    /**
     * Validates a submitted value as an integer.
     */
    public Integer validateInteger(CommandRequest request, String fieldId, boolean mandatory, int minValue, int maxValue) {
        String value = request.getParameter(fieldId);
        if (value == null) {
            value = "";
        } else {
            value = value.trim();
        }

        if (value.equals("")) {
            if (mandatory) {
                formStatus.addWrongField(fieldId);
                formStatus.addError(fieldId);
                formStatus.removeValue(fieldId);
            } else {
                formStatus.removeValue(fieldId);
            }

            return null;
        } else {
            // Try parsing number
            Integer number = null;
            try {
                int n = Integer.parseInt(value);
                if (n >= minValue && n <= maxValue)
                    number = new Integer(n);
            } catch (Exception e) {
                // Non validated
            }

            if (number == null) {
                formStatus.addWrongField(fieldId);
                formStatus.addError(fieldId);
                formStatus.removeValue(fieldId);
                return null;
            } else {
                formStatus.removeWrongField(fieldId);
                formStatus.removeError(fieldId);
                formStatus.setValue(fieldId, number);
                return number;
            }
        }
    }

    public Integer validateInteger(CommandRequest request, String fieldId, boolean mandatory) {
        return validateInteger(request, fieldId, mandatory, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }


    /**
     * Validates a submitted value as a Long
     */
    public Long validateLong(CommandRequest request, String fieldId, boolean mandatory) {
        return validateLong(request, fieldId, mandatory, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Validates a submitted value as a Long
     */
    public Long validateLong(CommandRequest request, String fieldId, boolean mandatory, long minValue, long maxValue) {
        String value = request.getParameter(fieldId);
        if (value == null) {
            value = "";
        } else {
            value = value.trim();
        }

        if (value.equals("")) {
            if (mandatory) {
                formStatus.addWrongField(fieldId);
                formStatus.addError(fieldId);
                formStatus.removeValue(fieldId);
            } else {
                formStatus.removeValue(fieldId);
            }

            return null;
        } else {
            // Try parsing number
            Long number = null;
            try {
                long n = Long.parseLong(value);
                if (n >= minValue && n <= maxValue)
                    number = new Long(n);
            } catch (Exception e) {
                // Non validated
            }

            if (number == null) {
                formStatus.addWrongField(fieldId);
                formStatus.addError(fieldId);
                formStatus.removeValue(fieldId);
                return null;
            } else {
                formStatus.removeWrongField(fieldId);
                formStatus.removeError(fieldId);
                formStatus.setValue(fieldId, number);
                return number;
            }
        }
    }

    /**
     * Validates a submitted value as an double.
     */
    public Double validateDouble(CommandRequest request, String fieldId, boolean mandatory, double minValue, double maxValue) {
        String value = request.getParameter(fieldId);
        if (value == null) {
            value = "";
        } else {
            value = value.trim();
        }

        if (value.equals("")) {
            if (mandatory) {
                formStatus.addWrongField(fieldId);
                formStatus.addError(fieldId);
                formStatus.removeValue(fieldId);
            } else {
                formStatus.removeValue(fieldId);
            }

            return null;
        } else {
            // Try parsing number
            Double number = null;
            try {
                double n = Double.parseDouble(value);
                if (n >= minValue && n <= maxValue)
                    number = new Double(n);
            } catch (Exception e) {
                // Non validated
            }

            if (number == null) {
                formStatus.addWrongField(fieldId);
                formStatus.addError(fieldId);
                formStatus.removeValue(fieldId);
                return null;
            } else {
                formStatus.removeWrongField(fieldId);
                formStatus.removeError(fieldId);
                formStatus.setValue(fieldId, number);
                return number;
            }
        }
    }

    public Double validateDouble(CommandRequest request, String fieldId, boolean mandatory) {
        return validateDouble(request, fieldId, mandatory, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Validates a submitted value as a boolean
     */
    public Boolean validateBoolean(CommandRequest request, String fieldId, boolean mandatory) {
        String param = request.getParameter(fieldId);
        boolean value = false;
        if (param == null || param.equals("")) {
            value = false;
        } else {
            value = true;
        }

        formStatus.removeWrongField(fieldId);
        formStatus.removeError(fieldId);
        formStatus.setValue(fieldId, value);
        return value;
    }

    public Date validateDate(CommandRequest req, String fieldId, boolean mandatory, boolean validateMinutes) {
        String d = req.getParameter(fieldId + "_day");
        String m = req.getParameter(fieldId + "_month");
        String y = req.getParameter(fieldId + "_year");

        if (isEmpty(d) || isEmpty(m) || isEmpty(y))
            return null;

        if (validateMinutes) {
            String hour = req.getParameter(fieldId + "_hour");
            String min = req.getParameter(fieldId + "_min");
            if (isEmpty(hour) || isEmpty(min)) {
                hour = "00";
                min = "00";
            }

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            try {
                Date date = df.parse(d + "/" + m + "/" + y + " " + hour + ":" + min);
                return date;
            } catch (ParseException e) {
                return null;
            }

        } else {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = df.parse(d + "/" + m + "/" + y);
                return date;
            } catch (ParseException e) {
                return null;
            }
        }
    }

    public FormStatus getFormStatus() {
        return formStatus;
    }

    private boolean isEmpty(String d) {
        return d == null || d.equals("");
    }
}
