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
package org.jboss.dashboard.ui.config.components.workspace;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.BeanHandler;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.components.MessagesComponentHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.WorkspacesManager;
import org.hibernate.Session;
import org.slf4j.Logger;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("workspacep_handler")
public class WorkspacePropertiesHandler extends BeanHandler {

    @Inject
    protected transient Logger log;

    private String workspaceId;
    private Map<String, String> name;
    private Map<String, String> title;
    private String skin;
    private String envelope;
    private String url;
    private int homeSearchMode;
    private boolean defaultWorkspace;

    public void setCurrentWorkspace(Workspace workspace) {
        workspaceId = workspace.getId();
        setDefaultWorkspace(workspace.getDefaultWorkspace());
        setEnvelope(workspace.getEnvelopeId());
        setHomeSearchMode(workspace.getHomeSearchMode());
        setName(workspace.getName());
        setTitle(workspace.getTitle());
        setSkin(workspace.getSkinId());
        setUrl(workspace.getFriendlyUrl());
    }

    public Workspace getWorkspace() throws Exception {
        return getWorkspacesManager().getWorkspace(workspaceId);
    }

    public WorkspacesManager getWorkspacesManager() {
        return UIServices.lookup().getWorkspacesManager();
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getEnvelope() {
        return envelope;
    }

    public void setEnvelope(String envelope) {
        this.envelope = envelope;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHomeSearchMode() {
        return homeSearchMode;
    }

    public void setHomeSearchMode(int homeSearchMode) {
        this.homeSearchMode = homeSearchMode;
    }

    public boolean isDefaultWorkspace() {
        return defaultWorkspace;
    }

    public void setDefaultWorkspace(boolean defaultWorkspace) {
        this.defaultWorkspace = defaultWorkspace;
    }

    public void actionSave(CommandRequest request) {
        buildI18nValues(request);

        //For current Workspace, save new values.
        if (validateBeforeEdition()) {
            try {
                final WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
                workspace.setName(name);
                workspace.setTitle(title);
                workspace.setSkinId(skin);
                workspace.setEnvelopeId(envelope);
                url = ("".equals(url)) ? null : url;
                workspace.setFriendlyUrl(url);
                workspace.setHomeSearchMode(homeSearchMode);

                HibernateTxFragment txFragment = new HibernateTxFragment() {
                    protected void txFragment(Session session) throws Exception {
                        if (defaultWorkspace) {
                            log.debug("Setting default workspace");
                            Workspace currentDefaultWorkspace = getWorkspacesManager().getDefaultWorkspace();
                            log.debug("Current default workspace is " + (currentDefaultWorkspace == null ? "null" : currentDefaultWorkspace.getId()));
                            if (currentDefaultWorkspace != null && (!currentDefaultWorkspace.getId().equals(workspace.getId())))
                            {
                                log.debug("Deleting default workspace property in current default workspace");
                                currentDefaultWorkspace.setDefaultWorkspace(false);
                                getWorkspacesManager().store(currentDefaultWorkspace);
                            }
                        }
                        workspace.setDefaultWorkspace(defaultWorkspace);
                        getWorkspacesManager().store(workspace);
                    }
                };

                txFragment.execute();
                MessagesComponentHandler.lookup().addMessage("ui.alert.workspaceEdition.OK");
            } catch (Exception e) {
                log.error("Error: ", e);
            }
        }
    }

    protected boolean validateBeforeEdition() {
        MessagesComponentHandler messagesHandler = MessagesComponentHandler.lookup();
        messagesHandler.clearAll();
        boolean valid = validate();
        if (!valid) messagesHandler.getErrorsToDisplay().add(0, "ui.alert.workspaceEdition.KO");
        return valid;
    }

    protected boolean validate() {
        MessagesComponentHandler messagesHandler = MessagesComponentHandler.lookup();
        boolean valid = true;
        if (name == null || name.isEmpty()) {
            addFieldError(new FactoryURL(getBeanName(), "name"), null, name);
            messagesHandler.addError("ui.alert.workspaceErrors.name");
            valid = false;
        }
        if (title == null || title.isEmpty()) {
            addFieldError(new FactoryURL(getBeanName(), "title"), null, title);
            messagesHandler.addError("ui.alert.workspaceErrors.title");
            valid = false;
        }
        if (!isValidURL(url)) {
            addFieldError(new FactoryURL(getBeanName(), "url"), null, url);
            messagesHandler.addError("ui.alert.workspaceErrors.url");
            valid = false;
        }
        return valid;
    }

    protected boolean isValidURL(String url) {
        if (url == null || "".equals(url))
            return true;
        String validStartingChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.";
        if (validStartingChars.indexOf(Character.toUpperCase(url.charAt(0))) == -1)
            return false;
        for (int i = 1; i < url.length(); i++)
            if (validChars.indexOf(Character.toUpperCase(url.charAt(i))) == -1)
                return false;
        //Chars are valid

        if (new File(Application.lookup().getBaseAppDirectory() + "/" + url).exists())
            return false;

        //No file or directory exists in root with same name
        try {
            Workspace p = getWorkspacesManager().getWorkspaceByUrl(url);
            if (p == null) return true;//No workspace with same url exists.
            WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
            if (workspace.getId().equals(p.getId())) return true;//It is my own workspace
        } catch (Exception e) {
            log.error("Error getting workspace", e);
        }
        return false;
    }

    protected void buildI18nValues(CommandRequest request) {
        name = buildI18n(request, "name");
        title = buildI18n(request, "title");
    }

    protected Map<String, String> buildI18n(CommandRequest request, String fieldName) {
        Map<String, String> result = new HashMap<String, String>();
        String[] langs = LocaleManager.lookup().getPlatformAvailableLangs();
        if (langs != null) {
            for (String lang : langs) {
                String name = fieldName + "_" + lang;
                String value = request.getParameter(name);
                if (value != null && !"".equals(value)) {
                    result.put(lang, value);
                }
            }
        }
        return result;
    }

}
