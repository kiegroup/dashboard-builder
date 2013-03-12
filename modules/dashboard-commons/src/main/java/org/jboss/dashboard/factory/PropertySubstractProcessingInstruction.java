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

import org.jboss.dashboard.commons.text.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class PropertySubstractProcessingInstruction extends PropertyChangeProcessingInstruction {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PropertySubstractProcessingInstruction.class.getName());

    public PropertySubstractProcessingInstruction(Component component, String propertyName, String propertyValue) {
        super(component, propertyName, propertyValue);
    }

    public Object getValueAfterChange(Object originalValue, Class expectedClass) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Processing instruction " + this + " on value " + originalValue + " (" + expectedClass + ")");
        Object valueToSubstract = null;
        Object currentValue = originalValue;
        currentValue = currentValue == null ? getNewInstanceForClass(expectedClass) : currentValue;
        valueToSubstract = getValueForParameter(getPropertyValue(), expectedClass);
        if (valueToSubstract != null) {
            if (expectedClass.isArray()) {
                if (((Object[]) valueToSubstract).length == 0) {
                    return null; //Special case. Using stringProperty-=, sets it to null.
                } else { //Otherwise, occurrences of valueToSubstract are removed from the array
                    List list = new ArrayList();
                    list.addAll(Arrays.asList((Object[]) currentValue));
                    list.removeAll(Arrays.asList((Object[]) valueToSubstract));
                    return list.toArray((Object[]) getBaseArray(expectedClass.getComponentType(), list.size()));
                }
            } else if (expectedClass.equals(int.class)) {
                return new Integer(((Integer) currentValue).intValue() - ((Integer) valueToSubstract).intValue());
            } else if (expectedClass.equals(long.class)) {
                return new Long(((Long) currentValue).longValue() - ((Long) valueToSubstract).longValue());
            } else if (expectedClass.equals(double.class)) {
                return new Double(((Double) currentValue).doubleValue() - ((Double) valueToSubstract).doubleValue());
            } else if (expectedClass.equals(float.class)) {
                return new Float(((Float) currentValue).floatValue() - ((Float) valueToSubstract).floatValue());
            } else if (expectedClass.equals(byte.class)) {
                return new Byte((byte) (((Byte) currentValue).byteValue() - ((Byte) valueToSubstract).byteValue()));
            } else if (expectedClass.equals(short.class)) {
                return new Short((short) (((Short) currentValue).shortValue() - ((Short) valueToSubstract).shortValue()));
            } else if (expectedClass.equals(Integer.class)) {
                return new Integer(((Integer) currentValue).intValue() - ((Integer) valueToSubstract).intValue());
            } else if (expectedClass.equals(Long.class)) {
                return new Long(((Long) currentValue).longValue() - ((Long) valueToSubstract).longValue());
            } else if (expectedClass.equals(Double.class)) {
                return new Double(((Double) currentValue).doubleValue() - ((Double) valueToSubstract).doubleValue());
            } else if (expectedClass.equals(Float.class)) {
                return new Float(((Float) currentValue).floatValue() - ((Float) valueToSubstract).floatValue());
            } else if (expectedClass.equals(Byte.class)) {
                return new Byte((byte) (((Byte) currentValue).byteValue() - ((Byte) valueToSubstract).byteValue()));
            } else if (expectedClass.equals(Short.class)) {
                return new Short((short) (((Short) currentValue).shortValue() - ((Short) valueToSubstract).shortValue()));
            } else if (expectedClass.equals(String.class)) {
                if ("".equals(valueToSubstract)) {
                    return null; //Special case. Using stringProperty-=, sets it to null.
                }
                if (((String) currentValue).indexOf((String) valueToSubstract) != -1) {
                    currentValue = StringUtil.replaceAll((String) currentValue, (String) valueToSubstract, "");
                } //Otherwise, occurrences of valueToSubstract are removed from the String.
            } else {
                log.error("Addition not supported for class " + expectedClass + ". Ignoring property " + getPropertyName() + "+=" + getPropertyValue());
            }
        }
        return originalValue;
    }

    public String toString() {
        return "do{" + getPropertyName() + "-=" + getPropertyValue() + "}";
    }
}
