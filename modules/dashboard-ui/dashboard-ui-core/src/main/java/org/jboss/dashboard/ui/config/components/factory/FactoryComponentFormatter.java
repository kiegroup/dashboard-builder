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
package org.jboss.dashboard.ui.config.components.factory;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.utils.forms.RenderUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.factory.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.*;
import java.util.*;

public class FactoryComponentFormatter extends Formatter {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FactoryComponentFormatter.class.getName());

    private FactoryComponentHandler factoryComponentHandler;

    public FactoryComponentHandler getFactoryComponentHandler() {
        return factoryComponentHandler;
    }

    public void setFactoryComponentHandler(FactoryComponentHandler factoryComponentHandler) {
        this.factoryComponentHandler = factoryComponentHandler;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        String componentId = getFactoryComponentHandler().getFactoryComponentName();
        if (componentId == null) {
            renderFragment("empty");
        } else
            try {
                serviceComponent(componentId);
            } catch (LookupException e) {
                log.error("Error: ", e);
            }
    }

    protected void serviceHeader(Component component) {
        renderFragment("basicPropertiesStart");
        setAttribute("propertyName", "name");
        setAttribute("propertyValue", RenderUtils.noNull(component.getName()));
        renderFragment("outputBasicProperty");

        setAttribute("propertyName", "class");
        setAttribute("propertyValue", RenderUtils.noNull(component.getClazz()));
        renderFragment("outputBasicProperty");

        setAttribute("propertyName", "scope");
        setAttribute("propertyValue", RenderUtils.noNull(component.getScope()));
        renderFragment("outputBasicProperty");

        setAttribute("propertyName", "description");
        setAttribute("propertyValue", RenderUtils.noNull(component.getDescription()));
        renderFragment("outputBasicProperty");

        setAttribute("propertyName", "alias");
        setAttribute("propertyValue", RenderUtils.noNull(component.getAlias()));
        renderFragment("outputBasicProperty");

        renderFragment("basicPropertiesEnd");

        switch (component.getStatus()) {
            case Component.STATUS_INVALID:
                log.error("Component in invalid status " + component.getName());
                renderFragment("statusInvalid");
                return;
            case Component.STATUS_VALID:
                renderFragment("statusValid");
                break;
            default:
                log.error("Component in unknown status " + component.getName());
        }
    }

    protected void serviceProperties(Component component) {
        Set properties = new TreeSet();
        if (component.getScope().equals(Component.SCOPE_GLOBAL) || component.getScope().equals(Component.SCOPE_VOLATILE)) {
            Object obj = null;
            try {
                obj = component.getObject();
            } catch (LookupException e) {
                log.error("Error: ", e);
            }

            if (obj instanceof Map) {
                properties.addAll(((Map) obj).keySet());
            } else if (obj instanceof List) {
                for (int i = 0; i < ((List) obj).size(); i++) {
                    properties.add(new Integer(i));
                }
            } else {
                Method[] methods = obj.getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    String propertyName = getPropertyName(method);
                    if (propertyName != null && isGetter(method))
                        properties.add(propertyName);
                }
                Field[] fields = obj.getClass().getFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (Modifier.isPublic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                        properties.add(field.getName());
                    }
                }
            }
            if (!properties.isEmpty()) {
                String propertyType = "";
                String fullPropertyType = "";
                renderFragment("propertiesStart");
                int indexStyle = 0;
                for (Iterator it = properties.iterator(); it.hasNext();) {
                    indexStyle++;
                    Object property = it.next();
                    Object value = null;
                    if (obj instanceof Map) {
                        value = ((Map) obj).get(property);
                        propertyType = "String";
                        fullPropertyType = "java.lang.String []";
                    } else if (obj instanceof List) {
                        value = ((List) obj).get(((Integer) property).intValue());
                        propertyType = "String";
                        fullPropertyType = "java.lang.String []";
                    } else {
                        String propertyName = (String) property;
                        // Find a getter.
                        String propertyAccessorSuffix = Character.toUpperCase(propertyName.charAt(0)) + (propertyName.length() > 1 ? propertyName.substring(1) : "");
                        String getterName = "get" + propertyAccessorSuffix;
                        String booleanGetterName = "is" + propertyAccessorSuffix;
                        Method getter = null;
                        Class propertyClass = null;
                        try {
                            getter = obj.getClass().getMethod(getterName, new Class[0]);
                            if (getter != null) try {
                                value = getter.invoke(obj, new Object[0]);
                                propertyClass = getter.getReturnType();
                            } catch (IllegalAccessException e) {
                                log.error("Error:", e);
                            } catch (InvocationTargetException e) {
                                log.error("Error:", e);
                            }
                        } catch (NoSuchMethodException e) {
                            log.debug("No getter " + getterName + " found.");
                            try {
                                getter = obj.getClass().getMethod(booleanGetterName, new Class[0]);
                                if (getter != null) try {
                                    value = getter.invoke(obj, new Object[0]);
                                    propertyClass = getter.getReturnType();
                                } catch (IllegalAccessException iae) {
                                    log.error("Error:", iae);
                                } catch (InvocationTargetException ite) {
                                    log.error("Error:", ite);
                                }
                            } catch (NoSuchMethodException e1) {
                                log.debug("No getter " + booleanGetterName + " found.");
                            }
                        }

                        if (propertyClass == null) {
                            //Find field
                            try {
                                Field field = obj.getClass().getField(propertyName);
                                if (field != null) {
                                    value = field.get(obj);
                                    propertyClass = field.getType();
                                }
                            } catch (NoSuchFieldException e) {
                                log.error("Error: ", e);
                            } catch (IllegalAccessException e) {
                                log.error("Error: ", e);
                            }
                        }

                        if (propertyClass.isArray()) {
                            String componentTypeClass = propertyClass.getComponentType().getName();
                            if (componentTypeClass.indexOf('.') != -1) {
                                propertyType = componentTypeClass.substring(componentTypeClass.lastIndexOf('.') + 1) + "[]";
                            } else {
                                propertyType = componentTypeClass + " []";
                            }
                            fullPropertyType = componentTypeClass + " []";
                        } else if (propertyClass.isPrimitive()) {
                            propertyType = propertyClass.getName();
                            fullPropertyType = propertyType;
                        } else {
                            if (propertyClass.getName().indexOf('.') != -1) {
                                propertyType = propertyClass.getName().substring(propertyClass.getName().lastIndexOf('.') + 1);
                            } else {
                                propertyType = propertyClass.getName();
                            }
                            fullPropertyType = propertyClass.getName();
                        }
                    }

                    if (value == null) {
                        value = "";
                    } else if (value instanceof String ||
                            value instanceof Integer ||
                            value instanceof Short ||
                            value instanceof Character ||
                            value instanceof Long ||
                            value instanceof Byte ||
                            value instanceof Boolean ||
                            value instanceof Float ||
                            value instanceof Double ||
                            value.getClass().isPrimitive()) {

                    } else if (value.getClass().isArray()) {
                        int length = Array.getLength(value);
                        String componentType = value.getClass().getComponentType().getName();
                        value = componentType + " [" + length + "]";
                    } else {
                        value = value.getClass().getName();
                    }

                    Map configuredValues = component.getComponentConfiguredProperties();
                    List configuredValue = (List) configuredValues.get(property);
                    StringBuffer sb = new StringBuffer();
                    if (configuredValue != null)
                        for (int i = 0; i < configuredValue.size(); i++) {
                            PropertyChangeProcessingInstruction instruction = (PropertyChangeProcessingInstruction) configuredValue.get(i);
                            if (instruction instanceof PropertyAddProcessingInstruction) {
                                sb.append("\n+").append(instruction.getPropertyValue());
                            } else if (instruction instanceof PropertySubstractProcessingInstruction) {
                                sb.append("\n-").append(instruction.getPropertyValue());
                            } else if (instruction instanceof PropertySetProcessingInstruction) {
                                sb.setLength(0);
                                sb.append(" ").append(instruction.getPropertyValue());
                            }
                        }

                    setAttribute("configuredValue", StringUtils.replace(sb.toString(), ",", ", "));
                    setAttribute("propertyType", propertyType);
                    setAttribute("fullPropertyType", fullPropertyType);
                    setAttribute("propertyName", property);
                    setAttribute("propertyValue", value);
                    setAttribute("estilo", indexStyle % 2 == 0 ? "skn-even_row" : "skn-odd_row");
                    renderFragment("outputProperty");
                }
                renderFragment("propertiesEnd");
            }
        }
    }

    protected void serviceFiles(Component component) {
        renderFragment("filesStart");
        List propertiesFilesAdded = component.getPropertiesFilesAdded();
        for (int i = 0; i < propertiesFilesAdded.size(); i++) {
            String filename = (String) propertiesFilesAdded.get(i);
            setAttribute("fileName", filename);
            renderFragment("file");
        }
        renderFragment("filesEnd");
    }

    protected void serviceComponent(String componentId) throws LookupException {
        renderFragment("outputStart");
        ComponentsTree tree = Application.lookup().getGlobalFactory().getTree();
        Component component = tree.getComponent(componentId);
        serviceHeader(component);
        if (component.getObject() == null) {
            renderFragment("outputDisabled");
        } else {
            if (component.getStatus() == Component.STATUS_VALID)
                serviceProperties(component);
        }
        serviceFiles(component);
        renderFragment("outputEnd");
    }

    protected String getPropertyName(Method method) {
        String propName = null;
        if (isSetter(method)) {
            propName = method.getName().substring(3);
        } else if (isGetter(method)) {
            if (method.getName().startsWith("get"))
                propName = method.getName().substring(3);
            else
                propName = method.getName().substring(2);
        }
        if (propName != null) {
            propName = Character.toLowerCase(propName.charAt(0)) + (propName.length() > 1 ? propName.substring(1) : "");
        }
        return propName;
    }

    protected boolean isGetter(Method m) {
        if (m.getParameterTypes().length > 0)
            return false;
        Class returnType = m.getReturnType();
        if (returnType == null || returnType.equals(void.class))
            return false;
        int modifiers = m.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            return false;
        }
        if (m.getName().startsWith("get") && !"get".equals(m.getName()) && !"getClass".equals(m.getName())) {
            return true;
        }
        if (m.getName().startsWith("is") && !"is".equals(m.getName()) && boolean.class.equals(returnType)) {
            return true;
        }
        return false;
    }

    protected boolean isSetter(Method m) {
        if (m.getParameterTypes().length != 1)
            return false;
        Class returnType = m.getReturnType();
        if (!returnType.equals(void.class))
            return false;
        if (m.getName().startsWith("set") && !"set".equals(m.getName())) {
            return true;
        }
        return false;
    }

}
