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
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;

import javax.servlet.RequestDispatcher;

/**
 *
 */
public class ShowPopupPage implements CommandResponse {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ShowPopupPage.class.getName());
    protected String page;
    protected String contentType = "text/html";

    public ShowPopupPage(String page) {
        this.page = page;
    }

    public ShowPopupPage(String page, String contentType) {
        this.contentType = contentType;
        this.page = page;
    }

    /**
     * Executes the response. It typically will be one of the response types
     * that are provided in the org.jboss.dashboard.ui.controller.responses package.
     *
     * @param cmdReq Object encapsulating the request information.
     * @return boolean if the execution has been successfuly executed, false otherwise.
     */
    public boolean execute(CommandRequest cmdReq) throws Exception {
        cmdReq.getResponseObject().setHeader("Content-Encoding", HTTPSettings.lookup().getEncoding());
        cmdReq.getResponseObject().setContentType(contentType + ";charset=" + HTTPSettings.lookup().getEncoding());
        RequestDispatcher rd = cmdReq.getRequestObject().getRequestDispatcher(page);
        rd.include(cmdReq.getRequestObject(), cmdReq.getResponseObject());
        return true;
    }
}
