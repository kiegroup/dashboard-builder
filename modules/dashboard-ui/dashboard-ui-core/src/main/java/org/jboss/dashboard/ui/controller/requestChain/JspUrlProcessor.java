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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.controller.responses.ShowScreenResponse;

/**
 * Process JSP forward requests.
 */
public class JspUrlProcessor extends AbstractChainProcessor {

    public static final String JSP_MAPPING = "/" + URLMarkupGenerator.JSP_PREFIX;

    public boolean processRequest() throws Exception {
        HttpServletRequest request = getHttpRequest();
        String servletPath = request.getServletPath();
        RequestContext requestContext = RequestContext.lookup();

        // No JSP mapping -> nothing to do.
        if (!servletPath.startsWith(JSP_MAPPING)) return true;

        String contextPath = request.getContextPath();
        requestContext.consumeURIPart(JSP_MAPPING);
        String requestUri = request.getRequestURI();
        String relativeUri = requestUri.substring(contextPath == null ? 0 : (contextPath.length()));
        relativeUri = relativeUri.substring(servletPath == null ? 0 : (servletPath.length()));

        // Empty URI -> nothing to do.
        if (StringUtils.isBlank(relativeUri)) return true;

        // Set the JSP as the response.
        int paramIndex = relativeUri.indexOf("?");
        String jsp = paramIndex != -1 ? relativeUri.substring(0, paramIndex) : relativeUri;
        requestContext.consumeURIPart(jsp);
        requestContext.setResponse(new ShowScreenResponse(jsp));
        return true;
    }
}
