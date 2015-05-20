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

import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.security.WorkspacePermission;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import org.jboss.dashboard.LocaleManager;

public class SectionsPropertiesFormatter extends Formatter {

    @Inject
    private transient Logger log;

    @Inject
    private SectionsPropertiesHandler sectionsPropertiesHandler;

    private List<String> pageTitles = new ArrayList<String>();
    private List<Long> pageIds = new ArrayList<Long>();

    public List<Long> getPageIds() {
        return pageIds;
    }

    public void setPageIds(List<Long> pageIds) {
        this.pageIds = pageIds;
    }

    public List<String> getPageTitles() {
        return pageTitles;
    }

    public void setPageTitles(List<String> pageTitles) {
        this.pageTitles = pageTitles;
    }

    public SectionsPropertiesHandler getSectionsPropertiesHandler() {
        return sectionsPropertiesHandler;
    }

    public void setSectionsPropertiesHandler(SectionsPropertiesHandler sectionsPropertiesHandler) {
        this.sectionsPropertiesHandler = sectionsPropertiesHandler;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        if (getSectionsPropertiesHandler().getCreateSection().booleanValue()) {
            renderFragment("outputCreateSection");
            return;
        }
        if (getSectionsPropertiesHandler().getDuplicateSection().booleanValue()) {
            renderFragment("outputDuplicateSection");
            return;
        }
        WorkspaceImpl workspace;
        renderFragment("outputStart");
        try {
            workspace = (WorkspaceImpl) getSectionsPropertiesHandler().getWorkspace();
            renderFragment("outputCommandsBarStart");

            String preffix = (String) getParameter("preffix");
            List<Section> pages = initSections(preffix == null ? "--" : preffix, workspace);

            if (!pages.isEmpty()) {
                renderFragment("outputStartSelect");
                renderFragment("outputNoneSelected");

                for (int i = 0; i < pages.size(); i++) {
                    Section section = pages.get(i);
                    String title = pageTitles.get(i);
                    setAttribute("id", section.getId());
                    setAttribute("title", StringEscapeUtils.ESCAPE_HTML4.translate(title));
                    renderFragment("outputSelect");
                }
                renderFragment("outputEndSelect");
            }

            WorkspacePermission workspacePerm = WorkspacePermission.newInstance(getSectionsPropertiesHandler().getWorkspace(), WorkspacePermission.ACTION_ADMIN_PROVIDERS);
            setAttribute("editPanels", UserStatus.lookup().hasPermission(workspacePerm));

            setAttribute("workspace", workspace);
            renderFragment("outputCommandsBarEnd");
            renderFragment("commandsBarSeparation");

            if (!getSectionsPropertiesHandler().getErrorPermission().isEmpty()) {

                for (int i = 0; i < getSectionsPropertiesHandler().getErrorPermission().size(); i++) {
                    setAttribute("errorCommand", getSectionsPropertiesHandler().getErrorPermission().get(i));
                    renderFragment("outputErrorCommands");
                }
                getSectionsPropertiesHandler().setErrorPermission(new ArrayList<String>());
            }

            renderFragment("outputTreeStart");
            if (workspace.getSectionsCount() == 0)
                renderFragment("outputEmptySections");
            else {
                setAttribute("workspaceId", workspace.getId());
                renderFragment("outputTreeBody");
            }
            renderFragment("outputTreeEnd");
            setAttribute("moveLoop", getSectionsPropertiesHandler().getMoveLoop());
            renderFragment("outputEnd");
            getSectionsPropertiesHandler().setMoveLoop(Boolean.FALSE);

        } catch (Exception e) {
            log.error("Error:", e);
        }

    }

    protected List<Section> initSections(String preffix, WorkspaceImpl workspace) {
        List<Section> pages = new ArrayList<Section>();
        if (workspace != null) {
            Section[] sections = workspace.getAllSections(); //Sorted!
            for (int i = 0; i < sections.length; i++) {
                Section section = sections[i];
                int depth = section.getDepthLevel();
                SectionPermission viewPerm = SectionPermission.newInstance(sections[i], SectionPermission.ACTION_VIEW);
                if (UserStatus.lookup().hasPermission(viewPerm)) {
                    pages.add(section);
                    String title = getTitle(sections[i]);
                    pageTitles.add(StringUtils.leftPad(title, title.length() + (depth * preffix.length()), preffix));
                } else { // Skip all following pages with larger depth (children)
                    while (i + 1 < sections.length && sections[i + 1].getDepthLevel() > depth) i++;
                }
            }
        }
        return pages;
    }

    public Map<String, String> setLangTitle(HttpServletRequest request) throws Exception {
        Map<String, String> m = new HashMap<String, String>();

        Map<String, String[]> params = request.getParameterMap();
        for (String paramName : params.keySet()) {
            if (paramName.startsWith("name_")) {
                String lang = paramName.substring("name_".length());
                String paramValue = request.getParameter(paramName);
                if (paramValue != null && !"".equals(paramValue))
                    m.put(lang, paramValue);
            }
        }
        return m;
    }


