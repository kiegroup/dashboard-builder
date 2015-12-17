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
package org.jboss.dashboard.ui.taglib.formatter;

import org.apache.commons.jxpath.JXPathContext;
import org.jboss.dashboard.ui.taglib.BaseTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.Map;

/**
 *
 */
public class FragmentTag extends BaseTag {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FragmentTag.class.getName());
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getParam(String name) {
        FormatterTag parent = (FormatterTag) getParent();
        Object value = parent.getFragmentParams().get(name);
        if (value == null && parent.getFormaterTagDynamicAttributesInterpreter() != null) {
            value = parent.getFormaterTagDynamicAttributesInterpreter().getValueForParameter(name);
        }
        if (value == null && name.indexOf('/') != -1) try {
            log.debug("Attempt JXPath detection of param...");
            JXPathContext ctx = JXPathContext.newContext(parent.getFragmentParams());
            ctx.setLenient(false);
            value = ctx.getValue(name);
            if (value == null && parent.getFormaterTagDynamicAttributesInterpreter() != null) {
                String firstName = name.substring(0, name.indexOf('/'));
                Object firstValue = parent.getFormaterTagDynamicAttributesInterpreter().getValueForParameter(firstName);
                if (firstValue != null) {
                    ctx = JXPathContext.newContext(firstValue);
                    ctx.setLenient(false);
                    value = ctx.getValue(name.substring(name.indexOf('/') + 1));
                }
            }
        }
        catch (Exception e) {
            if (log.isDebugEnabled())
                log.warn("Error getting attribute " + value + " in params.");
        }
        return value;
    }

    public Map getParams() {
        FormatterTag parent = (FormatterTag) getParent();
        return parent.getFragmentParams();
    }

    public int doStartTag() throws JspException {
        if (!(getParent() instanceof FormatterTag))
            throw new JspException("Wrong nesting: fragment named " + name + " must be inside a formatter.");

        FormatterTag parent = (FormatterTag) getParent();
        if (parent.getCurrentStage() == FormatterTag.STAGE_RENDERING_FRAGMENTS) {
            if (name.equals(parent.getCurrentEnabledFragment())) {
                log.debug("Rendering fragment " + name + ".");
                return EVAL_BODY_INCLUDE;//_AGAIN??
            }
        } else if (parent.getCurrentStage() == FormatterTag.STAGE_READING_PARAMS) {
            parent.addFragment(name);
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        FormatterTag parent = (FormatterTag) getParent();
        if (parent.getCurrentStage() == FormatterTag.STAGE_RENDERING_FRAGMENTS &&
                name.equals(parent.getCurrentEnabledFragment())) {
            parent.clearFragmentParams();
        }
        return EVAL_PAGE;
    }
}

