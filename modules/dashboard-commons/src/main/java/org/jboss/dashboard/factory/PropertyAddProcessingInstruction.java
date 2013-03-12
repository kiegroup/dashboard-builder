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

/**
 *
 */
public class PropertyAddProcessingInstruction extends PropertyChangeProcessingInstruction {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PropertyAddProcessingInstruction.class.getName());

    public PropertyAddProcessingInstruction(Component component, String propertyName, String propertyValue) {
        super(component, propertyName, propertyValue);
    }


    public Object getValueAfterChange(Object originalValue, Class expectedClass) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Processing instruction " + this + " on value " + originalValue + " (" + expectedClass + ")");
        Object valueToAdd = null;
        Object currentValue = originalValue;
        currentValue = currentValue == null ? getNewInstanceForClass(expectedClass) : currentValue;
        valueToAdd = getValueForParameter(getPropertyValue(), expectedClass);
        if (valueToAdd != null) {
            if (expectedClass.isArray()) {
                Object newValue = getBaseArray(expectedClass.getComponentType(), ((Object[]) currentValue).length + ((Object[]) valueToAdd).length);
                System.arraycopy(currentValue, 0, newValue, 0, ((Object[]) currentValue).length);
                System.arraycopy(valueToAdd, 0, newValue, ((Object[]) currentValue).length, ((Object[]) valueToAdd).length);
                return newValue;
            } else if (String.class.equals(expectedClass)) {
                return (String) currentValue + (String) valueToAdd;
            } else if (expectedClass.equals(int.class)) {
                return new Integer(((Integer) currentValue).intValue() + ((Integer) valueToAdd).intValue());
            } else if (expectedClass.equals(long.class)) {
                return new Long(((Long) currentValue).longValue() + ((Long) valueToAdd).longValue());
            } else if (expectedClass.equals(double.class)) {
                return new Double(((Double) currentValue).doubleValue() + ((Double) valueToAdd).doubleValue());
            } else if (expectedClass.equals(float.class)) {
                return new Float(((Float) currentValue).floatValue() + ((Float) valueToAdd).floatValue());
            } else if (expectedClass.equals(byte.class)) {
                return new Byte((byte) (((Byte) currentValue).byteValue() + ((Byte) valueToAdd).byteValue()));
            } else if (expectedClass.equals(short.class)) {
                return new Short((short) (((Short) currentValue).shortValue() + ((Short) valueToAdd).shortValue()));
            } else if (expectedClass.equals(Integer.class)) {
                return new Integer(((Integer) currentValue).intValue() + ((Integer) valueToAdd).intValue());
            } else if (expectedClass.equals(Long.class)) {
                return new Long(((Long) currentValue).longValue() + ((Long) valueToAdd).longValue());
            } else if (expectedClass.equals(Double.class)) {
                return new Double(((Double) currentValue).doubleValue() + ((Double) valueToAdd).doubleValue());
            } else if (expectedClass.equals(Float.class)) {
                return new Float(((Float) currentValue).floatValue() + ((Float) valueToAdd).floatValue());
            } else if (expectedClass.equals(Byte.class)) {
                return new Byte((byte) (((Byte) currentValue).byteValue() + ((Byte) valueToAdd).byteValue()));
            } else if (expectedClass.equals(Short.class)) {
                return new Short((short) (((Short) currentValue).shortValue() + ((Short) valueToAdd).shortValue()));
            } else {
                log.error("Addition not supported for class " + expectedClass + ". Ignoring property " + getPropertyName() + "+=" + getPropertyValue());
            }
        }
        return originalValue;
    }

    public String toString() {
        return "do{" + getPropertyName() + "+=" + getPropertyValue() + "}";
    }
}
