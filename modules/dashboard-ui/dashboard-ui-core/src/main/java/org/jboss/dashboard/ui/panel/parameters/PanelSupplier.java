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

import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.ui.taglib.LocalizeTag;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Section;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jboss.dashboard.LocaleManager;

/**
 * PanelSupplier presents a list of panels for a given class or subclasses.
 */
public class PanelSupplier implements ComboListParameterDataSupplier {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelSupplier.class.getName());

    private PanelInstance instance = null;
    private Class panelProvider = null;

    public PanelSupplier(Class panelProvider) {
        this.panelProvider = panelProvider;
    }

    /**
     * Called to initialize status
     */
    public void init(PanelInstance instance) {
        //TODO: Implement construction of values and keys here
        this.instance = instance;
    }

    /**
     * Returns the values to display for a given selection control
     */
    public List getValues() {
        List keys = getKeys();
        List values = new ArrayList();
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            Panel p = getPanelForKey(instance.getWorkspace(), key);
            if (p != null) {
                String panelTitle = LocalizeTag.getLocalizedValue(p.getTitle(), LocaleManager.currentLang(), true);
                String sectionTitle = LocalizeTag.getLocalizedValue(p.getSection().getTitle(), LocaleManager.currentLang(), true);
                values.add(panelTitle + " (" + sectionTitle + ")");
            }
        }
        return values;
    }

    /**
     * Returns the keys for displayed values
     */
    public List getKeys() {
        List keys = new ArrayList();
        List panels = getPanels();
        for (int i = 0; i < panels.size(); i++) {
            Panel panel = (Panel) panels.get(i);
            keys.add(panel.getPanelId() + "@" + panel.getSection().getId());
        }
        return keys;
    }

    private List getPanels() {
        UserStatus userStatus = UserStatus.lookup();
        List panels = new ArrayList();
        if (instance != null) {
            PanelInstance[] instances = instance.getWorkspace().getPanelInstances();
            for (int i = 0; i < instances.length; i++) {
                if (panelProvider.isAssignableFrom(instances[i].getProvider().getDriver().getClass())) {
                    PanelPermission viewPerm = PanelPermission.newInstance(instances[i], PanelPermission.ACTION_VIEW);
                    if (userStatus.hasPermission(viewPerm))
                        panels.addAll(Arrays.asList(instances[i].getAllPanels()));
                }
            }
        }
        return panels;
    }

    /**
     * Given a key like panelId@sectionId@workspaceId, return the panel if any, that
     * matches this pattern
     *
     * @param key    Encoded form of panel id.
     * @param workspace The workspace where the panel is expected to be.
     * @return the panel matching this pattern, or null if not found, or key is wrong.
     */
    public static Panel getPanelForKey(WorkspaceImpl workspace, String key) {
        try {
            int first = key.indexOf('@');
            String panelId = key.substring(0, first);
            String sectionId = key.substring(first + 1);
            Section section = workspace.getSection(Long.decode(sectionId));
            Panel panel = section.getPanel(panelId);
            return panel;
        } catch (Exception e) {
            log.error("Invalid panel key " + key);
        }
        return null;
    }
}
