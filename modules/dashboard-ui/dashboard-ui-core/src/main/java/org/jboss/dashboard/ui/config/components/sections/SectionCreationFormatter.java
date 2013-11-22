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
package org.jboss.dashboard.ui.config.components.sections;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.Layout;
import org.jboss.dashboard.ui.resources.Skin;
import org.jboss.dashboard.ui.resources.Envelope;
import org.jboss.dashboard.users.UserStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ResourceBundle;

public class SectionCreationFormatter extends SectionsPropertiesFormatter {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SectionCreationFormatter.class.getName());

    private SectionsPropertiesHandler sectionsPropertiesHandler;

    /** The locale manager. */
    protected LocaleManager localeManager;

    public SectionCreationFormatter() {
        localeManager = LocaleManager.lookup();
    }

    public SectionsPropertiesHandler getSectionsPropertiesHandler() {
        return sectionsPropertiesHandler;
    }

    public void setSectionsPropertiesHandler(SectionsPropertiesHandler sectionsPropertiesHandler) {
        this.sectionsPropertiesHandler = sectionsPropertiesHandler;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        renderFragment("outputStart");

        try {
            WorkspacePermission sectionPerm = WorkspacePermission.newInstance(getSectionsPropertiesHandler().getWorkspace(), WorkspacePermission.ACTION_CREATE_PAGE);

            ResourceBundle bundle = localeManager.getBundle("org.jboss.dashboard.ui.messages", getLocale());
            if (UserStatus.lookup().hasPermission(sectionPerm)) {

                setAttribute("error", getSectionsPropertiesHandler().hasError("title"));
                setAttribute("value",getSectionsPropertiesHandler().getTitleMap());
                renderFragment("outputName");

                String preffix = (String) getParameter("preffix");
                initSections(null, "", preffix == null ? "--" : preffix);
                if (!getPageIds().isEmpty()) {
                    renderFragment("outputParentsStart");
                    String parent = getSectionsPropertiesHandler().getParent();
                    for (int i = 0; i < getPageIds().size(); i++) {
                        Long id = (Long) getPageIds().get(i);
                        String title = (String) getPageTitles().get(i);
                        setAttribute("parentId", id);
                        setAttribute("parentTitle", title);
                        setAttribute("selected", id.toString().equals(parent) ? "selected" : "");
                        renderFragment("outputParent");
                    }
                    renderFragment("outputParentsEnd");
                }

                renderFragment("outputSkinsStart");

                GraphicElement[] skins = UIServices.lookup().getSkinsManager().getAvailableElements(getSectionsPropertiesHandler().getWorkspace().getId(), null, null);

                setAttribute("skinTitle", bundle.getString("ui.admin.configuration.skin.sameGraphicElement"));
                setAttribute("skinId", "");
                renderFragment("outputSkin");
                for (int i = 0; i < skins.length; i++) {
                    Skin skin = (Skin) skins[i];
                    if (getSectionsPropertiesHandler().getSkin() != null) {
                        if (skin.getId().equals(getSectionsPropertiesHandler().getSkin()))
                            setAttribute("selected", "selected");
                        else
                            setAttribute("selected", "");
                    }
                    setAttribute("skinTitle", LocaleManager.lookup().localize(skin.getDescription()));
                    setAttribute("skinId", skin.getId());
                    renderFragment("outputSkin");
                }
                renderFragment("outputSkinsEnd");

                renderFragment("outputEnvelopesStart");
                GraphicElement[] envelopes = UIServices.lookup().getEnvelopesManager().getAvailableElements();
                setAttribute("envelopeId", "");
                setAttribute("envelopeTitle", bundle.getString("ui.admin.configuration.envelope.sameGraphicElement"));
                renderFragment("outputEnvelope");
                for (int i = 0; i < envelopes.length; i++) {
                    Envelope envelope = (Envelope) envelopes[i];
                    if (getSectionsPropertiesHandler().getEnvelope() != null) {
                        if (envelope.getId().equals(getSectionsPropertiesHandler().getEnvelope()))
                            setAttribute("selected", "selected");
                        else
                            setAttribute("selected", "");
                    }
                    setAttribute("envelopeId", envelope.getId());
                    setAttribute("envelopeTitle", LocaleManager.lookup().localize(envelope.getDescription()));
                    renderFragment("outputEnvelope");
                }
                renderFragment("outputEnvelopesEnd");

                renderFragment("outputLayoutsStart");
                GraphicElement[] layouts = UIServices.lookup().getLayoutsManager().getAvailableElements();
                for (int i = 0; i < layouts.length; i++) {
                    Layout layout = (Layout) layouts[i];
                    setAttribute("layoutDescription", LocaleManager.lookup().localize(layout.getDescription()));
                    setAttribute("layoutId", layout.getId());
                    if (layout.getId().equals(getSectionsPropertiesHandler().getLayout()))
                        setAttribute("selected", "selected");
                    else
                        setAttribute("selected", "");
                    renderFragment("outputLayout");
                }


                renderFragment("outputLayoutsEnd");
                String layout = getSectionsPropertiesHandler().getLayout();
                setAttribute("itemId", layout);
                setAttribute("template", "../../../layouts/" + layout + "/" + layout + ".jsp");
                renderFragment("layoutPreview");


            }
        } catch (Exception e) {
            log.error("Error rendering section creation form: ", e);
        }
        renderFragment("outputEnd");
    }
}
