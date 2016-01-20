/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui.panel.jspincluder;

import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.parameters.StringParameter;
import org.jboss.dashboard.workspace.Panel;

public class JspIncluderDriver extends PanelDriver {

    public static final String PARAMETER_JSP = "jsp";

    public String getJspPath(Panel panel) {
        return panel.getParameterValue(PARAMETER_JSP);
    }

    public void init(PanelProvider provider) throws Exception {
        super.init(provider);
        addParameter(new StringParameter(provider, PARAMETER_JSP, true, false));
    }
}
