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

import javax.servlet.RequestDispatcher;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;

public class FullPanelResponse implements CommandResponse {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FullPanelResponse.class.getName());

    protected Long panelId;

    public FullPanelResponse(Panel panel) {
        this.panelId = panel.getDbid();
    }

    public Panel getPanel() throws Exception {
        if (panelId == null) return null;
        return UIServices.lookup().getPanelsManager().getPanelByDbId(panelId);
    }

    public boolean execute(CommandRequest cmdReq) throws Exception {
        Panel panel = getPanel();
        if (log.isDebugEnabled()) log.debug("FullPanelResponse: " + panel.getFullDescription());

        try {
            RequestContext.lookup().activatePanel(panel);
            RequestDispatcher rd = cmdReq.getRequestObject().getRequestDispatcher("/common/panels/panelContent.jsp");
            rd.include(cmdReq.getRequestObject(), cmdReq.getResponseObject());
            return true;
        } finally {
            RequestContext.lookup().deactivatePanel(panel);
        }
    }
}
