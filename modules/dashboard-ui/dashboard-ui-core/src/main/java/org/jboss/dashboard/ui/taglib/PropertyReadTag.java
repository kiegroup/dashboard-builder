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
package org.jboss.dashboard.ui.taglib;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.apache.commons.jxpath.JXPathContext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.Map;
import org.jboss.dashboard.LocaleManager;

/**
 *
 */
public class PropertyReadTag extends BaseTag {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PropertyReadTag.class.getName());
    private String object;
    private String property;
    private Boolean localize;

    public static class TEI extends TagExtraInfo {
        public VariableInfo[] getVariableInfo(TagData data) {
            String varName = data.getId();
            if (varName == null)
                return new VariableInfo[0];
            else
                return (new VariableInfo[]{
                        new VariableInfo(varName, "java.lang.String", true, VariableInfo.AT_END)
                });
        }

        ;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public final int doStartTag()
            throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    public Boolean getLocalize() {
        return localize;
    }

    public void setLocalize(Boolean localize) {
        this.localize = localize;
    }

    public int doEndTag() throws JspException {
        try {
            Object value = getPropertyValue();
            if (value == null) {
                log.debug("Property is null. Clearing content.");
                if (super.bodyContent != null) {
                    value = super.bodyContent.getString();
                    super.bodyContent.clear();
                }
            }
            if (value != null) {
                log.debug("value = " + value);
                if (super.id != null) {
                    log.debug("Setting " + super.id + " to " + value);
                    super.pageContext.setAttribute(super.id, value, PageContext.PAGE_SCOPE);
                    return SKIP_BODY;
                } else {
                    log.debug("Printing value " + value);
                    super.pageContext.getOut().print(value);
                }
            }
        } catch (Exception e) {
            handleError(e);
        }
        return EVAL_PAGE;
    }

    protected Object getPropertyValue() {
        log.debug("Getting property " + property + " from " + object);
        Object subjectOfTheGetter = null;
        if ("workspace".equalsIgnoreCase(object)) {
            subjectOfTheGetter = NavigationManager.lookup().getCurrentWorkspace();
        } else if ("section".equalsIgnoreCase(object)) {
            subjectOfTheGetter = NavigationManager.lookup().getCurrentSection();
        } else if ("panel".equalsIgnoreCase(object)) {
            subjectOfTheGetter = RequestContext.lookup().getActivePanel();
        } else if ("request".equalsIgnoreCase(object)) {
            subjectOfTheGetter = RequestContext.lookup().getRequest();
        } else if ("session".equalsIgnoreCase(object)) {
            subjectOfTheGetter = RequestContext.lookup().getRequest().getSessionObject();
        } else {
            log.warn("Invalid object to get property from: " + object);
        }
        if (subjectOfTheGetter == null) {
            log.debug("Cannot get current " + object);
            return null;
        }
        try {
            Object value = JXPathContext.newContext(subjectOfTheGetter).getValue(property);
            return formatValue(LocaleManager.lookup(), value, localize);
        }
        catch (Exception e) {
            log.warn("Error accessing property " + property + " in " + object + "." + e);
            return null;
        }
    }

    public static String formatValue(LocaleManager localeManager, Object value, Boolean localize) {
        boolean requiresL18n = localize != null && localize && value instanceof Map;
        String target = requiresL18n ? (String) localeManager.localize((Map) value) : (String) value;
        return StringEscapeUtils.ESCAPE_HTML4.translate(target);
    }
}
