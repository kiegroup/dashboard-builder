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
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Parameters;
import org.hibernate.Session;

import javax.servlet.RequestDispatcher;

/**
 *
 */
public class ShowPopupPanelPage extends PanelAjaxResponse implements CommandResponse {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ShowPopupPanelPage.class.getName());
    protected Long panelId;
    protected String page;
    protected String jsp;

    public ShowPopupPanelPage(Panel panel, String page) {
        this.panelId = panel.getDbid();
        this.page = page;
        jsp = panel.getProvider().getPage(page);
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


    /**
     * Executes the response. It typically will be one of the response types
     * that are provided in the org.jboss.dashboard.ui.controller.responses package.
     *
     * @param cmdReq Object encapsulating the request information.
     * @return boolean if the execution has been successfuly executed, false otherwise.
     */
    public boolean execute(CommandRequest cmdReq) throws Exception {
        Panel panel = getPanel();
        if (log.isDebugEnabled()) log.debug("ShowPopupPanelPage: " + panel.getFullDescription());
        cmdReq.getResponseObject().setHeader("Content-Encoding", HTTPSettings.lookup().getEncoding());
        cmdReq.getResponseObject().setContentType("text/html;charset=" + HTTPSettings.lookup().getEncoding());
        cmdReq.getRequestObject().setAttribute(Parameters.RENDER_PANEL, panel);
        SessionManager.setCurrentPanel(panel);
        RequestDispatcher rd = cmdReq.getRequestObject().getRequestDispatcher(jsp);
        RequestDispatcher rd1 = cmdReq.getRequestObject().getRequestDispatcher(commonRefreshPanelsPage);
        panel.getProvider().getDriver().fireBeforeRenderPanel(panel, cmdReq.getRequestObject(), cmdReq.getResponseObject());
        rd.include(cmdReq.getRequestObject(), cmdReq.getResponseObject());
        rd1.include(cmdReq.getRequestObject(), cmdReq.getResponseObject());
        panel.getProvider().getDriver().fireAfterRenderPanel(panel, cmdReq.getRequestObject(), cmdReq.getResponseObject());
        return true;
    }
}
