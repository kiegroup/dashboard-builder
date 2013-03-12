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

import org.jboss.dashboard.commons.text.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ReflectionUtils {

    private static final String PREFIX_GET = "get";
    private static final String PREFIX_SET = "set";
    private static final String PREFIX_IS = "is";

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

    public static Object invokePrivateMethod(Object o, String methodName, Object[] params) {
        Method methods[] = o.getClass().getDeclaredMethods();
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

    public static void main(String[] args) {
        String s = new String("pepe");
        Object v1 = getPrivateField(s, "cunt");
        Object v2 = invokePrivateMethod(s, "sustring", new Object[] {new Integer(1)});
        System.out.println(v1);
        System.out.println(v2);
    }
}