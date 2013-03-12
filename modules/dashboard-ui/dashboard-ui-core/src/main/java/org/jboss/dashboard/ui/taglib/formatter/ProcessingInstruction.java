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
package org.jboss.dashboard.ui.taglib.formatter;

/**
 * This class represent a processing instruction to render a formatter. It can be one of two
 * types: RENDER_FRAGMENT or SET_ATTRIBUTE.
 * If it is RENDER_FRAGMENT, the name will be the fragment name, and value will be null.
 * If it is SET_ATTRIBUTE, the name will be the attribute name, and value will be the attribute value.
 */
public class ProcessingInstruction {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ProcessingInstruction.class.getName());

    public static final int RENDER_FRAGMENT = 0;
    public static final int SET_ATTRIBUTE = 1;
    public static final int INCLUDE_PAGE = 2;
    public static final int WRITE_OUT = 3;
    public static final int SET_DYNAMIC_ATTRIBUTES_INTERPRETER = 4;

    private int type;
    private String name;
    private Object value;

    public static ProcessingInstruction getRenderFragmentInstruction(String fragmentName) {
        ProcessingInstruction pi = new ProcessingInstruction();
        pi.type = RENDER_FRAGMENT;
        pi.name = fragmentName;
        pi.value = null;
        return pi;
    }

    public static ProcessingInstruction getSetParameterInstruction(String paramName, Object paramValue) {
        ProcessingInstruction pi = new ProcessingInstruction();
        pi.type = SET_ATTRIBUTE;
        pi.name = paramName;
        pi.value = paramValue;
        return pi;
    }

    public static ProcessingInstruction getIncludePageInstruction(String pageName) {
        ProcessingInstruction pi = new ProcessingInstruction();
        pi.type = INCLUDE_PAGE;
        pi.name = pageName;
        return pi;
    }

    public static ProcessingInstruction getWriteToOutInstruction(String text) {
        ProcessingInstruction pi = new ProcessingInstruction();
        pi.type = WRITE_OUT;
        pi.value = text;
        return pi;
    }

    public static ProcessingInstruction getAddAttributesInterpreterInstruction(FormaterTagDynamicAttributesInterpreter interpreter){
        ProcessingInstruction pi = new ProcessingInstruction();
        pi.type = SET_DYNAMIC_ATTRIBUTES_INTERPRETER ;
        pi.value = interpreter;
        return pi;
    }

    private ProcessingInstruction() {
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[ ");
        if (type == RENDER_FRAGMENT) {
            sb.append("Render ");
            sb.append(name);
        } else if (type == SET_ATTRIBUTE) {
            sb.append("Set ");
            sb.append(name);
            sb.append(" = ");
            sb.append(value);
        } else if (type == INCLUDE_PAGE) {
            sb.append("Include ");
            sb.append(name);
        } else if (type == WRITE_OUT) {
            sb.append("Write ");
            sb.append(value);
        }
        sb.append(" ]");
        return sb.toString();
    }

}
