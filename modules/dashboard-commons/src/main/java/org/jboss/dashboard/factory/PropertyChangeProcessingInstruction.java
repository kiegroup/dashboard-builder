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
package org.jboss.dashboard.factory;

import org.jboss.dashboard.Application;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 */
public abstract class PropertyChangeProcessingInstruction {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PropertyChangeProcessingInstruction.class.getName());
    private String propertyName;
    private String propertyValue;
    private Component component;
    public static final String ARRAYS_DELIMITER = ",";

    protected PropertyChangeProcessingInstruction(Component component, String propertyName, String propertyValue) {
        this.component = component;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public abstract Object getValueAfterChange(Object originalValue, Class expectedClass) throws Exception;


    protected Object getNewInstanceForClass(Class expectedClass) {
        log.debug("Creating 'empty' class of type " + expectedClass);
        Object object = null;
        if (expectedClass.isArray()) {
            Class componentClass = expectedClass.getComponentType();
            if (componentClass.isPrimitive()) {
                if (componentClass.equals(int.class))
                    object = new int[0];
                else if (componentClass.equals(boolean.class))
                    object = new boolean[0];
                else if (componentClass.equals(long.class))
                    object = new long[0];
                else if (componentClass.equals(char.class))
                    object = new char[0];
                else if (componentClass.equals(double.class))
                    object = new double[0];
                else if (componentClass.equals(float.class))
                    object = new float[0];
                else if (componentClass.equals(byte.class))
                    object = new byte[0];
                else if (componentClass.equals(short.class)) object = new short[0];
            } else if (String.class.equals(componentClass)) //Array of Strings
                object = new String[0];
            else if (componentClass.equals(Integer.class))
                object = new Integer[0];
            else if (componentClass.equals(Boolean.class))
                object = new Boolean[0];
            else if (componentClass.equals(Long.class))
                object = new Long[0];
            else if (componentClass.equals(Character.class))
                object = new Character[0];
            else if (componentClass.equals(Double.class))
                object = new Double[0];
            else if (componentClass.equals(Float.class))
                object = new Float[0];
            else if (componentClass.equals(Byte.class))
                object = new Byte[0];
            else if (componentClass.equals(Short.class))
                object = new Short[0];
            else { //It will be an array of components
                object = Array.newInstance(componentClass, 0);
            }
        } else {
            if (String.class.equals(expectedClass))
                object = "";
            else if (expectedClass.equals(int.class))
                object = new Integer(0);
            else if (expectedClass.equals(boolean.class))
                object = new Boolean(false);
            else if (expectedClass.equals(long.class))
                object = new Long(0);
            else if (expectedClass.equals(char.class))
                object = new Character((char) 0);
            else if (expectedClass.equals(double.class))
                object = new Double(0);
            else if (expectedClass.equals(float.class))
                object = new Float(0);
            else if (expectedClass.equals(byte.class))
                object = new Byte((byte) 0);
            else if (expectedClass.equals(short.class))
                object = new Short((short) 0);
            else  if (expectedClass.equals(Integer.class))
                object = new Integer(0);
            else if (expectedClass.equals(Boolean.class))
                object = new Boolean(false);
            else if (expectedClass.equals(Long.class))
                object = new Long(0);
            else if (expectedClass.equals(Character.class))
                object = new Character((char) 0);
            else if (expectedClass.equals(Double.class))
                object = new Double(0);
            else if (expectedClass.equals(Float.class))
                object = new Float(0);
            else if (expectedClass.equals(Byte.class))
                object = new Byte((byte) 0);
            else if (expectedClass.equals(Short.class))
                object = new Short((short) 0);
            else { //It will be a component
                object = null;
            }
        }
        return object;
    }

    protected Object getValueForParameter(String paramValue, Class expectedClass) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Getting value with class " + expectedClass + ", parsing " + paramValue);
        Object object = null;
        if (expectedClass.isArray()) {
            Class componentClass = expectedClass.getComponentType();
            if (componentClass.isArray())
                throw new Exception("Nested array properties are not supported.");
            List objectValue = new ArrayList();
            StringTokenizer stk = new StringTokenizer(paramValue, ARRAYS_DELIMITER, true);
            while (stk.hasMoreTokens()) {
                String token = stk.nextToken();
                int delimiterCount = 0;
                boolean addTrailingToken = true;
                while (ARRAYS_DELIMITER.equals(token)) {
                    addTrailingToken = false;
                    delimiterCount++;
                    if (delimiterCount % 2 == 0) {
                        String lastToken = (String) objectValue.get(objectValue.size() - 1);
                        objectValue.set(objectValue.size() - 1, lastToken + ARRAYS_DELIMITER);
                        if (stk.hasMoreTokens()) {
                            token = stk.nextToken();
                            if (!ARRAYS_DELIMITER.equals(token)) {
                                lastToken = (String) objectValue.get(objectValue.size() - 1);
                                objectValue.remove(objectValue.size() - 1);
                                objectValue.add(lastToken + token);
                            }
                        } else {
                            addTrailingToken = false;
                            break;
                        }
                    } else if (delimiterCount % 2 == 1 && !stk.hasMoreTokens()) {
                        objectValue.add("");
                    } else if (stk.hasMoreTokens()) {
                        token = stk.nextToken();
                        addTrailingToken = true;
                    }
                }
                if (addTrailingToken) {
                    objectValue.add(token);
                }
            }
            for (int i = 0; i < objectValue.size(); i++) {
                String s = (String) objectValue.get(i);
                objectValue.set(i, getValueForParameter(s, componentClass));
            }

            Object baseObjectArray = getBaseArray(componentClass, objectValue.size());
            object = objectValue.toArray((Object[]) baseObjectArray);
        } else {
            if (String.class.equals(expectedClass)) {
                object = paramValue;
            } else if (expectedClass.equals(int.class)) {
                object = new Integer(toInt(paramValue));
            } else if (expectedClass.equals(boolean.class)) {
                object = (toBoolean(paramValue)) ? Boolean.TRUE : Boolean.FALSE;
            } else if (expectedClass.equals(long.class)) {
                object = new Long(toLong(paramValue));
            } else if (expectedClass.equals(char.class)) {
                object = new Character(toChar(paramValue));
            } else if (expectedClass.equals(double.class)) {
                object = new Double(toDouble(paramValue));
            } else if (expectedClass.equals(float.class)) {
                object = new Float(toFloat(paramValue));
            } else if (expectedClass.equals(byte.class)) {
                object = new Byte(toByte(paramValue));
            } else if (expectedClass.equals(short.class)) {
                object = new Short(toShort(paramValue));
            } else if (expectedClass.equals(File.class)) {
                object = toFile(paramValue);
            } else if (expectedClass.equals(Integer.class)) {
                object = new Integer(toInt(paramValue));
            } else if (expectedClass.equals(Boolean.class)) {
                object = (toBoolean(paramValue)) ? Boolean.TRUE : Boolean.FALSE;
            } else if (expectedClass.equals(Long.class)) {
                object = new Long(toLong(paramValue));
            } else if (expectedClass.equals(Character.class)) {
                object = new Character(toChar(paramValue));
            } else if (expectedClass.equals(Double.class)) {
                object = new Double(toDouble(paramValue));
            } else if (expectedClass.equals(Float.class)) {
                object = new Float(toFloat(paramValue));
            } else if (expectedClass.equals(Byte.class)) {
                object = new Byte(toByte(paramValue));
            } else if (expectedClass.equals(Short.class)) {
                object = new Short(toShort(paramValue));
            } else { //It will be a component
                object = toComponent(paramValue);
            }
        }
        return object;
    }

    protected Object getBaseArray(Class expectedClass, int length) {
        if (log.isDebugEnabled())
            log.debug("Creating base array with length " + length + " and class " + expectedClass);
        if (expectedClass.isPrimitive()) {
            if (expectedClass.equals(int.class))
                return new Integer[length];
            else if (expectedClass.equals(boolean.class))
                return new Boolean[length];
            else if (expectedClass.equals(long.class))
                return new Long[length];
            else if (expectedClass.equals(char.class))
                return new Character[length];
            else if (expectedClass.equals(double.class))
                return new Double[length];
            else if (expectedClass.equals(float.class))
                return new Float[length];
            else if (expectedClass.equals(byte.class))
                return new Byte[length];
            else if (expectedClass.equals(short.class))
                return new Short[length];
        }
        return Array.newInstance(expectedClass, length);

    }

    protected int toInt(String parameter) throws Exception {
        return Integer.parseInt(parameter);
    }

    protected boolean toBoolean(String parameter) throws Exception {
        return Boolean.valueOf(parameter).booleanValue();
    }

    protected long toLong(String parameter) throws Exception {
        return Long.parseLong(parameter);
    }

    protected char toChar(String parameter) throws Exception {
        if (parameter.length() != 1)
            throw new Exception("Invalid value for a char " + parameter);
        return parameter.charAt(0);
    }

    protected double toDouble(String parameter) throws Exception {
        return Double.parseDouble(parameter);
    }

    protected float toFloat(String parameter) throws Exception {
        return Float.parseFloat(parameter);
    }

    protected byte toByte(String parameter) throws Exception {
        return Byte.parseByte(parameter);
    }

    protected short toShort(String parameter) throws Exception {
        return Short.parseShort(parameter);
    }

    protected Object toFile(String paramValue) {
        if (paramValue == null || "".equals(paramValue.trim()))
            return null;
        if (paramValue.startsWith("/") || paramValue.indexOf(":") != -1) {
            return new File(paramValue);
        } else {
            return new File(Application.lookup().getBaseAppDirectory() + "/" + paramValue);
        }
    }

    protected Object toComponent(String parameter) throws Exception {
        if (parameter == null || "".equals(parameter))
            return null;

        Object o = null;
        try {
            o = getComponent().getTree().lookup(parameter, getComponent().getName());
        } catch (LookupException e) {
            log.error("Error looking up component: " + parameter + ". Trying to set property for component " + getComponent().getName(), e);
        }

        if (o == null) {
            log.error("Invalid component specified for property " + propertyName + " in " + component.getName() + ": '" + parameter + "'. Lookup is null.");
            if (!parameter.equals(parameter.trim())) {
                log.error("Component name contains blank spaces at the beginning or end. This is probably the cause of previous error(s).");
            }
        }
        return o;
    }

    protected Component getComponent() {
        return component;
    }

}
