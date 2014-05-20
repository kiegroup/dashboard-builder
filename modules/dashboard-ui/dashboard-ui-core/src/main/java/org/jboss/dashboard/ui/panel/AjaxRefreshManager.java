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

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

import java.util.List;
import java.util.ArrayList;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Manager which handles panels AJAX refresh requests.
 */
@RequestScoped
public class AjaxRefreshManager {

    public static final String FORM_IDENTIFIER_PREFFIX="refreshFormForPanel";

    public static AjaxRefreshManager lookup() {
        return CDIBeanLocator.getBeanByType(AjaxRefreshManager.class);
    }

    @Inject @Config("50")
    protected int maxAjaxRequests;

    List<Long> panelIdsToRefresh = new ArrayList<Long>();

    public List<Long> getPanelIdsToRefresh() {
        return panelIdsToRefresh;
    }

    public void setPanelIdsToRefresh(List<Long> panelIdsToRefresh) {
        this.panelIdsToRefresh = panelIdsToRefresh;
    }

    public int getMaxAjaxRequests() {
        return maxAjaxRequests;
    }

    public void setMaxAjaxRequests(int maxAjaxRequests) {
        this.maxAjaxRequests = maxAjaxRequests;
    }
}
