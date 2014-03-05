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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.Envelope;
import org.jboss.dashboard.ui.resources.Layout;
import org.jboss.dashboard.ui.resources.Skin;
import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.workspace.Workspace;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SectionPropertiesFormatter extends Formatter {

    @Inject
    private transient Logger log;

    @Inject
    private SectionPropertiesHandler sectionPropertiesHandler;

    @Inject /** The locale manager. */
    protected LocaleManager localeManager;

    public SectionPropertiesHandler getSectionPropertiesHandler() {
        return sectionPropertiesHandler;
    }

    public void setSectionPropertiesHandler(SectionPropertiesHandler sectionPropertiesHandler) {
        this.sectionPropertiesHandler = sectionPropertiesHandler;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        try {
            ResourceBundle bundle = localeManager.getBundle("org.jboss.dashboard.ui.messages", SessionManager.getCurrentLocale());

            renderFragment("outputStart");

            setAttribute("error", getSectionPropertiesHandler().hasError("title"));
            renderFragment("outputStartTitle");

            Map<String, String> mParam = setLangTitle(request);
            Map<String, String> titleMap = getSectionPropertiesHandler().getTitleMap();
            mParam.putAll(titleMap);

            String[] langs = getLocaleManager().getPlatformAvailableLangs();
            for (String lang : langs) {
                setAttribute("lang", lang);
                setAttribute("selected", lang.equals(getLocale().getLanguage()));
                setAttribute("value", StringUtils.defaultString(mParam.get(lang)));
                renderFragment("outputTitle");
            }

            renderFragment("outputTitleEnd");

            StringBuffer urlprefix = new StringBuffer("http://" + request.getServerName());
            if (request.getServerPort() != 80)
                urlprefix.append(":").append(request.getServerPort());
            urlprefix.append(request.getContextPath());
            urlprefix.append("/workspace/&lt;lang&gt;/");
            Workspace currentWorkspace = getSectionPropertiesHandler().getWorkspace();
            String friendlyUrl = currentWorkspace.getFriendlyUrl();
            friendlyUrl = StringUtils.defaultIfEmpty(friendlyUrl, currentWorkspace.getId());
            urlprefix.append(friendlyUrl).append("/");

            setAttribute("urlPreffix", urlprefix.toString());
            setAttribute("value", getSectionPropertiesHandler().getUrl());
            setAttribute("error", getSectionPropertiesHandler().hasError("url"));
            renderFragment("outputUrl");

            setAttribute("value", getSectionPropertiesHandler().getVisible());
            setAttribute("error", getSectionPropertiesHandler().hasError("visible"));
            renderFragment("outputVisible");

            setAttribute("error", getSectionPropertiesHandler().hasError("skin"));
            renderFragment("skinsPreStart");
            renderFragment("skinsStart");
            GraphicElement[] skins = UIServices.lookup().getSkinsManager().getAvailableElements(getSectionPropertiesHandler().getWorkspace().getId(), null, null);
            if ((getSectionPropertiesHandler().getSkin() == null) || (getSectionPropertiesHandler().getSkin().equals("")))
            {
                // Empty skin attribute to give the possibility for no skin selection.
                setAttribute("skinDescription", bundle.getString("ui.admin.configuration.skin.sameGraphicElement"));
                setAttribute("skinId", "");
                renderFragment("outputSelectedSkin");
                for (int i = 0; i < skins.length; i++) {
                    Skin skin = (Skin) skins[i];
                    setAttribute("skinDescription", skin.getDescription().get(SessionManager.getCurrentLocale().getLanguage()));
                    setAttribute("skinId", skin.getId());
                    renderFragment("outputSkin");
                }
            } else {
                // Empty skin attribute to give the possibility for no skin selection.
                setAttribute("skinDescription", bundle.getString("ui.admin.configuration.skin.sameGraphicElement"));
                setAttribute("skinId", "");
                renderFragment("outputSkin");
                for (int i = 0; i < skins.length; i++) {
                    Skin skin = (Skin) skins[i];
                    boolean currentSkin = skin.getId().equals(getSectionPropertiesHandler().getSkin());
                    setAttribute("skinDescription", skin.getDescription().get(SessionManager.getCurrentLocale().getLanguage()));
                    setAttribute("skinId", skin.getId());
                    renderFragment(currentSkin ? "outputSelectedSkin" : "outputSkin");
                }
            }
            renderFragment("skinsEnd");

            setAttribute("error", getSectionPropertiesHandler().hasError("envelope"));
            renderFragment("envelopesPreStart");
            renderFragment("envelopesStart");
            GraphicElement[] envelopes = UIServices.lookup().getEnvelopesManager().getAvailableElements(getSectionPropertiesHandler().getWorkspace().getId(), null, null);
            if ((getSectionPropertiesHandler().getEnvelope() == null) || (getSectionPropertiesHandler().getEnvelope().equals("")))
            {
                // Empty envelope attribute to give the possibility for no envelope selection.
                setAttribute("envelopeDescription", bundle.getString("ui.admin.configuration.envelope.sameGraphicElement"));
                setAttribute("envelopeId", "");
                renderFragment("outputSelectedEnvelope");
                for (int i = 0; i < envelopes.length; i++) {
                    Envelope envelope = (Envelope) envelopes[i];
                    setAttribute("envelopeDescription", envelope.getDescription().get(SessionManager.getCurrentLocale().getLanguage()));
                    setAttribute("envelopeId", envelope.getId());
                    renderFragment("outputEnvelope");
                }
            } else {
                // Empty envelope attribute to give the possibility for no envelope selection.
                setAttribute("envelopeDescription", bundle.getString("ui.admin.configuration.envelope.sameGraphicElement"));
                setAttribute("envelopeId", "");
                renderFragment("outputEnvelope");
                for (int i = 0; i < envelopes.length; i++) {
                    Envelope envelope = (Envelope) envelopes[i];
                    boolean currentEnvelope = envelope.getId().equals(getSectionPropertiesHandler().getEnvelope());
                    setAttribute("envelopeDescription", envelope.getDescription().get(SessionManager.getCurrentLocale().getLanguage()));
                    setAttribute("envelopeId", envelope.getId());
                    renderFragment(currentEnvelope ? "outputSelectedEnvelope" : "outputEnvelope");
                }
            }
            renderFragment("envelopesEnd");


            renderFragment("cellSpacingStart");

            setAttribute("value", getSectionPropertiesHandler().getRegionsCellSpacing());
            setAttribute("error", getSectionPropertiesHandler().hasError("regionscellspacing"));
            renderFragment("outputRegionsCellSpacing");

            setAttribute("value", getSectionPropertiesHandler().getPanelsCellSpacing());
            setAttribute("error", getSectionPropertiesHandler().hasError("panelscellspacing"));
            renderFragment("outputPanelsCellSpacing");

            renderFragment("cellSpacingEnd");

            setAttribute("error", getSectionPropertiesHandler().hasError("layout"));
            renderFragment("layoutsStart");
            GraphicElement[] layouts = UIServices.lookup().getLayoutsManager().getAvailableElements(getSectionPropertiesHandler().getWorkspace().getId(), null, null);
            for (int i = 0; i < layouts.length; i++) {
                Layout layout = (Layout) layouts[i];
                boolean currentLayout = layout.getId().equals(getSectionPropertiesHandler().getLayout());
                setAttribute("layoutDescription", layout.getDescription());
                setAttribute("layoutId", layout.getId());
                renderFragment(currentLayout ? "outputSelectedLayout" : "outputLayout");
            }
            renderFragment("layoutsEnd");

            //layout preview
            String layout = getSectionPropertiesHandler().getLayout();
            setAttribute("itemId", layout);
            setAttribute("template", "../../../layouts/" + layout + "/" + layout + ".jsp");
            renderFragment("layoutPreview");

            renderFragment("outputEnd");

        } catch (Exception e) {
            throw new FormatterException(e);
        }
    }


    public Map<String, String> setLangTitle(HttpServletRequest request) throws Exception {
        Map<String, String> titleMap = new HashMap<String, String>();

        Map<String, String[]> params = request.getParameterMap();
        for (String paramName : params.keySet()) {
            if (paramName.startsWith("name_")) {
                String lang = paramName.substring("name_".length());
                String paramValue = request.getParameter(paramName);
                if (paramValue != null && !"".equals(paramValue))
                    titleMap.put(lang, paramValue);
            }
        }
        return titleMap;
    }
}


