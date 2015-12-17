/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.dashboard.ui.components.BeanHandler;
import org.jboss.dashboard.ui.components.UIBeanHandler;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import java.io.IOException;

public class PropertyTag extends GenericFactoryTag {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PropertyTag.class.getName());

    public static final String VALUE_NAME = "value";

    public static class TEI extends TagExtraInfo {
        public VariableInfo[] getVariableInfo(TagData data) {
            String varName = data.getId();
            return (new VariableInfo[]{
                    new VariableInfo(varName == null ? VALUE_NAME : varName, "java.lang.Object", true, VariableInfo.NESTED)
            });
        }
    }

    private boolean valueIsHTML;
    private Object value;

    public boolean isValueIsHTML() {
        return valueIsHTML;
    }

    public void setValueIsHTML(boolean valueIsHTML) {
        this.valueIsHTML = valueIsHTML;
    }


    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspException {
        try {
            if (super.bodyContent == null) {
                Object value = getValue();
                String textValue = value == null ? "" : value.toString();
                pageContext.getOut().print(valueIsHTML ? textValue : StringEscapeUtils.ESCAPE_HTML4.translate(textValue));
            } else {
                pageContext.getOut().print(bodyContent.getString());
            }
        } catch (IOException e) {
            handleError(e);
        }
        value = null;
        return EVAL_PAGE;
    }

    protected Object getValue() {
        if (value != null) return value;

        Object beanObject = CDIBeanLocator.getBeanByNameOrType(getBeanName());
        if (beanObject != null) {
            JXPathContext ctx = JXPathContext.newContext(beanObject);
            try {
                return value = ctx.getValue(getProperty());
            } catch (JXPathException jxpe) {
                log.warn("Can't read property " + getProperty() + " in " + getBeanName() + ": ", jxpe);
            }
        }
        return null;
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doStartTag() throws JspException {
        try {
            Object value = getValue();
            String valueName = id == null ? VALUE_NAME : id;
            if (value == null) pageContext.removeAttribute(valueName);
            else pageContext.setAttribute(valueName, value);
        } catch (Exception e) {
            handleError(e);
        }
        return EVAL_BODY_AGAIN;
    }
}
