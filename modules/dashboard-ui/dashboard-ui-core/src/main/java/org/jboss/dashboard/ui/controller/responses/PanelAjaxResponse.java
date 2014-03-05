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

import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Panel;

public abstract class PanelAjaxResponse implements CommandResponse {

    protected String commonRefreshPanelsPage = "/common/panels/commonPanelHeader.jsp";
    protected String beforePanelsPage = "/common/panels/beforePanel.jsp";
    protected String afterPanelsPage = "/common/panels/afterPanel.jsp";

    /**
     * Attempt to convert a response to an AjaxResponse. If conversion fails, returns null
     *
     * @param panel  Panel that sends the response
     * @param response Response the panel sends.
     * @return an PanelAjaxResponse. If conversion fails, returns null.
     */
    public static PanelAjaxResponse getEquivalentAjaxResponse(Panel panel, CommandResponse response) {
        if (response instanceof PanelAjaxResponse) {
            return (PanelAjaxResponse) response;
        }
        if (response instanceof ShowPanelPage) {
            ShowPanelPage res = (ShowPanelPage) response;
            String page = res.getPageId();
            page = page == null ? panel.getPanelSession().getCurrentPageId() : page;
            return new FullPanelAjaxResponse(panel, page);
        }
        return new FullPanelAjaxResponse(panel, panel.getPanelSession().getCurrentPageId());
    }
}
