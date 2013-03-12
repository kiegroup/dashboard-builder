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
package org.jboss.dashboard.ui.config.components.workspace;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.components.HandlerFactoryElement;
import org.jboss.dashboard.ui.components.MessagesComponentHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.WorkspacesManager;
import org.hibernate.Session;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class WorkspacePropertiesHandler extends HandlerFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(WorkspacePropertiesHandler.class.getName());

    private MessagesComponentHandler messagesComponentHandler;
    private String workspaceId;
    private Map name;
    private Map title;
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

    public MessagesComponentHandler getMessagesComponentHandler() {
        return messagesComponentHandler;
    }

    public void setMessagesComponentHandler(MessagesComponentHandler messagesComponentHandler) {
        this.messagesComponentHandler = messagesComponentHandler;
    }

    public Map getName() {
        return name;
    }

    public void setName(Map name) {
        this.name = name;
    }

    public Map getTitle() {
        return title;
    }

    public void setTitle(Map title) {
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
                getMessagesComponentHandler().addMessage("ui.alert.workspaceEdition.OK");
            } catch (Exception e) {
                log.error("Error: ", e);
            }
        }
    }

    protected boolean validateBeforeEdition() {
        getMessagesComponentHandler().clearAll();
        boolean valid = validate();
        if (!valid) getMessagesComponentHandler().getErrorsToDisplay().add(0, "ui.alert.workspaceEdition.KO");
        return valid;
    }

    protected boolean validate() {
        boolean valid = true;
        if (name == null || name.get(LocaleManager.lookup().getDefaultLang()) == null) {
            addFieldError(new FactoryURL(getComponentName(), "name"), null, name);
            getMessagesComponentHandler().addError("ui.alert.workspaceErrors.name");
            valid = false;
        }
        if (title == null || title.get(LocaleManager.lookup().getDefaultLang()) == null) {
            addFieldError(new FactoryURL(getComponentName(), "title"), null, title);
            getMessagesComponentHandler().addError("ui.alert.workspaceErrors.title");
            valid = false;
        }
        if (!isValidURL(url)) {
            addFieldError(new FactoryURL(getComponentName(), "url"), null, url);
            getMessagesComponentHandler().addError("ui.alert.workspaceErrors.url");
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
            log.error(e);
        }
        return false;
    }

    protected void buildI18nValues(CommandRequest request) {
        name = buildI18n(request, "name");
        title = buildI18n(request, "title");
    }

    protected Map buildI18n(CommandRequest request, String fieldName) {
        Map result = new HashMap();
        String[] langs = LocaleManager.lookup().getPlatformAvailableLangs();
        if (langs != null) {
            for (int i = 0; i < langs.length; i++) {
                String name = fieldName + "_" + langs[i];
                String value = request.getParameter(name);
                if (value != null && !"".equals(value)) {
                    result.put(langs[i], value);
                }
            }
        }
        return result;
    }

}
