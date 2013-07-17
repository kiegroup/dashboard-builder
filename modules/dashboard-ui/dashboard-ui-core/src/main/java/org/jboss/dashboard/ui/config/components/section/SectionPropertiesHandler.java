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
package org.jboss.dashboard.ui.config.components.section;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.components.HandlerFactoryElement;
import org.jboss.dashboard.ui.components.MessagesComponentHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.hibernate.Session;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SectionPropertiesHandler extends HandlerFactoryElement {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SectionPropertiesHandler.class.getName());

    private String workspaceId;
    private Long sectionId;
    private Map titleMap;
    private String title;
    private boolean visible;
    private String skin;
    private String envelope;
    private String url;
    private String lang;
    private String layout;
    private int regionsCellSpacing;
    private int panelsCellSpacing;
    private String saveButtonPressed;
    private MessagesComponentHandler messagesComponentHandler;

    public MessagesComponentHandler getMessagesComponentHandler() {
        return messagesComponentHandler;
    }

    public void setMessagesComponentHandler(MessagesComponentHandler messagesComponentHandler) {
        this.messagesComponentHandler = messagesComponentHandler;
    }

    public void setWorkspace(Workspace p) {
        workspaceId = p.getId();
    }

    protected void readSectionValues() {
        try {

            Section section = getWorkspace().getSection(sectionId);
            if (section.getTitle().get(lang) == null) {
                setTitle((String) section.getTitle().get(LocaleManager.lookup().getDefaultLang()));
            } else {
                setTitle((String) section.getTitle().get(lang));

            }

            setTitleMap(section.getTitle());
            setVisible(section.isVisible().booleanValue());
            setEnvelope(section.getEnvelopeId());
            setSkin(section.getSkinId());
            setUrl(section.getFriendlyUrl());
            setLayout(section.getLayoutId());
            setEnvelope(section.getEnvelopeId());
            setRegionsCellSpacing(section.getRegionsCellSpacing().intValue());
            setPanelsCellSpacing(section.getPanelsCellSpacing().intValue());

        } catch (Exception e) {
            SectionPropertiesHandler.log.error("Error: ", e);
        }
    }

    public String getSaveButtonPressed() {
        return saveButtonPressed;
    }

    public void setSaveButtonPressed(String saveButtonPressed) {
        this.saveButtonPressed = saveButtonPressed;
    }

    public Workspace getWorkspace() throws Exception {
        return UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
    }

    public Section getSection() throws Exception {
        return UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId).getSection(sectionId);
    }

    public void setSection(Section section) {
        sectionId = section.getId();
        readSectionValues();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map getTitleMap() {
        return titleMap;
    }

    public void setTitleMap(Map titleMap) {
        this.titleMap = titleMap;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public int getRegionsCellSpacing() {
        return regionsCellSpacing;
    }

    public void setRegionsCellSpacing(int regionsCellSpacing) {
        this.regionsCellSpacing = regionsCellSpacing;
    }

    public int getPanelsCellSpacing() {
        return panelsCellSpacing;
    }

    public void setPanelsCellSpacing(int panelsCellSpacing) {
        this.panelsCellSpacing = panelsCellSpacing;
    }

    public void actionSave(CommandRequest request) throws Exception {
        if ("".equals(saveButtonPressed)) {
            setTitleMap(setLangTitle(request));
            return;
        }
        if ("false".equals(saveButtonPressed)) {
            readSectionValues();
            return;
        }

        setTitle((String) setLangTitle(request).get(LocaleManager.lookup().getDefaultLang()));
        if (validateBeforeEdition()) {
            try {
                final Section section = getSection();
                section.setTitle(setLangTitle(request));
                section.setVisible(Boolean.valueOf(visible));
                section.setSkinId(skin);
                section.setEnvelopeId(envelope);
                url = ("".equals(url)) ? null : url;
                section.setFriendlyUrl(url);
                section.setRegionsCellSpacing(new Integer(regionsCellSpacing));
                section.setPanelsCellSpacing(new Integer(panelsCellSpacing));
                section.setLayoutId(layout);


                HibernateTxFragment txFragment = new HibernateTxFragment() {
                    protected void txFragment(Session session) throws Exception {
                        UIServices.lookup().getSectionsManager().store(section);
                    }
                };

                txFragment.execute();
                getMessagesComponentHandler().addMessage("ui.alert.sectionEdition.OK");
                setSection(section);
            } catch (Exception e) {
                log.error("Error: ", e);
                getMessagesComponentHandler().clearAll();
                getMessagesComponentHandler().addError("ui.alert.sectionEdition.KO");
            }
        }
    }

    public Map setLangTitle(CommandRequest request) throws Exception {
        Map m = new HashMap();
        Map params = request.getRequestObject().getParameterMap();
        for (Iterator it = params.keySet().iterator(); it.hasNext();) {
            String paramName = (String) it.next();
            if (paramName.startsWith("name_")) {
                String lang = paramName.substring("name_".length());
                String paramValue = request.getParameter(paramName);
                if (paramValue != null && !"".equals(paramValue))
                    m.put(lang, paramValue);
            }
        }
        return m;
    }

    protected boolean validateBeforeEdition() {
        getMessagesComponentHandler().clearAll();
        boolean valid = validate();
        if (!valid) getMessagesComponentHandler().getErrorsToDisplay().add(0, "ui.alert.sectionEdition.KO");
        return valid;
    }

    protected boolean validate() {

        if (title == null || "".equals(title)) {
            addFieldError(new FactoryURL(getComponentName(), "title"), null, title);
            getMessagesComponentHandler().addError("ui.alert.sectionErrors.title");
        }
        if (!isValidURL(url)) {
            addFieldError(new FactoryURL(getComponentName(), "url"), null, url);
            getMessagesComponentHandler().addError("ui.alert.sectionErrors.url");
        }

        return getFieldErrors().isEmpty();
    }


    protected boolean isValidURL(String url) {
        if (url == null || "".equals(url))
            return true;
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.";
        for (int i = 0; i < url.length(); i++)
            if (validChars.indexOf(Character.toUpperCase(url.charAt(i))) == -1)
                return false;
        //Chars are valid

        if (new File(Application.lookup().getBaseAppDirectory() + "/" + url).exists())
            return false;

        //No file or directory exists in root with same name
        try {
            WorkspaceImpl currentWorkspace = (WorkspaceImpl) getWorkspace();

            Workspace p = UIServices.lookup().getWorkspacesManager().getWorkspaceByUrl(url);
            if (p != null && !currentWorkspace.getId().equals(p.getId()))
                return false;//Exists workspace with this friendly URL

            Section sections[] = currentWorkspace.getAllSections();

            if (sections != null) {
                for (int i = 0; i < sections.length; i++) {
                    if (!sections[i].getId().equals(sectionId) && sections[i].getFriendlyUrl() != null && sections[i].getFriendlyUrl().equals(url))
                        return false;
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error checking URL: " + url, e);
        }
        return false;
    }

}
