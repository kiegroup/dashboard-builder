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
package org.jboss.dashboard.ui.controller.requestChain;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.error.ErrorManager;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.ui.components.KPIViewer;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.DoNothingResponse;
import org.jboss.dashboard.ui.controller.responses.SendErrorResponse;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelsManager;
import org.jboss.dashboard.workspace.Parameters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * This processor will handler URLs of the following form
 * /KPI/(SHOW|CHECK)?kpi=(KPI_CODE)[&locale=(LOCALE)]
 *
 * Pending work:
 * - Security & login
 * - Clear filter interaction
 * - Solve concurrency problems among kpi in different pages
 * - Handle errors and avoid frames showing full screen
 */
public class KPIProcessor extends RequestChainProcessor {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KPIProcessor.class.getName());

    private NavigationManager navigationManager;

    public static final String KPI_MAPPING = "/kpi";
    public static final String LOCALE_PARAMETER = "locale";
    public static final String DEFAULT_KPI_JSP = "/components/bam/kpi_view_rest.jsp";

    public UserStatus getUserStatus() {
        return UserStatus.lookup();
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public String getDefaultKpiJsp() {
        return DEFAULT_KPI_JSP;
    }

    /**
     * Make required processing of request.
     *
     * @return true if processing must continue, false otherwise.
     */
    protected boolean processRequest() throws Exception {
        HttpServletRequest request = getRequest();
        String servletPath = request.getServletPath();

        // No friendly -> nothing to do.
        if (servletPath == null || !servletPath.startsWith(KPI_MAPPING)) return true;

        // Set locale if needed
        processSetLocale(getRequest());

        String contextPath = request.getContextPath();
        getControllerStatus().consumeURIPart(KPI_MAPPING);

        navigationManager.setShowingConfig(false);
        String requestUri = request.getRequestURI();
        String relativeUri = requestUri.substring(contextPath == null ? 0 : (contextPath.length()));
        relativeUri = relativeUri.substring(servletPath == null ? 0 : (servletPath.length()));

        // Empty URI -> nothing to do.
        if (StringUtils.isBlank(relativeUri)) return true;

        // Tokenize the friendly URI.
        StringTokenizer tokenizer = new StringTokenizer(relativeUri, "/", false);
        String action = null;
        if (tokenizer.hasMoreTokens()) {
            action = tokenizer.nextToken();
        }
        getControllerStatus().consumeURIPart(KPI_MAPPING);

        try {
            // Invoke the right action
            CommandResponse cmdResponse = null;
            if ("SHOW".equalsIgnoreCase(action)) {
                cmdResponse = processShowKPI(getRequest(), getResponse());
            }
            else {
                log.error("Invalid KPI URL: " + relativeUri);
            }

            // Set response if needed
            if (cmdResponse != null) {
                // Set successful response
                getControllerStatus().setResponse(cmdResponse);
            } else {
                // Response parameters missing or wrong action
                getControllerStatus().setResponse(new SendErrorResponse(HttpServletResponse.SC_NOT_FOUND));
            }
        } catch (Exception e) {
            // Handler response error
            log.error("Error serving URL " + requestUri, e);
            ErrorManager.lookup().notifyError(e, true);

            getControllerStatus().setResponse(new SendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }

        return true;
    }

    /**
     * Process show KPI request by rendering the KPi
     * @param request Request
     * @param response Response
     * @return the CommandResponse. or null if any wrong parameters is found
     * @throws Exception
     */
    protected CommandResponse processShowKPI(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        String kpiCode = request.getParameter("kpi");
        KPI kpi = null;
        Panel currentPanel = null;
        boolean resetFilter = "1".equals(request.getParameter("clean-filter"));

        if (kpiCode != null) {
            kpi = DataDisplayerServices.lookup().getKPIManager().getKPIByCode(kpiCode);
        }

        if (kpi != null) {
            // Check KPI panel
            currentPanel = getKPIPanel(kpi);
        }

        if (kpi != null && currentPanel != null) {
            // TODO: Check security constrains
            //

            // Set current workspace and section
            // TODO: Remove this requirement from core since it will lead to problems under concurrent requests for kpi's placed in different pages
            getNavigationManager().setCurrentWorkspace(currentPanel.getWorkspace());
            getNavigationManager().setCurrentSection(currentPanel.getSection());

            if(resetFilter ) {
                DashboardHandler.lookup().getCurrentDashboard().unfilter();
            }

            // Save the current panel instance and set the specified panel as current.
            request.setAttribute(Parameters.RENDER_PANEL, currentPanel);
            KPIViewer kpiViewer = KPIViewer.lookup();
            kpiViewer.setKpi(kpi,true);

            // Forward to entrance JSP
            request.getRequestDispatcher(getDefaultKpiJsp()).forward(request, response);
            return new DoNothingResponse();

        } else {
            // KPI NOT FOUND
            return null;
        }
    }

    /**
     * Handles the local parameter to set display locale
     *
     * @param request
     */
    protected void processSetLocale(HttpServletRequest request) {
        Locale localeToSet;

        // First check if a locale parameter is present in the URI query string.
        String localeParam = getRequest().getParameter(LOCALE_PARAMETER);

        if (localeParam != null && localeParam.trim().length() > 0) {
            // ---- Apply locale information, --------------
            LocaleManager localeManager = LocaleManager.lookup();

            localeToSet = localeManager.getLocaleById(localeParam);
            if (localeToSet != null) {
                localeManager.setCurrentLocale(localeToSet);
            }
        }
    }

    /**
     * Returns pannel where KPI has been configured. If more than one panel is showing this KPI,
     * then the first one is returned.
     *
     * @param kpi
     * @return
     * @throws Exception
     */
    protected Panel getKPIPanel(KPI kpi) throws Exception {
        Panel currentPanel = null;

        PanelsManager panelsManager = UIServices.lookup().getPanelsManager();

        Set<PanelInstance> panelsI = panelsManager.getPanelsByParameter(Dashboard.KPI_CODE, kpi.getCode());

        if (panelsI != null && !panelsI.isEmpty()) {
            List<Panel> allPanels = new ArrayList<Panel>();

            for (PanelInstance instance : panelsI) {
                Panel[] panels = instance.getAllPanels();
                if (panels != null && panels.length > 0) {
                    allPanels.addAll(Arrays.asList(panels));
                }
            }
            currentPanel = allPanels.get(0);
        }
        return currentPanel;
    }
}
