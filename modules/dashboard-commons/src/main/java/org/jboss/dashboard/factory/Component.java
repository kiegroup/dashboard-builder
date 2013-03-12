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

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * This class holds all the properties regarding a Factory bean definition.
 * Such definition is usually implemented by means of a .properties file inside the etc/factory directory..
 */
public class Component {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Component.class.getName());
    public static final String ALIAS_PROPERTY = "alias";
    public static final String CLASS_PROPERTY = "class";
    public static final String SCOPE_PROPERTY = "scope";
    public static final String DESC_PROPERTY = "description";
    public static final String ENABLED_PROPERTY = "enabled";
    public static final String SPECIAL_PROPERTY_PREFFIX = "$";

    public static final String SCOPE_GLOBAL = "global";
    public static final String SCOPE_SESSION = "session";
    public static final String SCOPE_PANEL_SESSION = "panelSession";
    public static final String SCOPE_REQUEST = "request";
    public static final String SCOPE_VOLATILE = "volatile";

    public final List VALID_SCOPES;

    public static final int STATUS_INCOMPLETE = 0;
    public static final int STATUS_VALID = 1;
    public static final int STATUS_INVALID = -1;


    private int status = STATUS_INCOMPLETE;
    private String name;
    private ComponentsTree tree;
    private String description;
    private String clazz;
    private String scope;
    private String alias;
    private boolean enabled = true;

    private Class componentClass;
    private Map componentConfiguredProperties = new Hashtable();
    private List propertiesFilesAdded = new ArrayList();
    private long creationOrderNumber;

    public long getCreationOrderNumber() {
        return creationOrderNumber;
    }

    public void setCreationOrderNumber(long creationOrderNumber) {
        this.creationOrderNumber = creationOrderNumber;
    }

    public Component(String name, ComponentsTree tree) {
        this.name = name;
        this.tree = tree;
        VALID_SCOPES = Collections.unmodifiableList(getInitScopes());
    }

    public ComponentsTree getTree() {
        return tree;
    }

    protected List getInitScopes() {
        List l = new ArrayList();
        l.add(SCOPE_GLOBAL);
        l.add(SCOPE_SESSION);
        l.add(SCOPE_PANEL_SESSION);
        l.add(SCOPE_REQUEST);
        l.add(SCOPE_VOLATILE);
        return l;
    }

    public Map getComponentConfiguredProperties() {
        return componentConfiguredProperties;
    }

    void addProperties(Properties properties, String fileName) {
        String declaredClass = properties.getProperty(SPECIAL_PROPERTY_PREFFIX + CLASS_PROPERTY);
        String declaredScope = properties.getProperty(SPECIAL_PROPERTY_PREFFIX + SCOPE_PROPERTY);
        String declaredDescr = properties.getProperty(SPECIAL_PROPERTY_PREFFIX + DESC_PROPERTY);
        String declaredEnabled = properties.getProperty(SPECIAL_PROPERTY_PREFFIX + ENABLED_PROPERTY);
        String declaredAlias = properties.getProperty(SPECIAL_PROPERTY_PREFFIX + ALIAS_PROPERTY);
        if (declaredClass != null && clazz != null) {
            try {

                Class currentClass = Class.forName(clazz);
                Class newClass = Class.forName(declaredClass);
                if (!currentClass.isAssignableFrom(newClass)) {
                    log.warn("Assigning class " + declaredClass + " to component " + name + " may cause errors. " +
                            "Default system class is " + clazz + ", new class is not descendant.");
                }
            } catch (ClassNotFoundException e) {
                log.error("Error:", e);
            }
        }
        clazz = declaredClass == null ? clazz : declaredClass;
        alias = declaredAlias == null ? alias : declaredAlias;
        scope = declaredScope == null ? scope : declaredScope;
        description = declaredDescr == null ? description : declaredDescr;
        enabled = declaredEnabled == null ? enabled : Boolean.valueOf(declaredEnabled).booleanValue();
        for (Enumeration en = properties.propertyNames(); en.hasMoreElements();) {
            String propertyName = (String) en.nextElement();
            if (propertyName.startsWith(SPECIAL_PROPERTY_PREFFIX)) {
                continue;
            }
            if (!isValidPropertyName(propertyName)) {
                log.error("Ignoring invalid property name " + propertyName + " in component " + name);
                continue;
            }
            String propertyValue = properties.getProperty(propertyName);
            storeProperty(propertyName, propertyValue);
        }
        propertiesFilesAdded.add(fileName);
    }

    /**
     * Determine if the property name is a valid property identifier
     *
     * @param propertyName Property name to evaluate
     * @return true  if the property name is a valid property identifier
     */
    protected boolean isValidPropertyName(String propertyName) {
        if (propertyName == null || propertyName.length() == 0)
            return false;
        if (propertyName.endsWith("-") || propertyName.endsWith("+"))
            propertyName = propertyName.substring(0, propertyName.length() - 1);
        if (propertyName.length() == 0)
            return false;
        for (int i = 0; i < propertyName.length(); i++) {
            char c = propertyName.charAt(i);
            if ('.' == c)
                break; //After this, it is a JXPath
            if (!Character.isJavaIdentifierPart(c))
                return false;
        }
        return true;
    }

    /**
     * Store in componentProperties the property given by name and value.
     *
     * @param propertyName
     * @param propertyValue
     */
    protected void storeProperty(String propertyName, String propertyValue) {
        List currentValue = null;
        PropertyChangeProcessingInstruction instruction = null;
        if (propertyName.endsWith("+")) {
            propertyName = propertyName.substring(0, propertyName.length() - 1).trim();
            instruction = new PropertyAddProcessingInstruction(this, propertyName, propertyValue);
        } else if (propertyName.endsWith("-")) {
            propertyName = propertyName.substring(0, propertyName.length() - 1).trim();
            instruction = new PropertySubstractProcessingInstruction(this, propertyName, propertyValue);
        } else {
            instruction = new PropertySetProcessingInstruction(this, propertyName, propertyValue);
        }

        currentValue = (List) componentConfiguredProperties.get(propertyName);
        if (currentValue == null)
            currentValue = new ArrayList();
        currentValue.add(instruction);
        componentConfiguredProperties.put(propertyName, currentValue);
    }

    /**
     * Writes a property value to the component
     *
     * @param propertyName
     * @param propertyValues
     */
    public void setProperty(String propertyName, String[] propertyValues) throws Exception {
        StringBuffer propertyValue = new StringBuffer();
        for (int i = 0; i < propertyValues.length; i++) {
            String value = propertyValues[i];
            if (i != 0) propertyValue.append(",");
            if (propertyValues.length > 1)
                propertyValue.append(StringUtils.replace(value, ",", ",,"));
            else
                propertyValue.append(value);
        }
        ArrayList list = new ArrayList();
        list.add(new PropertySetProcessingInstruction(this, propertyName, propertyValue.toString()));
        setObjectProperty(getObject(), propertyName, list, null);
    }

    public void setProperty(String propertyName, File propertyValue) throws Exception {
        Object obj = getObject();
        //Find a Field
        Field field = getField(obj, propertyName);
        if (field != null) {
            Class fieldClass = field.getType();
            if (File.class.equals(fieldClass)) {
                if (log.isDebugEnabled())
                    log.debug("Invoking field " + propertyName + " with file.");
                field.set(obj, propertyValue);
            } else if (byte[].class.equals(fieldClass)) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(propertyValue));
                int byteRead = -1;
                while ((byteRead = bis.read()) != -1) {
                    bos.write(byteRead);
                }
                bis.close();
                bos.close();

                if (log.isDebugEnabled())
                    log.debug("Invoking field " + propertyName + " with byte[].");
                field.set(obj, bos.toByteArray());
            } else {
                log.error("Cannot write a file to property " + propertyName + " in bean " + name);
            }
            return;
        }
        // Find a getter and setter.
        Method getter = getGetter(obj, propertyName);
        if (getter == null) {
            log.error("Cannot find a getter for property " + propertyName + " in class " + clazz + ". Ignoring property.");
            return;
        }
        Method setter = getSetter(obj, getter, propertyName);
        if (setter == null) {
            log.error("Cannot find a setter for property " + propertyName + " in class " + clazz + ". Ignoring property.");
            return;
        }
        Class returnType = getter.getReturnType();
        if (File.class.equals(returnType)) {
            if (log.isDebugEnabled())
                log.debug("Invoking " + setter + " with file.");
            setter.invoke(obj, new Object[]{propertyValue});
        } else if (byte[].class.equals(returnType)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(propertyValue));
            int byteRead = -1;
            while ((byteRead = bis.read()) != -1) {
                bos.write(byteRead);
            }
            bis.close();
            bos.close();

            if (log.isDebugEnabled())
                log.debug("Invoking " + setter + " with byte[].");
            setter.invoke(obj, new Object[]{bos.toByteArray()});
        } else {
            log.error("Cannot write a file to property " + propertyName + " in bean " + name);
        }
    }


    public String getName() {
        return name;
    }

    public String getClazz() {
        return clazz;
    }

    public String getScope() {
        return scope;
    }

    public String getAlias() {
        return alias;
    }

    public String getDescription() {
        return description;
    }

    public List getPropertiesFilesAdded() {
        return Collections.unmodifiableList(propertiesFilesAdded);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(scope).append(" Component named ").append(name).append(" with class ").append(clazz).append(" and params " + componentConfiguredProperties);
        return sb.toString();
    }

    /**
     * Update the status attribute to reflect the component status
     */
    public void validate() {
        if (!VALID_SCOPES.contains(scope)) {
            log.error("Invalid scope " + scope + " for component " + name + "");
            status = STATUS_INVALID;
            return;
        }
        try {
            componentClass = Class.forName(clazz);
        } catch (Throwable e) {
            log.error("Invalid class for component " + name + ": " + clazz, e);
            status = STATUS_INVALID;
            return;
        }
        status = STATUS_VALID;
    }

    public int getStatus() {
        return status;
    }

    /**
     * @return The object represented in this component.
     */
    public Object getObject() throws LookupException {
        if (!enabled)
            return null;
        if (log.isDebugEnabled())
            log.debug("Getting " + scope + " component " + name);
        if (status != STATUS_INVALID) {
            LookupHelper helper = ComponentsContextManager.getLookupHelper();
            synchronized (helper.getSynchronizationObject(getScope())) {
                if (getTheInstance() == null) {
                    return makeAndSetNewInstance();
                }
            }
            return getTheInstance();
        } else {
            log.warn("Component " + name + " is in invalid status.");
        }
        return null;
    }

    protected final Object getTheInstance() throws LookupException {
        LookupHelper helper = ComponentsContextManager.getLookupHelper();
        if (helper != null) {
            return helper.lookupObject(scope, name);
        } else {
            throw new LookupException("Cannot get " + getName() + ". Factory operation is outside a valid context.");
        }
    }

    protected final void setTheInstance(Object instance) {
        LookupHelper helper = ComponentsContextManager.getLookupHelper();
        if (helper != null) {
            helper.storeObject(scope, name, instance);
        } else {
            log.error("Cannot set " + getName() + ". Factory operation is outside a valid context.");
        }
    }

    protected Object makeAndSetNewInstance() {
        LookupHelper helper = ComponentsContextManager.getLookupHelper();
        synchronized (helper.getSynchronizationObject(getScope())) {
            Object object = null;
            if (status == STATUS_VALID) {
                if (log.isDebugEnabled())
                    log.debug("Making and initializing new " + clazz);

                if (scope.equals(SCOPE_GLOBAL)) {
                    try {
                        object = componentClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
                    } catch (Exception e) {
                        if (log.isDebugEnabled()) log.debug("Error using getInstance() method on " + clazz);
                    }
                }

                if (object == null)
                    try {
                        object = componentClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                    } catch (Exception e) {
                        log.error("Error creating instance for component " + name + " :", e);
                    }
                //End. Object should be created now
                if (object == null) {
                    status = STATUS_INVALID;
                    log.error("Make sure the component class has a default public constructor" +
                            (scope.equals(SCOPE_GLOBAL) ? ", or a static getInstance() method." : "."));
                } else {
                    if (object instanceof FactoryLifecycle) {
                        try {
                            if (object instanceof BasicFactoryElement) {
                                ((BasicFactoryElement) object).setComponentName(name);
                                ((BasicFactoryElement) object).setComponentScope(scope);
                                ((BasicFactoryElement) object).setComponentDescription(description);
                                ((BasicFactoryElement) object).setComponentAlias(alias);
                            }
                            ((FactoryLifecycle) object).init();
                            ((FactoryLifecycle) object).stop();
                        } catch (Exception e) {
                            log.error("Error in component lifecycle ", e);
                        }
                    }
                    setTheInstance(object);
                    JXPathContext ctx = JXPathContext.newContext(object);
                    for (Iterator it = componentConfiguredProperties.keySet().iterator(); it.hasNext();) {
                        String propertyName = (String) it.next();
                        List propertyValue = (List) componentConfiguredProperties.get(propertyName);
                        try {
                            setObjectProperty(object, propertyName, propertyValue, ctx);
                        } catch (Exception e) {
                            log.error("Error. Cannot set property " + getName() + "." + propertyName + " with configured values " + propertyValue, e);
                        }
                    }
                    status = STATUS_VALID;
                    if (object instanceof FactoryLifecycle) {
                        try {
                            ((FactoryLifecycle) object).start();
                        } catch (Exception e) {
                            log.error("Error in component lifecycle ", e);
                        }
                    }
                    setCreationOrderNumber(getTree().getNewOrderCounter());
                }
            } else {
                log.error("Infinite loop detected. Caused by component " + name + " or some other component used by it.");
            }
            return object;
        }
    }

    protected Object setObjectProperty(Object obj, String propertyName, List propertyValue, JXPathContext ctx) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Setting in " + clazz + " property " + propertyName + " with values " + propertyValue);
        if (obj instanceof Map) {
            ((Map) obj).put(propertyName, getValueForProperty(propertyValue, String.class)); //Map of Strings
        } else if (obj instanceof List) {
            try {
                int index = Integer.parseInt(propertyName);
                while (((List) obj).size() <= index) ((List) obj).add(null);
                ((List) obj).set(index, getValueForProperty(propertyValue, String.class)); //List of Strings
            } catch (Exception e) {
                log.error("Error setting position " + propertyName + " in List " + name + ". ", e);
            }
        } else if (propertyName.indexOf('.') != -1) { //Set it by JXPath, valid only for strings
            ctx = ctx != null ? ctx : JXPathContext.newContext(obj);
            ctx.setValue(propertyName.replace('.', '/'), getValueForProperty(propertyValue, String.class));
        } else {
            //Find a Field
            Field field = getField(obj, propertyName);
            if (field != null) {
                Class fieldClass = field.getType();
                Object value = getValueForProperty(propertyValue, fieldClass);
                field.set(obj, value);
                return obj;
            }

            // Find a getter and setter.
            Method getter = getGetter(obj, propertyName);
            if (getter == null) {
                log.error("Cannot find a getter for property " + propertyName + " in class " + clazz + ". Ignoring property.");
                return obj;
            }
            Method setter = getSetter(obj, getter, propertyName);
            if (setter == null) {
                log.error("Cannot find a setter for property " + propertyName + " in class " + clazz + ". Ignoring property.");
                return obj;
            }
            Object value = getValueForProperty(propertyValue, getter.getReturnType());
            if (log.isDebugEnabled())
                log.debug("Invoking " + setter + " with value=" + value);
            setter.invoke(obj, new Object[]{value});
        }
        return obj;
    }

    protected Field getField(Object obj, String propertyName) {
        try {
            Field field = obj.getClass().getField(propertyName);
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && !Modifier.isFinal(field.getModifiers())) {
                return field;
            }
        } catch (NoSuchFieldException e) {
        }
        if (log.isDebugEnabled()) {
            log.debug("Could not find a field for property " + propertyName + " in " + obj.getClass());
        }
        return null;
    }

    protected Method getGetter(Object obj, String propertyName) {
        Method getter = null;
        String propertyAccessorSuffix = StringUtils.capitalize(propertyName);
        String getterName = "get" + propertyAccessorSuffix;
        try {
            getter = obj.getClass().getMethod(getterName, new Class[0]);
        } catch (NoSuchMethodException e) {
            log.debug("No getter " + getterName + " found.");
            String booleanGetterName = "is" + propertyAccessorSuffix;
            try {
                getter = obj.getClass().getMethod(booleanGetterName, new Class[0]);
            } catch (NoSuchMethodException e1) {
                log.debug("No getter " + booleanGetterName + " found.");
            }
        }
        return getter;
    }

    protected Method getSetter(Object obj, Method getter, String propertyName) {
        Method setter = null;
        String propertyAccessorSuffix = StringUtils.capitalize(propertyName);
        String setterName = "set" + propertyAccessorSuffix;
        Class returnType = getter.getReturnType();
        try {
            setter = obj.getClass().getMethod(setterName, new Class[]{returnType});
        } catch (NoSuchMethodException e) {
            log.error("Cannot find a setter for property " + propertyName + " in class " + clazz + ". Ignoring property.");
        }
        return setter;
    }

    protected Object getValueForProperty(List values, Class expectedClass) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Converting to " + expectedClass + " values " + values);
        if (values == null || values.isEmpty())
            return null;
        Object valueToReturn = null;
        for (int i = 0; i < values.size(); i++) {
            PropertyChangeProcessingInstruction instruction = (PropertyChangeProcessingInstruction) values.get(i);
            valueToReturn = instruction.getValueAfterChange(valueToReturn, expectedClass);
        }
        if (expectedClass.isArray() && expectedClass.getComponentType().isPrimitive() && valueToReturn != null) {
            //Convert arrays of Integers, Shorts, ... to primitive type arrays.
            if (expectedClass.getComponentType().equals(int.class)) {
                int[] newValueToReturn = new int[((Object[]) valueToReturn).length];
                for (int i = 0; i < newValueToReturn.length; newValueToReturn[i] = ((Integer) ((Object[]) valueToReturn)[i++]).intValue())
                    ;
                return newValueToReturn;
            } else if (expectedClass.getComponentType().equals(boolean.class)) {
                boolean[] newValueToReturn = new boolean[((Object[]) valueToReturn).length];
                for (int i = 0; i < newValueToReturn.length; newValueToReturn[i] = ((Boolean) ((Object[]) valueToReturn)[i++]).booleanValue())
                    ;
                return newValueToReturn;
            } else if (expectedClass.getComponentType().equals(long.class)) {
                long[] newValueToReturn = new long[((Object[]) valueToReturn).length];
                for (int i = 0; i < newValueToReturn.length; newValueToReturn[i] = ((Long) ((Object[]) valueToReturn)[i++]).longValue())
                    ;
                return newValueToReturn;
            } else if (expectedClass.getComponentType().equals(char.class)) {
                char[] newValueToReturn = new char[((Object[]) valueToReturn).length];
                for (int i = 0; i < newValueToReturn.length; newValueToReturn[i] = ((Character) ((Object[]) valueToReturn)[i++]).charValue())
                    ;
                return newValueToReturn;
            } else if (expectedClass.getComponentType().equals(double.class)) {
                double[] newValueToReturn = new double[((Object[]) valueToReturn).length];
                for (int i = 0; i < newValueToReturn.length; newValueToReturn[i] = ((Double) ((Object[]) valueToReturn)[i++]).doubleValue())
                    ;
                return newValueToReturn;
            } else if (expectedClass.getComponentType().equals(float.class)) {
                float[] newValueToReturn = new float[((Object[]) valueToReturn).length];
                for (int i = 0; i < newValueToReturn.length; newValueToReturn[i] = ((Float) ((Object[]) valueToReturn)[i++]).floatValue())
                    ;
                return newValueToReturn;
            } else if (expectedClass.getComponentType().equals(byte.class)) {
                byte[] newValueToReturn = new byte[((Object[]) valueToReturn).length];
                for (int i = 0; i < newValueToReturn.length; newValueToReturn[i] = ((Byte) ((Object[]) valueToReturn)[i++]).byteValue())
                    ;
                return newValueToReturn;
            } else if (expectedClass.getComponentType().equals(short.class)) {
                short[] newValueToReturn = new short[((Object[]) valueToReturn).length];
                for (int i = 0; i < newValueToReturn.length; newValueToReturn[i] = ((Short) ((Object[]) valueToReturn)[i++]).shortValue())
                    ;
                return newValueToReturn;
            }
        }
        return valueToReturn;
    }
}
