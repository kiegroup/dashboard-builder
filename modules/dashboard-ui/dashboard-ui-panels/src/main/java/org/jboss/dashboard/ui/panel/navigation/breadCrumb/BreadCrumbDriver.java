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
package org.jboss.dashboard.ui.panel.navigation.breadCrumb;

import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.parameters.ComboListParameter;
import org.jboss.dashboard.ui.panel.parameters.ComboListParameterDataSupplier;
import org.jboss.dashboard.ui.panel.parameters.HTMLTextAreaParameter;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;

import java.util.ArrayList;
import java.util.List;

public class BreadCrumbDriver extends PanelDriver {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BreadCrumbDriver.class.getName());

    public static final String PARAM_INITIAL_TRIM_DEPTH = "initialTrimDepth";
    public static final String PARAM_ELEMENT_TEMPLATE = "elementTemplate";
    public static final String PARAM_SEPARATOR = "separator";

    public void init(PanelProvider panelProvider) throws Exception {
        super.init(panelProvider);
        addParameters(panelProvider);
    }

    protected void addParameters(PanelProvider panelProvider) throws Exception {
        addParameter(new ComboListParameter(panelProvider, PARAM_INITIAL_TRIM_DEPTH, true, new ComboListParameterDataSupplier() {
            private List keys
                    ,
                    values;

            public void init(PanelInstance panelInstance) {
                values = new ArrayList();
                keys = new ArrayList();
                for (int i = 0; i <= 5; i++) {
                    keys.add(String.valueOf(i));
                    values.add(String.valueOf(i));
                }
            }

            public List getValues() {
                return values;
            }

            public List getKeys() {
                return keys;
            }
        }, false));
        addParameter(new HTMLTextAreaParameter(panelProvider, PARAM_SEPARATOR, true, false));
        addParameter(new HTMLTextAreaParameter(panelProvider, PARAM_ELEMENT_TEMPLATE, true, false));
    }

    public int getInitialTrimDepth(Panel panel) {
        return Integer.parseInt(panel.getParameterValue(PARAM_INITIAL_TRIM_DEPTH));
    }

    public String getSeparator(Panel panel) {
        return panel.getParameterValue(PARAM_SEPARATOR);
    }

    public String getTemplate(Panel panel) {
        return panel.getParameterValue(PARAM_ELEMENT_TEMPLATE);
    }
}
