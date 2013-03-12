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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 */
public class FormatterParamTag extends TagSupport {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FormatterParamTag.class.getName());

    protected String name;
    protected Object value;

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


    public final int doStartTag() {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        if (!(getParent() instanceof FormatterTag))
            throw new JspException("Wrong nesting: formatterParam named " + name + " must be inside a formatter.");
        FormatterTag parent = (FormatterTag) getParent();
        if (parent.getCurrentStage() == FormatterTag.STAGE_READING_PARAMS)
            parent.setParam(name, value);
        return EVAL_PAGE;
    }

}
