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
package org.jboss.dashboard.ui.taglib;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.components.HandlerMarkupGenerator;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.workspace.Panel;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;

/**
 * Custom Tag which is used to provide URLs to invoke panels actions
 */
public class HiddenLinkTag extends javax.servlet.jsp.tagext.TagSupport {

    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HiddenLinkTag.class.getName());

    /**
     * Action to execute
     */
    private String action = null;

    private String params = null;

    /**
     * Panel ID if set by hand
     */
    private String panel = null;

    protected Panel getCurrentPanel() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest().getRequestObject();
        return (Panel) request.getAttribute(Parameters.RENDER_PANEL);
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        HandlerMarkupGenerator markupGenerator = (HandlerMarkupGenerator) Factory.lookup("org.jboss.dashboard.ui.components.HandlerMarkupGenerator");
        Panel thePanel = getCurrentPanel();
        if (getPanel() != null)
            thePanel = thePanel.getSection().getPanel(getPanel());
        String textToWrite = markupGenerator.getMarkupToPanelAction(thePanel, action);
        try {
            pageContext.getOut().print(textToWrite);
        } catch (java.io.IOException ex) {
            log.error(ex);
        }
        return EVAL_PAGE;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getPanel() {
        return panel;
    }

    public void setPanel(String panel) {
        this.panel = panel;
    }
}
