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

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;

public class LinkToWorkspaceTag extends BaseTag {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LinkToWorkspaceTag.class.getName());

    /**
     * Action to execute
     */
    private String workspace = null;


    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        try {
            pageContext.getOut().print(getLink((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), workspace));
        } catch (Exception e) {
            handleError(e);
        }
        return EVAL_PAGE;
    }

    /**
     * Get a non-friendly link to a workspace.
     *
     * @param request
     * @param response
     * @param workspaceId Workspace id to be referenced.
     * @return a non-friendly link to a workspace.
     * @deprecated use URLMarkupGenerator methods directly
     */
    public static String getLink(HttpServletRequest request, HttpServletResponse response, String workspaceId) {
        WorkspaceImpl workspace = null;
        try {
            workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
        } catch (Exception e) {
            log.error("Error: ", e);
            return null;
        }
        URLMarkupGenerator markupGenerator = UIServices.lookup().getUrlMarkupGenerator();
        return markupGenerator.getLinkToWorkspace(workspace, true);
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }
}
