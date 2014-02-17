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
package org.jboss.dashboard.ui.config.components.homePages;

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.users.Role;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class HomePagesFormatter extends Formatter {

    @Inject
    private transient Logger log;

    @Inject
    private HomePagesHandler homePagesHandler;

    public HomePagesHandler getHomePagesHandler() {
        return homePagesHandler;
    }

    public void setHomePagesHandler(HomePagesHandler homePagesHandler) {
        this.homePagesHandler = homePagesHandler;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        try {
            WorkspaceImpl workspace = (WorkspaceImpl) getHomePagesHandler().getWorkspace();
            renderFragment("outputStart");

            Set<Role> allRoles = SecurityServices.lookup().getRolesManager().getAllRoles();
            if (allRoles.size() > 0) {
                int i = 0;
                for (Role role : allRoles) {
                    setAttribute("className", (i++%2 == 0) ? "skn-even_row" : "skn-odd_row");
                    setAttribute("roleName", role.getName());
                    setAttribute("roleDescription", role.getDescription(getLocale()));
                    renderFragment("outputRoleStart");
                    Section defaultSectionForRole = getHomePagesHandler().getDefaultSectionForRole(role);
                    renderPageSelect(workspace, defaultSectionForRole != null ? defaultSectionForRole.getId() : null, role);
                }
            }
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new FormatterException(e);
        }
    }

    protected void renderPageSelect(WorkspaceImpl workspace, Long selectedOption, Role role) {
        Section[] sections = workspace.getAllSections(); //Sorted
        setAttribute("inputName", "defaultPageFor_" + role.getName());
        setAttribute("roleName", role.getName());
        setAttribute("roleDescription", role.getDescription(getLocale()));
        renderFragment("outputSelectStart");
        renderFragment("outputPageSelectOption");//Empty option
        for (int i = 0; i < sections.length; i++) {
            Section section = sections[i];
            setAttribute("selected", section.getId().equals(selectedOption));
            setAttribute("sectionId", section.getId());
            setAttribute("sectionName", StringEscapeUtils.escapeHtml(getLocalizedValue(section.getTitle())));
            renderFragment("outputPageSelectOption");
        }
        renderFragment("outputSelectEnd");
    }
}
