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

import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.error.ErrorManager;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.security.PanelPermission;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.controller.responses.FullPanelResponse;
import org.jboss.dashboard.ui.controller.responses.SendErrorResponse;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelsManager;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
@ApplicationScoped
public class KPIProcessor extends AbstractChainProcessor {

    public static final String KPI_MAPPING = "/kpi";
    public static final String LOCALE_PARAMETER = "locale";

    @Inject
    private transient Logger log;

    @Inject
    private NavigationManager navigationManager;

    @Inject
    private UserStatus userStatus;

    public boolean processRequest() throws Exception {
        HttpServletRequest request = getHttpRequest();
        HttpServletResponse response = getHttpResponse();
        String servletPath = request.getServletPath();

        // No friendly -> nothing to do.
        if (servletPath == null || !servletPath.startsWith(KPI_MAPPING)) {
            return true;
        }

        // Set locale if needed
        processSetLocale(request);

        RequestContext requestContext = getRequestContext();
        String contextPath = request.getContextPath();
        requestContext.consumeURIPart(KPI_MAPPING);
        navigationManager.setShowingConfig(false);
        String requestUri = request.getRequestURI();
        String relativeUri = requestUri.substring(contextPath == null ? 0 : (contextPath.length()));
        relativeUri = relativeUri.substring(servletPath == null ? 0 : (servletPath.length()));

        // Empty URI -> nothing to do.
        if (StringUtils.isBlank(relativeUri)) {
            return true;
        }

        // Tokenize the friendly URI.
        StringTokenizer tokenizer = new StringTokenizer(relativeUri, "/", false);
        String action = null;
        if (tokenizer.hasMoreTokens()) action = tokenizer.nextToken();

        try {
            // Invoke the right action
            CommandResponse cmdResponse = null;
            if ("show".equalsIgnoreCase(action)) {
                cmdResponse = processShowKPI(request, response);
                requestContext.consumeURIPart("/" + action);
            }
            else {
                log.error("Invalid KPI URL: " + relativeUri);
            }

            // Set response if needed
            if (cmdResponse != null) {
                // Set successful response
                requestContext.setResponse(cmdResponse);
            } else {
                // Response parameters missing or wrong action
                requestContext.setResponse(new SendErrorResponse(HttpServletResponse.SC_NOT_FOUND));
            }
        } catch (Exception e) {
            // Handler response error
            ErrorManager.lookup().notifyError(e, true);
            requestContext.setResponse(new SendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
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

        if (kpiCode != null) {
            kpi = DataDisplayerServices.lookup().getKPIManager().getKPIByCode(kpiCode);
        }

        if (kpi != null) {
            // Check KPI panel
            currentPanel = getKPIPanel(kpi);
        }

        if (kpi != null && currentPanel != null) {
            // Check security constraints
            PanelPermission panelPerm = PanelPermission.newInstance(currentPanel, PanelPermission.ACTION_VIEW);
            SectionPermission sectionPerm = SectionPermission.newInstance(currentPanel, SectionPermission.ACTION_VIEW);
            WorkspacePermission workspacePermission = WorkspacePermission.newInstance(currentPanel, WorkspacePermission.ACTION_LOGIN);
            if (!userStatus.hasPermission(panelPerm) || !userStatus.hasPermission(sectionPerm) || !userStatus.hasPermission(workspacePermission)) {
                // Forbidden access response
                setResponse(new SendErrorResponse(HttpServletResponse.SC_FORBIDDEN));
            }

            // Make sure the chart displays no filtered data.
            DashboardHandler.lookup().getCurrentDashboard().unfilter();

            // Show the KPI panel.
            return new FullPanelResponse(currentPanel);

        } else {
            // KPI NOT FOUND
            return null;
        }
    }

    /**
     * Handles the local parameter to set display locale
     */
    protected void processSetLocale(HttpServletRequest request) {

        // First check if a locale parameter is present in the URI query string.
        String localeParam = request.getParameter(LOCALE_PARAMETER);

        if (localeParam != null && localeParam.trim().length() > 0) {
            LocaleManager localeManager = LocaleManager.lookup();
            Locale localeToSet = localeManager.getLocaleById(localeParam);
            if (localeToSet != null) {
                localeManager.setCurrentLocale(localeToSet);
            }
        }
    }

    /**
     * Returns the panel where KPI has been configured. If more than one panel is showing this KPI,
     * then the first one is returned.
     */
    public static Panel getKPIPanel(KPI kpi) throws Exception {
        Panel currentPanel = null;
        PanelsManager panelsManager = UIServices.lookup().getPanelsManager();
        Set<PanelInstance> panelsI = panelsManager.getPanelsByParameter(DashboardHandler.KPI_CODE, kpi.getCode());

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
