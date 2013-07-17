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
package org.jboss.dashboard.ui.controller.responses;

import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.workspace.Panel;

/**
 * Response that shows a panel page.
 */
public class ShowPanelPage extends ShowCurrentScreenResponse {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ShowPanelPage.class.getName());

    private Panel panel = null;
    private String pageId = null;

    /**
     * Show specified panel page
     *
     * @param panel
     * @param req
     * @param pageId
     */
    public ShowPanelPage(Panel panel, CommandRequest req, String pageId) {
        SessionManager.getPanelSession(panel).setCurrentPageId(pageId);
        this.panel = panel;
        this.pageId = pageId;
    }

    /**
     * Show current Screen constructor
     */
    public ShowPanelPage() {

    }

    public String getPageId() {
        return pageId;
    }

    public boolean execute(CommandRequest cmdReq) throws Exception {
        if (log.isDebugEnabled()) log.debug("ShowPanelPage: " + panel.getFullDescription());
        return super.execute(cmdReq);
    }

    public String toString() {
        return this.getClass().getName() + " -->" + pageId;
    }
}
