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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.users.UserStatus;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderIndentedSectionsFormatter extends Formatter {

    @Inject
    private transient Logger log;

    @Inject
    private NavigationManager navigationManager;

    private List pageTitles = new ArrayList();
    private List pages = new ArrayList();

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        String preffix = (String) getParameter("preffix");
        String workspaceId = (String) getParameter("workspaceId");
        String permanentLinkParam = (String) getParameter("permanentLink");
        boolean permanentLink = "true".equals(permanentLinkParam);
        if (workspaceId == null) {
            workspaceId = getNavigationManager().getCurrentWorkspaceId();
        }
        try {
            initSections(preffix == null ? "--" : preffix, (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId));
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        //initSections(null, "", preffix == null ? "--" : preffix);
        if (pages.isEmpty()) {
            renderFragment("empty");
        } else {
            renderFragment("outputStart");
            Long currentSectionId = getNavigationManager().getCurrentSectionId();
            if (currentSectionId == null) {
                renderFragment("outputNoneSelected");
            }
            for (int i = 0; i < pages.size(); i++) {
                Section section = (Section) pages.get(i);
                String title = (String) pageTitles.get(i);
                setAttribute("id", section.getId());
                setAttribute("title", title);
                setAttribute("url", getPageUrl(request, section, permanentLink));
                renderFragment(section.getDbid().equals(currentSectionId) ? "outputSelected" : "output");
            }
            renderFragment("outputEnd");
        }
    }

    protected void initSections(String preffix, WorkspaceImpl workspace) {
        if (workspace != null) {
            Section[] sections = workspace.getAllSections(); //Sorted!
            for (int i = 0; i < sections.length; i++) {
                Section section = sections[i];
                int depth = section.getDepthLevel();
                SectionPermission viewPerm =
                        SectionPermission.newInstance(sections[i], SectionPermission.ACTION_VIEW);
                if (UserStatus.lookup().hasPermission(viewPerm)) {
                    pages.add(section);
                    String title = getTitle(sections[i]);
                    pageTitles.add(StringUtils.leftPad(title, title.length() + (depth * preffix.length()), preffix));
                } else { // Skip all following pages with larger depth (children)
                    while (i + 1 < sections.length && sections[i + 1].getDepthLevel() > depth) i++;
                }
            }
        }
    }

    protected String getTitle(Section section) {
        return StringUtils.defaultString(StringEscapeUtils.ESCAPE_HTML4.translate((String) LocaleManager.lookup().localize(section.getTitle())));
    }

    protected String getPageUrl(HttpServletRequest request, Section section, boolean permanent) {
        if (permanent) {
            Map parms = new HashMap();
            parms.put(NavigationManager.WORKSPACE_ID, section.getWorkspace().getId());
            parms.put(NavigationManager.PAGE_ID, section.getId());
            String pageURL = UIServices.lookup().getUrlMarkupGenerator().getPermanentLink("org.jboss.dashboard.ui.NavigationManager", "NavigateToPage", parms);
            if (pageURL.startsWith(request.getContextPath())) {
                pageURL = pageURL.substring((request.getContextPath()).length());
            }
            while (pageURL.startsWith("/")) pageURL = pageURL.substring(1);
            return pageURL;
        } else {
            return UIServices.lookup().getUrlMarkupGenerator().getLinkToPage(section, true);
        }
    }
}
