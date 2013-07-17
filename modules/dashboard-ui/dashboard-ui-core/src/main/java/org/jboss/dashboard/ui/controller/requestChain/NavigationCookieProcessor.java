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
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.workspace.Section;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

public class NavigationCookieProcessor extends RequestChainProcessor {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NavigationCookieProcessor.class.getName());

    private boolean useCookie = true;
    private String cookieName = "dashbuilderNavigationPoint";
    private String cookieSeparator = "-";
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

    protected boolean processRequest() throws Exception {
        if (isUseCookie()) {
            Section section = getNavigationManager().getCurrentSection();
            if (section != null) {
                String lang = LocaleManager.lookup().getCurrentLang();
                StringBuffer sb = new StringBuffer();
                sb.append(lang).append(cookieSeparator).append(Long.toString(section.getId().longValue(),idsRadix)).append(cookieSeparator);
                sb.append(section.getWorkspace().getId());
                Cookie navigationCookie = new Cookie(cookieName, sb.toString());
                navigationCookie.setPath(StringUtils.defaultIfEmpty(getRequest().getContextPath(),"/"));
                getResponse().addCookie(navigationCookie);
            }
        }
        return true;
    }
}
