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

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.ui.HTTPSettings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.servlet.http.HttpServletResponse;

public class ResponseHeadersProcessor extends RequestChainProcessor {

    private boolean useRefreshHeader = false;
    private String responseContentType = "text/html";

    protected boolean processRequest() {
        HttpServletResponse response = getResponse();
        if (responseContentType != null && !"".equals(responseContentType)) {
            response.setContentType(responseContentType);
            response.setHeader("Content-Type", responseContentType + "; charset=" + HTTPSettings.lookup().getEncoding());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
        response.setHeader("Expires", "Mon, 06 Jan 2003 21:29:02 GMT");
        response.setHeader("Last-Modified", sdf.format(new Date()) + " GMT");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        HTTPSettings httpSettings = HTTPSettings.lookup();
        if (httpSettings.isXSSProtectionEnabled()) {
            if (httpSettings.isXSSProtectionBlock()) response.setHeader("X-XSS-Protection", "1; mode=block");
            else response.setHeader("X-XSS-Protection", "1");
        }
        if (!StringUtils.isBlank(httpSettings.getXFrameOptions())) {
            response.setHeader("X-FRAME-OPTIONS", httpSettings.getXFrameOptions());
        }
        if (useRefreshHeader) {
            response.setHeader("Refresh", java.lang.String.valueOf(getRequest().getSession().getMaxInactiveInterval() + 61));
        }
        return true;
    }
}