/*
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.ui.HTTPSettings;

/**
 * Security filter to protect application from XSS and frame busting attacks.
 */
public class SecureHeaderFilter implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain chain) throws IOException, ServletException {

        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        applyHeaders(response);

        chain.doFilter(request, response);
    }

    public static void applyHeaders(HttpServletResponse response) {
        HTTPSettings httpSettings = HTTPSettings.lookup();
        applyHeaders(httpSettings, response);
    }

    public static void applyHeaders(HTTPSettings httpSettings, HttpServletResponse response) {
        if (httpSettings.isXSSProtectionEnabled()) {
            String mode = httpSettings.isXSSProtectionBlock() ? "1; mode=block" : "1";
            response.setHeader("X-XSS-Protection", mode);
        }
        if (!StringUtils.isBlank(httpSettings.getXFrameOptions())) {
            response.setHeader("X-FRAME-OPTIONS", httpSettings.getXFrameOptions());
        }
    }
}
