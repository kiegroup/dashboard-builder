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
package org.jboss.dashboard.ui.controller.requestChain;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.ControllerStatus;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.ui.components.RedirectionHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.ControllerServletHelper;
import org.jboss.dashboard.ui.controller.RequestMultipartWrapper;
import org.jboss.dashboard.ui.controller.SessionTmpDirFactory;
import org.jboss.dashboard.ui.controller.responses.RedirectToURLResponse;
import org.jboss.dashboard.ui.taglib.ContextTag;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;

/**
 * Handles multipart requests, building wrapper around the request object
 * in order to support files uploading.
 */
@ApplicationScoped
public class MultipartProcessor implements RequestChainProcessor {

    @Inject
    private transient Logger log;

    @Inject @Config("fileTooBig.jsp")
    private String errorRedirectPage;

    public boolean processRequest(CommandRequest req) throws Exception {
        HttpServletRequest request = req.getRequestObject();
        HttpServletResponse response = req.getResponseObject();
        ControllerStatus controllerStatus = ControllerStatus.lookup();
        String contentType = request.getContentType();
        String method = request.getMethod();
        HTTPSettings webSettings = HTTPSettings.lookup();
        if ("POST".equalsIgnoreCase(method) && contentType != null && contentType.startsWith("multipart") && webSettings.isMultipartProcessing()) {
            log.debug("Found multipart request. Building wrapper");

            String tmpDir = SessionTmpDirFactory.getTmpDir(request);
            if (log.isDebugEnabled())
                log.debug("Extracting to dir " + tmpDir);

            int maxSize = webSettings.getMaxPostSize() * 1024;
            if (log.isDebugEnabled()) {
                log.debug("Max post size is : " + maxSize + " bytes");
                log.debug("Encoding is: " + webSettings.getEncoding());
            }

            try {
                RequestMultipartWrapper wrap = new RequestMultipartWrapper(request, tmpDir, maxSize, webSettings.getEncoding());
                log.debug("Multipart request parsed: ");
                log.debug("getting files from request");
                ControllerServletHelper.lookup().updateRequestContext(wrap, response);
            }
            catch (IOException ioe) {
                log.warn("IOException processing multipart ", ioe);
                log.warn("Invalid " + method + ": URL=" + request.getRequestURL() + ". QueryString=" + request.getQueryString());
                URLMarkupGenerator markupGenerator = UIServices.lookup().getUrlMarkupGenerator();
                if (markupGenerator != null) {
                    Map paramsMap = new HashMap();
                    paramsMap.put(RedirectionHandler.PARAM_PAGE_TO_REDIRECT, errorRedirectPage);
                    String uri = ContextTag.getContextPath(markupGenerator.getMarkup("org.jboss.dashboard.ui.components.RedirectionHandler", "redirectToSection", paramsMap), request);
                    uri = StringEscapeUtils.unescapeHtml(uri);
                    controllerStatus.setResponse(new RedirectToURLResponse(uri, !uri.startsWith(request.getContextPath())));
                }
                return false;
            }
        }
        return true;
    }
}
