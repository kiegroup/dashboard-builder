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

import org.jboss.dashboard.ui.components.HandlerMarkupGenerator;

import javax.servlet.jsp.JspTagException;

public class HandlerTag extends GenericFactoryTag {

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        HandlerMarkupGenerator markupGenerator = HandlerMarkupGenerator.lookup();
        String textToWrite = markupGenerator.getMarkup(getBean(), getAction());
        try {
            pageContext.getOut().print(textToWrite);
        } catch (java.io.IOException ex) {
            handleError(ex);
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
