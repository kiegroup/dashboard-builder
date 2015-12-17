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
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 *
 */
public class MovableWindowTag extends BaseTag {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MovableWindowTag.class.getName());

    public static String WINDOW_ID_ATTRIBUTE = "WINDOW_POPUP_ID_ATTR";
    public static String WINDOW_TITLE_ATTRIBUTE = "WINDOW_POPUP_TITLE_ATTR";

    /** window id */
    private String windowId = null;

    /** window title */
    private String windowTitle = "";

    public String getId() {
        return windowId;
    }

    public void setId(String windowId) {
        this.windowId = windowId;
    }

    public String getTitle() {
        return windowTitle;
    }

    public void setTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public final int doStartTag() throws JspException {
        if (windowId == null) windowId = "_" + Math.random() + "_";
        pageContext.getRequest().setAttribute(WINDOW_ID_ATTRIBUTE, windowId);
        pageContext.getRequest().setAttribute(WINDOW_TITLE_ATTRIBUTE, windowTitle);
        jspInclude("/templates/popupWindowStart.jsp");
        pageContext.getRequest().removeAttribute(WINDOW_ID_ATTRIBUTE);
        pageContext.getRequest().removeAttribute(WINDOW_TITLE_ATTRIBUTE);
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        if (windowId == null) windowId = "_" + Math.random() + "_";
        pageContext.getRequest().setAttribute(WINDOW_ID_ATTRIBUTE, windowId);
        jspInclude("/templates/popupWindowEnd.jsp");
        pageContext.getRequest().removeAttribute(WINDOW_ID_ATTRIBUTE);
        return EVAL_PAGE;
    }
}
