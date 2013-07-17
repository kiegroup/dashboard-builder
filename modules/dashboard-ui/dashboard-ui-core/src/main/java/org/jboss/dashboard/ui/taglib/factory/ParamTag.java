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
package org.jboss.dashboard.ui.taglib.factory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

public class ParamTag extends javax.servlet.jsp.tagext.TagSupport {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ParamTag.class.getName());

    private String name;
    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int doEndTag() throws JspException {
        Tag parentTag = getParent();
        if (parentTag instanceof URLTag) {
            ((URLTag) parentTag).addParam(name, value);
        } else {
            throw new JspException("Invalid nesting, factory:param is not inside a factory:url tag.");
        }
        return SKIP_BODY;
    }

}
