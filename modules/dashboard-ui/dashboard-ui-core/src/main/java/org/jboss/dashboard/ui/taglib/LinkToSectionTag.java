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
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;

public class LinkToSectionTag extends BaseTag {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LinkToSectionTag.class.getName());

    /**
     * Action to execute
     */
    private Long section = null;


    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {

        WorkspaceImpl workspace = NavigationManager.lookup().getCurrentWorkspace();

        if (workspace != null) {
            Section section = workspace.getSection(getSection());

            if (section != null) {
                String linkStr = getLink((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), section);
                try {
                    pageContext.getOut().print(StringEscapeUtils.escapeHtml(linkStr));
                } catch (java.io.IOException ex) {
                    log.error("LinkToSectionTag error: " + linkStr, ex);
                }
            } else {
                log.error("Section '" + getSection() + "' not found");
            }
        } else {
            log.error("Current workspace not found");
        }

        return EVAL_PAGE;
    }

    protected String getLink(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Section section) {
        return UIServices.lookup().getUrlMarkupGenerator().getLinkToPage(section, true);
    }

    public Long getSection() {
        return section;
    }

    public void setSection(Long section) {
        this.section = section;
    }
}
