/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui.controller.requestChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.RequestContext;

/**
 * Base class for implementing request chain processors.
 */
public abstract class AbstractChainProcessor implements RequestChainProcessor {

    public RequestContext getRequestContext() {
        return RequestContext.lookup();
    }

    public HttpServletRequest getHttpRequest() {
        return getRequestContext().getRequest().getRequestObject();
    }

    public HttpServletResponse getHttpResponse() {
        return getRequestContext().getRequest().getResponseObject();
    }

    public CommandRequest getRequest() {
        return getRequestContext().getRequest();
    }

    public CommandResponse getResponse() {
        return getRequestContext().getResponse();
    }

    public void setResponse(CommandResponse response) {
        getRequestContext().setResponse(response);
    }
}
