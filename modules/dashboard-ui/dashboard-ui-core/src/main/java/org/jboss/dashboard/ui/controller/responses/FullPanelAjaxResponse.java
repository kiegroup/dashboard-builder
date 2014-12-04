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

import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelSession;

import javax.servlet.RequestDispatcher;

/**
 * Response that includes the full panel area including the buttons bar
 */
public class FullPanelAjaxResponse extends PanelAjaxResponse {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FullPanelAjaxResponse.class.getName());

    protected Long panelId;
    protected String page;

    public FullPanelAjaxResponse(Panel panel, String page) {
        this.panelId = panel.getDbid();
        this.page = page;
    }

    public Panel getPanel() throws Exception {
        if (panelId == null) return null;
        return UIServices.lookup().getPanelsManager().getPanelByDbId(panelId);
    }

    public boolean execute(CommandRequest cmdReq) throws Exception {
        Panel panel = getPanel();
        PanelSession pSession = panel.getPanelSession();
        pSession.setCurrentPageId(page);
        if (log.isDebugEnabled()) log.debug("FullPanelAjaxResponse: " + panel.getFullDescription());
        try {
            RequestContext.lookup().activatePanel(panel);

            cmdReq.getResponseObject().setHeader("Content-Encoding", HTTPSettings.lookup().getEncoding());
            cmdReq.getResponseObject().setContentType("text/html;charset=" + HTTPSettings.lookup().getEncoding());
            RequestDispatcher rd[] = {
                    cmdReq.getRequestObject().getRequestDispatcher(commonRefreshPanelsPage),
                    cmdReq.getRequestObject().getRequestDispatcher(beforePanelsPage),
                    cmdReq.getRequestObject().getRequestDispatcher(panel.getProvider().getPage(page)),
                    cmdReq.getRequestObject().getRequestDispatcher(afterPanelsPage)};
            panel.getProvider().getDriver().fireBeforeRenderPanel(panel, cmdReq.getRequestObject(), cmdReq.getResponseObject());
            for (int i = 0; i < rd.length; i++) {
                RequestDispatcher requestDispatcher = rd[i];
                requestDispatcher.include(cmdReq.getRequestObject(), cmdReq.getResponseObject());
                cmdReq.getResponseObject().flushBuffer();
            }
            panel.getProvider().getDriver().fireAfterRenderPanel(panel, cmdReq.getRequestObject(), cmdReq.getResponseObject());
            return true;
        } finally {
            RequestContext.lookup().deactivatePanel(panel);
        }
    }
}
