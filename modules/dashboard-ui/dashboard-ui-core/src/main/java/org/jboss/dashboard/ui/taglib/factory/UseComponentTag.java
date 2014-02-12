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

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.ui.components.BeanHandler;
import org.jboss.dashboard.ui.components.UIBeanHandler;

import javax.servlet.jsp.JspTagException;

public class UseComponentTag extends GenericFactoryTag {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UseComponentTag.class.getName());

    public static final String COMPONENT_ATTR_NAME = "currentComponentBeingRendered";

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        Object bean = CDIBeanLocator.getBeanByNameOrType(getBean());
        if (bean != null) {
            if (bean instanceof UIBeanHandler) {
                UIBeanHandler uiBean = (UIBeanHandler) bean;
                String page = uiBean.getBeanJSP();
                if (page == null) log.error("Page for bean " + getBean() + " is null.");

                CodeBlockTrace trace = new BeanHandler.HandlerTrace(uiBean, null).begin();
                Object previousComponent = pageContext.getRequest().getAttribute(COMPONENT_ATTR_NAME);
                try {
                    uiBean.beforeRenderBean();
                    pageContext.getRequest().setAttribute(COMPONENT_ATTR_NAME, bean);
                    jspInclude(page);
                    pageContext.getRequest().setAttribute(COMPONENT_ATTR_NAME, previousComponent);
                    uiBean.afterRenderBean();
                } catch (Exception e) {
                    handleError(e);
                } finally {
                    pageContext.getRequest().setAttribute(COMPONENT_ATTR_NAME, previousComponent);
                    trace.end();
                }
            } else {
                log.error("Bean " + getBean() + " is not a UIBeanHandler");
            }
        } else {
            log.error("Bean " + getBean() + " is null.");
        }
        return EVAL_PAGE;
    }


    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doStartTag() throws JspTagException {
        return SKIP_BODY;
    }
}
