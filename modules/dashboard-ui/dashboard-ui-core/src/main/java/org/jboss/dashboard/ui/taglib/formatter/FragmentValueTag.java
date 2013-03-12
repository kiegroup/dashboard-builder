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
import javax.servlet.jsp.tagext.*;
import java.io.IOException;

/**
 *
 */
public class FragmentValueTag extends BodyTagSupport {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FragmentValueTag.class.getName());
    public static final String VALUE_NAME = "value";

    public static class TEI extends TagExtraInfo {
        public VariableInfo[] getVariableInfo(TagData data) {
            String varName = data.getId();
            if (varName == null)
                return (new VariableInfo[]{
                        new VariableInfo(VALUE_NAME, "java.lang.Object", true, VariableInfo.NESTED)
                });
            else
                return (new VariableInfo[]{
                        new VariableInfo(varName, "java.lang.Object", true, VariableInfo.NESTED)
                });
        }

        ;
    }

    protected String name;
    protected Object value;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final int doStartTag() throws JspException {
        try {
            Tag parentTag = getParent();
            while (parentTag instanceof FragmentValueTag) {
                parentTag = parentTag.getParent();
            }
            if (!(parentTag instanceof FragmentTag))
                throw new JspException("Wrong nesting: fragmentValue named " + name + " must be inside a fragment.");
            FragmentTag parentFragment = (FragmentTag) parentTag;
            value = parentFragment.getParam(name);

            String valueName = id == null ? VALUE_NAME : id;
            if (value == null)
                pageContext.removeAttribute(valueName);
            else
                pageContext.setAttribute(valueName, value);
        } catch (Exception e) {
            log.error("Error:", e);
            throw new JspException(e);
        }
        return EVAL_BODY_AGAIN;
    }

    public int doEndTag() throws JspException {
        try {
            if (super.bodyContent == null)
                pageContext.getOut().print(value == null ? "" : value);
            else
                pageContext.getOut().print(bodyContent.getString());
        } catch (IOException e) {
            log.error("Error:", e);
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

}
