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

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;

import javax.servlet.jsp.JspException;

public class FormURLTag extends GenericFactoryTag {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FormURLTag.class.getName());

    private boolean friendly = true;

    public boolean isFriendly() {
        return friendly;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }

    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        URLMarkupGenerator urlMarkupGenerator = UIServices.lookup().getUrlMarkupGenerator();
        String markup = friendly ? urlMarkupGenerator.getBaseURI() : urlMarkupGenerator.getServletMapping();
        try {
            pageContext.getOut().print(markup);
        } catch (java.io.IOException e) {
            handleError(e);
        }
        return EVAL_PAGE;
    }

}
