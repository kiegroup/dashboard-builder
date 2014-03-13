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

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.ui.components.BeanHandler;
import org.jboss.dashboard.ui.components.UIBeanHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;

public class UseComponentTag extends GenericFactoryTag {

    public UIBeanHandler getBeanInstance() {
        Object currentBean = getBean();
        if (currentBean == null) {
            throw new RuntimeException("Bean not found");
        }
        else if (currentBean instanceof String) {
            Object result = CDIBeanLocator.getBeanByNameOrType((String) currentBean);
            if (result == null) throw new RuntimeException("Bean not found");
            if (result instanceof UIBeanHandler) return (UIBeanHandler) result;
        }
        else if (currentBean instanceof UIBeanHandler) {
            return (UIBeanHandler) currentBean;
        }
        throw new RuntimeException("Bean " + currentBean + " is not an UIBeanHandler");
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        UIBeanHandler uiBean = getBeanInstance();
        String page = uiBean.getBeanJSP();
        if (StringUtils.isBlank(page)) throw new RuntimeException("Page for bean " + getBeanName() + " is null.");

        CodeBlockTrace trace = new BeanHandler.HandlerTrace(uiBean, null).begin();
        Object previousComponent = pageContext.getRequest().getAttribute(CURRENT_BEAN);
        try {
            uiBean.beforeRenderBean();
            pageContext.getRequest().setAttribute(CURRENT_BEAN, uiBean);
            jspInclude(page);
            pageContext.getRequest().setAttribute(CURRENT_BEAN, previousComponent);
            uiBean.afterRenderBean();
        } catch (Exception e) {
            handleError(e);
        } finally {
            pageContext.getRequest().setAttribute(CURRENT_BEAN, previousComponent);
            trace.end();
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
