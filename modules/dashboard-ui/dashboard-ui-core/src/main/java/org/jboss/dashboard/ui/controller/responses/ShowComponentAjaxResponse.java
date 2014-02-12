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
import org.jboss.dashboard.ui.components.UIBeanHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;

/**
 * Response that embeds a component view into the output stream.
 */
public class ShowComponentAjaxResponse extends PanelAjaxResponse {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ShowComponentAjaxResponse.class.getName());

    protected UIBeanHandler component;

    public ShowComponentAjaxResponse(UIBeanHandler component) {
        this.component = component;
    }

    public UIBeanHandler getComponent() {
        return component;
    }

    public boolean execute(CommandRequest cmdReq) throws Exception {
        if (log.isDebugEnabled()) log.debug("ShowComponentAjaxResponse: " + component.getBeanName());
        cmdReq.getResponseObject().setHeader("Content-Encoding", HTTPSettings.lookup().getEncoding());
        cmdReq.getResponseObject().setContentType("text/html;charset=" + HTTPSettings.lookup().getEncoding());
        cmdReq.getRequestObject().getRequestDispatcher("/templates/component_response.jsp").include(cmdReq.getRequestObject(), cmdReq.getResponseObject());
        return true;
    }
}
