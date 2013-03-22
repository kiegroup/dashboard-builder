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
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.users.UserStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FriendlyUrlProcessor extends RequestChainProcessor {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FriendlyUrlProcessor.class.getName());

    private NavigationManager navigationManager;
    private boolean showLoginBackDoorOnPermissionDenied = true;

    public static final String FRIENDLY_MAPPING = "/" + URLMarkupGenerator.FRIENDLY_PREFIX;


    public boolean isShowLoginBackDoorOnPermissionDenied() {
        return showLoginBackDoorOnPermissionDenied;
    }

    public void setShowLoginBackDoorOnPermissionDenied(boolean showLoginBackDoorOnPermissionDenied) {
        this.showLoginBackDoorOnPermissionDenied = showLoginBackDoorOnPermissionDenied;
    }

    public UserStatus getUserStatus() {
        return UserStatus.lookup();
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    /**
     * Make required processing of request.
     *
     * @return true if processing must continue, false otherwise.
     */
    protected boolean processRequest() throws Exception {
        HttpServletRequest request = getRequest();
        String servletPath = request.getServletPath();

        // No friendly -> nothing to do.
        if (!servletPath.startsWith(FRIENDLY_MAPPING)) return true;

        String contextPath = request.getContextPath();
        getControllerStatus().consumeURIPart(FRIENDLY_MAPPING);
        navigationManager.setShowingConfig(false);
        String requestUri = request.getRequestURI();
        String relativeUri = requestUri.substring(contextPath == null ? 0 : (contextPath.length()));
        relativeUri = relativeUri.substring(servletPath == null ? 0 : (servletPath.length()));

        // Empty URI -> nothing to do.
        if (StringUtils.isBlank(relativeUri)) return true;

        // Apply language information, if any.
        String[] possibleLangs = LocaleManager.lookup().getPlatformAvailableLangs();
        for (int i = 0; i < possibleLangs.length; i++) {
            String lang = possibleLangs[i];
            if (relativeUri.startsWith("/" + lang)) {
                relativeUri = relativeUri.substring(lang.length() + 1);
                LocaleManager.lookup().setCurrentLang(lang);
                getControllerStatus().consumeURIPart("/" + lang);
                break;
            }
        }
        // Tokenize the friendly URI.
        StringTokenizer tokenizer = new StringTokenizer(relativeUri, "/", false);
        List tokens = new ArrayList();
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (i < 2) {
                tokens.add(token);
                i++;
            } else if (tokens.size() == 2) {
                tokens.add("/" + token);
            } else {
                tokens.set(2, tokens.get(2) + "/" + token);
            }
        }
        try {
            // Get the target workspace/section spècified.
            log.debug("Tokens=" + tokens);
            String workspaceCandidate = null;
            String sectionCandidate = null;
            if (tokens.size() > 0) workspaceCandidate = (String) tokens.get(0);
            if (tokens.size() > 1) sectionCandidate = (String) tokens.get(1);
            if (log.isDebugEnabled()) {
                log.debug("workspaceCandidate=" + workspaceCandidate);
                log.debug("sectionCandidate=" + sectionCandidate);
            }
            WorkspaceImpl workspace = null;
            Section section = null;
            if (workspaceCandidate != null) {
                boolean canbeWorkspaceId = canBeWorkspaceId(workspaceCandidate);
                if (canbeWorkspaceId) workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceCandidate);
                if (workspace == null) workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspaceByUrl(workspaceCandidate);
            }
            if (workspace != null && sectionCandidate != null) {
                try {
                    section = workspace.getSection(Long.decode(sectionCandidate));
                } catch (NumberFormatException nfe) {
                    section = workspace.getSectionByUrl(sectionCandidate);
                }
            }
            // Check the user has access permissions to the target workspace.
            if (workspace != null && section == null) {
                try {
                    Workspace currentWorkspace = navigationManager.getCurrentWorkspace();
                    log.debug("currentWorkspace = " + (currentWorkspace == null ? "null" : currentWorkspace.getId()) + " workspaceCandidate = " + workspaceCandidate);
                    if (!workspace.equals(currentWorkspace)) {

                        WorkspacePermission workspacePerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_LOGIN);
                        if (getUserStatus().hasPermission(workspacePerm)) {
                            navigationManager.setCurrentWorkspace(workspace);
                            log.debug("SessionManager.setWorkspace(" + workspace.getId() + ")");
                        } else {
                            if (log.isDebugEnabled()) log.debug("User has no " + WorkspacePermission.ACTION_LOGIN + " permission in workspace " + workspaceCandidate);
                            if (isShowLoginBackDoorOnPermissionDenied()) {
                                navigationManager.setUserRequiresLoginBackdoor(true);
                                navigationManager.setCurrentWorkspace(workspace);
                            }
                        }
                    }
                    getControllerStatus().consumeURIPart("/" + workspaceCandidate);
                } catch (Exception e) {
                    log.error("Cannot set current workspace.", e);
                }
            }
            // Check the user has access permissions to the target section.
            else if (section != null) {
                try {
                    if (!section.equals(navigationManager.getCurrentSection())) {

                        WorkspacePermission workspacePerm = WorkspacePermission.newInstance(section.getWorkspace(), WorkspacePermission.ACTION_LOGIN);
                        SectionPermission sectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_VIEW);
                        if (getUserStatus().hasPermission(workspacePerm) && getUserStatus().hasPermission(sectionPerm)) {
                            if (log.isDebugEnabled()) log.debug("SessionManager.setSection(" + section.getId() + ")");
                            navigationManager.setCurrentSection(section);
                        }
                        else {
                            if (log.isDebugEnabled()) log.debug("User has no " + WorkspacePermission.ACTION_LOGIN + " permission in workspace " + workspaceCandidate);
                            if (isShowLoginBackDoorOnPermissionDenied()) {
                                navigationManager.setUserRequiresLoginBackdoor(true);
                                navigationManager.setCurrentSection(section);
                            }
                        }
                    }
                    getControllerStatus().consumeURIPart("/" + workspaceCandidate);
                    getControllerStatus().consumeURIPart("/" + sectionCandidate);
                } catch (Exception e) {
                    log.error("Cannot set current section.", e);
                }
            }
        } catch (Exception e) {
            log.error("Exception processing friendly URI", e);
        }
        return true;
    }

    protected boolean canBeWorkspaceId(String workspaceCandidate) {
        try {
            Long.decode(workspaceCandidate);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
