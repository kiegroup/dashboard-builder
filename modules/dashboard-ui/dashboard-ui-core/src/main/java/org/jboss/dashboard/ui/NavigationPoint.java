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
package org.jboss.dashboard.ui;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class NavigationPoint {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NavigationPoint.class.getName());

    private String url;
    private String workspaceId = null;
    private String sectionId = null;
    private Long panelId = null;
    private String actionName = null;

    public NavigationPoint(String url) {
        init(url);
    }

    protected void init(String url) {
        // Try to interpret current workspace, page, and panel action if any and calculate permissions.
        this.url = url;
        String uri = null;
        String queryString = null;

        // Remove anchor
        int anchorPoint = url.indexOf('#');
        if (anchorPoint != -1) {
            url = url.substring(0, anchorPoint);
        }

        try {
            int dividerPoint = url.indexOf('?');
            uri = dividerPoint == -1 ? url : url.substring(0, dividerPoint);
            uri = uri.substring(URLMarkupGenerator.FRIENDLY_PREFIX.length() + 1);
            queryString = dividerPoint == -1 ? "" : url.substring(dividerPoint + 1);
        } catch (Exception e) {
            //Ignore "parsing" errors
        }

        //Remove language information if any
        LocaleManager localeManager = LocaleManager.lookup();
        String[] possibleLangs = localeManager.getPlatformAvailableLangs();
        for (int i = 0; i < possibleLangs.length; i++) {
            String lang = possibleLangs[i];
            if (uri.startsWith(lang + "/")) {
                uri = uri.substring(lang.length() + 1);
                break;
            }
        }

        try {
            int dividerPoint = uri.indexOf('/');
            workspaceId = dividerPoint == -1 ? null : uri.substring(0, dividerPoint);
            sectionId = dividerPoint == -1 ? null : uri.substring(dividerPoint + 1);
            int sectionDividerPoint = sectionId.indexOf('/');
            if (sectionDividerPoint != -1) {
                sectionId = sectionId.substring(0, sectionDividerPoint);
            }
        } catch (Exception e) {
            //Ignore "parsing" errors
        }

        try {
            Map params = new HashMap();
            StringTokenizer strtk = new StringTokenizer(StringEscapeUtils.UNESCAPE_HTML4.translate(queryString), "&");
            while (strtk.hasMoreTokens()) {
                String token = strtk.nextToken();
                int dividerPoint = token.indexOf('=');
                if (dividerPoint != -1) {
                    params.put(token.substring(0, dividerPoint), token.substring(1 + dividerPoint));
                }
            }
            String panelIdParam = (String) params.get(Parameters.DISPATCH_IDPANEL);
            if (!StringUtils.isEmpty(panelIdParam)) {
                panelId = Long.decode(panelIdParam);
                actionName = (String) params.get(Parameters.DISPATCH_ACTION);
            }
        } catch (Exception e) {
            //Ignore "parsing" errors
        }
    }


    public String getUrl() {
        return url;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public Long getPanelId() {
        return panelId;
    }

    public String getActionName() {
        return actionName;
    }

    public WorkspaceImpl getWorkspace() {
        if (getWorkspaceId() == null) return null;
        try {
            WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(getWorkspaceId());
            if (workspace != null) return workspace;
            return (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspaceByUrl(getWorkspaceId());
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    public Section getPage() {
        if (getSectionId() == null) return null;
        WorkspaceImpl workspace = getWorkspace();
        if (workspace != null) {
            try {
                Section page = workspace.getSection(Long.decode(getSectionId()));
                if (page != null) return page;
            } catch (NumberFormatException nfe) {
            }
            return workspace.getSectionByUrl(getSectionId());
        }
        return null;
    }

    public Panel getPanel() {
        if (getPanelId() == null) return null;
        Section page = getPage();
        if (page != null) {
            return page.getPanel(getPanelId().toString());
        }
        return null;
    }
}
