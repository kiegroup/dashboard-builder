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
package org.jboss.dashboard.ui.panel.navigation.menu;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.panel.parameters.HTMLTextAreaParameter;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.parameters.ComboListParameter;
import org.jboss.dashboard.ui.taglib.LinkToWorkspaceTag;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelSession;
import org.jboss.dashboard.workspace.Section;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the implementation for the advanced menu panel driver, providing support
 * for all required services.
 * <p/>
 * This class also provides some useful services and shortcuts to make panel
 * developments easier.
 */
public class MenuDriver extends PanelDriver {

     // Pages IDs

    private final static String PAGE_SHOW = "show";
    private final static String PAGE_EDIT_LANG = "edit.lang";
    private final static String PAGE_EDIT_PAGE = "edit.page";
    private final static String PAGE_EDIT_WORKSPACE = "edit.workspace";

    // Parameters

    public static final String PARAMETER_PAGE = "page";
    public static final String PARAMETER_WORKSPACE = "workspace";
    public static final String PARAMETER_ID_WORKSPACE = "idWorkspace";
    public static final String PARAMETER_LANG = "lang";
    public static final String PARAMETER_ALL_ITEMS = "*";

    public static final String ATTRIBUTE_SELECTED_PAGE = "selected_page";
    public static final String ATTRIBUTE_SELECTED_LANG = "selected_lang";
    public static final String ATTRIBUTE_SELECTED_WORKSPACE = "selected_workspace";

    private final static String PARAMETER_TYPE_PANEL = "menu.type";
    private final static String PARAMETER_START_HTML = "start.html";
    private final static String PARAMETER_END_HTML = "end.html";
    private final static String PARAMETER_BEFORE_LINK = "before.link";
    private final static String PARAMETER_AFTER_LINK = "after.link";

    /**
     * This method is called once for each panel instance.
     */
    public void initPanelSession(PanelSession status, HttpSession session) {
        status.setCurrentPageId(PAGE_SHOW);
    }

    /**
     * This method is called once for all panels.
     */
    public void init(PanelProvider provider) throws Exception {
        super.init(provider);
        addParameter(new ComboListParameter(provider, PARAMETER_TYPE_PANEL, true, MenuTypeListDataSupplier.PARAMETER_PAGE, new MenuTypeListDataSupplier(), false));
        addParameter(new HTMLTextAreaParameter(provider, PARAMETER_START_HTML, false, "", false));
        addParameter(new HTMLTextAreaParameter(provider, PARAMETER_END_HTML, false, "", false));
        addParameter(new HTMLTextAreaParameter(provider, PARAMETER_BEFORE_LINK, false, "", false));
        addParameter(new HTMLTextAreaParameter(provider, PARAMETER_AFTER_LINK, false, "&nbsp;", false));
    }

    /**
     * Returns if this driver defines support to activate edit mode.
     *
     * @param panel
     * @return boolean
     */
    public boolean supportsEditMode(Panel panel) {
        return true;
    }

    @Override
    protected String getPageEdit(Panel panel) {
        String redirectTo = getMenuType(panel);
        if (MenuTypeListDataSupplier.PARAMETER_LANGUAGE.equals(redirectTo)) {
            return PAGE_EDIT_LANG;
        } else if (MenuTypeListDataSupplier.PARAMETER_WORKSPACE.equals(redirectTo)) {
            return PAGE_EDIT_WORKSPACE;
        } else {
            return PAGE_EDIT_PAGE;
        }
    }

    /**
     * Returns parameter value fixed in panel definition file.
     *
     * @param parameter Parameter name
     * @param provider
     * @return String
     */
    protected String getFixedParamValue(String parameter, PanelProvider provider) {
        String value = provider.getProperties().getProperty(parameter);
        return (value != null && value.length() > 0) ? value : null;
    }

    /**
     * Returns menu type value.
     *
     * @param panel
     * @return String
     */
    public String getMenuType(Panel panel) {
        return panel.getParameterValue(PARAMETER_TYPE_PANEL);
    }

