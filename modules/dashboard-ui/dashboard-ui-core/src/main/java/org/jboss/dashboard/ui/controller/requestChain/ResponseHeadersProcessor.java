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

    @Inject @Config("false")
    private boolean xssProtectionEnabled;

    @Inject @Config("false")
    private boolean xssProtectionBlock;

    /** There are three possible values for the X-Frame-Options headers:<ul>
     *  <li>DENY, which prevents any domain from framing the content.</li>
     *  <li>SAMEORIGIN, which only allows the current site to frame the content.</li>
     *  <li>ALLOW-FROM uri, which permits the specified 'uri' to frame this page. (e.g., ALLOW-FROM http://www.example.com) The ALLOW-FROM option is a relatively recent addition (circa 2012) and may not be supported by all browsers yet. BE CAREFUL ABOUT DEPENDING ON ALLOW-FROM. If you apply it and the browser does not support it, then you will have NO clickjacking defense in place.</li>
     * </ul>
     */
    @Inject @Config("")
    private String xFrameOptions;

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

        if (xssProtectionEnabled) {
            if (xssProtectionBlock) response.setHeader("X-XSS-Protection", "1; mode=block");
            else response.setHeader("X-XSS-Protection", "1");
        }
        if (!StringUtils.isBlank(xFrameOptions)) {
            response.setHeader("X-FRAME-OPTIONS", xFrameOptions);
        }
        if (useRefreshHeader) {
            response.setHeader("Refresh", java.lang.String.valueOf(request.getSession().getMaxInactiveInterval() + 61));
        }
        return true;
    }
}