    public void initSections(Section rootSection, String indent, String preffix) throws Exception {
        WorkspaceImpl workspace;

        workspace = (WorkspaceImpl) getSectionsPropertiesHandler().getWorkspace();
        if (workspace != null) {
            Section[] sections = rootSection != null ?
                    workspace.getAllChildSections(rootSection.getId()) :
                    workspace.getAllRootSections();
            for (Section section : sections) {
                SectionPermission viewPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_VIEW);
                if (UserStatus.lookup().hasPermission(viewPerm)) {
                    pageIds.add(section.getId());
                    pageTitles.add(indent + getTitle(section));
                    initSections(section, indent + preffix, preffix);
                }
            }
        }
    }

    protected String getTitle(Section section) {
        return (String) LocaleManager.lookup().localize(section.getTitle());
    }

    /**
     public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
     renderFragment("outputCommandsBar");
     renderFragment("outputStartRow");
     renderFragment("outputHeaderDelete");
     setAttribute("value","ui.title");
     renderFragment("outputHeaders");
     setAttribute("value","ui.url");
     renderFragment("outputHeaders");
     setAttribute("value","ui.sections.visibleSection");
     renderFragment("outputHeaders");
     renderFragment("outputEndRow");

     int n=0;
     User Adapter user = getUser();
     WorkspaceImpl workspace;
     WorkspacePermission workspacePerm;
     SectionPermission sectionPerm;
     try {

     ResourceBundle bundle = localeManager.getBundle("org.jboss.dashboard.ui.messages", getCurrentLocale());

     workspace = (WorkspaceImpl) getSectionsPropertiesHandler().getCurrentWorkspace();
     Section[] sections = workspace.getAllSections();
     for (int i = 0; i < sections.length; i++) {
     sectionPerm = SectionPermission.newInstance(sections[i], SectionPermission.ACTION_VIEW);
     if (!security Manager.hasPermission(user,sectionPerm)) break;
     String estilo;
     if (n % 2==0) estilo="skn-odd_row"; else estilo="skn-even_row";
     renderFragment("outputStartRow");
     setAttribute("value",sections[i].getId());
     setAttribute("estilo",estilo);
     renderFragment("outputDelete");
     setAttribute("value",sections[i].getTitle().get(getLang()));
     setAttribute("estilo",estilo);
     renderFragment("outputTitle");
     String url = (sections[i].getFriendlyUrl()==null) ? "" : sections[i].getFriendlyUrl();
     setAttribute("value",url);
     setAttribute("estilo",estilo);
     renderFragment("outputUrl");
     setAttribute("value",sections[i].isVisible().booleanValue());
     setAttribute("estilo",estilo);
     renderFragment("outputVisible");
     renderFragment("outputEndRow");
     n++;
     }
     if (getCurrentWorkspace()==null) {
     renderFragment("outputEnd");
     return;
     }
     renderFragment("endTable");
     renderFragment("startTable");
     sectionPerm = SectionPermission.newInstance(workspace, SectionPermission.ACTION_CREATE);
     if (security Manager.hasPermission(user,sectionPerm)) {
     renderFragment("outputCreateSection");

     renderFragment("outputCreateSectionSkinsStart");
     GraphicElement[] skins = getSkinsManager().getAvailableElements(workspace.getId(), null, null);
     // Empty skin attribute to give the possibility for no skin selection.
     setAttribute("skinTitle", bundle.getString("ui.admin.configuration.skin.sameGraphicElement"));
     setAttribute("skinId", "");
     renderFragment ("outputCreateSectionSkins");
     for (int i = 0; i < skins.length; i++) {
     Skin skin = (Skin) skins[i];
     setAttribute("skinTitle", skin.getDescription().get(getCurrentLocale().getLanguage()));
     setAttribute("skinId", skin.getId());
     renderFragment("outputCreateSectionSkins");
     }
     renderFragment("outputCreateSectionSkinsEnd");

     renderFragment("outputCreateSectionEnvelopesStart");
     GraphicElement[] envelopes = envelopesManager.getAvailableElements();
     // Empty skin attribute to give the possibility for no envelope selection.
     setAttribute("envelopeId", "");
     setAttribute("envelopeTitle", bundle.getString("ui.admin.configuration.envelope.sameGraphicElement"));
     renderFragment ("outputCreateSectionEnvelopes");
     for (int i=0; i<envelopes.length; i++) {
     Envelope envelope = (Envelope) envelopes[i];
     setAttribute("envelopeId",envelope.getId());
     setAttribute("envelopeTitle",envelope.getDescription().get(getLang()));
     renderFragment("outputCreateSectionEnvelopes");
     }
     renderFragment("outputCreateSectionEnvelopesEnd");

     renderFragment("outputCreateSectionLayoutsStart");
     GraphicElement[] layouts = getLayoutsManager().getAvailableElements();
     for (int i = 0; i < layouts.length; i++) {
     Layout layout = (Layout) layouts[i];
     setAttribute("layoutDescription", layout.getDescription());
     setAttribute("layoutId", layout.getId());
     renderFragment("outputCreateSectionLayouts");
     }
     renderFragment("outputCreateSectionLayoutsEnd");

     renderFragment("outputCreateSectionEnd");
     }
     renderFragment("outputEnd");
     } catch (Exception e) {
     log.error("Error:", e);
     }

     }
     */
}
