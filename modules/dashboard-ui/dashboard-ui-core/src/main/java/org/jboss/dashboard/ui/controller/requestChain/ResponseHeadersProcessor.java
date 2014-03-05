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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class ResponseHeadersProcessor extends AbstractChainProcessor {

    @Inject @Config("false")
    private boolean useRefreshHeader;

    @Inject @Config("text/html")
    private String responseContentType;

    public boolean processRequest() throws Exception {
        HttpServletRequest request = getHttpRequest();
        HttpServletResponse response = getHttpResponse();
        if (responseContentType != null && !"".equals(responseContentType)) {
            response.setContentType(responseContentType);
            response.setHeader("Content-Type", responseContentType + "; charset=" + HTTPSettings.lookup().getEncoding());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
        response.setHeader("Expires", "Mon, 06 Jan 2003 21:29:02 GMT");
        response.setHeader("Last-Modified", sdf.format(new Date()) + " GMT");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        if (useRefreshHeader) {
            response.setHeader("Refresh", java.lang.String.valueOf(request.getSession().getMaxInactiveInterval() + 61));
        }
        return true;
    }
}
