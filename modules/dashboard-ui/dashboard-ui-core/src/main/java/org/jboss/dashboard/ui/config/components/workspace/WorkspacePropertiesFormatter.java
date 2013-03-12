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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.SkinsManager;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.Envelope;
import org.jboss.dashboard.ui.resources.Skin;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Workspace;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class WorkspacePropertiesFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(WorkspacePropertiesFormatter.class.getName());

    private WorkspacePropertiesHandler workspacePropertiesHandler;

    public WorkspacePropertiesHandler getWorkspacePropertiesHandler() {
        return workspacePropertiesHandler;
    }

    public void setWorkspacePropertiesHandler(WorkspacePropertiesHandler workspacePropertiesHandler) {
        this.workspacePropertiesHandler = workspacePropertiesHandler;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        try {
            renderFragment("outputStart");
            setAttribute("error", getWorkspacePropertiesHandler().hasError("name"));
            renderFragment("outputName");
            renderI18nInputs("name", 50, getWorkspacePropertiesHandler().getName());

            setAttribute("error", getWorkspacePropertiesHandler().hasError("title"));
            renderFragment("outputTitle");
            renderI18nInputs("title", 50, getWorkspacePropertiesHandler().getTitle());

            setAttribute("error", getWorkspacePropertiesHandler().hasError("skin"));
            renderFragment("skinsStart");
            SkinsManager skinsManager = UIServices.lookup().getSkinsManager();
            GraphicElement[] skins = skinsManager.getAvailableElements(getWorkspacePropertiesHandler().getWorkspace().getId(), null, null);
            for (int i = 0; i < skins.length; i++) {
                Skin skin = (Skin) skins[i];
                boolean currentSkin = skin.getId().equals(getWorkspacePropertiesHandler().getSkin());
                setAttribute("skinDescription", skin.getDescription());
                setAttribute("skinId", skin.getId());
                renderFragment(currentSkin ? "outputSelectedSkin" : "outputSkin");
            }
            renderFragment("skinsEnd");


            setAttribute("error", getWorkspacePropertiesHandler().hasError("envelope"));
            renderFragment("envelopesStart");
            GraphicElement[] envelopes = UIServices.lookup().getEnvelopesManager().getAvailableElements(getWorkspacePropertiesHandler().getWorkspace().getId(), null, null);
            for (int i = 0; i < envelopes.length; i++) {
                Envelope envelope = (Envelope) envelopes[i];
                boolean currentEnvelope = envelope.getId().equals(getWorkspacePropertiesHandler().getEnvelope());
                setAttribute("envelopeDescription", envelope.getDescription());
                setAttribute("envelopeId", envelope.getId());
                renderFragment(currentEnvelope ? "outputSelectedEnvelope" : "outputEnvelope");
            }
            renderFragment("envelopesEnd");


            StringBuffer urlprefix = new StringBuffer("http://" + request.getServerName());
            if (request.getServerPort() != 80)
                urlprefix.append(":").append(request.getServerPort());
            urlprefix.append(request.getContextPath());
            urlprefix.append("/workspace/&lt;lang&gt;/");
            setAttribute("urlPreffix", urlprefix.toString());
            setAttribute("value", getWorkspacePropertiesHandler().getUrl());
            setAttribute("error", getWorkspacePropertiesHandler().hasError("url"));
            renderFragment("outputUrl");

            setAttribute("error", getWorkspacePropertiesHandler().hasError("homeSearchMode"));
            renderFragment("homeSearchModeStart");
            int[] possibleHomeModes = new int[]{Workspace.SEARCH_MODE_ROLE_HOME_PREFERENT, Workspace.SEARCH_MODE_CURRENT_SECTION_PREFERENT};
            String[] homeModeDescriptions = new String[]{"ui.workspace.homeSearchMode.roleHome", "ui.workspace.homeSearchMode.currentPage"};
            for (int i = 0; i < possibleHomeModes.length; i++) {
                int possibleMode = possibleHomeModes[i];
                boolean currentMode = possibleMode == getWorkspacePropertiesHandler().getHomeSearchMode();
                setAttribute("modeDescription", homeModeDescriptions[i]);
                setAttribute("modeId", possibleMode);
                renderFragment(currentMode ? "outputSelectedMode" : "outputMode");
            }
            renderFragment("homeSearchModeEnd");

            if (UserStatus.lookup().isRootUser()) {
                setAttribute("value", getWorkspacePropertiesHandler().isDefaultWorkspace());
                setAttribute("error", getWorkspacePropertiesHandler().hasError("defaultWorkspace"));
                renderFragment("outputDefaultWorkspace");
            }

            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error(e);
            throw new FormatterException(e);
        }
    }

    protected void renderI18nInputs(String fieldName, int maxlength, Map defaultValue) {
        setAttribute("name", fieldName);
        renderFragment("outputI18nStart");
        String[] langs = LocaleManager.lookup().getPlatformAvailableLangs();
        if (langs != null) {
            for (int i = 0; i < langs.length; i++) {
                String value = defaultValue != null ? (String) defaultValue.get(langs[i]) : null;
                setAttribute("name", fieldName);
                setAttribute("langId", langs[i]);
                setAttribute("maxlength", maxlength);
                setAttribute("value", value != null ? value : "");
                renderFragment("outputInput");
            }
        }
        renderFragment("outputI18nEnd");
    }
}
