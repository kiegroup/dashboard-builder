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
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.workspace.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class NavigationCookieProcessor extends AbstractChainProcessor {

    @Inject @Config("true")
    private boolean useCookie;

    @Inject @Config("dashbuilderNavigationPoint")
    private String cookieName;

    @Inject @Config("-")
    private String cookieSeparator;

    private int idsRadix = Character.MAX_RADIX;

    public int getIdsRadix() {
        return idsRadix;
    }

    public void setIdsRadix(int idsRadix) {
        this.idsRadix = idsRadix;
    }

    public boolean isUseCookie() {
        return useCookie;
    }

    public void setUseCookie(boolean useCookie) {
        this.useCookie = useCookie;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieSeparator() {
        return cookieSeparator;
    }

    public void setCookieSeparator(String cookieSeparator) {
        this.cookieSeparator = cookieSeparator;
    }

    public NavigationManager getNavigationManager() {
        return NavigationManager.lookup();
    }

    public boolean processRequest() throws Exception {
        HttpServletRequest request = getHttpRequest();
        HttpServletResponse response = getHttpResponse();
        if (isUseCookie()) {
            Section section = getNavigationManager().getCurrentSection();
            if (section != null) {
                String lang = LocaleManager.lookup().getCurrentLang();
                StringBuffer sb = new StringBuffer();
                sb.append(lang).append(cookieSeparator).append(Long.toString(section.getId().longValue(),idsRadix)).append(cookieSeparator);
                sb.append(section.getWorkspace().getId());
                Cookie navigationCookie = new Cookie(cookieName, sb.toString());
                navigationCookie.setPath(StringUtils.defaultIfEmpty(request.getContextPath(),"/"));
                response.addCookie(navigationCookie);
            }
        }
        return true;
    }
}
