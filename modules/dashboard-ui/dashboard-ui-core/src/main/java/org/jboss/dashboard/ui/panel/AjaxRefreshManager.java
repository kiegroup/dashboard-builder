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
package org.jboss.dashboard.ui.panel;

import org.jboss.dashboard.factory.BasicFactoryElement;
import org.jboss.dashboard.factory.Factory;

import java.util.List;
import java.util.ArrayList;

public class AjaxRefreshManager extends BasicFactoryElement {
    
    public static AjaxRefreshManager lookup() {
        return (AjaxRefreshManager) Factory.lookup("org.jboss.dashboard.ui.panel.AjaxRefreshManager");
    }
    
    public static final String FORM_IDENTIFIER_PREFFIX="refreshFormForPanel";
    protected int maxAjaxRequests;

    List panelIdsToRefresh = new ArrayList();

    public List getPanelIdsToRefresh() {
        return panelIdsToRefresh;
    }

    public void setPanelIdsToRefresh(List panelIdsToRefresh) {
        this.panelIdsToRefresh = panelIdsToRefresh;
    }

    public int getMaxAjaxRequests() {
        return maxAjaxRequests;
    }

    public void setMaxAjaxRequests(int maxAjaxRequests) {
        this.maxAjaxRequests = maxAjaxRequests;
    }
}
