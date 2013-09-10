/**
 * Copyright (C) 2013 JBoss Inc
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
package org.jboss.dashboard.ui.controller.requestChain;

import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.ui.components.DashboardHandler;

/**
 * This is a request chain processor for dashbuilder.
 * It reads some request parameters and perform operations.
 */
public class DashboardProcessor extends RequestChainProcessor {

    private static final String PARAM_REFRESH = "refresh";

    @Override
    protected boolean processRequest() throws Exception {
        Dashboard dashboard = DashboardHandler.lookup().getCurrentDashboard();
        if (dashboard != null) {
            String refresh = getRequest().getParameter(PARAM_REFRESH);
            boolean isRefresh = Boolean.parseBoolean(refresh);
            if (isRefresh) dashboard.refresh();
        }

        return true;
    }
}
