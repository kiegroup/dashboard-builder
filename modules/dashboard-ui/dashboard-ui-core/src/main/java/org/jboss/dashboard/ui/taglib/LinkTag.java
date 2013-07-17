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
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.workspace.Panel;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;

/**
 * Custom Tag which is used to provide URLs to invoke panels actions
 */
public class LinkTag extends javax.servlet.jsp.tagext.TagSupport {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LinkTag.class.getName());


    /**
     * Action to execute
     */
    private String action = null;

    private String params = null;

    /**
     * Panel ID if set by hand
     */
    private String panel = null;

    private String useFriendlyUrl = "true";

    public String getUseFriendlyUrl() {
        return useFriendlyUrl;
    }

    public void setUseFriendlyUrl(String useFriendlyUrl) {
        this.useFriendlyUrl = useFriendlyUrl;
    }


    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        Panel panel;
        if (this.panel != null) {
            NavigationManager navigationManager = NavigationManager.lookup();
            panel = navigationManager.getCurrentSection().getPanel(this.panel);
        } else {
            panel = (Panel) pageContext.getRequest().getAttribute(Parameters.RENDER_PANEL);
        }
        URLMarkupGenerator markupGenerator = UIServices.lookup().getUrlMarkupGenerator();
        String linkStr = markupGenerator.getLinkToPanelAction(panel, getAction(), params, Boolean.valueOf(useFriendlyUrl).booleanValue());
        try {
            pageContext.getOut().print(linkStr);
        } catch (java.io.IOException ex) {
            log.error("LinkTag error: " + linkStr, ex);
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

    /**
     * @param request
     * @param response
     * @param panel
     * @param action
     * @param extraParams
     * @return
     * @deprecated. Use URLMarkupGenerator directly
     */
    public static String getLink(HttpServletRequest request, HttpServletResponse response, Panel panel, String action, String extraParams) {
        URLMarkupGenerator markupGenerator = UIServices.lookup().getUrlMarkupGenerator();
        String linkStr = markupGenerator.getLinkToPanelAction(panel, action, extraParams, true);
        return linkStr;
    }
}
