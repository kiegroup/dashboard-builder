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
package org.jboss.dashboard.commons.misc;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.Application;
import org.jboss.dashboard.commons.text.StringUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ReflectionUtils {

    private static final String PREFIX_GET = "get";
    private static final String PREFIX_SET = "set";
    private static final String PREFIX_IS = "is";
    public static final String ARRAYS_DELIMITER = ",";


    public static Object getPrivateField(Object o, String fieldName) {
        Field fields[] = o.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if (fieldName.equals(fields[i].getName())) {
                try {
                    fields[i].setAccessible(true);
                    return fields[i].get(o);
                }
                catch (IllegalAccessException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    public static Object invokeMethod(Object o, String methodName, Object[] params) {
        Method methods[] = o.getClass().getMethods();
        for (int i = 0; i < methods.length; ++i) {
            if (methodName.equals(methods[i].getName())) {
                try {
                    methods[i].setAccessible(true);
                    return methods[i].invoke(o, params);
                }
                catch (IllegalAccessException ex) {
                    return null;
                }
                catch (InvocationTargetException ite) {
                    return null;
                }
            }
        }
        return null;
    }

    public static List<Field> getClassHierarchyFields(Class clazz, Class fieldType, boolean isStatic, String[] fieldsToIgnore) {
        List<Field> results = new ArrayList<Field>();
        if (clazz == null) return results;
        if (clazz.equals(java.lang.Object.class)) return results;

        List<Field> superClassFields = getClassHierarchyFields(clazz.getSuperclass(), fieldType, isStatic, fieldsToIgnore);
        results.addAll(superClassFields);

        List<Field> classFields = getClassFields(clazz, fieldType, isStatic, fieldsToIgnore);
        results.addAll(classFields);
        return results;
    }

    public static List<Field> getClassFields(Class clazz, Class type, boolean isStatic, String[] fieldsToIgnore) {
        List<Field> results = new ArrayList<Field>();
        if (clazz == null) return results;
        if (clazz.isPrimitive()) return results;
        if (clazz.isAnnotation()) return results;
        if (clazz.isInterface()) return results;
        if (clazz.isEnum()) return results;

        Collection<String> toIgnore = fieldsToIgnore != null ? Arrays.asList(fieldsToIgnore) : Collections.EMPTY_SET;
        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (toIgnore.contains(field.getName())) continue;
            if (isStatic && !Modifier.isStatic(field.getModifiers())) continue;
            if (!isStatic && Modifier.isStatic(field.getModifiers())) continue;
            if (type != null && !field.getType().equals(type)) continue;
            results.add(field);
        }
        return results;
    }

    public static Class[] getClassHierarchyInterfaces(Class clazz) {
        if (clazz == null) return new Class[] {};
        if (clazz.equals(java.lang.Object.class)) return new Class[] {};

        List<Class> results = new ArrayList<Class>();
        Class[] ifaces = clazz.getInterfaces();
        for (int i = 0; i < ifaces.length; i++) {
            if (!results.contains(ifaces[i])) results.add(ifaces[i]);
        }

        ifaces = getClassHierarchyInterfaces(clazz.getSuperclass());
        for (int i = 0; i < ifaces.length; i++) {
            if (!results.contains(ifaces[i])) results.add(ifaces[i]);             
        }

        int index = 0;
        Class[] array = new Class[results.size()];
        for (Class result : results) array[index++] = result;            
        return array;
    }

    /**
     * Generates the name of a get method for the given field name.
     *
     * @param fieldName the field name to generate a method name for
     * @return the generated get method name
     */
    public static final String getGetMethodName(String fieldName) {
        return PREFIX_GET + getValidAccessorName(fieldName);
    }

    /**
     * Generates the name of a create method for the given field name.
     *
     * @param fieldName the field name to generate a method name for
     * @return the generated create method name
     */
    public static final String getSetMethodName(String fieldName) {
        return PREFIX_SET + getValidAccessorName(fieldName);
    }

    /**
     * Convert the fieldName to a valid name to be append to METHOD_PREFIX_GET/SET
     *
     * @param fieldName name of the accessor property
     * @return valid name for accessor
     */
    private static String getValidAccessorName(String fieldName) {
        char firstChar = fieldName.charAt(0);
        String field = StringUtil.toJavaClassName(fieldName);
        if (fieldName.length() > 1) {
            //fix if firstCharacter is lowercase and second is uppercase, then create correct accessor (i.e. getter for field xXX is getxXX)
            char secondChar = fieldName.charAt(1);
            if (Character.isLowerCase(firstChar) && Character.isUpperCase(secondChar)) {
                field = field.replaceFirst(Character.toString(field.charAt(0)), Character.toString(firstChar));
            }
        }
        return field;
    }

    /**
     * Checks if the given method is a set method.
     *
     * @param method the Method to check
     * @return true if it is a set method
     */
    public static final boolean isSetMethod(final Method method) {
        if (method == null) {
            return false;
        }
        if (!method.getName().startsWith(PREFIX_SET)) {
            return false;
        }
        if (method.getParameterTypes().length != 1) {
            return false;
        }
        if ((method.getReturnType() != void.class) && (method.getReturnType() != Void.class)) {
            return false;
        }
        return true;
    }


    /**
     * Checks if the given method is a get method.
     *
     * @param method the Method to check
     * @return true if it is a get method
     */
    public static final boolean isGetMethod(final Method method) {
        if (method == null) {
            return false;
        }
        if (!method.getName().startsWith(PREFIX_GET)) {
            return false;
        }
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        if (method.getReturnType() == null) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the given method is a 'is' method.
     *
     * @param method the Method to check
     * @return true if it is a 'is' method
     */
    public static final boolean isIsMethod(final Method method) {
        if (method == null) {
            return false;
        }
        if (!method.getName().startsWith(PREFIX_IS)) {
            return false;
        }
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        if ((method.getReturnType().isPrimitive()) && (method.getReturnType() != Boolean.TYPE)) {
            return false;
        }
        if ((!method.getReturnType().isPrimitive()) && (method.getReturnType() != Boolean.class)) {
            return false;
        }
        return true;
    }

    public static Object parseAndSetFieldValues(Object obj, String fieldName, String[] fieldValues) throws Exception {
        StringBuffer propertyValue = new StringBuffer();
        for (int i = 0; i < fieldValues.length; i++) {
            String value = fieldValues[i];
            if (i != 0) propertyValue.append(ARRAYS_DELIMITER);
            if (fieldValues.length > 1) propertyValue.append(StringUtils.replace(value, ",", ",,"));
            else propertyValue.append(value);
        }
        return parseAndSetFieldValue(obj, fieldName, propertyValue.toString());
    }

    public static Object parseAndSetFieldValue(Object obj, String fieldName, String fieldValue) throws Exception {
        if (obj instanceof Map) {
            ((Map) obj).put(fieldName, parseValue(fieldValue, String.class)); //Map of Strings
        }
        else if (obj instanceof List) {
            int index = Integer.parseInt(fieldName);
            while (((List) obj).size() <= index) ((List) obj).add(null);
            ((List) obj).set(index, parseValue(fieldValue, String.class)); //List of Strings
        }
        else if (fieldName.indexOf('.') != -1) { //Set it by JXPath, valid only for strings
            JXPathContext ctx = JXPathContext.newContext(obj);
            ctx.setValue(fieldName.replace('.', '/'), parseValue(fieldValue, String.class));
        }
        else {
            // Find a Field
            Field field = getField(obj, fieldName);
            if (field != null) {
                Class fieldClass = field.getType();
                Object value = parseValue(fieldValue, fieldClass);
                field.set(obj, value);
                return obj;
            }
            // Find a getter and setter
            Method getter = getGetter(obj, fieldName);
            Method setter = getSetter(obj, getter, fieldName);
            if (setter != null && getter != null) {
                Object value = parseValue(fieldValue, getter.getReturnType());
                setter.invoke(obj, value);
            }
        }
        return obj;
    }


    public static Object setFieldValue(Object obj, String fieldName, Object fieldValue) throws Exception {
        if (obj instanceof Map) {
            ((Map) obj).put(fieldName, fieldValue); //Map of Strings
        }
        else if (obj instanceof List) {
            int index = Integer.parseInt(fieldName);
            while (((List) obj).size() <= index) ((List) obj).add(null);
            ((List) obj).set(index, fieldValue); //List of Strings
        }
        else if (fieldName.indexOf('.') != -1) { //Set it by JXPath, valid only for strings
            JXPathContext ctx = JXPathContext.newContext(obj);
            ctx.setValue(fieldName.replace('.', '/'), fieldValue);
        }
        else {
            // Find a Field
            Field field = getField(obj, fieldName);
            if (field != null) {
                field.set(obj, fieldValue);
                return obj;
            }
            // Find a getter and setter.
            Method getter = getGetter(obj, fieldName);
            Method setter = getSetter(obj, getter, fieldName);
            if (setter != null && getter != null) {
                setter.invoke(obj, fieldValue);
            }
        }
        return obj;
    }

    public static Object setFieldFile(Object obj, String fieldName, File fieldValue) throws Exception {
        // Find a Field
        Field field = getField(obj, fieldName);
        if (field != null) {
            Class fieldClass = field.getType();
            if (File.class.equals(fieldClass)) {
                field.set(obj, fieldValue);
            }
            else if (byte[].class.equals(fieldClass)) {
                field.set(obj, toByteArray(fieldValue));
            }
            else {
                throw new IllegalArgumentException("Field specified is not a File/byte[]: " + fieldName);
            }
        }
        // Find a getter and setter.
        Method getter = getGetter(obj, fieldName);
        Method setter = getSetter(obj, getter, fieldName);
        if (setter != null && getter != null) {
            Class returnType = getter.getReturnType();
            if (File.class.equals(returnType)) {
                setter.invoke(obj, fieldValue);
            }
            else if (byte[].class.equals(returnType)) {
                setter.invoke(obj, new Object[]{toByteArray(fieldValue)});
            }
            else {
                throw new IllegalArgumentException("Field specified is not a File/byte[]: " + fieldName);
            }
        }
        return obj;
    }

    public static Field getField(Object obj, String propertyName) {
        try {
            Field field = obj.getClass().getField(propertyName);
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && !Modifier.isFinal(field.getModifiers())) {
                return field;
            }
        } catch (NoSuchFieldException e) {
        }
        return null;
    }

    public static Method getGetter(Object obj, String propertyName) {
        Method getter = null;
        String propertyAccessorSuffix = StringUtils.capitalize(propertyName);
        String getterName = "get" + propertyAccessorSuffix;
        try {
            getter = obj.getClass().getMethod(getterName, new Class[0]);
        } catch (NoSuchMethodException e) {
            String booleanGetterName = "is" + propertyAccessorSuffix;
            try {
                getter = obj.getClass().getMethod(booleanGetterName, new Class[0]);
            } catch (NoSuchMethodException e1) {
            }
        }
        return getter;
    }

    public static Method getSetter(Object obj, Method getter, String propertyName) {
        Method setter = null;
        String propertyAccessorSuffix = StringUtils.capitalize(propertyName);
        String setterName = "set" + propertyAccessorSuffix;
        Class returnType = getter.getReturnType();
        try {
            setter = obj.getClass().getMethod(setterName, new Class[]{returnType});
        } catch (NoSuchMethodException e) {
        }
        return setter;
    }

    public static <T> T newInstanceForClass(Class<T> type) {
        Object object = null;
        if (type.isArray()) {
            Class componentClass = type.getComponentType();
            if (componentClass.isPrimitive()) {
                if (componentClass.equals(int.class)) object = new int[0];
                else if (componentClass.equals(boolean.class)) object = new boolean[0];
                else if (componentClass.equals(long.class)) object = new long[0];
                else if (componentClass.equals(char.class)) object = new char[0];
                else if (componentClass.equals(double.class)) object = new double[0];
                else if (componentClass.equals(float.class)) object = new float[0];
                else if (componentClass.equals(byte.class)) object = new byte[0];
                else if (componentClass.equals(short.class)) object = new short[0];
            }
            else if (String.class.equals(componentClass)) object = new String[0];
            else if (componentClass.equals(Integer.class)) object = new Integer[0];
            else if (componentClass.equals(Boolean.class)) object = new Boolean[0];
            else if (componentClass.equals(Long.class)) object = new Long[0];
            else if (componentClass.equals(Character.class)) object = new Character[0];
            else if (componentClass.equals(Double.class)) object = new Double[0];
            else if (componentClass.equals(Float.class)) object = new Float[0];
            else if (componentClass.equals(Byte.class)) object = new Byte[0];
            else if (componentClass.equals(Short.class)) object = new Short[0];
            else object = Array.newInstance(componentClass, 0);
        }
        else {
            if (String.class.equals(type)) object = "";
            else if (type.equals(int.class)) object = new Integer(0);
            else if (type.equals(boolean.class)) object = new Boolean(false);
            else if (type.equals(long.class)) object = new Long(0);
            else if (type.equals(char.class)) object = new Character((char) 0);
            else if (type.equals(double.class)) object = new Double(0);
            else if (type.equals(float.class)) object = new Float(0);
            else if (type.equals(byte.class)) object = new Byte((byte) 0);
            else if (type.equals(short.class)) object = new Short((short) 0);
            else  if (type.equals(Integer.class)) object = new Integer(0);
            else if (type.equals(Boolean.class)) object = new Boolean(false);
            else if (type.equals(Long.class)) object = new Long(0);
            else if (type.equals(Character.class)) object = new Character((char) 0);
            else if (type.equals(Double.class)) object = new Double(0);
            else if (type.equals(Float.class)) object = new Float(0);
            else if (type.equals(Byte.class)) object = new Byte((byte) 0);
            else if (type.equals(Short.class)) object = new Short((short) 0);
            else object = null;
        }
        return type.cast(object);
    }

    public static <T> T parseValue(String value, Class<T> type) throws Exception {
        if (type.isArray()) {
            return type.cast(toArray(value, type.getComponentType()));
        } else {
            Object object = null;
            if (String.class.equals(type)) object = value;
            else if (type.equals(int.class)) object = toInt(value);
            else if (type.equals(boolean.class)) object = (toBoolean(value)) ? Boolean.TRUE : Boolean.FALSE;
            else if (type.equals(long.class)) object = toLong(value);
            else if (type.equals(char.class)) object = toChar(value);
            else if (type.equals(double.class)) object = toDouble(value);
            else if (type.equals(float.class)) object = toFloat(value);
            else if (type.equals(byte.class)) object = toByte(value);
            else if (type.equals(short.class)) object = toShort(value);
            else if (type.equals(File.class)) object = toFile(value);
            else if (type.equals(Integer.class)) object = toInt(value);
            else if (type.equals(Boolean.class)) object = (toBoolean(value)) ? Boolean.TRUE : Boolean.FALSE;
            else if (type.equals(Long.class)) object = toLong(value);
            else if (type.equals(Character.class)) object = toChar(value);
            else if (type.equals(Double.class)) object = toDouble(value);
            else if (type.equals(Float.class)) object = toFloat(value);
            else if (type.equals(Byte.class)) object = toByte(value);
            else if (type.equals(Short.class)) object = toShort(value);

            return type.cast(object);
        }
    }

    public static Object[] toArray(String value, Class type) throws Exception {
        if (type.isArray()) throw new Exception("Nested array is not supported.");

        List objectValue = new ArrayList();
        StringTokenizer stk = new StringTokenizer(value, ARRAYS_DELIMITER, true);
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
            objectValue.set(i, parseValue(s, type));
        }

        Object baseObjectArray = getBaseArray(type, objectValue.size());
        return objectValue.toArray((Object[]) baseObjectArray);
    }

    private static Object getBaseArray(Class type, int length) {
        if (type.isPrimitive()) {
            if (type.equals(int.class)) return new Integer[length];
            else if (type.equals(boolean.class)) return new Boolean[length];
            else if (type.equals(long.class)) return new Long[length];
            else if (type.equals(char.class)) return new Character[length];
            else if (type.equals(double.class)) return new Double[length];
            else if (type.equals(float.class)) return new Float[length];
            else if (type.equals(byte.class)) return new Byte[length];
            else if (type.equals(short.class)) return new Short[length];
        }
        return Array.newInstance(type, length);

    }

    public static int toInt(String parameter) throws Exception {
        return Integer.parseInt(parameter);
    }

    public static boolean toBoolean(String parameter) throws Exception {
        return Boolean.valueOf(parameter);
    }

    public static long toLong(String parameter) throws Exception {
        return Long.parseLong(parameter);
    }

    public static char toChar(String parameter) throws Exception {
        if (parameter.length() != 1) throw new Exception("Invalid value for a char " + parameter);
        return parameter.charAt(0);
    }

    public static double toDouble(String parameter) throws Exception {
        return Double.parseDouble(parameter);
    }

    public static float toFloat(String parameter) throws Exception {
        return Float.parseFloat(parameter);
    }

    public static byte toByte(String parameter) throws Exception {
        return Byte.parseByte(parameter);
    }

    public static short toShort(String parameter) throws Exception {
        return Short.parseShort(parameter);
    }

    public static Object toFile(String paramValue) {
        if (paramValue == null || "".equals(paramValue.trim())) {
            return null;
        }
        if (paramValue.startsWith("/") || paramValue.contains(":")) {
            return new File(paramValue);
        } else {
            return new File(Application.lookup().getBaseAppDirectory() + "/" + paramValue);
        }
    }

    public static byte[] toByteArray(File f) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        int byteRead = -1;
        while ((byteRead = bis.read()) != -1) bos.write(byteRead);
        bis.close();
        bos.close();
        return bos.toByteArray();
    }

    public static void main(String[] args) {
        String s = "pepe";
        Object v1 = getPrivateField(s, "cunt");
        Object v2 = invokeMethod(s, "sustring", new Object[]{new Integer(1)});
        System.out.println(v1);
        System.out.println(v2);
    }
}