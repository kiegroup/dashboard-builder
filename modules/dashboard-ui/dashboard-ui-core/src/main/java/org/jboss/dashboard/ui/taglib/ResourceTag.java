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

import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Section;


import javax.servlet.jsp.JspTagException;
import java.util.Locale;

/**
 * Custom Tag which is used to render a resource defined for a panel
 */
public class ResourceTag extends javax.servlet.jsp.tagext.TagSupport {

    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ResourceTag.class.getName());

    /**
     * Action to execute
     */
    private String key = null;

    /**
     * Locale to use
     */
    private Locale locale = null;

    /**
     * Panel ID if set by hand
     */
    private String panel = null;

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {

        String key = getKey();
        String idPanel = null;

        if (getPanel() != null) {
            // Panel ID has been  passed
            idPanel = getPanel();
            setPanel(null);
        } else {
            // Try to get panel ID from request
            Panel panel = (Panel) pageContext.getRequest().getAttribute(Parameters.RENDER_PANEL);
            if(panel != null){
                idPanel = panel.getPanelId().toString();
            }
        }
        Panel panel = null;
        if (idPanel != null) {
            Section currSection = NavigationManager.lookup().getCurrentSection();
            if (currSection != null) {
                panel = currSection.getPanel(idPanel);
            }
        }

        if (key != null && panel != null) {

            Locale locale = getLocale();

            try {
                // Retrieve and render resource
                // Resources may be a single text, or a jsp to include
                // They're located in the panel descriptor
                String res = panel.getResource(key, locale); // getProvider().getResource(key);

                if (res == null) {
                    // Resource not found => Print key to make it noticiable
                    pageContext.getOut().print(key);
                    log.error("Resource " + key + " for panel " + panel.getId() + " not found!");
                } else if (res.toLowerCase().indexOf(".jsp") != -1) {
                    // It's a JSP to include
                    pageContext.include(res);
                } else {
                    // Just output resource value
                    pageContext.getOut().print(res);
                }

            } catch (Exception ex) {
                log.error("Error rendering resource " + key + " for panel " + panel.getId(), ex);
            }
        }

        return EVAL_PAGE;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPanel() {
        return panel;
    }

    public void setPanel(String panel) {
        this.panel = panel;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
