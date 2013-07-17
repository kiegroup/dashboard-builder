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
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Parameters;
import org.hibernate.Session;

public class ShowJspInsidePanelContextResponse extends ShowScreenResponse implements CommandResponse {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ShowJspInsidePanelContextResponse.class.getName());

    private Long panelId;

    public ShowJspInsidePanelContextResponse(String jspRoute) {
        super(jspRoute);
        init();
    }

    protected Panel getPanel() throws Exception {
        final Panel[] panel = new Panel[]{null};
        if (panelId != null)
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    panel[0] = (Panel) session.load(Panel.class, panelId);
                }
            }.execute();
        return panel[0];
    }

    private void init() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Panel currentPanel = (Panel) ctx.getRequest().getRequestObject().getAttribute(Parameters.RENDER_PANEL);
        if (currentPanel != null) {
            panelId = currentPanel.getDbid();
        }
    }

    public boolean execute(CommandRequest cmdReq) throws Exception {
        Panel panel = getPanel();
        if (log.isDebugEnabled()) log.debug("ShowJspInsidePanelContextResponse: " + panel.getFullDescription());
        cmdReq.getRequestObject().setAttribute(Parameters.RENDER_PANEL, panel);
        boolean b = super.execute(cmdReq);
        cmdReq.getRequestObject().removeAttribute(Parameters.RENDER_PANEL);
        return b;
    }
}
