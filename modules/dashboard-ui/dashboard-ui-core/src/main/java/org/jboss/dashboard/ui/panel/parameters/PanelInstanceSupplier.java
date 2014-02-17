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
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.PanelInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * PanelInstanceSupplier presents a list of panel instances for a given class or subclasses.
 */
public class PanelInstanceSupplier implements ComboListParameterDataSupplier {

    private PanelInstance instance = null;
    private Class panelProvider = null;

    public PanelInstanceSupplier(Class panelProvider) {
        this.panelProvider = panelProvider;
    }

    public void init(PanelInstance instance) {
        //TODO: Implement construction of values and keys here
        this.instance = instance;
    }

    public List getValues() {
        List values = new ArrayList();
        List panels = getPanels();
        for (int i = 0; i < panels.size(); i++)
            values.add(getTitle((PanelInstance) panels.get(i)));
        return values;
    }

    public List getKeys() {
        List keys = new ArrayList();
        List panels = getPanels();
        for (int i = 0; i < panels.size(); i++)
            keys.add(((PanelInstance) panels.get(i)).getId());
        return keys;
    }

    private String getTitle(PanelInstance instance) {
        return (String) (LocaleManager.lookup()).localize(instance.getTitle());
    }

    protected UserStatus getUserStatus() {
        return UserStatus.lookup();
    }

    private List getPanels() {
        List panels = new ArrayList();
        UserStatus userStatus = getUserStatus();
        if (instance != null) {
            PanelInstance[] instances = instance.getWorkspace().getPanelInstances();
            for (int i = 0; i < instances.length; i++) {
                if (panelProvider.isAssignableFrom(instances[i].getProvider().getDriver().getClass())) {
                    PanelPermission viewPerm = PanelPermission.newInstance(instances[i], PanelPermission.ACTION_VIEW);
                    if (userStatus.hasPermission(viewPerm))
                        panels.add(instances[i]);
                }
            }
        }

        return panels;
    }

}
