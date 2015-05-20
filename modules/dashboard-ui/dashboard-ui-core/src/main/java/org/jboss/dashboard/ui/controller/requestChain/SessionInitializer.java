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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.controller.responses.RedirectToURLResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.StringTokenizer;

@ApplicationScoped
public class SessionInitializer extends AbstractChainProcessor {

    private final static String SESSION_ATTRIBUTE_INITIALIZED = "dashbuilder.initialized";

    @Inject
    private transient Logger log;

    @Inject
    private NavigationCookieProcessor navigationCookieProcessor;

    @Inject
    private SessionManager sessionManager;

    @Inject
    private LocaleManager localeManager;

    public static boolean isNewSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        return !"true".equals(session.getAttribute(SESSION_ATTRIBUTE_INITIALIZED));
    }

    protected void initSession(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Initializing new session.");
        request.getSession().setAttribute(SESSION_ATTRIBUTE_INITIALIZED, "true");

        // Catch the user preferred language.
        PreferredLocale preferredLocale =  getPreferredLocale(request);
        localeManager.setCurrentLocale(preferredLocale.asLocale());
    }

    public boolean processRequest() throws Exception {
        RequestContext requestContext= getRequestContext();
        HttpServletRequest request = getHttpRequest();
        HttpServletResponse response = getHttpResponse();
        HttpSession session = request.getSession(true);

        // Catch new sessions
        if (isNewSession(request)) {
            initSession(request, response);
            return true;
        }

        // Check session expiration
        if (request.getRequestedSessionId() != null && !request.getRequestedSessionId().equals(session.getId())) {
            log.debug("Session expiration detected.");
            requestContext.setResponse(new RedirectToURLResponse(getExpirationRecoveryURL(request)));
            requestContext.consumeURIPart(requestContext.getURIToBeConsumed());
            return false;
        }
        return true;
    }

    protected String getExpirationRecoveryURL(HttpServletRequest request) {
        //Parse cookie
        String[] cookieValues = getParsedNavigationCookieValues(getNavigationCookieValue(request));
        String defaultUrl = StringUtils.defaultString(request.getRequestURI());

        if (cookieValues == null) { // No cookie => Nothing to do!
            return defaultUrl;
        }

        String lang = cookieValues[0];
        String sectionURL = String.valueOf(Long.parseLong(cookieValues[1], navigationCookieProcessor.getIdsRadix()));
        String workspaceURL = cookieValues[2];

        // Set the lang
        LocaleManager.lookup().setCurrentLang(lang);

        String contextPath = StringUtils.defaultString(request.getContextPath());
        while (contextPath.endsWith("/"))
            contextPath = contextPath.substring(0, contextPath.length() - 1);

        if (!defaultUrl.startsWith(contextPath + FriendlyUrlProcessor.FRIENDLY_MAPPING)) {
            // URL is not friendly, use cookie to construct it!
            defaultUrl = contextPath + FriendlyUrlProcessor.FRIENDLY_MAPPING + "/" + workspaceURL + "/" + sectionURL;
        }

        return defaultUrl + "?" + request.getQueryString();
    }

    protected String getNavigationCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (navigationCookieProcessor.getCookieName().equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        return null;
    }

    protected String[] getParsedNavigationCookieValues(String cookieValue) {
        if (cookieValue == null)
            return null;
        String[] s = new String[3];
        int index = 0;
        StringTokenizer strtk = new StringTokenizer(cookieValue, navigationCookieProcessor.getCookieSeparator());
        while (strtk.hasMoreTokens() && index < s.length) {
            s[index++] = strtk.nextToken();
        }
        if (index != 3) //Incorrect cookie value
            return null;
        return s;
    }

    public static PreferredLocale getPreferredLocale(HttpServletRequest request) {
        if (request == null) return null;

        String headerLang = request.getHeader("Accept-Language"); // f.i: "en-GB, sv;q=0.7, en;q=0.9"

        PreferredLocale result = null;
        if (headerLang != null) {
            // Return the locale with the highest quality.
            String[] headerLocales = StringUtils.split(headerLang, ",");
            if (headerLocales != null) {
                for (int i = 0; i < headerLocales.length; i++) {
                    String headerLocale = headerLocales[i];
                    PreferredLocale preferredLocale = new PreferredLocale(headerLocale);
                    if (result == null || preferredLocale.quality > result.quality) {
                        result = preferredLocale;
                    }
                }
            }
        }
        return result;
    }

    public static class PreferredLocale {

        String language = null;
        String country = null;
        float quality = 1;

        public PreferredLocale(String locale) {
            String[] temp = StringUtils.split(locale, ";");
            if (temp.length == 1) {
                parseLocale(temp[0]);
            }
            else if (temp.length == 2) {
                parseLocale(temp[0]);
                parseQuality(temp[1]);
            }
        }

        public Locale asLocale() {
            if (country == null) return new Locale(language);
            return new Locale(language, country);
        }

        protected void parseLocale(String locale) {
            String[] temp = StringUtils.split(locale, "-");
            this.language = temp[0];
            if (temp.length == 2) {
                this.country = temp[1];
            }
        }

        protected void parseQuality(String q) {
            String[] temp = StringUtils.split(q, "=");
            if (temp.length == 2) {
                this.quality = Float.parseFloat(temp[1]);
            }
        }
    }
}
