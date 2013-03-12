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

import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;

/**
 * Forwards to a resource
 */
public class ForwardResponse implements CommandResponse {

    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ForwardResponse.class.getName());

    private String resource = null;

    public ForwardResponse(String resource) {
        this.resource = resource;
    }

    /**
     * Executes the response.
     *
     * @param cmdReq Object encapsulating the request information.
     * @return boolean if the execution has been successfuly executed, false otherwise.
     */
    public boolean execute(CommandRequest cmdReq) throws Exception {
        if (log.isDebugEnabled()) log.debug("ForwardResponse: " + resource);
        
        cmdReq.getRequestObject().getRequestDispatcher(resource).forward(cmdReq.getRequestObject(), cmdReq.getResponseObject());
        return true;
    }
}
