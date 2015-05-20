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
package org.jboss.dashboard.ui.controller;

import java.util.Stack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.ui.controller.responses.RedirectToURLResponse;
import org.jboss.dashboard.ui.controller.responses.ShowScreenResponse;
import org.jboss.dashboard.workspace.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestContext {

    private static Logger log = LoggerFactory.getLogger(RequestContext.class);

    private static ThreadLocal<RequestContext> _threadLocal = new ThreadLocal<RequestContext>();

    public static RequestContext lookup() {
        return _threadLocal.get();
    }

    public static RequestContext init(HttpServletRequest req, HttpServletResponse res) {
        RequestContext ctx = new RequestContext(req, res);
        _threadLocal.set(ctx);
        return ctx;
    }

    public static void destroy() {
        _threadLocal.set(null);
    }

    protected String showPage = "/templates/standard_template.jsp";
    private CommandRequest request;
    private CommandResponse response;
    private String requestURI;
    private StringBuffer consumedRequestURI;
    protected Stack<Panel> panelStack = new Stack<Panel>();

    protected RequestContext(HttpServletRequest req, HttpServletResponse res) {
        request = new CommandRequestImpl(req, res);
        response = new ShowScreenResponse(showPage);
        setURIToBeConsumed(req.getRequestURI().substring(req.getContextPath().length()));
    }

    public CommandRequest getRequest() {
        return request;
    }

    public Panel getActivePanel() {
        return panelStack.isEmpty() ? null: panelStack.peek();
    }

    public Panel activatePanel(Panel panel) {
        if (panel != null) panelStack.push(panel);
        return panel;
    }

    public Panel deactivatePanel(Panel panel) {
        if (panel != null && !panelStack.isEmpty()) {
            Panel last = panelStack.pop();
            if (!last.equals(panel)) {
                deactivatePanel(panel);
            }
        }
        return panel;
    }

    public String getShowPage() {
        return showPage;
    }

    public void setShowPage(String showPage) {
        this.showPage = showPage;
    }

    public CommandResponse getResponse() {
        return response;
    }

    public void setResponse(CommandResponse response) {
        this.response = response;
    }

    protected StringBuffer getConsumedRequestURI() {
        return consumedRequestURI;
    }

    public void setURIToBeConsumed(String requestURI) {
        this.requestURI = StringUtils.replace(requestURI, "//", "/");
        consumedRequestURI = new StringBuffer();
    }

    public String getURIToBeConsumed() {
        return requestURI;
    }

    public void consumeURIPart(String uriPart) {
        consumedRequestURI.append(uriPart);
    }

    public void compareConsumedUri() {
        String consumedUri = StringUtils.replace(consumedRequestURI.toString(), "//", "/");
        if (consumedUri.endsWith("/")) consumedUri = consumedUri.substring(0, consumedUri.length() - 1);
        if (!requestURI.equals(consumedUri)) {
            if (log.isDebugEnabled()) {
                log.debug("Received URI: " + requestURI);
                log.debug("Consumed URI: " + consumedUri);
            }
            if (StringUtils.isEmpty(consumedUri)) {
                log.error("No part of the received URI " + requestURI + " has been consumed. Trying to serve it as good as possible.");
            }
            else if (requestURI.startsWith(consumedUri)) {
                String uriToForward = requestURI.substring(consumedUri.length());
                setResponse(new RedirectToURLResponse(uriToForward, !uriToForward.startsWith(request.getRequestObject().getContextPath())));
                log.debug("Redirecting to static URI: " + uriToForward);
            }
            else {
                log.error("Consumed URI " + consumedUri + " is not even part of request URI: " + requestURI +
                        ". Trying to serve it as good as possible.");
            }
        }
    }
}
