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

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.security.SectionPermission;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class RenderNestedSectionsFormatter extends Formatter {

    @Inject
    private transient Logger log;

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        List visibleIds = (List) getParameter("visibleIds");
        List checkedIds = (List) getParameter("checkedIds");
        List selectableIds = (List) getParameter("selectableIds");
        String workspaceId = (String) getParameter("workspaceId");
        String rootSectionId = (String) getParameter("rootSectionId");

        boolean showHiddenPages = (!(getParameter("showHiddenPages") != null && !"".equals(getParameter("showHiddenPages")))) || Boolean.valueOf((String) getParameter("showHiddenPages")).booleanValue();
        Boolean checkPermissions = Boolean.valueOf((String) getParameter("checkPermissions"));
        try {
            WorkspaceImpl currentWorkspace = (WorkspaceImpl) (workspaceId != null && !"".equals(workspaceId) ? UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId) : NavigationManager.lookup().getCurrentWorkspace());
            Section[] rootSections;
            if (StringUtils.isEmpty(rootSectionId)) {
                rootSections = currentWorkspace.getAllRootSections();
            } else {
                Section parentSection  = currentWorkspace.getSection(Long.decode(rootSectionId));
                List<Section> children = parentSection.getChildren();
                rootSections = children.toArray(new Section[children.size()]);
            }
            renderFragment("outputStart");
            for (Section rootSection : rootSections) {
                renderSection(httpServletRequest, httpServletResponse, rootSection, visibleIds, checkedIds, selectableIds, Boolean.TRUE.equals(checkPermissions), showHiddenPages);
            }
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error rendering sections: ", e);
        }
    }

    protected void renderSection(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Section section, List visibleIds, List checkedIds, List selectableIds, boolean checkPermissions, boolean showHiddenPages) {

        if (!showHiddenPages && !section.isVisible().booleanValue()) return;

        if (checkPermissions) {
            SectionPermission viewPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_VIEW);
            boolean canView = UserStatus.lookup().hasPermission(viewPerm);
            if (!canView)
                return;
        }

        if (visibleIds != null && !visibleIds.contains(section.getId())) return;
        boolean current = section.equals(NavigationManager.lookup().getCurrentSection());
        setAttribute("current", current);
        renderFragment("sectionStart");
        setAttribute("name", StringEscapeUtils.escapeHtml(getLocalizedValue(section.getTitle())));
        setAttribute("url", UIServices.lookup().getUrlMarkupGenerator().getLinkToPage(section, true));
        setAttribute("id", section.getId());
        setAttribute("current", current);
        setAttribute("isRoot", section.isRoot());
        setAttribute("hasChildren", section.getChildren() != null && !section.getChildren().isEmpty());
        setAttribute("checked", checkedIds == null || checkedIds.contains(section.getId()));
        setAttribute("selectable", selectableIds == null || selectableIds.contains(section.getId()));

        SectionPermission sectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_EDIT);
        setAttribute("editSection", UserStatus.lookup().hasPermission(sectionPerm));

        sectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_EDIT_PERMISSIONS);
        setAttribute("editPermissions", UserStatus.lookup().hasPermission(sectionPerm));

        sectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_DELETE);
        setAttribute("deleteSection", UserStatus.lookup().hasPermission(sectionPerm));

        renderFragment("outputSection");
        List<Section> childSections = section.getChildren();
        if (!childSections.isEmpty()) {
            setAttribute("id", section.getId());
            renderFragment("outputChildStart");
            for (Section childSection : childSections) {
                renderSection(httpServletRequest, httpServletResponse, childSection, visibleIds, checkedIds, selectableIds, checkPermissions, showHiddenPages);
            }
            renderFragment("outputChildEnd");
        }
        renderFragment("sectionEnd");
    }
}
