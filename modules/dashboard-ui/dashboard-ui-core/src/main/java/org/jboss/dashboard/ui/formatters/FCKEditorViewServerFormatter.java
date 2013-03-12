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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.components.FCKEditorHandler;
import org.apache.commons.lang.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FCKEditorViewServerFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FCKEditorViewServerFormatter.class.getName());

    private FCKEditorHandler FCKEditorHandler;

    public FCKEditorHandler getFCKEditorHandler() {
        return FCKEditorHandler;
    }

    public void setFCKEditorHandler(FCKEditorHandler FCKEditorHandler) {
        this.FCKEditorHandler = FCKEditorHandler;
    }


    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        String[] tabs = getFCKEditorHandler().getTabs();
        String currentTab = getFCKEditorHandler().getCurrentTab();
        if (!ArrayUtils.contains(tabs, currentTab)) {
            getFCKEditorHandler().setCurrentTab(currentTab = tabs[0]);
        }

        renderFragment("outputTabsStart");
        for (int i = 0; i < tabs.length; i++) {
            String tab = tabs[i];
            boolean current = currentTab.equals(tab);
            setAttribute("tabName", tab);
            setAttribute("current", current);
            renderFragment("outputTab");
        }
        renderFragment("outputTabsEnd");

        renderFragment("beforeCurrentPage");
        includePage((String) getParameter("page-"+currentTab));
        renderFragment("afterCurrentPage");
    }
}
