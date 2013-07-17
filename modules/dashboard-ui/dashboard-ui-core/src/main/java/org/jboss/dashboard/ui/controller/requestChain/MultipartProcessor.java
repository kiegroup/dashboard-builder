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

import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.ui.components.RedirectionHandler;
import org.jboss.dashboard.ui.controller.ControllerServletHelper;
import org.jboss.dashboard.ui.controller.RequestMultipartWrapper;
import org.jboss.dashboard.ui.controller.SessionTmpDirFactory;
import org.jboss.dashboard.ui.controller.responses.RedirectToURLResponse;
import org.jboss.dashboard.ui.taglib.ContextTag;
import org.jboss.dashboard.factory.Factory;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Handles multipart requests, building wrapper around the request object
 * in order to support files uploading.
 */
public class MultipartProcessor extends RequestChainProcessor {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MultipartProcessor.class.getName());

    private String errorRedirectPage = "fileTooBig.jsp";

    public String getErrorRedirectPage() {
        return errorRedirectPage;
    }

    public void setErrorRedirectPage(String errorRedirectPage) {
        this.errorRedirectPage = errorRedirectPage;
    }

    /**
     * Make required processing of request.
     *
     * @return true if processing must continue, false otherwise.
     */
    protected boolean processRequest() throws Exception {
        /*
        *  Hack for handling multipart requests.
        */
        String contentType = getRequest().getContentType();
        String method = getRequest().getMethod();
        HTTPSettings webSettings = HTTPSettings.lookup();
        if ("POST".equalsIgnoreCase(method) && contentType != null && contentType.startsWith("multipart") && webSettings.isMultipartProcessing()) {
            log.debug("Found multipart request. Building wrapper");

            String tmpDir = SessionTmpDirFactory.getTmpDir(getRequest());
            if (log.isDebugEnabled())
                log.debug("Extracting to dir " + tmpDir);

            int maxSize = webSettings.getMaxPostSize() * 1024;
            if (log.isDebugEnabled()) {
                log.debug("Max post size is : " + maxSize + " bytes");
                log.debug("Encoding is: " + webSettings.getEncoding());
            }

            try {
                RequestMultipartWrapper wrap = new RequestMultipartWrapper(getRequest(), tmpDir, maxSize, webSettings.getEncoding());
                log.debug("Multipart request parsed: ");
                log.debug("getting files from request");
                ControllerServletHelper.lookup().initThreadLocal(wrap, getResponse());
            }
            catch (IOException ioe) {
                log.warn("IOException processing multipart ", ioe);
                log.warn("Invalid " + method + ": URL=" + getRequest().getRequestURL() + ". QueryString=" + getRequest().getQueryString());
                URLMarkupGenerator markupGenerator = UIServices.lookup().getUrlMarkupGenerator();
                if (markupGenerator != null) {
                    Map paramsMap = new HashMap();
                    paramsMap.put(RedirectionHandler.PARAM_PAGE_TO_REDIRECT, getErrorRedirectPage());
                    String uri = ContextTag.getContextPath(markupGenerator.getMarkup("org.jboss.dashboard.ui.components.RedirectionHandler", "redirectToSection", paramsMap), getRequest());
                    uri = StringEscapeUtils.unescapeHtml(uri);
                    getControllerStatus().setResponse(new RedirectToURLResponse(uri, !uri.startsWith(getRequest().getContextPath())));
                }
                return false;
            }
        }
        return true;
    }
}
