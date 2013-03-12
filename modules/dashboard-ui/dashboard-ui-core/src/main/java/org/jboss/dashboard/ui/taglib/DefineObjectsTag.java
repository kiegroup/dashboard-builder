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
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.LocaleManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.Locale;

/**
 * The defineObjects tag must define the following variables in the JSP page:
 * <p/>
 * <li>
 * User Adapter user<br>
 * org.jboss.dashboard.workspace.Workspace currentWorkspace<br>
 * org.jboss.dashboard.workspace.Section currentSection<br>
 * String currentPanelId<br>
 * org.jboss.dashboard.workspace.Panel currentPanel<br>
 * org.jboss.dashboard.ui.panel.PanelProvider panelProvider<br>
 * org.jboss.dashboard.ui.panel.PanelDriver panelDriver<br>
 * org.jboss.dashboard.workspace.PanelSession panelSession<br>
 * org.jboss.dashboard.ui.utils.forms.FormStatus currentForm<br>
 * java.util.Locale currentLocale<br>
 * java.lang.Boolean isAdminMode<br>
 * </li>
 * <p/>
 * These variables must reference the same panel API objects stored in the
 * request object of the JSP.
 * <p/>
 * A JSP using the defineObjects tag may use these variables from scriptlets
 * throughout the page.
 * <p/>
 * The defineObjects tag must not define any attribute and it must not contain
 * any body content.
 * <p/>
 * An example of a JSP using the defineObjects tag could be:
 * <CODE>
 * <panel:defineObjects/>
 * <p/>
 * <%=panel.getResource("panel.id.label", currentLocale)%>
 * </CODE>
 * After using the defineObjects tag, the JSP invokes the getResource() method of
 * the Panel to get the resource "panel.id.label" value using the current
 * session locale
 * @deprecated This class enforces use of JAVA blocks inside JSP's. Use of formatters avoids this incorrect pattern.
 */

public class DefineObjectsTag extends TagSupport {

    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DefineObjectsTag.class.getName());

    public static class TEI extends TagExtraInfo {

        public VariableInfo[] getVariableInfo(TagData tagData) {
            VariableInfo[] info = new VariableInfo[]{
                    new VariableInfo("currentSection",
                            "org.jboss.dashboard.workspace.Section",
                            true,
                            VariableInfo.AT_BEGIN),
                    new VariableInfo("currentWorkspace",
                            "org.jboss.dashboard.workspace.WorkspaceImpl",
                            true,
                            VariableInfo.AT_BEGIN),
                    new VariableInfo("currentPanelId",
                            "java.lang.String",
                            true,
                            VariableInfo.AT_BEGIN),
                    new VariableInfo("currentPanel",
                            "org.jboss.dashboard.workspace.Panel",
                            true,
                            VariableInfo.AT_BEGIN),
                    new VariableInfo("panelSession",
                            "org.jboss.dashboard.workspace.PanelSession",
                            true,
                            VariableInfo.AT_BEGIN),
                    new VariableInfo("panelProvider",
                            "org.jboss.dashboard.ui.panel.PanelProvider",
                            true,
                            VariableInfo.AT_BEGIN),
                    new VariableInfo("panelDriver",
                            "org.jboss.dashboard.ui.panel.PanelDriver",
                            true,
                            VariableInfo.AT_BEGIN),
                    new VariableInfo("currentLocale",
                            "java.util.Locale",
                            true,
                            VariableInfo.AT_BEGIN),
                    new VariableInfo("isAdminMode",
                            "java.lang.Boolean",
                            true,
                            VariableInfo.AT_BEGIN)

            };

            return info;
        }
    }

    /**
     * Processes the <CODE>defineObjects</CODE> tag.
     *
     * @return <CODE>SKIP_BODY</CODE>
     */
    public int doStartTag() throws JspException {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        // Current panel (passed as a parameter through the request)
        Panel currentPanel = null;
        try {
            currentPanel = (Panel) request.getAttribute(Parameters.RENDER_PANEL);
            if (currentPanel == null) {
                log.error("Current context values are: ");
                log.error("request.ATTRIBUTE_SECTION_ID = " + request.getAttribute("current_section_id"));
                log.error("session.ATTRIBUTE_SECTION_ID = " + request.getSession().getAttribute("current_section_id"));
                log.error("request.ATTRIBUTE_WORKSPACE_ID = " + request.getAttribute("current_workspace_id"));
                log.error("session.ATTRIBUTE_WORKSPACE_ID = " + request.getSession().getAttribute("current_workspace_id"));
                Section currentSectionValue = NavigationManager.lookup().getCurrentSection();
                Workspace currentWorkspaceValue = NavigationManager.lookup().getCurrentWorkspace();
                log.error("Current section = " + currentSectionValue == null ? "null" : currentSectionValue.getId().toString());
                log.error("Current workspace = " + currentWorkspaceValue == null ? "null" : currentWorkspaceValue.getId());
                throw new JspException("The panel is not present in the workspace response"); // Don't render
            }
        } catch (Exception e) {
            throw new JspException("The panel is not present in the workspace response"); // Don't render
        }

        // Retrieve current section from session
        Section currentSection = currentPanel.getSection();
        if (currentSection == null)
            throw new JspException("The section is not present in the workspace response"); // Don't render


        // Current workspace
        WorkspaceImpl currentWorkspace = currentSection.getWorkspace();

        PanelSession panelSession = SessionManager.getPanelSession(currentPanel);

        // Provider
        PanelProvider panelProvider = currentPanel.getInstance().getProvider();

        // Panel Driver
        PanelDriver panelDriver = panelProvider.getDriver();
        Locale currentLocale = LocaleManager.currentLocale();

        if (pageContext.getAttribute("currentSection") == null)
            pageContext.setAttribute("currentSection", currentSection, PageContext.PAGE_SCOPE);

        if (pageContext.getAttribute("currentWorkspace") == null)
            pageContext.setAttribute("currentWorkspace", currentWorkspace, PageContext.PAGE_SCOPE);

        if (pageContext.getAttribute("currentPanel") == null)
            pageContext.setAttribute("currentPanel", currentPanel, PageContext.PAGE_SCOPE);

        if (pageContext.getAttribute("panelProvider") == null)
            pageContext.setAttribute("panelProvider", panelProvider, PageContext.PAGE_SCOPE);

        if (pageContext.getAttribute("panelDriver") == null)
            pageContext.setAttribute("panelDriver", panelDriver, PageContext.PAGE_SCOPE);

        if (pageContext.getAttribute("panelSession") == null)
            pageContext.setAttribute("panelSession", panelSession, PageContext.PAGE_SCOPE);

        if (pageContext.getAttribute("currentLocale") == null)
            pageContext.setAttribute("currentLocale", currentLocale, PageContext.PAGE_SCOPE);

        return SKIP_BODY;
    }

}
