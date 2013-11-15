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

import org.jboss.dashboard.ui.formatters.FactoryURL;

import javax.servlet.jsp.JspTagException;

public class BeanTag extends GenericFactoryTag {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BeanTag.class.getName());

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        String linkStr = new FactoryURL(getBean(), getProperty()).toString();
        try {
            pageContext.getOut().print(linkStr);
        } catch (java.io.IOException e) {
            handleError(e);
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