    /**
     * Returns start html value.
     *
     * @param panel
     * @return String
     */
    public String getStartHTML(Panel panel) {
        return panel.getParameterValue(PARAMETER_START_HTML);
    }

    /**
     * Returns end html value.
     *
     * @param panel
     * @return String
     */
    public String getEndHTML(Panel panel) {
        return panel.getParameterValue(PARAMETER_END_HTML);
    }

    /**
     * Returns before link value.
     *
     * @param panel
     * @return String
     */
    public String getBeforeLink(Panel panel) {
        return panel.getParameterValue(PARAMETER_BEFORE_LINK);
    }

    /**
     * Returns after link value.
     *
     * @param panel
     * @return String
     */
    public String getAfterLink(Panel panel) {
        return panel.getParameterValue(PARAMETER_AFTER_LINK);
    }

    /**
     * Returns a URL for a given language change
     *
     * @param currentPage  Current workspace page.
     * @param lang lang to change to
     * @return the URL.
     */
    protected String getChangeLanguageLink(Section currentPage, String lang) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("lang", lang);
        String linkStr = UIServices.lookup().getUrlMarkupGenerator().getLinkToPage(currentPage, true, lang);
        return linkStr;
    }

    /**
     * Returns a URL for a given handler and action, setting the current view, appending a string
     * of parameters.
     *
     * @param req      Request.
     * @param res      Response.
     * @param workspaceId Workspace identifier.
     * @return the URL.
     */
    protected String getChangeWorkspaceLink(HttpServletRequest req, HttpServletResponse res, String workspaceId) {
        return LinkToWorkspaceTag.getLink(req, res, workspaceId);
    }

    /**
     * Returns a URL for a given handler and action, setting the current view, appending a string
     * of parameters.
     *
     * @param req     Request.
     * @param res     Response.
     * @param section Section.
     * @return the URL.
     */
    protected String getChangePageLink(HttpServletRequest req, HttpServletResponse res, Section section) {
        return UIServices.lookup().getUrlMarkupGenerator().getLinkToPage(section, true);
    }

    /**
     * Returns identifiers selected from pages, workspaces and languages.
     *
     * @param panel Panel.
     * @return selectedMenuItemsIds
     */
    protected Map<String, List<String>> getSelectedMenuItemsIds(Panel panel) {
        Map<String, List<String>> selectedMenuItemsIds = (Map) panel.getContentData();
        if (selectedMenuItemsIds == null || selectedMenuItemsIds.isEmpty()) {
            ArrayList<String> allPages = new ArrayList<String>();
            allPages.add(PARAMETER_ALL_ITEMS);
            ArrayList<String> allWorkspaces = new ArrayList<String>();
            allWorkspaces.add(PARAMETER_ALL_ITEMS);
            ArrayList<String> allLangs = new ArrayList<String>();
            allLangs.add(PARAMETER_ALL_ITEMS);

            selectedMenuItemsIds = new HashMap<String, List<String>>();
            selectedMenuItemsIds.put(PARAMETER_PAGE, allPages);
            selectedMenuItemsIds.put(PARAMETER_WORKSPACE, allWorkspaces);
            selectedMenuItemsIds.put(PARAMETER_LANG, allLangs);
        }
        return selectedMenuItemsIds;
    }

    /**
     * Returns language identifiers selected.
     *
     * @param panel Panel.
     * @return selectedLangIds
     */
    public List<String> getSelectedLangIds(Panel panel) {
        return getSelectedMenuItemsIds(panel).get(PARAMETER_LANG);
    }

    /**
     * Returns page identifiers selected.
     *
     * @param panel Panel.
     * @return selectedPageIds
     */
    public List<String> getSelectedPageIds(Panel panel) {
        return getSelectedMenuItemsIds(panel).get(PARAMETER_PAGE);
    }

    /**
     * Returns workspace identifiers selected.
     *
     * @param panel Panel.
     * @return selectedWorkspaceIds
     */
    public List<String> getSelectedWorkspaceIds(Panel panel) {
        return getSelectedMenuItemsIds(panel).get(PARAMETER_WORKSPACE);
    }

    /**
     * Defines the action to be taken when save languages selected
     *
     * @param panel Panel.
     * @param request CommandRequest.
     * @return CommandResponse
     */
    public CommandResponse actionSaveLangs(Panel panel, CommandRequest request) throws Exception {
        removeAllVisibleMenuItem(panel, PARAMETER_LANG);

        String[] visibleItems = request.getRequestObject().getParameterValues(ATTRIBUTE_SELECTED_LANG);
        if (visibleItems != null) {
            for (String item : visibleItems) {
                if (item != null && item.trim().length() > 0) {
                    addVisibleMenuItem(panel, item, PARAMETER_LANG);
                }
            }
        } else {
            addVisibleMenuItem(panel, PARAMETER_ALL_ITEMS, PARAMETER_LANG);
        }
        panel.getInstance().persist();

        return panelActionShowMode(panel, request);
    }

    /**
     * Defines the action to be taken when save workspaces selected
     *
     * @param panel Panel.
     * @param request CommandRequest.
     * @return CommandResponse
     */
    public CommandResponse actionSaveWorkspaces(Panel panel, CommandRequest request) throws Exception {
        removeAllVisibleMenuItem(panel, PARAMETER_WORKSPACE);

        String[] visible_items = request.getRequestObject().getParameterValues(ATTRIBUTE_SELECTED_WORKSPACE);
        if (visible_items != null && visible_items.length > 0) {
            for (int i = 0; i < visible_items.length; i++) {
                if (visible_items[i] != null && visible_items[i].trim().length() > 0) {
                    addVisibleMenuItem(panel, visible_items[i], PARAMETER_WORKSPACE);
                }
            }
        } else {
            addVisibleMenuItem(panel, PARAMETER_ALL_ITEMS, PARAMETER_WORKSPACE);
        }
        panel.getInstance().persist();

        return panelActionShowMode(panel, request);
    }

    /**
     * Defines the action to be taken when save pages selected
     *
     * @param panel Panel.
     * @param request CommandRequest.
     * @return CommandResponse
     */
    public CommandResponse actionSavePages(Panel panel, CommandRequest request) throws Exception {
        removeAllVisibleMenuItem(panel, PARAMETER_PAGE);

        String[] visibleItems = request.getRequestObject().getParameterValues(ATTRIBUTE_SELECTED_PAGE);
        if (visibleItems != null) {
            for (String item : visibleItems) {
                if (item != null && item.trim().length() > 0) {
                    addVisibleMenuItem(panel, item, PARAMETER_PAGE);
                }
            }
        } else {
            addVisibleMenuItem(panel, PARAMETER_ALL_ITEMS, PARAMETER_PAGE);
        }
        panel.getInstance().persist();

        return panelActionShowMode(panel, request);
    }

    /**
     * Removes all visible menu items selected in panel
     *
     * @param panel  Panel.
     * @param menuItem MenuItem.
     */
    private void removeAllVisibleMenuItem(Panel panel, String menuItem) {
        Map<String, List<String>> selectedMenuItemsIds = getSelectedMenuItemsIds(panel);
        selectedMenuItemsIds.put(menuItem, new ArrayList<String>());
        panel.setContentData((HashMap) selectedMenuItemsIds);
    }

    /**
     * Adds a menu item in an identifiers list
     *
     * @param panel  Panel.
     * @param menuItem MenuItem.
     */
    private void addVisibleMenuItem(Panel panel, String itemId, String menuItem) {
        Map<String, List<String>> selectedMenuItemsIds = getSelectedMenuItemsIds(panel);
        List<String> item = selectedMenuItemsIds.get(menuItem);
        if (itemId != null && itemId.length() > 0 && !item.contains(itemId)) {
            item.add(itemId);
        }
    }
}
