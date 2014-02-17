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
package org.jboss.dashboard.ui.panel.parameters;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Supplies visible sections for current user.
 */
public class SectionDataSupplier implements ComboListParameterDataSupplier {
    public void init(PanelInstance instance) {
        //TODO: Implement construction of values and keys here
    }

    public List getValues() {
        List values = new ArrayList();
        List sections = getSections();
        for (int i = 0; i < sections.size(); i++)
            values.add(getTitle((Section) sections.get(i)));
        return values;
    }

    public List getKeys() {
        List keys = new ArrayList();
        List sections = getSections();
        for (int i = 0; i < sections.size(); i++)
            keys.add(String.valueOf(((Section) sections.get(i)).getId()));
        return keys;
    }

    private String getTitle(Section section) {
        return (String) (LocaleManager.lookup()).localize(section.getTitle());
    }

    private List getSections() {
        UserStatus userStatus = UserStatus.lookup();
        List sections = new ArrayList();
        WorkspaceImpl workspace = NavigationManager.lookup().getCurrentWorkspace();
        if (workspace != null) {
            Section[] workspaceSections = workspace.getAllSections();
            for (int i = 0; i < workspaceSections.length; i++) {
                SectionPermission viewPerm = SectionPermission.newInstance(workspaceSections[i], SectionPermission.ACTION_VIEW);
                if (userStatus.hasPermission(viewPerm))
                    sections.add(workspaceSections[i]);
            }
        }
        return sections;
    }
}
