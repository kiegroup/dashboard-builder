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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.RedirectToURLResponse;
import org.jboss.dashboard.ui.taglib.ContextTag;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;

public class FCKEditorHandler extends HandlerFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FCKEditorHandler.class.getName());

    private String[] tabs;
    private String currentTab;
    private String mode = "image";

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(String currentTab) {
        this.currentTab = currentTab;
    }

    public String[] getTabs() {
        return tabs;
    }

    public void setTabs(String[] tabs) {
        this.tabs = tabs;
    }

    public CommandResponse actionChangeTab(CommandRequest request) throws Exception {
        setCurrentTab(request.getRequestObject().getParameter("tab"));
        Map paramsMap = new HashMap();
        paramsMap.put(RedirectionHandler.PARAM_PAGE_TO_REDIRECT, "/fckeditor/custom/FCKSelectImage.jsp");
        String uri = ContextTag.getContextPath(UIServices.lookup().getUrlMarkupGenerator().getMarkup("org.jboss.dashboard.ui.components.RedirectionHandler", "redirectToSection", paramsMap), request.getRequestObject());
        uri = StringEscapeUtils.unescapeHtml(uri);
        return new RedirectToURLResponse(uri, !uri.startsWith(request.getRequestObject().getContextPath()));
    }
}