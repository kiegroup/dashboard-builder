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
package org.jboss.dashboard.ui.panel;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.commons.text.JavaNamesFormatter;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.responses.ShowPanelPage;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.components.FactoryRequestHandler;
import org.jboss.dashboard.ui.components.HandlerFactoryElement;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.jboss.dashboard.ui.panel.parameters.*;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.ui.components.panelManagement.ShowPanelConfigComponent;
import org.jboss.dashboard.ui.components.panelManagement.ShowPanelPageComponent;
import org.jboss.dashboard.ui.controller.responses.PanelAjaxResponse;
import org.jboss.dashboard.ui.controller.responses.ShowJSPAjaxResponse;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.security.PanelSecurity;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.ui.utils.javascriptUtils.JavascriptTree;
import org.hibernate.Session;
import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.workspace.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.*;

/**
 * This is the basic implementation for a functional panel driver, providing support
 * for all required services. Every Driver has to subclass
 * PanelDriver, and implement just it's own actions.
 * <p/>
 * This class also provides some useful services and shortcuts to make panel
 * developments easier.
 */
public class PanelDriver {

    public final static String PAGE_MANAGE_INVALID_DRIVER = "manage_invalid_panel_driver";
    public final static String PAGE_HELP_MODE = "page_help_mode";
    public final static String PAGE_SHOW = "show";
    public final static String PAGE_EDIT = "edit";
    public final static String PAGE_HEADER = "header";
    public static final String PARAMETER_ACTION_EXECUTED_ENABLED = "actionExecutedEnabled";

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelDriver.class.getName());

    /**
     * Parameters definitions to be supplied to the panel
     */
    protected List parameters = new ArrayList();

    /**
     * Security table
     */
    protected PanelSecurity panelSecurity;

    /**
     * Returns all the parameters this panel driver allows to configure.
     */
    public PanelProviderParameter[] getAllParameters() {
        return (PanelProviderParameter[]) parameters.toArray(new PanelProviderParameter[parameters.size()]);
    }

    /**
     * Adds a parameter for this panel driver. This method is intended to be called from the init method.
     *
     * @param parameter Parameter to add.
     */
    protected void addParameter(PanelProviderParameter parameter) {
        parameter.setSystemParameter(false);
        doAddParameter(parameter);
    }

    /**
     * Adds an array of parameters for this panel driver. This method is intended to be called from the init method.
     *
     * @param parameters Parameters to add.
     */
    protected void addParameters(PanelProviderParameter[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            addParameter(parameters[i]);
        }
    }

    private void doAddParameter(PanelProviderParameter parameter) {
        Properties properties = parameter.getProvider().getProperties();
        String parameterId = parameter.getId();
        if (parameter.isI18n()) {
            LocaleManager localeManager = LocaleManager.lookup();
            String[] langs = localeManager.getPlatformAvailableLangs();
            for (int i = 0; i < langs.length; i++) {
                String lang = langs[i];
                String defaultValueInLang = properties.getProperty("parameter." + parameterId + "." + lang + ".default");
                if (!StringUtils.isEmpty(defaultValueInLang))
                    parameter.setDefaultValue(defaultValueInLang, lang);
            }
        } else if (!properties.containsKey("parameter." + parameterId)) {
            parameter.setDefaultValue(properties.getProperty("parameter." + parameterId + ".default", parameter.getDefaultValue()));
        }
        parameters.remove(parameter);
        this.parameters.add(parameter);
    }

    /**
     * Adds a system parameter.
     *
     * @param parameter Parameter to add
     */
    protected void addSystemParameter(PanelProviderParameter parameter) {
        parameter.setSystemParameter(true);
        doAddParameter(parameter);
    }

    /**
     * Adds a security restriction to a method in this panel.
     *
     * @param methodName      Method name
     * @param permissionClass Type of permission to apply before invoking it
     * @param action          action on the resource indicated by the permission
     * @throws NoSuchMethodException
     */
    protected void addMethodPermission(String methodName, Class permissionClass, String action) throws NoSuchMethodException {
        if (panelSecurity == null)
            panelSecurity = new PanelSecurity();
        //This is to verify that method exists
        this.getClass().getMethod(methodName, new Class[]{Panel.class, CommandRequest.class});
        panelSecurity.addMethodPermission(methodName, permissionClass, action);
    }

    /**
     * Called on provider initialization. Subclasses should override it to add custom parameters. Be sure to call super()
     * if you override it.
     *
     * @param provider Panel provider for this driver.
     */
    public void init(PanelProvider provider) throws Exception {
        initSystemParameters(provider);
        initPermissionsParameters();
    }

    protected void initSystemParameters(PanelProvider provider) {
        log.debug("Init driver. Adding system parameters");
        addSystemParameter(new StringParameter(provider, PanelInstance.PARAMETER_GROUP, false, false));
        addSystemParameter(new StringParameter(provider, PanelInstance.PARAMETER_TITLE, true, true));
        addParameter(new HTMLTextAreaParameter(provider, PanelInstance.PARAMETER_HTML_BEFORE, false, true));
        addParameter(new HTMLTextAreaParameter(provider, PanelInstance.PARAMETER_HTML_AFTER, false, true));

        // Probably will be deleted but not decided yet.
        //addSystemParameter(new IntParameter(provider, PanelInstance.PARAMETER_HEIGHT, true, "0"));
        //addSystemParameter(new BooleanParameter(provider, PanelInstance.PARAMETER_MAXIMIZABLE, false, false));
        //addSystemParameter(new BooleanParameter(provider, PanelInstance.PARAMETER_MINIMIZABLE, false, false));
        //addSystemParameter(new BooleanParameter(provider, PanelInstance.PARAMETER_PAINT_TITLE, false, false));
        //addSystemParameter(new BooleanParameter(provider, PanelInstance.PARAMETER_PAINT_BORDER, false, false));
        //addSystemParameter(new BooleanParameter(provider, PanelInstance.PARAMETER_SESSION_KEEP_ALIVE, false, false));
    }

    protected void initPermissionsParameters() throws NoSuchMethodException {
        Method[] methods = this.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if ((method.getName().startsWith("action") || method.getName().startsWith("panelAction"))
                && method.getParameterTypes().length == 2
                && method.getParameterTypes()[0].equals(Panel.class)
                && method.getParameterTypes()[1].equals(CommandRequest.class)
                && CommandResponse.class.equals(method.getReturnType())) {

                addMethodPermission(method.getName(), PanelPermission.class, PanelPermission.ACTION_VIEW);
                if ("panelActionEditMode".equals(method.getName()))
                    addMethodPermission(method.getName(), PanelPermission.class, PanelPermission.ACTION_EDIT);
                if ("panelActionMaximize".equals(method.getName()))
                    addMethodPermission(method.getName(), PanelPermission.class, PanelPermission.ACTION_MAXIMIZE);
                if ("panelActionMinimize".equals(method.getName()))
                    addMethodPermission(method.getName(), PanelPermission.class, PanelPermission.ACTION_MINIMIZE);
                if ("panelActionMoveBack".equals(method.getName()))
                    addMethodPermission(method.getName(), WorkspacePermission.class, WorkspacePermission.ACTION_ADMIN);
                if ("panelActionMoveForward".equals(method.getName()))
                    addMethodPermission(method.getName(), WorkspacePermission.class, WorkspacePermission.ACTION_ADMIN);
                if ("panelActionRemove".equals(method.getName()))
                    addMethodPermission(method.getName(), WorkspacePermission.class, WorkspacePermission.ACTION_ADMIN);
                if ("panelActionResourcePreview".equals(method.getName()))
                    addMethodPermission(method.getName(), PanelPermission.class, PanelPermission.ACTION_EDIT);
                if ("panelActionResourcePreviewConfirm".equals(method.getName()))
                    addMethodPermission(method.getName(), PanelPermission.class, PanelPermission.ACTION_EDIT);
                if ("panelActionResourcesMode".equals(method.getName()))
                    addMethodPermission(method.getName(), PanelPermission.class, PanelPermission.ACTION_EDIT);
                if ("panelActionRestore".equals(method.getName()))
                    addMethodPermission(method.getName(), PanelPermission.class, PanelPermission.ACTION_MINIMIZE);
            }
        }
    }

    /**
     * Called on panel initialization
     *
     * @param instance PanelInstance where the panel belongs.
     * @throws Exception
     */
    public void initPanel(PanelInstance instance) throws Exception {
    }

    /**
     * Return the panel session related to this panel.
     *
     * @param panel
     * @return The panel session related to this panel.
     */
    public PanelSession getPanelSession(Panel panel) {
        return SessionManager.getPanelSession(panel);
    }

    /**
     * @deprecated use getPanelSession(Panel panel)
     */
    public PanelSession getPanelSession(CommandRequest req, Panel panel) {
        return SessionManager.getPanelSession(panel);
    }

    /**
     * @deprecated use getPanelSession(Panel panel)
     */
    public PanelSession getPanelSession(HttpServletRequest req, Panel panel) {
        return SessionManager.getPanelSession(panel);
    }

    /**
     * Called before dispatching to an action method
     *
     * @param panel
     * @param request
     * @return Null if the action execution can continue, or a CommandResponse if not
     */
    protected CommandResponse beforeInvokeAction(Panel panel, CommandRequest request) {
        LayoutRegionStatus regionStatus = SessionManager.getRegionStatus(panel.getSection(), panel.getRegion());
        PanelSession panelStatus = getPanelSession(panel);

        if (regionStatus != null) {
            regionStatus.setSelectedPanel(panel);

            if (panelStatus.isMinimized()) {
                panelStatus.setStatus(PanelSession.STATUS_REGULAR_SIZE);
            }
        }
        return null;
    }

    /**
     * Called after executing an action method
     *
     * @param panel
     * @param request
     * @param actionResponse Panel response
     * @return same panel response or a different one if any condition makes it necessary a different response
     */
    protected CommandResponse afterInvokeAction(Panel panel, CommandRequest request, CommandResponse actionResponse) {
        return actionResponse;
    }

    /**
     * Called before render the panel.
     *
     * @param panel
     * @param request
     * @param response
     */
    protected void beforeRenderPanel(Panel panel, HttpServletRequest request, HttpServletResponse response) {
        PanelProviderParameter[] params = panel.getProvider().getDriver().getAllParameters();
        String lang = LocaleManager.currentLang();
        for (int i = 0; i < params.length; i++) {
            PanelProviderParameter param = params[i];
            String paramId = param.getId();
            String value = panel.getParameterValue(paramId);
            if (param.isI18n()) {
                String valueInCurrentLang = panel.getParameterValue(paramId, lang);
                value = StringUtils.isEmpty(valueInCurrentLang) ? value : valueInCurrentLang;
            }
            request.setAttribute(paramId, value);
        }
    }

    /**
     * Called after panel has been redered
     *
     * @param panel
     * @param request
     */
    protected void afterRenderPanel(Panel panel, HttpServletRequest request, HttpServletResponse response) {
        PanelParameter[] panelParameters = panel.getInstance().getPanelParameters();
        for (int i = 0; i < panelParameters.length; i++) {
            PanelParameter panelParameter = panelParameters[i];
            request.removeAttribute(panelParameter.getIdParameter());
        }
        PanelSession session = SessionManager.getPanelSession(panel);
        session.setAttribute(PARAMETER_ACTION_EXECUTED_ENABLED, Boolean.TRUE);
    }

    /**
     * @deprecated use the version without session, and create your txFragment if needed.
     */
    protected void beforePanelInstanceRemove(PanelInstance instance, Session session) throws Exception {
        beforePanelInstanceRemove(instance);
    }

    /**
     * Called when a panel instance is removed from the system. This is to
     * allow the driver to cleanup any data attached to this panel instance, if needed.
     * Subclasses implementing this method must first call to super.
     *
     * @param instance Panel which is about to be removed
     */
    protected void beforePanelInstanceRemove(PanelInstance instance) throws Exception {
        // Empty panel dir, if needed
        try {
            File dir = getPanelDir(instance);
            log.debug("Directory to delete is " + dir.getCanonicalPath());
            if (dir.exists()) {
                log.debug("Emptying panel directory " + dir.getCanonicalPath());
                // Empty directory
                recursiveDelete(dir);
            }
        } catch (IOException e) {
            log.error("Error removing panel", e);
        }
    }

    /**
     * Fires in the same transaction, just before a panel is closed
     *
     * @param panel panel closed
     */
    protected void beforePanelClosed(Panel panel) {

    }

    /**
     * Fires in the same transaction, just after a panel is closed
     *
     * @param panel panel closed
     */
    protected void afterPanelClosed(Panel panel) throws Exception {
    }

    /**
     * Fires in the same transaction, just after a panel is closed
     *
     * @param panel panel closed
     */
    public final void fireAfterPanelClosed(Panel panel) throws Exception {
        afterPanelClosed(panel);
    }


    /**
     * Fires before the panel is removed
     *
     * @param panel
     */
    protected void beforePanelRemoved(Panel panel) throws Exception {

    }

    /**
     * Fires in the same transaction, just before a panel is put in a region
     *
     * @param panel   panel closed
     * @param newRegion region where the panel is being placed
     */
    protected void beforePanelPlacedInRegion(Panel panel, LayoutRegion newRegion) {

    }

    /**
     * Fires in the same transaction, just after a panel is put in a region
     *
     * @param panel   panel closed
     * @param oldRegion region where the panel was before being moved.
     */
    protected void afterPanelPlacedInRegion(Panel panel, LayoutRegion oldRegion) throws Exception {
    }

    /**
     * Called when the a panel properties have been manually modified
     *
     * @param instance
     */
    protected void afterPanelPropertiesModified(PanelInstance instance) {
        JavascriptTree.regenerateTrees(instance.getWorkspace().getId());
    }

    /**
     * Called when the a panel custom properties have been manually modified
     *
     * @param instance
     */
    protected void afterPanelCustomPropertiesModified(PanelInstance instance) {
    }

    /*
     *
     * Actions shared by all panels (they may be overriden by subclasses)
     *
     */

    /**
     * Maximizes the panel
     */
    public CommandResponse panelActionMaximize(Panel panel, CommandRequest request) {
        log.debug("Maximizing panel " + panel.getPanelId());
        getPanelSession(panel).setStatus(PanelSession.STATUS_MAXIMIZED);
        return new ShowPanelPage();
    }

    /**
     * Maximizes the panel in region
     */
    public CommandResponse panelActionMaximizeInRegion(Panel panel, CommandRequest request) {
        log.debug("Maximizing panel in region " + panel.getPanelId());
        getPanelSession(panel).setStatus(PanelSession.STATUS_MAXIMIZED_IN_REGION);
        return new ShowPanelPage();
    }

    /**
     * Minimizes the panel
     */
    public CommandResponse panelActionMinimize(Panel panel, CommandRequest request) {
        log.debug("Minimizing panel " + panel.getPanelId());
        getPanelSession(panel).setStatus(PanelSession.STATUS_MINIMIZED);
        return new ShowPanelPage();
    }


    /**
     * Restores the panel to its regular size
     */
    public CommandResponse panelActionRestore(Panel panel, CommandRequest request) {
        log.debug("Restoring panel " + panel.getPanelId());
        getPanelSession(panel).setStatus(PanelSession.STATUS_REGULAR_SIZE);
        return new ShowPanelPage();
    }

    /**
     * Sets the panel in show mode
     */
    public CommandResponse panelActionShowMode(Panel panel, CommandRequest request) throws Exception {
        log.debug("Setting panel in configuration mode " + panel.getPanelId());
        activateNormalMode(panel, request);
        return new ShowPanelPage();
    }

    /**
     * Sets the panel in edit mode
     */
    public CommandResponse panelActionEditMode(Panel panel, CommandRequest request) throws Exception {
        if (supportsEditMode(panel)) {
            log.debug("Setting panel in edit mode " + panel.getPanelId());
            activateEditMode(panel, request);
        }
        return new ShowPanelPage();
    }

    /**
     * Sets the panel in help management mode
     */
    public CommandResponse panelActionHelpMode(Panel panel, CommandRequest request) throws Exception {
        if (supportsHelpMode(panel)) {
            log.debug("Setting panel in help mode " + panel.getPanelId());
            activateHelpMode(panel, request);
        }
        return new ShowPanelPage();
    }

    public CommandResponse panelActionStartConfig(Panel panel, CommandRequest request) throws Exception {
        log.debug("Setting panel in config mode " + panel.getPanelId());
        activateConfigMode(panel, request);
        return new ShowPanelPage();
    }

    protected String getPageHelpMode(Panel panel) {
        return PAGE_HELP_MODE;
    }

    protected String getPageEdit(Panel panel) {
        return PAGE_EDIT;
    }


    public void activateHelpMode(Panel panel, CommandRequest request) throws Exception {
        getPanelSession(panel).setWorkMode(PanelSession.HELP_MODE);
        getShowPanelPageComponent().openDialog(panel, request,  getPageHelpMode(panel), getActionsBundle().getString("title.help"), getHelpWidth(panel, request), getHelpHeight(panel, request));
    }

    public int getHelpWidth(Panel panel, CommandRequest request) {
        return 800;
    }

    public int getHelpHeight(Panel panel, CommandRequest request) {
        return 600;
    }

    /**
     * Defines the action to be taken when activating edit mode
     *
     * @param panel
     * @param request
     */
    public void activateNormalMode(Panel panel, CommandRequest request) throws Exception {
        getPanelSession(panel).setWorkMode(PanelSession.SHOW_MODE);
        getShowPanelPageComponent().closePopup();
    }

    /**
     * Defines the action to be taken when activating edit mode
     *
     * @param panel
     * @param request
     */
    public void activateEditMode(Panel panel, CommandRequest request) throws Exception {
        getPanelSession(panel).setWorkMode(PanelSession.EDIT_MODE);
        getShowPanelPageComponent().openDialog(panel, request, getPageEdit(panel), getActionsBundle().getString("title.edit"), getEditWidth(panel, request), getEditHeight(panel, request));
    }

    public int getEditWidth(Panel panel, CommandRequest request) {
        return 800;
    }

    public int getEditHeight(Panel panel, CommandRequest request) {
        return 600;
    }

    public void activateConfigMode(Panel panel, CommandRequest request) throws Exception {
        getPanelSession(panel).setWorkMode(PanelSession.CONFIGURATION_MODE);
        getShowPanelConfigComponent().openDialog(panel, request, getActionsBundle().getString("title.edit"), getConfigWidth(panel, request), getConfigHeight(panel, request));
    }

    public int getConfigWidth(Panel panel, CommandRequest request) {
        return 800;
    }

    public int getConfigHeight(Panel panel, CommandRequest request) {
        return 600;
    }

    private ShowPanelConfigComponent getShowPanelConfigComponent() {
        return ShowPanelConfigComponent.lookup();
    }

    public ResourceBundle getActionsBundle() {
        return ResourceBundle.getBundle("org.jboss.dashboard.ui.components.panelManagement.messages", LocaleManager.currentLocale());
    }

    /**
     * Removes panel from its region, but keeping it assigned to this section.
     */
    public CommandResponse panelActionClose(final Panel panel, CommandRequest request) {

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                log.debug("Closing panel " + panel.getPanelId());
                beforePanelClosed(panel);
                Section section = panel.getSection();
                section.removePanelFromRegion(panel);
                UIServices.lookup().getSectionsManager().store(section);
                fireAfterPanelClosed(panel);
            }
        };

        try {
            txFragment.execute();
        } catch (Exception e) {
            log.error("Can't remove panel from region.", e);
        }
        return new ShowPanelPage();
    }

    /**
     * Moves panel backwards in its region
     */
    public CommandResponse panelActionMoveBack(final Panel panel, CommandRequest request) {

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Section section = NavigationManager.lookup().getCurrentSection();
                if (section != null) section.moveBackInRegion(panel);
                UIServices.lookup().getSectionsManager().store(section);
            }
        };

        try {
            txFragment.execute();
        } catch (Exception e) {
            log.error("Can't move back panel in region.", e);
        }
        return new ShowPanelPage();
    }

    /**
     * Moves a panel from one region to another
     *
     * @param panel
     * @param regionName new region name
     */
    public void move(Panel panel, String regionName) throws Exception {
        Section section = panel.getSection();
        log.debug("Moving panel " + panel.getPanelId());

        if (regionName != null) {
            log.debug("Region name " + regionName);
            LayoutRegion oldRegion = panel.getRegion();
            LayoutRegion region = section.getLayout().getRegion(regionName);
            fireBeforePanelPlacedInRegion(panel, region);

            if (region != null) {
                log.debug("Moving panel to region " + region.getId());
                section.assignPanel(panel, region);
                if (oldRegion != null) {
                    LayoutRegionStatus status = SessionManager.getRegionStatus(panel.getSection(), oldRegion);
                    if (status.isSelected(panel)) {
                        status.setSelectedPanel(null);
                    }
                }
            }
            fireAfterPanelPlacedInRegion(panel, oldRegion);
        }

    }

    /**
     * Moves panel forward in its region
     */
    public CommandResponse panelActionMoveForward(final Panel panel, CommandRequest request) {
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Section section = NavigationManager.lookup().getCurrentSection();
                if (section != null) section.moveForwardInRegion(panel);
                UIServices.lookup().getSectionsManager().store(section);
            }
        };
        try {
            txFragment.execute();
        } catch (Exception e) {
            log.error("Can't move forward panel in region.", e);
        }
        return new ShowPanelPage();
    }

    /**
     * Removes panel from system
     */
    public CommandResponse panelActionRemove(final Panel panel, CommandRequest request) {
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Section section = NavigationManager.lookup().getCurrentSection();
                if (section != null) {
                    section.removePanel(panel);
                    UIServices.lookup().getSectionsManager().store(section);
                }
            }
        };
        try {
            txFragment.execute();
        } catch (Exception e) {
            log.error("Panel " + panel.getPanelId() + " can't be removed.", e);
        }
        return new ShowPanelPage();
    }

    /**
     * Invalidates panel cache
     */
    public CommandResponse panelActionInvalidateCache(Panel panel, CommandRequest request) {
        // TODO: Invalidate panel cache here
        return new ShowPanelPage();
    }


    /**
     * Marks panel as selected in its region
     */
    public CommandResponse panelActionSelect(Panel panel, CommandRequest request) {
        LayoutRegionStatus regionStatus = SessionManager.getRegionStatus(panel.getSection(), panel.getRegion());
        PanelSession panelStatus = getPanelSession(panel);

        if (regionStatus != null) {
            regionStatus.setSelectedPanel(panel);

            if (panelStatus.isMinimized()) {
                panelStatus.setStatus(PanelSession.STATUS_REGULAR_SIZE);
            }
        }

        if (StringUtils.defaultString(request.getRequestObject().getHeader("user-agent")).indexOf("MSIE") != -1)
            return new ShowPanelPage();

        return new ShowJSPAjaxResponse("/section/render_tabbed_region.jsp", panel.getRegion());
    }

    /**
     * Refresh a panel
     *
     * @param panel
     * @param request
     * @return ShowPanelPage
     */
    public CommandResponse panelActionRefreshPanel(Panel panel, CommandRequest request) {
        return new ShowPanelPage();
    }

    /**
     * Determine the method name for a given action parameter.<br>
     * Examples:
     * <li> start -> actionStart
     * <li> _start -> panelActionStart
     * <li> retrieve-file -> actionRetrieveFile
     * <li> retrieveFile -> actionRetrieveFile
     *
     * @param action Action to be performed
     * @return equivalent method name.
     */
    public static String getMethodName(String action) {
        String prefix;
        if (isSystemAction(action)) {
            // System commands
            prefix = "panel-action";
            action = action.substring(1);
        } else {
            prefix = "action";
        }
        return JavaNamesFormatter.toJavaName(prefix + "-" + action, false);
    }

    /**
     * Determine if given action is a system action (currently, if it starts with "_".
     *
     * @param action Action to process
     * @return true if it is a system action.
     */
    public static boolean isSystemAction(String action) {
        return action.startsWith("_");
    }


    /**
     * Execute an action on this panel.
     */
    public CommandResponse execute(Panel panel, CommandRequest req) throws Exception {
        String action = req.getParameter(Parameters.DISPATCH_ACTION);
        PanelSession session = SessionManager.getPanelSession(panel);
        if (!isSystemAction(action) && !Boolean.TRUE.equals(session.getAttribute(PARAMETER_ACTION_EXECUTED_ENABLED)) && isDoubleClickProtected(action)) {
            // Factory actions have their own double click control.
            log.warn("Discarding duplicated execution in panel " + panel.getInstance().getProvider().getDescription() + ", action: " + action + ". User should be advised not to double click!");
            return new ShowPanelPage();
        }
        if (!isSystemAction(action)) {
            session.removeAttribute(PARAMETER_ACTION_EXECUTED_ENABLED);
        }

        // Get the panel's action method.
        String methodName = getMethodName(action);
        if (log.isDebugEnabled()) log.debug("Invoking method " + methodName + " for class " + this.getClass().getName());
        Class[] params = {Panel.class, CommandRequest.class};
        Object[] args = {panel, req};
        Class cmdClass = this.getClass();
        Method method = cmdClass.getMethod(methodName, params);

        // Check the user has permission to access the panel.
        if (panelSecurity != null) {
            Set securityEntries = panelSecurity.entrySet(method.getName());
            for (Iterator it = securityEntries.iterator(); it.hasNext();) {
                PanelSecurity.PanelSecurityEntry entry = (PanelSecurity.PanelSecurityEntry) it.next();
                checkMethodSecurity(entry.getMethodName(), entry.getPermissionClass(), entry.getAction(), panel);
            }
        }
        // Invoke the panel's action method.
        CommandResponse res = null;
        if (!isSystemAction(action)) res = fireBeforeInvokeAction(panel, req);
        if (res == null) res = (CommandResponse) method.invoke(this, args);
        if (!isSystemAction(action)) res = fireAfterInvokeAction(panel, req, res);

        // If no AJAX then return the response get from panel invocation.
        String ajaxParam = req.getParameter(Parameters.AJAX_ACTION);
        if (ajaxParam == null || !Boolean.valueOf(ajaxParam).booleanValue()) return res;

        // Despite the request was AJAX the panel has decided refreshing the full screen.
        if (res != null && res.getClass().equals(ShowCurrentScreenResponse.class)) return res;

        // Else return the response wrapped as AJAX.
        PanelAjaxResponse response = PanelAjaxResponse.getEquivalentAjaxResponse(panel, res);
        if (response == null) log.error("Cannot convert response with " + res.getClass() + " to PanelAjaxResponse.");
        return response;
    }

    protected boolean isDoubleClickProtected(String action) {
        return true;
    }

    /**
     * Determine if an user can invoke an action in a panel based on permissions.
     *
     * @param panel Panel where action is to be invoked
     * @param action  Action to be performed
     * @return true if the user is allowed to execute the action.
     */
    public boolean canInvokeAction(Panel panel, String action) {
        String methodName = getMethodName(action);
        Set securityEntries = panelSecurity.entrySet(methodName);
        for (Iterator it = securityEntries.iterator(); it.hasNext();) {
            PanelSecurity.PanelSecurityEntry entry = (PanelSecurity.PanelSecurityEntry) it.next();
            if (!hasMethodAccess(methodName, entry.getPermissionClass(), entry.getAction(), panel))
                return false;
        }
        return true;
    }

    /**
     * Determine if an user can invoke an action in a panel based on permissions.
     *
     * @param panel Panel where action is to be invoked
     * @param action  Action to be performed
     */
    public void checkInvokeAction(Panel panel, String action) {
        String methodName = getMethodName(action);
        Set securityEntries = panelSecurity.entrySet(methodName);
        for (Iterator it = securityEntries.iterator(); it.hasNext();) {
            PanelSecurity.PanelSecurityEntry entry = (PanelSecurity.PanelSecurityEntry) it.next();
            checkMethodSecurity(entry.getMethodName(), entry.getPermissionClass(), entry.getAction(), panel);
        }
    }

    /**
     * @param methodName      Method to check
     * @param permissionClass Type of permission to check
     * @param action          Action to check
     * @param panel         Panel where action is to be invoked
     */
    protected void checkMethodSecurity(String methodName, Class permissionClass, String action, Panel panel) {
        try {
            Method instanceCreator = permissionClass.getMethod("newInstance", new Class[]{Object.class, String.class});
            Permission perm = (Permission) instanceCreator.invoke(null, new Object[]{getResourceForPermission(permissionClass, action, panel), action});
            UserStatus.lookup().checkPermission(perm);
        } catch (NoSuchMethodException e) {
            log.error("Error checking permission " + permissionClass.getName() + "[" + action + "] for method " + methodName + ":", e);
        } catch (IllegalAccessException e) {
            log.error("Error checking permission " + permissionClass.getName() + "[" + action + "] for method " + methodName + ":", e);
        } catch (InvocationTargetException e) {
            log.error("Error checking permission " + permissionClass.getName() + "[" + action + "] for method " + methodName + ":", e);
        }
    }

    /**
     * Checks security for a method in a panel for givrn user.
     *
     * @param methodName      Method to check
     * @param permissionClass Type of permission to check
     * @param action          Action to check
     * @param panel         Panel where action is to be invoked
     * @return true if the user is allowed to execute the method.
     */
    protected boolean hasMethodAccess(String methodName, Class permissionClass, String action, Panel panel) {
        try {
            Method instanceCreator = permissionClass.getMethod("newInstance", new Class[]{Object.class, String.class});
            Permission perm = (Permission) instanceCreator.invoke(null, new Object[]{getResourceForPermission(permissionClass, action, panel), action});
            return UserStatus.lookup().hasPermission(perm);
        } catch (NoSuchMethodException e) {
            log.error("Error checking permission " + permissionClass.getName() + "[" + action + "] for method " + methodName + ":", e);
        } catch (IllegalAccessException e) {
            log.error("Error checking permission " + permissionClass.getName() + "[" + action + "] for method " + methodName + ":", e);
        } catch (InvocationTargetException e) {
            log.error("Error checking permission " + permissionClass.getName() + "[" + action + "] for method " + methodName + ":", e);
        }
        return false;
    }

    /**
     * Determine the resource associated for a permission. For instance, if it is a PanelPermission, it will be the panel itself.
     *
     * @param permissionClass Type of permission
     * @param action          action being performed, may be used internally.
     * @param panel         current panel
     * @return The object where the permission applies, or null if none applies.
     */
    protected Object getResourceForPermission(Class permissionClass, String action, Panel panel) {
        if (permissionClass.equals(PanelPermission.class))
            return panel;
        if (permissionClass.equals(WorkspacePermission.class))
            return panel.getWorkspace();
        if (permissionClass.equals(SectionPermission.class))
            return panel.getSection();
        log.warn("Cannot determine resource where permission applies: " + permissionClass);
        return null;
    }

    /*
     *
     * Services and shortcuts to subclasses
     *
     */

    /**
     * Returns the directory where this panel can store its files. It will be automatically cleaned when the panel
     * is removed
     *
     * @param panelInstance Panel instance whose directory is retrieved
     * @return directory assigned to given panel
     * @throws Exception
     */
    protected File getPanelDir(PanelInstance panelInstance) throws Exception {
        String baseDir = Application.lookup().getBaseAppDirectory() + File.separator +
                panelInstance.getProvider().getPanelsDir();

        File dir = new File(baseDir + File.separator + panelInstance.getDbid());
        if (!dir.exists()) {
            //New directory format. Since 2.0.00
            dir = new File(baseDir + File.separator + panelInstance.getWorkspace().getId() + File.separator + panelInstance.getInstanceId());
        }

        log.debug("getPanelDir() for instance dbid=" + panelInstance.getDbid() + " is " + dir.getCanonicalPath());
        if (!dir.exists()) {
            log.debug("Creating dir");
            if (!dir.mkdirs()) {
                throw new Exception("Can't create directory: " + dir.getCanonicalPath());
            }
        }

        return dir;
    }

    /**
     * Returns the directory where this panel can store its files. It will be automatically cleaned when the panel
     * is removed
     *
     * @param panel Panel whose directory is retrieved
     * @return directory assigned to given panel
     * @throws Exception
     */
    protected File getPanelDir(Panel panel) throws Exception {
        return getPanelDir(panel.getInstance());
    }

    /**
     * Returns the URL mapping used to reference the directory which holds the panel data
     *
     * @param panel Panel being attended
     * @return the URL mapping to the panel resources directory
     */
    protected String getPanelDirMapping(Panel panel) throws Exception {
        return getPanelDirMapping(panel.getInstance());
    }

    /**
     * Returns the URL mapping used to reference the directory which holds the panel data
     *
     * @param panelInstance PanelInstance being attended
     * @return the URL mapping to the panel resources directory
     */
    protected String getPanelDirMapping(PanelInstance panelInstance) throws Exception {
        String basePanelsDir = Application.lookup().getBaseAppDirectory() + File.separator +
                panelInstance.getProvider().getPanelsDir();

        File dir = new File(basePanelsDir + File.separator + panelInstance.getDbid());
        String urlMapping = panelInstance.getProvider().getPanelsUrlMapping();

        if (dir.exists()) {//old format
            return urlMapping + "/" + panelInstance.getInstanceId();
        }
        return urlMapping + "/" + panelInstance.getWorkspace().getId() + "/" + panelInstance.getInstanceId();
    }

    /**
     * Returns the URL to show when maximized, or null if none
     *
     * @param panelStatus
     * @return null
     * @deprecated It is of no practical use
     */
    public String getURLWhenMaximized(PanelSession panelStatus) {
        return null;
    }

    /**
     * Returns if this driver defines support to activate edit mode.
     *
     * @param panel
     * @return true if this driver defines support to activate resources mode.
     */
    public boolean supportsEditMode(Panel panel) {
        return false;
    }

    /**
     * Returns if this driver defines support to activate help mode.
     *
     * @param panel
     * @return true if this driver defines support to activate help mode.
     */
    public boolean supportsHelpMode(Panel panel) {
        return panel.getProvider().getPanelHelp() != null;
    }

    /**
     * Called on panel initialization (when a new PanelSession instance is created attached to a given session)
     */
    public void initPanelSession(PanelSession panelSession, HttpSession session) {
        panelSession.setCurrentPageId(PAGE_SHOW);
    }

    /**
     * Called before the panel is placed in a region
     *
     * @param panel panel being placed
     * @param region  region where the panel is being placed
     */
    public final void fireBeforePanelPlacedInRegion(Panel panel, LayoutRegion region) {
        beforePanelPlacedInRegion(panel, region);
    }

    /**
     * Called after the panel is placed in a region
     *
     * @param panel panel being placed
     * @param region  region where the panel was before
     */
    public final void fireAfterPanelPlacedInRegion(Panel panel, LayoutRegion region) throws Exception {
        afterPanelPlacedInRegion(panel, region);
    }

    /**
     * Called before the panel is removed
     *
     * @param panel panel being placed
     */
    public final void fireBeforePanelRemoved(Panel panel) throws Exception {
        beforePanelRemoved(panel);
    }

    /**
     * Called before dispatching to an action method.
     *
     * @param panel
     * @param request
     * @return Null if the action execution can continue, or a CommandResponse if not
     * @see PanelDriver#beforeInvokeAction(org.jboss.dashboard.workspace.Panel, org.jboss.dashboard.ui.controller.CommandRequest)
     */
    public final CommandResponse fireBeforeInvokeAction(Panel panel, CommandRequest request) {
        return beforeInvokeAction(panel, request);
    }

    /**
     * Called after executing an action method
     *
     * @param panel
     * @param request
     * @param response
     * @return
     * @see PanelDriver#afterInvokeAction(org.jboss.dashboard.workspace.Panel, org.jboss.dashboard.ui.controller.CommandRequest, org.jboss.dashboard.ui.controller.CommandResponse)
     */
    public final CommandResponse fireAfterInvokeAction(Panel panel, CommandRequest request, CommandResponse response) {
        return afterInvokeAction(panel, request, response);
    }

    /**
     * Called before render the panel.
     *
     * @param panel
     * @param request
     * @param response
     * @see PanelDriver#beforeRenderPanel
     */
    public final void fireBeforeRenderPanel(Panel panel, HttpServletRequest request, HttpServletResponse response) {
        beforeRenderPanel(panel, request, response);
    }

    /**
     * Called after panel has been redered
     *
     * @param panel
     * @param request
     * @see PanelDriver#afterRenderPanel
     */
    public final void fireAfterRenderPanel(Panel panel, HttpServletRequest request, HttpServletResponse response) {
        afterRenderPanel(panel, request, response);
    }

    /**
     * Called when a panel instance is removed from the system. This is to
     * allow the driver to cleanup any data atteched to this panel instance, if needed.
     *
     * @param instance Panel which is about to be removed
     * @see PanelDriver#beforePanelInstanceRemove
     */
    public final void fireBeforePanelInstanceRemove(PanelInstance instance) throws Exception {
        beforePanelInstanceRemove(instance);
    }

    /**
     * Called when the a panel properties have been manually modified
     *
     * @param instance
     * @see PanelDriver#afterPanelPropertiesModified
     */
    public final void firePanelPropertiesModified(PanelInstance instance) {
        afterPanelPropertiesModified(instance);
    }

    /**
     * Called when the a panel properties have been manually modified
     *
     * @param instance
     * @see PanelDriver#afterPanelPropertiesModified
     */
    public final void firePanelCustomPropertiesModified(PanelInstance instance) {
        afterPanelCustomPropertiesModified(instance);
    }


    /**
     * Replicates panel data. Called when a workspace is duplicated.
     *
     * @param src  Source PanelInstance
     * @param dest Destinaton PanelInstance
     */
    public void replicateData(final PanelInstance src, final PanelInstance dest) throws Exception {
        // Default implementation. If the panel uses special tables or files, should overwrite this
        // to implement duplication of own data.
        File dirSrc = getPanelDir(src);
        File dirDest = getPanelDir(dest);
        log.debug("Copying directory tree from " + dirSrc.getCanonicalPath() + " to " + dirDest.getCanonicalPath());
        copyDirectory(dirSrc, dirDest);
    }

    protected void copyDirectory(File src, File dest) throws IOException {
        if (src.isFile()) {
            log.warn("File " + src + " is not a directory. Copying it as a file");
            copyFile(src, dest);
            return;
        }
        log.debug("Copying directory ");
        log.debug("   " + src);
        log.debug("to");
        log.debug("   " + dest);
        File[] files = src.listFiles();
        if (files == null) {
            log.warn("File " + src + " cannot contain files. Reasons: it is a file or it does not exist.");
            return;
        }
        dest.mkdirs();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                copyFile(file, new File(dest.getAbsolutePath() + File.separator + file.getName()));
            } else if (file.isDirectory()) {
                File fDest = new File(dest.getAbsolutePath() + File.separator + file.getName());
                copyDirectory(file, fDest);
            }
        }
    }

    protected void copyFile(File src, File dest) throws IOException {
        log.debug("Copying file ");
        log.debug("   " + src);
        log.debug("to");
        log.debug("   " + dest);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest));
        for (int byteRead = bis.read(); byteRead != -1; byteRead = bis.read()) {
            bos.write(byteRead);
        }
        bos.close();
        bis.close();
    }

    private void recursiveDelete(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    recursiveDelete(file);
                }
            }
            if (!f.delete())
                f.deleteOnExit();
        } else if (f.isFile()) {
            if (!f.delete())
                f.deleteOnExit();
        }
    }

    /**
     * Action that dispatches to a factory component.
     */
    public CommandResponse panelActionFactory(final Panel panel, CommandRequest request) throws Exception {
        FactoryRequestHandler requestHandler = (FactoryRequestHandler) Factory.lookup("org.jboss.dashboard.ui.components.FactoryRequestHandler");
        CommandResponse factoryResponse = requestHandler.handleRequest(request);
        String action = request.getRequestObject().getParameter(FactoryURL.PARAMETER_PROPERTY);
        String componentName = request.getRequestObject().getParameter(FactoryURL.PARAMETER_BEAN);
        if (action != null) {
            HandlerFactoryElement handler = (HandlerFactoryElement)Factory.lookup(componentName);
            if (handler != null) action = handler.getActionForShortcut(action);
            String methodName = getMethodName(action);
            Class[] params = {Panel.class, CommandRequest.class};
            Object[] args = {panel, request};
            Class cmdClass = this.getClass();
            Method method;
            try {
                method = cmdClass.getMethod(methodName, params);
                if (factoryResponse == null)
                    factoryResponse = (CommandResponse) method.invoke(this, args);
                else
                    method.invoke(this, args);
            } catch (NoSuchMethodException e) {
                if (log.isDebugEnabled())
                    log.debug("Method " + cmdClass.getName() + "." + methodName + "() not found");
            } catch (IllegalAccessException e) {
                log.error("Error: ", e);
            } catch (InvocationTargetException e) {
                log.error("Error: ", e);
            }
        }
        if (factoryResponse != null)
            return factoryResponse;
        return new ShowPanelPage();
    }

    /**
     * Action to change a page for a panel. It expects a parameter in request called "pageId"
     */
    public CommandResponse actionChangePage(final Panel panel, CommandRequest request) {
        String pageId = request.getParameter("pageId");
        if (pageId == null || "".equals(pageId)) {
            pageId = SessionManager.getPanelSession(panel).getCurrentPageId();
        }
        return new ShowPanelPage(panel, request, pageId);
    }

    public Map getTextShownByInstance(PanelInstance instance) {
        Map m = new HashMap();
        LocaleManager localeManager = LocaleManager.lookup();
        String[] langs = localeManager.getPlatformAvailableLangs();
        for (int i = 0; i < langs.length; i++) {
            String lang = langs[i];
            String before = instance.getParameterValue(PanelInstance.PARAMETER_HTML_BEFORE, lang);
            String panelBody = getPanelHTMLContent(instance, lang);
            String after = instance.getParameterValue(PanelInstance.PARAMETER_HTML_AFTER, lang);
            StringBuffer sb = new StringBuffer();
            if (!StringUtils.isEmpty(before))
                sb.append(before);
            if (!StringUtils.isEmpty(panelBody))
                sb.append(panelBody);
            if (!StringUtils.isEmpty(after))
                sb.append(after);
            String total = sb.toString().trim();
            if (!StringUtils.isEmpty(total)) {
                m.put(lang, total);
            }
        }
        return m.isEmpty() ? null : m;
    }

    protected String getPanelHTMLContent(PanelInstance instance, String lang) {
        return "";
    }

    public ShowPanelPageComponent getShowPanelPageComponent() {
        return ShowPanelPageComponent.lookup();
    }
}
