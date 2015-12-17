/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui.panel.navigation.treeMenu;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.parameters.BooleanParameter;
import org.jboss.dashboard.ui.panel.parameters.HTMLTextAreaParameter;
import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Section;
import org.slf4j.Logger;

import java.util.*;
import javax.inject.Inject;

public class TreeMenuDriver extends PanelDriver {

    @Inject
    private transient Logger log;

    public static final String PARAM_PAGE_TEMPLATE_1 = "PageTemplate1";
    public static final String PARAM_CURRENT_PAGE_TEMPLATE_1 = "currentPageTemplate1";
    public static final String PARAM_PAGE_TEMPLATE_2 = "PageTemplate2";
    public static final String PARAM_CURRENT_PAGE_TEMPLATE_2 = "currentPageTemplate2";
    public static final String PARAM_PAGE_TEMPLATE_3 = "PageTemplate3";
    public static final String PARAM_CURRENT_PAGE_TEMPLATE_3 = "currentPageTemplate3";
    public static final String PARAM_TABULATION = "tabulation";
    public static final String PARAM_OPEN_ALL = "openAllSections";
    public static final String PARAM_MARK_OPENED_SECTIONS = "markOppenedSections";

    public static final String TOKEN_LINK = "{LINK}";
    public static final String TOKEN_PAGE_NAME = "{PAGE}";
    public static final String TOKEN_PAGE_URL = "{URL}";
    public static final String TOKEN_PAGE_DBID = "{DBID}";


    public void init(PanelProvider panelProvider) throws Exception {
        super.init(panelProvider);
        addParameter(new HTMLTextAreaParameter(panelProvider, PARAM_TABULATION, true, false));
        addParameter(new HTMLTextAreaParameter(panelProvider, PARAM_PAGE_TEMPLATE_1, true, false));
        addParameter(new HTMLTextAreaParameter(panelProvider, PARAM_CURRENT_PAGE_TEMPLATE_1, true, false));
        addParameter(new HTMLTextAreaParameter(panelProvider, PARAM_PAGE_TEMPLATE_2, false, false));
        addParameter(new HTMLTextAreaParameter(panelProvider, PARAM_CURRENT_PAGE_TEMPLATE_2, false, false));
        addParameter(new HTMLTextAreaParameter(panelProvider, PARAM_PAGE_TEMPLATE_3, false, false));
        addParameter(new HTMLTextAreaParameter(panelProvider, PARAM_CURRENT_PAGE_TEMPLATE_3, false, false));
        addParameter(new BooleanParameter(panelProvider, PARAM_OPEN_ALL, false));
        addParameter(new BooleanParameter(panelProvider, PARAM_MARK_OPENED_SECTIONS, false));
    }

    public boolean supportsEditMode(Panel panel) {
        return true;
    }

    public int getEditWidth(Panel panel, CommandRequest request) {
        return 500;
    }

    public int getEditHeight(Panel panel, CommandRequest request) {
        return 500;
    }

    public String getPatternForPage(Section section, int level, Panel panel, boolean current) {
        level = level > 3 ? 3 : level;
        if (level < 1) {
            log.error("Invalid page depth: " + level);
            return null;
        }
        String parameterName = (current ? "current" : "") + "PageTemplate" + level;
        String pattern = panel.getParameterValue(parameterName);
        if (StringUtils.isEmpty(pattern)) {
            return getPatternForPage(section, level - 1, panel, current);
        }
        return pattern;
    }

    public String performReplacementsInPattern(String pattern, Section section, String sectionName) {
        String link = UIServices.lookup().getUrlMarkupGenerator().getLinkToPage(section, true);
        pattern = StringUtils.replace(pattern, TOKEN_LINK, link);
        pattern = StringUtils.replace(pattern, TOKEN_PAGE_NAME, sectionName);
        pattern = StringUtils.replace(pattern, TOKEN_PAGE_URL, StringUtils.defaultString(section.getFriendlyUrl()));
        pattern = StringUtils.replace(pattern, TOKEN_PAGE_DBID, section.getDbid().toString());
        return pattern;
    }

    public String getTabulationString(Panel panel) {
        return panel.getParameterValue(PARAM_TABULATION);
    }

    public void actionSaveEdit(Panel panel, CommandRequest request) throws Exception {
        List listOfChecked = new ArrayList();
        Enumeration en = request.getRequestObject().getParameterNames();
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            if (paramName.startsWith("show_")) {
                String sectionId = paramName.substring("show_".length());
                listOfChecked.add(sectionId);
            }
        }
        HashMap h = new HashMap();
        h.put("visibleIds", listOfChecked);
        panel.setContentData(h);
        panel.getInstance().persist();
        super.activateNormalMode( panel, request );
    }

    public boolean isChecked(Panel p, String token) {
        Map m = (Map) p.getContentData();
        return m == null || ((List) m.get("visibleIds")).contains(token);
    }

}
