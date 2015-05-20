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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.ui.taglib.LocalizeTag;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelSession;
import org.jboss.dashboard.workspace.SectionRegion;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Formatter that displays a panel menu
 */
public class RenderPanelMenuFormatter extends Formatter {

    @Inject @Config("panelProperties/CloseMenu.png")
    private String closeIco;

    @Inject @Config("panelProperties/EditMod.png")
    private String editModeIco;

    @Inject @Config("panelProperties/Erase.png")
    private String eraseIco;

    @Inject @Config("panelProperties/HelpMod.png")
    private String helpModeIco;

    @Inject @Config("panelProperties/PropertiesMod.png")
    private String propertiesModeIco;

    @Inject @Config("panelProperties/ShowMod.png")
    private String showModeIco;

    @Inject @Config("panelProperties/Refresh.png")
    private String refreshIco;

    /**
     * Perform the required logic for this Formatter. Inside, the methods
     * setAttribute and renderFragment are intended to be used to generate the
     * output and set parameters for this output.
     * Method getParameter is intended to retrieve input parameters by name.
     * <p/>
     * Exceptions are to be catched inside the method, and not to be thrown, normally,
     * formatters could use a error fragment to be displayed when an error happens
     * in displaying. But if the error is unexpected, it can be wrapped inside a
     * FormatterException.
     *
     * @param request  user request
     * @param response response to the user
     * @throws org.jboss.dashboard.ui.taglib.formatter.FormatterException
     *          in case of an unexpected exception.
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        Panel panel = (Panel) getParameter("panel");
        PanelSession status = panel.getPanelSession();
        SectionRegion sectionRegion = panel.getSection().getSectionRegion(panel.getRegion().getId());
        boolean columnRegion = panel.getRegion().isColumnRegion();
        WorkspaceImpl workspace = NavigationManager.lookup().getCurrentWorkspace();
        boolean userIsAdmin = false;
        if (workspace != null) {
            WorkspacePermission permToCheck = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_ADMIN);
            userIsAdmin = UserStatus.lookup().hasPermission(permToCheck);
        }

        if (!userIsAdmin) return;

        PanelPermission editPanelPerm = PanelPermission.newInstance(panel, PanelPermission.ACTION_EDIT);
        boolean userCanEditPanel = UserStatus.lookup().hasPermission(editPanelPerm);

        renderFragment("movePanel");

        setAttribute("cursorStyle", "move");
        setAttribute("title", StringEscapeUtils.ESCAPE_HTML4.translate((String) LocaleManager.lookup().localize(panel.getTitle())));
        renderFragment("menuLink");
        renderFragment("menuStart");

        setAttribute("title", LocalizeTag.getLocalizedValue(panel.getTitle(), getLang(), true));
        renderFragment("menuTitle");

        //Invalidate cache button.
        if (panel.getCacheTime() > 0)
            renderMenuOption("ui.panel.refresh", getPanelActionLink(request, response, panel, "_invalidate-cache"), refreshIco, "R", panel.getCacheTime() > 0);

        if (status.isShowMode()) {
            boolean isEditModeEnabled = !status.isEditMode() && panel.supportsEditMode() && userCanEditPanel;
            //Edit button.
            if (isEditModeEnabled) renderMenuOption("ui.panel.editMode", getPanelActionLink(request, response, panel, "_edit-mode"), editModeIco, "L", isEditModeEnabled);
        } else {
            //Show mode.
            renderMenuOption("ui.panel.showMode", getPanelActionLink(request, response, panel, "_show-mode"), showModeIco, "S", !status.isShowMode());
        }
        //Properties mode
        renderMenuOption("ui.panel.editProperties", getPanelActionLink(request, response, panel, "_start-config"), propertiesModeIco, "P", true);

        //Close button
        renderFragment("menuSeparator");
        renderMenuOption("ui.panel.close", getPanelActionLink(request, response, panel, "_close"), eraseIco, "D", true, false);

        //Help mode
        renderMenuOption("ui.panel.helpMode", getPanelActionLink(request, response, panel, "_help-mode"), helpModeIco, "?", !status.isHelpMode() && panel.supportsHelpMode());

        setAttribute("imageKey", closeIco);
        renderFragment("menuEnd");
    }

    protected void renderMenuOption(String key, String url, String image, String alt, boolean enabled) {
        renderMenuOption(key, url, image, alt, enabled, true);
    }

    protected void renderMenuOption(String key, String url, String image, String alt, boolean enabled, boolean isAjax) {
        setAttribute("menukey", key);
        setAttribute("url", url);
        setAttribute("imageKey", image);
        setAttribute("imageAlt", alt);
        setAttribute("isAjax", isAjax);
        renderFragment("menuEntry" + (enabled ? "" : "Disabled"));
    }

    protected String getPanelActionLink(HttpServletRequest request, HttpServletResponse response, Panel panel, String action) {
        URLMarkupGenerator markupGenerator = UIServices.lookup().getUrlMarkupGenerator();
        return markupGenerator.getLinkToPanelAction(panel, action, true);
    }

    public String getEditModeIco() {
        return editModeIco;
    }

    public void setEditModeIco(String editModeIco) {
        this.editModeIco = editModeIco;
    }

    public String getEraseIco() {
        return eraseIco;
    }

    public void setEraseIco(String eraseIco) {
        this.eraseIco = eraseIco;
    }

    public String getHelpModeIco() {
        return helpModeIco;
    }

    public void setHelpMode(String helpModeIco) {
        this.helpModeIco = helpModeIco;
    }

    public String getPropertiesModeIco() {
        return propertiesModeIco;
    }

    public void setPropertiesModeIco(String propertiesModeIco) {
        this.propertiesModeIco = propertiesModeIco;
    }

    public String getShowModeIco() {
        return showModeIco;
    }

    public void setShowModeIco(String showModeIco) {
        this.showModeIco = showModeIco;
    }

    public String getRefreshIco() {
        return refreshIco;
    }

    public void setRefreshIco(String refreshIco) {
        this.refreshIco = refreshIco;
    }

    public String getCloseIco() {
        return closeIco;
    }

    public void setCloseIco(String closeIco) {
        this.closeIco = closeIco;
    }
}
