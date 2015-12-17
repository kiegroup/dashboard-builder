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
package org.jboss.dashboard.ui.panel.parameters;

import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import java.util.ArrayList;
import java.util.List;

public class TreeSectionDataSupplier implements ComboListParameterDataSupplier {
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TreeSectionDataSupplier.class);

    private List keys;
    private List values;

    public void init(PanelInstance instance) {
        values = new ArrayList();
        keys = new ArrayList();
        initSections(null, "", instance.getWorkspace());
    }

    public List getValues() {
        return values;
    }

    public List getKeys() {
        return keys;
    }

    private String getTitle(Section section) {
        return (String) (LocaleManager.lookup()).localize(section.getTitle());
    }

    private void initSections(Section rootSection, String indent, WorkspaceImpl workspace) {
        if (workspace != null) {
            UserStatus userStatus = UserStatus.lookup();
            Section[] sections = rootSection != null ? workspace.getAllChildSections(rootSection.getId()) : workspace.getAllRootSections();
            for (int i = 0; i < sections.length; i++) {
                SectionPermission viewPerm = SectionPermission.newInstance(sections[i], SectionPermission.ACTION_VIEW);
                if (userStatus.hasPermission(viewPerm)) {
                    keys.add("" + sections[i].getId());
                    values.add(indent + getTitle(sections[i]));
                    initSections(sections[i], indent + "--", workspace);
                }
            }
        }
    }
}
