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
package org.jboss.dashboard.ui.panel.navigation.menu;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.ui.panel.parameters.ComboListParameterDataSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Provides constant values for the menu panel: Workspace, page and panel
 */
public class MenuTypeListDataSupplier implements ComboListParameterDataSupplier {
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MenuTypeListDataSupplier.class);

    private List keys;
    private List values;

    /**
     * Parameters
     */
    public static final String PARAMETER_LANGUAGE = "lang";
    public static final String PARAMETER_PAGE = "page";
    public static final String PARAMETER_WORKSPACE = "workspace";

    /** The locale manager. */
    protected LocaleManager localeManager;

    public MenuTypeListDataSupplier() {
        localeManager = LocaleManager.lookup();
    }

    public void init(PanelInstance instance) {
        if (keys == null) {
            keys = new ArrayList();
            keys.add(PARAMETER_LANGUAGE);
            keys.add(PARAMETER_PAGE);
            keys.add(PARAMETER_WORKSPACE);
        }
        if (values == null) {
            ResourceBundle bundle = localeManager.getBundle("org.jboss.dashboard.ui.panel.navigation.menu.messages", LocaleManager.currentLocale());
            values = new ArrayList();
            values.add(bundle.getString("advanced.menu.language"));
            values.add(bundle.getString("advanced.menu.page"));
            values.add(bundle.getString("advanced.menu.workspace"));
        }
    }

    public List getValues() {
        return values;
    }

    public List getKeys() {
        return keys;
    }
}
