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

import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelSession;
import org.jboss.dashboard.workspace.Parameters;
import org.hibernate.Session;

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

    protected Panel getPanel() throws Exception {
        final Panel[] panel = new Panel[]{null};
        new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                panel[0] = (Panel) session.load(Panel.class, panelId);
            }
        }.execute();
        return panel[0];
    }


    public boolean execute(CommandRequest cmdReq) throws Exception {
        Panel panel = getPanel();
        if (log.isDebugEnabled()) log.debug("FullPanelAjaxResponse: " + panel.getFullDescription());
        cmdReq.getResponseObject().setHeader("Content-Encoding", HTTPSettings.lookup().getEncoding());
        cmdReq.getResponseObject().setContentType("text/html;charset=" + HTTPSettings.lookup().getEncoding());
        cmdReq.getRequestObject().setAttribute(Parameters.RENDER_PANEL, panel);
        PanelSession pSession = SessionManager.getPanelSession(panel);
        pSession.setCurrentPageId(page);
        RequestDispatcher rd[] = {
                cmdReq.getRequestObject().getRequestDispatcher(commonRefreshPanelsPage),
                cmdReq.getRequestObject().getRequestDispatcher(beforePanelsPage),
                cmdReq.getRequestObject().getRequestDispatcher(panel.getProvider().getPage(page)),
                cmdReq.getRequestObject().getRequestDispatcher(afterPanelsPage)};
        panel.getProvider().getDriver().fireBeforeRenderPanel(panel, cmdReq.getRequestObject(), cmdReq.getResponseObject());
        for (int i = 0; i < rd.length; i++) {
            RequestDispatcher requestDispatcher = rd[i];
            requestDispatcher.include(cmdReq.getRequestObject(), cmdReq.getResponseObject());
        }
        panel.getProvider().getDriver().fireAfterRenderPanel(panel, cmdReq.getRequestObject(), cmdReq.getResponseObject());
        return true;

    }
}
