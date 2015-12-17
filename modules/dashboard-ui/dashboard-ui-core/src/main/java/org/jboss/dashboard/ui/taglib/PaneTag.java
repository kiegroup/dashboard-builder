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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * Outputs the content of a pane defined for a given template, screen and view. This information
 * is retrieved from the ViewsManager object.
 */
public class PaneTag extends BaseTag {
    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaneTag.class.getName());

    private String paneId = null;


    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspException {
         jspInclude("/configuration/show.jsp");
        return EVAL_PAGE;
    }


    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doStartTag() throws JspTagException {

        return SKIP_BODY;
    }

    public java.lang.String getId() {
        return paneId;
    }

    public void setId(java.lang.String newPaneId) {
        paneId = newPaneId;
    }
}
